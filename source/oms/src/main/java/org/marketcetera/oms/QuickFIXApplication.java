package org.marketcetera.oms;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.LoggerAdapter;
import org.marketcetera.core.MarketceteraException;
import org.marketcetera.quickfix.*;
import org.springframework.jms.core.JmsOperations;
import quickfix.*;
import quickfix.field.*;

import java.util.Date;

/**
 * @author gmiller
 * $Id$
 */
@ClassVersion("$Id$")
public class QuickFIXApplication implements Application {

    private JmsOperations jmsOperations;
    private JmsOperations tradeRecorderJMS;
    private FIXMessageFactory fixMessageFactory;
    private boolean fLoggedOn;
    protected IQuickFIXSender quickFIXSender;
    private MessageModifierManager messageModifierMgr;

    public QuickFIXApplication(FIXMessageFactory fixMessageFactory) {
        fLoggedOn = false;
        this.fixMessageFactory = fixMessageFactory;
        quickFIXSender = createQuickFIXSender();
    }

    public void fromAdmin(Message message, SessionID session)  {
        if (jmsOperations != null){
            try {
                if(MsgType.REJECT.equals(message.getHeader().getString(MsgType.FIELD))) {
                    // bug #219
                    if(LoggerAdapter.isDebugEnabled(this)) { LoggerAdapter.debug("received reject: "+message.getString(Text.FIELD), this); }
                }

                jmsOperations.convertAndSend(message);
            } catch (Exception ex) {
                LoggerAdapter.error(OMSMessageKey.ERROR_SENDING_JMS_MESSAGE.getLocalizedMessage(ex.toString()), this);
                if(LoggerAdapter.isDebugEnabled(this)) { LoggerAdapter.debug("reason for above exception: "+ex, ex, this); }
            }
        }
	}

	public void fromApp(Message message, SessionID session) throws UnsupportedMessageType, FieldNotFound {
        /** This check is specifically for OpenFIX certification - we need to white list/black list all
         * supported messages to implement this for real, and make that configurable
         */
        if(MsgType.ALLOCATION_INSTRUCTION_ACK.equals(message.getHeader().getString(MsgType.FIELD))) {
            throw new UnsupportedMessageType();
        }

		if (jmsOperations != null){
            try {
                jmsOperations.convertAndSend(message);
                if (message.getHeader().isSetField(DeliverToCompID.FIELD)) {
                    // Support OpenFIX certification - we reject all DeliverToCompID since we don't redilever
                    Message reject = fixMessageFactory.createSessionReject(message, SessionRejectReason.COMPID_PROBLEM);
                    reject.setString(Text.FIELD,
                            OMSMessageKey.ERROR_DELIVER_TO_COMP_ID_NOT_HANDLED.getLocalizedMessage(message.getHeader().getString(DeliverToCompID.FIELD)));
                    quickFIXSender.sendToTarget(reject);
                    return;
                }
            } catch (Exception ex) {
                LoggerAdapter.error(OMSMessageKey.ERROR_SENDING_JMS_MESSAGE.getLocalizedMessage(ex.toString()), ex, this);
                if(LoggerAdapter.isDebugEnabled(this)) { LoggerAdapter.debug("reason for above exception: "+ex, ex, this); }
            }
        }

        if(FIXMessageUtil.isExecutionReport(message)) {
            char ordStatus = message.getChar(OrdStatus.FIELD);
            if((ordStatus == OrdStatus.FILLED) || (ordStatus == OrdStatus.PARTIALLY_FILLED)) {
                logMessage(message, session);
            }
        }

        if (FIXMessageUtil.isTradingSessionStatus(message)) {
            if (LoggerAdapter.isDebugEnabled(this)) {
                LoggerAdapter.debug(OMSMessageKey.TRADE_SESSION_STATUS.getLocalizedMessage(
                        FIXDataDictionaryManager.getCurrentFIXDataDictionary().getHumanFieldValue(TradSesStatus.FIELD,
                                message.getString(TradSesStatus.FIELD))), this);
            }
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
        logout.getHeader().setField(new SendingTime(new Date()));
        if (jmsOperations != null) {
            try {
                jmsOperations.convertAndSend(logout);
            } catch (Exception ex) {
                LoggerAdapter.error(OMSMessageKey.ERROR_SENDING_JMS_MESSAGE.getLocalizedMessage(ex.toString()), this);
                if(LoggerAdapter.isDebugEnabled(this)) { LoggerAdapter.debug("reason for above exception: "+ex, ex, this); }
            }
        }
    }

    /** Apply message modifiers to all outgoing to-admin messages (such as logout/login) */
    public void toAdmin(Message message, SessionID session) {
        try {
            if(messageModifierMgr != null) {
                messageModifierMgr.modifyMessage(message);
            }
        } catch (MarketceteraException ex) {
            if(LoggerAdapter.isDebugEnabled(this)) { LoggerAdapter.debug("Error modifying message: "+message, ex, this); }
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
