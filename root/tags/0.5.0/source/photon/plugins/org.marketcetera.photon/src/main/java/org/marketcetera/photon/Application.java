package org.marketcetera.photon;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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

    /**
     * The system property name that contains photon installation
     * directory
     */
	private static final String APP_DIR_PROP="org.marketcetera.appDir";
	
	/**
	 * The configuration sub directory for the application
	 */
	private static final String CONF_DIR = "conf";

	/**
	 * Delay for rereading log4j configuration.
	 */
	private static final int LOGGER_WATCH_DELAY = 20*1000;


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
		configureLogs();
		
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


	/**
	 * Configure Logs
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void configureLogs() throws FileNotFoundException, IOException {
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
		//Figure out if the application install dir is specified
        String appDir=System.getProperty(APP_DIR_PROP);
        File confDir = null;
		// Configure loggers
        if(appDir != null) {
        	File dir = new File(appDir,CONF_DIR);
        	if(dir.isDirectory()) {
        		confDir = dir;
        	}
        }
        // Configure Java Logging
        boolean logConfigured = false;
        if (confDir != null) {
            File logConfig = new File(confDir,JAVA_LOGGING_CONFIG);
			if (logConfig.isFile()) {
				FileInputStream fis = null;
				try {
					fis = new FileInputStream(logConfig);
					LogManager.getLogManager().readConfiguration(fis);
					logConfigured = true;
				} catch (Exception ignored) {
				} finally {
					if (fis != null) {
						try {
							fis.close();
						} catch (IOException ignored) {
						}
					}
				}

			}
		}
		//Do default configuration, if its not already done.
        if(!logConfigured) {
    		LogManager.getLogManager().readConfiguration(getClass().
    				getClassLoader().getResourceAsStream(
    						JAVA_LOGGING_CONFIG));
        }
        
        // Configure Log4j
		// Remove default configuration done via log4j.properties file
		// present in one of the jars that we depend on 
		BasicConfigurator.resetConfiguration();
		logConfigured = false;
		if(confDir != null) {
	        File logConfig = new File(confDir,LOG4J_CONFIG);
	        if(logConfig.isFile()) {
	        	PropertyConfigurator.configureAndWatch(
	        			logConfig.getAbsolutePath(),LOGGER_WATCH_DELAY);
	        	logConfigured = true;
	        } 			
		}
        if(!logConfigured) {
    		//Do default log4j configuration, if its not already done.
    		PropertyConfigurator.configure(getClass().
    				getClassLoader().getResource(LOG4J_CONFIG));
        }
	}



	public void stop() {
		// Do nothing
		
	}

}
