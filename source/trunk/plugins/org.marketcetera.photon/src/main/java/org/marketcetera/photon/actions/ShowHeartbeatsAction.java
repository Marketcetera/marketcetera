package org.marketcetera.photon.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.marketcetera.photon.IImageKeys;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.views.FIXMessagesView;

public class ShowHeartbeatsAction extends Action {

	private static final String ID = ".marketcetera.photon.actions.ShowHeartbeatsAction";
	private FIXMessagesView view;

	public ShowHeartbeatsAction(FIXMessagesView view) {
		this.view = view;
		setId(ID);
		setText("&Show heartbeats");
		setToolTipText("Show heartbeats");
		setImageDescriptor(PhotonPlugin.getImageDescriptor(IImageKeys.SHOW_HEARTBEATS));
		setChecked(false);
	}

	@Override
	public void run() {
		view.setShowHeartbeats(isChecked());
	}


}
