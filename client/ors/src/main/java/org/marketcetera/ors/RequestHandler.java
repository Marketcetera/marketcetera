package org.marketcetera.ors;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.lang.ObjectUtils;
import org.marketcetera.client.jms.OrderEnvelope;
import org.marketcetera.client.jms.ReceiveOnlyHandler;
import org.marketcetera.core.CoreException;
import org.marketcetera.core.IDFactory;
import org.marketcetera.metrics.ConditionsFactory;
import org.marketcetera.metrics.ThreadedMetric;
import org.marketcetera.ors.brokers.Broker;
import org.marketcetera.ors.brokers.Brokers;
import org.marketcetera.ors.brokers.Selector;
import org.marketcetera.ors.filters.OrderFilter;
import org.marketcetera.ors.info.RequestInfo;
import org.marketcetera.ors.info.RequestInfoImpl;
import org.marketcetera.ors.info.SessionInfo;
import org.marketcetera.ors.security.SimpleUser;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.quickfix.IQuickFIXSender;
import org.marketcetera.trade.*;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.misc.ClassVersion;

import quickfix.*;
import quickfix.field.*;
import quickfix.field.OrderID;

/**
 * A handler for incoming trade requests (orders).
 *
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class RequestHandler 
        implements ReceiveOnlyHandler<OrderEnvelope>
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

    private final Brokers mBrokers;
    private final Selector mSelector;
    private final List<OrderFilter> mAllowedOrders;
    private final ReplyPersister mPersister;
    private final IQuickFIXSender mSender;
    private final UserManager mUserManager;
    private final IDFactory mIDFactory;
    private final DataDictionary mDataDictionary;


    // CONSTRUCTORS.

    public RequestHandler
        (Brokers brokers,
         Selector selector,
         List<OrderFilter> allowedOrders,
         ReplyPersister persister,
         IQuickFIXSender sender,
         UserManager userManager,
         IDFactory idFactory)
        throws ConfigError
    {
        mBrokers=brokers;
        mSelector=selector;
        mAllowedOrders=allowedOrders;
        mPersister=persister;
        mSender=sender;
        mUserManager=userManager;
        mIDFactory=idFactory;
        mDataDictionary=new DataDictionary
            (FIXVersion.FIX_SYSTEM.getDataDictionaryURL());
    }


    // INSTANCE METHODS.

    public Brokers getBrokers()
    {
        return mBrokers;
    }

    public Selector getSelector()
    {
        return mSelector;
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

    public IQuickFIXSender getSender()
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
     * @param ex The exception.
     * @param b The broker. It may be null.
     * @param msg The message, in FIX Agnostic form. It may be null.
     *
     * @return The rejection.
     */

    private Message createRejection
        (I18NException ex,
         Broker b,
         Order msg)
    {
        // Special handling of unsupported incoming messages.

        if (ex.getI18NBoundMessage()==Messages.RH_UNSUPPORTED_MESSAGE) {
            return getBestMsgFactory(b).newBusinessMessageReject
                (msg.getClass().getName(),
                 BusinessRejectReason.UNSUPPORTED_MESSAGE_TYPE,
                 ex.getLocalizedDetail().replace(SOH,SOH_REPLACE));
        }

        // Attempt conversion of incoming message into a QuickFIX/J
        // message.

        Message qMsg=null;
        try {
            qMsg=FIXConverter.toQMessage
                (getBestMsgFactory(b),getBestDataDictionary(b),msg);
        } catch (I18NException ex2) {
            Messages.RH_REJ_CONVERSION_FAILED.warn(this,ex2,msg);
        }

        // Create basic rejection shell.

        Message qMsgReply;
        boolean orderCancelType=
            (FIXMessageUtil.isCancelRequest(qMsg) ||
             FIXMessageUtil.isCancelReplaceRequest(qMsg));
        if (orderCancelType) {
            qMsgReply=getBestMsgFactory(b).newOrderCancelRejectEmpty();
            char reason;
            if (FIXMessageUtil.isCancelRequest(qMsg)) {
                reason=CxlRejResponseTo.ORDER_CANCEL_REQUEST;
            } else {
                reason=CxlRejResponseTo.ORDER_CANCEL_REPLACE_REQUEST;
            }
            qMsgReply.setField(new CxlRejResponseTo(reason));
        } else {
            qMsgReply=getBestMsgFactory(b).newExecutionReportEmpty();
            try {
                qMsgReply.setField(getNextExecId());
            } catch (CoreException ex2) {
                Messages.RH_REJ_ID_GENERATION_FAILED.warn(this,ex2);
                qMsgReply.setField(new ExecID(UNKNOWN_EXEC_ID));
            }
            qMsgReply.setField(new AvgPx(0));
            qMsgReply.setField(new CumQty(0));
            qMsgReply.setField(new LastShares(0));
            qMsgReply.setField(new LastPx(0));
            qMsgReply.setField(new ExecTransType(ExecTransType.STATUS));
        }
        qMsgReply.setField(new OrdStatus(OrdStatus.REJECTED));
        qMsgReply.setString(Text.FIELD,ex.getLocalizedDetail().replace
                            (SOH,SOH_REPLACE));

        // Add all the fields of the incoming message.

        if (qMsg!=null) {
            FIXMessageUtil.fillFieldsFromExistingMessage
                (qMsgReply,qMsg,getBestDataDictionary(b),false);
        }

        // Add an order ID, if there was none from the incoming message.

        if (!qMsgReply.isSetField(OrderID.FIELD)) {
            qMsgReply.setField(new OrderID(SELF_ORDER_ID));
        }

        // Augment rejection.

        if (!orderCancelType) {
            try {
                getBestMsgFactory(b).getMsgAugmentor().executionReportAugment
                    (qMsgReply);
            } catch (FieldNotFound ex2) {
                Messages.RH_REJ_AUGMENTATION_FAILED.warn(this,ex2,qMsgReply);
            }
        }

        // Add required header/trailer fields.

        addRequiredFields(qMsgReply);
        return qMsgReply;
    }
    // ReplyHandler.

    @Override
    public void receiveMessage
        (OrderEnvelope msgEnv)
    {
        Messages.RH_RECEIVED_MESSAGE.info(this,msgEnv);
        Order msg=null;
        UserID actorID=null;
        BrokerID bID=null;
        Broker b=null;
        Message qMsg=null;
        Message qMsgToSend=null;
        boolean responseExpected=false;
        OrderInfo orderInfo=null;
        try {
            // Reject null message envelopes.
            if (msgEnv==null) {
                throw new I18NException(Messages.RH_NULL_MESSAGE_ENVELOPE);
            }
            ThreadedMetric.begin((msgEnv.getOrder() instanceof OrderBase) ? ((OrderBase)msgEnv.getOrder()).getOrderID() : null);
            // Reject null messages.

            msg=msgEnv.getOrder();
            if (msg==null) {
                throw new I18NException(Messages.RH_NULL_MESSAGE);
            }

            // Reject invalid sessions.

            SessionInfo sessionInfo=
                getUserManager().getSessionInfo(msgEnv.getSessionId());
            if (sessionInfo==null) {
                throw new I18NException
                    (new I18NBoundMessage1P
                     (Messages.RH_SESSION_EXPIRED,msgEnv.getSessionId()));
            }
            actorID=(UserID)sessionInfo.getValue(SessionInfo.ACTOR_ID);
            RequestInfo requestInfo=new RequestInfoImpl(sessionInfo);
            ThreadedMetric.event
                ("requestHandler.sessionInfoObtained"); //$NON-NLS-1$

            // Reject messages of unsupported types.

            if (!(msg instanceof OrderSingle) &&
                !(msg instanceof OrderCancel) &&                
                !(msg instanceof OrderReplace) &&                
                !(msg instanceof FIXOrder)) {
                throw new I18NException(Messages.RH_UNSUPPORTED_MESSAGE);
            }
            Order oMsg=(Order)msg;

            // Identify broker.

            bID=oMsg.getBrokerID();
            if (bID==null) {
                bID=getSelector().chooseBroker(oMsg);
            }
            if (bID==null) {
                throw new I18NException(Messages.RH_UNKNOWN_BROKER);
            }
            requestInfo.setValue
                (RequestInfo.BROKER_ID,bID);
            
            // Ensure broker ID maps to existing broker.

            b=getBrokers().getBroker(bID);
            if (b==null) {
                throw new I18NException(Messages.RH_UNKNOWN_BROKER_ID);
            }
            requestInfo.setValue
                (RequestInfo.BROKER,b);
            requestInfo.setValue
                (RequestInfo.FIX_MESSAGE_FACTORY,b.getFIXMessageFactory());
            ThreadedMetric.event
                ("requestHandler.brokerSelected"); //$NON-NLS-1$

            // Convert to a QuickFIX/J message.

            try {
                qMsg=FIXConverter.toQMessage
                    (b.getFIXMessageFactory(),b.getDataDictionary(),oMsg);
            } catch (I18NException ex) {
                throw new I18NException(ex,Messages.RH_CONVERSION_FAILED);
            }
            orderInfo=getPersister().addOutgoingOrder(qMsg,actorID);
            b.logMessage(qMsg);
            ThreadedMetric.event
                ("requestHandler.orderConverted"); //$NON-NLS-1$
            // Ensure broker is allowed for this user
            final SimpleUser actor = (SimpleUser)sessionInfo.getValue(SessionInfo.ACTOR);
            if(!b.getSpringBroker().isUserAllowed(actor.getName())) {
                throw new I18NException(Messages.RH_UNKNOWN_BROKER_ID);
            }
            // Ensure broker is available.

            if (!b.getLoggedOn()) {
                throw new I18NException(Messages.RH_UNAVAILABLE_BROKER);
            }

            // Ensure the order is allowed.
            if(getAllowedOrders() != null) {
                for(OrderFilter orderFilter : getAllowedOrders()) {
                    if(!orderFilter.isAccepted(new OrderFilter.MessageInfo() {
                        @Override
                        public SimpleUser getUser()
                        {
                            return actor;
                        }
                    },qMsg)) {
                        throw new I18NException(Messages.RH_ORDER_DISALLOWED);
                    }
                }
            }
            ThreadedMetric.event
                ("requestHandler.orderAllowed"); //$NON-NLS-1$

            // Apply message modifiers.
            
            if (b.getModifiers()!=null) {
                requestInfo.setValue(RequestInfo.CURRENT_MESSAGE,qMsg);
                try {
                    b.getModifiers().modifyMessage(requestInfo);
                } catch (I18NException ex) {
                    throw new I18NException(ex,Messages.RH_MODIFICATION_FAILED);
                }
                qMsg=requestInfo.getValueIfInstanceOf
                    (RequestInfo.CURRENT_MESSAGE,Message.class);
            }
            ThreadedMetric.event
                ("requestHandler.modifiersApplied"); //$NON-NLS-1$

            // Apply order routing.

            if (b.getRoutes()!=null) {
                try {
                    b.getRoutes().modifyMessage
                        (qMsg,b.getFIXMessageAugmentor());
                } catch (I18NException ex) {
                    throw new I18NException(ex,Messages.RH_ROUTING_FAILED);
                }
            }
            ThreadedMetric.event
                ("requestHandler.orderRoutingApplied"); //$NON-NLS-1$

            // Apply pre-sending message modifiers.

            if (b.getPreSendModifiers()!=null) {
                qMsgToSend=(Message)qMsg.clone();
                requestInfo.setValue(RequestInfo.CURRENT_MESSAGE,qMsgToSend);
                try {
                    try {
                        b.getPreSendModifiers().modifyMessage(requestInfo);
                    } catch (I18NException ex) {
                        throw new I18NException
                            (ex,Messages.RH_PRE_SEND_MODIFICATION_FAILED);
                    }
                    qMsgToSend=requestInfo.getValueIfInstanceOf
                        (RequestInfo.CURRENT_MESSAGE,Message.class);
                } finally {
                    requestInfo.setValue(RequestInfo.CURRENT_MESSAGE,qMsg);
                }
            } else {
                qMsgToSend=qMsg;
            }
            ThreadedMetric.event
                ("requestHandler.preSendModifiersApplied"); //$NON-NLS-1$

            // Send message to QuickFIX/J.

            try {
                getSender().sendToTarget(qMsgToSend,b.getSessionID());
            } catch (SessionNotFound ex) {
                throw new I18NException(ex,Messages.RH_UNAVAILABLE_BROKER);
            }
            responseExpected=true;
            ThreadedMetric.event
                ("requestHandler.orderSent"); //$NON-NLS-1$
        } catch (I18NException ex) {
            Messages.RH_MESSAGE_PROCESSING_FAILED.error(this,ex,msg,qMsg,qMsgToSend,ObjectUtils.toString(b,ObjectUtils.toString(bID)));
            Message qMsgReply = createRejection(ex,
                                                b,
                                                msg);
            Principals principals=getPersister().getPrincipals(qMsgReply,
                                                               true);
            TradeMessage reply;
            try {
                reply=FIXConverter.fromQMessage(qMsgReply,
                                                Originator.Server,
                                                bID,
                                                principals.getActorID(),
                                                principals.getViewerID());
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
        } finally {
            if (orderInfo!=null) {
                orderInfo.setResponseExpected(responseExpected);
            }
        }
        ThreadedMetric.end(METRIC_CONDITION_RH);
	}
}
