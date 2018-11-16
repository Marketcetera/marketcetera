package org.marketcetera.core;

import java.math.BigDecimal;

import org.marketcetera.core.Pair;

/* $License$ */

/**
 * Tracks price/quantity pairs for an order.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class PriceQtyTuple
        extends Pair<BigDecimal,BigDecimal>
{
    /**
     * Get the qty value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getQty()
    {
        return getSecondMember();
    }
    /**
     * Get the price value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getPrice()
    {
        return getFirstMember();
    }
    /**
     * Create a new PriceQtyTuple instance.
     *
     * @param inPrice a <code>BigDecimal</code> value
     * @param inQty a <code>BigDecimal</code> value
     */
    public PriceQtyTuple(BigDecimal inPrice,
                         BigDecimal inQty)
    {
        super(inPrice,
              inQty);
    }
}
