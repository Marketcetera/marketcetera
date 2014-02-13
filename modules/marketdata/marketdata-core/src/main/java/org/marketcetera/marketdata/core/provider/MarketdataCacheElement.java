package org.marketcetera.marketdata.core.provider;

import java.util.*;

import org.marketcetera.event.*;
import org.marketcetera.event.impl.QuoteEventBuilder;
import org.marketcetera.event.util.MarketstatEventCache;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.OrderBook;
import org.marketcetera.trade.Instrument;

class MarketdataCacheElement
{
    MarketdataCacheElement(Instrument inInstrument)
    {
        instrument = inInstrument;
        marketstatCache = new MarketstatEventCache(inInstrument);
        trade = null;
    }
    Event getSnapshot(Content inContent)
    {
        if(inContent == Content.MARKET_STAT) {
            return marketstatCache.get();
        } else if(inContent == Content.LATEST_TICK) {
            return trade;
        }
        throw new UnsupportedOperationException(); // TODO
    }
    List<Event> update(Content inContent,
                       Event...inEvents)
    {
        List<Event> results = new ArrayList<Event>();
        switch(inContent) {
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
                            dividends = new ArrayList<DividendEvent>();
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
                // TODO might need to add some kind of sanity check here (is the ID on the event correct, is the action correct) to help develop providers
                orderbook.process((QuoteEvent)event);
                // processing for top of book is different than processing for depth
                if(inContent != Content.TOP_OF_BOOK) {
                    inoutResults.add(event);
                }
            } else {
                // TODO warn - ignoring event
            }
        }
        if(inContent == Content.TOP_OF_BOOK) {
            // determine if the top has changed, send only the net change
            TopOfBookEvent newTop = orderbook.getTopOfBook();
            // compare newTop to latestTop
            if(latestTop == null) {
                // everything in newTop is new (and should be of ADD action)
                // TODO verify that all events in newTop are ADDs
                inoutResults.addAll(newTop.decompose());
            } else {
                // there was a previous top sent out, measure the difference between the two
                // TODO figure out difference more economically
                for(QuoteEvent quote : latestTop.decompose()) {
                    inoutResults.add(QuoteEventBuilder.delete(quote));
                }
                for(QuoteEvent quote : newTop.decompose()) {
                    inoutResults.add(QuoteEventBuilder.add(quote));
                }
                latestTop = newTop;
            }
        }
    }
    private final Instrument instrument;
    private final Map<Content,OrderBook> orderbooks = new HashMap<Content,OrderBook>();
    private Collection<DividendEvent> dividends;
    private MarketstatEventCache marketstatCache;
    private TradeEvent trade;
    private TopOfBookEvent latestTop;
}
