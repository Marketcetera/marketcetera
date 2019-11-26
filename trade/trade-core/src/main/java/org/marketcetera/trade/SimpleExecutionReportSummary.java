package org.marketcetera.trade;

import java.math.BigDecimal;
import java.util.Date;

import org.marketcetera.admin.User;

/* $License$ */

/**
 * Provides a POJO {@link MutableExecutionReportSummary} implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SimpleExecutionReportSummary
        implements MutableExecutionReportSummary
{
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getOrderID()
     */
    @Override
    public OrderID getOrderID()
    {
        return orderId;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getOriginalOrderID()
     */
    @Override
    public OrderID getOriginalOrderID()
    {
        return originalOrderId;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getSecurityType()
     */
    @Override
    public SecurityType getSecurityType()
    {
        return securityType;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getSymbol()
     */
    @Override
    public String getSymbol()
    {
        return symbol;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getExpiry()
     */
    @Override
    public String getExpiry()
    {
        return expiry;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getStrikePrice()
     */
    @Override
    public BigDecimal getStrikePrice()
    {
        return strikePrice;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getOptionType()
     */
    @Override
    public OptionType getOptionType()
    {
        return optionType;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getAccount()
     */
    @Override
    public String getAccount()
    {
        return account;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getRootOrderID()
     */
    @Override
    public OrderID getRootOrderID()
    {
        return rootOrderId;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getSide()
     */
    @Override
    public Side getSide()
    {
        return side;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getCumulativeQuantity()
     */
    @Override
    public BigDecimal getCumulativeQuantity()
    {
        return cumulativeQuantity;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getEffectiveCumulativeQuantity()
     */
    @Override
    public BigDecimal getEffectiveCumulativeQuantity()
    {
        return effectiveCumulativeQuantity;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getAvgPrice()
     */
    @Override
    public BigDecimal getAveragePrice()
    {
        return averagePrice;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getLastQuantity()
     */
    @Override
    public BigDecimal getLastQuantity()
    {
        return lastQuantity;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getLastPrice()
     */
    @Override
    public BigDecimal getLastPrice()
    {
        return lastPrice;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getOrderStatus()
     */
    @Override
    public OrderStatus getOrderStatus()
    {
        return orderStatus;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getExecutionType()
     */
    @Override
    public ExecutionType getExecutionType()
    {
        return executionType;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getSendingTime()
     */
    @Override
    public Date getSendingTime()
    {
        return sendingTime;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getViewer()
     */
    @Override
    public User getViewer()
    {
        return viewer;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getActor()
     */
    @Override
    public User getActor()
    {
        return actor;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getReport()
     */
    @Override
    public Report getReport()
    {
        return report;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getViewerID()
     */
    @Override
    public UserID getViewerID()
    {
        return viewer==null?null:viewer.getUserID();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getBrokerOrderId()
     */
    @Override
    public OrderID getBrokerOrderId()
    {
        return brokerOrderId;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getExecutionId()
     */
    @Override
    public String getExecutionId()
    {
        return executionId;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getLeavesQuantity()
     */
    @Override
    public BigDecimal getLeavesQuantity()
    {
        return leavesQuantity;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getMutableVersion()
     */
    @Override
    public MutableExecutionReportSummary getMutableVersion()
    {
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableExecutionReportSummary#setOrderID(org.marketcetera.trade.OrderID)
     */
    @Override
    public void setOrderID(OrderID inOrderId)
    {
        orderId = inOrderId;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableExecutionReportSummary#setOriginalOrderID(org.marketcetera.trade.OrderID)
     */
    @Override
    public void setOriginalOrderID(OrderID inOriginalOrderId)
    {
        originalOrderId = inOriginalOrderId;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableExecutionReportSummary#setSecurityType(org.marketcetera.trade.SecurityType)
     */
    @Override
    public void setSecurityType(SecurityType inSecurityType)
    {
        securityType = inSecurityType;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableExecutionReportSummary#setSymbol(java.lang.String)
     */
    @Override
    public void setSymbol(String inSymbol)
    {
        symbol = inSymbol;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableExecutionReportSummary#setExpiry(java.lang.String)
     */
    @Override
    public void setExpiry(String inExpiry)
    {
        expiry = inExpiry;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableExecutionReportSummary#setStrikePrice(java.math.BigDecimal)
     */
    @Override
    public void setStrikePrice(BigDecimal inStrikePrice)
    {
        strikePrice = inStrikePrice;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableExecutionReportSummary#setOptionType(org.marketcetera.trade.OptionType)
     */
    @Override
    public void setOptionType(OptionType inOptionType)
    {
        optionType = inOptionType;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableExecutionReportSummary#setAccount(java.lang.String)
     */
    @Override
    public void setAccount(String inAccount)
    {
        account = inAccount;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableExecutionReportSummary#setRootOrderID(org.marketcetera.trade.OrderID)
     */
    @Override
    public void setRootOrderID(OrderID inRootOrderId)
    {
        rootOrderId = inRootOrderId;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableExecutionReportSummary#setSide(org.marketcetera.trade.Side)
     */
    @Override
    public void setSide(Side inSide)
    {
        side = inSide;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableExecutionReportSummary#setCumulativeQuantity(java.math.BigDecimal)
     */
    @Override
    public void setCumulativeQuantity(BigDecimal inCumulativeQuantity)
    {
        cumulativeQuantity = inCumulativeQuantity;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableExecutionReportSummary#setEffectiveCumulativeQuantity(java.math.BigDecimal)
     */
    @Override
    public void setEffectiveCumulativeQuantity(BigDecimal inEffectiveCumulativeQuantity)
    {
        effectiveCumulativeQuantity = inEffectiveCumulativeQuantity;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableExecutionReportSummary#setAveragePrice(java.math.BigDecimal)
     */
    @Override
    public void setAveragePrice(BigDecimal inAveragePrice)
    {
        averagePrice = inAveragePrice;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableExecutionReportSummary#setLastQuantity(java.math.BigDecimal)
     */
    @Override
    public void setLastQuantity(BigDecimal inLastQuantity)
    {
        lastQuantity = inLastQuantity;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableExecutionReportSummary#setLastPrice(java.math.BigDecimal)
     */
    @Override
    public void setLastPrice(BigDecimal inLastPrice)
    {
        lastPrice = inLastPrice;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableExecutionReportSummary#setOrderStatus(org.marketcetera.trade.OrderStatus)
     */
    @Override
    public void setOrderStatus(OrderStatus inOrderStatus)
    {
        orderStatus = inOrderStatus;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableExecutionReportSummary#setExecutionType(org.marketcetera.trade.ExecutionType)
     */
    @Override
    public void setExecutionType(ExecutionType inExecutionType)
    {
        executionType = inExecutionType;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableExecutionReportSummary#setSendingTime(java.util.Date)
     */
    @Override
    public void setSendingTime(Date inSendingTime)
    {
        sendingTime = inSendingTime;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableExecutionReportSummary#setViewer(org.marketcetera.admin.User)
     */
    @Override
    public void setViewer(User inViewer)
    {
        viewer = inViewer;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableExecutionReportSummary#setActor(org.marketcetera.admin.User)
     */
    @Override
    public void setActor(User inActor)
    {
        actor = inActor;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableExecutionReportSummary#setReport(org.marketcetera.trade.Report)
     */
    @Override
    public void setReport(Report inReport)
    {
        report = inReport;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableExecutionReportSummary#setBrokerOrderId(org.marketcetera.trade.OrderID)
     */
    @Override
    public void setBrokerOrderId(OrderID inBrokerOrderId)
    {
        brokerOrderId = inBrokerOrderId;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableExecutionReportSummary#setExecutionId(java.lang.String)
     */
    @Override
    public void setExecutionId(String inExecutionId)
    {
        executionId = inExecutionId;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableExecutionReportSummary#setLeavesQuantity(java.math.BigDecimal)
     */
    @Override
    public void setLeavesQuantity(BigDecimal inLeavesQuantity)
    {
        leavesQuantity = inLeavesQuantity;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getOrderQuantity()
     */
    @Override
    public BigDecimal getOrderQuantity()
    {
        return orderQuantity;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableExecutionReportSummary#setOrderQuantity(java.math.BigDecimal)
     */
    @Override
    public void setOrderQuantity(BigDecimal inOrderQuantity)
    {
        orderQuantity = inOrderQuantity;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getOrderType()
     */
    @Override
    public OrderType getOrderType()
    {
        return orderType;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableExecutionReportSummary#setOrderType(org.marketcetera.trade.OrderType)
     */
    @Override
    public void setOrderType(OrderType inOrderType)
    {
        orderType = inOrderType;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableExecutionReportSummary#setInstrument(org.marketcetera.trade.Instrument)
     */
    @Override
    public void setInstrument(Instrument inInstrument)
    {
        securityType = null;
        expiry = null;
        strikePrice = null;
        optionType = null;
        if(inInstrument == null) {
            symbol = null;
        } else {
            symbol = inInstrument.getSymbol();
            securityType = inInstrument.getSecurityType();
            if(inInstrument instanceof Option) {
                Option option = (Option)inInstrument;
                expiry = option.getExpiry();
                strikePrice = option.getStrikePrice();
                optionType = option.getType();
            }
        }
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("SimpleExecutionReportSummary [orderId=").append(orderId).append(", originalOrderId=")
                .append(originalOrderId).append(", securityType=").append(securityType).append(", symbol=")
                .append(symbol).append(", expiry=").append(expiry).append(", strikePrice=").append(strikePrice)
                .append(", optionType=").append(optionType).append(", account=").append(account)
                .append(", rootOrderId=").append(rootOrderId).append(", side=").append(side)
                .append(", cumulativeQuantity=").append(cumulativeQuantity).append(", effectiveCumulativeQuantity=")
                .append(effectiveCumulativeQuantity).append(", averagePrice=").append(averagePrice)
                .append(", lastQuantity=").append(lastQuantity).append(", lastPrice=").append(lastPrice)
                .append(", orderStatus=").append(orderStatus).append(", executionType=").append(executionType)
                .append(", sendingTime=").append(sendingTime).append(", viewer=").append(viewer).append(", actor=")
                .append(actor).append(", report=").append(report).append(", brokerOrderId=").append(brokerOrderId)
                .append(", executionId=").append(executionId).append(", leavesQuantity=").append(leavesQuantity)
                .append(", orderQuantity=").append(orderQuantity).append(", orderType=").append(orderType).append("]");
        return builder.toString();
    }
    /**
     * order id value
     */
    private OrderID orderId;
    /**
     * original order id value
     */
    private OrderID originalOrderId;
    /**
     * security type value
     */
    private SecurityType securityType;
    /**
     * symbol value
     */
    private String symbol;
    /**
     * expiry value
     */
    private String expiry;
    /**
     * strike price value
     */
    private BigDecimal strikePrice;
    /**
     * option type value
     */
    private OptionType optionType;
    /**
     * account value
     */
    private String account;
    /**
     * root order id value
     */
    private OrderID rootOrderId;
    /**
     * side value
     */
    private Side side;
    /**
     * cumulative quantity value
     */
    private BigDecimal cumulativeQuantity;
    /**
     * effective cumulative quantity value
     */
    private BigDecimal effectiveCumulativeQuantity;
    /**
     * average price value
     */
    private BigDecimal averagePrice;
    /**
     * last quantity value
     */
    private BigDecimal lastQuantity;
    /**
     * last price value
     */
    private BigDecimal lastPrice;
    /**
     * order status value
     */
    private OrderStatus orderStatus;
    /**
     * execution type value
     */
    private ExecutionType executionType;
    /**
     * sending time value
     */
    private Date sendingTime;
    /**
     * viewer value
     */
    private User viewer;
    /**
     * actor value
     */
    private User actor;
    /**
     * report value
     */
    private Report report;
    /**
     * broker order id value
     */
    private OrderID brokerOrderId;
    /**
     * execution id value
     */
    private String executionId;
    /**
     * leaves quantity value
     */
    private BigDecimal leavesQuantity;
    /**
     * order quantity value
     */
    private BigDecimal orderQuantity;
    /**
     * order type value
     */
    private OrderType orderType;
}
