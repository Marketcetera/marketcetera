package org.marketcetera.util.ws.wrappers;

import javax.jws.WebService;
import org.apache.cxf.jaxws.JaxWsClientProxy;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.ws.stateless.StatelessClient;
import org.marketcetera.util.ws.stateless.StatelessServer;
import org.marketcetera.util.ws.stateless.StatelessServiceBase;
import org.marketcetera.util.ws.stateless.StatelessServiceBaseImpl;

import static org.junit.Assert.*;
import static org.marketcetera.util.test.EqualityAssert.*;

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

    private static final String TEST_MESSAGE=
        "testMessage";
    private static final Throwable TEST_THROWABLE=
        new CloneNotSupportedException(TEST_MESSAGE);
    private static final I18NException TEST_I18N_THROWABLE=
        new I18NException
        (TEST_THROWABLE,
         new I18NBoundMessage1P(TestMessages.EXCEPTION,TEST_MESSAGE));
    private static final String LOCAL_PROXY_SOURCE=
        RemoteExceptionTest.class.getName();
    private static final String JAXB_PROXY_SOURCE=
        JaxWsClientProxy.class.getName();
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
         Throwable cause,
         RemoteProperties properties,
         String source,
         boolean proxyUsed)
    {
        assertEquals(EXPECTED_MESSAGE,ex.getMessage());
        assertThrowable(cause,ex.getCause(),proxyUsed);
        assertEquals(properties,ex.getProperties());
        if (proxyUsed) {
            assertSerWrapperFailure(ex.getProperties().getWrapper());
        }
        assertEquals(source,ex.getStackTrace()[0].getClassName());
    }

    private void single
        (RemoteException ex,
         Throwable cause)
        throws Exception
    {
        RemoteProperties p=null;
        if (cause!=null) {
            p=new RemoteProperties(cause);
        }
        singleBase
            (ex,cause,p,LOCAL_PROXY_SOURCE,false);
        singleBase
            (assertRoundTripJAXBEx(ex),cause,p,JAXB_PROXY_SOURCE,false);
        singleBase
            (assertRoundTripJava(ex),cause,p,JAVA_PROXY_SOURCE,false);
    }

    private void singleMissingResources
        (RemoteException server,
         RemoteException client,
         String source)
    {
        singleBase(client,
                   server.getCause(),
                   server.getProperties(),
                   source,
                   true);
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
               null);

        assertEquality(new RemoteException(null),
                       new RemoteException(null),
                       new RemoteException(TEST_THROWABLE),
                       new RemoteException(TEST_I18N_THROWABLE));
        single(new RemoteException(null),
               null);
        assertEquals(new RemoteException(),new RemoteException(null));

        assertEquality(new RemoteException(TEST_THROWABLE),
                       new RemoteException(TEST_THROWABLE),
                       new RemoteException(),
                       new RemoteException(null),
                       new RemoteException(TEST_I18N_THROWABLE));
        single(new RemoteException(TEST_THROWABLE),
               TEST_THROWABLE);

        assertEquality(new RemoteException(TEST_I18N_THROWABLE),
                       new RemoteException(TEST_I18N_THROWABLE),
                       new RemoteException(),
                       new RemoteException(null),
                       new RemoteException(TEST_THROWABLE));
        single(new RemoteException(TEST_I18N_THROWABLE),
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
    public void missingResources()
        throws Exception
    {
        I18NException throwable=new I18NException
            (TEST_THROWABLE,createBadProviderMessage());

        RemoteException server=new RemoteException(throwable);
        assertEquality(server,
                       new RemoteException(throwable),
                       new RemoteException(),
                       new RemoteException(null),
                       new RemoteException(TEST_THROWABLE),
                       new RemoteException(TEST_I18N_THROWABLE));
        singleBase(server,
                   throwable,
                   new RemoteProperties(throwable),
                   LOCAL_PROXY_SOURCE,
                   false);

        singleMissingResources
            (server,assertRoundTripJAXBEx(server),JAXB_PROXY_SOURCE);
        singleMissingResources
            (server,assertRoundTripJava(server),JAVA_PROXY_SOURCE);
    }
}
