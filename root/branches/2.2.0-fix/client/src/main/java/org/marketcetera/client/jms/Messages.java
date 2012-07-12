package org.marketcetera.client.jms;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.*;

/* $License$ */
/**
 * Internationalized messages used by this package.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public interface Messages {
    /**
     * The message provider
     */
    static final I18NMessageProvider PROVIDER =
            new I18NMessageProvider("client_jms",  //$NON-NLS-1$ 
                    Messages.class.getClassLoader());
    /**
     * The message logger.
     */
    static final I18NLoggerProxy LOGGER =
            new I18NLoggerProxy(PROVIDER);

    static final I18NMessage1P UNEXPECTED_MESSAGE_TO_SEND =
            new I18NMessage1P(LOGGER, "unexpected_message_to_send");   //$NON-NLS-1$
    static final I18NMessage1P UNEXPECTED_MESSAGE_RECEIVED =
            new I18NMessage1P(LOGGER, "unexpected_message_received");   //$NON-NLS-1$
    static final I18NMessage1P ERROR_CONVERTING_MESSAGE_TO_OBJECT =
            new I18NMessage1P(LOGGER, "error_converting_message_to_object");   //$NON-NLS-1$
    static final I18NMessage1P ERROR_CONVERTING_OBJECT_TO_MESSAGE =
            new I18NMessage1P(LOGGER, "error_converting_object_to_message");   //$NON-NLS-1$
    static final I18NMessage1P ERROR_JMS_MESSAGE_CONVERSION = 
        new I18NMessage1P(LOGGER,"error_jms_message_conversion"); //$NON-NLS-1$

    static final I18NMessage2P ORDER_ENVELOPE_TO_STRING =
        new I18NMessage2P(LOGGER,"order_envelope_to_string"); //$NON-NLS-1$
}
