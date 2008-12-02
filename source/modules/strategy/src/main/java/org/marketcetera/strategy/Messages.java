package org.marketcetera.strategy;

import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessage1P;
import org.marketcetera.util.log.I18NMessage2P;
import org.marketcetera.util.log.I18NMessage3P;
import org.marketcetera.util.log.I18NMessage5P;
import org.marketcetera.util.log.I18NMessage6P;
import org.marketcetera.util.log.I18NMessageProvider;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Message keys for the strategy module.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface Messages
{
    /**
     * The message provider.
     */
    static final I18NMessageProvider PROVIDER = new I18NMessageProvider("strategy");  //$NON-NLS-1$
    /**
     * The logger.
     */
    static final I18NLoggerProxy LOGGER = new I18NLoggerProxy(PROVIDER);
    static final I18NMessage0P PROVIDER_DESCRIPTION = new I18NMessage0P(LOGGER,
                                                                        "provider_description"); //$NON-NLS-1$
    static final I18NMessage0P PARAMETER_COUNT_ERROR = new I18NMessage0P(LOGGER,
                                                                         "parameter_count_error"); //$NON-NLS-1$
    static final I18NMessage3P PARAMETER_TYPE_ERROR = new I18NMessage3P(LOGGER,
                                                                        "parameter_type_error"); //$NON-NLS-1$
    static final I18NMessage2P NULL_PARAMETER_ERROR = new I18NMessage2P(LOGGER,
                                                                        "null_parameter_error"); //$NON-NLS-1$
    static final I18NMessage1P INVALID_LANGUAGE_ERROR = new I18NMessage1P(LOGGER,
                                                                          "invalid_language_error"); //$NON-NLS-1$
    static final I18NMessage0P EMPTY_NAME_ERROR = new I18NMessage0P(LOGGER,
                                                                    "empty_name_error"); //$NON-NLS-1$
    static final I18NMessage1P FILE_DOES_NOT_EXIST_OR_IS_NOT_READABLE = new I18NMessage1P(LOGGER,
                                                                                          "file_does_not_exist_or_is_not_readable"); //$NON-NLS-1$
    static final I18NMessage0P FAILED_TO_START = new I18NMessage0P(LOGGER,
                                                                   "failed_to_start"); //$NON-NLS-1$
    static final I18NMessage0P NO_STRATEGY_CLASS = new I18NMessage0P(LOGGER,
                                                                     "no_strategy_class"); //$NON-NLS-1$
    static final I18NMessage1P NO_SUPPORT_FOR_LANGUAGE = new I18NMessage1P(LOGGER,
                                                                           "no_support_for_language"); //$NON-NLS-1$
    static final I18NMessage2P MARKET_DATA_REQUEST_FAILED = new I18NMessage2P(LOGGER,
                                                                              "market_data_request_failed"); //$NON-NLS-1$
    static final I18NMessage2P CEP_REQUEST_FAILED = new I18NMessage2P(LOGGER,
                                                                      "cep_request_failed"); //$NON-NLS-1$
    static final I18NMessage0P NULL_PROPERTY_KEY = new I18NMessage0P(LOGGER,
                                                                     "null_property_key"); //$NON-NLS-1$
    static final I18NMessage1P NO_PARAMETERS = new I18NMessage1P(LOGGER,
                                                                 "no_parameters"); //$NON-NLS-1$
    static final I18NMessage3P INVALID_MARKET_DATA_REQUEST = new I18NMessage3P(LOGGER,
                                                                               "invalid_market_data_request"); //$NON-NLS-1$
    static final I18NMessage1P INVALID_TRADE_SUGGESTION = new I18NMessage1P(LOGGER,
                                                                            "invalid_trade_suggestion"); //$NON-NLS-1$
    static final I18NMessage1P CALLBACK_ERROR = new I18NMessage1P(LOGGER,
                                                                  "callback_error"); //$NON-NLS-1$
    static final I18NMessage1P INVALID_MESSAGE = new I18NMessage1P(LOGGER,
                                                                   "invalid_message"); //$NON-NLS-1$
    static final I18NMessage1P COMPILATION_FAILED = new I18NMessage1P(LOGGER,
                                                                      "compilation_failed"); //$NON-NLS-1$
    static final I18NMessage1P INVALID_STRATEGY_NAME = new I18NMessage1P(LOGGER,
                                                                         "invalid_strategy_name"); //$NON-NLS-1$
    static final I18NMessage1P INVALID_ORDER = new I18NMessage1P(LOGGER,
                                                                "invalid_order"); //$NON-NLS-1$
    static final I18NMessage1P INVALID_CANCEL = new I18NMessage1P(LOGGER,
                                                                  "invalid_cancel"); //$NON-NLS-1$
    static final I18NMessage2P INVALID_ORDERID = new I18NMessage2P(LOGGER,
                                                                   "invalid_orderid"); //$NON-NLS-1$
    static final I18NMessage2P ORDER_CANCEL_FAILED = new I18NMessage2P(LOGGER,
                                                                       "order_cancel_failed"); //$NON-NLS-1$
    static final I18NMessage1P INVALID_REPLACEMENT_ORDER = new I18NMessage1P(LOGGER,
                                                                             "invalid_replacement_order"); //$NON-NLS-1$
    static final I18NMessage1P CANNOT_RETRIEVE_DESTINATIONS = new I18NMessage1P(LOGGER,
                                                                                "cannot_retrieve_destinations"); //$NON-NLS-1$
    static final I18NMessage3P INVALID_POSITION_REQUEST = new I18NMessage3P(LOGGER,
                                                                            "invalid_position_request"); //$NON-NLS-1$
    static final I18NMessage3P CANNOT_RETRIEVE_POSITION = new I18NMessage3P(LOGGER,
                                                                            "cannot_retrieve_position"); //$NON-NLS-1$
    static final I18NMessage0P CANNOT_INITIALIZE_CLIENT = new I18NMessage0P(LOGGER,
                                                                            "cannot_initialize_client"); //$NON-NLS-1$
    static final I18NMessage2P EXECUTION_REPORT_REQUEST_FAILED = new I18NMessage2P(LOGGER,
                                                                                   "execution_report_request_failed"); //$NON-NLS-1$
    static final I18NMessage3P INVALID_CEP_REQUEST = new I18NMessage3P(LOGGER,
                                                                       "invalid_cep_request"); //$NON-NLS-1$
    static final I18NMessage2P UNABLE_TO_CANCEL_MARKET_DATA_REQUEST = new I18NMessage2P(LOGGER,
                                                                                        "unable_to_cancel_market_data_request"); //$NON-NLS-1$
    static final I18NMessage2P NO_MARKET_DATA_HANDLE = new I18NMessage2P(LOGGER,
                                                                         "no_market_data_handle"); //$NON-NLS-1$
    static final I18NMessage2P UNABLE_TO_CANCEL_CEP_REQUEST = new I18NMessage2P(LOGGER,
                                                                                "unable_to_cancel_cep_request"); //$NON-NLS-1$
    static final I18NMessage2P NO_CEP_HANDLE = new I18NMessage2P(LOGGER,
                                                                 "no_cep_handle"); //$NON-NLS-1$
    static final I18NMessage3P SEND_MESSAGE_FAILED = new I18NMessage3P(LOGGER,
                                                                       "send_message_failed"); //$NON-NLS-1$
    static final I18NMessage1P INVALID_EVENT = new I18NMessage1P(LOGGER,
                                                                 "invalid_event"); //$NON-NLS-1$
    static final I18NMessage3P CANNOT_SEND_EVENT_TO_CEP = new I18NMessage3P(LOGGER,
                                                                            "cannot_send_event_to_cep"); //$NON-NLS-1$
    static final I18NMessage1P STOP_ERROR = new I18NMessage1P(LOGGER,
                                                              "stop_error"); //$NON-NLS-1$
    static final I18NMessage2P CANNOT_CREATE_CONNECTION = new I18NMessage2P(LOGGER,
                                                                            "cannot_create_connection"); //$NON-NLS-1$
    static final I18NMessage3P INVALID_EVENT_TO_CEP = new I18NMessage3P(LOGGER,
                                                                        "invalid_event_to_cep"); //$NON-NLS-1$
    static final I18NMessage6P INVALID_COMBINED_DATA_REQUEST = new I18NMessage6P(LOGGER,
                                                                                 "invalid_combined_data_request"); //$NON-NLS-1$
    static final I18NMessage5P COMBINED_DATA_REQUEST_FAILED = new I18NMessage5P(LOGGER,
                                                                                "combined_data_request_failed"); //$NON-NLS-1$
}
