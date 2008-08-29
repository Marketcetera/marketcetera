package org.marketcetera.photon;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessage1P;
import org.marketcetera.util.log.I18NMessage2P;
import org.marketcetera.util.log.I18NMessage3P;
import org.marketcetera.util.log.I18NMessage4P;
import org.marketcetera.util.log.I18NMessage5P;
import org.marketcetera.util.log.I18NMessageProvider;

/* $License$ */

/**
 * External message constants for Photon.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.6.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public interface Messages
{
    static final I18NMessageProvider PROVIDER = new I18NMessageProvider("photon"); //$NON-NLS-1$

    static final I18NLoggerProxy LOGGER = new I18NLoggerProxy(PROVIDER);

    public static I18NMessage0P ApplicationActionBarAdvisor_WindowMenuName = new I18NMessage0P(LOGGER,
                                                                                               "ApplicationActionBarAdvisor_WindowMenuName"); //$NON-NLS-1$
    public static I18NMessage0P ApplicationActionBarAdvisor_OpenViewMenuItemName = new I18NMessage0P(LOGGER,
                                                                                                     "ApplicationActionBarAdvisor_OpenViewMenuItemName"); //$NON-NLS-1$
    public static I18NMessage0P ApplicationActionBarAdvisor_FileMenuName = new I18NMessage0P(LOGGER,
                                                                                             "ApplicationActionBarAdvisor_FileMenuName"); //$NON-NLS-1$
    public static I18NMessage0P ApplicationActionBarAdvisor_EditMenuName = new I18NMessage0P(LOGGER,
                                                                                             "ApplicationActionBarAdvisor_EditMenuName"); //$NON-NLS-1$
    public static I18NMessage0P ApplicationActionBarAdvisor_ScriptMenuName = new I18NMessage0P(LOGGER,
                                                                                               "ApplicationActionBarAdvisor_ScriptMenuName"); //$NON-NLS-1$
    public static I18NMessage0P ApplicationActionBarAdvisor_ScriptMenuID = new I18NMessage0P(LOGGER,
                                                                                             "ApplicationActionBarAdvisor_ScriptMenuID"); //$NON-NLS-1$
    public static I18NMessage0P ApplicationActionBarAdvisor_NavigationMenuName = new I18NMessage0P(LOGGER,
                                                                                                   "ApplicationActionBarAdvisor_NavigationMenuName"); //$NON-NLS-1$
    public static I18NMessage0P ApplicationActionBarAdvisor_HelpMenuName = new I18NMessage0P(LOGGER,
                                                                                             "ApplicationActionBarAdvisor_HelpMenuName"); //$NON-NLS-1$
    public static I18NMessage0P MainConsole_Name = new I18NMessage0P(LOGGER,
                                                                     "MainConsole_Name"); //$NON-NLS-1$
    public static I18NMessage0P MarketDataConsole_Name = new I18NMessage0P(LOGGER,
                                                                           "MarketDataConsole_Name"); //$NON-NLS-1$
    public static I18NMessage0P ApplicationActionBarAdvisor_OpenPerspectiveMenuName = new I18NMessage0P(LOGGER,
                                                                                                        "ApplicationActionBarAdvisor_OpenPerspectiveMenuName"); //$NON-NLS-1$
    public static I18NMessage0P ApplicationActionBarAdvisor_OpenPerspectiveMenuID = new I18NMessage0P(LOGGER,
                                                                                                      "ApplicationActionBarAdvisor_OpenPerspectiveMenuID"); //$NON-NLS-1$
    public static I18NMessage0P CommandStatusLineContribution_CommandLabel = new I18NMessage0P(LOGGER,
                                                                                               "CommandStatusLineContribution_CommandLabel"); //$NON-NLS-1$
    public static I18NMessage0P CANNOT_START_SCRIPT_ENGINE = new I18NMessage0P(LOGGER,
                                                                               "cannot_start_script_engine"); //$NON-NLS-1$
    public static I18NMessage0P CANNOT_START_DEFAULT_SCRIPT_PROJECT = new I18NMessage0P(LOGGER,
                                                                                        "cannot_start_default_script_project"); //$NON-NLS-1$
    public static I18NMessage1P LOGGER_LEVEL_CHANGED = new I18NMessage1P(LOGGER,
                                                                         "logger_level_changed"); //$NON-NLS-1$
    public static I18NMessage0P CANNOT_LOAD_RUBY = new I18NMessage0P(LOGGER,
                                                                     "cannot_load_ruby"); //$NON-NLS-1$
    public static I18NMessage0P MESSAGE_LABEL = new I18NMessage0P(LOGGER,
                                                                  "message_label"); //$NON-NLS-1$
    public static I18NMessage1P ApplicationWorkbenchWindowAdvisor_ApplicationInitializing = new I18NMessage1P(LOGGER,
                                                                                                              "ApplicationWorkbenchWindowAdvisor_ApplicationInitializing"); //$NON-NLS-1$ 
    public static I18NMessage0P ApplicationWorkbenchWindowAdvisor_OnlineLabel = new I18NMessage0P(LOGGER,
                                                                                                  "ApplicationWorkbenchWindowAdvisor_Online"); //$NON-NLS-1$
    public static I18NMessage0P CANNOT_GET_ID = new I18NMessage0P(LOGGER,
                                                                  "cannot_get_id"); //$NON-NLS-1$
    public static I18NMessage0P RECONNECT_MESSAGE_SERVER = new I18NMessage0P(LOGGER,
                                                                             "reconnect_message_server"); //$NON-NLS-1$
    public static I18NMessage0P CANNOT_DISCONNECT_FROM_MESSAGE_QUEUE = new I18NMessage0P(LOGGER,
                                                                                         "cannot_disconnect_from_message_queue"); //$NON-NLS-1$
    public static I18NMessage0P RECONNECT_QUOTE_FEED = new I18NMessage0P(LOGGER,
                                                                         "reconnect_quote_feed"); //$NON-NLS-1$
    public static I18NMessage0P CANNOT_DISCONNECT_FROM_QUOTE_FEED = new I18NMessage0P(LOGGER,
                                                                                      "cannot_disconnect_from_quote_feed"); //$NON-NLS-1$
    public static I18NMessage1P CANNOT_DECODE_INCOMING_SPECIFIED_MESSAGE = new I18NMessage1P(LOGGER,
                                                                                             "cannot_decode_incoming_specified_message"); //$NON-NLS-1$
    public static I18NMessage0P CANNOT_DECODE_INCOMING_MESSAGE = new I18NMessage0P(LOGGER,
                                                                                   "cannot_decode_incoming_message"); //$NON-NLS-1$
    public static I18NMessage1P UNKNOWN_INTERNAL_MESSAGE_TYPE = new I18NMessage1P(LOGGER,
                                                                                  "unknown_internal_message_type"); //$NON-NLS-1$
    public static I18NMessage1P CANNOT_DECODE_OUTGOING_SPECIFIED_MESSAGE = new I18NMessage1P(LOGGER,
                                                                                             "cannot_decode_outgoing_specified_message"); //$NON-NLS-1$
    public static I18NMessage0P CANNOT_DECODE_OUTGOING_MESSAGE = new I18NMessage0P(LOGGER,
                                                                                   "cannot_decode_outgoing_message"); //$NON-NLS-1$
    public static I18NMessage3P REJECT_MESSAGE = new I18NMessage3P(LOGGER,
                                                                   "reject_message"); //$NON-NLS-1$
    public static I18NMessage5P CANCEL_REJECT_MESSAGE = new I18NMessage5P(LOGGER,
                                                                          "cancel_reject_message"); //$NON-NLS-1$
    public static I18NMessage2P HANDLE_REJECT_MESSAGE = new I18NMessage2P(LOGGER,
                                                                          "handle_reject_message"); //$NON-NLS-1$
    public static I18NMessage0P CANNOT_SEND_MESSAGE_NO_ID = new I18NMessage0P(LOGGER,
                                                                              "cannot_send_message_no_id"); //$NON-NLS-1$
    public static I18NMessage1P CANNOT_SEND_CANCEL = new I18NMessage1P(LOGGER,
                                                                       "cannot_send_cancel"); //$NON-NLS-1$
    public static I18NMessage2P CANNOT_SEND_CANCEL_FOR_REASON = new I18NMessage2P(LOGGER,
                                                                                  "cannot_send_cancel_for_reason"); //$NON-NLS-1$
    public static I18NMessage1P MISSING_SIDE = new I18NMessage1P(LOGGER,
                                                                 "missing_side"); //$NON-NLS-1$
    public static I18NMessage0P CANNOT_SEND_NOT_CONNECTED = new I18NMessage0P(LOGGER,
                                                                              "cannot_send_not_connected"); //$NON-NLS-1$
    public static I18NMessage0P WEB_HELP_ACTION = new I18NMessage0P(LOGGER,
                                                                    "web_help_action"); //$NON-NLS-1$
    public static I18NMessage0P WEB_HELP_ACTION_DESCRIPTION = new I18NMessage0P(LOGGER,
                                                                                "web_help_action_description"); //$NON-NLS-1$
    public static I18NMessage0P SHOW_HEARTBEATS_ACTION = new I18NMessage0P(LOGGER,
                                                                           "show_heartbeats_action"); //$NON-NLS-1$
    public static I18NMessage0P SHOW_HEARTBEATS_ACTION_DESCRIPTION = new I18NMessage0P(LOGGER,
                                                                                       "show_heartbeats_action_description"); //$NON-NLS-1$
    public static I18NMessage0P FOCUS_COMMAND_ACTION = new I18NMessage0P(LOGGER,
                                                                         "focus_command_action"); //$NON-NLS-1$
    public static I18NMessage0P FOCUS_COMMAND_ACTION_DESCRIPTION = new I18NMessage0P(LOGGER,
                                                                                     "focus_command_action_description"); //$NON-NLS-1$
    public static I18NMessage0P START_SCRIPT_REGISTRY = new I18NMessage0P(LOGGER,
                                                                          "start_script_registry"); //$NON-NLS-1$
    public static I18NMessage0P RECONNECT_MARKET_DATA_FEED_ACTION = new I18NMessage0P(LOGGER,
                                                                                      "reconnect_market_data_feed_action"); //$NON-NLS-1$
    public static I18NMessage0P RECONNECT_MARKET_DATA_FEED_ACTION_DESCRIPTION = new I18NMessage0P(LOGGER,
                                                                                                  "reconnect_market_data_feed_action_description"); //$NON-NLS-1$
    public static I18NMessage1P MISSING_DEFAULT_CONSTRUCTOR = new I18NMessage1P(LOGGER,
                                                                                "missing_default_constructor"); //$NON-NLS-1$
    public static I18NMessage1P MISSING_STATIC_METHOD = new I18NMessage1P(LOGGER,
                                                                          "missing_static_method"); //$NON-NLS-1$
    public static I18NMessage1P DATAFEED_SKIPPED = new I18NMessage1P(LOGGER,
                                                                     "datafeed_skipped"); //$NON-NLS-1$
    public static I18NMessage0P CANNOT_CONNECT_TO_MARKETDATA_FEED = new I18NMessage0P(LOGGER,
                                                                                      "cannot_connect_to_marketdata_feed"); //$NON-NLS-1$
    public static I18NMessage0P NULL_TARGET_PAGE = new I18NMessage0P(LOGGER,
                                                                     "null_target_page"); //$NON-NLS-1$
    public static I18NMessage1P VIEW_DOES_NOT_IMPLEMENT = new I18NMessage1P(LOGGER,
                                                                            "view_does_not_implement"); //$NON-NLS-1$
    public static I18NMessage1P FAILED_TO_OPEN_VIEW = new I18NMessage1P(LOGGER,
                                                                        "failed_to_open_view"); //$NON-NLS-1$
    public static I18NMessage0P CANNOT_CANCEL = new I18NMessage0P(LOGGER,
                                                                  "cannot_cancel"); //$NON-NLS-1$
    public static I18NMessage0P CANCEL_ALL_OPEN_ORDERS_ACTION = new I18NMessage0P(LOGGER,
                                                                                  "cancel_all_open_orders_action"); //$NON-NLS-1$
    public static I18NMessage0P CANCEL_ALL_OPEN_ORDERS_ACTION_DESCRIPTION = new I18NMessage0P(LOGGER,
                                                                                              "cancel_all_open_orders_action_description"); //$NON-NLS-1$
    public static I18NMessage0P MISSING_CLORDID = new I18NMessage0P(LOGGER,
                                                                    "missing_clordid"); //$NON-NLS-1$
    public static I18NMessage0P DISCONNECT_MESSAGE_SERVER = new I18NMessage0P(LOGGER,
                                                                              "disconnect_message_server"); //$NON-NLS-1$
    public static I18NMessage0P CANNOT_SHOW_ORS_DIALOG = new I18NMessage0P(LOGGER,
                                                                           "cannot_show_ors_dialog"); //$NON-NLS-1$
    public static I18NMessage0P CANNOT_SHOW_PROGRESS_DIALOG = new I18NMessage0P(LOGGER,
                                                                                "cannot_show_progress_dialog"); //$NON-NLS-1$
    public static I18NMessage0P JMS_CONNECTION_FAILED = new I18NMessage0P(LOGGER,
                                                                          "jms_connection_failed"); //$NON-NLS-1$
    public static I18NMessage1P MESSAGE_QUEUE_CONNECTED = new I18NMessage1P(LOGGER,
                                                                            "message_queue_connected"); //$NON-NLS-1$
    public static I18NMessage0P CANNOT_CONNECT_TO_MESSAGE_QUEUE = new I18NMessage0P(LOGGER,
                                                                                    "cannot_connect_to_message_queue"); //$NON-NLS-1$
    public static I18NMessage0P RECONNECT_JMS_ACTION = new I18NMessage0P(LOGGER,
                                                                         "reconnect_jms_action"); //$NON-NLS-1$
    public static I18NMessage0P RECONNECT_JMS_ACTION_DESCRIPTION = new I18NMessage0P(LOGGER,
                                                                                     "reconnect_jms_action_description"); //$NON-NLS-1$
    public static I18NMessage0P RECONNECT_JMS_JOB_ACTION = new I18NMessage0P(LOGGER,
                                                                             "reconnect_jms_job_action"); //$NON-NLS-1$
    public static I18NMessage0P CHECK_FOR_UPDATES_ACTION = new I18NMessage0P(LOGGER,
                                                                             "check_for_updates_action"); //$NON-NLS-1$
    public static I18NMessage0P CHECK_FOR_UPDATES_ACTION_DESCRIPTION = new I18NMessage0P(LOGGER,
                                                                                         "check_for_updates_action_description"); //$NON-NLS-1$
    public static I18NMessage0P CHECK_FOR_UPDATES_JOB_ACTION = new I18NMessage0P(LOGGER,
                                                                                 "check_for_updates_job_action"); //$NON-NLS-1$
    public static I18NMessage0P OPTION_MARKET_DATA_COMMAND_ACTION = new I18NMessage0P(LOGGER,
                                                                                      "option_market_data_command_action"); //$NON-NLS-1$
    public static I18NMessage0P OPTION_MARKET_DATA_COMMAND_ACTION_DESCRIPTION = new I18NMessage0P(LOGGER,
                                                                                                  "option_market_data_command_action_description"); //$NON-NLS-1$
    public static I18NMessage0P OPTION_MARKET_DATA_SYMBOL_ACTION = new I18NMessage0P(LOGGER,
                                                                                     "option_market_data_symbol_action"); //$NON-NLS-1$
    public static I18NMessage0P OPTION_MARKET_DATA_SYMBOL_ACTION_DESCRIPTION = new I18NMessage0P(LOGGER,
                                                                                                 "option_market_data_symbol_action_description"); //$NON-NLS-1$
    public static I18NMessage0P CANNOT_SEND_ORDER = new I18NMessage0P(LOGGER,
                                                                      "cannot_send_order"); //$NON-NLS-1$
    public static I18NMessage0P CANNOT_CANCEL_ORDER = new I18NMessage0P(LOGGER,
                                                                        "cannot_cancel_order"); //$NON-NLS-1$
    public static I18NMessage1P INVALID_PUT_OR_CALL = new I18NMessage1P(LOGGER,
                                                                        "invalid_put_or_call"); //$NON-NLS-1$
    public static I18NMessage1P NO_MESSAGE_CONVERTER = new I18NMessage1P(LOGGER,
                                                                         "no_message_converter"); //$NON-NLS-1$
    public static I18NMessage0P CANNOT_DETERMINE_RESPONSE_DESTINATION = new I18NMessage0P(LOGGER,
                                                                                          "cannot_determine_response_destination"); //$NON-NLS-1$
    public static I18NMessage0P JMS_EXCEPTION = new I18NMessage0P(LOGGER,
                                                                  "jms_exception"); //$NON-NLS-1$
    public static I18NMessage1P EXPECTED_PRICE = new I18NMessage1P(LOGGER,
                                                                   "expected_price"); //$NON-NLS-1$
    public static I18NMessage0P MISSING_TIME_IN_FORCE = new I18NMessage0P(LOGGER,
                                                                          "missing_time_in_force"); //$NON-NLS-1$
    public static I18NMessage0P OPEN_LABEL = new I18NMessage0P(LOGGER,
                                                               "open_label"); //$NON-NLS-1$
    public static I18NMessage0P CLOSE_LABEL = new I18NMessage0P(LOGGER,
                                                                "close_label"); //$NON-NLS-1$
    public static I18NMessage0P CUSTOMER_LABEL = new I18NMessage0P(LOGGER,
                                                                   "customer_label"); //$NON-NLS-1$
    public static I18NMessage0P BROKER_DEALER_LABEL = new I18NMessage0P(LOGGER,
                                                                        "broker_dealer_label"); //$NON-NLS-1$
    public static I18NMessage0P MARKET_MAKER_LABEL = new I18NMessage0P(LOGGER,
                                                                       "market_maker_label"); //$NON-NLS-1$
    public static I18NMessage0P UNKNOWN_MESSAGE_TYPE = new I18NMessage0P(LOGGER,
                                                                         "unknown_message_type"); //$NON-NLS-1$
    public static I18NMessage0P MISSING_FIELD = new I18NMessage0P(LOGGER,
                                                                  "missing_field"); //$NON-NLS-1$
    public static I18NMessage1P INVALID_PROMPT = new I18NMessage1P(LOGGER,
                                                                   "invalid_prompt"); //$NON-NLS-1$
    public static I18NMessage1P MUST_NOT_BE_BLANK = new I18NMessage1P(LOGGER,
                                                                      "must_not_be_blank"); //$NON-NLS-1$
    public static I18NMessage1P INVALID_URL = new I18NMessage1P(LOGGER,
                                                                "invalid_url"); //$NON-NLS-1$
    public static I18NMessage0P LOG_LEVEL_LABEL = new I18NMessage0P(LOGGER,
                                                                    "log_level_label"); //$NON-NLS-1$
    public static I18NMessage0P LOG_LEVEL_ERROR_LABEL = new I18NMessage0P(LOGGER,
                                                                          "log_level_error_label"); //$NON-NLS-1$
    public static I18NMessage0P LOG_LEVEL_WARN_LABEL = new I18NMessage0P(LOGGER,
                                                                         "log_level_warn_label"); //$NON-NLS-1$
    public static I18NMessage0P LOG_LEVEL_INFO_LABEL = new I18NMessage0P(LOGGER,
                                                                         "log_level_info_label"); //$NON-NLS-1$
    public static I18NMessage0P LOG_LEVEL_DEBUG_LABEL = new I18NMessage0P(LOGGER,
                                                                          "log_level_debug_label"); //$NON-NLS-1$
    public static I18NMessage0P FIX_MESSAGE_DETAIL_PREFERENCE_LABEL = new I18NMessage0P(LOGGER,
                                                                                        "fix_message_detail_preference_label"); //$NON-NLS-1$
    public static I18NMessage0P VIEW_LABEL = new I18NMessage0P(LOGGER,
                                                               "view_label"); //$NON-NLS-1$
    public static I18NMessage0P AVAILABLE_COLUMNS_LABEL = new I18NMessage0P(LOGGER,
                                                                            "available_columns_label"); //$NON-NLS-1$
    public static I18NMessage0P COLUMN_FILTER_LABEL = new I18NMessage0P(LOGGER,
                                                                        "column_filter_label"); //$NON-NLS-1$
    public static I18NMessage0P ACCOUNT_TYPE_LABEL = new I18NMessage0P(LOGGER,
                                                                       "account_type_label"); //$NON-NLS-1$
    public static I18NMessage0P CLEAR_LABEL = new I18NMessage0P(LOGGER,
                                                                "clear_label"); //$NON-NLS-1$
    public static I18NMessage0P CUSTOM_FIX_FIELD_ID_LABEL = new I18NMessage0P(LOGGER,
                                                                              "custom_fix_field_id_label"); //$NON-NLS-1$
    public static I18NMessage1P CUSTOM_FIELD_ID_INVALID = new I18NMessage1P(LOGGER,
                                                                            "custom_field_id_invalid"); //$NON-NLS-1$
    public static I18NMessage1P CUSTOM_FIELD_ID_NEGATIVE = new I18NMessage1P(LOGGER,
                                                                             "custom_field_id_negative"); //$NON-NLS-1$
    public static I18NMessage2P CUSTOM_FIELD_CONFLICT = new I18NMessage2P(LOGGER,
                                                                          "custom_field_conflict"); //$NON-NLS-1$
    public static I18NMessage0P AVERAGE_PRICE_LABEL = new I18NMessage0P(LOGGER,
                                                                        "average_price_label"); //$NON-NLS-1$
    public static I18NMessage0P FILLS_LABEL = new I18NMessage0P(LOGGER,
                                                                "fills_label"); //$NON-NLS-1$
    public static I18NMessage0P FIX_MESSAGES_LABEL = new I18NMessage0P(LOGGER,
                                                                       "fix_messages_label"); //$NON-NLS-1$
    public static I18NMessage0P OPEN_ORDERS_LABEL = new I18NMessage0P(LOGGER,
                                                                      "open_orders_label"); //$NON-NLS-1$
    public static I18NMessage0P FIELD_LABEL = new I18NMessage0P(LOGGER,
                                                                "field_label"); //$NON-NLS-1$
    public static I18NMessage0P MULTI_SELECT_NOT_ENABLED = new I18NMessage0P(LOGGER,
                                                                             "multi_select_not_enabled"); //$NON-NLS-1$
    public static I18NMessage0P RUBY_SCRIPT_SELECTION_LABEL = new I18NMessage0P(LOGGER,
                                                                                "ruby_script_selection_label"); //$NON-NLS-1$
    public static I18NMessage0P RUBY_SCRIPT_SELECTION_DESCRIPTION = new I18NMessage0P(LOGGER,
                                                                                      "ruby_script_selection_description"); //$NON-NLS-1$
    public static I18NMessage0P RUBY_TITLE_LABEL = new I18NMessage0P(LOGGER,
                                                                     "ruby_title_label"); //$NON-NLS-1$
    public static I18NMessage0P RUBY_SCRIPT_LABEL = new I18NMessage0P(LOGGER,
                                                                      "ruby_script_label"); //$NON-NLS-1$
    public static I18NMessage0P BROWSE_LABEL = new I18NMessage0P(LOGGER,
                                                                 "browse_label"); //$NON-NLS-1$
    public static I18NMessage0P CUSTOM_FIELDS_LABEL = new I18NMessage0P(LOGGER,
                                                                        "custom_fields_label"); //$NON-NLS-1$
    public static I18NMessage0P KEY_LABEL = new I18NMessage0P(LOGGER,
                                                              "key_label"); //$NON-NLS-1$
    public static I18NMessage0P VALUE_LABEL = new I18NMessage0P(LOGGER,
                                                                "value_label"); //$NON-NLS-1$
    public static I18NMessage0P NEW_CUSTOM_FIELD_LABEL = new I18NMessage0P(LOGGER,
                                                                           "new_custom_field_label"); //$NON-NLS-1$
    public static I18NMessage0P INVALID_CUSTOM_FIELD_KEY = new I18NMessage0P(LOGGER,
                                                                             "invalid_custom_field_key"); //$NON-NLS-1$
    public static I18NMessage1P CUSTOM_FIELD_INVALID_DIGIT = new I18NMessage1P(LOGGER,
                                                                               "custom_field_invalid_digit"); //$NON-NLS-1$
    public static I18NMessage0P SCRIPT_REGISTRY_LABEL = new I18NMessage0P(LOGGER,
                                                                          "script_registry_label"); //$NON-NLS-1$
    public static I18NMessage0P FIX_VERSION_LABEL = new I18NMessage0P(LOGGER,
                                                                      "fix_version_label"); //$NON-NLS-1$
    public static I18NMessage0P FIX_VERSION_TOOLTIP = new I18NMessage0P(LOGGER,
                                                                        "fix_version_tooltip"); //$NON-NLS-1$
    public static I18NMessage0P JMS_SERVER_URL_LABEL = new I18NMessage0P(LOGGER,
                                                                         "jms_server_url_label"); //$NON-NLS-1$
    public static I18NMessage0P INCOMING_TOPIC_LABEL = new I18NMessage0P(LOGGER,
                                                                         "incoming_topic_label"); //$NON-NLS-1$
    public static I18NMessage0P OUTGOING_QUEUE_LABEL = new I18NMessage0P(LOGGER,
                                                                         "outgoing_queue_label"); //$NON-NLS-1$
    public static I18NMessage0P WEB_APP_HOST_LABEL = new I18NMessage0P(LOGGER,
                                                                       "web_app_host_label"); //$NON-NLS-1$
    public static I18NMessage0P WEB_APP_PORT_LABEL = new I18NMessage0P(LOGGER,
                                                                       "web_app_port_label"); //$NON-NLS-1$
    public static I18NMessage0P MARKET_DATA_FEED_LABEL = new I18NMessage0P(LOGGER,
                                                                           "market_data_feed_label"); //$NON-NLS-1$
    public static I18NMessage0P ORDER_ID_PREFIX_LABEL = new I18NMessage0P(LOGGER,
                                                                          "order_id_prefix_label"); //$NON-NLS-1$
    public static I18NMessage0P UNKNOWN_VALIDATION_STRATEGY = new I18NMessage0P(LOGGER,
                                                                                "unknown_validation_strategy"); //$NON-NLS-1$
    public static I18NMessage1P SCRIPT_REGISTRY_ERROR = new I18NMessage1P(LOGGER,
                                                                          "script_registry_error"); //$NON-NLS-1$
    public static I18NMessage1P UNREGISTERING_SCRIPT = new I18NMessage1P(LOGGER,
                                                                         "unregistering_script"); //$NON-NLS-1$
    public static I18NMessage1P CANNOT_SAVE_SCRIPT_REGISTRY_PREFERENCES = new I18NMessage1P(LOGGER,
                                                                                            "cannot_save_script_registry_preferences"); //$NON-NLS-1$
    public static I18NMessage0P CANNOT_PROCESS_RESOURCE_CHANGE = new I18NMessage0P(LOGGER,
                                                                                   "cannot_process_resource_change"); //$NON-NLS-1$
    public static I18NMessage1P REGISTRY_DISCARDED_MESSAGE = new I18NMessage1P(LOGGER,
                                                                               "registry_discarded_message"); //$NON-NLS-1$
    public static I18NMessage1P UNABLE_TO_REGISTER_SCRIPT = new I18NMessage1P(LOGGER,
                                                                              "unable_to_register_script"); //$NON-NLS-1$
    public static I18NMessage1P CALLBACK_FUNCTION_ERROR = new I18NMessage1P(LOGGER,
                                                                            "callback_function_error"); //$NON-NLS-1$
    public static I18NMessage0P CANNOT_GET_SCRIPT_CHANGE_RESULT = new I18NMessage0P(LOGGER,
                                                                                    "cannot_get_script_change_result"); //$NON-NLS-1$
    public static I18NMessage1P NO_STRATEGY_SUBCLASS = new I18NMessage1P(LOGGER,
                                                                         "no_strategy_subclass"); //$NON-NLS-1$
    public static I18NMessage1P PARSE_EXCEPTION = new I18NMessage1P(LOGGER,
                                                                    "parse_exception"); //$NON-NLS-1$
    public static I18NMessage0P CHOOSE_COLUMNS_LABEL = new I18NMessage0P(LOGGER,
                                                                         "choose_columns_label"); //$NON-NLS-1$
    public static I18NMessage0P MORE_COLUMNS_LABEL = new I18NMessage0P(LOGGER,
                                                                       "more_columns_label"); //$NON-NLS-1$
    public static I18NMessage0P CANNOT_PARSE_LEVEL_TWO_DATA = new I18NMessage0P(LOGGER,
                                                                                "cannot_parse_level_two_data"); //$NON-NLS-1$
    public static I18NMessage0P CANNOT_DISPOSE_OF_ARROWS = new I18NMessage0P(LOGGER,
                                                                             "cannot_dispose_of_arrows"); //$NON-NLS-1$
    public static I18NMessage1P CANNOT_REMOVE_NONEXISTANT_LISTENER = new I18NMessage1P(LOGGER,
                                                                                       "cannot_remove_nonexistant_listener"); //$NON-NLS-1$
    public static I18NMessage0P PASSWORD_LABEL = new I18NMessage0P(LOGGER,
                                                                   "password_label"); //$NON-NLS-1$
    public static I18NMessage0P ORS_LOGIN_LABEL = new I18NMessage0P(LOGGER,
                                                                    "ors_login_label"); //$NON-NLS-1$
    public static I18NMessage0P ACCOUNT_DETAILS_LABEL = new I18NMessage0P(LOGGER,
                                                                          "account_details_label"); //$NON-NLS-1$
    public static I18NMessage0P MENU_USER_ID_LABEL = new I18NMessage0P(LOGGER,
                                                                       "menu_user_id_label"); //$NON-NLS-1$
    public static I18NMessage0P MENU_PASSWORD_LABEL = new I18NMessage0P(LOGGER,
                                                                        "menu_password_label"); //$NON-NLS-1$
    public static I18NMessage0P MENU_CLEAR_LABEL = new I18NMessage0P(LOGGER,
                                                                     "menu_clear_label"); //$NON-NLS-1$
    public static I18NMessage0P MENU_LOGIN_LABEL = new I18NMessage0P(LOGGER,
                                                                     "menu_login_label"); //$NON-NLS-1$
    public static I18NMessage0P INVALID_USER_ID = new I18NMessage0P(LOGGER,
                                                                   "invalid_user_id"); //$NON-NLS-1$
    public static I18NMessage0P USER_ID_MUST_NOT_BE_BLANK = new I18NMessage0P(LOGGER,
                                                                              "user_id_must_not_be_blank"); //$NON-NLS-1$
    public static I18NMessage1P ORS_LOGIN_HELP_URL = new I18NMessage1P(LOGGER,
                                                                       "ors_login_help_url"); //$NON-NLS-1$
    public static I18NMessage0P INPUT_REQUIRED = new I18NMessage0P(LOGGER,
                                                                   "input_required"); //$NON-NLS-1$
    public static I18NMessage0P VALUE_REQUIRED = new I18NMessage0P(LOGGER,
                                                                   "value_required"); //$NON-NLS-1$
    public static I18NMessage0P ARGUMENT_MUST_BE_STRING = new I18NMessage0P(LOGGER,
                                                                            "argument_must_be_string"); //$NON-NLS-1$
    public static I18NMessage0P INTEGER_REQUIRED = new I18NMessage0P(LOGGER,
                                                                     "integer_required"); //$NON-NLS-1$
    public static I18NMessage0P MAY_NOT_BE_VALID_FOR_FIX_VERSION = new I18NMessage0P(LOGGER,
                                                                                     "may_not_be_valid_for_fix_version"); //$NON-NLS-1$
    public static I18NMessage0P DECIMAL_REQUIRED = new I18NMessage0P(LOGGER,
                                                                     "decimal_required"); //$NON-NLS-1$
    public static I18NMessage0P INVALID_VALUE = new I18NMessage0P(LOGGER,
                                                                  "invalid_value"); //$NON-NLS-1$
    public static I18NMessage1P INVALID_SPECIFIED_VALUE = new I18NMessage1P(LOGGER,
                                                                            "invalid_specified_value"); //$NON-NLS-1$
    public static I18NMessage2P INVALID_SPECIFIED_DATE = new I18NMessage2P(LOGGER,
                                                                           "invalid_specified_date"); //$NON-NLS-1$
    public static I18NMessage0P SET_SYMBOL_LABEL = new I18NMessage0P(LOGGER,
                                                                     "set_symbol_label"); //$NON-NLS-1$
    public static I18NMessage0P SET_SYMBOL_TOOTLTIPS = new I18NMessage0P(LOGGER,
                                                                         "set_symbol_tooltips"); //$NON-NLS-1$
    public static I18NMessage0P FAILED_TO_DISPOSE_MARKET_DATA_WIDGET = new I18NMessage0P(LOGGER,
                                                                                         "failed_to_dispose_market_data_widget"); //$NON-NLS-1$
    public static I18NMessage0P CANNOT_PARSE_OPTION_INFO = new I18NMessage0P(LOGGER,
                                                                             "cannot_parse_option_info"); //$NON-NLS-1$
    public static I18NMessage1P CANNOT_GET_OPTION_CONTRACT_INFO = new I18NMessage1P(LOGGER,
                                                                                    "cannot_get_option_contract_info"); //$NON-NLS-1$
    public static I18NMessage0P COPY_LABEL = new I18NMessage0P(LOGGER,
                                                               "copy_label"); //$NON-NLS-1$
    public static I18NMessage1P CANNOT_SUBSCRIBE_TO_MARKET_DATA = new I18NMessage1P(LOGGER,
                                                                                    "cannot_subscribe_to_market_data"); //$NON-NLS-1$
    public static I18NMessage0P MISSING_QUOTE_FEED = new I18NMessage0P(LOGGER,
                                                                       "missing_quote_feed"); //$NON-NLS-1$
    public static I18NMessage0P OPTIONS_LABEL = new I18NMessage0P(LOGGER,
                                                                  "options_label"); //$NON-NLS-1$
    public static I18NMessage0P SKIPPING_MARKET_DATA = new I18NMessage0P(LOGGER,
                                                                         "skipping_market_data"); //$NON-NLS-1$
    public static I18NMessage2P CANNOT_GET_OPTION_CONTRACT_INFO_SPECIFIED = new I18NMessage2P(LOGGER,
                                                                                              "cannot_get_option_contract_info_specified"); //$NON-NLS-1$
    public static I18NMessage0P MESSAGE_NOT_DERIVATIVE_SECURITY_LIST = new I18NMessage0P(LOGGER,
                                                                                         "message_not_derivative_security_list"); //$NON-NLS-1$
    public static I18NMessage1P CANNOT_PARSE_OPTION_INFO_SPECIFIED = new I18NMessage1P(LOGGER,
                                                                                       "cannot_parse_option_info_specified"); //$NON-NLS-1$
    public static I18NMessage0P NEW_OPTION_LABEL = new I18NMessage0P(LOGGER,
                                                                     "new_option_label"); //$NON-NLS-1$
    public static I18NMessage0P REPLACE_OPTION_LABEL = new I18NMessage0P(LOGGER,
                                                                         "replace_option_label"); //$NON-NLS-1$
    public static I18NMessage0P VALUE_NOT_FOUND = new I18NMessage0P(LOGGER,
                                                                    "value_not_found"); //$NON-NLS-1$
    public static I18NMessage0P CANNOT_UNSUBSCRIBE = new I18NMessage0P(LOGGER,
                                                                       "cannot_unsubscribe"); //$NON-NLS-1$
    public static I18NMessage1P CANNOT_READ_CUSTOM_FIELD = new I18NMessage1P(LOGGER,
                                                                             "cannot_read_custom_field"); //$NON-NLS-1$
    public static I18NMessage0P INVALID_TIME_IN_FORCE = new I18NMessage0P(LOGGER,
                                                                          "invalid_time_in_force"); //$NON-NLS-1$
    public static I18NMessage0P CANNOT_BIND_TO_TICKET = new I18NMessage0P(LOGGER,
                                                                          "cannot_bind_to_ticket"); //$NON-NLS-1$
    public static I18NMessage1P CANNOT_SEND_ORDER_SPECIFIED = new I18NMessage1P(LOGGER,
                                                                                "cannot_send_order_specified"); //$NON-NLS-1$
    public static I18NMessage0P NEW_EQUITY_LABEL = new I18NMessage0P(LOGGER,
                                                                     "new_equity_label"); //$NON-NLS-1$
    public static I18NMessage0P REPLACE_EQUITY_LABEL = new I18NMessage0P(LOGGER,
                                                                         "replace_equity_label"); //$NON-NLS-1$
    public static I18NMessage0P TIME_LABEL = new I18NMessage0P(LOGGER,
                                                               "time_label"); //$NON-NLS-1$
    public static I18NMessage0P TRADE_LABEL = new I18NMessage0P(LOGGER,
                                                                "trade_label"); //$NON-NLS-1$
    public static I18NMessage0P SCORE_LABEL = new I18NMessage0P(LOGGER,
                                                                "score_label"); //$NON-NLS-1$
    public static I18NMessage0P DELETE_ITEMS_LABEL = new I18NMessage0P(LOGGER,
                                                                       "delete_items_label"); //$NON-NLS-1$
    public static I18NMessage0P DELETE_ITEMS_TOOLTIPS = new I18NMessage0P(LOGGER,
                                                                          "delete_items_tooltips"); //$NON-NLS-1$
    public static I18NMessage0P DELETE_ALL_ITEMS_LABEL = new I18NMessage0P(LOGGER,
                                                                           "delete_all_items_label"); //$NON-NLS-1$
    public static I18NMessage0P DELETE_ALL_ITEMS_TOOLTIPS = new I18NMessage0P(LOGGER,
                                                                              "delete_all_items_tooltips"); //$NON-NLS-1$
    public static I18NMessage0P SEND_ITEMS_LABEL = new I18NMessage0P(LOGGER,
                                                                     "send_items_label"); //$NON-NLS-1$
    public static I18NMessage0P SEND_ITEMS_TOOLTIPS = new I18NMessage0P(LOGGER,
                                                                        "send_items_tooltips"); //$NON-NLS-1$
    public static I18NMessage0P CANNOT_FORMAT_TRADE_RECOMMENDATION = new I18NMessage0P(LOGGER,
                                                                                       "cannot_format_trade_recommendation"); //$NON-NLS-1$
    public static I18NMessage1P INVALID_MONTH_NAME = new I18NMessage1P(LOGGER,
                                                                       "invalid_month_name"); //$NON-NLS-1$
    public static I18NMessage0P READY_LABEL = new I18NMessage0P(LOGGER,
                                                                "ready_label"); //$NON-NLS-1$
    public static I18NMessage1P CANNOT_CONNECT_TO_URL = new I18NMessage1P(LOGGER,
                                                                          "cannot_connect_to_url"); //$NON-NLS-1$
    public static I18NMessage0P SHOW_SYMBOL_NEW_VIEW_LABEL = new I18NMessage0P(LOGGER,
                                                                               "show_symbol_new_view_label"); //$NON-NLS-1$
    public static I18NMessage0P SHOW_SYMBOL_NEW_VIEW_TOOLTIPS = new I18NMessage0P(LOGGER,
                                                                                  "show_symbol_new_view_tooltips"); //$NON-NLS-1$
    public static I18NMessage1P NULL_VIEW = new I18NMessage1P(LOGGER,
                                                              "null_view"); //$NON-NLS-1$
    public static I18NMessage0P NULL_TARGET = new I18NMessage0P(LOGGER,
                                                                "null_target"); //$NON-NLS-1$
    public static I18NMessage4P VIEW_DOES_NOT_ACCEPT_SYMBOLS = new I18NMessage4P(LOGGER,
                                                                                 "view_does_not_accept_symbols"); //$NON-NLS-1$
    public static I18NMessage1P DUPLICATE_SYMBOL = new I18NMessage1P(LOGGER,
                                                                     "duplicate_symbol"); //$NON-NLS-1$
    public static I18NMessage1P CANNOT_UNSUBSCRIBE_NO_RECORD = new I18NMessage1P(LOGGER,
                                                                                 "cannot_unsubscribe_no_record"); //$NON-NLS-1$
    public static I18NMessage0P ENABLED_LABEL = new I18NMessage0P(LOGGER,
                                                                  "enabled_label"); //$NON-NLS-1$
    public static I18NMessage1P CANNOT_FIND_CUSTOM_FIELD = new I18NMessage1P(LOGGER,
                                                                             "cannot_find_custom_field"); //$NON-NLS-1$
    public static I18NMessage0P SHOW_SYMBOL_LABEL = new I18NMessage0P(LOGGER,
                                                                      "show_symbol_label"); //$NON-NLS-1$
    public static I18NMessage0P SHOW_SYMBOL_TOOLTIPS = new I18NMessage0P(LOGGER,
                                                                         "show_symbol_tooltips"); //$NON-NLS-1$
    public static I18NMessage0P FULL_MESSAGE_LABEL = new I18NMessage0P(LOGGER,
                                                                       "full_message_label"); //$NON-NLS-1$
    public static I18NMessage0P COPY_MESSAGE_LABEL = new I18NMessage0P(LOGGER,
                                                                       "copy_message_label"); //$NON-NLS-1$
    public static I18NMessage0P COPY_MESSAGE_TOOLTIPS = new I18NMessage0P(LOGGER,
                                                                          "copy_message_tooltips"); //$NON-NLS-1$
    public static I18NMessage0P COPY_TABLE_LABEL = new I18NMessage0P(LOGGER,
                                                                     "copy_table_label"); //$NON-NLS-1$
    public static I18NMessage0P COPY_TABLE_TOOLTIPS = new I18NMessage0P(LOGGER,
                                                                        "copy_table_tooltips"); //$NON-NLS-1$
    public static I18NMessage0P TAG_LABEL = new I18NMessage0P(LOGGER,
                                                              "tag_label"); //$NON-NLS-1$
    public static I18NMessage0P VALUE_NAME_LABEL = new I18NMessage0P(LOGGER,
                                                                     "value_name_label"); //$NON-NLS-1$
    public static I18NMessage0P REQUIRED_LABEL = new I18NMessage0P(LOGGER,
                                                                   "required_label"); //$NON-NLS-1$
    public static I18NMessage0P LISTENER_EXECUTION_FAILED = new I18NMessage0P(LOGGER,
                                                                              "listener_execution_failed"); //$NON-NLS-1$
    public static I18NMessage0P BID_SZ_LABEL = new I18NMessage0P(LOGGER,
                                                                 "bid_sz_label"); //$NON-NLS-1$
    public static I18NMessage0P BID_LABEL = new I18NMessage0P(LOGGER,
                                                              "bid_label"); //$NON-NLS-1$
    public static I18NMessage0P ASK_LABEL = new I18NMessage0P(LOGGER,
                                                              "ask_label"); //$NON-NLS-1$
    public static I18NMessage0P ASK_SZ_LABEL = new I18NMessage0P(LOGGER,
                                                                 "ask_sz_label"); //$NON-NLS-1$
    public static I18NMessage1P OPEN_NEW_LABEL = new I18NMessage1P(LOGGER,
                                                                   "open_new_label"); //$NON-NLS-1$
    public static I18NMessage1P OPEN_NEW_TOOLTIPS = new I18NMessage1P(LOGGER,
                                                                      "open_new_tooltips"); //$NON-NLS-1$
    public static I18NMessage1P CANNOT_OPEN_VIEW = new I18NMessage1P(LOGGER,
                                                                     "cannot_open_view"); //$NON-NLS-1$
    public static I18NMessage0P AVERAGE_PRICE_VIEW_LABEL = new I18NMessage0P(LOGGER,
                                                                             "average_price_view_label"); //$NON-NLS-1$
    public static I18NMessage0P OPEN_ORDERS_VIEW_LABEL = new I18NMessage0P(LOGGER,
                                                                           "open_orders_view_label"); //$NON-NLS-1$
    public static I18NMessage0P FIX_MESSAGES_VIEW_LABEL = new I18NMessage0P(LOGGER,
                                                                            "fix_messages_view_label"); //$NON-NLS-1$
    public static I18NMessage0P FILLS_VIEW_LABEL = new I18NMessage0P(LOGGER,
                                                                     "fills_view_label"); //$NON-NLS-1$
    public static I18NMessage0P MATCHER_FAILED = new I18NMessage0P(LOGGER,
                                                                   "matcher_failed"); //$NON-NLS-1$
    public static I18NMessage1P UNRECOGNIZED_FIELD = new I18NMessage1P(LOGGER,
                                                                       "unrecognized_field"); //$NON-NLS-1$
}
