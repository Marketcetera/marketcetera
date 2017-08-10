package org.marketcetera.trade.service.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.marketcetera.admin.User;
import org.marketcetera.core.Cacheable;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.IdentifyOwnerStrategy;
import org.marketcetera.trade.OutgoingMessage;
import org.marketcetera.trade.OutgoingMessageFactory;
import org.marketcetera.trade.UserID;
import org.marketcetera.trade.dao.PersistentOutgoingMessage;
import org.marketcetera.trade.dao.PersistentOutgoingMessageDao;
import org.marketcetera.trade.service.OutgoingMessageService;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.SessionID;

/* $License$ */

/**
 * Provides services for outgoing messages.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class OutgoingMessageServiceImpl
        implements OutgoingMessageService, Cacheable
{
    /* (non-Javadoc)
     * @see com.marketcetera.ors.outgoingorder.OrderService#cache(quickfix.Message, org.marketcetera.trade.UserID)
     */
    @Override
    public void cacheMessageOwner(Message inOutgoingMessage,
                                  UserID inActor)
    {
        String orderId = null;
        if(inOutgoingMessage.isSetField(quickfix.field.ClOrdID.FIELD)) {
            try {
                orderId = inOutgoingMessage.getString(quickfix.field.ClOrdID.FIELD);
            } catch (FieldNotFound e) {
                throw new RuntimeException(e);
            }
        } else if(inOutgoingMessage.isSetField(quickfix.field.OrderID.FIELD)) {
            try {
                orderId = inOutgoingMessage.getString(quickfix.field.OrderID.FIELD);
            } catch (FieldNotFound e) {
                throw new RuntimeException(e);
            }
        }
        if(orderId == null) {
            SLF4JLoggerProxy.warn(this,
                                  "Not caching owner for {}",
                                  inOutgoingMessage);
        } else {
            usersByOrderId.put(orderId,
                               inActor);
        }
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.outgoingorder.OrderService#recordOutgoingMessage(quickfix.Message, quickfix.SessionID, org.marketcetera.trade.BrokerID, org.marketcetera.trade.UserID)
     */
    @Override
    public PersistentOutgoingMessage recordOutgoingMessage(Message inOutgoingMessage,
                                                           SessionID inSessionId,
                                                           BrokerID inBrokerId,
                                                           User inActor)
    {
        OutgoingMessage outgoingMessage = outgoingMessageFactory.create(inOutgoingMessage,
                                                                        inBrokerId,
                                                                        inSessionId,
                                                                        inActor);
        return save(outgoingMessage);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.outgoingorder.OrderService#save(com.marketcetera.ors.domain.OutgoingMessage)
     */
    @Override
    @Transactional(readOnly=false,propagation=Propagation.REQUIRED)
    public PersistentOutgoingMessage save(OutgoingMessage inOutgoingMessage)
    {
        PersistentOutgoingMessage pOutgoingMessage;
        if(inOutgoingMessage instanceof PersistentOutgoingMessage) {
            pOutgoingMessage = (PersistentOutgoingMessage)inOutgoingMessage;
        } else {
            pOutgoingMessage = new PersistentOutgoingMessage(inOutgoingMessage);
        }
        return outgoingMessageDao.save(pOutgoingMessage);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.outgoingorder.OutgoingMessageService#getMessageOwner(quickfix.Message, quickfix.SessionID, org.marketcetera.trade.BrokerID)
     */
    @Override
    public UserID getMessageOwner(Message inIncomingMessage,
                                  SessionID inSessionId,
                                  BrokerID inBrokerId)
    {
        // first, check the cache if the message has an order id
        String orderId = null;
        if(inIncomingMessage.isSetField(quickfix.field.ClOrdID.FIELD)) {
            try {
                orderId = inIncomingMessage.getString(quickfix.field.ClOrdID.FIELD);
            } catch (FieldNotFound e) {
                throw new RuntimeException(e);
            }
        } else if(inIncomingMessage.isSetField(quickfix.field.OrderID.FIELD)) {
            try {
                orderId = inIncomingMessage.getString(quickfix.field.OrderID.FIELD);
            } catch (FieldNotFound e) {
                throw new RuntimeException(e);
            }
        }
        if(orderId == null) {
            SLF4JLoggerProxy.debug(this,
                                   "{} has no order id field, cannot determine owner from cache",
                                   inIncomingMessage);
        } else {
            UserID owner = usersByOrderId.getIfPresent(orderId);
            if(owner != null) {
                SLF4JLoggerProxy.debug(this,
                                       "{} is owned by {} according to the cache",
                                       orderId,
                                       owner);
                return owner;
            }
        }
        SLF4JLoggerProxy.debug(this,
                               "{} has no cached owner, beginning owner identity strategies",
                               orderId);
        for(IdentifyOwnerStrategy strategy : identifyOwnerStrategies) {
            UserID owner = strategy.getOwnerOf(inIncomingMessage,
                                               inSessionId,
                                               inBrokerId);
            if(owner != null) {
                if(orderId != null) {
                    usersByOrderId.put(orderId,
                                       owner);
                }
                SLF4JLoggerProxy.debug(this,
                                       "{} is owned by {} according to identity strategies",
                                       inIncomingMessage,
                                       owner);
                return owner;
            }
        }
        throw new UnsupportedOperationException("No owner could be assigned to the message - consider using a default owner strategy as all messages must have an owner");
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.Cacheable#clear()
     */
    @Override
    public void clear()
    {
        usersByOrderId.invalidateAll();
    }
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
    {
        usersByOrderId = CacheBuilder.newBuilder().maximumSize(orderCacheSize).build();
    }
    /**
     * Get the outgoingMessageFactory value.
     *
     * @return an <code>OutgoingMessageFactory</code> value
     */
    public OutgoingMessageFactory getOutgoingMessageFactory()
    {
        return outgoingMessageFactory;
    }
    /**
     * Sets the outgoingMessageFactory value.
     *
     * @param inOutgoingMessageFactory an <code>OutgoingMessageFactory</code> value
     */
    public void setOutgoingMessageFactory(OutgoingMessageFactory inOutgoingMessageFactory)
    {
        outgoingMessageFactory = inOutgoingMessageFactory;
    }
    /**
     * Get the outgoingMessageDao value.
     *
     * @return a <code>PersistentOutgoingMessageDao</code> value
     */
    public PersistentOutgoingMessageDao getOutgoingMessageDao()
    {
        return outgoingMessageDao;
    }
    /**
     * Sets the outgoingMessageDao value.
     *
     * @param inOutgoingMessageDao a <code>PersistentOutgoingMessageDao</code> value
     */
    public void setOutgoingMessageDao(PersistentOutgoingMessageDao inOutgoingMessageDao)
    {
        outgoingMessageDao = inOutgoingMessageDao;
    }
    /**
     * Get the orderCacheSize value.
     *
     * @return a <code>long</code> value
     */
    public long getOrderCacheSize()
    {
        return orderCacheSize;
    }
    /**
     * Sets the orderCacheSize value.
     *
     * @param a <code>long</code> value
     */
    public void setOrderCacheSize(long inOrderCacheSize)
    {
        orderCacheSize = inOrderCacheSize;
    }
    /**
     * Get the identifyOwnerStrategies value.
     *
     * @return a <code>List&lt;IdentifyOwnerStrategy&gt;</code> value
     */
    public List<IdentifyOwnerStrategy> getIdentifyOwnerStrategies()
    {
        return identifyOwnerStrategies;
    }
    /**
     * Set the identifyOwnerStrategies value.
     *
     * @param a <code>List&lt;IdentifyOwnerStrategy&gt;</code> value
     */
    public void setIdentifyOwnerStrategies(List<IdentifyOwnerStrategy> inIdentifyOwnerStrategies)
    {
        identifyOwnerStrategies = inIdentifyOwnerStrategies;
    }
    /**
     * allows datastore access to outgoing messages
     */
    @Autowired
    private PersistentOutgoingMessageDao outgoingMessageDao;
    /**
     * creates outgoing message objects
     */
    @Autowired
    private OutgoingMessageFactory outgoingMessageFactory;
    /**
     * max number of order owners to cache
     */
    private long orderCacheSize = 10000;
    /**
     * caches owner id by order id
     */
    private Cache<String,UserID> usersByOrderId;
    /**
     * provides a collection of strategies to use to identify the owner of a message
     */
    private List<IdentifyOwnerStrategy> identifyOwnerStrategies = Lists.newArrayList();
}
