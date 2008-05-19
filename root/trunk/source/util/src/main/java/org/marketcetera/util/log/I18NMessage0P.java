package org.marketcetera.util.log;

import java.util.Locale;
import org.marketcetera.core.ClassVersion;

/**
 * An internationalized message, requiring exactly zero parameters.
 * 
 * @author tlerios
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class I18NMessage0P
    extends I18NMessage
    implements I18NBoundMessage
{

    // CONSTRUCTORS.

    /**
     * Constructor mirroring superclass constructor.
     *
     * @see I18NMessage#I18NMessage(I18NLoggerProxy,String,String)
     */

    public I18NMessage0P
        (I18NLoggerProxy loggerProxy,
         String messageId,
         String entryId)
    {
        super(loggerProxy,messageId,entryId);
    }

    /**
     * Constructor mirroring superclass constructor.
     *
     * @see I18NMessage#I18NMessage(I18NLoggerProxy,String)
     */

    public I18NMessage0P
        (I18NLoggerProxy loggerProxy,
         String messageId)
    {
        super(loggerProxy,messageId);
    }


    // I18NBoundMessage.

    @Override
    public I18NMessage0P getMessage()
    {
        return this;
    }

    @Override
    public Object[] getParams()
    {
        return new Object[0];
    }

    @Override
    public String getText
        (Locale locale)
    {
        return getMessageProvider().getText(locale,this);
    }

    @Override
    public String getText()
    {
        return getMessageProvider().getText(this);
    }

    @Override
    public void error
        (Object category,
         Throwable throwable)
    {
        getLoggerProxy().error(category,throwable,this);
    }

    @Override
    public void error
        (Object category)
    {
        getLoggerProxy().error(category,this);
    }

    @Override
    public void warn
        (Object category,
         Throwable throwable)
    {
        getLoggerProxy().warn(category,throwable,this);
    }
    
    @Override
    public void warn
        (Object category)
    {
        getLoggerProxy().warn(category,this);
    }

    @Override
    public void info
        (Object category,
         Throwable throwable)
    {
        getLoggerProxy().info(category,throwable,this);
    }
    
    @Override
    public void info
        (Object category)
    {
        getLoggerProxy().info(category,this);
    }

    @Override
    public void debug
        (Object category,
         Throwable throwable)
    {
        getLoggerProxy().debug(category,throwable,this);
    }
    
    @Override
    public void debug
        (Object category)
    {
        getLoggerProxy().debug(category,this);
    }

    @Override
    public void trace
        (Object category,
         Throwable throwable)
    {
        getLoggerProxy().trace(category,throwable,this);
    }
    
    @Override
    public void trace
        (Object category)
    {
        getLoggerProxy().trace(category,this);
    }


    // Object.

    @Override
    public String toString()
    {
        return getText();
    }
}
