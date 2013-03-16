package org.marketcetera.core.marketdata;

import org.marketcetera.core.util.log.I18NLoggerProxy;
import org.marketcetera.core.util.log.I18NMessage0P;
import org.marketcetera.core.util.log.I18NMessageProvider;

/* $License$ */

/**
 * The internationalization constants used by this package.
 *
 * @since 0.6.0
 * @version $Id: TestMessages.java 82329 2012-04-10 16:28:13Z colin $
 */
public interface TestMessages
{
    /**
     * The message provider.
     */

    static final I18NMessageProvider PROVIDER = 
        new I18NMessageProvider("marketdata_test"); //$NON-NLS-1$

    /**
     * The logger.
     */

    static final I18NLoggerProxy LOGGER = 
        new I18NLoggerProxy(PROVIDER);

    /*
     * The messages.
     */

    static final I18NMessage0P EXPECTED_EXCEPTION = 
        new I18NMessage0P(LOGGER,"expected_exception"); //$NON-NLS-1$
}
