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
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Report#getViewerID()
     */
    @Override
    public UserID getViewerID()
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Report#getOrderID()
     */
    @Override
    public OrderID getOrderID()
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Report#getActor()
     */
    @Override
    public User getActor()
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Report#getViewer()
     */
    @Override
    public User getViewer()
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Report#getFixMessage()
     */
    @Override
    public String getFixMessage()
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Report#getSessionId()
     */
    @Override
    public SessionID getSessionId()
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Report#getMsgSeqNum()
     */
    @Override
    public int getMsgSeqNum()
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Report#getSendingTime()
     */
    @Override
    public Date getSendingTime()
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Report#getReportType()
     */
    @Override
    public ReportType getReportType()
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Report#getBrokerID()
     */
    @Override
    public BrokerID getBrokerID()
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Report#getReportID()
     */
    @Override
    public ReportID getReportID()
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Report#getOriginator()
     */
    @Override
    public Originator getOriginator()
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Report#getHierarchy()
     */
    @Override
    public Hierarchy getHierarchy()
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableReport#setActorID(org.marketcetera.trade.UserID)
     */
    @Override
    public void setActorID(UserID inUserId)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableReport#setViewerID(org.marketcetera.trade.UserID)
     */
    @Override
    public void setViewerID(UserID inUserId)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableReport#setOrderID(org.marketcetera.trade.OrderID)
     */
    @Override
    public void setOrderID(OrderID inOrderId)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableReport#setActor(org.marketcetera.admin.User)
     */
    @Override
    public void setActor(User inUser)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableReport#setViewer(org.marketcetera.admin.User)
     */
    @Override
    public void setViewer(User inUser)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableReport#setFixMessage(java.lang.String)
     */
    @Override
    public void setFixMessage(String inFixMessage)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableReport#setSessionId(quickfix.SessionID)
     */
    @Override
    public void setSessionId(SessionID inSessionId)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableReport#setMsgSeqNum(int)
     */
    @Override
    public void setMsgSeqNum(int inMsgSeqNum)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableReport#setSendingTime(java.util.Date)
     */
    @Override
    public void setSendingTime(Date inSendingTime)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableReport#setReportType(org.marketcetera.trade.ReportType)
     */
    @Override
    public void setReportType(ReportType inReportType)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableReport#setBrokerID(org.marketcetera.trade.BrokerID)
     */
    @Override
    public void setBrokerID(BrokerID inBrokerId)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableReport#setReportID(org.marketcetera.trade.ReportID)
     */
    @Override
    public void setReportID(ReportID inReportId)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableReport#setOriginator(org.marketcetera.trade.Originator)
     */
    @Override
    public void setOriginator(Originator inOriginator)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.MutableReport#setHierarchy(org.marketcetera.trade.Hierarchy)
     */
    @Override
    public void setHierarchy(Hierarchy inHierarchy)
    {
        throw new UnsupportedOperationException(); // TODO
        
    }
}
