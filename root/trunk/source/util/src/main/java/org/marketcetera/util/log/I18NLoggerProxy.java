package org.marketcetera.util.log;

import org.marketcetera.util.misc.ClassVersion;

/**
 * A logger which supports internationalized messages.
 * 
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class I18NLoggerProxy
{

    // INSTANCE DATA.

    private I18NMessageProvider mMessageProvider;


    // CONSTRUCTORS.

    /**
     * Creates a new logger which uses the given message provider to
     * map messages onto text.
     *
     * @param messageProvider The message provider.
     */

    public I18NLoggerProxy
        (I18NMessageProvider messageProvider)
    {
        mMessageProvider=messageProvider;
    }


    // INSTANCE METHODS.

    /**
     * Returns the receiver's message provider.
     *
     * @return The message provider.
     */

    public I18NMessageProvider getMessageProvider()
    {
        return mMessageProvider;
    }

    /**
     * Logs the given throwable under the given logging category at
     * the error level. No logging takes place if logging of error
     * messages is disabled for the given logging category.
     * 
     * @param category The category.
     * @param throwable The throwable.
     */
    
    public void error
        (Object category,
         Throwable throwable)
    {
        if (!SLF4JLoggerProxy.isErrorEnabled(category)) {
            return;
        }
        SLF4JLoggerProxy.error(category,throwable);
    }

    /**
     * Logs the given parameterized message and throwable under the
     * given logging category at the error level. No logging takes
     * place if logging of error messages is disabled for the given
     * logging category.
     * 
     * @param category The category.
     * @param throwable The throwable.
     * @param message The message.
     * @param params The message parameters.
     */
    
    public void error
        (Object category,
         Throwable throwable,
         I18NMessage message,
         Object... params)
    {
        if (!SLF4JLoggerProxy.isErrorEnabled(category)) {
            return;
        }
        SLF4JLoggerProxy.error
            (category,getMessageProvider().getText(message,params),throwable);
    }

    /**
     * Logs the given parameterized message under the given logging
     * category at the error level. No logging takes place if logging
     * of error messages is disabled for the given logging category.
     * 
     * @param category The category.
     * @param message The message.
     * @param params The message parameters.
     */
    
    public void error
        (Object category,
         I18NMessage message,
         Object... params)
    {
        if (!SLF4JLoggerProxy.isErrorEnabled(category)) {
            return;
        }
        SLF4JLoggerProxy.error
            (category,getMessageProvider().getText(message,params));
    }

    /**
     * Logs the given throwable under the given logging category at
     * the warning level. No logging takes place if logging of warning
     * messages is disabled for the given logging category.
     * 
     * @param category The category.
     * @param throwable The throwable.
     */
    
    public void warn
        (Object category,
         Throwable throwable)
    {
        if (!SLF4JLoggerProxy.isWarnEnabled(category)) {
            return;
        }
        SLF4JLoggerProxy.warn(category,throwable);
    }

    /**
     * Logs the given parameterized message and throwable under the
     * given logging category at the warning level. No logging takes
     * place if logging of warning messages is disabled for the given
     * logging category.
     * 
     * @param category The category.
     * @param throwable The throwable.
     * @param message The message.
     * @param params The message parameters.
     */
    
    public void warn
        (Object category,
         Throwable throwable,
         I18NMessage message,
         Object... params)
    {
        if (!SLF4JLoggerProxy.isWarnEnabled(category)) {
            return;
        }
        SLF4JLoggerProxy.warn
            (category,getMessageProvider().getText(message,params),throwable);
    }

    /**
     * Logs the given parameterized message under the given logging
     * category at the warning level. No logging takes place if
     * logging of warning messages is disabled for the given logging
     * category.
     * 
     * @param category The category.
     * @param message The message.
     * @param params The message parameters.
     */
    
    public void warn
        (Object category,
         I18NMessage message,
         Object... params)
    {
        if (!SLF4JLoggerProxy.isWarnEnabled(category)) {
            return;
        }
        SLF4JLoggerProxy.warn
            (category,getMessageProvider().getText(message,params));
    }

    /**
     * Logs the given throwable under the given logging category at
     * the informational level. No logging takes place if logging of
     * informational messages is disabled for the given logging
     * category.
     * 
     * @param category The category.
     * @param throwable The throwable.
     */
    
    public void info
        (Object category,
         Throwable throwable)
    {
        if (!SLF4JLoggerProxy.isInfoEnabled(category)) {
            return;
        }
        SLF4JLoggerProxy.info(category,throwable);
    }

    /**
     * Logs the given parameterized message and throwable under the
     * given logging category at the informational level. No logging
     * takes place if logging of informational messages is disabled
     * for the given logging category.
     * 
     * @param category The category.
     * @param throwable The throwable.
     * @param message The message.
     * @param params The message parameters.
     */
    
    public void info
        (Object category,
         Throwable throwable,
         I18NMessage message,
         Object... params)
    {
        if (!SLF4JLoggerProxy.isInfoEnabled(category)) {
            return;
        }
        SLF4JLoggerProxy.info
            (category,getMessageProvider().getText(message,params),throwable);
    }

    /**
     * Logs the given parameterized message under the given logging
     * category at the informational level. No logging takes place if
     * logging of informational messages is disabled for the given
     * logging category.
     * 
     * @param category The category.
     * @param message The message.
     * @param params The message parameters.
     */
    
    public void info
        (Object category,
         I18NMessage message,
         Object... params)
    {
        if (!SLF4JLoggerProxy.isInfoEnabled(category)) {
            return;
        }
        SLF4JLoggerProxy.info
            (category,getMessageProvider().getText(message,params));
    }

    /**
     * Logs the given throwable under the given logging category at
     * the debugging level. No logging takes place if logging of
     * debugging messages is disabled for the given logging category.
     * 
     * @param category The category.
     * @param throwable The throwable.
     */
    
    public void debug
        (Object category,
         Throwable throwable)
    {
        if (!SLF4JLoggerProxy.isDebugEnabled(category)) {
            return;
        }
        SLF4JLoggerProxy.debug(category,throwable);
    }

    /**
     * Logs the given parameterized message and throwable under the
     * given logging category at the debugging level. No logging takes
     * place if logging of debugging messages is disabled for the
     * given logging category.
     * 
     * @param category The category.
     * @param throwable The throwable.
     * @param message The message.
     * @param params The message parameters.
     */
    
    public void debug
        (Object category,
         Throwable throwable,
         I18NMessage message,
         Object... params)
    {
        if (!SLF4JLoggerProxy.isDebugEnabled(category)) {
            return;
        }
        SLF4JLoggerProxy.debug
            (category,getMessageProvider().getText(message,params),throwable);
    }

    /**
     * Logs the given parameterized message under the given logging
     * category at the debugging level. No logging takes place if
     * logging of debugging messages is disabled for the given logging
     * category.
     * 
     * @param category The category.
     * @param message The message.
     * @param params The message parameters.
     */
    
    public void debug
        (Object category,
         I18NMessage message,
         Object... params)
    {
        if (!SLF4JLoggerProxy.isDebugEnabled(category)) {
            return;
        }
        SLF4JLoggerProxy.debug
            (category,getMessageProvider().getText(message,params));
    }

    /**
     * Logs the given throwable under the given logging category at
     * the tracing level. No logging takes place if logging of tracing
     * messages is disabled for the given logging category.
     * 
     * @param category The category.
     * @param throwable The throwable.
     */
    
    public void trace
        (Object category,
         Throwable throwable)
    {
        if (!SLF4JLoggerProxy.isTraceEnabled(category)) {
            return;
        }
        SLF4JLoggerProxy.trace(category,throwable);
    }

    /**
     * Logs the given parameterized message and throwable under the
     * given logging category at the tracing level. No logging takes
     * place if logging of tracing messages is disabled for the given
     * logging category.
     * 
     * @param category The category.
     * @param throwable The throwable.
     * @param message The message.
     * @param params The message parameters.
     */
    
    public void trace
        (Object category,
         Throwable throwable,
         I18NMessage message,
         Object... params)
    {
        if (!SLF4JLoggerProxy.isTraceEnabled(category)) {
            return;
        }
        SLF4JLoggerProxy.trace
            (category,getMessageProvider().getText(message,params),throwable);
    }

    /**
     * Logs the given parameterized message under the given logging
     * category at the tracing level. No logging takes place if
     * logging of tracing messages is disabled for the given logging
     * category.
     * 
     * @param category The category.
     * @param message The message.
     * @param params The message parameters.
     */
    
    public void trace
        (Object category,
         I18NMessage message,
         Object... params)
    {
        if (!SLF4JLoggerProxy.isTraceEnabled(category)) {
            return;
        }
        SLF4JLoggerProxy.trace
            (category,getMessageProvider().getText(message,params));
    }
}
