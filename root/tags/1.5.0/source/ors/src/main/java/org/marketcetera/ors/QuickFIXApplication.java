package org.marketcetera.ors;

import org.marketcetera.core.CoreException;
import org.marketcetera.ors.brokers.Broker;
import org.marketcetera.ors.brokers.Brokers;
import org.marketcetera.ors.filters.MessageFilter;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.quickfix.IQuickFIXSender;
import org.marketcetera.trade.FIXConverter;
import org.marketcetera.trade.MessageCreationException;
import org.marketcetera.trade.Originator;
import org.marketcetera.trade.TradeMessage;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.jms.core.JmsOperations;
import quickfix.Application;
import quickfix.DoNotSend;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.SessionID;
import quickfix.SessionNotFound;
import quickfix.UnsupportedMessageType;
import quickfix.field.DeliverToCompID;
import quickfix.field.MsgType;
import quickfix.field.OrdStatus;
import quickfix.field.RefMsgType;
import quickfix.field.SessionRejectReason;
import quickfix.field.Text;
import quickfix.field.TradSesStatus;

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

    private final Brokers mBrokers;
    private final MessageFilter mSupportedMessages;
    private final ReplyPersister mPersister;
    private final IQuickFIXSender mSender;
    private final UserManager mUserManager;
    private final JmsOperations mToClientStatus;
    private final JmsOperations mToTradeRecorder;


    // CONSTRUCTORS.

    public QuickFIXApplication
        (Brokers brokers,
         MessageFilter supportedMessages,
         ReplyPersister persister,
         IQuickFIXSender sender,
         UserManager userManager,
         JmsOperations toClientStatus,
         JmsOperations toTradeRecorder)
    {
        mBrokers=brokers;
        mSupportedMessages=supportedMessages;
        mPersister=persister;
        mSender=sender;
        mUserManager=userManager;
        mToClientStatus=toClientStatus;
        mToTradeRecorder=toTradeRecorder;
    }


    // INSTANCE METHODS.

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

        // Convert reply to FIX Agnostic messsage.

        Principals principals=getPersister().getPrincipals(msg);
        TradeMessage reply=null;
        try {
            reply=FIXConverter.fromQMessage
                (msg,originator,b.getBrokerID(),
                 (admin?null:principals.getActorID()),
                 admin?null:principals.getViewerID());
        } catch (MessageCreationException ex) {
            Messages.QF_REPORT_FAILED.error
                (getCategory(msg),ex,msg,b.toString());
        }
        if (reply==null) {
            Messages.QF_REPORT_TYPE_UNSUPPORTED.warn
                (getCategory(msg),msg,b.toString());
        }

        // If reply could not be packaged in FIX Agnostic format, we
        // are done (an error has already been reported).

        if (reply==null) {
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
        // fromAdmin() will forward an execution report following the
        // logon; there is no need to send a message from here.
    }

    @Override
	public void onLogout
        (SessionID session)
    {
        Broker b=getBrokers().getBroker(session);
        updateStatus(b,false);
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
                b.getModifiers().modifyMessage(msg);
            } catch (CoreException ex) {
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
        Broker b=getBrokers().getBroker(session);
        Messages.QF_FROM_ADMIN.info(getCategory(msg),msg,b);
        b.logMessage(msg);

        // Send message to client.

        sendToClientTrades(true,b,msg,Originator.Broker);
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
	public void fromApp
        (Message msg,
         SessionID session)
        throws UnsupportedMessageType,
               FieldNotFound
    {
        Broker b=getBrokers().getBroker(session);
        Messages.QF_FROM_APP.info(getCategory(msg),msg,b);
        b.logMessage(msg);

        // Accept only certain message types.

        if (!getSupportedMessages().isAccepted(msg)){
            Messages.QF_DISALLOWED_MESSAGE.info(getCategory(msg));
            throw new UnsupportedMessageType();
        }

        // Report trading session status in a human-readable format.

        if (FIXMessageUtil.isTradingSessionStatus(msg)) {
            Messages.QF_TRADE_SESSION_STATUS.info
                (getCategory(msg),b.getFIXDataDictionary().getHumanFieldValue
                 (TradSesStatus.FIELD,msg.getString(TradSesStatus.FIELD)));
        }

        // Send message to client.

        sendToClientTrades(false,b,msg,Originator.Broker);

        // OpenFIX certification: we reject all DeliverToCompID since
        // we don't redeliver.

        if (msg.getHeader().isSetField(DeliverToCompID.FIELD)) {
            try {
                Message reject=b.getFIXMessageFactory().createSessionReject
                    (msg,SessionRejectReason.COMPID_PROBLEM);
                reject.setString
                    (Text.FIELD,Messages.QF_COMP_ID_REJECT.getText
                     (msg.getHeader().getString(DeliverToCompID.FIELD)));
                getSender().sendToTarget(reject,session);
            } catch (SessionNotFound ex) {
                Messages.QF_COMP_ID_REJECT_FAILED.error
                    (getCategory(msg),ex,b.toString());
            }
            return;
        }

        // Record filled (partially or totally) execution reports.

        if (FIXMessageUtil.isExecutionReport(msg)) {
            char ordStatus=msg.getChar(OrdStatus.FIELD);
            if ((ordStatus==OrdStatus.FILLED) ||
                (ordStatus==OrdStatus.PARTIALLY_FILLED)) {
                sendTradeRecord(msg);
            }
        }
    }
}
