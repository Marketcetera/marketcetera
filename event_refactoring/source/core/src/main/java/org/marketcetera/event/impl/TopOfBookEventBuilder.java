package org.marketcetera.event.impl;

import java.util.Date;

import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.TopOfBookEvent;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class TopOfBookEventBuilder
        extends EventBuilderImpl
        implements EventBuilder<TopOfBookEvent>
{
    public static TopOfBookEventBuilder newTopOfBook()
    {
        return new TopOfBookEventBuilder(){
            @Override
            public TopOfBookEvent create()
            {
                return new TopOfBookEventImpl(getMessageId(),
                                              getTimestamp(),
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
    /**
     * Sets the ask value.
     *
     * @param a <code>AskEvent</code> value
     */
    public final TopOfBookEventBuilder withAsk(AskEvent inAsk)
    {
        ask = inAsk;
        return this;
    }
    /**
     * Sets the bid value.
     *
     * @param a <code>BidEvent</code> value
     */
    public final TopOfBookEventBuilder withBid(BidEvent inBid)
    {
        bid = inBid;
        return this;
    }
    /**
     * Get the ask value.
     *
     * @return a <code>AskEvent</code> value
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
     * 
     */
    private AskEvent ask;
    /**
     * 
     */
    private BidEvent bid;
}
