package org.marketcetera.systemmodel.impl;

import java.util.Date;

import org.marketcetera.ors.history.ReportType;
import org.marketcetera.systemmodel.OrderDestinationID;
import org.marketcetera.systemmodel.Report;
import org.marketcetera.systemmodel.ReportSummary;
import org.marketcetera.systemmodel.User;
import org.marketcetera.trade.OrderID;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * {@link Report} implementation. 
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
class ReportImpl
        implements Report
{
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.Report#getDestinationID()
     */
    @Override
    public OrderDestinationID getDestinationID()
    {
        return orderDestinationId;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.Report#getRawMessage()
     */
    @Override
    public String getRawMessage()
    {
        return rawMessage;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.Report#getReportType()
     */
    @Override
    public ReportType getReportType()
    {
        return reportType;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.Report#getSendingTime()
     */
    @Override
    public Date getSendingTime()
    {
        return sendingTime;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.Report#getOrderID()
     */
    @Override
    public OrderID getOrderID()
    {
        return orderId;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.Report#getOwner()
     */
    @Override
    public User getOwner()
    {
        return owner;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.Report#getSummary()
     */
    @Override
    public ReportSummary getSummary()
    {
        return reportSummary;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return String.format("Report [orderId=%s, orderDestinationId=%s, sendingTime=%s, reportType=%s, owner=%s]",
                             orderId,
                             orderDestinationId,
                             sendingTime,
                             reportType,
                             owner);
    }
    /**
     * Create a new ReportImpl instance.
     *
     * @param inOrderId an <code>OrderID</code> value
     * @param inOwner a <code>User</code> value
     * @param inOrderDestinationId an <code>OrderDestinationID</code> value
     * @param inRawMessage a <code>String</code> value containing a representation of the underlying execution report or <code>null</code>
     * @param inSendingTime a <code>Date</code> value
     * @param inReportType a <code>ReportType</code> value
     * @param inReportSummary a <code>ReportSummary</code. value
     */
    ReportImpl(OrderID inOrderId,
               User inOwner,
               OrderDestinationID inOrderDestinationId,
               String inRawMessage,
               Date inSendingTime,
               ReportType inReportType,
               ReportSummary inReportSummary)
    {
        orderId = inOrderId;
        owner = inOwner;
        orderDestinationId = inOrderDestinationId;
        rawMessage = inRawMessage;
        sendingTime = inSendingTime;
        reportType = inReportType;
        reportSummary = inReportSummary;
    }
    /**
     * Create a new ReportImpl instance.
     */
    @SuppressWarnings("unused")
    private ReportImpl()
    {
        orderId = null;
        owner = null;
        orderDestinationId = null;
        rawMessage = null;
        sendingTime = null;
        reportType = null;
        reportSummary = null;
    }
    /**
     * orderID for the execution report 
     */
    private final OrderID orderId;
    /**
     * owner of the execution report
     */
    private final User owner;
    /**
     * order destination ID from which the execution report came
     */
    private final OrderDestinationID orderDestinationId;
    /**
     * the raw message or <code>null</code>
     */
    private final String rawMessage;
    /**
     * the sending time of the execution report
     */
    private final Date sendingTime;
    /**
     * the type of the execution report
     */
    private final ReportType reportType;
    /**
     * the execution report summary linked to this execution report
     */
    private final ReportSummary reportSummary;
}
