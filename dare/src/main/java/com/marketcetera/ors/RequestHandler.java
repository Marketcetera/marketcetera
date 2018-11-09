package com.marketcetera.ors;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.marketcetera.algo.BrokerAlgo;
import org.marketcetera.algo.BrokerAlgoSpec;
import org.marketcetera.algo.HasBrokerAlgo;
import org.marketcetera.client.EventListener;
import org.marketcetera.client.EventPublisher;
import org.marketcetera.client.brokers.BrokerUnavailable;
import org.marketcetera.client.jms.DataEnvelope;
import org.marketcetera.client.jms.ReceiveOnlyHandler;
import org.marketcetera.core.CoreException;
import org.marketcetera.core.IDFactory;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.Event;
import org.marketcetera.event.TopOfBookEvent;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.core.manager.MarketDataManager;
import org.marketcetera.metrics.ConditionsFactory;
import org.marketcetera.metrics.IsotopeService;
import org.marketcetera.metrics.ThreadedMetric;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.quickfix.QuickFIXSender;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.FIXConverter;
import org.marketcetera.trade.FIXOrder;
import org.marketcetera.trade.Hierarchy;
import org.marketcetera.trade.MessageCreationException;
import org.marketcetera.trade.NewOrReplaceOrder;
import org.marketcetera.trade.Order;
import org.marketcetera.trade.OrderBase;
import org.marketcetera.trade.OrderCancel;
import org.marketcetera.trade.OrderReplace;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.trade.OrderStatus;
import org.marketcetera.trade.Originator;
import org.marketcetera.trade.TradeMessage;
import org.marketcetera.trade.UserID;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.I18NBoundMessage2P;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.beans.factory.annotation.Autowired;

import quickfix.ConfigError;
import quickfix.DataDictionary;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.SessionNotFound;
import quickfix.field.AvgPx;
import quickfix.field.BusinessRejectReason;
import quickfix.field.CumQty;
import quickfix.field.CxlRejResponseTo;
import quickfix.field.ExecID;
import quickfix.field.ExecTransType;
import quickfix.field.LastPx;
import quickfix.field.LastShares;
import quickfix.field.MsgSeqNum;
import quickfix.field.OrdStatus;
import quickfix.field.OrderID;
import quickfix.field.SenderCompID;
import quickfix.field.SendingTime;
import quickfix.field.TargetCompID;
import quickfix.field.Text;

import com.google.common.collect.Sets;
import com.marketcetera.admin.NotAuthorizedException;
import com.marketcetera.admin.service.AuthorizationService;
import com.marketcetera.ors.brokers.Broker;
import com.marketcetera.ors.brokers.BrokerService;
import com.marketcetera.ors.brokers.Selector;
import com.marketcetera.ors.dao.ReportService;
import com.marketcetera.ors.filters.OrderFilter;
import com.marketcetera.ors.info.RequestInfo;
import com.marketcetera.ors.info.RequestInfoImpl;
import com.marketcetera.ors.info.SessionInfo;
import com.marketcetera.ors.outgoingorder.OutgoingMessageService;
import com.marketcetera.ors.security.SimpleUser;

/* $License$ */

/**
 * A handler for incoming trade requests (orders).
 *
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id: RequestHandler.java 17492 2018-04-03 13:57:25Z colin $
 */
