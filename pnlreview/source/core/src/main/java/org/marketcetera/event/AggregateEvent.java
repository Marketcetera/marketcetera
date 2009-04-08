package org.marketcetera.event;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.marketdata.Exchange;
import org.marketcetera.trade.MSymbol;

/* $License$ */

/**
 * An aggregation of {@link EventBase} objects that represents an {@link Exchange} data output. 
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public abstract class AggregateEvent
    extends EventBase
    implements Serializable, HasSymbol
{
    /**
     * Create a new AggregateEvent instance.
     *
     * @param inTimestamp a <code>Date</code> value indicating when the event occurred
     * @param inSymbol an <code>MSymbol</code> value containing the symbol for which the event occurred
     * @throws IllegalArgumentException if <code>inTimestamp</code> &lt; 0
     * @throws NullPointerException if <code>inSymbol</code> is null
     */
    protected AggregateEvent(Date inTimestamp,
                             MSymbol inSymbol)
    {
        super(counter.incrementAndGet(),
              inTimestamp.getTime());
        if(inSymbol == null) {
            throw new NullPointerException();
        }
        symbol = inSymbol;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.HasSymbol#getSymbol()
     */
    @Override
    public MSymbol getSymbol()
    {
        return symbol;
    }
    /**
     * Produces a list of <code>EventBase</code> objects that describe
     * this <code>AggregateEvent</code>.
     * 
     * @return a <code>List&lt;EventBase&gt;</code> value
     */
    public abstract List<EventBase> decompose();
    /**
     * the symbol of this event
     */
    private final MSymbol symbol;
    /**
     * the counter used to guarantee that aggregate events are distinct from each-other
     */
    private static final AtomicLong counter = new AtomicLong(0);
    private static final long serialVersionUID = 1L;
}
