package org.marketcetera.photon.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;
import org.apache.xbean.spring.context.ClassPathXmlApplicationContext;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWTException;
import org.eclipse.ui.progress.UIJob;
import org.marketcetera.core.ApplicationBase;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.messaging.JMSFeedService;
import org.marketcetera.photon.messaging.PhotonControllerListenerAdapter;
import org.marketcetera.photon.messaging.ScriptingControllerListenerAdapter;
import org.marketcetera.photon.messaging.SimpleMessageListenerContainer;
import org.marketcetera.photon.ui.LoginDialog;
import org.marketcetera.quickfix.ConnectionConstants;
import org.osgi.framework.BundleContext;
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
public class ReconnectJMSJob
    extends UIJob
    implements Messages
{

	

	private static AtomicBoolean reconnectInProgress = new AtomicBoolean(false);
	private final boolean disconnectOnly;

	private static class CreateApplicationContextRunnable
		implements IRunnableWithProgress
	{
		private ClassPathXmlApplicationContext jmsApplicationContext=null;
		private Throwable failure=null;
		private StaticApplicationContext brokerContext;
		
		public CreateApplicationContextRunnable(StaticApplicationContext brokerContext) {
			this.brokerContext = brokerContext;
		}

		@Override
		public void run(IProgressMonitor monitor)
		throws InvocationTargetException,
		InterruptedException {
			try {
				setJmsApplicationContext(new ClassPathXmlApplicationContext(new String[]{"jms.xml"}, brokerContext)); //$NON-NLS-1$
			} catch (Throwable ex) {
				setFailure(ex);
			}				
		}
		
		public ClassPathXmlApplicationContext getJmsApplicationContext() {
			return jmsApplicationContext;
		}

		public void setJmsApplicationContext(
				ClassPathXmlApplicationContext jmsApplicationContext) {
			this.jmsApplicationContext = jmsApplicationContext;
		}

		public Throwable getFailure() {
			return failure;
		}

		public void setFailure(Throwable failure) {
			this.failure = failure;
		}
	}

	
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
	public IStatus runInUIThread(IProgressMonitor monitor) {
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
				monitor.beginTask(DISCONNECT_MESSAGE_SERVER.getText(),
				                  2);
				disconnect(jmsFeedTracker);
			} catch (Exception ex){
				logger.error(CANNOT_DISCONNECT_FROM_MESSAGE_QUEUE.getText(),
				             ex);
			}
			
			if (!disconnectOnly){
				boolean succeeded = false;
		
				JMSFeedService feedService = new JMSFeedService();
				ServiceRegistration registration = bundleContext.registerService(JMSFeedService.class.getName(), feedService, null);
				feedService.setServiceRegistration(registration);
		
				try {
					monitor.beginTask(RECONNECT_MESSAGE_SERVER.getText(),
					                  4);

					String url = PhotonPlugin.getDefault().getPreferenceStore().getString(ConnectionConstants.JMS_URL_KEY);
					ClassPathXmlApplicationContext jmsApplicationContext=null;
					Random random=new Random();
					LoginDialog loginDialog = new LoginDialog(null);
					while (true){
						try {
							if (loginDialog.open() != Window.OK){
								return Status.CANCEL_STATUS;
							}
						} catch (SWTException ex) {
							logger.error(CANNOT_SHOW_ORS_DIALOG.getText(),
							             ex);
							return Status.CANCEL_STATUS;
						}
						ConnectionDetails details=loginDialog.getConnectionDetails();
						StaticApplicationContext brokerContext =
							getBrokerApplicationContext(url,details.getUserId(),details.getPassword());
						ProgressMonitorDialog progress = new ProgressMonitorDialog(null);
						progress.setCancelable(false);
						CreateApplicationContextRunnable runnable=new CreateApplicationContextRunnable(brokerContext);
						try {
							progress.run(true, true,runnable);
						} catch (InvocationTargetException ex){
							logger.error(CANNOT_SHOW_PROGRESS_DIALOG.getText(),
							             ex);
							return Status.CANCEL_STATUS;
						} catch (InterruptedException ex){
                            logger.error(CANNOT_SHOW_PROGRESS_DIALOG.getText(),
                                         ex);
							return Status.CANCEL_STATUS;
						} 
						Throwable failure=runnable.getFailure();
						if (failure==null) {
							jmsApplicationContext=runnable.getJmsApplicationContext();
							break;
						}
						Thread.sleep(500+random.nextInt(1000));
						logger.error(JMS_CONNECTION_FAILED.getText(),
						             failure);
					}
			
					SimpleMessageListenerContainer photonControllerContainer = (SimpleMessageListenerContainer) jmsApplicationContext.getBean("photonControllerContainer"); //$NON-NLS-1$
					photonControllerContainer.setExceptionListener(feedService);
					
					PhotonControllerListenerAdapter photonControllerAdapter = (PhotonControllerListenerAdapter) jmsApplicationContext.getBean("photonControllerListener"); //$NON-NLS-1$
					photonControllerAdapter.setPhotonController(PhotonPlugin.getDefault().getPhotonController());

					monitor.worked(1);
					
					SimpleMessageListenerContainer scriptingControllerContainer = (SimpleMessageListenerContainer) jmsApplicationContext.getBean("scriptingControllerContainer"); //$NON-NLS-1$
					scriptingControllerContainer.setExceptionListener(feedService);
					
					ScriptingControllerListenerAdapter scriptingControllerAdapter = (ScriptingControllerListenerAdapter) jmsApplicationContext.getBean("scriptingControllerListener"); //$NON-NLS-1$
					scriptingControllerAdapter.setScriptRegistry(PhotonPlugin.getDefault().getScriptRegistry());

					monitor.worked(1);
					
					jmsApplicationContext.start();
					feedService.setApplicationContext(jmsApplicationContext);
					
					JmsOperations outgoingJmsOperations;
					outgoingJmsOperations = (JmsOperations)jmsApplicationContext.getBean("outgoingJmsTemplate"); //$NON-NLS-1$
		
					feedService.setJmsOperations(outgoingJmsOperations);
					feedService.afterPropertiesSet();
					monitor.worked(1);
		
					succeeded = true;
					logger.info(MESSAGE_QUEUE_CONNECTED.getText(url));
				} catch (BeanCreationException bce){
					Throwable toLog = bce.getCause();
					if (toLog instanceof UncategorizedJmsException)
						toLog = toLog.getCause();
					logger.error(CANNOT_CONNECT_TO_MESSAGE_QUEUE.getText(),
					             toLog);
				} catch (Throwable t){
                    logger.error(CANNOT_CONNECT_TO_MESSAGE_QUEUE.getText(),
                                 t);
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

    private static void inject
        (StaticApplicationContext context,
         String name,
         String value)
    {
        if (value==null) {
            return;
        }
        RootBeanDefinition bean = new RootBeanDefinition(String.class);
        ConstructorArgumentValues constructorArgumentValues = new ConstructorArgumentValues();
        constructorArgumentValues.addGenericArgumentValue(value);
        bean.setConstructorArgumentValues(constructorArgumentValues );
        context.registerBeanDefinition(name,bean);
    }

	private static StaticApplicationContext getBrokerApplicationContext
        (String url,
         String username,
         String password) {
		StaticApplicationContext context;
		context=new StaticApplicationContext();
        inject(context,"brokerURL",url); //$NON-NLS-1$
        inject(context,ApplicationBase.USERNAME_BEAN_NAME,username);
        inject(context,ApplicationBase.PASSWORD_BEAN_NAME,password);
		context.refresh();
		return context;
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
				SimpleMessageListenerContainer photonControllerContainer = (SimpleMessageListenerContainer) feed.getApplicationContext().getBean("photonControllerContainer"); //$NON-NLS-1$
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
