package org.marketcetera.photon.actions;

import org.eclipse.jface.action.Action;
import org.marketcetera.photon.IImageKeys;
import org.marketcetera.photon.PhotonPlugin;

public class CancelAllOpenOrdersAction extends Action {

	private static final String ID = "org.marketcetera.photon.actions.CancelAllOpenOrdersAction";

	public CancelAllOpenOrdersAction() {
		setId(ID);
		setText("&Cancel all open orders");
		setToolTipText("Cancel all open orders");
		setImageDescriptor(PhotonPlugin.getImageDescriptor(IImageKeys.CANCEL_ALL_OPEN_ORDERS));
	}

	@Override
	public void run() {
		PhotonPlugin.getDefault().getPhotonController().cancelAllOpenOrders();
	}

}
