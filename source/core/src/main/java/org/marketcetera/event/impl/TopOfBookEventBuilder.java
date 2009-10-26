package org.marketcetera.event.impl;

import java.util.Date;

import javax.annotation.concurrent.NotThreadSafe;

import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.TopOfBookEvent;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Constructs {@link TopOfBookEvent} objects.
 * 
 * <p>Construct a <code>TopOfBookEvent</code> by getting a <code>TopOfBookEventBuilder</code>,
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
public abstract class TopOfBookEventBuilder
        extends AbstractEventBuilderImpl<TopOfBookEvent>
{
    /**
     * Returns a <code>TopOfBookEventBuilder</code> suitable for constructing a new <code>TopOfBookEvent</code> object.
     *
     * @return a <code>TopOfBookEventBuilder</code> value
     */
    public static TopOfBookEventBuilder topOfBookEvent()
    {
        return new TopOfBookEventBuilder(){
            @Override
            public TopOfBookEvent create()
            {
                return new TopOfBookEventImpl(getEvent(),
                                              getInstrument(),
                                              getBid(),
                                              getAsk());
            }
        };
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.AbstractEventBuilder#withMessageId(long)
     */
    @Override
    public final TopOfBookEventBuilder withMessageId(long inMessageId)
    {
        super.withMessageId(inMessageId);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.AbstractEventBuilder#withTimestamp(java.util.Date)
     */
    @Override
    public final TopOfBookEventBuilder withTimestamp(Date inTimestamp)
    {
        super.withTimestamp(inTimestamp);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.impl.AbstractEventBuilderImpl#withSource(java.lang.Object)
     */
    @Override
    public TopOfBookEventBuilder withSource(Object inSource)
    {
        super.withSource(inSource);
        return this;
    }
    /**
     * Sets the ask value.
     *
     * @param inAsk an <code>AskEvent</code> value or <code>null</code>
     * @return a <code>TopOfBookEventBuilder</code> value
     */
    public final TopOfBookEventBuilder withAsk(AskEvent inAsk)
    {
        ask = inAsk;
        return this;
    }
    /**
     * Sets the bid value.
     *
     * @param inBid a <code>BidEvent</code> value or <code>null</code>
     * @return a <code>TopOfBookEventBuilder</code> value
     */
    public final TopOfBookEventBuilder withBid(BidEvent inBid)
    {
        bid = inBid;
        return this;
    }
    /**
     * Sets the instrument value.
     *
     * @param inInstrument an <code>Instrument</code> value or <code>null</code>
     * @return a <code>TopOfBookEventBuilder</code> value
     */
    public final TopOfBookEventBuilder withInstrument(Instrument inInstrument)
    {
        instrument = inInstrument;
        return this;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return String.format("TopOfBookEventBuilder [ask=%s, bid=%s, instrument=%s, event=%s]", //$NON-NLS-1$
                             ask,
                             bid,
                             instrument,
                             getEvent());
    }
    /**
     * Get the ask value.
     *
     * @return an <code>AskEvent</code> value
     */
    protected final AskEvent getAsk()
    {
        return ask;
    }
    /**
     * Get the bid value.
     *
     * @return a <code>BidEvent</code> value
     */
    protected final BidEvent getBid()
    {
        return bid;
    }
    /**
     * Get the instrument value.
     *
     * @return a <code>Instrument</code> value
     */
    protected final Instrument getInstrument()
    {
        return instrument;
    }
    /**
     * the ask event
     */
    private AskEvent ask;
    /**
     * the bid event
     */
    private BidEvent bid;
    /**
     * the instrument
     */
    private Instrument instrument;
}
