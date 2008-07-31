package org.marketcetera.ors;

import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessage1P;
import org.marketcetera.util.log.I18NMessage2P;
import org.marketcetera.util.log.I18NMessage3P;
import org.marketcetera.util.log.I18NMessageProvider;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * The internationalization constants used by this package.
 *
 * @author klim@marketcetera.com
 * @since $Release$
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

    static final I18NMessage0P APP_EXIT = 
        new I18NMessage0P(LOGGER,"app_exit"); //$NON-NLS-1$
    static final I18NMessage1P APP_USAGE = 
        new I18NMessage1P(LOGGER,"app_usage"); //$NON-NLS-1$
    static final I18NMessage0P APP_AUTH_OPTIONS = 
        new I18NMessage0P(LOGGER,"app_auth_options"); //$NON-NLS-1$
    static final I18NMessage0P APP_NO_ARGS_ALLOWED = 
        new I18NMessage0P(LOGGER,"app_no_args_allowed"); //$NON-NLS-1$

    static final I18NMessage0P ERROR_CONFIG = 
        new I18NMessage0P(LOGGER,"error_config"); //$NON-NLS-1$
    static final I18NMessage0P ERROR_STACK_TRACE = 
        new I18NMessage0P(LOGGER,"error_stack_trace"); //$NON-NLS-1$

    static final I18NMessage2P ERROR_MESSAGE_EXCEPTION = 
        new I18NMessage2P(LOGGER,"error_message_exception"); //$NON-NLS-1$

    static final I18NMessage2P CONNECTING_TO = 
        new I18NMessage2P(LOGGER,"connecting_to"); //$NON-NLS-1$
    static final I18NMessage1P TRADE_SESSION_STATUS = 
        new I18NMessage1P(LOGGER,"trade_session_status"); //$NON-NLS-1$

    static final I18NMessage1P ERROR_INIT_PROPNAME_IGNORE = 
        new I18NMessage1P(LOGGER,"error_init_propname_ignore"); //$NON-NLS-1$
    static final I18NMessage0P ERROR_INCOMING_MSG_NULL = 
        new I18NMessage0P(LOGGER,"error_incoming_msg_null"); //$NON-NLS-1$
    static final I18NMessage2P ERROR_INCOMING_MSG_REJECTED = 
        new I18NMessage2P(LOGGER,"error_incoming_msg_rejected"); //$NON-NLS-1$
    static final I18NMessage0P ERROR_SENDING_QF_MESSAGE = 
        new I18NMessage0P(LOGGER,"error_sending_qf_message"); //$NON-NLS-1$
    static final I18NMessage1P ERROR_SENDING_JMS_MESSAGE = 
        new I18NMessage1P(LOGGER,"error_sending_jms_message"); //$NON-NLS-1$
    static final I18NMessage1P ERROR_GENERATING_EXEC_ID = 
        new I18NMessage1P(LOGGER,"error_generating_exec_id"); //$NON-NLS-1$
    static final I18NMessage0P ERROR_MESSAGE_MALFORMED_NO_FIX_VERSION = 
        new I18NMessage0P(LOGGER,"error_message_malformed_no_fix_version"); //$NON-NLS-1$
    static final I18NMessage2P ERROR_MISMATCHED_FIX_VERSION = 
        new I18NMessage2P(LOGGER,"error_mismatched_fix_version"); //$NON-NLS-1$
    static final I18NMessage1P ERROR_ORDER_LIMITS_UNINITIALIZED = 
        new I18NMessage1P(LOGGER,"error_order_limits_uninitialized"); //$NON-NLS-1$
    static final I18NMessage0P ERROR_ORDER_LIST_UNSUPPORTED = 
        new I18NMessage0P(LOGGER,"error_order_list_unsupported"); //$NON-NLS-1$
    static final I18NMessage1P ERROR_UNSUPPORTED_ORDER_TYPE = 
        new I18NMessage1P(LOGGER,"error_unsupported_order_type"); //$NON-NLS-1$
    static final I18NMessage0P ERROR_NO_DESTINATION_CONNECTION = 
        new I18NMessage0P(LOGGER,"error_no_destination_connection"); //$NON-NLS-1$
    static final I18NMessage1P ERROR_NO_DELIVER_TO_COMPID_FIELD = 
        new I18NMessage1P(LOGGER,"error_no_deliver_to_compid_field"); //$NON-NLS-1$

    static final I18NMessage3P ERROR_OL_MAX_QTY = 
        new I18NMessage3P(LOGGER,"error_ol_max_qty"); //$NON-NLS-1$
    static final I18NMessage3P ERROR_OL_MAX_NOTIONAL = 
        new I18NMessage3P(LOGGER,"error_ol_max_notional"); //$NON-NLS-1$
    static final I18NMessage3P ERROR_OL_MAX_PRICE = 
        new I18NMessage3P(LOGGER,"error_ol_max_price"); //$NON-NLS-1$
    static final I18NMessage3P ERROR_OL_MIN_PRICE = 
        new I18NMessage3P(LOGGER,"error_ol_min_price"); //$NON-NLS-1$
    static final I18NMessage1P ERROR_OL_MARKET_NOT_ALLOWED_PRICE = 
        new I18NMessage1P(LOGGER,"error_ol_market_not_allowed_price"); //$NON-NLS-1$
    static final I18NMessage1P ERROR_OL_MARKET_NOT_ALLOWED = 
        new I18NMessage1P(LOGGER,"error_ol_market_not_allowed"); //$NON-NLS-1$
}
