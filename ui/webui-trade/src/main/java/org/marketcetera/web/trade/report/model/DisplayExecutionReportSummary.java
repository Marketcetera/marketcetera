package org.marketcetera.web.trade.report.model;

import java.math.BigDecimal;
import java.util.Date;

import org.marketcetera.admin.User;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.ExecutionReportSummary;
import org.marketcetera.trade.ExecutionType;
import org.marketcetera.trade.Hierarchy;
import org.marketcetera.trade.OptionType;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.OrderStatus;
import org.marketcetera.trade.OrderType;
import org.marketcetera.trade.Originator;
import org.marketcetera.trade.Report;
import org.marketcetera.trade.ReportID;
import org.marketcetera.trade.ReportType;
import org.marketcetera.trade.SecurityType;
import org.marketcetera.trade.Side;
import org.marketcetera.trade.UserID;

/* $License$ */

/**
 * Provides a displayble, flattened {@link ExecutionReportSummary} implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class DisplayExecutionReportSummary
        implements ExecutionReportSummary,Report
{
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Report#getActorID()
     */
    @Override
    public UserID getActorID()
    {
        return executionReportSummary.getReport().getActorID();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Report#getFixMessage()
     */
    @Override
    public String getFixMessage()
    {
        return executionReportSummary.getReport().getFixMessage();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Report#getSessionID()
     */
    @Override
    public quickfix.SessionID getSessionId()
    {
        return executionReportSummary.getReport().getSessionId();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Report#getMsgSeqNum()
     */
    @Override
    public int getMsgSeqNum()
    {
        return executionReportSummary.getReport().getMsgSeqNum();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Report#getTransactTime()
     */
    @Override
    public Date getTransactTime()
    {
        return executionReportSummary.getReport().getTransactTime();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Report#getReportType()
     */
    @Override
    public ReportType getReportType()
    {
        return executionReportSummary.getReport().getReportType();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Report#getBrokerID()
     */
    @Override
    public BrokerID getBrokerID()
    {
        return executionReportSummary.getReport().getBrokerID();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Report#getReportID()
     */
    @Override
    public ReportID getReportID()
    {
        return executionReportSummary.getReport().getReportID();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Report#getOriginator()
     */
    @Override
    public Originator getOriginator()
    {
        return executionReportSummary.getReport().getOriginator();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Report#getHierarchy()
     */
    @Override
    public Hierarchy getHierarchy()
    {
        return executionReportSummary.getReport().getHierarchy();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Report#getText()
     */
    @Override
    public String getText()
    {
        return executionReportSummary.getReport().getText();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getOrderID()
     */
    @Override
    public OrderID getOrderID()
    {
        return executionReportSummary.getOrderID();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getOriginalOrderID()
     */
    @Override
    public OrderID getOriginalOrderID()
    {
        return executionReportSummary.getOriginalOrderID();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getSecurityType()
     */
    @Override
    public SecurityType getSecurityType()
    {
        return executionReportSummary.getSecurityType();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getSymbol()
     */
    @Override
    public String getSymbol()
    {
        return executionReportSummary.getSymbol();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getExpiry()
     */
    @Override
    public String getExpiry()
    {
        return executionReportSummary.getExpiry();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getStrikePrice()
     */
    @Override
    public BigDecimal getStrikePrice()
    {
        return executionReportSummary.getStrikePrice();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getOptionType()
     */
    @Override
    public OptionType getOptionType()
    {
        return executionReportSummary.getOptionType();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getAccount()
     */
    @Override
    public String getAccount()
    {
        return executionReportSummary.getAccount();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getRootOrderID()
     */
    @Override
    public OrderID getRootOrderID()
    {
        return executionReportSummary.getRootOrderID();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getSide()
     */
    @Override
    public Side getSide()
    {
        return executionReportSummary.getSide();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getCumulativeQuantity()
     */
    @Override
    public BigDecimal getCumulativeQuantity()
    {
        return executionReportSummary.getCumulativeQuantity();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getEffectiveCumulativeQuantity()
     */
    @Override
    public BigDecimal getEffectiveCumulativeQuantity()
    {
        return executionReportSummary.getEffectiveCumulativeQuantity();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getAveragePrice()
     */
    @Override
    public BigDecimal getAveragePrice()
    {
        return executionReportSummary.getAveragePrice();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getLastQuantity()
     */
    @Override
    public BigDecimal getLastQuantity()
    {
        return executionReportSummary.getLastQuantity();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getLastPrice()
     */
    @Override
    public BigDecimal getLastPrice()
    {
        return executionReportSummary.getLastPrice();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getOrderStatus()
     */
    @Override
    public OrderStatus getOrderStatus()
    {
        return executionReportSummary.getOrderStatus();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getExecutionType()
     */
    @Override
    public ExecutionType getExecutionType()
    {
        return executionReportSummary.getExecutionType();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getSendingTime()
     */
    @Override
    public Date getSendingTime()
    {
        return executionReportSummary.getSendingTime();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getViewer()
     */
    @Override
    public User getViewer()
    {
        return executionReportSummary.getViewer();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getActor()
     */
    @Override
    public User getActor()
    {
        return executionReportSummary.getActor();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getReport()
     */
    @Override
    public Report getReport()
    {
        return executionReportSummary.getReport();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getViewerID()
     */
    @Override
    public UserID getViewerID()
    {
        return executionReportSummary.getViewerID();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getBrokerOrderId()
     */
    @Override
    public OrderID getBrokerOrderId()
    {
        return executionReportSummary.getBrokerOrderId();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getExecutionId()
     */
    @Override
    public String getExecutionId()
    {
        return executionReportSummary.getExecutionId();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getLeavesQuantity()
     */
    @Override
    public BigDecimal getLeavesQuantity()
    {
        return executionReportSummary.getLeavesQuantity();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getOrderQuantity()
     */
    @Override
    public BigDecimal getOrderQuantity()
    {
        return executionReportSummary.getOrderQuantity();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.ExecutionReportSummary#getOrderType()
     */
    @Override
    public OrderType getOrderType()
    {
        return executionReportSummary.getOrderType();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("DisplayExecutionReportSummary [executionReportSummary=").append(executionReportSummary)
                .append("]");
        return builder.toString();
    }
    /**
     * Create a new DisplayExecutionReportSummary instance.
     *
     * @param inExecutionReportSummary an <code>ExecutionReportSummary</code> value
     */
    public DisplayExecutionReportSummary(ExecutionReportSummary inExecutionReportSummary)
    {
        executionReportSummary = inExecutionReportSummary;
    }
    /**
     * execution report summary value
     */
    private final ExecutionReportSummary executionReportSummary;
}
