package org.marketcetera.event.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.DepthOfBookEvent;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Constructs {@link DepthOfBookEvent} objects.
 * 
 * <p>Construct a <code>DepthOfBookEvent</code> by getting a <code>DepthOfBookEventBuilder</code>,
 * setting the appropriate attributes on the builder, and calling {@link #create()}.  Note that
 * the builder does no validation.  The object does its own validation with {@link #create()} is
 * called.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@NotThreadSafe
@ClassVersion("$Id$")
public abstract class DepthOfBookEventBuilder
        extends AbstractEventBuilderImpl<DepthOfBookEvent>
{
    /**
     * Returns a <code>DepthOfBookEventBuilder</code> suitable for constructing a new <code>DepthOfBookEvent</code> object.
     *
     * @return a <code>DepthOfBookEventBuilder</code> value
     */
    public static DepthOfBookEventBuilder depthOfBook()
    {
        return new DepthOfBookEventBuilder(){
            @Override
            public DepthOfBookEvent create()
            {
                return new DepthOfBookEventImpl(getEvent(),
                                                getBids(),
                                                getAsks(),
                                                getInstrument());
            }
        };
    }
    /**
     * Sets the bids to use in the new <code>DepthOfBookEvent</code> value.
     * 
     * <p>The passed bids will replace the existing bids.  If the passed
     * <code>List</code> is empty or <code>null</code>, all bids will be removed. 
     *
     * @param inBids a <code>List&lt;BidEvent&gt;</code> value or <code>null</code>
     * @return a <code>DepthOfBookEventBuilder</code> value
     */
    public final DepthOfBookEventBuilder withBids(List<BidEvent> inBids)
    {
        bids.clear();
        if(inBids != null) {
            bids.addAll(inBids);
        }
        return this;
    }
    /**
     * Sets the asks to use in the new <code>DepthOfBookEvent</code> value.
     * 
     * <p>The passed asks will replace the existing asks.  If the passed
     * <code>List</code> is empty or <code>null</code>, all asks will be removed. 
     *
     * @param inAsks a <code>List&lt;AskEvent&gt;</code> value or <code>null</code>
     * @return a <code>DepthOfBookEventBuilder</code> value
     */
    public final DepthOfBookEventBuilder withAsks(List<AskEvent> inAsks)
    {
        asks.clear();
        if(inAsks != null) {
            asks.addAll(inAsks);
        }
        return this;
    }
    /**
     * Sets the instrument to use in the new <code>DepthOfBookEvent</code> value.
     *
     * @param inInstrument an <code>Instrument</code> value or <code>null</code>
     * @return a <code>DepthOfBookEventBuilder</code> value
     */
    public final DepthOfBookEventBuilder withInstrument(Instrument inInstrument)
    {
        instrument = inInstrument;
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.EventBuilderImpl#withMessageId(long)
     */
    @Override
    public final DepthOfBookEventBuilder withMessageId(long inMessageId)
    {
        super.withMessageId(inMessageId);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.EventBuilderImpl#withTimestamp(java.util.Date)
     */
    @Override
    public final DepthOfBookEventBuilder withTimestamp(Date inTimestamp)
    {
        super.withTimestamp(inTimestamp);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.impl.AbstractEventBuilderImpl#withSource(java.lang.Object)
     */
    @Override
    public DepthOfBookEventBuilder withSource(Object inSource)
    {
        super.withSource(inSource);
        return this;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return String.format("DepthOfBookEventBuilder [asks=%s, bids=%s, instrument=%s, event=%s]", //$NON-NLS-1$
                             asks,
                             bids,
                             instrument,
                             getEvent());
    }
    /**
     * Get the instrument value.
     *
     * @return an <code>Instrument</code> value
     */
    protected final Instrument getInstrument()
    {
        return instrument;
    }
    /**
     * Get the bids value.
     *
     * @return a <code>List<BidEvent></code> value
     */
    protected final List<BidEvent> getBids()
    {
        return bids;
    }
    /**
     * Get the asks value.
     *
     * @return a <code>List<AskEvent></code> value
     */
    protected final List<AskEvent> getAsks()
    {
        return asks;
    }
    /**
     * the instrument to use 
     */
    private Instrument instrument;
    /**
     * the bids to use
     */
    private final List<BidEvent> bids = new ArrayList<BidEvent>();
    /**
     * the asks to use
     */
    private final List<AskEvent> asks = new ArrayList<AskEvent>();
}
