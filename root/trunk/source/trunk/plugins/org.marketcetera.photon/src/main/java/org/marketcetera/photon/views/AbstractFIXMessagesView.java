package org.marketcetera.photon.views;

import org.eclipse.swt.widgets.Table;
import org.marketcetera.photon.core.MessageHolder;
import org.marketcetera.photon.ui.ContextMenuFactory;
import org.marketcetera.photon.ui.EventListContentProvider;
import org.marketcetera.photon.ui.FIXMessageTableFormat;
import org.marketcetera.photon.ui.FIXMessageTableRefresher;
import org.marketcetera.photon.ui.IndexedTableViewer;

/**
 * A view for FIX messages that uses a FIXMessageTableFormat and ensures the
 * columns are refreshed when preferences change.
 * 
 * @author michael.lossos@softwaregoodness.com
 * 
 */
public abstract class AbstractFIXMessagesView extends HistoryMessagesView {

	private FIXMessageTableRefresher tableRefresher;

	/**
	 * The FIXMessageTableFormat manages the addition/removal of columns. The
	 * Enum[] columns feature should not be used to create columns.
	 * 
	 * @return always null
	 */
	protected Enum[] getEnumValues() {
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
			Enum[] enums) {
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
