package org.marketcetera.ors.ws;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.marketcetera.client.ClientVersion;
import org.marketcetera.client.IncompatibleComponentsException;
import org.marketcetera.core.ApplicationVersion;
import org.marketcetera.core.Util;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.ors.PersistTestBase;
import org.marketcetera.util.log.I18NBoundMessage;
import org.marketcetera.util.log.I18NBoundMessage2P;
import org.marketcetera.util.log.I18NBoundMessage3P;
import org.marketcetera.util.ws.stateful.Authenticator;
import org.marketcetera.util.ws.stateless.StatelessClientContext;

/**
 * @author tlerios@marketcetera.com
 * @since 1.5.0
 * @version $Id$
 */

/* $License$ */

public class DBAuthenticatorTest
        extends PersistTestBase
{
    /**
     * Runs before each test.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Before
    public void setup()
            throws Exception
    {
        instance = context.getBean(Authenticator.class);
    }
    @Test
    public void basics()
    {
        assertFalse(DBAuthenticator.compatibleVersions
                    (null,"x"));
        assertFalse(DBAuthenticator.compatibleVersions
                    ("x",null));
        assertFalse(DBAuthenticator.compatibleVersions
                    ("x","y"));

        assertFalse(ApplicationVersion.DEFAULT_VERSION.equals("x"));
        assertTrue(DBAuthenticator.compatibleVersions
                   ("x","x"));
        assertTrue(DBAuthenticator.compatibleVersions
                   ("x",ApplicationVersion.DEFAULT_VERSION));

        assertFalse(DBAuthenticator.compatibleApp(null));
        assertFalse(DBAuthenticator.compatibleApp(""));
        assertFalse(DBAuthenticator.compatibleApp("x"));
        assertTrue(DBAuthenticator.compatibleApp(ClientVersion.APP_ID_NAME));
    }

    @Test
    public void shouldAllowFailures() throws Exception
    {
        StatelessClientContext ctx=new StatelessClientContext();
        //empty context
        shouldAllowFailure(
                new I18NBoundMessage2P(Messages.APP_MISMATCH,null,null),
                ctx,null,null);
        //no app name
        ctx.setAppId(Util.getAppId("",ApplicationVersion.getVersion()));
        shouldAllowFailure(
                new I18NBoundMessage2P(Messages.APP_MISMATCH,null,null),
                ctx,null,null);
        //invalid app name
        ctx.setAppId(Util.getAppId("x",ApplicationVersion.getVersion()));
        shouldAllowFailure(
                new I18NBoundMessage2P(Messages.APP_MISMATCH,"x",null),
                ctx,null,null);
        //no app version
        ctx.setAppId(Util.getAppId(ClientVersion.APP_ID_NAME,
                                   ""));
        System.out.println("Executing test");
        shouldAllowFailure(new I18NBoundMessage3P(Messages.VERSION_MISMATCH,
                                                  null,
                                                  ApplicationVersion.getVersion(),
                                                  null),
                          ctx,
                          null,
                          null);
        //invalid app version
        ctx.setAppId(Util.getAppId(ClientVersion.APP_ID_NAME,"x"));
        shouldAllowFailure(
                new I18NBoundMessage3P(Messages.VERSION_MISMATCH, "x",
                        ApplicationVersion.getVersion(),null),
                ctx,null,null);
        //valid app name & version
        ctx.setAppId(Util.getAppId(ClientVersion.APP_ID_NAME,
                ApplicationVersion.getVersion()));
        assertFalse(instance.shouldAllow(ctx,"x",null));
    }

    private static void shouldAllowFailure(I18NBoundMessage inMessage,
                                           final StatelessClientContext inContext,
                                           final String inUsername,
                                           final char[] inPassword)
            throws Exception
    {
        new ExpectedFailure<IncompatibleComponentsException>(inMessage) {
            @Override
            protected void run()
                    throws Exception
            {
                instance.shouldAllow(inContext,
                                     inUsername,
                                     inPassword);
            }
        };
    }
    /**
     * authenticator instance
     */
    private static Authenticator instance;
}
