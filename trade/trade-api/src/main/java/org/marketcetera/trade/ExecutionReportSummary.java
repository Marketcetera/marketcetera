package org.marketcetera.trade;

import java.math.BigDecimal;
import java.util.Date;

import org.marketcetera.admin.User;

/* $License$ */

/**
 * Represents an execution report received as part of a trade flow.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface ExecutionReportSummary
{
    /**
     * Gets the order id value.
     *
     * @return an <code>OrderID</code> value
     */
    OrderID getOrderID();
    /**
     * Gets the original order id value.
     *
     * @return an <code>OrderID</code> value
     */
    OrderID getOrigOrderID();
    /**
     * Gets the security type value.
     *
     * @return a <code>SecurityType</code> value
     */
    SecurityType getSecurityType();
    /**
     * Gets the symbol value.
     *
     * @return a <code>String</code> value
     */
    String getSymbol();
    /**
     * Gets the expiry value.
     *
     * @return a <code>String</code> value
     */
    String getExpiry();
    /**
     * Gets the strike price value.
     *
     * @return a <code>BigDecimal</code> value
     */
    BigDecimal getStrikePrice();
    /**
     * Gets the option type value.
     *
     * @return an <code>OptionType</code> value
     */
    OptionType getOptionType();
    /**
     * Gets the account value.
     *
     * @return a <code>String</code> value
     */
    String getAccount();
    /**
     * Get the rootOrderId value.
     *
     * @return an <code>OrderID</code> value
     */
    OrderID getRootOrderID();
    /**
     * Get the side value.
     *
     * @return a <code>Side</code> value
     */
    Side getSide();
    /**
     * Get the cumQuantity value.
     *
     * @return a <code>BigDecimal</code> value
     */
    BigDecimal getCumQuantity();
    /**
     * Get the effectiveCumQuantity value.
     *
     * @return a <code>BigDecimal</code> value
     */
    BigDecimal getEffectiveCumQuantity();
    /**
     * Get the avgPrice value.
     *
     * @return a <code>BigDecimal</code> value
     */
    BigDecimal getAvgPrice();
    /**
     * Get the lastQuantity value.
     *
     * @return a <code>BigDecimal</code> value
     */
    BigDecimal getLastQuantity();
    /**
     * Get the lastPrice value.
     *
     * @return a <code>BigDecimal</code> value
     */
    BigDecimal getLastPrice();
    /**
     * Get the orderStatus value.
     *
     * @return an <code>OrderStatus</code> value
     */
    OrderStatus getOrderStatus();
    /**
     * Get the execType value.
     *
     * @return an <code>ExecutionType</code> value
     */
    ExecutionType getExecType();
    /**
     * Get the sendingTime value.
     *
     * @return a <code>Date</code> value
     */
    Date getSendingTime();
    /**
     * Get the viewer value.
     *
     * @return a <code>User</code> value
     */
    User getViewer();
    /**
     * Get the actor value.
     *
     * @return a <code>User</code> value
     */
    User getActor();
    /**
     * Get the report value.
     *
     * @return a <code>Report</code> value
     */
    Report getReport();
    /**
     * Gets the viewer ID value.
     *
     * @return a <code>UserID</code> value or <code>null</code>
     */
    UserID getViewerID();
}
