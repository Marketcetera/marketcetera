package org.marketcetera.util.log;

import java.io.Serializable;
import org.apache.commons.lang.ObjectUtils;
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
    implements Serializable
{

    // CLASS DATA.

    private static final long serialVersionUID=1L;

    /**
     * The logging proxy name used when none is explicitly specified.
     */

    private final static String SELF_PROXY=
        I18NLoggerProxy.class.getName();


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
     * the error level via the given logging proxy. No logging takes
     * place if logging of error messages is disabled for the given
     * logging category.
     * 
     * @param proxy The proxy.
     * @param category The category.
     * @param throwable The throwable.
     */
    
    void errorProxy
        (String proxy,
         Object category,
         Throwable throwable)
    {
        if (!SLF4JLoggerProxy.isErrorEnabled(category)) {
            return;
        }
        SLF4JLoggerProxy.errorProxy(proxy,category,throwable);
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
        errorProxy(SELF_PROXY,category,throwable);
    }

    /**
     * Logs the given parameterized message and throwable under the
     * given logging category at the error level via the given logging
     * proxy. No logging takes place if logging of error messages is
     * disabled for the given logging category.
     * 
     * @param proxy The proxy.
     * @param category The category.
     * @param throwable The throwable.
     * @param message The message.
     * @param params The message parameters.
     */
    
    void errorProxy
        (String proxy,
         Object category,
         Throwable throwable,
         I18NMessage message,
         Object... params)
    {
        if (!SLF4JLoggerProxy.isErrorEnabled(category)) {
            return;
        }
        SLF4JLoggerProxy.errorProxy
            (proxy,category,getMessageProvider().getText(message,params),
             throwable);
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
        errorProxy(SELF_PROXY,category,throwable,message,params);
    }

    /**
     * Logs the given parameterized message under the given logging
     * category at the error level via the given logging proxy. No
     * logging takes place if logging of error messages is disabled
     * for the given logging category.
     * 
     * @param proxy The proxy.
     * @param category The category.
     * @param message The message.
     * @param params The message parameters.
     */
    
    void errorProxy
        (String proxy,
         Object category,
         I18NMessage message,
         Object... params)
    {
        if (!SLF4JLoggerProxy.isErrorEnabled(category)) {
            return;
        }
        SLF4JLoggerProxy.errorProxy
            (proxy,category,getMessageProvider().getText(message,params));
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
        errorProxy(SELF_PROXY,category,message,params);
    }

    /**
     * Logs the given throwable under the given logging category at
     * the warning level via the given logging proxy. No logging takes
     * place if logging of warning messages is disabled for the given
     * logging category.
     * 
     * @param proxy The proxy.
     * @param category The category.
     * @param throwable The throwable.
     */
    
    void warnProxy
        (String proxy,
         Object category,
         Throwable throwable)
    {
        if (!SLF4JLoggerProxy.isWarnEnabled(category)) {
            return;
        }
        SLF4JLoggerProxy.warnProxy(proxy,category,throwable);
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
        warnProxy(SELF_PROXY,category,throwable);
    }

    /**
     * Logs the given parameterized message and throwable under the
     * given logging category at the warning level via the given
     * logging proxy. No logging takes place if logging of warning
     * messages is disabled for the given logging category.
     * 
     * @param proxy The proxy.
     * @param category The category.
     * @param throwable The throwable.
     * @param message The message.
     * @param params The message parameters.
     */
    
    void warnProxy
        (String proxy,
         Object category,
         Throwable throwable,
         I18NMessage message,
         Object... params)
    {
        if (!SLF4JLoggerProxy.isWarnEnabled(category)) {
            return;
        }
        SLF4JLoggerProxy.warnProxy
            (proxy,category,getMessageProvider().getText(message,params),
             throwable);
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
        warnProxy(SELF_PROXY,category,throwable,message,params);
    }

    /**
     * Logs the given parameterized message under the given logging
     * category at the warning level via the given logging proxy. No
     * logging takes place if logging of warning messages is disabled
     * for the given logging category.
     * 
     * @param proxy The proxy.
     * @param category The category.
     * @param message The message.
     * @param params The message parameters.
     */
    
    void warnProxy
        (String proxy,
         Object category,
         I18NMessage message,
         Object... params)
    {
        if (!SLF4JLoggerProxy.isWarnEnabled(category)) {
            return;
        }
        SLF4JLoggerProxy.warnProxy
            (proxy,category,getMessageProvider().getText(message,params));
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
        warnProxy(SELF_PROXY,category,message,params);
    }

    /**
     * Logs the given throwable under the given logging category at
     * the informational level via the given logging proxy. No logging
     * takes place if logging of informational messages is disabled
     * for the given logging category.
     * 
     * @param proxy The proxy.
     * @param category The category.
     * @param throwable The throwable.
     */
    
    void infoProxy
        (String proxy,
         Object category,
         Throwable throwable)
    {
        if (!SLF4JLoggerProxy.isInfoEnabled(category)) {
            return;
        }
        SLF4JLoggerProxy.infoProxy(proxy,category,throwable);
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
        infoProxy(SELF_PROXY,category,throwable);
    }

    /**
     * Logs the given parameterized message and throwable under the
     * given logging category at the informational level via the given
     * logging proxy. No logging takes place if logging of
     * informational messages is disabled for the given logging
     * category.
     * 
     * @param proxy The proxy.
     * @param category The category.
     * @param throwable The throwable.
     * @param message The message.
     * @param params The message parameters.
     */
    
    void infoProxy
        (String proxy,
         Object category,
         Throwable throwable,
         I18NMessage message,
         Object... params)
    {
        if (!SLF4JLoggerProxy.isInfoEnabled(category)) {
            return;
        }
        SLF4JLoggerProxy.infoProxy
            (proxy,category,getMessageProvider().getText(message,params),
             throwable);
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
        infoProxy(SELF_PROXY,category,throwable,message,params);
    }

    /**
     * Logs the given parameterized message under the given logging
     * category at the informational level via the given logging
     * proxy. No logging takes place if logging of informational
     * messages is disabled for the given logging category.
     * 
     * @param proxy The proxy.
     * @param category The category.
     * @param message The message.
     * @param params The message parameters.
     */
    
    void infoProxy
        (String proxy,
         Object category,
         I18NMessage message,
         Object... params)
    {
        if (!SLF4JLoggerProxy.isInfoEnabled(category)) {
            return;
        }
        SLF4JLoggerProxy.infoProxy
            (proxy,category,getMessageProvider().getText(message,params));
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
        infoProxy(SELF_PROXY,category,message,params);
    }

    /**
     * Logs the given throwable under the given logging category at
     * the debugging level via the given logging proxy. No logging
     * takes place if logging of debugging messages is disabled for
     * the given logging category.
     * 
     * @param proxy The proxy.
     * @param category The category.
     * @param throwable The throwable.
     */
    
    void debugProxy
        (String proxy,
         Object category,
         Throwable throwable)
    {
        if (!SLF4JLoggerProxy.isDebugEnabled(category)) {
            return;
        }
        SLF4JLoggerProxy.debugProxy(proxy,category,throwable);
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
        debugProxy(SELF_PROXY,category,throwable);
    }

    /**
     * Logs the given parameterized message and throwable under the
     * given logging category at the debugging level via the given
     * logging proxy. No logging takes place if logging of debugging
     * messages is disabled for the given logging category.
     * 
     * @param proxy The proxy.
     * @param category The category.
     * @param throwable The throwable.
     * @param message The message.
     * @param params The message parameters.
     */
    
    void debugProxy
        (String proxy,
         Object category,
         Throwable throwable,
         I18NMessage message,
         Object... params)
    {
        if (!SLF4JLoggerProxy.isDebugEnabled(category)) {
            return;
        }
        SLF4JLoggerProxy.debugProxy
            (proxy,category,getMessageProvider().getText(message,params),
             throwable);
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
        debugProxy(SELF_PROXY,category,throwable,message,params);
    }

    /**
     * Logs the given parameterized message under the given logging
     * category at the debugging level via the given logging proxy. No
     * logging takes place if logging of debugging messages is
     * disabled for the given logging category.
     * 
     * @param proxy The proxy.
     * @param category The category.
     * @param message The message.
     * @param params The message parameters.
     */
    
    void debugProxy
        (String proxy,
         Object category,
         I18NMessage message,
         Object... params)
    {
        if (!SLF4JLoggerProxy.isDebugEnabled(category)) {
            return;
        }
        SLF4JLoggerProxy.debugProxy
            (proxy,category,getMessageProvider().getText(message,params));
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
        debugProxy(SELF_PROXY,category,message,params);
    }

    /**
     * Logs the given throwable under the given logging category at
     * the tracing level via the given logging proxy. No logging takes
     * place if logging of tracing messages is disabled for the given
     * logging category.
     * 
     * @param proxy The proxy.
     * @param category The category.
     * @param throwable The throwable.
     */
    
    void traceProxy
        (String proxy,
         Object category,
         Throwable throwable)
    {
        if (!SLF4JLoggerProxy.isTraceEnabled(category)) {
            return;
        }
        SLF4JLoggerProxy.traceProxy(proxy,category,throwable);
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
        traceProxy(SELF_PROXY,category,throwable);
    }

    /**
     * Logs the given parameterized message and throwable under the
     * given logging category at the tracing level via the given
     * logging proxy. No logging takes place if logging of tracing
     * messages is disabled for the given logging category.
     * 
     * @param proxy The proxy.
     * @param category The category.
     * @param throwable The throwable.
     * @param message The message.
     * @param params The message parameters.
     */
    
    void traceProxy
        (String proxy,
         Object category,
         Throwable throwable,
         I18NMessage message,
         Object... params)
    {
        if (!SLF4JLoggerProxy.isTraceEnabled(category)) {
            return;
        }
        SLF4JLoggerProxy.traceProxy
            (proxy,category,getMessageProvider().getText(message,params),
             throwable);
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
        traceProxy(SELF_PROXY,category,throwable,message,params);
    }

    /**
     * Logs the given parameterized message under the given logging
     * category at the tracing level via the given logging proxy. No
     * logging takes place if logging of tracing messages is disabled
     * for the given logging category.
     * 
     * @param proxy The proxy.
     * @param category The category.
     * @param message The message.
     * @param params The message parameters.
     */

    void traceProxy
        (String proxy,
         Object category,
         I18NMessage message,
         Object... params)
    {
        if (!SLF4JLoggerProxy.isTraceEnabled(category)) {
            return;
        }
        SLF4JLoggerProxy.traceProxy
            (proxy,category,getMessageProvider().getText(message,params));
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
        traceProxy(SELF_PROXY,category,message,params);
    }


    // Object.

    @Override
    public int hashCode()
    {
        return ObjectUtils.hashCode(getMessageProvider());
    }

    @Override
    public boolean equals
        (Object other)
    {
        if (this==other) {
            return true;
        }
        if ((other==null) || !getClass().equals(other.getClass())) {
            return false;
        }
        I18NLoggerProxy o=(I18NLoggerProxy)other;
        return ObjectUtils.equals(getMessageProvider(),o.getMessageProvider());
    }
}
