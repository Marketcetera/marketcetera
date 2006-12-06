package org.marketcetera.photon.actions;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.progress.IProgressService;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.photon.Application;
import org.marketcetera.photon.IImageKeys;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.quickfix.ConnectionConstants;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.jms.core.JmsOperations;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;

/**
 * RCP platform action responsible for initiating a reconnect of the 
 * application's connection to the JMS server.
 * 
 * @author gmiller
 * @see Application#initJMSConnector()
 */
@ClassVersion("$Id$")
public class ReconnectJMSAction extends Action implements IWorkbenchAction {

	public static final String ID = "org.marketcetera.photon.actions.ReconnectJMSAction";
	private IWorkbenchWindow window;
	
	/**
	 * Create the default instance of ReconnectJMSAction, setting the ID, text,
	 * tool-tip text, and image to the defaults.
	 */
	public ReconnectJMSAction(IWorkbenchWindow window){
		this.window = window;
		setId(ID);
		setText("&Reconnect Message Connection");
		setToolTipText("Reconnect to the message server");
		setImageDescriptor(PhotonPlugin.getImageDescriptor(IImageKeys.RECONNECT_JMS_HISTORY));
	}
	/**
	 *  
	 * Default implementation does nothing.
	 * 
	 * @see org.eclipse.ui.actions.ActionFactory$IWorkbenchAction#dispose()
	 */
	public void dispose() {
		// TODO Auto-generated method stub
	}

	/**
	 * Attempt to reconnect to the JMS server.
	 * 
	 */
	public void run() {
		PhotonPlugin plugin = PhotonPlugin.getDefault();
		stopJMS(plugin);
		startJMS(window.getWorkbench().getProgressService(), plugin);
	}

	
	public static void stopJMS(PhotonPlugin plugin)
	{
		ClassPathXmlApplicationContext applicationContext = plugin.getJMSApplicationContext();
		if (applicationContext != null){
			plugin.setJMSApplicationContext(null);
			applicationContext.stop();
			plugin.setOutgoingJMSOperations(null);
		}
	}
	
	public static void startJMS(IProgressService service, final PhotonPlugin plugin)
	{
		StaticApplicationContext brokerURLContext = getBrokerURLApplicationContext();
		final ClassPathXmlApplicationContext jmsApplicationContext;

		jmsApplicationContext = new ClassPathXmlApplicationContext(new String[]{"jms.xml"}, brokerURLContext);

		try {
			service.run(true, true, new IRunnableWithProgress(){
				public void run(IProgressMonitor monitor){
					jmsApplicationContext.start();
					MessageListenerAdapter photonControllerListenerAdapter = (MessageListenerAdapter)jmsApplicationContext.getBean("photonControllerListener");
					photonControllerListenerAdapter.setDelegate(plugin.getPhotonController());
					JmsOperations outgoingJmsOperations;
					outgoingJmsOperations = (JmsOperations)jmsApplicationContext.getBean("outgoingJmsTemplate");
					plugin.getPhotonController().setJmsOperations(outgoingJmsOperations);
					
					plugin.setJMSApplicationContext(jmsApplicationContext);
					plugin.setOutgoingJMSOperations(outgoingJmsOperations);
				}
			});
		} catch (InvocationTargetException e) {
			plugin.getMainLogger().error("Exception connecting to message server", e);
		} catch (InterruptedException e) {
			plugin.getMainLogger().error("User cancelled connection to message server");
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

}
