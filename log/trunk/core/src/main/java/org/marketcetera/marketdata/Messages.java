package org.marketcetera.marketdata;

import org.marketcetera.util.log.*;
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
        new I18NMessageProvider("marketdata"); //$NON-NLS-1$

    /**
     * The logger.
     */

    static final I18NLoggerProxy LOGGER = 
        new I18NLoggerProxy(PROVIDER);

    /*
     * The messages.
     */

    static final I18NMessage0P ERROR_MARKET_DATA_FEED_CANNOT_FIND_SYMBOL = 
        new I18NMessage0P(LOGGER,"error_market_data_feed_cannot_find_symbol"); //$NON-NLS-1$
    static final I18NMessage0P ERROR_MARKET_DATA_FEED_EXECUTION_FAILED = 
        new I18NMessage0P(LOGGER,"error_market_data_feed_execution_failed"); //$NON-NLS-1$
    static final I18NMessage0P ERROR_MARKET_DATA_FEED_UNKNOWN_MESSAGE_TYPE = 
        new I18NMessage0P(LOGGER,"error_market_data_feed_unknown_message_type"); //$NON-NLS-1$
    static final I18NMessage1P ERROR_TOKEN_RESUBMIT_FAILED = 
        new I18NMessage1P(LOGGER,"error_token_resubmit_failed"); //$NON-NLS-1$

    static final I18NMessage1P WARNING_MARKET_DATA_FEED_DATA_IGNORED = 
        new I18NMessage1P(LOGGER,"warning_market_data_feed_data_ignored"); //$NON-NLS-1$
    static final I18NMessage1P WARNING_MARKET_DATA_FEED_CANNOT_CANCEL_SUBSCRIPTION = 
        new I18NMessage1P(LOGGER,"warning_market_data_feed_cannot_cancel_subscription"); //$NON-NLS-1$
    static final I18NMessage0P WARNING_MARKET_DATA_FEED_NOT_RETURN_HANDLE = 
        new I18NMessage0P(LOGGER,"warning_market_data_feed_not_return_handle"); //$NON-NLS-1$
    static final I18NMessage0P WARNING_MARKET_DATA_FEED_CANNOT_INITIALIZE = 
        new I18NMessage0P(LOGGER,"warning_market_data_feed_cannot_initialize"); //$NON-NLS-1$
    static final I18NMessage0P WARNING_MARKET_DATA_FEED_CANNOT_EXEC_COMMAND = 
        new I18NMessage0P(LOGGER,"warning_market_data_feed_cannot_exec_command"); //$NON-NLS-1$
    static final I18NMessage0P WARNING_MARKET_DATA_FEED_CANNOT_DETERMINE_SUBSCRIPTION = 
        new I18NMessage0P(LOGGER,"warning_market_data_feed_cannot_determine_subscription"); //$NON-NLS-1$

    static final I18NMessage1P MARKET_DATA_FEED_CANNOT_EXEC_REQUESTS = 
        new I18NMessage1P(LOGGER,"market_data_feed_cannot_exec_requests"); //$NON-NLS-1$
    static final I18NMessage0P ORDER_BOOK_DEPTH_MUST_BE_POSITIVE = new I18NMessage0P(LOGGER,
                                                                                     "order_book_depth_must_be_positive"); //$NON-NLS-1$
    static final I18NMessage1P CANNOT_CONVERT_EVENT_TO_ENTRY_TYPE = new I18NMessage1P(LOGGER,
                                                                                      "cannot_convert_event_to_entry_type"); //$NON-NLS-1$
    static final I18NMessage2P INSTRUMENT_DOES_NOT_MATCH_ORDER_BOOK_INSTRUMENT = new I18NMessage2P(LOGGER,
                                                                                           "symbol_does_not_match_order_book_symbol"); //$NON-NLS-1$
    static final I18NMessage1P INVALID_SYMBOLS = new I18NMessage1P(LOGGER,
                                                                   "invalid_symbols"); //$NON-NLS-1$
    static final I18NMessage1P INVALID_UNDERLYING_SYMBOLS = new I18NMessage1P(LOGGER,
                                                                              "invalid_underlying_symbols"); //$NON-NLS-1$
    static final I18NMessage0P MISSING_SYMBOLS = new I18NMessage0P(LOGGER,
                                                                   "missing_symbols"); //$NON-NLS-1$
    static final I18NMessage0P MISSING_UNDERLYING_SYMBOLS = new I18NMessage0P(LOGGER,
                                                                              "missing_underlying_symbols"); //$NON-NLS-1$
    static final I18NMessage1P BOTH_SYMBOLS_AND_UNDERLYING_SYMBOLS_SPECIFIED = new I18NMessage1P(LOGGER,
                                                                                                 "both_symbols_and_underlying_symbols_specified"); //$NON-NLS-1$
    static final I18NMessage1P NEITHER_SYMBOLS_NOR_UNDERLYING_SYMBOLS_SPECIFIED = new I18NMessage1P(LOGGER,
                                                                                                    "neither_symbols_nor_underlying_symbols_specified"); //$NON-NLS-1$
    static final I18NMessage2P VALID_UNDERLYING_ASSET_CLASS_REQUIRED = new I18NMessage2P(LOGGER,
                                                                                         "valid_underlying_asset_class_required"); //$NON-NLS-1$
    static final I18NMessage1P DIVIDEND_REQUIRES_SYMBOLS = new I18NMessage1P(LOGGER,
                                                                             "dividend_requires_symbols"); //$NON-NLS-1$
    static final I18NMessage1P EQUITY_REQUIRES_SYMBOLS = new I18NMessage1P(LOGGER,
                                                                           "equity_requires_symbols"); //$NON-NLS-1$
    static final I18NMessage1P INVALID_TYPE = new I18NMessage1P(LOGGER,
                                                                "invalid_type"); //$NON-NLS-1$
    static final I18NMessage0P MISSING_TYPE = new I18NMessage0P(LOGGER,
                                                                "missing_type"); //$NON-NLS-1$
    static final I18NMessage0P BEAN_ATTRIBUTE_CHANGED = new I18NMessage0P(LOGGER,
                                                                          "bean_attribute_changed"); //$NON-NLS-1$
    static final I18NMessage0P FEED_STATUS_CHANGED = new I18NMessage0P(LOGGER,
                                                                       "feed_status_changed"); //$NON-NLS-1$
    static final I18NMessage0P NULL_URL = new I18NMessage0P(LOGGER,
                                                           "null_url"); //$NON-NLS-1$
    static final I18NMessage0P MISSING_CONTENT = new I18NMessage0P(LOGGER,
                                                                  "missing_content"); //$NON-NLS-1$
    static final I18NMessage0P MISSING_ASSET_CLASS = new I18NMessage0P(LOGGER,
                                                                       "missing_asset_class"); //$NON-NLS-1$
    static final I18NMessage0P INVALID_REQUEST = new I18NMessage0P(LOGGER,
                                                                   "invalid_request"); //$NON-NLS-1$
    static final I18NMessage1P INVALID_CONTENT = new I18NMessage1P(LOGGER,
                                                                   "invalid_content"); //$NON-NLS-1$
    static final I18NMessage1P INVALID_ASSET_CLASS = new I18NMessage1P(LOGGER,
                                                                       "invalid_asset_class"); //$NON-NLS-1$
    static final I18NMessage1P INVALID_DATE = new I18NMessage1P(LOGGER,
                                                                "invalid_date"); //$NON-NLS-1$
    static final I18NMessage1P UNSUPPORTED_REQUEST = new I18NMessage1P(LOGGER,
                                                                       "unsupported_request"); //$NON-NLS-1$
    static final I18NMessage1P SIMULATED_EXCHANGE_TICK_ERROR = new I18NMessage1P(LOGGER,
                                                                                 "simulated_exchange_tick_error"); //$NON-NLS-1$
    static final I18NMessage1P SIMULATED_EXCHANGE_PUBLICATION_ERROR = new I18NMessage1P(LOGGER,
                                                                                        "simulated_exchange_publication_error"); //$NON-NLS-1$
    static final I18NMessage1P SIMULATED_EXCHANGE_OUT_OF_EVENTS = new I18NMessage1P(LOGGER,
                                                                                    "simulated_exchange_out_of_events"); //$NON-NLS-1$
    static final I18NMessage1P STARTING_SCRIPTED_EXCHANGE = new I18NMessage1P(LOGGER,
                                                                              "starting_scripted_exchange"); //$NON-NLS-1$
    static final I18NMessage1P STARTING_RANDOM_EXCHANGE = new I18NMessage1P(LOGGER,
                                                                            "starting_random_exchange"); //$NON-NLS-1$
    static final I18NMessage1P STOPPING_SIMULATED_EXCHANGE = new I18NMessage1P(LOGGER,
                                                                               "stopping_simulated_exchange"); //$NON-NLS-1$
    static final I18NMessage4P SIMULATED_EXCHANGE_CODE_MISMATCH = new I18NMessage4P(LOGGER,
                                                                                    "simulated_exchange_code_mismatch");  //$NON-NLS-1$
    static final I18NMessage2P SIMULATED_EXCHANGE_SKIPPED_EVENT = new I18NMessage2P(LOGGER,
                                                                                    "simulated_exchange_skipped_event"); //$NON-NLS-1$
    static final I18NMessage1P SCHEME_REQUIRED = new I18NMessage1P(LOGGER,
                                                                   "missing_scheme"); //$NON-NLS-1$
    static final I18NMessage1P INVALID_HOSTNAME = new I18NMessage1P(LOGGER,
                                                                    "invalid_hostname"); //$NON-NLS-1$
    static final I18NMessage0P PORT_REQUIRED = new I18NMessage0P(LOGGER,
                                                                 "port_required"); //$NON-NLS-1$
    static final I18NMessage1P INVALID_URI = new I18NMessage1P(LOGGER,
                                                               "invalid_uri"); //$NON-NLS-1$
    static final I18NMessage1P INVALID_SCHEME_NAME = new I18NMessage1P(LOGGER,
                                                                       "invalid_scheme_name"); //$NON-NLS-1$
    static final I18NMessage0P INSTRUMENT_OR_UNDERLYING_INSTRUMENT_REQUIRED = new I18NMessage0P(LOGGER,
                                                                                                "instrument_or_underlying_instrument_required"); //$NON-NLS-1$
    static final I18NMessage1P OPTION_REQUIRES_UNDERLYING_INSTRUMENT = new I18NMessage1P(LOGGER,
                                                                                         "option_requires_underlying_instrument"); //$NON-NLS-1$
    static final I18NMessage1P DIVIDEND_REQUEST_MISSING_INSTRUMENT = new I18NMessage1P(LOGGER,
                                                                                       "dividend_request_missing_instrument"); //$NON-NLS-1$
    static final I18NMessage3P UNSUPPORTED_ASSET_CLASS = new I18NMessage3P(LOGGER,
                                                                           "unsupported_asset_class"); //$NON-NLS-1$
}
