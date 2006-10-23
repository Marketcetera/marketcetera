package org.marketcetera.quickfix;

import org.marketcetera.core.*;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.StringField;
import quickfix.DataDictionary;
import quickfix.field.*;
import quickfix.fix42.OrderCancelReplaceRequest;
import quickfix.fix42.MarketDataRequest;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Collection of utilities to create new JMSFIX messages
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
        try {
            MsgType msgTypeField = new MsgType();
            fixMessage.getHeader().getField(msgTypeField);
            return msgType.equals(msgTypeField.getValue());
        } catch (Exception exception) {
            return false;
        }
    }

    /** Currently, we are hardcoding version 4.2
     * this is a static function that needs to be used *everywhere*
     * in the code where we create a new quickfix message
     * Ultimately we'll have a factory method that knows which version
     * of FIX the app is using and will be able to fill the missing fields
     * if there's not enough data
     * @return  Currently hardcoded to return FIX42 message
     */
    public static Message createNewMessage() {
        Message msg = new quickfix.fix42.Message();
        msg.setField(new TransactTime(new Date()));
        return msg;
    }

    /**
     * Checks to see if the {@link OrdStatus} and the {@link ExecType} fields
     * are equal in the execution report
     *
     * @param jmsMessage
     * @return boolean signifying whehther the {@link OrdStatus} and the
     *         {@link ExecType} fields are the same
     */
    public static boolean isStateChangingExecutionReport(Message jmsMessage) {
        if (isExecutionReport(jmsMessage)) {
            try {
                OrdStatus ordStatus = new OrdStatus();
                jmsMessage.getField(ordStatus);
                ExecType execType = new ExecType();
                jmsMessage.getField(execType);
                return ordStatus.getValue() == execType.getValue();
            } catch (FieldNotFound exception) {
                return false;
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

    public static boolean isReplaceRequest(Message jmsMessage) {
        return msgTypeHelper(jmsMessage, MsgType.ORDER_CANCEL_REPLACE_REQUEST);
    }

    public static boolean isCancelReplaceRequest(Message jmsMessage) {
        return msgTypeHelper(jmsMessage, MsgType.ORDER_CANCEL_REPLACE_REQUEST);
    }

    public static Message newLimitOrder(
            InternalID orderID,
            char side,
            BigDecimal quantity,
            MSymbol symbol,
            BigDecimal price,
            char timeInForce,
            AccountID account
    ) {

        Message newMessage = newOrderHelper(orderID, side, quantity, symbol, timeInForce, account);
        newMessage.setField(new OrdType(OrdType.LIMIT));
        newMessage.setField(new StringField(Price.FIELD, price.toString()));

        return newMessage;
    }

    public static Message newMarketOrder(
            InternalID orderID,
            char side,
            BigDecimal quantity,
            MSymbol symbol,
            char timeInForce,
            AccountID account
    ) {
        Message newMessage = newOrderHelper(orderID, side, quantity, symbol, timeInForce, account);
        newMessage.setField(new OrdType(OrdType.MARKET));
        return newMessage;
    }

    /**
     * Creates a new FIX order
     *
     * @param orderID     Internally generated orderID that will become the {@link ClOrdID} that
     *                    uniquely identifies this orderlater
     * @param side        Buy/Sell side
     * @param quantity    # of shares being bought/sold
     * @param symbol      Stock symbol
     * @param timeInForce How long the order is in effect
     * @param account     Account ID
     * @return Message representing this new order
     */
    public static Message newOrderHelper(
            InternalID orderID,
            char side,
            BigDecimal quantity,
            MSymbol symbol,
            char timeInForce,
            AccountID account) {
        Message aMessage = createNewMessage();
        aMessage.getHeader().setField(new MsgType(MsgType.ORDER_SINGLE));

        aMessage.setField(new ClOrdID(orderID.toString()));
        aMessage.setField(new HandlInst(HandlInst.MANUAL_ORDER));
        aMessage.setField(new Symbol(symbol.getFullSymbol()));
        aMessage.setField(new Side(side));

        aMessage.setField(new StringField(OrderQty.FIELD, quantity.toPlainString()));
        aMessage.setField(new TimeInForce(timeInForce));
        if (account != null) {
            aMessage.setField(new Account(account.toString()));
        }
        return aMessage;
    }

    /**
     * Helps create a cancel order for an existing cancel request
     *
     * @param clOrderId        Newly generated OrderID for this cancel request
     * @param origClOrderID    our original clOrderID of the existing order we are trying to cancel
     * that we gave to it the first time we ack'ed
     * @param side           Buy/Sell side of the initial order
     * @param quantity       Initial quantity
     * @param symbol         Stock symbol of initial order
     * @param counterpartyOrderID The counterpartyOrderID. can be null. this is the ID given to this order
     * by the "counterparty" financial institution
     * @return Message representing the new order
     */
    public static Message newCancel(
            InternalID clOrderId,
            InternalID origClOrderID,
            char side,
            BigDecimal quantity,
            MSymbol symbol,
            String counterpartyOrderID
    ) {
        Message aMessage = createNewMessage();
        aMessage.getHeader().setField(new MsgType(MsgType.ORDER_CANCEL_REQUEST));

        aMessage.setField(new ClOrdID(clOrderId.toString()));
        aMessage.setField(new OrigClOrdID(origClOrderID.toString()));
        aMessage.setField(new Side(side));
        aMessage.setField(new Symbol(symbol.getFullSymbol()));
        aMessage.setField(new StringField(OrderQty.FIELD, quantity.toPlainString()));
        if (counterpartyOrderID != null) {
            aMessage.setField(new OrderID(counterpartyOrderID));
        }
        return aMessage;
    }

    /** Incoming price may be null for MARKET orders
     * @param inAccount Account name of the institution that's sending the order. may be null
     * @param orderQty  Original order qty
     * @param orderPrice    Original order price
     * */
    public static Message newExecutionReport(
            InternalID orderID,
            InternalID clOrderID,
            String execID,
            char execTransType,
            char execType,
            char ordStatus,
            char side,
            BigDecimal orderQty,
            BigDecimal orderPrice,
            BigDecimal lastShares,
            BigDecimal lastPrice,
            BigDecimal leavesQty,
            BigDecimal cumQty,
            BigDecimal avgPrice,
            MSymbol symbol,
            AccountID inAccount) {
        Message aMessage = createNewMessage();

        aMessage.getHeader().setField(new MsgType(MsgType.EXECUTION_REPORT));

        if (orderID != null) aMessage.setField(new OrderID(orderID.toString()));
        aMessage.setField(new ClOrdID(clOrderID.toString()));
        aMessage.setField(new ExecID(execID));
        aMessage.setField(new ExecTransType(execTransType));
        aMessage.setField(new ExecType(execType));
        aMessage.setField(new OrdStatus(ordStatus));
        aMessage.setField(new Side(side));
        aMessage.setField(new StringField(OrderQty.FIELD, orderQty.toPlainString()));
        if(orderPrice != null) {
            aMessage.setField(new StringField(Price.FIELD, orderPrice.toPlainString()));
        }
        if (lastShares != null) aMessage.setField(new StringField(LastShares.FIELD, lastShares.toPlainString()));
        if (lastPrice != null) aMessage.setField(new StringField(LastPx.FIELD, lastPrice.toPlainString()));
        aMessage.setField(new StringField(LeavesQty.FIELD, leavesQty.toPlainString()));
        aMessage.setField(new StringField(CumQty.FIELD, cumQty.toPlainString()));
        aMessage.setField(new StringField(AvgPx.FIELD, avgPrice.toPlainString()));
        aMessage.setField(new Symbol(symbol.getFullSymbol()));
        if(inAccount != null) {
            aMessage.setField(new Account(inAccount.toString()));
        }
        return aMessage;

    }

    /**
     * Creates a new ExecutionReport that with a {@link ExecType.REJECTED} type
     *
     * @param orderID   OrderID for the new report (can be null)
     * @param clOrderID OrderID of the original (client) order that got rejected
     * @param execID    Execution ID for this order (can be null)
     * @param side      {@link Side} of the transaction
     * @param orderQty  Original order quantity
     * @param cumQty    Cumuluative order qty       (can be 0)
     * @param avgPrice  Average price for the order (can be 0)
     * @param symbol    Stock symbol for the order
     * @return A new {@link Message} signifying a reject
     */
    public static Message newRejectExecutionReport(
            InternalID orderID,
            InternalID clOrderID,
            String execID,
            char side,
            BigDecimal orderQty,
            BigDecimal cumQty,
            BigDecimal avgPrice,
            MSymbol symbol,
            OrdRejReason rejReason,
            AccountID inAccount
    ) {
        Message execReport = newExecutionReport(orderID, clOrderID, execID, ExecTransType.NEW, ExecType.REJECTED,
                OrdStatus.REJECTED, side, orderQty, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                BigDecimal.ZERO, cumQty, avgPrice, symbol, inAccount);
        execReport.setField(rejReason);
        return execReport;
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

    public static Message newCancelReplaceShares(
            InternalID orderID,
            InternalID origOrderID,
            BigDecimal quantity
    ) {
        Message aMessage = createNewMessage();
        aMessage.getHeader().setField(new MsgType(MsgType.ORDER_CANCEL_REPLACE_REQUEST));
        aMessage.setField(new ClOrdID(orderID.toString()));
        aMessage.setField(new OrigClOrdID(origOrderID.toString()));
        aMessage.setField(new StringField(OrderQty.FIELD, quantity.toPlainString()));
        aMessage.setField(new HandlInst(HandlInst.MANUAL_ORDER));
        return aMessage;
    }

    public static Message newCancelReplacePrice(
            InternalID orderID,
            InternalID origOrderID,
            BigDecimal price
    ) {
        Message aMessage = createNewMessage();
        aMessage.getHeader().setField(new MsgType(MsgType.ORDER_CANCEL_REPLACE_REQUEST));
        aMessage.setField(new ClOrdID(orderID.toString()));
        aMessage.setField(new OrigClOrdID(origOrderID.toString()));
        aMessage.setField(new StringField(Price.FIELD, price.toPlainString()));
        aMessage.setField(new HandlInst(HandlInst.MANUAL_ORDER));
        return aMessage;
    }

	public static Message newCancelReplaceFromMessage(Message oldMessage) throws FieldNotFound
	{
		Message cancelReplaceMessage = new OrderCancelReplaceRequest();
		cancelReplaceMessage.setField(new OrigClOrdID(oldMessage.getString(ClOrdID.FIELD)));
		fillFieldsFromExistingMessage(cancelReplaceMessage, oldMessage);
		if (oldMessage.getChar(OrdType.FIELD) != OrdType.MARKET && oldMessage.isSetField(Price.FIELD)){
			cancelReplaceMessage.setField(oldMessage.getField(new Price()));
		}
		if (oldMessage.isSetField(OrderQty.FIELD)){
			cancelReplaceMessage.setField(oldMessage.getField(new OrderQty()));
		}
		return cancelReplaceMessage;

	}

    private static final int TOP_OF_BOOK_DEPTH = 1;

    /** Creates a new {@link MarketDataRequest} for the specified symbols.
     * Setting the incoming symbols array to empty results in a "get all" request
     * @param reqID request id to assign to this
     * @param symbols   List of symbols, or an empty list to get all available
     * @return Message corresponding to the market data request
     */
    public static MarketDataRequest newMarketDataRequest(String reqID, List<MSymbol> symbols) {
        MarketDataRequest request = new MarketDataRequest();
        request.setField(new MarketDepth(TOP_OF_BOOK_DEPTH));
        request.setField(new MDReqID(reqID));
        request.setChar(SubscriptionRequestType.FIELD, SubscriptionRequestType.SNAPSHOT);
        request.setInt(NoMDEntryTypes.FIELD, 2);
        MarketDataRequest.NoMDEntryTypes entryTypeGroup =  new MarketDataRequest.NoMDEntryTypes();
        entryTypeGroup.set(new MDEntryType(MDEntryType.BID));
        request.addGroup(entryTypeGroup);
        entryTypeGroup.set(new MDEntryType(MDEntryType.OFFER));
        request.addGroup(entryTypeGroup);

        request.setInt(NoRelatedSym.FIELD, symbols.size());
        for (MSymbol oneSymbol : symbols) {
            MarketDataRequest.NoRelatedSym symbolGroup =  new MarketDataRequest.NoRelatedSym();
            symbolGroup.setField(new Symbol(oneSymbol.getFullSymbol()));
            request.addGroup(symbolGroup);
        }
        return request;
    }
}
