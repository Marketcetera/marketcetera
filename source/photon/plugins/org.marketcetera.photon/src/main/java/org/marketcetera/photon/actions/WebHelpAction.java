package org.marketcetera.photon.actions;

import java.net.URL;
import java.text.MessageFormat;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * WebHelpAction opens the Marketcetera help site in an external browser, using
 * {@link IWorkbenchBrowserSupport#createBrowser(String)} and
 * {@link IWebBrowser#openURL(URL)}.
 * 
 * @author gmiller
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public class WebHelpAction extends Action {

	public static final String ID = "org.marketcetera.photon.actions.HelpBrowserAction"; //$NON-NLS-1$
	private static final String MAIN_HELP_URL = MessageFormat
			.format(
					"http://www.marketcetera.com/masha/docs?version={0}&qualifier=photon", //$NON-NLS-1$
					PhotonPlugin.getDefault().getBundle().getHeaders().get(
							"Bundle-Version")); //$NON-NLS-1$
	private IWorkbenchWindow mWindow;

	/**
	 * Create the default instance of HelpBrowserAction, setting the ID, text,
	 * tool-tip text, and image to the defaults.
	 */
	public WebHelpAction(IWorkbenchWindow window) {
		setId(ID);
		setText(Messages.WEB_HELP_ACTION.getText());
		setToolTipText(Messages.WEB_HELP_ACTION_DESCRIPTION.getText());
		mWindow = window;
	}

	/**
	 * Attempt to open help in a browser
	 */
	@Override
	public void run() {
		// maybe do this at some point?
		// window.getWorkbench().getHelpSystem().displayHelpResource(MAIN_HELP_URL);
		// for now, just show it in an external browser
		try {
			IWorkbenchBrowserSupport browserSupport = mWindow.getWorkbench()
					.getBrowserSupport();
			IWebBrowser browser = browserSupport.createBrowser("_blank"); //$NON-NLS-1$
			browser.openURL(new URL(MAIN_HELP_URL));
		} catch (Exception e) {
			Messages.WEB_HELP_ERROR.error(this, e, MAIN_HELP_URL);
			ErrorDialog.openError(mWindow.getShell(), null,
					Messages.WEB_HELP_ERROR.getText(MAIN_HELP_URL), new Status(
							IStatus.ERROR, PhotonPlugin.ID, e
									.getLocalizedMessage()));
		}
	}
}
