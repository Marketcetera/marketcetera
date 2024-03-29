package org.marketcetera.util.log;

import java.io.Serializable;
import java.util.Locale;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A bound message, representing the combination of an {@link
 * I18NMessage} and its parameters, if any.
 * 
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id: I18NBoundMessage.java 17760 2018-11-14 14:54:11Z colin $
 */

/* $License$ */

@ClassVersion("$Id: I18NBoundMessage.java 17760 2018-11-14 14:54:11Z colin $")
public interface I18NBoundMessage
    extends Serializable
{
    /**
     * An empty parameter list for a bound message.
     */
    static final Serializable[] EMPTY_PARAMS = new Serializable[0];
    /**
     * Returns the logger proxy that can log the receiver.
     *
     * @return The proxy.
     */
    I18NLoggerProxy getLoggerProxy();
    /**
     * Returns the message provider that can map the receiver.
     *
     * @return The message provider.
     */
    I18NMessageProvider getMessageProvider();
    /**
     * Returns the receiver's message.
     *
     * @return The message.
     */
    I18NMessage getMessage();
    /**
     * Returns the receiver's parameters.
     *
     * @return The parameters.
     */
    Serializable[] getParams();
    /**
     * Returns the receiver's parameters as objects.
     *
     * @return The parameters.
     */
    Object[] getParamsAsObjects();
    /**
     * A convenience method for {@link I18NMessageProvider#getText(Locale,I18NMessage,Object...)}.
     * 
     * @param locale a <code>Locale</code> value
     * @return a <code>String</code> value
     */
    String getText(Locale locale);
    /**
     * A convenience method for {@link I18NMessageProvider#getText(I18NMessage,Object...)}.
     * 
     * @return a <code>String</code> value
     */
    String getText();
    /**
     * A convenience method for {@link I18NLoggerProxy#error(Object,Throwable,I18NMessage,Object...)}.
     * 
     * @param category an <code>Object</code> value
     * @param throwable a <code>Throwable</code> value
     */
    void error(Object category,
               Throwable throwable);
    /**
     * A convenience method for {@link I18NLoggerProxy#error(Object,I18NMessage,Object...)}.
     * 
     * @param category an <code>Object</code> value
     */
    void error(Object category);
    /**
     * A convenience method for {@link I18NLoggerProxy#warn(Object,Throwable,I18NMessage,Object...)}.
     * 
     * @param category an <code>Object</code> value
     * @param throwable a <code>Throwable</code> value
     */
    void warn(Object category,
              Throwable throwable);
    /**
     * A convenience method for {@link I18NLoggerProxy#warn(Object,I18NMessage,Object...)}.
     * 
     * @param category an <code>Object</code> value
     */
    void warn(Object category);
    /**
     * A convenience method for {@link I18NLoggerProxy#info(Object,Throwable,I18NMessage,Object...)}.
     * 
     * @param category an <code>Object</code> value
     * @param throwable a <code>Throwable</code> value
     */
    void info(Object category,
              Throwable throwable);
    /**
     * A convenience method for {@link I18NLoggerProxy#info(Object,I18NMessage,Object...)}.
     * 
     * @param category an <code>Object</code> value
     */
    void info(Object category);
    /**
     * A convenience method for {@link I18NLoggerProxy#debug(Object,Throwable,I18NMessage,Object...)}.
     * 
     * @param category an <code>Object</code> value
     * @param throwable a <code>Throwable</code> value
     */
    void debug(Object category,
               Throwable throwable);
    /**
     * A convenience method for {@link I18NLoggerProxy#debug(Object,I18NMessage,Object...)}.
     * 
     * @param category an <code>Object</code> value
     */
    void debug(Object category);
    /**
     * A convenience method for {@link I18NLoggerProxy#trace(Object,Throwable,I18NMessage,Object...)}.
     * 
     * @param category an <code>Object</code> value
     * @param throwable a <code>Throwable</code> value
     */
    void trace(Object category,
               Throwable throwable);
    /**
     * A convenience method for {@link
     * I18NLoggerProxy#trace(Object,I18NMessage,Object...)}.
     * 
     * @param category an <code>Object</code> value
     */
    void trace(Object category);
}
