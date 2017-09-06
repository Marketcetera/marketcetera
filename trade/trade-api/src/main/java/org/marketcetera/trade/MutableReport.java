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
     * Get the orderID value.
     *
     * @param inOrderId an <code>OrderID</code> value
     */
    void setOrderID(OrderID inOrderId);
    /**
     * Get the actor value.
     *
     * @param inUser a <code>User</code> value
     */
    void setActor(User inUser);
    /**
     * Get the viewer value.
     *
     * @param inUser a <code>User</code> value
     */
    void setViewer(User inUser);
    /**
     * Get the fixMessage value.
     *
     * @param inFixMessage a <code>String</code> value
     */
    void setFixMessage(String inFixMessage);
    /**
     * Get the sessionId value.
     *
     * @param inSessionId a <code>SessionID</code> value
     */
    void setSessionId(SessionID inSessionId);
    /**
     * Get the msgSeqNum value.
     *
     * @param inMsgSeqNum an <code>int</code> value
     */
    void setMsgSeqNum(int inMsgSeqNum);
    /**
     * Get the sendingTime value.
     *
     * @param inSendingTime a <code>Date</code> value
     */
    void setSendingTime(Date inSendingTime);
    /**
     * Get the reportType value.
     *
     * @param inReportType a <code>ReportType</code> value
     */
    void setReportType(ReportType inReportType);
    /**
     * Get the brokerID value.
     *
     * @param inBrokerId a <code>BrokerID</code> value
     */
    void setBrokerID(BrokerID inBrokerId);
    /**
     * Get the reportID value.
     *
     * @param inReportId a <code>ReportID</code> value
     */
    void setReportID(ReportID inReportId);
    /**
     * Get the originator value.
     *
     * @param inOriginator an <code>Originator</code> value
     */
    void setOriginator(Originator inOriginator);
    /**
     * Get the hierarchy value.
     *
     * @param inHierarchy a <code>Hierarchy</code> value
     */
    void setHierarchy(Hierarchy inHierarchy);
}
