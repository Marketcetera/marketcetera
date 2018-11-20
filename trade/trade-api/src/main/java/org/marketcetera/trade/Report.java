package org.marketcetera.trade;

import java.util.Date;

import org.marketcetera.admin.User;

import quickfix.SessionID;

/* $License$ */

/**
 * Represents a report object received as part of a trade flow.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface Report
{
    /**
     * Gets the actor id of the report.
     *
     * @return a <code>UserID</code> value or <code>null</code>
     */
    UserID getActorID();
    /**
     * Gets the viewer id of the report.
     *
     * @return a <code>UserID</code> value or <code>null</code>
     */
    UserID getViewerID();
    /**
     * Get the orderID value.
     *
     * @return an <code>OrderID</code> value
     */
    OrderID getOrderID();
    /**
     * Get the actor value.
     *
     * @return a <code>User</code> value
     */
    User getActor();
    /**
     * Get the viewer value.
     *
     * @return a <code>User</code> value
     */
    User getViewer();
    /**
     * Get the fixMessage value.
     *
     * @return a <code>String</code> value
     */
    String getFixMessage();
    /**
     * Get the sessionId value.
     *
     * @return a <code>SessionID</code> value
     */
    SessionID getSessionId();
    /**
     * Get the msgSeqNum value.
     *
     * @return an <code>int</code> value
     */
    int getMsgSeqNum();
    /**
     * Get the sendingTime value.
     *
     * @return a <code>Date</code> value
     */
    Date getSendingTime();
    /**
     * Get the reportType value.
     *
     * @return a <code>ReportType</code> value
     */
    ReportType getReportType();
    /**
     * Get the brokerID value.
     *
     * @return a <code>BrokerID</code> value
     */
    BrokerID getBrokerID();
    /**
     * Get the reportID value.
     *
     * @return a <code>ReportID</code> value
     */
    ReportID getReportID();
    /**
     * Get the originator value.
     *
     * @return an <code>Originator</code> value
     */
    Originator getOriginator();
    /**
     * Get the hierarchy value.
     *
     * @return a <code>Hierarchy</code> value
     */
    Hierarchy getHierarchy();
}
