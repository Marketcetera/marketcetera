package org.marketcetera.marketdata.exsim;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Deque;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.marketcetera.core.BatchQueueProcessor;
import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.Event;
import org.marketcetera.event.EventType;
import org.marketcetera.event.HasEventType;
import org.marketcetera.event.QuoteAction;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.event.impl.MarketstatEventBuilder;
import org.marketcetera.event.impl.QuoteEventBuilder;
import org.marketcetera.event.impl.TradeEventBuilder;
import org.marketcetera.marketdata.AbstractMarketDataModuleMXBean;
import org.marketcetera.marketdata.AssetClass;
import org.marketcetera.marketdata.Capability;
import org.marketcetera.marketdata.FeedStatus;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.MarketDataRequestBuilder;
import org.marketcetera.marketdata.OrderBook;
import org.marketcetera.module.AutowiredModule;
import org.marketcetera.module.DataEmitter;
import org.marketcetera.module.DataEmitterSupport;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.Module;
import org.marketcetera.module.ModuleException;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.module.RequestDataException;
import org.marketcetera.module.RequestID;
import org.marketcetera.module.StopDataFlowException;
import org.marketcetera.options.ExpirationType;
import org.marketcetera.quickfix.EventLogFactory;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.symbol.SymbolResolverService;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;

import quickfix.Application;
import quickfix.ConfigError;
import quickfix.DoNotSend;
import quickfix.FieldNotFound;
import quickfix.FileLogFactory;
import quickfix.Group;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.LogFactory;
import quickfix.MemoryStoreFactory;
import quickfix.Message;
import quickfix.MessageStoreFactory;
import quickfix.RejectLogon;
import quickfix.Session;
import quickfix.SessionFactory;
import quickfix.SessionID;
import quickfix.SessionNotFound;
import quickfix.SessionSettings;
import quickfix.SocketInitiator;
import quickfix.UnsupportedMessageType;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/* $License$ */

