package org.marketcetera.web.trade.report.model;

import java.util.Date;

import org.marketcetera.trade.Report;

/* $License$ */

/**
 * Provides a displayble version of {@link Report} objects with natively sortable attributes.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class DisplayReport
{
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("DisplayReport [sessionId=").append(sessionId).append(", brokerId=").append(brokerId)
                .append(", msgSeqNum=").append(msgSeqNum).append(", sendingTime=").append(sendingTime)
                .append(", orderId=").append(orderId).append(", reportId=").append(reportId).append(", reportType=")
                .append(reportType).append(", originator=").append(originator).append(", hierarchy=").append(hierarchy)
                .append(", user=").append(user).append(", fixMessage=").append(fixMessage).append("]");
        return builder.toString();
    }
    /**
     * Get the sessionId value.
     *
     * @return a <code>String</code> value
     */
    public String getSessionId()
    {
        return sessionId;
    }
    /**
     * Get the msgSeqNum value.
     *
     * @return an <code>int</code> value
     */
    public int getMsgSeqNum()
    {
        return msgSeqNum;
    }
    /**
     * Get the sendingTime value.
     *
     * @return a <code>Date</code> value
     */
    public Date getSendingTime()
    {
        return sendingTime;
    }
    /**
     * Get the orderId value.
     *
     * @return a <code>String</code> value
     */
    public String getOrderId()
    {
        return orderId;
    }
    /**
     * Get the reportId value.
     *
     * @return a <code>long</code> value
     */
    public long getReportId()
    {
        return reportId;
    }
    /**
     * Get the reportType value.
     *
     * @return a <code>String</code> value
     */
    public String getReportType()
    {
        return reportType;
    }
    /**
     * Get the originator value.
     *
     * @return a <code>String</code> value
     */
    public String getOriginator()
    {
        return originator;
    }
    /**
     * Get the hierarchy value.
     *
     * @return a <code>String</code> value
     */
    public String getHierarchy()
    {
        return hierarchy;
    }
    /**
     * Get the user value.
     *
     * @return a <code>String</code> value
     */
    public String getUser()
    {
        return user;
    }
    /**
     * Get the fixMessage value.
     *
     * @return a <code>String</code> value
     */
    public String getFixMessage()
    {
        return fixMessage;
    }
    /**
     * Get the brokerId value.
     *
     * @return a <code>String</code> value
     */
    public String getBrokerId()
    {
        return brokerId;
    }
    /**
     * Get the report value.
     *
     * @return a <code>Report</code> value
     */
    public Report getReport()
    {
        return report;
    }
    /**
     * Create a new DisplayReport instance.
     *
     * @param inReport a <code>Report</code> value
     */
    public DisplayReport(Report inReport)
    {
        report = inReport;
        sessionId = inReport.getSessionId().toString();
        brokerId = inReport.getBrokerID().getValue();
        msgSeqNum = inReport.getMsgSeqNum();
        sendingTime = inReport.getSendingTime();
        orderId = inReport.getOrderID().getValue();
        reportId = inReport.getReportID().longValue();
        reportType = inReport.getReportType().name();
        originator = inReport.getOriginator().name();
        hierarchy = inReport.getHierarchy().name();
        user = inReport.getActor().getName();
        fixMessage = inReport.getFixMessage();
    }
    /**
     * session id value
     */
    private final String sessionId;
    /**
     * broker id value
     */
    private final String brokerId;
    /**
     * msg seq num value
     */
    private final int msgSeqNum;
    /**
     * sending time value
     */
    private final Date sendingTime;
    /**
     * order id value
     */
    private final String orderId;
    /**
     * report id value
     */
    private final long reportId;
    /**
     * report type value
     */
    private final String reportType;
    /**
     * originator value
     */
    private final String originator;
    /**
     * hierarchy value
     */
    private final String hierarchy;
    /**
     * user value
     */
    private final String user;
    /**
     * fix message value
     */
    private final String fixMessage;
    /**
     * report value
     */
    private final Report report;
}
