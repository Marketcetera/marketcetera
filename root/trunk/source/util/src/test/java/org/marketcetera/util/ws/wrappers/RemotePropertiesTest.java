package org.marketcetera.util.ws.wrappers;

import org.junit.Test;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.I18NBoundMessage1P;

import static org.junit.Assert.*;
import static org.marketcetera.util.test.EqualityAssert.*;
import static org.marketcetera.util.test.RegExAssert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

public class RemotePropertiesTest
    extends WrapperTestBase
{
    private static final String TEST_MESSAGE=
        "testMessage";
    private static final Throwable TEST_THROWABLE=
        new CloneNotSupportedException(TEST_MESSAGE);
    private static final I18NException TEST_I18N_THROWABLE=
        new I18NException
        (TEST_THROWABLE,
         new I18NBoundMessage1P(TestMessages.EXCEPTION,TEST_MESSAGE));
    private static final String[] TEST_TRACE=
        new String[] {"testTrace"};
    private static final String TEST_STRING=
        "testString";


    private void singleBase
        (RemoteProperties p,
         Throwable throwable,
         String serverMessage,
         String serverString,
         boolean proxyUsed)
    {
        if (throwable==null) {
            assertNull(p.getWrapper());
        } else if (proxyUsed) {
            assertSerWrapperFailure(p.getWrapper());
        } else {
            assertThrowable(throwable,p.getWrapper().getRaw(),false);
        }
        assertEquals(serverMessage,p.getServerMessage());
        assertEquals(serverString,p.getServerString());
        if (serverString==null) {
            assertNull(p.getTraceCapture());
        } else {
            assertEquals
                (serverString,p.getTraceCapture()[0]);
            assertMatches
                ("\\s*at\\s*"+
                 RemotePropertiesTest.class.getName().replace(".","\\.")+
                 ".*",p.getTraceCapture()[1]);
        }
        assertThrowable(throwable,p.getThrowable(),proxyUsed);
    }

    private void single
        (RemoteProperties p,
         Throwable throwable,
         String serverMessage,
         String serverString)
        throws Exception
    {
        singleBase
            (p,throwable,serverMessage,serverString,false);
        singleBase
            (assertRoundTripJAXB(p),throwable,serverMessage,serverString,false);
        singleBase
            (assertRoundTripJava(p),throwable,serverMessage,serverString,false);
    }

    private void singleMissingResources
        (RemoteProperties server,
         RemoteProperties client)
    {
        singleBase(client,
                   server.getThrowable(),
                   server.getServerMessage(),
                   server.getServerString(),
                   true);
    }


    @Test
    public void basics()
        throws Exception
    {
        assertEquality(new RemoteProperties(),
                       new RemoteProperties(),
                       new RemoteProperties(TEST_THROWABLE),
                       new RemoteProperties(TEST_I18N_THROWABLE));
        single(new RemoteProperties(),
               null,
               null,
               null);

        assertEquality(new RemoteProperties(null),
                       new RemoteProperties(null),
                       new RemoteProperties(TEST_THROWABLE),
                       new RemoteProperties(TEST_I18N_THROWABLE));
        single(new RemoteProperties(null),
               null,
               null,
               null);
        assertEquals(new RemoteProperties(),new RemoteProperties(null));

        assertEquality(new RemoteProperties(TEST_THROWABLE),
                       new RemoteProperties(TEST_THROWABLE),
                       new RemoteProperties(),
                       new RemoteProperties(null),
                       new RemoteProperties(TEST_I18N_THROWABLE));
        single(new RemoteProperties(TEST_THROWABLE),
               TEST_THROWABLE,
               TEST_THROWABLE.getLocalizedMessage(),
               CloneNotSupportedException.class.getName()+": "+TEST_MESSAGE);

        assertEquality(new RemoteProperties(TEST_I18N_THROWABLE),
                       new RemoteProperties(TEST_I18N_THROWABLE),
                       new RemoteProperties(),
                       new RemoteProperties(null),
                       new RemoteProperties(TEST_THROWABLE));
        single(new RemoteProperties(TEST_I18N_THROWABLE),
               TEST_I18N_THROWABLE,
               TEST_I18N_THROWABLE.getLocalizedDetail(),
               I18NException.class.getName()+": "+
               TEST_I18N_THROWABLE.getLocalizedMessage());

        RemoteProperties p=new RemoteProperties(TEST_I18N_THROWABLE);
        p.setWrapper(null);
        assertEquality(p,new RemoteProperties(TEST_I18N_THROWABLE),
                       new RemoteProperties(),
                       new RemoteProperties(null),
                       new RemoteProperties(TEST_THROWABLE));
        single(p, 
               null,
               TEST_I18N_THROWABLE.getLocalizedDetail(),
               I18NException.class.getName()+": "+
               TEST_I18N_THROWABLE.getLocalizedMessage());
    }

    @Test
    public void setters()
    {
        RemoteProperties p=new RemoteProperties();

        SerWrapper<Throwable> wrapper=new SerWrapper<Throwable>();
        p.setWrapper(wrapper);
        assertEquals(wrapper,p.getWrapper());

        p.setTraceCapture(TEST_TRACE);
        assertArrayEquals(TEST_TRACE,p.getTraceCapture());

        p.setServerMessage(TEST_MESSAGE);
        assertEquals(TEST_MESSAGE,p.getServerMessage());

        p.setServerString(TEST_STRING);
        assertEquals(TEST_STRING,p.getServerString());

        p.setWrapper(null);
        assertNull(p.getWrapper());

        p.setTraceCapture(null);
        assertNull(p.getTraceCapture());

        p.setServerMessage(null);
        assertNull(p.getServerMessage());

        p.setServerString(null);
        assertNull(p.getServerString());
    }

    @Test
    public void missingResources()
        throws Exception
    {
        I18NException throwable=new I18NException
            (TEST_THROWABLE,createBadProviderMessage());

        RemoteProperties server=new RemoteProperties(throwable);
        assertEquality(server,
                       new RemoteProperties(throwable),
                       new RemoteProperties(),
                       new RemoteProperties(null),
                       new RemoteProperties(TEST_THROWABLE),
                       new RemoteProperties(TEST_I18N_THROWABLE));
        singleBase(server,
                   throwable,
                   throwable.getLocalizedDetail(),
                   I18NException.class.getName()+": "+
                   throwable.getLocalizedMessage(),
                   false);

        singleMissingResources(server,assertRoundTripJAXB(server));
        singleMissingResources(server,assertRoundTripJava(server));
    }
}