/**
 * Supplies market data from the Marketcetera Exchange Simulator.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@AutowiredModule
public class ExsimFeedModule
        extends Module
        implements DataEmitter,AbstractMarketDataModuleMXBean
{
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataModuleMXBean#getFeedStatus()
     */
    @Override
    public String getFeedStatus()
    {
        if(feedStatus == null) {
            return FeedStatus.UNKNOWN.name();
        }
        return feedStatus.name();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataModuleMXBean#reconnect()
     */
    @Override
    public void reconnect()
    {
        preStop();
        preStart();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataModuleMXBean#getCapabilities()
     */
    @Override
    public Set<Capability> getCapabilities()
    {
        return supportedCapabilities;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.AbstractMarketDataModuleMXBean#getAssetClasses()
     */
    @Override
    public Set<AssetClass> getAssetClasses()
    {
        return supportedAssetClasses;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.DataEmitter#requestData(org.marketcetera.module.DataRequest, org.marketcetera.module.DataEmitterSupport)
     */
    @Override
    public void requestData(DataRequest inRequest,
                            DataEmitterSupport inSupport)
            throws RequestDataException
    {
        SLF4JLoggerProxy.debug(this,
                               "Received a data flow request: {}",
                               inRequest);
        if(!feedStatus.isRunning()) {
            // TODO message
            throw new RequestDataException(new IllegalArgumentException("Feed is unavailable"));
        }
        Object payload = inRequest.getData();
        try {
            Validate.notNull(payload,
                             "Data request data required");
            if(payload instanceof String) {
                String stringPayload = (String)payload;
                try {
                    doMarketDataRequest(MarketDataRequestBuilder.newRequestFromString(stringPayload),
                                        inRequest,
                                        inSupport);
                } catch (Exception e) {
                    throw new UnsupportedOperationException(stringPayload + " is not a valid market data request or a valid feed status request");
                }
            } else if(payload instanceof MarketDataRequest) {
                doMarketDataRequest((MarketDataRequest)payload,
                                    inRequest,
                                    inSupport);
            } else {
                throw new UnsupportedOperationException("Unsupported data request data type: " + payload.getClass().getSimpleName());
            }
        } catch (Exception e) {
            if(SLF4JLoggerProxy.isDebugEnabled(this)) {
                SLF4JLoggerProxy.debug(this,
                                       e,
                                       "Market data request failed: {}",
                                       ExceptionUtils.getRootCauseMessage(e));
            } else {
                SLF4JLoggerProxy.warn(this,
                                      "Market data request failed: {}",
                                      ExceptionUtils.getRootCauseMessage(e));
            }
            throw new RequestDataException(e);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.DataEmitter#cancel(org.marketcetera.module.DataFlowID, org.marketcetera.module.RequestID)
     */
    @Override
    public void cancel(DataFlowID inFlowID,
                       RequestID inRequestID)
    {
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.Module#preStart()
     */
    @Override
    protected void preStart()
            throws ModuleException
    {
        Validate.notNull(exsimFeedConfig,
                         "Exsim feed configuration required");
        try {
            fixMessageProcessor = new FixMessageProcessor();
            fixMessageProcessor.start();
            FIXVersion version = exsimFeedConfig.getFixVersion();
            messageFactory = version.getMessageFactory();
            application = new FixApplication();
            sessionId = exsimFeedConfig.getSessionId();
            MessageStoreFactory messageStoreFactory = new MemoryStoreFactory();
            SessionSettings sessionSettings = new SessionSettings();
            exsimFeedConfig.populateSessionSettings(sessionSettings);
            File workspaceDir = new File(System.getProperty("java.io.tmpdir")); //$NON-NLS-1$
            File quoteFeedLogDir = new File(workspaceDir,
                                            "marketdata-exsim-" + UUID.randomUUID().toString());
            FileUtils.forceMkdir(quoteFeedLogDir);
            sessionSettings.setString(sessionId,
                                      FileLogFactory.SETTING_FILE_LOG_PATH,
                                      quoteFeedLogDir.getAbsolutePath());
            sessionSettings.setString(SessionFactory.SETTING_CONNECTION_TYPE,
                                      SessionFactory.INITIATOR_CONNECTION_TYPE);
            SLF4JLoggerProxy.debug(this,
                                   "Session settings: {}",
                                   sessionSettings);
            LogFactory logFactory = new EventLogFactory(sessionSettings);
            socketInitiator = new SocketInitiator(application,
                                                  messageStoreFactory,
                                                  sessionSettings,
                                                  logFactory,
                                                  messageFactory.getUnderlyingMessageFactory());
            socketInitiator.start();
        } catch (IOException | ConfigError e) {
            SLF4JLoggerProxy.warn(this,
                                  e);
            throw new ModuleException(e);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.Module#preStop()
     */
    @Override
    protected void preStop()
            throws ModuleException
    {
        updateFeedStatus(FeedStatus.OFFLINE);
        if(fixMessageProcessor != null) {
            try {
                fixMessageProcessor.stop();
            } catch (Exception ignored) {}
            fixMessageProcessor = null;
        }
        if(socketInitiator != null) {
            try {
                socketInitiator.stop();
            } catch (Exception ignored) {}
            socketInitiator = null;
        }
        messageFactory = null;
        sessionId = null;
        application = null;
    }
    /**
     * Create a new ExsimFeedModule instance.
     *
     * @param inInstanceUrn a <code>ModuleURN</code> value
     */
    protected ExsimFeedModule(ModuleURN inInstanceUrn)
    {
        super(inInstanceUrn,
              false);
        feedStatus = FeedStatus.OFFLINE;
        orderBooksByInstrument = CacheBuilder.newBuilder().build();
    }
    /**
     * Perform the market data request
     *
     * @param inPayload a <code>MarketDataRequest</code> value
     * @param inRequest a <code>DataRequest</code> value
     * @param inSupport a <code>DataEmitterSupport</code> value
     * @throws ExecutionException if the request could not be built
     * @throws FieldNotFound if the request could not be built
     * @throws SessionNotFound if the message could not be sent
     */
    private void doMarketDataRequest(MarketDataRequest inPayload,
                                     DataRequest inRequest,
                                     DataEmitterSupport inSupport)
            throws FieldNotFound, ExecutionException, SessionNotFound
    {
        // build some number of market data request object and fire it off
        List<Instrument> requestedInstruments = Lists.newArrayList();
        for(String symbol : inPayload.getSymbols()) {
            Instrument instrument = symbolResolverService.resolveSymbol(symbol);
            if(instrument == null) {
                SLF4JLoggerProxy.warn(this,
                                      "Could not resolve symbol {}",
                                      instrument);
            } else {
                requestedInstruments.add(instrument);
            }
        }
        String id = UUID.randomUUID().toString();
        Message marketDataRequest = messageFactory.newMarketDataRequest(id,
                                                                        requestedInstruments,
                                                                        inPayload.getExchange(),
                                                                        Lists.newArrayList(inPayload.getContent()),
                                                                        quickfix.field.SubscriptionRequestType.SNAPSHOT_PLUS_UPDATES);
        SLF4JLoggerProxy.debug(this,
                               "Built {} for {} from {}",
                               marketDataRequest,
                               inRequest,
                               inPayload);
        receiversByMdReqId.put(id,
                               inSupport);
        if(!Session.sendToTarget(marketDataRequest,
                                 sessionId)) {
            receiversByMdReqId.remove(id);
            throw new StopDataFlowException(null); // TODO
        }
    }
    /**
     * Update the feed status to the new given value.
     *
     * @param inNewStatus a <code>FeedStatus</code> value
     */
    private void updateFeedStatus(FeedStatus inNewStatus)
    {
        if(inNewStatus == feedStatus) {
            return;
        }
        feedStatus = inNewStatus;
    }
    private final Cache<Instrument,OrderBook> orderBooksByInstrument;
    private final AtomicLong idCounter = new AtomicLong(0);
    private OrderBook getOrderBookFor(final Instrument inInstrument)
            throws ExecutionException
    {
        return orderBooksByInstrument.get(inInstrument,
                                          new Callable<OrderBook>() {
            @Override
            public OrderBook call()
                    throws Exception
            {
                return new OrderBook(inInstrument);
            }
        });
    }
    
    /**
     * Get the events that are present in the message.
     *
     * @param inMessageWrapper a <code>MessageWrapper</code> value
     * @return a <code>Deque&lt;Event&gt;</code> value
     * @throws FieldNotFound if the message cannot be processed
     * @throws ExecutionException 
     */
    private Deque<Event> getEventsFromMessage(MessageWrapper inMessageWrapper)
            throws FieldNotFound, ExecutionException
    {
        Message message = inMessageWrapper.getMessage();
        String msgType = inMessageWrapper.getMsgType();
        long receivedTimestamp = inMessageWrapper.getReceivedTimestamp();
        String reqId = inMessageWrapper.getRequestId();
        Deque<Event> events = Lists.newLinkedList();
        boolean isSnapshot = true;
        // if the message is a snapshot, then it's one instrument/exchange per message. if it's an incremental refresh, then it can be multiple instrument/exchange tuples per message
        switch(msgType) {
            case quickfix.field.MsgType.MARKET_DATA_SNAPSHOT_FULL_REFRESH:
                Instrument instrument = FIXMessageUtil.getInstrumentFromMessageFragment(message);
                if(instrument == null) {
                    throw new UnsupportedOperationException("Message does not specify an instrument");
                }
                OrderBook orderbook = getOrderBookFor(instrument);
                String exchange = FIXMessageUtil.getSecurityExchangeFromMessageFragment(message);
                // what types of events do we have here?
                List<Group> mdEntries = getMdEntriesFromMessage(message);
                SLF4JLoggerProxy.debug(this,
                                       "Extracted {} from {}",
                                       mdEntries,
                                       inMessageWrapper);
                throw new UnsupportedOperationException("not implemented yet"); // TODO
            case quickfix.field.MsgType.MARKET_DATA_INCREMENTAL_REFRESH:
                isSnapshot = false;
                mdEntries = getMdEntriesFromMessage(message);
                SLF4JLoggerProxy.debug(this,
                                       "Extracted {} groups from {}",
                                       mdEntries.size(),
                                       message);
                boolean marketstat = false;
                MarketstatEventBuilder marketstatBuilder = null;
                for(Group mdEntry : mdEntries) {
                    SLF4JLoggerProxy.debug(this,
                                           "Examining group {}",
                                           mdEntry);
                    BigDecimal closingPrice = null;
                    BigDecimal volume = null;
                    BigDecimal highPrice = null;
                    BigDecimal lowPrice = null;
                    BigDecimal vwap = null;
                    instrument = FIXMessageUtil.getInstrumentFromMessageFragment(mdEntry);
                    orderbook = getOrderBookFor(instrument);
                    exchange = FIXMessageUtil.getSecurityExchangeFromMessageFragment(mdEntry);
                    char entryType = mdEntry.getChar(quickfix.field.MDEntryType.FIELD);
                    QuoteAction quoteAction = null;
                    char updateAction = mdEntry.getChar(quickfix.field.MDUpdateAction.FIELD);
                    switch(updateAction) {
                        case quickfix.field.MDUpdateAction.CHANGE:
                            quoteAction = QuoteAction.CHANGE;
                            break;
                        case quickfix.field.MDUpdateAction.DELETE:
                            quoteAction = QuoteAction.DELETE;
                            break;
                        case quickfix.field.MDUpdateAction.NEW:
                            quoteAction = QuoteAction.ADD;
                            break;
                        case quickfix.field.MDUpdateAction.DELETE_FROM:
                        case quickfix.field.MDUpdateAction.DELETE_THRU:
                        default:
                            throw new UnsupportedOperationException("Unsupported update action: " + updateAction);
                    }
                    Date date = mdEntry.getUtcDateOnly(quickfix.field.MDEntryDate.FIELD);
                    Date time = mdEntry.getUtcTimeOnly(quickfix.field.MDEntryTime.FIELD);
                    Date eventDate = new Date(date.getTime()+time.getTime());
                    switch(entryType) {
                        case quickfix.field.MDEntryType.BID:
                            QuoteEventBuilder<BidEvent> bidBuilder = QuoteEventBuilder.bidEvent(instrument);
                            // TODO if it's non-add, we need to set the ID correctly, right?
                            bidBuilder.withAction(quoteAction);
                            bidBuilder.withCount(mdEntry.getInt(quickfix.field.NumberOfOrders.FIELD));
                            bidBuilder.withEventType(EventType.UPDATE_PART);
                            bidBuilder.withExchange(exchange);
                            int level = mdEntry.getInt(quickfix.field.MDEntryPositionNo.FIELD);
                            bidBuilder.withLevel(level);
                            bidBuilder.withPrice(mdEntry.getDecimal(quickfix.field.MDEntryPx.FIELD));
                            bidBuilder.withProcessedTimestamp(System.nanoTime());
                            bidBuilder.withProvider(ExsimFeedModuleFactory.IDENTIFIER);
                            bidBuilder.withQuoteDate(eventDate);
                            bidBuilder.withReceivedTimestamp(receivedTimestamp);
                            bidBuilder.withSize(mdEntry.getDecimal(quickfix.field.MDEntrySize.FIELD));
                            bidBuilder.withSource(reqId);
                            if(instrument instanceof Option) {
                                bidBuilder.withExpirationType(ExpirationType.UNKNOWN);
                                bidBuilder.withUnderlyingInstrument(new Equity(instrument.getSymbol()));
                            }
                            switch(quoteAction) {
                                case CHANGE:
                                case DELETE:
                                    bidBuilder.withMessageId(getBidIdFor(orderbook,
                                                                         level));
                                    break;
                                case ADD:
                                    bidBuilder.withMessageId(idCounter.incrementAndGet());
                                    break;
                            }
                            BidEvent bid = bidBuilder.create();
                            orderbook.process(bid);
                            events.add(bid);
                            break;
                        case quickfix.field.MDEntryType.OFFER:
                            QuoteEventBuilder<AskEvent> askBuilder = QuoteEventBuilder.askEvent(instrument);
                            // TODO if it's non-add, we need to set the ID correctly, right?
                            askBuilder.withAction(quoteAction);
                            askBuilder.withCount(mdEntry.getInt(quickfix.field.NumberOfOrders.FIELD));
                            askBuilder.withEventType(EventType.UPDATE_PART);
                            askBuilder.withExchange(exchange);
                            level = mdEntry.getInt(quickfix.field.MDEntryPositionNo.FIELD);
                            askBuilder.withLevel(level);
                            askBuilder.withPrice(mdEntry.getDecimal(quickfix.field.MDEntryPx.FIELD));
                            askBuilder.withProcessedTimestamp(System.nanoTime());
                            askBuilder.withProvider(ExsimFeedModuleFactory.IDENTIFIER);
                            askBuilder.withQuoteDate(eventDate);
                            askBuilder.withReceivedTimestamp(receivedTimestamp);
                            askBuilder.withSize(mdEntry.getDecimal(quickfix.field.MDEntrySize.FIELD));
                            askBuilder.withSource(reqId);
                            if(instrument instanceof Option) {
                                askBuilder.withExpirationType(ExpirationType.UNKNOWN);
                                askBuilder.withUnderlyingInstrument(new Equity(instrument.getSymbol()));
                            }
                            switch(quoteAction) {
                                case CHANGE:
                                case DELETE:
                                    askBuilder.withMessageId(getAskIdFor(orderbook,
                                                                         level));
                                    break;
                                case ADD:
                                    askBuilder.withMessageId(idCounter.incrementAndGet());
                                    break;
                            }
                            AskEvent ask = askBuilder.create();
                            orderbook.process(ask);
                            events.add(ask);
                            break;
                        case quickfix.field.MDEntryType.TRADE:
                            TradeEventBuilder<? extends TradeEvent> tradeBuilder = TradeEventBuilder.tradeEvent(instrument);
                            tradeBuilder.withEventType(EventType.UPDATE_PART);
                            tradeBuilder.withExchange(exchange);
                            tradeBuilder.withPrice(mdEntry.getDecimal(quickfix.field.MDEntryPx.FIELD));
                            tradeBuilder.withProcessedTimestamp(System.nanoTime());
                            tradeBuilder.withProvider(ExsimFeedModuleFactory.IDENTIFIER);
                            tradeBuilder.withTradeDate(eventDate);
                            tradeBuilder.withReceivedTimestamp(receivedTimestamp);
                            tradeBuilder.withSize(mdEntry.getDecimal(quickfix.field.MDEntrySize.FIELD));
                            tradeBuilder.withSource(reqId);
                            if(instrument instanceof Option) {
                                tradeBuilder.withExpirationType(ExpirationType.UNKNOWN);
                                tradeBuilder.withUnderlyingInstrument(new Equity(instrument.getSymbol()));
                            }
                            events.add(tradeBuilder.create());
                            if(mdEntry.isSetField(quickfix.field.TotalVolumeTraded.FIELD)) {
                                marketstat = true;
                                volume = mdEntry.getDecimal(quickfix.field.TotalVolumeTraded.FIELD);
                            }
                            break;
                        case quickfix.field.MDEntryType.CLOSING_PRICE:
                            marketstat = true;
                            closingPrice = mdEntry.getDecimal(quickfix.field.MDEntryPx.FIELD);
                            break;
                        case quickfix.field.MDEntryType.TRADE_VOLUME:
                            marketstat = true;
                            volume = mdEntry.getDecimal(quickfix.field.MDEntryPx.FIELD);
                            break;
                        case quickfix.field.MDEntryType.TRADING_SESSION_HIGH_PRICE:
                            marketstat = true;
                            highPrice = mdEntry.getDecimal(quickfix.field.MDEntryPx.FIELD);
                            break;
                        case quickfix.field.MDEntryType.TRADING_SESSION_LOW_PRICE:
                            marketstat = true;
                            lowPrice = mdEntry.getDecimal(quickfix.field.MDEntryPx.FIELD);
                            break;
                        case quickfix.field.MDEntryType.TRADING_SESSION_VWAP_PRICE:
                            marketstat = true;
                            vwap = mdEntry.getDecimal(quickfix.field.MDEntryPx.FIELD);
                            break;
                        case quickfix.field.MDEntryType.AUCTION_CLEARING_PRICE:
                        case quickfix.field.MDEntryType.COMPOSITE_UNDERLYING_PRICE:
                        case quickfix.field.MDEntryType.EARLY_PRICES:
                        case quickfix.field.MDEntryType.EMPTY_BOOK:
                        case quickfix.field.MDEntryType.IMBALANCE:
                        case quickfix.field.MDEntryType.INDEX_VALUE:
                        case quickfix.field.MDEntryType.MARGIN_RATE:
                        case quickfix.field.MDEntryType.MID_PRICE:
                        case quickfix.field.MDEntryType.OPEN_INTEREST:
                        case quickfix.field.MDEntryType.PRIOR_SETTLE_PRICE:
                        case quickfix.field.MDEntryType.SESSION_HIGH_BID:
                        case quickfix.field.MDEntryType.SESSION_LOW_OFFER:
                        case quickfix.field.MDEntryType.SETTLE_HIGH_PRICE:
                        case quickfix.field.MDEntryType.SETTLE_LOW_PRICE:
                        case quickfix.field.MDEntryType.SETTLEMENT_PRICE:
                        case quickfix.field.MDEntryType.SIMULATED_BUY_PRICE:
                        case quickfix.field.MDEntryType.SIMULATED_SELL_PRICE:
                        default:
                            SLF4JLoggerProxy.warn(this,
                                                  "Ignoring unhandled update type: {}",
                                                  entryType);
                    }
                    if(marketstat) {
                        if(marketstatBuilder == null) {
                            marketstatBuilder = MarketstatEventBuilder.marketstat(instrument);
                        }
                        marketstatBuilder.withExchangeCode(exchange);
                        if(closingPrice != null) {
                            marketstatBuilder.withClosePrice(closingPrice);
                        }
                        if(volume != null) {
                            marketstatBuilder.withVolume(volume);
                        }
                        if(highPrice != null) {
                            marketstatBuilder.withHighPrice(highPrice);
                        }
                        if(lowPrice != null) {
                            marketstatBuilder.withLowPrice(lowPrice);
                        }
                        if(vwap != null) {
                            marketstatBuilder.withValue(vwap);
                        }
                        if(instrument instanceof Option) {
                            marketstatBuilder.withExpirationType(ExpirationType.UNKNOWN);
                            marketstatBuilder.withUnderlyingInstrument(new Equity(instrument.getSymbol()));
                        }
                        marketstatBuilder.withEventType(EventType.UPDATE_PART);
                    }
                }
                if(marketstat) {
                    events.add(marketstatBuilder.create());
                }
                break;
            default:
                throw new UnsupportedOperationException("Cannot retrieve events from message type " + msgType);
        }
        if(!events.isEmpty()) {
            Event lastEvent = events.getLast();
            if(lastEvent instanceof HasEventType) {
                HasEventType eventTypeEvent = (HasEventType)lastEvent;
                eventTypeEvent.setEventType(isSnapshot?EventType.SNAPSHOT_FINAL:EventType.UPDATE_FINAL);
            }
        }
        SLF4JLoggerProxy.debug(this,
                               "Produced {} from {}",
                               events,
                               message);
        return events;
    }
    /**
     *
     *
     * @param inOrderbook
     * @param inLevel
     * @return
     */
    private long getAskIdFor(OrderBook inOrderbook,
                             int inLevel)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    /**
     *
     *
     * @param inOrderbook
     * @param inLevel
     * @return
     */
    private long getBidIdFor(OrderBook inOrderbook,
                             int inLevel)
    {
        return inOrderbook.getBidBook().get(inLevel-1).getMessageId();
    }
    /**
     * 
     *
     *
     * @param inMessage a <code>Message</code> value
     * @return a <code>List&lt:Group&gt;</code> value
     * @throws FieldNotFound
     */
    private List<Group> getMdEntriesFromMessage(Message inMessage)
            throws FieldNotFound
    {
        List<Group> mdEntries = Lists.newArrayList();
        int noMdEntries = inMessage.getInt(quickfix.field.NoMDEntries.FIELD);
        for(int i=1;i<=noMdEntries;i++) {
            Group mdEntryGroup = messageFactory.createGroup(inMessage.getHeader().getString(quickfix.field.MsgType.FIELD),
                                                            quickfix.field.NoMDEntries.FIELD);
            mdEntryGroup = inMessage.getGroup(i,
                                              mdEntryGroup);
            mdEntries.add(mdEntryGroup);
        }
        return mdEntries;
    }
    /**
     * 
     *
     *
     * @param inEvents
     * @param inRequestId
     */
    private void publishEvents(Deque<Event> inEvents,
                               String inRequestId)
    {
        if(inRequestId == null) {
            throw new UnsupportedOperationException("Cannot process response without MDReqID (262)");
        }
        if(inEvents.isEmpty()) {
            throw new UnsupportedOperationException("Cannot process response with no events");
        }
        DataEmitterSupport receiver = receiversByMdReqId.get(inRequestId);
        if(receiver == null) {
            SLF4JLoggerProxy.debug(this,
                                   "Not publishing {} to {} because it seems to have just been canceled",
                                   inEvents,
                                   inRequestId);
            return;
        }
        for(Event event : inEvents) {
            try {
                receiver.send(event);
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(this,
                                      e,
                                      "Caught and ignored exception while sending {} to {}",
                                      event,
                                      receiver);
            }
        }
    }
    /**
     * Process incoming FIX messages received from the exchange simulator.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private class FixMessageProcessor
            extends BatchQueueProcessor<MessageWrapper>
    {
        /* (non-Javadoc)
         * @see org.marketcetera.core.BatchQueueProcessor#processData(java.util.Deque)
         */
        @Override
        protected void processData(Deque<MessageWrapper> inData)
                throws Exception
        {
            for(MessageWrapper messageWrapper : inData) {
                Message message = messageWrapper.getMessage();
                try {
                    SLF4JLoggerProxy.trace(ExsimFeedModule.this,
                                           "{} procssing {}",
                                           this,
                                           message);
                    String msgType = messageWrapper.getMsgType();
                    String requestId = messageWrapper.getRequestId();
                    switch(msgType) {
                        case quickfix.field.MsgType.MARKET_DATA_SNAPSHOT_FULL_REFRESH:
                        case quickfix.field.MsgType.MARKET_DATA_INCREMENTAL_REFRESH:
                            publishEvents(getEventsFromMessage(messageWrapper),
                                          requestId);
                            break;
                        case quickfix.field.MsgType.MARKET_DATA_REQUEST_REJECT:
                            // TODO cancel corresponding request?
                            break;
                        default:
                            SLF4JLoggerProxy.warn(ExsimFeedModule.this,
                                                  "{} ignoring unexpected message {}",
                                                  this,
                                                  message);
                    }
                } catch (Exception e) {
                    SLF4JLoggerProxy.warn(ExsimFeedModule.this,
                                          e,
                                          "{} unable to process {}: {}",
                                          this,
                                          message,
                                          ExceptionUtils.getRootCauseMessage(e));
                }
            }
        }
        /**
         * Add the given message to the processing queue.
         *
         * @param inMessage a <code>Message</code> value
         * @throws FieldNotFound if the message is invalid
         */
        private void add(Message inMessage)
                throws FieldNotFound
        {
            super.add(new MessageWrapper(inMessage));
        }
        /**
         * Create a new FixMessageProcessor instance.
         */
        private FixMessageProcessor()
        {
            super(ExsimFeedModule.class.getSimpleName()+"-MessageProcessor");
        }
    }
    /**
     * Contains a received message and meta information about that message.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private static class MessageWrapper
    {
        /**
         * Create a new MessageWrapper instance.
         *
         * @param inMessage a <code>Message</code> value
         * @throws FieldNotFound if the message is invalid
         */
        private MessageWrapper(Message inMessage)
                throws FieldNotFound
        {
            message = inMessage;
            msgType = message.getHeader().getString(quickfix.field.MsgType.FIELD);
            requestId = message.getString(quickfix.field.MDReqID.FIELD);
        }
        /**
         * Get the requestId value.
         *
         * @return a <code>String</code> value
         */
        private String getRequestId()
        {
            return requestId;
        }
        /**
         * Get the msgType value.
         *
         * @return a <code>String</code> value
         */
        private String getMsgType()
        {
            return msgType;
        }
        /**
         * Get the message value.
         *
         * @return a <code>Message</code> value
         */
        private Message getMessage()
        {
            return message;
        }
        /**
         * Get the receivedTimestamp value.
         *
         * @return a <code>long</code> value
         */
        private long getReceivedTimestamp()
        {
            return receivedTimestamp;
        }
        /**
         * request ID value
         */
        private final String requestId;
        /**
         * message value
         */
        private final Message message;
        /**
         * message type value
         */
        private final String msgType;
        /**
         * received timestamp value
         */
        private final long receivedTimestamp = System.nanoTime();
    }
    /**
     * Provides an API to the FIX connection to the exchange simulator.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private class FixApplication
            implements Application
    {
        /* (non-Javadoc)
         * @see quickfix.Application#onCreate(quickfix.SessionID)
         */
        @Override
        public void onCreate(SessionID inSessionId)
        {
            SLF4JLoggerProxy.debug(ExsimFeedModule.this,
                                   "Session {} created",
                                   inSessionId);
        }
        /* (non-Javadoc)
         * @see quickfix.Application#onLogon(quickfix.SessionID)
         */
        @Override
        public void onLogon(SessionID inSessionId)
        {
            SLF4JLoggerProxy.debug(ExsimFeedModule.this,
                                   "Session {} logon",
                                   inSessionId);
            updateFeedStatus(FeedStatus.AVAILABLE);
        }
        /* (non-Javadoc)
         * @see quickfix.Application#onLogout(quickfix.SessionID)
         */
        @Override
        public void onLogout(SessionID inSessionId)
        {
            SLF4JLoggerProxy.debug(ExsimFeedModule.this,
                                   "Session {} logout",
                                   inSessionId);
            updateFeedStatus(FeedStatus.OFFLINE);
        }
        /* (non-Javadoc)
         * @see quickfix.Application#toAdmin(quickfix.Message, quickfix.SessionID)
         */
        @Override
        public void toAdmin(Message inMessage,
                            SessionID inSessionId)
        {
            SLF4JLoggerProxy.trace(ExsimFeedModule.this,
                                   "{} sending admin {}",
                                   inSessionId,
                                   inMessage);
        }
        /* (non-Javadoc)
         * @see quickfix.Application#fromAdmin(quickfix.Message, quickfix.SessionID)
         */
        @Override
        public void fromAdmin(Message inMessage,
                              SessionID inSessionId)
                throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon
        {
            SLF4JLoggerProxy.trace(ExsimFeedModule.this,
                                   "{} received admin {}",
                                   inSessionId,
                                   inMessage);
        }
        /* (non-Javadoc)
         * @see quickfix.Application#toApp(quickfix.Message, quickfix.SessionID)
         */
        @Override
        public void toApp(Message inMessage,
                          SessionID inSessionId)
                throws DoNotSend
        {
            SLF4JLoggerProxy.trace(ExsimFeedModule.this,
                                   "{} sending app {}",
                                   inSessionId,
                                   inMessage);
            FIXMessageUtil.logMessage(inMessage);
        }
        /* (non-Javadoc)
         * @see quickfix.Application#fromApp(quickfix.Message, quickfix.SessionID)
         */
        @Override
        public void fromApp(Message inMessage,
                            SessionID inSessionId)
                throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType
        {
            SLF4JLoggerProxy.trace(ExsimFeedModule.this,
                                   "{} received app {}",
                                   inSessionId,
                                   inMessage);
            FIXMessageUtil.logMessage(inMessage);
            if(fixMessageProcessor == null) {
                
            } else {
                fixMessageProcessor.add(inMessage);
            }
        }
    }
    /**
     * processes incoming FIX messages
     */
    private FixMessageProcessor fixMessageProcessor;
    /**
     * session ID value that the module will use to connect
     */
    private SessionID sessionId;
    /**
     * handles physical connection to the simualted exchange
     */
    private SocketInitiator socketInitiator;
    /**
     * message factory for the specified FIX version
     */
    private FIXMessageFactory messageFactory;
    /**
     * manages the FIX connection to the exchange
     */
    private FixApplication application;
    /**
     * configuration used to connect to the exchange
     */
    @Autowired
    private ExsimFeedConfig exsimFeedConfig;
    /**
     * resolves symbols to instruments
     */
    @Autowired
    private SymbolResolverService symbolResolverService;
    /**
     * current status of the feed
     */
    private volatile FeedStatus feedStatus;
    /**
     * 
     */
    private final Map<String,DataEmitterSupport> receiversByMdReqId = Maps.newHashMap();
    /**
     * supported asset classes for this provider
     */
    private static final Set<AssetClass> supportedAssetClasses = EnumSet.of(AssetClass.CONVERTIBLE_BOND,AssetClass.CURRENCY,AssetClass.EQUITY,AssetClass.FUTURE,AssetClass.OPTION);
    /**
     * supported capabilities for this provider
     */
    private static final Set<Capability> supportedCapabilities = EnumSet.of(Capability.AGGREGATED_DEPTH,Capability.BBO10,Capability.EVENT_BOUNDARY,Capability.LATEST_TICK,Capability.MARKET_STAT,Capability.TOP_OF_BOOK);
}
