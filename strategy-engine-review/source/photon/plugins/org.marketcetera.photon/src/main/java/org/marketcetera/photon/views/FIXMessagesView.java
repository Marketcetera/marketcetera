package org.marketcetera.photon.views;

import org.eclipse.jface.action.IToolBarManager;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.messagehistory.ReportHolder;
import org.marketcetera.messagehistory.TradeReportsHistory;
import org.marketcetera.photon.actions.OpenAdditionalViewAction;

import ca.odell.glazedlists.EventList;

/* $License$ */

/**
 * FIX Messages view.
 * 
 * @author gmiller
 * @author andrei@lissovski.org
 * @author michael.lossos@softwaregoodness.com
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 */
@ClassVersion("$Id$")
public class FIXMessagesView extends AbstractFIXMessagesView {
	public static final String ID = "org.marketcetera.photon.views.FIXMessagesView"; //$NON-NLS-1$

	@Override
	protected String getViewID() {
		return ID;
	}

	protected void initializeToolBar(IToolBarManager theToolBarManager) {
		super.initializeToolBar(theToolBarManager);
		theToolBarManager.add(new OpenAdditionalViewAction(getViewSite().getWorkbenchWindow(),
				FIX_MESSAGES_VIEW_LABEL.getText(), ID));
	}

	@Override
	protected EventList<ReportHolder> getMessageList(TradeReportsHistory inHistory) {
		return inHistory.getAllMessagesList();
	}
}
