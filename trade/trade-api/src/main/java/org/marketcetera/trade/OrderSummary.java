package org.marketcetera.trade;

import java.math.BigDecimal;
import java.util.Date;

import org.marketcetera.admin.User;

/* $License$ */

/**
 * Provides a summary of each order.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface OrderSummary
{
    /**
     * Get the orderStatus value.
     *
     * @return an <code>OrderStatus</code> value
     */
    OrderStatus getOrderStatus();
    /**
     * Get the report value.
     *
     * @return a <code>Report</code> value
     */
    Report getReport();
    /**
     * Get the rootOrderId value.
     *
     * @return an <code>OrderID</code> value
     */
    OrderID getRootOrderId();
    /**
     * Get the orderId value.
     *
     * @return an <code>OrderID</code> value
     */
    OrderID getOrderId();
    /**
     * Get the cumulative quantity value.
     *
     * @return a <code>BigDecimal</code> value
     */
    BigDecimal getCumulativeQuantity();
    /**
     * Get the account value.
     *
     * @return a <code>String</code> value
     */
    String getAccount();
    /**
     * Get the brokerId value.
     *
     * @return a <code>BrokerID</code> value
     */
    BrokerID getBrokerId();
    /**
     * Get the side value.
     *
     * @return a <code>Side</code> value
     */
    Side getSide();
    /**
     * Get the instrument value.
     *
     * @return an <code>Instrument</code> value
     */
    Instrument getInstrument();
    /**
     * Get the average price value.
     *
     * @return a <code>BigDecimal</code> value
     */
    BigDecimal getAveragePrice();
    /**
     * Get the last quantity value.
     *
     * @return a <code>BigDecimal</code> value
     */
    BigDecimal getLastQuantity();
    /**
     * Get the leaves quantity value.
     *
     * @return a <code>BigDecimal</code> value
     */
    BigDecimal getLeavesQuantity();
    /**
     * Get the order quantity value.
     *
     * @return a <code>BigDecimal</code> value
     */
    BigDecimal getOrderQuantity();
    /**
     * Get the last price value.
     *
     * @return a <code>BigDecimal</code> value
     */
    BigDecimal getLastPrice();
    /**
     * Get the order price value.
     *
     * @return a <code>BigDecimal</code> value
     */
    BigDecimal getOrderPrice();
    /**
     * Get the sending time value.
     *
     * @return a <code>Date</code> value
     */
    Date getSendingTime();
    /**
     * Get the transact time value.
     *
     * @return a <code>Date</code> value
     */
    Date getTransactTime();
    /**
     * Get the order status actor value.
     *
     * @return a <code>User</code> value
     */
    User getActor();
    /**
     * Get the order status viewer value.
     *
     * @return a <code>User</code> value
     */
    User getViewer();
}
