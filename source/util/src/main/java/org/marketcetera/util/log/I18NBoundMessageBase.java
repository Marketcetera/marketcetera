package org.marketcetera.util.log;

import java.util.Locale;
import org.marketcetera.core.ClassVersion;

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
public class I18NBoundMessageBase
    implements I18NBoundMessage
{

    // INSTANCE DATA.

    private I18NMessage mMessage;
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
        (I18NMessage message,
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
    public I18NMessage getMessage()
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
        getLoggerProxy().error(category,throwable,getMessage(),getParams());
    }

    @Override
    public void error
        (Object category)
    {
        getLoggerProxy().error(category,getMessage(),getParams());
    }

    @Override
    public void warn
        (Object category,
         Throwable throwable)
    {
        getLoggerProxy().warn(category,throwable,getMessage(),getParams());
    }
    
    @Override
    public void warn
        (Object category)
    {
        getLoggerProxy().warn(category,getMessage(),getParams());
    }

    @Override
    public void info
        (Object category,
         Throwable throwable)
    {
        getLoggerProxy().info(category,throwable,getMessage(),getParams());
    }
    
    @Override
    public void info
        (Object category)
    {
        getLoggerProxy().info(category,getMessage(),getParams());
    }

    @Override
    public void debug
        (Object category,
         Throwable throwable)
    {
        getLoggerProxy().debug(category,throwable,getMessage(),getParams());
    }
    
    @Override
    public void debug
        (Object category)
    {
        getLoggerProxy().debug(category,getMessage(),getParams());
    }

    @Override
    public void trace
        (Object category,
         Throwable throwable)
    {
        getLoggerProxy().trace(category,throwable,getMessage(),getParams());
    }

    @Override
    public void trace
        (Object category)
    {
        getLoggerProxy().trace(category,getMessage(),getParams());
    }


    // Object.

    @Override
    public String toString()
    {
        return getText();
    }
}
