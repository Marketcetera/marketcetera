//
// this file is automatically generated
//
package org.marketcetera.trade.pnl;

import org.marketcetera.core.Factory;
import org.marketcetera.core.Preserve;

/* $License$ */

/**
 * Creates new {@link Trade} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Preserve
public interface TradeFactory
        extends Factory<Trade>
{
    /**
     * Create a new <code>Trade</code> instance.
     *
     * @return a <code>Trade</code> value
     */
    @Override
    Trade create();
    /**
     * Create a new <code>Trade</code> instance from the given object.
     *
     * @param inTrade a <code>Trade</code> value
     * @return a <code>Trade</code> value
     */
    @Override
    Trade create(Trade inTrade);
}
