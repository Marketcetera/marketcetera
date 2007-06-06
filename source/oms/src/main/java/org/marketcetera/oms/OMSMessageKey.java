package org.marketcetera.oms;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.LocalizedMessage;
import org.marketcetera.core.MessageKey;

/**
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public enum OMSMessageKey implements LocalizedMessage {
    ERROR_INIT_PROPNAME_IGNORE("oms.init.ignoring_propname"),
    ERROR_INCOMING_MSG_NULL("oms.error_incoming_msg_null"),
    ERROR_SENDING_QF_MESSAGE("oms.error.send_qf_message"),
    ERROR_SENDING_JMS_MESSAGE("oms.error.send_jms_message"),
    ERROR_GENERATING_EXEC_ID("oms.error.gen_exec_id"),
    CONNECTING_TO("oms.connecting_to"),
    ERROR_MISMATCHED_FIX_VERSION("oms.error.mismatched_fix_version"),
    ERROR_ORDER_LIMIT_UNINIT("oms.error.order_limits.uninitialized"),
    ERROR_MALFORMED_MESSAGE_NO_FIX_VERSION("oms.error.message_malformed_no_fix_version"),
    ERROR_ORDER_LIST_UNSUPPORTED("oms.error.order_list_unsupported"),
    ERROR_UNSUPPORTED_ORDER_TYPE("oms.error.only_nos_supported"),
    ERROR_NO_DESTINATION_CONNECTION("oms.error.no_destination_connection"),

    // order limits
    ERROR_OL_MAX_QTY("oms.error.ol.max_qty"),
    ERROR_OL_MAX_NOTIONAL("oms.error.ol.max_notional"),
    ERROR_OL_MAX_PRICE("oms.error.ol.max_price"),
    ERROR_OL_MIN_PRICE("oms.error.ol.min_price"),
    ERROR_OL_MARKET_NOT_ALLOWED_PRICE("oms.error.ol.market_not_allowed_price"),
    ERROR_OL_MARKET_NOT_ALLOWED("oms.error.ol.market_not_allowed"),

    MESSAGE_EXCEPTION("oms.message.error.generic");

    private OMSMessageKey(String inKey) {
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
