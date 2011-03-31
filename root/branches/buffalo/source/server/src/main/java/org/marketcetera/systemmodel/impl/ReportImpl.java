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
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class ReportImpl
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
    /**
     * Create a new ReportImpl instance.
     *
     * @param inOrderId
     * @param inOwner
     * @param inOrderDestinationId
     * @param inRawMessage
     * @param inSendingTime
     * @param inReportType
     * @param inReportSummary
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
     * 
     */
    private final OrderID orderId;
    /**
     * 
     */
    private final User owner;
    /**
     * 
     */
    private final OrderDestinationID orderDestinationId;
    /**
     * 
     */
    private final String rawMessage;
    /**
     * 
     */
    private final Date sendingTime;
    /**
     * 
     */
    private final ReportType reportType;
    /**
     * 
     */
    private final ReportSummary reportSummary;
}
