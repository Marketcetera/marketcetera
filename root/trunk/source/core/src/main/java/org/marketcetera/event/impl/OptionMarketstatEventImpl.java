package org.marketcetera.event.impl;

import java.math.BigDecimal;

import javax.annotation.concurrent.ThreadSafe;

import org.marketcetera.event.HasOption;
import org.marketcetera.event.MarketstatEvent;
import org.marketcetera.event.OptionMarketstatEvent;
import org.marketcetera.event.beans.MarketstatBean;
import org.marketcetera.event.beans.OptionBean;
import org.marketcetera.options.ExpirationType;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides an Option representation of {@link MarketstatEvent}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.0.0
 */
@ThreadSafe
@ClassVersion("$Id$")
class OptionMarketstatEventImpl
        extends AbstractMarketstatEventImpl
        implements HasOption, OptionMarketstatEvent
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
     * @see org.marketcetera.event.HasOption#getInstrument()
     */
    @Override
    public Option getInstrument()
    {
        return option.getInstrument();
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
     * @see org.marketcetera.event.OptionEvent#getOpraSymbol()
     */
    @Override
    public String getProviderSymbol()
    {
        return option.getProviderSymbol();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.OptionMarketstatEvent#getInterestChange()
     */
    @Override
    public BigDecimal getInterestChange()
    {
        return interestChange;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.OptionMarketstatEvent#getVolumeChange()
     */
    @Override
    public BigDecimal getVolumeChange()
    {
        return volumeChange;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append(super.toString());
        builder.append(option.toString());
        builder.append(" volumeChange=").append(getVolumeChange()); //$NON-NLS-1$
        builder.append(" interestChange=").append(getInterestChange()); //$NON-NLS-1$
        return builder.toString();
    }
    /**
     * Create a new OptionMarketstatEventImpl instance.
     *
     * @param inMarketstatBean a <code>MarketstatBean</code> value
     * @param inOptionBean an <code>OptionBean</code> value
     * @throws IllegalArgumentException if <code>MessageId</code> &lt; 0
     * @throws IllegalArgumentException if <code>Timestamp</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Instrument</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>UnderlyingInstrument</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>ExpirationType</code> is <code>null</code>
     */
    OptionMarketstatEventImpl(MarketstatBean inMarketstat,
                              OptionBean inOption,
                              BigDecimal inVolumeChange,
                              BigDecimal inInterestChange)
    {
        super(inMarketstat);
        option = OptionBean.copy(inOption);
        option.validate();
        volumeChange = inVolumeChange;
        interestChange = inInterestChange;
    }
    /**
     * the option attributes
     */
    private final OptionBean option;
    /**
     * the change in volume since the previous close, may be <code>null</code> 
     */
    private final BigDecimal volumeChange;
    /**
     * the change in interest since the previous close, may be <code>null</code>
     */
    private final BigDecimal interestChange;
    private static final long serialVersionUID = 1L;
}
