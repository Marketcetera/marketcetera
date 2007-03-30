/**
 * 
 */
package org.marketcetera.photon.actions;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.jms.JMSException;

import org.apache.log4j.Logger;
import org.apache.xbean.spring.context.ClassPathXmlApplicationContext;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.messaging.JMSFeedService;
import org.marketcetera.photon.messaging.PhotonControllerListenerAdapter;
import org.marketcetera.photon.messaging.SimpleMessageListenerContainer;
import org.marketcetera.quickfix.ConnectionConstants;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.jms.UncategorizedJmsException;
import org.springframework.jms.core.JmsOperations;

/**
 * @author gmiller
 *
 */
public class ReconnectJMSJob extends Job {

	

	private static AtomicBoolean reconnectInProgress = new AtomicBoolean(false);
	private final boolean disconnectOnly;

	public ReconnectJMSJob(String name) {
		this(name, false);
	}
	
	public ReconnectJMSJob(String name, boolean disconnectOnly) {
		super(name);
		this.disconnectOnly = disconnectOnly;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		if (reconnectInProgress.getAndSet(true))
			return Status.CANCEL_STATUS;

		ServiceTracker jmsFeedTracker = null;
		BundleContext bundleContext;

		try {
	
			bundleContext = PhotonPlugin.getDefault().getBundleContext();
			jmsFeedTracker = new ServiceTracker(bundleContext, JMSFeedService.class.getName(), null);
			jmsFeedTracker.open();
	
			Logger logger = PhotonPlugin.getDefault().getMainLogger();
			
			try {
				monitor.beginTask("Disconnect message server", 2);
				disconnect(jmsFeedTracker);
			} catch (Exception ex){
				logger.info("Exception disconnecting from JMS", ex);
			}
			
			if (!disconnectOnly){
				boolean succeeded = false;
		
				JMSFeedService feedService = new JMSFeedService();
				ServiceRegistration registration = bundleContext.registerService(JMSFeedService.class.getName(), feedService, null);
				feedService.setServiceRegistration(registration);
		
				try {
					monitor.beginTask("Connect message server", 3);
					String url = PhotonPlugin.getDefault().getPreferenceStore().getString(ConnectionConstants.JMS_URL_KEY);
					StaticApplicationContext brokerURLContext = getBrokerURLApplicationContext(url);
					final ClassPathXmlApplicationContext jmsApplicationContext;
				
					jmsApplicationContext = new ClassPathXmlApplicationContext(new String[]{"jms.xml"}, brokerURLContext);
			
					SimpleMessageListenerContainer photonControllerContainer = (SimpleMessageListenerContainer) jmsApplicationContext.getBean("photonControllerContainer");
					photonControllerContainer.setExceptionListener(feedService);
					
					PhotonControllerListenerAdapter photonControllerAdapter = (PhotonControllerListenerAdapter) jmsApplicationContext.getBean("photonControllerListener");
					photonControllerAdapter.setPhotonController(PhotonPlugin.getDefault().getPhotonController());
		
					monitor.worked(1);
					
					jmsApplicationContext.start();
					feedService.setApplicationContext(jmsApplicationContext);
					
					JmsOperations outgoingJmsOperations;
					outgoingJmsOperations = (JmsOperations)jmsApplicationContext.getBean("outgoingJmsTemplate");
		
					feedService.setJmsOperations(outgoingJmsOperations);
					feedService.afterPropertiesSet();
					monitor.worked(1);
		
					succeeded = true;
					logger.info("Message queue connected ("+url+")");
				} catch (BeanCreationException bce){
					Throwable toLog = bce.getCause();
					if (toLog instanceof UncategorizedJmsException)
						toLog = toLog.getCause();
					logger.error("Error connecting to message server", toLog);
				} catch (Throwable t){
					logger.error("Error connecting to message server", t);
				} finally {
					reconnectInProgress.set(false);
					feedService.setExceptionOccurred(!succeeded);
					registration.setProperties(null);
					monitor.done();
				}
			}
		} finally {
			if (jmsFeedTracker != null) {
				jmsFeedTracker.close();
			}
		}
		return Status.OK_STATUS;
	}
	private static StaticApplicationContext getBrokerURLApplicationContext(String url) {
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

	public static void disconnect(ServiceTracker jmsFeedTracker)
	{
		JMSFeedService feed = (JMSFeedService) jmsFeedTracker.getService();

		if (feed != null){
			ServiceRegistration serviceRegistration;
			if (((serviceRegistration = feed.getServiceRegistration())!=null)){
				serviceRegistration.unregister();
			}
	
			try {
				SimpleMessageListenerContainer photonControllerContainer = (SimpleMessageListenerContainer) feed.getApplicationContext().getBean("photonControllerContainer");
				photonControllerContainer.setExceptionListener(null);
			} catch (Exception e) {
			}
			RuntimeException caughtException = null;
			if (feed != null){
				ClassPathXmlApplicationContext applicationContext = feed.getApplicationContext();
				if (applicationContext != null){
					try {
						applicationContext.stop();
					} catch (RuntimeException ex){
						caughtException = ex;
					}
				}
			}
			if (caughtException != null){
				throw caughtException;
			}
		}
	}

	@Override
	public boolean shouldRun() {
		return !reconnectInProgress.get();
	}

	@Override
	public boolean shouldSchedule() {
		return !reconnectInProgress.get();
	}

	
}
