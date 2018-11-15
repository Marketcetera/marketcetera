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
    private static final long serialVersionUID=1L;
    /**
     * The logging proxy name.
     */
    private final static String SELF_PROXY = I18NMessageNP.class.getName();
    /**
     * Create a new I18NMessageNP instance.
     *
     * @param loggerProxy an <code>I18NLoggerProxy</code> value
     * @param messageId a <code>String</code> value
     * @param entryId a <code>String</code> value
     * @see I18NMessage#I18NMessage(I18NLoggerProxy,String,String)
     */
    public I18NMessageNP(I18NLoggerProxy loggerProxy,
                         String messageId,
                         String entryId)
    {
        super(loggerProxy,messageId,entryId);
    }
    /**
     * Create a new I18NMessageNP instance.
     *
     * @param loggerProxy an <code>I18NLoggerProxy</code> value
     * @param messageId a <code>String</code> value
     * @see I18NMessage#I18NMessage(I18NLoggerProxy,String)
     */
    public I18NMessageNP(I18NLoggerProxy loggerProxy,
                         String messageId)
    {
        super(loggerProxy,messageId);
    }
    @Override
    public int getParamCount()
    {
        return -1;
    }
    /**
     * Get the text value.
     * 
     * @param locale a <code>Locale</code> value
     * @param ps an <code>Object...</code> value
     * @return a <code>String</code> value
     */
    public String getText(Locale locale,
                          Object... ps)
    {
        return getMessageProvider().getText(locale,this,ps);
    }
    /**
     * Get the text value.
     * 
     * @param ps an <code>Object...</code> value
     * @return a <code>String</code> value
     */
    public String getText(Object... ps)
    {
        return getMessageProvider().getText(this,ps);
    }
    /**
     * Log an error message.
     *
     * @param category an <code>Object</code> value
     * @param throwable a <code>Throwable</code> value
     * @param ps an <code>Object...</code> value
     */
    public void error(Object category,
                      Throwable throwable,
                      Object... ps)
    {
        getLoggerProxy().errorProxy(SELF_PROXY,category,throwable,this,ps);
    }
    /**
     * Log an error message.
     *
     * @param category an <code>Object</code> value
     * @param ps an <code>Object...</code> value
     */
    public void error(Object category,
                      Object... ps)
    {
        getLoggerProxy().errorProxy(SELF_PROXY,category,this,ps);
    }
    /**
     * Log a warn message.
     *
     * @param category an <code>Object</code> value
     * @param throwable a <code>Throwable</code> value
     * @param ps an <code>Object...</code> value
     */
    public void warn(Object category,
                     Throwable throwable,
                     Object... ps)
    {
        getLoggerProxy().warnProxy(SELF_PROXY,category,throwable,this,ps);
    }
    /**
     * Log a warn message.
     *
     * @param category an <code>Object</code> value
     * @param ps an <code>Object...</code> value
     */
    public void warn(Object category,
                     Object... ps)
    {
        getLoggerProxy().warnProxy(SELF_PROXY,category,this,ps);
    }
    /**
     * Log an info message.
     *
     * @param category an <code>Object</code> value
     * @param throwable a <code>Throwable</code> value
     * @param ps an <code>Object...</code> value
     */
    public void info(Object category,
                     Throwable throwable,
                     Object... ps)
    {
        getLoggerProxy().infoProxy(SELF_PROXY,category,throwable,this,ps);
    }
    /**
     * Log an info message.
     *
     * @param category an <code>Object</code> value
     * @param ps an <code>Object...</code> value
     */
    public void info(Object category,
                     Object... ps)
    {
        getLoggerProxy().infoProxy(SELF_PROXY,category,this,ps);
    }
    /**
     * Log a debug message.
     *
     * @param category an <code>Object</code> value
     * @param throwable a <code>Throwable</code> value
     * @param ps an <code>Object...</code> value
     */
    public void debug(Object category,
                      Throwable throwable,
                      Object... ps)
    {
        getLoggerProxy().debugProxy(SELF_PROXY,category,throwable,this,ps);
    }
    /**
     * Log a debug message.
     *
     * @param category an <code>Object</code> value
     * @param ps an <code>Object...</code> value
     */
    public void debug(Object category,
                      Object... ps)
    {
        getLoggerProxy().debugProxy(SELF_PROXY,category,this,ps);
    }
    /**
     * Log a trace message.
     *
     * @param category an <code>Object</code> value
     * @param throwable a <code>Throwable</code> value
     * @param ps an <code>Object...</code> value
     */
    public void trace(Object category,
                      Throwable throwable,
                      Object... ps)
    {
        getLoggerProxy().traceProxy(SELF_PROXY,category,throwable,this,ps);
    }
    /**
     * Log a trace message.
     *
     * @param category an <code>Object</code> value
     * @param ps an <code>Object...</code> value
     */
    public void trace(Object category,
                      Object... ps)
    {
        getLoggerProxy().traceProxy(SELF_PROXY,category,this,ps);
    }
}
