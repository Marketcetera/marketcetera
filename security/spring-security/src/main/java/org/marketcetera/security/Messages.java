package org.marketcetera.security;

import org.marketcetera.core.util.log.*;
import org.marketcetera.api.attributes.ClassVersion;

/* $License$ */

/**
 * Provides messages for the security package.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: Messages.java 82318 2012-03-22 22:37:06Z colin $
 * @since $Release$
 */
@ClassVersion("$Id: Messages.java 82318 2012-03-22 22:37:06Z colin $")
public interface Messages
{
    /**
     * The message provider.
     */
    static final I18NMessageProvider PROVIDER = new I18NMessageProvider("security", //$NON-NLS-1$
                                                                        Messages.class.getClassLoader());
    /**
     * The logger.
     */
    static final I18NLoggerProxy LOGGER = new I18NLoggerProxy(PROVIDER);
    // the messages
    static final I18NMessage0P NULL_SALT = new I18NMessage0P(LOGGER,
                                                             "null_salt"); //$NON-NLS-1$
    static final I18NMessage0P NULL_ENCODER = new I18NMessage0P(LOGGER,
                                                                "null_encoder"); //$NON-NLS-1$
    static final I18NMessage0P NULL_ENCODING = new I18NMessage0P(LOGGER,
                                                                 "null_encoding"); //$NON-NLS-1$
    static final I18NMessage1P INVALID_ENCODING = new I18NMessage1P(LOGGER,
                                                                    "invalid_encoding"); //$NON-NLS-1$
    static final I18NMessage2P ITERATION_COUNT_TOO_LOW = new I18NMessage2P(LOGGER,
                                                                           "iteration_count_too_low"); //$NON-NLS-1$
    static final I18NMessage2P KEY_LENGTH_TOO_SHORT = new I18NMessage2P(LOGGER,
                                                                        "key_length_too_short"); //$NON-NLS-1$
    static final I18NMessage2P SALT_TOO_SHORT = new I18NMessage2P(LOGGER,
                                                                  "salt_too_short"); //$NON-NLS-1$
    static final I18NMessage0P NULL_ALGORITHM = new I18NMessage0P(LOGGER,
                                                                  "null_algorithm"); //$NON-NLS-1$
    static final I18NMessage0P NULL_SOURCE = new I18NMessage0P(LOGGER,
                                                               "null_source"); //$NON-NLS-1$
    I18NMessage0P CONTEXT_FAILED=
        new I18NMessage0P(Messages.LOGGER,"context_failed"); //$NON-NLS-1$
    I18NMessage0P CONTEXT_ANONYMOUS=
        new I18NMessage0P(Messages.LOGGER,"context_anonymous"); //$NON-NLS-1$
    I18NMessage0P CONTEXT_OVERRIDES=
        new I18NMessage0P(Messages.LOGGER,"context_overrides"); //$NON-NLS-1$
    I18NMessage0P CONSOLE_UNAVAILABLE=
        new I18NMessage0P(Messages.LOGGER,"console_unavailable"); //$NON-NLS-1$
    I18NMessage0P PARSING_FAILED=
        new I18NMessage0P(Messages.LOGGER,"parsing_failed"); //$NON-NLS-1$
    I18NMessage0P SPRING_NAME=
        new I18NMessage0P(Messages.LOGGER,"spring_name"); //$NON-NLS-1$
    I18NMessage0P CLI_NAME=
        new I18NMessage0P(Messages.LOGGER,"cli_name"); //$NON-NLS-1$
    I18NMessage0P CONSOLE_NAME=
        new I18NMessage0P(Messages.LOGGER,"console_name"); //$NON-NLS-1$
    I18NMessage0P USER_PROMPT=
        new I18NMessage0P(Messages.LOGGER,"user_prompt"); //$NON-NLS-1$
    I18NMessage0P USER_DESCRIPTION=
        new I18NMessage0P(Messages.LOGGER,"user_description"); //$NON-NLS-1$
    I18NMessage1P USER_SPRING_USAGE=
        new I18NMessage1P(Messages.LOGGER,"user_spring_usage"); //$NON-NLS-1$
    I18NMessage2P USER_CLI_USAGE=
        new I18NMessage2P(Messages.LOGGER,"user_cli_usage"); //$NON-NLS-1$
    I18NMessage0P USER_CONSOLE_USAGE=
        new I18NMessage0P(Messages.LOGGER,"user_console_usage"); //$NON-NLS-1$
    I18NMessage0P NO_USER=
        new I18NMessage0P(Messages.LOGGER,"no_user"); //$NON-NLS-1$
    I18NMessage0P PASSWORD_PROMPT=
        new I18NMessage0P(Messages.LOGGER,"password_prompt"); //$NON-NLS-1$
    I18NMessage0P PASSWORD_DESCRIPTION=
        new I18NMessage0P(Messages.LOGGER,"password_description"); //$NON-NLS-1$
    I18NMessage1P PASSWORD_SPRING_USAGE=
        new I18NMessage1P(Messages.LOGGER,"password_spring_usage"); //$NON-NLS-1$
    I18NMessage2P PASSWORD_CLI_USAGE=
        new I18NMessage2P(Messages.LOGGER,"password_cli_usage"); //$NON-NLS-1$
    I18NMessage0P PASSWORD_CONSOLE_USAGE=
        new I18NMessage0P(Messages.LOGGER,"password_console_usage"); //$NON-NLS-1$
    I18NMessage0P NO_PASSWORD=
        new I18NMessage0P(Messages.LOGGER,"no_password"); //$NON-NLS-1$
}
