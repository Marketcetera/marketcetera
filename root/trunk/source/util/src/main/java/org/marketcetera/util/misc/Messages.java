package org.marketcetera.util.misc;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessageProvider;

/**
 * The internationalization constants used by this package.
 *
 * @author tlerios
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
        new I18NMessageProvider("util_misc");

    /**
     * The logger.
     */

    static final I18NLoggerProxy LOGGER=
        new I18NLoggerProxy(PROVIDER);

    /*
     * The messages.
     */

    static final I18NMessage0P USER_PROMPT=
        new I18NMessage0P(LOGGER,"user_prompt");
    static final I18NMessage0P USER_DESCRIPTION=
        new I18NMessage0P(LOGGER,"user_description");
    static final I18NMessage0P PASSWORD_PROMPT=
        new I18NMessage0P(LOGGER,"password_prompt");
    static final I18NMessage0P PASSWORD_DESCRIPTION=
        new I18NMessage0P(LOGGER,"password_description");
    static final I18NMessage0P PARSING_FAILED=
        new I18NMessage0P(LOGGER,"parsing_failed");
    static final I18NMessage0P NO_USER=
        new I18NMessage0P(LOGGER,"no_user");
    static final I18NMessage0P NO_PASSWORD=
        new I18NMessage0P(LOGGER,"no_password");
}
