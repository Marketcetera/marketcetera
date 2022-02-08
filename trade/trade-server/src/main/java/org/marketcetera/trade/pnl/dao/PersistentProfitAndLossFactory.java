//
// this file is automatically generated
//
package org.marketcetera.trade.pnl.dao;

/* $License$ */

/**
 * Creates new {@link PersistentProfitAndLoss} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class PersistentProfitAndLossFactory
        implements org.marketcetera.trade.pnl.ProfitAndLossFactory
{
    /**
     * Create a new <code>org.marketcetera.trade.pnl.dao.PersistentProfitAndLoss</code> instance.
     *
     * @return a <code>org.marketcetera.trade.pnl.dao.PersistentProfitAndLoss</code> value
     */
    @Override
    public org.marketcetera.trade.pnl.dao.PersistentProfitAndLoss create()
    {
        return new org.marketcetera.trade.pnl.dao.PersistentProfitAndLoss();
    }
    /**
     * Create a new <code>org.marketcetera.trade.pnl.dao.PersistentProfitAndLoss</code> instance from the given object.
     *
     * @param inObject a <code>org.marketcetera.trade.pnl.dao.PersistentProfitAndLoss</code> value
     * @return a <code>org.marketcetera.trade.pnl.dao.PersistentProfitAndLoss</code> value
     */
    @Override
    public org.marketcetera.trade.pnl.dao.PersistentProfitAndLoss create(org.marketcetera.trade.pnl.ProfitAndLoss inPersistentProfitAndLoss)
    {
        return new org.marketcetera.trade.pnl.dao.PersistentProfitAndLoss(inPersistentProfitAndLoss);
    }
}
