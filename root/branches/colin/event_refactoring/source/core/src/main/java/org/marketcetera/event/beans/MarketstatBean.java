package org.marketcetera.event.beans;

import java.math.BigDecimal;

import javax.annotation.concurrent.NotThreadSafe;

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
@NotThreadSafe
@ClassVersion("$Id$")
public final class MarketstatBean
        extends EventBean
{
    /**
     * Get the openPrice value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getOpen()
    {
        return openPrice;
    }
    /**
     * Sets the openPrice value.
     *
     * @param a <code>BigDecimal</code> value
     */
    public void setOpen(BigDecimal inOpenPrice)
    {
        openPrice = inOpenPrice;
    }
    /**
     * Get the highPrice value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getHigh()
    {
        return highPrice;
    }
    /**
     * Sets the highPrice value.
     *
     * @param a <code>BigDecimal</code> value
     */
    public void setHigh(BigDecimal inHighPrice)
    {
        highPrice = inHighPrice;
    }
    /**
     * Get the lowPrice value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getLow()
    {
        return lowPrice;
    }
    /**
     * Sets the lowPrice value.
     *
     * @param a <code>BigDecimal</code> value
     */
    public void setLow(BigDecimal inLowPrice)
    {
        lowPrice = inLowPrice;
    }
    /**
     * Get the closePrice value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getClose()
    {
        return closePrice;
    }
    /**
     * Sets the closePrice value.
     *
     * @param a <code>BigDecimal</code> value
     */
    public void setClose(BigDecimal inClosePrice)
    {
        closePrice = inClosePrice;
    }
    /**
     * Get the previousClosePrice value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getPreviousClose()
    {
        return previousClosePrice;
    }
    /**
     * Sets the previousClosePrice value.
     *
     * @param a <code>BigDecimal</code> value
     */
    public void setPreviousClose(BigDecimal inPreviousClosePrice)
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
    /**
     * Get the instrument value.
     *
     * @return an <code>Instrument</code> value
     */
    public Instrument getInstrument()
    {
        return instrument.getInstrument();
    }
    /**
     * Set the instrument value.
     *
     * @param inInstrument an <code>Instrument</code> value
     */
    public void setInstrument(Instrument inInstrument)
    {
        instrument.setInstrument(inInstrument);
    }
    /**
     * Performs validation of the attributes.
     *
     * <p>Subclasses should override this method to validate
     * their attributes and invoke the parent method.
     * @throws IllegalArgumentException if {@link #timestamp} is <code>null</code>
     * @throws IllegalArgumentException if {@link #messageId} &lt; 0
     * @throws IllegalArgumentException if {@link #instrument} is <code>null</code>
     */
    @Override
    public void validate()
    {
        super.validate();
        instrument.validate();
    }
    /**
     * the open price for the current or most recent session
     */
    private BigDecimal openPrice;
    /**
     * the high price for the current or most recent session
     */
    private BigDecimal highPrice;
    /**
     * the low price for the current or most recent session
     */
    private BigDecimal lowPrice;
    /**
     * the close price for the current or most recent session
     */
    private BigDecimal closePrice;
    /**
     * the close price from the previous session
     */
    private BigDecimal previousClosePrice;
    /**
     * the cumulative volume for the current or most recent session
     */
    private BigDecimal volume;
    /**
     * the market close date - format is dependent on the market data provider
     */
    private String closeDate;
    /**
     * the market previous close date - format is dependent on the market data provider
     */
    private String previousCloseDate;
    /**
     * the time of the high trade for the current or most recent session - format is dependent on the market data provider 
     */
    private String tradeHighTime;
    /**
     * the time of the low trade for the current or most recent session - format is dependent on the market data provider 
     */
    private String tradeLowTime;
    /**
     * the exchange for which the open price was reported 
     */
    private String openExchange;
    /**
     * the exchange for which the high price was reported 
     */
    private String highExchange;
    /**
     * the exchange for which the low price was reported 
     */
    private String lowExchange;
    /**
     * the exchange for which the close price was reported 
     */
    private String closeExchange;
    /**
     * the instrument
     */
    private final InstrumentBean instrument = new InstrumentBean();
    private static final long serialVersionUID = 1L;
}
