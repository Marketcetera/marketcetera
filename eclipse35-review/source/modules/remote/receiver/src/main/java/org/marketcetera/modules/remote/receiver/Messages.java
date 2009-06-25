package org.marketcetera.modules.remote.receiver;

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
            new I18NMessageProvider("modules_remote_receiver",  //$NON-NLS-1$ 
                    Messages.class.getClassLoader());
    /**
     * The message logger.
     */
    static final I18NLoggerProxy LOGGER =
            new I18NLoggerProxy(PROVIDER);
    static final I18NMessage0P PROVIDER_DESCRIPTION =
            new I18NMessage0P(LOGGER, "provider_description");   //$NON-NLS-1$
    static final I18NMessage0P ILLEGAL_STATE_SET_URL =
            new I18NMessage0P(LOGGER, "illegal_state_set_url");   //$NON-NLS-1$
    static final I18NMessage0P PROMPT_USERNAME =
            new I18NMessage0P(LOGGER, "prompt_username");   //$NON-NLS-1$
    static final I18NMessage0P PROMPT_PASSWORD =
            new I18NMessage0P(LOGGER, "prompt_password");   //$NON-NLS-1$
    static final I18NMessage0P EMPTY_USERNAME =
            new I18NMessage0P(LOGGER, "empty_username");   //$NON-NLS-1$
    static final I18NMessage1P USER_LOGIN_FAIL =
            new I18NMessage1P(LOGGER, "user_login_fail");   //$NON-NLS-1$
    static final I18NMessage0P USER_LOGIN_ERROR =
            new I18NMessage0P(LOGGER, "user_login_error");   //$NON-NLS-1$
    static final I18NMessage0P ERROR_STARTING_MODULE =
            new I18NMessage0P(LOGGER, "error_starting_module");   //$NON-NLS-1$
    static final I18NMessage0P START_FAIL_NO_URL =
            new I18NMessage0P(LOGGER, "start_fail_no_url");   //$NON-NLS-1$
    static final I18NMessage1P NULL_LEVEL_VALUE =
            new I18NMessage1P(LOGGER, "null_level_value");   //$NON-NLS-1$
    static final I18NMessage0P ILLEGAL_STATE_SET_SKIP_JAAS =
            new I18NMessage0P(LOGGER, "illegal_state_set_skip_jaas");   //$NON-NLS-1$
    static final I18NMessage1P ERROR_WHEN_TRANSMITTING =
            new I18NMessage1P(LOGGER, "error_when_transmitting");   //$NON-NLS-1$


    static final I18NMessage1P USER_LOGIN_ERROR_LOG =
            new I18NMessage1P(LOGGER, "user_login_error_log");   //$NON-NLS-1$
    static final I18NMessage1P USER_LOGIN_LOG =
            new I18NMessage1P(LOGGER, "user_login_log");   //$NON-NLS-1$
    static final I18NMessage1P USER_LOGOUT_LOG =
            new I18NMessage1P(LOGGER, "user_logout_log");   //$NON-NLS-1$
    static final I18NMessage0P ERROR_STOPPING_MODULE_LOG =
            new I18NMessage0P(LOGGER, "error_stopping_module_log");   //$NON-NLS-1$
}