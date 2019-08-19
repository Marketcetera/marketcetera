package org.marketcetera.trade;

import java.math.BigDecimal;
import java.util.Date;

import org.marketcetera.admin.User;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MutableOrderSummary
        extends OrderSummary
{
    /**
     * Set the orderStatus value.
     *
     * @param inOrderStatus an <code>OrderStatus</code> value
     */
    void setOrderStatus(OrderStatus inOrderStatus);
    /**
     * Set the report value.
     *
     * @param inReport a <code>Report</code> value
     */
     void setReport(Report inReport);
    /**
     * Set the rootOrderId value.
     *
     * @param inRootOrderId an <code>OrderID</code> value
     */
    void setRootOrderId(OrderID inRootOrderId);
    /**
     * Set the orderId value.
     *
     * @param inOrderId an <code>OrderID</code> value
     */
    void setOrderId(OrderID inOrderId);
    /**
     * Set the cumulative quantity value.
     *
     * @param inCumulativeQuantity a <code>BigDecimal</code> value
     */
    void setCumulativeQuantity(BigDecimal inCumulativeQuantity);
    /**
     * Set the account value.
     *
     * @param inAccount a <code>String</code> value
     */
    void setAccount(String inAccount);
    /**
     * Set the brokerId value.
     *
     * @param inBrokerId a <code>BrokerID</code> value
     */
    void setBrokerId(BrokerID inBrokerId);
    /**
     * Set the side value.
     *
     * @param inSide a <code>Side</code> value
     */
    void setSide(Side inSide);
    /**
     * Set the instrument value.
     *
     * @param inInstrument an <code>Instrument</code> value
     */
    void setInstrument(Instrument inInstrument);
    /**
     * Set the average price value.
     *
     * @param inAveragePrice a <code>BigDecimal</code> value
     */
    void setAveragePrice(BigDecimal inAveragePrice);
    /**
     * Set the last quantity value.
     *
     * @param inLastQuantity a <code>BigDecimal</code> value
     */
    void setLastQuantity(BigDecimal inLastQuantity);
    /**
     * Set the leaves quantity value.
     *
     * @param inLeavesQuantity a <code>BigDecimal</code> value
     */
    void setLeavesQuantity(BigDecimal inLeavesQuantity);
    /**
     * Set the order quantity value.
     *
     * @param inOrderQuantity a <code>BigDecimal</code> value
     */
    void setOrderQuantity(BigDecimal inOrderQuantity);
    /**
     * Set the last price value.
     *
     * @param inLastPrice a <code>BigDecimal</code> value
     */
    void setLastPrice(BigDecimal inLastPrice);
    /**
     * Set the order price value.
     *
     * @param inOrderPrice a <code>BigDecimal</code> value
     */
    void setOrderPrice(BigDecimal inOrderPrice);
    /**
     * Set the sending time value.
     *
     * @param inSendingTime a <code>Date</code> value
     */
    void setSendingTime(Date inSendingTime);
    /**
     * Set the transact time value.
     *
     * @param inTransactTime a <code>Date</code> value
     */
    void setTransactTime(Date inTransactTime);
    /**
     * Set the order status actor value.
     *
     * @param inActor a <code>User</code> value
     */
    void setActor(User inActor);
    /**
     * Set the order status viewer value.
     *
     * @param inViewer a <code>User</code> value
     */
    void setViewer(User inViewer);
}
