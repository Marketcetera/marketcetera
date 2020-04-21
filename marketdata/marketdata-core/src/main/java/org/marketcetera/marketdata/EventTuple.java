package org.marketcetera.marketdata;

import org.marketcetera.event.Event;
import org.marketcetera.trade.Instrument;

/* $License$ */

/**
 * Contains the information of a produced event.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class EventTuple
{
    /**
     * Get the instrument value.
     *
     * @return an <code>Instrument</code> value
     */
    public Instrument getInstrument()
    {
        return instrument;
    }
    /**
     * Get the content value.
     *
     * @return a <code>Content</code> value
     */
    public Content getContent()
    {
        return content;
    }
    /**
     * Get the event value.
     *
     * @return a <code>Event</code> value
     */
    public Event getEvent()
    {
        return event;
    }
    /**
     * Get the exchange value.
     *
     * @return a <code>String</code> value
     */
    public String getExchange()
    {
        return exchange;
    }
    /**
     * Create a new EventTuple instance.
     *
     * @param inInstrument an <code>Instrument</code> value
     * @param inContent a <code>Content</code> value
     * @param inExchange a <code>String</code> value
     * @param inEvent an <code>Event</code> value
     */
    public EventTuple(Instrument inInstrument,
                      Content inContent,
                      String inExchange,
                      Event inEvent)
    {
        instrument = inInstrument;
        content = inContent;
        exchange = inExchange;
        event = inEvent;
    }
    /**
     * instrument value
     */
    private final Instrument instrument;
    /**
     * content value
     */
    private final Content content;
    /**
     * exchange value
     */
    private final String exchange;
    /**
     * event value
     */
    private final Event event;
}
