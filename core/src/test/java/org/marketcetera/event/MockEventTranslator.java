package org.marketcetera.event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.marketcetera.core.CoreException;
import org.marketcetera.marketdata.MarketDataRequest;

/* $License$ */
/**
 * Test implementation of <code>IEventTranslator</code>.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.5.0
 */
public class MockEventTranslator
    implements EventTranslator
{
    private static boolean sTranslateToEventsThrows = false;
    private static boolean sTranslateToEventsReturnsNull = false;
    private static boolean sTranslateToEventsReturnsZeroEvents = false;
    private static MockEventTranslator sInstance = new MockEventTranslator();
    private static MarketDataRequest requestToReturn = null;
    /**
     * Gets a <code>TestEventTranslator</code> value.
     *
     * @return a <code>TestEventTranslator</code> value
     */
    public static MockEventTranslator getTestEventTranslator()
    {
        return sInstance;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.IEventTranslator#translate(java.lang.Object)
     */
    @Override
    public List<Event> toEvent(Object inData,
                                      String inHandle)
            throws CoreException
    {
        if(getTranslateToEventsThrows()) {
            throw new NullPointerException("This exception is expected");
        }
        if(getTranslateToEventsReturnsNull()) {
            return null;
        }
        if(getTranslateToEventsReturnsZeroEvents()) {
            return new ArrayList<Event>();
        }
        MarketDataRequest request = null;
        if(requestToReturn != null) {
            request = requestToReturn;
        } else if(inData instanceof MarketDataRequest) {
            request = (MarketDataRequest)inData;
        }
        if(inData instanceof QuoteEvent ||
           inData instanceof AggregateEvent) {
            return Arrays.asList(new Event[] {(Event)inData });
        }
        if(inData instanceof Event) {
            return Arrays.asList(new Event[] { (Event)inData });
        }
        return Arrays.asList(new Event[] { new MockEvent(request) });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.IEventTranslator#translate(org.marketcetera.event.Event)
     */
    public String fromEvent(Event inEvent)
            throws CoreException
    {
        return inEvent.toString();
    }
    public static boolean getTranslateToEventsThrows()
    {
        return sTranslateToEventsThrows;
    }
    public static void setTranslateToEventsThrows(boolean inTranslateToEventsThrows)
    {
        sTranslateToEventsThrows = inTranslateToEventsThrows;
    }
    public static boolean getTranslateToEventsReturnsZeroEvents()
    {
        return sTranslateToEventsReturnsZeroEvents;
    }
    public static void setTranslateToEventsReturnsZeroEvents(boolean inTranslateToEventsReturnsZeroEvents)
    {
        sTranslateToEventsReturnsZeroEvents = inTranslateToEventsReturnsZeroEvents;
    }
    public static boolean getTranslateToEventsReturnsNull()
    {
        return sTranslateToEventsReturnsNull;
    }
    public static void setTranslateToEventsReturnsNull(boolean inTranslateToEventsReturnsNull)
    {
        sTranslateToEventsReturnsNull = inTranslateToEventsReturnsNull;
    }
    /**
     * Sets the requestToReturn value.
     *
     * @param inRequestToReturn a <code>DataRequest</code> value
     */
    public static void setRequestToReturn(MarketDataRequest inRequestToReturn)
    {
        requestToReturn = inRequestToReturn;
    }
    /**
     * Resets the behavior to the default.  Tests that configure this class should use this method to cleanup.
     */
    public static void reset()
    {
    	requestToReturn = null;
    	sTranslateToEventsReturnsNull = false;
    	sTranslateToEventsReturnsZeroEvents = false;
    	sTranslateToEventsThrows = false;
    }
}
