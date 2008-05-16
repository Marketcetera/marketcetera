package org.marketcetera.ors;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.LocalizedMessage;
import org.marketcetera.core.MessageKey;

/**
 * @author toli
 * @version $Id: ORSMessageKey.java 3587 2008-04-24 23:38:47Z tlerios $
 */

@ClassVersion("$Id: ORSMessageKey.java 3587 2008-04-24 23:38:47Z tlerios $")
public enum ORSMessageKey implements LocalizedMessage {
    ERROR_INIT_PROPNAME_IGNORE("ors.init.ignoring_propname"),
    ERROR_INCOMING_MSG_NULL("ors.error_incoming_msg_null"),
    ERROR_INCOMING_MSG_REJECTED("ors.error_incoming_msg_rejected"),
    ERROR_SENDING_QF_MESSAGE("ors.error.send_qf_message"),
    ERROR_SENDING_JMS_MESSAGE("ors.error.send_jms_message"),
    ERROR_GENERATING_EXEC_ID("ors.error.gen_exec_id"),
    CONNECTING_TO("ors.connecting_to"),
    TRADE_SESSION_STATUS("ors.trade_session_status"),
    ERROR_MISMATCHED_FIX_VERSION("ors.error.mismatched_fix_version"),
    ERROR_ORDER_LIMIT_UNINIT("ors.error.order_limits.uninitialized"),
    ERROR_MALFORMED_MESSAGE_NO_FIX_VERSION("ors.error.message_malformed_no_fix_version"),
    ERROR_ORDER_LIST_UNSUPPORTED("ors.error.order_list_unsupported"),
    ERROR_UNSUPPORTED_ORDER_TYPE("ors.error.unsupported_msg_type"),
    ERROR_NO_DESTINATION_CONNECTION("ors.error.no_destination_connection"),
    ERROR_DELIVER_TO_COMP_ID_NOT_HANDLED("ors.error.no_delivertocompid_field"),

    // order limits
    ERROR_OL_MAX_QTY("ors.error.ol.max_qty"),
    ERROR_OL_MAX_NOTIONAL("ors.error.ol.max_notional"),
    ERROR_OL_MAX_PRICE("ors.error.ol.max_price"),
    ERROR_OL_MIN_PRICE("ors.error.ol.min_price"),
    ERROR_OL_MARKET_NOT_ALLOWED_PRICE("ors.error.ol.market_not_allowed_price"),
    ERROR_OL_MARKET_NOT_ALLOWED("ors.error.ol.market_not_allowed"),

    MESSAGE_EXCEPTION("ors.message.error.generic");

    private ORSMessageKey(String inKey) {
        key = inKey;
    }

    private final String key;

    public String toString() {
        return key;
    }

    public String getLocalizedMessage()
    {
        return MessageKey.getMessageString(toString());
    }

    public String getLocalizedMessage(Object ... args)
    {
        return MessageKey.getMessageString(toString(), args);
    }

}
