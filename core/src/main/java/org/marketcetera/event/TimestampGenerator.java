package org.marketcetera.event;

import org.joda.time.DateTime;
import org.marketcetera.event.QuoteEvent;
import org.marketcetera.event.TradeEvent;

/* $License$ */

/**
 * Generates timestamp values.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: TimestampGenerator.java 84930 2015-10-07 02:32:22Z colin $
 * @since $Release$
 */
public interface TimestampGenerator
{
    /**
     * Generates a timestamp value from the given trade.
     *
     * @param inTrade a <code>TradeEvent</code> value
     * @return a <code>DateTime</code> value
     */
    DateTime generateTimestamp(TradeEvent inTrade);
    /**
     * Generates a timestamp value from the given quote.
     *
     * @param inQuote a <code>QuoteEvent</code> value
     * @return a <code>DateTime</code> value
     */
    DateTime generateTimestamp(QuoteEvent inQuote);
    /**
     * Generates a timestamp value from the given specific string representation.
     *
     * @param inTimestamp a <code>String</code> value
     * @return a <code>DateTime</code> value
     */
    DateTime generateTimestamp(String inTimestamp);
}
