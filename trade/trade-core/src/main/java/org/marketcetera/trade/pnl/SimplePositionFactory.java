//
// this file is automatically generated
//
package org.marketcetera.trade.pnl;

/* $License$ */

/**
 * Creates new {@link SimplePosition} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SimplePositionFactory
        implements org.marketcetera.trade.pnl.PositionFactory
{
    /**
     * Create a new <code>org.marketcetera.trade.pnl.SimplePosition</code> instance.
     *
     * @return a <code>org.marketcetera.trade.pnl.SimplePosition</code> value
     */
    @Override
    public org.marketcetera.trade.pnl.SimplePosition create()
    {
        return new org.marketcetera.trade.pnl.SimplePosition();
    }
    /**
     * Create a new <code>org.marketcetera.trade.pnl.SimplePosition</code> instance from the given object.
     *
     * @param inPosition an <code>org.marketcetera.trade.pnl.SimplePosition</code> value
     * @return an <code>org.marketcetera.trade.pnl.SimplePosition</code> value
     */
    @Override
    public org.marketcetera.trade.pnl.SimplePosition create(org.marketcetera.trade.pnl.Position inSimplePosition)
    {
        return new org.marketcetera.trade.pnl.SimplePosition(inSimplePosition);
    }
}
