package org.marketcetera.event.impl;

import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.concurrent.NotThreadSafe;

import org.marketcetera.event.Messages;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.event.beans.MarketDataBean;
import org.marketcetera.event.beans.OptionBean;
import org.marketcetera.options.ExpirationType;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Constructs {@link TradeEvent} objects.
 * 
 * <p>Construct a <code>TradeEvent</code> by getting a <code>TradeEventBuilder</code>,
 * setting the appropriate attributes on the builder, and calling {@link #create()}.  Note that
 * the builder does no validation.  The object does its own validation when {@link #create()} is
 * called.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@NotThreadSafe
@ClassVersion("$Id$")
public abstract class TradeEventBuilder<E extends TradeEvent>
        implements EventBuilder<E>, Messages
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
    public static TradeEventBuilder<TradeEvent> tradeEvent(Instrument inInstrument)
    {
        if(inInstrument instanceof Equity) {
            return equityTradeEvent().withInstrument(inInstrument);
        }
        if(inInstrument instanceof Option) {
            return optionTradeEvent().withInstrument(inInstrument);
        }
        throw new UnsupportedOperationException();
    }
    /**
     * Returns a <code>TradeEventBuilder</code> suitable for constructing a new Equity <code>TradeEvent</code> object.
     *
     * @return a <code>TradeEventBuilder</code> value
     * @throws IllegalArgumentException if the value passed to {@link #withInstrument(Instrument)} is not an {@link Equity}
     */
    public static TradeEventBuilder<TradeEvent> equityTradeEvent()
    {
        return new TradeEventBuilder<TradeEvent>() {
            /* (non-Javadoc)
             * @see org.marketcetera.event.EventBuilder#create()
             */
            @Override
            public TradeEvent create()
            {
                if(getMarketData().getInstrument() instanceof Equity) {
                    return new EquityTradeEventImpl(getMarketData());
                }
                throw new IllegalArgumentException(VALIDATION_EQUITY_REQUIRED.getText());
            }
        };
    }
    /**
     * Returns a <code>TradeEventBuilder</code> suitable for constructing a new Option <code>TradeEvent</code> object.
     *
     * @return a <code>TradeEventBuilder</code> value
     * @throws IllegalArgumentException if the value passed to {@link #withInstrument(Instrument)} is not an {@link Option}
     */
    public static TradeEventBuilder<TradeEvent> optionTradeEvent()
    {
        return new TradeEventBuilder<TradeEvent>() {
            @Override
            public TradeEvent create()
            {
                if(getMarketData().getInstrument() instanceof Option) {
                    return new OptionTradeEventImpl(getMarketData(),
                                                    getOption());
                }
                throw new IllegalArgumentException(VALIDATION_OPTION_REQUIRED.getText());
            }
        };
    }
    /**
     * Sets the message id to use with the new event. 
     *
     * @param inMessageId a <code>long</code> value
     * @return a <code>TradeEventBuilder</code> value
     */
    public TradeEventBuilder<E> withMessageId(long inMessageId)
    {
        marketData.setMessageId(inMessageId);
        return this;
    }
    /**
     * Sets the timestamp value to use with the new event.
     *
     * @param inTimestamp a <code>Date</code> value or <code>null</code>
     * @return a <code>TradeEventBuilder</code> value
     */
    public TradeEventBuilder<E> withTimestamp(Date inTimestamp)
    {
        marketData.setTimestamp(inTimestamp);
        return this;
    }
    /**
     * Sets the source value to use with the new event.
     *
     * @param inSource an <code>Object</code> value or <code>null</code>
     * @return a <code>TradeEventBuilder</code> value
     */
    public TradeEventBuilder<E> withSource(Object inSource)
    {
        marketData.setSource(inSource);
        return this;
    }
    /**
     * Sets the instrument value.
     *
     * @param inInstrument an <code>Instrument</code> value or <code>null</code>
     * @return a <code>TradeEventBuilder</code> value
     */
    public TradeEventBuilder<E> withInstrument(Instrument inInstrument)
    {
        marketData.setInstrument(inInstrument);
        if(inInstrument instanceof Option) {
            option.setInstrument((Option)inInstrument);
        } else if(inInstrument == null) {
            option.setInstrument(null);
        }
        return this;
    }
    /**
     * Sets the price value.
     *
     * @param inPrice a <code>BigDecimal</code> value or <code>null</code>
     * @return a <code>TradeEventBuilder</code> value
     */
    public TradeEventBuilder<E> withPrice(BigDecimal inPrice)
    {
        marketData.setPrice(inPrice);
        return this;
    }
    /**
     * Sets the size value.
     *
     * @param inSize a <code>BigDecimal</code> value or <code>null</code>
     * @return a <code>TradeEventBuilder</code> value
     */
    public TradeEventBuilder<E> withSize(BigDecimal inSize)
    {
        marketData.setSize(inSize);
        return this;
    }
    /**
     * Sets the exchange value.
     *
     * @param inExchange a <code>String</code> value or <code>null</code>
     * @return a <code>TradeEventBuilder</code> value
     */
    public TradeEventBuilder<E> withExchange(String inExchange)
    {
        marketData.setExchange(inExchange);
        return this;
    }
    /**
     * Sets the tradeDate value.
     *
     * @param inTradeDate a <code>String</code> value or <code>null</code>
     * @return a <code>TradeEventBuilder</code> value
     */
    public TradeEventBuilder<E> withTradeDate(String inTradeDate)
    {
        marketData.setExchangeTimestamp(inTradeDate);
        return this;
    }
    /**
     * Sets the underlyingInstrument value.
     *
     * @param inUnderlyingInstrument an <code>Instrument</code> value or <code>null</code>
     * @return a <code>TradeEventBuilder</code> value
     */
    public TradeEventBuilder<E> withUnderlyingInstrument(Instrument inUnderlyingInstrument)
    {
        option.setUnderlyingInstrument(inUnderlyingInstrument);
        return this;
    }
    /**
     * Sets the expirationType value.
     *
     * @param inExpirationType an <code>ExpirationType</code> value or <code>null</code>
     * @return a <code>TradeEventBuilder</code> value
     */
    public TradeEventBuilder<E> withExpirationType(ExpirationType inExpirationType)
    {
        option.setExpirationType(inExpirationType);
        return this;
    }
    /**
     * Sets the multiplier value.
     *
     * @param inMultiplier a <code>BigDecimal</code> value
     * @return a <code>TradeEventBuilder&lt;E&gt;</code> value
     */
    public TradeEventBuilder<E> withMultiplier(BigDecimal inMultiplier)
    {
        option.setMultiplier(inMultiplier);
        return this;
    }
    /**
     * Sets the hasDeliverable value.
     *
     * @param inHasDeliverable a <code>boolean</code> value
     * @return a <code>TradeEventBuilder</code> value
     */
    public TradeEventBuilder<E> hasDeliverable(boolean inHasDeliverable)
    {
        option.setHasDeliverable(inHasDeliverable);
        return this;
    }
    /**
     * Sets the provider symbol value.
     *
     * @param inProviderSymbol a <code>String</code> value
     * @return a <code>TradeEventBuilder&lt;E&gt;</code> value
     */
    public final TradeEventBuilder<E> withProviderSymbol(String inProviderSymbol)
    {
        option.setProviderSymbol(inProviderSymbol);
        return this;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return String.format("TradeEventBuilder [marketData=%s, option=%s]", //$NON-NLS-1$
                             marketData,
                             option);
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
