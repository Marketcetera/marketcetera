package org.marketcetera.trade;

import java.util.Date;

import org.marketcetera.admin.User;

import quickfix.SessionID;

/* $License$ */

/**
 * Provides a simple {@link MutableReport} implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SimpleReport
        implements MutableReport
{
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Report#getActorID()
     */
    @Override
    public UserID getActorID()
    {
        return user==null?null:user.getUserID();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Report#getViewerID()
     */
    @Override
    public UserID getViewerID()
    {
        return user==null?null:user.getUserID();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Report#getOrderID()
     */
    @Override
    public OrderID getOrderID()
    {
        return orderId;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Report#getActor()
     */
    @Override
    public User getActor()
    {
        return user;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Report#getViewer()
     */
    @Override
    public User getViewer()
    {
        return user;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Report#getFixMessage()
     */
    @Override
    public String getFixMessage()
    {
        return fixMessage;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Report#getSessionId()
     */
    @Override
    public SessionID getSessionId()
    {
        return sessionId;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Report#getMsgSeqNum()
     */
    @Override
    public int getMsgSeqNum()
    {
        return msgSeqNum;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Report#getSendingTime()
     */
    @Override
    public Date getSendingTime()
    {
        return sendingTime;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Report#getReportType()
     */
    @Override
    public ReportType getReportType()
    {
        return reportType;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Report#getBrokerID()
     */
    @Override
    public BrokerID getBrokerID()
    {
        return brokerId;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Report#getReportID()
     */
    @Override
    public ReportID getReportID()
    {
        return reportId;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Report#getOriginator()
     */
    @Override
    public Originator getOriginator()
    {
        return originator;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Report#getHierarchy()
     */
    @Override
    public Hierarchy getHierarchy()
    {
        return hierarchy;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableReport#setOrderID(org.marketcetera.trade.OrderID)
     */
    @Override
    public void setOrderID(OrderID inOrderId)
    {
        orderId = inOrderId;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableReport#setActor(org.marketcetera.admin.User)
     */
    @Override
    public void setActor(User inUser)
    {
        user = inUser;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableReport#setViewer(org.marketcetera.admin.User)
     */
    @Override
    public void setViewer(User inUser)
    {
        user = inUser;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableReport#setFixMessage(java.lang.String)
     */
    @Override
    public void setFixMessage(String inFixMessage)
    {
        fixMessage = inFixMessage;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableReport#setSessionId(quickfix.SessionID)
     */
    @Override
    public void setSessionId(SessionID inSessionId)
    {
        sessionId = inSessionId;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableReport#setMsgSeqNum(int)
     */
    @Override
    public void setMsgSeqNum(int inMsgSeqNum)
    {
        msgSeqNum = inMsgSeqNum;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableReport#setSendingTime(java.util.Date)
     */
    @Override
    public void setSendingTime(Date inSendingTime)
    {
        sendingTime = inSendingTime;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableReport#setReportType(org.marketcetera.trade.ReportType)
     */
    @Override
    public void setReportType(ReportType inReportType)
    {
        reportType = inReportType;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableReport#setBrokerID(org.marketcetera.trade.BrokerID)
     */
    @Override
    public void setBrokerID(BrokerID inBrokerId)
    {
        brokerId = inBrokerId;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableReport#setReportID(org.marketcetera.trade.ReportID)
     */
    @Override
    public void setReportID(ReportID inReportId)
    {
        reportId = inReportId;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableReport#setOriginator(org.marketcetera.trade.Originator)
     */
    @Override
    public void setOriginator(Originator inOriginator)
    {
        originator = inOriginator;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableReport#setHierarchy(org.marketcetera.trade.Hierarchy)
     */
    @Override
    public void setHierarchy(Hierarchy inHierarchy)
    {
        hierarchy = inHierarchy;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("SimpleReport [orderId=").append(orderId).append(", sessionId=").append(sessionId)
                .append(", msgSeqNum=").append(msgSeqNum).append(", brokerId=").append(brokerId).append(", user=")
                .append(user).append(", sendingTime=").append(sendingTime).append(", reportType=").append(reportType)
                .append(", reportId=").append(reportId).append(", originator=").append(originator)
                .append(", hierarchy=").append(hierarchy).append(", fixMessage=").append(fixMessage).append("]");
        return builder.toString();
    }
    /**
     * order id value
     */
    private OrderID orderId;
    /**
     * user value
     */
    private User user;
    /**
     * FIX message value
     */
    private String fixMessage;
    /**
     * session id value
     */
    private SessionID sessionId;
    /**
     * msg seq num value
     */
    private int msgSeqNum;
    /**
     * sending time value
     */
    private Date sendingTime;
    /**
     * report type value
     */
    private ReportType reportType;
    /**
     * broker id value
     */
    private BrokerID brokerId;
    /**
     * report id value
     */
    private ReportID reportId;
    /**
     * originator value
     */
    private Originator originator;
    /**
     * hierarchy value
     */
    private Hierarchy hierarchy;
}
