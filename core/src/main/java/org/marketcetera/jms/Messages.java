package org.marketcetera.jms;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.*;

/* $License$ */
/**
 * Internationalized messages used by this package.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public interface Messages {
    /**
     * The message provider
     */
    static final I18NMessageProvider PROVIDER =
            new I18NMessageProvider("core_jms",  //$NON-NLS-1$
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
}