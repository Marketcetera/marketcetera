package org.marketcetera.util.log;

import java.util.Locale;
import org.marketcetera.util.misc.ClassVersion;

/**
 * An internationalized message, accepting an arbitrary number of
 * parameters.
 * 
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class I18NMessageNP
    extends I18NMessage
{

    // CLASS DATA.

    private static final long serialVersionUID=1L;

    /**
     * The logging proxy name.
     */

    private final static String SELF_PROXY=
        I18NMessageNP.class.getName();


    // CONSTRUCTORS.

    /**
     * Constructor mirroring superclass constructor.
     *
     * @see I18NMessage#I18NMessage(I18NLoggerProxy,String,String)
     */

    public I18NMessageNP
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

    public I18NMessageNP
        (I18NLoggerProxy loggerProxy,
         String messageId)
    {
        super(loggerProxy,messageId);
    }


    // I18NMessage.

    @Override
    public int getParamCount()
    {
        return -1;
    }


    // INSTANCE METHODS.

    /**
     * A convenience method for {@link
     * I18NMessageProvider#getText(Locale,I18NMessage,Object...)}.
     */

    public String getText
        (Locale locale,
         Object... ps)
    {
        return getMessageProvider().getText(locale,this,ps);
    }

    /**
     * A convenience method for {@link
     * I18NMessageProvider#getText(I18NMessage,Object...)}.
     */

    public String getText
        (Object... ps)
    {
        return getMessageProvider().getText(this,ps);
    }

    /**
     * A convenience method for {@link
     * I18NLoggerProxy#error(Object,Throwable,I18NMessage,Object...)}.
     */

    public void error
        (Object category,
         Throwable throwable,
         Object... ps)
    {
        getLoggerProxy().errorProxy(SELF_PROXY,category,throwable,this,ps);
    }

    /**
     * A convenience method for {@link
     * I18NLoggerProxy#error(Object,I18NMessage,Object...)}.
     */
    
    public void error
        (Object category,
         Object... ps)
    {
        getLoggerProxy().errorProxy(SELF_PROXY,category,this,ps);
    }

    /**
     * A convenience method for {@link
     * I18NLoggerProxy#warn(Object,Throwable,I18NMessage,Object...)}.
     */

    public void warn
        (Object category,
         Throwable throwable,
         Object... ps)
    {
        getLoggerProxy().warnProxy(SELF_PROXY,category,throwable,this,ps);
    }

    /**
     * A convenience method for {@link
     * I18NLoggerProxy#warn(Object,I18NMessage,Object...)}.
     */
    
    public void warn
        (Object category,
         Object... ps)
    {
        getLoggerProxy().warnProxy(SELF_PROXY,category,this,ps);
    }

    /**
     * A convenience method for {@link
     * I18NLoggerProxy#info(Object,Throwable,I18NMessage,Object...)}.
     */

    public void info
        (Object category,
         Throwable throwable,
         Object... ps)
    {
        getLoggerProxy().infoProxy(SELF_PROXY,category,throwable,this,ps);
    }

    /**
     * A convenience method for {@link
     * I18NLoggerProxy#info(Object,I18NMessage,Object...)}.
     */
    
    public void info
        (Object category,
         Object... ps)
    {
        getLoggerProxy().infoProxy(SELF_PROXY,category,this,ps);
    }

    /**
     * A convenience method for {@link
     * I18NLoggerProxy#debug(Object,Throwable,I18NMessage,Object...)}.
     */

    public void debug
        (Object category,
         Throwable throwable,
         Object... ps)
    {
        getLoggerProxy().debugProxy(SELF_PROXY,category,throwable,this,ps);
    }

    /**
     * A convenience method for {@link
     * I18NLoggerProxy#debug(Object,I18NMessage,Object...)}.
     */
    
    public void debug
        (Object category,
         Object... ps)
    {
        getLoggerProxy().debugProxy(SELF_PROXY,category,this,ps);
    }

    /**
     * A convenience method for {@link
     * I18NLoggerProxy#trace(Object,Throwable,I18NMessage,Object...)}.
     */

    public void trace
        (Object category,
         Throwable throwable,
         Object... ps)
    {
        getLoggerProxy().traceProxy(SELF_PROXY,category,throwable,this,ps);
    }

    /**
     * A convenience method for {@link
     * I18NLoggerProxy#trace(Object,I18NMessage,Object...)}.
     */
    
    public void trace
        (Object category,
         Object... ps)
    {
        getLoggerProxy().traceProxy(SELF_PROXY,category,this,ps);
    }
}
