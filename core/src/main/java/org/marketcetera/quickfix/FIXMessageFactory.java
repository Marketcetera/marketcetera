package org.marketcetera.quickfix;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.joda.time.DateTime;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.instruments.InstrumentToMessage;
import org.marketcetera.marketdata.Content;
import org.marketcetera.quickfix.messagefactory.FIXMessageAugmentor;
import org.marketcetera.trade.Instrument;

import com.google.common.collect.Lists;

import quickfix.DataDictionary;
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
import quickfix.field.MDReqID;
import quickfix.field.MsgSeqNum;
import quickfix.field.MsgType;
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
import quickfix.field.SecurityListRequestType;
import quickfix.field.SecurityReqID;
import quickfix.field.SenderCompID;
import quickfix.field.SendingTime;
import quickfix.field.SessionRejectReason;
import quickfix.field.Side;
import quickfix.field.TargetCompID;
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
@ClassVersion("$Id$") //$NON-NLS-1$
public class FIXMessageFactory
{
    private MessageFactory msgFactory;
    private FIXMessageAugmentor msgAugmentor;
    private String beginString;
    /*package */static final char SOH_REPLACE_CHAR = '|';
    private static final char SOH_CHAR = '\001';
    /**
     * Create a new FIXMessageFactory instance.
     *
     * @param inBeginString a <code>String</code> value
     * @param inFactory a <code>MessageFactory</code> value
     * @param inAugmentor a <code>FIXMessageAugmentor</code> value
     */
    public FIXMessageFactory(String inBeginString,
                             MessageFactory inFactory,
                             FIXMessageAugmentor inAugmentor)
    {
        beginString = inBeginString;
        msgFactory = inFactory;
        msgAugmentor = inAugmentor;
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
    /**
     * Create a new cancel replace from the given message.
     *
     * @param inOldMessage a <code>Message</code> value
     * @return a <code>Message</code> value
     * @throws FieldNotFound
     */
    public Message newCancelReplaceFromMessage(Message inOldMessage)
            throws FieldNotFound
    {
        Message cancelMessage = newCancelHelper(MsgType.ORDER_CANCEL_REPLACE_REQUEST,
                                                inOldMessage,
                                                false);
        if(inOldMessage.isSetField(Price.FIELD)) {
            cancelMessage.setField(inOldMessage.getField(new Price()));
        }
        addHandlingInst(cancelMessage);
        return cancelMessage;
	}
    /**
     * Create a new cancel helper from the given inputs.
     *
     * @param inMsgType a <code>String</code> value
     * @param inOldMessage a <code>Message</code> value
     * @param inOnlyCopyRequiredFields a <code>boolean</code> value
     * @return a <code>Message</code> value
     * @throws FieldNotFound
     */
    public Message newCancelHelper(String inMsgType,
                                   Message inOldMessage,
                                   boolean inOnlyCopyRequiredFields)
            throws FieldNotFound
    {
        Message cancelMessage = msgFactory.create(beginString,
                                                  inMsgType);
        cancelMessage.setField(new OrigClOrdID(inOldMessage.getString(ClOrdID.FIELD)));
        fillFieldsFromExistingMessage(inOldMessage, inOnlyCopyRequiredFields, cancelMessage);
        if (inOldMessage.isSetField(OrderQty.FIELD)){
            cancelMessage.setField(inOldMessage.getField(new OrderQty()));
        }
        addTransactionTimeIfNeeded(cancelMessage);
        addSendingTime(cancelMessage);
        return cancelMessage;
    }

    protected void addSendingTime(Message inCancelMessage) {
        inCancelMessage.getHeader().setField(new SendingTime(new Date()));
    }

    protected void fillFieldsFromExistingMessage(Message oldMessage,
                                                 boolean onlyCopyRequiredFields,
                                                 Message inCancelMessage) {
        FIXMessageUtil.fillFieldsFromExistingMessage(inCancelMessage,
                oldMessage, onlyCopyRequiredFields);
    }
    /**
     * Create a new market data snapshow (35=W) message.
     *
     * @param inRequestId a <code>String</code> value
     * @param inInstrument an <code>Instrument</code> value
     * @return a <code>Message</code> value
     * @throws FieldNotFound if the message could not be built
     */
    public Message newMarketDataSnapshot(String inRequestId,
                                         Instrument inInstrument)
            throws FieldNotFound
    {
        Message snapshot = msgFactory.create(beginString,
                                             MsgType.MARKET_DATA_SNAPSHOT_FULL_REFRESH);
        DataDictionary fixDictionary = FIXMessageUtil.getDataDictionary(snapshot);
        InstrumentToMessage<?> instrumentFunction = InstrumentToMessage.SELECTOR.forInstrument(inInstrument);
        instrumentFunction.set(inInstrument,
                               fixDictionary,
                               quickfix.field.MsgType.ORDER_SINGLE,
                               snapshot);
        // some weirdness for currencies
        snapshot.removeField(quickfix.field.Currency.FIELD);
        snapshot.removeField(quickfix.field.OrdType.FIELD);
        snapshot.setField(new quickfix.field.MDReqID(inRequestId));
        return snapshot;
    }
    /**
     * Create a new market data incremental refresh (35=X) message.
     *
     * @param inRequestId a <code>String</code> value
     */
    public Message newMarketDataIncrementalRefresh(String inRequestId)
    {
        Message request = msgFactory.create(beginString,
                                            MsgType.MARKET_DATA_INCREMENTAL_REFRESH);
        request.setField(new quickfix.field.MDReqID(inRequestId));
        return request;
    }
    /**
     * Create an MDEntry group.
     *
     * @param inMessageFactory a <code>FIXMessageFactory</code> value
     * @param inMdEntryType a <code>char</code> value
     * @return a <code>Group</code> value
     */
    public Group createMdEntryGroup(String inMsgType,
                                    char inMdEntryType)
    {
        Group newGroup = createGroup(inMsgType,
                                     quickfix.field.NoMDEntries.FIELD);
        newGroup.setField(new quickfix.field.MDEntryType(inMdEntryType));
        return newGroup;
    }
    /**
     * Get the MDEntry groups from the given message.
     *
     * @param inMessage a <code>Message</code> value
     * @return a <code>List&lt:Group&gt;</code> value
     * @throws FieldNotFound if the groups could not be extracted
     */
    public List<Group> getMdEntriesFromMessage(Message inMessage)
            throws FieldNotFound
    {
        List<Group> mdEntries = Lists.newArrayList();
        int noMdEntries = inMessage.getInt(quickfix.field.NoMDEntries.FIELD);
        for(int i=1;i<=noMdEntries;i++) {
            Group mdEntryGroup = createGroup(inMessage.getHeader().getString(quickfix.field.MsgType.FIELD),
                                             quickfix.field.NoMDEntries.FIELD);
            mdEntryGroup = inMessage.getGroup(i,
                                              mdEntryGroup);
            mdEntries.add(mdEntryGroup);
        }
        return mdEntries;
    }
    /**
     * Populate the MDEntry given group with the given date time value.
     *
     * @param inGroup a <code>Group</code> value
     * @param inDateTime a <code>DateTime</code> value
     */
    public void populateMdEntryGroupWithDateTime(Group inGroup,
                                                 DateTime inDateTime)
    {
        if(inDateTime == null) {
            return;
        }
        // TODO the time doesn't seem quite right
        inGroup.setField(new quickfix.field.MDEntryDate(inDateTime.minusMillis(inDateTime.getMillisOfDay()).toDate()));
        inGroup.setField(new quickfix.field.MDEntryTime(inDateTime.minusYears(inDateTime.getYear()).minusDays(inDateTime.getDayOfYear()).toDate()));
    }
    /**
     * Create a new market data request that cancels the market data request based on the given original request.
     *
     * @param inOriginalMessage a <code>String</code> value
     * @return a <code>Message</code> value
     * @throws FieldNotFound if the message could not be constructed
     */
    public Message newMarketDataRequestCancel(Message inOriginalMessage)
            throws FieldNotFound
    {
        Message cancelRequest = inOriginalMessage;
        cancelRequest.setField(new quickfix.field.SubscriptionRequestType(quickfix.field.SubscriptionRequestType.DISABLE_PREVIOUS_SNAPSHOT_PLUS_UPDATE_REQUEST));
        return cancelRequest;
    }
    /**
     * Create a new market data request with the given parameters.
     *
     * @param inRequestId a <code>String</code> value
     * @param inInstruments a <code>List&lt;Instrument&gt;</code> value, may be empty for "all instruments"
     * @param inExchange a <code>String</code> value, may be <code>null</code> for "all exchanges"
     * @param inContent a <code>List&lt;Content&gt;</code> value
     * @param inSubscriptionType a <code>char</code> value
     * @return a <code>Message</code> value
     * @throws FieldNotFound if the message could not be constructed
     * @throws IllegalArgumentException if the provided content is contradictory, eg. aggregated depth and unaggregated depth or top of book and bbo10
     */
    public Message newMarketDataRequest(String inRequestId,
                                        List<Instrument> inInstruments,
                                        String inExchange,
                                        List<Content> inContent,
                                        char inSubscriptionType)
            throws FieldNotFound
    {
        // TODO add support for content in non 4.2 if dictionary supports it (imbalance, eg)
        Message request = msgFactory.create(beginString,
                                            MsgType.MARKET_DATA_REQUEST);
        DataDictionary fixDictionary = FIXMessageUtil.getDataDictionary(request);
        request.setField(new MDReqID(inRequestId));
        int contentCount = 0;
        Integer maxDepth = null;
        Boolean aggregatedBook = null;
        if(inContent != null) {
            for(Content content : inContent) {
                switch(content) {
                    case AGGREGATED_DEPTH:
                        contentCount = addMdEntry(request,quickfix.field.MDEntryType.BID,contentCount);
                        contentCount = addMdEntry(request,quickfix.field.MDEntryType.OFFER,contentCount);
                        maxDepth = setMaxDepth(request,
                                               Integer.MAX_VALUE,
                                               maxDepth);
                        aggregatedBook = setAggregatedBook(request,
                                                           true,
                                                           aggregatedBook);
                        break;
                    case BBO10:
                        contentCount = addMdEntry(request,quickfix.field.MDEntryType.BID,contentCount);
                        contentCount = addMdEntry(request,quickfix.field.MDEntryType.OFFER,contentCount);
                        maxDepth = setMaxDepth(request,
                                               10,
                                               maxDepth);
                        aggregatedBook = setAggregatedBook(request,
                                                           true,
                                                           aggregatedBook);
                        break;
                    case OPEN_BOOK:
                    case TOTAL_VIEW:
                    case UNAGGREGATED_DEPTH:
                        contentCount = addMdEntry(request,quickfix.field.MDEntryType.BID,contentCount);
                        contentCount = addMdEntry(request,quickfix.field.MDEntryType.OFFER,contentCount);
                        maxDepth = setMaxDepth(request,
                                               Integer.MAX_VALUE,
                                               maxDepth);
                        aggregatedBook = setAggregatedBook(request,
                                                           false,
                                                           aggregatedBook);
                        break;
                    case LATEST_TICK:
                        contentCount = addMdEntry(request,quickfix.field.MDEntryType.TRADE,contentCount);
                        break;
                    case LEVEL_2:
                        contentCount = addMdEntry(request,quickfix.field.MDEntryType.INDEX_VALUE,contentCount);
                        maxDepth = setMaxDepth(request,
                                               Integer.MAX_VALUE,
                                               maxDepth);
                        aggregatedBook = setAggregatedBook(request,
                                                           false,
                                                           aggregatedBook);
                        break;
                    case MARKET_STAT:
                        contentCount = addMdEntry(request,quickfix.field.MDEntryType.OPENING_PRICE,contentCount);
                        contentCount = addMdEntry(request,quickfix.field.MDEntryType.CLOSING_PRICE,contentCount);
                        if(fixDictionary.isFieldValue(quickfix.field.MDEntryType.FIELD,
                                                      String.valueOf(quickfix.field.MDEntryType.TRADE_VOLUME))) {
                            contentCount = addMdEntry(request,quickfix.field.MDEntryType.TRADE_VOLUME,contentCount);
                        }
                        contentCount = addMdEntry(request,quickfix.field.MDEntryType.TRADING_SESSION_HIGH_PRICE,contentCount);
                        contentCount = addMdEntry(request,quickfix.field.MDEntryType.TRADING_SESSION_LOW_PRICE,contentCount);
                        contentCount = addMdEntry(request,quickfix.field.MDEntryType.TRADING_SESSION_VWAP_PRICE,contentCount);
                        break;
                    case NBBO:
                        contentCount = addMdEntry(request,quickfix.field.MDEntryType.INDEX_VALUE,contentCount);
                        maxDepth = setMaxDepth(request,
                                               TOP_OF_BOOK_DEPTH,
                                               maxDepth);
                        break;
                    case TOP_OF_BOOK:
                        contentCount = addMdEntry(request,quickfix.field.MDEntryType.BID,contentCount);
                        contentCount = addMdEntry(request,quickfix.field.MDEntryType.OFFER,contentCount);
                        maxDepth = setMaxDepth(request,
                                               TOP_OF_BOOK_DEPTH,
                                               maxDepth);
                        break;
                    case IMBALANCE:
                    case DIVIDEND:
                    default:
                        throw new UnsupportedOperationException("Unsupported content: " + content);
                }
            }
        }
        if(!request.isSetField(quickfix.field.MarketDepth.FIELD)) {
            maxDepth = setMaxDepth(request,
                                   1,
                                   maxDepth);
        }
        request.setField(new quickfix.field.NoMDEntryTypes(contentCount));
        request.setChar(quickfix.field.SubscriptionRequestType.FIELD,
                        inSubscriptionType);
        request.setField(new quickfix.field.MDUpdateType(quickfix.field.MDUpdateType.FULL_REFRESH));
        int numSymbols = 0;
        if(inInstruments != null) {
            numSymbols = inInstruments.size();
            if(numSymbols == 0){
                request.setInt(quickfix.field.NoRelatedSym.FIELD,
                               numSymbols);
            }
            for(Instrument instrument : inInstruments) {
                if(instrument != null) {
                    InstrumentToMessage<?> instrumentFunction = InstrumentToMessage.SELECTOR.forInstrument(instrument);
                    Group symbolGroup =  msgFactory.create(beginString,
                                                           MsgType.MARKET_DATA_REQUEST,
                                                           NoRelatedSym.FIELD);
                    instrumentFunction.set(instrument,
                                           fixDictionary,
                                           quickfix.field.MsgType.ORDER_SINGLE,
                                           symbolGroup);
                    // some weirdness for currencies
                    symbolGroup.removeField(quickfix.field.Currency.FIELD);
                    symbolGroup.removeField(quickfix.field.OrdType.FIELD);
                    if(inExchange != null && !inExchange.isEmpty()) {
                        symbolGroup.setField(new quickfix.field.SecurityExchange(inExchange));
                    }
                    request.addGroup(symbolGroup);
                }
            }
        } else {
            request.setInt(quickfix.field.NoRelatedSym.FIELD,
                           0);
        }
        return request;
    }
    /**
     * Returns a Market Data Request for the given symbols from the given exchange.
     *
     * @param inRequestId a <code>String</code> value containing the identifier to assign to the message
     * @param inInstruments a <code>List&lt;Instrument&gt;</code> value containing the symbols for which to request data
     * @param inExchange a <code>String</code> value containing the exchange from which to request data or <code>null</code> to not specify an exchange
     * @return a <code>Message</code> value
     * @throws FieldNotFound if the message could not be constructed
     */
    public Message newMarketDataRequest(String inRequestId,
                                        List<Instrument> inInstruments,
                                        String inExchange)
            throws FieldNotFound
    {
        return newMarketDataRequest(inRequestId,
                                    inInstruments,
                                    inExchange,
                                    Lists.newArrayList(Content.TOP_OF_BOOK,Content.LATEST_TICK),
                                    quickfix.field.SubscriptionRequestType.SNAPSHOT_PLUS_UPDATES);
    }
    /** Creates a new MarketDataRequest for the specified symbols.
     * Setting the incoming symbols array to empty results in a "get all" request
     * @param reqID request id to assign to this
     * @param inInstruments   List of symbols, or an empty list to get all available
     * @return Message corresponding to the market data request
     * @throws FieldNotFound if the message could not be constructed
     */
    public Message newMarketDataRequest(String reqID,
                                        List<Instrument> inInstruments)
            throws FieldNotFound
    {
        return newMarketDataRequest(reqID,
                                    inInstruments,
                                    null);
    }
    /**
     * Create a new market data request reject message.
     *
     * @param inRequestId a <code>String</code> value
     * @param inRejectReason a <code>Character</code> value or <code>null</code>
     * @param inMessage a <code>String</code> value or <code>null</code>
     * @return a <code>Message</code> value
     */
    public Message newMarketDataRequestReject(String inRequestId,
                                              Character inRejectReason,
                                              String inMessage)
    {
        Message reject = msgFactory.create(beginString,
                                           quickfix.field.MsgType.MARKET_DATA_REQUEST_REJECT);
        reject.setField(new quickfix.field.MDReqID(inRequestId));
        if(inRejectReason != null) {
            reject.setField(new quickfix.field.MDReqRejReason(inRejectReason));
        }
        if(inMessage != null) {
            reject.setField(new quickfix.field.Text(inMessage));
        }
        return reject;
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

    /**
     * Creates a new limit order with it fields set to the specified values.
     *
     * <p>
     * <b>NOTE:</b> This method is only meant to be used for unit testing.
     *
     * @param clOrderID the client order ID
     * @param side the side
     * @param quantity the quantity
     * @param instrument the instrument
     * @param price the price
     * @param timeInForce the time in force
     * @param account the account
     *
     * @return the new limit order
     */
    public Message newLimitOrder(
            String clOrderID,
            char side,
            BigDecimal quantity,
            Instrument instrument,
            BigDecimal price,
            char timeInForce,
            String account) {

        Message newMessage = newOrderHelper(clOrderID, side, quantity, instrument, timeInForce, account);
        newMessage.setField(new OrdType(OrdType.LIMIT));
        newMessage.setField(new Price(price));

        return newMessage;
    }

    /**
     * Creates a new market order with it fields set to the specified values.
     * <p>
     * <b>NOTE:</b> This method is only meant to be used for unit testing.
     *
     * @param clOrderID the client order ID
     * @param side the side
     * @param quantity the quantity
     * @param instrument the instrument
     * @param timeInForce the time in force
     * @param account the account
     *
     * @return the new market order
     */
    public Message newMarketOrder(
            String clOrderID,
            char side,
            BigDecimal quantity,
            Instrument instrument,
            char timeInForce,
            String account) {
        Message newMessage = newOrderHelper(clOrderID, side, quantity, instrument, timeInForce, account);
        newMessage.setField(new OrdType(OrdType.MARKET));
        return newMessage;
    }
    /**
     * Helps create a cancel order for an existing cancel request
     * <p>
     * <b>NOTE:</b> This method is only meant to be used for unit testing.
     *
     * @param clOrderId        Newly generated OrderID for this cancel request
     * @param origClOrderID    our original clOrderID of the existing order we are trying to cancel
     * that we gave to it the first time we ack'ed
     * @param side           Buy/Sell side of the initial order
     * @param quantity       Initial quantity
     * @param instrument         instrument for the initial order
     * @param counterpartyOrderID The counterpartyOrderID. can be null. this is the ID given to this order
     * by the "counterparty" financial institution
     * @return Message representing the new order
     */
    public Message newCancel(
            String clOrderId,
            String origClOrderID,
            char side,
            BigDecimal quantity,
            Instrument instrument,
            String counterpartyOrderID) {
        Message aMessage = msgFactory.create(beginString,MsgType.ORDER_CANCEL_REQUEST);

        addTransactionTimeIfNeeded(aMessage);
        aMessage.setField(new ClOrdID(clOrderId));
        aMessage.setField(new OrigClOrdID(origClOrderID));
        aMessage.setField(new Side(side));
        InstrumentToMessage.SELECTOR.forInstrument(instrument).
                set(instrument, beginString, aMessage);
        aMessage.setField(new OrderQty(quantity));
        if (counterpartyOrderID != null) {
            aMessage.setField(new OrderID(counterpartyOrderID));
        }
        msgAugmentor.cancelRequestAugment(aMessage);
        return aMessage;
    }

    /** Incoming price may be null for MARKET orders
     * <p>
     * <b>NOTE:</b> This method is only meant to be used for unit testing.
     * 
     * @param orderQty  Original order qty
     * @param orderPrice    Original order price
     * @param inAccount Account name of the institution that's sending the order. may be null
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
            Instrument instrument,
            String inAccount,
            String inText) throws FieldNotFound {
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
        InstrumentToMessage.SELECTOR.forInstrument(instrument).
                set(instrument, beginString, aMessage);
        if(inAccount != null) {
            aMessage.setField(new Account(inAccount));
        }
        if(inText != null) {
            aMessage.setField(new Text(inText));
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
     * @param side      {@link quickfix.field.Side} of the transaction
     * @param orderQty  Original order quantity
     * @param cumQty    Cumuluative order qty       (can be 0)
     * @param avgPrice  Average price for the order (can be 0)
     * @param instrument instrument for the order
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
            Instrument instrument,
            OrdRejReason rejReason,
            String inAccount,
            String inText
    ) throws FieldNotFound {
        Message execReport = newExecutionReport(orderID, clOrderID, execID,
                OrdStatus.REJECTED, side, orderQty, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                cumQty, avgPrice, instrument, inAccount, inText);
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
    /**
     * Create a new empty order single.
     *
     * @return a <code>Message</code> value
     */
    public Message newOrderEmpty()
    {
        Message msg = msgFactory.create(beginString,
                                        MsgType.ORDER_SINGLE);
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
    /**
     * Add an MDEntry to the given message with the given type.
     *
     * @param inMarketDataRequest a <code>Message</code> value
     * @param inMdEntryType a <code>char</code> value
     * @param inCurrentContentCount an <code>int</code> value
     * @return an <code>int</code> value
     * @throws FieldNotFound if the given message is invalid
     */
    private int addMdEntry(Message inMarketDataRequest,
                           char inMdEntryType,
                           int inCurrentContentCount)
            throws FieldNotFound
    {
        Group newGroup =  msgFactory.create(beginString,
                                            inMarketDataRequest.getHeader().getString(quickfix.field.MsgType.FIELD),
                                            quickfix.field.NoMDEntryTypes.FIELD);
        newGroup.setField(new quickfix.field.MDEntryType(inMdEntryType));
        inMarketDataRequest.addGroup(newGroup);
        return inCurrentContentCount + 1;
    }
    /**
     * Set the AggregatedBook value on the given message based on the inputs.
     *
     * @param inMarketDataRequest a <code>Message</code> value
     * @param inNewAggregatedBookValue a <code>boolean</code> value
     * @param inCurrentAggregatedBookValue a <code>Boolean</code> value or <code>null</code> if the value has not yet been set
     * @return a <code>boolean</code> value holding the new AggregatedBook value for the message
     * @throws IllegalArgumentException if the new aggregated book value conflicts with the old aggregated book value
     */
    private boolean setAggregatedBook(Message inMarketDataRequest,
                                      boolean inNewAggregatedBookValue,
                                      Boolean inCurrentAggregatedBookValue)
    {
        Validate.isTrue(inCurrentAggregatedBookValue == null || inNewAggregatedBookValue == inCurrentAggregatedBookValue,
                        "Request has conflicting implied aggregated book");
        inMarketDataRequest.setField(new quickfix.field.AggregatedBook(inNewAggregatedBookValue));
        return inNewAggregatedBookValue;
    }
    /**
     * Set the MaxDepth value on the given message based on the inputs.
     *
     * @param inMarketDataRequest a <code>Message</code> value
     * @param inNewDepthValue an <code>int</code> value
     * @param inCurrentDepthValue an <code>Integer</code> value or <code>null</code> if the value has not yet been set 
     * @return an <code>int</code> value containing the new MaxDepth value for the message
     * @throws IllegalArgumentException if the new MaxDepth value conflicts with the old MaxDepth value
     */
    private int setMaxDepth(Message inMarketDataRequest,
                            int inNewDepthValue,
                            Integer inCurrentDepthValue)
    {
        Validate.isTrue(inCurrentDepthValue == null || inNewDepthValue == inCurrentDepthValue,
                        "Request has conflicting implied market depth");
        inMarketDataRequest.setField(new quickfix.field.MarketDepth(inNewDepthValue));
        return inNewDepthValue;
    }
    /**
     * Creates a new FIX order
     * <p>
     * <b>NOTE:</b> This method is only meant to be used for unit testing.
     *
     * @param clOrderID     Internally generated clOrderID that will become the {@link ClOrdID} that
     *                    uniquely identifies this orderlater
     * @param side        Buy/Sell side
     * @param quantity    # of shares being bought/sold
     * @param instrument      instrument
     * @param timeInForce How long the order is in effect
     * @param account     Account ID
     * @return Message representing this new order
     */
    private Message newOrderHelper(String clOrderID, char side, BigDecimal quantity, 
            Instrument instrument, char timeInForce, String account) {
        Message aMessage = msgFactory.create(beginString, MsgType.ORDER_SINGLE);
        aMessage.setField(new ClOrdID(clOrderID));
        addHandlingInst(aMessage);
        InstrumentToMessage.SELECTOR.forInstrument(instrument).
                set(instrument, beginString, aMessage);
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
     * value used to indicate top of book only
     */
    private final int TOP_OF_BOOK_DEPTH = 1;
}
