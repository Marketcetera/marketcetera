package org.marketcetera.photon.views;

import org.eclipse.jface.action.IToolBarManager;
import org.marketcetera.messagehistory.FIXMessageHistory;
import org.marketcetera.messagehistory.MessageHolder;
import org.marketcetera.photon.actions.CancelAllOpenOrdersAction;

import ca.odell.glazedlists.EventList;

/**
 * @author gmiller
 * @author michael.lossos@softwaregoodness.com
 *
 */
public class OpenOrdersView extends AbstractFIXMessagesView {

	public static final String ID = "org.marketcetera.photon.views.OpenOrdersView"; //$NON-NLS-1$

	@Override
	protected String getViewID() {
		return ID;
	}

	@Override
	protected void initializeToolBar(IToolBarManager theToolBarManager) {
		theToolBarManager.add(new CancelAllOpenOrdersAction());
	}

	public EventList<MessageHolder> extractList(FIXMessageHistory hist) {
		return hist.getOpenOrdersList();
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
