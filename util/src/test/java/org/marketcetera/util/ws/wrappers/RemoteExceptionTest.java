package org.marketcetera.util.ws.wrappers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.marketcetera.util.test.EqualityAssert.assertEquality;

import javax.jws.WebService;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.util.ws.stateless.StatelessClient;
import org.marketcetera.util.ws.stateless.StatelessServer;
import org.marketcetera.util.ws.stateless.StatelessServiceBase;
import org.marketcetera.util.ws.stateless.StatelessServiceBaseImpl;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

public class RemoteExceptionTest
    extends WrapperTestBase
{
    public static final String EXPECTED_MESSAGE=
        "Remote exception; see cause for details";

    private static final String LOCAL_PROXY_SOURCE=
        RemoteExceptionTest.class.getName();
    private static final String JAVA_PROXY_SOURCE=
        "sun.reflect.NativeConstructorAccessorImpl";


    @WebService
    public static interface Thrower
        extends StatelessServiceBase
    {
        void throwException()
            throws RemoteException;
    }

    public static class ThrowerImpl
        extends StatelessServiceBaseImpl
        implements Thrower
    {
        private RemoteException mException;

        public void setException
            (RemoteException exception)
        {
            mException=exception;
        }

        // Thrower.

        @Override
        public void throwException()
            throws RemoteException
        {
            throw mException;
        }
    }

    private static ThrowerImpl sThrower;
    private static StatelessServer sServer;
    private static Thrower sClient;


    @BeforeClass
    public static void setupRemoteExceptionTest()
    {
        sThrower=new ThrowerImpl();
        sServer=new StatelessServer();
        sServer.publish(sThrower,Thrower.class);
        sClient=(new StatelessClient()).getService(Thrower.class);
    }

    @AfterClass
    public static void tearDownRemoteExceptionTest()
    {
        sServer.stop();
    }
    

    private RemoteException assertRoundTripJAXBEx
        (RemoteException exception)
    {
        RemoteException result=null;
        sThrower.setException(exception);
        prepareSerWrapperFailure();
        try {
            sClient.throwException();
        } catch (RemoteException ex) {
            result=ex;
        }
        assertEquals(exception,result);
        return result;
    }

    private void singleBase
        (RemoteException ex,
         RemoteProperties properties,
         boolean wrapperSerFailure,
         boolean wrapperDeSerFailure,
         String source,
         Throwable cause,
         boolean proxyUsed)
    {
        assertEquals(EXPECTED_MESSAGE,ex.getMessage());
        assertEquals(properties,ex.getProperties());
        if (wrapperSerFailure) {
            assertSerWrapperSerFailure(ex.getProperties().getWrapper());
        } else if (ex.getProperties()!=null) {
            assertNull(ex.getProperties().getWrapper().
                       getSerializationException());
        }
        if (wrapperDeSerFailure) {
            assertSerWrapperDeSerFailure(ex.getProperties().getWrapper());
        } else if (ex.getProperties()!=null) {
            assertNull(ex.getProperties().getWrapper().
                       getDeserializationException());
        }
        assertEquals(source,ex.getStackTrace()[0].getClassName());
        assertThrowable(cause,ex.getCause(),proxyUsed);
    }

    private void singleNonSerializable
        (RemoteException server,
         RemoteException client,
         String source)
    {
        singleBase(client,
                   server.getProperties(),
                   false,
                   false,
                   source,
                   server.getCause(),
                   true);
    }

    private void singleNonDeserializable
        (RemoteException server,
         RemoteException client,
         String source)
    {
        singleBase(client,
                   server.getProperties(),
                   false,
                   true,
                   source,
                   server.getCause(),
                   true);
    }

    private void single
        (RemoteException server,
         RemoteProperties properties,
         Throwable cause)
        throws Exception
    {
        singleBase(server,properties,false,false,
                   LOCAL_PROXY_SOURCE,cause,false);
        singleBase(assertRoundTripJAXBEx(server),properties,false,false,
                   JAVA_PROXY_SOURCE,cause,false);
        singleBase(assertRoundTripJava(server),properties,false,false,
                   JAVA_PROXY_SOURCE,cause,false);
    }


    @Test
    public void basics()
        throws Exception
    {
        assertEquality(new RemoteException(),
                       new RemoteException(),
                       new RemoteException(TEST_THROWABLE),
                       new RemoteException(TEST_I18N_THROWABLE));
        single(new RemoteException(),
               null,
               null);

        assertEquality(new RemoteException(null),
                       new RemoteException(null),
                       new RemoteException(TEST_THROWABLE),
                       new RemoteException(TEST_I18N_THROWABLE));
        single(new RemoteException(null),
               null,
               null);
        assertEquals(new RemoteException(),new RemoteException(null));

        assertEquality(new RemoteException(TEST_THROWABLE),
                       new RemoteException(TEST_THROWABLE),
                       new RemoteException(),
                       new RemoteException(null),
                       new RemoteException(TEST_I18N_THROWABLE));
        single(new RemoteException(TEST_THROWABLE),
               new RemoteProperties(TEST_THROWABLE),
               TEST_THROWABLE);

        assertEquality(new RemoteException(TEST_I18N_THROWABLE),
                       new RemoteException(TEST_I18N_THROWABLE),
                       new RemoteException(),
                       new RemoteException(null),
                       new RemoteException(TEST_THROWABLE));
        single(new RemoteException(TEST_I18N_THROWABLE),
               new RemoteProperties(TEST_I18N_THROWABLE),
               TEST_I18N_THROWABLE);
    }

    @Test
    public void setters()
    {
        RemoteException ex=new RemoteException();

        RemoteProperties p=new RemoteProperties();
        ex.setProperties(p);
        assertEquals(p,ex.getProperties());

        ex.setProperties(null);
        assertNull(ex.getProperties());
    }

    @Test
    public void nonSerializableThrowable()
        throws Exception
    {
        prepareSerWrapperFailure();
        RemoteException server=new RemoteException(TEST_NONSER_THROWABLE);
        assertEquality(server,
                       new RemoteException(TEST_NONSER_THROWABLE),
                       new RemoteException(),
                       new RemoteException(null),
                       new RemoteException(TEST_THROWABLE),
                       new RemoteException(TEST_I18N_THROWABLE));
        singleBase(server,
                   new RemoteProperties(TEST_NONSER_THROWABLE),
                   true,
                   false,
                   LOCAL_PROXY_SOURCE,
                   TEST_NONSER_THROWABLE,
                   false);

        singleNonSerializable
            (server,assertRoundTripJAXBEx(server),JAVA_PROXY_SOURCE);
        singleNonSerializable
            (server,assertRoundTripJava(server),JAVA_PROXY_SOURCE);
    }

    @Test
    public void nonDeserializableThrowable()
        throws Exception
    {
        RemoteException server=new RemoteException(TEST_NONDESER_THROWABLE);
        assertEquality(server,
                       new RemoteException(TEST_NONDESER_THROWABLE),
                       new RemoteException(),
                       new RemoteException(null),
                       new RemoteException(TEST_THROWABLE),
                       new RemoteException(TEST_I18N_THROWABLE),
                       new RemoteException(TEST_NONSER_THROWABLE));
        singleBase(server,
                   new RemoteProperties(TEST_NONDESER_THROWABLE),
                   false,
                   false,
                   LOCAL_PROXY_SOURCE,
                   TEST_NONDESER_THROWABLE,
                   false);

        singleNonDeserializable
            (server,assertRoundTripJAXBEx(server),JAVA_PROXY_SOURCE);
        singleNonDeserializable
            (server,assertRoundTripJava(server),JAVA_PROXY_SOURCE);
    }
}
