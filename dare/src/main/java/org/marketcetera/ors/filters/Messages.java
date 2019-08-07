package org.marketcetera.ors.filters;

import org.marketcetera.util.log.*;
import org.marketcetera.util.misc.ClassVersion;

/**
 * The internationalization constants used by this package.
 *
 * @author tlerios@marketcetera.com
 * @since 1.0.0
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
        new I18NMessageProvider("ors_filters"); //$NON-NLS-1$

    /**
     * The logger.
     */

    static final I18NLoggerProxy LOGGER=
        new I18NLoggerProxy(PROVIDER);

    /*
     * The messages.
     */

    static final I18NMessage0P NO_SYMBOL= 
        new I18NMessage0P(LOGGER,"no_symbol"); //$NON-NLS-1$
    static final I18NMessage0P NO_ORDER_TYPE= 
        new I18NMessage0P(LOGGER,"no_order_type"); //$NON-NLS-1$
    static final I18NMessage0P NO_PRICE= 
        new I18NMessage0P(LOGGER,"no_price"); //$NON-NLS-1$
    static final I18NMessage0P NO_QUANTITY= 
        new I18NMessage0P(LOGGER,"no_quantity"); //$NON-NLS-1$

    static final I18NMessage1P MARKET_NOT_ALLOWED = 
        new I18NMessage1P(LOGGER,"market_not_allowed"); //$NON-NLS-1$
    static final I18NMessage3P MAX_QTY= 
        new I18NMessage3P(LOGGER,"max_qty"); //$NON-NLS-1$
    static final I18NMessage3P MAX_NOTIONAL= 
        new I18NMessage3P(LOGGER,"max_notional"); //$NON-NLS-1$
    static final I18NMessage3P MIN_PRICE= 
        new I18NMessage3P(LOGGER,"min_price"); //$NON-NLS-1$
    static final I18NMessage3P MAX_PRICE= 
        new I18NMessage3P(LOGGER,"max_price"); //$NON-NLS-1$
    static final I18NMessage2P ASSET_CLASS_RESTRICTED = new I18NMessage2P(LOGGER,"asset_class_restricted"); //$NON-NLS-1$
    static final I18NMessage1P UNKNOWN_ASSET_CLASS = new I18NMessage1P(LOGGER,"unknown_asset_class"); //$NON-NLS-1$
    static final I18NMessage0P NO_SESSION_USERNAME = new I18NMessage0P(LOGGER,"no_session_username"); //$NON-NLS-1$
    static final I18NMessage1P NO_BROKER_USERNAME = new I18NMessage1P(LOGGER,"no_broker_username"); //$NON-NLS-1$
    static final I18NMessage0P NON_CONFORMING_FIELD_SPECIFICATION = new I18NMessage0P(LOGGER,
                                                                                      "non_conforming_field_specification"); //$NON-NLS-1$
    // LEGACY CODE.

    static final I18NMessage1P ERROR_UNRECOGNIZED_ROUTE = 
        new I18NMessage1P(LOGGER,"error_unrecognized_route"); //$NON-NLS-1$
    static final I18NMessage1P ORDER_MODIFIER_WRONG_FIELD_FORMAT = 
        new I18NMessage1P(LOGGER,"order_modifier_wrong_field_format"); //$NON-NLS-1$
}
