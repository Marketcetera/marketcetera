package org.marketcetera.photon.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;
import org.marketcetera.photon.IImageKeys;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * RCP platform action responsible for initiating a reconnect of the 
 * application's connection to the server via the client API.
 * 
 * @author gmiller
 * @author anshul@marketcetera.com
 * @see ReconnectClientJob#runInUIThread(org.eclipse.core.runtime.IProgressMonitor)
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class ReconnectClientAction
    extends Action 
    implements Messages
{

	public static final String ID = "org.marketcetera.photon.actions.ReconnectClientAction"; //$NON-NLS-1$
	/**
	 * Create the default instance of ReconnectClientAction, setting the ID, text,
	 * tool-tip text, and image to the defaults.
	 */
	public ReconnectClientAction(IWorkbenchWindow window){
		setId(ID);
		setText(RECONNECT_CLIENT_ACTION.getText());
		setToolTipText(RECONNECT_CLIENT_ACTION_DESCRIPTION.getText());
		setImageDescriptor(PhotonPlugin.getImageDescriptor(IImageKeys.RECONNECT_CLIENT_HISTORY));
	}

	/**
	 * Attempt to reconnect to the server.
	 * 
	 */
	@Override
	public void run() {
		ReconnectClientJob job = new ReconnectClientJob(RECONNECT_CLIENT_JOB_ACTION.getText());
		job.schedule();
	}

	
}
