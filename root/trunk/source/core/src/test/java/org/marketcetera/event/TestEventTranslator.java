package org.marketcetera.event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.marketcetera.core.MarketceteraException;

/**
 * Test implementation of <code>IEventTranslator</code>.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.43-SNAPSHOT
 */
public class TestEventTranslator
        implements IEventTranslator
{
    private static boolean sTranslateToEventsThrows = false;
    private static boolean sTranslateToEventsReturnsNull = false;
    private static boolean sTranslateToEventsReturnsZeroEvents = false;
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
        return Arrays.asList(new EventBase[] { new UnknownEvent(System.nanoTime(),
                                                                System.currentTimeMillis()) });
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
