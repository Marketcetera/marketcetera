package org.marketcetera.server.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.concurrent.GuardedBy;

import org.apache.commons.lang.Validate;
import org.marketcetera.server.service.OrderManager;
import org.marketcetera.server.service.OrderManagerSelector;
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
class RoundRobinOrderManagerSelector
        implements OrderManagerSelector, InitializingBean
{
    /* (non-Javadoc)
     * @see org.marketcetera.server.service.OrderManagerSelector#send(org.marketcetera.trade.OrderSingle)
     */
    @Override
    public void send(OrderSingle inOrderSingle)
    {
        roundRobinSelect().send(inOrderSingle);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.server.service.OrderManagerSelector#send(org.marketcetera.trade.OrderReplace)
     */
    @Override
    public void send(OrderReplace inOrderReplace)
    {
        roundRobinSelect().send(inOrderReplace);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.server.service.OrderManagerSelector#send(org.marketcetera.trade.OrderCancel)
     */
    @Override
    public void send(OrderCancel inOrderCancel)
    {
        roundRobinSelect().send(inOrderCancel);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.server.service.OrderManagerSelector#send(org.marketcetera.trade.FIXOrder)
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
        if(managers.isEmpty()) {
            SLF4JLoggerProxy.error(RoundRobinOrderManagerSelector.class,
                                   "RoundRobinOrderManagerSelector requires at least one OrderManager");
        }
        Validate.notEmpty(managers,
                          "RoundRobinOrderManagerSelector requires at least one OrderManager");
    }
    /**
     * 
     *
     *
     * @param inManagers
     */
    public void setOrderManagers(List<OrderManager> inManagers)
    {
        synchronized(managers) {
            managers.clear();
            managers.addAll(inManagers);
        }
    }
    /**
     * 
     *
     *
     * @return
     */
    private OrderManager roundRobinSelect()
    {
        synchronized(managers) {
            if(iterator == null ||
               !iterator.hasNext()) {
                iterator = managers.iterator();
            }
            OrderManager manager = iterator.next();
            SLF4JLoggerProxy.debug(RoundRobinOrderManagerSelector.class,
                                   "Selected OrderManager {}",
                                   manager);
            return manager;
        }
    }
    /**
     * 
     */
    @GuardedBy("managers")
    private volatile Iterator<OrderManager> iterator = null;
    /**
     * 
     */
    @GuardedBy("managers")
    private final List<OrderManager> managers = new ArrayList<OrderManager>();
}
