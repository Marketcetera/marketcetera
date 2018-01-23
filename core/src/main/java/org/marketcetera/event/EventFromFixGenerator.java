package org.marketcetera.event;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.marketcetera.core.Cacheable;
import org.marketcetera.event.impl.MarketstatEventBuilder;
import org.marketcetera.event.impl.QuoteEventBuilder;
import org.marketcetera.event.impl.TradeEventBuilder;
import org.marketcetera.marketdata.OrderBook;
import org.marketcetera.options.ExpirationType;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import quickfix.FieldNotFound;
import quickfix.Group;
import quickfix.Message;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;

/* $License$ */

/**
 * Generates events from FIX market data messages and manages order book state.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class EventFromFixGenerator
        implements Cacheable
{
    /**
     * Get all manager order books.
     *
     * @return a <code>Collection&lt;OrderBook&gt;</code> value
     */
    public Collection<OrderBook> getOrderBooks()
    {
        return Collections.unmodifiableCollection(orderBooksByInstrument.asMap().values());
    }
    /**
     * Get the order book for the given parameters.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @param inExchange a <code>String</code> value
     * @param inRequestId a <code>String</code> value
     * @return an <code>OrderBook</code> value
     */
    public OrderBook getOrderBook(Instrument inInstrument,
                                  String inExchange,
                                  String inRequestId)
    {
        return orderBooksByInstrument.getUnchecked(new OrderBookKey(inRequestId,
                                                                    inInstrument,
                                                                    inExchange));
    }
    /**
     * Produce events from the given message.
     *
     * @param inMessage a <code>Message</code> value
     * @param inIsSnapshot a <code>boolean</code> value
     * @param inReceivedTimestamp a <code>long</code> value
     * @return a <code>List&lt;Event&gt;</code> value
     * @throws FieldNotFound
     */
    public List<Event> events(Message inMessage,
                              boolean inIsSnapshot,
                              long inReceivedTimestamp)
            throws FieldNotFound
    {
        FIXVersion version = FIXVersion.getFIXVersion(inMessage);
        FIXMessageFactory messageFactory = version.getMessageFactory();
        String requestId = inMessage.getString(quickfix.field.MDReqID.FIELD);
        List<Group> mdEntries = messageFactory.getMdEntriesFromMessage(inMessage);
        List<Event> events = Lists.newArrayList();
        boolean marketstat = false;
        MarketstatEventBuilder marketstatBuilder = null;
        Instrument instrument = null;
        OrderBook orderbook = null;
        String exchange = null;
        if(inIsSnapshot) {
            instrument = FIXMessageUtil.getInstrumentFromMessageFragment(inMessage);
            exchange = FIXMessageUtil.getSecurityExchangeFromMessageFragment(inMessage);
            orderbook = getOrderBookFor(instrument,
                                        requestId,
                                        exchange);
            orderbook.clear();
        }
        BigDecimal volume = null;
        if(inMessage.isSetField(quickfix.field.TotalVolumeTraded.FIELD)) {
            volume = inMessage.getDecimal(quickfix.field.TotalVolumeTraded.FIELD);
        }
        for(Group mdEntry : mdEntries) {
            SLF4JLoggerProxy.debug(this,
                                   "Examining group {}", //$NON-NLS-1$
                                   mdEntry);
            BigDecimal closingPrice = null;
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
            if(entryType == quickfix.field.MDEntryType.EMPTY_BOOK) {
                continue;
            }
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
                        throw new UnsupportedOperationException();
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
                    bidBuilder.withProvider(provider);
                    bidBuilder.withQuoteDate(eventDate);
                    bidBuilder.withReceivedTimestamp(inReceivedTimestamp);
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
                    askBuilder.withProvider(provider);
                    askBuilder.withQuoteDate(eventDate);
                    askBuilder.withReceivedTimestamp(inReceivedTimestamp);
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
                    tradeBuilder.withProvider(provider);
                    tradeBuilder.withTradeDate(eventDate);
                    tradeBuilder.withReceivedTimestamp(inReceivedTimestamp);
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
                case quickfix.field.MDEntryType.PRIOR_SETTLE_PRICE:
                case quickfix.field.MDEntryType.SESSION_HIGH_BID:
                case quickfix.field.MDEntryType.SESSION_LOW_OFFER:
                case quickfix.field.MDEntryType.SETTLE_HIGH_PRICE:
                case quickfix.field.MDEntryType.SETTLE_LOW_PRICE:
                case quickfix.field.MDEntryType.SETTLEMENT_PRICE:
                case quickfix.field.MDEntryType.SIMULATED_BUY_PRICE:
                case quickfix.field.MDEntryType.SIMULATED_SELL_PRICE:
                default:
                    throw new UnsupportedOperationException();
            }
            if(marketstat) {
                if(marketstatBuilder == null) {
                    marketstatBuilder = MarketstatEventBuilder.marketstat(instrument);
                }
                marketstatBuilder.withExchangeCode(exchange);
                if(closingPrice != null) {
                    marketstatBuilder.withClosePrice(closingPrice);
                    marketstatBuilder.withPreviousClosePrice(closingPrice);
                }
                if(volume != null) {
                    marketstatBuilder.withVolume(volume);
                }
                if(highPrice != null) {
                    marketstatBuilder.withHighPrice(highPrice);
                }
                if(openPrice != null) {
                    marketstatBuilder.withOpenPrice(openPrice);
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
     * Create a new EventFromFixGenerator instance.
     *
     * @param inProvider a <code>String</code> value
     */
    public EventFromFixGenerator(String inProvider)
    {
        orderBooksByInstrument = CacheBuilder.newBuilder().build(new CacheLoader<OrderBookKey,OrderBook>() {
            @Override
            public OrderBook load(OrderBookKey inKey)
                    throws Exception
            {
                return new OrderBook(inKey.instrument);
            }
        });
        provider = inProvider;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.Cacheable#clear()
     */
    @Override
    public void clear()
    {
        orderBooksByInstrument.invalidateAll();
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
     * provider label to attach to events
     */
    private final String provider;
    /**
     * used to assign unique event ids
     */
    private final AtomicLong idCounter = new AtomicLong(0);
    /**
     * event order books keyed by instrument
     */
    private final LoadingCache<OrderBookKey,OrderBook> orderBooksByInstrument;
}
