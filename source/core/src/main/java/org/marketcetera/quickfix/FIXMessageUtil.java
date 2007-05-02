package org.marketcetera.quickfix;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.LoggerAdapter;
import org.marketcetera.core.MarketceteraException;
import org.marketcetera.core.MessageKey;

import quickfix.DataDictionary;
import quickfix.FieldMap;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.StringField;
import quickfix.Message.Header;
import quickfix.field.CollReqID;
import quickfix.field.ConfirmReqID;
import quickfix.field.EncodedText;
import quickfix.field.MDReqID;
import quickfix.field.MsgType;
import quickfix.field.NetworkRequestID;
import quickfix.field.PosReqID;
import quickfix.field.QuoteID;
import quickfix.field.QuoteReqID;
import quickfix.field.QuoteStatusReqID;
import quickfix.field.RFQReqID;
import quickfix.field.SecurityReqID;
import quickfix.field.SecurityStatusReqID;
import quickfix.field.SettlInstReqID;
import quickfix.field.Text;
import quickfix.field.TradSesReqID;
import quickfix.field.TradeRequestID;
import quickfix.field.UserRequestID;

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

    public static boolean isExecutionReport(Message message) {
        return msgTypeHelper(message, MsgType.EXECUTION_REPORT);
    }

    public static boolean isOrderSingle(Message message) {
        return msgTypeHelper(message, MsgType.ORDER_SINGLE);
    }

    public static boolean isReject(Message message) {
        return msgTypeHelper(message, MsgType.REJECT);
    }

    public static boolean isCancelReject(Message message) {
        return msgTypeHelper(message, MsgType.ORDER_CANCEL_REJECT);
    }

    public static boolean isStatusRequest(Message message) {
        return msgTypeHelper(message, MsgType.ORDER_STATUS_REQUEST);
    }

    public static boolean isCancelRequest(Message message) {
        return msgTypeHelper(message, MsgType.ORDER_CANCEL_REQUEST);
    }

    public static boolean isCancelReplaceRequest(Message message) {
        return msgTypeHelper(message, MsgType.ORDER_CANCEL_REPLACE_REQUEST);
    }

    public static boolean isOrderList(Message message) {
        return msgTypeHelper(message, MsgType.ORDER_LIST);
    }

    public static boolean isLogon(Message message){
    	return msgTypeHelper(message, MsgType.LOGON);
    }

    public static boolean isLogout(Message message){
    	return msgTypeHelper(message, MsgType.LOGOUT);
    }

	public static boolean isMarketDataRequest(Message message) {
    	return msgTypeHelper(message, MsgType.MARKET_DATA_REQUEST);
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
            DataDictionary dict = FIXDataDictionaryManager.getCurrentFIXDataDictionary().getDictionary();
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

	public static void insertFieldIfMissing(int fieldNumber, String value, FieldMap fieldMap) throws MarketceteraException {
		if (fieldMap.isSetField(fieldNumber)){
			throw new MarketceteraException("Field "+fieldNumber+" is already set in message.");
		} else {
			fieldMap.setField(new StringField(fieldNumber, value));
		}
	}

	public static String getTextOrEncodedText(Message aMessage, String defaultString) {
		String text = defaultString;
		if (aMessage.isSetField(Text.FIELD)){
			try {
				text = aMessage.getString(Text.FIELD);
			} catch (FieldNotFound e) {
			}
		} else {
			try {
				text = aMessage.getString(EncodedText.FIELD);
			} catch (FieldNotFound e) {
			}
		}
		return text;
	}

	/**
	 * 
	 * @param msgType the msgType of the request message
	 * @return the field that represents the request/response correlation ID in the request message
	 */
	public static StringField getCorrelationField(FIXVersion version, String msgType) {
		StringField reqIDField = null;
		if (MsgType.COLLATERAL_REQUEST.equals(msgType) || MsgType.COLLATERAL_RESPONSE.equals(msgType)){
			reqIDField = new CollReqID();
		} else if (MsgType.CONFIRMATION_REQUEST.equals(msgType) || MsgType.CONFIRMATION.equals(msgType)){
			reqIDField = new ConfirmReqID();
		} else if (MsgType.DERIVATIVE_SECURITY_LIST_REQUEST.equals(msgType) || MsgType.DERIVATIVE_SECURITY_LIST.equals(msgType)){
			reqIDField = new SecurityReqID();
		} else if (MsgType.MARKET_DATA_REQUEST.equals(msgType) || MsgType.MARKET_DATA_INCREMENTAL_REFRESH.equals(msgType) || MsgType.MARKET_DATA_REQUEST_REJECT.equals(msgType) || MsgType.MARKET_DATA_SNAPSHOT_FULL_REFRESH.equals(msgType)){
			reqIDField = new MDReqID();
		} else if (MsgType.NETWORK_STATUS_REQUEST.equals(msgType) || MsgType.NETWORK_STATUS_RESPONSE.equals(msgType)){
			reqIDField = new NetworkRequestID();
		} else if (MsgType.POSITION_MAINTENANCE_REQUEST.equals(msgType) || MsgType.POSITION_MAINTENANCE_REPORT.equals(msgType)
				||MsgType.REQUEST_FOR_POSITIONS.equals(msgType) || MsgType.REQUEST_FOR_POSITIONS_ACK.equals(msgType) || MsgType.POSITION_REPORT.equals(msgType)){
			reqIDField = new PosReqID();
		} else if (MsgType.QUOTE_REQUEST.equals(msgType) || MsgType.QUOTE_REQUEST_REJECT.equals(msgType) || MsgType.QUOTE.equals(msgType)){
			reqIDField = new QuoteReqID();
		} else if (MsgType.QUOTE_STATUS_REQUEST.equals(msgType) || MsgType.QUOTE_STATUS_REPORT.equals(msgType)){
			if (FIXVersion.FIX42.equals(version)){
				reqIDField = new QuoteID();
			} else {
				reqIDField = new QuoteStatusReqID();
			}
		} else if (MsgType.RFQ_REQUEST.equals(msgType)){
			reqIDField = new RFQReqID();
		} else if (MsgType.SECURITY_DEFINITION_REQUEST.equals(msgType) || MsgType.SECURITY_DEFINITION.equals(msgType)
				|| MsgType.SECURITY_LIST_REQUEST.equals(msgType) || MsgType.SECURITY_LIST.equals(msgType)
				|| MsgType.SECURITY_TYPE_REQUEST.equals(msgType) || MsgType.SECURITY_TYPES.equals(msgType)){
			reqIDField = new SecurityReqID();
		} else if (MsgType.SECURITY_STATUS_REQUEST.equals(msgType) || MsgType.SECURITY_STATUS.equals(msgType)){
			reqIDField = new SecurityStatusReqID();
		} else if (MsgType.SETTLEMENT_INSTRUCTION_REQUEST.equals(msgType) || MsgType.SETTLEMENT_INSTRUCTIONS.equals(msgType)){
			reqIDField = new SettlInstReqID();
		} else if (MsgType.TRADE_CAPTURE_REPORT_REQUEST.equals(msgType) || MsgType.TRADE_CAPTURE_REPORT.equals(msgType) || MsgType.TRADE_CAPTURE_REPORT_REQUEST_ACK.equals(msgType)){
			reqIDField = new TradeRequestID();
		} else if (MsgType.TRADING_SESSION_STATUS_REQUEST.equals(msgType) || MsgType.TRADING_SESSION_STATUS.equals(msgType)){
			reqIDField = new TradSesReqID();
		} else if (MsgType.USER_REQUEST.equals(msgType) || MsgType.USER_RESPONSE.equals(msgType)){
			reqIDField = new UserRequestID();
		}
		return reqIDField;
	}



}
