package org.marketcetera.photon.views;

import org.eclipse.jface.action.IToolBarManager;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.messagehistory.FIXMessageHistory;
import org.marketcetera.messagehistory.MessageHolder;
import org.marketcetera.photon.actions.OpenAdditionalViewAction;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;

/* $License$ */

/**
 * View which displays the average price of a series of trades.
 * 
 * @author gmiller
 * @author michael.lossos@softwaregoodness.com
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class AveragePriceView extends AbstractFIXMessagesView {

	public static final String ID = "org.marketcetera.photon.views.AveragePriceView"; //$NON-NLS-1$

	
	@Override
	protected String getViewID() {
		return ID;
	}

	@Override
	protected void initializeToolBar(IToolBarManager theToolBarManager)
	{
        theToolBarManager.add(new OpenAdditionalViewAction(getViewSite().getWorkbenchWindow(),
                                                           AVERAGE_PRICE_VIEW_LABEL.getText(),
                                                           ID));
	}

	protected FilterList<MessageHolder> getFilterList() {
		return (FilterList<MessageHolder>) getInput();
	}

	public EventList<MessageHolder> extractList(FIXMessageHistory input) {
		return input.getAveragePricesList();
	}

	@Override
	public void setFocus()
	{
	}
}
