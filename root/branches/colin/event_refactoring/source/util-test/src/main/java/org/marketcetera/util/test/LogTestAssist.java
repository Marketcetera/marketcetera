package org.marketcetera.util.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

import java.util.NoSuchElementException;

/* $License$ */
/**
 * A utility to class to help test log messages in unit tests.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
public class LogTestAssist
{
    private final MemoryAppender mAppender = new MemoryAppender();

    /**
     * Creates a new instance that doesn't track any logger output.
     * The instance can be configured to track logger output by invoking
     * {@link #trackLogger(String, org.apache.log4j.Level)}.
     */
    public LogTestAssist()
    {
    }

    /**
     * Creates an instance that tracks the log events sent out to
     * the specified logger.
     *
     * @param inName The logger name.
     * @param inLevel the new log level for the logger. If null, the logger's
     * level is not reset.
     */
    public LogTestAssist(String inName, Level inLevel)
    {
        trackLogger(inName, inLevel);
    }

    /**
     * Starts tracking log events for the specified logger and
     * resets the log level of the specified logger to the supplied value.
     *
     * @param inName the name of the logger to track.
     * @param inLevel the new log level for the logger. If null, the logger's
     * level is not reset.
     */
    public void trackLogger(String inName, Level inLevel)
    {
        Logger logger=Logger.getLogger(inName);
        logger.addAppender(getAppender());
        if (inLevel!=null) {
            logger.setLevel(inLevel);
        }
    }
    /**
     * Clears all the log events received by the appender.
     */
    public void resetAppender()
    {
        mAppender.clear();
    }

    /**
     * Asserts that the receiver's collector contains no events.
     */

    public void assertNoEvents()
    {
        assertEquals(0,getAppender().getEvents().size());
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

    public void assertLastEvent
        (Level level,
         String logger,
         String message,
         String location)
    {
        LoggingEvent event=null;
        try {
            event=getAppender().getEvents().getLast();
        } catch (NoSuchElementException ex) {
            fail("List is empty"); //$NON-NLS-1$
        }
        TestCaseBase.assertEvent(event,level,logger,message,location);
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

    public void assertSomeEvent
        (Level level,
         String logger,
         String message,
         String location)
    {
        for (LoggingEvent event:getAppender().getEvents()) {
            if (((level==null) ||
                 level.equals(event.getLevel())) &&
                ((logger==null) ||
                 logger.equals(event.getLoggerName())) &&
                ((message==null) ||
                 message.equals(event.getMessage())) &&
                ((location==null) ||
                 location.equals
                 (event.getLocationInformation().getClassName()))) {
                return;
            }
        }
        fail("No matches against given event:"+getAppender().getEvents().toString()); //$NON-NLS-1$
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

    public void assertSingleEvent
        (Level level,
         String logger,
         String message,
         String location)
    {
        assertEquals(1,getAppender().getEvents().size());
        assertLastEvent(level,logger,message,location);
        getAppender().clear();
    }

    /**
     * Returns the receiver's collector (appender) of retained events.
     *
     * @return The appender.
     */

    MemoryAppender getAppender()
    {
        return mAppender;
    }
}
