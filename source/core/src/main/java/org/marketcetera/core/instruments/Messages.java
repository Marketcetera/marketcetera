package org.marketcetera.core.instruments;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.I18NMessageProvider;
import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage2P;
import org.marketcetera.util.log.I18NMessage1P;

/* $License$ */
/**
 * Internationalized messages for this package.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public interface Messages {
    /**
     * The message provider.
     */

    static final I18NMessageProvider PROVIDER =
        new I18NMessageProvider("core_instruments");  //$NON-NLS-1$

    /**
     * The logger.
     */

    static final I18NLoggerProxy LOGGER =
        new I18NLoggerProxy(PROVIDER);

    static final I18NMessage2P NO_HANDLER_FOR_INSTRUMENT =
            new I18NMessage2P(LOGGER, "no_handler_for_instrument");   //$NON-NLS-1$
    static final I18NMessage2P NO_HANDLER_FOR_VALUE =
            new I18NMessage2P(LOGGER, "no_handler_for_value");   //$NON-NLS-1$
    static final I18NMessage1P OPTION_NOT_SUPPORTED_FOR_FIX_VERSION =
            new I18NMessage1P(LOGGER, "option_not_supported_for_fix_version");   //$NON-NLS-1$
    static final I18NMessage1P FUTURES_NOT_SUPPORTED_FOR_FIX_VERSION =
            new I18NMessage1P(LOGGER, "futures_not_supported_for_fix_version");   //$NON-NLS-1$


}
