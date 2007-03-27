package org.marketcetera.quickfix;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.LoggerAdapter;
import org.marketcetera.core.MessageKey;
import quickfix.DataDictionary;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.Message.Header;
import quickfix.StringField;
import quickfix.field.MsgType;

/**
 * Collection of utilities to create work with FIX messages
 *
 * @author gmiller
 *         $Id$
 */
@ClassVersion("$Id$")
public class FIXMessageUtil {

    private static final String LOGGER_NAME = FIXMessageUtil.class.getName();
    private static final int MAX_FIX_FIELDS = 2000;     // What we think the ID of the last fix field is

    /**
     * Creates a new instance of FIXMessageUtil
     */
    public FIXMessageUtil() {
    }

    private static boolean msgTypeHelper(Message fixMessage, String msgType) {
    	if (fixMessage != null){
	    	try {
	            MsgType msgTypeField = new MsgType();
	            Header header = fixMessage.getHeader();
				if (header.isSetField(msgTypeField)){
	            	header.getField(msgTypeField);
	            	return msgType.equals(msgTypeField.getValue());
	            }
	        } catch (Exception ignored) {
                // ignored
            }
    	}
        return false;
    }

    public static boolean isExecutionReport(Message jmsMessage) {
        return msgTypeHelper(jmsMessage, MsgType.EXECUTION_REPORT);
    }

    public static boolean isOrderSingle(Message jmsMessage) {
        return msgTypeHelper(jmsMessage, MsgType.ORDER_SINGLE);
    }

    public static boolean isReject(Message jmsMessage) {
        return msgTypeHelper(jmsMessage, MsgType.REJECT);
    }

    public static boolean isCancelReject(Message jmsMessage) {
        return msgTypeHelper(jmsMessage, MsgType.ORDER_CANCEL_REJECT);
    }

    public static boolean isStatusRequest(Message jmsMessage) {
        return msgTypeHelper(jmsMessage, MsgType.ORDER_STATUS_REQUEST);
    }

    public static boolean isCancelRequest(Message jmsMessage) {
        return msgTypeHelper(jmsMessage, MsgType.ORDER_CANCEL_REQUEST);
    }

    public static boolean isCancelReplaceRequest(Message jmsMessage) {
        return msgTypeHelper(jmsMessage, MsgType.ORDER_CANCEL_REPLACE_REQUEST);
    }

    public static boolean isOrderList(Message jmsMessage) {
        return msgTypeHelper(jmsMessage, MsgType.ORDER_LIST);
    }


    /** Helper method to extract all useful fields from an existing message into another message
     * This is usually called when the "existing" message is malformed and is missing some fields,
     * and an appropriate "reject" message needs to be sent.
     * Can't say we are proud of this method - it's rather a kludge.
     * Goes through all the required fields in "outgoing" message, and ignores any missing ones
     * Skips over any of the outgoing fields that have already been set
     *
     * Use cases: an order comes in missing a Side, so we need to create an ExecutionReport
     * that's a rejection, and need to extract all the other fields (ClOrdId, size, etc)
     * which may or may not be present since the order is malformed
     *
     * @param outgoingMessage
     * @param existingMessage
     */
    public static void fillFieldsFromExistingMessage(Message outgoingMessage, Message existingMessage)
    {
        try {
            String msgType = outgoingMessage.getHeader().getString(MsgType.FIELD);
            DataDictionary dict = FIXDataDictionaryManager.getDictionary();
            for (int fieldInt = 1; fieldInt < MAX_FIX_FIELDS; fieldInt++){
                if (dict.isRequiredField(msgType, fieldInt) && existingMessage.isSetField(fieldInt) &&
                        !outgoingMessage.isSetField(fieldInt)){
                    try {
                        outgoingMessage.setField(existingMessage.getField(new StringField(fieldInt)));
                    } catch (FieldNotFound e) {
                        // do nothing and ignore
                    }
                }
            }

        } catch (FieldNotFound ex) {
            LoggerAdapter.error(MessageKey.FIX_OUTGOING_NO_MSGTYPE.getLocalizedMessage(), ex, LOGGER_NAME);
        }
    }


}
