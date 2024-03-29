package org.marketcetera.util.log;

import java.util.Locale;
import org.marketcetera.util.misc.ClassVersion;

/**
 * An internationalized message, requiring exactly three parameters.
 * 
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id: I18NMessage3P.java 17760 2018-11-14 14:54:11Z colin $
 */

/* $License$ */

@ClassVersion("$Id: I18NMessage3P.java 17760 2018-11-14 14:54:11Z colin $")
public class I18NMessage3P
    extends I18NMessage
{

    // CLASS DATA.

    private static final long serialVersionUID=1L;

    /**
     * The logging proxy name.
     */

    private final static String SELF_PROXY=
        I18NMessage3P.class.getName();
    /**
     * Create a new I18NMessage3P instance.
     *
     * @param loggerProxy an <code>I18NLoggerProxy</code> value
     * @param messageId a <code>String</code> value
     * @param entryId a <code>String</code> value
     * @see I18NMessage#I18NMessage(I18NLoggerProxy,String,String)
     */
    public I18NMessage3P(I18NLoggerProxy loggerProxy,
                         String messageId,
                         String entryId)
    {
        super(loggerProxy,messageId,entryId);
    }
    /**
     * Create a new I18NMessage3P instance.
     *
     * @param loggerProxy an <code>I18NLoggerProxy</code> value
     * @param messageId a <code>String</code> value
     * @see I18NMessage#I18NMessage(I18NLoggerProxy,String)
     */
    public I18NMessage3P(I18NLoggerProxy loggerProxy,
                         String messageId)
    {
        super(loggerProxy,messageId);
    }
    @Override
    public int getParamCount()
    {
        return 3;
    }
    /**
     * Get the text value.
     * 
     * @param locale a <code>Locale</code> value
     * @param p1 an <code>Object</code> value
     * @param p2 an <code>Object</code> value
     * @param p3 an <code>Object</code> value
     * @return a <code>String</code> value
     */
    public String getText(Locale locale,
                          Object p1,
                          Object p2,
                          Object p3)
    {
        return getMessageProvider().getText(locale,this,p1,p2,p3);
    }
    /**
     * Get the text value.
     * 
     * @param p1 an <code>Object</code> value
     * @param p2 an <code>Object</code> value
     * @param p3 an <code>Object</code> value
     * @return a <code>String</code> value
     */
    public String getText(Object p1,
                          Object p2,
                          Object p3)
    {
        return getMessageProvider().getText(this,p1,p2,p3);
    }
    /**
     * Log an error message.
     *
     * @param category an <code>Object</code> value
     * @param throwable a <code>Throwable</code> value
     * @param p1 an <code>Object</code> value
     * @param p2 an <code>Object</code> value
     * @param p3 an <code>Object</code> value
     */
    public void error(Object category,
                      Throwable throwable,
                      Object p1,
                      Object p2,
                      Object p3)
    {
        getLoggerProxy().errorProxy
            (SELF_PROXY,category,throwable,this,p1,p2,p3);
    }
    /**
     * Log an error message.
     *
     * @param category an <code>Object</code> value
     * @param p1 an <code>Object</code> value
     * @param p2 an <code>Object</code> value
     * @param p3 an <code>Object</code> value
     */
    public void error(Object category,
                      Object p1,
                      Object p2,
                      Object p3)
    {
        getLoggerProxy().errorProxy(SELF_PROXY,category,this,p1,p2,p3);
    }
    /**
     * Log a warn message.
     *
     * @param category an <code>Object</code> value
     * @param throwable a <code>Throwable</code> value
     * @param p1 an <code>Object</code> value
     * @param p2 an <code>Object</code> value
     * @param p3 an <code>Object</code> value
     */
    public void warn(Object category,
                     Throwable throwable,
                     Object p1,
                     Object p2,
                     Object p3)
    {
        getLoggerProxy().warnProxy
            (SELF_PROXY,category,throwable,this,p1,p2,p3);
    }
    /**
     * Log a warn message.
     *
     * @param category an <code>Object</code> value
     * @param p1 an <code>Object</code> value
     * @param p2 an <code>Object</code> value
     * @param p3 an <code>Object</code> value
     */
    public void warn(Object category,
                     Object p1,
                     Object p2,
                     Object p3)
    {
        getLoggerProxy().warnProxy(SELF_PROXY,category,this,p1,p2,p3);
    }
    /**
     * Log an info message.
     *
     * @param category an <code>Object</code> value
     * @param throwable a <code>Throwable</code> value
     * @param p1 an <code>Object</code> value
     * @param p2 an <code>Object</code> value
     * @param p3 an <code>Object</code> value
     */
    public void info(Object category,
                     Throwable throwable,
                     Object p1,
                     Object p2,
                     Object p3)
    {
        getLoggerProxy().infoProxy
            (SELF_PROXY,category,throwable,this,p1,p2,p3);
    }
    /**
     * Log an info message.
     *
     * @param category an <code>Object</code> value
     * @param p1 an <code>Object</code> value
     * @param p2 an <code>Object</code> value
     * @param p3 an <code>Object</code> value
     */
    public void info(Object category,
                     Object p1,
                     Object p2,
                     Object p3)
    {
        getLoggerProxy().infoProxy(SELF_PROXY,category,this,p1,p2,p3);
    }
    /**
     * Log a debug message.
     *
     * @param category an <code>Object</code> value
     * @param throwable a <code>Throwable</code> value
     * @param p1 an <code>Object</code> value
     * @param p2 an <code>Object</code> value
     * @param p3 an <code>Object</code> value
     */
    public void debug(Object category,
                      Throwable throwable,
                      Object p1,
                      Object p2,
                      Object p3)
    {
        getLoggerProxy().debugProxy
            (SELF_PROXY,category,throwable,this,p1,p2,p3);
    }
    /**
     * Log a debug message.
     *
     * @param category an <code>Object</code> value
     * @param p1 an <code>Object</code> value
     * @param p2 an <code>Object</code> value
     * @param p3 an <code>Object</code> value
     */
    public void debug(Object category,
                      Object p1,
                      Object p2,
                      Object p3)
    {
        getLoggerProxy().debugProxy(SELF_PROXY,category,this,p1,p2,p3);
    }
    /**
     * Log a trace message.
     *
     * @param category an <code>Object</code> value
     * @param throwable a <code>Throwable</code> value
     * @param p1 an <code>Object</code> value
     * @param p2 an <code>Object</code> value
     * @param p3 an <code>Object</code> value
     */
    public void trace(Object category,
                      Throwable throwable,
                      Object p1,
                      Object p2,
                      Object p3)
    {
        getLoggerProxy().traceProxy
            (SELF_PROXY,category,throwable,this,p1,p2,p3);
    }
    /**
     * Log a trace message.
     *
     * @param category an <code>Object</code> value
     * @param p1 an <code>Object</code> value
     * @param p2 an <code>Object</code> value
     * @param p3 an <code>Object</code> value
     */
    public void trace(Object category,
                      Object p1,
                      Object p2,
                      Object p3)
    {
        getLoggerProxy().traceProxy(SELF_PROXY,category,this,p1,p2,p3);
    }
}
