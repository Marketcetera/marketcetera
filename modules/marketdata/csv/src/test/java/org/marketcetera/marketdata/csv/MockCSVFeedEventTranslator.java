package org.marketcetera.marketdata.csv;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.marketcetera.core.CoreException;
import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.Event;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.event.impl.QuoteEventBuilder;
import org.marketcetera.event.impl.TradeEventBuilder;
import org.marketcetera.marketdata.Content;
import org.marketcetera.trade.Equity;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.1.0
 */
public class MockCSVFeedEventTranslator
        extends CSVFeedEventTranslator
{
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.csv.CSVFeedEventTranslator#toEvent(java.lang.Object, java.lang.String)
     */
    @Override
    public List<Event> toEvent(Object inData,
                               String inHandle)
            throws CoreException
    {
        CSVQuantum q = (CSVQuantum)inData;
        List<Event> events = new ArrayList<Event>();
        String[] data = q.getLine();
        long timeMillis = Long.parseLong(data[0]);
        String symbol = data[1];
        String exchange = data[2];
        BigDecimal price = new BigDecimal(data[3]);
        BigDecimal size = new BigDecimal(data[4]);
        String type = data[5];
        if(type.equals("TRADE")) {
            if(q.getRequest().getContent().contains(Content.LATEST_TICK)) {
                TradeEventBuilder<? extends TradeEvent> builder = TradeEventBuilder.equityTradeEvent();
                builder.withExchange(exchange)
                       .withInstrument(new Equity(symbol))
                       .withTimestamp(new Date(timeMillis))
                       .withPrice(price)
                       .withSize(size)
                       .withTradeDate(new Date());
                events.add(builder.create());
            }
        } else if(type.equals("BID")) {
            if(q.getRequest().getContent().contains(Content.TOP_OF_BOOK)) {
                QuoteEventBuilder<BidEvent> builder = QuoteEventBuilder.bidEvent(new Equity(symbol));
                builder.withExchange(exchange)
                       .withInstrument(new Equity(symbol))
                       .withTimestamp(new Date(timeMillis))
                       .withPrice(price)
                       .withSize(size)
                       .withQuoteDate(new Date());
                events.add(builder.create());
            }
        } else if(type.equals("ASK")) {
            if(q.getRequest().getContent().contains(Content.TOP_OF_BOOK)) {
                QuoteEventBuilder<AskEvent> builder = QuoteEventBuilder.askEvent(new Equity(symbol));
                builder.withExchange(exchange)
                       .withInstrument(new Equity(symbol))
                       .withTimestamp(new Date(timeMillis))
                       .withPrice(price)
                       .withSize(size)
                       .withQuoteDate(new Date());
                events.add(builder.create());
            }
        } else {
            throw new UnsupportedOperationException("Unsupported type: " + Arrays.toString(data));
        }
        return events;
    }
}
