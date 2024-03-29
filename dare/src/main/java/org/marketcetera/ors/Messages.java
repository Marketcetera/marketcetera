package org.marketcetera.ors;

import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessage1P;
import org.marketcetera.util.log.I18NMessage2P;
import org.marketcetera.util.log.I18NMessage3P;
import org.marketcetera.util.log.I18NMessage4P;
import org.marketcetera.util.log.I18NMessageProvider;
import org.marketcetera.util.misc.ClassVersion;

/**
 * The internationalization constants used by this package.
 *
 * @author klim@marketcetera.com
 * @since 0.6.0
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
        new I18NMessageProvider("ors");  //$NON-NLS-1$

    /**
     * The logger.
     */

    static final I18NLoggerProxy LOGGER= 
        new I18NLoggerProxy(PROVIDER);

    /*
     * The messages.
     */

    static final I18NMessage0P APP_COPYRIGHT=
        new I18NMessage0P(LOGGER,"app_copyright"); //$NON-NLS-1$
    static final I18NMessage2P APP_VERSION_BUILD=
            new I18NMessage2P(LOGGER,"app_version_build");   //$NON-NLS-1$

    static final I18NMessage0P APP_START=
        new I18NMessage0P(LOGGER,"app_start"); //$NON-NLS-1$
    static final I18NMessage0P APP_STARTED=
        new I18NMessage0P(LOGGER,"app_started"); //$NON-NLS-1$
    static final I18NMessage0P APP_STOP=
        new I18NMessage0P(LOGGER,"app_stop"); //$NON-NLS-1$
    static final I18NMessage0P APP_STOP_SUCCESS=
        new I18NMessage0P(LOGGER,"app_stop_success"); //$NON-NLS-1$
    static final I18NMessage0P APP_STOP_ERROR=
        new I18NMessage0P(LOGGER,"app_stop_error"); //$NON-NLS-1$
    static final I18NMessage1P APP_USAGE=
        new I18NMessage1P(LOGGER,"app_usage"); //$NON-NLS-1$
    static final I18NMessage0P APP_AUTH_OPTIONS=
        new I18NMessage0P(LOGGER,"app_auth_options"); //$NON-NLS-1$
    static final I18NMessage0P APP_MISSING_CREDENTIALS=
        new I18NMessage0P(LOGGER,"app_missing_credentials"); //$NON-NLS-1$
    static final I18NMessage0P APP_NO_ARGS_ALLOWED=
        new I18NMessage0P(LOGGER,"app_no_args_allowed"); //$NON-NLS-1$
    static final I18NMessage0P APP_NO_CONFIGURATION=
        new I18NMessage0P(LOGGER,"app_no_configuration"); //$NON-NLS-1$

    static final I18NMessage2P QF_SENDING_STATUS=
        new I18NMessage2P(LOGGER,"qf_sending_status"); //$NON-NLS-1$
    static final I18NMessage1P QF_SENDING_TRADE_RECORD=
        new I18NMessage1P(LOGGER,"qf_sending_trade_record"); //$NON-NLS-1$
    static final I18NMessage2P QF_REPORT_FAILED=
        new I18NMessage2P(LOGGER,"qf_report_failed"); //$NON-NLS-1$
    static final I18NMessage1P QF_SENDING_REPLY=
        new I18NMessage1P(LOGGER,"qf_sending_reply"); //$NON-NLS-1$
    static final I18NMessage2P QF_TO_ADMIN=
        new I18NMessage2P(LOGGER,"qf_to_admin"); //$NON-NLS-1$
    static final I18NMessage2P QF_FROM_ADMIN=
        new I18NMessage2P(LOGGER,"qf_from_admin"); //$NON-NLS-1$
    static final I18NMessage2P QF_TO_APP=
        new I18NMessage2P(LOGGER,"qf_to_app"); //$NON-NLS-1$
    static final I18NMessage2P QF_FROM_APP=
        new I18NMessage2P(LOGGER,"qf_from_app"); //$NON-NLS-1$
    static final I18NMessage2P QF_MODIFICATION_FAILED=
        new I18NMessage2P(LOGGER,"qf_modification_failed"); //$NON-NLS-1$
    static final I18NMessage2P QF_IN_MESSAGE_REJECTED=
        new I18NMessage2P(LOGGER,"qf_in_message_rejected"); //$NON-NLS-1$
    static final I18NMessage0P QF_DISALLOWED_MESSAGE=
        new I18NMessage0P(LOGGER,"qf_disallowed_message"); //$NON-NLS-1$
    static final I18NMessage1P QF_TRADE_SESSION_STATUS=
        new I18NMessage1P(LOGGER,"qf_trade_session_status"); //$NON-NLS-1$
    static final I18NMessage1P QF_COMP_ID_REJECT= 
        new I18NMessage1P(LOGGER,"qf_comp_id_reject"); //$NON-NLS-1$
    static final I18NMessage1P QF_COMP_ID_REJECT_FAILED=
        new I18NMessage1P(LOGGER,"qf_comp_id_reject_failed"); //$NON-NLS-1$
    static final I18NMessage1P QF_UNKNOWN_BROKER_ID = new I18NMessage1P(LOGGER,"qf_unknown_broker_id"); //$NON-NLS-1$
    static final I18NMessage1P QF_CANNOT_ADD_INVALID_REPORT = new I18NMessage1P(LOGGER,"qf_cannot_add_invalid_report"); //$NON-NLS-1$
    static final I18NMessage1P QF_UNAUTHORIZED_DESTINATION = new I18NMessage1P(LOGGER,"qf_unauthorized_destination"); //$NON-NLS-1$
    static final I18NMessage4P QF_SESSION_RESTORE = new I18NMessage4P(LOGGER,"qf_session_restore"); //$NON-NLS-1$
    static final I18NMessage2P QF_SESSION_RESET = new I18NMessage2P(LOGGER,"qf_session_reset"); //$NON-NLS-1$
    static final I18NMessage1P QF_NO_ROOT_ORDER_ID = new I18NMessage1P(LOGGER,"qf_no_root_order_id"); //$NON-NLS-1$
    static final I18NMessage1P QF_STANDARD_SHUTDOWN = new I18NMessage1P(LOGGER,"qf_standard_shutdown"); //$NON-NLS-1$
    static final I18NMessage1P QF_SHUTDOWN_ERROR = new I18NMessage1P(LOGGER,"qf_shutdown_error"); //$NON-NLS-1$
    static final I18NMessage1P ANALYZED_MESSAGE = new I18NMessage1P(LOGGER,"analyzed_message"); //$NON-NLS-1$

    static final I18NMessage1P RH_REJ_CONVERSION_FAILED=
        new I18NMessage1P(LOGGER,"rh_rej_conversion_failed"); //$NON-NLS-1$
    static final I18NMessage0P RH_REJ_ID_GENERATION_FAILED=
        new I18NMessage0P(LOGGER,"rh_rej_id_generation_failed"); //$NON-NLS-1$
    static final I18NMessage1P RH_REJ_AUGMENTATION_FAILED=
        new I18NMessage1P(LOGGER,"rh_rej_augmentation_failed"); //$NON-NLS-1$
    static final I18NMessage1P RH_RECEIVED_MESSAGE=
        new I18NMessage1P(LOGGER,"rh_received_message"); //$NON-NLS-1$
    static final I18NMessage0P RH_NULL_MESSAGE_ENVELOPE=
        new I18NMessage0P(LOGGER,"rh_null_message_envelope"); //$NON-NLS-1$
    static final I18NMessage1P RH_SESSION_EXPIRED=
        new I18NMessage1P(LOGGER,"rh_session_expired"); //$NON-NLS-1$
    static final I18NMessage0P RH_NULL_MESSAGE=
        new I18NMessage0P(LOGGER,"rh_null_message"); //$NON-NLS-1$
    static final I18NMessage0P RH_UNSUPPORTED_MESSAGE=
        new I18NMessage0P(LOGGER,"rh_unsupported_message"); //$NON-NLS-1$
    static final I18NMessage0P RH_UNKNOWN_BROKER=
        new I18NMessage0P(LOGGER,"rh_unknown_broker"); //$NON-NLS-1$
    static final I18NMessage0P RH_UNKNOWN_BROKER_ID=
        new I18NMessage0P(LOGGER,"rh_unknown_broker_id"); //$NON-NLS-1$
    static final I18NMessage0P RH_UNAVAILABLE_BROKER=
        new I18NMessage0P(LOGGER,"rh_unavailable_broker"); //$NON-NLS-1$
    static final I18NMessage0P RH_CONVERSION_FAILED=
        new I18NMessage0P(LOGGER,"rh_conversion_failed"); //$NON-NLS-1$
    static final I18NMessage0P RH_ORDER_DISALLOWED=
        new I18NMessage0P(LOGGER,"rh_order_disallowed"); //$NON-NLS-1$
    static final I18NMessage0P RH_MODIFICATION_FAILED=
        new I18NMessage0P(LOGGER,"rh_modification_failed"); //$NON-NLS-1$
    static final I18NMessage0P RH_ROUTING_FAILED=
        new I18NMessage0P(LOGGER,"rh_routing_failed"); //$NON-NLS-1$
    static final I18NMessage0P RH_PRE_SEND_MODIFICATION_FAILED=
        new I18NMessage0P
        (LOGGER,"rh_pre_send_modification_failed"); //$NON-NLS-1$
    static final I18NMessage3P RH_ACK_FAILED_WARN=
        new I18NMessage3P(LOGGER,"rh_ack_failed_warn"); //$NON-NLS-1$
    static final I18NMessage0P RH_ACK_FAILED=
        new I18NMessage0P(LOGGER,"rh_ack_failed"); //$NON-NLS-1$
    static final I18NMessage4P RH_MESSAGE_PROCESSING_FAILED=
        new I18NMessage4P(LOGGER,"rh_message_processing_failed"); //$NON-NLS-1$
    static final I18NMessage1P RH_ANALYZED_MESSAGE=
        new I18NMessage1P(LOGGER,"rh_analyzed_message"); //$NON-NLS-1$
    static final I18NMessage1P RH_REPORT_FAILED=
        new I18NMessage1P(LOGGER,"rh_report_failed"); //$NON-NLS-1$
    static final I18NMessage1P RH_SENDING_REPLY=
        new I18NMessage1P(LOGGER,"rh_sending_reply"); //$NON-NLS-1$
    static final I18NMessage1P RH_NO_SELECTOR = new I18NMessage1P(LOGGER,"rh_no_selector");
    static final I18NMessage2P RH_PEG_TO_MIDPOINT_FAILED = new I18NMessage2P(LOGGER,"rh_peg_to_midpoint_failed");
    static final I18NMessage2P RH_REPRICING = new I18NMessage2P(LOGGER,"rh_repricing");
    static final I18NMessage2P RH_UNABLE_TO_DETERMINE_ORDER_STATUS = new I18NMessage2P(LOGGER,"rh_unable_to_determine_order_status"); //$NON-NLS-1$

    static final I18NMessage1P RP_PERSISTED_REPLY=
        new I18NMessage1P(LOGGER,"rp_persisted_reply"); //$NON-NLS-1$
    static final I18NMessage1P RP_PERSIST_ERROR=
        new I18NMessage1P(LOGGER,"rp_persist_error"); //$NON-NLS-1$
    static final I18NMessage2P RP_ADD_TO_CACHE_FAILED=
        new I18NMessage2P(LOGGER,"rp_add_to_cache_failed"); //$NON-NLS-1$
    static final I18NMessage2P RP_ADD_TO_MAP_FAILED=
        new I18NMessage2P(LOGGER,"rp_add_to_map_failed"); //$NON-NLS-1$
    static final I18NMessage1P RP_GET_FROM_DB_FAILED=
        new I18NMessage1P(LOGGER,"rp_get_from_db_failed"); //$NON-NLS-1$
    static final I18NMessage2P RP_COULD_NOT_DETERMINE_PRINCIPALS = new I18NMessage2P(LOGGER,
                                                                                     "rp_could_not_determine_principals"); //$NON-NLS-1$
    static final I18NMessage1P RP_NO_ORDER_INFO = new I18NMessage1P(LOGGER,
                                                                    "rp_no_order_info"); //$NON-NLS-1$

    static final I18NMessage2P OIM_ADDED_ENTRY=
        new I18NMessage2P(LOGGER,"oim_added_entry"); //$NON-NLS-1$
    static final I18NMessage2P OIM_REMOVED_ENTRY=
        new I18NMessage2P(LOGGER,"oim_removed_entry"); //$NON-NLS-1$

    static final I18NMessage1P ORUM_LOG_ERROR_LOADING_FILE =
            new I18NMessage1P(LOGGER, "orum_log_error_loading_file");   //$NON-NLS-1$
    static final I18NMessage0P ORUM_LOG_SKIP_LOAD_FILE =
            new I18NMessage0P(LOGGER, "orum_log_skip_load_file");   //$NON-NLS-1$
}
