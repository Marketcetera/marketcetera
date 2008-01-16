package org.marketcetera.photon.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.photon.IImageKeys;
import org.marketcetera.photon.PhotonPlugin;

/**
 * RCP platform action responsible for initiating a reconnect of the 
 * application's connection to the JMS server.
 * 
 * @author gmiller
 * @see ReconnectMarketDataFeedJob#schedule()
 */
@ClassVersion("$Id: ReconnectJMSAction.java 677 2007-01-05 01:21:20Z gmiller $")
public class ReconnectMarketDataFeedAction extends Action implements IWorkbenchAction {

	public static final String ID = "org.marketcetera.photon.actions.ReconnectJMSAction";
	private IWorkbenchWindow window;
	
	/**
	 * Create the default instance of ReconnectJMSAction, setting the ID, text,
	 * tool-tip text, and image to the defaults.
	 */
	public ReconnectMarketDataFeedAction(IWorkbenchWindow window){
		this.window = window;
		setId(ID);
		setText("Reconnect &Quote Feed");
		setToolTipText("Reconnect to the quote feed");
		setImageDescriptor(PhotonPlugin.getImageDescriptor(IImageKeys.RECONNECT_QUOTE_FEED));
	}
	/**
	 *  
	 * Default implementation does nothing.
	 * 
	 * @see org.eclipse.ui.actions.ActionFactory$IWorkbenchAction#dispose()
	 */
	public void dispose() {
	}

	/**
	 * Attempt to reconnect to the JMS server.
	 * 
	 */
	public void run() {
		ReconnectMarketDataFeedJob job = new ReconnectMarketDataFeedJob("Reconnect quote feed");
		job.schedule();
	}

	
}
