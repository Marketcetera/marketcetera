package org.marketcetera.photon.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.photon.Application;
import org.marketcetera.photon.IImageKeys;
import org.marketcetera.photon.PhotonPlugin;

@ClassVersion("$Id$")
public class ReconnectJMSAction extends Action implements IWorkbenchAction {

	public static final String ID = "org.marketcetera.photon.actions.ReconnectJMSAction";
	private IWorkbenchWindow window;
	
	public ReconnectJMSAction(IWorkbenchWindow window){
		this.window = window;
		setId(ID);
		setText("&Reconnect Message Connection");
		setToolTipText("Reconnect to the message server");
		setImageDescriptor(PhotonPlugin.getImageDescriptor(IImageKeys.RECONNECT_JMS_HISTORY));
	}
	public void dispose() {
		// TODO Auto-generated method stub
	}
	public void run() {
		Application.initJMSConnector();
	}

}
