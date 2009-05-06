package org.marketcetera.strategy;

import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessage1P;
import org.marketcetera.util.log.I18NMessage2P;
import org.marketcetera.util.log.I18NMessage3P;
import org.marketcetera.util.log.I18NMessage4P;
import org.marketcetera.util.log.I18NMessage5P;
import org.marketcetera.util.log.I18NMessageProvider;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Message keys for the strategy module.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public interface Messages
{
    /**
     * The message provider.
     */
    static final I18NMessageProvider PROVIDER = new I18NMessageProvider("strategy", Messages.class.getClassLoader());  //$NON-NLS-1$
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
    static final I18NMessage0P EMPTY_INSTANCE_ERROR = new I18NMessage0P(LOGGER,
                                                                        "empty_instance_error"); //$NON-NLS-1$
    static final I18NMessage1P FILE_DOES_NOT_EXIST_OR_IS_NOT_READABLE = new I18NMessage1P(LOGGER,
                                                                                          "file_does_not_exist_or_is_not_readable"); //$NON-NLS-1$
    static final I18NMessage0P FAILED_TO_START = new I18NMessage0P(LOGGER,
                                                                   "failed_to_start"); //$NON-NLS-1$
    static final I18NMessage0P NO_STRATEGY_CLASS = new I18NMessage0P(LOGGER,
                                                                     "no_strategy_class"); //$NON-NLS-1$
    static final I18NMessage1P NO_SUPPORT_FOR_LANGUAGE = new I18NMessage1P(LOGGER,
                                                                           "no_support_for_language"); //$NON-NLS-1$
    static final I18NMessage1P MARKET_DATA_REQUEST_FAILED = new I18NMessage1P(LOGGER,
                                                                              "market_data_request_failed"); //$NON-NLS-1$
    static final I18NMessage2P CEP_REQUEST_FAILED = new I18NMessage2P(LOGGER,
                                                                      "cep_request_failed"); //$NON-NLS-1$
    static final I18NMessage0P NULL_PROPERTY_KEY = new I18NMessage0P(LOGGER,
                                                                     "null_property_key"); //$NON-NLS-1$
    static final I18NMessage1P NO_PARAMETERS = new I18NMessage1P(LOGGER,
                                                                 "no_parameters"); //$NON-NLS-1$
    static final I18NMessage2P INVALID_MARKET_DATA_REQUEST = new I18NMessage2P(LOGGER,
                                                                               "invalid_market_data_request"); //$NON-NLS-1$
    static final I18NMessage1P INVALID_TRADE_SUGGESTION = new I18NMessage1P(LOGGER,
                                                                            "invalid_trade_suggestion"); //$NON-NLS-1$
    static final I18NMessage1P INVALID_NOTIFICATION = new I18NMessage1P(LOGGER,
                                                                        "invalid_notification"); //$NON-NLS-1$
    static final I18NMessage1P INVALID_LOG = new I18NMessage1P(LOGGER,
                                                               "invalid_log"); //$NON-NLS-1$
    static final I18NMessage2P CALLBACK_ERROR = new I18NMessage2P(LOGGER,
                                                                  "callback_error"); //$NON-NLS-1$
    static final I18NMessage1P INVALID_MESSAGE = new I18NMessage1P(LOGGER,
                                                                   "invalid_message"); //$NON-NLS-1$
    static final I18NMessage2P COMPILATION_FAILED = new I18NMessage2P(LOGGER,
                                                                      "compilation_failed"); //$NON-NLS-1$
    static final I18NMessage1P INVALID_STRATEGY_NAME = new I18NMessage1P(LOGGER,
                                                                         "invalid_strategy_name"); //$NON-NLS-1$
    static final I18NMessage1P INVALID_ORDER = new I18NMessage1P(LOGGER,
                                                                "invalid_order"); //$NON-NLS-1$
    static final I18NMessage1P ORDER_VALIDATION_FAILED = new I18NMessage1P(LOGGER,
                                                                           "order_validation_failed"); //$NON-NLS-1$
    static final I18NMessage1P INVALID_CANCEL = new I18NMessage1P(LOGGER,
                                                                  "invalid_cancel"); //$NON-NLS-1$
    static final I18NMessage2P INVALID_ORDERID = new I18NMessage2P(LOGGER,
                                                                   "invalid_orderid"); //$NON-NLS-1$
    static final I18NMessage2P ORDER_CANCEL_FAILED = new I18NMessage2P(LOGGER,
                                                                       "order_cancel_failed"); //$NON-NLS-1$
    static final I18NMessage1P INVALID_REPLACEMENT_ORDER = new I18NMessage1P(LOGGER,
                                                                             "invalid_replacement_order"); //$NON-NLS-1$
    static final I18NMessage1P CANNOT_RETRIEVE_BROKERS = new I18NMessage1P(LOGGER,
                                                                                "cannot_retrieve_brokers"); //$NON-NLS-1$
    static final I18NMessage3P INVALID_POSITION_REQUEST = new I18NMessage3P(LOGGER,
                                                                            "invalid_position_request"); //$NON-NLS-1$
    static final I18NMessage3P CANNOT_RETRIEVE_POSITION = new I18NMessage3P(LOGGER,
                                                                            "cannot_retrieve_position"); //$NON-NLS-1$
    static final I18NMessage0P CANNOT_INITIALIZE_CLIENT = new I18NMessage0P(LOGGER,
                                                                            "cannot_initialize_client"); //$NON-NLS-1$
    static final I18NMessage2P EXECUTION_REPORT_REQUEST_FAILED = new I18NMessage2P(LOGGER,
                                                                                   "execution_report_request_failed"); //$NON-NLS-1$
    static final I18NMessage4P INVALID_CEP_REQUEST = new I18NMessage4P(LOGGER,
                                                                       "invalid_cep_request"); //$NON-NLS-1$
    static final I18NMessage2P UNABLE_TO_CANCEL_DATA_REQUEST = new I18NMessage2P(LOGGER,
                                                                                 "unable_to_cancel_data_request"); //$NON-NLS-1$
    static final I18NMessage2P NO_DATA_HANDLE = new I18NMessage2P(LOGGER,
                                                                  "no_data_handle"); //$NON-NLS-1$
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
    static final I18NMessage5P INVALID_COMBINED_DATA_REQUEST = new I18NMessage5P(LOGGER,
                                                                                 "invalid_combined_data_request"); //$NON-NLS-1$
    static final I18NMessage4P COMBINED_DATA_REQUEST_FAILED = new I18NMessage4P(LOGGER,
                                                                                "combined_data_request_failed"); //$NON-NLS-1$
    static final I18NMessage1P STRATEGY_COMPILATION_NULL_RESULT = new I18NMessage1P(LOGGER,
                                                                                    "strategy_compilation_null_result"); //$NON-NLS-1$
    static final I18NMessage3P RUNTIME_ERROR = new I18NMessage3P(LOGGER,
                                                                 "runtime_error"); //$NON-NLS-1$
    static final I18NMessage2P CANNOT_REQUEST_DATA = new I18NMessage2P(LOGGER,
                                                                       "cannot_request_data"); //$NON-NLS-1$
    static final I18NMessage2P CANNOT_SEND_DATA = new I18NMessage2P(LOGGER,
                                                                       "cannot_send_data"); //$NON-NLS-1$
    static final I18NMessage3P INVALID_STATUS_TO_RECEIVE_DATA = new I18NMessage3P(LOGGER,
                                                                                  "invalid_status_to_receive_data"); //$NON-NLS-1$
    static final I18NMessage3P CANNOT_CHANGE_STATE = new I18NMessage3P(LOGGER,
                                                                       "cannot_change_state"); //$NON-NLS-1$
    static final I18NMessage2P STRATEGY_STILL_RUNNING = new I18NMessage2P(LOGGER,
                                                                          "strategy_still_running"); //$NON-NLS-1$
    static final I18NMessage1P INTERRUPT_START_ERROR = new I18NMessage1P(LOGGER,
                                                                         "interrupt_start_error"); //$NON-NLS-1$
    static final I18NMessage2P CANNOT_STOP = new I18NMessage2P(LOGGER,
                                                               "cannot_stop"); //$NON-NLS-1$
    static final I18NMessage1P ERROR_WAITING_FOR_STOP = new I18NMessage1P(LOGGER,
                                                                          "error_waiting_for_stop"); //$NON-NLS-1$
    static final I18NMessage1P INTERRUPT_STOP_ERROR = new I18NMessage1P(LOGGER,
                                                                        "interrupt_stop_error"); //$NON-NLS-1$
    static final I18NMessage0P BEAN_ATTRIBUTE_CHANGED = new I18NMessage0P(LOGGER,
                                                                        "bean_attribute_changed"); //$NON-NLS-1$
    static final I18NMessage0P STATUS_CHANGED = new I18NMessage0P(LOGGER,
                                                                  "status_changed"); //$NON-NLS-1$
    static final I18NMessage1P CANCELING_START_JOB = new I18NMessage1P(LOGGER,
                                                                       "canceling_start_job"); //$NON-NLS-1$
    static final I18NMessage1P CANCELING_STOP_JOB = new I18NMessage1P(LOGGER,
                                                                      "canceling_stop_job"); //$NON-NLS-1$
    static final I18NMessage4P INTERRUPT_COMPLETE = new I18NMessage4P(LOGGER,
                                                                      "interrupt_complete"); //$NON-NLS-1$
    static final I18NMessage2P EXECUTING_CALLBACK = new I18NMessage2P(LOGGER,
                                                                      "executing_callback"); //$NON-NLS-1$
    static final I18NMessage1P CANCELING_ALL_DATA_REQUESTS = new I18NMessage1P(LOGGER,
                                                                               "canceling_all_data_requests"); //$NON-NLS-1$
    static final I18NMessage1P SUBMITTING_CANCEL_ALL_ORDERS_REQUEST = new I18NMessage1P(LOGGER,
                                                                                        "submitting_cancel_all_orders_request"); //$NON-NLS-1$
    static final I18NMessage2P SUBMITTING_CANCEL_ORDER_REQUEST = new I18NMessage2P(LOGGER,
                                                                                   "submitting_cancel_order_request"); //$NON-NLS-1$
    static final I18NMessage2P SUBMITTING_CANCEL_REPLACE_REQUEST = new I18NMessage2P(LOGGER,
                                                                                     "submitting_cancel_replace_request"); //$NON-NLS-1$
    static final I18NMessage2P CANCEL_REQUEST_SUBMITTED = new I18NMessage2P(LOGGER,
                                                                            "cancel_request_submitted"); //$NON-NLS-1$
    static final I18NMessage2P CANCELING_DATA_REQUEST = new I18NMessage2P(LOGGER,
                                                                          "canceling_data_request"); //$NON-NLS-1$
    static final I18NMessage2P RECEIVED_BROKERS = new I18NMessage2P(LOGGER,
                                                                    "received_brokers"); //$NON-NLS-1$
    static final I18NMessage4P RECEIVED_POSITION = new I18NMessage4P(LOGGER,
                                                                     "received_position"); //$NON-NLS-1$
    static final I18NMessage4P SUBMITTING_CEP_REQUEST = new I18NMessage4P(LOGGER,
                                                                          "submitting_cep_request"); //$NON-NLS-1$
    static final I18NMessage2P SUBMITTING_MARKET_DATA_REQUEST = new I18NMessage2P(LOGGER,
                                                                                  "submitting_market_data_request"); //$NON-NLS-1$
    static final I18NMessage5P SUBMITTING_PROCESSED_MARKET_DATA_REQUEST = new I18NMessage5P(LOGGER,
                                                                                            "submitting_processed_market_data_request"); //$NON-NLS-1$
    static final I18NMessage3P EXECUTION_REPORTS_FOUND = new I18NMessage3P(LOGGER,
                                                                           "execution_reports_found"); //$NON-NLS-1$
    static final I18NMessage2P USING_EXECUTION_REPORT = new I18NMessage2P(LOGGER,
                                                                          "using_execution_report"); //$NON-NLS-1$
    static final I18NMessage1P NO_EXECUTION_REPORT = new I18NMessage1P(LOGGER,
                                                                       "no_execution_report"); //$NON-NLS-1$
    static final I18NMessage4P SUBMITTING_EVENT_TO_CEP = new I18NMessage4P(LOGGER,
                                                                           "submitting_event_to_cep"); //$NON-NLS-1$
    static final I18NMessage3P SUBMITTING_FIX_MESSAGE = new I18NMessage3P(LOGGER,
                                                                          "submitting_fix_message"); //$NON-NLS-1$
    static final I18NMessage3P SUBMITTING_ORDER = new I18NMessage3P(LOGGER,
                                                                    "submitting_order"); //$NON-NLS-1$
    static final I18NMessage2P SUBMITTING_TRADE_SUGGESTION = new I18NMessage2P(LOGGER,
                                                                               "submitting_trade_suggestion"); //$NON-NLS-1$
    static final I18NMessage1P MESSAGE_1P = new I18NMessage1P(LOGGER,
                                                              "message_1p"); //$NON-NLS-1$
    static final I18NMessage2P COMPILATION_FAILED_DIAGNOSTIC = new I18NMessage2P(LOGGER,
                                                                                 "compilation_failed_diagnostic"); //$NON-NLS-1$
    static final I18NMessage1P ERROR_CONVERTING_CLASSPATH_URL =
            new I18NMessage1P(LOGGER, "error_converting_classpath_url");   //$NON-NLS-1$
}
