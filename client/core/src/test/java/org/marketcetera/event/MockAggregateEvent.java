package org.marketcetera.event;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.marketcetera.event.beans.EventBean;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Instrument;

/* $License$ */

/**
 * Extends {@link AggregateEvent} in order to test the parent class.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.5.0
 */
public class MockAggregateEvent
        implements AggregateEvent
{
    /**
     * Create a new TestEvent instance.
     *
     * @param inTimestamp a <code>Date</code> value
     * @param inInstrument an <code>Instrument</code> value
     */
    public MockAggregateEvent(Date inTimestamp,
                              Instrument inInstrument)
    {
        event.setMessageId(System.nanoTime());
        event.setTimestamp(new Date());
        instrument = inInstrument;
    }
    /**
     * Create a new MockAggregateEvent instance.
     *
     * @param inCompositeEvents a <code>List&lt;QuoteEvent&gt;</code> value containing the events to which this event should decompose
     */
    public MockAggregateEvent(List<QuoteEvent> inCompositeEvents)
    {
        this(new Date(),
             new Equity("METC"));
        compositeEvents.addAll(inCompositeEvents);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.Event#getMessageId()
     */
    @Override
    public long getMessageId()
    {
        return event.getMessageId();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.Event#getSource()
     */
    @Override
    public Object getSource()
    {
        return event.getSource();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.Event#getTimestamp()
     */
    @Override
    public Date getTimestamp()
    {
        return event.getTimestamp();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.Event#setSource(java.lang.Object)
     */
    @Override
    public void setSource(Object inSource)
    {
        event.setSource(inSource);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.TimestampCarrier#getTimeMillis()
     */
    @Override
    public long getTimeMillis()
    {
        return getTimestamp().getTime();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.AggregateEvent#decompose()
     */
    @Override
    public List<QuoteEvent> decompose()
    {
        return compositeEvents;
    }
    /**
     * Get the instrument value.
     *
     * @return a <code>InstrumentBean</code> value
     */
    public Instrument getInstrument()
    {
        return instrument;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.HasInstrument#getInstrumentAsString()
     */
    @Override
    public String getInstrumentAsString()
    {
        return instrument.getSymbol();
    }
    /**
     * stores the events to which this event should decompose
     */
    private final List<QuoteEvent> compositeEvents = new ArrayList<QuoteEvent>();
    /**
     * event attributes
     */
    private final EventBean event = new EventBean();
    /**
     * instrument attributes
     */
    private final Instrument instrument;
    private static final long serialVersionUID = 2L;
}
