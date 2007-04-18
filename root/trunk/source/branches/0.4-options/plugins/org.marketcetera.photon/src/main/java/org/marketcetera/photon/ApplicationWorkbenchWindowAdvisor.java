package org.marketcetera.photon;

import java.util.Date;

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
import org.marketcetera.photon.ui.MainConsole;
import org.marketcetera.photon.views.OptionOrderTicket;
import org.marketcetera.photon.views.OptionOrderTicketController;
import org.marketcetera.photon.views.StockOrderTicket;
import org.marketcetera.photon.views.StockOrderTicketController;
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
		Logger mainConsoleLogger = PhotonPlugin.getMainConsoleLogger();
		for (IConsole console : consoles) {
			if (console instanceof MainConsole) {
				MainConsole mainConsole = (MainConsole) console;
				PhotonConsoleAppender appender = new PhotonConsoleAppender(
						mainConsole);
				mainConsoleLogger.addAppender(appender);
			}
		} 


		mainConsoleLogger.info(
				"Application initializing: " + new Date());

		PhotonPlugin plugin = PhotonPlugin.getDefault();
		plugin.ensureDefaultProject(ProgressManager.getInstance().getDefaultMonitor());
		StartScriptRegistryJob job = new StartScriptRegistryJob("Start script registry");
		job.schedule();
		startJMS();
		startMarketDataFeed();
		startIDFactory();
		// todo: A new controller should be associated with each new view. This method, postWindowOpen, is not executed after view creation. If a new StockOrderTicket view is created, it will not have a controller.  
		if (StockOrderTicket.getDefault() != null) {
			plugin.setStockOrderTicketController(new StockOrderTicketController(
							StockOrderTicket.getDefault()));
		}
		if (OptionOrderTicket.getDefault() != null) {
			plugin.setOptionOrderTicketController(new OptionOrderTicketController(
							OptionOrderTicket.getDefault()));
		}
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
