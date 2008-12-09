package org.marketcetera.messagehistory;

import java.util.concurrent.atomic.AtomicLong;

import org.marketcetera.event.HasFIXMessage;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.ReportBase;
import org.marketcetera.util.misc.ClassVersion;

import quickfix.Message;

/* $License$ */

/**
 * Represents a Photon trading report.
 *
 * @author anshul@marketcetera.com
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class ReportHolder
    implements Comparable<ReportHolder> {

    /**
     * Creates an instance.
     *
     * @param inReport the report instance.
     */
    public ReportHolder(ReportBase inReport) {
        this(inReport, null);
    }

    /**
     * Creates an instance.
     *
     * @param inReport the report instance.
     * @param inGroupID the orderID of the first order in this chain of orders.
     */
    public ReportHolder(ReportBase inReport, OrderID inGroupID){
        this.mReport = inReport;
        this.mMessageReference = sCounter.incrementAndGet();
        this.mGroupID = inGroupID;
    }

    /**
     * Returns the report instance.
     *
     * @return the report instance.
     */
    public ReportBase getReport() {
        return mReport;
    }

    /**
     * The FIX message underlying the report, if the report has a FIX message,
     * null otherwise.
     *
     * @return the FIX message underlying the report.
     */
    public Message getMessage() {
        if(mReport instanceof HasFIXMessage) {
            return ((HasFIXMessage)mReport).getMessage();
        }
        return null;
    }
    
    /**
     * Gets the ID of the broker from which this report was received.
     *
     * @return the broker ID from which this report was received.
     */
    public BrokerID getBrokerID() {
    	return mReport.getBrokerID();
    }

    /**
     * The message reference value. This is a unique, monotonically
     * increasing value in the order in which instances of this class
     * are created.
     *
     * @return the message reference value.
     */
    public long getMessageReference()
    {
        return mMessageReference;
    }

    @Override
    public int compareTo(ReportHolder mh) {
        return (int)(mMessageReference - mh.mMessageReference);
    }

    /**
     * The orderID of the first order in this chain of orders.
     *
     * @return the orderID of the first order in this chain of orders.
     */
    public OrderID getGroupID() {
        return mGroupID;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (mMessageReference ^ (mMessageReference >>> 32));
        return result;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ReportHolder other = (ReportHolder) obj;
        return mMessageReference == other.mMessageReference;
    }
    private final ReportBase mReport;
    private final long mMessageReference;
    private static AtomicLong sCounter = new AtomicLong();
    private final OrderID mGroupID;
}