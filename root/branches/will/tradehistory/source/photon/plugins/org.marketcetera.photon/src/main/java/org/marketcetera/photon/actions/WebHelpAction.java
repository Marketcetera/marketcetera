package org.marketcetera.photon.actions;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.marketcetera.photon.Messages;
import org.marketcetera.util.misc.ClassVersion;
/* $License$ */

/**
 * WebHelpAction opens the Marketcetera help site in an external 
 * browser, using {@link IWorkbenchBrowserSupport#createBrowser(String)}
 * and {@link IWebBrowser#openURL(URL)}.
 * 
 * @author gmiller
 * @version $Id$
 * @since $Release$
 *
 */
@ClassVersion("$Id$")
public class WebHelpAction 
    extends Action 
    implements IWorkbenchAction, Messages
{

	public static final String ID = "org.marketcetera.photon.actions.HelpBrowserAction"; //$NON-NLS-1$
	private static final String MAIN_HELP_URL = "http://trac.marketcetera.org/trac.fcgi/wiki/Marketcetera/PhotonGuide"; //$NON-NLS-1$
	/**
	 * Create the default instance of HelpBrowserAction, setting the ID, text,
	 * tool-tip text, and image to the defaults.
	 */
	public WebHelpAction(IWorkbenchWindow window){
		setId(ID);
		setText(WEB_HELP_ACTION.getText());
		setToolTipText(WEB_HELP_ACTION_DESCRIPTION.getText());
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
	 * Attempt to open help in a browser
	 * 
	 * @see org.eclipse.jface.action.Action#run()
	 */
	public void run() {

		//maybe do this at some point?
		//window.getWorkbench().getHelpSystem().displayHelpResource(MAIN_HELP_URL);
		// for now, just show it in an external browser
		
		IWorkbenchBrowserSupport browserSupport = PlatformUI.getWorkbench()
				.getBrowserSupport();
		IWebBrowser browser;
		try {
			browser = browserSupport.createBrowser("_blank"); //$NON-NLS-1$
			browser.openURL(new URL(MAIN_HELP_URL));
			
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}


