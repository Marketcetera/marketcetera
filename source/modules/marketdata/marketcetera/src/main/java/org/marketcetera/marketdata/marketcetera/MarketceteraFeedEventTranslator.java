package org.marketcetera.marketdata.marketcetera;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.ObjectUtils;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.CoreException;
import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.EventBase;
import org.marketcetera.event.EventTranslator;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.event.UnsupportedEventException;
import org.marketcetera.marketdata.MarketDataRequest.Content;
import org.marketcetera.marketdata.marketcetera.MarketceteraFeed.Request;
import org.marketcetera.trade.MSymbol;
import org.marketcetera.util.log.I18NBoundMessage1P;

import quickfix.FieldNotFound;
import quickfix.Group;
import quickfix.field.MDEntryPx;
import quickfix.field.MDEntrySize;
import quickfix.field.MDEntryType;
import quickfix.field.MDMkt;
import quickfix.field.NoMDEntries;
import quickfix.field.Symbol;
import quickfix.fix44.MarketDataSnapshotFullRefresh;

/* $License$ */

/**
 * Market data feed implementation that connects to Marketcetera's
 * exchange simulator.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.5.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class MarketceteraFeedEventTranslator
    implements EventTranslator, Messages
{
    private static final String UNKNOWN = "?"; //$NON-NLS-1$
    private static final MarketceteraFeedEventTranslator sInstance = new MarketceteraFeedEventTranslator();
    public static MarketceteraFeedEventTranslator getInstance()
    {
        return sInstance;
    }
    private MarketceteraFeedEventTranslator()
    {        
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.IEventTranslator#translate(java.lang.Object)
     */
    public List<EventBase> toEvent(Object inData,
                                   String inHandle) 
        throws CoreException
    {
        if(!(inData instanceof MarketDataSnapshotFullRefresh)) {
            throw new UnsupportedEventException(new I18NBoundMessage1P(UNKNOWN_EVENT_TYPE,
                                                                       ObjectUtils.toString(inData,
                                                                                            null)));
        }
        MarketDataSnapshotFullRefresh refresh = (MarketDataSnapshotFullRefresh)inData;
        List<EventBase> events = new ArrayList<EventBase>();
        try {
            int entries = refresh.getInt(NoMDEntries.FIELD);
            // marketcetera feed returns bid/ask/trade for every query (each entry corresponds to one of these).
            // we have to decide which data to convert to events and pass along.  we know the symbol and the handle.
            // the handle is sufficient to determine what content was requested with the original request.
            Request request = MarketceteraFeed.getRequestByHandle(inHandle);
            if(request == null) {
                // this could happen if the request were canceled (and removed from the collection) but the feed
                //  is still sending updates.  just bail out, no worries, the feed will stop soon.
                return events;
            }
            Set<Content> requestedContent = request.getRequest().getContent();
            for(int i=1;i<=entries;i++) {
                Group group = new MarketDataSnapshotFullRefresh.NoMDEntries();
                refresh.getGroup(i, 
                                 group);
                String symbol = refresh.getString(Symbol.FIELD);
                // exchange is *somewhat* optional
                String exchange;
                try {
                    exchange = group.getString(MDMkt.FIELD);
                } catch (FieldNotFound e) {
                    exchange = UNKNOWN;
                }
                String price = group.getString(MDEntryPx.FIELD);
                String size = group.getString(MDEntrySize.FIELD);
                char type = group.getChar(MDEntryType.FIELD);
                switch(type){
                    case MDEntryType.BID :
                        if(requestedContent.contains(Content.TOP_OF_BOOK)) {
                            BidEvent bid = new BidEvent(System.nanoTime(),
                                                        System.currentTimeMillis(),
                                                        new MSymbol(symbol),
                                                        exchange,
                                                        new BigDecimal(price),
                                                        new BigDecimal(size));
                            events.add(bid);
                        }
                        break;
                    case MDEntryType.OFFER :
                        if(requestedContent.contains(Content.TOP_OF_BOOK)) {
                            AskEvent ask = new AskEvent(System.nanoTime(),
                                                        System.currentTimeMillis(),
                                                        new MSymbol(symbol),
                                                        exchange,
                                                        new BigDecimal(price),
                                                        new BigDecimal(size));
                            events.add(ask);
                        }
                        break;
                    case MDEntryType.TRADE:
                        if(requestedContent.contains(Content.LATEST_TICK)) {
                            TradeEvent trade = new TradeEvent(System.nanoTime(),
                                                              System.currentTimeMillis(),
                                                              new MSymbol(symbol),
                                                              exchange,
                                                              new BigDecimal(price),
                                                              new BigDecimal(size));
                            events.add(trade);
                        }
                        break;
                    default:
                        throw new UnsupportedEventException(new I18NBoundMessage1P(UNKNOWN_MESSAGE_ENTRY_TYPE,
                                                                                   type));
                };
            }
        } catch (FieldNotFound e) {
            e.printStackTrace();
        }
        return events;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.IEventTranslator#translate(org.marketcetera.event.EventBase)
     */
    public Object fromEvent(EventBase inEvent) 
        throws CoreException
    {
        throw new UnsupportedOperationException();
    }
}
