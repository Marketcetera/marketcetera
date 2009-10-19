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
public class MarketstatBean
{
    /**
     * Get the openPrice value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getOpenPrice()
    {
        return openPrice;
    }
    /**
     * Sets the openPrice value.
     *
     * @param a <code>BigDecimal</code> value
     */
    public void setOpenPrice(BigDecimal inOpenPrice)
    {
        openPrice = inOpenPrice;
    }
    /**
     * Get the highPrice value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getHighPrice()
    {
        return highPrice;
    }
    /**
     * Sets the highPrice value.
     *
     * @param a <code>BigDecimal</code> value
     */
    public void setHighPrice(BigDecimal inHighPrice)
    {
        highPrice = inHighPrice;
    }
    /**
     * Get the lowPrice value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getLowPrice()
    {
        return lowPrice;
    }
    /**
     * Sets the lowPrice value.
     *
     * @param a <code>BigDecimal</code> value
     */
    public void setLowPrice(BigDecimal inLowPrice)
    {
        lowPrice = inLowPrice;
    }
    /**
     * Get the closePrice value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getClosePrice()
    {
        return closePrice;
    }
    /**
     * Sets the closePrice value.
     *
     * @param a <code>BigDecimal</code> value
     */
    public void setClosePrice(BigDecimal inClosePrice)
    {
        closePrice = inClosePrice;
    }
    /**
     * Get the previousClosePrice value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getPreviousClosePrice()
    {
        return previousClosePrice;
    }
    /**
     * Sets the previousClosePrice value.
     *
     * @param a <code>BigDecimal</code> value
     */
    public void setPreviousClosePrice(BigDecimal inPreviousClosePrice)
    {
        previousClosePrice = inPreviousClosePrice;
    }
    /**
     * Get the volume value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getVolume()
    {
        return volume;
    }
    /**
     * Sets the volume value.
     *
     * @param a <code>BigDecimal</code> value
     */
    public void setVolume(BigDecimal inVolume)
    {
        volume = inVolume;
    }
    /**
     * Get the closeDate value.
     *
     * @return a <code>String</code> value
     */
    public String getCloseDate()
    {
        return closeDate;
    }
    /**
     * Sets the closeDate value.
     *
     * @param a <code>String</code> value
     */
    public void setCloseDate(String inCloseDate)
    {
        closeDate = inCloseDate;
    }
    /**
     * Get the previousCloseDate value.
     *
     * @return a <code>String</code> value
     */
    public String getPreviousCloseDate()
    {
        return previousCloseDate;
    }
    /**
     * Sets the previousCloseDate value.
     *
     * @param a <code>String</code> value
     */
    public void setPreviousCloseDate(String inPreviousCloseDate)
    {
        previousCloseDate = inPreviousCloseDate;
    }
    /**
     * Get the tradeHighTime value.
     *
     * @return a <code>String</code> value
     */
    public String getTradeHighTime()
    {
        return tradeHighTime;
    }
    /**
     * Sets the tradeHighTime value.
     *
     * @param a <code>String</code> value
     */
    public void setTradeHighTime(String inTradeHighTime)
    {
        tradeHighTime = inTradeHighTime;
    }
    /**
     * Get the tradeLowTime value.
     *
     * @return a <code>String</code> value
     */
    public String getTradeLowTime()
    {
        return tradeLowTime;
    }
    /**
     * Sets the tradeLowTime value.
     *
     * @param a <code>String</code> value
     */
    public void setTradeLowTime(String inTradeLowTime)
    {
        tradeLowTime = inTradeLowTime;
    }
    /**
     * Get the openExchange value.
     *
     * @return a <code>String</code> value
     */
    public String getOpenExchange()
    {
        return openExchange;
    }
    /**
     * Sets the openExchange value.
     *
     * @param a <code>String</code> value
     */
    public void setOpenExchange(String inOpenExchange)
    {
        openExchange = inOpenExchange;
    }
    /**
     * Get the highExchange value.
     *
     * @return a <code>String</code> value
     */
    public String getHighExchange()
    {
        return highExchange;
    }
    /**
     * Sets the highExchange value.
     *
     * @param a <code>String</code> value
     */
    public void setHighExchange(String inHighExchange)
    {
        highExchange = inHighExchange;
    }
    /**
     * Get the lowExchange value.
     *
     * @return a <code>String</code> value
     */
    public String getLowExchange()
    {
        return lowExchange;
    }
    /**
     * Sets the lowExchange value.
     *
     * @param a <code>String</code> value
     */
    public void setLowExchange(String inLowExchange)
    {
        lowExchange = inLowExchange;
    }
    /**
     * Get the closeExchange value.
     *
     * @return a <code>String</code> value
     */
    public String getCloseExchange()
    {
        return closeExchange;
    }
    /**
     * Sets the closeExchange value.
     *
     * @param a <code>String</code> value
     */
    public void setCloseExchange(String inCloseExchange)
    {
        closeExchange = inCloseExchange;
    }
    private BigDecimal openPrice;
    private BigDecimal highPrice;
    private BigDecimal lowPrice;
    private BigDecimal closePrice;
    private BigDecimal previousClosePrice;
    private BigDecimal volume;
    private String closeDate;
    private String previousCloseDate;
    private String tradeHighTime;
    private String tradeLowTime;
    private String openExchange;
    private String highExchange;
    private String lowExchange;
    private String closeExchange;
}
