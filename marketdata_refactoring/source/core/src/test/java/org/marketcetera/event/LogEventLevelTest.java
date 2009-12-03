package org.marketcetera.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.apache.log4j.Level;
import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.event.impl.LogEventBuilder;
import org.marketcetera.util.test.LogTestAssist;

/* $License$ */

/**
 * Tests {@link LogEventLevel}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.0.0
 */
public class LogEventLevelTest
        implements Messages
{
    /**
     * Executed once before all tests.
     *
     * @throws Exception if an unexpected error occurs
     */
    @BeforeClass
    public static void once()
            throws Exception
    {
        LoggerConfiguration.logSetup();
    }
    /**
     * Tests {@link LogEventLevel#shouldLog(org.marketcetera.event.LogEvent, String)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void shouldLog()
            throws Exception
    {
        LogTestAssist.setLevel(category,
                               Level.ERROR);
        // test null values
        assertFalse(LogEventLevel.shouldLog(null,
                                            category));
        assertFalse(LogEventLevel.shouldLog(debugLogEvent,
                                            null));
        // test the wrong category
        LogTestAssist.setLevel("some.other.category",
                               Level.ALL);
        doLogTest(false,
                  false,
                  false,
                  true);
        // test the correct category
        doLogTest(false,
                  false,
                  false,
                  true);
        LogTestAssist.setLevel(category,
                               Level.WARN);
        doLogTest(false,
                  false,
                  true,
                  true);
        LogTestAssist.setLevel(category,
                               Level.INFO);
        doLogTest(false,
                  true,
                  true,
                  true);
        LogTestAssist.setLevel(category,
                               Level.DEBUG);
        doLogTest(true,
                  true,
                  true,
                  true);
        LogTestAssist.setLevel(category,
                               Level.ALL);
        doLogTest(true,
                  true,
                  true,
                  true);
    }
    /**
     * Executes a single iteration of the log test.
     *
     * @param inDebug a <code>boolean</code> value indicating if a message at debug level should be logged
     * @param inInfo a <code>boolean</code> value indicating if a message at info level should be logged
     * @param inWarn a <code>boolean</code> value indicating if a message at warn level should be logged
     * @param inError a <code>boolean</code> value indicating if a message at error level should be logged
     * @throws Exception if an unexpected error occurs
     */
    private void doLogTest(boolean inDebug,
                           boolean inInfo,
                           boolean inWarn,
                           boolean inError)
            throws Exception
    {
        assertEquals(inDebug,
                     LogEventLevel.shouldLog(debugLogEvent,
                                             category));
        assertEquals(inInfo,
                     LogEventLevel.shouldLog(infoLogEvent,
                                             category));
        assertEquals(inWarn,
                     LogEventLevel.shouldLog(warnLogEvent,
                                             category));
        assertEquals(inError,
                     LogEventLevel.shouldLog(errorLogEvent,
                                             category));
    }
    /**
     * the category for which to manipulate log settings
     */
    private final static String category = LogEventLevelTest.class.getName();
    /**
     * test debug log event
     */
    private final LogEvent debugLogEvent = LogEventBuilder.debug().withMessage(VALIDATION_NULL_AMOUNT).create();
    /**
     * test info log event
     */
    private final LogEvent infoLogEvent = LogEventBuilder.info().withMessage(VALIDATION_NULL_AMOUNT).create();
    /**
     * test warn log event
     */
    private final LogEvent warnLogEvent = LogEventBuilder.warn().withMessage(VALIDATION_NULL_AMOUNT).create();
    /**
     * test error log event
     */
    private final LogEvent errorLogEvent = LogEventBuilder.error().withMessage(VALIDATION_NULL_AMOUNT).create();
}
