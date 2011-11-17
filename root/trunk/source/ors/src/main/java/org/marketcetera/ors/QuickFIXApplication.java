package org.marketcetera.ors;

import java.util.Collection;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicLong;

import org.marketcetera.ors.brokers.Broker;
import org.marketcetera.ors.brokers.Brokers;
import org.marketcetera.ors.config.LogonAction;
import org.marketcetera.ors.config.LogoutAction;
import org.marketcetera.ors.filters.MessageFilter;
import org.marketcetera.ors.info.*;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.quickfix.IQuickFIXSender;
import org.marketcetera.trade.FIXConverter;
import org.marketcetera.trade.MessageCreationException;
import org.marketcetera.trade.Originator;
import org.marketcetera.trade.TradeMessage;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.jms.core.JmsOperations;

import quickfix.*;
import quickfix.field.*;

/**
 * The QuickFIX/J intermediary, intercepting messages from/to the
 * QuickFIX/J counterparties and the ORS.
 *
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class QuickFIXApplication
    implements Application
{

    // CLASS DATA

    private static final String HEARTBEAT_CATEGORY=
        QuickFIXApplication.class.getName()+".HEARTBEATS"; //$NON-NLS-1$


    // INSTANCE DATA.

    private final SystemInfo mSystemInfo;
    private final Brokers mBrokers;
    private final MessageFilter mSupportedMessages;
    private final ReplyPersister mPersister;
    private final IQuickFIXSender mSender;
    private final UserManager mUserManager;
    private final JmsOperations mToClientStatus;
    private final JmsOperations mToTradeRecorder;
    private final BlockingDeque<MessagePackage> messagesToProcess = new LinkedBlockingDeque<MessagePackage>();


    // CONSTRUCTORS.

    public QuickFIXApplication
        (SystemInfo systemInfo,
         Brokers brokers,
         MessageFilter supportedMessages,
         ReplyPersister persister,
         IQuickFIXSender sender,
         UserManager userManager,
         JmsOperations toClientStatus,
         JmsOperations toTradeRecorder)
    {
        mSystemInfo=systemInfo;
        mBrokers=brokers;
        mSupportedMessages=supportedMessages;
        mPersister=persister;
        mSender=sender;
        mUserManager=userManager;
        mToClientStatus=toClientStatus;
        mToTradeRecorder=toTradeRecorder;
        new MessageProcessor();
    }


    // INSTANCE METHODS.

    public SystemInfo getSystemInfo()
    {
        return mSystemInfo;         
    }
         
    public Brokers getBrokers()
    {
        return mBrokers;
    }

    public MessageFilter getSupportedMessages()
    {
        return mSupportedMessages;
    }

    public ReplyPersister getPersister()
    {
        return mPersister;
    }

    public IQuickFIXSender getSender()
    {
        return mSender;
    }

    public UserManager getUserManager()
    {
        return mUserManager;
    }

    public JmsOperations getToClientStatus()
    {
        return mToClientStatus;
    }

    public JmsOperations getToTradeRecorder()
    {
        return mToTradeRecorder;
    }

    private Object getCategory
        (Message msg)
    {
        if (FIXMessageUtil.isHeartbeat(msg)) {
            return HEARTBEAT_CATEGORY;
        }
        return this;
    }

    private void updateStatus
        (Broker b,
         boolean status)
    {
        if (b.getLoggedOn()==status) {
            return;
        }
        Messages.QF_SENDING_STATUS.info(this,status,b);
        b.setLoggedOn(status);
        if (getToClientStatus()==null) {
            return;
        }
        getToClientStatus().convertAndSend(b.getStatus());
    }

    private void sendTradeRecord
        (Message msg)
    {
        Messages.QF_SENDING_TRADE_RECORD.info(getCategory(msg),msg);
        if (getToTradeRecorder()==null) {
            return;
        }
        getToTradeRecorder().convertAndSend(msg);
    }

    private void sendToClientTrades
        (boolean admin,
         Broker b,
         Message msg,
         Originator originator)
    {
        if (getUserManager()==null) {
            return;
        }

        // Obtain principals.

        Principals principals;
        if (admin) {
            principals=Principals.UNKNOWN;
        } else {
            principals=getPersister().getPrincipals(msg,false);
        }

        // Apply message modifiers.

        if ((originator==Originator.Broker) &&
            (b.getResponseModifiers()!=null)) {
            try {
                SessionInfo sessionInfo=new SessionInfoImpl(getSystemInfo());
                sessionInfo.setValue
                    (SessionInfo.ACTOR_ID,principals.getActorID());
                RequestInfo requestInfo=new RequestInfoImpl(sessionInfo);
                requestInfo.setValue
                    (RequestInfo.BROKER,b);
                requestInfo.setValue
                    (RequestInfo.BROKER_ID,b.getBrokerID());
                requestInfo.setValue
                    (RequestInfo.ORIGINATOR,originator);
                requestInfo.setValue
                    (RequestInfo.FIX_MESSAGE_FACTORY,b.getFIXMessageFactory());
                requestInfo.setValue
                    (RequestInfo.CURRENT_MESSAGE,msg);
                b.getResponseModifiers().modifyMessage(requestInfo);
                msg=requestInfo.getValueIfInstanceOf
                    (RequestInfo.CURRENT_MESSAGE,Message.class);
            } catch (I18NException ex) {
                Messages.QF_MODIFICATION_FAILED.error
                    (getCategory(msg),ex,msg,b.toString());
                return;
            }
        }

        // Convert reply to FIX Agnostic messsage.

        TradeMessage reply;
        try {
            reply=FIXConverter.fromQMessage
                (msg,originator,b.getBrokerID(),
                 principals.getActorID(),principals.getViewerID());
        } catch (MessageCreationException ex) {
            Messages.QF_REPORT_FAILED.error
                (getCategory(msg),ex,msg,b.toString());
            return;
        }

        // Persist and send reply.
        
        getPersister().persistReply(reply);
        Messages.QF_SENDING_REPLY.info(getCategory(msg),reply);
        getUserManager().convertAndSend(reply);
    }


    // Application.

    @Override
    public void onCreate
        (SessionID session) {}

    @Override
	public void onLogon
        (SessionID session)
    {
        Broker b=getBrokers().getBroker(session);
        updateStatus(b,true);
        if(b.getSpringBroker().getLogonActions() != null) {
            for(LogonAction action : b.getSpringBroker().getLogonActions()) {
                try {
                    action.onLogon(b,
                                   getSender());
                } catch (Exception e) {
                    SLF4JLoggerProxy.warn(QuickFIXApplication.class,
                                          e);
                }
            }
        }
         // fromAdmin() will forward an execution report following the
        // logon; there is no need to send a message from here.
    }

    @Override
	public void onLogout
        (SessionID session)
    {
        Broker b=getBrokers().getBroker(session);
        updateStatus(b,false);
        Collection<LogoutAction> logoutActions = b.getSpringBroker().getLogoutActions();
        if(logoutActions != null) {
            for(LogoutAction action : logoutActions) {
                try {
                    action.onLogout(b,
                                    getSender());
                } catch (Exception e) {
                    SLF4JLoggerProxy.warn(QuickFIXApplication.class,
                                          e);
                }
            }
        }
    }

    @Override
    public void toAdmin
        (Message msg,
         SessionID session)
    {
        Broker b=getBrokers().getBroker(session);
        Messages.QF_TO_ADMIN.info(getCategory(msg),msg,b);
        b.logMessage(msg);

        // Apply message modifiers.

        if (b.getModifiers()!=null) {
            try {
                RequestInfo requestInfo=
                    new RequestInfoImpl(new SessionInfoImpl(getSystemInfo()));
                requestInfo.setValue
                    (RequestInfo.BROKER,b);
                requestInfo.setValue
                    (RequestInfo.BROKER_ID,b.getBrokerID());
                requestInfo.setValue
                    (RequestInfo.FIX_MESSAGE_FACTORY,b.getFIXMessageFactory());
                requestInfo.setValue
                    (RequestInfo.CURRENT_MESSAGE,msg);
                b.getModifiers().modifyMessage(requestInfo);
                msg=requestInfo.getValueIfInstanceOf
                    (RequestInfo.CURRENT_MESSAGE,Message.class);
            } catch (I18NException ex) {
                Messages.QF_MODIFICATION_FAILED.warn
                    (getCategory(msg),ex,msg,b.toString());
            }
        }

        // If the QuickFIX/J engine is sending a reject (e.g. the
        // counterparty sent us a malformed execution report, for
        // example, and we are rejecting it), we notify the client of
        // the rejection.

        if (FIXMessageUtil.isReject(msg)) {
            try {
                String msgType=(msg.isSetField(MsgType.FIELD)?null:
                                msg.getString(RefMsgType.FIELD));
                String msgTypeName=b.getFIXDataDictionary().
                    getHumanFieldValue(MsgType.FIELD, msgType);
                msg.setString(Text.FIELD,Messages.QF_IN_MESSAGE_REJECTED.
                              getText(msgTypeName,msg.getString(Text.FIELD)));
            } catch (FieldNotFound ex) {
                Messages.QF_MODIFICATION_FAILED.warn
                    (getCategory(msg),ex,msg,b.toString());
                // Send original message instead of modified one.
            }
            sendToClientTrades(true,b,msg,Originator.Server);
        }
    }

    @Override
    public void fromAdmin
        (Message msg,
         SessionID session)
    {
        messagesToProcess.add(new MessagePackage(msg,
                                                 MessageType.FROM_ADMIN,
                                                 session));
    }

    @Override
	public void toApp
        (Message msg,
         SessionID session)
        throws DoNotSend
    {
        Broker b=getBrokers().getBroker(session);
        Messages.QF_TO_APP.info(getCategory(msg),msg,b);
        b.logMessage(msg);
    }

    @Override
	public void fromApp(Message msg,
	                    SessionID session)
            throws UnsupportedMessageType, FieldNotFound
    {
        Broker b=getBrokers().getBroker(session);
        Messages.QF_FROM_APP.info(getCategory(msg),msg,b);
        b.logMessage(msg);

        // Accept only certain message types.

        if (!getSupportedMessages().isAccepted(msg)){
            Messages.QF_DISALLOWED_MESSAGE.info(getCategory(msg));
            throw new UnsupportedMessageType();
        }
        messagesToProcess.add(new MessagePackage(msg,
                                                 MessageType.FROM_APP,
                                                 session));
    }
    /**
     * Indicates the type of message.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 2.1.4
     */
    @ClassVersion("$Id$")
    private enum MessageType
    {
        FROM_ADMIN,
        FROM_APP
    }
    /**
     * Encapsulates a message to be processed.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 2.1.4
     */
    @ClassVersion("$Id$")
    private static class MessagePackage
    {
        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + (int) (id ^ (id >>> 32));
            return result;
        }
        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj)
        {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof MessagePackage)) {
                return false;
            }
            MessagePackage other = (MessagePackage) obj;
            if (id != other.id) {
                return false;
            }
            return true;
        }
      /**
         * Create a new MessagePackage instance.
         *
         * @param inMessage a <code>Message</code> value
         * @param inMessageType a <code>MessageType</code> value
         * @param inSessionId a <code>SessionID</code> value
         */
        private MessagePackage(Message inMessage,
                               MessageType inMessageType,
                               SessionID inSessionId)
        {
            message = inMessage;
            messageType = inMessageType;
            sessionId = inSessionId;
        }
        /**
         * Gets the <code>Message</code> value.
         *
         * @return a <code>Message</code> value
         */
        private Message getMessage()
        {
            return message;
        }
        /**
         * Gets the <code>MessageType</code> value.
         *
         * @return a <code>MessageType</code> value
         */
        private MessageType getMessageType()
        {
            return messageType;
        }
        /**
         * Gets the <code>SessionID</code> value.
         *
         * @return a <code>SessionID</code> value
         */
        private SessionID getSessionId()
        {
            return sessionId;
        }
        /**
         * message value
         */
        private final Message message;
        /**
         * message type value
         */
        private final MessageType messageType;
        /**
         * session ID value
         */
        private final SessionID sessionId;
        /**
         * message counter
         */
        private final long id = counter.incrementAndGet();
        /**
         * counter used to uniquely and sequentially identify messages
         */
        private static final AtomicLong counter = new AtomicLong(0);
    }
    /**
     * Processes incoming messages.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 2.1.4
     */
    @ClassVersion("$Id$")
    private class MessageProcessor
            implements Runnable
    {
        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run()
        {
            try {
                while(true) {
                    MessagePackage message = messagesToProcess.take();
                    switch(message.getMessageType()) {
                        case FROM_ADMIN: {
                            SessionID session = message.getSessionId();
                            Message msg = message.getMessage();
                            Broker b = getBrokers().getBroker(session);
                            Messages.QF_FROM_ADMIN.info(getCategory(msg),
                                                        msg,
                                                        b);
                            b.logMessage(msg);
                            // Send message to client.
                            sendToClientTrades(true,
                                               b,
                                               msg,
                                               Originator.Broker);
                            break;
                        }
                        case FROM_APP: {
                            SessionID session = message.getSessionId();
                            Message msg = message.getMessage();
                            Broker b = getBrokers().getBroker(session);
                            try {
                                // Report trading session status in a human-readable format.
                                if (FIXMessageUtil.isTradingSessionStatus(msg)) {
                                    Messages.QF_TRADE_SESSION_STATUS.info(getCategory(msg),
                                                                          b.getFIXDataDictionary().getHumanFieldValue(TradSesStatus.FIELD,
                                                                                                                      msg.getString(TradSesStatus.FIELD)));
                                }
                                // Send message to client.
                                sendToClientTrades(false,b,msg,Originator.Broker);
                                // OpenFIX certification: we reject all DeliverToCompID since we don't redeliver.
                                if (msg.getHeader().isSetField(DeliverToCompID.FIELD)) {
                                    try {
                                        Message reject = b.getFIXMessageFactory().createSessionReject(msg,
                                                                                                      SessionRejectReason.COMPID_PROBLEM);
                                        reject.setString(Text.FIELD,
                                                         Messages.QF_COMP_ID_REJECT.getText(msg.getHeader().getString(DeliverToCompID.FIELD)));
                                        getSender().sendToTarget(reject,
                                                                 session);
                                    } catch (SessionNotFound ex) {
                                        Messages.QF_COMP_ID_REJECT_FAILED.error(getCategory(msg),
                                                                                ex,
                                                                                b.toString());
                                    }
                                    break;
                                }
                                // Record filled (partially or totally) execution reports.
                                if (FIXMessageUtil.isExecutionReport(msg)) {
                                    char ordStatus=msg.getChar(OrdStatus.FIELD);
                                    if ((ordStatus==OrdStatus.FILLED) ||
                                        (ordStatus==OrdStatus.PARTIALLY_FILLED)) {
                                        sendTradeRecord(msg);
                                    }
                                }
                            } catch (FieldNotFound e) {
                                SLF4JLoggerProxy.error(QuickFIXApplication.class,
                                                       e);
                            }
                            break;
                        }
                        default:
                            throw new UnsupportedOperationException();
                    }
                }
            } catch (InterruptedException ignored) {}
        }
        /**
         * Create a new MessageProcessor instance.
         */
        private MessageProcessor()
        {
            thread = new Thread(this,
                                "QuickFIXApplication Message Processing Tread"); //$NON-NLS-1$
            thread.start();
        }
        /**
         * thread on which the messages are processed
         */
        private final Thread thread;
    }
}
