package org.marketcetera.photon;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.core.internal.jobs.JobStatus;
import org.eclipse.core.runtime.IPlatformRunnable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.HttpDatabaseIDFactory;
import org.marketcetera.core.NoMoreIDsException;
import org.marketcetera.photon.actions.ReconnectJMSAction;
import org.marketcetera.photon.messaging.SimpleMessageListenerContainer;
import org.marketcetera.photon.preferences.PhotonPage;
import org.marketcetera.quickfix.ConnectionConstants;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.jms.core.JmsOperations;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;

/**
 * This class provides methods for some basic application services,
 * as well as singleton member variables for Photon components, along
 * with static getters.
 * 
 * @author gmiller
 *
 */
@ClassVersion("$Id$")
public class Application implements IPlatformRunnable, IPropertyChangeListener {


	
	/**
	 * This method is called by the Eclipse RCP, and therefore is the main 
	 * entry point to the Application.  This method initializes the FIX version
	 * for the application (to FIX 4.2), the FIXMessageHistory, the JMSConnector
	 * the OrderManager.  Finally it creates the display, and creates and runs
	 * the workbench.
	 * 
	 * @see org.eclipse.core.runtime.IPlatformRunnable#run(java.lang.Object)
	 * @see PlatformUI#createDisplay()
	 * @see PlatformUI#createAndRunWorkbench(Display, org.eclipse.ui.application.WorkbenchAdvisor)
	 */
	public Object run(Object args) throws Exception {
		PhotonPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(this);

		
		Display display = PlatformUI.createDisplay();
		try {
			int returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
			if (returnCode == PlatformUI.RETURN_RESTART) {
				return IPlatformRunnable.EXIT_RESTART;
			}
			return IPlatformRunnable.EXIT_OK;
		} finally {
			display.dispose();
		}

	}

	
	private void changeLogLevel(String levelValue){
		Logger logger = PhotonPlugin.getMainConsoleLogger();
		if (PhotonPage.LOG_LEVEL_VALUE_ERROR.equals(levelValue)){
			logger.setLevel(Level.ERROR);
		} else if (PhotonPage.LOG_LEVEL_VALUE_WARN.equals(levelValue)){
			logger.setLevel(Level.WARN);
		} else if (PhotonPage.LOG_LEVEL_VALUE_INFO.equals(levelValue)){
			logger.setLevel(Level.INFO);
		} else if (PhotonPage.LOG_LEVEL_VALUE_DEBUG.equals(levelValue)){
			logger.setLevel(Level.DEBUG);
		}
		logger.info("Changed log level to '"+levelValue+"'");
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(PhotonPage.LOG_LEVEL_KEY)){
			changeLogLevel(""+event.getNewValue());
		}
	}

}
