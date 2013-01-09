package org.marketcetera.photon.views;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Table;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.messagehistory.ReportHolder;
import org.marketcetera.messagehistory.TradeReportsHistory;
import org.marketcetera.photon.actions.OpenAdditionalViewAction;
import org.marketcetera.photon.ui.FIXMessageTableFormat;

import ca.odell.glazedlists.EventList;

/* $License$ */

/**
 * View which displays the average price of a series of trades.
 * 
 * @author gmiller
 * @author michael.lossos@softwaregoodness.com
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 */
@ClassVersion("$Id$")
public class AveragePriceView extends AbstractFIXMessagesView {
	public static final String ID = "org.marketcetera.photon.views.AveragePriceView"; //$NON-NLS-1$

	@Override
	protected String getViewID() {
		return ID;
	}

	@Override
	protected void initializeToolBar(IToolBarManager inTheToolBarManager) {
		super.initializeToolBar(inTheToolBarManager);
		inTheToolBarManager.add(new OpenAdditionalViewAction(getViewSite()
				.getWorkbenchWindow(), AVERAGE_PRICE_VIEW_LABEL.getText(), ID));
	}

	@Override
	protected EventList<ReportHolder> getMessageList(
			TradeReportsHistory inHistory) {
		return inHistory.getAveragePricesList();
	}

	@Override
	protected FIXMessageTableFormat<ReportHolder> createFIXMessageTableFormat(
			Table inMessageTable) {
		// Override to leave broker id out since it does not make sense in this
		// view
		return new FIXMessageTableFormat<ReportHolder>(inMessageTable,
				getViewID(), ReportHolder.class);
	}
}
