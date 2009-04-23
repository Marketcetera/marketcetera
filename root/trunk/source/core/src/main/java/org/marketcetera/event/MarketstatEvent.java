package org.marketcetera.event;

import java.math.BigDecimal;
import java.util.Date;

import org.marketcetera.marketdata.DateUtils;
import org.marketcetera.trade.MSymbol;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Represents the set of available statistics of a specific {@link MSymbol}.
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
public class MarketstatEvent
        extends EventBase
        implements Messages, HasSymbol
{
    /**
     * Create a new MarketstatEvent instance.
     * 
     * @param inSymbol an <code>MSymbol</code> value
     * @param timestamp a <code>Date</code> value expressing the time this event occurred
     * @param inOpen a <code>BigDecimal</code> value or null
     * @param inHigh a <code>BigDecimal</code> value or null
     * @param inLow a <code>BigDecimal</code> value or null
     * @param inClose a <code>BigDecimal</code> value or null
     * @param inPreviousClose a <code>BigDecimal</code> value or null
     * @param inVolume a <code>BigDecimal</code> value or null
     * @param inCloseDate a <code>Date</code> value or null
     * @param inPreviousCloseDate a <code>Date</code> value or null
     * @param inTradeHighTime a <code>Date</code> value or null
     * @param inTradeLowTime a <code>Date</code> value or null
     * @param inOpenExchange a <code>String</code> value or null
     * @param inHighExchange a <code>String</code> value or null
     * @param inLowExchange a <code>String</code> value or null
     * @param inCloseExchange a <code>String</code> value or null
     * @throws IllegalArgumentException if <code>timestamp</code> &lt; 0
     */
    public MarketstatEvent(MSymbol inSymbol,
                           Date inTimestamp,
                           BigDecimal inOpen,
                           BigDecimal inHigh,
                           BigDecimal inLow,
                           BigDecimal inClose,
                           BigDecimal inPreviousClose,
                           BigDecimal inVolume,
                           Date inCloseDate,
                           Date inPreviousCloseDate,
                           Date inTradeHighTime,
                           Date inTradeLowTime,
                           String inOpenExchange,
                           String inHighExchange,
                           String inLowExchange,
                           String inCloseExchange)
    {
        super(EventBase.assignCounter(),
              inTimestamp.getTime());
        if(inSymbol == null) {
            throw new NullPointerException();
        }
        open = inOpen;
        high = inHigh;
        low = inLow;
        close = inClose;
        previousClose = inPreviousClose;
        volume = inVolume;
        closeDate = inCloseDate;
        previousCloseDate = inPreviousCloseDate;
        symbol = inSymbol;
        highTime = inTradeHighTime;
        lowTime = inTradeLowTime;
        closeExchange = inCloseExchange;
        openExchange = inOpenExchange;
        highExchange = inHighExchange;
        lowExchange = inLowExchange;
    }
    /**
     * Get the open value.
     *
     * @return a <code>BigDecimal</code> value or null
     */
    public BigDecimal getOpen()
    {
        return open;
    }
    /**
     * Get the high value.
     *
     * @return a <code>BigDecimal</code> value or null
     */
    public BigDecimal getHigh()
    {
        return high;
    }
    /**
     * Get the low value.
     *
     * @return a <code>BigDecimal</code> value or null
     */
    public BigDecimal getLow()
    {
        return low;
    }
    /**
     * Get the close value.
     *
     * @return a <code>BigDecimal</code> value or null
     */
    public BigDecimal getClose()
    {
        return close;
    }
    /**
     * Get the previousClose value.
     *
     * @return a <code>BigDecimal</code> value or null
     */
    public BigDecimal getPreviousClose()
    {
        return previousClose;
    }
    /**
     * Get the volume value.
     *
     * @return a <code>BigDecimal</code> value or null
     */
    public BigDecimal getVolume()
    {
        return volume;
    }
    /**
     * Get the closeDate value.
     *
     * @return a <code>Date</code> value or null
     */
    public Date getCloseDate()
    {
        return closeDate;
    }
    /**
     * Get the previousCloseDate value.
     *
     * @return a <code>Date</code> value or null
     */
    public Date getPreviousCloseDate()
    {
        return previousCloseDate;
    }
    /**
     * Get the symbol value.
     *
     * @return a <code>MSymbol</code> value
     */
    public MSymbol getSymbol()
    {
        return symbol;
    }
    /**
     * Get the highTime value.
     *
     * @return a <code>Date</code> value or null
     */
    public Date getHighTime()
    {
        return highTime;
    }
    /**
     * Get the lowTime value.
     *
     * @return a <code>Date</code> value or null
     */
    public Date getLowTime()
    {
        return lowTime;
    }
    /**
     * Get the closeExchange value.
     *
     * @return a <code>String</code> value or null
     */
    public String getCloseExchange()
    {
        return closeExchange;
    }
    /**
     * Get the openExchange value.
     *
     * @return a <code>String</code> value or null
     */
    public String getOpenExchange()
    {
        return openExchange;
    }
    /**
     * Get the highExchange value.
     *
     * @return a <code>String</code> value or null
     */
    public String getHighExchange()
    {
        return highExchange;
    }
    /**
     * Get the lowExchange value.
     *
     * @return a <code>String</code> value or null
     */
    public String getLowExchange()
    {
        return lowExchange;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        String closeDateString = "---"; //$NON-NLS-1$
        if(closeDate != null) {
            closeDateString = DateUtils.dateToString(closeDate,
                                                     DateUtils.DAYS);
        }
        String previousCloseDateString = "---"; //$NON-NLS-1$
        if(previousCloseDate != null) {
            previousCloseDateString = DateUtils.dateToString(previousCloseDate,
                                                             DateUtils.DAYS);
        }
        return String.format("Statistics for %s -> Open: %s High: %s Low: %s Close: %s (%s) Previous Close: %s (%s) Volume: %s", //$NON-NLS-1$
                             getSymbol(),
                             open,
                             high,
                             low,
                             close,
                             closeDateString,
                             previousClose,
                             previousCloseDateString,
                             volume);
    }
    /**
     * symbol for the event
     */
    private final MSymbol symbol;
    /**
     * open price
     */
    private final BigDecimal open;
    /**
     * high price
     */
    private final BigDecimal high;
    /**
     * low price
     */
    private final BigDecimal low;
    /**
     * close price
     */
    private final BigDecimal close;
    /**
     * previous close price
     */
    private final BigDecimal previousClose;
    /**
     * volume
     */
    private final BigDecimal volume;
    /**
     * close price date
     */
    private final Date closeDate;
    /**
     * previous close date
     */
    private final Date previousCloseDate;
    /**
     * the time that the high price occurred
     */
    private final Date highTime;
    /**
     * the time that the low price occurred
     */
    private final Date lowTime;
    /**
     * the exchange for the close price
     */
    private final String closeExchange;
    /**
     * the exchange for the open price
     */
    private final String openExchange;
    /**
     * the exchange for the high price
     */
    private final String highExchange;
    /**
     * the exchange for the low price
     */
    private final String lowExchange;
    private static final long serialVersionUID = 1L;
}
