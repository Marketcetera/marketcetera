package org.marketcetera.util.ws.wrappers;

import org.junit.Test;
import org.marketcetera.util.except.I18NException;

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
    private static final String[] TEST_TRACE=
        new String[] {"testTrace"};
    private static final String TEST_STRING=
        "testString";
    private static final String TEST_CLASS=
        "testClass";


    private void singleBase
        (RemoteProperties p,
         SerWrapper<Throwable> wrapper,
         boolean wrapperSerFailure,
         boolean wrapperDeSerFailure,
         String serverMessage,
         String serverString,
         String serverName,
         Throwable throwable,
         boolean proxyUsed)
    {
        assertEquals(wrapper,p.getWrapper());
        if (wrapperSerFailure) {
            assertSerWrapperSerFailure(p.getWrapper());
        } else if (p.getWrapper()!=null) {
            assertNull(p.getWrapper().getSerializationException());
        }
        if (wrapperDeSerFailure) {
            assertSerWrapperDeSerFailure(p.getWrapper());
        } else if (p.getWrapper()!=null) {
            assertNull(p.getWrapper().getDeserializationException());
        }
        assertEquals(serverMessage,p.getServerMessage());
        assertEquals(serverString,p.getServerString());
        assertEquals(serverName,p.getServerName());
        if (serverString==null) {
            assertNull(p.getTraceCapture());
        } else {
            assertEquals
                (serverString,p.getTraceCapture()[0]);
            assertMatches
                ("\\s*at\\s*"+
                 WrapperTestBase.class.getName().replace(".","\\.")+
                 ".*",p.getTraceCapture()[1]);
        }
        assertThrowable(throwable,p.getThrowable(),proxyUsed);
    }

    private void singleNonSerializable
        (RemoteProperties server,
         RemoteProperties client)
    {
        singleBase(client,
                   server.getWrapper(),
                   false,
                   false,
                   server.getServerMessage(),
                   server.getServerString(),
                   server.getServerName(),
                   server.getThrowable(),
                   true);
    }

    private void singleNonDeserializable
        (RemoteProperties server,
         RemoteProperties client)
    {
        singleBase(client,
                   new SerWrapper<Throwable>(),
                   false,
                   true,
                   server.getServerMessage(),
                   server.getServerString(),
                   server.getServerName(),
                   server.getThrowable(),
                   true);
    }

    private void single
        (RemoteProperties server,
         SerWrapper<Throwable> wrapper,
         String serverMessage,
         String serverString,
         String serverName,
         Throwable throwable)
        throws Exception
    {
        singleBase(server,wrapper,false,false,
                   serverMessage,serverString,serverName,throwable,false);
        singleBase(assertRoundTripJAXB(server),wrapper,false,false,
                   serverMessage,serverString,serverName,throwable,false);
        singleBase(assertRoundTripJava(server),wrapper,false,false,
                   serverMessage,serverString,serverName,throwable,false);
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
               new SerWrapper<Throwable>(TEST_THROWABLE),
               TEST_THROWABLE.getLocalizedMessage(),
               TestThrowable.class.getName()+": "+
               TEST_MESSAGE,
               TestThrowable.class.getName(),
               TEST_THROWABLE);

        assertEquality(new RemoteProperties(TEST_I18N_THROWABLE),
                       new RemoteProperties(TEST_I18N_THROWABLE),
                       new RemoteProperties(),
                       new RemoteProperties(null),
                       new RemoteProperties(TEST_THROWABLE));
        single(new RemoteProperties(TEST_I18N_THROWABLE),
               new SerWrapper<Throwable>(TEST_I18N_THROWABLE),
               TEST_I18N_THROWABLE.getLocalizedDetail(),
               I18NException.class.getName()+": "+
               TEST_I18N_THROWABLE.getLocalizedMessage(),
               I18NException.class.getName(),
               TEST_I18N_THROWABLE);
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

        p.setServerName(TEST_CLASS);
        assertEquals(TEST_CLASS,p.getServerName());

        p.setWrapper(null);
        assertNull(p.getWrapper());

        p.setTraceCapture(null);
        assertNull(p.getTraceCapture());

        p.setServerMessage(null);
        assertNull(p.getServerMessage());

        p.setServerString(null);
        assertNull(p.getServerString());

        p.setServerName(null);
        assertNull(p.getServerName());
    }

    @Test
    public void nonSerializableThrowable()
        throws Exception
    {
        prepareSerWrapperFailure();
        RemoteProperties server=new RemoteProperties(TEST_NONSER_THROWABLE);
        assertEquality(server,
                       new RemoteProperties(TEST_NONSER_THROWABLE),
                       new RemoteProperties(),
                       new RemoteProperties(null),
                       new RemoteProperties(TEST_THROWABLE),
                       new RemoteProperties(TEST_I18N_THROWABLE));
        singleBase
            (server,
             new SerWrapper<Throwable>(),
             true,
             false,
             TEST_NONSER_THROWABLE.getLocalizedMessage(),
             TestUnserializableThrowable.class.getName()+": "+TEST_MESSAGE,
             TestUnserializableThrowable.class.getName(),
             TEST_NONSER_THROWABLE,
             false);

        singleNonSerializable(server,assertRoundTripJAXB(server));
        singleNonSerializable(server,assertRoundTripJava(server));
    }

    @Test
    public void nonDeserializableThrowable()
        throws Exception
    {
        RemoteProperties server=new RemoteProperties(TEST_NONDESER_THROWABLE);
        assertEquality(server,
                       new RemoteProperties(TEST_NONDESER_THROWABLE),
                       new RemoteProperties(),
                       new RemoteProperties(null),
                       new RemoteProperties(TEST_THROWABLE),
                       new RemoteProperties(TEST_I18N_THROWABLE),
                       new RemoteProperties(TEST_NONSER_THROWABLE));
        singleBase(server,
                   new SerWrapper<Throwable>(TEST_NONDESER_THROWABLE),
                   false,
                   false,
                   TEST_NONDESER_THROWABLE.getLocalizedDetail(),
                   I18NException.class.getName()+": "+
                   TEST_NONDESER_THROWABLE.getLocalizedMessage(),
                   I18NException.class.getName(),
                   TEST_NONDESER_THROWABLE,
                   false);

        singleNonDeserializable(server,assertRoundTripJAXB(server));
        singleNonDeserializable(server,assertRoundTripJava(server));
    }
}
