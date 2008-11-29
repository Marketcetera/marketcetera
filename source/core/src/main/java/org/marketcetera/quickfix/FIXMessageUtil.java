package org.marketcetera.quickfix;

import static org.marketcetera.quickfix.Messages.CANNOT_CREATE_FIX_FIELD;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Pattern;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.CoreException;
import org.marketcetera.quickfix.cficode.OptionCFICode;
import org.marketcetera.util.log.I18NBoundMessage1P;

import quickfix.DataDictionary;
import quickfix.Field;
import quickfix.FieldMap;
import quickfix.FieldNotFound;
import quickfix.Group;
import quickfix.Message;
import quickfix.StringField;
import quickfix.Message.Header;
import quickfix.field.CFICode;
import quickfix.field.CollReqID;
import quickfix.field.ConfirmReqID;
import quickfix.field.EncodedText;
import quickfix.field.MDEntryType;
import quickfix.field.MDReqID;
import quickfix.field.MarketDepth;
import quickfix.field.MsgType;
import quickfix.field.NetworkRequestID;
import quickfix.field.NoMDEntries;
import quickfix.field.OrdStatus;
import quickfix.field.PosReqID;
import quickfix.field.PutOrCall;
import quickfix.field.QuoteID;
import quickfix.field.QuoteReqID;
import quickfix.field.QuoteStatusReqID;
import quickfix.field.RFQReqID;
import quickfix.field.SecurityReqID;
import quickfix.field.SecurityStatusReqID;
import quickfix.field.SettlInstReqID;
import quickfix.field.Symbol;
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
@ClassVersion("$Id$") //$NON-NLS-1$
public class FIXMessageUtil {

    private static final String LOGGER_NAME = FIXMessageUtil.class.getName();
    private static final int MAX_FIX_FIELDS = 2000;     // What we think the ID of the last fix field is
    public static final Pattern optionSymbolPattern = Pattern.compile("(\\w{1,3})\\+(\\w)(\\w)"); //$NON-NLS-1$

    /**
     * Creates a new instance of FIXMessageUtil
     */
    public FIXMessageUtil() {
    }

