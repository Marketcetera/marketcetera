package org.marketcetera.util.log;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.marketcetera.util.test.TestCaseBase;

/**
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

public class TSLF4JLoggerProxyTest
    extends TestCaseBase
{
    private static final String TEST_CATEGORY=
        "TestCategory";
    private static final String TEST_MESSAGE=
        "Test message (expected)";
    private static final Exception TEST_THROWABLE=
        new IllegalArgumentException("Test exception (expected)");


    @Test
    public void categories()
    {
        SLF4JLoggerProxy.error(null,TEST_MESSAGE);
        SLF4JLoggerProxy.error(TEST_CATEGORY,TEST_MESSAGE);
        SLF4JLoggerProxy.error(getClass(),TEST_MESSAGE);
        SLF4JLoggerProxy.error(this,TEST_MESSAGE);
    }

    @Test
    public void messages()
    {

        assertTrue
            (SLF4JLoggerProxy.isErrorEnabled(TEST_CATEGORY));
        SLF4JLoggerProxy.error(TEST_CATEGORY,TEST_MESSAGE);
        SLF4JLoggerProxy.error(TEST_CATEGORY,TEST_THROWABLE);
        SLF4JLoggerProxy.error(TEST_CATEGORY,TEST_MESSAGE,TEST_THROWABLE);
        SLF4JLoggerProxy.error(TEST_CATEGORY,TEST_MESSAGE+" {} {}","a");
        SLF4JLoggerProxy.error
            (TEST_CATEGORY,TEST_THROWABLE,TEST_MESSAGE+" {}","a");
        assertTrue
            (SLF4JLoggerProxy.isErrorEnabled(TEST_CATEGORY));
        SLF4JLoggerProxy.error(TEST_CATEGORY,TEST_MESSAGE);
        SLF4JLoggerProxy.error(TEST_CATEGORY,TEST_THROWABLE);
        SLF4JLoggerProxy.error(TEST_CATEGORY,TEST_MESSAGE,TEST_THROWABLE);
        SLF4JLoggerProxy.error(TEST_CATEGORY,TEST_MESSAGE+" {} {}","a");
        SLF4JLoggerProxy.error(TEST_CATEGORY,TEST_MESSAGE,"a");
        SLF4JLoggerProxy.error
            (TEST_CATEGORY,TEST_THROWABLE,TEST_MESSAGE+" {}","a");

        assertFalse
            (SLF4JLoggerProxy.isWarnEnabled(TEST_CATEGORY));
        SLF4JLoggerProxy.warn(TEST_CATEGORY,TEST_MESSAGE);
        SLF4JLoggerProxy.warn(TEST_CATEGORY,TEST_THROWABLE);
        SLF4JLoggerProxy.warn(TEST_CATEGORY,TEST_MESSAGE,TEST_THROWABLE);
        SLF4JLoggerProxy.warn(TEST_CATEGORY,TEST_MESSAGE+" {} {}","a");
        SLF4JLoggerProxy.warn
            (TEST_CATEGORY,TEST_THROWABLE,TEST_MESSAGE+" {}","a");
        assertFalse
            (SLF4JLoggerProxy.isWarnEnabled(TEST_CATEGORY));
        SLF4JLoggerProxy.warn(TEST_CATEGORY,TEST_MESSAGE);
        SLF4JLoggerProxy.warn(TEST_CATEGORY,TEST_THROWABLE);
        SLF4JLoggerProxy.warn(TEST_CATEGORY,TEST_MESSAGE,TEST_THROWABLE);
        SLF4JLoggerProxy.warn(TEST_CATEGORY,TEST_MESSAGE+" {} {}","a");
        SLF4JLoggerProxy.warn(TEST_CATEGORY,TEST_MESSAGE,"a");
        SLF4JLoggerProxy.warn
            (TEST_CATEGORY,TEST_THROWABLE,TEST_MESSAGE+" {}","a");

        assertFalse
            (SLF4JLoggerProxy.isInfoEnabled(TEST_CATEGORY));
        SLF4JLoggerProxy.info(TEST_CATEGORY,TEST_MESSAGE);
        SLF4JLoggerProxy.info(TEST_CATEGORY,TEST_THROWABLE);
        SLF4JLoggerProxy.info(TEST_CATEGORY,TEST_MESSAGE,TEST_THROWABLE);
        SLF4JLoggerProxy.info(TEST_CATEGORY,TEST_MESSAGE+" {} {}","a");
        SLF4JLoggerProxy.info
            (TEST_CATEGORY,TEST_THROWABLE,TEST_MESSAGE+" {}","a");

        assertFalse
            (SLF4JLoggerProxy.isInfoEnabled(TEST_CATEGORY));
        SLF4JLoggerProxy.info(TEST_CATEGORY,TEST_MESSAGE);
        SLF4JLoggerProxy.info(TEST_CATEGORY,TEST_THROWABLE);
        SLF4JLoggerProxy.info(TEST_CATEGORY,TEST_MESSAGE,TEST_THROWABLE);
        SLF4JLoggerProxy.info(TEST_CATEGORY,TEST_MESSAGE+" {} {}","a");
        SLF4JLoggerProxy.info(TEST_CATEGORY,TEST_MESSAGE,"a");
        SLF4JLoggerProxy.info
            (TEST_CATEGORY,TEST_THROWABLE,TEST_MESSAGE+" {}","a");

        assertFalse
            (SLF4JLoggerProxy.isDebugEnabled(TEST_CATEGORY));
        SLF4JLoggerProxy.debug(TEST_CATEGORY,TEST_MESSAGE);
        SLF4JLoggerProxy.debug(TEST_CATEGORY,TEST_THROWABLE);
        SLF4JLoggerProxy.debug(TEST_CATEGORY,TEST_MESSAGE,TEST_THROWABLE);
        SLF4JLoggerProxy.debug(TEST_CATEGORY,TEST_MESSAGE+" {} {}","a");
        SLF4JLoggerProxy.debug
            (TEST_CATEGORY,TEST_THROWABLE,TEST_MESSAGE+" {}","a");

        assertFalse
            (SLF4JLoggerProxy.isDebugEnabled(TEST_CATEGORY));
        SLF4JLoggerProxy.debug(TEST_CATEGORY,TEST_MESSAGE);
        SLF4JLoggerProxy.debug(TEST_CATEGORY,TEST_THROWABLE);
        SLF4JLoggerProxy.debug(TEST_CATEGORY,TEST_MESSAGE,TEST_THROWABLE);
        SLF4JLoggerProxy.debug(TEST_CATEGORY,TEST_MESSAGE+" {} {}","a");
        SLF4JLoggerProxy.debug(TEST_CATEGORY,TEST_MESSAGE,"a");
        SLF4JLoggerProxy.debug
            (TEST_CATEGORY,TEST_THROWABLE,TEST_MESSAGE+" {}","a");

        assertFalse
            (SLF4JLoggerProxy.isTraceEnabled(TEST_CATEGORY));
        SLF4JLoggerProxy.trace(TEST_CATEGORY,TEST_MESSAGE);
        SLF4JLoggerProxy.trace(TEST_CATEGORY,TEST_THROWABLE);
        SLF4JLoggerProxy.trace(TEST_CATEGORY,TEST_MESSAGE,TEST_THROWABLE);
        SLF4JLoggerProxy.trace(TEST_CATEGORY,TEST_MESSAGE+" {} {}","a");
        SLF4JLoggerProxy.trace
            (TEST_CATEGORY,TEST_THROWABLE,TEST_MESSAGE+" {}","a");

        assertFalse
            (SLF4JLoggerProxy.isTraceEnabled(TEST_CATEGORY));
        SLF4JLoggerProxy.trace(TEST_CATEGORY,TEST_MESSAGE);
        SLF4JLoggerProxy.trace(TEST_CATEGORY,TEST_THROWABLE);
        SLF4JLoggerProxy.trace(TEST_CATEGORY,TEST_MESSAGE,TEST_THROWABLE);
        SLF4JLoggerProxy.trace(TEST_CATEGORY,TEST_MESSAGE+" {} {}","a");
        SLF4JLoggerProxy.trace(TEST_CATEGORY,TEST_MESSAGE,"a");
        SLF4JLoggerProxy.trace
            (TEST_CATEGORY,TEST_THROWABLE,TEST_MESSAGE+" {}","a");
    }
}
