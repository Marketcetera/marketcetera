package org.marketcetera.core.event;

import org.marketcetera.api.attributes.ClassVersion;

/* $License$ */

/**
 * Represents a log entry event.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: LogEvent.java 16063 2012-01-31 18:21:55Z colin $
 * @since 1.5.0
 */
@ClassVersion("$Id: LogEvent.java 16063 2012-01-31 18:21:55Z colin $")
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
