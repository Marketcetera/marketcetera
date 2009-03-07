package org.marketcetera.quickfix;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.quickfix.messagefactory.FIXMessageAugmentor;
import org.marketcetera.trade.MSymbol;

import quickfix.FieldNotFound;
import quickfix.Group;
import quickfix.Message;
import quickfix.MessageFactory;
import quickfix.SessionNotFound;
import quickfix.StringField;
import quickfix.field.Account;
import quickfix.field.AvgPx;
import quickfix.field.BeginSeqNo;
import quickfix.field.BusinessRejectReason;
import quickfix.field.ClOrdID;
import quickfix.field.CumQty;
import quickfix.field.CxlRejReason;
import quickfix.field.CxlRejResponseTo;
import quickfix.field.EndSeqNo;
import quickfix.field.ExecID;
import quickfix.field.HandlInst;
import quickfix.field.LastPx;
import quickfix.field.LastShares;
import quickfix.field.MDEntryType;
import quickfix.field.MDReqID;
import quickfix.field.MarketDepth;
import quickfix.field.MsgSeqNum;
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
import quickfix.field.RefMsgType;
import quickfix.field.RefSeqNum;
import quickfix.field.SecurityExchange;
import quickfix.field.SecurityListRequestType;
import quickfix.field.SecurityReqID;
import quickfix.field.SenderCompID;
import quickfix.field.SendingTime;
import quickfix.field.SessionRejectReason;
import quickfix.field.Side;
import quickfix.field.SubscriptionRequestType;
import quickfix.field.Symbol;
import quickfix.field.TargetCompID;
import quickfix.field.Text;
import quickfix.field.TimeInForce;
import quickfix.field.TransactTime;
import quickfix.field.SecurityType;

