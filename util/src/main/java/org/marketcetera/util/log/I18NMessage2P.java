package org.marketcetera.util.log;

import java.util.Locale;
import org.marketcetera.util.misc.ClassVersion;

/**
 * An internationalized message, requiring exactly two parameters.
 * 
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class I18NMessage2P
    extends I18NMessage
{

    // CLASS DATA.

    private static final long serialVersionUID=1L;

    /**
     * The logging proxy name.
     */

    private final static String SELF_PROXY=
        I18NMessage2P.class.getName();


    // CONSTRUCTORS.

    /**
     * Create a new I18NMessage2P instance.
     *
     * @param loggerProxy an <code>I18NLoggerProxy</code> value
     * @param messageId a <code>String</code> value
     * @param entryId a <code>String</code> value
     * @see I18NMessage#I18NMessage(I18NLoggerProxy,String,String)
     */
    public I18NMessage2P(I18NLoggerProxy loggerProxy,
                         String messageId,
                         String entryId)
    {
        super(loggerProxy,messageId,entryId);
    }
    /**
     * Create a new I18NMessage2P instance.
     *
     * @param loggerProxy an <code>I18NLoggerProxy</code> value
     * @param messageId a <code>String</code> value
     * @see I18NMessage#I18NMessage(I18NLoggerProxy,String)
     */
    public I18NMessage2P(I18NLoggerProxy loggerProxy,
                         String messageId)
    {
        super(loggerProxy,messageId);
    }
    @Override
    public int getParamCount()
    {
        return 2;
    }
    /**
     * Get the text value.
     * 
     * @param locale a <code>Locale</code> value
     * @param p1 an <code>Object</code> value
     * @param p2 an <code>Object</code> value
     * @return a <code>String</code> value
     */
    public String getText(Locale locale,
                          Object p1,
                          Object p2)
    {
        return getMessageProvider().getText(locale,this,p1,p2);
    }
    /**
     * Get the text value.
     * 
     * @param p1 an <code>Object</code> value
     * @param p2 an <code>Object</code> value
     * @return a <code>String</code> value
     */
    public String getText(Object p1,
                          Object p2)
    {
        return getMessageProvider().getText(this,p1,p2);
    }
    /**
     * Log an error message.
     *
     * @param category an <code>Object</code> value
     * @param throwable a <code>Throwable</code> value
     * @param p1 an <code>Object</code> value
     * @param p2 an <code>Object</code> value
     */
    public void error(Object category,
                      Throwable throwable,
                      Object p1,
                      Object p2)
    {
        getLoggerProxy().errorProxy(SELF_PROXY,category,throwable,this,p1,p2);
    }
    /**
     * Log an error message.
     *
     * @param category an <code>Object</code> value
     * @param p1 an <code>Object</code> value
     * @param p2 an <code>Object</code> value
     */
    public void error(Object category,
                      Object p1,
                      Object p2)
    {
        getLoggerProxy().errorProxy(SELF_PROXY,category,this,p1,p2);
    }
    /**
     * Log a warn message.
     *
     * @param category an <code>Object</code> value
     * @param throwable a <code>Throwable</code> value
     * @param p1 an <code>Object</code> value
     * @param p2 an <code>Object</code> value
     */
    public void warn(Object category,
                     Throwable throwable,
                     Object p1,
                     Object p2)
    {
        getLoggerProxy().warnProxy(SELF_PROXY,category,throwable,this,p1,p2);
    }
    /**
     * Log a warn message.
     *
     * @param category an <code>Object</code> value
     * @param p1 an <code>Object</code> value
     * @param p2 an <code>Object</code> value
     */
    public void warn(Object category,
                     Object p1,
                     Object p2)
    {
        getLoggerProxy().warnProxy(SELF_PROXY,category,this,p1,p2);
    }
    /**
     * Log an info message.
     *
     * @param category an <code>Object</code> value
     * @param throwable a <code>Throwable</code> value
     * @param p1 an <code>Object</code> value
     * @param p2 an <code>Object</code> value
     */
    public void info(Object category,
                     Throwable throwable,
                     Object p1,
                     Object p2)
    {
        getLoggerProxy().infoProxy(SELF_PROXY,category,throwable,this,p1,p2);
    }
    /**
     * Log an info message.
     *
     * @param category an <code>Object</code> value
     * @param p1 an <code>Object</code> value
     * @param p2 an <code>Object</code> value
     */
    public void info(Object category,
                     Object p1,
                     Object p2)
    {
        getLoggerProxy().infoProxy(SELF_PROXY,category,this,p1,p2);
    }
    /**
     * Log a debug message.
     *
     * @param category an <code>Object</code> value
     * @param throwable a <code>Throwable</code> value
     * @param p1 an <code>Object</code> value
     * @param p2 an <code>Object</code> value
     */
    public void debug(Object category,
                      Throwable throwable,
                      Object p1,
                      Object p2)
    {
        getLoggerProxy().debugProxy(SELF_PROXY,category,throwable,this,p1,p2);
    }
    /**
     * Log a debug message.
     *
     * @param category an <code>Object</code> value
     * @param p1 an <code>Object</code> value
     * @param p2 an <code>Object</code> value
     */
    public void debug(Object category,
                      Object p1,
                      Object p2)
    {
        getLoggerProxy().debugProxy(SELF_PROXY,category,this,p1,p2);
    }
    /**
     * Log a trace message.
     *
     * @param category an <code>Object</code> value
     * @param throwable a <code>Throwable</code> value
     * @param p1 an <code>Object</code> value
     * @param p2 an <code>Object</code> value
     */
    public void trace(Object category,
                      Throwable throwable,
                      Object p1,
                      Object p2)
    {
        getLoggerProxy().traceProxy(SELF_PROXY,category,throwable,this,p1,p2);
    }
    /**
     * Log a trace message.
     *
     * @param category an <code>Object</code> value
     * @param p1 an <code>Object</code> value
     * @param p2 an <code>Object</code> value
     */
    public void trace(Object category,
                      Object p1,
                      Object p2)
    {
        getLoggerProxy().traceProxy(SELF_PROXY,category,this,p1,p2);
    }
}
