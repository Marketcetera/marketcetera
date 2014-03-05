package org.marketcetera.marketdata.core.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.marketcetera.event.*;
import org.marketcetera.event.impl.QuoteEventBuilder;
import org.marketcetera.event.util.MarketstatEventCache;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.OrderBook;
import org.marketcetera.marketdata.core.Messages;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/* $License$ */

/**
 * Caches market data for a given instrument.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
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
        marketstatCache = new MarketstatEventCache(inInstrument);
        trade = null;
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
            case TOP_OF_BOOK:
                OrderBook orderbook = orderbooks.get(inContent);
                return orderbook == null ? null : orderbook.getTopOfBook();
            case AGGREGATED_DEPTH:
            case BBO10:
            case DIVIDEND:
            case LEVEL_2:
            case NBBO:
            case OPEN_BOOK:
            case TOTAL_VIEW:
            case UNAGGREGATED_DEPTH:
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
                    if(event instanceof DividendEvent) {
                        if(dividends == null) {
                            dividends = Lists.newArrayList();
                        }
                        dividends.add((DividendEvent)event);
                        results.add(event);
                    } else {
                        // TODO warn - skipping event
                    }
                }
                break;
            case LATEST_TICK:
                for(Event event : inEvents) {
                    if(event instanceof TradeEvent) {
                        trade = (TradeEvent)event;
                        results.add(event);
                    } else {
                        // TODO warn - skipping event
                    }
                }
                break;
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
        OrderBook orderbook = orderbooks.get(inContent);
        if(orderbook == null) {
            orderbook = new OrderBook(instrument);
            orderbooks.put(inContent,
                           orderbook);
        }
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
                orderbook.process(quoteEvent);
                latestTop = orderbook.getTopOfBook();
                inoutResults.add(quoteEvent);
            } else {
                throw new IllegalArgumentException(Messages.CONTENT_REQUIRES_QUOTE_EVENTS.getText(inContent,event.getClass().getName()));
            }
        }
    }
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
    private Collection<DividendEvent> dividends;
    /**
     * cached marketstat data
     */
    private MarketstatEventCache marketstatCache;
    /**
     * most recent trade
     */
    private TradeEvent trade;
    /**
     * most recent top-of-book
     */
    private TopOfBookEvent latestTop;
}
