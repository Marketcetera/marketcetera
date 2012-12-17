package org.marketcetera.trade;

import org.marketcetera.util.misc.ClassVersion;

import java.io.Serializable;
import java.util.Date;

/* $License$ */
/**
 * Declares common fields between various reports. This type is not
 * meant to be used directly, use one of its sub-types instead.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public interface ReportBase extends Serializable {
    /**
     * The client assigned orderID of the order that generated this report.
     *
     * @return the orderID of the order.
     */
    OrderID getOrderID();

    /**
     * If the order that generated this report was sent to cancel or
     * replace an existing order, the orderID of that original client assigned
     * orderID is returned. Otherwise, the returned value is null.
     *
     * @return the orderID of the original order.
     */
    OrderID getOriginalOrderID();

    /**
     * The Order status.
     *
     * @return The order status.
     */
    OrderStatus getOrderStatus();

    /**
     * Text associated with the execution report.
     *
     * @return text associated with the execution report.
     */
    String getText();

    /**
     * Gets the ID of the broker from which this report was received.
     *
     * @return the broker ID from which this report was received.
     */
    BrokerID getBrokerID();

    /**
     * Time of message transmission in UTC.
     *
     * @return the time of message transmission in UTC.
     */
    Date getSendingTime();
    
    /**
     * The order ID assigned by broker to the original order.
     *
     * @return the broker assigned order ID.
     */
    String getBrokerOrderID();

    /**
     * The unique ID for this report.
     *
     * @return the unique ID for this report
     */
    ReportID getReportID();

    /**
     * The originator of this message.
     *
     * @return the originator of this message.
     */
    Originator getOriginator();

    /**
     * The ID of the actor user of this message.
     *
     * @return the ID of the actor user of this message.
     */
    UserID getActorID();

    /**
     * The ID of the viewer user of this message.
     *
     * @return the ID of the viewer user of this message.
     */
    UserID getViewerID();
}
