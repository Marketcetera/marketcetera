package org.marketcetera.oms;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.LoggerAdapter;
import org.springframework.jms.core.JmsOperations;
import quickfix.Application;
import quickfix.DoNotSend;
import quickfix.Message;
import quickfix.SessionID;
import quickfix.field.MsgType;
import quickfix.field.Text;

/**
 * @author gmiller
 * $Id$
 */
@ClassVersion("$Id$")
public class QuickFIXApplication implements Application {

    private JmsOperations jmsOperations;
    private boolean fLoggedOn;

    public QuickFIXApplication() {
        fLoggedOn = false;
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
    }

	public void onLogout(SessionID session) {
        fLoggedOn = false;
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
