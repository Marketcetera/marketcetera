package org.marketcetera.oms;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.LoggerAdapter;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.springframework.jms.core.JmsOperations;
import quickfix.Application;
import quickfix.DoNotSend;
import quickfix.Message;
import quickfix.SessionID;
import quickfix.field.*;

import java.util.Date;

/**
 * @author gmiller
 * $Id$
 */
@ClassVersion("$Id$")
public class QuickFIXApplication implements Application {

    private JmsOperations jmsOperations;
    private FIXMessageFactory fixMesageFactory;
    private boolean fLoggedOn;

    public QuickFIXApplication(FIXMessageFactory fixMessageFactory) {
        fLoggedOn = false;
        this.fixMesageFactory = fixMessageFactory; 
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
            } catch (Exception ex) {
                LoggerAdapter.error(OMSMessageKey.ERROR_SENDING_JMS_MESSAGE.getLocalizedMessage(ex.toString()), this);
                if(LoggerAdapter.isDebugEnabled(this)) { LoggerAdapter.debug("reason for above exception: "+ex, ex, this); }
            }
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
