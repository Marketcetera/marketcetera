package org.marketcetera.photon.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.photon.IImageKeys;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.PhotonPlugin;

/**
 * RCP platform action responsible for initiating a reconnect of the 
 * application's connection to the JMS server.
 * 
 * @author gmiller
 * @see ReconnectMarketDataFeedJob#schedule()
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class ReconnectMarketDataFeedAction 
    extends Action 
    implements IWorkbenchAction, Messages
{

	public static final String ID = "org.marketcetera.photon.actions.ReconnectJMSAction"; //$NON-NLS-1$
	/**
	 * Create the default instance of ReconnectJMSAction, setting the ID, text,
	 * tool-tip text, and image to the defaults.
	 */
	public ReconnectMarketDataFeedAction(IWorkbenchWindow window){
		setId(ID);
		setText(RECONNECT_MARKET_DATA_FEED_ACTION.getText());
		setToolTipText(RECONNECT_MARKET_DATA_FEED_ACTION_DESCRIPTION.getText());
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
		ReconnectMarketDataFeedJob job = new ReconnectMarketDataFeedJob(RECONNECT_QUOTE_FEED.getText());
		job.schedule();
	}
}
