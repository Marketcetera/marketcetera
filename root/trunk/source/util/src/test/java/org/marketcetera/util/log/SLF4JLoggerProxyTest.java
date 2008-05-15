package org.marketcetera.util.log;

import org.apache.log4j.Level;
import org.junit.Test;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;

public class SLF4JLoggerProxyTest
    extends TestCaseBase
{
    private static final String TEST_CATEGORY=
        SLF4JLoggerProxyTest.class.getName();
    private static final String TEST_MESSAGE=
        "Test message (expected)";
    private static final Exception TEST_THROWABLE=
        new IllegalArgumentException("Test exception (expected)");


    @Test
    public void categories()
    {
        setLevel(TEST_CATEGORY,Level.ERROR);

        SLF4JLoggerProxy.error(null,TEST_MESSAGE);
        assertSingleEvent
            (Level.ERROR,SLF4JLoggerProxy.UNKNOWN_LOGGER_NAME,TEST_MESSAGE);
        SLF4JLoggerProxy.error(TEST_CATEGORY,TEST_MESSAGE);
        assertSingleEvent
            (Level.ERROR,TEST_CATEGORY,TEST_MESSAGE);
        SLF4JLoggerProxy.error(getClass(),TEST_MESSAGE);
        assertSingleEvent
            (Level.ERROR,getClass().getName(),TEST_MESSAGE);
        SLF4JLoggerProxy.error(this,TEST_MESSAGE);
        assertSingleEvent
            (Level.ERROR,getClass().getName(),TEST_MESSAGE);
    }

    @Test
    public void messages()
    {
        setLevel(TEST_CATEGORY,Level.OFF);

        assertFalse
            (SLF4JLoggerProxy.isErrorEnabled(TEST_CATEGORY));
        SLF4JLoggerProxy.error(TEST_CATEGORY,TEST_MESSAGE);
        SLF4JLoggerProxy.error(TEST_CATEGORY,TEST_THROWABLE);
        SLF4JLoggerProxy.error(TEST_CATEGORY,TEST_MESSAGE,TEST_THROWABLE);
        SLF4JLoggerProxy.error(TEST_CATEGORY,TEST_MESSAGE+" {} {}","a");
        assertNoEvents();

        setLevel(TEST_CATEGORY,Level.ERROR);
        assertTrue
            (SLF4JLoggerProxy.isErrorEnabled(TEST_CATEGORY));
        SLF4JLoggerProxy.error(TEST_CATEGORY,TEST_MESSAGE);
        assertSingleEvent
            (Level.ERROR,TEST_CATEGORY,TEST_MESSAGE);
        SLF4JLoggerProxy.error(TEST_CATEGORY,TEST_THROWABLE);
        assertSingleEvent
            (Level.ERROR,TEST_CATEGORY,SLF4JLoggerProxy.UNKNOWN_MESSAGE);
        SLF4JLoggerProxy.error(TEST_CATEGORY,TEST_MESSAGE,TEST_THROWABLE);
        assertSingleEvent
            (Level.ERROR,TEST_CATEGORY,TEST_MESSAGE);
        SLF4JLoggerProxy.error(TEST_CATEGORY,TEST_MESSAGE+" {} {}","a");
        assertSingleEvent
            (Level.ERROR,TEST_CATEGORY,TEST_MESSAGE+" a {}");

        assertFalse
            (SLF4JLoggerProxy.isWarnEnabled(TEST_CATEGORY));
        SLF4JLoggerProxy.warn(TEST_CATEGORY,TEST_MESSAGE);
        SLF4JLoggerProxy.warn(TEST_CATEGORY,TEST_THROWABLE);
        SLF4JLoggerProxy.warn(TEST_CATEGORY,TEST_MESSAGE,TEST_THROWABLE);
        SLF4JLoggerProxy.warn(TEST_CATEGORY,TEST_MESSAGE+" {} {}","a");
        assertNoEvents();

        setLevel(TEST_CATEGORY,Level.WARN);
        assertTrue
            (SLF4JLoggerProxy.isWarnEnabled(TEST_CATEGORY));
        SLF4JLoggerProxy.warn(TEST_CATEGORY,TEST_MESSAGE);
        assertSingleEvent
            (Level.WARN,TEST_CATEGORY,TEST_MESSAGE);
        SLF4JLoggerProxy.warn(TEST_CATEGORY,TEST_THROWABLE);
        assertSingleEvent
            (Level.WARN,TEST_CATEGORY,SLF4JLoggerProxy.UNKNOWN_MESSAGE);
        SLF4JLoggerProxy.warn(TEST_CATEGORY,TEST_MESSAGE,TEST_THROWABLE);
        assertSingleEvent
            (Level.WARN,TEST_CATEGORY,TEST_MESSAGE);
        SLF4JLoggerProxy.warn(TEST_CATEGORY,TEST_MESSAGE+" {} {}","a");
        assertSingleEvent
            (Level.WARN,TEST_CATEGORY,TEST_MESSAGE+" a {}");

        assertFalse
            (SLF4JLoggerProxy.isInfoEnabled(TEST_CATEGORY));
        SLF4JLoggerProxy.info(TEST_CATEGORY,TEST_MESSAGE);
        SLF4JLoggerProxy.info(TEST_CATEGORY,TEST_THROWABLE);
        SLF4JLoggerProxy.info(TEST_CATEGORY,TEST_MESSAGE,TEST_THROWABLE);
        SLF4JLoggerProxy.info(TEST_CATEGORY,TEST_MESSAGE+" {} {}","a");
        assertNoEvents();

        setLevel(TEST_CATEGORY,Level.INFO);
        assertTrue
            (SLF4JLoggerProxy.isInfoEnabled(TEST_CATEGORY));
        SLF4JLoggerProxy.info(TEST_CATEGORY,TEST_MESSAGE);
        assertSingleEvent
            (Level.INFO,TEST_CATEGORY,TEST_MESSAGE);
        SLF4JLoggerProxy.info(TEST_CATEGORY,TEST_THROWABLE);
        assertSingleEvent
            (Level.INFO,TEST_CATEGORY,SLF4JLoggerProxy.UNKNOWN_MESSAGE);
        SLF4JLoggerProxy.info(TEST_CATEGORY,TEST_MESSAGE,TEST_THROWABLE);
        assertSingleEvent
            (Level.INFO,TEST_CATEGORY,TEST_MESSAGE);
        SLF4JLoggerProxy.info(TEST_CATEGORY,TEST_MESSAGE+" {} {}","a");
        assertSingleEvent
            (Level.INFO,TEST_CATEGORY,TEST_MESSAGE+" a {}");

        assertFalse
            (SLF4JLoggerProxy.isDebugEnabled(TEST_CATEGORY));
        SLF4JLoggerProxy.debug(TEST_CATEGORY,TEST_MESSAGE);
        SLF4JLoggerProxy.debug(TEST_CATEGORY,TEST_THROWABLE);
        SLF4JLoggerProxy.debug(TEST_CATEGORY,TEST_MESSAGE,TEST_THROWABLE);
        SLF4JLoggerProxy.debug(TEST_CATEGORY,TEST_MESSAGE+" {} {}","a");
        assertNoEvents();

        setLevel(TEST_CATEGORY,Level.DEBUG);
        assertTrue
            (SLF4JLoggerProxy.isDebugEnabled(TEST_CATEGORY));
        SLF4JLoggerProxy.debug(TEST_CATEGORY,TEST_MESSAGE);
        assertSingleEvent
            (Level.DEBUG,TEST_CATEGORY,TEST_MESSAGE);
        SLF4JLoggerProxy.debug(TEST_CATEGORY,TEST_THROWABLE);
        assertSingleEvent
            (Level.DEBUG,TEST_CATEGORY,SLF4JLoggerProxy.UNKNOWN_MESSAGE);
        SLF4JLoggerProxy.debug(TEST_CATEGORY,TEST_MESSAGE,TEST_THROWABLE);
        assertSingleEvent
            (Level.DEBUG,TEST_CATEGORY,TEST_MESSAGE);
        SLF4JLoggerProxy.debug(TEST_CATEGORY,TEST_MESSAGE+" {} {}","a");
        assertSingleEvent
            (Level.DEBUG,TEST_CATEGORY,TEST_MESSAGE+" a {}");

        assertFalse
            (SLF4JLoggerProxy.isTraceEnabled(TEST_CATEGORY));
        SLF4JLoggerProxy.trace(TEST_CATEGORY,TEST_MESSAGE);
        SLF4JLoggerProxy.trace(TEST_CATEGORY,TEST_THROWABLE);
        SLF4JLoggerProxy.trace(TEST_CATEGORY,TEST_MESSAGE,TEST_THROWABLE);
        SLF4JLoggerProxy.trace(TEST_CATEGORY,TEST_MESSAGE+" {} {}","a");
        assertNoEvents();

        setLevel(TEST_CATEGORY,Level.TRACE);
        assertTrue
            (SLF4JLoggerProxy.isTraceEnabled(TEST_CATEGORY));
        SLF4JLoggerProxy.trace(TEST_CATEGORY,TEST_MESSAGE);
        assertSingleEvent
            (Level.TRACE,TEST_CATEGORY,TEST_MESSAGE);
        SLF4JLoggerProxy.trace(TEST_CATEGORY,TEST_THROWABLE);
        assertSingleEvent
            (Level.TRACE,TEST_CATEGORY,SLF4JLoggerProxy.UNKNOWN_MESSAGE);
        SLF4JLoggerProxy.trace(TEST_CATEGORY,TEST_MESSAGE,TEST_THROWABLE);
        assertSingleEvent
            (Level.TRACE,TEST_CATEGORY,TEST_MESSAGE);
        SLF4JLoggerProxy.trace(TEST_CATEGORY,TEST_MESSAGE+" {} {}","a");
        assertSingleEvent
            (Level.TRACE,TEST_CATEGORY,TEST_MESSAGE+" a {}");
    }
}
