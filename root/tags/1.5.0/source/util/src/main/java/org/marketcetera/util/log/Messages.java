package org.marketcetera.util.log;

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
        new I18NMessageProvider("util_log"); //$NON-NLS-1$

    /**
     * The logger.
     */

    static final I18NLoggerProxy LOGGER=
        new I18NLoggerProxy(PROVIDER);

    /*
     * The messages.
     */

    static final I18NMessage4P MESSAGE_NOT_FOUND=
        new I18NMessage4P(LOGGER,"message_not_found"); //$NON-NLS-1$
    static final I18NMessage4P UNEXPECTED_EXCEPTION=
        new I18NMessage4P(LOGGER,"unexpected_exception"); //$NON-NLS-1$
}
