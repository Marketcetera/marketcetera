package org.marketcetera.photon.messaging;

public class SimpleMessageListenerContainer extends
		org.springframework.jms.listener.SimpleMessageListenerContainer {

	public SimpleMessageListenerContainer() {
		super();
	}

	@Override
	public Object getMessageListener() {
		return super.getMessageListener();
	}

	
}
