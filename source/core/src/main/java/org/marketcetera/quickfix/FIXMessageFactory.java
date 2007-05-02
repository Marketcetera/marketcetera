package org.marketcetera.quickfix;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.MSymbol;
import org.marketcetera.quickfix.messagefactory.FIXMessageAugmentor;

import quickfix.FieldNotFound;
import quickfix.Group;
import quickfix.Message;
import quickfix.MessageFactory;
import quickfix.StringField;
import quickfix.field.Account;
import quickfix.field.AvgPx;
import quickfix.field.ClOrdID;
import quickfix.field.CumQty;
import quickfix.field.CxlRejReason;
import quickfix.field.CxlRejResponseTo;
import quickfix.field.ExecID;
import quickfix.field.HandlInst;
import quickfix.field.LastPx;
import quickfix.field.LastShares;
import quickfix.field.MDEntryType;
import quickfix.field.MDReqID;
import quickfix.field.MarketDepth;
import quickfix.field.MsgType;
import quickfix.field.NoMDEntryTypes;
import quickfix.field.NoRelatedSym;
import quickfix.field.OrdRejReason;
import quickfix.field.OrdStatus;
import quickfix.field.OrdType;
import quickfix.field.OrderID;
import quickfix.field.OrderQty;
import quickfix.field.OrigClOrdID;
import quickfix.field.Price;
import quickfix.field.SendingTime;
import quickfix.field.Side;
import quickfix.field.SubscriptionRequestType;
import quickfix.field.Symbol;
import quickfix.field.Text;
import quickfix.field.TimeInForce;
import quickfix.field.TransactTime;

/**
 * Factory class that creates a particular beginString of the FIX message
 * based on the beginString specified.
 * Uses an instance of the {@link FIXMessageAugmentor} to add version-specific
 * fields to each newly created message.
 *
 * @author toli
 * @version $Id$
 */
@ClassVersion("$Id$")
public class FIXMessageFactory {

    private MessageFactory msgFactory;
    private FIXMessageAugmentor msgAugmentor;
    private String beginString;

    public FIXMessageFactory(String beginString, MessageFactory inFactory, FIXMessageAugmentor augmentor) {
        this.beginString = beginString;
        msgFactory = inFactory;
        msgAugmentor = augmentor;
    }


    /**
     * Creates a message representing an ExecutionReport (type {@link MsgType#ORDER_CANCEL_REJECT}
     * @return  appropriately versioned message object
     */
    public Message newOrderCancelReject()
    {
        Message msg = msgFactory.create(beginString, MsgType.ORDER_CANCEL_REJECT);
        addTransactionTimeIfNeeded(msg);
        return msg;
    }

    public Message newCancelReplaceShares(String orderID, String origOrderID, BigDecimal quantity) 
    {
        Message aMessage = msgFactory.create(beginString, MsgType.ORDER_CANCEL_REPLACE_REQUEST);
        addTransactionTimeIfNeeded(aMessage);
        aMessage.setField(new ClOrdID(orderID));
        aMessage.setField(new OrigClOrdID(origOrderID));
        aMessage.setField(new StringField(OrderQty.FIELD, quantity.toPlainString()));
        aMessage.setField(new HandlInst(HandlInst.MANUAL_ORDER));
        return aMessage;
    }

    public Message newCancelReplacePrice(
            String orderID,
            String origOrderID,
            BigDecimal price
    ) {
        Message aMessage = msgFactory.create(beginString, MsgType.ORDER_CANCEL_REPLACE_REQUEST);
        addTransactionTimeIfNeeded(aMessage);
        aMessage.setField(new ClOrdID(orderID));
        aMessage.setField(new OrigClOrdID(origOrderID));
        aMessage.setField(new StringField(Price.FIELD, price.toPlainString()));
        aMessage.setField(new HandlInst(HandlInst.MANUAL_ORDER));
        return aMessage;
    }
    
    public Message newCancelFromMessage(Message oldMessage) throws FieldNotFound {
    	return newCancelHelper(MsgType.ORDER_CANCEL_REQUEST, oldMessage);
    }

	public Message newCancelReplaceFromMessage(Message oldMessage) throws FieldNotFound {
    	Message cancelMessage = newCancelHelper(MsgType.ORDER_CANCEL_REPLACE_REQUEST, oldMessage);
		if (oldMessage.isSetField(Price.FIELD)){
			cancelMessage.setField(oldMessage.getField(new Price()));
		}
        cancelMessage.setField(new HandlInst(HandlInst.MANUAL_ORDER));
		return cancelMessage;
	}
	
	public Message newCancelHelper(String msgType, Message oldMessage) throws FieldNotFound {
        Message cancelMessage = msgFactory.create(beginString, msgType);
		cancelMessage.setField(new OrigClOrdID(oldMessage.getString(ClOrdID.FIELD)));
		FIXMessageUtil.fillFieldsFromExistingMessage(cancelMessage, oldMessage);
		if (oldMessage.isSetField(OrderQty.FIELD)){
			cancelMessage.setField(oldMessage.getField(new OrderQty()));
		}
        addTransactionTimeIfNeeded(cancelMessage);
        cancelMessage.getHeader().setField(new SendingTime());
		return cancelMessage;

	}

