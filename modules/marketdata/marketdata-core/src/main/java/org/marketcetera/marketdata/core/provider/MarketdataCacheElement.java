package org.marketcetera.marketdata.core.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.DividendEvent;
import org.marketcetera.event.Event;
import org.marketcetera.event.EventType;
import org.marketcetera.event.HasEventType;
import org.marketcetera.event.ImbalanceEvent;
import org.marketcetera.event.MarketstatEvent;
import org.marketcetera.event.QuoteAction;
import org.marketcetera.event.QuoteEvent;
import org.marketcetera.event.TopOfBookEvent;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.event.impl.QuoteEventBuilder;
import org.marketcetera.event.util.MarketstatEventCache;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.OrderBook;
import org.marketcetera.marketdata.core.Messages;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/* $License$ */

/**
 * Caches market data for a given instrument.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.0
 */
@ClassVersion("$Id$")
public class MarketdataCacheElement
{
    /**
     * Create a new MarketdataCacheElement instance.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @throws IllegalArgumentException if the given instrument is <code>null</code>
     */
    public MarketdataCacheElement(Instrument inInstrument)
    {
        Validate.notNull(inInstrument);
        instrument = inInstrument;
        clear();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("MarketdataCacheElement [").append(instrument.getFullSymbol()).append("]").append(System.lineSeparator());
        for(Map.Entry<Content,OrderBook> entry : orderbooks.entrySet()) {
            builder.append(entry.getKey()).append(System.lineSeparator());
            builder.append(entry.getValue()).append(System.lineSeparator());
        }
        builder.append("Latest trade: ").append(trade).append(System.lineSeparator());
        builder.append("Stats: ").append(marketstatCache).append(System.lineSeparator());
        builder.append("Imbalance: ").append(imbalance).append(System.lineSeparator());
        builder.append("Dividends: ").append(dividends).append(System.lineSeparator());
        return builder.toString();
    }
    /**
     * Clear the cache.
     */
    public void clear()
    {
        marketstatCache = new MarketstatEventCache(instrument,
                                                   true);
        trade = null;
        imbalance = null;
        orderbooks.clear();
        dividends.clear();
    }
    /**
     * Invalidate the given content type of the cache.
     *
     * @param inContent a <code>Content</code>value
     */
    public void invalidate(Content inContent)
    {
        switch(inContent) {
            case NBBO:
            case UNAGGREGATED_DEPTH:
            case AGGREGATED_DEPTH:
            case BBO10:
            case TOP_OF_BOOK:
            case LEVEL_2:
            case OPEN_BOOK:
            case TOTAL_VIEW:
                getOrderBookFor(inContent).clear();
                break;
            case DIVIDEND:
                dividends.clear();
                break;
            case LATEST_TICK:
                trade = null;
                break;
            case IMBALANCE:
                imbalance = null;
            case MARKET_STAT:
                marketstatCache = null;
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }
    /**
     * Gets the latest snapshot for the given content.
     *
     * @param inContent a <code>Content</code> value
     * @return an <code>Event</code> value or <code>null</code> if no cached data exists for this content type
     */
    public Event getSnapshot(Content inContent)
    {
        switch(inContent) {
            case MARKET_STAT:
                return marketstatCache.get();
            case LATEST_TICK:
                return trade;
            case IMBALANCE:
                return imbalance;
            case TOP_OF_BOOK:
            case NBBO:
                return getOrderBookFor(inContent).getTopOfBook();
            case AGGREGATED_DEPTH:
            case BBO10:
            case LEVEL_2:
            case OPEN_BOOK:
            case TOTAL_VIEW:
            case UNAGGREGATED_DEPTH:
                return getOrderBookFor(inContent).getDepthOfBook();
            case DIVIDEND:
                // TODO we actually need to return multiple events here
            default:
                throw new UnsupportedOperationException();
        }
    }
    /**
     * Updates the cache for the given content with the given events.
     *
     * @param inContent a <code>Content</code> value
     * @param inEvents an <code>Event[]</code> value
     * @return a <code>List&lt;Event&gt;</code> value containing the net change represented by the given update
     */
    public List<Event> update(Content inContent,
                              Event...inEvents)
    {
        List<Event> results = new ArrayList<Event>();
        switch(inContent) {
            case NBBO:
            case UNAGGREGATED_DEPTH:
            case AGGREGATED_DEPTH:
            case BBO10:
            case TOP_OF_BOOK:
            case LEVEL_2:
            case OPEN_BOOK:
            case TOTAL_VIEW:
                doBookUpdate(inContent,
                             results,
                             inEvents);
                break;
            case DIVIDEND:
                for(Event event : inEvents) {
                    resetOnSnapshot(inContent,
                                    event);
                    if(event instanceof DividendEvent) {
                        dividends.add((DividendEvent)event);
                        results.add(event);
                    } else {
                        // TODO warn - skipping event
                    }
                }
                break;
            case LATEST_TICK:
                for(Event event : inEvents) {
                    resetOnSnapshot(inContent,
                                    event);
                    if(event instanceof TradeEvent) {
                        trade = (TradeEvent)event;
                        results.add(event);
                    } else {
                        // TODO warn - skipping event
                    }
                }
                break;
            case IMBALANCE:
                for(Event event : inEvents) {
                    resetOnSnapshot(inContent,
                                    event);
                    if(event instanceof ImbalanceEvent) {
                        imbalance = (ImbalanceEvent)event;
                        results.add(event);
                    } else {
                        // TODO warn - skipping event
                    }
                }
            case MARKET_STAT:
                for(Event event : inEvents) {
                    if(event instanceof MarketstatEvent) {
                        if(marketstatCache == null) {
                            marketstatCache = new MarketstatEventCache(instrument);
                        }
                        marketstatCache.cache((MarketstatEvent)event);
                    } else {
                        // TODO warn - skipping event
                    }
                    // note that this intentionally combines a potential multitude of incoming marketstat events into a single result
                    results.add(marketstatCache.get());
                }
                break;
            default:
                throw new UnsupportedOperationException();
        }
        return results;
    }
    /**
     * Updates the order book for the given content with the given events.
     *
     * @param inContent a <code>Content</code> value
     * @param inoutResults a <code>Collection&lt;Event&gt;</code> value containing the net change of the update
     * @param inEvents an <code>Event[]</code> value containing the update
     */
    private void doBookUpdate(Content inContent,
                              Collection<Event> inoutResults,
                              Event...inEvents)
    {
        OrderBook orderbook = getOrderBookFor(inContent);
        for(Event event : inEvents) {
            if(event instanceof QuoteEvent) {
                QuoteEvent quoteEvent = (QuoteEvent)event;
                if(inContent == Content.TOP_OF_BOOK) {
                    // generate DEL event for existing top, if necessary
                    if(quoteEvent.getAction() == QuoteAction.ADD) {
                        if(latestTop != null) {
                            QuoteEvent deleteEvent = null;
                            if(quoteEvent instanceof BidEvent) {
                                BidEvent latestBid = latestTop.getBid();
                                if(latestBid != null) {
                                    deleteEvent = QuoteEventBuilder.delete(latestBid);
                                }
                            } else if(quoteEvent instanceof AskEvent) {
                                AskEvent latestAsk = latestTop.getAsk();
                                if(latestAsk != null) {
                                    deleteEvent = QuoteEventBuilder.delete(latestAsk);
                                }
                            } else {
                                throw new UnsupportedOperationException();
                            }
                            if(deleteEvent != null) {
                                orderbook.process(deleteEvent);
                                inoutResults.add(deleteEvent);
                            }
                        }
                    }
                }
                resetOnSnapshot(inContent,
                                event);
                orderbook.process(quoteEvent);
                latestTop = orderbook.getTopOfBook();
                inoutResults.add(quoteEvent);
            } else {
                throw new IllegalArgumentException(Messages.CONTENT_REQUIRES_QUOTE_EVENTS.getText(inContent,event.getClass().getName()));
            }
        }
    }
    /**
     * Gets the order book for the given content.
     *
     * @param inContent a <code>Content</code> value
     * @return an <code>OrderBook</code> value
     */
    private OrderBook getOrderBookFor(Content inContent)
    {
        OrderBook book = orderbooks.get(inContent);
        if(book == null) {
            book = new OrderBook(instrument,
                                 true);
            orderbooks.put(inContent,
                           book);
        }
        return book;
    }
    /**
     * Reset the content cache if necessary.
     *
     * @param inContent a <code>Content</code> value
     * @param inEvent an <code>Event</code> value
     */
    private void resetOnSnapshot(Content inContent,
                                 Event inEvent)
    {
        if(inEvent instanceof HasEventType) {
            HasEventType hasEventType = (HasEventType)inEvent;
            EventType eventType = hasEventType.getEventType();
            if(eventType.isSnapshot()) {
                boolean resetOnSnapshot = resetOnSnapshotIndicator.getUnchecked(inContent);
                if(resetOnSnapshot) {
                    SLF4JLoggerProxy.debug(this,
                                           "{} cache invalidating {} on {}",
                                           instrument,
                                           inContent,
                                           inEvent);
                    invalidate(inContent);
                }
            }
            boolean resetFlag = eventType.isUpdate() || eventType.isComplete();
            resetOnSnapshotIndicator.put(inContent,
                                         resetFlag);
        }
    }
    /**
     * indicates whether to reset the given content on snapshot
     */
    private final LoadingCache<Content,Boolean> resetOnSnapshotIndicator = CacheBuilder.newBuilder().build(new CacheLoader<Content,Boolean>() {
        @Override
        public Boolean load(Content inKey)
                throws Exception
        {
            return false;
        }});
    /**
     * instrument for which market data is cached
     */
    private final Instrument instrument;
    /**
     * order book structures, by content
     */
    private final Map<Content,OrderBook> orderbooks = Maps.newHashMap();
    /**
     * cached dividend data
     */
    private final List<DividendEvent> dividends = Lists.newArrayList();
    /**
     * cached marketstat data
     */
    private MarketstatEventCache marketstatCache;
    /**
     * most recent trade
     */
    private TradeEvent trade;
    /**
     * most recent imbalance
     */
    private ImbalanceEvent imbalance;
    /**
     * most recent top-of-book
     */
    private TopOfBookEvent latestTop;
}
