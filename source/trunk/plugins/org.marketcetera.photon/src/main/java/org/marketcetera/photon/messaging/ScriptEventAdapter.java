package org.marketcetera.photon.messaging;

import org.apache.bsf.BSFException;
import org.marketcetera.photon.scripting.ScriptRegistry;
import org.marketcetera.spring.JMSFIXMessageConverter;
import org.springframework.jms.listener.adapter.ListenerExecutionFailedException;

import quickfix.Message;

public class ScriptEventAdapter extends DirectMessageListenerAdapter {
	private ScriptRegistry registry;

	public ScriptEventAdapter() {
		super();
		this.setMessageConverter(new JMSFIXMessageConverter());
	}

	public ScriptRegistry getRegistry() {
		return registry;
	}

	public void setRegistry(ScriptRegistry registry) {
		this.registry = registry;
	}

	@Override
	protected Object doOnMessage(Object convertedMessage) {
		
		if (registry != null){
			try {
				registry.onEvent((Message)convertedMessage);
			} catch (BSFException e) {
				throw new ListenerExecutionFailedException(
						"Exception while executing script", e);
			}
		}
		return null;
	}

}