@ClassVersion("$Id: RequestHandler.java 17492 2018-04-03 13:57:25Z colin $")
public class RequestHandler 
        implements ReceiveOnlyHandler<DataEnvelope>,EventPublisher
{

    // CLASS DATA.

    private static final String SELF_SENDER_COMP_ID=
        "ORS"; //$NON-NLS-1$
    private static final String SELF_TARGET_COMP_ID=
        "ORS Client"; //$NON-NLS-1$
    private static final String SELF_ORDER_ID=
        "NONE"; //$NON-NLS-1$
    private static final String UNKNOWN_EXEC_ID=
        "ERROR"; //$NON-NLS-1$
    private static final char SOH=
        '\u0001';
    private static final char SOH_REPLACE=
        '|';
    private static final Callable<Boolean> METRIC_CONDITION_RH=
        ConditionsFactory.createSamplingCondition
        (100,"metc.metrics.ors.rh.sampling.interval"); //$NON-NLS-1$


    // INSTANCE DATA.

    private Selector mSelector;
    private final List<OrderFilter> mAllowedOrders = new ArrayList<>();
    private ReplyPersister mPersister;
    private QuickFIXSender mSender;
    private UserManager mUserManager;
    private IDFactory mIDFactory;
    private DataDictionary mDataDictionary;
    // CONSTRUCTORS.
    /**
     * Create a new RequestHandler instance.
     *
     * @throws ConfigError if an error occurs creating the request handler
     */
    public RequestHandler()
            throws ConfigError
    {
        mDataDictionary=new DataDictionary(FIXVersion.FIX_SYSTEM.getDataDictionaryName());
    }
    // INSTANCE METHODS.

    public Selector getSelector()
    {
        return mSelector;
    }
    /**
     * Sets the selector value.
     *
     * @param inSelector a <code>Selector</code> value
     */
    public void setSelector(Selector inSelector)
    {
        mSelector = inSelector;
    }


    /**
     * Sets the allowedOrders value.
     *
     * @param inAllowedOrders a <code>List<OrderFilter></code> value
     */
    public void setAllowedOrders(List<OrderFilter> inAllowedOrders)
    {
        mAllowedOrders.clear();
        if(inAllowedOrders != null) {
            mAllowedOrders.addAll(inAllowedOrders);
        }
    }


    /**
     * Sets the persister value.
     *
     * @param inPersister a <code>ReplyPersister</code> value
     */
    public void setPersister(ReplyPersister inPersister)
    {
        mPersister = inPersister;
    }


    /**
     * Sets the sender value.
     *
     * @param inSender a <code>QuickFIXSender</code> value
     */
    public void setSender(QuickFIXSender inSender)
    {
        mSender = inSender;
    }


    /**
     * Sets the userManager value.
     *
     * @param inUserManager a <code>UserManager</code> value
     */
    public void setUserManager(UserManager inUserManager)
    {
        mUserManager = inUserManager;
    }


    /**
     * Sets the iDFactory value.
     *
     * @param inIDFactory a <code>IDFactory</code> value
     */
    public void setIDFactory(IDFactory inIDFactory)
    {
        mIDFactory = inIDFactory;
    }


    /**
     * Sets the dataDictionary value.
     *
     * @param inDataDictionary a <code>DataDictionary</code> value
     */
    public void setDataDictionary(DataDictionary inDataDictionary)
    {
        mDataDictionary = inDataDictionary;
    }


    public List<OrderFilter> getAllowedOrders()
    {
        return mAllowedOrders;
    }

    public ReplyPersister getPersister()
    {
        return mPersister;
    }

    public UserManager getUserManager()
    {
        return mUserManager;
    }

    public QuickFIXSender getSender()
    {
        return mSender;
    }

    public IDFactory getIDFactory()
    {
        return mIDFactory;
    }

    public FIXMessageFactory getMsgFactory()
    {
        return FIXVersion.FIX_SYSTEM.getMessageFactory();
    }

    public DataDictionary getDataDictionary()
    {
        return mDataDictionary;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.EventPublisher#addEventListener(org.marketcetera.client.EventListener)
     */
    @Override
    public void addEventListener(EventListener inEventListener)
    {
        eventListeners.add(inEventListener);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.client.EventPublisher#removeEventListener(org.marketcetera.client.EventListener)
     */
    @Override
    public void removeEventListener(EventListener inEventListener)
    {
        eventListeners.remove(inEventListener);
    }
    /**
     * Returns the best message factory available: this is either the
     * system factory, if the given broker is null, or the broker's
     * factory otherwise.
     *
     * @param b The broker.
     *
     * @return The factory.
     */

    private FIXMessageFactory getBestMsgFactory
        (Broker b)
    {
        if (b==null) {
            return getMsgFactory();
        }
        return b.getFIXMessageFactory();
    }

    /**
     * Returns the best data dictionary available: this is either the
     * system dictionary, if the given broker is null, or the broker's
     * dictionary otherwise.
     *
     * @param b The broker. It may be null.
     *
     * @return The data dictionary.
     */

    private DataDictionary getBestDataDictionary
        (Broker b)
    {
        if (b==null) {
            return getDataDictionary();
        }
        return b.getDataDictionary();
    }

    private ExecID getNextExecId()
        throws CoreException
    {
        return new ExecID(getIDFactory().getNext());
    }
    private static void addRequiredFields
        (Message msg)
    {
        msg.getHeader().setField(new MsgSeqNum(0));
        msg.getHeader().setField(new SenderCompID(SELF_SENDER_COMP_ID));
        msg.getHeader().setField(new TargetCompID(SELF_TARGET_COMP_ID));
        msg.getHeader().setField(new SendingTime(new Date()));

        // This indirectly adds body length and checksum.
        msg.toString();
    }
    /**
     * Creates a QuickFIX/J rejection if processing of the given
     * message, associated with the given broker, failed with the
     * given exception.
     *
     * @param inException The exception.
     * @param inBroker The broker. It may be null.
     * @param inOrder The message, in FIX Agnostic form. It may be null.
     *
     * @return The rejection.
     */
    private Message createRejection(I18NException inException,
                                    Broker inBroker,
                                    Order inOrder)
    {
        // Special handling of unsupported incoming messages.
        if(inException.getI18NBoundMessage() == Messages.RH_UNSUPPORTED_MESSAGE) {
            return getBestMsgFactory(inBroker).newBusinessMessageReject(inOrder.getClass().getName(),
                                                                        BusinessRejectReason.UNSUPPORTED_MESSAGE_TYPE,
                                                                        inException.getLocalizedDetail().replace(SOH,
                                                                                                                 SOH_REPLACE));
        }
        // Attempt conversion of incoming message into a QuickFIX/J message.
        Message qMsg = null;
        try {
            qMsg = FIXConverter.toQMessage(getBestMsgFactory(inBroker),
                                           getBestDataDictionary(inBroker),
                                           inOrder);
        } catch (I18NException ex2) {
            Messages.RH_REJ_CONVERSION_FAILED.warn(this,
                                                   ex2,
                                                   inOrder);
        }
        // Create basic rejection shell.
        Message qMsgReply;
        boolean orderCancelType = (FIXMessageUtil.isCancelRequest(qMsg) || FIXMessageUtil.isCancelReplaceRequest(qMsg));
        if(orderCancelType) {
            qMsgReply = getBestMsgFactory(inBroker).newOrderCancelRejectEmpty();
            char reason;
            if(FIXMessageUtil.isCancelRequest(qMsg)) {
                reason = CxlRejResponseTo.ORDER_CANCEL_REQUEST;
            } else {
                reason = CxlRejResponseTo.ORDER_CANCEL_REPLACE_REQUEST;
            }
            qMsgReply.setField(new CxlRejResponseTo(reason));
            // set the order status to the last know status of this order
            quickfix.field.OrdStatus ordStatus = null;
            try {
                if(qMsg.isSetField(quickfix.field.OrigClOrdID.FIELD)) {
                    String origClOrdId = qMsg.getString(quickfix.field.OrigClOrdID.FIELD);
                    OrderStatus orderStatus = reportService.getOrderStatusForOrderChain(new org.marketcetera.trade.OrderID(origClOrdId));
                    SLF4JLoggerProxy.debug(this,
                                           "Setting order status for {} to {}",
                                           qMsgReply,
                                           orderStatus);
                    ordStatus = new OrdStatus(orderStatus.getFIXValue());
                }
            } catch (Exception e) {
                Messages.RH_UNABLE_TO_DETERMINE_ORDER_STATUS.warn(this,
                                                                  e,
                                                                  qMsgReply,
                                                                  ExceptionUtils.getRootCauseMessage(e));
            }
            if(ordStatus == null) {
                // this is a quandry. we have to set the order status to the current status, but we've been
                //  unable to figure out what the proper status is. Set it to something appropriate but open
                //  so a follow-up message can be sent, if desired.
                if(FIXMessageUtil.isCancelRequest(qMsg)) {
                    ordStatus = new quickfix.field.OrdStatus(quickfix.field.OrdStatus.PENDING_CANCEL);
                } else if(FIXMessageUtil.isCancelReplaceRequest(qMsg)) {
                    qMsgReply.setField(new OrdStatus(OrdStatus.PENDING_REPLACE));
                } else {
                    // this is a code defect. we decided above that this order is either a cancel or a cancel replace
                    //  but now we insist it's neither
                    ordStatus = new quickfix.field.OrdStatus(quickfix.field.OrdStatus.SUSPENDED);
                }
            }
            qMsgReply.setField(ordStatus);
        } else {
            qMsgReply = getBestMsgFactory(inBroker).newExecutionReportEmpty();
            try {
                qMsgReply.setField(getNextExecId());
            } catch (CoreException ex2) {
                Messages.RH_REJ_ID_GENERATION_FAILED.warn(this,
                                                          ex2);
                qMsgReply.setField(new ExecID(UNKNOWN_EXEC_ID));
            }
            qMsgReply.setField(new AvgPx(0));
            qMsgReply.setField(new CumQty(0));
            qMsgReply.setField(new LastShares(0));
            qMsgReply.setField(new LastPx(0));
            qMsgReply.setField(new ExecTransType(ExecTransType.NEW));
            qMsgReply.setField(new OrdStatus(OrdStatus.REJECTED));
        }
        qMsgReply.setString(Text.FIELD,
                            inException.getLocalizedDetail().replace(SOH,
                                                                     SOH_REPLACE));
        // Add all the fields of the incoming message.
        if(qMsg != null) {
            FIXMessageUtil.fillFieldsFromExistingMessage(qMsgReply,
                                                         qMsg,
                                                         getBestDataDictionary(inBroker),
                                                         false);
        }
        // Add an order ID, if there was none from the incoming message.
        if(!qMsgReply.isSetField(OrderID.FIELD)) {
            qMsgReply.setField(new OrderID(SELF_ORDER_ID));
        }
        // Augment rejection.
        if(!orderCancelType) {
            try {
                getBestMsgFactory(inBroker).getMsgAugmentor().executionReportAugment(qMsgReply);
            } catch (FieldNotFound ex2) {
                Messages.RH_REJ_AUGMENTATION_FAILED.warn(this,
                                                         ex2,
                                                         qMsgReply);
            }
        }
        // Add required header/trailer fields.
        addRequiredFields(qMsgReply);
        return qMsgReply;
    }
    // ReplyHandler.
    @Override
    public void receiveMessage(DataEnvelope inDataEnvelope)
    {
        Order order=null;
        SimpleUser actor = null;
        BrokerID brokerId=null;
        Broker broker=null;
        Message qMsg=null;
        Message qMsgToSend=null;
        boolean sendOrder = true;
        try {
            // Reject null message envelopes.
            if(inDataEnvelope==null) {
                throw new I18NException(Messages.RH_NULL_MESSAGE_ENVELOPE);
            }
            if(inDataEnvelope.getEvent() != null) {
                handleEvent(inDataEnvelope.getEvent());
                return;
            }
            Messages.RH_RECEIVED_MESSAGE.info(this,inDataEnvelope);
            ThreadedMetric.begin((inDataEnvelope.getOrder() instanceof OrderBase) ? ((OrderBase)inDataEnvelope.getOrder()).getOrderID() : null);
            // Reject null messages.
            order = inDataEnvelope.getOrder();
            // Reject invalid sessions.
            SessionInfo sessionInfo = getUserManager().getSessionInfo(inDataEnvelope.getSessionId());
            if(sessionInfo == null) {
                throw new I18NException(new I18NBoundMessage1P(Messages.RH_SESSION_EXPIRED,
                                                               inDataEnvelope.getSessionId()));
            }
            actor = (SimpleUser)sessionInfo.getValue(SessionInfo.ACTOR);
            ThreadedMetric.begin((inDataEnvelope.getOrder() instanceof OrderBase) ? ((OrderBase)inDataEnvelope.getOrder()).getOrderID() : null);
            // Reject null messages.
            if(order==null) {
                throw new I18NException(Messages.RH_NULL_MESSAGE);
            }
            SimpleUser user = (SimpleUser)sessionInfo.getValue(SessionInfo.ACTOR);
            try {
                authzService.authorize(user.getName(),
                                       TradingPermissions.SendOrderAction.name());
            } catch (NotAuthorizedException e) {
                throw new I18NException(e);
            }
            RequestInfo requestInfo = new RequestInfoImpl(sessionInfo);
            ThreadedMetric.event("requestHandler.sessionInfoObtained"); //$NON-NLS-1$
            // Reject messages of unsupported types.
            if(!(order instanceof OrderSingle) && !(order instanceof OrderCancel) && !(order instanceof OrderReplace) && !(order instanceof FIXOrder)) {
                throw new I18NException(Messages.RH_UNSUPPORTED_MESSAGE);
            }
            // Identify broker.
            brokerId = order.getBrokerID();
            if(brokerId == null) {
                Selector selector = getSelector();
                if(selector == null) {
                    throw new I18NException(new I18NBoundMessage1P(Messages.RH_NO_SELECTOR,
                                                                   inDataEnvelope));
                }
                brokerId = getSelector().chooseBroker(order);
            }
            if(brokerId == null) {
                throw new I18NException(Messages.RH_UNKNOWN_BROKER);
            }
            requestInfo.setValue(RequestInfo.BROKER_ID,
                                 brokerId);
            // Ensure broker ID maps to existing broker.
            broker = brokerService.getBroker(brokerId);
            if(broker == null) {
                throw new I18NException(Messages.RH_UNKNOWN_BROKER_ID);
            }
            // check to see if the selected broker is a virtual broker
            if(broker.getMappedBrokerId() != null) {
                SLF4JLoggerProxy.debug(this,
                                       "Selected broker id {} is virtual and maps to {}",
                                       brokerId,
                                       broker.getMappedBrokerId());
                brokerId = broker.getMappedBrokerId();
                broker = brokerService.getBroker(brokerId);
                if(broker == null) {
                    throw new I18NException(Messages.RH_UNKNOWN_BROKER_ID);
                }
            }
            requestInfo.setValue(RequestInfo.BROKER,
                                 broker);
            requestInfo.setValue(RequestInfo.FIX_MESSAGE_FACTORY,
                                 broker.getFIXMessageFactory());
            ThreadedMetric.event("requestHandler.brokerSelected"); //$NON-NLS-1$
            // if algos are specified, apply them (before transforming to FIX)
            if(order instanceof HasBrokerAlgo) {
                BrokerAlgo brokerAlgo = ((HasBrokerAlgo)order).getBrokerAlgo();
                if(brokerAlgo != null) {
                    if(order instanceof NewOrReplaceOrder) {
                        Set<BrokerAlgoSpec> cannonicalAlgoSpecs = broker.getSpringBroker().getBrokerAlgoSpecs();
                        BrokerAlgoSpec cannonicalAlgoSpec = null;
                        for(BrokerAlgoSpec cannonicalAlgoSpecCandidate : cannonicalAlgoSpecs) {
                            if(cannonicalAlgoSpecCandidate.equals(brokerAlgo.getAlgoSpec())) {
                                cannonicalAlgoSpec = cannonicalAlgoSpecCandidate;
                                break;
                            }
                        }
                        if(cannonicalAlgoSpec == null) {
                            // TODO message
                            throw new IllegalArgumentException(order + " specified a broker algo " + brokerAlgo.getAlgoSpec().getName() + " but no algo matches in the broker config");
                        }
                        brokerAlgo.mapValidatorsFrom(cannonicalAlgoSpec);
                        brokerAlgo.validate();
                        brokerAlgo.applyTo((NewOrReplaceOrder)order);
                    }
                }
            }
            // optionally reprice
            if(order instanceof NewOrReplaceOrder) {
                NewOrReplaceOrder newOrReplaceOrder = (NewOrReplaceOrder)order;
                if(newOrReplaceOrder.getPegToMidpoint()) {
                    try {
                        if(marketDataManager == null) {
                            throw new IllegalArgumentException("Market data nexus unavailable");
                        }
                        Event marketData = marketDataManager.requestMarketDataSnapshot(newOrReplaceOrder.getInstrument(),
                                                                                       Content.TOP_OF_BOOK,
                                                                                       null);
                        if(marketData == null) {
                            throw new IllegalArgumentException("No market data available for " + newOrReplaceOrder.getInstrument().getFullSymbol());
                        }
                        TopOfBookEvent topOfBook = (TopOfBookEvent)marketData;
                        BidEvent bid = topOfBook.getBid();
                        AskEvent ask = topOfBook.getAsk();
                        if(bid == null || ask == null) {
                            throw new IllegalArgumentException("Insufficient liquidity to peg-to-midpoint for " + newOrReplaceOrder.getInstrument().getFullSymbol());
                        }
                        BigDecimal totalPrice = bid.getPrice().add(ask.getPrice());
                        BigDecimal newPrice = totalPrice.divide(new BigDecimal(2)).setScale(6,RoundingMode.HALF_UP);
                        newOrReplaceOrder.setPrice(newPrice);
                        Messages.RH_REPRICING.info(this,
                                                   newOrReplaceOrder.getOrderID(),
                                                   newPrice);
                    } catch (Exception e) {
                        String cause = PlatformServices.getMessage(e);
                        Messages.RH_PEG_TO_MIDPOINT_FAILED.warn(this,
                                                                e,
                                                                newOrReplaceOrder.getOrderID(),
                                                                cause);
                        throw new I18NException(new I18NBoundMessage2P(Messages.RH_PEG_TO_MIDPOINT_FAILED,
                                                                       newOrReplaceOrder.getOrderID(),
                                                                       cause));
                    }
                }
            }
            // Convert to a QuickFIX/J message.
            try {
                qMsg = FIXConverter.toQMessage(broker.getFIXMessageFactory(),
                                               broker.getDataDictionary(),
                                               order);
            } catch (I18NException ex) {
                throw new I18NException(ex,Messages.RH_CONVERSION_FAILED);
            }
            outgoingMessageService.cacheMessageOwner(qMsg,
                                                     actor.getUserID());
            broker.logMessage(qMsg);
            ThreadedMetric.event("requestHandler.orderConverted"); //$NON-NLS-1$
            // Ensure broker is allowed for this user
            if(!broker.getSpringBroker().isUserAllowed(actor.getName())) {
                throw new I18NException(Messages.RH_UNKNOWN_BROKER_ID);
            }
            // Ensure broker is available.
            if(!broker.getLoggedOn()) {
                throw new BrokerUnavailable(Messages.RH_UNAVAILABLE_BROKER);
            }
            // Ensure the order is allowed.
            if(getAllowedOrders() != null) {
                for(OrderFilter orderFilter : getAllowedOrders()) {
                    final SimpleUser actorToUse = actor;
                    if(!orderFilter.isAccepted(new OrderFilter.MessageInfo() {
                        @Override
                        public SimpleUser getUser()
                        {
                            return actorToUse;
                        }
                    },qMsg)) {
                        throw new I18NException(Messages.RH_ORDER_DISALLOWED);
                    }
                }
            }
            ThreadedMetric.event("requestHandler.orderAllowed"); //$NON-NLS-1$
            // Apply message modifiers.
            if(broker.getModifiers()!=null) {
                requestInfo.setValue(RequestInfo.CURRENT_MESSAGE,
                                     qMsg);
                try {
                    broker.getModifiers().modifyMessage(requestInfo);
                } catch (OrderIntercepted e) {
                    SLF4JLoggerProxy.info(this,
                                          "{} intercepted",
                                          inDataEnvelope);
                    sendOrder = false;
                } catch (I18NException ex) {
                    throw new I18NException(ex,Messages.RH_MODIFICATION_FAILED);
                }
                qMsg=requestInfo.getValueIfInstanceOf
                    (RequestInfo.CURRENT_MESSAGE,Message.class);
            }
            ThreadedMetric.event("requestHandler.modifiersApplied"); //$NON-NLS-1$
            // Apply order routing.
            if(broker.getRoutes()!=null) {
                try {
                    broker.getRoutes().modifyMessage(qMsg,
                                                     broker.getFIXMessageAugmentor());
                } catch (OrderIntercepted e) {
                    SLF4JLoggerProxy.info(this,
                                          "{} intercepted",
                                          inDataEnvelope);
                    sendOrder = false;
                } catch (I18NException ex) {
                    throw new I18NException(ex,Messages.RH_ROUTING_FAILED);
                }
            }
            ThreadedMetric.event("requestHandler.orderRoutingApplied"); //$NON-NLS-1$
            // Apply pre-sending message modifiers.
            if(broker.getPreSendModifiers()!=null) {
                qMsgToSend=(Message)qMsg.clone();
                requestInfo.setValue(RequestInfo.CURRENT_MESSAGE,qMsgToSend);
                try {
                    try {
                        broker.getPreSendModifiers().modifyMessage(requestInfo);
                    } catch (OrderIntercepted e) {
                        SLF4JLoggerProxy.info(this,
                                              "{} intercepted",
                                              inDataEnvelope);
                        sendOrder = false;
                    } catch (I18NException ex) {
                        throw new I18NException(ex,Messages.RH_PRE_SEND_MODIFICATION_FAILED);
                    }
                    qMsgToSend=requestInfo.getValueIfInstanceOf(RequestInfo.CURRENT_MESSAGE,
                                                                Message.class);
                } finally {
                    requestInfo.setValue(RequestInfo.CURRENT_MESSAGE,qMsg);
                }
            } else {
                qMsgToSend=qMsg;
            }
            ThreadedMetric.event("requestHandler.preSendModifiersApplied"); //$NON-NLS-1$
            // Send message to QuickFIX/J.
            try {
                if(sendOrder) {
                    isotopeService.remove(qMsgToSend);
                    getSender().sendToTarget(qMsgToSend,
                                             broker.getSessionID());
                }
            } catch (SessionNotFound ex) {
                throw new I18NException(ex,Messages.RH_UNAVAILABLE_BROKER);
            }
            ThreadedMetric.event("requestHandler.orderSent"); //$NON-NLS-1$
            try {
                if(sendOrder) {
                    outgoingMessageService.recordOutgoingMessage(qMsgToSend,
                                                                 broker.getSessionID(),
                                                                 brokerId,
                                                                 actor);
                }
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(this,
                                      e);
            }
            ThreadedMetric.event("requestHandler.orderSaved"); //$NON-NLS-1$
        } catch (I18NException ex) {
            Messages.RH_MESSAGE_PROCESSING_FAILED.error(this,ex,order,qMsg,qMsgToSend,ObjectUtils.toString(broker,ObjectUtils.toString(brokerId)));
            Message qMsgReply = createRejection(ex,
                                                broker,
                                                order);
            TradeMessage reply;
            try {
                UserID actorToUse;
                if(actor == null) {
                    // actor will be null if the session is invalid
                    actorToUse = null;
                } else {
                    actorToUse = actor.getUserID();
                }
                reply = FIXConverter.fromQMessage(qMsgReply,
                                                  Originator.Server,
                                                  brokerId,
                                                  Hierarchy.Flat,
                                                  actorToUse,
                                                  actorToUse);
            } catch (MessageCreationException ex2) {
                Messages.RH_REPORT_FAILED.error(this,
                                                ex2,
                                                qMsgReply);
                return;
            }
            getPersister().persistReply(reply);
            Messages.RH_SENDING_REPLY.info(this,
                                           reply);
            getUserManager().convertAndSend(reply);
            throw ex;
        } catch (Exception e) {
            SLF4JLoggerProxy.warn(this,
                                  e);
        }
        ThreadedMetric.end(METRIC_CONDITION_RH);
    }
    /**
     * Get the brokerService value.
     *
     * @return a <code>BrokerService</code> value
     */
    public BrokerService getBrokerService()
    {
        return brokerService;
    }
    /**
     * Sets the brokerService value.
     *
     * @param inBrokerService a <code>BrokerService</code> value
     */
    public void setBrokerService(BrokerService inBrokerService)
    {
        brokerService = inBrokerService;
    }
    /**
     * Handle incoming events.
     *
     * @param inEvent an <code>Event</code> value
     */
    private void handleEvent(Event inEvent)
    {
        for(EventListener eventListener : eventListeners) {
            try {
                eventListener.receiveEvent(inEvent);
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(this,
                                      e);
            }
        }
    }
    /**
     * provides access to broker services
     */
    @Autowired
    private BrokerService brokerService;
    /**
     * provides access to isotope services
     */
    @Autowired
    private IsotopeService isotopeService;
    /**
     * provides access to market data services (optionally required)
     */
    @Autowired(required=false)
    private MarketDataManager marketDataManager;
    /**
     * provides access to report services
     */
    @Autowired
    private ReportService reportService;
    /**
     * provides access to authorization services
     */
    @Autowired
    private AuthorizationService authzService;
    /**
     * provides access to outgoing message services
     */
    @Autowired
    private OutgoingMessageService outgoingMessageService;
    /**
     * holds subscribers to incoming events
     */
    private final Set<EventListener> eventListeners = Sets.newConcurrentHashSet();
}
