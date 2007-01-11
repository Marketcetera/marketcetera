package org.marketcetera.photon.messaging;


import javax.jms.ExceptionListener;
import javax.jms.JMSException;

import org.apache.xbean.spring.context.ClassPathXmlApplicationContext;
import org.marketcetera.photon.FeedComponentAdapterBase;
import org.marketcetera.photon.PhotonPlugin;
import org.springframework.jms.core.JmsOperations;

public class JMSFeedService extends FeedComponentAdapterBase implements ExceptionListener {

	JmsOperations jmsOperations;
	ClassPathXmlApplicationContext applicationContext;
	private boolean hasException = false;
	
	public FeedStatus getFeedStatus() {
		return hasException ? FeedStatus.ERROR : FeedStatus.AVAILABLE;
	}

	public void afterPropertiesSet() throws Exception {
		hasException = false;
	}

	public String getID() {
		return "JMS";
	}


	public void onException(JMSException ex) {
		hasException = true;
		PhotonPlugin.getMainConsoleLogger().error("Message server exception", ex);
	}

	public ClassPathXmlApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public void setApplicationContext(
			ClassPathXmlApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public JmsOperations getJmsOperations() {
		return jmsOperations;
	}

	public void setJmsOperations(JmsOperations jmsOperations) {
		this.jmsOperations = jmsOperations;
	}



}
