package org.marketcetera.photon;

import java.util.Date;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.internal.WorkbenchWindow;
import org.eclipse.ui.internal.layout.ITrimManager;
import org.eclipse.ui.internal.layout.IWindowTrim;
import org.eclipse.ui.internal.progress.ProgressManager;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.photon.actions.ReconnectClientJob;
import org.marketcetera.photon.messaging.ClientFeedService;
import org.marketcetera.photon.ui.PhotonConsole;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Required by the RCP platform this class is responsible for setting up the
 * workbench upon startup.
 * @author gmiller
 *
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class ApplicationWorkbenchWindowAdvisor 
    extends WorkbenchWindowAdvisor
    implements Messages
{

    @Override
	public boolean preWindowShellClose() {
    	try {
    		stopClient();
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

		PhotonPlugin.getDefault().initOrderTickets();
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
		Logger.getLogger(PhotonPlugin.STRATEGY_LOGGER_NAME).addAppender(new PhotonConsoleAppender(mainConsole)); //$NON-NLS-1$
		// loop through a second time to find the secondary consoles.
		for (IConsole console : consoles) {
			PhotonConsole photonConsole = (PhotonConsole) console;
			if (PhotonPlugin.MARKETDATA_CONSOLE_LOGGER_NAME
					.equals(photonConsole.getIdentifier())) {
				PhotonConsoleAppender photonConsoleAppender = new PhotonConsoleAppender(photonConsole);
				marketDataLogger.addAppender(photonConsoleAppender);
				// also output logging to the main console appender if the level is high enough
				photonConsoleAppender.setSecondaryConsole(mainConsole, Level.WARN);
			}
		}


		mainConsoleLogger.info(ApplicationWorkbenchWindowAdvisor_ApplicationInitializing.getText(new Date()));

		plugin.ensureDefaultProject(ProgressManager.getInstance().getDefaultMonitor());
		
		// The login dialog interferes with testing, this check is to ensure tests are not being run
		if (PlatformUI.getTestableObject().getTestHarness() == null) {
			startClient();
		}
		startMarketDataFeed();
		initStatusLine();
	}

	/** 
	 * Initializes the status line.
	 * 
	 */
	private void initStatusLine() {
		IStatusLineManager statusline = getWindowConfigurer().getActionBarConfigurer().getStatusLineManager();
		statusline.setMessage(ApplicationWorkbenchWindowAdvisor_OnlineLabel.getText()); 
	}

	private void startClient() {
		ReconnectClientJob job = new ReconnectClientJob(RECONNECT_MESSAGE_SERVER.getText());
		job.schedule();
	}


	private void stopClient() {
		try {
			BundleContext bundleContext = PhotonPlugin.getDefault().getBundleContext();
			ServiceTracker clientFeedTracker = new ServiceTracker(bundleContext, 
					ClientFeedService.class.getName(), null);
			clientFeedTracker.open();

			ReconnectClientJob.disconnect(clientFeedTracker);
		} catch (Throwable t){
			PhotonPlugin.getMainConsoleLogger().error(CANNOT_DISCONNECT_FROM_MESSAGE_QUEUE.getText(),
			                                          t);
		}
	}
	

	private void startMarketDataFeed() {
		PhotonPlugin.getDefault().getMarketDataManager().reconnectFeed();
	}
	
	@Override
	public void createWindowContents(Shell shell) {
		super.createWindowContents(shell);
		// Could not do this declaratively due to https://bugs.eclipse.org/bugs/show_bug.cgi?id=253232 
		ITrimManager trimManager = ((WorkbenchWindow) getWindowConfigurer().getWindow()).getTrimManager();
		IWindowTrim trim = trimManager.getTrim("org.marketcetera.photon.statusToolbar"); //$NON-NLS-1$
		IWindowTrim beforeMe = trimManager.getTrim("org.eclipse.jface.action.StatusLineManager"); //$NON-NLS-1$
		trimManager.addTrim(SWT.BOTTOM, trim, beforeMe);
	}
}
