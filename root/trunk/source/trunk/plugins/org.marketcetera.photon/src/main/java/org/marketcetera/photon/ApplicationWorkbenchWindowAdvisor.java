package org.marketcetera.photon;

import java.util.Date;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.internal.progress.ProgressManager;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.HttpDatabaseIDFactory;
import org.marketcetera.core.NoMoreIDsException;
import org.marketcetera.photon.actions.ReconnectJMSJob;
import org.marketcetera.photon.actions.ReconnectMarketDataFeedJob;
import org.marketcetera.photon.actions.StartScriptRegistryJob;
import org.marketcetera.photon.marketdata.MarketDataFeedTracker;
import org.marketcetera.photon.messaging.JMSFeedService;
import org.marketcetera.photon.ui.PhotonConsole;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Required by the RCP platform this class is responsible for setting up the
 * workbench upon startup.
 * @author gmiller
 *
 */
@ClassVersion("$Id$")
public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

    @Override
	public boolean preWindowShellClose() {
    	try {
    		stopJMS();
    	} catch (Throwable t){}
    	try {
    		stopMarketDataFeed();
    	} catch (Throwable t){}
    	return true;
    }

	/**
     * Simply calls superclass constructor.
     * @param configurer the configurer to pass to the superclass
     */
    public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        super(configurer);
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.application.WorkbenchWindowAdvisor#createActionBarAdvisor(org.eclipse.ui.application.IActionBarConfigurer)
     */
    public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
        return new ApplicationActionBarAdvisor(configurer);
    }
    
    /**
     * Sets a number of options on the IWorkbenchWindowConfigurer prior
     * to opening the window.
     * 
     * @see org.eclipse.ui.application.WorkbenchWindowAdvisor#preWindowOpen()
     */
    public void preWindowOpen() {
        IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
        configurer.setInitialSize(new Point(800, 600));
        configurer.setShowCoolBar(true);
        configurer.setShowStatusLine(true);
        configurer.setShowMenuBar(true);
//        IProduct product = Platform.getProduct();
//        String productName = product == null ? "" : product.getName()
//        configurer.setTitle(productName);
        configurer.setShowPerspectiveBar(true);
        configurer.setShowProgressIndicator(true);
    }
    
	/*
	 * Called after the window has opened, and all UI elements have been initialized,
	 * this method takes care of wiring UI components to
	 * the underlying model and controller elements.  For example it connects the
	 * Console view to a logger appender to feed it data.
	 * 
	 * 
	 * @see org.eclipse.ui.application.WorkbenchWindowAdvisor#postWindowOpen()
	 */
	@Override
	public void postWindowOpen() {


		IConsole[] consoles = ConsolePlugin.getDefault().getConsoleManager()
				.getConsoles();
		PhotonPlugin plugin = PhotonPlugin.getDefault();
		Logger mainConsoleLogger = plugin.getMainLogger();
		Logger marketDataLogger = plugin.getMarketDataLogger();
		PhotonConsole mainConsole = null;
		// loop through once to find the main console
		for (IConsole console : consoles) {
			if (console instanceof PhotonConsole) {
				PhotonConsole photonConsole = (PhotonConsole) console;
				if (PhotonPlugin.MAIN_CONSOLE_LOGGER_NAME.equals(photonConsole.getIdentifier())){
					mainConsole = photonConsole;
				}
			}
		}
		assert(mainConsole != null);
		mainConsoleLogger.addAppender(new PhotonConsoleAppender(mainConsole));
		// loop through a second time to find the secondary consoles.
		for (IConsole console : consoles) {
			PhotonConsole photonConsole = (PhotonConsole) console;
			if (PhotonPlugin.MARKETDATA_CONSOLE_LOGGER_NAME
					.equals(photonConsole.getIdentifier())) {
				marketDataLogger.addAppender(new PhotonConsoleAppender(photonConsole));
				// also output logging to the main console appender if the level is high enough
				mainConsoleLogger.addAppender(new PhotonConsoleAppender(mainConsole, Level.WARN));
			}
		}


		mainConsoleLogger.info(
				"Application initializing: " + new Date());

		plugin.ensureDefaultProject(ProgressManager.getInstance().getDefaultMonitor());
		StartScriptRegistryJob job = new StartScriptRegistryJob("Start script registry");
		job.schedule();
		startJMS();
		startMarketDataFeed();
		startIDFactory();
	}

	/** 
	 * Initializes the status line.
	 * 
	 */
	private void initStatusLine() {
		IStatusLineManager statusline = getWindowConfigurer()
				.getActionBarConfigurer().getStatusLineManager();
		statusline.setMessage("Online");
	}

	private void startIDFactory(){
		try {
			((HttpDatabaseIDFactory)PhotonPlugin.getDefault().getIDFactory()).grabIDs();
		} catch (NoMoreIDsException e) {
			PhotonPlugin.getMainConsoleLogger().warn("Error connecting to web app for ID base, reverting to built in IDFactory.");
		}
	}


	private void startJMS() {
		ReconnectJMSJob job = new ReconnectJMSJob("Reconnect message server");
		job.schedule();
	}


	private void stopJMS() {
		ReconnectJMSJob job = null;
		try {
			BundleContext bundleContext = PhotonPlugin.getDefault().getBundleContext();
			ServiceTracker jmsFeedTracker = new ServiceTracker(bundleContext, JMSFeedService.class.getName(), null);
			jmsFeedTracker.open();

			ReconnectJMSJob.disconnect(jmsFeedTracker);
		} catch (Throwable t){
			PhotonPlugin.getMainConsoleLogger().error("Could not disconnect from message queue", t);
		}
	}
	

	private void startMarketDataFeed() {
		ReconnectMarketDataFeedJob job = null;
		job = new ReconnectMarketDataFeedJob("Reconnect quote feed");
		job.schedule();
	}
	
	private void stopMarketDataFeed() {
		ReconnectMarketDataFeedJob job = null;
		try {
			BundleContext bundleContext = PhotonPlugin.getDefault().getBundleContext();
			MarketDataFeedTracker marketDataFeedTracker = new MarketDataFeedTracker(bundleContext);
			marketDataFeedTracker.open();

			ReconnectMarketDataFeedJob.disconnect(marketDataFeedTracker);
		} catch (Throwable t){
			PhotonPlugin.getMainConsoleLogger().error("Could not disconnect from message queue", t);
		}
	}
}
