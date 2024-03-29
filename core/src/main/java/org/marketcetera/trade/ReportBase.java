package org.marketcetera.trade;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.marketcetera.util.misc.ClassVersion;


/* $License$ */
/**
 * Declares common fields between various reports. This type is not
 * meant to be used directly, use one of its sub-types instead.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public interface ReportBase
        extends Serializable,HasBrokerID,HasReportID
{
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
     * The originator of this message.
     *
     * @return the originator of this message.
     */
    Originator getOriginator();
    /**
     * The hierarchy of the report.
     *
     * @return a <code>Hierarchy</code> value
     */
    Hierarchy getHierarchy();
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
    /**
     * Compares two <code>ReportBase</code> objects.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 2.4.0
     */
    public static enum ReportComparator
            implements Comparator<ReportBase>
    {
        INSTANCE;
        /* (non-Javadoc)
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
        public int compare(ReportBase inLHS,
                           ReportBase inRHS)
        {
            return new CompareToBuilder().append(inLHS.getSendingTime(),inRHS.getSendingTime()).append(inLHS.getReportID(),inRHS.getReportID()).toComparison();
        }
    }
}
