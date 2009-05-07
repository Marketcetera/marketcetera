package org.marketcetera.photon;

import java.io.PrintStream;
import java.util.Date;

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
import org.marketcetera.marketdata.AbstractMarketDataFeed;
import org.marketcetera.photon.actions.ReconnectServerJob;
import org.marketcetera.photon.module.ui.ModuleUI;
import org.marketcetera.photon.notification.NotificationConsoleController;
import org.marketcetera.photon.ui.PhotonConsole;

/* $License$ */

/**
 * Sets up the workbench UI
 * 
 * @author gmiller
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor implements Messages {

	/**
	 * Constructor.
	 * 
	 * @param configurer
	 *            an object for configuring the workbench window
	 */
	public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}

	@Override
	public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
		return new ApplicationActionBarAdvisor(configurer);
	}

	@Override
	public void preWindowOpen() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		configurer.setInitialSize(new Point(1024, 768));
		configurer.setShowCoolBar(true);
		configurer.setShowStatusLine(true);
		configurer.setShowMenuBar(true);
		configurer.setShowPerspectiveBar(true);
		configurer.setShowProgressIndicator(true);

		PhotonPlugin.getDefault().initOrderTickets();
	}

	@Override
	public void postWindowOpen() {
		PhotonConsole photonConsole =
				new PhotonConsole(Messages.MainConsole_Name.getText(),
						PhotonPlugin.MAIN_CONSOLE_LOGGER_NAME);
		System.setOut(new PrintStream(photonConsole.getInfoMessageStream(), true));
		System.setErr(new PrintStream(photonConsole.getErrorMessageStream(), true));

		ConsolePlugin.getDefault().getConsoleManager()
				.addConsoles(new IConsole[] { photonConsole });
		ModuleUI.installSinkConsole();
		new NotificationConsoleController().openConsole();

		// activate the main console
		photonConsole.activate();

		PhotonPlugin.getMainConsoleLogger().addAppender(new PhotonConsoleAppender(photonConsole));
		Logger.getLogger(org.marketcetera.core.Messages.USER_MSG_CATEGORY).addAppender(
				new PhotonConsoleAppender(photonConsole));
		Logger.getLogger(AbstractMarketDataFeed.DATAFEED_STATUS_MESSAGES).addAppender(
				new PhotonConsoleAppender(photonConsole));

		PhotonPlugin.getMainConsoleLogger().info(
				ApplicationWorkbenchWindowAdvisor_ApplicationInitializing.getText(new Date()));

		PhotonPlugin.getDefault().ensureDefaultProject(
				ProgressManager.getInstance().getDefaultMonitor());

		// The login dialog interferes with testing, this check is to ensure tests are not being run
		if (PlatformUI.getTestableObject().getTestHarness() == null) {
			startClient();
		}
		PhotonPlugin.getDefault().reconnectMarketDataFeed();
		initStatusLine();
	}

	/**
	 * Initializes the status line.
	 */
	private void initStatusLine() {
		IStatusLineManager statusline =
				getWindowConfigurer().getActionBarConfigurer().getStatusLineManager();
		statusline.setMessage(ApplicationWorkbenchWindowAdvisor_OnlineLabel.getText());
	}

	private void startClient() {
		new ReconnectServerJob().schedule();
	}

	@Override
	public void createWindowContents(Shell shell) {
		super.createWindowContents(shell);
		// Could not do this declaratively due to http://bugs.eclipse.org/253232
		ITrimManager trimManager =
				((WorkbenchWindow) getWindowConfigurer().getWindow()).getTrimManager();
		IWindowTrim trim = trimManager.getTrim("org.marketcetera.photon.statusToolbar"); //$NON-NLS-1$
		IWindowTrim beforeMe = trimManager.getTrim("org.eclipse.jface.action.StatusLineManager"); //$NON-NLS-1$
		trimManager.addTrim(SWT.BOTTOM, trim, beforeMe);
	}
}
