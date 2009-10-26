package org.marketcetera.event.impl;

import javax.annotation.concurrent.ThreadSafe;

import org.marketcetera.event.HasEquity;
import org.marketcetera.event.MarketstatEvent;
import org.marketcetera.event.beans.MarketstatBean;
import org.marketcetera.trade.Equity;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides an Equity implementation of {@link MarketstatEvent}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ThreadSafe
@ClassVersion("$Id$")
class EquityMarketstatEventImpl
        extends AbstractMarketstatEventImpl
        implements HasEquity
{
    /* (non-Javadoc)
     * @see org.marketcetera.event.HasEquity#getInstrument()
     */
    @Override
    public Equity getInstrument()
    {
        return (Equity)super.getInstrument();
    }
    /**
     * Create a new EquityMarketstatEventImpl instance.
     *
     * @param inMarketstatBean a <code>MarketstatBean</code> value
     * @throws IllegalArgumentException if <code>MessageId</code> &lt; 0
     * @throws IllegalArgumentException if <code>Timestamp</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Instrument</code> is <code>null</code>
     */
    EquityMarketstatEventImpl(MarketstatBean inMarketstat)
    {
        super(inMarketstat);
    }
    private static final long serialVersionUID = 1L;
}
