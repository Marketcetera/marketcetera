package org.marketcetera.client.instruments;

import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessageProvider;
import org.marketcetera.util.log.I18NMessage1P;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * The internationalized messages used by this package.
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
            new I18NMessageProvider("client_instruments",  //$NON-NLS-1$
                    Messages.class.getClassLoader());

    /**
     * The logger.
     */

    static final I18NLoggerProxy LOGGER = new I18NLoggerProxy(PROVIDER);

    static final I18NMessage1P INVALID_OPTION_EXPIRY_FORMAT =
            new I18NMessage1P(LOGGER, "invalid_option_expiry_format");   //$NON-NLS-1$
    static final I18NMessage1P INVALID_OPTION_EXPIRY_WEEK =
            new I18NMessage1P(LOGGER, "invalid_option_expiry_week");   //$NON-NLS-1$
    static final I18NMessage1P INVALID_OPTION_EXPIRY_DAY =
            new I18NMessage1P(LOGGER, "invalid_option_expiry_day");   //$NON-NLS-1$
    static final I18NMessage1P INVALID_FUTURE_EXPIRATION_YEAR_FORMAT = new I18NMessage1P(LOGGER,
           "invalid_future_expiration_year_format");   //$NON-NLS-1$
    static final I18NMessage1P INVALID_CURRENCY_SYMBOL_FORMAT = new I18NMessage1P(LOGGER,
           "invalid_currency_symbol_format");   //$NON-NLS-1$
    static final I18NMessage1P INVALID_CURRENCY_TENOR_FORMAT = new I18NMessage1P(LOGGER,
			"invalid_currency_tenor_format");   //$NON-NLS-1$
    static final I18NMessage1P INVALID_CURRENCY_TENOR_VALUE = new I18NMessage1P(LOGGER,
			"invalid_currency_tenor_value");   //$NON-NLS-1$

}