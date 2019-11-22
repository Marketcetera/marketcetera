package org.marketcetera.web.trade.report.model;

import java.util.Date;

import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.Hierarchy;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.Originator;
import org.marketcetera.trade.Report;
import org.marketcetera.trade.ReportID;
import org.marketcetera.trade.ReportType;

/* $License$ */

/**
 * Provides a display version of a {@link Report} with sortable attributes.
 * 
 * <p>This class is necessary because foreign type {@link quickfix.SessionID} is
 * not {@link Comparable}, which disables sorting in the UI.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class DisplayReport
{
    /**
     * Get the order id value.
     *
     * @return an <code>OrderID</code> value
     */
    public OrderID getOrderID()
    {
        return report.getOrderID();
    }
    /**
     * Get the trader name value.
     *
     * @return a <code>String</code> value
     */
    public String getTrader()
    {
        return report.getActor().getName();
    }
    /**
     * Get the FIX message value.
     *
     * @return a <code>String</code> value
     */
    public String getFixMessage()
    {
        return report.getFixMessage();
    }
    /**
     * Get the session ID value.
     *
     * @return a <code>String</code> value
     */
    public String getSessionID()
    {
        return report.getSessionId().toString();
    }
    /**
     * Get the message sequence number value.
     *
     * @return an <code>int</code> value
     */
    public int getMsgSeqNum()
    {
        return report.getMsgSeqNum();
    }
    /**
     * Get the sending time value.
     *
     * @return a <code>Date</code> value
     */
    public Date getSendingTime()
    {
        return report.getSendingTime();
    }
    /**
     * Get the transact time value.
     *
     * @return a <code>Date</code> value
     */
    public Date getTransactTime()
    {
        return report.getTransactTime();
    }
    /**
     * Get the message type value.
     *
     * @return a <code>ReportType</code> value
     */
    public ReportType getMsgType()
    {
        return report.getReportType();
    }
    /**
     * Get the broker ID value.
     *
     * @return a <code>BrokerID</code> value
     */
    public BrokerID getBrokerID()
    {
        return report.getBrokerID();
    }
    /**
     * Get the report ID value.
     *
     * @return a <code>ReportID</code> value
     */
    public ReportID getReportID()
    {
        return report.getReportID();
    }
    /**
     * Get the originator value.
     *
     * @return an <code>Originator</code> value
     */
    public Originator getOriginator()
    {
        return report.getOriginator();
    }
    /**
     * Get the hierarchy value.
     *
     * @return a <code>Hierarchy</code> value
     */
    public Hierarchy getHierarchy()
    {
        return report.getHierarchy();
    }
    /**
     * Get the text value.
     *
     * @return a <code>String</code> value
     */
    public String getText()
    {
        return report.getText();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("DisplayReport [report=").append(report).append("]");
        return builder.toString();
    }
    /**
     * Create a new DisplayReport instance.
     *
     * @param inReport a <code>Report</code> value
     */
    public DisplayReport(Report inReport)
    {
        report = inReport;
    }
    /**
     * report value
     */
    private final Report report;
}
