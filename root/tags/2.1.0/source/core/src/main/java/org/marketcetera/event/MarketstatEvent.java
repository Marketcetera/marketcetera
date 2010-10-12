package org.marketcetera.event;

import java.math.BigDecimal;

import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Represents the set of available statistics of a specific {@link Instrument}.
 * 
 * <p>The data contained in this event represent the best-effort result of a request
 * to retrieve available statistics for a specific symbol at a specific time.  Some
 * or all of the attributes may be null if the data was not available.
 * 
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public interface MarketstatEvent
        extends Event, HasInstrument, HasEventType
{
    /**
     * Gets the value of the {@link Instrument} at the time of market open.
     *
     * @return a <code>BigDecimal</code> value or null
     */
    public BigDecimal getOpen();
    /**
     * Gets highest value trade during the current or
     * most recent session.
     *
     * @return a <code>BigDecimal</code> value or null
     */
    public BigDecimal getHigh();
    /**
     * Gets the low trade value of the {@link Instrument} for this market session.
     *
     * @return a <code>BigDecimal</code> value or null
     */
    public BigDecimal getLow();
    /**
     * Gets the value of the {@link Instrument} at the time of market close.
     *
     * @return a <code>BigDecimal</code> value or null
     */
    public BigDecimal getClose();
    /**
     * Gets the value of the {@link Instrument} at the time of market close
     * of the previous session.
     *
     * @return a <code>BigDecimal</code> value or null
     */
    public BigDecimal getPreviousClose();
    /**
     * Gets the cumulative volume of trades of the {@link Instrument} during
     * the current or last market session.
     *
     * @return a <code>BigDecimal</code> value or null
     */
    public BigDecimal getVolume();
    /**
     * Gets the cumulative value for the session. 
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getValue();
    /**
     * Gets the close date of the most recent session.
     *
     * <p>The format of the date returned is dependent on the market data
     * provider.
     *
     * @return a <code>String</code> value
     */
    public String getCloseDate();
    /**
     * Gets the close date of the previous session.
     *
     * <p>The format of the date returned is dependent on the market data
     * provider.
     *
     * @return a <code>String</code> value
     */
    public String getPreviousCloseDate();
    /**
     * Gets the time of the highest value trade during the current or
     * most recent session.
     *
     * <p>The format of the time returned is dependent on the market data
     * provider.
     *
     * @return a <code>String</code> value
     */
    public String getTradeHighTime();
    /**
     * Gets the time of the lowest value trade during the current or
     * most recent session.
     *
     * <p>The format of the time returned is dependent on the market data
     * provider.
     *
     * @return a <code>String</code> value
     */
    public String getTradeLowTime();
    /**
     * Gets the exchange on which the open price was reported.
     *
     * @return a <code>String</code> value
     */
    public String getOpenExchange();
    /**
     * Gets the exchange on which the trade high price was reported.
     *
     * @return a <code>String</code> value
     */
    public String getHighExchange();
    /**
     * Gets the exchange on which the trade low price was reported.
     *
     * @return a <code>String</code> value
     */
    public String getLowExchange();
    /**
     * Gets the exchange on which the close price was reported.
     *
     * @return a <code>String</code> value
     */
    public String getCloseExchange();
    /**
     * Gets the type of the event.
     *
     * @return an <code>EventType</code> value
     */
    public EventType getEventType();
    /**
     * Sets the type of the event.
     *
     * @param inEventType an <code>EventType</code> value
     */
    public void setEventType(EventType inEventType);
}
