package org.marketcetera.photon.messaging;


import javax.jms.ExceptionListener;
import javax.jms.JMSException;

import org.apache.xbean.spring.context.ClassPathXmlApplicationContext;
import org.marketcetera.marketdata.FeedStatus;
import org.marketcetera.photon.FeedComponentAdapterBase;
import org.marketcetera.photon.PhotonPlugin;
import org.osgi.framework.ServiceRegistration;
import org.springframework.jms.core.JmsOperations;

public class JMSFeedService extends FeedComponentAdapterBase implements ExceptionListener {

	JmsOperations jmsOperations;
	ClassPathXmlApplicationContext applicationContext;
	private boolean exceptionOccurred = false;
	private ServiceRegistration serviceRegistration;
	
	public FeedStatus getFeedStatus() {
		return exceptionOccurred ? FeedStatus.ERROR : FeedStatus.AVAILABLE;
	}

	public void afterPropertiesSet() throws Exception {
		exceptionOccurred = false;
	}

	public String getID() {
		return "JMS";
	}


	public void onException(JMSException ex) {
		onException((Exception)ex);
	}
	public void onException(Exception ex)
	{
		exceptionOccurred = true;
		PhotonPlugin.getMainConsoleLogger().error("Message server exception", ex);
		fireFeedComponentChanged();
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

	public void setServiceRegistration(ServiceRegistration serviceRegistration){
		this.serviceRegistration = serviceRegistration;
	}

	public ServiceRegistration getServiceRegistration() {
		return serviceRegistration;
	}

	@Override
	protected void fireFeedComponentChanged() {
		if (serviceRegistration != null)
			serviceRegistration.setProperties(null);
	}

	public boolean hasExceptionOccurred() {
		return exceptionOccurred;
	}

	public void setExceptionOccurred(boolean exceptionOccurred) {
		this.exceptionOccurred = exceptionOccurred;
	}


}
