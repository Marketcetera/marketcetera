package org.marketcetera.photon.views;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.marketcetera.messagehistory.FIXMessageHistory;
import org.marketcetera.messagehistory.MessageHolder;

import ca.odell.glazedlists.EventList;

/**
 * @author gmiller
 * @author michael.lossos@softwaregoodness.com
 *
 */
public class FillsView extends AbstractFIXMessagesView {

	public static final String ID = "org.marketcetera.photon.views.FillsView";

	@Override
	protected String getViewID() {
		return ID;
	}

	@Override
	protected void initializeToolBar(IToolBarManager theToolBarManager) {
		theToolBarManager.add(new ContributionItem() {
			@Override
			public void fill(ToolBar parent, int index) {
				new Text(parent, SWT.BORDER);
			}
		});
		theToolBarManager.update(true);
	}

	public EventList<MessageHolder> extractList(FIXMessageHistory input) {
		return input.getFillsList();
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}