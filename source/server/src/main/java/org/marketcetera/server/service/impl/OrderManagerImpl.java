package org.marketcetera.server.service.impl;

import org.marketcetera.server.service.OrderManager;
import org.marketcetera.systemmodel.persistence.OrderDao;
import org.marketcetera.trade.Order;
import org.marketcetera.trade.OrderID;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/* $License$ */

/**
 * Provides order management services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Service
@ClassVersion("$Id$")
class OrderManagerImpl
        implements OrderManager
{
    /* (non-Javadoc)
     * @see org.marketcetera.server.service.OrderManager#write(org.marketcetera.trade.Order)
     */
    @Override
    @Transactional(propagation=Propagation.REQUIRED,readOnly=false)
    public void write(Order inOrder)
    {
        orderDao.write(inOrder);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.server.service.OrderManager#getBy(org.marketcetera.trade.OrderID)
     */
    @Override
    @Transactional(propagation=Propagation.SUPPORTS,readOnly=true)
    public Order getBy(OrderID inOrderID)
    {
        return orderDao.getBy(inOrderID);
    }
    /**
     * 
     */
    @Autowired
    private OrderDao orderDao;
}
