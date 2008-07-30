package org.marketcetera.photon.actions;

import org.eclipse.jface.action.Action;
import org.marketcetera.photon.IImageKeys;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.views.IHeartbeatsToggle;

public class ShowHeartbeatsAction 
    extends Action 
    implements Messages
{

	private static final String ID = "org.marketcetera.photon.actions.ShowHeartbeatsAction"; //$NON-NLS-1$
	private IHeartbeatsToggle view;

	public ShowHeartbeatsAction(IHeartbeatsToggle view) {
		this.view = view;
		setId(ID);
		setText(SHOW_HEARTBEATS_ACTION.getText());
		setToolTipText(SHOW_HEARTBEATS_ACTION_DESCRIPTION.getText());
		setImageDescriptor(PhotonPlugin.getImageDescriptor(IImageKeys.SHOW_HEARTBEATS));
		setChecked(false);
	}

	@Override
	public void run() {
		view.setShowHeartbeats(isChecked());
	}

}
