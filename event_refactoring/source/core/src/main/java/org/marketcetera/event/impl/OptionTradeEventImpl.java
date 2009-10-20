package org.marketcetera.event.impl;

import java.math.BigDecimal;
import java.util.Date;

import org.marketcetera.event.OptionEvent;
import org.marketcetera.event.beans.OptionBean;
import org.marketcetera.options.ExpirationType;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
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
     * @see org.marketcetera.event.HasOption#getOption()
     */
    @Override
    public Option getOption()
    {
        return option.getInstrument();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.HasUnderlyingEquity#getUnderlyingEquity()
     */
    @Override
    public Equity getUnderlyingEquity()
    {
        return option.getUnderlyingEquity();
    }
    /**
     * Create a new OptionAskEventImpl instance.
     *
     * @param inMessageId
     * @param inTimestamp
     * @param inOption
     * @param inExchange
     * @param inPrice
     * @param inSize
     * @param inTradeTime
     * @param inUnderlyingEquity
     * @param inStrike
     * @param inOptionType
     * @param inExpiry
     * @param inHasDeliverable
     * @param inMultiplier
     * @param inExpirationType
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
    }
    /**
     * option attributes
     */
    private final OptionBean option = new OptionBean();
    private static final long serialVersionUID = 1L;
}
