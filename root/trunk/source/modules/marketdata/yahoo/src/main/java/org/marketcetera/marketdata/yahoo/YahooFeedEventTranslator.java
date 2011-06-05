package org.marketcetera.marketdata.yahoo;

import java.math.BigDecimal;
import java.util.*;

import javax.annotation.concurrent.ThreadSafe;

import org.marketcetera.core.CoreException;
import org.marketcetera.event.*;
import org.marketcetera.event.impl.QuoteEventBuilder;
import org.marketcetera.event.impl.TradeEventBuilder;
import org.marketcetera.marketdata.DateUtils;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ThreadSafe
@ClassVersion("$Id$")
public enum YahooFeedEventTranslator
        implements EventTranslator
{
    INSTANCE;
    /* (non-Javadoc)
     * @see org.marketcetera.event.EventTranslator#toEvent(java.lang.Object, java.lang.String)
     */
    @Override
    public synchronized List<Event> toEvent(Object inData,
                                            String inHandle)
            throws CoreException
    {
        if(!(inData instanceof String)) {
            throw new UnsupportedOperationException("Unexpected data type: " + inData.getClass().getName());
        }
        /*
?s=GOOG+AMZN&f=k3,d1,t1,l1,a5,b3,b6,b2,x,e1,s,&&/&&100,"6/3/2011","3:48pm",523.38,100,523.26,100,523.40,"NasdaqNM","N/A","GOOG"100,"6/3/2011","3:48pm",188.57,100,188.58,400,188.62,"NasdaqNM","N/A","AMZN"
Fields: [k3, d1, t1, l1, a5, b3, b6, b2, x, e1, s]
Values: [100, "6/3/2011", "3:48pm", 523.38, 100, 523.26, 100, 523.40, "NasdaqNM", "N/A", "GOOG"100, "6/3/2011", "3:48pm", 188.57, 100, 188.58, 400, 188.62, "NasdaqNM", "N/A", "AMZN"]
         */
        String data = (String)inData;
        SLF4JLoggerProxy.debug(YahooFeedEventTranslator.class,
                               "Received [{}] {}",
                               inHandle,
                               data);
        // split the data into the query description string and the data itself
        String[] components = data.split(YahooClientImpl.QUERY_SEPARATOR);
        // the query consists of a header and a field description, split that again to get just the field description
        String header = components[0];
        String completeFields = header.split("&f=")[1];
        // split the fields using the delimiter
        String[] fields = completeFields.split(",");
        // the values are also comma-delimited
        String completeValues = components[1];
        String[] values = completeValues.split(",");
        System.out.println("Fields: " + Arrays.toString(fields));
        System.out.println("Values: " + Arrays.toString(values));
        if(fields.length != values.length) {
            throw new RuntimeException(Arrays.toString(fields) + " does not match " + Arrays.toString(values));
        }
        Map<YahooField,String> matchedData = new HashMap<YahooField,String>();
        for(int i=0;i<fields.length;i++) {
            YahooField field = YahooField.getFieldFor(fields[i]);
            if(field == null) {
                SLF4JLoggerProxy.error(YahooFeedEventTranslator.class,
                                       "Unknown field code {}",
                                       fields[i]);
            } else {
                matchedData.put(field,
                                values[i]);
            }
        }
        return getEventsFrom(matchedData);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.EventTranslator#fromEvent(org.marketcetera.event.Event)
     */
    @Override
    public Object fromEvent(Event inEvent)
            throws CoreException
    {
        throw new UnsupportedOperationException();
    }
    /**
     * Gets all the events it can find from the given data collection.
     *
     * @param inData a <code>Map&lt;YahooField,String&gt;</code> value
     * @return a <code>List&lt;Event&gt;</code> value
     */
    private List<Event> getEventsFrom(Map<YahooField,String> inData)
    {
        SLF4JLoggerProxy.debug(YahooFeedEventTranslator.class,
                               "Getting events from {}",
                               inData);
        String errorIndication = inData.get(YahooField.ERROR_INDICATION);
        if(!errorIndication.equals(NO_ERROR)) {
            SLF4JLoggerProxy.warn(org.marketcetera.core.Messages.USER_MSG_CATEGORY,
                                  errorIndication);
            return EMPTY_EVENT_LIST;
        }
        // no error found, continue
        LinkedList<Event> events = new LinkedList<Event>();
        // look for specific event types
        lookForBidEvent(inData,
                        events);
        lookForAskEvent(inData,
                        events);
        lookForTradeEvent(inData,
                          events);
        lookForDividendEvent(inData,
                             events);
        // iterate over the event candidates in reverse order to accomplish two things:
        //  1) Mark events as part or final (this is the EVENT_BOUNDARY capability contract)
        //  2) compare events to the event cache to make sure we're not sending the same event over and over - this is necessary
        //      because the data source is poll-based rather than push-based.
        Iterator<Event> marker = events.descendingIterator();
        boolean markedFinal = false;
        while(marker.hasNext()) {
            Event event = marker.next();
            // compare event candidate to cache to make sure we're not just repeating ourselves
            if(shouldSendEvent(event)) {
                if(event instanceof HasEventType) {
                    if(!markedFinal) {
                        ((HasEventType)event).setEventType(EventType.UPDATE_FINAL);
                        markedFinal = true;
                    } else {
                        ((HasEventType)event).setEventType(EventType.UPDATE_PART);
                    }
                }
            } else {
                // this event matches the cache, so don't return it
                marker.remove();
            }
        }
        return events;
    }
    /**
     * 
     *
     * <p>This method requires external synchronization.
     *
     * @param inEvent
     * @return
     */
    private boolean shouldSendEvent(Event inEvent)
    {
        Event cachedEvent = eventCache.get(inEvent.getClass());
        if(cachedEvent == null) {
            eventCache.put(inEvent.getClass(),
                           inEvent);
            return true;
        }
        // compare just the salient parts (e.g., the timestamp will be different, but that won't matter to us)
        Comparator<Event> comparator = getComparator(inEvent);
        if(comparator == null) {
            throw new UnsupportedOperationException("No comparator for " + inEvent.getClass());
        }
        if(comparator.compare(cachedEvent,
                              inEvent) == 0) {
            // event compares to the cachedEvent, do nothing
            return false;
        }
        // event is not the same as the cachedEvent
        eventCache.put(inEvent.getClass(),
                       inEvent);
        return true;
    }
    /**
     * 
     * <p>This method requires external synchronization.
     *
     * @param inEvent
     * @return
     */
    private Comparator<Event> getComparator(Event inEvent)
    {
        if(comparators.isEmpty()) {
            comparators.put(TradeEvent.class,
                            TRADE_COMPARATOR);
            comparators.put(BidEvent.class,
                            QUOTE_COMPARATOR);
            comparators.put(AskEvent.class,
                            QUOTE_COMPARATOR);
        }
        return comparators.get(inEvent);
    }
    /**
     * 
     *
     *
     * @param inData
     * @param inEvents
     */
    private void lookForDividendEvent(Map<YahooField,String> inData,
                                      List<Event> inEvents)
    {
        // TODO
    }
    /**
     * 
     *
     *
     * @param <T>
     * @param inData
     * @param inEvents
     * @param inPrice
     * @param inSize
     * @param inBuilder
     */
    private <T extends QuoteEvent> void lookForQuoteEvent(Map<YahooField,String> inData,
                                                          List<Event> inEvents,
                                                          String inPrice,
                                                          String inSize,
                                                          String inSymbol,
                                                          Instrument inInstrument,
                                                          QuoteEventBuilder<T> inBuilder)
    {
        String exchange = inData.get(YahooField.STOCK_EXCHANGE);
        // check for a missing field
        if(exchange == null ||
           inPrice == null ||
           inSize == null) {
            return;
        }
        // all fields are non-null
        // convert the incoming string values to numbers, if possible
        BigDecimal price;
        BigDecimal size;
        try {
            price = new BigDecimal(inPrice);
            size = new BigDecimal(inSize);
        } catch (Exception e) {
            return;
        }
        Date date = new Date();
        inBuilder.withAction(QuoteAction.ADD)
                 .withExchange(exchange)
                 .withPrice(price)
                 .withProviderSymbol(inSymbol)
                 .withQuoteDate(DateUtils.dateToString(date))
                 .withSize(size)
                 .withTimestamp(date);
        addFutureAttributes(inBuilder,
                            inInstrument,
                            inData);
        addOptionAttributes(inBuilder,
                            inInstrument,
                            inData);
        inEvents.add(inBuilder.create());
    }
    /**
     * 
     *
     *
     * @param inData
     * @param inEvents
     */
    private void lookForBidEvent(Map<YahooField,String> inData,
                                 List<Event> inEvents)
    {
        String bidPrice = inData.get(YahooField.REAL_TIME_BID);
        String bidSize = inData.get(YahooField.BID_SIZE);
        String symbol = inData.get(YahooField.SYMBOL);
        // check for a missing field
        if(symbol == null ||
           bidPrice == null ||
           bidSize == null) {
            return;
        }
        // construct instrument
        Instrument instrument = getInstrumentFrom(symbol);
        QuoteEventBuilder<BidEvent> builder = QuoteEventBuilder.bidEvent(instrument);
        lookForQuoteEvent(inData,
                          inEvents,
                          bidPrice,
                          bidSize,
                          symbol,
                          instrument,
                          builder);
    }
    /**
     * 
     *
     *
     * @param inData
     * @param inEvents
     */
    private void lookForAskEvent(Map<YahooField,String> inData,
                                 List<Event> inEvents)
    {
        String askPrice = inData.get(YahooField.REAL_TIME_ASK);
        String askSize = inData.get(YahooField.ASK_SIZE);
        String symbol = inData.get(YahooField.SYMBOL);
        // check for a missing field
        if(symbol == null ||
           askPrice == null ||
           askSize == null) {
            return;
        }
        // construct instrument
        Instrument instrument = getInstrumentFrom(symbol);
        QuoteEventBuilder<AskEvent> builder = QuoteEventBuilder.askEvent(instrument);
        lookForQuoteEvent(inData,
                          inEvents,
                          askPrice,
                          askSize,
                          symbol,
                          instrument,
                          builder);
    }
    /**
     * 
     *
     *
     * @param inData
     * @param inEvents
     */
    private void lookForTradeEvent(Map<YahooField,String> inData,
                                   List<Event> inEvents)
    {
        String tradePrice = inData.get(YahooField.LAST_TRADE_PRICE_ONLY);
        String tradeSize = inData.get(YahooField.LAST_TRADE_SIZE);
        String tradeDate = inData.get(YahooField.LAST_TRADE_DATE);
        String tradeTime = inData.get(YahooField.LAST_TRADE_TIME);
        String symbol = inData.get(YahooField.SYMBOL);
        String exchange = inData.get(YahooField.STOCK_EXCHANGE);
        // check for a missing field
        if(symbol == null ||
           exchange == null ||
           tradePrice == null ||
           tradeSize == null ||
           tradeDate == null ||
           tradeTime == null) {
            return;
        }
        // construct instrument
        Instrument instrument = getInstrumentFrom(symbol);
        BigDecimal price;
        BigDecimal size;
        try {
            price = new BigDecimal(tradePrice);
            size = new BigDecimal(tradeSize);
        } catch (Exception e) {
            return;
        }
        TradeEventBuilder<? extends TradeEvent> builder = TradeEventBuilder.tradeEvent(instrument);
        Date date = new Date();
        builder.withExchange(exchange)
               .withPrice(price)
               .withProviderSymbol(symbol)
               .withSize(size)
               .withTimestamp(date)
               .withTradeDate(String.format("%s %s",
                                            tradeDate,
                                            tradeTime));
        addFutureAttributes(builder,
                            instrument,
                            inData);
        addOptionAttributes(builder,
                            instrument,
                            inData);
        inEvents.add(builder.create());
    }
    /**
     * 
     *
     *
     * @param <T>
     * @param inBuilder
     * @param inInstrument
     * @param inData
     */
    private <T extends TradeEvent> void addFutureAttributes(TradeEventBuilder<T> inBuilder,
                                                            Instrument inInstrument,
                                                            Map<YahooField,String> inData)
    {
        // TODO
    }
    /**
     * 
     *
     *
     * @param <T>
     * @param inBuilder
     * @param inInstrument
     * @param inData
     */
    private <T extends TradeEvent> void addOptionAttributes(TradeEventBuilder<T> inBuilder,
                                                            Instrument inInstrument,
                                                            Map<YahooField,String> inData)
    {
        // TODO
    }
    /**
     * 
     *
     *
     * @param <T>
     * @param inBuilder
     * @param inInstrument
     * @param inData
     */
    private <T extends QuoteEvent> void addFutureAttributes(QuoteEventBuilder<T> inBuilder,
                                                            Instrument inInstrument,
                                                            Map<YahooField,String> inData)
    {
        // TODO
    }
    /**
     * 
     *
     *
     * @param <T>
     * @param inBuilder
     * @param inInstrument
     * @param inData
     */
    private <T extends QuoteEvent> void addOptionAttributes(QuoteEventBuilder<T> inBuilder,
                                                            Instrument inInstrument,
                                                            Map<YahooField,String> inData)
    {
        // TODO
    }
    /**
     * 
     *
     *
     * @param inSymbol
     * @return
     */
    private Instrument getInstrumentFrom(String inSymbol)
    {
        // TODO account for other instrument types
        return new Equity(inSymbol);
    }
    /**
     * 
     */
    private final Map<Class<? extends Event>,Event> eventCache = new HashMap<Class<? extends Event>,Event>();
    /**
     * 
     */
    private static final Comparator<Event> TRADE_COMPARATOR = new Comparator<Event>() {
        @Override
        public int compare(Event inO1,
                           Event inO2)
        {
            TradeEvent trade1 = (TradeEvent)inO1;
            TradeEvent trade2 = (TradeEvent)inO2;
            // compare instrument and trade date
            int result = trade1.getInstrumentAsString().compareTo(trade2.getInstrumentAsString());
            if(result != 0) {
                return result;
            }
            // instrument are the same, compare trade date
            return trade1.getTradeDate().compareTo(trade2.getTradeDate());
        }
    };
    /**
     * 
     */
    private static final Comparator<Event> QUOTE_COMPARATOR = new Comparator<Event>() {
        @Override
        public int compare(Event inO1,
                           Event inO2)
        {
            QuoteEvent quote1 = (QuoteEvent)inO1;
            QuoteEvent quote2 = (QuoteEvent)inO2;
            // compare class (bid vs. ask), instrument, quote date
            int result = quote1.getClass().getName().compareTo(quote2.getClass().getName());
            if(result != 0) {
                return result;
            }
            // same class (bid vs. ask), check instrument
            result = quote1.getInstrumentAsString().compareTo(quote2.getInstrumentAsString());
            if(result != 0) {
                return result;
            }
            // same instrument, check quote date
            return quote1.getQuoteDate().compareTo(quote2.getQuoteDate());
        }
    };
    /**
     * 
     */
    private static final Map<Class<? extends Event>,Comparator<Event>> comparators = new HashMap<Class<? extends Event>,Comparator<Event>>();
    /**
     * 
     */
    private static final List<Event> EMPTY_EVENT_LIST = new ArrayList<Event>();
    /**
     * 
     */
    private static final String NO_ERROR = "\"N/A\""; 
}
