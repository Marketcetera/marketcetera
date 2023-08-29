//
// this file is automatically generated
//
package org.marketcetera.trade.pnl;

/* $License$ */

/**
 * Creates new {@link SimpleTrade} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SimpleTradeFactory
        implements org.marketcetera.trade.pnl.TradeFactory
{
    /**
     * Create a new <code>org.marketcetera.trade.pnl.SimpleTrade</code> instance.
     *
     * @return a <code>org.marketcetera.trade.pnl.SimpleTrade</code> value
     */
    @Override
    public org.marketcetera.trade.pnl.SimpleTrade create()
    {
        return new org.marketcetera.trade.pnl.SimpleTrade();
    }
    /**
     * Create a new <code>org.marketcetera.trade.pnl.SimpleTrade</code> instance from the given object.
     *
     * @param inTrade an <code>org.marketcetera.trade.pnl.SimpleTrade</code> value
     * @return an <code>org.marketcetera.trade.pnl.SimpleTrade</code> value
     */
    @Override
    public org.marketcetera.trade.pnl.SimpleTrade create(org.marketcetera.trade.pnl.Trade inSimpleTrade)
    {
        return new org.marketcetera.trade.pnl.SimpleTrade(inSimpleTrade);
    }
}