/**
 * Factory class that creates a particular beginString of the FIX message
 * based on the beginString specified.
 * Uses an instance of the {@link FIXMessageAugmentor} to add version-specific
 * fields to each newly created message.
 *
 * @author toli
 * @version $Id$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class FIXMessageFactory {

    private MessageFactory msgFactory;
    private FIXMessageAugmentor msgAugmentor;
    private String beginString;
    /*package */static final char SOH_REPLACE_CHAR = '|';
    private static final char SOH_CHAR = '\001';

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

    public Message newCancelReplaceShares(String orderID, String origOrderID, BigDecimal quantity) {
        Message aMessage = msgFactory.create(beginString, MsgType.ORDER_CANCEL_REPLACE_REQUEST);
        addTransactionTimeIfNeeded(aMessage);
        aMessage.setField(new ClOrdID(orderID));
        aMessage.setField(new OrigClOrdID(origOrderID));
        aMessage.setField(new OrderQty(quantity));
        addHandlingInst(aMessage);
        return aMessage;
    }

    protected void addHandlingInst(Message inMessage) {
        inMessage.setField(new HandlInst(HandlInst.AUTOMATED_EXECUTION_ORDER_PRIVATE));
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
        aMessage.setField(new Price(price));
        addHandlingInst(aMessage);
        return aMessage;
    }
    
    public Message newCancelFromMessage(Message oldMessage) throws FieldNotFound {
    	return newCancelHelper(MsgType.ORDER_CANCEL_REQUEST, oldMessage, false);
    }

	public Message newCancelReplaceFromMessage(Message oldMessage) throws FieldNotFound {
    	Message cancelMessage = newCancelHelper(MsgType.ORDER_CANCEL_REPLACE_REQUEST, oldMessage, false);
		if (oldMessage.isSetField(Price.FIELD)){
			cancelMessage.setField(oldMessage.getField(new Price()));
		}
        addHandlingInst(cancelMessage);
        return cancelMessage;
	}
	
	public Message newCancelHelper(String msgType, Message oldMessage, boolean onlyCopyRequiredFields) throws FieldNotFound {
        Message cancelMessage = msgFactory.create(beginString, msgType);
		cancelMessage.setField(new OrigClOrdID(oldMessage.getString(ClOrdID.FIELD)));
        fillFieldsFromExistingMessage(oldMessage, onlyCopyRequiredFields, cancelMessage);
        if (oldMessage.isSetField(OrderQty.FIELD)){
			cancelMessage.setField(oldMessage.getField(new OrderQty()));
		}
        addTransactionTimeIfNeeded(cancelMessage);
        addSendingTime(cancelMessage);
        return cancelMessage;

	}

    protected void addSendingTime(Message inCancelMessage) {
        inCancelMessage.getHeader().setField(new SendingTime(new Date())); //non-i18n
    }

    protected void fillFieldsFromExistingMessage(Message oldMessage,
                                                 boolean onlyCopyRequiredFields,
                                                 Message inCancelMessage) {
        FIXMessageUtil.fillFieldsFromExistingMessage(inCancelMessage,
                oldMessage, onlyCopyRequiredFields);
    }

    private final int TOP_OF_BOOK_DEPTH = 1;
    /**
     * Returns a Market Data Request for the given symbols from the given exchange.
     *
     * @param reqID a <code>String</code> value containing the identifier to assign to the message
     * @param symbols a <code>List&lt;MSymbol&gt;</code> value containing the symbols for which to request data
     * @param inExchange a <code>String</code> value containing the exchange from which to request data or <code>null</code> to not specify an exchange
     * @return a <code>Message</code> value
     */
    public Message newMarketDataRequest(String reqID,
                                        List<MSymbol> symbols,
                                        String inExchange)
    {
        Message request = msgFactory.create(beginString, MsgType.MARKET_DATA_REQUEST);
        request.setField(new MarketDepth(TOP_OF_BOOK_DEPTH));
        request.setField(new MDReqID(reqID));
        request.setChar(SubscriptionRequestType.FIELD, SubscriptionRequestType.SNAPSHOT);
        Group entryTypeGroup =  msgFactory.create(beginString, MsgType.MARKET_DATA_REQUEST, NoMDEntryTypes.FIELD);
        entryTypeGroup.setField(new MDEntryType(MDEntryType.BID));
        request.addGroup(entryTypeGroup);
        entryTypeGroup.setField(new MDEntryType(MDEntryType.OFFER));
        request.addGroup(entryTypeGroup);

        int numSymbols = symbols.size();
        if (numSymbols == 0){
            request.setInt(NoRelatedSym.FIELD, numSymbols);
        }
        for (MSymbol oneSymbol : symbols) {
            if(oneSymbol != null) {
                Group symbolGroup =  msgFactory.create(beginString, MsgType.MARKET_DATA_REQUEST, NoRelatedSym.FIELD);
                symbolGroup.setField(new Symbol(oneSymbol.getFullSymbol()));
                if(inExchange != null &&
                   !inExchange.isEmpty()) {
                    symbolGroup.setField(new SecurityExchange(inExchange));
                }
                request.addGroup(symbolGroup);
            }
        }
        return request;
    }
    /** Creates a new MarketDataRequest for the specified symbols.
     * Setting the incoming symbols array to empty results in a "get all" request
     * @param reqID request id to assign to this
     * @param symbols   List of symbols, or an empty list to get all available
     * @return Message corresponding to the market data request
     */
    public Message newMarketDataRequest(String reqID, List<MSymbol> symbols) {
        return newMarketDataRequest(reqID,
                                    symbols,
                                    null);
    }
    /**
     * Generates a <code>Security List Request</code> FIX message.
     *
     * @param inReqID a <code>String</code> value containing a unique request ID
     * @return a <code>Message</code> value
     */
    public Message newSecurityListRequest(String inReqID)
    {        
        FIXVersion thisVersion = FIXVersion.getFIXVersion(beginString);
        if(thisVersion.equals(FIXVersion.FIX43) ||
           thisVersion.equals(FIXVersion.FIX44)) {
            Message request = msgFactory.create(beginString, 
                                                MsgType.SECURITY_LIST_REQUEST);
            request.setField(new SecurityReqID(inReqID));
            request.setField(new SecurityListRequestType(SecurityListRequestType.SYMBOL));            
            return request;
        }
        throw new IllegalStateException();
    }
    /**
     * Generates a <code>Derivative Security List Request</code> FIX message.
     *
     * @param inReqID a <code>String</code> value containing a unique request ID
     * @return a <code>Message</code> value
     */
    public Message newDerivativeSecurityListRequest(String inReqID)
    {
        FIXVersion thisVersion = FIXVersion.getFIXVersion(beginString);
        if(thisVersion.equals(FIXVersion.FIX43) ||
           thisVersion.equals(FIXVersion.FIX44)) {
            Message request = msgFactory.create(beginString, 
                                                MsgType.DERIVATIVE_SECURITY_LIST_REQUEST);
            request.setField(new SecurityReqID(inReqID));
            request.setField(new SecurityListRequestType(SecurityListRequestType.SYMBOL));            
            return request;
        }
        throw new IllegalStateException();
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
        newMessage.setField(new Price(price));

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
    private Message newOrderHelper(String clOrderID, char side, BigDecimal quantity, MSymbol symbol,
                                  char timeInForce, String account) {
        Message aMessage = msgFactory.create(beginString, MsgType.ORDER_SINGLE);
        aMessage.setField(new ClOrdID(clOrderID));
        addHandlingInst(aMessage);
        aMessage.setField(new Symbol(symbol.getFullSymbol()));
        if(symbol.getSecurityType() != null &&
                org.marketcetera.trade.SecurityType.Unknown != symbol.getSecurityType()) {
            aMessage.setField(new SecurityType(symbol.getSecurityType().getFIXValue()));
        }
        aMessage.setField(new Side(side));

        aMessage.setField(new OrderQty(quantity));
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
        if(symbol.getSecurityType() != null &&
                org.marketcetera.trade.SecurityType.Unknown != symbol.getSecurityType()) {
            aMessage.setField(new SecurityType(symbol.getSecurityType().getFIXValue()));
        }
        aMessage.setField(new OrderQty(quantity));
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
        aMessage.setField(new OrderQty(orderQty));
        if(orderPrice != null) {
            aMessage.setField(new Price(orderPrice));
        }
        if (lastShares != null) aMessage.setField(new LastShares(lastShares));
        if (lastPrice != null) aMessage.setField(new LastPx(lastPrice));
        aMessage.setField(new CumQty(cumQty));
        aMessage.setField(new AvgPx(avgPrice));
        aMessage.setField(new Symbol(symbol.getFullSymbol()));
        if(symbol.getSecurityType() != null &&
                org.marketcetera.trade.SecurityType.Unknown != symbol.getSecurityType()) {
            aMessage.setField(new SecurityType(symbol.getSecurityType().getFIXValue()));
        }
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

    /** Creates a new BusinessMessageReject message based on passed-in parameters */ 
    public Message newBusinessMessageReject(String refMsgType, int rejReason, String textReason) {
        Message bmReject = createMessage(MsgType.BUSINESS_MESSAGE_REJECT);
        bmReject.setField(new RefMsgType(refMsgType));
        bmReject.setField(new BusinessRejectReason(rejReason));
        bmReject.setField(new Text(textReason));
        return bmReject;
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
        reject.setString(Text.FIELD, rejectReasonText.replace(SOH_CHAR, SOH_REPLACE_CHAR));
        if(cxlRejReason!=null) {
            reject.setField(cxlRejReason);
        }

        return reject;
    }

    /** Creates a new order message and poopulates it with current {@link TransactTime}
     * @return  new order single
     */
    public Message newBasicOrder() {
        Message msg =  msgFactory.create(beginString, MsgType.ORDER_SINGLE);
        addHandlingInst(msg);
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
    public void addTransactionTimeIfNeeded(Message msg)
    {
        if(msgAugmentor.needsTransactTime(msg)) {
            msg.setField(new TransactTime(new Date())); //non-i18n
        }
    }


	public Message newResendRequest(BigInteger beginSeqNo, BigInteger endSeqNo) {
		Message rr = msgFactory.create(beginString, MsgType.RESEND_REQUEST);
		if (beginSeqNo == null){
			// from 0
			rr.setField(new BeginSeqNo(0));
		} else {
			rr.setField(new StringField(BeginSeqNo.FIELD,beginSeqNo.toString()));//i18n_number
		}
		if (endSeqNo == null){
			// to infinity 
			rr.setField(new EndSeqNo(0));
		} else {
			rr.setField(new StringField(EndSeqNo.FIELD,endSeqNo.toString()));//i18n_number
		}
		return rr;
	}

    /** Creates a new session-level reject with the given reason to return
     * to the sender of the incomingMsg
     * @param incomingMsg   Message that generated this session-level reject
     * @param rejectReason  Reason for reject
     * @return Session-level reject message to send out
     * @throws FieldNotFound
     * @throws SessionNotFound
     */
    public Message createSessionReject(Message incomingMsg, int rejectReason) throws FieldNotFound,
            SessionNotFound {
        Message reply = createMessage(MsgType.REJECT);
        reverseRoute(incomingMsg, reply);
        String refSeqNum = incomingMsg.getHeader().getString(MsgSeqNum.FIELD);
        reply.setString(RefSeqNum.FIELD, refSeqNum);
        reply.setString(RefMsgType.FIELD, incomingMsg.getHeader().getString(MsgType.FIELD));
        reply.setInt(SessionRejectReason.FIELD, rejectReason);
        return reply;
    }

    /** Reverses the sender/target compIDs from reply and sets them in the outgoing outgoingMsg */
    public void reverseRoute(Message outgoingMsg, Message reply) throws FieldNotFound {
        reply.getHeader().setString(SenderCompID.FIELD,
                outgoingMsg.getHeader().getString(TargetCompID.FIELD));
        reply.getHeader().setString(TargetCompID.FIELD,
                outgoingMsg.getHeader().getString(SenderCompID.FIELD));
    }



    public Message newCancelReplaceEmpty()
    {
        Message msg=msgFactory.create
            (beginString,MsgType.ORDER_CANCEL_REPLACE_REQUEST);
        addTransactionTimeIfNeeded(msg);
        addHandlingInst(msg);
        return msg;
    }

    public Message newCancelEmpty()
    {
        Message msg=msgFactory.create
            (beginString,MsgType.ORDER_CANCEL_REQUEST);
        addTransactionTimeIfNeeded(msg);
        return msg;
    }

    public Message newOrderEmpty()
    {
        Message msg=msgFactory.create
            (beginString,MsgType.ORDER_SINGLE);
        addHandlingInst(msg);
        addTransactionTimeIfNeeded(msg);
        return msg;
    }

    public Message newOrderCancelRejectEmpty()
    {
        Message msg = msgFactory.create
            (beginString,MsgType.ORDER_CANCEL_REJECT);
        addTransactionTimeIfNeeded(msg);
        return msg;
    }

    public Message newExecutionReportEmpty()
    {
        Message msg = msgFactory.create
            (beginString,MsgType.EXECUTION_REPORT);
        addTransactionTimeIfNeeded(msg);
        return msg;
    }
}
