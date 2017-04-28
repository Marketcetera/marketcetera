package org.marketcetera.marketdata.exsim;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Deque;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.marketcetera.core.BatchQueueProcessor;
import org.marketcetera.core.CoreException;
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
import org.marketcetera.marketdata.CapabilityCollection;
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
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.quickfix.NullLogFactory;
import org.marketcetera.symbol.SymbolResolverService;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.util.log.I18NBoundMessage;
import org.marketcetera.util.log.I18NBoundMessage0P;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.I18NBoundMessage2P;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;

import quickfix.Application;
import quickfix.ConfigError;
import quickfix.DoNotSend;
import quickfix.FieldNotFound;
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

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
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
     * @see org.marketcetera.marketdata.AbstractMarketDataModuleMXBean#disconnect()
     */
    @Override
    public void disconnect()
    {
        preStop();
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
                               "Received a data flow request: {}", //$NON-NLS-1$
                               inRequest);
        if(!feedStatus.isRunning()) {
            try {
                long timestamp = System.currentTimeMillis();
                while(!feedStatus.isRunning() && System.currentTimeMillis() < timestamp+feedAvailableTimeout) {
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                throw new RequestDataException(Messages.FEED_OFFLINE);
            }
            if(!feedStatus.isRunning()) {
                throw new RequestDataException(Messages.FEED_OFFLINE);
            }
        }
        Object payload = inRequest.getData();
        try {
            if(payload == null) {
                throw new RequestDataException(Messages.DATA_REQUEST_PAYLOAD_REQUIRED);
            }
            if(payload instanceof String) {
                String stringPayload = (String)payload;
                try {
                    doMarketDataRequest(MarketDataRequestBuilder.newRequestFromString(stringPayload),
                                        inRequest,
                                        inSupport);
                } catch (Exception e) {
                    throw new RequestDataException(new I18NBoundMessage2P(Messages.INVALID_DATA_REQUEST_PAYLOAD,
                                                                          stringPayload,
                                                                          ExceptionUtils.getRootCause(e)));
                }
            } else if(payload instanceof MarketDataRequest) {
                doMarketDataRequest((MarketDataRequest)payload,
                                    inRequest,
                                    inSupport);
            } else {
                throw new RequestDataException(new I18NBoundMessage1P(Messages.UNSUPPORTED_DATA_REQUEST_PAYLOAD,
                                                                      payload.getClass().getSimpleName()));
            }
        } catch (Exception e) {
            if(SLF4JLoggerProxy.isDebugEnabled(this)) {
                Messages.MARKET_DATA_REQUEST_FAILED.warn(this,
                                                         e,
                                                         inRequest,
                                                         ExceptionUtils.getRootCauseMessage(e));
            } else {
                Messages.MARKET_DATA_REQUEST_FAILED.warn(this,
                                                         inRequest,
                                                         ExceptionUtils.getRootCauseMessage(e));
            }
            throw new RequestDataException(e,
                                           new I18NBoundMessage2P(Messages.MARKET_DATA_REQUEST_FAILED,
                                                                  inRequest,
                                                                  ExceptionUtils.getRootCause(e)));
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.DataEmitter#cancel(org.marketcetera.module.DataFlowID, org.marketcetera.module.RequestID)
     */
    @Override
    public void cancel(DataFlowID inFlowId,
                       RequestID inRequestID)
    {
        RequestData requestData = requestsByDataFlowId.remove(inFlowId);
        if(requestData == null) {
            Messages.DATA_FLOW_ALREADY_CANCELED.warn(this,
                                                     inFlowId);
        } else {
            SLF4JLoggerProxy.debug(this,
                                   "Canceling data flow {} with market data request id {}", //$NON-NLS-1$
                                   inFlowId,
                                   requestData);
            requestsByRequestId.remove(requestData);
            try {
                cancelMarketDataRequest(requestData);
            } catch (Exception e) {
                if(SLF4JLoggerProxy.isDebugEnabled(this)) {
                    Messages.CANNOT_CANCEL_DATA_FLOW.warn(this,
                                                          e,
                                                          inFlowId);
                } else {
                    Messages.CANNOT_CANCEL_DATA_FLOW.warn(this,
                                                          inFlowId);
                }
            }
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.Module#preStart()
     */
    @Override
    protected void preStart()
            throws ModuleException
    {
        if(exsimFeedConfig == null) {
            throw new ModuleException(Messages.FEED_CONFIG_REQUIRED);
        }
        try {
            CapabilityCollection.reportCapability(getCapabilities());
            fixMessageProcessor = new FixMessageProcessor();
            fixMessageProcessor.start();
            String aplVersion = exsimFeedConfig.getFixAplVersion();
            FIXVersion version;
            if(aplVersion == null) {
                version = FIXVersion.getFIXVersion(exsimFeedConfig.getFixVersion());
            } else {
                version = FIXVersion.getFIXVersion(aplVersion);
            }
            messageFactory = version.getMessageFactory();
            application = new FixApplication();
            sessionId = exsimFeedConfig.getSessionId();
            SessionSettings sessionSettings = new SessionSettings();
            exsimFeedConfig.populateSessionSettings(sessionSettings);
            sessionSettings.setString(SessionFactory.SETTING_CONNECTION_TYPE,
                                      SessionFactory.INITIATOR_CONNECTION_TYPE);
            SLF4JLoggerProxy.debug(this,
                                   "Session settings: {}", //$NON-NLS-1$
                                   sessionSettings);
            LogFactory logFactory = new NullLogFactory();
            MessageStoreFactory messageStoreFactory = new MemoryStoreFactory();
            socketInitiator = new SocketInitiator(application,
                                                  messageStoreFactory,
                                                  sessionSettings,
                                                  logFactory,
                                                  messageFactory.getUnderlyingMessageFactory());
            socketInitiator.start();
        } catch (ConfigError e) {
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
        orderBooksByInstrument = CacheBuilder.newBuilder().build(new CacheLoader<OrderBookKey,OrderBook>() {
            @Override
            public OrderBook load(OrderBookKey inKey)
                    throws Exception
            {
                return new OrderBook(inKey.instrument);
            }
        });
    }
    /**
     * Perform the market data request
     *
     * @param inPayload a <code>MarketDataRequest</code> value
     * @param inRequest a <code>DataRequest</code> value
     * @param inSupport a <code>DataEmitterSupport</code> value
     * @throws FieldNotFound if the request could not be built
     * @throws SessionNotFound if the message could not be sent
     */
    private void doMarketDataRequest(MarketDataRequest inPayload,
                                     DataRequest inRequest,
                                     DataEmitterSupport inSupport)
            throws FieldNotFound, SessionNotFound
    {
        // build some number of market data request object and fire it off
        List<Instrument> requestedInstruments = Lists.newArrayList();
        for(String symbol : inPayload.getSymbols()) {
            Instrument instrument = symbolResolverService.resolveSymbol(symbol);
            if(instrument == null) {
                Messages.CANNOT_RESOLVE_SYMBOL.warn(this,
                                                    symbol);
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
                               "Built {} for {} from {}", //$NON-NLS-1$
                               marketDataRequest,
                               inRequest,
                               inPayload);
        RequestData requestData = new RequestData(marketDataRequest,
                                                  inSupport,
                                                  id,
                                                  inPayload,
                                                  requestedInstruments);
        requestsByRequestId.put(id,
                                requestData);
        requestsByDataFlowId.put(inSupport.getFlowID(),
                                 requestData);
        if(!Session.sendToTarget(marketDataRequest,
                                 sessionId)) {
            requestsByRequestId.remove(id);
            requestsByDataFlowId.remove(inSupport.getFlowID());
            throw new StopDataFlowException(new I18NBoundMessage1P(Messages.CANNOT_REQUEST_DATA,
                                                                   marketDataRequest));
        }
    }
    /**
     * Cancel the market data request with the given id.
     *
     * @param inMarketDataRequestData a <code>String</code> value
     * @throws FieldNotFound if the market data request cancel cannot be constructed
     * @throws SessionNotFound if the cancel message cannot be sent
     */
    private void cancelMarketDataRequest(RequestData inMarketDataRequestData)
            throws FieldNotFound, SessionNotFound
    {
        Message marketDataCancel = messageFactory.newMarketDataRequest(inMarketDataRequestData.requestId,
                                                                       inMarketDataRequestData.requestedInstruments,
                                                                       inMarketDataRequestData.marketDataRequest.getExchange(),
                                                                       Lists.newArrayList(inMarketDataRequestData.marketDataRequest.getContent()),
                                                                       quickfix.field.SubscriptionRequestType.DISABLE_PREVIOUS_SNAPSHOT_PLUS_UPDATE_REQUEST);
        if(!Session.sendToTarget(marketDataCancel,
                                 sessionId)) {
            throw new CoreException(new I18NBoundMessage2P(Messages.CANNOT_CANCEL_DATA,
                                                           marketDataCancel,
                                                           sessionId));
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
        SLF4JLoggerProxy.debug(this,
                               "Updating feed status from {} to {}",
                               feedStatus,
                               inNewStatus);
        feedStatus = inNewStatus;
        Messages.FEED_STATUS_UPDATE.info(this,
                                         ExsimFeedModuleFactory.IDENTIFIER.toUpperCase(),
                                         feedStatus);
        if(feedStatus.isRunning()) {
            orderBooksByInstrument.invalidateAll();
            SLF4JLoggerProxy.debug(this,
                                   "Feed is available, resubmitting data requests");
//            for(RequestData requestData : requestsByRequestId.values()) {
//                try {
//                    try {
//                        SLF4JLoggerProxy.debug(this,
//                                               "Canceling {}",
//                                               requestData);
//                        requestData.resubmitting = true;
//                        cancelMarketDataRequest(requestData);
//                    } catch (Exception e) {
//                        SLF4JLoggerProxy.warn(this,
//                                              e);
//                    }
//                    SLF4JLoggerProxy.debug(this,
//                                           "Resubmitting {}",
//                                           requestData.getRequestMessage());
//                    Session.sendToTarget(requestData.getRequestMessage(),
//                                         sessionId);
//                } catch (SessionNotFound e) {
//                    SLF4JLoggerProxy.warn(this,
//                                          e);
//                }
//            }
        }
    }
    /**
     * Get the order book for the given instrument.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @param inRequestId a <code>String</code> value
     * @param inExchange a <code>String</code> value
     * @return an <code>OrderBook</code> value
     */
    private OrderBook getOrderBookFor(final Instrument inInstrument,
                                      String inRequestId,
                                      String inExchange)
    {
        return orderBooksByInstrument.getUnchecked(new OrderBookKey(inRequestId,
                                                                    inInstrument,
                                                                    inExchange));
    }
    /**
     * Get the market data events from the given message.
     *
     * @param inMessageWrapper a <code>MessageWrapper</code>value
     * @param inIsSnapshot a <code>boolean</code> vlue
     * @return a <code>List&lt;Event&gt;</code> value containing the constructed events
     * @throws FieldNotFound if an expected field cannot be found
     */
    private List<Event> getEvents(MessageWrapper inMessageWrapper,
                                  boolean inIsSnapshot)
            throws FieldNotFound
    {
        Message message = inMessageWrapper.getMessage();
        String requestId = inMessageWrapper.getRequestId();
        List<Group> mdEntries = messageFactory.getMdEntriesFromMessage(message);
        long receivedTimestamp = inMessageWrapper.getReceivedTimestamp();
        List<Event> events = Lists.newArrayList();
        boolean marketstat = false;
        MarketstatEventBuilder marketstatBuilder = null;
        Instrument instrument = null;
        OrderBook orderbook = null;
        String exchange = null;
        if(inIsSnapshot) {
            instrument = FIXMessageUtil.getInstrumentFromMessageFragment(message);
            exchange = FIXMessageUtil.getSecurityExchangeFromMessageFragment(message);
            orderbook = getOrderBookFor(instrument,
                                        requestId,
                                        exchange);
            orderbook.clear();
        }
        BigDecimal volume = null;
        if(message.isSetField(quickfix.field.TotalVolumeTraded.FIELD)) {
            volume = message.getDecimal(quickfix.field.TotalVolumeTraded.FIELD);
        }
        for(Group mdEntry : mdEntries) {
            SLF4JLoggerProxy.debug(this,
                                   "Examining group {}", //$NON-NLS-1$
                                   mdEntry);
            BigDecimal closingPrice = null;
            BigDecimal prevClosingPrice = null;
            BigDecimal openPrice = null;
            BigDecimal highPrice = null;
            BigDecimal lowPrice = null;
            BigDecimal vwap = null;
            if(!inIsSnapshot) {
                instrument = FIXMessageUtil.getInstrumentFromMessageFragment(mdEntry);
                exchange = FIXMessageUtil.getSecurityExchangeFromMessageFragment(mdEntry);
                orderbook = getOrderBookFor(instrument,
                                            requestId,
                                            exchange);
            }
            char entryType = mdEntry.getChar(quickfix.field.MDEntryType.FIELD);
            QuoteAction quoteAction = QuoteAction.ADD;
            if(!inIsSnapshot) {
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
                        throw new CoreException(new I18NBoundMessage1P(Messages.UNSUPPORTED_UPDATE_ACTION,
                                                                       updateAction));
                }
            }
            Date date = mdEntry.getUtcDateOnly(quickfix.field.MDEntryDate.FIELD);
            Date time = mdEntry.getUtcTimeOnly(quickfix.field.MDEntryTime.FIELD);
            Date eventDate = new Date(date.getTime()+time.getTime());
            switch(entryType) {
                case quickfix.field.MDEntryType.BID:
                    QuoteEventBuilder<BidEvent> bidBuilder = QuoteEventBuilder.bidEvent(instrument);
                    bidBuilder.withAction(quoteAction);
                    bidBuilder.withCount(mdEntry.getInt(quickfix.field.NumberOfOrders.FIELD));
                    bidBuilder.withEventType(inIsSnapshot?EventType.SNAPSHOT_PART:EventType.UPDATE_PART);
                    bidBuilder.withExchange(exchange);
                    int level = mdEntry.getInt(quickfix.field.MDEntryPositionNo.FIELD);
                    bidBuilder.withLevel(level);
                    bidBuilder.withPrice(mdEntry.getDecimal(quickfix.field.MDEntryPx.FIELD));
                    bidBuilder.withProcessedTimestamp(System.nanoTime());
                    bidBuilder.withProvider(ExsimFeedModuleFactory.IDENTIFIER);
                    bidBuilder.withQuoteDate(eventDate);
                    bidBuilder.withReceivedTimestamp(receivedTimestamp);
                    bidBuilder.withSize(mdEntry.getDecimal(quickfix.field.MDEntrySize.FIELD));
                    bidBuilder.withSource(requestId);
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
                    if(inIsSnapshot && level == 0) {
                        bidBuilder.isEmpty(true);
                    }
                    BidEvent bid = bidBuilder.create();
                    orderbook.process(bid);
                    events.add(bid);
                    break;
                case quickfix.field.MDEntryType.OFFER:
                    QuoteEventBuilder<AskEvent> askBuilder = QuoteEventBuilder.askEvent(instrument);
                    askBuilder.withAction(quoteAction);
                    askBuilder.withCount(mdEntry.getInt(quickfix.field.NumberOfOrders.FIELD));
                    askBuilder.withEventType(inIsSnapshot?EventType.SNAPSHOT_PART:EventType.UPDATE_PART);
                    askBuilder.withExchange(exchange);
                    level = mdEntry.getInt(quickfix.field.MDEntryPositionNo.FIELD);
                    askBuilder.withLevel(level);
                    askBuilder.withPrice(mdEntry.getDecimal(quickfix.field.MDEntryPx.FIELD));
                    askBuilder.withProcessedTimestamp(System.nanoTime());
                    askBuilder.withProvider(ExsimFeedModuleFactory.IDENTIFIER);
                    askBuilder.withQuoteDate(eventDate);
                    askBuilder.withReceivedTimestamp(receivedTimestamp);
                    askBuilder.withSize(mdEntry.getDecimal(quickfix.field.MDEntrySize.FIELD));
                    askBuilder.withSource(requestId);
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
                    if(inIsSnapshot && level == 0) {
                        askBuilder.isEmpty(true);
                    }
                    AskEvent ask = askBuilder.create();
                    orderbook.process(ask);
                    events.add(ask);
                    break;
                case quickfix.field.MDEntryType.TRADE:
                    TradeEventBuilder<? extends TradeEvent> tradeBuilder = TradeEventBuilder.tradeEvent(instrument);
                    tradeBuilder.withEventType(inIsSnapshot?EventType.SNAPSHOT_PART:EventType.UPDATE_PART);
                    tradeBuilder.withExchange(exchange);
                    tradeBuilder.withPrice(mdEntry.getDecimal(quickfix.field.MDEntryPx.FIELD));
                    tradeBuilder.withProcessedTimestamp(System.nanoTime());
                    tradeBuilder.withProvider(ExsimFeedModuleFactory.IDENTIFIER);
                    tradeBuilder.withTradeDate(eventDate);
                    tradeBuilder.withReceivedTimestamp(receivedTimestamp);
                    tradeBuilder.withSize(mdEntry.getDecimal(quickfix.field.MDEntrySize.FIELD));
                    tradeBuilder.withSource(requestId);
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
                case quickfix.field.MDEntryType.PRIOR_SETTLE_PRICE:
                    marketstat = true;
                    prevClosingPrice = mdEntry.getDecimal(quickfix.field.MDEntryPx.FIELD);
                    break;
                case quickfix.field.MDEntryType.CLOSING_PRICE:
                    marketstat = true;
                    closingPrice = mdEntry.getDecimal(quickfix.field.MDEntryPx.FIELD);
                    break;
                case quickfix.field.MDEntryType.OPENING_PRICE:
                    marketstat = true;
                    openPrice = mdEntry.getDecimal(quickfix.field.MDEntryPx.FIELD);
                    break;
                case quickfix.field.MDEntryType.TRADE_VOLUME:
                    marketstat = true;
                    volume = mdEntry.getDecimal(quickfix.field.MDEntrySize.FIELD);
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
                case quickfix.field.MDEntryType.SESSION_HIGH_BID:
                case quickfix.field.MDEntryType.SESSION_LOW_OFFER:
                case quickfix.field.MDEntryType.SETTLE_HIGH_PRICE:
                case quickfix.field.MDEntryType.SETTLE_LOW_PRICE:
                case quickfix.field.MDEntryType.SETTLEMENT_PRICE:
                case quickfix.field.MDEntryType.SIMULATED_BUY_PRICE:
                case quickfix.field.MDEntryType.SIMULATED_SELL_PRICE:
                default:
                    Messages.IGNORING_UNHANDLED_UPDATE_TYPE.warn(this,
                                                                 entryType);
            }
            if(marketstat) {
                if(marketstatBuilder == null) {
                    marketstatBuilder = MarketstatEventBuilder.marketstat(instrument);
                }
                marketstatBuilder.withExchangeCode(exchange);
                if(openPrice != null) {
                    marketstatBuilder.withOpenPrice(openPrice);
                    marketstatBuilder.withClosePrice(openPrice);
                    marketstatBuilder.withPreviousClosePrice(openPrice);
                }
                if(closingPrice != null) {
                    marketstatBuilder.withClosePrice(closingPrice);
                    marketstatBuilder.withPreviousClosePrice(closingPrice);
                }
                if(prevClosingPrice != null) {
                    marketstatBuilder.withPreviousClosePrice(prevClosingPrice);
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
                marketstatBuilder.withEventType(inIsSnapshot?EventType.SNAPSHOT_PART:EventType.UPDATE_PART);
            }
        }
        if(marketstat) {
            events.add(marketstatBuilder.create());
        }
        return events;
    }
    /**
     * Get the message id used for the event at the given level of the ask book.
     *
     * @param inOrderbook an <code>OrderBook</code> value
     * @param inLevel an <code>int</code> value
     * @return a <code>long</code> value
     */
    private long getAskIdFor(OrderBook inOrderbook,
                             int inLevel)
    {
        return inOrderbook.getAskBook().get(inLevel-1).getMessageId();
    }
    /**
     * Get the message id used for the event at the given level of the bid book.
     *
     * @param inOrderbook an <code>OrderBook</code> value
     * @param inLevel an <code>int</code> value
     * @return a <code>long</code> value
     */
    private long getBidIdFor(OrderBook inOrderbook,
                             int inLevel)
    {
        return inOrderbook.getBidBook().get(inLevel-1).getMessageId();
    }
    /**
     * Publish the given events to the data flow associated with the given id.
     *
     * @param inEvents a <code>Deque&lt;Event&gt;</code> value
     * @param inRequestId a <code>String</code> value
     */
    private void publishEvents(Deque<Event> inEvents,
                               String inRequestId)
    {
        RequestData requestData = requestsByRequestId.get(inRequestId);
        if(requestData == null) {
            SLF4JLoggerProxy.debug(this,
                                   "Not publishing {} to {} because it seems to have just been canceled", //$NON-NLS-1$
                                   inEvents,
                                   inRequestId);
            return;
        }
        SLF4JLoggerProxy.trace(this,
                               "Publishing {} to {}", //$NON-NLS-1$
                               inEvents,
                               inRequestId);
        for(Event event : inEvents) {
            try {
                requestData.getDataEmitterSupport().send(event);
            } catch (Exception e) {
                if(SLF4JLoggerProxy.isDebugEnabled(this)) {
                    Messages.IGNORED_EXCEPTION_ON_SEND.warn(this,
                                                            e,
                                                            ExceptionUtils.getRootCause(e),
                                                            event,
                                                            requestData);
                } else {
                    Messages.IGNORED_EXCEPTION_ON_SEND.warn(this,
                                                            ExceptionUtils.getRootCause(e),
                                                            event,
                                                            requestData);
                }
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
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            return description;
        }
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
                                           "{} processing {}", //$NON-NLS-1$
                                           this,
                                           message);
                    String msgType = messageWrapper.getMsgType();
                    String requestId = messageWrapper.getRequestId();
                    boolean isSnapshot = false;
                    switch(msgType) {
                        case quickfix.field.MsgType.MARKET_DATA_SNAPSHOT_FULL_REFRESH:
                            isSnapshot = true;
                        case quickfix.field.MsgType.MARKET_DATA_INCREMENTAL_REFRESH:
                            Deque<Event> events = Lists.newLinkedList();
                            events.addAll(getEvents(messageWrapper,
                                                    isSnapshot));
                            if(!events.isEmpty()) {
                                Event lastEvent = events.getLast();
                                if(lastEvent instanceof HasEventType) {
                                    HasEventType eventTypeEvent = (HasEventType)lastEvent;
                                    eventTypeEvent.setEventType(isSnapshot?EventType.SNAPSHOT_FINAL:EventType.UPDATE_FINAL);
                                }
                            }
                            SLF4JLoggerProxy.debug(this,
                                                   "Produced {}", //$NON-NLS-1$
                                                   events);
                            publishEvents(events,
                                          requestId);
                            break;
                        case quickfix.field.MsgType.MARKET_DATA_REQUEST_REJECT:
                            // cancel corresponding request, unless resubmitting due to feed status change
                            RequestData requestData = requestsByRequestId.get(messageWrapper.getRequestId());
                            if(requestData == null) {
                            } else {
                                if(!requestData.resubmitting) {
                                    requestsByRequestId.remove(messageWrapper.getRequestId());
                                    requestsByDataFlowId.remove(requestData.getDataEmitterSupport().getFlowID());
                                    I18NBoundMessage errorMessage;
                                    if(message.isSetField(quickfix.field.Text.FIELD)) {
                                        errorMessage = new I18NBoundMessage1P(Messages.MARKETDATA_REJECT_WITH_MESSAGE,
                                                                              message.getString(quickfix.field.Text.FIELD));
                                    } else {
                                        errorMessage = new I18NBoundMessage0P(Messages.MARKETDATA_REJECT_WITHOUT_MESSAGE);
                                    }
                                    requestData.getDataEmitterSupport().dataEmitError(errorMessage,
                                                                                      true);
                                }
                            }
                            break;
                        default:
                            Messages.IGNORING_UNEXPECTED_MESSAGE.warn(ExsimFeedModule.this,
                                                                      this,
                                                                      message);
                    }
                } catch (Exception e) {
                    if(SLF4JLoggerProxy.isDebugEnabled(ExsimFeedModule.this)) {
                        Messages.UNABLE_TO_PROCESS_MESSAGE.warn(ExsimFeedModule.this,
                                                                e,
                                                                this,
                                                                message,
                                                                ExceptionUtils.getRootCauseMessage(e));
                    } else {
                        Messages.UNABLE_TO_PROCESS_MESSAGE.warn(ExsimFeedModule.this,
                                                                this,
                                                                message,
                                                                ExceptionUtils.getRootCauseMessage(e));
                    }
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
            super(ExsimFeedModule.class.getSimpleName()+"-MessageProcessor"); //$NON-NLS-1$
            description = ExsimFeedModule.class.getSimpleName()+"-MessageProcessor"; //$NON-NLS-1$
        }
        /**
         * human-readable description of the processor
         */
        private final String description;
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
                                   "Session {} created", //$NON-NLS-1$
                                   inSessionId);
        }
        /* (non-Javadoc)
         * @see quickfix.Application#onLogon(quickfix.SessionID)
         */
        @Override
        public void onLogon(SessionID inSessionId)
        {
            SLF4JLoggerProxy.debug(ExsimFeedModule.this,
                                   "Session {} logon", //$NON-NLS-1$
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
                                   "Session {} logout", //$NON-NLS-1$
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
                                   "{} sending admin {}", //$NON-NLS-1$
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
                                   "{} received admin {}", //$NON-NLS-1$
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
                                   "{} sending app {}", //$NON-NLS-1$
                                   inSessionId,
                                   inMessage);
            if(SLF4JLoggerProxy.isTraceEnabled(ExsimFeedModule.this)) {
                FIXMessageUtil.logMessage(inSessionId,
                                          inMessage);
            }
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
                                   "{} received app {}", //$NON-NLS-1$
                                   inSessionId,
                                   inMessage);
            if(SLF4JLoggerProxy.isTraceEnabled(ExsimFeedModule.this)) {
                FIXMessageUtil.logMessage(inSessionId,
                                          inMessage);
            }
            if(fixMessageProcessor == null) {
                
            } else {
                fixMessageProcessor.add(inMessage);
            }
        }
    }
    /**
     * Holds data relevant to a market data request as part of a module data flow.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private static class RequestData
    {
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            return description;
        }
        /**
         * Get the requestMessage value.
         *
         * @return a <code>Message</code> value
         */
        @SuppressWarnings("unused")
        private Message getRequestMessage()
        {
            return requestMessage;
        }
        /**
         * Get the dataEmitterSupport value.
         *
         * @return a <code>DataEmitterSupport</code> value
         */
        private DataEmitterSupport getDataEmitterSupport()
        {
            return dataEmitterSupport;
        }
        /**
         * Create a new RequestData instance.
         *
         * @param inRequestMessage a <code>Message</code> value
         * @param inDataEmitterSupport a <code>DataEmitterSupport</code> value
         * @param inRequestId a <code>String</code> value
         * @param inMarketDataRequest a <code>MarketDataRequest</code> value
         * @param inRequestedInstruments a <code>List&lt;Instrument&gt;</code> value
         */
        private RequestData(Message inRequestMessage,
                            DataEmitterSupport inDataEmitterSupport,
                            String inRequestId,
                            MarketDataRequest inMarketDataRequest,
                            List<Instrument> inRequestedInstruments)
        {
            requestMessage = inRequestMessage;
            dataEmitterSupport = inDataEmitterSupport;
            description = RequestData.class.getSimpleName() + " [" + inDataEmitterSupport.getFlowID() + "]"; //$NON-NLS-1$ //$NON-NLS-2$
            requestId = inRequestId;
            requestedInstruments = inRequestedInstruments;
            marketDataRequest = inMarketDataRequest;
        }
        /**
         * indicates if the request is in the process of being resubmitted
         */
        private volatile boolean resubmitting = false;
        /**
         * human-readable description of the object
         */
        private final String description;
        /**
         * original request message sent to the exchange
         */
        private final Message requestMessage;
        /**
         * information about the data flow requester
         */
        private final DataEmitterSupport dataEmitterSupport;
        /**
         * request id of the request
         */
        private final String requestId;
        /**
         * instruments requested
         */
        private final List<Instrument> requestedInstruments;
        /**
         * original market data request
         */
        private final MarketDataRequest marketDataRequest;
    }
    /**
     * Serves as the unique key for a cached order book.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private static final class OrderBookKey
    {
        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode()
        {
            return new HashCodeBuilder().append(requestId).append(instrument).append(exchange).toHashCode();
        }
        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj)
        {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof OrderBookKey)) {
                return false;
            }
            OrderBookKey other = (OrderBookKey) obj;
            return new EqualsBuilder().append(requestId,other.requestId).append(instrument,other.instrument).append(exchange,other.exchange).isEquals();
        }
        /**
         * Create a new OrderBookKey instance.
         *
         * @param inRequestId a <code>String</code> value
         * @param inInstrument an <code>Instrument</code> value
         * @param inExchange a <code>String</code> value
         */
        private OrderBookKey(String inRequestId,
                             Instrument inInstrument,
                             String inExchange)
        {
            requestId = inRequestId;
            instrument = inInstrument;
            exchange = inExchange;
        }
        /**
         * request id value
         */
        private final String requestId;
        /**
         * instrument value
         */
        private final Instrument instrument;
        /**
         * exchange value
         */
        private final String exchange;
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
     * number of milliseconds to wait for the feed to become available if a request is made while it is offline
     */
    private long feedAvailableTimeout = 10000;
    /**
     * current status of the feed
     */
    private volatile FeedStatus feedStatus;
    /**
     * event order books keyed by instrument
     */
    private final LoadingCache<OrderBookKey,OrderBook> orderBooksByInstrument;
    /**
     * used to assign unique event ids
     */
    private final AtomicLong idCounter = new AtomicLong(0);
    /**
     * holds data request info keyed by request id
     */
    private final Map<String,RequestData> requestsByRequestId = Maps.newHashMap();
    /**
     * holds market data request info by data flow id
     */
    private final Map<DataFlowID,RequestData> requestsByDataFlowId = Maps.newHashMap();
    /**
     * supported asset classes for this provider
     */
    private static final Set<AssetClass> supportedAssetClasses = EnumSet.of(AssetClass.CONVERTIBLE_BOND,AssetClass.CURRENCY,AssetClass.EQUITY,AssetClass.FUTURE,AssetClass.OPTION);
    /**
     * supported capabilities for this provider
     */
    private static final Set<Capability> supportedCapabilities = EnumSet.of(Capability.BBO10,Capability.EVENT_BOUNDARY,Capability.LATEST_TICK,Capability.MARKET_STAT,Capability.TOP_OF_BOOK);
}
