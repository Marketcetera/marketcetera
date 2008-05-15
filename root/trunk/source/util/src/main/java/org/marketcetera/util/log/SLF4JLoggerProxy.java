package org.marketcetera.util.log;

import org.marketcetera.core.ClassVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SLF4J proxy with automatic logger selection and variable number of
 * arguments.
 * 
 * @author tlerios
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public final class SLF4JLoggerProxy
{
    
    // CLASS DATA.
    
    /**
     * The logging message used when none is explicitly specified. 
     */
    
    public static final String UNKNOWN_MESSAGE="Unknown Message";

    /**
     * The name of the category used when no category is explicitly
     * specified.
     */

    public static final String UNKNOWN_LOGGER_NAME="UNKNOWN";

    /**
     * The logger used when no category is explicitly specified.
     */

    private static final Logger UNKNOWN_LOGGER=
        LoggerFactory.getLogger(UNKNOWN_LOGGER_NAME);

    
    // CLASS METHODS.

    /**
     * Returns the logger for the given logging category.
     * 
     * @param category The category.
     * 
     * @return The logger.
     */
    
    private static Logger getLogger
        (Object category)
    {
        if (category==null) {
            return UNKNOWN_LOGGER;
        }
        if (category instanceof String) {
            return LoggerFactory.getLogger((String)category);
        }
        if (category instanceof Class<?>) {
            return LoggerFactory.getLogger(((Class<?>)category).getName());
        }
        return LoggerFactory.getLogger(category.getClass().getName());
    }

    /**
     * Returns true if logging of error messages is enabled for the
     * given logging category.
     * 
     * @param category The category.
     */
    
    public static boolean isErrorEnabled
        (Object category)
    {
        return getLogger(category).isErrorEnabled();
    }

    /**
     * Logs the given message under the given logging category at the
     * error level.
     * 
     * @param category The category.
     * @param message The message.
     */
    
    public static void error
        (Object category,
         String message)
    {
        getLogger(category).error(message);
    }

    /**
     * Logs the given throwable under the given logging category at
     * the error level.
     * 
     * @param category The category.
     * @param throwable The throwable.
     */
    
    public static void error
        (Object category,
         Throwable throwable)
    {
        getLogger(category).error(UNKNOWN_MESSAGE,throwable);
    }

    /**
     * Logs the given message and throwable under the given logging
     * category at the error level.
     * 
     * @param category The category.
     * @param message The message.
     * @param throwable The throwable.
     */
    
    public static void error
        (Object category,
         String message,
         Throwable throwable)
    {
        getLogger(category).error(message,throwable);
    }

    /**
     * Logs the given parameterized message under the given logging
     * category at the error level.
     * 
     * @param category The category.
     * @param message The message.
     * @param params The message parameters.
     */
    
    public static void error
        (Object category,
         String message,
         Object... params)
    {
        getLogger(category).error(message,params);
    }

    /**
     * Returns true if logging of warning messages is enabled for the
     * given logging category.
     * 
     * @param category The category.
     */
    
    public static boolean isWarnEnabled
        (Object category)
    {
        return getLogger(category).isWarnEnabled();
    }

    /**
     * Logs the given message under the given logging category at the
     * warning level.
     * 
     * @param category The category.
     * @param message The message.
     */
    
    public static void warn
        (Object category,
         String message)
    {
        getLogger(category).warn(message);
    }

    /**
     * Logs the given throwable under the given logging category at
     * the warning level.
     * 
     * @param category The category.
     * @param throwable The throwable.
     */
    
    public static void warn
        (Object category,
         Throwable throwable)
    {
        getLogger(category).warn(UNKNOWN_MESSAGE,throwable);
    }

    /**
     * Logs the given message and throwable under the given logging
     * category at the warning level.
     * 
     * @param category The category.
     * @param message The message.
     * @param throwable The throwable.
     */
    
    public static void warn
        (Object category,
         String message,
         Throwable throwable)
    {
        getLogger(category).warn(message,throwable);
    }

    /**
     * Logs the given parameterized message under the given logging
     * category at the warning level.
     * 
     * @param category The category.
     * @param message The message.
     * @param params The message parameters.
     */
    
    public static void warn
        (Object category,
         String message,
         Object... params)
    {
        getLogger(category).warn(message,params);
    }

    /**
     * Returns true if logging of informational messages is enabled
     * for the given logging category.
     * 
     * @param category The category.
     */
    
    public static boolean isInfoEnabled
        (Object category)
    {
        return getLogger(category).isInfoEnabled();
    }

    /**
     * Logs the given message under the given logging category at the
     * informational level.
     * 
     * @param category The category.
     * @param message The message.
     */
    
    public static void info
        (Object category,
         String message)
    {
        getLogger(category).info(message);
    }

    /**
     * Logs the given throwable under the given logging category at
     * the informational level.
     * 
     * @param category The category.
     * @param throwable The throwable.
     */
    
    public static void info
        (Object category,
         Throwable throwable)
    {
        getLogger(category).info(UNKNOWN_MESSAGE,throwable);
    }

    /**
     * Logs the given message and throwable under the given logging
     * category at the informational level.
     * 
     * @param category The category.
     * @param message The message.
     * @param throwable The throwable.
     */
    
    public static void info
        (Object category,
         String message,
         Throwable throwable)
    {
        getLogger(category).info(message,throwable);
    }

    /**
     * Logs the given parameterized message under the given logging
     * category at the informational level.
     * 
     * @param category The category.
     * @param message The message.
     * @param params The message parameters.
     */
    
    public static void info
        (Object category,
         String message,
         Object... params)
    {
        getLogger(category).info(message,params);
    }

    /**
     * Returns true if logging of debugging messages is enabled for
     * the given logging category.
     * 
     * @param category The category.
     */
    
    public static boolean isDebugEnabled
        (Object category)
    {
        return getLogger(category).isDebugEnabled();
    }

    /**
     * Logs the given message under the given logging category at the
     * debugging level.
     * 
     * @param category The category.
     * @param message The message.
     */
    
    public static void debug
        (Object category,
         String message)
    {
        getLogger(category).debug(message);
    }

    /**
     * Logs the given throwable under the given logging category at
     * the debugging level.
     * 
     * @param category The category.
     * @param throwable The throwable.
     */
    
    public static void debug
        (Object category,
         Throwable throwable)
    {
        getLogger(category).debug(UNKNOWN_MESSAGE,throwable);
    }

    /**
     * Logs the given message and throwable under the given logging
     * category at the debugging level.
     * 
     * @param category The category.
     * @param message The message.
     * @param throwable The throwable.
     */
    
    public static void debug
        (Object category,
         String message,
         Throwable throwable)
    {
        getLogger(category).debug(message,throwable);
    }

    /**
     * Logs the given parameterized message under the given logging
     * category at the debugging level.
     * 
     * @param category The category.
     * @param message The message.
     * @param params The message parameters.
     */
    
    public static void debug
        (Object category,
         String message,
         Object... params)
    {
        getLogger(category).debug(message,params);
    }
    
    /**
     * Returns true if logging of tracing messages is enabled for the
     * given logging category.
     * 
     * @param category The category.
     */
    
    public static boolean isTraceEnabled
        (Object category)
    {
        return getLogger(category).isTraceEnabled();
    }

    /**
     * Logs the given message under the given logging category at the
     * tracing level.
     * 
     * @param category The category.
     * @param message The message.
     */
    
    public static void trace
        (Object category,
         String message)
    {
        getLogger(category).trace(message);
    }

    /**
     * Logs the given throwable under the given logging category at
     * the tracing level.
     * 
     * @param category The category.
     * @param throwable The throwable.
     */
    
    public static void trace
        (Object category,
         Throwable throwable)
    {
        getLogger(category).trace(UNKNOWN_MESSAGE,throwable);
    }

    /**
     * Logs the given message and throwable under the given logging
     * category at the tracing level.
     * 
     * @param category The category.
     * @param message The message.
     * @param throwable The throwable.
     */
    
    public static void trace
        (Object category,
         String message,
         Throwable throwable)
    {
        getLogger(category).trace(message,throwable);
    }

    /**
     * Logs the given parameterized message under the given logging
     * category at the tracing level.
     * 
     * @param category The category.
     * @param message The message.
     * @param params The message parameters.
     */
    
    public static void trace
        (Object category,
         String message,
         Object... params)
    {
        getLogger(category).trace(message,params);
    }


    // CONSTRUCTORS.

    /**
     * Constructor. It is private so that no instances can be created.
     */

    private SLF4JLoggerProxy() {}
}
