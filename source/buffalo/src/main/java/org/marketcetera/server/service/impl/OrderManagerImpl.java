package org.marketcetera.server.service.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang.StringUtils;
import org.marketcetera.server.service.OrderDestinationManager;
import org.marketcetera.server.service.OrderManager;
import org.marketcetera.server.service.OrderValidatorFactory;
import org.marketcetera.trade.*;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.Lifecycle;

/* $License$ */

/**
 * Provides order management services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
class OrderManagerImpl
        implements OrderManager, Lifecycle
{
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#start()
     */
    @Override
    public void start()
    {
        SLF4JLoggerProxy.debug(OrderManagerImpl.class,
                               "Starting order manager");
        if(orderProcessingService.isShutdown() ||
           orderProcessingService.isTerminated()) {
            SLF4JLoggerProxy.error(OrderManagerImpl.class,
                                   "Error starting OrderManager");
            throw new IllegalStateException();
        }
        running.set(true);
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#stop()
     */
    @Override
    public void stop()
    {
        try {
            // this forbids additional submissions and allows existing ones to continue
            orderProcessingService.shutdown();
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
     * @see org.marketcetera.server.service.OrderManager#sendOrder(org.marketcetera.trade.OrderSingle)
     */
    @Override
    public void send(OrderSingle inOrderSingle)
    {
        process(inOrderSingle);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.server.service.OrderManager#sendOrder(org.marketcetera.trade.OrderReplace)
     */
    @Override
    public void send(OrderReplace inOrderReplace)
    {
        process(inOrderReplace);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.server.service.OrderManager#sendOrder(org.marketcetera.trade.OrderCancel)
     */
    @Override
    public void send(OrderCancel inOrderCancel)
    {
        process(inOrderCancel);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.server.service.OrderManager#sendOrderRaw(org.marketcetera.trade.FIXOrder)
     */
    @Override
    public void send(FIXOrder inFIXOrder)
    {
        process(inFIXOrder);
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return String.format("OrderManager %s [%s]",
                             name == null ? "(unnamed)" : name,
                             running.get() ? "running" : "stopped");
    }
    /**
     * Get the name value.
     *
     * @return a <code>String</code> value
     */
    public String getName()
    {
        return name;
    }
    /**
     * Sets the name value.
     *
     * @param a <code>String</code> value
     */
    public void setName(String inName)
    {
        name = StringUtils.trimToNull(inName);
    }
    /**
     * Process the given order. 
     *
     * @param inOrder a <code>T extends Order</code> value
     */
    private <T extends Order> void process(T inOrder)
    {
        orderProcessingService.execute(new OrderProcessor<T>(inOrder));
    }
    /**
     * Processes a single order.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    @ClassVersion("$Id$")
    private final class OrderProcessor<T extends Order>
            implements Runnable
    {
        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run()
        {
            SLF4JLoggerProxy.debug(OrderManagerImpl.class,
                                   "{} beginning to process {}",
                                   OrderManagerImpl.this,
                                   order);
            try {
                try {
                    validatorFactory.getValidator().validate(order);
                    SLF4JLoggerProxy.debug(OrderManagerImpl.class,
                                           "Order validation for {} succeeded",
                                           order);
                } catch (Exception e) {
                    SLF4JLoggerProxy.warn(OrderManagerImpl.class,
                                          e,
                                          "Order validation failed");
                    return;
                }
                SLF4JLoggerProxy.debug(OrderManagerImpl.class,
                                       "Sending {}",
                                       order);
                orderDestinationManager.send(order);
                SLF4JLoggerProxy.debug(OrderManagerImpl.class,
                                       "{} sent",
                                       order);
            } finally {
                SLF4JLoggerProxy.debug(OrderManagerImpl.class,
                                       "{} completed processing {}",
                                       this,
                                       order);
            }
        }
        /**
         * Create a new OrderProcessor instance.
         *
         * @param inOrder a <code>T</code> value
         */
        private OrderProcessor(T inOrder)
        {
            order = inOrder;
        }
        /**
         * the order to process 
         */
        private final T order;
    }
    /**
     * manages a thread pool to process orders
     */
    private final ExecutorService orderProcessingService = Executors.newCachedThreadPool();
    /**
     * indicates if the order manager is running
     */
    private final AtomicBoolean running = new AtomicBoolean(false);
    /**
     * provides an order validator factory to create an order validator
     */
    @Autowired
    private OrderValidatorFactory validatorFactory;
    /**
     * provides access to broker services
     */
    @Autowired
    private OrderDestinationManager orderDestinationManager;
    /**
     * identifies this order manager
     */
    private String name;
}
