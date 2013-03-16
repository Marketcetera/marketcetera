package org.marketcetera.core.event;

import org.marketcetera.core.util.log.SLF4JLoggerProxy;

/* $License$ */

/**
 * Severity level of {@link LogEvent}.
 *
 * @version $Id$
 * @since 1.5.0
 */
public enum LogEventLevel
{
    DEBUG,
    INFO,
    WARN,
    ERROR;
    /**
     * Determines if the given event should be logged or not.
     * 
     * @param inEvent a <code>LogEvent</code> value
     * @param inCategory a <code>String</code> value containing the log category
     * @return a <code>boolean</code> value
     */
    public static boolean shouldLog(LogEvent inEvent,
                                    String inCategory)
    {
        if(inEvent == null) {
            return false;
        }
        if(DEBUG.equals(inEvent.getLevel())) {
            return SLF4JLoggerProxy.isDebugEnabled(inCategory);
        }
        if(INFO.equals(inEvent.getLevel())) {
            return SLF4JLoggerProxy.isInfoEnabled(inCategory);
        }
        if(WARN.equals(inEvent.getLevel())) {
            return SLF4JLoggerProxy.isWarnEnabled(inCategory);
        }
        if(ERROR.equals(inEvent.getLevel())) {
            return SLF4JLoggerProxy.isErrorEnabled(inCategory);
        }
        return false;
    }
}
