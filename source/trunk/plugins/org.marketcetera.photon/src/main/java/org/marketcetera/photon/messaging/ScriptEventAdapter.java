package org.marketcetera.photon.messaging;

import org.apache.bsf.BSFException;
import org.marketcetera.photon.scripting.EventScriptController;
import org.marketcetera.spring.JMSFIXMessageConverter;
import org.springframework.jms.listener.adapter.ListenerExecutionFailedException;

import quickfix.Message;

public class ScriptEventAdapter extends DirectMessageListenerAdapter {
	private EventScriptController controller;

	public ScriptEventAdapter() {
		super();
		this.setMessageConverter(new JMSFIXMessageConverter());
	}

	public EventScriptController getController() {
		return controller;
	}

	public void setController(EventScriptController controller) {
		this.controller = controller;
	}

	@Override
	protected Object doOnMessage(Object convertedMessage) {
		
		if (controller != null){
			try {
				controller.onEvent((Message)convertedMessage);
			} catch (BSFException e) {
				// TODO Auto-generated catch block
				throw new ListenerExecutionFailedException(
						"Exception while executing script", e);
			}
		}
		return null;
	}

}
