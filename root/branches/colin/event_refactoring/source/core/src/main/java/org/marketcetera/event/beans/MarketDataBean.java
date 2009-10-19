package org.marketcetera.event.beans;

import java.math.BigDecimal;

import javax.annotation.concurrent.NotThreadSafe;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@NotThreadSafe
public final class MarketDataBean
{
    /**
     * Get the exchangeTimestamp value.
     *
     * @return a <code>String</code> value
     */
    public String getExchangeTimestamp()
    {
        return exchangeTimestamp;
    }
    /**
     * Sets the exchangeTimestamp value.
     *
     * @param a <code>String</code> value
     */
    public void setExchangeTimestamp(String inExchangeTimestamp)
    {
        exchangeTimestamp = inExchangeTimestamp;
    }
    /**
     * Get the price value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getPrice()
    {
        return price;
    }
    /**
     * Sets the price value.
     *
     * @param a <code>BigDecimal</code> value
     */
    public void setPrice(BigDecimal inPrice)
    {
        price = inPrice;
    }
    /**
     * Get the size value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getSize()
    {
        return size;
    }
    /**
     * Sets the size value.
     *
     * @param a <code>BigDecimal</code> value
     */
    public void setSize(BigDecimal inSize)
    {
        size = inSize;
    }
    /**
     * Get the exchange value.
     *
     * @return a <code>String</code> value
     */
    public String getExchange()
    {
        return exchange;
    }
    /**
     * Sets the exchange value.
     *
     * @param a <code>String</code> value
     */
    public void setExchange(String inExchange)
    {
        exchange = inExchange;
    }
    /**
     * 
     */
    private BigDecimal price;
    /**
     * 
     */
    private BigDecimal size;
    /**
     * 
     */
    private String exchange;
    /**
     * 
     */
    private String exchangeTimestamp;
}
