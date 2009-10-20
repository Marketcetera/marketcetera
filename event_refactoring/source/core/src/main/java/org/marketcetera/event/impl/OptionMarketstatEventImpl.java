package org.marketcetera.event.impl;

import java.math.BigDecimal;
import java.util.Date;

import org.marketcetera.event.HasOption;
import org.marketcetera.event.OptionEvent;
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
class OptionMarketstatEventImpl
        extends MarketstatEventImpl
        implements HasOption, OptionEvent
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
        return option.getOption();
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
     * Create a new OptionMarketstatEventImpl instance.
     *
     * @param inMessageId
     * @param inTimestamp
     * @param inInstrument
     * @param inOpenPrice
     * @param inHighPrice
     * @param inLowPrice
     * @param inClosePrice
     * @param inPreviousClosePrice
     * @param inCloseDate
     * @param inPreviousCloseDate
     * @param inTradeHighTime
     * @param inTradeLowTime
     * @param inOpenExchange
     * @param inHighExchange
     * @param inLowExchange
     * @param inCloseExchange
     * @param inUnderlyingEquity
     * @param inStrike
     * @param inOptionType
     * @param inExpiry
     * @param inHasDeliverable
     * @param inMultiplier
     * @param inExpirationType
     * @throws EventValidationException 
     */
    OptionMarketstatEventImpl(long inMessageId,
                              Date inTimestamp,
                              Option inInstrument,
                              BigDecimal inOpenPrice,
                              BigDecimal inHighPrice,
                              BigDecimal inLowPrice,
                              BigDecimal inClosePrice,
                              BigDecimal inPreviousClosePrice,
                              String inCloseDate,
                              String inPreviousCloseDate,
                              String inTradeHighTime,
                              String inTradeLowTime,
                              String inOpenExchange,
                              String inHighExchange,
                              String inLowExchange,
                              String inCloseExchange,
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
              (Option)inInstrument,
              inOpenPrice,
              inHighPrice,
              inLowPrice,
              inClosePrice,
              inPreviousClosePrice,
              inCloseDate,
              inPreviousCloseDate,
              inTradeHighTime,
              inTradeLowTime,
              inOpenExchange,
              inHighExchange,
              inLowExchange,
              inCloseExchange);
        option = new OptionEventImpl(inInstrument,
                                     inUnderlyingEquity,
                                     inStrike,
                                     inOptionType,
                                     inExpiry,
                                     inHasDeliverable,
                                     inMultiplier,
                                     inExpirationType);
    }
    /**
     * 
     */
    private final OptionEventImpl option;
    private static final long serialVersionUID = 1L;
}
