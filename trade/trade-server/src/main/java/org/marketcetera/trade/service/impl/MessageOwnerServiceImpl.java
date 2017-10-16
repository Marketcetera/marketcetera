package org.marketcetera.trade.service.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.marketcetera.admin.HasUser;
import org.marketcetera.admin.User;
import org.marketcetera.core.Cacheable;
import org.marketcetera.core.CoreException;
import org.marketcetera.event.HasFIXMessage;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.IdentifyOwnerStrategy;
import org.marketcetera.trade.UserID;
import org.marketcetera.trade.service.MessageOwnerService;
import org.marketcetera.trade.service.Messages;
import org.marketcetera.util.log.I18NBoundMessage3P;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.SessionID;

/* $License$ */

/**
 * Identifies the owner of messages.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MessageOwnerServiceImpl
        implements MessageOwnerService,Cacheable
{
    /* (non-Javadoc)
     * @see org.marketcetera.trade.service.MessageOwnerService#cacheMessageOwner(quickfix.Message, org.marketcetera.trade.UserID)
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
            SLF4JLoggerProxy.debug(this,
                                   "Not caching owner for {} because there is no order id",
                                   inOutgoingMessage);
        } else {
            SLF4JLoggerProxy.debug(this,
                                   "Caching owner {} for {}",
                                   inActor,
                                   orderId);
            usersByOrderId.put(orderId,
                               inActor);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.service.MessageOwnerService#getMessageOwner(org.marketcetera.event.HasFIXMessage, quickfix.SessionID, org.marketcetera.trade.BrokerID)
     */
    @Override
    public UserID getMessageOwner(HasFIXMessage inIncomingMessage,
                                  SessionID inSessionId,
                                  BrokerID inBrokerId)
    {
        if(inIncomingMessage instanceof HasUser) {
            // this is the easy case - we already know who the owner is
            HasUser hasUser = (HasUser)inIncomingMessage;
            User owner = hasUser.getUser();
            if(owner == null) {
                SLF4JLoggerProxy.warn(this,
                                      "{} alleges to have an incoming owner, but the owner is null, continuing to identify the owner through other means",
                                      inIncomingMessage);
            } else {
                SLF4JLoggerProxy.debug(this,
                                       "{} has an attached owner: {}",
                                       inIncomingMessage,
                                       owner);
                return owner.getUserID();
            }
        }
        quickfix.Message fixMessage = inIncomingMessage.getMessage();
        // first, check the cache if the message has an order id
        String orderId = null;
        if(fixMessage.isSetField(quickfix.field.ClOrdID.FIELD)) {
            try {
                orderId = fixMessage.getString(quickfix.field.ClOrdID.FIELD);
            } catch (FieldNotFound e) {
                throw new RuntimeException(e);
            }
        } else if(fixMessage.isSetField(quickfix.field.OrderID.FIELD)) {
            try {
                orderId = fixMessage.getString(quickfix.field.OrderID.FIELD);
            } catch (FieldNotFound e) {
                throw new RuntimeException(e);
            }
        }
        if(orderId == null) {
            SLF4JLoggerProxy.debug(this,
                                   "{} has no order id field, cannot determine owner from cache",
                                   fixMessage);
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
            UserID owner = strategy.getOwnerOf(fixMessage,
                                               inSessionId,
                                               inBrokerId);
            if(owner != null) {
                if(orderId != null) {
                    usersByOrderId.put(orderId,
                                       owner);
                }
                SLF4JLoggerProxy.debug(this,
                                       "{} is owned by {} according to identity strategies",
                                       fixMessage,
                                       owner);
                return owner;
            }
        }
        throw new CoreException(new I18NBoundMessage3P(Messages.NO_OWNER,
                                                       fixMessage,
                                                       inBrokerId,
                                                       inSessionId));
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
        SLF4JLoggerProxy.info(this,
                              "Message owner service started");
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
