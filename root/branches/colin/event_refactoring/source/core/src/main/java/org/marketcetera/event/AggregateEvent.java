package org.marketcetera.event;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.marketcetera.marketdata.Exchange;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * An aggregation of {@link EventBase} objects that represents an {@link Exchange} data output. 
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public abstract class AggregateEvent
    extends EventBase
    implements Serializable, HasInstrument
{
    /**
     * Create a new AggregateEvent instance.
     *
     * @param inTimestamp a <code>Date</code> value indicating when the event occurred
     * @param inInstrument an <code>Instrument</code> value specifying the instrument for which the event occurred
     * @throws IllegalArgumentException if <code>inTimestamp</code> &lt; 0
     * @throws NullPointerException if <code>inSymbol</code> is null
     */
    protected AggregateEvent(Date inTimestamp,
                             Instrument inInstrument)
    {
        super(EventBase.assignCounter(),
              inTimestamp.getTime());
        if(inInstrument == null) {
            throw new NullPointerException();
        }
        instrument = inInstrument;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.HasInstrument#getInstrument()
     */
    @Override
    public Instrument getInstrument()
    {
        return instrument;
    }
    /**
     * Produces a list of <code>EventBase</code> objects that describe
     * this <code>AggregateEvent</code>.
     * 
     * @return a <code>List&lt;EventBase&gt;</code> value
     */
    public abstract List<EventBase> decompose();
    /**
     * the instrument of this event
     */
    private final Instrument instrument;
    private static final long serialVersionUID = 2L;
}
