package org.marketcetera.util.log;

import java.util.Locale;
import org.marketcetera.core.ClassVersion;

/**
 * A bound message, representing the combination of an {@link
 * I18NMessage} and its parameters, if any.
 * 
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public interface I18NBoundMessage
{

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

    Object[] getParams();

    /**
     * A convenience method for {@link
     * I18NMessageProvider#getText(Locale,I18NMessage,Object...)}.
     */

    String getText
        (Locale locale);

    /**
     * A convenience method for {@link
     * I18NMessageProvider#getText(I18NMessage,Object...)}.
     */

    String getText();

    /**
     * A convenience method for {@link
     * I18NLoggerProxy#error(Object,Throwable,I18NMessage,Object...)}.
     */

    void error
        (Object category,
         Throwable throwable);

    /**
     * A convenience method for {@link
     * I18NLoggerProxy#error(Object,I18NMessage,Object...)}.
     */
    
    void error
        (Object category);

    /**
     * A convenience method for {@link
     * I18NLoggerProxy#warn(Object,Throwable,I18NMessage,Object...)}.
     */

    void warn
        (Object category,
         Throwable throwable);

    /**
     * A convenience method for {@link
     * I18NLoggerProxy#warn(Object,I18NMessage,Object...)}.
     */
    
    void warn
        (Object category);

    /**
     * A convenience method for {@link
     * I18NLoggerProxy#info(Object,Throwable,I18NMessage,Object...)}.
     */

    void info
        (Object category,
         Throwable throwable);

    /**
     * A convenience method for {@link
     * I18NLoggerProxy#info(Object,I18NMessage,Object...)}.
     */
    
    void info
        (Object category);

    /**
     * A convenience method for {@link
     * I18NLoggerProxy#debug(Object,Throwable,I18NMessage,Object...)}.
     */

    void debug
        (Object category,
         Throwable throwable);

    /**
     * A convenience method for {@link
     * I18NLoggerProxy#debug(Object,I18NMessage,Object...)}.
     */
    
    void debug
        (Object category);

    /**
     * A convenience method for {@link
     * I18NLoggerProxy#trace(Object,Throwable,I18NMessage,Object...)}.
     */

    void trace
        (Object category,
         Throwable throwable);

    /**
     * A convenience method for {@link
     * I18NLoggerProxy#trace(Object,I18NMessage,Object...)}.
     */
    
    void trace
        (Object category);
}
