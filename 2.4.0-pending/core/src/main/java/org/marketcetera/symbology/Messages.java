package org.marketcetera.symbology;

import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessage1P;
import org.marketcetera.util.log.I18NMessageProvider;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * The internationalization constants used by this package.
 *
 * @author klim@marketcetera.com
 * @since 0.6.0
 * @version $Id$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public interface Messages
{
    /**
     * The message provider.
     */

    static final I18NMessageProvider PROVIDER = 
        new I18NMessageProvider("symbology"); //$NON-NLS-1$

    /**
     * The logger.
     */

    static final I18NLoggerProxy LOGGER = 
        new I18NLoggerProxy(PROVIDER);

    /*
     * The messages.
     */

    static final I18NMessage0P ERROR_EXCHANGES_INIT = 
        new I18NMessage0P(LOGGER,"error_exchanges_init"); //$NON-NLS-1$
    static final I18NMessage1P ERROR_EXCHANGE_DNE = 
        new I18NMessage1P(LOGGER,"error_exchange_dne"); //$NON-NLS-1$
}
