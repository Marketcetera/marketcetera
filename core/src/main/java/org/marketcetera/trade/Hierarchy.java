package org.marketcetera.trade;

import java.util.EnumSet;
import java.util.Set;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Indicates the hierarchy of an order, if any.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.0
 */
@ClassVersion("$Id$")
public enum Hierarchy
{
    /**
     * order has no hierarchy
     */
    Flat,
    /**
     * order is parent of 1 or more children
     */
    Parent,
    /**
     * order is the child of another order
     */
    Child;
    /**
     * Indicates that the order should be included in positions.
     *
     * @return a <code>boolean</code> value
     */
    public boolean forPositions()
    {
        return POSITIONS.contains(this);
    }
    /**
     * Indicates that the order should be included in orders.
     *
     * @return a <code>boolean</code> value
     */
    public boolean forOrders()
    {
        return ORDERS.contains(this);
    }
    /**
     * Indicates that the order should be allowed to be canceled.
     *
     * @return a <code>boolean</code> value
     */
    public boolean allowCancel()
    {
        return CANCELLABLE.contains(this);
    }
    /**
     * values used for positions
     */
    private static final Set<Hierarchy> POSITIONS = EnumSet.of(Flat,Parent);
    /**
     * values used for orders
     */
    private static final Set<Hierarchy> ORDERS = EnumSet.of(Flat,Child);
    /**
     * values used for cancels
     */
    private static final Set<Hierarchy> CANCELLABLE = EnumSet.of(Flat,Child);
}
