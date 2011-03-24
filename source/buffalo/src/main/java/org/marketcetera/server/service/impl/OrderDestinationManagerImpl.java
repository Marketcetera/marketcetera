package org.marketcetera.server.service.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang.Validate;
import org.marketcetera.server.service.*;
import org.marketcetera.systemmodel.OrderDestinationID;
import org.marketcetera.trade.Order;
import org.marketcetera.trade.ReportBase;
import org.marketcetera.trade.TradeMessage;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.beans.factory.InitializingBean;
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
        implements OrderDestinationManager, InitializingBean
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
    /**
     * Sets the <code>OrderDestination</code> values to the given values.
     *
     * @param inDestinations a <code>Set&lt;OrderDestination&gt;</code> value
     */
    public void setDestinations(Set<OrderDestination> inDestinations)
    {
        synchronized(destinations) {
            destinations.clear();
            destinations.addAll(inDestinations);
            
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
        OrderDestination destination = destinationSelector.selectDestination(inOrder);
        SLF4JLoggerProxy.debug(OrderDestinationManagerImpl.class,
                               "{} selected {} for {}",
                               this,
                               destination,
                               inOrder);
        if(destination instanceof HasOrderModifiers) {
            HasOrderModifiers hasOrderModifiers = (HasOrderModifiers)destination;
            List<OrderModifier> orderModifiers = hasOrderModifiers.getOrderModifiers();
            if(orderModifiers != null) {
                try {
                    for(OrderModifier orderModifier : orderModifiers) {
                        orderModifier.modify(inOrder);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e); // TODO handle properly
                }
            }
        }
        destination.send(inOrder);
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#start()
     */
    @Override
    public synchronized void start()
    {
        if(isRunning()) {
            SLF4JLoggerProxy.warn(OrderDestinationManagerImpl.class,
                                  "{} already started",
                                  this);
            return;
        }
        try {
            synchronized(destinations) {
                for(OrderDestination destination : destinations) {
                    SLF4JLoggerProxy.debug(OrderDestinationManagerImpl.class,
                                           "Starting {}",
                                           destination);
                    destination.start();
                    SLF4JLoggerProxy.debug(OrderDestinationManagerImpl.class,
                                           "{} successfully started",
                                           destination);
                }
            }
        } catch (Exception e) {
            SLF4JLoggerProxy.error(OrderDestinationManagerImpl.class,
                                   e,
                                   "Unable to start {}",
                                   this);
            stop();
            if(e instanceof RuntimeException) {
                throw (RuntimeException)e;
            }
            throw new RuntimeException(e);
        }
        running.set(true);
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#stop()
     */
    @Override
    public synchronized void stop()
    {
        if(!isRunning()) {
            SLF4JLoggerProxy.warn(OrderDestinationManagerImpl.class,
                                  "{} already stopped",
                                  this);
            return;
        }
        try {
            synchronized(destinations) {
                for(OrderDestination destination : destinations) {
                    SLF4JLoggerProxy.debug(OrderDestinationManagerImpl.class,
                                           "Stopping {}",
                                           destination);
                    try {
                        if(destination.isRunning()) {
                            destination.stop();
                        } else {
                            SLF4JLoggerProxy.debug(OrderDestinationManagerImpl.class,
                                                   "{} not running, skipped",
                                                   destination);
                        }
                        SLF4JLoggerProxy.debug(OrderDestinationManagerImpl.class,
                                               "{} successfully stopped",
                                               destination);
                    } catch (Exception e2) {
                        SLF4JLoggerProxy.warn(OrderDestinationManagerImpl.class,
                                              e2,
                                              "Unable to stop {}, skipping",
                                              destination);
                    }
                }
            }
        } finally {
            running.set(false);
        }
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#isRunning()
     */
    @Override
    public boolean isRunning()
    {
        return running.get();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "OrderDestinationManager";
    }
    /* (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet()
            throws Exception
    {
        Validate.notEmpty(destinations,
                          "No order destinations defined for " + this);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.server.service.OrderDestinationManager#getOrderDestinationFor(org.marketcetera.systemmodel.OrderDestinationID)
     */
    @Override
    public OrderDestination getOrderDestinationFor(OrderDestinationID inId)
    {
        synchronized(destinations) {
            // TODO this is O(n), add a map for destinations
            for(OrderDestination destination : destinations) {
                if(inId.equals(destination.getId())) {
                    return destination;
                }
            }
        }
        return null;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.server.service.OrderDestinationManager#receive(org.marketcetera.trade.TradeMessage, org.marketcetera.server.service.OrderDestination)
     */
    @Override
    public void receive(TradeMessage inMessage,
                        OrderDestination inOrderDestination)
    {
        if(inOrderDestination instanceof HasOrderModifiers) {
            HasOrderModifiers hasOrderModifiers = (HasOrderModifiers)inOrderDestination;
            List<TradeMessageModifier> tradeMessageModifiers = hasOrderModifiers.getTradeMessageModifiers();
            if(tradeMessageModifiers != null) {
                try {
                    for(TradeMessageModifier tradeMessageModifier : tradeMessageModifiers) {
                        tradeMessageModifier.modify(inMessage);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e); // TODO throw typed exception
                }
            }
        }
        if(inMessage instanceof ReportBase) {
            ReportBase report = (ReportBase)inMessage;
            // TODO assign report ID
            // TODO persist report
            //                PersistentReport.save(report);
        } else {
            SLF4JLoggerProxy.warn(OrderDestinationManagerImpl.class,
                                  "Cannot persist {} as it is not a ReportBase",
                                  inMessage);
        }
        // TODO send message to listeners
    }
    /**
     * the set of destinations known to the system
     */
    private final Set<OrderDestination> destinations = new HashSet<OrderDestination>();
    /**
     * the destination select which chooses a destination for an order
     */
    @Autowired
    private OrderDestinationSelector destinationSelector;
    /**
     * indicates if the manager is running or not
     */
    private final AtomicBoolean running = new AtomicBoolean(false);
}
