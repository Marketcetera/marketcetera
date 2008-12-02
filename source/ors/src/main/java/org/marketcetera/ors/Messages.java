package org.marketcetera.ors;

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
        new I18NMessageProvider("ors");  //$NON-NLS-1$

    /**
     * The logger.
     */

    static final I18NLoggerProxy LOGGER = 
        new I18NLoggerProxy(PROVIDER);

    /*
     * The messages.
     */

    static final I18NMessage0P APP_COPYRIGHT=
        new I18NMessage0P(LOGGER,"app_copyright"); //$NON-NLS-1$
    static final I18NMessage0P APP_START=
        new I18NMessage0P(LOGGER,"app_start"); //$NON-NLS-1$
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
    static final I18NMessage2P QF_SENDING_REPLY=
        new I18NMessage2P(LOGGER,"qf_sending_reply"); //$NON-NLS-1$
    static final I18NMessage2P QF_SENDING_TRADE_RECORD=
        new I18NMessage2P(LOGGER,"qf_sending_trade_record"); //$NON-NLS-1$
    static final I18NMessage2P QF_TO_ADMIN=
        new I18NMessage2P(LOGGER,"qf_to_admin"); //$NON-NLS-1$
    static final I18NMessage2P QF_FROM_ADMIN=
        new I18NMessage2P(LOGGER,"qf_from_admin"); //$NON-NLS-1$
    static final I18NMessage2P QF_TO_APP=
        new I18NMessage2P(LOGGER,"qf_to_app"); //$NON-NLS-1$
    static final I18NMessage2P QF_FROM_APP=
        new I18NMessage2P(LOGGER,"qf_from_app"); //$NON-NLS-1$
    static final I18NMessage0P QF_REPORT_FAILED=
        new I18NMessage0P(LOGGER,"qf_report_failed"); //$NON-NLS-1$
    static final I18NMessage0P QF_REPORT_TYPE_UNSUPPORTED=
        new I18NMessage0P(LOGGER,"qf_report_type_unsupported"); //$NON-NLS-1$
    static final I18NMessage0P QF_MODIFICATION_FAILED=
        new I18NMessage0P(LOGGER,"qf_modification_failed"); //$NON-NLS-1$
    static final I18NMessage0P QF_DISALLOWED_MESSAGE=
        new I18NMessage0P(LOGGER,"qf_disallowed_message"); //$NON-NLS-1$
    static final I18NMessage0P QF_COMP_ID_REJECT_FAILED=
        new I18NMessage0P(LOGGER,"qf_comp_id_reject_failed"); //$NON-NLS-1$
    static final I18NMessage1P QF_TRADE_SESSION_STATUS=
        new I18NMessage1P(LOGGER,"qf_trade_session_status"); //$NON-NLS-1$
    static final I18NMessage2P QF_IN_MESSAGE_REJECTED=
        new I18NMessage2P(LOGGER,"qf_in_message_rejected"); //$NON-NLS-1$
    static final I18NMessage1P QF_COMP_ID_REJECT= 
        new I18NMessage1P(LOGGER,"qf_comp_id_reject"); //$NON-NLS-1$

    static final I18NMessage0P RH_NULL_MESSAGE=
        new I18NMessage0P(LOGGER,"rh_null_message"); //$NON-NLS-1$
    static final I18NMessage0P RH_UNSUPPORTED_MESSAGE=
        new I18NMessage0P(LOGGER,"rh_unsupported_message"); //$NON-NLS-1$
    static final I18NMessage0P RH_UNKNOWN_DESTINATION=
        new I18NMessage0P(LOGGER,"rh_unknown_destination"); //$NON-NLS-1$
    static final I18NMessage1P RH_UNAVAILABLE_DESTINATION=
        new I18NMessage1P(LOGGER,"rh_unavailable_destination"); //$NON-NLS-1$
    static final I18NMessage0P RH_CONVERSION_FAILED=
        new I18NMessage0P(LOGGER,"rh_conversion_failed"); //$NON-NLS-1$
    static final I18NMessage1P RH_MODIFICATION_FAILED=
        new I18NMessage1P(LOGGER,"rh_modification_failed"); //$NON-NLS-1$
    static final I18NMessage0P RH_ORDER_DISALLOWED=
        new I18NMessage0P(LOGGER,"rh_order_disallowed"); //$NON-NLS-1$
    static final I18NMessage1P RH_ROUTING_FAILED=
        new I18NMessage1P(LOGGER,"rh_routing_failed"); //$NON-NLS-1$
    static final I18NMessage1P RH_MESSAGE_REJECTED=
        new I18NMessage1P(LOGGER,"rh_message_rejected"); //$NON-NLS-1$
    static final I18NMessage1P RH_REPORT_FAILED=
        new I18NMessage1P(LOGGER,"rh_report_failed"); //$NON-NLS-1$
    static final I18NMessage1P RH_REPORT_TYPE_UNSUPPORTED=
        new I18NMessage1P(LOGGER,"rh_report_type_unsupported"); //$NON-NLS-1$
    static final I18NMessage1P RH_REPORT_FAILED_SENT=
        new I18NMessage1P(LOGGER,"rh_report_failed_sent"); //$NON-NLS-1$

    static final I18NMessage1P RP_PERSISTED_REPLY=
        new I18NMessage1P(LOGGER,"rp_persisted_reply"); //$NON-NLS-1$


    // LEGACY CODE.

    static final I18NMessage2P ERROR_MESSAGE_EXCEPTION = 
        new I18NMessage2P(LOGGER,"error_message_exception"); //$NON-NLS-1$
    static final I18NMessage1P ERROR_GENERATING_EXEC_ID = 
        new I18NMessage1P(LOGGER,"error_generating_exec_id"); //$NON-NLS-1$
}
