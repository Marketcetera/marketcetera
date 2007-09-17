package org.marketcetera.oms;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.LoggerAdapter;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.springframework.jms.core.JmsOperations;
import quickfix.*;
import quickfix.field.*;

import java.util.Date;
import java.util.HashMap;

/**
 * @author gmiller
 * $Id$
 */
@ClassVersion("$Id$")
public class QuickFIXApplication implements Application {

    private JmsOperations jmsOperations;
    private FIXMessageFactory fixMesageFactory;
    private boolean fLoggedOn;
    private HashMap<SessionID, Log> logMap;
    private JdbcLogFactory logFactory;

    public QuickFIXApplication(FIXMessageFactory fixMessageFactory, JdbcLogFactory logFactory) {
        fLoggedOn = false;
        this.fixMesageFactory = fixMessageFactory;
        this.logFactory = logFactory;
        logMap = new HashMap<SessionID, Log>();
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

	public void fromApp(Message message, SessionID session) {
		if (jmsOperations != null){
            try {
                jmsOperations.convertAndSend(message);
                if(FIXMessageUtil.isExecutionReport(message)) {
                    char ordStatus = message.getChar(OrdStatus.FIELD);
                    if((ordStatus == OrdStatus.FILLED) || (ordStatus == OrdStatus.PARTIALLY_FILLED)) {
                        logMessage(message, session);
                    }
                }
            } catch (Exception ex) {
                LoggerAdapter.error(OMSMessageKey.ERROR_SENDING_JMS_MESSAGE.getLocalizedMessage(ex.toString()), ex, this);
                if(LoggerAdapter.isDebugEnabled(this)) { LoggerAdapter.debug("reason for above exception: "+ex, ex, this); }
            }
        }
	}

    /** Wrapper around logging a message to be overridden by tests with a noop */
    protected void logMessage(Message message, SessionID sessionID) {
        Log log = logMap.get(sessionID);
        if(log == null) {
            log = logFactory.create(sessionID);
            logMap.put(sessionID, log);
        }
        log.onIncoming(message.toString());
    }

    public void onCreate(SessionID session) {
	}

	public void onLogon(SessionID session) {
        fLoggedOn = true;
        // do not forward the logon message over JMS as it's already coming through in the fromAdmin call
    }

	public void onLogout(SessionID session) {
        fLoggedOn = false;
        Message logout = fixMesageFactory.createMessage(MsgType.LOGOUT);
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

	public void toAdmin(Message message, SessionID session) {
	}

	public void toApp(Message message, SessionID session) throws DoNotSend {
	}

	public JmsOperations getJmsOperations() {
		return jmsOperations;
	}

	public void setJmsOperations(JmsOperations jmsOperations) {
		this.jmsOperations = jmsOperations;
	}


    public boolean isLoggedOn() {
        return fLoggedOn;
    }
}