    private final int TOP_OF_BOOK_DEPTH = 1;

    /** Creates a new MarketDataRequest for the specified symbols.
     * Setting the incoming symbols array to empty results in a "get all" request
     * @param reqID request id to assign to this
     * @param symbols   List of symbols, or an empty list to get all available
     * @return Message corresponding to the market data request
     */
    public Message newMarketDataRequest(String reqID, List<MSymbol> symbols) {
        Message request = msgFactory.create(beginString, MsgType.MARKET_DATA_REQUEST);
        request.setField(new MarketDepth(TOP_OF_BOOK_DEPTH));
        request.setField(new MDReqID(reqID));
        request.setChar(SubscriptionRequestType.FIELD, SubscriptionRequestType.SNAPSHOT);
        request.setInt(NoMDEntryTypes.FIELD, 2);
        Group entryTypeGroup =  msgFactory.create(beginString, MsgType.MARKET_DATA_REQUEST, NoMDEntryTypes.FIELD);
        entryTypeGroup.setField(new MDEntryType(MDEntryType.BID));
        request.addGroup(entryTypeGroup);
        entryTypeGroup.setField(new MDEntryType(MDEntryType.OFFER));
        request.addGroup(entryTypeGroup);

        request.setInt(NoRelatedSym.FIELD, symbols.size());
        for (MSymbol oneSymbol : symbols) {
            Group symbolGroup =  msgFactory.create(beginString, MsgType.MARKET_DATA_REQUEST, NoRelatedSym.FIELD);
            symbolGroup.setField(new Symbol(oneSymbol.getFullSymbol()));
            request.addGroup(symbolGroup);
        }
        return request;
    }
    public Message newLimitOrder(
            String clOrderID,
            char side,
            BigDecimal quantity,
            MSymbol symbol,
            BigDecimal price,
            char timeInForce,
            String account
    ) {

        Message newMessage = newOrderHelper(clOrderID, side, quantity, symbol, timeInForce, account);
        newMessage.setField(new OrdType(OrdType.LIMIT));
        newMessage.setField(new StringField(Price.FIELD, price.toString()));

        return newMessage;
    }

    public Message newMarketOrder(
            String clOrderID,
            char side,
            BigDecimal quantity,
            MSymbol symbol,
            char timeInForce,
            String account
    ) {
        Message newMessage = newOrderHelper(clOrderID, side, quantity, symbol, timeInForce, account);
        newMessage.setField(new OrdType(OrdType.MARKET));
        return newMessage;
    }

