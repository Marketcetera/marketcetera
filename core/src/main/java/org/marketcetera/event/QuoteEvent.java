package org.marketcetera.event;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Represents a Bid or an Ask for an instrument on an exchange at a particular time.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.5.0
 */
@ClassVersion("$Id$")
public interface QuoteEvent
        extends MarketDataEvent
{
    /**
     * Get the time the event occurred. 
     *
     * <p>The format of the returned value is dependent on the
     * originating market data provider.
     * 
     * <p>This is the same as {@link #getExchangeTimestamp()}.
     *
     * @return a <code>String</code> value
     */
    String getQuoteDate();
    /**
     * Get the action value indicating how this quote should be processed.
     *
     * @return a <code>QuoteAction</code> value
     */
    QuoteAction getAction();
    /**
     * Get the book level of the quote.
     *
     * <p>The level of the top-of-book is 1, the number 2 quote in the book is level 2, etc.
     *
     * @return an <code>int</code> value
     */
    int getLevel();
    /**
     * Set the book level of the quote.
     *
     * <p>The level of the top-of-book is 1, the number 2 quote in the book is level 2, etc.
     * 
     * @param inLevel an <code>int</code> value
     */
    void setLevel(int inLevel);
    /**
     * Get the number of quotes at this level of the book.
     *
     * <p>This attribute applies to aggregated quotes only. Some adapters may support it, some may not.
     *
     * @return an <code>int</code> value
     */
    int getCount();
}
