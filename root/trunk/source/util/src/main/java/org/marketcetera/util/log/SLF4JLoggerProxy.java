package org.marketcetera.util.log;

import org.marketcetera.util.misc.ClassVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;
import org.slf4j.spi.LocationAwareLogger;

/**
 * SLF4J proxy with automatic logger selection and variable number of
 * arguments.
 * 
 * @author tlerios@marketcetera.com
 * @since 0.5.0
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
    
    public static final String UNKNOWN_MESSAGE=
        "Unknown Message"; //$NON-NLS-1$

    /**
     * The name of the category used when no category is explicitly
     * specified.
     */

    public static final String UNKNOWN_LOGGER_NAME=
        "UNKNOWN"; //$NON-NLS-1$

    /**
     * The logger used when no category is explicitly specified.
     */

    private static final Logger UNKNOWN_LOGGER=
        LoggerFactory.getLogger(UNKNOWN_LOGGER_NAME);

    /**
     * The logging proxy name used when none is explicitly specified.
     */

    private final static String SELF_PROXY=
        SLF4JLoggerProxy.class.getName();
    
    
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
     * Attempts to log the given message using the given logger at the
     * given level and via the given logging proxy. Logging takes
     * place only of the logger is location-aware.
     * 
     * @param logger The logger.
     * @param proxy The proxy.
     * @param level The level.
     * @param message The message.
     *
     * @return True if logging takes place.
     */

    private static boolean log
        (Logger logger,
         String proxy,
         int level,
         String message)
    {
        return log(logger,proxy,level,message,(Throwable)null);
    }

    /**
     * Attempts to log the given message and throwable using the given
     * logger at the given level and via the given logging
     * proxy. Logging takes place only of the logger is
     * location-aware.
     * 
     * @param logger The logger.
     * @param proxy The proxy.
     * @param level The level.
     * @param message The message.
     * @param throwable The throwable.
     *
     * @return True if logging takes place.
     */

    private static boolean log
        (Logger logger,
         String proxy,
         int level,
         String message,
         Throwable throwable)
    {
        if (!(logger instanceof LocationAwareLogger)) {
            return false;
        }
        ((LocationAwareLogger)logger).log
            (null,proxy,level,message,throwable);
        return true;
    }
    
    /**
     * Attempts to log the given parameterized message using the given
     * logger at the given level and via the given logging
     * proxy. Logging takes place only of the logger is
     * location-aware.
     * 
     * @param logger The logger.
     * @param proxy The proxy.
     * @param level The level.
     * @param message The message.
     * @param params The message parameters.
     *
     * @return True if logging takes place.
     */

    private static boolean log
        (Logger logger,
         String proxy,
         int level,
         String message,
         Object[] params)
    {
        return log(logger,proxy,level,null,message,params);
    }

    /**
     * Attempts to log the given parameterized message and throwable
     * using the given logger at the given level and via the given
     * logging proxy. Logging takes place only of the logger is
     * location-aware.
     * 
     * @param logger The logger.
     * @param proxy The proxy.
     * @param level The level.
     * @param throwable The throwable.
     * @param message The message.
     * @param params The message parameters.
     *
     * @return True if logging takes place.
     */

    private static boolean log
        (Logger logger,
         String proxy,
         int level,
         Throwable throwable,
         String message,
         Object[] params)
    {
        if (!(logger instanceof LocationAwareLogger)) {
            return false;
        }
        ((LocationAwareLogger)logger).log
            (null,proxy,level,MessageFormatter.arrayFormat
             (message,params),throwable);
        return true;
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
     * error level via the given logging proxy.
     * 
     * @param proxy The proxy.
     * @param category The category.
     * @param message The message.
     */
    
    static void errorProxy
        (String proxy,
         Object category,
         String message)
    {
        Logger logger=getLogger(category);
        if (!logger.isErrorEnabled()) {
            return;
        }
        if (log(logger,proxy,LocationAwareLogger.ERROR_INT,message)) {
            return;
        }
        logger.error(message);
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
        errorProxy(SELF_PROXY,category,message);
    }

    /**
     * Logs the given throwable under the given logging category at
     * the error level via the given logging proxy.
     * 
     * @param proxy The proxy.
     * @param category The category.
     * @param throwable The throwable.
     */
    
    static void errorProxy
        (String proxy,
         Object category,
         Throwable throwable)
    {
        Logger logger=getLogger(category);
        if (!logger.isErrorEnabled()) {
            return;
        }
        if (log(logger,proxy,LocationAwareLogger.ERROR_INT,
                UNKNOWN_MESSAGE,throwable)) {
            return;
        }
        logger.error(UNKNOWN_MESSAGE,throwable);
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
        errorProxy(SELF_PROXY,category,throwable);
    }

    /**
     * Logs the given message and throwable under the given logging
     * category at the error level via the given logging proxy.
     * 
     * @param proxy The proxy.
     * @param category The category.
     * @param message The message.
     * @param throwable The throwable.
     */
    
    static void errorProxy
        (String proxy,
         Object category,
         String message,
         Throwable throwable)
    {
        Logger logger=getLogger(category);
        if (!logger.isErrorEnabled()) {
            return;
        }
        if (log(logger,proxy,LocationAwareLogger.ERROR_INT,
                message,throwable)) {
            return;
        }
        logger.error(message,throwable);
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
        errorProxy(SELF_PROXY,category,message,throwable);
    }

    /**
     * Logs the given parameterized message under the given logging
     * category at the error level via the given logging proxy.
     * 
     * @param proxy The proxy.
     * @param category The category.
     * @param message The message.
     * @param params The message parameters.
     */
    
    static void errorProxy
        (String proxy,
         Object category,
         String message,
         Object... params)
    {
        Logger logger=getLogger(category);
        if (!logger.isErrorEnabled()) {
            return;
        }
        if (log(logger,proxy,LocationAwareLogger.ERROR_INT,
                message,params)) {
            return;
        }
        logger.error(message,params);
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
        errorProxy(SELF_PROXY,category,message,params);
    }

    /**
     * Logs the given parameterized message and throwable under the
     * given logging category at the error level via the given logging
     * proxy.
     * 
     * @param proxy The proxy.
     * @param category The category.
     * @param throwable The throwable.
     * @param message The message.
     * @param params The message parameters.
     */
    
    static void errorProxy
        (String proxy,
         Object category,
         Throwable throwable,
         String message,
         Object... params)
    {
        Logger logger=getLogger(category);
        if (!logger.isErrorEnabled()) {
            return;
        }
        if (log(logger,proxy,LocationAwareLogger.ERROR_INT,
                throwable,message,params)) {
            return;
        }
        logger.error(MessageFormatter.arrayFormat
                     (message,params),throwable);
    }

    /**
     * Logs the given parameterized message and throwable under the
     * given logging category at the error level.
     * 
     * @param category The category.
     * @param throwable The throwable.
     * @param message The message.
     * @param params The message parameters.
     */
    
    public static void error
        (Object category,
         Throwable throwable,
         String message,
         Object... params)
    {
        errorProxy(SELF_PROXY,category,throwable,message,params);
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
     * warning level via the given logging proxy.
     * 
     * @param proxy The proxy.
     * @param category The category.
     * @param message The message.
     */
    
    static void warnProxy
        (String proxy,
         Object category,
         String message)
    {
        Logger logger=getLogger(category);
        if (!logger.isWarnEnabled()) {
            return;
        }
        if (log(logger,proxy,LocationAwareLogger.WARN_INT,message)) {
            return;
        }
        logger.warn(message);
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
        warnProxy(SELF_PROXY,category,message);
    }

    /**
     * Logs the given throwable under the given logging category at
     * the warning level via the given logging proxy.
     * 
     * @param proxy The proxy.
     * @param category The category.
     * @param throwable The throwable.
     */
    
    static void warnProxy
        (String proxy,
         Object category,
         Throwable throwable)
    {
        Logger logger=getLogger(category);
        if (!logger.isWarnEnabled()) {
            return;
        }
        if (log(logger,proxy,LocationAwareLogger.WARN_INT,
                UNKNOWN_MESSAGE,throwable)) {
            return;
        }
        logger.warn(UNKNOWN_MESSAGE,throwable);
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
        warnProxy(SELF_PROXY,category,throwable);
    }

    /**
     * Logs the given message and throwable under the given logging
     * category at the warning level via the given logging proxy.
     * 
     * @param proxy The proxy.
     * @param category The category.
     * @param message The message.
     * @param throwable The throwable.
     */
    
    static void warnProxy
        (String proxy,
         Object category,
         String message,
         Throwable throwable)
    {
        Logger logger=getLogger(category);
        if (!logger.isWarnEnabled()) {
            return;
        }
        if (log(logger,proxy,LocationAwareLogger.WARN_INT,
                message,throwable)) {
            return;
        }
        logger.warn(message,throwable);
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
        warnProxy(SELF_PROXY,category,message,throwable);
    }

    /**
     * Logs the given parameterized message under the given logging
     * category at the warning level via the given logging proxy.
     * 
     * @param proxy The proxy.
     * @param category The category.
     * @param message The message.
     * @param params The message parameters.
     */
    
    static void warnProxy
        (String proxy,
         Object category,
         String message,
         Object... params)
    {
        Logger logger=getLogger(category);
        if (!logger.isWarnEnabled()) {
            return;
        }
        if (log(logger,proxy,LocationAwareLogger.WARN_INT,
                message,params)) {
            return;
        }
        logger.warn(message,params);
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
        warnProxy(SELF_PROXY,category,message,params);
    }

    /**
     * Logs the given parameterized message and throwable under the
     * given logging category at the warning level via the given
     * logging proxy.
     * 
     * @param proxy The proxy.
     * @param category The category.
     * @param throwable The throwable.
     * @param message The message.
     * @param params The message parameters.
     */
    
    static void warnProxy
        (String proxy,
         Object category,
         Throwable throwable,
         String message,
         Object... params)
    {
        Logger logger=getLogger(category);
        if (!logger.isWarnEnabled()) {
            return;
        }
        if (log(logger,proxy,LocationAwareLogger.WARN_INT,
                throwable,message,params)) {
            return;
        }
        logger.warn(MessageFormatter.arrayFormat
                    (message,params),throwable);
    }

    /**
     * Logs the given parameterized message and throwable under the
     * given logging category at the warning level.
     * 
     * @param category The category.
     * @param throwable The throwable.
     * @param message The message.
     * @param params The message parameters.
     */
    
    public static void warn
        (Object category,
         Throwable throwable,
         String message,
         Object... params)
    {
        warnProxy(SELF_PROXY,category,throwable,message,params);
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
     * informational level via the given logging proxy.
     * 
     * @param proxy The proxy.
     * @param category The category.
     * @param message The message.
     */
    
    static void infoProxy
        (String proxy,
         Object category,
         String message)
    {
        Logger logger=getLogger(category);
        if (!logger.isInfoEnabled()) {
            return;
        }
        if (log(logger,proxy,LocationAwareLogger.INFO_INT,message)) {
            return;
        }
        logger.info(message);
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
        infoProxy(SELF_PROXY,category,message);
    }

    /**
     * Logs the given throwable under the given logging category at
     * the informational level via the given logging proxy.
     * 
     * @param proxy The proxy.
     * @param category The category.
     * @param throwable The throwable.
     */
    
    static void infoProxy
        (String proxy,
         Object category,
         Throwable throwable)
    {
        Logger logger=getLogger(category);
        if (!logger.isInfoEnabled()) {
            return;
        }
        if (log(logger,proxy,LocationAwareLogger.INFO_INT,
                UNKNOWN_MESSAGE,throwable)) {
            return;
        }
        logger.info(UNKNOWN_MESSAGE,throwable);
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
        infoProxy(SELF_PROXY,category,throwable);
    }

    /**
     * Logs the given message and throwable under the given logging
     * category at the informational level via the given logging
     * proxy.
     * 
     * @param proxy The proxy.
     * @param category The category.
     * @param message The message.
     * @param throwable The throwable.
     */
    
    static void infoProxy
        (String proxy,
         Object category,
         String message,
         Throwable throwable)
    {
        Logger logger=getLogger(category);
        if (!logger.isInfoEnabled()) {
            return;
        }
        if (log(logger,proxy,LocationAwareLogger.INFO_INT,
                message,throwable)) {
            return;
        }
        logger.info(message,throwable);
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
        infoProxy(SELF_PROXY,category,message,throwable);
    }

    /**
     * Logs the given parameterized message under the given logging
     * category at the informational level via the given logging
     * proxy.
     * 
     * @param proxy The proxy.
     * @param category The category.
     * @param message The message.
     * @param params The message parameters.
     */
    
    static void infoProxy
        (String proxy,
         Object category,
         String message,
         Object... params)
    {
        Logger logger=getLogger(category);
        if (!logger.isInfoEnabled()) {
            return;
        }
        if (log(logger,proxy,LocationAwareLogger.INFO_INT,
                message,params)) {
            return;
        }
        logger.info(message,params);
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
        infoProxy(SELF_PROXY,category,message,params);
    }

    /**
     * Logs the given parameterized message and throwable under the
     * given logging category at the informational level via the given
     * logging proxy.
     * 
     * @param proxy The proxy.
     * @param category The category.
     * @param throwable The throwable.
     * @param message The message.
     * @param params The message parameters.
     */
    
    static void infoProxy
        (String proxy,
         Object category,
         Throwable throwable,
         String message,
         Object... params)
    {
        Logger logger=getLogger(category);
        if (!logger.isInfoEnabled()) {
            return;
        }
        if (log(logger,proxy,LocationAwareLogger.INFO_INT,
                throwable,message,params)) {
            return;
        }
        logger.info(MessageFormatter.arrayFormat
                    (message,params),throwable);
    }

    /**
     * Logs the given parameterized message and throwable under the
     * given logging category at the informational level.
     * 
     * @param category The category.
     * @param throwable The throwable.
     * @param message The message.
     * @param params The message parameters.
     */
    
    public static void info
        (Object category,
         Throwable throwable,
         String message,
         Object... params)
    {
        infoProxy(SELF_PROXY,category,throwable,message,params);
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
     * debugging level via the given logging proxy.
     * 
     * @param proxy The proxy.
     * @param category The category.
     * @param message The message.
     */
    
    static void debugProxy
        (String proxy,
         Object category,
         String message)
    {
        Logger logger=getLogger(category);
        if (!logger.isDebugEnabled()) {
            return;
        }
        if (log(logger,proxy,LocationAwareLogger.DEBUG_INT,message)) {
            return;
        }
        logger.debug(message);
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
        debugProxy(SELF_PROXY,category,message);
    }

    /**
     * Logs the given throwable under the given logging category at
     * the debugging level via the given logging proxy.
     * 
     * @param proxy The proxy.
     * @param category The category.
     * @param throwable The throwable.
     */
    
    static void debugProxy
        (String proxy,
         Object category,
         Throwable throwable)
    {
        Logger logger=getLogger(category);
        if (!logger.isDebugEnabled()) {
            return;
        }
        if (log(logger,proxy,LocationAwareLogger.DEBUG_INT,
                UNKNOWN_MESSAGE,throwable)) {
            return;
        }
        logger.debug(UNKNOWN_MESSAGE,throwable);
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
        debugProxy(SELF_PROXY,category,throwable);
    }

    /**
     * Logs the given message and throwable under the given logging
     * category at the debugging level via the given logging proxy.
     * 
     * @param proxy The proxy.
     * @param category The category.
     * @param message The message.
     * @param throwable The throwable.
     */
    
    static void debugProxy
        (String proxy,
         Object category,
         String message,
         Throwable throwable)
    {
        Logger logger=getLogger(category);
        if (!logger.isDebugEnabled()) {
            return;
        }
        if (log(logger,proxy,LocationAwareLogger.DEBUG_INT,
                message,throwable)) {
            return;
        }
        logger.debug(message,throwable);
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
        debugProxy(SELF_PROXY,category,message,throwable);
    }

    /**
     * Logs the given parameterized message under the given logging
     * category at the debugging level via the given logging proxy.
     * 
     * @param proxy The proxy.
     * @param category The category.
     * @param message The message.
     * @param params The message parameters.
     */
    
    static void debugProxy
        (String proxy,
         Object category,
         String message,
         Object... params)
    {
        Logger logger=getLogger(category);
        if (!logger.isDebugEnabled()) {
            return;
        }
        if (log(logger,proxy,LocationAwareLogger.DEBUG_INT,
                message,params)) {
            return;
        }
        logger.debug(message,params);
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
        debugProxy(SELF_PROXY,category,message,params);
    }

    /**
     * Logs the given parameterized message and throwable under the
     * given logging category at the debugging level via the given
     * logging proxy.
     * 
     * @param proxy The proxy.
     * @param category The category.
     * @param throwable The throwable.
     * @param message The message.
     * @param params The message parameters.
     */
    
    static void debugProxy
        (String proxy,
         Object category,
         Throwable throwable,
         String message,
         Object... params)
    {
        Logger logger=getLogger(category);
        if (!logger.isDebugEnabled()) {
            return;
        }
        if (log(logger,proxy,LocationAwareLogger.DEBUG_INT,
                throwable,message,params)) {
            return;
        }
        logger.debug(MessageFormatter.arrayFormat
                     (message,params),throwable);
    }

    /**
     * Logs the given parameterized message and throwable under the
     * given logging category at the debugging level.
     * 
     * @param category The category.
     * @param throwable The throwable.
     * @param message The message.
     * @param params The message parameters.
     */
    
    public static void debug
        (Object category,
         Throwable throwable,
         String message,
         Object... params)
    {
        debugProxy(SELF_PROXY,category,throwable,message,params);
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
     * tracing level via the given logging proxy.
     * 
     * @param proxy The proxy.
     * @param category The category.
     * @param message The message.
     */
    
    static void traceProxy
        (String proxy,
         Object category,
         String message)
    {
        Logger logger=getLogger(category);
        if (!logger.isTraceEnabled()) {
            return;
        }
        if (log(logger,proxy,LocationAwareLogger.TRACE_INT,message)) {
            return;
        }
        logger.trace(message);
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
        traceProxy(SELF_PROXY,category,message);
    }

    /**
     * Logs the given throwable under the given logging category at
     * the tracing level via the given logging proxy.
     * 
     * @param proxy The proxy.
     * @param category The category.
     * @param throwable The throwable.
     */
    
    static void traceProxy
        (String proxy,
         Object category,
         Throwable throwable)
    {
        Logger logger=getLogger(category);
        if (!logger.isTraceEnabled()) {
            return;
        }
        if (log(logger,proxy,LocationAwareLogger.TRACE_INT,
                UNKNOWN_MESSAGE,throwable)) {
            return;
        }
        logger.trace(UNKNOWN_MESSAGE,throwable);
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
        traceProxy(SELF_PROXY,category,throwable);
    }

    /**
     * Logs the given message and throwable under the given logging
     * category at the tracing level via the given logging proxy.
     * 
     * @param proxy The proxy.
     * @param category The category.
     * @param message The message.
     * @param throwable The throwable.
     */
    
    static void traceProxy
        (String proxy,
         Object category,
         String message,
         Throwable throwable)
    {
        Logger logger=getLogger(category);
        if (!logger.isTraceEnabled()) {
            return;
        }
        if (log(logger,proxy,LocationAwareLogger.TRACE_INT,
                message,throwable)) {
            return;
        }
        logger.trace(message,throwable);
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
        traceProxy(SELF_PROXY,category,message,throwable);
    }

    /**
     * Logs the given parameterized message under the given logging
     * category at the tracing level via the given logging proxy.
     * 
     * @param proxy The proxy.
     * @param category The category.
     * @param message The message.
     * @param params The message parameters.
     */
    
    static void traceProxy
        (String proxy,
         Object category,
         String message,
         Object... params)
    {
        Logger logger=getLogger(category);
        if (!logger.isTraceEnabled()) {
            return;
        }
        if (log(logger,proxy,LocationAwareLogger.TRACE_INT,
                message,params)) {
            return;
        }
        logger.trace(message,params);
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
        traceProxy(SELF_PROXY,category,message,params);
    }

    /**
     * Logs the given parameterized message and throwable under the
     * given logging category at the tracing level via the given
     * logging proxy.
     * 
     * @param proxy The proxy.
     * @param category The category.
     * @param throwable The throwable.
     * @param message The message.
     * @param params The message parameters.
     */
    
    static void traceProxy
        (String proxy,
         Object category,
         Throwable throwable,
         String message,
         Object... params)
    {
        Logger logger=getLogger(category);
        if (!logger.isTraceEnabled()) {
            return;
        }
        if (log(logger,proxy,LocationAwareLogger.TRACE_INT,
                throwable,message,params)) {
            return;
        }
        logger.trace(MessageFormatter.arrayFormat
                     (message,params),throwable);
    }

    /**
     * Logs the given parameterized message and throwable under the
     * given logging category at the tracing level.
     * 
     * @param category The category.
     * @param throwable The throwable.
     * @param message The message.
     * @param params The message parameters.
     */
    
    public static void trace
        (Object category,
         Throwable throwable,
         String message,
         Object... params)
    {
        traceProxy(SELF_PROXY,category,throwable,message,params);
    }


    // CONSTRUCTORS.

    /**
     * Constructor. It is private so that no instances can be created.
     */

    private SLF4JLoggerProxy() {}
}
