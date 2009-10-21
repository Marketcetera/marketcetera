package org.marketcetera.event.impl;

import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.concurrent.ThreadSafe;

import org.marketcetera.event.HasOption;
import org.marketcetera.event.MarketstatEvent;
import org.marketcetera.event.OptionEvent;
import org.marketcetera.event.beans.OptionBean;
import org.marketcetera.options.ExpirationType;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides an Option representation of {@link MarketstatEvent}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ThreadSafe
@ClassVersion("$Id$")
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
     * Create a new OptionMarketstatEventImpl instance.
     *
     * @param inMessageId a <code>long</code> value
     * @param inTimestamp a <code>Date</code> value
     * @param inOption an <code>Instrument</code> value
     * @param inOpenPrice a <code>BigDecimal</code> value
     * @param inHighPrice a <code>BigDecimal</code> value
     * @param inLowPrice a <code>BigDecimal</code> value
     * @param inClosePrice a <code>BigDecimal</code> value
     * @param inPreviousClosePrice a <code>BigDecimal</code> value
     * @param inCloseDate a <code>String</code> value
     * @param inPreviousCloseDate a <code>String</code> value
     * @param inTradeHighTime a <code>String</code> value
     * @param inTradeLowTime a <code>String</code> value
     * @param inOpenExchange a <code>String</code> value
     * @param inHighExchange a <code>String</code> value
     * @param inLowExchange a <code>String</code> value
     * @param inCloseExchange a <code>String</code> value
     * @param inUnderlyingEquity an <code>Equity</code> value
     * @param inStrike a <code>BigDecimal</code> value
     * @param inOptionType an <code>OptionType</code> value
     * @param inExpiry a <code>String</code> value
     * @param inHasDeliverable a <code>boolean</code> value
     * @param inMultiplier an <code>int</code> value
     * @param inExpirationType an <code>ExpirationType</code> value
     * @param inAction a <code>QuoteAction</code> value
     * @throws IllegalArgumentException if <code>inMessageId</code> &lt; 0
     * @throws IllegalArgumentException if <code>inTimestamp</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>inInstrument</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>inUnderlyingEquity</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>inExpiry</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>inStrike</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>inOptionType</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>inExpirationType</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>inQuoteAction</code> is <code>null</code>
     */
    OptionMarketstatEventImpl(long inMessageId,
                              Date inTimestamp,
                              Option inOption,
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
              inOption,
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
        option.setExpirationType(inExpirationType);
        option.setExpiry(inExpiry);
        option.setHasDeliverable(inHasDeliverable);
        option.setInstrument(inOption);
        option.setMultiplier(inMultiplier);
        option.setOptionType(inOptionType);
        option.setStrike(inStrike);
        option.setUnderlyingEquity(inUnderlyingEquity);
        option.validate();
    }
    /**
     * the option attributes
     */
    private final OptionBean option = new OptionBean();
    private static final long serialVersionUID = 1L;
}
