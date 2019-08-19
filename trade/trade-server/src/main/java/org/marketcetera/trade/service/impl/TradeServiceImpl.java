package org.marketcetera.trade.service.impl;

import java.util.Collection;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.marketcetera.brokers.BrokerUnavailable;
import org.marketcetera.brokers.MessageModifier;
import org.marketcetera.brokers.Selector;
import org.marketcetera.brokers.service.BrokerService;
import org.marketcetera.core.CoreException;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.event.HasFIXMessage;
import org.marketcetera.fix.FixSessionStatus;
import org.marketcetera.fix.ServerFixSession;
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
import quickfix.SessionID;

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
    public ServerFixSession selectServerFixSession(Order inOrder)
    {
        ServerFixSession serverFixSession = null;
        if(inOrder.getBrokerID() != null) {
            serverFixSession = brokerService.getServerFixSession(inOrder.getBrokerID());
            SLF4JLoggerProxy.debug(this,
                                   "Order {} requsted broker id {} which resolves to {}",
                                   inOrder,
                                   inOrder.getBrokerID(),
                                   serverFixSession);
        }
        if(serverFixSession == null && brokerSelector != null) {
            BrokerID brokerId = brokerSelector.chooseBroker(inOrder);
            if(brokerId != null) {
                serverFixSession = brokerService.getServerFixSession(brokerId);
            }
            SLF4JLoggerProxy.debug(this,
                                   "No session was initially selected for {}, the session selector chose {} which resolves to {}",
                                   inOrder,
                                   brokerId,
                                   serverFixSession);
        }
        if(serverFixSession == null) {
            Messages.NO_BROKER_SELECTED.warn(this,
                                             inOrder);
            throw new CoreException(new I18NBoundMessage1P(Messages.NO_BROKER_SELECTED,
                                                           inOrder));
        } else {
            SLF4JLoggerProxy.debug(this,
                                   "Selected {} for {}",
                                   serverFixSession,
                                   inOrder.getBrokerID());
        }
        return serverFixSession;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.service.TradeService#convertOrder(org.marketcetera.trade.Order, org.marketcetera.brokers.Broker)
     */
    @Override
    public Message convertOrder(Order inOrder,
                                ServerFixSession inServerFixSession)
    {
        // verify the broker is available
        FixSessionStatus sessionStatus = brokerService.getFixSessionStatus(new BrokerID(inServerFixSession.getActiveFixSession().getFixSession().getBrokerId()));
        if(sessionStatus == null) {
            throw new BrokerUnavailable(new I18NBoundMessage1P(Messages.UNKNOWN_BROKER_ID,
                                                               inServerFixSession.getActiveFixSession().getFixSession().getBrokerId()));
        }
        if(!sessionStatus.isLoggedOn()) {
            throw new BrokerUnavailable(Messages.UNAVAILABLE_BROKER);
        }
        // TODO broker algos
        // TODO reprice
        // construct the list of order modifiers to apply
        Collection<MessageModifier> orderModifiers = getOrderMessageModifiers(inServerFixSession);
        // choose the broker to use
        ServerFixSession mappedServerFixSession = resolveVirtualServerFixSession(inServerFixSession);
        // create the FIX message (we can use only one message factory, so if the broker is a virtual broker, we defer to the mapped broker, otherwise the virtual broker would have to duplicate
        //  the entire mapped broker dictionary, etc)
        Message message = FIXConverter.toQMessage(mappedServerFixSession.getFIXVersion().getMessageFactory(),
                                                  FIXMessageUtil.getDataDictionary(mappedServerFixSession.getFIXVersion()),
                                                  inOrder);
        // apply modifiers
        for(MessageModifier orderModifier : orderModifiers) {
            try {
                orderModifier.modify(mappedServerFixSession,
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
                                        ServerFixSession inServerFixSession)
    {
        Message fixMessage = inMessage.getMessage();
        try {
            if(FIXMessageUtil.isTradingSessionStatus(fixMessage)) {
                Messages.TRADE_SESSION_STATUS.info(this,
                                                   inServerFixSession.getFIXDataDictionary().getHumanFieldValue(quickfix.field.TradSesStatus.FIELD,
                                                                                                                fixMessage.getString(quickfix.field.TradSesStatus.FIELD)));
            }
        } catch (FieldNotFound e) {
            PlatformServices.handleException(this,
                                             "Unable to process trading session status message",
                                             e);
        }
        ServerFixSession mappedServerFixSession = resolveVirtualServerFixSession(inServerFixSession);
        Collection<MessageModifier> responseModifiers = getReportMessageModifiers(inServerFixSession);
        for(MessageModifier responseModifier : responseModifiers) {
            try {
                responseModifier.modify(mappedServerFixSession,
                                        fixMessage);
                SLF4JLoggerProxy.debug(this,
                                       "Applied {} to {}",
                                       responseModifier,
                                       fixMessage);
            } catch (Exception e) {
                Messages.MODIFICATION_FAILED.warn(this,
                                                  e,
                                                  fixMessage,
                                                  inServerFixSession);
            }
        }
        TradeMessage reply;
        try {
            BrokerID brokerId = new BrokerID(inServerFixSession.getActiveFixSession().getFixSession().getBrokerId());
            UserID actor = orderOwnerService.getMessageOwner(inMessage,
                                                             new SessionID(inServerFixSession.getActiveFixSession().getFixSession().getSessionId()),
                                                             brokerId);
            // TODO determine hierarchy - this might need the original order to resolve
            reply = FIXConverter.fromQMessage(fixMessage,
                                              Originator.Broker,
                                              brokerId,
                                              Hierarchy.Flat,
                                              actor,
                                              actor);
        } catch (MessageCreationException e) {
            Messages.REPORT_FAILED.error(this,
                                         e,
                                         fixMessage,
                                         inServerFixSession);
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
     * Resolve the given session into the appropriate virtual or physical session.
     *
     * @param inServerFixSession a <code>ServerFixSession</code> value
     * @return a <code>ServerFixSession</code> value
     */
    private ServerFixSession resolveVirtualServerFixSession(ServerFixSession inServerFixSession)
    {
        ServerFixSession mappedServerFixSession = inServerFixSession;
        if(inServerFixSession.getActiveFixSession().getFixSession().getMappedBrokerId() != null) {
            mappedServerFixSession = brokerService.getServerFixSession(new BrokerID(inServerFixSession.getActiveFixSession().getFixSession().getMappedBrokerId()));
            if(mappedServerFixSession == null) {
                throw new BrokerUnavailable(new I18NBoundMessage1P(Messages.UNKNOWN_BROKER_ID,
                                                                   inServerFixSession.getActiveFixSession().getFixSession().getMappedBrokerId()));
            }
        }
        return mappedServerFixSession;
    }
    /**
     * Get the complete collection of order modifiers for the given broker.
     *
     * @param inSession a <code>ServerFixSession</code> value
     * @return a <code>Collection&lt;MessageModifier&gt;</code> value
     */
    private Collection<MessageModifier> getOrderMessageModifiers(ServerFixSession inSession)
    {
        return getMessageModifiers(inSession,
                                   true);
    }
    /**
     * Get the complete collection of report modifiers for the given broker.
     *
     * @param inServerFixSession a <code>ServerFixSession</code> value
     * @return a <code>Collection&lt;MessageModifier&gt;</code> value
     */
    private Collection<MessageModifier> getReportMessageModifiers(ServerFixSession inServerFixSession)
    {
        return getMessageModifiers(inServerFixSession,
                                   false);
    }
    /**
     * Get the complete collection of message modifiers for the given broker.
     *
     * @param inServerFixSession a <code>ServerFixSession</code> value
     * @param inIsOrder a <code>boolean</code> value
     * @return a <code>Collection&lt;MessageModifier&gt;</code> value
     */
    private Collection<MessageModifier> getMessageModifiers(ServerFixSession inServerFixSession,
                                                            boolean inIsOrder)
    {
        Collection<MessageModifier> modifiers = Lists.newArrayList();
        if(inIsOrder) {
            modifiers.addAll(inServerFixSession.getOrderModifiers());
        } else {
            modifiers.addAll(inServerFixSession.getResponseModifiers());
        }
        if(inServerFixSession.getActiveFixSession().getFixSession().getMappedBrokerId() != null) {
            ServerFixSession mappedServerFixSession = resolveVirtualServerFixSession(inServerFixSession);
            if(inIsOrder) {
                modifiers.addAll(mappedServerFixSession.getOrderModifiers());
            } else {
                modifiers.addAll(mappedServerFixSession.getResponseModifiers());
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
