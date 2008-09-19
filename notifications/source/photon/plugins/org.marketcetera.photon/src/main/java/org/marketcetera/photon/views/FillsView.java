package org.marketcetera.photon.views;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.messagehistory.FIXMessageHistory;
import org.marketcetera.messagehistory.MessageHolder;
import org.marketcetera.photon.actions.OpenAdditionalViewAction;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;

/* $License$ */

/**
 * View which shows filled orders. 
 * 
 * @author gmiller
 * @author michael.lossos@softwaregoodness.com
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class FillsView
    extends AbstractFIXMessagesView
{
	public static final String ID = "org.marketcetera.photon.views.FillsView"; //$NON-NLS-1$
	@Override
	protected String getViewID() {
		return ID;
	}

	@Override
	protected void initializeToolBar(IToolBarManager inTheToolBarManager)
	{
	    super.initializeToolBar(inTheToolBarManager);
		inTheToolBarManager.add(new ContributionItem() {
			@Override
			public void fill(ToolBar parent, int index) {
				new Text(parent, SWT.BORDER);
			}
		});
        inTheToolBarManager.add(new OpenAdditionalViewAction(getViewSite().getWorkbenchWindow(),
                                                             FILLS_VIEW_LABEL.getText(),
                                                             ID));
		inTheToolBarManager.update(true);
	}
	public EventList<MessageHolder> extractList(FIXMessageHistory input) {
		return input.getFillsList();
	}
	@Override
	public void setFocus()
	{
	}
    /* (non-Javadoc)
     * @see org.marketcetera.photon.views.AbstractFIXMessagesView#getMessageList(org.marketcetera.messagehistory.FIXMessageHistory)
     */
    @Override
    protected FilterList<MessageHolder> getMessageList(FIXMessageHistory inHistory)
    {
        return new FilterList<MessageHolder>(inHistory.getFillsList(),
                                             getFilterMatcherEditor());
    }
}
