package org.marketcetera.event;

import java.time.LocalDateTime;

/* $License$ */

/**
 * Generates timestamp values.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
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
    LocalDateTime generateTimestamp(TradeEvent inTrade);
    /**
     * Generates a timestamp value from the given quote.
     *
     * @param inQuote a <code>QuoteEvent</code> value
     * @return a <code>DateTime</code> value
     */
    LocalDateTime generateTimestamp(QuoteEvent inQuote);
    /**
     * Generates a timestamp value from the given specific string representation.
     *
     * @param inTimestamp a <code>String</code> value
     * @return a <code>DateTime</code> value
     */
    LocalDateTime generateTimestamp(String inTimestamp);
}
