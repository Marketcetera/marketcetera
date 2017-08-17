package org.marketcetera.trade.service.impl;

import java.util.List;

import org.marketcetera.brokers.Broker;
import org.marketcetera.brokers.BrokerStatus;
import org.marketcetera.brokers.BrokerUnavailable;
import org.marketcetera.brokers.MessageModifier;
import org.marketcetera.brokers.Selector;
import org.marketcetera.brokers.service.BrokerService;
import org.marketcetera.core.CoreException;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.FIXConverter;
import org.marketcetera.trade.Order;
import org.marketcetera.trade.TradeMessage;
import org.marketcetera.trade.service.Messages;
import org.marketcetera.trade.service.TradeService;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;

import quickfix.Message;

/* $License$ */

/**
 * Provides trade services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class TradeServiceImpl
        implements TradeService
{
    /* (non-Javadoc)
     * @see org.marketcetera.trade.service.TradeService#selectBroker(org.marketcetera.trade.Order)
     */
    @Override
    public Broker selectBroker(Order inOrder)
    {
        Broker broker = null;
        if(inOrder.getBrokerID() != null) {
            broker = brokerService.getBroker(inOrder.getBrokerID());
            SLF4JLoggerProxy.debug(this,
                                   "Order {} requsted broker id {} which resolves to {}",
                                   inOrder,
                                   inOrder.getBrokerID(),
                                   broker);
        }
        if(broker == null) {
            BrokerID brokerId = brokerSelector.chooseBroker(inOrder);
            if(brokerId != null) {
                broker = brokerService.getBroker(brokerId);
            }
            SLF4JLoggerProxy.debug(this,
                                   "No broker was initially selected for {}, the broker selector chose {} which resolves to {}",
                                   inOrder,
                                   brokerId,
                                   broker);
        }
        if(broker == null) {
            Messages.NO_BROKER_SELECTED.warn(this,
                                             inOrder);
            throw new CoreException(new I18NBoundMessage1P(Messages.NO_BROKER_SELECTED,
                                                           inOrder));
        } else {
            SLF4JLoggerProxy.debug(this,
                                   "Selected {} for {}",
                                   broker,
                                   inOrder.getBrokerID());
        }
        return broker;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.service.TradeService#convertOrder(org.marketcetera.trade.Order, org.marketcetera.brokers.Broker)
     */
    @Override
    public Message convertOrder(Order inOrder,
                                Broker inBroker)
    {
        // verify the broker is available
        BrokerStatus brokerStatus = brokerService.getBrokerStatus(inBroker.getBrokerId());
        if(brokerStatus == null) {
            throw new BrokerUnavailable(new I18NBoundMessage1P(Messages.UNKNOWN_BROKER_ID,
                                                               inBroker.getBrokerId()));
        }
        if(!brokerStatus.getLoggedOn()) {
            throw new BrokerUnavailable(Messages.UNAVAILABLE_BROKER);
        }
        // TODO broker algos
        // TODO reprice
        // construct the list of order modifiers to apply
        List<MessageModifier> orderModifiers = Lists.newArrayList();
        orderModifiers.addAll(inBroker.getOrderModifiers());
        Broker mappedBroker = inBroker;
        if(inBroker.getMappedBrokerId() != null) {
            mappedBroker = brokerService.getBroker(inBroker.getMappedBrokerId());
            if(mappedBroker == null) {
                throw new BrokerUnavailable(new I18NBoundMessage1P(Messages.UNKNOWN_BROKER_ID,
                                                                   inBroker.getMappedBrokerId()));
            }
            orderModifiers.addAll(mappedBroker.getOrderModifiers());
        }
        // create the FIX message (we can use only one message factory, so if the broker is a virtual broker, we defer to the mapped broker, otherwise the virtual broker would have to duplicate
        //  the entire mapped broker dictionary, etc)
        Message message = FIXConverter.toQMessage(mappedBroker.getFIXVersion().getMessageFactory(),
                                                  FIXMessageUtil.getDataDictionary(mappedBroker.getFIXVersion()),
                                                  inOrder);
        // apply modifiers
        for(MessageModifier orderModifier : orderModifiers) {
            try {
                orderModifier.modify(mappedBroker,
                                     message);
                SLF4JLoggerProxy.debug(this,
                                       "Applied {} to {}",
                                       orderModifier,
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
     * @see org.marketcetera.trade.service.TradeService#convertResponse(quickfix.Message, org.marketcetera.brokers.Broker)
     */
    @Override
    public TradeMessage convertResponse(Message inMessage,
                                        Broker inBroker)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /**
     * provides access to broker services
     */
    @Autowired
    private BrokerService brokerService;
    /**
     * optional broker selector
     */
    @Autowired(required=false)
    private Selector brokerSelector;
}
