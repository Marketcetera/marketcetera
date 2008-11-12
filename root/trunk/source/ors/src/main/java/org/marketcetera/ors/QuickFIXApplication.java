package org.marketcetera.ors;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.CoreException;
import org.marketcetera.quickfix.*;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.jms.core.JmsOperations;
import quickfix.*;
import quickfix.field.*;

import java.util.Date;
import java.util.Set;

/**
 * @author gmiller
 * $Id$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class QuickFIXApplication implements Application {

    private JmsOperations jmsOperations;
    private JmsOperations tradeRecorderJMS;
    private FIXMessageFactory fixMessageFactory;
    private Set supportedMsgs;
    private boolean fLoggedOn;
    protected IQuickFIXSender quickFIXSender;
    private MessageModifierManager messageModifierMgr;

    public QuickFIXApplication(FIXMessageFactory fixMessageFactory) {
        fLoggedOn = false;
        this.fixMessageFactory = fixMessageFactory;
        quickFIXSender = createQuickFIXSender();
        supportedMsgs = java.util.Collections.EMPTY_SET; 
    }

    public void fromAdmin(Message message, SessionID session)  {
        if (jmsOperations != null){
            try {
                if(MsgType.REJECT.equals(message.getHeader().getString(MsgType.FIELD))) {
                    // bug #219
                    SLF4JLoggerProxy.debug(this, "received reject: {}", message.getString(Text.FIELD)); //$NON-NLS-1$
                }

                jmsOperations.convertAndSend(message);
            } catch (Exception ex) {
                Messages.ERROR_SENDING_JMS_MESSAGE.error(this, ex, ex.toString());
                SLF4JLoggerProxy.debug(this, ex, "Failed sending message: {}", message); //$NON-NLS-1$
            }
        }
    }

	public void fromApp(Message message, SessionID session) throws UnsupportedMessageType, FieldNotFound {
       if (!supportedMsgs.contains(message.getHeader().getString(MsgType.FIELD)))
                throw new UnsupportedMessageType(); 
		if (jmsOperations != null){
            try {
                jmsOperations.convertAndSend(message);
                if (message.getHeader().isSetField(DeliverToCompID.FIELD)) {
                    // Support OpenFIX certification - we reject all DeliverToCompID since we don't redilever
                    Message reject = fixMessageFactory.createSessionReject(message, SessionRejectReason.COMPID_PROBLEM);
                    reject.setString(Text.FIELD,
                            Messages.ERROR_NO_DELIVER_TO_COMPID_FIELD.getText(message.getHeader().getString(DeliverToCompID.FIELD)));
                    quickFIXSender.sendToTarget(reject);
                    return;
                }
            } catch (Exception ex) {
                Messages.ERROR_SENDING_JMS_MESSAGE.error(this, ex, ex.toString());
                SLF4JLoggerProxy.debug(this, ex, "Failed sending message: {}", message); //$NON-NLS-1$
            }
        }

        if(FIXMessageUtil.isExecutionReport(message)) {
            char ordStatus = message.getChar(OrdStatus.FIELD);
            if((ordStatus == OrdStatus.FILLED) || (ordStatus == OrdStatus.PARTIALLY_FILLED)) {
                logMessage(message, session);
            }
        }

        if (FIXMessageUtil.isTradingSessionStatus(message)) {
            Messages.TRADE_SESSION_STATUS.debug(this, 
                                                CurrentFIXDataDictionary.getCurrentFIXDataDictionary().getHumanFieldValue(TradSesStatus.FIELD, message.getString(TradSesStatus.FIELD)));
        }
    }

    /** Wrapper around logging a message to be overridden by tests with a noop */
    protected void logMessage(final Message message, SessionID sessionID) {
        if(tradeRecorderJMS != null) {
            tradeRecorderJMS.convertAndSend(message);
        }
    }

    public void onCreate(SessionID session) {
	}

	public void onLogon(SessionID session) {
        fLoggedOn = true;
        // do not forward the logon message over JMS as it's already coming through in the fromAdmin call
    }

	public void onLogout(SessionID session) {
        fLoggedOn = false;
        Message logout = fixMessageFactory.createMessage(MsgType.LOGOUT);
        logout.getHeader().setField(new SenderCompID(session.getSenderCompID()));
        logout.getHeader().setField(new TargetCompID(session.getTargetCompID()));
        logout.getHeader().setField(new SendingTime(new Date())); //non-i18n
        if (jmsOperations != null) {
            try {
                jmsOperations.convertAndSend(logout);
            } catch (Exception ex) {
                Messages.ERROR_SENDING_JMS_MESSAGE.error(this, ex, ex.toString());
                SLF4JLoggerProxy.debug(this, ex, "Failed sending message: {}", logout); //$NON-NLS-1$
            }
        }
    }

    /** Apply message modifiers to all outgoing to-admin messages (such as logout/login)
     * In case the underlying QFJ engine is sending a reject (ie the counterparty sent us a
     * malformed ExecReport for example) we want to add some more information to it and
     * send a copy of the reject on the shared ors-messages topic as well so that the
     * outgoing reject is visible in Photon and to other subscribers
     * */
    public void toAdmin(Message message, SessionID session) {
        try {
            if(messageModifierMgr != null) {
                messageModifierMgr.modifyMessage(message);
            }
            if(FIXMessageUtil.isReject(message)) {
                try {
                    String origText = message.getString(Text.FIELD);
                    String msgType = (message.isSetField(MsgType.FIELD)) ? null : message.getString(RefMsgType.FIELD);
                    String msgTypeName = CurrentFIXDataDictionary.getCurrentFIXDataDictionary().getHumanFieldValue(MsgType.FIELD, msgType);
                    String combinedText = Messages.ERROR_INCOMING_MSG_REJECTED.getText(msgTypeName, origText);
                    message.setString(Text.FIELD, combinedText);
                } catch (FieldNotFound fieldNotFound) {
                    // ignore - don't modify the message, send original error through
                }
                jmsOperations.convertAndSend(message);
            }
        } catch (CoreException ex) {
            SLF4JLoggerProxy.debug(this, ex, "Failed modifying message: {}", message); //$NON-NLS-1$
        }
    }

	public void toApp(Message message, SessionID session) throws DoNotSend {
	}

	public JmsOperations getJmsOperations() {
		return jmsOperations;
	}

	public void setJmsOperations(JmsOperations jmsOperations) {
		this.jmsOperations = jmsOperations;
	}

    public void setTradeRecorderJMS(JmsOperations tradeRecorderJMS) {
        this.tradeRecorderJMS = tradeRecorderJMS;
    }

    public void setSupportedMsgs(Set supportedMsgs) {
        this.supportedMsgs = supportedMsgs;
    }
 


    public void setMessageModifierMgr(MessageModifierManager inMgr){
		messageModifierMgr = inMgr;
	}

    public boolean isLoggedOn() {
        return fLoggedOn;
    }

    /** To be overridden by tests */
    protected IQuickFIXSender createQuickFIXSender() {
        return new QuickFIXSender();
    }
}
