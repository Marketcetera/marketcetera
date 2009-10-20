package org.marketcetera.event.impl;

import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.concurrent.NotThreadSafe;

import org.marketcetera.event.TradeEvent;
import org.marketcetera.event.beans.MarketDataBean;
import org.marketcetera.event.beans.OptionBean;
import org.marketcetera.options.ExpirationType;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Constructs {@link TradeEvent} objects.
 * 
 * <p>Construct a <code>TradeEvent</code> by getting a <code>TradeEventBuilder</code>,
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
public abstract class TradeEventBuilder
        extends EventBuilderImpl
        implements EventBuilder<TradeEvent>
{
    /**
     * Returns a <code>TradeEventBuilder</code> suitable for constructing a new <code>TradeEvent</code> object.
     *
     * <p>The type of <code>TradeEvent</code> returned will match the type of the given <code>Instrument</code>,
     * i.e., an Equity-type <code>TradeEvent</code> for an {@link Equity}, an Option-type <code>TradeEvent</code> for an
     * {@link Option}, etc.
     * 
     * @param inInstrument an <code>Instrument</code> value indicating the type of {@link TradeEvent} to create
     * @return a <code>QuoteEventBuilder&lt;TradeEvent&gt;</code> value
     * @throws UnsupportedOperationException if the asset class of the given <code>Instrument</code> isn't supported
     */
    public static TradeEventBuilder tradeEvent(Instrument inInstrument)
    {
        if(inInstrument instanceof Equity) {
            return equityTradeEvent().withInstrument(inInstrument);
        } else if(inInstrument instanceof Option) {
            return optionTradeEvent().withInstrument(inInstrument);
        } else {
            throw new UnsupportedOperationException();
        }
    }
    /**
     * Returns a <code>TradeEventBuilder</code> suitable for constructing a new Equity <code>TradeEvent</code> object.
     *
     * @return a <code>TradeEventBuilder</code> value
     * @throw IllegalArgumentException if the value passed to {@link #withInstrument(Instrument)} is not an {@link Equity}
     */
    public static TradeEventBuilder equityTradeEvent()
    {
        return new TradeEventBuilder() {
            /* (non-Javadoc)
             * @see org.marketcetera.event.EventBuilder#create()
             */
            @Override
            public TradeEvent create()
            {
                if(getMarketData().getInstrument() instanceof Equity) {
                    return new TradeEventImpl(getMessageId(),
                                                    getTimestamp(),
                                                    (Equity)getMarketData().getInstrument(),
                                                    getMarketData().getExchange(),
                                                    getMarketData().getPrice(),
                                                    getMarketData().getSize(),
                                                    getMarketData().getExchangeTimestamp());
                }
                throw new IllegalArgumentException();
            }
        };
    }
    /**
     * Returns a <code>TradeEventBuilder</code> suitable for constructing a new Option <code>TradeEvent</code> object.
     *
     * @return a <code>TradeEventBuilder</code> value
     * @throw IllegalArgumentException if the value passed to {@link #withInstrument(Instrument)} is not an {@link Option}
     */
    public static TradeEventBuilder optionTradeEvent()
    {
        return new TradeEventBuilder() {
            @Override
            public TradeEvent create()
            {
                // TODO Auto-generated method stub
                return null;
            }
        };
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.AbstractEventBuilder#withMessageId(long)
     */
    @Override
    public TradeEventBuilder withMessageId(long inMessageId)
    {
        super.withMessageId(inMessageId);
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.AbstractEventBuilder#withTimestamp(java.util.Date)
     */
    @Override
    public TradeEventBuilder withTimestamp(Date inTimestamp)
    {
        super.withTimestamp(inTimestamp);
        return this;
    }
    /**
     * Sets the instrument value.
     *
     * @param a <code>I</code> value
     */
    public final TradeEventBuilder withInstrument(Instrument inInstrument)
    {
        marketData.setInstrument(inInstrument);
        return this;
    }
    /**
     * Sets the price value.
     *
     * @param a <code>BigDecimal</code> value
     */
    public final TradeEventBuilder withPrice(BigDecimal inPrice)
    {
        marketData.setPrice(inPrice);
        return this;
    }
    /**
     * Sets the size value.
     *
     * @param a <code>BigDecimal</code> value
     */
    public final TradeEventBuilder withSize(BigDecimal inSize)
    {
        marketData.setSize(inSize);
        return this;
    }
    /**
     * Sets the exchange value.
     *
     * @param a <code>String</code> value
     */
    public final TradeEventBuilder withExchange(String inExchange)
    {
        marketData.setExchange(inExchange);
        return this;
    }
    /**
     * Sets the quoteDate value.
     *
     * @param a <code>String</code> value
     */
    public final TradeEventBuilder withTradeDate(String inTradeDate)
    {
        marketData.setExchangeTimestamp(inTradeDate);
        return this;
    }
    /**
     * Sets the underlyingEquity value.
     *
     * @param a <code>Equity</code> value
     * @return a <code>TradeEventBuilder</code> value
     */
    public final TradeEventBuilder withUnderlyingEquity(Equity inUnderlyingEquity)
    {
        option.setUnderlyingEquity(inUnderlyingEquity);
        return this;
    }
    /**
     * Sets the expiry value.
     *
     * @param a <code>String</code> value
     * @return a <code>TradeEventBuilder</code> value
     */
    public final TradeEventBuilder withExpiry(String inExpiry)
    {
        option.setExpiry(inExpiry);
        return this;
    }
    /**
     * Sets the optionType value.
     *
     * @param a <code>OptionType</code> value
     * @return a <code>TradeEventBuilder</code> value
     */
    public final TradeEventBuilder withOptionType(OptionType inOptionType)
    {
        option.setOptionType(inOptionType);
        return this;
    }
    /**
     * Sets the expirationType value.
     *
     * @param a <code>ExpirationType</code> value
     * @return a <code>TradeEventBuilder</code> value
     */
    public final TradeEventBuilder withExpirationType(ExpirationType inExpirationType)
    {
        option.setExpirationType(inExpirationType);
        return this;
    }
    /**
     * Sets the multiplier value.
     *
     * @param a <code>int</code> value
     * @return a <code>TradeEventBuilder</code> value
     */
    public final TradeEventBuilder withMultiplier(int inMultiplier)
    {
        option.setMultiplier(inMultiplier);
        return this;
    }
    /**
     * Sets the hasDeliverable value.
     *
     * @param a <code>boolean</code> value
     * @return a <code>TradeEventBuilder</code> value
     */
    public final TradeEventBuilder hasDeliverable(boolean inHasDeliverable)
    {
        option.setHasDeliverable(inHasDeliverable);
        return this;
    }
    /**
     * Get the marketData value.
     *
     * @return a <code>MarketDataBean</code> value
     */
    protected final MarketDataBean getMarketData()
    {
        return marketData;
    }
    /**
     * Get the option value.
     *
     * @return a <code>OptionBean</code> value
     */
    protected final OptionBean getOption()
    {
        return option;
    }
    /**
     * the market data attributes 
     */
    private final MarketDataBean marketData = new MarketDataBean();
    /**
     * the option attributes
     */
    private final OptionBean option = new OptionBean();
}
