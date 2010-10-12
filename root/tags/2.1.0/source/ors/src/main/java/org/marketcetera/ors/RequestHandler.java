package org.marketcetera.ors;

import java.math.BigDecimal;
import java.util.Date;
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
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.quickfix.IQuickFIXSender;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.FIXConverter;
import org.marketcetera.trade.FIXOrder;
import org.marketcetera.trade.MessageCreationException;
import org.marketcetera.trade.Order;
import org.marketcetera.trade.OrderBase;
import org.marketcetera.trade.OrderCancel;
import org.marketcetera.trade.OrderReplace;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.trade.Originator;
import org.marketcetera.trade.TradeMessage;
import org.marketcetera.trade.UserID;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.quickfix.AnalyzedMessage;
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
import quickfix.field.MsgType;
import quickfix.field.OrdStatus;
import quickfix.field.OrderID;
import quickfix.field.SenderCompID;
import quickfix.field.SendingTime;
import quickfix.field.TargetCompID;
import quickfix.field.Text;

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
    private final OrderFilter mAllowedOrders;
    private final ReplyPersister mPersister;
    private final IQuickFIXSender mSender;
    private final UserManager mUserManager;
    private final IDFactory mIDFactory;
    private final DataDictionary mDataDictionary;


    // CONSTRUCTORS.

    public RequestHandler
        (Brokers brokers,
         Selector selector,
         OrderFilter allowedOrders,
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

    public OrderFilter getAllowedOrders()
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

    private static String getOptFieldStr
        (Message msg,
         int field)
    {
        try {
            return msg.getString(field);
        } catch(FieldNotFound ex) {
            return null;
        }
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

    /**
     * Creates a QuickFIX/J ACK execution report (always of the system
     * FIX version) for the given message, associated with the given
     * broker.
     *
     * @param b The broker.
     * @param qMsg The message, in QuickFIX/J form.
     *
     * @return The report. It may be null if a report cannot be
     * generated for the given message type.
     *
     * @throws CoreException Thrown if there is a problem.
     * @throws FieldNotFound Thrown if there is a QuickFIX/J problem.
     */

    private Message createExecutionReport
        (Broker b,
         Message qMsg)
        throws CoreException,
               FieldNotFound
    {
        // Choose status that matches that of the incoming message.

        char ordStatus;
        String orderID;
        if (FIXMessageUtil.isOrderSingle(qMsg)) {
            ordStatus=OrdStatus.PENDING_NEW;
            orderID=SELF_ORDER_ID;
        } else if (FIXMessageUtil.isCancelReplaceRequest(qMsg)) {
            ordStatus=OrdStatus.PENDING_REPLACE;
            orderID=getOptFieldStr(qMsg,OrderID.FIELD);
            if(orderID==null) {
                orderID=SELF_ORDER_ID;
            }
        } else if (FIXMessageUtil.isCancelRequest(qMsg)) {
            ordStatus=OrdStatus.PENDING_CANCEL;
            orderID=getOptFieldStr(qMsg,OrderID.FIELD);
            if(orderID==null) {
                orderID=SELF_ORDER_ID;
            }
        } else {
            return null;
        }

        // Create execution report.

        FIXMessageFactory messageFactory = getBestMsgFactory(b);
        Message qMsgReply= messageFactory.newExecutionReportEmpty();
        String msgType = MsgType.EXECUTION_REPORT;
        DataDictionary dict=getBestDataDictionary(b);
        if(dict.isMsgField(msgType,OrderID.FIELD)) {
            qMsgReply.setField(new OrderID(orderID));
        }
        if(dict.isMsgField(msgType,ExecID.FIELD)) {
            qMsgReply.setField(new ExecID(getNextExecId().getValue()));
        }
        if(dict.isMsgField(msgType,OrdStatus.FIELD)) {
            qMsgReply.setField(new OrdStatus(ordStatus));
        }
        if(dict.isMsgField(msgType,LastShares.FIELD)) {
            qMsgReply.setField(new LastShares(BigDecimal.ZERO));
        }
        if(dict.isMsgField(msgType,LastPx.FIELD)) {
            qMsgReply.setField(new LastPx(BigDecimal.ZERO));
        }
        if(dict.isMsgField(msgType,CumQty.FIELD)) {
            qMsgReply.setField(new CumQty(BigDecimal.ZERO));
        }
        if(dict.isMsgField(msgType,AvgPx.FIELD)) {
            qMsgReply.setField(new AvgPx(BigDecimal.ZERO));
        }

        // Add all the fields of the incoming message.

        FIXMessageUtil.fillFieldsFromExistingMessage
            (qMsgReply,qMsg,getBestDataDictionary(b),false);
        messageFactory.getMsgAugmentor().executionReportAugment(qMsgReply);

        // Add required header/trailer fields.

        addRequiredFields(qMsgReply);
        return qMsgReply;
    }


    // ReplyHandler.

    @Override
    public void receiveMessage
        (OrderEnvelope msgEnv)
    {
        ThreadedMetric.begin
            ((msgEnv.getOrder() instanceof OrderBase)
             ?((OrderBase)msgEnv.getOrder()).getOrderID()
             :null);
        Messages.RH_RECEIVED_MESSAGE.info(this,msgEnv);
        Order msg=null;
        UserID actorID=null;
        BrokerID bID=null;
        Broker b=null;
        Message qMsg=null;
        Message qMsgToSend=null;
        Message qMsgReply=null;
        boolean responseExpected=false;
        OrderInfo orderInfo=null;
        try {

            // Reject null message envelopes.

            if (msgEnv==null) {
                throw new I18NException(Messages.RH_NULL_MESSAGE_ENVELOPE);
            }

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

            // Ensure broker is available.

            if (!b.getLoggedOn()) {
                throw new I18NException(Messages.RH_UNAVAILABLE_BROKER);
            }

            // Ensure the order is allowed.

            try {
                getAllowedOrders().assertAccepted(qMsg);
            } catch (CoreException ex) {
                throw new I18NException(ex,Messages.RH_ORDER_DISALLOWED);
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

            // Compose ACK execution report (with pending status).

            try {
                qMsgReply=createExecutionReport(b,qMsg);
                if (qMsgReply==null) {
                    Messages.RH_ACK_FAILED_WARN.warn
                        (this,msg,qMsg,b.toString());
                }
            } catch (FieldNotFound ex) {
                throw new I18NException(ex,Messages.RH_ACK_FAILED);
            } catch (CoreException ex) {
                throw new I18NException(ex,Messages.RH_ACK_FAILED);
            }
        } catch (I18NException ex) {
            Messages.RH_MESSAGE_PROCESSING_FAILED.error
                (this,ex,msg,qMsg,qMsgToSend,
                 ObjectUtils.toString(b,ObjectUtils.toString(bID)));
            qMsgReply=createRejection(ex,b,msg);
        } finally {
            if (orderInfo!=null) {
                orderInfo.setResponseExpected(responseExpected);
            }
        }
        ThreadedMetric.event
            ("requestHandler.replyComposed"); //$NON-NLS-1$

        boolean msgAckExpected=false;
        Principals principals;
        try {

        // If the reply could not be created, we are done (a
        // warning/error has already been reported).

        if (qMsgReply==null) {
            return;
        }
        if (SLF4JLoggerProxy.isDebugEnabled(this)) {
            Messages.RH_ANALYZED_MESSAGE.debug
                (this,new AnalyzedMessage
                 (getBestDataDictionary(b),qMsgReply).toString());
        }

        // Convert reply to FIX Agnostic messsage.

            principals=getPersister().getPrincipals(qMsgReply,true);
            msgAckExpected=true;
            ThreadedMetric.event
                ("requestHandler.principalsFetched"); //$NON-NLS-1$
        } finally {
            if (orderInfo!=null) {
                orderInfo.setAckExpected(msgAckExpected);
            }
        }

        TradeMessage reply;
        try {
            reply=FIXConverter.fromQMessage
                (qMsgReply,Originator.Server,bID,
                 principals.getActorID(),principals.getViewerID());
        } catch (MessageCreationException ex) {
            Messages.RH_REPORT_FAILED.error(this,ex,qMsgReply);
            return;
        }
        ThreadedMetric.event
            ("requestHandler.replyConverted"); //$NON-NLS-1$

        // Persist and send reply.
        
        getPersister().persistReply(reply);
        Messages.RH_SENDING_REPLY.info(this,reply);
        ThreadedMetric.event
            ("requestHandler.replyPersisted"); //$NON-NLS-1$
        getUserManager().convertAndSend(reply);
        ThreadedMetric.end(METRIC_CONDITION_RH);
	}
}
