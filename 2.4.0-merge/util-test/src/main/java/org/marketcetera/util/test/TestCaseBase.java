package org.marketcetera.util.test;

import java.io.File;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Before;


/**
 * Base class for test cases.
 * 
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

public class TestCaseBase
{

    // CLASS DATA.

    /**
     * The root directory for test files.
     */

    protected static final String DIR_ROOT=
        "src"+File.separator+"test"+ //$NON-NLS-1$ //$NON-NLS-2$
        File.separator+"sample_data"; //$NON-NLS-1$

    /**
     * The root directory for target files.
     */

    protected static final String DIR_TARGET=
        "target"; //$NON-NLS-1$

    /**
     * The root directory for class files.
     */

    protected static final String DIR_CLASSES=
        DIR_TARGET+File.separator+"classes"; //$NON-NLS-1$

    /**
     * The root directory for test class files.
     */

    protected static final String DIR_TEST_CLASSES=
        DIR_TARGET+File.separator+"test-classes"; //$NON-NLS-1$


    // INSTANCE DATA.

    private LogTestAssist mLogAssist;


    // CLASS METHODS.

    /**
     * Sets the level of the root logger to the given level.
     *
     * @param level The level.
     */

    protected static void setDefaultLevel
        (Level level)
    {
        LogTestAssist.setDefaultLevel(level);
    }

    /**
     * Sets the level of the logger with the given name to the given
     * level.
     *
     * @param name The logger name.
     * @param level The level.
     */

    protected static void setLevel
        (String name,
         Level level)
    {
        LogTestAssist.setLevel(name,level);
    }

    /**
     * Asserts that the contents of the given event match the given
     * expected level, logger name, message, and location.
     *
     * @param event The event.
     * @param level The expected level. Use null to indicate "don't
     * care".
     * @param logger The expected logger name. Use null to indicate
     * "don't care".
     * @param message The expected message. Use null to indicate
     * "don't care".
     * @param location The expected location. Use null to indicate
     * "don't care".
     */

    protected static void assertEvent
        (LoggingEvent event,
         Level level,
         String logger,
         String message,
         String location)
    {
        LogTestAssist.assertEvent(event,level,logger,
                                  message,location);
    }


    // INSTANCE METHODS.

    /**
     * Sets up the receiver's collector of retained events as part of
     * each test's fixture.
     */

    @Before
    public void setupTestCaseBase()
    {
        mLogAssist=new LogTestAssist();
        BasicConfigurator.configure(getAppender());
    }

    /**
     * Returns the receiver's log message helper.
     *
     * @return The helper.
     */

    protected LogTestAssist getLogAssist()
    {
        return mLogAssist;
    }

    /**
     * Returns the receiver's collector (appender) of retained events.
     *
     * @return The appender.
     */

    protected MemoryAppender getAppender()
    {
        return getLogAssist().getAppender();
    }

    /**
     * Asserts that the receiver's collector contains the given number
     * of events.
     *
     * @param count The number of events.
     */

    protected void assertEventCount
        (int count)
    {
        getLogAssist().assertEventCount(count);
    }

    /**
     * Asserts that the receiver's collector contains no events.
     */

    protected void assertNoEvents()
    {
        getLogAssist().assertNoEvents();
    }

    /**
     * Asserts that the contents of the receiver's collector's most
     * recent event match the given expected level, logger name,
     * message, and location. The assertion fails if the receiver has
     * no events.
     *
     * @param level The expected level. Use null to indicate "don't
     * care".
     * @param logger The expected logger name. Use null to indicate
     * "don't care".
     * @param message The expected message. Use null to indicate
     * "don't care".
     * @param location The expected location. Use null to indicate
     * "don't care".
     */

    protected void assertLastEvent
        (Level level,
         String logger,
         String message,
         String location)
    {
        getLogAssist().assertLastEvent(level,logger,message,location);
    }

    /**
     * Asserts that at least one of the receiver's collector's events
     * matches the given expected level, logger name, message, and
     * location. If the collector has no events, the assertion fails.
     *
     * @param level The expected level. Use null to indicate "don't
     * care".
     * @param logger The expected logger name. Use null to indicate
     * "don't care".
     * @param message The expected message. Use null to indicate
     * "don't care".
     * @param location The expected location. Use null to indicate
     * "don't care".
     */

    protected void assertSomeEvent
        (Level level,
         String logger,
         String message,
         String location)
    {
        getLogAssist().assertSomeEvent(level,logger,message,location);
    }

    /**
     * Asserts that the receiver's collector contains a single event,
     * and that its contents match the given expected level, logger
     * name, message, and location. The collector's list of retained
     * events is cleared upon success.
     *
     * @param level The expected level. Use null to indicate "don't
     * care".
     * @param logger The expected logger name. Use null to indicate
     * "don't care".
     * @param message The expected message. Use null to indicate
     * "don't care".
     * @param location The expected location. Use null to indicate
     * "don't care".
     */

    protected void assertSingleEvent
        (Level level,
         String logger,
         String message,
         String location)
    {
        getLogAssist().assertSingleEvent(level,logger,message,location);
    }
}
