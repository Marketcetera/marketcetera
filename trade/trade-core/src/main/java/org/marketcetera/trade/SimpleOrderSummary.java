package org.marketcetera.trade;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.marketcetera.admin.User;
import org.marketcetera.core.BigDecimalUtil;

/* $License$ */

/**
 * Provides a POJO {@link MutableOrderSummary} implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SimpleOrderSummary
        implements MutableOrderSummary
{
    /* (non-Javadoc)
     * @see org.marketcetera.trade.OrderSummary#getOrderStatus()
     */
    @Override
    public OrderStatus getOrderStatus()
    {
        return orderStatus;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.OrderSummary#getReport()
     */
    @Override
    public Report getReport()
    {
        return report;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.OrderSummary#getRootOrderId()
     */
    @Override
    public OrderID getRootOrderId()
    {
        return rootOrderId;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.OrderSummary#getOrderId()
     */
    @Override
    public OrderID getOrderId()
    {
        return orderId;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.OrderSummary#getCumulativeQuantity()
     */
    @Override
    public BigDecimal getCumulativeQuantity()
    {
        return cumulativeQuantity;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.OrderSummary#getAccount()
     */
    @Override
    public String getAccount()
    {
        return account;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.OrderSummary#getBrokerId()
     */
    @Override
    public BrokerID getBrokerId()
    {
        return brokerId;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.OrderSummary#getSide()
     */
    @Override
    public Side getSide()
    {
        return side;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.OrderSummary#getInstrument()
     */
    @Override
    public Instrument getInstrument()
    {
        return instrument;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.OrderSummary#getAveragePrice()
     */
    @Override
    public BigDecimal getAveragePrice()
    {
        return averagePrice;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.OrderSummary#getLastQuantity()
     */
    @Override
    public BigDecimal getLastQuantity()
    {
        return lastQuantity;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.OrderSummary#getLeavesQuantity()
     */
    @Override
    public BigDecimal getLeavesQuantity()
    {
        return leavesQuantity;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.OrderSummary#getOrderQuantity()
     */
    @Override
    public BigDecimal getOrderQuantity()
    {
        return orderQuantity;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.OrderSummary#getLastPrice()
     */
    @Override
    public BigDecimal getLastPrice()
    {
        return lastPrice;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.OrderSummary#getOrderPrice()
     */
    @Override
    public BigDecimal getOrderPrice()
    {
        return orderPrice;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.OrderSummary#getSendingTime()
     */
    @Override
    public LocalDateTime getSendingTime()
    {
        return sendingTime;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.OrderSummary#getTransactTime()
     */
    @Override
    public LocalDateTime getTransactTime()
    {
        return transactTime;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.OrderSummary#getActor()
     */
    @Override
    public User getActor()
    {
        return actor;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.OrderSummary#getViewer()
     */
    @Override
    public User getViewer()
    {
        return viewer;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableOrderSummary#setOrderStatus(org.marketcetera.trade.OrderStatus)
     */
    @Override
    public void setOrderStatus(OrderStatus inOrderStatus)
    {
        orderStatus = inOrderStatus;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableOrderSummary#setReport(org.marketcetera.trade.Report)
     */
    @Override
    public void setReport(Report inReport)
    {
        report = inReport;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableOrderSummary#setRootOrderId(org.marketcetera.trade.OrderID)
     */
    @Override
    public void setRootOrderId(OrderID inRootOrderId)
    {
        rootOrderId = inRootOrderId;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableOrderSummary#setOrderId(org.marketcetera.trade.OrderID)
     */
    @Override
    public void setOrderId(OrderID inOrderId)
    {
        orderId = inOrderId;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableOrderSummary#setCumulativeQuantity(java.math.BigDecimal)
     */
    @Override
    public void setCumulativeQuantity(BigDecimal inCumulativeQuantity)
    {
        cumulativeQuantity = inCumulativeQuantity;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableOrderSummary#setAccount(java.lang.String)
     */
    @Override
    public void setAccount(String inAccount)
    {
        account = inAccount;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableOrderSummary#setBrokerId(org.marketcetera.trade.BrokerID)
     */
    @Override
    public void setBrokerId(BrokerID inBrokerId)
    {
        brokerId = inBrokerId;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableOrderSummary#setSide(org.marketcetera.trade.Side)
     */
    @Override
    public void setSide(Side inSide)
    {
        side = inSide;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableOrderSummary#setInstrument(org.marketcetera.trade.Instrument)
     */
    @Override
    public void setInstrument(Instrument inInstrument)
    {
        instrument = inInstrument;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableOrderSummary#setAveragePrice(java.math.BigDecimal)
     */
    @Override
    public void setAveragePrice(BigDecimal inAveragePrice)
    {
        averagePrice = inAveragePrice;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableOrderSummary#setLastQuantity(java.math.BigDecimal)
     */
    @Override
    public void setLastQuantity(BigDecimal inLastQuantity)
    {
        lastQuantity = inLastQuantity;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableOrderSummary#setLeavesQuantity(java.math.BigDecimal)
     */
    @Override
    public void setLeavesQuantity(BigDecimal inLeavesQuantity)
    {
        leavesQuantity = inLeavesQuantity;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableOrderSummary#setOrderQuantity(java.math.BigDecimal)
     */
    @Override
    public void setOrderQuantity(BigDecimal inOrderQuantity)
    {
        orderQuantity = inOrderQuantity;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableOrderSummary#setLastPrice(java.math.BigDecimal)
     */
    @Override
    public void setLastPrice(BigDecimal inLastPrice)
    {
        lastPrice = inLastPrice;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableOrderSummary#setOrderPrice(java.math.BigDecimal)
     */
    @Override
    public void setOrderPrice(BigDecimal inOrderPrice)
    {
        orderPrice = inOrderPrice;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableOrderSummary#setSendingTime(java.time.LocalDateTime)
     */
    @Override
    public void setSendingTime(LocalDateTime inSendingTime)
    {
        sendingTime = inSendingTime;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableOrderSummary#setTransactTime(java.time.LocalDateTime)
     */
    @Override
    public void setTransactTime(LocalDateTime inTransactTime)
    {
        transactTime = inTransactTime;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableOrderSummary#setActor(org.marketcetera.admin.User)
     */
    @Override
    public void setActor(User inActor)
    {
        actor = inActor;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableOrderSummary#setViewer(org.marketcetera.admin.User)
     */
    @Override
    public void setViewer(User inViewer)
    {
        viewer = inViewer;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("SimpleOrderSummary [orderId=").append(orderId).append(", orderStatus=").append(orderStatus)
                .append(", rootOrderId=").append(rootOrderId).append(", cumulativeQuantity=").append(BigDecimalUtil.render(cumulativeQuantity))
                .append(", account=").append(account).append(", brokerId=").append(brokerId).append(", side=")
                .append(side).append(", instrument=").append(instrument).append(", averagePrice=").append(BigDecimalUtil.renderCurrency(averagePrice))
                .append(", lastQuantity=").append(BigDecimalUtil.render(lastQuantity)).append(", leavesQuantity=").append(BigDecimalUtil.render(leavesQuantity))
                .append(", orderQuantity=").append(BigDecimalUtil.render(orderQuantity)).append(", lastPrice=").append(BigDecimalUtil.renderCurrency(lastPrice))
                .append(", orderPrice=").append(BigDecimalUtil.renderCurrency(orderPrice)).append(", sendingTime=").append(sendingTime)
                .append(", transactTime=").append(transactTime).append(", actor=").append(actor).append(", viewer=")
                .append(viewer).append(", report=").append(report).append("]");
        return builder.toString();
    }
    /**
     * order status value
     */
    private OrderStatus orderStatus;
    /**
     * report value
     */
    private Report report;
    /**
     * root order id value
     */
    private OrderID rootOrderId;
    /**
     * order id value
     */
    private OrderID orderId;
    /**
     * cumulative quantity value
     */
    private BigDecimal cumulativeQuantity;
    /**
     * account value
     */
    private String account;
    /**
     * broker id value
     */
    private BrokerID brokerId;
    /**
     * side value
     */
    private Side side;
    /**
     * instrument value
     */
    private Instrument instrument;
    /**
     * average price value
     */
    private BigDecimal averagePrice;
    /**
     * last quantity value
     */
    private BigDecimal lastQuantity;
    /**
     * leaves quantity value
     */
    private BigDecimal leavesQuantity;
    /**
     * order quantity value
     */
    private BigDecimal orderQuantity;
    /**
     * last price value
     */
    private BigDecimal lastPrice;
    /**
     * order price value
     */
    private BigDecimal orderPrice;
    /**
     * sending time value
     */
    private LocalDateTime sendingTime;
    /**
     * transact time value
     */
    private LocalDateTime transactTime;
    /**
     * actor value
     */
    private User actor;
    /**
     * viewer value
     */
    private User viewer;
}
