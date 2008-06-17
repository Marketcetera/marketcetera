package org.marketcetera.photon.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.marketcetera.messagehistory.FIXMessageHistory;
import org.marketcetera.messagehistory.MessageHolder;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.photon.ui.ContextMenuFactory;
import org.marketcetera.photon.ui.EventListContentProvider;
import org.marketcetera.photon.ui.FIXMessageTableFormat;
import org.marketcetera.photon.ui.FIXMessageTableRefresher;
import org.marketcetera.photon.ui.IndexedTableViewer;

import ca.odell.glazedlists.EventList;

/**
 * A view for FIX messages that uses a FIXMessageTableFormat and ensures the
 * columns are refreshed when preferences change.
 * 
 * @author michael.lossos@softwaregoodness.com
 * 
 */
public abstract class AbstractFIXMessagesView extends MessagesViewBase<MessageHolder> {

	private FIXMessageTableRefresher tableRefresher;

	protected abstract EventList<MessageHolder> extractList(FIXMessageHistory input);

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		FIXMessageHistory messageHistory = PhotonPlugin.getDefault().getFIXMessageHistory();
		if (messageHistory!= null){
			setInput(messageHistory);
		}
	}

	public void setInput(FIXMessageHistory input) {
		EventList<MessageHolder> extractedList = extractList(input);
		super.setInput(extractedList);
	}

	/**
	 * The FIXMessageTableFormat manages the addition/removal of columns. The
	 * Enum[] columns feature should not be used to create columns.
	 * 
	 * @return always null
	 */
	protected Enum<?>[] getEnumValues() {
		return null;
	}

	@Override
	public void dispose() {
		super.dispose();
		if (tableRefresher != null) {
			tableRefresher.dispose();
			tableRefresher = null;
		}
	}

	/**
	 * @return the unique ID of the view. This is used to identify the
	 *         preferences set in the FIXMessageColumnPreferencePage.
	 */
	protected abstract String getViewID();

	protected FIXMessageTableFormat<MessageHolder> createFIXMessageTableFormat(
			Table aMessageTable) {
		String viewID = getViewID();
		return new FIXMessageTableFormat<MessageHolder>(aMessageTable, viewID,
				MessageHolder.class);
	}

	@Override
	protected IndexedTableViewer createTableViewer(Table aMessageTable,
			Enum<?>[] enums) {
		IndexedTableViewer aMessagesViewer = new IndexedTableViewer(
				aMessageTable);
		getSite().setSelectionProvider(aMessagesViewer);
		aMessagesViewer
				.setContentProvider(new EventListContentProvider<MessageHolder>());

		FIXMessageTableFormat<MessageHolder> tableFormat = createFIXMessageTableFormat(aMessageTable);
		aMessagesViewer.setLabelProvider(tableFormat);

		tableRefresher = new FIXMessageTableRefresher(aMessagesViewer,
				tableFormat);
		
		createContextMenu(aMessageTable);
		
		return aMessagesViewer;
	}
	
	protected void createContextMenu(Table table) {
		ContextMenuFactory contextMenuFactory = new ContextMenuFactory();
		contextMenuFactory.createContextMenu("fixMessageContextMenu", table, getSite());
	}
}
