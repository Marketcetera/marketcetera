package org.marketcetera.trade;

import java.math.BigDecimal;
import java.util.Date;

import org.marketcetera.admin.User;
import org.marketcetera.core.BigDecimalUtil;

import io.swagger.v3.oas.annotations.media.Schema;

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
    public Date getSendingTime()
    {
        return sendingTime;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.OrderSummary#getTransactTime()
     */
    @Override
    public Date getTransactTime()
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
     * @see org.marketcetera.trade.MutableOrderSummary#setSendingTime(java.util.Date)
     */
    @Override
    public void setSendingTime(Date inSendingTime)
    {
        sendingTime = inSendingTime;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableOrderSummary#setTransactTime(java.util.Date)
     */
    @Override
    public void setTransactTime(Date inTransactTime)
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
    @Schema(type="string",example="Filled")
    private OrderStatus orderStatus;
    /**
     * report value
     */
    private Report report;
    /**
     * root order id value
     */
    @Schema(type="string",example="10001001")
    private OrderID rootOrderId;
    /**
     * order id value
     */
    @Schema(type="string",example="10001005")
    private OrderID orderId;
    /**
     * cumulative quantity value
     */
    @Schema(type="number",example="100.00")
    private BigDecimal cumulativeQuantity;
    /**
     * account value
     */
    @Schema(type="string",example="broker-assigned-account")
    private String account;
    /**
     * broker id value
     */
    @Schema(type="string",example="my-broker-id")
    private BrokerID brokerId;
    /**
     * side value
     */
    @Schema(type="string",example="Buy")
    private Side side;
    /**
     * instrument value
     */
    @Schema(type="string",example="IBM")
    private Instrument instrument;
    /**
     * average price value
     */
    @Schema(type="number",example="160.55")
    private BigDecimal averagePrice;
    /**
     * last quantity value
     */
    @Schema(type="number",example="20.00")
    private BigDecimal lastQuantity;
    /**
     * leaves quantity value
     */
    @Schema(type="number",example="80.00")
    private BigDecimal leavesQuantity;
    /**
     * order quantity value
     */
    @Schema(type="number",example="180.00")
    private BigDecimal orderQuantity;
    /**
     * last price value
     */
    @Schema(type="number",example="160.53")
    private BigDecimal lastPrice;
    /**
     * order price value
     */
    @Schema(type="number",example="160.58")
    private BigDecimal orderPrice;
    /**
     * sending time value
     */
    @Schema(type="string",example="20240110T100452.522Z")
    private Date sendingTime;
    /**
     * transact time value
     */
    @Schema(type="string",example="20240110T100452.522Z")
    private Date transactTime;
    /**
     * actor value
     */
    @Schema(type="integer",example="1234")
    private User actor;
    /**
     * viewer value
     */
    @Schema(type="integer",example="1234")
    private User viewer;
}
