package org.marketcetera.server.service.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.marketcetera.server.service.OrderDestination;
import org.marketcetera.server.service.OrderDestinationManager;
import org.marketcetera.server.service.OrderDestinationSelector;
import org.marketcetera.trade.Order;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.beans.factory.annotation.Autowired;

/* $License$ */

/**
 * <code>OrderDestinationManager</code> implementation that provides access
 * to <code>OrderDestination</code> objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
class OrderDestinationManagerImpl
        implements OrderDestinationManager
{
    /* (non-Javadoc)
     * @see org.marketcetera.server.service.OrderDestinationManager#getDestinations()
     */
    @Override
    public Set<OrderDestination> getDestinations()
    {
        synchronized(destinations) {
            return Collections.unmodifiableSet(destinations);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.server.service.OrderDestinationManager#send(org.marketcetera.trade.Order)
     */
    @Override
    public void send(Order inOrder)
    {
        SLF4JLoggerProxy.debug(OrderDestinationManagerImpl.class,
                               "{} received {}",
                               this,
                               inOrder);
        // TODO handle IAE if the brokerID on the order is specified but does not exist
        OrderDestination destination = destinationSelector.selectDestination(inOrder);
        SLF4JLoggerProxy.debug(OrderDestinationManagerImpl.class,
                               "{} selected {} for {}",
                               this,
                               destination,
                               inOrder);
        destination.send(inOrder);
    }
    /**
     * Create a new OrderDestinationManagerImpl instance.
     *
     * @param inDestinations
     */
    public OrderDestinationManagerImpl(Set<OrderDestination> inDestinations)
    {
        destinations.addAll(inDestinations);
        Validate.notEmpty(destinations,
                          "No order destinations defined");
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return String.format("OrderDestinationManager");
    }
    /**
     * the set of destinations 
     */
    private final Set<OrderDestination> destinations = new HashSet<OrderDestination>();
    /**
     * 
     */
    @Autowired
    private OrderDestinationSelector destinationSelector;
}
