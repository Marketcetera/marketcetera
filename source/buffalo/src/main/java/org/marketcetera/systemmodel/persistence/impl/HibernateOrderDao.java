package org.marketcetera.systemmodel.persistence.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.Validate;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.marketcetera.systemmodel.persistence.OrderDao;
import org.marketcetera.trade.Order;
import org.marketcetera.trade.OrderID;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Repository
@ClassVersion("$Id$")
public class HibernateOrderDao
        implements OrderDao
{
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.persistence.OrderDao#write(org.marketcetera.trade.Order)
     */
    @Override
    public void write(Order inOrder)
    {
        PersistentOrder pOrder;
        if(inOrder instanceof PersistentOrder) {
            pOrder = (PersistentOrder)inOrder;
        } else {
            pOrder = new PersistentOrder(inOrder);
        }
        currentSession().saveOrUpdate(pOrder);
        orderCache.put(pOrder.getOrderID(),
                       pOrder);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.systemmodel.persistence.OrderDao#getBy(org.marketcetera.trade.OrderID)
     */
    @Override
    public Order getBy(OrderID inOrderID)
    {
        PersistentOrder order = orderCache.get(inOrderID);
        if(order == null) {
            Criteria criteria = currentSession().createCriteria(PersistentOrder.class);
            criteria.add(Restrictions.eq("orderID",
                                         inOrderID));
            order = (PersistentOrder)criteria.uniqueResult();
            orderCache.put(order.getOrderID(),
                           order);
        }
        return order;
    }
    /**
     * Create a new HibernateOrderDao instance.
     *
     * @param inSessionFactory
     */
    @Autowired
    public HibernateOrderDao(SessionFactory inSessionFactory)
    {
        Validate.notNull(inSessionFactory,
                         "Session factory missing");
        sessionFactory = inSessionFactory;
    }
    /**
    *
    *
    *
    * @return
    */
   private Session currentSession()
   {
       return sessionFactory.getCurrentSession();
   }
   /**
    * 
    */
   private final SessionFactory sessionFactory;
   /**
    * 
    */
   private final Map<OrderID,PersistentOrder> orderCache = new ConcurrentHashMap<OrderID,PersistentOrder>();
}
