package org.marketcetera.util.ws.wrappers;

import org.junit.Test;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.test.TestCaseBase;

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
    private static final String[] TEST_TRACE=
        new String[] {"testTrace"};
    private static final String TEST_STRING=
        "testString";


    private static void single
        (RemoteProperties p,
         Throwable throwable,
         String serverMessage,
         String serverString)
    {
        assertEquals(new SerWrapper<Throwable>(throwable),p.getWrapper());
        assertEquals(serverMessage,p.getServerMessage());
        assertEquals(serverString,p.getServerString());
        assertEquals
            (serverString,p.getTraceCapture()[0]);
        assertMatches
            ("\\s*at\\s*"+
             RemotePropertiesTest.class.getName().replace(".","\\.")+
             ".*",p.getTraceCapture()[1]);
    }


    @Test
    public void all()
    {
        assertEquality(new RemoteProperties(TEST_THROWABLE),
                       new RemoteProperties(TEST_THROWABLE),
                       new RemoteProperties(),
                       new RemoteProperties(TEST_I18N_THROWABLE));
        assertEquality(new RemoteProperties(TEST_I18N_THROWABLE),
                       new RemoteProperties(TEST_I18N_THROWABLE),
                       new RemoteProperties(),
                       new RemoteProperties(TEST_THROWABLE));

        RemoteProperties p=new RemoteProperties(TEST_I18N_THROWABLE);
        p.setWrapper(null);
        assertEquality(p,new RemoteProperties(TEST_I18N_THROWABLE),
                       new RemoteProperties(),
                       new RemoteProperties(TEST_THROWABLE));

        single
            (new RemoteProperties(TEST_THROWABLE),
             TEST_THROWABLE,
             TEST_THROWABLE.getLocalizedMessage(),
             CloneNotSupportedException.class.getName()+": "+TEST_MESSAGE);
        single
            (new RemoteProperties(TEST_I18N_THROWABLE),
             TEST_I18N_THROWABLE,
             TEST_I18N_THROWABLE.getLocalizedDetail(),
             I18NException.class.getName()+": "+
             TEST_I18N_THROWABLE.getLocalizedMessage());

        p=new RemoteProperties();
        assertNull(p.getWrapper());
        assertNull(p.getTraceCapture());
        assertNull(p.getServerMessage());
        assertNull(p.getServerString());

        SerWrapper<Throwable> wrapper=new SerWrapper<Throwable>();
        p.setWrapper(wrapper);
        assertEquals(wrapper,p.getWrapper());

        p.setTraceCapture(TEST_TRACE);
        assertArrayEquals(TEST_TRACE,p.getTraceCapture());

        p.setServerMessage(TEST_MESSAGE);
        assertEquals(TEST_MESSAGE,p.getServerMessage());

        p.setServerString(TEST_STRING);
        assertEquals(TEST_STRING,p.getServerString());
    }
}
