package org.marketcetera.event.impl;

import java.math.BigDecimal;
import java.util.Date;

import org.marketcetera.event.MarketstatEvent;
import org.marketcetera.event.beans.InstrumentBean;
import org.marketcetera.event.beans.MarketstatBean;
import org.marketcetera.event.beans.OptionBean;
import org.marketcetera.options.ExpirationType;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Instrument;
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
public abstract class MarketstatEventBuilder
        extends EventBuilderImpl
        implements EventBuilder<MarketstatEvent>
{
    public static MarketstatEventBuilder newEvent(Instrument inInstrument)
    {
        if(inInstrument instanceof Equity) {
            return newEquityMarketstatEvent().withInstrument(inInstrument);
        } else if(inInstrument instanceof Option) {
            return newOptionMarketstatEvent().withInstrument(inInstrument);
        } else {
            throw new UnsupportedOperationException();
        }
    }
    /**
     * 
     *
     *
     * @return
     */
    public static MarketstatEventBuilder newEquityMarketstatEvent()
    {
        return new MarketstatEventBuilder()
        {
            @Override
            public MarketstatEvent create()
            {
                return new EquityMarketstatEventImpl(getMessageId(),
                                                     getTimestamp(),
                                                     (Equity)getInstrument().getInstrument(),
                                                     getMarketstat().getOpenPrice(),
                                                     getMarketstat().getHighPrice(),
                                                     getMarketstat().getLowPrice(),
                                                     getMarketstat().getClosePrice(),
                                                     getMarketstat().getPreviousClosePrice(),
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
     * 
     *
     *
     * @return
     */
    public static MarketstatEventBuilder newOptionMarketstatEvent()
    {
        return new MarketstatEventBuilder()
        {
            @Override
            public MarketstatEvent create()
            {
                return new OptionMarketstatEventImpl(getMessageId(),
                                                     getTimestamp(),
                                                     (Option)getInstrument().getInstrument(),
                                                     getMarketstat().getOpenPrice(),
                                                     getMarketstat().getHighPrice(),
                                                     getMarketstat().getLowPrice(),
                                                     getMarketstat().getClosePrice(),
                                                     getMarketstat().getPreviousClosePrice(),
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
                                                     getOption().getHasDeliverable(),
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
     * @param a <code>I</code> value
     */
    public final MarketstatEventBuilder withInstrument(Instrument inInstrument)
    {
        instrument.setInstrument(inInstrument);
        return this;
    }
    /**
     * Sets the openPrice value.
     *
     * @param a <code>BigDecimal</code> value
     */
    public final MarketstatEventBuilder withOpenPrice(BigDecimal inOpenPrice)
    {
        marketstat.setOpenPrice(inOpenPrice);
        return this;
    }
    /**
     * Sets the highPrice value.
     *
     * @param a <code>BigDecimal</code> value
     */
    public final MarketstatEventBuilder withHighPrice(BigDecimal inHighPrice)
    {
        marketstat.setHighPrice(inHighPrice);
        return this;
    }
    /**
     * Sets the lowPrice value.
     *
     * @param a <code>BigDecimal</code> value
     */
    public final MarketstatEventBuilder withLowPrice(BigDecimal inLowPrice)
    {
        marketstat.setLowPrice(inLowPrice);
        return this;
    }
    /**
     * Sets the closePrice value.
     *
     * @param a <code>BigDecimal</code> value
     */
    public final MarketstatEventBuilder withClosePrice(BigDecimal inClosePrice)
    {
        marketstat.setClosePrice(inClosePrice);
        return this;
    }
    /**
     * Sets the previousClosePrice value.
     *
     * @param a <code>BigDecimal</code> value
     */
    public final MarketstatEventBuilder withPreviousClosePrice(BigDecimal inPreviousClosePrice)
    {
        marketstat.setPreviousClosePrice(inPreviousClosePrice);
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
     * 
     *
     *
     * @return
     */
    protected final InstrumentBean getInstrument()
    {
        return instrument;
    }
    /**
     * 
     *
     *
     * @return
     */
    protected final OptionBean getOption()
    {
        return option;
    }
    /**
     * 
     */
    private InstrumentBean instrument = new InstrumentBean();
    /**
     * 
     */
    private MarketstatBean marketstat = new MarketstatBean();
    /**
     * 
     */
    private OptionBean option = new OptionBean();
}
