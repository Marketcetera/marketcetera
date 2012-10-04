package org.marketcetera.core.event;

/* $License$ */

/**
 * Represents a log entry event.
 *
 * @version $Id: LogEvent.java 16063 2012-01-31 18:21:55Z colin $
 * @since 1.5.0
 */
public interface LogEvent
        extends Event
{
    /**
     * Get the Level value.
     *
     * @return a <code>LogEventLevel</code> value
     */
    public LogEventLevel getLevel();
    /**
     * Get the exception value.
     *
     * @return a <code>Throwable</code> value
     */
    public Throwable getException();
    /**
     * Returns the bound event message. 
     *
     * @return a <code>String</code> value
     */
    public String getMessage();
}
