package org.marketcetera.trade.pnl.dao;

import java.math.BigDecimal;

/* $License$ */

/**
 * Contains the results of a query to calculate lot values.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class LotCalculationResult
{
    /**
     * Create a new LotCalculationResult instance.
     *
     * @param inExtendedQuantity a <code>BigDecimal</code> value
     * @param inTotalQuantity a <code>BigDecimal</code> value
     */
    public LotCalculationResult(BigDecimal inTotalQuantity,
                                BigDecimal inExtendedQuantity)
    {
        extendedQuantity = inExtendedQuantity;
        totalQuantity = inTotalQuantity;
    }
    /**
     * Get the extendedQuantity value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getExtendedQuantity()
    {
        return extendedQuantity;
    }
    /**
     * Sets the extendedQuantity value.
     *
     * @param inExtendedQuantity a <code>BigDecimal</code> value
     */
    public void setExtendedQuantity(BigDecimal inExtendedQuantity)
    {
        extendedQuantity = inExtendedQuantity;
    }
    /**
     * Get the totalQuantity value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getTotalQuantity()
    {
        return totalQuantity;
    }
    /**
     * Sets the totalQuantity value.
     *
     * @param inTotalQuantity a <code>BigDecimal</code> value
     */
    public void setTotalQuantity(BigDecimal inTotalQuantity)
    {
        totalQuantity = inTotalQuantity;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("LotCalculationResult [extendedQuantity=")
                .append(extendedQuantity).append(", totalQuantity=").append(totalQuantity).append("]");
        return builder.toString();
    }
    /**
     * extended quantity
     */
    private BigDecimal extendedQuantity;
    /**
     * total quantity
     */
    private BigDecimal totalQuantity;
}
