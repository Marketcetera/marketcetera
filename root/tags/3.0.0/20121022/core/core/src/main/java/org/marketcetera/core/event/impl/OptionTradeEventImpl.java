package org.marketcetera.core.event.impl;

import java.math.BigDecimal;

import javax.annotation.concurrent.ThreadSafe;
import org.marketcetera.core.event.OptionEvent;
import org.marketcetera.core.event.beans.MarketDataBean;
import org.marketcetera.core.event.beans.OptionBean;
import org.marketcetera.core.options.ExpirationType;
import org.marketcetera.core.trade.Instrument;
import org.marketcetera.core.trade.Option;

/* $License$ */

/**
 * Provides an Option implementation of {@link org.marketcetera.core.event.TradeEvent}.
 *
 * @version $Id: OptionTradeEventImpl.java 16063 2012-01-31 18:21:55Z colin $
 * @since 2.0.0
 */
@ThreadSafe
final class OptionTradeEventImpl
        extends AbstractTradeEventImpl
        implements OptionEvent
{
    /* (non-Javadoc)
     * @see org.marketcetera.event.OptionEvent#getExpirationType()
     */
    @Override
    public ExpirationType getExpirationType()
    {
        return option.getExpirationType();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.OptionEvent#getMultiplier()
     */
    @Override
    public BigDecimal getMultiplier()
    {
        return option.getMultiplier();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.OptionEvent#hasDeliverable()
     */
    @Override
    public boolean hasDeliverable()
    {
        return option.hasDeliverable();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.HasUnderlyingInstrument#getUnderlyingInstrument()
     */
    @Override
    public Instrument getUnderlyingInstrument()
    {
        return option.getUnderlyingInstrument();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.HasOption#getInstrument()
     */
    @Override
    public Option getInstrument()
    {
        return (Option)super.getInstrument();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.OptionEvent#getOpraSymbol()
     */
    @Override
    public String getProviderSymbol()
    {
        return option.getProviderSymbol();
    }
    /**
     * Create a new OptionTradeEventImpl instance.
     *
     * @param inMarketData a <code>MarketDataBean</code> value
     * @param inOption an <code>OptionBean</code> value
     * @throws IllegalArgumentException if <code>MessageId</code> &lt; 0
     * @throws IllegalArgumentException if <code>Timestamp</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Instrument</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Price</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Size</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Exchange</code> is <code>null</code> or empty
     * @throws IllegalArgumentException if <code>ExchangeTimestamp</code> is <code>null</code> or empty
     * @throws IllegalArgumentException if <code>Action</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>UnderlyingInstrument</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>ExpirationType</code> is <code>null</code>
     */
    OptionTradeEventImpl(MarketDataBean inMarketData,
                         OptionBean inOption)
    {
        super(inMarketData);
        option = OptionBean.copy(inOption);
        option.validate();
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
    private static final String description = "Option Trade"; //$NON-NLS-1$
    /**
     * the option attributes 
     */
    private final OptionBean option;
    private static final long serialVersionUID = 1L;
}
