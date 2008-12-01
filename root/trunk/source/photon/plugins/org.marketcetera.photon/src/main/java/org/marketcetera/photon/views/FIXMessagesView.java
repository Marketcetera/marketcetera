package org.marketcetera.photon.views;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Table;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.messagehistory.FIXMessageHistory;
import org.marketcetera.messagehistory.MessageHolder;
import org.marketcetera.photon.actions.OpenAdditionalViewAction;
import org.marketcetera.photon.ui.DirectionalMessageTableFormat;
import org.marketcetera.photon.ui.FIXMessageTableFormat;

import ca.odell.glazedlists.FilterList;

/* $License$ */

/**
 * FIX Messages view.
 * 
 * @author gmiller
 * @author andrei@lissovski.org
 * @author michael.lossos@softwaregoodness.com
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class FIXMessagesView
    extends AbstractFIXMessagesView
{
    public static final String ID = "org.marketcetera.photon.views.FIXMessagesView"; //$NON-NLS-1$
    
    @Override
	protected String getViewID() {
		return ID;
	}

	protected void initializeToolBar(IToolBarManager theToolBarManager) {
	    super.initializeToolBar(theToolBarManager);
        theToolBarManager.add(new OpenAdditionalViewAction(getViewSite().getWorkbenchWindow(),
                                                           FIX_MESSAGES_VIEW_LABEL.getText(),
                                                           ID));
	}

	@Override
	protected FIXMessageTableFormat<MessageHolder> createFIXMessageTableFormat(
			Table aMessageTable) {
		String viewID = getViewID();
		return new DirectionalMessageTableFormat<MessageHolder>(aMessageTable,
				viewID, MessageHolder.class);
	}
    
	@Override
    protected FilterList<MessageHolder> getMessageList(FIXMessageHistory inHistory)
    {
        return new FilterList<MessageHolder>(inHistory.getAllMessagesList(),
                                             getFilterMatcherEditor());
    }
}
