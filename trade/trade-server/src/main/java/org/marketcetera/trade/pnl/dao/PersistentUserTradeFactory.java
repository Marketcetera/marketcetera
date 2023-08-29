//
// this file is automatically generated
//
package org.marketcetera.trade.pnl.dao;

/* $License$ */

/**
 * Creates new {@link PersistentUserTrade} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class PersistentUserTradeFactory
        implements org.marketcetera.trade.pnl.UserTradeFactory
{
    /**
     * Create a new <code>org.marketcetera.trade.pnl.dao.PersistentUserTrade</code> instance.
     *
     * @return a <code>org.marketcetera.trade.pnl.dao.PersistentUserTrade</code> value
     */
    @Override
    public org.marketcetera.trade.pnl.dao.PersistentUserTrade create()
    {
        return new org.marketcetera.trade.pnl.dao.PersistentUserTrade();
    }
    /**
     * Create a new <code>org.marketcetera.trade.pnl.dao.PersistentUserTrade</code> instance from the given object.
     *
     * @param inUserTrade an <code>org.marketcetera.trade.pnl.dao.PersistentUserTrade</code> value
     * @return an <code>org.marketcetera.trade.pnl.dao.PersistentUserTrade</code> value
     */
    @Override
    public org.marketcetera.trade.pnl.dao.PersistentUserTrade create(org.marketcetera.trade.pnl.UserTrade inPersistentUserTrade)
    {
        return new org.marketcetera.trade.pnl.dao.PersistentUserTrade(inPersistentUserTrade);
    }
}
