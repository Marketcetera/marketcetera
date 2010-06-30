package org.marketcetera.event;

import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessage1P;
import org.marketcetera.util.log.I18NMessage2P;
import org.marketcetera.util.log.I18NMessage4P;
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
@ClassVersion("$Id$")
public interface Messages
{
    /**
     * The message provider.
     */

    static final I18NMessageProvider PROVIDER = 
        new I18NMessageProvider("event"); //$NON-NLS-1$

    /**
     * The logger.
     */

    static final I18NLoggerProxy LOGGER = 
        new I18NLoggerProxy(PROVIDER);

    /*
     * The messages.
     */

    static final I18NMessage0P ERROR_MSG_NOT_EXEC_REPORT = 
        new I18NMessage0P(LOGGER,"error_msg_not_exec_report"); //$NON-NLS-1$
    static final I18NMessage4P INVALID_EVENT_TIMESTAMP = new I18NMessage4P(LOGGER,
                                                                           "invalid_event_timestamp"); //$NON-NLS-1$
    static final I18NMessage0P VALIDATION_NULL_TIMESTAMP = new I18NMessage0P(LOGGER,
                                                                             "validation_null_timestamp"); //$NON-NLS-1$
    static final I18NMessage1P VALIDATION_INVALID_MESSAGEID = new I18NMessage1P(LOGGER,
                                                                                "validation_invalid_messageid"); //$NON-NLS-1$
    static final I18NMessage0P VALIDATION_NULL_EQUITY = new I18NMessage0P(LOGGER,
                                                                          "validation_null_equity"); //$NON-NLS-1$
    static final I18NMessage0P VALIDATION_NULL_AMOUNT = new I18NMessage0P(LOGGER,
                                                                          "validation_null_amount"); //$NON-NLS-1$
    static final I18NMessage0P VALIDATION_NULL_CURRENCY = new I18NMessage0P(LOGGER,
                                                                            "validation_null_currency"); //$NON-NLS-1$
    static final I18NMessage0P VALIDATION_NULL_DECLARE_DATE = new I18NMessage0P(LOGGER,
                                                                                "validation_null_declare_date"); //$NON-NLS-1$
    static final I18NMessage0P VALIDATION_NULL_EXECUTION_DATE = new I18NMessage0P(LOGGER,
                                                                                  "validation_null_execution_date"); //$NON-NLS-1$
    static final I18NMessage0P VALIDATION_NULL_PAYMENT_DATE = new I18NMessage0P(LOGGER,
                                                                                "validation_null_payment_date"); //$NON-NLS-1$
    static final I18NMessage0P VALIDATION_NULL_RECORD_DATE = new I18NMessage0P(LOGGER,
                                                                               "validation_null_record_date"); //$NON-NLS-1$
    static final I18NMessage0P VALIDATION_NULL_FREQUENCY = new I18NMessage0P(LOGGER,
                                                                             "validation_null_frequency"); //$NON-NLS-1$
    static final I18NMessage0P VALIDATION_NULL_STATUS = new I18NMessage0P(LOGGER,
                                                                          "validation_null_status"); //$NON-NLS-1$
    static final I18NMessage0P VALIDATION_NULL_TYPE = new I18NMessage0P(LOGGER,
                                                                        "validation_null_type"); //$NON-NLS-1$
    static final I18NMessage0P VALIDATION_NULL_INSTRUMENT = new I18NMessage0P(LOGGER,
                                                                              "validation_null_instrument"); //$NON-NLS-1$
    static final I18NMessage0P VALIDATION_NULL_PRICE = new I18NMessage0P(LOGGER,
                                                                         "validation_null_price"); //$NON-NLS-1$
    static final I18NMessage0P VALIDATION_NULL_SIZE = new I18NMessage0P(LOGGER,
                                                                        "validation_null_size"); //$NON-NLS-1$
    static final I18NMessage0P VALIDATION_NULL_EXCHANGE = new I18NMessage0P(LOGGER,
                                                                            "validation_null_exchange"); //$NON-NLS-1$
    static final I18NMessage0P VALIDATION_NULL_EXCHANGE_TIMESTAMP = new I18NMessage0P(LOGGER,
                                                                                      "validation_null_exchange_timestamp"); //$NON-NLS-1$
    static final I18NMessage0P VALIDATION_NULL_UNDERLYING_INSTRUMENT = new I18NMessage0P(LOGGER,
                                                                                         "validation_null_underlying_instrument"); //$NON-NLS-1$
    static final I18NMessage0P VALIDATION_NULL_EXPIRATION_TYPE = new I18NMessage0P(LOGGER,
                                                                                   "validation_null_expiration_type"); //$NON-NLS-1$
    static final I18NMessage0P VALIDATION_NULL_QUOTE_ACTION = new I18NMessage0P(LOGGER,
                                                                                "validation_null_quote_action"); //$NON-NLS-1$
    static final I18NMessage0P VALIDATION_NULL_LOG_LEVEL = new I18NMessage0P(LOGGER,
                                                                             "validation_null_log_level"); //$NON-NLS-1$
    static final I18NMessage2P VALIDATION_BID_INCORRECT_INSTRUMENT = new I18NMessage2P(LOGGER,
                                                                                       "validation_bid_incorrect_instrument"); //$NON-NLS-1$
    static final I18NMessage2P VALIDATION_ASK_INCORRECT_INSTRUMENT = new I18NMessage2P(LOGGER,
                                                                                       "validation_ask_incorrect_instrument"); //$NON-NLS-1$
    static final I18NMessage1P VALIDATION_LIST_CONTAINS_NULL = new I18NMessage1P(LOGGER,
                                                                                 "validation_list_contains_null"); //$NON-NLS-1$
    static final I18NMessage2P VALIDATION_LIST_INCORRECT_INSTRUMENT = new I18NMessage2P(LOGGER,
                                                                                        "validation_list_incorrect_instrument"); //$NON-NLS-1$
    static final I18NMessage0P VALIDATION_EQUITY_REQUIRED = new I18NMessage0P(LOGGER,
                                                                              "validation_equity_required"); //$NON-NLS-1$
    static final I18NMessage0P VALIDATION_OPTION_REQUIRED = new I18NMessage0P(LOGGER,
                                                                              "validation_option_required"); //$NON-NLS-1$
    static final I18NMessage0P VALIDATION_FUTURE_REQUIRED = new I18NMessage0P(LOGGER,
                                                                              "validation_future_required"); //$NON-NLS-1$
}