    public static int getMaxFIXFields() {
    	return MAX_FIX_FIELDS;
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

    public static boolean isBusinessMessageReject(Message message) {
    	return msgTypeHelper(message, MsgType.BUSINESS_MESSAGE_REJECT);
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

	public static boolean isMarketDataSnapshotFullRefresh(Message message) {
    	return msgTypeHelper(message, MsgType.MARKET_DATA_SNAPSHOT_FULL_REFRESH);
	}

	public static boolean isMarketDataIncrementalRefresh(Message message) {
    	return msgTypeHelper(message, MsgType.MARKET_DATA_INCREMENTAL_REFRESH);
	}

	public static boolean isResendRequest(Message message) {
		return msgTypeHelper(message, MsgType.RESEND_REQUEST);
	}

	public static boolean isDerivativeSecurityList(
			Message message) {
		return msgTypeHelper(message, MsgType.DERIVATIVE_SECURITY_LIST);
	}

	public static boolean isDerivativeSecurityListRequest(Message message) {
		return msgTypeHelper(message, MsgType.DERIVATIVE_SECURITY_LIST_REQUEST);
	}

	public static boolean isSecurityDefinitionRequest(Message message) {
		return msgTypeHelper(message, MsgType.SECURITY_DEFINITION_REQUEST);
	}

	public static boolean isSecurityDefinition(Message message) {
		return msgTypeHelper(message, MsgType.SECURITY_DEFINITION);
	}

    public static boolean isTradingSessionStatus(Message message) {
        return msgTypeHelper(message, MsgType.TRADING_SESSION_STATUS);
    }

    public static boolean isHeartbeat(Message message) {
		return msgTypeHelper(message, MsgType.HEARTBEAT);
	}
    public static boolean isSecurityListRequest(Message inMessage)
    {
        return msgTypeHelper(inMessage,
                             MsgType.SECURITY_LIST_REQUEST);
    }

	public static boolean isEquityOptionOrder(Message message)
	{
		try {
			return isOrderSingle(message)
			&& (
					message.isSetField(PutOrCall.FIELD) ||
					(message.isSetField(Symbol.FIELD) && optionSymbolPattern.matcher(message.getString(Symbol.FIELD)).matches()) ||
					message.isSetField(CFICode.FIELD) && OptionCFICode.isOptionCFICode(message.getString(CFICode.FIELD))
				);
		} catch (FieldNotFound e) {
			// should never happen
			return false;
		}
	}

	public static boolean isCancellable(char ordStatus) {
		switch (ordStatus){
		case OrdStatus.ACCEPTED_FOR_BIDDING:
		case OrdStatus.CALCULATED:
		case OrdStatus.NEW:
		case OrdStatus.PARTIALLY_FILLED:
		case OrdStatus.PENDING_CANCEL:
		case OrdStatus.PENDING_NEW:
		case OrdStatus.PENDING_REPLACE:
		case OrdStatus.STOPPED:
		case OrdStatus.SUSPENDED:
		case OrdStatus.REPLACED:
			return true;
		case OrdStatus.CANCELED:
		case OrdStatus.DONE_FOR_DAY:
		case OrdStatus.EXPIRED:
		case OrdStatus.FILLED:
		case OrdStatus.REJECTED:
		default:
			return false;
		}
	}

	public static boolean isCancellable(Message executionReport){
		if (isExecutionReport(executionReport)){
			try {
				return isCancellable(executionReport.getChar(OrdStatus.FIELD));
			} catch (FieldNotFound e) {
				return false;
			}
		}
		return false;
	}
    /**
     * FIX market depth constant for "best bid or offer" or Level I
     */
    public static final int TOP_OF_BOOK_DEPTH = 1;
	/**
	 * FIX market depth constant for "full book" or Level II
	 */
    public static final int FULL_BOOK_DEPTH = 0;
	/**
	 * Determines if the given message is a Level I or "top of book" message.
	 *
	 * @param inMessage a <code>Message</code> value
	 * @return a <code>boolean</code> value
	 */
	public static boolean isLevelOne(Message inMessage)
	{
	    int depth;
	    try {
            depth = getMarketDepth(inMessage);
        } catch (FieldNotFound e) {
            return false;
        }
        return depth == TOP_OF_BOOK_DEPTH;
	}
    /**
     * Determines if the given message is a Level II or "depth of book" message.
     *
     * @param inMessage a <code>Message</code> value
     * @return a <code>boolean</code> value
     */
    public static boolean isLevelTwo(Message inMessage)
    {
        int depth;
        try {
            depth = getMarketDepth(inMessage);
        } catch (FieldNotFound e) {
            return false;
        }
        return depth == FULL_BOOK_DEPTH;
    }
	/**
	 * Determines if the given message is a Level II or "full book" message.
	 *
     * @param inMessage a <code>Message</code> value
     * @return a <code>boolean</code> value
	 */
	public static boolean isFullBook(Message inMessage)
	{
        int depth;
        try {
            depth = getMarketDepth(inMessage);
        } catch (FieldNotFound e) {
            return false;
        }
        return depth == FULL_BOOK_DEPTH;
	}
	/**
	 * Gets the market depth for the given message.
	 *
	 * @param inMessage a <code>Message</code> value
	 * @return an <code>int</code>
	 * @throws FieldNotFound if the given message does not contain market depth information
	 */
	private static int getMarketDepth(Message inMessage)
	    throws FieldNotFound
	{
	    return inMessage.getInt(MarketDepth.FIELD);
	}

    /**
     * See {@link #fillFieldsFromExistingMessage(quickfix.Message, quickfix.Message, boolean, java.util.Set)}.
     * Invokes this api with a null inclusion set.
     *
     * @param outgoingMessage The message whose fields need to be filled.
     * @param existingMessage The message whose fields need to be copied.
     * @param onlyCopyRequiredFields if only required fields should be copied.
     */
    public static void fillFieldsFromExistingMessage(Message outgoingMessage,
                                                     Message existingMessage,
                                                     boolean onlyCopyRequiredFields)
    {
        fillFieldsFromExistingMessage(outgoingMessage, existingMessage, onlyCopyRequiredFields, null);
    }

    /**
     * Helper method to extract all useful fields from an existing message into another message
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
     * @param outgoingMessage The message whose fields need to be filled.
     * @param existingMessage The message whose fields need to be copied.
     * @param onlyCopyRequiredFields if only required fields should be copied.
     * @param inclusionSet the set of fields that should be copied. Can be null.
     * If not null, only the fields present in this set are copied.
     */
    public static void fillFieldsFromExistingMessage(Message outgoingMessage,
                                                     Message existingMessage,
                                                     boolean onlyCopyRequiredFields,
                                                     Set<Integer> inclusionSet)
    {
        try {
            String msgType = outgoingMessage.getHeader().getString(MsgType.FIELD);
            DataDictionary dict = CurrentFIXDataDictionary.getCurrentFIXDataDictionary().getDictionary();
            for (int fieldInt = 1; fieldInt < MAX_FIX_FIELDS; fieldInt++){
                if(inclusionSet != null && !(inclusionSet.contains(fieldInt))) {
                    continue;
                }
                if ((!onlyCopyRequiredFields || dict.isRequiredField(msgType,
						fieldInt))
						&& existingMessage.isSetField(fieldInt)
						&& !outgoingMessage.isSetField(fieldInt)
                        && dict.isMsgField(msgType, fieldInt)) {
					try {
						outgoingMessage.setField(existingMessage
								.getField(new StringField(fieldInt)));
					} catch (FieldNotFound e) {
						// do nothing and ignore
					}
				}
            }

        } catch (FieldNotFound ex) {
            Messages.FIX_OUTGOING_NO_MSGTYPE.error(LOGGER_NAME, ex);
        }
    }
    /**
     * Warning! This method does not handle groups.
     * @param copyTo
     * @param copyFrom
     */
    public static void copyFields(FieldMap copyTo,
                                  FieldMap copyFrom)
    {
    	Iterator<Field<?>> iter = copyFrom.iterator();
    	while (iter.hasNext()){
    		Field<?> field = iter.next();
    		try {
				copyTo.setField(copyFrom.getField(new StringField(field.getTag())));
			} catch (FieldNotFound e) {
				// do nothing
			}
    	}
    }

    public static boolean isRequiredField(Message message, int whichField) {
    	boolean required = false;
		try {
			String msgType;
			msgType = message.getHeader().getString(MsgType.FIELD);
			return isRequiredField(msgType, whichField);
		} catch (Exception e) {
			// Ignore
		}
		return required;
    }

    public static boolean isRequiredField(String msgType, int whichField){
    	boolean required = false;
		try {
			DataDictionary dictionary = CurrentFIXDataDictionary
					.getCurrentFIXDataDictionary().getDictionary();
			required = dictionary.isRequiredField(msgType, whichField);
		} catch (Exception anyException) {
			// Ignore
		}
		return required;
	}

    //cl todo:need to check if this take care of custom fields
    public static boolean isValidField(int whichField) {
		boolean valid = false;
		try {
			DataDictionary dictionary = CurrentFIXDataDictionary
					.getCurrentFIXDataDictionary().getDictionary();
			valid = dictionary.isField(whichField);
		} catch (Exception anyException) {
			// Ignore
		}
		return valid;
	}

    /**
	 * Copy only required fields.
	 */
    public static void fillFieldsFromExistingMessage(Message outgoingMessage, Message existingMessage) {
    	fillFieldsFromExistingMessage(outgoingMessage, existingMessage, true);
    }

	public static void insertFieldIfMissing(int fieldNumber, String value, FieldMap fieldMap) throws CoreException {
		if (fieldMap.isSetField(fieldNumber)){
			StringField testField = new StringField(fieldNumber);
			try {
				fieldMap.getField(testField);
				if(testField.getValue().equals(value)){
					return;
				}
			} catch (FieldNotFound ignored) {
				//Unexpected as isSetField() returned true
				//Don't do anything so that we set the field before we return.
			}
		}
		fieldMap.setField(new StringField(fieldNumber, value));
	}

	public static String getTextOrEncodedText(Message aMessage, String defaultString) {
		String text = defaultString;
		if (aMessage.isSetField(Text.FIELD)){
			try {
				text = aMessage.getString(Text.FIELD);
			} catch (FieldNotFound ignored) {
			}
		} else {
			try {
				text = aMessage.getString(EncodedText.FIELD); //i18n_string todo use the correct MessageEncoding value
			} catch (FieldNotFound ignored) {
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

	public static void mergeMarketDataMessages(Message marketDataSnapshotFullRefresh, Message marketDataIncrementalRefresh, FIXMessageFactory factory){
		if (!isMarketDataSnapshotFullRefresh(marketDataSnapshotFullRefresh)){
			throw new IllegalArgumentException(Messages.FIX_MD_MERGE_INVALID_INCOMING_SNAPSHOT.getText());
		}
		if (!isMarketDataIncrementalRefresh(marketDataIncrementalRefresh)){
			throw new IllegalArgumentException(Messages.FIX_MD_MERGE_INVALID_INCOMING_INCREMENTAL.getText());
		}

		HashMap<Character, Group> consolidatingSet = new HashMap<Character, Group>();

		addGroupsToMap(marketDataSnapshotFullRefresh, factory, consolidatingSet);
		addGroupsToMap(marketDataIncrementalRefresh, factory, consolidatingSet);
		marketDataSnapshotFullRefresh.removeGroup(NoMDEntries.FIELD);
		for (Group aGroup : consolidatingSet.values()) {
			Group group = factory.createGroup(MsgType.MARKET_DATA_SNAPSHOT_FULL_REFRESH, NoMDEntries.FIELD);
			group.setFields(aGroup);
			marketDataSnapshotFullRefresh.addGroup(group);
		}
	}

	private static void addGroupsToMap(Message marketDataMessage, FIXMessageFactory factory, HashMap<Character, Group> consolidatingSet){
		try {
			int noMDEntries = marketDataMessage.getInt(NoMDEntries.FIELD);
			String msgType = marketDataMessage.getHeader().getString(MsgType.FIELD);
			for (int i = 1; i <= noMDEntries; i++){
				Group group = factory.createGroup(msgType, NoMDEntries.FIELD);
				try {
					marketDataMessage.getGroup(i, group);
					consolidatingSet.put(group.getChar(MDEntryType.FIELD), group);
				} catch (FieldNotFound e) {
					//just continue
				}
			}
		} catch (FieldNotFound e) {
			// ignore
		}

	}
	/**
	 * package name for the quickfix fields
	 */
	private static final String QUICKFIX_PACKAGE = "quickfix.field."; //$NON-NLS-1$
	/**
	 * Create a <code>FIX</code> field of the given name.
	 *
	 * @param inFieldName a <code>String</code> value to containing the name of the field to create
	 * @return a <code>Field&lt;?&gt;</code> value
	 * @throws NullPointerException if <code>inFieldName</code> is null
	 * @throws CoreException if the value cannot be converted to a field
	 */
	public static Field<?> getQuickFixFieldFromName(String inFieldName)
	    throws CoreException
	{
	    if(inFieldName == null) {
	        throw new NullPointerException();
	    }
	    Throwable error = null;
	    try {
	        // try the easy case first: the given field might be one of the pre-packaged quickfix fields
	        return (Field<?>)Class.forName(QUICKFIX_PACKAGE + inFieldName).newInstance();
	    } catch(ClassNotFoundException cnfe) {
	        // if this exception is thrown, that means that the field does not correspond to a pre-packaged quickfix field
	        // see if we can create a custom field - this means that the field name has to be parseable as an int
	        try {
	            int fieldInt = Integer.parseInt(inFieldName);
	            return new CustomField<Integer>(fieldInt,
	                                            null);
	        } catch (Throwable t) {
	            error = t;
	        }
	    } catch (Throwable t) {
	        error = t;
	    }
	    // bah, can't create a field with the stuff we're given
	    throw new CoreException(error,
	                            new I18NBoundMessage1P(CANNOT_CREATE_FIX_FIELD,
	                                                   inFieldName));
	}
    /**
     * Converts the supplied fix message to a pretty string that can be logged.
     * The returned string prints the human readable representations of field
     * tags and enumeration type field values to make them easily readable.
     * <p>
     * This method ignores any repeating groups in the message.
     *
     * @param msg the FIX Message.
     * @param inDict the data dictionary for the message
     *
     * @return the string form of the message.
     */
    public static String toPrettyString(Message msg, FIXDataDictionary inDict) {
        HashMap<String, String> fields = fieldsToMap(msg, inDict);
        fields.put("HEADER", fieldsToMap(msg.getHeader(),  //$NON-NLS-1$
                inDict).toString());
        fields.put("TRAILER", fieldsToMap(msg.getTrailer(),  //$NON-NLS-1$
                inDict).toString());
        return fields.toString();
    }

    /**
     * Converts the supplied FieldMap to a map with human readable field
     * names (based on the supplied dictionary) as keys and field values
     * as string values.
     * <p>
     * This method ignores any repeating groups present in the fieldMap.
     *
     * @param inMap The FIX FieldMap.
     * @param inDict The FIX data dictionary.
     *
     * @return The map containing supplied fieldMap's keys & values.
     */
    private static HashMap<String, String> fieldsToMap(FieldMap inMap,
                                                      FIXDataDictionary inDict) {
        HashMap<String, String> fields = new HashMap<String, String>();
        Iterator<Field<?>> iterator = inMap.iterator();
        while(iterator.hasNext()) {
            Field<?> f = iterator.next();
            String value;
            if(f instanceof StringField) {
                value = ((StringField)f).getValue();
                if (inDict != null) {
                    String humanValue = inDict.getHumanFieldValue(f.getTag(),value);
                    if(humanValue != null) {
                        value = new StringBuilder().append(humanValue).
                                append("(").append(value).  //$NON-NLS-1$
                                append(")").toString();  //$NON-NLS-1$
                    }
                }
            } else {
                value = String.valueOf(f.getObject());
            }
            String name = null;
            if (inDict != null) {
                name = inDict.getHumanFieldName(f.getTag());
            }
            if(name == null) {
                name = String.valueOf(f.getTag());
            } else {
                name = new StringBuilder().append(name).
                        append("(").append(f.getTag()).  //$NON-NLS-1$
                        append(")").toString();  //$NON-NLS-1$
            }
            fields.put(name,value);
        }
        return fields;
    }
}
