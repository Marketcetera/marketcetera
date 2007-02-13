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
    ERROR_DECODING_MESSAGE("oms.error_decoding_message"),
    ERROR_SENDING_QF_MESSAGE("oms.error.send_qf_message"),
    ERROR_SENDING_JMS_MESSAGE("oms.error.send_jms_message"),
    CONNECTING_TO("oms.connecting_to"),
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
