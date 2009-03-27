package org.marketcetera.event;

import java.util.Date;
import java.util.List;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.marketdata.DateUtils;
import org.marketcetera.trade.MSymbol;
import org.marketcetera.util.log.I18NBoundMessage4P;

/* $License$ */

/**
 * Represents the Open-High-Low-Close of a specific {@link MSymbol} for a specific interval.
 * 
 * <p>This object represents the {@link TradeEvent} values that correspond to a symbol's open,
 * high, low, and close for the given interval.  If the interval end is in the future, the
 * close may be null.  The interval start must be less than or equal to the interval end.
 * 
 * <p>Events are validated as follows:
 * <ul>
 *   <li>start &lt;= end and must be non-null</li>
 *   <li>open, high, and low must be non-null</li>
 *   <li>if timestamp &lt; end, close must be non-null (may be non-null even if timestamp &gt; end)</li>
 *   <li>all trades must be for the same symbol as the symbol of this event</li>
 *   <li>high price must be &gt;= all other prices</li>
 *   <li>low price must be &lt;= all other prices</li>
 *   <li>all trade timestamps must fall in the interval (start,end)</li>
 * </ul>
 * 
 * <p>Note that events may be for different exchanges.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class OpenHighLowClose
        extends AggregateEvent
        implements Messages
{
    /**
     * Create a new <code>OpenHighLowClose</code> instance.
     *
     * <p><code>inClose</code> may be null if the interval is such
     * that a closing price was impossible to determine (e.g. the
     * close date requested is in the future).
     * 
     * @param inOpen a <code>TradeEvent</code> value
     * @param inHigh a <code>TradeEvent</code> value
     * @param inLow a <code>TradeEvent</code> value
     * @param inClose a <code>TradeEvent</code> value or null
     * @param inIntervalStart a <code>Date</code> value
     * @param inIntervalEnd a <code>Date</code> value
     * @param inTimestamp a <code>Date</code> value
     * @param inSymbol an <code>MSymbol</code> value
     * @throws IllegalArgumentException if <code>inIntervalStart</code> &gt; <code>inIntervalEnd</code> or one
     *  of the given <code>TradeEvent</code> is for a symbol that does not match the given symbol
     */
    public OpenHighLowClose(TradeEvent inOpen,
                            TradeEvent inHigh,
                            TradeEvent inLow,
                            TradeEvent inClose,
                            Date inIntervalStart,
                            Date inIntervalEnd,
                            Date inTimestamp,
                            MSymbol inSymbol)
    {
        super(inTimestamp,
              inSymbol);
        // null inClose is allowed
        if(inOpen == null ||
           inHigh == null ||
           inLow == null ||
           inIntervalStart == null ||
           inIntervalEnd == null) {
            throw new NullPointerException();
        }
        // start must be <= end
        if(inIntervalStart.getTime() > inIntervalEnd.getTime()) {
            throw new IllegalArgumentException();
        }
        open = inOpen;
        high = inHigh;
        low = inLow;
        close = inClose;
        start = inIntervalStart;
        end = inIntervalEnd;
        validateTrades();
    }
    /**
     * Get the open value.
     *
     * @return a <code>TradeEvent</code> value
     */
    public TradeEvent getOpen()
    {
        return open;
    }
    /**
     * Get the high value.
     *
     * @return a <code>TradeEvent</code> value
     */
    public TradeEvent getHigh()
    {
        return high;
    }
    /**
     * Get the low value.
     *
     * @return a <code>TradeEvent</code> value
     */
    public TradeEvent getLow()
    {
        return low;
    }
    /**
     * Get the close value.
     *
     * @return a <code>TradeEvent</code> value
     */
    public TradeEvent getClose()
    {
        return close;
    }
    /**
     * Get the interval start value.
     *
     * @return a <code>Date</code> value
     */
    public Date getIntervalStart()
    {
        return start;
    }
    /**
     * Get the interval end value.
     *
     * @return a <code>Date</code> value
     */
    public Date getIntervalEnd()
    {
        return end;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.AggregateEvent#decompose()
     */
    @Override
    public List<EventBase> decompose()
    {
        throw new UnsupportedOperationException();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return String.format("OHLC for %s over %s-%s -> Open:%s High %s Low: %s Close: %s", //$NON-NLS-1$
                             getSymbol(),
                             DateUtils.dateToString(start),
                             DateUtils.dateToString(end),
                             open.getPrice(),
                             high.getPrice(),
                             low.getPrice(),
                             close == null ? "---" : close.getPrice()); //$NON-NLS-1$
    }
    /**
     * Validates the given trades to ensure the trade symbol is the correct symbol.
     * 
     * <p>The following rules are enforced:
     * <ul>
     *   <li>open, high, and low must be non-null</li>
     *   <li>if timestamp &lt; end, close must be non-null (may be non-null even if timestamp &gt; end)</li>
     *   <li>all trades must be for the same symbol as the symbol of this event</li>
     *   <li>high price must be &gt;= all other prices</li>
     *   <li>low price must be &lt;= all other prices</li>
     *   <li>all trade timestamps must fall in the interval (start,end)</li>
     * </ul>
     * @param inSymbol an <code>MSymbol</code> value
     * @throws NullPointerException if one of the given <code>TradeEvent</code> values is null
     * @throws IllegalArgumentException if one of the given <code>TradeEvent</code> values is for a symbol
     *  that is not the same as the given symbol, high is less than open, low, or close, or low is more than
     *  open or close
     */
    private void validateTrades()
    {
        TradeEvent[] mandatoryTrades = new TradeEvent[] { open, high, low };
        for(TradeEvent trade : mandatoryTrades) {
            validateTrade(trade);
        }
        // if the interval end time is less than what this event declares is the time of its creation,
        //  the close is expected to be non-null.  also perform validation if the close is present
        //  in an option scenario.
        if(close != null ||
           end.getTime() < getTimeMillis()) {
            validateTrade(close);
            // high must be >= close
            if(high.getPrice().compareTo(close.getPrice()) == -1) {
                throw new IllegalArgumentException();
            }
            // low must be <= close
            if(low.getPrice().compareTo(close.getPrice()) == 1) {
                throw new IllegalArgumentException();
            }
        }
        // high must be >= open
        if(high.getPrice().compareTo(open.getPrice()) == -1) {
            throw new IllegalArgumentException();
        }
        // high must be >= low
        if(high.getPrice().compareTo(low.getPrice()) == -1) {
            throw new IllegalArgumentException();
        }
        // low must be <= open
        if(low.getPrice().compareTo(open.getPrice()) == 1) {
            throw new IllegalArgumentException();
        }
    }
    /**
     * Validates a single trade.
     *
     * @param inTrade a <code>TradeEvent</code>
     * @throws NullPointerException if the trade is null
     * @throws IllegalArgumentException if the trade symbol does not match this event's symbol or
     *  the trade's timestamp falls outside the interval (start,end)
     */
    private void validateTrade(TradeEvent inTrade)
    {
        if(inTrade == null) {
            throw new NullPointerException();
        }
        if(!inTrade.getSymbol().equals(getSymbol())) {
            throw new IllegalArgumentException();
        }
        if(inTrade.getTimeMillis() < start.getTime() ||
           inTrade.getTimeMillis() > end.getTime()) {
            throw new IllegalArgumentException(new I18NBoundMessage4P(INVALID_EVENT_TIMESTAMP,
                                                                      inTrade.getTimestampAsDate(),
                                                                      inTrade,
                                                                      start,
                                                                      end).getText());
        }
    }
    /**
     * open trade
     */
    private final TradeEvent open;
    /**
     * high trade
     */
    private final TradeEvent high;
    /**
     * low trade
     */
    private final TradeEvent low;
    /**
     * close trade, may be null
     */
    private final TradeEvent close;
    /**
     * interval start
     */
    private final Date start;
    /**
     * interval end
     */
    private final Date end;
    private static final long serialVersionUID = 1L;
}
