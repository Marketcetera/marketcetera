package org.marketcetera.server.service;

import org.marketcetera.client.ReportListener;
import org.marketcetera.systemmodel.OrderDestinationID;
import org.marketcetera.trade.Order;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.context.Lifecycle;

/* $License$ */

/**
 * Represents a destination to which an {@link Order} can be sent.
 * 
 * <p>The destination may be an intermediary, a broker, or an exchange. Essentially,
 * an <code>OrderDestination</code> is anything to which an order can be sent.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface OrderDestination
        extends Lifecycle
{
    /**
     * 
     *
     *
     * @return
     */
    public DestinationStatus getStatus();
    /**
     * 
     *
     *
     * @return
     */
    public String getName();
    /**
     * 
     *
     *
     * @return
     */
    public OrderDestinationID getId();
    /**
     * 
     *
     *
     * @param inOrder
     */
    public void send(Order inOrder);
    /**
     * 
     *
     *
     * @param inReportListener
     */
    public void subscribe(ReportListener inReportListener);
    /**
     * 
     *
     *
     * @param inReportListener
     */
    public void cancelSubscription(ReportListener inReportListener);
}