    /**
     * Creates a new FIX order
     *
     * @param clOrderID     Internally generated clOrderID that will become the {@link ClOrdID} that
     *                    uniquely identifies this orderlater
     * @param side        Buy/Sell side
     * @param quantity    # of shares being bought/sold
     * @param symbol      Stock symbol
     * @param timeInForce How long the order is in effect
     * @param account     Account ID
     * @return Message representing this new order
     */
    public Message newOrderHelper(String clOrderID, char side, BigDecimal quantity, MSymbol symbol,
                                  char timeInForce, String account) {
        Message aMessage = msgFactory.create(beginString, MsgType.ORDER_SINGLE);
        aMessage.setField(new ClOrdID(clOrderID));
        aMessage.setField(new HandlInst(HandlInst.MANUAL_ORDER));
        aMessage.setField(new Symbol(symbol.getFullSymbol()));
        aMessage.setField(new Side(side));

        aMessage.setField(new StringField(OrderQty.FIELD, quantity.toPlainString()));
        aMessage.setField(new TimeInForce(timeInForce));
        if (account != null) {
            aMessage.setField(new Account(account));
        }
        addTransactionTimeIfNeeded(aMessage);
        msgAugmentor.newOrderSingleAugment(aMessage);
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
    public Message newCancel(
            String clOrderId,
            String origClOrderID,
            char side,
            BigDecimal quantity,
            MSymbol symbol,
            String counterpartyOrderID
    ) {
        Message aMessage = msgFactory.create(beginString,MsgType.ORDER_CANCEL_REQUEST);

        addTransactionTimeIfNeeded(aMessage);
        aMessage.setField(new ClOrdID(clOrderId));
        aMessage.setField(new OrigClOrdID(origClOrderID));
        aMessage.setField(new Side(side));
        aMessage.setField(new Symbol(symbol.getFullSymbol()));
        aMessage.setField(new StringField(OrderQty.FIELD, quantity.toPlainString()));
        if (counterpartyOrderID != null) {
            aMessage.setField(new OrderID(counterpartyOrderID));
        }
        msgAugmentor.cancelRequestAugment(aMessage);
        return aMessage;
    }

    /** Incoming price may be null for MARKET orders
     * @param inAccount Account name of the institution that's sending the order. may be null
     * @param orderQty  Original order qty
     * @param orderPrice    Original order price
     * */
    public Message newExecutionReport(
            String orderID,
            String clOrderID,
            String execID,
            char ordStatus,
            char side,
            BigDecimal orderQty,
            BigDecimal orderPrice,
            BigDecimal lastShares,
            BigDecimal lastPrice,
            BigDecimal cumQty,
            BigDecimal avgPrice,
            MSymbol symbol,
            String inAccount) throws FieldNotFound {
        Message aMessage = msgFactory.create(beginString, MsgType.EXECUTION_REPORT);
        addTransactionTimeIfNeeded(aMessage);
        if (orderID != null) aMessage.setField(new OrderID(orderID));
        aMessage.setField(new ClOrdID(clOrderID));
        aMessage.setField(new ExecID(execID));
        aMessage.setField(new OrdStatus(ordStatus));
        aMessage.setField(new Side(side));
        aMessage.setField(new StringField(OrderQty.FIELD, orderQty.toPlainString()));
        if(orderPrice != null) {
            aMessage.setField(new StringField(Price.FIELD, orderPrice.toPlainString()));
        }
        if (lastShares != null) aMessage.setField(new StringField(LastShares.FIELD, lastShares.toPlainString()));
        if (lastPrice != null) aMessage.setField(new StringField(LastPx.FIELD, lastPrice.toPlainString()));
        aMessage.setField(new StringField(CumQty.FIELD, cumQty.toPlainString()));
        aMessage.setField(new StringField(AvgPx.FIELD, avgPrice.toPlainString()));
        aMessage.setField(new Symbol(symbol.getFullSymbol()));
        if(inAccount != null) {
            aMessage.setField(new Account(inAccount));
        }
        msgAugmentor.executionReportAugment(aMessage);
        return aMessage;

    }

    /**
     * Creates a new ExecutionReport that with a {@link OrdStatus#REJECTED} type
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
    public Message newRejectExecutionReport(
            String orderID,
            String clOrderID,
            String execID,
            char side,
            BigDecimal orderQty,
            BigDecimal cumQty,
            BigDecimal avgPrice,
            MSymbol symbol,
            OrdRejReason rejReason,
            String inAccount
    ) throws FieldNotFound {
        Message execReport = newExecutionReport(orderID, clOrderID, execID,
                OrdStatus.REJECTED, side, orderQty, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                cumQty, avgPrice, symbol, inAccount);
        addTransactionTimeIfNeeded(execReport);
        execReport.setField(rejReason);
        return execReport;
    }

    /** Creates a {@link MsgType#ORDER_CANCEL_REJECT} message
     * @param rejectReasonText  Text explanation for why reject is sent
     */
    public Message newOrderCancelReject(OrderID orderID, ClOrdID clOrdID, OrigClOrdID origClOrdID,
                                         String rejectReasonText, CxlRejReason cxlRejReason)
    {
        Message reject = newOrderCancelReject();
        reject.setField(orderID);
        reject.setField(clOrdID);
        reject.setField(origClOrdID);
        reject.setField(new OrdStatus(OrdStatus.REJECTED));
        reject.setField(new CxlRejResponseTo(CxlRejResponseTo.ORDER_CANCEL_REQUEST));
        reject.setString(Text.FIELD, rejectReasonText);
        if(cxlRejReason!=null) {
            reject.setField(cxlRejReason);
        }

        return reject;
    }

    /** Creates a new order message and poopulates it with current {@link TransactTime}
     * @return  new order single
     */
    public Message createNewMessage() {
        Message msg =  msgFactory.create(beginString, MsgType.ORDER_SINGLE);
        addTransactionTimeIfNeeded(msg);
        return msg;
    }

    /** Creates a group based on the specified container message and group id */
    public Group createGroup(String msgType, int groupID)
    {
        return msgFactory.create(beginString, msgType, groupID);
    }

    /** Creates a message baed on the specified message type */
    public Message createMessage(String msgType)
    {
        Message msg = msgFactory.create(beginString, msgType);
        addTransactionTimeIfNeeded(msg);
        return msg;
    }

    public String getBeginString()
    {
        return beginString;
    }

    /** Returns the underlying Quickfix/J {@link MessageFactory} that is used
     * to create all messages.
     */
    public MessageFactory getUnderlyingMessageFactory()
    {
        return msgFactory;
    }

    public FIXMessageAugmentor getMsgAugmentor() {
        return msgAugmentor;
    }

    /** Only add the transaction time if it's necessary for this message */
    protected void addTransactionTimeIfNeeded(Message msg)
    {
        if(msgAugmentor.needsTransactTime(msg)) {
            msg.setField(new TransactTime(new Date()));
        }
    }
}
