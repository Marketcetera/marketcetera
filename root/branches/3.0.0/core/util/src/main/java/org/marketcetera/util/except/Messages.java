package org.marketcetera.util.except;

import org.marketcetera.core.attributes.ClassVersion;
import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessage2P;
import org.marketcetera.util.log.I18NMessageProvider;

/**
 * The internationalization constants used by this package.
 *
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id: Messages.java 16063 2012-01-31 18:21:55Z colin $
 */

/* $License$ */

@ClassVersion("$Id: Messages.java 16063 2012-01-31 18:21:55Z colin $")
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
