package org.marketcetera.server.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.concurrent.GuardedBy;

import org.apache.commons.lang.Validate;
import org.marketcetera.server.service.OrderProcessor;
import org.marketcetera.server.service.OrderProcessorSelector;
import org.marketcetera.trade.FIXOrder;
import org.marketcetera.trade.OrderCancel;
import org.marketcetera.trade.OrderReplace;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.beans.factory.InitializingBean;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
class RoundRobinOrderProcessorSelector
        implements OrderProcessorSelector, InitializingBean
{
    /* (non-Javadoc)
     * @see org.marketcetera.server.service.OrderProcessorSelector#send(org.marketcetera.trade.OrderSingle)
     */
    @Override
    public void send(OrderSingle inOrderSingle)
    {
        roundRobinSelect().send(inOrderSingle);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.server.service.OrderProcessorSelector#send(org.marketcetera.trade.OrderReplace)
     */
    @Override
    public void send(OrderReplace inOrderReplace)
    {
        roundRobinSelect().send(inOrderReplace);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.server.service.OrderProcessorSelector#send(org.marketcetera.trade.OrderCancel)
     */
    @Override
    public void send(OrderCancel inOrderCancel)
    {
        roundRobinSelect().send(inOrderCancel);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.server.service.OrderProcessorSelector#send(org.marketcetera.trade.FIXOrder)
     */
    @Override
    public void send(FIXOrder inFIXOrder)
    {
        roundRobinSelect().send(inFIXOrder);
    }
    /* (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet()
            throws Exception
    {
        if(processors.isEmpty()) {
            SLF4JLoggerProxy.error(RoundRobinOrderProcessorSelector.class,
                                   "RoundRobinOrderProcessorSelector requires at least one OrderProcessor");
        }
        Validate.notEmpty(processors,
                          "RoundRobinOrderProcessorSelector requires at least one OrderProcessor");
    }
    /**
     * 
     *
     *
     * @param inProcessors
     */
    public void setOrderProcessors(List<OrderProcessor> inProcessors)
    {
        synchronized(processors) {
            processors.clear();
            processors.addAll(inProcessors);
        }
    }
    /**
     * 
     *
     *
     * @return
     */
    private OrderProcessor roundRobinSelect()
    {
        synchronized(processors) {
            if(iterator == null ||
               !iterator.hasNext()) {
                iterator = processors.iterator();
            }
            OrderProcessor processor = iterator.next();
            SLF4JLoggerProxy.debug(RoundRobinOrderProcessorSelector.class,
                                   "Selected OrderProcessor {}",
                                   processor);
            return processor;
        }
    }
    /**
     * 
     */
    @GuardedBy("processors")
    private volatile Iterator<OrderProcessor> iterator = null;
    /**
     * 
     */
    @GuardedBy("processors")
    private final List<OrderProcessor> processors = new ArrayList<OrderProcessor>();
}
