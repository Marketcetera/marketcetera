package org.marketcetera.photon.views;

import org.eclipse.jface.action.IToolBarManager;
import org.marketcetera.messagehistory.FIXMessageHistory;
import org.marketcetera.messagehistory.MessageHolder;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;

/**
 * @author gmiller
 * @author michael.lossos@softwaregoodness.com
 *
 */
public class AveragePriceView extends AbstractFIXMessagesView {

	public static final String ID = "org.marketcetera.photon.views.AveragePriceView"; //$NON-NLS-1$

	
	@Override
	protected String getViewID() {
		return ID;
	}

	@Override
	protected void initializeToolBar(IToolBarManager theToolBarManager) {

	}

	@SuppressWarnings("unchecked") //$NON-NLS-1$
	protected FilterList<MessageHolder> getFilterList() {
		return (FilterList<MessageHolder>) getInput();
	}

	public EventList<MessageHolder> extractList(FIXMessageHistory input) {
		return input.getAveragePricesList();
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
