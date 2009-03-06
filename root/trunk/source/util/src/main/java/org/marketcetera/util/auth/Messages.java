package org.marketcetera.util.auth;

import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessage1P;
import org.marketcetera.util.log.I18NMessage2P;
import org.marketcetera.util.log.I18NMessageProvider;
import org.marketcetera.util.misc.ClassVersion;

/**
 * The internationalization constants used by this package.
 *
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public interface Messages
{

    /**
     * The message provider.
     */

    static final I18NMessageProvider PROVIDER=
        new I18NMessageProvider("util_auth"); //$NON-NLS-1$

    /**
     * The logger.
     */

    static final I18NLoggerProxy LOGGER=
        new I18NLoggerProxy(PROVIDER);

    /*
     * The messages.
     */

    static final I18NMessage0P CONTEXT_FAILED=
        new I18NMessage0P(LOGGER,"context_failed"); //$NON-NLS-1$
    static final I18NMessage0P CONTEXT_ANONYMOUS=
        new I18NMessage0P(LOGGER,"context_anonymous"); //$NON-NLS-1$
    static final I18NMessage0P CONTEXT_OVERRIDES=
        new I18NMessage0P(LOGGER,"context_overrides"); //$NON-NLS-1$
    static final I18NMessage0P CONSOLE_UNAVAILABLE=
        new I18NMessage0P(LOGGER,"console_unavailable"); //$NON-NLS-1$
    static final I18NMessage0P PARSING_FAILED=
        new I18NMessage0P(LOGGER,"parsing_failed"); //$NON-NLS-1$

    static final I18NMessage0P SPRING_NAME=
        new I18NMessage0P(LOGGER,"spring_name"); //$NON-NLS-1$
    static final I18NMessage0P CLI_NAME=
        new I18NMessage0P(LOGGER,"cli_name"); //$NON-NLS-1$
    static final I18NMessage0P CONSOLE_NAME=
        new I18NMessage0P(LOGGER,"console_name"); //$NON-NLS-1$

    static final I18NMessage0P USER_PROMPT=
        new I18NMessage0P(LOGGER,"user_prompt"); //$NON-NLS-1$
    static final I18NMessage0P USER_DESCRIPTION=
        new I18NMessage0P(LOGGER,"user_description"); //$NON-NLS-1$
    static final I18NMessage1P USER_SPRING_USAGE=
        new I18NMessage1P(LOGGER,"user_spring_usage"); //$NON-NLS-1$
    static final I18NMessage2P USER_CLI_USAGE=
        new I18NMessage2P(LOGGER,"user_cli_usage"); //$NON-NLS-1$
    static final I18NMessage0P USER_CONSOLE_USAGE=
        new I18NMessage0P(LOGGER,"user_console_usage"); //$NON-NLS-1$
    static final I18NMessage0P NO_USER=
        new I18NMessage0P(LOGGER,"no_user"); //$NON-NLS-1$

    static final I18NMessage0P PASSWORD_PROMPT=
        new I18NMessage0P(LOGGER,"password_prompt"); //$NON-NLS-1$
    static final I18NMessage0P PASSWORD_DESCRIPTION=
        new I18NMessage0P(LOGGER,"password_description"); //$NON-NLS-1$
    static final I18NMessage1P PASSWORD_SPRING_USAGE=
        new I18NMessage1P(LOGGER,"password_spring_usage"); //$NON-NLS-1$
    static final I18NMessage2P PASSWORD_CLI_USAGE=
        new I18NMessage2P(LOGGER,"password_cli_usage"); //$NON-NLS-1$
    static final I18NMessage0P PASSWORD_CONSOLE_USAGE=
        new I18NMessage0P(LOGGER,"password_console_usage"); //$NON-NLS-1$
    static final I18NMessage0P NO_PASSWORD=
        new I18NMessage0P(LOGGER,"no_password"); //$NON-NLS-1$
}
