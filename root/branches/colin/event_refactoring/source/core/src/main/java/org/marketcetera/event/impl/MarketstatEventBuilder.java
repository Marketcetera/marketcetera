package org.marketcetera.event.impl;

import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.concurrent.NotThreadSafe;

import org.marketcetera.event.MarketstatEvent;
import org.marketcetera.event.beans.MarketstatBean;
import org.marketcetera.event.beans.OptionBean;
import org.marketcetera.options.ExpirationType;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Constructs {@link MarketstatEvent} objects.
 * 
 * <p>Construct a <code>MarketstatEvent</code> by getting a <code>MarketstatEventBuilder</code>,
 * setting the appropriate attributes on the builder, and calling {@link #create()}.  Note that
 * the builder does no validation.  The object does its own validation with {@link #create()} is
 * called.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@NotThreadSafe
@ClassVersion("$Id$")
public abstract class MarketstatEventBuilder
        extends AbstractEventBuilderImpl
        implements EventBuilder<MarketstatEvent>
{
    /**
     * Returns a <code>MarketstatEventBuilder</code> suitable for constructing a new <code>MarketstatEvent</code> object.
     *
     * <p>The type of marketstat event returned will match the type of the given <code>Instrument</code>,
     * i.e., an Equity-type marketstat event for an {@link Equity}, an Option-type marketstat event for an
     * {@link Option}, etc.
     * 
     * @param inInstrument an <code>Instrument</code> value indicating the type of {@link MarketstatEvent} to create
     * @return a <code>MarketstatEventBuilder</code> value
     * @throws UnsupportedOperationException if the asset class of the given <code>Instrument</code> isn't supported
     */
    public static MarketstatEventBuilder marketstat(Instrument inInstrument)
    {
        if(inInstrument instanceof Equity) {
            return equityMarketstat().withInstrument(inInstrument);
        } else if(inInstrument instanceof Option) {
            return optionMarketstat().withInstrument(inInstrument);
        } else {
            throw new UnsupportedOperationException();
        }
    }
    /**
     * Returns a <code>MarketstatEventBuilder</code> suitable for constructing a new <code>MarketstatEvent</code> object
     * of type <code>Equity</code>.
     *
     * @return a <code>MarketstatEventBuilder</code> value
     * @throw IllegalArgumentException if the value passed to {@link #withInstrument(Instrument)} is not an {@link Equity}
     */
    public static MarketstatEventBuilder equityMarketstat()
    {
        return new MarketstatEventBuilder()
        {
            @Override
            public MarketstatEvent create()
            {
                if(!(getInstrument() instanceof Equity)) {
                    throw new IllegalArgumentException();
                }
                return new EquityMarketstatEventImpl(getMessageId(),
                                                     getTimestamp(),
                                                     (Equity)getInstrument(),
                                                     getMarketstat().getOpen(),
                                                     getMarketstat().getHigh(),
                                                     getMarketstat().getLow(),
                                                     getMarketstat().getClose(),
                                                     getMarketstat().getPreviousClose(),
                                                     getMarketstat().getVolume(),
                                                     getMarketstat().getCloseDate(),
                                                     getMarketstat().getPreviousCloseDate(),
                                                     getMarketstat().getTradeHighTime(),
                                                     getMarketstat().getTradeLowTime(),
                                                     getMarketstat().getOpenExchange(),
                                                     getMarketstat().getHighExchange(),
                                                     getMarketstat().getLowExchange(),
                                                     getMarketstat().getCloseExchange());
            }
        };
    }
    /**
     * Returns a <code>MarketstatEventBuilder</code> suitable for constructing a new <code>MarketstatEvent</code> object
     * of type <code>Option</code>.
     *
     * @return a <code>MarketstatEventBuilder</code> value
     * @throw IllegalArgumentException if the value passed to {@link #withInstrument(Instrument)} is not an {@link Option}
     */
    public static MarketstatEventBuilder optionMarketstat()
    {
        return new MarketstatEventBuilder()
        {
            @Override
            public MarketstatEvent create()
            {
                if(!(getInstrument() instanceof Option)) {
                    throw new IllegalArgumentException();
                }
                return new OptionMarketstatEventImpl(getMessageId(),
                                                     getTimestamp(),
                                                     (Option)getInstrument(),
                                                     getMarketstat().getOpen(),
                                                     getMarketstat().getHigh(),
                                                     getMarketstat().getLow(),
                                                     getMarketstat().getClose(),
                                                     getMarketstat().getPreviousClose(),
                                                     getMarketstat().getVolume(),
                                                     getMarketstat().getCloseDate(),
                                                     getMarketstat().getPreviousCloseDate(),
                                                     getMarketstat().getTradeHighTime(),
                                                     getMarketstat().getTradeLowTime(),
                                                     getMarketstat().getOpenExchange(),
                                                     getMarketstat().getHighExchange(),
                                                     getMarketstat().getLowExchange(),
                                                     getMarketstat().getCloseExchange(),
                                                     getOption().getUnderlyingEquity(),
                                                     getOption().getStrike(),
                                                     getOption().getOptionType(),
                                                     getOption().getExpiry(),
                                                     getOption().hasDeliverable(),
                                                     getOption().getMultiplier(),
                                                     getOption().getExpirationType());
            }
        };
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.AbstractEventBuilder#withMessageId(long)
     */
    @Override
    public MarketstatEventBuilder withMessageId(long inMessageId)
    {
        super.withMessageId(inMessageId);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.AbstractEventBuilder#withTimestamp(java.util.Date)
     */
    @Override
    public MarketstatEventBuilder withTimestamp(Date inTimestamp)
    {
        super.withTimestamp(inTimestamp);
        return this;
    }
    /**
     * Sets the instrument value.
     *
     * @param an <code>Instrument</code> value
     */
    public final MarketstatEventBuilder withInstrument(Instrument inInstrument)
    {
        marketstat.setInstrument(inInstrument);
        return this;
    }
    /**
     * Sets the openPrice value.
     *
     * @param a <code>BigDecimal</code> value
     */
    public final MarketstatEventBuilder withOpenPrice(BigDecimal inOpenPrice)
    {
        marketstat.setOpen(inOpenPrice);
        return this;
    }
    /**
     * Sets the highPrice value.
     *
     * @param a <code>BigDecimal</code> value
     */
    public final MarketstatEventBuilder withHighPrice(BigDecimal inHighPrice)
    {
        marketstat.setHigh(inHighPrice);
        return this;
    }
    /**
     * Sets the lowPrice value.
     *
     * @param a <code>BigDecimal</code> value
     */
    public final MarketstatEventBuilder withLowPrice(BigDecimal inLowPrice)
    {
        marketstat.setLow(inLowPrice);
        return this;
    }
    /**
     * Sets the closePrice value.
     *
     * @param a <code>BigDecimal</code> value
     */
    public final MarketstatEventBuilder withClosePrice(BigDecimal inClosePrice)
    {
        marketstat.setClose(inClosePrice);
        return this;
    }
    /**
     * Sets the previousClosePrice value.
     *
     * @param a <code>BigDecimal</code> value
     */
    public final MarketstatEventBuilder withPreviousClosePrice(BigDecimal inPreviousClosePrice)
    {
        marketstat.setPreviousClose(inPreviousClosePrice);
        return this;
    }
    /**
     * Sets the volume value.
     *
     * @param a <code>BigDecimal</code> value
     */
    public final MarketstatEventBuilder withVolume(BigDecimal inVolume)
    {
        marketstat.setVolume(inVolume);
        return this;
    }
    /**
     * Sets the closeDate value.
     *
     * @param a <code>String</code> value
     */
    public final MarketstatEventBuilder withCloseDate(String inCloseDate)
    {
        marketstat.setCloseDate(inCloseDate);
        return this;
    }
    /**
     * Sets the previousCloseDate value.
     *
     * @param a <code>String</code> value
     */
    public final MarketstatEventBuilder withPreviousCloseDate(String inPreviousCloseDate)
    {
        marketstat.setPreviousCloseDate(inPreviousCloseDate);
        return this;
    }
    /**
     * Sets the tradeHighTime value.
     *
     * @param a <code>String</code> value
     */
    public final MarketstatEventBuilder withTradeHighTime(String inTradeHighTime)
    {
        marketstat.setTradeHighTime(inTradeHighTime);
        return this;
    }
    /**
     * Sets the tradeLowTime value.
     *
     * @param a <code>String</code> value
     */
    public final MarketstatEventBuilder withTradeLowTime(String inTradeLowTime)
    {
        marketstat.setTradeLowTime(inTradeLowTime);
        return this;
    }
    /**
     * Sets the openExchange value.
     *
     * @param a <code>String</code> value
     */
    public final MarketstatEventBuilder withOpenExchange(String inOpenExchange)
    {
        marketstat.setOpenExchange(inOpenExchange);
        return this;
    }
    /**
     * Sets the highExchange value.
     *
     * @param a <code>String</code> value
     */
    public final MarketstatEventBuilder withHighExchange(String inHighExchange)
    {
        marketstat.setHighExchange(inHighExchange);
        return this;
    }
    /**
     * Sets the lowExchange value.
     *
     * @param a <code>String</code> value
     */
    public final MarketstatEventBuilder withLowExchange(String inLowExchange)
    {
        marketstat.setLowExchange(inLowExchange);
        return this;
    }
    /**
     * Sets the closeExchange value.
     *
     * @param a <code>String</code> value
     */
    public final MarketstatEventBuilder withCloseExchange(String inCloseExchange)
    {
        marketstat.setCloseExchange(inCloseExchange);
        return this;
    }
    /**
     * Sets the underlyingEquity value.
     *
     * @param a <code>Equity</code> value
     */
    public final MarketstatEventBuilder withUnderlyingEquity(Equity inUnderlyingEquity)
    {
        option.setUnderlyingEquity(inUnderlyingEquity);
        return this;
    }
    /**
     * Sets the expiry value.
     *
     * @param a <code>String</code> value
     */
    public final MarketstatEventBuilder withExpiry(String inExpiry)
    {
        option.setExpiry(inExpiry);
        return this;
    }
    /**
     * Sets the optionType value.
     *
     * @param a <code>OptionType</code> value
     */
    public final MarketstatEventBuilder ofOptionType(OptionType inOptionType)
    {
        option.setOptionType(inOptionType);
        return this;
    }
    /**
     * Sets the expirationType value.
     *
     * @param a <code>ExpirationType</code> value
     */
    public final MarketstatEventBuilder ofExpirationType(ExpirationType inExpirationType)
    {
        option.setExpirationType(inExpirationType);
        return this;
    }
    /**
     * Sets the multiplier value.
     *
     * @param a <code>int</code> value
     */
    public final MarketstatEventBuilder withMultiplier(int inMultiplier)
    {
        option.setMultiplier(inMultiplier);
        return this;
    }
    /**
     * Sets the hasDeliverable value.
     *
     * @param a <code>boolean</code> value
     */
    public final MarketstatEventBuilder hasDeliverable(boolean inHasDeliverable)
    {
        option.setHasDeliverable(inHasDeliverable);
        return this;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("MarketstatEventBuilder [marketstat=").append(marketstat).append(", option=").append(option) //$NON-NLS-1$ //$NON-NLS-2$
                .append(", getMessageId()=").append(getMessageId()).append(", getTimestamp()=").append(getTimestamp()) //$NON-NLS-1$ //$NON-NLS-2$
                .append("]"); //$NON-NLS-1$
        return builder.toString();
    }
    /**
     * Get the marketstat value.
     *
     * @return a <code>MarketstatBean</code> value
     */
    protected final MarketstatBean getMarketstat()
    {
        return marketstat;
    }
    /**
     * Gets the instrument value.
     *
     * @return an <code>Instrument</code> value
     */
    protected final Instrument getInstrument()
    {
        return marketstat.getInstrument();
    }
    /**
     * Gets the option value.
     *
     * @return an <code>OptionBean</code> value
     */
    protected final OptionBean getOption()
    {
        return option;
    }
    /**
     * the marketstat attributes 
     */
    private final MarketstatBean marketstat = new MarketstatBean();
    /**
     *  the option attributes
     */
    private final OptionBean option = new OptionBean();
}
