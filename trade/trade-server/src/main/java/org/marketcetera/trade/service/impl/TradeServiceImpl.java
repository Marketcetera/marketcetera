package org.marketcetera.trade.service.impl;

import org.apache.commons.lang.Validate;
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
        }
        if(broker == null) {
            BrokerID brokerId = brokerSelector.chooseBroker(inOrder);
            if(brokerId != null) {
                broker = brokerService.getBroker(brokerId);
            }
        }
        // TODO mapped/virtual broker stuff?
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
        if(brokerStatus == null || !brokerStatus.getLoggedOn()) {
            throw new BrokerUnavailable(Messages.UNAVAILABLE_BROKER);
        }
        Validate.isTrue(brokerStatus.getLoggedOn(),
                        inBroker.getBrokerId() + " is not available"); // TODO
        // TODO broker algos
        // TODO reprice
        // create the FIX message
        Message message = FIXConverter.toQMessage(inBroker.getFIXVersion().getMessageFactory(),
                                                  FIXMessageUtil.getDataDictionary(inBroker.getFIXVersion()),
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
