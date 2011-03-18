package org.marketcetera.server.service.impl;

import java.util.Set;

import org.apache.commons.lang.Validate;
import org.marketcetera.server.service.OrderDestination;
import org.marketcetera.server.service.OrderDestinationManager;
import org.marketcetera.server.service.OrderDestinationSelector;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.Order;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.beans.factory.annotation.Autowired;

/* $License$ */

/**
 * Selects an <code>OrderDestination</code>.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
class DefaultOrderDestinationSelector
        implements OrderDestinationSelector
{
    /* (non-Javadoc)
     * @see org.marketcetera.server.service.OrderDestinationSelector#selectDestination(org.marketcetera.trade.Order)
     */
    @Override
    public OrderDestination selectDestination(Order inOrder)
    {
        if(inOrder.getBrokerID() == null) {
            return getDefaultDestination();
        } else {
            BrokerID brokerID = inOrder.getBrokerID();
            Set<OrderDestination> destinations = orderDestinationManager.getDestinations();
            // TODO this is O(n) - should be in a map, might be worth pre-loading
            for(OrderDestination destination : destinations) {
                if(destination.getId().equals(brokerID.getValue())) {
                    return destination;
                }
            }
            throw new IllegalArgumentException("The specified destination id \"" + brokerID + "\" does not correspond to a known destination");
        }
    }
    /**
     * Sets the default <code>OrderDestination</code> for this selector.
     *
     * @param inDefaultDestination an <code>OrderDestination</code> value
     * @throws IllegalArgumentException if the given <code>OrderDestination</code> is invalid
     */
    public void setDefaultDestination(OrderDestination inDefaultDestination)
    {
        Validate.notNull(inDefaultDestination,
                         "Default order destination must not be null");
        defaultDestination = inDefaultDestination;
    }
    /**
     * Gets the default <code>OrderDestination</code> for this selector.
     *
     * @return an <code>OrderDestination</code> value
     */
    public OrderDestination getDefaultDestination()
    {
        return defaultDestination;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return String.format("DefaultOrderDestinationSelector");
    }
    /**
     * the default <code>OrderDestination</code>
     */
    private volatile OrderDestination defaultDestination;
    /**
     * the <code>OrderDestinationManager</code> used as a source for <code>OrderDestination</code> values
     */
    @Autowired
    private OrderDestinationManager orderDestinationManager;
}
