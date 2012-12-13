package org.marketcetera.core.event.impl;

import javax.annotation.concurrent.ThreadSafe;

import org.marketcetera.core.event.ConvertibleBondEvent;
import org.marketcetera.core.event.MarketstatEvent;
import org.marketcetera.core.event.beans.ConvertibleBondBean;
import org.marketcetera.core.event.beans.MarketstatBean;
import org.marketcetera.core.trade.*;

/* $License$ */

/**
 * Provides a ConvertibleBond implementation of {@link MarketstatEvent}.
 *
 * @version $Id$
 * @since 2.1.0
 */
@ThreadSafe
class ConvertibleBondMarketstatEventImpl
        extends AbstractMarketstatEventImpl
        implements ConvertibleBondEvent
{
    /* (non-Javadoc)
     * @see org.marketcetera.event.HasConvertibleBond#getInstrument()
     */
    @Override
    public ConvertibleBond getInstrument()
    {
        return (ConvertibleBond)super.getInstrument();
    }
    /**
     * Create a new ConvertibleBondMarketstatEventImpl instance.
     *
     * @param inMarketstatBean a <code>MarketstatBean</code> value
     * @throws IllegalArgumentException if <code>MessageId</code> &lt; 0
     * @throws IllegalArgumentException if <code>Timestamp</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Instrument</code> is <code>null</code>
     */
    ConvertibleBondMarketstatEventImpl(MarketstatBean inMarketstat,
                                       ConvertibleBondBean inConvertibleBond)
    {
        super(inMarketstat);
        bond = inConvertibleBond;
        bond.validate();
    }
    /**
     * the bond attributes 
     */
    private final ConvertibleBondBean bond;
    private static final long serialVersionUID = 1L;
}
