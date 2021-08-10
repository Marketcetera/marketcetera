package org.marketcetera.trade.service.impl;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.marketcetera.admin.User;
import org.marketcetera.brokers.BrokerSelector;
import org.marketcetera.brokers.BrokerUnavailable;
import org.marketcetera.brokers.service.BrokerService;
import org.marketcetera.core.CoreException;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.event.HasFIXMessage;
import org.marketcetera.eventbus.EventBusService;
import org.marketcetera.fix.FixSessionStatus;
import org.marketcetera.fix.ServerFixSession;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.FIXConverter;
import org.marketcetera.trade.Hierarchy;
import org.marketcetera.trade.MessageCreationException;
import org.marketcetera.trade.Order;
import org.marketcetera.trade.OrderBase;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.Originator;
import org.marketcetera.trade.SendOrderFailed;
import org.marketcetera.trade.TradeMessage;
import org.marketcetera.trade.TradeMessageBroadcaster;
import org.marketcetera.trade.TradeMessageListener;
import org.marketcetera.trade.UserID;
import org.marketcetera.trade.event.OutgoingOrderStatusEvent;
import org.marketcetera.trade.event.SimpleOutgoingOrderEvent;
import org.marketcetera.trade.event.SimpleOutgoingOrderStatusEvent;
import org.marketcetera.trade.service.MessageOwnerService;
import org.marketcetera.trade.service.Messages;
import org.marketcetera.trade.service.TradeService;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Sets;
import com.google.common.eventbus.Subscribe;

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
        boolean failed = false;
        String message = null;
        quickfix.Message fixMessage = null;
        try {
            // verify the broker is available
            FixSessionStatus sessionStatus = brokerService.getFixSessionStatus(new BrokerID(inServerFixSession.getActiveFixSession().getFixSession().getBrokerId()));
            if(sessionStatus == null) {
                failed = true;
                RuntimeException e = new BrokerUnavailable(new I18NBoundMessage1P(Messages.UNKNOWN_BROKER_ID,
                                                                                  inServerFixSession.getActiveFixSession().getFixSession().getBrokerId()));
                message = PlatformServices.getMessage(e);
                throw e;
            }
            if(!sessionStatus.isLoggedOn()) {
                throw new BrokerUnavailable(Messages.UNAVAILABLE_BROKER);
            }
            // TODO broker algos
            // TODO reprice
            // choose the broker to use
            ServerFixSession mappedServerFixSession = resolveVirtualServerFixSession(inServerFixSession);
            // create the FIX message (we can use only one message factory, so if the broker is a virtual broker, we defer to the mapped broker, otherwise the virtual broker would have to duplicate
            //  the entire mapped broker dictionary, etc)
            fixMessage = FIXConverter.toQMessage(mappedServerFixSession.getFIXVersion().getMessageFactory(),
                                                 FIXMessageUtil.getDataDictionary(mappedServerFixSession.getFIXVersion()),
                                                 inOrder);
            return fixMessage;
        } catch (Exception e) {
            OrderID orderId = null;
            failed = true;
            message = PlatformServices.getMessage(e);
            if(inOrder instanceof OrderBase) {
                orderId = ((OrderBase)inOrder).getOrderID();
            } else if(fixMessage != null && fixMessage.isSetField(quickfix.field.OrderID.FIELD)) {
                try {
                    orderId = new OrderID(fixMessage.getString(quickfix.field.OrderID.FIELD));
                } catch (FieldNotFound ignored) {} // this exception cannot occur because we explicitly check for the existance of the field above
            }
            if(orderId == null) {
                SLF4JLoggerProxy.warn(this,
                                      e,
                                      "Unable to send outgoing order status for {}/{} because it doesn't appear to have an order id",
                                      inOrder,
                                      fixMessage);
            } else {
                eventBusService.post(new SimpleOutgoingOrderStatusEvent(message,
                                                                        failed,
                                                                        inOrder,
                                                                        orderId,
                                                                        fixMessage));
            }
            throw e;
        }
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
        TradeMessage reply;
        try {
            BrokerID brokerId = new BrokerID(inServerFixSession.getActiveFixSession().getFixSession().getBrokerId());
            UserID actor = orderOwnerService.getMessageOwner(inMessage,
                                                             new quickfix.SessionID(inServerFixSession.getActiveFixSession().getFixSession().getSessionId()),
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
    /**
     * Receive order status from outgoing orders.
     *
     * @param inEvent an <code>OutgoingOrderStatusEvent</code> value
     */
    @Subscribe
    public void onOutgoingOrderStatus(OutgoingOrderStatusEvent inEvent)
    {
        SLF4JLoggerProxy.info(this,
                              "Received {}",
                              inEvent);
        orderStatusEventsByOrderId.put(inEvent.getOrderId(),
                                       inEvent);
        synchronized(orderStatusEventsByOrderId) {
            orderStatusEventsByOrderId.notifyAll();
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.service.TradeService#sendOrder(org.marketcetera.admin.User, org.marketcetera.trade.Order)
     */
    @Override
    public void sendOrder(User inUser,
                          Order inOrder)
    {
        SLF4JLoggerProxy.info(this,
                              "{} sending {}",
                              inUser.getName(),
                              inOrder);
        eventBusService.post(new SimpleOutgoingOrderEvent(inUser,
                                                          inOrder));
        // wait a reasonable amount of time in case of async operations? TODO
        if(inOrder instanceof OrderBase) {
            OrderID orderId = ((OrderBase)inOrder).getOrderID();
            OutgoingOrderStatusEvent orderStatusEvent = orderStatusEventsByOrderId.getIfPresent(orderId);
            SLF4JLoggerProxy.info(this,
                                  "Order status for {}: {}",
                                  orderId,
                                  orderStatusEvent);
            if(orderStatusEvent == null || orderStatusEvent.getFailed()) {
                String message = orderStatusEvent==null?"none":orderStatusEvent.getErrorMessage();
                SLF4JLoggerProxy.warn(this,
                                      "Unable to submit {}: {}",
                                      orderId,
                                      message);
                throw new SendOrderFailed(message);
            }
        } else {
            SLF4JLoggerProxy.info(this,
                                  "Cannot retrieve order status for {} because it has no order id",
                                  inOrder);
        }
    }
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
    {
        SLF4JLoggerProxy.info(this,
                              "Trade service started");
        orderStatusEventsByOrderId = CacheBuilder.newBuilder().expireAfterAccess(orderStatusTimeout,
                                                                                 TimeUnit.SECONDS).build();
        eventBusService.register(this);
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
     * provides access to event bus services
     */
    @Autowired
    private EventBusService eventBusService;
    /**
     * optional broker selector
     */
    @Autowired(required=false)
    private BrokerSelector brokerSelector;
    /**
     * timeout in seconds for the order status to be retained
     */
    @Value("${metc.trade.order.status.timeout.interval.seconds:10}")
    private long orderStatusTimeout;
    /**
     * caches most recent status update for outgoing orders
     */
    private Cache<OrderID,OutgoingOrderStatusEvent> orderStatusEventsByOrderId;
    /**
     * holds trade message listener subscribers
     */
    private final Set<TradeMessageListener> tradeMessageListeners = Sets.newConcurrentHashSet();
}
