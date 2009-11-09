package org.marketcetera.util.test;

import java.util.NoSuchElementException;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

import static org.junit.Assert.*;

/**
 * A log message test helper.
 *
 * @author tlerios@marketcetera.com
 * @author anshul@marketcetera.com
 * @since $Release$
 * @version $Id$
 */

/* $License$ */

public class LogTestAssist
{

    // INSTANCE DATA.

    private final MemoryAppender mAppender=
        new MemoryAppender();


    // CONSTRUCTORS.

    /**
     * Creates a new helper which tracks the log events sent out to
     * the logger with the given name at the given optional threshold
     * level.
     *
     * @param name The logger name.
     * @param level The level. It may be null, in which case logger's
     * threshold level is not altered.
     */

    public LogTestAssist
        (String name,
         Level level)
    {
        trackLogger(name,level);
    }

    /**
     * Creates a new helper which tracks the log events sent out to
     * the logger with the given name.
     *
     * @param name The logger name.
     */

    public LogTestAssist
        (String name)
    {
        this(name,null);
    }

    /**
     * Creates a new helper which doesn't track any logger output. The
     * helper can be configured to track logger output using {@link
     * #trackLogger(String,org.apache.log4j.Level)}.
     */

    public LogTestAssist() {}


    // CLASS METHODS.

    /**
     * Sets the level of the root logger to the given level.
     *
     * @param level The level.
     */

    public static void setDefaultLevel
        (Level level)
    {
        Logger.getRootLogger().setLevel(level);
    }

    /**
     * Sets the level of the logger with the given name to the given
     * level.
     *
     * @param name The logger name.
     * @param level The level.
     */

    public static void setLevel
        (String name,
         Level level)
    {
        Logger.getLogger(name).setLevel(level);
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

    public static void assertEvent
        (LoggingEvent event,
         Level level,
         String logger,
         String message,
         String location)
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
        if (location!=null) {
            assertEquals
                (location,event.getLocationInformation().getClassName());
        }
    }


    // INSTANCE METHODS.

    /**
     * Starts tracking log events sent out to the logger with the
     * given name at the given optional threshold level.
     *
     * @param name The logger name.
     * @param level The level. It may be null, in which case logger's
     * threshold level is not altered.
     */

    public void trackLogger
        (String name,
         Level level)
    {
        Logger logger=Logger.getLogger(name);
        logger.addAppender(getAppender());
        if (level!=null) {
            logger.setLevel(level);
        }
    }

    /**
     * Starts tracking log events sent out to the logger with the
     * given name.
     *
     * @param name The logger name.
     */

    public void trackLogger
        (String name)
    {
        trackLogger(name,null);
    }

    /**
     * Clears the contents of the receiver's collector.
     */

    public void resetAppender()
    {
        getAppender().clear();
    }

    /**
     * Returns a string with all the events of the receiver's
     * collector.
     *
     * @return The string.
     */

    public String getEventsAsString()
    {
        StringBuilder builder=new StringBuilder();
        builder.append("Event count: "); //$NON-NLS-1$
        builder.append(getAppender().getEvents().size());
        int i=0;
        for (LoggingEvent event:getAppender().getEvents()) {
            builder.append(SystemUtils.LINE_SEPARATOR);
            builder.append("Event "); //$NON-NLS-1$
            builder.append(i++);
            builder.append(": level: "); //$NON-NLS-1$
            builder.append(event.getLevel());
            builder.append("; logger: "); //$NON-NLS-1$
            builder.append(event.getLoggerName());
            builder.append("; message: "); //$NON-NLS-1$
            builder.append(event.getMessage());
            builder.append("; location: "); //$NON-NLS-1$
            builder.append(event.getLocationInformation().getClassName());
        }
        return builder.toString();
    }

    /**
     * Returns the receiver's collector (appender) of retained events.
     *
     * @return The appender.
     */

    public MemoryAppender getAppender()
    {
        return mAppender;
    }

    /**
     * Asserts that the receiver's collector contains the given number
     * of events.
     *
     * @param count The number of events.
     */

    public void assertEventCount
        (int count)
    {
        if (getAppender().getEvents().size()==count) {
            return;
        }
        StringBuilder builder=new StringBuilder();
        builder.append("Incorrect event count; expected "); //$NON-NLS-1$
        builder.append(count);
        builder.append(" actual "); //$NON-NLS-1$
        builder.append(SystemUtils.LINE_SEPARATOR);
        builder.append(getEventsAsString());
        fail(builder.toString());
    }

    /**
     * Asserts that the receiver's collector contains no events.
     */

    public void assertNoEvents()
    {
        assertEventCount(0);
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
        assertEvent(event,level,logger,message,location);
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
        fail("No matches against given event. Events are:"+ //$NON-NLS-1$
             SystemUtils.LINE_SEPARATOR+
             getEventsAsString());
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
        assertEventCount(1);
        assertLastEvent(level,logger,message,location);
        resetAppender();
    }
}
