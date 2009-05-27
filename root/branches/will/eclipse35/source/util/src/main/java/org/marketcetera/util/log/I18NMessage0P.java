package org.marketcetera.util.log;

import java.io.Serializable;
import java.util.Locale;
import org.apache.commons.lang.ArrayUtils;
import org.marketcetera.util.misc.ClassVersion;

/**
 * An internationalized message, requiring exactly zero parameters.
 * 
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class I18NMessage0P
    extends I18NMessage
    implements I18NBoundMessage
{

    // CLASS DATA.

    private static final long serialVersionUID=1L;

    /**
     * The logging proxy name.
     */

    private final static String SELF_PROXY=
        I18NMessage0P.class.getName();


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


    // I18NMessage.

    @Override
    public int getParamCount()
    {
        return 0;
    }


    // I18NBoundMessage.

    @Override
    public I18NMessage0P getMessage()
    {
        return this;
    }

    @Override
    public Serializable[] getParams()
    {
        return I18NBoundMessage.EMPTY_PARAMS;
    }

    @Override
    public Object[] getParamsAsObjects()
    {
        return ArrayUtils.EMPTY_OBJECT_ARRAY;
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
        getLoggerProxy().errorProxy(SELF_PROXY,category,throwable,this);
    }

    @Override
    public void error
        (Object category)
    {
        getLoggerProxy().errorProxy(SELF_PROXY,category,this);
    }

    @Override
    public void warn
        (Object category,
         Throwable throwable)
    {
        getLoggerProxy().warnProxy(SELF_PROXY,category,throwable,this);
    }
    
    @Override
    public void warn
        (Object category)
    {
        getLoggerProxy().warnProxy(SELF_PROXY,category,this);
    }

    @Override
    public void info
        (Object category,
         Throwable throwable)
    {
        getLoggerProxy().infoProxy(SELF_PROXY,category,throwable,this);
    }
    
    @Override
    public void info
        (Object category)
    {
        getLoggerProxy().infoProxy(SELF_PROXY,category,this);
    }

    @Override
    public void debug
        (Object category,
         Throwable throwable)
    {
        getLoggerProxy().debugProxy(SELF_PROXY,category,throwable,this);
    }
    
    @Override
    public void debug
        (Object category)
    {
        getLoggerProxy().debugProxy(SELF_PROXY,category,this);
    }

    @Override
    public void trace
        (Object category,
         Throwable throwable)
    {
        getLoggerProxy().traceProxy(SELF_PROXY,category,throwable,this);
    }
    
    @Override
    public void trace
        (Object category)
    {
        getLoggerProxy().traceProxy(SELF_PROXY,category,this);
    }


    // Object.

    @Override
    public String toString()
    {
        return getText();
    }
}
