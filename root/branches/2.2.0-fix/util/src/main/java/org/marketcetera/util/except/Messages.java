package org.marketcetera.util.except;

import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage0P;
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
        new I18NMessageProvider("util_except"); //$NON-NLS-1$

    /**
     * The logger.
     */

    static final I18NLoggerProxy LOGGER=
        new I18NLoggerProxy(PROVIDER);

    /*
     * The messages.
     */

    static final I18NMessage0P THREAD_INTERRUPTED=
        new I18NMessage0P(LOGGER,"thread_interrupted"); //$NON-NLS-1$
    static final I18NMessage0P THROWABLE_IGNORED=
        new I18NMessage0P(LOGGER,"throwable_ignored"); //$NON-NLS-1$
    static final I18NMessage2P COMBINE_MESSAGES=
        new I18NMessage2P(LOGGER,"combine_messages"); //$NON-NLS-1$
}
