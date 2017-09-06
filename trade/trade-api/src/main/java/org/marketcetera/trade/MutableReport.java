package org.marketcetera.trade;

import java.util.Date;

import org.marketcetera.admin.User;

import quickfix.SessionID;

/* $License$ */

/**
 * Provides a mutable {@link Report} implementation
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MutableReport
        extends Report
{
    /**
     * Gets the actor id of the report.
     *
     * @return a <code>UserID</code> value or <code>null</code>
     */
    void setActorID(UserID inUserId);
    /**
     * Gets the viewer id of the report.
     *
     * @return a <code>UserID</code> value or <code>null</code>
     */
    void setViewerID(UserID inUserId);
    /**
     * Get the orderID value.
     *
     * @return an <code>OrderID</code> value
     */
    void setOrderID(OrderID inOrderId);
    /**
     * Get the actor value.
     *
     * @return a <code>User</code> value
     */
    void setActor(User inUser);
    /**
     * Get the viewer value.
     *
     * @return a <code>User</code> value
     */
    void setViewer(User inUser);
    /**
     * Get the fixMessage value.
     *
     * @return a <code>String</code> value
     */
    void setFixMessage(String inFixMessage);
    /**
     * Get the sessionId value.
     *
     * @return a <code>SessionID</code> value
     */
    void setSessionId(SessionID inSessionId);
    /**
     * Get the msgSeqNum value.
     *
     * @return an <code>int</code> value
     */
    void setMsgSeqNum(int inMsgSeqNum);
    /**
     * Get the sendingTime value.
     *
     * @return a <code>Date</code> value
     */
    void setSendingTime(Date inSendingTime);
    /**
     * Get the reportType value.
     *
     * @return a <code>ReportType</code> value
     */
    void setReportType(ReportType inReportType);
    /**
     * Get the brokerID value.
     *
     * @return a <code>BrokerID</code> value
     */
    void setBrokerID(BrokerID inBrokerId);
    /**
     * Get the reportID value.
     *
     * @return a <code>ReportID</code> value
     */
    void setReportID(ReportID inReportId);
    /**
     * Get the originator value.
     *
     * @return an <code>Originator</code> value
     */
    void setOriginator(Originator inOriginator);
    /**
     * Get the hierarchy value.
     *
     * @return a <code>Hierarchy</code> value
     */
    void setHierarchy(Hierarchy inHierarchy);
}
