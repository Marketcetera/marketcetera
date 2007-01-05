package org.marketcetera.photon.messaging;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.progress.IProgressService;
import org.marketcetera.photon.FeedComponentAdapterBase;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.quickfix.ConnectionConstants;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.jms.core.JmsOperations;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;

public class JMSFeedComponentAdapter extends FeedComponentAdapterBase
	implements ExceptionListener {

	private IProgressService service;
	private PhotonPlugin photonPlugin;
	private AtomicBoolean failedOperation = new AtomicBoolean(false);

	public void afterPropertiesSet() throws Exception {
		failedOperation.set(true);
		try {
			if (service == null)
				throw new IllegalStateException("Progress service must not be null");
			if (photonPlugin == null)
				throw new IllegalStateException("Photon plugin must not be null");
			
			StaticApplicationContext brokerURLContext = getBrokerURLApplicationContext();
			final ClassPathXmlApplicationContext jmsApplicationContext;
		
			jmsApplicationContext = new ClassPathXmlApplicationContext(new String[]{"jms.xml"}, brokerURLContext);
	
			SimpleMessageListenerContainer photonControllerContainer = (SimpleMessageListenerContainer) jmsApplicationContext.getBean("photonControllerContainer");
			photonControllerContainer.setExceptionListener(this);
			failedOperation.set(false);
			
			service.run(true, true, new IRunnableWithProgress(){
				public void run(IProgressMonitor monitor){
					try {
						jmsApplicationContext.start();
						MessageListenerAdapter photonControllerListenerAdapter = (MessageListenerAdapter)jmsApplicationContext.getBean("photonControllerListener");
						photonControllerListenerAdapter.setDelegate(photonPlugin.getPhotonController());
						JmsOperations outgoingJmsOperations;
						outgoingJmsOperations = (JmsOperations)jmsApplicationContext.getBean("outgoingJmsTemplate");
						photonPlugin.getPhotonController().setJmsOperations(outgoingJmsOperations);
						
						photonPlugin.setOutgoingJMSOperations(outgoingJmsOperations);
					} catch (Throwable t){
						photonPlugin.getMainLogger().error("Error connecting to message server", t);
						failedOperation.set(true);
					} finally {
						fireFeedComponentChanged();
					}
				}
			});

		} finally {
			if (failedOperation.get()){
				fireFeedComponentChanged();
			}
		}
	}

	private static StaticApplicationContext getBrokerURLApplicationContext() {
		String url = PhotonPlugin.getDefault().getPreferenceStore().getString(ConnectionConstants.JMS_URL_KEY);
		StaticApplicationContext brokerURLContext;
		brokerURLContext = new StaticApplicationContext();
		if (url != null){
			RootBeanDefinition brokerURLBeanDefinition = new RootBeanDefinition(String.class);
			ConstructorArgumentValues constructorArgumentValues = new ConstructorArgumentValues();
			constructorArgumentValues.addGenericArgumentValue(url);
			brokerURLBeanDefinition.setConstructorArgumentValues(constructorArgumentValues );
			brokerURLContext.registerBeanDefinition("brokerURL", brokerURLBeanDefinition);
		}
		brokerURLContext.refresh();
		return brokerURLContext;
	}

	public void stop(PhotonPlugin plugin)
	{
		ClassPathXmlApplicationContext applicationContext = plugin.getJMSApplicationContext();
		if (applicationContext != null){
			applicationContext.stop();
			plugin.setOutgoingJMSOperations(null);
			applicationContext = null;
		}
	}

	public IProgressService getProgressService() {
		return service;
	}

	public void setProgressService(IProgressService service) {
		this.service = service;
	}

	public PhotonPlugin getPhotonPlugin() {
		return photonPlugin;
	}

	public void setPhotonPlugin(PhotonPlugin photonPlugin) {
		this.photonPlugin = photonPlugin;
	}

	@Override
	public FeedStatus getFeedStatus() {
		if (failedOperation.get()){
			return FeedStatus.ERROR;
		} else {
			return FeedStatus.AVAILABLE;
		}
	}

	public void onException(JMSException e) {
		PhotonPlugin.getMainConsoleLogger().error(e.getMessage(), e);
		if (!failedOperation.getAndSet(true)){
			fireFeedComponentChanged();
		}
	}

	public String getID() {
		return "JMS";
	}
	
}
