package org.marketcetera.ors;

import org.marketcetera.core.CoreException;
import org.marketcetera.ors.dest.Destination;
import org.marketcetera.ors.dest.Destinations;
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
 * A handler for incoming trade requests (orders).
 *
 * @author tlerios@marketcetera.com
 * @since $Release$
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$") //$NON-NLS-1$
public class QuickFIXApplication
    implements Application
{

    // CLASS DATA

    private static final String HEARTBEAT_CATEGORY=
        QuickFIXApplication.class.getName()+".HEARTBEATS";


    // INSTANCE DATA.

    private final Destinations mDestinations;
    private final MessageFilter mSupportedMessages;
    private final ReplyPersister mPersister;
    private final IQuickFIXSender mSender;
    private final JmsOperations mToClientTrades;
    private final JmsOperations mToClientStatus;
    private final JmsOperations mToTradeRecorder;


    // CONSTRUCTORS.

    public QuickFIXApplication
        (Destinations destinations,
         MessageFilter supportedMessages,
         ReplyPersister persister,
         IQuickFIXSender sender,
         JmsOperations toClientTrades,
         JmsOperations toClientStatus,
         JmsOperations toTradeRecorder)
    {
        mDestinations=destinations;
        mSupportedMessages=supportedMessages;
        mPersister=persister;
        mSender=sender;
        mToClientTrades=toClientTrades;
        mToClientStatus=toClientStatus;
        mToTradeRecorder=toTradeRecorder;
    }


    // INSTANCE METHODS.

    public Destinations getDestinations()
    {
        return mDestinations;
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

    public JmsOperations getToClientTrades()
    {
        return mToClientTrades;
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
        (Destination d,
         boolean status)
    {
        Messages.QF_SENDING_STATUS.info(this,status,d);
        d.setLoggedOn(status);
        if (getToClientStatus()==null) {
            return;
        }
        getToClientStatus().convertAndSend(d.getStatus());
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
        (Destination d,
         Message msg,
         Originator originator)
    {
        if (getToClientTrades()==null) {
            return;
        }

        // Convert reply to FIX Agnostic messsage.

        TradeMessage reply=null;
        try {
            reply=FIXConverter.fromQMessage
                (msg,originator,d.getDestinationID());
            if (reply==null) {
                Messages.QF_REPORT_TYPE_UNSUPPORTED.warn
                    (getCategory(msg),msg,d.toString());
            }
        } catch (MessageCreationException ex) {
            Messages.QF_REPORT_FAILED.error
                (getCategory(msg),ex,msg,d.toString());
        }

        // If reply could not be packaged in FIX Agnostic format, we
        // are done (an error has already been reported).

        if (reply==null) {
            return;
        }

        // Persist and send reply.
        
        getPersister().persistReply(reply);
        Messages.QF_SENDING_REPLY.info(getCategory(msg),reply);
        getToClientTrades().convertAndSend(reply);
    }


    // Application.

    @Override
    public void onCreate
        (SessionID session) {}

    @Override
	public void onLogon
        (SessionID session)
    {
        Destination d=getDestinations().getDestination(session);
        updateStatus(d,true);
        // fromAdmin() will forward an execution report following the
        // logon; there is no need to send a message from here.
    }

    @Override
	public void onLogout
        (SessionID session)
    {
        Destination d=getDestinations().getDestination(session);
        updateStatus(d,false);
    }

    @Override
    public void toAdmin
        (Message msg,
         SessionID session)
    {
        Destination d=getDestinations().getDestination(session);
        Messages.QF_TO_ADMIN.info(getCategory(msg),msg,d);
        d.logMessage(msg);

        // Apply message modifiers.

        if (d.getModifiers()!=null) {
            try {
                d.getModifiers().modifyMessage(msg);
            } catch (CoreException ex) {
                Messages.QF_MODIFICATION_FAILED.warn
                    (getCategory(msg),ex,msg,d.toString());
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
                String msgTypeName=d.getFIXDataDictionary().
                    getHumanFieldValue(MsgType.FIELD, msgType);
                msg.setString(Text.FIELD,Messages.QF_IN_MESSAGE_REJECTED.
                              getText(msgTypeName,msg.getString(Text.FIELD)));
            } catch (FieldNotFound ex) {
                Messages.QF_MODIFICATION_FAILED.warn
                    (getCategory(msg),ex,msg,d.toString());
                // Send original message instead of modified one.
            }
            sendToClientTrades(d,msg,Originator.Server);
        }
    }

    @Override
    public void fromAdmin
        (Message msg,
         SessionID session)
    {
        Destination d=getDestinations().getDestination(session);
        Messages.QF_FROM_ADMIN.info(getCategory(msg),msg,d);
        d.logMessage(msg);

        // Send message to client.

        sendToClientTrades(d,msg,Originator.Destination);
    }

    @Override
	public void toApp
        (Message msg,
         SessionID session)
        throws DoNotSend
    {
        Destination d=getDestinations().getDestination(session);
        Messages.QF_TO_APP.info(getCategory(msg),msg,d);
        d.logMessage(msg);
    }

    @Override
	public void fromApp
        (Message msg,
         SessionID session)
        throws UnsupportedMessageType,
               FieldNotFound
    {
        Destination d=getDestinations().getDestination(session);
        Messages.QF_FROM_APP.info(getCategory(msg),msg,d);
        d.logMessage(msg);

        // Accept only certain message types.

        if (!getSupportedMessages().isAccepted(msg)){
            Messages.QF_DISALLOWED_MESSAGE.info(getCategory(msg));
            throw new UnsupportedMessageType();
        }

        // Report trading session status in a human-readable format.

        if (FIXMessageUtil.isTradingSessionStatus(msg)) {
            Messages.QF_TRADE_SESSION_STATUS.info
                (getCategory(msg),d.getFIXDataDictionary().getHumanFieldValue
                 (TradSesStatus.FIELD,msg.getString(TradSesStatus.FIELD)));
        }

        // Send message to client.

        sendToClientTrades(d,msg,Originator.Destination);

        // OpenFIX certification: we reject all DeliverToCompID since
        // we don't redeliver.

        if (msg.getHeader().isSetField(DeliverToCompID.FIELD)) {
            try {
                Message reject=d.getFIXMessageFactory().createSessionReject
                    (msg,SessionRejectReason.COMPID_PROBLEM);
                reject.setString
                    (Text.FIELD,Messages.QF_COMP_ID_REJECT.getText
                     (msg.getHeader().getString(DeliverToCompID.FIELD)));
                getSender().sendToTarget(reject,session);
            } catch (SessionNotFound ex) {
                Messages.QF_COMP_ID_REJECT_FAILED.error
                    (getCategory(msg),ex,d.toString());
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
