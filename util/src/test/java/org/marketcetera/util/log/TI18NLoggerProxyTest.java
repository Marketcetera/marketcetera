package org.marketcetera.util.log;

import static org.junit.Assert.assertEquals;
import static org.marketcetera.util.test.EqualityAssert.assertEquality;
import static org.marketcetera.util.test.SerializableAssert.assertSerializable;

import java.util.Locale;

import org.junit.Test;
import org.marketcetera.util.test.TestCaseBase;

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


    private void messageCheck
        (Locale locale,
         String msg,
         String msgNull,
         String msgNoSub)
    {
        ActiveLocale.setProcessLocale(locale);

        TestMessages.LOGGER.error
            (TEST_CATEGORY,TEST_THROWABLE);
        TestMessages.LOGGER.error
            (TEST_CATEGORY,TEST_THROWABLE,TestMessages.LOG_MSG,"a");
        TestMessages.LOGGER.error
            (TEST_CATEGORY,TestMessages.LOG_MSG,"a");

        TestMessages.LOGGER.error
            (TEST_CATEGORY,TEST_THROWABLE);
        TestMessages.LOGGER.error
            (TEST_CATEGORY,TEST_THROWABLE,TestMessages.LOG_MSG,"a");
        TestMessages.LOGGER.error(TEST_CATEGORY,TestMessages.LOG_MSG,"a");

        TestMessages.LOGGER.error
            (TEST_CATEGORY,TEST_THROWABLE,TestMessages.LOG_MSG,(Object[])null);
        TestMessages.LOGGER.error
            (TEST_CATEGORY,TestMessages.LOG_MSG,(Object[])null);

        TestMessages.LOGGER.error
            (TEST_CATEGORY,TEST_THROWABLE,TestMessages.LOG_MSG,(Object)null);
        TestMessages.LOGGER.error
            (TEST_CATEGORY,TestMessages.LOG_MSG,(Object)null);

        TestMessages.LOGGER.warn
            (TEST_CATEGORY,TEST_THROWABLE);
        TestMessages.LOGGER.warn
            (TEST_CATEGORY,TEST_THROWABLE,TestMessages.LOG_MSG,"a");
        TestMessages.LOGGER.warn
            (TEST_CATEGORY,TestMessages.LOG_MSG,"a");

        TestMessages.LOGGER.warn
            (TEST_CATEGORY,TEST_THROWABLE);
        TestMessages.LOGGER.warn
            (TEST_CATEGORY,TEST_THROWABLE,TestMessages.LOG_MSG,"a");
        TestMessages.LOGGER.warn(TEST_CATEGORY,TestMessages.LOG_MSG,"a");

        TestMessages.LOGGER.warn
            (TEST_CATEGORY,TEST_THROWABLE,TestMessages.LOG_MSG,(Object[])null);
        TestMessages.LOGGER.warn
            (TEST_CATEGORY,TestMessages.LOG_MSG,(Object[])null);

        TestMessages.LOGGER.warn
            (TEST_CATEGORY,TEST_THROWABLE,TestMessages.LOG_MSG,(Object)null);
        TestMessages.LOGGER.warn
            (TEST_CATEGORY,TestMessages.LOG_MSG,(Object)null);

        TestMessages.LOGGER.info
            (TEST_CATEGORY,TEST_THROWABLE);
        TestMessages.LOGGER.info
            (TEST_CATEGORY,TEST_THROWABLE,TestMessages.LOG_MSG,"a");
        TestMessages.LOGGER.info
            (TEST_CATEGORY,TestMessages.LOG_MSG,"a");

        TestMessages.LOGGER.info
            (TEST_CATEGORY,TEST_THROWABLE);
        TestMessages.LOGGER.info
            (TEST_CATEGORY,TEST_THROWABLE,TestMessages.LOG_MSG,"a");
        TestMessages.LOGGER.info(TEST_CATEGORY,TestMessages.LOG_MSG,"a");

        TestMessages.LOGGER.info
            (TEST_CATEGORY,TEST_THROWABLE,TestMessages.LOG_MSG,(Object[])null);
        TestMessages.LOGGER.info
            (TEST_CATEGORY,TestMessages.LOG_MSG,(Object[])null);

        TestMessages.LOGGER.info
            (TEST_CATEGORY,TEST_THROWABLE,TestMessages.LOG_MSG,(Object)null);
        TestMessages.LOGGER.info
            (TEST_CATEGORY,TestMessages.LOG_MSG,(Object)null);

        TestMessages.LOGGER.debug
            (TEST_CATEGORY,TEST_THROWABLE);
        TestMessages.LOGGER.debug
            (TEST_CATEGORY,TEST_THROWABLE,TestMessages.LOG_MSG,"a");
        TestMessages.LOGGER.debug
            (TEST_CATEGORY,TestMessages.LOG_MSG,"a");

        TestMessages.LOGGER.debug
            (TEST_CATEGORY,TEST_THROWABLE);
        TestMessages.LOGGER.debug
            (TEST_CATEGORY,TEST_THROWABLE,TestMessages.LOG_MSG,"a");
        TestMessages.LOGGER.debug(TEST_CATEGORY,TestMessages.LOG_MSG,"a");

        TestMessages.LOGGER.debug
            (TEST_CATEGORY,TEST_THROWABLE,TestMessages.LOG_MSG,(Object[])null);
        TestMessages.LOGGER.debug
            (TEST_CATEGORY,TestMessages.LOG_MSG,(Object[])null);

        TestMessages.LOGGER.debug
            (TEST_CATEGORY,TEST_THROWABLE,TestMessages.LOG_MSG,(Object)null);
        TestMessages.LOGGER.debug
            (TEST_CATEGORY,TestMessages.LOG_MSG,(Object)null);

        TestMessages.LOGGER.trace
            (TEST_CATEGORY,TEST_THROWABLE);
        TestMessages.LOGGER.trace
            (TEST_CATEGORY,TEST_THROWABLE,TestMessages.LOG_MSG,"a");
        TestMessages.LOGGER.trace
            (TEST_CATEGORY,TestMessages.LOG_MSG,"a");

        TestMessages.LOGGER.trace
            (TEST_CATEGORY,TEST_THROWABLE);
        TestMessages.LOGGER.trace
            (TEST_CATEGORY,TEST_THROWABLE,TestMessages.LOG_MSG,"a");
        TestMessages.LOGGER.trace(TEST_CATEGORY,TestMessages.LOG_MSG,"a");

        TestMessages.LOGGER.trace
            (TEST_CATEGORY,TEST_THROWABLE,TestMessages.LOG_MSG,(Object[])null);
        TestMessages.LOGGER.trace
            (TEST_CATEGORY,TestMessages.LOG_MSG,(Object[])null);

        TestMessages.LOGGER.trace
            (TEST_CATEGORY,TEST_THROWABLE,TestMessages.LOG_MSG,(Object)null);
        TestMessages.LOGGER.trace
            (TEST_CATEGORY,TestMessages.LOG_MSG,(Object)null);
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
