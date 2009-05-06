package org.marketcetera.modules.remote.emitter;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.*;

/* $License$ */
/**
 * Internationalized Message keys for classes in this package.
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
            new I18NMessageProvider("modules_remote_emitter",  //$NON-NLS-1$ 
                    Messages.class.getClassLoader());
    /**
     * The message logger.
     */
    static final I18NLoggerProxy LOGGER =
            new I18NLoggerProxy(PROVIDER);
    static final I18NMessage0P PROVIDER_DESCRIPTION =
            new I18NMessage0P(LOGGER, "provider_description");   //$NON-NLS-1$
    static final I18NMessage0P START_FAIL_NO_URL =
            new I18NMessage0P(LOGGER, "start_fail_no_url");   //$NON-NLS-1$
    static final I18NMessage0P ERROR_STARTING_MODULE =
            new I18NMessage0P(LOGGER, "error_starting_module");   //$NON-NLS-1$
    static final I18NMessage0P ILLEGAL_STATE_CHANGE_URL =
            new I18NMessage0P(LOGGER, "illegal_state_change_url");   //$NON-NLS-1$
    static final I18NMessage0P ILLEGAL_STATE_CHANGE_USERNAME =
            new I18NMessage0P(LOGGER, "illegal_state_change_username");   //$NON-NLS-1$
    static final I18NMessage0P ILLEGAL_STATE_CHANGE_PASSWORD =
            new I18NMessage0P(LOGGER, "illegal_state_change_password");   //$NON-NLS-1$
    static final I18NMessage0P ATTRIB_CHANGE_NOTIFICATION =
            new I18NMessage0P(LOGGER, "attrib_change_notification");   //$NON-NLS-1$

    static final I18NMessage1P LOG_ERROR_STOPPING_MODULE =
            new I18NMessage1P(LOGGER, "log_error_stopping_module");   //$NON-NLS-1$
    static final I18NMessage0P LOG_ERROR_RECEIVER_CONNECTION =
            new I18NMessage0P(LOGGER, "log_error_receiver_connection");   //$NON-NLS-1$

}