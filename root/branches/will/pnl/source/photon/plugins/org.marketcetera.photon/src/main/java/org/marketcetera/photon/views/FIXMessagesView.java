package org.marketcetera.photon.views;

import org.eclipse.jface.action.IToolBarManager;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.messagehistory.ReportHolder;
import org.marketcetera.messagehistory.TradeReportsHistory;
import org.marketcetera.photon.actions.OpenAdditionalViewAction;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.util.concurrent.Lock;

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
	protected FilterList<ReportHolder> getMessageList(TradeReportsHistory inHistory) {
		EventList<ReportHolder> allMessagesList = inHistory.getAllMessagesList();
		Lock readLock = allMessagesList.getReadWriteLock().readLock();
		readLock.lock();
		try {
			return new FilterList<ReportHolder>(allMessagesList, getFilterMatcherEditor());
		} finally {
			readLock.unlock();
		}
	}
}
