package org.marketcetera.core.event.impl;

import javax.annotation.concurrent.ThreadSafe;

import org.marketcetera.core.event.EquityEvent;
import org.marketcetera.core.event.MarketstatEvent;
import org.marketcetera.core.event.beans.MarketstatBean;
import org.marketcetera.core.trade.Equity;
import org.marketcetera.api.attributes.ClassVersion;

/* $License$ */

/**
 * Provides an Equity implementation of {@link MarketstatEvent}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: EquityMarketstatEventImpl.java 16063 2012-01-31 18:21:55Z colin $
 * @since 2.0.0
 */
@ThreadSafe
@ClassVersion("$Id: EquityMarketstatEventImpl.java 16063 2012-01-31 18:21:55Z colin $")
class EquityMarketstatEventImpl
        extends AbstractMarketstatEventImpl
        implements EquityEvent
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
