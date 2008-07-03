package org.marketcetera.event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.MarketceteraException;

import quickfix.Message;

/* $License$ */
/**
 * Test implementation of <code>IEventTranslator</code>.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.5.0
 */
@ClassVersion("$Id$")
public class MockEventTranslator
        extends AbstractEventTranslator
{
    private static boolean sTranslateToEventsThrows = false;
    private static boolean sTranslateToEventsReturnsNull = false;
    private static boolean sTranslateToEventsReturnsZeroEvents = false;
    private static MockEventTranslator sInstance = new MockEventTranslator();
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
    public List<EventBase> translate(Object inData)
            throws MarketceteraException
    {
        if(getTranslateToEventsThrows()) {
            throw new NullPointerException("This exception is expected");
        }
        if(getTranslateToEventsReturnsNull()) {
            return null;
        }
        if(getTranslateToEventsReturnsZeroEvents()) {
            return new ArrayList<EventBase>();
        }
        Message message = null;
        if(inData instanceof Message) {
            message = (Message)inData;
        }
        if(inData instanceof SymbolExchangeEvent) {
            SymbolExchangeEvent see = (SymbolExchangeEvent)inData;
            updateEventFixMessageSnapshot(see);
            return Arrays.asList(new EventBase[] { see });
        }
        return Arrays.asList(new EventBase[] { new UnknownEvent(System.nanoTime(),
                                                                System.currentTimeMillis(),
                                                                message) });
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.IEventTranslator#translate(org.marketcetera.event.EventBase)
     */
    public String translate(EventBase inEvent)
            throws MarketceteraException
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
}
