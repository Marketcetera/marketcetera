package org.marketcetera.event.impl;

import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.concurrent.ThreadSafe;

import org.marketcetera.event.OptionEvent;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.event.beans.OptionBean;
import org.marketcetera.options.ExpirationType;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides an Option implementation of {@link TradeEvent}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ThreadSafe
@ClassVersion("$Id$")
final class OptionTradeEventImpl
        extends TradeEventImpl
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
     * @see org.marketcetera.event.OptionEvent#getExpiry()
     */
    @Override
    public String getExpiry()
    {
        return option.getExpiry();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.OptionEvent#getMultiplier()
     */
    @Override
    public int getMultiplier()
    {
        return option.getMultiplier();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.OptionEvent#getOptionType()
     */
    @Override
    public OptionType getOptionType()
    {
        return option.getOptionType();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.OptionEvent#getStrike()
     */
    @Override
    public BigDecimal getStrike()
    {
        return option.getStrike();
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
     * @see org.marketcetera.event.HasUnderlyingEquity#getUnderlyingEquity()
     */
    @Override
    public Equity getUnderlyingEquity()
    {
        return option.getUnderlyingEquity();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.HasOption#getOption()
     */
    @Override
    public Option getOption()
    {
        return (Option)getInstrument();
    }
    /**
     * Create a new OptionTradeEventImpl instance.
     *
     * @param inMessageId a <code>long</code> value
     * @param inTimestamp a <code>Date</code> value
     * @param inInstrument an <code>Option</code> value
     * @param inExchange a <code>String</code> value
     * @param inPrice a <code>BigDecimal</code> value
     * @param inSize a <code>BigDecimal</code> value
     * @param inTradeTime a <code>String</code> value
     * @throws IllegalArgumentException if <code>inMessageId</code> &lt; 0
     * @throws IllegalArgumentException if <code>inTimestamp</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>inOption</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>inExchange</code> is <code>null</code> or empty
     * @throws IllegalArgumentException if <code>inPrice</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>inSize</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>inTradeTime</code> is <code>null</code> or empty
     * @throws IllegalArgumentException if <code>inUnderlyingEquity</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>inStrike</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>inOptionType</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>inExpiry</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>inExpirationType</code> is <code>null</code>
     */
    OptionTradeEventImpl(long inMessageId,
                         Date inTimestamp,
                         Option inOption,
                         String inExchange,
                         BigDecimal inPrice,
                         BigDecimal inSize,
                         String inTradeTime,
                         Equity inUnderlyingEquity,
                         BigDecimal inStrike,
                         OptionType inOptionType,
                         String inExpiry,
                         boolean inHasDeliverable,
                         int inMultiplier,
                         ExpirationType inExpirationType)
    {
        super(inMessageId,
              inTimestamp,
              inOption,
              inExchange,
              inPrice,
              inSize,
              inTradeTime);
        option.setInstrument(inOption);
        option.setUnderlyingEquity(inUnderlyingEquity);
        option.setStrike(inStrike);
        option.setOptionType(inOptionType);
        option.setExpiry(inExpiry);
        option.setHasDeliverable(inHasDeliverable);
        option.setMultiplier(inMultiplier);
        option.setExpirationType(inExpirationType);
        option.validate();
    }
    /**
     * the option attributes 
     */
    private final OptionBean option = new OptionBean();
    private static final long serialVersionUID = 1L;
}
