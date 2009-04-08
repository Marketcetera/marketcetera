package org.marketcetera.event;

import java.math.BigDecimal;
import java.util.Date;

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
 * @since $Release$
 */
@ClassVersion("$Id$")
public class SymbolStatisticEvent
        extends EventBase
        implements Messages
{
    /**
     * Create a new SymbolStatisticEvent instance.
     * 
     * @param inSymbol an <code>MSymbol</code> value
     * @param timestamp a <code>long</code> value expressing the time this event occurred in milliseconds since
     *   EPOCH in GMT
     * @param inOpen a <code>BigDecimal</code> value or null
     * @param inHigh a <code>BigDecimal</code> value or null
     * @param inLow a <code>BigDecimal</code> value or null
     * @param inClose a <code>BigDecimal</code> value or null
     * @param inPreviousClose a <code>BigDecimal</code> value or null
     * @param inVolume a <code>BigDecimal</code> value or null
     * @param inCloseDate a <code>Date</code> value or null
     * @param inPreviousCloseDate a <code>Date</code> value or null
     * @throws IllegalArgumentException if <code>timestamp</code> &lt; 0
     */
    public SymbolStatisticEvent(MSymbol inSymbol,
                                Date inTimestamp,
                                BigDecimal inOpen,
                                BigDecimal inHigh,
                                BigDecimal inLow,
                                BigDecimal inClose,
                                BigDecimal inPreviousClose,
                                BigDecimal inVolume,
                                Date inCloseDate,
                                Date inPreviousCloseDate)
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
    }
    /**
     * symbol for the event
     */
    private final MSymbol symbol;
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
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return String.format("Statistics for %s -> Open: %s High: %s Low: %s Close: %s (%s) Previous Close: %s (%s) Volume: %s", //$NON-NLS-1$
                             getSymbol(),
                             open,
                             high,
                             low,
                             close,
                             closeDate,
                             previousClose,
                             previousCloseDate,
                             volume);
    }
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
    private static final long serialVersionUID = 1L;
}
