package org.marketcetera.server.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.marketcetera.client.ReportListener;
import org.marketcetera.server.service.DestinationStatus;
import org.marketcetera.server.service.OrderDestination;
import org.marketcetera.systemmodel.OrderDestinationID;
import org.marketcetera.systemmodel.OrderDestinationIdFactory;
import org.marketcetera.trade.Order;
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
class OrderDestinationImpl
        implements OrderDestination
{
    /* (non-Javadoc)
     * @see org.marketcetera.server.service.OrderDestination#getStatus()
     */
    @Override
    public DestinationStatus getStatus()
    {
        return status;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.server.service.OrderDestination#getName()
     */
    @Override
    public String getName()
    {
        return name;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.server.service.OrderDestination#getId()
     */
    @Override
    public OrderDestinationID getId()
    {
        return id;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.server.service.OrderDestination#send(org.marketcetera.trade.Order)
     */
    @Override
    public void send(Order inOrder)
    {
        throw new UnsupportedOperationException(); // TODO Auto-generated method stub
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#start()
     */
    @Override
    public void start()
    {
        throw new UnsupportedOperationException(); // TODO Auto-generated method stub
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#stop()
     */
    @Override
    public void stop()
    {
        throw new UnsupportedOperationException(); // TODO Auto-generated method stub
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#isRunning()
     */
    @Override
    public boolean isRunning()
    {
        throw new UnsupportedOperationException(); // TODO Auto-generated method stub
    }
    /* (non-Javadoc)
     * @see org.marketcetera.server.service.OrderDestination#subscribe(org.marketcetera.client.ReportListener)
     */
    @Override
    public void subscribe(ReportListener inReportListener)
    {
        throw new UnsupportedOperationException(); // TODO Auto-generated method stub
    }
    /* (non-Javadoc)
     * @see org.marketcetera.server.service.OrderDestination#cancelSubscription(org.marketcetera.client.ReportListener)
     */
    @Override
    public void cancelSubscription(ReportListener inReportListener)
    {
        throw new UnsupportedOperationException(); // TODO Auto-generated method stub
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return String.format("%s [%s] - %s",
                             name,
                             id,
                             status);
    }
    /**
     * 
     *
     *
     * @param inStatus
     */
    void setStatus(DestinationStatus inStatus)
    {
        status = inStatus;
        synchronized(this) {
            notifyAll();
        }
    }
    /**
     * Create a new OrderDestinationImpl instance.
     *
     * @param inName a <code>String</code> value
     * @param inId a <code>String</code> value
     * @throws IllegalArgumentException if either parameter is invalid
     */
    public OrderDestinationImpl(String inName,
                                String inId,
                                OrderDestinationIdFactory inOrderDestinationIdFactory)
    {
        name = StringUtils.trimToNull(inName);
        id = inOrderDestinationIdFactory.create(inId);
        Validate.notNull(name,
                         "Destination name must be specified");
    }
    /**
     * 
     */
    private volatile DestinationStatus status = DestinationStatus.UNKNOWN;
    /**
     * 
     */
    private volatile String name;
    /**
     * 
     */
    private volatile OrderDestinationID id;
}
