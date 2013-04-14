package org.marketcetera.core.event.impl;

import javax.annotation.concurrent.ThreadSafe;

import org.marketcetera.core.event.ConvertibleSecurityEvent;
import org.marketcetera.core.event.TradeEvent;
import org.marketcetera.core.event.beans.ConvertibleSecurityBean;
import org.marketcetera.core.event.beans.MarketDataBean;
import org.marketcetera.core.trade.*;

/* $License$ */

/**
 * Provides a ConvertibleSecurity implementation of {@link TradeEvent}.
 *
 * @version $Id$
 * @since 2.1.0
 */
@ThreadSafe
final class ConvertibleSecurityTradeEventImpl
        extends AbstractTradeEventImpl
        implements ConvertibleSecurityEvent
{
    /* (non-Javadoc)
     * @see org.marketcetera.event.HasConvertibleSecurity#getInstrument()
     */
    @Override
    public ConvertibleSecurity getInstrument()
    {
        return (ConvertibleSecurity)super.getInstrument();
    }
    /**
     * Create a new ConvertibleSecurityTradeEventImpl instance.
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
    ConvertibleSecurityTradeEventImpl(MarketDataBean inMarketData,
                         ConvertibleSecurityBean inConvertibleSecurity)
    {
        super(inMarketData);
        convertibleSecurity = inConvertibleSecurity;
        convertibleSecurity.validate();
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
    private static final String description = "Convertible Security Trade"; //$NON-NLS-1$
    /**
     * the convertible security attributes 
     */
    private final ConvertibleSecurityBean convertibleSecurity;
    private static final long serialVersionUID = 1L;
}
