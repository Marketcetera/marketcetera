package org.marketcetera.util.ws.stateless;

import java.util.Locale;
import javax.jws.WebService;
import javax.xml.ws.soap.SOAPFaultException;
import org.apache.cxf.service.factory.ServiceConstructionException;
import org.marketcetera.util.ws.tags.AppId;
import org.marketcetera.util.ws.tags.VersionId;
import org.marketcetera.util.ws.wrappers.RemoteException;
import org.marketcetera.util.ws.wrappers.RemoteExceptionTest;
import org.marketcetera.util.ws.wrappers.RemoteProperties;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

public class ClientServerTestBase
    extends NodeTestBase
{
    protected static final AppId TEST_APP=
        new AppId("testApp");
    protected static final int TEST_BAD_PORT=
        -1;
    private static final Exception TEST_EXCEPTION=
        new IllegalArgumentException();

    private static final String EXPECTED_MESSAGE=
        RemoteExceptionTest.EXPECTED_MESSAGE;


    @WebService
    public static interface TestService
        extends StatelessServiceBase
    {
        String testCall
            (StatelessClientContext arg);

        void testException()
            throws RemoteException;
    }
    
    private static class TestServiceImpl
        extends StatelessServiceBaseImpl
        implements TestService
    {
        @Override
        public String testCall
            (StatelessClientContext arg)
        {
            return arg.toString();
        }

        @Override
        public void testException()
            throws RemoteException
        {
            throw new RemoteException(TEST_EXCEPTION);
        }
    }


    protected static void singleClientEmpty
        (StatelessClient client,
         StatelessClient empty)
    {
        assertEquals(TEST_APP,client.getAppId());

        assertNull(empty.getAppId());

        StatelessClientContext context=client.getContext();
        assertEquals(VersionId.SELF,context.getVersionId());
        assertEquals(TEST_APP,context.getAppId());
        assertEquals(client.getId(),context.getClientId());
        assertEquals(Locale.getDefault(),context.getLocale().getRaw());

        singleNode(client,empty);
    }

    protected static void singleClientJustId
        (StatelessClient client,
         StatelessClient justId)
    {
        assertEquals(TEST_APP,justId.getAppId());

        singleNode(client,justId);
    }

    protected static void singleServer
        (StatelessServer server,
         StatelessServer empty)
    {
        singleNode(server,empty);

        server.stop();
    }

    protected static void checkException
        (RemoteException ex)
    {
        assertEquals(EXPECTED_MESSAGE,
                     ex.getMessage());
        assertEquals(new RemoteProperties(TEST_EXCEPTION),
                     ex.getProperties());
        assertEquals(TEST_EXCEPTION.getClass(),
                     ex.getCause().getClass());
        assertEquals(ClientServerTestBase.class.getName(),
                     ex.getCause().getStackTrace()[0].getClassName());
    }

    protected static void calls
        (StatelessServer server1,
         StatelessClient client1,
         String contextStr1,
         StatelessServer server2,
         StatelessClient client2,
         String contextStr2)
    {
        ServiceInterface si1=server1.publish
            (new TestServiceImpl(),TestService.class);
        ServiceInterface si2=server2.publish
            (new TestServiceImpl(),TestService.class);

        TestService i1=client1.getService(TestService.class);
        TestService i2=client2.getService(TestService.class);

        assertEquals(contextStr1,i1.testCall(client1.getContext()));
        try {
            i1.testException();
            fail();
        } catch (RemoteException ex) {
            checkException(ex);
        }

        assertEquals(contextStr2,i2.testCall(client2.getContext()));
        try {
            i2.testException();
            fail();
        } catch (RemoteException ex) {
            checkException(ex);
        }

        si1.stop();
        try {
            i1.testCall(client1.getContext());
            fail();
        } catch (SOAPFaultException ex) {
            // Desired.
        }
        assertEquals(contextStr2,i2.testCall(client2.getContext()));

        si2.stop();
        try {
            i2.testCall(client2.getContext());
            fail();
        } catch (SOAPFaultException ex) {
            // Desired.
        }

        server1.stop();
        server2.stop();
    }

    protected static void badConnection
        (StatelessServer badServer,
         StatelessClient badClient)
    {
        try {
            badServer.publish(new TestServiceImpl(),TestService.class);
            fail();
        } catch (ServiceConstructionException ex) {
            // Desired.
        }

        TestService i=badClient.getService(TestService.class);
        try {
            i.testCall(badClient.getContext());
            fail();
        } catch (SOAPFaultException ex) {
            // Desired.
        }

        badServer.stop();
    }
}
