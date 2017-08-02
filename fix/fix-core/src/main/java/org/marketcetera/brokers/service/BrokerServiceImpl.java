package org.marketcetera.brokers.service;

import java.util.Collection;
import java.util.Collections;

import org.apache.commons.lang.Validate;
import org.marketcetera.brokers.Broker;
import org.marketcetera.brokers.MessageModifier;
import org.marketcetera.client.brokers.BrokerStatus;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.FIXConverter;
import org.marketcetera.trade.Order;
import org.marketcetera.trade.TradeMessage;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import quickfix.Message;

/* $License$ */

/**
 * Provides broker services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class BrokerServiceImpl
        implements BrokerService
{
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.BrokerService#getBrokers()
     */
    @Override
    public Collection<Broker> getBrokers()
    {
        return Collections.unmodifiableCollection(brokers.asMap().values());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.BrokerService#selectBroker(org.marketcetera.trade.Order)
     */
    @Override
    public Broker selectBroker(Order inOrder)
    {
        Broker broker = null;
        if(inOrder.getBrokerID() != null) {
            broker = brokers.getIfPresent(inOrder.getBrokerID());
        }
        if(broker == null) {
            // TODO apply selector
        }
        // TODO mapped/virtual broker stuff?
        Validate.notNull(broker,
                         "No broker for " + inOrder); // TODO
        return broker;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.BrokerService#convertOrder(org.marketcetera.trade.Order, org.marketcetera.brokers.Broker)
     */
    @Override
    public Message convertOrder(Order inOrder,
                                Broker inBroker)
    {
        // verify the broker is available
        BrokerStatus brokerStatus = getBrokerStatus(inBroker.getBrokerId());
        Validate.isTrue(brokerStatus.getLoggedOn(),
                        inBroker.getBrokerId() + " is not available"); // TODO
        // TODO broker algos
        // TODO reprice
        // create the FIX message
        Message message = FIXConverter.toQMessage(inBroker.getFixVersion().getMessageFactory(),
                                                  FIXMessageUtil.getDataDictionary(inBroker.getFixVersion()),
                                                  inOrder);
        // apply modifiers
        for(MessageModifier orderModifier : inBroker.getOrderModifiers()) {
            try {
                orderModifier.modify(inBroker,
                                     message);
                // TODO catch OrderIntercepted
            } catch (Exception e) {
                PlatformServices.handleException(this,
                                                 "Unable to modify order",
                                                 e);
            }
        }
        return message;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.BrokerService#convertResponse(quickfix.Message, org.marketcetera.brokers.Broker)
     */
    @Override
    public TradeMessage convertResponse(Message inMessage,
                                        Broker inBroker)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.service.BrokerService#getBrokerStatus(org.marketcetera.trade.BrokerID)
     */
    @Override
    public BrokerStatus getBrokerStatus(BrokerID inBrokerId)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /**
     * Sets the brokers value.
     *
     * @param inBrokers a <code>Collection&lt;Broker&gt;</code> value
     */
    public void setBrokers(Collection<Broker> inBrokers)
    {
        brokers.invalidateAll();
        if(inBrokers != null) {
            for(Broker broker : inBrokers) {
                brokers.put(broker.getBrokerId(),
                            broker);
            }
        }
    }
    /**
     * 
     */
    private final Cache<BrokerID,Broker> brokers = CacheBuilder.newBuilder().build();
}
