package org.marketcetera.marketdata.yahoo;


import static org.junit.Assert.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;
import org.marketcetera.core.CoreException;
import org.marketcetera.event.Event;
import org.marketcetera.event.QuoteAction;
import org.marketcetera.event.QuoteEvent;

public class YahooFeedEventTranslatorTest {

	private static final String QUOTE_FORMAT_STRING = "&f=sa5,sb3,sb6,sb2,sx,se1,ss,&&/&&"; 
	private AtomicInteger handle = new AtomicInteger(1);

	private final YahooFeedEventTranslator translator = YahooFeedEventTranslator.INSTANCE;

	private boolean validateEventsForActions(List<Event> eventList,
			QuoteAction bidAction, QuoteAction askAction) {
		QuoteEvent bidEvent = (QuoteEvent)eventList.get(0);
		if (!bidEvent.getAction().equals(bidAction)) {
			return false;
		}
		QuoteEvent askEvent = (QuoteEvent)eventList.get(1);
		return askEvent.getAction().equals(askAction);
	}
	@Test
	public void testLookForQuoteEventWithNullSize() throws CoreException {
		String responseData = "?s=AMZN" + QUOTE_FORMAT_STRING + "\"AMZN\",,\"AMZN\"" +
				",235.22,\"AMZN\",,\"AMZN\",235.77,\"AMZN\",\"NasdaqNM\",\"AMZN\",\"N/A\",\"AMZN\",\"AMZN\"";
		List<Event>  eventList = translator.toEvent(responseData, new Integer(handle.incrementAndGet()).toString());
		assertTrue(eventList.size() == 2);
		assertTrue(validateEventsForActions(eventList, QuoteAction.DELETE, QuoteAction.DELETE));
	}

	@Test
	public void testLookForQuoteEventWithNullPrice() throws CoreException {
		String responseData = "?s=AMZN" + QUOTE_FORMAT_STRING + "\"AMZN\",100,\"AMZN\"" +
				",,\"AMZN\",100,\"AMZN\",,\"AMZN\",\"NasdaqNM\",\"AMZN\",\"N/A\",\"AMZN\",\"AMZN\"";
		List<Event>  eventList = translator.toEvent(responseData, new Integer(handle.incrementAndGet()).toString());
		assertTrue(eventList.size() == 2);
		assertTrue(validateEventsForActions(eventList, QuoteAction.DELETE, QuoteAction.DELETE));
	}

	@Test
	public void testLookForQuoteEventWithInvalidNumberForSize() throws CoreException {
		String responseData = "?s=GOOGL" + QUOTE_FORMAT_STRING + "\"GOOGL\",N/A,\"GOOGL\"" +
				",235.22,\"GOOGL\",N/A,\"GOOGL\",235.77,\"GOOGL\",\"NasdaqNM\",\"GOOGL\",\"N/A\",\"GOOGL\",\"GOOGL\"";
		List<Event>  eventList = translator.toEvent(responseData, new Integer(handle.incrementAndGet()).toString());
		assertTrue(eventList.size() == 2);
		assertTrue(validateEventsForActions(eventList, QuoteAction.DELETE, QuoteAction.DELETE));
	}

	@Test
	public void testLookForQuoteEventWithInvalidNumberForPrice() throws CoreException {
		String responseData = "?s=AAAC" + QUOTE_FORMAT_STRING + "\"AAAC\",100,\"AAAC\"" +
				",N/A,\"AAAC\",100,\"AAAC\",N/A,\"AAAC\",\"NasdaqNM\",\"AAAC\",\"N/A\",\"AAAC\",\"AAAC\"";
		List<Event>  eventList = translator.toEvent(responseData, new Integer(handle.incrementAndGet()).toString());
		assertTrue(eventList.size() == 2);
		assertTrue(validateEventsForActions(eventList, QuoteAction.DELETE, QuoteAction.DELETE));
	}

	@Test
	public void testLookForQuoteEventWithValidData() throws CoreException {
		String responseData = "?s=AAOC" + QUOTE_FORMAT_STRING + "\"AAOC\",100,\"AAOC\"" +
				",235.22,\"AAOC\",100,\"AAOC\",235.77,\"AAOC\",\"NasdaqNM\",\"AAOC\",\"N/A\",\"AAOC\",\"AAOC\"";
		List<Event>  eventList = translator.toEvent(responseData, new Integer(handle.incrementAndGet()).toString());
		assertTrue(eventList.size() == 2);
		assertTrue(validateEventsForActions(eventList, QuoteAction.ADD, QuoteAction.ADD));
	}
	
}
