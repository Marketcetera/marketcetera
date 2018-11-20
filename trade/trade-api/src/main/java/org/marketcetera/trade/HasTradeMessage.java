package org.marketcetera.trade;

/* $License$ */

/**
 * Indicates that the implementer has an {@link TradeMessage}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface HasTradeMessage
{
    /**
     * Get the trade message value.
     *
     * @return a <code>TradeMessage</code> value
     */
    TradeMessage getTradeMessage();
}
