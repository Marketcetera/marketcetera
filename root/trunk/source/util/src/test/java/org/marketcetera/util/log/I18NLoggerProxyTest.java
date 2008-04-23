package org.marketcetera.util.log;

import java.util.Locale;
import org.apache.log4j.Level;
import org.junit.Test;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;

public class I18NLoggerProxyTest
	extends TestCaseBase
{
    private static final String TEST_CATEGORY=
        I18NLoggerProxyTest.class.getName();
    private static final String TEST_MSG_ENGLISH=
        "Test here (expected): 'a'";
    private static final String TEST_MSG_FRENCH=
        "Test voil\u00E0 (attendu): 'a'";
    private static final Exception TEST_THROWABLE=
        new IllegalArgumentException("Test exception (expected)");


    @Test
    public void providerIsValid()
    {
        assertEquals
            (TestMessages.PROVIDER,TestMessages.LOGGER.getMessageProvider());
    }

    @Test
    public void messages()
    {
        Messages.PROVIDER.setLocale(Locale.US);
        setLevel(TEST_CATEGORY,Level.OFF);

        TestMessages.LOGGER.error
            (TEST_CATEGORY,TEST_THROWABLE);
        TestMessages.LOGGER.error
            (TEST_CATEGORY,TEST_THROWABLE,TestMessages.LOG_MSG,"a");
        TestMessages.LOGGER.error
            (TEST_CATEGORY,TestMessages.LOG_MSG,"a");
        assertNoEvents();

        setLevel(TEST_CATEGORY,Level.ERROR);
        TestMessages.LOGGER.error
            (TEST_CATEGORY,TEST_THROWABLE);
        assertSingleEvent
            (Level.ERROR,TEST_CATEGORY,SLF4JLoggerProxy.UNKNOWN_MESSAGE);
        TestMessages.LOGGER.error
            (TEST_CATEGORY,TEST_THROWABLE,TestMessages.LOG_MSG,"a");
        assertSingleEvent
            (Level.ERROR,TEST_CATEGORY,TEST_MSG_ENGLISH);
        TestMessages.LOGGER.error(TEST_CATEGORY,TestMessages.LOG_MSG,"a");
        assertSingleEvent
            (Level.ERROR,TEST_CATEGORY,TEST_MSG_ENGLISH);

        TestMessages.LOGGER.warn
            (TEST_CATEGORY,TEST_THROWABLE);
        TestMessages.LOGGER.warn
            (TEST_CATEGORY,TEST_THROWABLE,TestMessages.LOG_MSG,"a");
        TestMessages.LOGGER.warn
            (TEST_CATEGORY,TestMessages.LOG_MSG,"a");
        assertNoEvents();

        setLevel(TEST_CATEGORY,Level.WARN);
        TestMessages.LOGGER.warn
            (TEST_CATEGORY,TEST_THROWABLE);
        assertSingleEvent
            (Level.WARN,TEST_CATEGORY,SLF4JLoggerProxy.UNKNOWN_MESSAGE);
        TestMessages.LOGGER.warn
            (TEST_CATEGORY,TEST_THROWABLE,TestMessages.LOG_MSG,"a");
        assertSingleEvent
            (Level.WARN,TEST_CATEGORY,TEST_MSG_ENGLISH);
        TestMessages.LOGGER.warn(TEST_CATEGORY,TestMessages.LOG_MSG,"a");
        assertSingleEvent
            (Level.WARN,TEST_CATEGORY,TEST_MSG_ENGLISH);

        TestMessages.LOGGER.info
            (TEST_CATEGORY,TEST_THROWABLE);
        TestMessages.LOGGER.info
            (TEST_CATEGORY,TEST_THROWABLE,TestMessages.LOG_MSG,"a");
        TestMessages.LOGGER.info
            (TEST_CATEGORY,TestMessages.LOG_MSG,"a");
        assertNoEvents();

        setLevel(TEST_CATEGORY,Level.INFO);
        TestMessages.LOGGER.info
            (TEST_CATEGORY,TEST_THROWABLE);
        assertSingleEvent
            (Level.INFO,TEST_CATEGORY,SLF4JLoggerProxy.UNKNOWN_MESSAGE);
        TestMessages.LOGGER.info
            (TEST_CATEGORY,TEST_THROWABLE,TestMessages.LOG_MSG,"a");
        assertSingleEvent
            (Level.INFO,TEST_CATEGORY,TEST_MSG_ENGLISH);
        TestMessages.LOGGER.info(TEST_CATEGORY,TestMessages.LOG_MSG,"a");
        assertSingleEvent
            (Level.INFO,TEST_CATEGORY,TEST_MSG_ENGLISH);

        TestMessages.LOGGER.debug
            (TEST_CATEGORY,TEST_THROWABLE);
        TestMessages.LOGGER.debug
            (TEST_CATEGORY,TEST_THROWABLE,TestMessages.LOG_MSG,"a");
        TestMessages.LOGGER.debug
            (TEST_CATEGORY,TestMessages.LOG_MSG,"a");
        assertNoEvents();

        setLevel(TEST_CATEGORY,Level.DEBUG);
        TestMessages.LOGGER.debug
            (TEST_CATEGORY,TEST_THROWABLE);
        assertSingleEvent
            (Level.DEBUG,TEST_CATEGORY,SLF4JLoggerProxy.UNKNOWN_MESSAGE);
        TestMessages.LOGGER.debug
            (TEST_CATEGORY,TEST_THROWABLE,TestMessages.LOG_MSG,"a");
        assertSingleEvent
            (Level.DEBUG,TEST_CATEGORY,TEST_MSG_ENGLISH);
        TestMessages.LOGGER.debug(TEST_CATEGORY,TestMessages.LOG_MSG,"a");
        assertSingleEvent
            (Level.DEBUG,TEST_CATEGORY,TEST_MSG_ENGLISH);

        TestMessages.LOGGER.trace
            (TEST_CATEGORY,TEST_THROWABLE);
        TestMessages.LOGGER.trace
            (TEST_CATEGORY,TEST_THROWABLE,TestMessages.LOG_MSG,"a");
        TestMessages.LOGGER.trace
            (TEST_CATEGORY,TestMessages.LOG_MSG,"a");
        assertNoEvents();

        setLevel(TEST_CATEGORY,Level.TRACE);
        TestMessages.LOGGER.trace
            (TEST_CATEGORY,TEST_THROWABLE);
        assertSingleEvent
            (Level.TRACE,TEST_CATEGORY,SLF4JLoggerProxy.UNKNOWN_MESSAGE);
        TestMessages.LOGGER.trace
            (TEST_CATEGORY,TEST_THROWABLE,TestMessages.LOG_MSG,"a");
        assertSingleEvent
            (Level.TRACE,TEST_CATEGORY,TEST_MSG_ENGLISH);
        TestMessages.LOGGER.trace(TEST_CATEGORY,TestMessages.LOG_MSG,"a");
        assertSingleEvent
            (Level.TRACE,TEST_CATEGORY,TEST_MSG_ENGLISH);

        Messages.PROVIDER.setLocale(Locale.FRENCH);
        TestMessages.LOGGER.trace
            (TEST_CATEGORY,TEST_THROWABLE,TestMessages.LOG_MSG,"a");
        assertSingleEvent
            (Level.TRACE,TEST_CATEGORY,TEST_MSG_FRENCH);
        TestMessages.LOGGER.trace(TEST_CATEGORY,TestMessages.LOG_MSG,"a");
        assertSingleEvent
            (Level.TRACE,TEST_CATEGORY,TEST_MSG_FRENCH);
    }
}
