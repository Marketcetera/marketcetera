package org.marketcetera.event;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Represents a log entry event.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
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
