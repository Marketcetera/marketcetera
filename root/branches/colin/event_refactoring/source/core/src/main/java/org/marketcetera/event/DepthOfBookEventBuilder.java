package org.marketcetera.event;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.marketcetera.event.beans.InstrumentBean;
import org.marketcetera.trade.Instrument;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class DepthOfBookEventBuilder
        extends EventBuilderImpl
        implements EventBuilder<DepthOfBookEvent>
{
    /**
     * 
     *
     *
     * @return
     */
    public static DepthOfBookEventBuilder equityDepthOfBook()
    {
        return new DepthOfBookEventBuilder(){
            @Override
            public DepthOfBookEvent create()
            {
                return new DepthOfBookEventImpl(getMessageId(),
                                                getTimestamp(),
                                                getBids(),
                                                getAsks(),
                                                getInstrument().getInstrument());
            }
        };
    }
    /**
     * 
     *
     *
     * @return
     */
    public static DepthOfBookEventBuilder optionDepthOfBook()
    {
        return new DepthOfBookEventBuilder(){
            @Override
            public DepthOfBookEvent create()
            {
                return new DepthOfBookEventImpl(getMessageId(),
                                                getTimestamp(),
                                                getBids(),
                                                getAsks(),
                                                getInstrument().getInstrument());
            }
        };
    }
    /**
     * 
     *
     *
     * @param inBids
     * @return
     */
    public final DepthOfBookEventBuilder withBids(List<BidEvent> inBids)
    {
        bids.clear();
        bids.addAll(inBids);
        return this;
    }
    /**
     * 
     *
     *
     * @param inAsks
     * @return
     */
    public final DepthOfBookEventBuilder withAsks(List<AskEvent> inAsks)
    {
        asks.clear();
        asks.addAll(inAsks);
        return this;
    }
    /**
     * 
     *
     *
     * @param inInstrument
     * @return
     */
    public final DepthOfBookEventBuilder withInstrument(Instrument inInstrument)
    {
        instrument.setInstrument(inInstrument);
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
    /**
     * Get the instrument value.
     *
     * @return a <code>InstrumentBean</code> value
     */
    protected final InstrumentBean getInstrument()
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
     * 
     */
    private final InstrumentBean instrument = new InstrumentBean();
    /**
     * 
     */
    private final List<BidEvent> bids = new ArrayList<BidEvent>();
    /**
     * 
     */
    private final List<AskEvent> asks = new ArrayList<AskEvent>();
}
