package org.marketcetera.photon;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IPlatformRunnable;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.marketcetera.core.IDFactory;
import org.marketcetera.core.InMemoryIDFactory;

/**
 * This class controls all aspects of the application's execution
 */
public class Application implements IPlatformRunnable {

	public static String DEBUG_CONSOLE_LOGGER_NAME = "debug.console.logger";
    private static Logger debugConsoleLogger = Logger.getLogger(DEBUG_CONSOLE_LOGGER_NAME);
    private static IDFactory idFactory = new InMemoryIDFactory(777);
	private static OrderManager orderManager;

	public static final String PLUGIN_ID = "org.marketcetera.photon";
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IPlatformRunnable#run(java.lang.Object)
	 */
	public Object run(Object args) throws Exception {
		JMSConnector.init();
		orderManager = new OrderManager(idFactory);
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

	public static Logger getDebugConsoleLogger()
	{
		return debugConsoleLogger;
	}
	
	public static OrderManager getOrderManager()
	{
		return orderManager;
	}
}
