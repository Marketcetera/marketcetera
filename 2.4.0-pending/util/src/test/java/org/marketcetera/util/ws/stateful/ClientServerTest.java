package org.marketcetera.util.ws.stateful;

import javax.xml.ws.soap.SOAPFaultException;
import org.junit.Test;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.ws.stateless.ClientServerTestBase;
import org.marketcetera.util.ws.stateless.StatelessClientContext;
import org.marketcetera.util.ws.stateless.StatelessServer;
import org.marketcetera.util.ws.tags.SessionId;
import org.marketcetera.util.ws.wrappers.RemoteException;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

public class ClientServerTest
    extends ClientServerTestBase
{
    private static final Authenticator TEST_AUTHENTICATOR=
        new FixedAuthenticator();
    private static final SessionManager<Object> TEST_MANAGER=
        new SessionManager<Object>();
    private static final String TEST_USER=
        "metc";
    private static final String TEST_USER_D=
        "metcD";
    private static final char[] TEST_PASSWORD=
        "metc".toCharArray();


    private static StatelessClientContext getStatelessContext
        (Client client)
    {
        ClientContext context=client.getContext();
        StatelessClientContext statelessContext=new StatelessClientContext();
        statelessContext.setVersionId(context.getVersionId());
        statelessContext.setAppId(context.getAppId());
        statelessContext.setClientId(context.getClientId());
        statelessContext.setLocale(context.getLocale());
        return statelessContext;
    }

    private static <T> void calls
        (Server<T> server,
         Client client)
    {
        Client client2=new Client
            (client.getHost(),client.getPort()+1,client.getAppId());
        calls(server,client,getStatelessContext(client).toString(),
              new Server<T>(client2.getHost(),
                            client2.getPort(),
                            server.getAuthenticator(),
                            server.getSessionManager()),
              client2,getStatelessContext(client2).toString());
    }                         

    protected static void singleServer
        (Server<?> server,
         Server<?> empty)
    {
        singleServer((StatelessServer)server,
                     (StatelessServer)empty);

        assertNull(server.getAuthenticator());
        assertEquals(TEST_MANAGER,server.getSessionManager());

        assertNull(empty.getAuthenticator());
        assertNull(empty.getSessionManager());
    }


    @Test
    public void basics()
    {
        singleClientEmpty
            (new Client(TEST_HOST,TEST_PORT,TEST_APP),
             new Client());
        singleClientJustId
            (new Client(TEST_HOST,TEST_PORT,TEST_APP),
             new Client(TEST_APP));
        singleServer
            (new Server<Object>(TEST_HOST,TEST_PORT,null,TEST_MANAGER),
             new Server<Object>());
        calls
            (new Server<Object>(),
             new Client());
        calls
            (new Server<Object>(),
             new Client(TEST_APP));
        badConnection
            (new Server<Object>(TEST_HOST,TEST_BAD_PORT,null,TEST_MANAGER),
             new Client(TEST_HOST,TEST_BAD_PORT,TEST_APP));
    }

    @Test
    public void session()
        throws Exception
    {
        Client c=new Client();

        Server<Object> s=new Server<Object>(TEST_AUTHENTICATOR,TEST_MANAGER);
        assertEquals(TEST_AUTHENTICATOR,s.getAuthenticator());

        try {
            c.assertValidSession();
            fail();
        } catch (I18NException ex) {
            assertEquals(Messages.NOT_LOGGED_IN,ex.getI18NBoundMessage());
        }

        c.login(TEST_USER,TEST_PASSWORD);
        SessionId id=c.getSessionId();
        assertNotNull(id);
        assertEquals(id,c.getContext().getSessionId());
        assertEquals(TEST_USER,TEST_MANAGER.get(id).getUser());

        try {
            c.login(TEST_USER,TEST_PASSWORD);
            fail();
        } catch (I18NException ex) {
            assertEquals(Messages.ALREADY_LOGGED_IN,ex.getI18NBoundMessage());
        }

        assertEquals(id,c.getSessionId());
        assertEquals(TEST_USER,TEST_MANAGER.get(id).getUser());

        c.logout();
        assertNull(c.getSessionId());
        assertNull(c.getContext().getSessionId());
        assertNull(TEST_MANAGER.get(id));

        try {
            c.login(TEST_USER_D,TEST_PASSWORD);
            fail();
        } catch (RemoteException ex) {
            assertEquals(Messages.BAD_CREDENTIALS,
                         ((I18NException)ex.getCause()).getI18NBoundMessage());
        }
        assertNull(c.getSessionId());

        c.logout();

        s.stop();
        try {
            c.login(TEST_USER,TEST_PASSWORD);
            fail();
        } catch (SOAPFaultException ex) {
            // Desired.
        }
    }
}
