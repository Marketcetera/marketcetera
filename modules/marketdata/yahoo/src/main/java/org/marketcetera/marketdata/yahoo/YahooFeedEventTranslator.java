package org.marketcetera.marketdata.yahoo;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.lang.ArrayUtils;
import org.marketcetera.core.CoreException;
import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.Event;
import org.marketcetera.event.EventTranslator;
import org.marketcetera.event.EventType;
import org.marketcetera.event.HasEventType;
import org.marketcetera.event.QuoteAction;
import org.marketcetera.event.QuoteEvent;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.event.impl.QuoteEventBuilder;
import org.marketcetera.event.impl.TradeEventBuilder;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Translates events from the Yahoo market data supplier.
 * 
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.1.4
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
            throw new UnsupportedOperationException(Messages.UNEXPECTED_DATA.getText(inData.getClass().getName()));
        }
        String data = (String)inData;
        SLF4JLoggerProxy.debug(YahooFeedEventTranslator.class,
                               "Received [{}] {}", //$NON-NLS-1$
                               inHandle,
                               data);
        // split the data into the query description string and the data itself
        String[] components = data.split(YahooClientImpl.QUERY_SEPARATOR);
        // the query consists of a header and a field description, split that again to get just the field description
        String header = components[0];
        String completeFields = header.split("&f=")[1]; //$NON-NLS-1$
        // split the fields using the delimiter
        String[] fields = completeFields.split(YahooClientImpl.FIELD_DELIMITER); //$NON-NLS-1$
        // the values are also comma-delimited
        String completeValues = components[1];
        String symbol = completeValues.split(YahooClientImpl.FIELD_DELIMITER)[0];
    	
        //extract the field values splitting based on symbol which avoids any , as part of data could be split up properly..
        StringBuilder builder = new StringBuilder();
        builder.append(YahooClientImpl.DELIMITER_SYMBOL );
        builder.append(symbol);
        builder.append(YahooClientImpl.FIELD_DELIMITER);
        String[] values = completeValues.split(builder.toString()); //$NON-NLS-1$
        values = (String[]) ArrayUtils.subarray(values, 1, values.length); 

        if (fields.length != values.length) {
        	String errorMsg = String.format("fields.length: %s, values.length : %s", fields.length, values.length);
            SLF4JLoggerProxy.warn(YahooFeedEventTranslator.class,errorMsg);
        	throw new CoreException(Messages.UNEXPECTED_VALUE_CODE);
        }
        
        Map<YahooField,String> matchedData = new HashMap<YahooField,String>();
        for(int i=0;i<fields.length;i++) {
            YahooField field = YahooField.getFieldFor(fields[i].substring(1));
            if(field == null) {
                Messages.UNEXPECTED_FIELD_CODE.error(YahooFeedEventTranslator.class,
                                                     fields[i]);
            } else {
                matchedData.put(field,
                                values[i]);
            }
        }
        return getEventsFrom(matchedData, inHandle);
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
     * @param inHandle 
     * @return a <code>List&lt;Event&gt;</code> value
     */
    private List<Event> getEventsFrom(Map<YahooField,String> inData, String inHandle)
    {
        SLF4JLoggerProxy.debug(YahooFeedEventTranslator.class,
                               "Getting events from {}", //$NON-NLS-1$
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
                        events, inHandle);
        lookForAskEvent(inData,
                        events, inHandle);
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
     * Determines if the given event should be sent to the client or not. 
     *
     * <p>This method requires external synchronization.
     *
     * @param inEvent an <code>Event</code> value
     * @return a <code>boolean</code> value
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
            throw new UnsupportedOperationException(Messages.NO_COMPARATOR.getText(inEvent.getClass()));
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
     * Gets the comparator to use for the given <code>Event</code>.
     * 
     * <p>This method requires external synchronization.
     *
     * @param inEvent an <code>Event</code> value
     * @return a <code>Comparator&lt;Event&gt;</code> value
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
        Comparator<Event> comparator = comparators.get(inEvent.getClass());
        if(comparator == null) {
            // no comparator there now, look for one that matches most closely
            for(Map.Entry<Class<? extends Event>,Comparator<Event>> entry : comparators.entrySet()) {
                if(entry.getKey().isAssignableFrom(inEvent.getClass())) {
                    // this comparator can be used for this event
                    // do two things: one, add this comparator for this class type to make the next check more efficient;
                    //  two, return this comparator
                    comparator = entry.getValue();
                    comparators.put(inEvent.getClass(),
                                    comparator);
                    break;
                }
            }
        }
        return comparator;
    }
    /**
     * Determines if a <code>DividendEvent</code> can be found in the given data.
     *
     * @param inData a <code>Map&lt;YahooField,String&gt;</code> value
     * @param inEvents a <code>List&lt;Event&gt;</code> value
     */
    private void lookForDividendEvent(Map<YahooField,String> inData,
                                      List<Event> inEvents)
    {
        // TODO
    }

    private static final ThreadLocal<NumberFormat> SHARED_NUMBER_FORMAT = new ThreadLocal<NumberFormat>() {
        @Override
        protected NumberFormat initialValue() {
          return NumberFormat.getInstance(Locale.US);
        }
      };
    
    /**
     * Gets the Quote data (price, size). Checks if the quote data is from a new
     * response / subsequent response by checking the cache for the particular event (bid / ask).
     * Returns the QuoteDataAction (quote details and the new action to perform for the response).
     *
     * @param inSymbol a <code>String</code> value
     * @param inPrice a <code>String</code> value
     * @param inSize a <code>String</code> value
     * @param quoteDataEventMap a <code>Map&lt;String,QuoteData&gt;</code> value
     * @param handle a <code>String</code> value
     * @return a <code>QuoteDataAction</code> value
     */
    private QuoteDataAction getQuoteDataAction(String inSymbol, String inPrice, 
    								String inSize, Map<String, QuoteData> quoteDataEventMap, 
    								String handle) {
        BigDecimal price;
        BigDecimal size;

        NumberFormat numberFormat = SHARED_NUMBER_FORMAT.get();
        boolean parsedState = true;
		QuoteAction action = null;
        try {
        	Number number = numberFormat.parse(inPrice);
        	price = BigDecimal.valueOf(number.doubleValue());
        } catch (ParseException e) {
        	price = BigDecimal.valueOf(0);
        	parsedState = false;
		}

    	try {
        	Number number = numberFormat.parse(inSize);
    		size = BigDecimal.valueOf(number.doubleValue());
		} catch (ParseException e) {
        	size = BigDecimal.valueOf(0);
        	parsedState = false;
		}
        
		QuoteData currentQuoteData = new QuoteData(price, size, inSymbol);
		QuoteDataAction quoteDataAction = new QuoteDataAction(currentQuoteData);
		
		QuoteData cachedData = quoteDataEventMap.get(handle);
		
		if (cachedData == null) {
			if (parsedState) {			
				action = QuoteAction.ADD;
				quoteDataEventMap.put(handle, currentQuoteData);
			} else {
				//need to check whether to send delete action for the first request.
				action = QuoteAction.DELETE;
				quoteDataEventMap.put(handle, null);
			}
		} else if (QUOTE_DATA_COMPARATOR.compare(currentQuoteData, cachedData) != 0) {
			if(parsedState) {
				action = QuoteAction.CHANGE;
				quoteDataEventMap.put(handle, currentQuoteData);				
			} else {
				action = QuoteAction.DELETE;
				quoteDataEventMap.put(handle, null);
			}
		}
		quoteDataAction.setQuoteAction(action);
		return quoteDataAction;
    }
    /**
     * Determines if a <code>QuoteEvent</code> can be found in the given data. 
     *
     * @param inData a <code>Map&lt;YahooField,String&gt;</code> value
     * @param inEvents a <code>List&lt;Event&gt;</code> value
     * @param inPrice a <code>String</code> value
     * @param inSize a <code>String</code> value
     * @param inSymbol a <code>String</code> value
     * @param inInstrument an <code>Instrument</code> value
     * @param inBuilder a <code>QuoteEventBuilder&lt;T&gt;</code> value
     */
    private <T extends QuoteEvent> void lookForQuoteEvent(Map<YahooField,String> inData,
                                                          List<Event> inEvents,
                                                          String inPrice,
                                                          String inSize,
                                                          String inSymbol,
                                                          Instrument inInstrument,
                                                          QuoteEventBuilder<T> inBuilder,
                                                          Map<String, QuoteData> quoteDataEventMap,
                                                          String handle)
    {
        String exchange = inData.get(YahooField.STOCK_EXCHANGE);
        // check for a missing field
        if(exchange == null ||
           inPrice == null ||
           inSize == null) {
            return;
        }
        QuoteDataAction quoteDataAction = getQuoteDataAction(inSymbol, inPrice, inSize, quoteDataEventMap, handle);
        QuoteAction action = quoteDataAction.getQuoteAction();
        QuoteData quoteData = quoteDataAction.getQuoteData();        
        if (action == null) {
        	return;
        }

        Date date = new Date();
        
        inBuilder.withExchange(exchange)
        		 .withAction(action)
                 .withProviderSymbol(inSymbol)
                 .withQuoteDate(date)
                 .withTimestamp(date)
                 .withPrice(quoteData.getPrice())
                 .withSize(quoteData.getSize());
        addFutureAttributes(inBuilder,
                            inInstrument,
                            inData);
        addOptionAttributes(inBuilder,
                            inInstrument,
                            inData);
        QuoteEvent quoteEvent = inBuilder.create();
        inEvents.add(quoteEvent);
    }
    
    private Map<String, QuoteData> getEventQuoteDataMap(String className) {
        Map<String, QuoteData> quoteSpecificDataMap = quoteDataMap.get(className);
        if (quoteSpecificDataMap == null) {
        	quoteSpecificDataMap = new HashMap<String, QuoteData>();
        	quoteDataMap.put(className, quoteSpecificDataMap);
        }
        return quoteSpecificDataMap;
    	
    }
    /**
     * Looks for bid events in the given data. 
     *
     * @param inData a <code>Map&lt;YahooField,String&gt;</code> value
     * @param inEvents a <code>List&lt;Event&gt;</code> value
     * @param inHandle 
     */
    private void lookForBidEvent(Map<YahooField,String> inData,
                                 List<Event> inEvents, String inHandle)
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
        String className = BidEvent.class.getName();
        Map<String, QuoteData> bidQuoteDataMap = getEventQuoteDataMap(className);        
        lookForQuoteEvent(inData,
                          inEvents,
                          bidPrice,
                          bidSize,
                          symbol,
                          instrument,
                          builder, bidQuoteDataMap, inHandle);
    }
    /**
     * Looks for ask events in the given data. 
     *
     * @param inData a <code>Map&lt;YahooField,String&gt;</code> value
     * @param inEvents a <code>List&lt;Event&gt;</code> value
     */
    private void lookForAskEvent(Map<YahooField,String> inData,
                                 List<Event> inEvents, String inHandle)
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
        String className = AskEvent.class.getName();
        Map<String, QuoteData> askQuoteDataMap = getEventQuoteDataMap(className);        
        lookForQuoteEvent(inData,
                          inEvents,
                          askPrice,
                          askSize,
                          symbol,
                          instrument,
                          builder, askQuoteDataMap, inHandle);
    }
    /**
     * Looks for trade events in the given data. 
     *
     * @param inData a <code>Map&lt;YahooField,String&gt;</code> value
     * @param inEvents a <code>List&lt;Event&gt;</code> value
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
        // TODO build trade date properly
        builder.withExchange(exchange)
               .withPrice(price)
               .withProviderSymbol(symbol)
               .withSize(size)
               .withTimestamp(date)
               .withTradeDate(date);
        addFutureAttributes(builder,
                            instrument,
                            inData);
        addOptionAttributes(builder,
                            instrument,
                            inData);
        inEvents.add(builder.create());
    }
    /**
     * Adds future attributes to the given trade events, if applicable.
     *
     * @param inBuilder a <code>TradeEventBuilder&lt;T&gt;</code> value
     * @param inInstrument an <code>Instrument</code> value
     * @param inData a <code>Map&lt;YahooField,String&gt;</code> value
     */
    private <T extends TradeEvent> void addFutureAttributes(TradeEventBuilder<T> inBuilder,
                                                            Instrument inInstrument,
                                                            Map<YahooField,String> inData)
    {
        // TODO
    }
    /**
     * Adds option attributes to the given trade events, if applicable.
     *
     * @param inBuilder a <code>TradeEventBuilder&lt;T&gt;</code> value
     * @param inInstrument an <code>Instrument</code> value
     * @param inData a <code>Map&lt;YahooField,String&gt;</code> value
     */
    private <T extends TradeEvent> void addOptionAttributes(TradeEventBuilder<T> inBuilder,
                                                            Instrument inInstrument,
                                                            Map<YahooField,String> inData)
    {
        // TODO
    }
    /**
     * Adds future attributes to the given quote events, if applicable.
     *
     * @param inBuilder a <code>TradeEventBuilder&lt;T&gt;</code> value
     * @param inInstrument an <code>Instrument</code> value
     * @param inData a <code>Map&lt;YahooField,String&gt;</code> value
     */
    private <T extends QuoteEvent> void addFutureAttributes(QuoteEventBuilder<T> inBuilder,
                                                            Instrument inInstrument,
                                                            Map<YahooField,String> inData)
    {
        // TODO
    }
    /**
     * Adds option attributes to the given quote events, if applicable.
     *
     * @param inBuilder a <code>TradeEventBuilder&lt;T&gt;</code> value
     * @param inInstrument an <code>Instrument</code> value
     * @param inData a <code>Map&lt;YahooField,String&gt;</code> value
     */
    private <T extends QuoteEvent> void addOptionAttributes(QuoteEventBuilder<T> inBuilder,
                                                            Instrument inInstrument,
                                                            Map<YahooField,String> inData)
    {
        // TODO
    }
    /**
     * Gets an <code>Instrument</code> for the given symbol.
     *
     * @param inSymbol a <code>String</code> value
     * @return an <code>Instrument</code> value
     */
    private Instrument getInstrumentFrom(String inSymbol)
    {
        // TODO account for other instrument types
        return new Equity(inSymbol);
    }
    /**
     * MRU cache of events 
     */
    private final Map<Class<? extends Event>,Event> eventCache = new HashMap<Class<? extends Event>,Event>();
    /**
     * comparator used to compare subsequent trade events
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
     * comparator used to compare subsequent quote events
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
     * comparator used to compare subsequent quote data.
     */
    private static final Comparator<QuoteData> QUOTE_DATA_COMPARATOR = new Comparator<QuoteData>() {
    	public int compare(QuoteData quoteData1, QuoteData quoteData2 ) {
            int result = quoteData1.getPrice().compareTo(quoteData2.getPrice());
            if(result != 0) {
                return result;
            }
            result = quoteData1.getSize().compareTo(quoteData2.getSize());
            if(result != 0) {
                return result;
            }
            result = quoteData1.getSymbol().compareTo(quoteData2.getSymbol());
            if(result != 0) {
                return result;
            }
            return result;
    	}
    };

    /**
     * Holder class for Quote data and action. 
     */
    private static final class QuoteDataAction {
    	private QuoteData quoteData;
    	private QuoteAction quoteAction;

    	public QuoteDataAction(QuoteData quoteData) {
    		this.quoteData = quoteData;
		}

		public QuoteAction getQuoteAction() {
			return quoteAction;
		}

		public void setQuoteAction(QuoteAction quoteAction) {
			this.quoteAction = quoteAction;
		}

		public QuoteData getQuoteData() {
			return quoteData;
		}
    }

    /**
     * Holder class for Quote data (Quote price, Quote size and symbol). 
     */
    private static final class QuoteData {
    	private BigDecimal price;
    	private BigDecimal size;
    	private String symbol;
    	
    	
    	public QuoteData(BigDecimal price, BigDecimal size, String symbol) {
    		this.price = price;
    		this.size = size;
    		this.symbol = symbol;
    	}
    	
		public BigDecimal getPrice() {
			return price;
		}
		public BigDecimal getSize() {
			return size;
		}
		public String getSymbol() {
			return symbol;
		}
    }
    
    private static final Map<String, Map<String, QuoteData>> quoteDataMap = new HashMap<String, Map<String, QuoteData>>();
    
    /**
     * comparators stored by event type
     */
    private static final Map<Class<? extends Event>,Comparator<Event>> comparators = new HashMap<Class<? extends Event>,Comparator<Event>>();

    /**
     * empty event list
     */
    private static final List<Event> EMPTY_EVENT_LIST = new ArrayList<Event>();
    /**
     * indicates no error
     */
    private static final String NO_ERROR = "\"N/A\"";  //$NON-NLS-1$
}
