//
// this file is automatically generated
//
package org.marketcetera.trade.pnl;

/* $License$ */

/**
 * Identifies a lot used to calculate profit and loss.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface Lot
        extends org.marketcetera.trade.pnl.HasPosition,org.marketcetera.trade.pnl.HasTrade,org.marketcetera.admin.HasUser
{
    /**
     * Get the quantity value.
     *
     * @return a <code>java.math.BigDecimal</code> value
     */
    java.math.BigDecimal getQuantity();
    /**
     * Set the quantity value.
     *
     * @param inQuantity a <code>java.math.BigDecimal</code> value
     */
    void setQuantity(java.math.BigDecimal inQuantity);
    /**
     * Get the allocatedQuantity value.
     *
     * @return a <code>java.math.BigDecimal</code> value
     */
    java.math.BigDecimal getAllocatedQuantity();
    /**
     * Set the allocatedQuantity value.
     *
     * @param inAllocatedQuantity a <code>java.math.BigDecimal</code> value
     */
    void setAllocatedQuantity(java.math.BigDecimal inAllocatedQuantity);
    /**
     * Get the effectiveDate value.
     *
     * @return a <code>java.util.Date</code> value
     */
    java.util.Date getEffectiveDate();
    /**
     * Set the effectiveDate value.
     *
     * @param inEffectiveDate a <code>java.util.Date</code> value
     */
    void setEffectiveDate(java.util.Date inEffectiveDate);
    /**
     * Get the basisPrice value.
     *
     * @return a <code>java.math.BigDecimal</code> value
     */
    java.math.BigDecimal getBasisPrice();
    /**
     * Set the basisPrice value.
     *
     * @param inBasisPrice a <code>java.math.BigDecimal</code> value
     */
    void setBasisPrice(java.math.BigDecimal inBasisPrice);
    /**
     * Get the gain value.
     *
     * @return a <code>java.math.BigDecimal</code> value
     */
    java.math.BigDecimal getGain();
    /**
     * Set the gain value.
     *
     * @param inGain a <code>java.math.BigDecimal</code> value
     */
    void setGain(java.math.BigDecimal inGain);
    /**
     * Get the tradePrice value.
     *
     * @return a <code>java.math.BigDecimal</code> value
     */
    java.math.BigDecimal getTradePrice();
    /**
     * Set the tradePrice value.
     *
     * @param inTradePrice a <code>java.math.BigDecimal</code> value
     */
    void setTradePrice(java.math.BigDecimal inTradePrice);
}
