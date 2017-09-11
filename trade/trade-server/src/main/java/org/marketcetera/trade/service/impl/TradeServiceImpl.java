package org.marketcetera.trade.service.impl;

import java.util.Collection;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.marketcetera.brokers.Broker;
import org.marketcetera.brokers.BrokerStatus;
import org.marketcetera.brokers.BrokerUnavailable;
import org.marketcetera.brokers.MessageModifier;
import org.marketcetera.brokers.Selector;
import org.marketcetera.brokers.service.BrokerService;
import org.marketcetera.core.CoreException;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.event.HasFIXMessage;
import org.marketcetera.modules.headwater.HeadwaterModule;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.FIXConverter;
import org.marketcetera.trade.HasOrder;
import org.marketcetera.trade.Hierarchy;
import org.marketcetera.trade.MessageCreationException;
import org.marketcetera.trade.Order;
import org.marketcetera.trade.Originator;
import org.marketcetera.trade.TradeConstants;
import org.marketcetera.trade.TradeMessage;
import org.marketcetera.trade.TradeMessageBroadcaster;
import org.marketcetera.trade.TradeMessageListener;
import org.marketcetera.trade.UserID;
import org.marketcetera.trade.service.MessageOwnerService;
import org.marketcetera.trade.service.Messages;
import org.marketcetera.trade.service.TradeService;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import quickfix.FieldNotFound;
import quickfix.Message;

/* $License$ */

