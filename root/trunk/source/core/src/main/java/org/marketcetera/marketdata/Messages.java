package org.marketcetera.marketdata;

import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessage1P;
import org.marketcetera.util.log.I18NMessage2P;
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
    static final I18NMessage0P ERROR_MARKET_DATA_FEED_CANNOT_GENERATE_MESSAGE = 
        new I18NMessage0P(LOGGER,"error_market_data_feed_cannot_generate_message"); //$NON-NLS-1$
    static final I18NMessage0P ERROR_MARKET_DATA_FEED_EXECUTION_FAILED = 
        new I18NMessage0P(LOGGER,"error_market_data_feed_execution_failed"); //$NON-NLS-1$
    static final I18NMessage0P ERROR_MARKET_DATA_FEED_UNKNOWN_MESSAGE_TYPE = 
        new I18NMessage0P(LOGGER,"error_market_data_feed_unknown_message_type"); //$NON-NLS-1$
    static final I18NMessage0P ERROR_NO_ID_FOR_TOKEN = 
        new I18NMessage0P(LOGGER,"error_no_id_for_token"); //$NON-NLS-1$
    static final I18NMessage1P ERROR_TOKEN_RESUBMIT_FAILED = 
        new I18NMessage1P(LOGGER,"error_token_resubmit_failed"); //$NON-NLS-1$

    static final I18NMessage1P WARNING_MARKET_DATA_FEED_DATA_IGNORED = 
        new I18NMessage1P(LOGGER,"warning_market_data_feed_data_ignored"); //$NON-NLS-1$
    static final I18NMessage1P WARNING_MARKET_DATA_FEED_CANNOT_CANCEL_SUBSCRIPTION = 
        new I18NMessage1P(LOGGER,"warning_market_data_feed_cannot_cancel_subscription"); //$NON-NLS-1$
    static final I18NMessage0P WARNING_MARKET_DATA_FEED_NOT_RETURN_HANDLE = 
        new I18NMessage0P(LOGGER,"warning_market_data_feed_not_return_handle"); //$NON-NLS-1$
    static final I18NMessage0P WARNING_MARKET_DATA_FEED_CANNOT_LOGIN = 
        new I18NMessage0P(LOGGER,"warning_market_data_feed_cannot_login"); //$NON-NLS-1$
    static final I18NMessage0P WARNING_MARKET_DATA_FEED_CANNOT_INITIALIZE = 
        new I18NMessage0P(LOGGER,"warning_market_data_feed_cannot_initialize"); //$NON-NLS-1$
    static final I18NMessage0P WARNING_MARKET_DATA_FEED_CANNOT_EXEC_COMMAND = 
        new I18NMessage0P(LOGGER,"warning_market_data_feed_cannot_exec_command"); //$NON-NLS-1$
    static final I18NMessage0P WARNING_MARKET_DATA_FEED_CANNOT_DETERMINE_SUBSCRIPTION = 
        new I18NMessage0P(LOGGER,"warning_market_data_feed_cannot_determine_subscription"); //$NON-NLS-1$

    static final I18NMessage1P MARKET_DATA_FEED_CANNOT_EXEC_REQUESTS = 
        new I18NMessage1P(LOGGER,"market_data_feed_cannot_exec_requests"); //$NON-NLS-1$
    static final I18NMessage1P CANNOT_RETRIEVE_STORED_EVENT_INFORMATION = new I18NMessage1P(LOGGER,
                                                                                            "cannot_retrieve_stored_event_information"); //$NON-NLS-1$
    static final I18NMessage0P ORDER_BOOK_DEPTH_MUST_BE_POSITIVE = new I18NMessage0P(LOGGER,
                                                                                     "order_book_depth_must_be_positive"); //$NON-NLS-1$
    static final I18NMessage1P CANNOT_CONVERT_EVENT_TO_ENTRY_TYPE = new I18NMessage1P(LOGGER,
                                                                                      "cannot_convert_event_to_entry_type"); //$NON-NLS-1$
    static final I18NMessage2P SYMBOL_DOES_NOT_MATCH_ORDER_BOOK_SYMBOL = new I18NMessage2P(LOGGER,
                                                                                           "symbol_does_not_match_order_book_symbol"); //$NON-NLS-1$
}
