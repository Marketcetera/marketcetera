package org.marketcetera.oms;

import org.springframework.jms.core.JmsTemplate;

import quickfix.Application;
import quickfix.DoNotSend;
import quickfix.FieldNotFound;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.Message;
import quickfix.RejectLogon;
import quickfix.SessionID;
import quickfix.UnsupportedMessageType;

public class QuickFIXApplication implements Application {

    private JmsTemplate jmsTemplate;

	public QuickFIXApplication() {
	}
	
	public void fromAdmin(Message message, SessionID session) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
		if (jmsTemplate != null){
			jmsTemplate.convertAndSend(message);
		}
	}

	public void fromApp(Message message, SessionID session) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
		if (jmsTemplate != null){
			jmsTemplate.convertAndSend(message);
		}
	}

	public void onCreate(SessionID session) {
	}

	public void onLogon(SessionID session) {
	}

	public void onLogout(SessionID session) {
	}

	public void toAdmin(Message message, SessionID session) {
	}

	public void toApp(Message message, SessionID session) throws DoNotSend {
	}

	public JmsTemplate getJmsTemplate() {
		return jmsTemplate;
	}

	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

}
