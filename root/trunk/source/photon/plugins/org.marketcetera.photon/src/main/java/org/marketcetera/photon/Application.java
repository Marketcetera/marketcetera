package org.marketcetera.photon;

import java.lang.management.ManagementFactory;
import java.util.logging.LogManager;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.photon.preferences.PhotonPage;

/**
 * This class provides methods for some basic application services,
 * as well as singleton member variables for Photon components, along
 * with static getters.
 * 
 * @author gmiller
 *
 */
@ClassVersion("$Id$")
public class Application implements IApplication, IPropertyChangeListener {


	/**
	 * The system property that is set to a unique number for every photon
	 * process. An attempt is made to use the pid value as the value of this
	 * property. However, if that doesn't work, the system time at the time
	 * this property is set, is set as the value of this property. 
	 */
	private static final String PROCESS_UNIQUE_PROPERTY = "org.marketcetera.photon.unique";
	/**
	 * log4j configuration file name.
	 */
	private static final String LOG4J_CONFIG = "photon-log4j.properties";
	/**
	 * java logging configuration file name.
	 */
	private static final String JAVA_LOGGING_CONFIG = "java.util.logging.properties";



	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(PhotonPage.LOG_LEVEL_KEY)){
			PhotonPlugin.getDefault().changeLogLevel(""+event.getNewValue());
		}
	}


	/**
	 * @see IApplication#start(IApplicationContext)
 	 * @see PlatformUI#createDisplay()
	 * @see PlatformUI#createAndRunWorkbench(Display, org.eclipse.ui.application.WorkbenchAdvisor)
	 */
	public Object start(IApplicationContext context) throws Exception {
		// Fetch the java process ID. Do note that this mechanism relies on
		// a non-public interface of the jvm but its very useful to be able
		// to use the pid.
		String id = ManagementFactory.getRuntimeMXBean().getName().replaceAll("[^0-9]", "");
		if(id == null || id.trim().length() < 1) {
			id = String.valueOf(System.currentTimeMillis());  
		}
		// Supply the pid as a system property so that it can be used in
		// log 4j configuration
		System.setProperty(PROCESS_UNIQUE_PROPERTY,id);
		// Configure loggers
		LogManager.getLogManager().readConfiguration(getClass().
				getClassLoader().getResourceAsStream(
						JAVA_LOGGING_CONFIG));
		// Remove default configuration done via log4j.properties file
		// present in one of the jars that we depend on 
		BasicConfigurator.resetConfiguration();
		//Explicitly configure log4j via photon log4j configuration file.
		PropertyConfigurator.configure(getClass().
				getClassLoader().getResource(LOG4J_CONFIG));
		
		PhotonPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(this);

		Display display = PlatformUI.createDisplay();
		try {
			int returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
			if (returnCode == PlatformUI.RETURN_RESTART) {
				return IApplication.EXIT_RESTART;
			}
			return IApplication.EXIT_OK;
		} finally {
			display.dispose();
		}
	}



	public void stop() {
		// Do nothing
		
	}

}
