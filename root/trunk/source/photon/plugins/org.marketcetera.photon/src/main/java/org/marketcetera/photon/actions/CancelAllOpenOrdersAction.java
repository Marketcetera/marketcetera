package org.marketcetera.photon.actions;

import org.eclipse.jface.action.Action;
import org.marketcetera.photon.IImageKeys;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.PhotonPlugin;

public class CancelAllOpenOrdersAction 
    extends Action
    implements Messages
{

	private static final String ID = "org.marketcetera.photon.actions.CancelAllOpenOrdersAction"; //$NON-NLS-1$

	public CancelAllOpenOrdersAction() {
		setId(ID);
		setText(CANCEL_ALL_OPEN_ORDERS_ACTION.getText());
		setToolTipText(CANCEL_ALL_OPEN_ORDERS_ACTION_DESCRIPTION.getText());
		setImageDescriptor(PhotonPlugin.getImageDescriptor(IImageKeys.CANCEL_ALL_OPEN_ORDERS));
	}

	@Override
	public void run() {
		PhotonPlugin.getDefault().getPhotonController().cancelAllOpenOrders();
	}

}
