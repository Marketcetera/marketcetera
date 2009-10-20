package org.marketcetera.event.beans;

import java.math.BigDecimal;

import javax.annotation.concurrent.ThreadSafe;

import org.marketcetera.event.MarketDataEvent;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Stores the attributes necessary for {@link MarketDataEvent}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ThreadSafe
@ClassVersion("$Id$")
public class MarketDataBean
        extends EventBean
{
    /**
     * Get the instrument value.
     *
     * @return an <code>Instrument</code> value
     */
    public final Instrument getInstrument()
    {
        return instrument.getInstrument();
    }
    /**
     * Set the instrument value.
     *
     * @param inInstrument an <code>Instrument</code> value
     */
    public final void setInstrument(Instrument inInstrument)
    {
        instrument.setInstrument(inInstrument);
    }
    /**
     * Get the exchangeTimestamp value.
     *
     * @return a <code>String</code> value
     */
    public final String getExchangeTimestamp()
    {
        return exchangeTimestamp;
    }
    /**
     * Sets the exchangeTimestamp value.
     *
     * @param a <code>String</code> value
     */
    public final void setExchangeTimestamp(String inExchangeTimestamp)
    {
        exchangeTimestamp = inExchangeTimestamp;
    }
    /**
     * Get the price value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public final BigDecimal getPrice()
    {
        return price;
    }
    /**
     * Sets the price value.
     *
     * @param a <code>BigDecimal</code> value
     */
    public final void setPrice(BigDecimal inPrice)
    {
        price = inPrice;
    }
    /**
     * Get the size value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public final BigDecimal getSize()
    {
        return size;
    }
    /**
     * Sets the size value.
     *
     * @param a <code>BigDecimal</code> value
     */
    public final void setSize(BigDecimal inSize)
    {
        size = inSize;
    }
    /**
     * Get the exchange value.
     *
     * @return a <code>String</code> value
     */
    public final String getExchange()
    {
        return exchange;
    }
    /**
     * Sets the exchange value.
     *
     * @param a <code>String</code> value
     */
    public final void setExchange(String inExchange)
    {
        exchange = inExchange;
    }
    /**
     * the market data price
     */
    private volatile BigDecimal price;
    /**
     * the market data size
     */
    private volatile BigDecimal size;
    /**
     * the market data exchange
     */
    private volatile String exchange;
    /**
     * the market data exchange timestamp (format is dependent on the market data provider)
     */
    private volatile String exchangeTimestamp;
    /**
     * the market data instrument
     */
    private final InstrumentBean instrument = new InstrumentBean();
    private static final long serialVersionUID = 1L;
}
