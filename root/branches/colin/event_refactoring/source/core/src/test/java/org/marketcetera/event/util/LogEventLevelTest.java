package org.marketcetera.event.util;

import static org.junit.Assert.assertFalse;

import org.apache.log4j.Level;
import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.event.LogEvent;
import org.marketcetera.event.Messages;
import org.marketcetera.event.impl.LogEventBuilder;
import org.marketcetera.util.test.LogTestAssist;

/* $License$ */

/**
 * Tests {@link LogEventLevel}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
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
        // test null values
        assertFalse(LogEventLevel.shouldLog(null,
                                            LogEventLevelTest.class.getName()));
        assertFalse(LogEventLevel.shouldLog(debugLogEvent,
                                            null));
        // right level, wrong category
        
    }
    private final LogEvent debugLogEvent = LogEventBuilder.debug().withMessage(VALIDATION_NULL_AMOUNT).create();
    private final LogEvent infoLogEvent = LogEventBuilder.info().withMessage(VALIDATION_NULL_AMOUNT).create();
    private final LogEvent warnLogEvent = LogEventBuilder.warn().withMessage(VALIDATION_NULL_AMOUNT).create();
    private final LogEvent errorLogEvent = LogEventBuilder.error().withMessage(VALIDATION_NULL_AMOUNT).create();
    private final LogTestAssist debugLogTester = new LogTestAssist(LogEventLevelTest.class.getName(),
                                                                  Level.DEBUG);
}
