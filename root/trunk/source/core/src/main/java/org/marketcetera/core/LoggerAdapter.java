package org.marketcetera.core;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.apache.log4j.PropertyConfigurator;

/**
 * High-level wrapper for doing all the logging
 * The basic add-on is that very debug/warn/error/info call is wrapped in
 * if(isDebugEnabled) { then log } construct
 *
 * Since we are using a wrapper class, we need to channel all the logger calls
 * through the {@link Logger#log} function call.
 *
 * We are not hardcoding the log4j.properties config file location when we call into
 * {@link PropertyConfigurator#configureAndWatch}. The props file needs to be in the
 * classpath and then Log4J knows how to find it.
 *
 * @author Toli Kuznets
 * @version $Id$
 */
@ClassVersion("$Id$")
public class LoggerAdapter extends Logger
{
    private static LoggerAdapter sLogger;
    public static String LOGGER_CONF_FILE = "log4j.properties";
    public static final int LOGGER_WATCH_DELAY = 20*1000;

    private static final String WRAPPER_FQCN = LoggerAdapter.class.getName();

    private LoggerAdapter(String name)
    {
        super(name);
    }

    public static LoggerAdapter initializeLogger(String name)
    {
        if(sLogger != null) { return sLogger; }

        System.out.println("initializing logger for " + name);
        sLogger = new LoggerAdapter(name);
        PropertyConfigurator.configureAndWatch(LOGGER_CONF_FILE, LOGGER_WATCH_DELAY);
        sLogger.setLevel(Level.ERROR);
        System.out.println(MessageKey.LOGGER_INIT.getLocalizedMessage(name));
        return sLogger;
    }

    public void debug(String inMsg)
    {
        throw new IllegalArgumentException(MessageKey.LOGGER_MISSING_CAT.getLocalizedMessage());
    }

    /** Get the appropriate logger for the incoming category and delegate the debug to it */
    public static void debug(String msg, Object inCat)
    {
        getMyLogger(inCat).log(WRAPPER_FQCN, Level.DEBUG, msg, null);
    }
    public static void debug(String msg, Throwable ex, Object inCat)
    {
        getMyLogger(inCat).log(WRAPPER_FQCN, Level.DEBUG, msg, ex);
    }


    /** Get the appropriate logger for the incoming category and delegate the info to it */
    public static void info(String msg, Object inCat)
    {
        getMyLogger(inCat).log(WRAPPER_FQCN, Level.INFO, msg, null);
    }
    public void info(String msg, Throwable ex, Object inCat)
    {
        getMyLogger(inCat).log(WRAPPER_FQCN, Level.INFO, msg, ex);
    }
    /** Get the appropriate logger for the incoming category and delegate the warn to it */
    public static void warn(String msg, Object inCat)
    {
        getMyLogger(inCat).log(WRAPPER_FQCN, Level.WARN, msg, null);
    }
    public static void warn(String msg, Throwable ex, Object inCat)
    {
        getMyLogger(inCat).log(WRAPPER_FQCN, Level.WARN, msg, ex);
    }
    /** Get the appropriate logger for the incoming category and delegate the error to it */
    public static void error(String msg, Object inCat)
    {
        getMyLogger(inCat).log(WRAPPER_FQCN, Level.ERROR, msg, null);
    }
    public static void error(String msg, Throwable ex, Object inCat)
    {
        getMyLogger(inCat).log(WRAPPER_FQCN, Level.ERROR, msg, ex);
    }

    public void error(String msg)
    {
        throw new IllegalArgumentException(MessageKey.LOGGER_MISSING_CAT.getLocalizedMessage());
    }

    public void info(String msg)
    {
        throw new IllegalArgumentException(MessageKey.LOGGER_MISSING_CAT.getLocalizedMessage());
    }

    public void warn(String msg)
    {
        throw new IllegalArgumentException(MessageKey.LOGGER_MISSING_CAT.getLocalizedMessage());
    }

    public static boolean isDebugEnabled(Object inCat)
    {
        return(getMyLogger(inCat).isDebugEnabled());
    }

    public static boolean isInfoEnabled(Object inCat)
    {
        return(getMyLogger(inCat).isInfoEnabled());
    }
    
    public static boolean isWarnEnabled(Object inCat)
    {
        return(getMyLogger(inCat).isEnabledFor(Priority.WARN));
    }
    
    public static boolean isErrorEnabled(Object inCat)
    {
        return(getMyLogger(inCat).isEnabledFor(Priority.ERROR));
    }

    private static Logger getMyLogger(Object inCategory)
    {
        if(inCategory == null) {
            return getLogger("");
        } else if (inCategory instanceof String) {
            return getLogger((String)inCategory);
        } else if(inCategory instanceof Class) {
            return getLogger(((Class) inCategory).getName());
        } else {
            return getLogger(inCategory.getClass().getName());
        }
    }
}
