package org.marketcetera.util.test;

import java.io.File;
import java.util.NoSuchElementException;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Before;

import static org.junit.Assert.*;

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
        "src"+File.separator+"test"+File.separator+"sample_data";

    /**
     * The root directory for class files.
     */

    protected static final String DIR_CLASSES=
        "target"+File.separator+"classes";

    /**
     * The root directory for test class files.
     */

    protected static final String DIR_TEST_CLASSES=
        "target"+File.separator+"test-classes";


    // INSTANCE DATA.

    private MemoryAppender mAppender;


    // CLASS METHODS.

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
        Logger.getLogger(name).setLevel(level);
    }

    /**
     * Asserts that the contents of the given event match the given
     * expected level, logger name, and message.
     *
     * @param event The event.
     * @param level The expected level. Use null to indicate "don't
     * care".
     * @param logger The expected logger name. Use null to indicate
     * "don't care".
     * @param message The expected message. Use null to indicate
     * "don't care".
     */

    protected static void assertEvent
        (LoggingEvent event,
         Level level,
         String logger,
         String message)
    {
        if (level!=null) {
            assertEquals(level,event.getLevel());
        }
        if (logger!=null) {
            assertEquals(logger,event.getLoggerName());
        }
        if (message!=null) {
            assertEquals(message,event.getMessage());
        }
    }


    // INSTANCE METHODS.

    /**
     * Sets up the receiver's collector of retained events as part of
     * each test's fixture.
     */

    @Before
    public void setupTestCaseBase()
    {
        mAppender=new MemoryAppender();
        BasicConfigurator.configure(getAppender());
    }

    /**
     * Returns the receiver's collector (appender) of retained events.
     *
     * @return The appender.
     */

    protected MemoryAppender getAppender()
    {
        return mAppender;
    }

    /**
     * Asserts that the receiver's collector contains no events.
     */

    protected void assertNoEvents()
    {
        assertEquals(0,getAppender().getEvents().size());
    }

    /**
     * Asserts that the contents of the receiver's collector's most
     * recent event match the given expected level, logger name, and
     * message. The assertion fails if the receiver has no events.
     *
     * @param level The expected level. Use null to indicate "don't
     * care".
     * @param logger The expected logger name. Use null to indicate
     * "don't care".
     * @param message The expected message. Use null to indicate
     * "don't care".
     */

    protected void assertLastEvent
        (Level level,
         String logger,
         String message)
    {
        LoggingEvent event=null;
        try {
            event=getAppender().getEvents().getLast();
        } catch (NoSuchElementException ex) {
            fail("List is empty");
        }
        assertEvent(event,level,logger,message);
    }

    /**
     * Asserts that the receiver's collector contains a single event,
     * and that its contents match the given expected level, logger
     * name, and message. The collector's list of retained events is
     * cleared upon success.
     *
     * @param level The expected level. Use null to indicate "don't
     * care".
     * @param logger The expected logger name. Use null to indicate
     * "don't care".
     * @param message The expected message. Use null to indicate
     * "don't care".
     */

    protected void assertSingleEvent
        (Level level,
         String logger,
         String message)
    {
        assertEquals(1,getAppender().getEvents().size());
        assertLastEvent(level,logger,message);
        getAppender().clear();
    }
}
