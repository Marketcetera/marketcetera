package org.marketcetera.core.util.misc;

import org.marketcetera.core.util.log.I18NLoggerProxy;
import org.marketcetera.core.util.log.I18NMessageProvider;

/**
 * The internationalization constants used by this package.
 *
 * @since 0.5.0
 * @version $Id: Messages.java 16063 2012-01-31 18:21:55Z colin $
 */

/* $License$ */

public interface Messages
{

    /**
     * The message provider.
     */

    static final I18NMessageProvider PROVIDER=
        new I18NMessageProvider("util_misc"); //$NON-NLS-1$

    /**
     * The logger.
     */

    static final I18NLoggerProxy LOGGER=
        new I18NLoggerProxy(PROVIDER);

    /*
     * The messages.
     */
}
