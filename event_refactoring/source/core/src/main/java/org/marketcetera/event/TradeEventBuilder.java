package org.marketcetera.event;

import java.math.BigDecimal;
import java.util.Date;

import org.marketcetera.event.beans.InstrumentBean;
import org.marketcetera.event.beans.MarketDataBean;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class TradeEventBuilder
        extends EventBuilderImpl
        implements EventBuilder<TradeEvent>
{
    /**
     * 
     *
     *
     * @param inInstrument
     * @return
     */
    public static TradeEventBuilder newTradeEvent(Instrument inInstrument)
    {
        if(inInstrument instanceof Equity) {
            return newEquityTradeEvent().withInstrument(inInstrument);
        } else if(inInstrument instanceof Option) {
            return newOptionTradeEvent().withInstrument(inInstrument);
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
    public static TradeEventBuilder newEquityTradeEvent()
    {
        return new EquityTradeEventBuilder();
    }
    /**
     * 
     *
     *
     * @return
     */
    public static TradeEventBuilder newOptionTradeEvent()
    {
        return new OptionTradeEventBuilder();
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
        instrument.setInstrument(inInstrument);
        return this;
    }
    /**
     * Sets the price value.
     *
     * @param a <code>BigDecimal</code> value
     */
    public final TradeEventBuilder withPrice(BigDecimal inPrice)
    {
        exchangeCommon.setPrice(inPrice);
        return this;
    }
    /**
     * Sets the size value.
     *
     * @param a <code>BigDecimal</code> value
     */
    public final TradeEventBuilder withSize(BigDecimal inSize)
    {
        exchangeCommon.setSize(inSize);
        return this;
    }
    /**
     * Sets the exchange value.
     *
     * @param a <code>String</code> value
     */
    public final TradeEventBuilder withExchange(String inExchange)
    {
        exchangeCommon.setExchange(inExchange);
        return this;
    }
    /**
     * Sets the quoteDate value.
     *
     * @param a <code>String</code> value
     */
    public final TradeEventBuilder atQuoteDate(String inQuoteDate)
    {
        exchangeCommon.setExchangeTimestamp(inQuoteDate);
        return this;
    }
    protected final InstrumentBean getInstrument()
    {
        return instrument;
    }
    protected final MarketDataBean getExchangeCommon()
    {
        return exchangeCommon;
    }
    private static final class EquityTradeEventBuilder
            extends TradeEventBuilder
    {
        /* (non-Javadoc)
         * @see org.marketcetera.event.EventBuilder#create()
         */
        @Override
        public TradeEvent create()
        {
            return new EquityTradeEventImpl(getMessageId(),
                                            getTimestamp(),
                                            (Equity)getInstrument().getInstrument(),
                                            getExchangeCommon().getExchange(),
                                            getExchangeCommon().getPrice(),
                                            getExchangeCommon().getSize(),
                                            getExchangeCommon().getExchangeTimestamp());
        }
    }
    private static final class OptionTradeEventBuilder
            extends TradeEventBuilder
    {
        /* (non-Javadoc)
         * @see org.marketcetera.event.EventBuilder#create()
         */
        @Override
        public TradeEvent create()
        {
            // TODO Auto-generated method stub
            return null;
        }
    }
    private final InstrumentBean instrument = new InstrumentBean();
    private final MarketDataBean exchangeCommon = new MarketDataBean();
}
