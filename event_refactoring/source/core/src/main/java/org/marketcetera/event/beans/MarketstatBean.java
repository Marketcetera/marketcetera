package org.marketcetera.event.beans;

import java.math.BigDecimal;

import javax.annotation.concurrent.ThreadSafe;

import org.marketcetera.event.MarketstatEvent;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Stores the attributes necessary for {@link MarketstatEvent}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ThreadSafe
@ClassVersion("$Id$")
public final class MarketstatBean
        extends EventBean
{
    /**
     * Get the openPrice value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public final BigDecimal getOpen()
    {
        return openPrice;
    }
    /**
     * Sets the openPrice value.
     *
     * @param a <code>BigDecimal</code> value
     */
    public final void setOpen(BigDecimal inOpenPrice)
    {
        openPrice = inOpenPrice;
    }
    /**
     * Get the highPrice value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public final BigDecimal getHigh()
    {
        return highPrice;
    }
    /**
     * Sets the highPrice value.
     *
     * @param a <code>BigDecimal</code> value
     */
    public final void setHigh(BigDecimal inHighPrice)
    {
        highPrice = inHighPrice;
    }
    /**
     * Get the lowPrice value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public final BigDecimal getLow()
    {
        return lowPrice;
    }
    /**
     * Sets the lowPrice value.
     *
     * @param a <code>BigDecimal</code> value
     */
    public final void setLow(BigDecimal inLowPrice)
    {
        lowPrice = inLowPrice;
    }
    /**
     * Get the closePrice value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public final BigDecimal getClose()
    {
        return closePrice;
    }
    /**
     * Sets the closePrice value.
     *
     * @param a <code>BigDecimal</code> value
     */
    public final void setClose(BigDecimal inClosePrice)
    {
        closePrice = inClosePrice;
    }
    /**
     * Get the previousClosePrice value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public final BigDecimal getPreviousClose()
    {
        return previousClosePrice;
    }
    /**
     * Sets the previousClosePrice value.
     *
     * @param a <code>BigDecimal</code> value
     */
    public final void setPreviousClose(BigDecimal inPreviousClosePrice)
    {
        previousClosePrice = inPreviousClosePrice;
    }
    /**
     * Get the volume value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public final BigDecimal getVolume()
    {
        return volume;
    }
    /**
     * Sets the volume value.
     *
     * @param a <code>BigDecimal</code> value
     */
    public final void setVolume(BigDecimal inVolume)
    {
        volume = inVolume;
    }
    /**
     * Get the closeDate value.
     *
     * @return a <code>String</code> value
     */
    public final String getCloseDate()
    {
        return closeDate;
    }
    /**
     * Sets the closeDate value.
     *
     * @param a <code>String</code> value
     */
    public final void setCloseDate(String inCloseDate)
    {
        closeDate = inCloseDate;
    }
    /**
     * Get the previousCloseDate value.
     *
     * @return a <code>String</code> value
     */
    public final String getPreviousCloseDate()
    {
        return previousCloseDate;
    }
    /**
     * Sets the previousCloseDate value.
     *
     * @param a <code>String</code> value
     */
    public final void setPreviousCloseDate(String inPreviousCloseDate)
    {
        previousCloseDate = inPreviousCloseDate;
    }
    /**
     * Get the tradeHighTime value.
     *
     * @return a <code>String</code> value
     */
    public final String getTradeHighTime()
    {
        return tradeHighTime;
    }
    /**
     * Sets the tradeHighTime value.
     *
     * @param a <code>String</code> value
     */
    public final void setTradeHighTime(String inTradeHighTime)
    {
        tradeHighTime = inTradeHighTime;
    }
    /**
     * Get the tradeLowTime value.
     *
     * @return a <code>String</code> value
     */
    public final String getTradeLowTime()
    {
        return tradeLowTime;
    }
    /**
     * Sets the tradeLowTime value.
     *
     * @param a <code>String</code> value
     */
    public final void setTradeLowTime(String inTradeLowTime)
    {
        tradeLowTime = inTradeLowTime;
    }
    /**
     * Get the openExchange value.
     *
     * @return a <code>String</code> value
     */
    public final String getOpenExchange()
    {
        return openExchange;
    }
    /**
     * Sets the openExchange value.
     *
     * @param a <code>String</code> value
     */
    public final void setOpenExchange(String inOpenExchange)
    {
        openExchange = inOpenExchange;
    }
    /**
     * Get the highExchange value.
     *
     * @return a <code>String</code> value
     */
    public final String getHighExchange()
    {
        return highExchange;
    }
    /**
     * Sets the highExchange value.
     *
     * @param a <code>String</code> value
     */
    public final void setHighExchange(String inHighExchange)
    {
        highExchange = inHighExchange;
    }
    /**
     * Get the lowExchange value.
     *
     * @return a <code>String</code> value
     */
    public final String getLowExchange()
    {
        return lowExchange;
    }
    /**
     * Sets the lowExchange value.
     *
     * @param a <code>String</code> value
     */
    public final void setLowExchange(String inLowExchange)
    {
        lowExchange = inLowExchange;
    }
    /**
     * Get the closeExchange value.
     *
     * @return a <code>String</code> value
     */
    public final String getCloseExchange()
    {
        return closeExchange;
    }
    /**
     * Sets the closeExchange value.
     *
     * @param a <code>String</code> value
     */
    public final void setCloseExchange(String inCloseExchange)
    {
        closeExchange = inCloseExchange;
    }
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
     * the open price for the current or most recent session
     */
    private volatile BigDecimal openPrice;
    /**
     * the high price for the current or most recent session
     */
    private volatile BigDecimal highPrice;
    /**
     * the low price for the current or most recent session
     */
    private volatile BigDecimal lowPrice;
    /**
     * the close price for the current or most recent session
     */
    private volatile BigDecimal closePrice;
    /**
     * the close price from the previous session
     */
    private volatile BigDecimal previousClosePrice;
    /**
     * the cumulative volume for the current or most recent session
     */
    private volatile BigDecimal volume;
    /**
     * the market close date - format is dependent on the market data provider
     */
    private volatile String closeDate;
    /**
     * the market previous close date - format is dependent on the market data provider
     */
    private volatile String previousCloseDate;
    /**
     * the time of the high trade for the current or most recent session - format is dependent on the market data provider 
     */
    private volatile String tradeHighTime;
    /**
     * the time of the low trade for the current or most recent session - format is dependent on the market data provider 
     */
    private volatile String tradeLowTime;
    /**
     * the exchange for which the open price was reported 
     */
    private volatile String openExchange;
    /**
     * the exchange for which the high price was reported 
     */
    private volatile String highExchange;
    /**
     * the exchange for which the low price was reported 
     */
    private volatile String lowExchange;
    /**
     * the exchange for which the close price was reported 
     */
    private volatile String closeExchange;
    /**
     * the instrument
     */
    private final InstrumentBean instrument = new InstrumentBean();
    private static final long serialVersionUID = 1L;
}
