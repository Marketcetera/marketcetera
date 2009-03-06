package org.marketcetera.util.log;

import java.util.Locale;
import org.marketcetera.util.misc.ClassVersion;

/**
 * An internationalized message, requiring exactly six parameters.
 * 
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class I18NMessage6P
    extends I18NMessage
{

    // CLASS DATA.

    private static final long serialVersionUID=1L;

    /**
     * The logging proxy name.
     */

    private final static String SELF_PROXY=
        I18NMessage6P.class.getName();


    // CONSTRUCTORS.

    /**
     * Constructor mirroring superclass constructor.
     *
     * @see I18NMessage#I18NMessage(I18NLoggerProxy,String,String)
     */

    public I18NMessage6P
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

    public I18NMessage6P
        (I18NLoggerProxy loggerProxy,
         String messageId)
    {
        super(loggerProxy,messageId);
    }


    // I18NMessage.

    @Override
    public int getParamCount()
    {
        return 6;
    }


    // INSTANCE METHODS.

    /**
     * A convenience method for {@link
     * I18NMessageProvider#getText(Locale,I18NMessage,Object...)}.
     */

    public String getText
        (Locale locale,
         Object p1,
         Object p2,
         Object p3,
         Object p4,
         Object p5,
         Object p6)
    {
        return getMessageProvider().getText(locale,this,p1,p2,p3,p4,p5,p6);
    }

    /**
     * A convenience method for {@link
     * I18NMessageProvider#getText(I18NMessage,Object...)}.
     */

    public String getText
        (Object p1,
         Object p2,
         Object p3,
         Object p4,
         Object p5,
         Object p6)
    {
        return getMessageProvider().getText(this,p1,p2,p3,p4,p5,p6);
    }

    /**
     * A convenience method for {@link
     * I18NLoggerProxy#error(Object,Throwable,I18NMessage,Object...)}.
     */

    public void error
        (Object category,
         Throwable throwable,
         Object p1,
         Object p2,
         Object p3,
         Object p4,
         Object p5,
         Object p6)
    {
        getLoggerProxy().errorProxy
            (SELF_PROXY,category,throwable,this,p1,p2,p3,p4,p5,p6);
    }

    /**
     * A convenience method for {@link
     * I18NLoggerProxy#error(Object,I18NMessage,Object...)}.
     */
    
    public void error
        (Object category,
         Object p1,
         Object p2,
         Object p3,
         Object p4,
         Object p5,
         Object p6)
    {
        getLoggerProxy().errorProxy(SELF_PROXY,category,this,p1,p2,p3,p4,p5,p6);
    }

    /**
     * A convenience method for {@link
     * I18NLoggerProxy#warn(Object,Throwable,I18NMessage,Object...)}.
     */

    public void warn
        (Object category,
         Throwable throwable,
         Object p1,
         Object p2,
         Object p3,
         Object p4,
         Object p5,
         Object p6)
    {
        getLoggerProxy().warnProxy
            (SELF_PROXY,category,throwable,this,p1,p2,p3,p4,p5,p6);
    }

    /**
     * A convenience method for {@link
     * I18NLoggerProxy#warn(Object,I18NMessage,Object...)}.
     */
    
    public void warn
        (Object category,
         Object p1,
         Object p2,
         Object p3,
         Object p4,
         Object p5,
         Object p6)
    {
        getLoggerProxy().warnProxy(SELF_PROXY,category,this,p1,p2,p3,p4,p5,p6);
    }

    /**
     * A convenience method for {@link
     * I18NLoggerProxy#info(Object,Throwable,I18NMessage,Object...)}.
     */

    public void info
        (Object category,
         Throwable throwable,
         Object p1,
         Object p2,
         Object p3,
         Object p4,
         Object p5,
         Object p6)
    {
        getLoggerProxy().infoProxy
            (SELF_PROXY,category,throwable,this,p1,p2,p3,p4,p5,p6);
    }

    /**
     * A convenience method for {@link
     * I18NLoggerProxy#info(Object,I18NMessage,Object...)}.
     */
    
    public void info
        (Object category,
         Object p1,
         Object p2,
         Object p3,
         Object p4,
         Object p5,
         Object p6)
    {
        getLoggerProxy().infoProxy(SELF_PROXY,category,this,p1,p2,p3,p4,p5,p6);
    }

    /**
     * A convenience method for {@link
     * I18NLoggerProxy#debug(Object,Throwable,I18NMessage,Object...)}.
     */

    public void debug
        (Object category,
         Throwable throwable,
         Object p1,
         Object p2,
         Object p3,
         Object p4,
         Object p5,
         Object p6)
    {
        getLoggerProxy().debugProxy
            (SELF_PROXY,category,throwable,this,p1,p2,p3,p4,p5,p6);
    }

    /**
     * A convenience method for {@link
     * I18NLoggerProxy#debug(Object,I18NMessage,Object...)}.
     */
    
    public void debug
        (Object category,
         Object p1,
         Object p2,
         Object p3,
         Object p4,
         Object p5,
         Object p6)
    {
        getLoggerProxy().debugProxy(SELF_PROXY,category,this,p1,p2,p3,p4,p5,p6);
    }

    /**
     * A convenience method for {@link
     * I18NLoggerProxy#trace(Object,Throwable,I18NMessage,Object...)}.
     */

    public void trace
        (Object category,
         Throwable throwable,
         Object p1,
         Object p2,
         Object p3,
         Object p4,
         Object p5,
         Object p6)
    {
        getLoggerProxy().traceProxy
            (SELF_PROXY,category,throwable,this,p1,p2,p3,p4,p5,p6);
    }

    /**
     * A convenience method for {@link
     * I18NLoggerProxy#trace(Object,I18NMessage,Object...)}.
     */
    
    public void trace
        (Object category,
         Object p1,
         Object p2,
         Object p3,
         Object p4,
         Object p5,
         Object p6)
    {
        getLoggerProxy().traceProxy(SELF_PROXY,category,this,p1,p2,p3,p4,p5,p6);
    }
}
