package org.marketcetera.log;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.message.Message;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Plugin(name="ExactThresholdFilter",category="Core",elementType="filter",printObject=true)
public class ExactThresholdFilter
        extends AbstractFilter
{
    
    private final Level level;
 
    private ExactThresholdFilter(Level level, Result onMatch, Result onMismatch) {
        super(onMatch, onMismatch);
        this.level = level;
    }
 
    public Result filter(Logger logger,
                         Level level,
                         Marker marker,
                         String msg,
                         Object[] params)
    {
        return filter(level);
    }
 
    public Result filter(Logger logger, Level level, Marker marker, Object msg, Throwable t) {
        return filter(level);
    }
 
    public Result filter(Logger logger, Level level, Marker marker, Message msg, Throwable t) {
        return filter(level);
    }
 
    @Override
    public Result filter(LogEvent event) {
        return filter(event.getLevel());
    }
 
    private Result filter(Level level)
    {
        return level == this.level ? onMatch : onMismatch;
    }
 
    @Override
    public String toString() {
        return level.toString();
    }
 
    /**
     * Create a ThresholdFilter.
     * @param loggerLevel The log Level.
     * @param match The action to take on a match.
     * @param mismatch The action to take on a mismatch.
     * @return The created ThresholdFilter.
     */
    @PluginFactory
    public static ExactThresholdFilter createFilter(@PluginAttribute(value = "level", defaultString = "ERROR") Level level,
                                               @PluginAttribute(value = "onMatch", defaultString = "NEUTRAL") Result onMatch,
                                               @PluginAttribute(value = "onMismatch", defaultString = "DENY") Result onMismatch) {
        return new ExactThresholdFilter(level, onMatch, onMismatch);
    }
    private static final long serialVersionUID = 5773154389436464026L;
}
