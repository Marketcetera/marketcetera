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
import org.marketcetera.photon.messaging.JMSFeedComponentAdapter;
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
		JMSFeedComponentAdapter adapter = plugin.getJMSFeedComponentAdapter();
		stopJMS(adapter, plugin);
		startJMS(adapter, window.getWorkbench().getProgressService(), plugin);
	}

	
	public static void stopJMS(JMSFeedComponentAdapter adapter, PhotonPlugin plugin)
	{
		adapter.stop(plugin);
	}
	
	public static void startJMS(JMSFeedComponentAdapter adapter, IProgressService service, final PhotonPlugin plugin)
	{
		try {
			adapter.setPhotonPlugin(plugin);
			adapter.setProgressService(service);
			adapter.afterPropertiesSet();
		} catch (Exception e) {
			PhotonPlugin.getMainConsoleLogger().error("Exception starting JMS connection", e);
		}
	}
	

}
