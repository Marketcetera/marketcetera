package org.marketcetera.saclient;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.*;

/* $License$ */
/**
 * Internationalized Message keys for classes in this package.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface Messages {
    /**
     * The message provider
     */
    static final I18NMessageProvider PROVIDER =
            new I18NMessageProvider("saclient",  //$NON-NLS-1$
                    Messages.class.getClassLoader());
    /**
     * The message logger.
     */
    static final I18NLoggerProxy LOGGER =
            new I18NLoggerProxy(PROVIDER);
    static final I18NMessage3P ERROR_WS_CONNECT =
            new I18NMessage3P(LOGGER, "error_ws_connect");   //$NON-NLS-1$
    static final I18NMessage2P ERROR_JMS_CONNECT =
            new I18NMessage2P(LOGGER, "error_jms_connect");   //$NON-NLS-1$
    static final I18NMessage0P CLIENT_CLOSED =
            new I18NMessage0P(LOGGER, "client_closed");   //$NON-NLS-1$
    static final I18NMessage0P CLIENT_DISCONNECTED =
            new I18NMessage0P(LOGGER, "client_disconnected");   //$NON-NLS-1$
    static final I18NMessage1P ERROR_WS_OPERATION =
            new I18NMessage1P(LOGGER, "error_ws_operation");   //$NON-NLS-1$

    static final I18NMessage1P LOG_ERROR_RECEIVE_DATA =
            new I18NMessage1P(LOGGER, "log_error_receive_data");   //$NON-NLS-1$
    static final I18NMessage1P LOG_ERROR_RECEIVE_CONNECT_STATUS =
            new I18NMessage1P(LOGGER, "log_error_receive_connect_status");   //$NON-NLS-1$

}