/**
 * Provides trade services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Service
public class TradeServiceImpl
        implements TradeService,TradeMessageBroadcaster
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
        if(broker == null && brokerSelector != null) {
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
        Collection<MessageModifier> orderModifiers = getOrderMessageModifiers(inBroker);
        // choose the broker to use
        Broker mappedBroker = resolveVirtualBroker(inBroker);
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
            } catch (Exception e) {
                // unable to modify the order, but the show must go on!
                PlatformServices.handleException(this,
                                                 "Unable to modify order",
                                                 e);
            }
        }
        return message;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.service.TradeService#convertResponse(org.marketcetera.event.HasFIXMessage, org.marketcetera.brokers.Broker)
     */
    @Override
    public TradeMessage convertResponse(HasFIXMessage inMessage,
                                        Broker inBroker)
    {
        Message fixMessage = inMessage.getMessage();
        try {
            if(FIXMessageUtil.isTradingSessionStatus(fixMessage)) {
                Messages.TRADE_SESSION_STATUS.info(this,
                                                   inBroker.getFIXDataDictionary().getHumanFieldValue(quickfix.field.TradSesStatus.FIELD,
                                                                                                      fixMessage.getString(quickfix.field.TradSesStatus.FIELD)));
            }
        } catch (FieldNotFound e) {
            PlatformServices.handleException(this,
                                             "Unable to process trading session status message",
                                             e);
        }
        Broker mappedBroker = resolveVirtualBroker(inBroker);
        Collection<MessageModifier> responseModifiers = getReportMessageModifiers(inBroker);
        for(MessageModifier responseModifier : responseModifiers) {
            try {
                responseModifier.modify(mappedBroker,
                                        fixMessage);
                SLF4JLoggerProxy.debug(this,
                                       "Applied {} to {}",
                                       responseModifier,
                                       fixMessage);
            } catch (Exception e) {
                Messages.MODIFICATION_FAILED.warn(this,
                                                  e,
                                                  fixMessage,
                                                  inBroker);
            }
        }
        TradeMessage reply;
        try {
            UserID actor = orderOwnerService.getMessageOwner(inMessage,
                                                             inBroker.getSessionId(),
                                                             inBroker.getBrokerId());
            // TODO determine hierarchy - this might need the original order to resolve
            reply = FIXConverter.fromQMessage(fixMessage,
                                              Originator.Broker,
                                              inBroker.getBrokerId(),
                                              Hierarchy.Flat,
                                              actor,
                                              actor);
        } catch (MessageCreationException e) {
            Messages.REPORT_FAILED.error(this,
                                         e,
                                         fixMessage,
                                         inBroker);
            throw e;
        }
        return reply;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.TradeMessagePublisher#addTradeMessageListener(org.marketcetera.trade.TradeMessageListener)
     */
    @Override
    public void addTradeMessageListener(TradeMessageListener inTradeMessageListener)
    {
        tradeMessageListeners.add(inTradeMessageListener);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.TradeMessagePublisher#removeTradeMessageListener(org.marketcetera.trade.TradeMessageListener)
     */
    @Override
    public void removeTradeMessageListener(TradeMessageListener inTradeMessageListener)
    {
        tradeMessageListeners.remove(inTradeMessageListener);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.TradeMessageBroadcaster#reportTradeMessage(org.marketcetera.trade.TradeMessage)
     */
    @Override
    public void reportTradeMessage(TradeMessage inTradeMessage)
    {
        for(TradeMessageListener tradeMessageListener : tradeMessageListeners) {
            try {
                tradeMessageListener.receiveTradeMessage(inTradeMessage);
            } catch (Exception e) {
                PlatformServices.handleException(this,
                                                 "Error broadcasting trade message",
                                                 e);
            }
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.service.TradeService#submitOrderToOutgoingDataFlow(org.marketcetera.trade.HasOrder)
     */
    @Override
    public Object submitOrderToOutgoingDataFlow(HasOrder inOrder)
    {
        HeadwaterModule outgoingDataFlowModule = HeadwaterModule.getInstance(TradeConstants.outgoingDataFlowName);
        if(outgoingDataFlowModule == null) {
            throw new IllegalStateException("Outgoing data flow not established");
        }
        outgoingDataFlowModule.emit(inOrder);
        // note that this object won't have deterministic state if async flows are used
        return inOrder;
    }
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
    {
        SLF4JLoggerProxy.info(this,
                              "Trade service started");
    }
    /**
     * Resolve the given broker into the appropriate virtual or physical broker.
     *
     * @param inBroker a <code>Broker</code> value
     * @return a <code>Broker</code> value
     */
    private Broker resolveVirtualBroker(Broker inBroker)
    {
        Broker mappedBroker = inBroker;
        if(inBroker.getMappedBrokerId() != null) {
            mappedBroker = brokerService.getBroker(inBroker.getMappedBrokerId());
            if(mappedBroker == null) {
                throw new BrokerUnavailable(new I18NBoundMessage1P(Messages.UNKNOWN_BROKER_ID,
                                                                   inBroker.getMappedBrokerId()));
            }
        }
        return mappedBroker;
    }
    /**
     * Get the complete collection of order modifiers for the given broker.
     *
     * @param inBroker a <code>Broker</code> value
     * @return a <code>Collection&lt;MessageModifier&gt;</code> value
     */
    private Collection<MessageModifier> getOrderMessageModifiers(Broker inBroker)
    {
        return getMessageModifiers(inBroker,
                                   true);
    }
    /**
     * Get the complete collection of report modifiers for the given broker.
     *
     * @param inBroker a <code>Broker</code> value
     * @return a <code>Collection&lt;MessageModifier&gt;</code> value
     */
    private Collection<MessageModifier> getReportMessageModifiers(Broker inBroker)
    {
        return getMessageModifiers(inBroker,
                                   false);
    }
    /**
     * Get the complete collection of message modifiers for the given broker.
     *
     * @param inBroker a <code>Broker</code> value
     * @param inIsOrder a <code>boolean</code> value
     * @return a <code>Collection&lt;MessageModifier&gt;</code> value
     */
    private Collection<MessageModifier> getMessageModifiers(Broker inBroker,
                                                            boolean inIsOrder)
    {
        Collection<MessageModifier> modifiers = Lists.newArrayList();
        if(inIsOrder) {
            modifiers.addAll(inBroker.getOrderModifiers());
        } else {
            modifiers.addAll(inBroker.getResponseModifiers());
        }
        if(inBroker.getMappedBrokerId() != null) {
            Broker mappedBroker = resolveVirtualBroker(inBroker);
            if(inIsOrder) {
                modifiers.addAll(mappedBroker.getOrderModifiers());
            } else {
                modifiers.addAll(mappedBroker.getResponseModifiers());
            }
        }
        return modifiers;
    }
    /**
     * provides access to outgoing message services
     */
    @Autowired
    private MessageOwnerService orderOwnerService;
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
    /**
     * holds trade message listener subscribers
     */
    private final Set<TradeMessageListener> tradeMessageListeners = Sets.newConcurrentHashSet();
}
