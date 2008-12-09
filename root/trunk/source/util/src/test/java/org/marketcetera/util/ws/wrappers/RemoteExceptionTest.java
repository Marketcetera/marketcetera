package org.marketcetera.util.ws.wrappers;

import org.junit.Test;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

public class RemoteExceptionTest
    extends TestCaseBase
{
    private static final String TEST_MESSAGE=
        "testMessage";
    private static final Throwable TEST_THROWABLE=
        new CloneNotSupportedException(TEST_MESSAGE);
    private static final I18NException TEST_I18N_THROWABLE=
        new I18NException
        (TEST_THROWABLE,
         new I18NBoundMessage1P(TestMessages.EXCEPTION,TEST_MESSAGE));

    public static final String EXPECTED_MESSAGE=
        "Remote exception; see cause for details";


    private static void singleClient
        (RemoteException ex,
         RemoteProperties properties,
         Throwable cause)
    {
        assertEquals(EXPECTED_MESSAGE,ex.getMessage());
        assertEquals(properties,ex.getProperties());
        assertEquals(cause,ex.getCause());
        assertEquals(RemoteExceptionTest.class.getName(),
                     ex.getStackTrace()[0].getClassName());
    }

    private static void singleServer
        (boolean sendWrapper,
         RemoteException server,
         Throwable cause)
    {
        RemoteProperties pServer=server.getProperties();
        RemoteProperties pClient=new RemoteProperties();
        if (sendWrapper) {
            pClient.setWrapper(pServer.getWrapper());
        }
        pClient.setServerMessage(pServer.getServerMessage());
        pClient.setTraceCapture(pServer.getTraceCapture());
        pClient.setServerString(pServer.getServerString());
        RemoteException client=new RemoteException();
        client.setProperties(pClient);
        singleClient(client,pClient,cause);
    }

    private static void singleServerWrapper
        (RemoteException server,
         Throwable cause)
    {
        singleServer(true,server,cause);
    }

    private static void singleServerNoWrapper
        (RemoteException server,
         Throwable serverCause)
    {
        RemoteProperties p=new RemoteProperties(serverCause);
        singleServer
            (false,server,
             new RemoteProxyException
             (p.getServerMessage(),p.getTraceCapture(),p.getServerString()));
    }


    @Test
    public void client()
    {
        singleClient
            (new RemoteException(),
             null,
             null);
        singleClient
            (new RemoteException(TEST_THROWABLE),
             new RemoteProperties(TEST_THROWABLE),
             TEST_THROWABLE);
        singleClient
            (new RemoteException(TEST_I18N_THROWABLE),
             new RemoteProperties(TEST_I18N_THROWABLE),
             TEST_I18N_THROWABLE);
    }

    @Test
    public void serverWrapper()
    {
        singleServerWrapper
            (new RemoteException(TEST_THROWABLE),
             TEST_THROWABLE);
        singleServerWrapper
            (new RemoteException(TEST_I18N_THROWABLE),
             TEST_I18N_THROWABLE);
    }

    @Test
    public void serverNoWrapper()
    {
        singleServerNoWrapper
            (new RemoteException(TEST_THROWABLE),
             TEST_THROWABLE);
        singleServerNoWrapper
            (new RemoteException(TEST_I18N_THROWABLE),
             TEST_I18N_THROWABLE);
    }
}
