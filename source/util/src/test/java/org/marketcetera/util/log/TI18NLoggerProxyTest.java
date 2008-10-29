package org.marketcetera.util.log;

import java.util.Locale;
import org.apache.log4j.Level;
import org.junit.Test;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;
import static org.marketcetera.util.test.EqualityAssert.*;
import static org.marketcetera.util.test.SerializableAssert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

public class TI18NLoggerProxyTest
    extends TestCaseBase
{
    private static final String TEST_CATEGORY=
        "TestCategory";
    private static final String TEST_MSG_EN=
        "Test here (expected): 'a'";
    private static final String TEST_MSG_FR=
        "Test voil\u00E0 (attendu): 'a'";
    private static final String TEST_MSG_EN_NULL=
        "Test here (expected): 'null'";
    private static final String TEST_MSG_FR_NULL=
        "Test voil\u00E0 (attendu): 'null'";
    private static final String TEST_MSG_EN_NOSUB=
        "Test here (expected): ''{0}''";
    private static final String TEST_MSG_FR_NOSUB=
        "Test voil\u00E0 (attendu): ''{0}''";
    private static final Exception TEST_THROWABLE=
        new IllegalArgumentException("Test exception (expected)");
    private static final String TEST_LOCATION=
        TI18NLoggerProxyTest.class.getName();


    private void messageCheck
        (Locale locale,
         String msg,
         String msgNull,
         String msgNoSub)
    {
        ActiveLocale.setProcessLocale(locale);
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
            (Level.ERROR,TEST_CATEGORY,SLF4JLoggerProxy.UNKNOWN_MESSAGE,
             TEST_LOCATION);
        TestMessages.LOGGER.error
            (TEST_CATEGORY,TEST_THROWABLE,TestMessages.LOG_MSG,"a");
        assertSingleEvent
            (Level.ERROR,TEST_CATEGORY,msg,TEST_LOCATION);
        TestMessages.LOGGER.error(TEST_CATEGORY,TestMessages.LOG_MSG,"a");
        assertSingleEvent
            (Level.ERROR,TEST_CATEGORY,msg,TEST_LOCATION);

        TestMessages.LOGGER.error
            (TEST_CATEGORY,TEST_THROWABLE,TestMessages.LOG_MSG,(Object[])null);
        assertSingleEvent
            (Level.ERROR,TEST_CATEGORY,msgNoSub,TEST_LOCATION);
        TestMessages.LOGGER.error
            (TEST_CATEGORY,TestMessages.LOG_MSG,(Object[])null);
        assertSingleEvent
            (Level.ERROR,TEST_CATEGORY,msgNoSub,TEST_LOCATION);

        TestMessages.LOGGER.error
            (TEST_CATEGORY,TEST_THROWABLE,TestMessages.LOG_MSG,(Object)null);
        assertSingleEvent
            (Level.ERROR,TEST_CATEGORY,msgNull,TEST_LOCATION);
        TestMessages.LOGGER.error
            (TEST_CATEGORY,TestMessages.LOG_MSG,(Object)null);
        assertSingleEvent
            (Level.ERROR,TEST_CATEGORY,msgNull,TEST_LOCATION);

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
            (Level.WARN,TEST_CATEGORY,SLF4JLoggerProxy.UNKNOWN_MESSAGE,
             TEST_LOCATION);
        TestMessages.LOGGER.warn
            (TEST_CATEGORY,TEST_THROWABLE,TestMessages.LOG_MSG,"a");
        assertSingleEvent
            (Level.WARN,TEST_CATEGORY,msg,TEST_LOCATION);
        TestMessages.LOGGER.warn(TEST_CATEGORY,TestMessages.LOG_MSG,"a");
        assertSingleEvent
            (Level.WARN,TEST_CATEGORY,msg,TEST_LOCATION);

        TestMessages.LOGGER.warn
            (TEST_CATEGORY,TEST_THROWABLE,TestMessages.LOG_MSG,(Object[])null);
        assertSingleEvent
            (Level.WARN,TEST_CATEGORY,msgNoSub,TEST_LOCATION);
        TestMessages.LOGGER.warn
            (TEST_CATEGORY,TestMessages.LOG_MSG,(Object[])null);
        assertSingleEvent
            (Level.WARN,TEST_CATEGORY,msgNoSub,TEST_LOCATION);

        TestMessages.LOGGER.warn
            (TEST_CATEGORY,TEST_THROWABLE,TestMessages.LOG_MSG,(Object)null);
        assertSingleEvent
            (Level.WARN,TEST_CATEGORY,msgNull,TEST_LOCATION);
        TestMessages.LOGGER.warn
            (TEST_CATEGORY,TestMessages.LOG_MSG,(Object)null);
        assertSingleEvent
            (Level.WARN,TEST_CATEGORY,msgNull,TEST_LOCATION);

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
            (Level.INFO,TEST_CATEGORY,SLF4JLoggerProxy.UNKNOWN_MESSAGE,
             TEST_LOCATION);
        TestMessages.LOGGER.info
            (TEST_CATEGORY,TEST_THROWABLE,TestMessages.LOG_MSG,"a");
        assertSingleEvent
            (Level.INFO,TEST_CATEGORY,msg,TEST_LOCATION);
        TestMessages.LOGGER.info(TEST_CATEGORY,TestMessages.LOG_MSG,"a");
        assertSingleEvent
            (Level.INFO,TEST_CATEGORY,msg,TEST_LOCATION);

        TestMessages.LOGGER.info
            (TEST_CATEGORY,TEST_THROWABLE,TestMessages.LOG_MSG,(Object[])null);
        assertSingleEvent
            (Level.INFO,TEST_CATEGORY,msgNoSub,TEST_LOCATION);
        TestMessages.LOGGER.info
            (TEST_CATEGORY,TestMessages.LOG_MSG,(Object[])null);
        assertSingleEvent
            (Level.INFO,TEST_CATEGORY,msgNoSub,TEST_LOCATION);

        TestMessages.LOGGER.info
            (TEST_CATEGORY,TEST_THROWABLE,TestMessages.LOG_MSG,(Object)null);
        assertSingleEvent
            (Level.INFO,TEST_CATEGORY,msgNull,TEST_LOCATION);
        TestMessages.LOGGER.info
            (TEST_CATEGORY,TestMessages.LOG_MSG,(Object)null);
        assertSingleEvent
            (Level.INFO,TEST_CATEGORY,msgNull,TEST_LOCATION);

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
            (Level.DEBUG,TEST_CATEGORY,SLF4JLoggerProxy.UNKNOWN_MESSAGE,
             TEST_LOCATION);
        TestMessages.LOGGER.debug
            (TEST_CATEGORY,TEST_THROWABLE,TestMessages.LOG_MSG,"a");
        assertSingleEvent
            (Level.DEBUG,TEST_CATEGORY,msg,TEST_LOCATION);
        TestMessages.LOGGER.debug(TEST_CATEGORY,TestMessages.LOG_MSG,"a");
        assertSingleEvent
            (Level.DEBUG,TEST_CATEGORY,msg,TEST_LOCATION);

        TestMessages.LOGGER.debug
            (TEST_CATEGORY,TEST_THROWABLE,TestMessages.LOG_MSG,(Object[])null);
        assertSingleEvent
            (Level.DEBUG,TEST_CATEGORY,msgNoSub,TEST_LOCATION);
        TestMessages.LOGGER.debug
            (TEST_CATEGORY,TestMessages.LOG_MSG,(Object[])null);
        assertSingleEvent
            (Level.DEBUG,TEST_CATEGORY,msgNoSub,TEST_LOCATION);

        TestMessages.LOGGER.debug
            (TEST_CATEGORY,TEST_THROWABLE,TestMessages.LOG_MSG,(Object)null);
        assertSingleEvent
            (Level.DEBUG,TEST_CATEGORY,msgNull,TEST_LOCATION);
        TestMessages.LOGGER.debug
            (TEST_CATEGORY,TestMessages.LOG_MSG,(Object)null);
        assertSingleEvent
            (Level.DEBUG,TEST_CATEGORY,msgNull,TEST_LOCATION);

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
            (Level.TRACE,TEST_CATEGORY,SLF4JLoggerProxy.UNKNOWN_MESSAGE,
             TEST_LOCATION);
        TestMessages.LOGGER.trace
            (TEST_CATEGORY,TEST_THROWABLE,TestMessages.LOG_MSG,"a");
        assertSingleEvent
            (Level.TRACE,TEST_CATEGORY,msg,TEST_LOCATION);
        TestMessages.LOGGER.trace(TEST_CATEGORY,TestMessages.LOG_MSG,"a");
        assertSingleEvent
            (Level.TRACE,TEST_CATEGORY,msg,TEST_LOCATION);

        TestMessages.LOGGER.trace
            (TEST_CATEGORY,TEST_THROWABLE,TestMessages.LOG_MSG,(Object[])null);
        assertSingleEvent
            (Level.TRACE,TEST_CATEGORY,msgNoSub,TEST_LOCATION);
        TestMessages.LOGGER.trace
            (TEST_CATEGORY,TestMessages.LOG_MSG,(Object[])null);
        assertSingleEvent
            (Level.TRACE,TEST_CATEGORY,msgNoSub,TEST_LOCATION);

        TestMessages.LOGGER.trace
            (TEST_CATEGORY,TEST_THROWABLE,TestMessages.LOG_MSG,(Object)null);
        assertSingleEvent
            (Level.TRACE,TEST_CATEGORY,msgNull,TEST_LOCATION);
        TestMessages.LOGGER.trace
            (TEST_CATEGORY,TestMessages.LOG_MSG,(Object)null);
        assertSingleEvent
            (Level.TRACE,TEST_CATEGORY,msgNull,TEST_LOCATION);
    }


    @Test
    public void providerIsValid()
    {
        assertEquals
            (TestMessages.PROVIDER,TestMessages.LOGGER.getMessageProvider());
    }

    @Test
    public void equality()
    {
        assertEquality(new I18NLoggerProxy(new I18NMessageProvider("a")),
                       new I18NLoggerProxy(new I18NMessageProvider("a")),
                       new I18NLoggerProxy(new I18NMessageProvider("b")));
        assertSerializable(TestMessages.LOGGER);
    }

    @Test
    public void messages()
    {
        messageCheck
            (Locale.ROOT,TEST_MSG_EN,TEST_MSG_EN_NULL,TEST_MSG_EN_NOSUB);
        messageCheck
            (Locale.FRENCH,TEST_MSG_FR,TEST_MSG_FR_NULL,TEST_MSG_FR_NOSUB);
    }
}
