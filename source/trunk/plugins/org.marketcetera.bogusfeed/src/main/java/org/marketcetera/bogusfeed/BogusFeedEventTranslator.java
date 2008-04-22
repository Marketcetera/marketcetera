package org.marketcetera.bogusfeed;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.marketcetera.core.MarketceteraException;
import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.EventBase;
import org.marketcetera.event.IEventTranslator;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.event.UnsupportedEventException;

import quickfix.FieldNotFound;
import quickfix.Group;
import quickfix.field.MDEntryPx;
import quickfix.field.MDEntrySize;
import quickfix.field.MDEntryType;
import quickfix.field.MDMkt;
import quickfix.field.NoMDEntries;
import quickfix.field.Symbol;
import quickfix.fix44.MarketDataSnapshotFullRefresh;

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: $
 * @since 0.43-SNAPSHOT
 */
public class BogusFeedEventTranslator
        implements IEventTranslator
{
    /* (non-Javadoc)
     * @see org.marketcetera.event.IEventTranslator#translate(java.lang.Object)
     */
    public List<EventBase> translate(Object inData)
            throws MarketceteraException
    {
        if(!(inData instanceof MarketDataSnapshotFullRefresh)) {
            throw new UnsupportedEventException(String.format("Unknown event type: %s",
                                                              inData.getClass()));
        }
        MarketDataSnapshotFullRefresh refresh = (MarketDataSnapshotFullRefresh)inData;
        List<EventBase> events = new ArrayList<EventBase>();
        try {
            int entries = refresh.getInt(NoMDEntries.FIELD);
            for(int i=1;i<=entries;i++) {
                Group group = new MarketDataSnapshotFullRefresh.NoMDEntries();
                refresh.getGroup(i, 
                                 group);
                String symbol = refresh.getString(Symbol.FIELD);
                String exchange = group.getString(MDMkt.FIELD);
                String price = group.getString(MDEntryPx.FIELD);
                String size = group.getString(MDEntrySize.FIELD);
                char type = group.getChar(MDEntryType.FIELD);
                switch(type){
                    case MDEntryType.BID :
                        BidEvent bid = new BidEvent(System.nanoTime(),
                                                    System.currentTimeMillis(),
                                                    refresh,
                                                    symbol,
                                                    exchange,
                                                    new BigDecimal(price),
                                                    new BigDecimal(size));
                        events.add(bid);
                        break;
                    case MDEntryType.OFFER :
                        AskEvent ask = new AskEvent(System.nanoTime(),
                                                    System.currentTimeMillis(),
                                                    refresh,
                                                    symbol,
                                                    exchange,
                                                    new BigDecimal(price),
                                                    new BigDecimal(size));
                        events.add(ask);
                        break;
                    case MDEntryType.TRADE:
                        TradeEvent trade = new TradeEvent(System.nanoTime(),
                                                          System.currentTimeMillis(),
                                                          symbol,
                                                          exchange,
                                                          new BigDecimal(price),
                                                          new BigDecimal(size),
                                                          refresh);
                        events.add(trade);
                        break;
                    default:
                        throw new UnsupportedEventException(String.format("Unknown message entry type: %c",
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
    public Object translate(EventBase inEvent)
            throws MarketceteraException
    {
        // TODO Auto-generated method stub
        return null;
    }

}
