package org.marketcetera.core.event.impl;

import javax.annotation.concurrent.ThreadSafe;

import org.marketcetera.core.event.ConvertibleBondEvent;
import org.marketcetera.core.event.TradeEvent;
import org.marketcetera.core.event.beans.ConvertibleBondBean;
import org.marketcetera.core.event.beans.MarketDataBean;
import org.marketcetera.core.trade.*;

/* $License$ */

/**
 * Provides a ConvertibleBond implementation of {@link TradeEvent}.
 *
 * @version $Id$
 * @since 2.1.0
 */
@ThreadSafe
final class ConvertibleBondTradeEventImpl
        extends AbstractTradeEventImpl
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
     * Create a new ConvertibleBondTradeEventImpl instance.
     *
     * @param inMarketData a <code>MarketDataBean</code> value
     * @throws IllegalArgumentException if <code>MessageId</code> &lt; 0
     * @throws IllegalArgumentException if <code>Timestamp</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Instrument</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Price</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Size</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Exchange</code> is <code>null</code> or empty
     * @throws IllegalArgumentException if <code>ExchangeTimestamp</code> is <code>null</code> or empty
     */
    ConvertibleBondTradeEventImpl(MarketDataBean inMarketData,
                         ConvertibleBondBean inConvertibleBond)
    {
        super(inMarketData);
        convertibleBond = inConvertibleBond;
        convertibleBond.validate();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.impl.AbstractQuoteEventImpl#getDescription()
     */
    @Override
    protected String getDescription()
    {
        return description;
    }
    /**
     * provides a human-readable description of this event type (does not need to be localized)
     */
    private static final String description = "Convertible Bond Trade"; //$NON-NLS-1$
    /**
     * the convertible bond attributes 
     */
    private final ConvertibleBondBean convertibleBond;
    private static final long serialVersionUID = 1L;
}
