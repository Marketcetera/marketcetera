package org.marketcetera.ors.ws;

import org.junit.Test;
import org.marketcetera.core.ApplicationVersion;
import org.marketcetera.core.Util;
import org.marketcetera.util.test.TestCaseBase;
import org.marketcetera.util.ws.stateless.StatelessClientContext;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.I18NBoundMessage;
import org.marketcetera.util.log.I18NBoundMessage2P;
import org.marketcetera.util.log.I18NBoundMessage3P;
import org.marketcetera.client.ClientVersion;
import org.marketcetera.module.ExpectedFailure;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 1.5.0
 * @version $Id$
 */

/* $License$ */

public class DBAuthenticatorTest
    extends TestCaseBase
{
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
        ctx.setAppId(Util.getAppId(ClientVersion.APP_ID_NAME,""));
        shouldAllowFailure(
                new I18NBoundMessage3P(Messages.VERSION_MISMATCH, null,
                        ApplicationVersion.getVersion(),null),
                ctx,null,null);
        //invalid app version
        ctx.setAppId(Util.getAppId(ClientVersion.APP_ID_NAME,"x"));
        shouldAllowFailure(
                new I18NBoundMessage3P(Messages.VERSION_MISMATCH, "x",
                        ApplicationVersion.getVersion(),null),
                ctx,null,null);
        //valid app name & version
        ctx.setAppId(Util.getAppId(ClientVersion.APP_ID_NAME,
                ApplicationVersion.getVersion()));
        assertFalse(new DBAuthenticator().shouldAllow(ctx,"x",null));
    }

    private static void shouldAllowFailure(I18NBoundMessage inMessage,
                                           final StatelessClientContext inContext,
                                           final String inUsername,
                                           final char[] inPassword)
            throws Exception {
        new ExpectedFailure<I18NException>(inMessage){
            @Override
            protected void run() throws Exception {
                new DBAuthenticator().shouldAllow(inContext,
                        inUsername, inPassword);
            }
        };
    }
}
