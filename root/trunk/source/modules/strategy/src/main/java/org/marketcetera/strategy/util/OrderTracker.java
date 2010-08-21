package org.marketcetera.strategy.util;

import static java.math.BigDecimal.ZERO;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static org.marketcetera.strategy.Messages.*;

import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.marketcetera.trade.*;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

import quickfix.FieldNotFound;
import quickfix.field.ClOrdID;

/* $License$ */

/**
 * Tracks the status of an order based on an aggregation of {@link ExecutionReport} objects.
 * 
 * <p>This class does not enforce valid state changes nor does it check FIX versions.  It
 * simply tracks execution reports for a given order and caches the most recent status.  It
 * is up to the caller to make sure that the execution reports are delivered to the tracker
 * in the order they are received.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ThreadSafe
@ClassVersion("$Id$")
public class OrderTracker
{
    /**
     * Create a new OrderTracker instance.
     *
     * @param inOrder an <code>Order</code> value
     * @throws UnsupportedOperationException if the given <code>Order</code> is of an unsupported type
     * @throws IllegalArgumentException if the given <code>Order</code> does not contain a valid <code>OrderID</code>
     */
    public OrderTracker(Order inOrder)
    {
        Validate.notNull(inOrder,
                         NULL_ORDER.getText());
        order = inOrder;
        try {
            underlyingOrderID = getUnderlyingOrderID(order);
        } catch (FieldNotFound e) {
            throw new IllegalArgumentException(NO_ORDER_ID.getText(inOrder));
        }
    }
    /**
     * Gets the <code>Order</code> being tracked.
     *
     * @return an <code>Order</code> value
     */
    public Order getUnderlyingOrder()
    {
        return order;
    }
    /**
     * Get the underlyingOrderID value.
     *
     * @return a <code>OrderID</code> value
     */
    public OrderID getUnderlyingOrderID()
    {
        return underlyingOrderID;
    }
    /**
     * Gets the current <code>OrderStatus</code> value.
     *
     * @return an <code>OrderStatus</code> value
     */
    public OrderStatus getStatus()
    {
        return currentStatus;
    }
    /**
     * Get the quantity value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getOrderQuantity()
    {
        return orderQuantity;
    }
    /**
     * Get the averagePrice value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getAveragePrice()
    {
        return averagePrice;
    }
    /**
     * Get the cumulativeQuantity value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getCumulativeQuantity()
    {
        return cumulativeQuantity;
    }
    /**
     * Get the lastPrice value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getLastPrice()
    {
        return lastPrice;
    }
    /**
     * Get the lastQuantity value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getLastQuantity()
    {
        return lastQuantity;
    }
    /**
     * Get the leavesQuantity value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getLeavesQuantity()
    {
        return leavesQuantity;
    }
    /**
     * Processes an <code>ExecutionReport</code>.
     *
     * @param inReport an <code>ExecutionReport</code> value
     * @return an <code>OrderStatus</code> value
     * @throws IllegalArgumentException if the given <code>ExecutionReport</code> is <code>null</code>
     */
    public synchronized OrderStatus process(ExecutionReport inReport)
    {
        // the report applies to the order the IDs match (as determined by the type of the order)
        SLF4JLoggerProxy.debug(OrderTracker.class,
                               "Received {}",
                               inReport);
        Validate.notNull(inReport,
                         NULL_REPORT.getText());
        if(!checkOrderID(inReport)) {
            return currentStatus;
        }
        reports.add(inReport);
        currentStatus = inReport.getOrderStatus() == null ? currentStatus : inReport.getOrderStatus();
        averagePrice = inReport.getAveragePrice() == null ? averagePrice : inReport.getAveragePrice();
        cumulativeQuantity = inReport.getCumulativeQuantity() == null ? cumulativeQuantity : inReport.getCumulativeQuantity();
        lastPrice = inReport.getLastPrice() == null ? lastPrice : inReport.getLastPrice();
        lastQuantity = inReport.getLastQuantity() == null ? lastQuantity : inReport.getLastQuantity();
        leavesQuantity = inReport.getLeavesQuantity() == null ? leavesQuantity : inReport.getLeavesQuantity();
        orderQuantity = inReport.getOrderQuantity() == null ? orderQuantity : inReport.getOrderQuantity();
        return currentStatus;
    }
    /**
     * Gets the list of <code>ExecutionReport</code> objects accepted by the tracker in the order
     * they were received. 
     *
     * @return a <code>List&lt;ExecutionReport&gt;</code> value
     */
    public List<ExecutionReport> getReports()
    {
        return Collections.unmodifiableList(reports);
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "";
    }
    /**
     * Determines if the given <code>ExecutionReport</code> applies to the underlying
     * order being tracked.
     *
     * @param inReport an <code>ExecutionReport</code> value
     */
    private boolean checkOrderID(ExecutionReport inReport)
    {
        OrderID orderID = inReport.getOrderID();
        boolean comparison = underlyingOrderID.equals(orderID);
        SLF4JLoggerProxy.debug(OrderTracker.class,
                               "Execution report ID is {}, underlying orderID is {}, result is {}",
                               orderID,
                               underlyingOrderID,
                               comparison);
        return comparison;
    }
    /**
     * Gets the <code>OrderID</code> of the given <code>Order</code>.
     *
     * @return an <code>OrderID</code> value
     * @throws FieldNotFound if the <code>Order</code> is a FIX order and does not contain the proper field
     * @throws UnsupportedOperationException if the given <code>Order</code> is of an unsupported type
     */
    private OrderID getUnderlyingOrderID(Order inOrder)
            throws FieldNotFound
    {
        if(inOrder instanceof FIXOrderImpl) {
            FIXOrderImpl fixOrder = (FIXOrderImpl)inOrder;
            ClOrdID oid = new quickfix.field.ClOrdID();
            fixOrder.getMessage().getField(oid);
            String oidValue = StringUtils.trimToNull(oid.getValue());
            Validate.notNull(oidValue,
                             NULL_ORDER_ID.getText(inOrder));
            return new OrderID(oidValue);
        } else if(inOrder instanceof OrderBase) {
            return ((OrderBase)inOrder).getOrderID();
        }
        throw new UnsupportedOperationException(UNKNOWN_ORDER_TYPE.getText(inOrder));
    }
    /**
     * the most recent order quantity value
     */
    private volatile BigDecimal orderQuantity = ZERO;
    /**
     * the most recent average price value
     */
    private volatile BigDecimal averagePrice = ZERO;
    /**
     * the most recent cumulative quantity value
     */
    private volatile BigDecimal cumulativeQuantity = ZERO;
    /**
     * the most recent last price value
     */
    private volatile BigDecimal lastPrice = ZERO;
    /**
     * the most recent last quantity value
     */
    private volatile BigDecimal lastQuantity = ZERO;
    /**
     * the most recent leaves quantity value
     */
    private volatile BigDecimal leavesQuantity = ZERO;
    /**
     * the most recent current status value
     */
    private volatile OrderStatus currentStatus = OrderStatus.Unknown;
    /**
     * the cumulative list of <code>ExecutionReport</code> objects received and retained
     */
    private final List<ExecutionReport> reports = new ArrayList<ExecutionReport>();
    /**
     * holds the orderID of the base order - used to compare against execution reports as they come in
     */
    private final OrderID underlyingOrderID;
    /**
     * the original underlying order
     */
    private final Order order;
}
