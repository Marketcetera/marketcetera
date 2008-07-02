package org.marketcetera.util.log;

import java.util.Locale;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A bound message implementation, representing the combination of an
 * {@link I18NMessage} and its parameters, if any.
 * 
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class I18NBoundMessageBase<T extends I18NMessage>
    implements I18NBoundMessage
{

    // CLASS DATA.

    /**
     * The logging proxy name.
     */

    private final static String SELF_PROXY=
        I18NBoundMessageBase.class.getName();


    // INSTANCE DATA.

    private T mMessage;
    private Object[] mParams;


    // CONSTRUCTORS.

    /**
     * Creates a new bound message with the given message and
     * parameters.
     *
     * @param message The message.
     * @param params The parameters.
     */

    I18NBoundMessageBase
        (T message,
         Object... params)
    {
        mMessage=message;
        mParams=params;
    }


    // I18NBoundMessage.

    @Override
    public I18NLoggerProxy getLoggerProxy()
    {
        return getMessage().getLoggerProxy();
    }

    @Override
    public I18NMessageProvider getMessageProvider()
    {
        return getMessage().getMessageProvider();
    }

    @Override
    public T getMessage()
    {
        return mMessage;
    }

    @Override
    public Object[] getParams()
    {
        return mParams;
    }

    @Override
    public String getText
        (Locale locale)
    {
        return getMessageProvider().getText(locale,getMessage(),getParams());
    }

    @Override
    public String getText()
    {
        return getMessageProvider().getText(getMessage(),getParams());
    }

    @Override
    public void error
        (Object category,
         Throwable throwable)
    {
        getLoggerProxy().errorProxy
            (SELF_PROXY,category,throwable,getMessage(),getParams());
    }

    @Override
    public void error
        (Object category)
    {
        getLoggerProxy().errorProxy
            (SELF_PROXY,category,getMessage(),getParams());
    }

    @Override
    public void warn
        (Object category,
         Throwable throwable)
    {
        getLoggerProxy().warnProxy
            (SELF_PROXY,category,throwable,getMessage(),getParams());
    }
    
    @Override
    public void warn
        (Object category)
    {
        getLoggerProxy().warnProxy
            (SELF_PROXY,category,getMessage(),getParams());
    }

    @Override
    public void info
        (Object category,
         Throwable throwable)
    {
        getLoggerProxy().infoProxy
            (SELF_PROXY,category,throwable,getMessage(),getParams());
    }
    
    @Override
    public void info
        (Object category)
    {
        getLoggerProxy().infoProxy
            (SELF_PROXY,category,getMessage(),getParams());
    }

    @Override
    public void debug
        (Object category,
         Throwable throwable)
    {
        getLoggerProxy().debugProxy
            (SELF_PROXY,category,throwable,getMessage(),getParams());
    }
    
    @Override
    public void debug
        (Object category)
    {
        getLoggerProxy().debugProxy
            (SELF_PROXY,category,getMessage(),getParams());
    }

    @Override
    public void trace
        (Object category,
         Throwable throwable)
    {
        getLoggerProxy().traceProxy
            (SELF_PROXY,category,throwable,getMessage(),getParams());
    }

    @Override
    public void trace
        (Object category)
    {
        getLoggerProxy().traceProxy
            (SELF_PROXY,category,getMessage(),getParams());
    }


    // Object.

    @Override
    public String toString()
    {
        return getText();
    }
}
