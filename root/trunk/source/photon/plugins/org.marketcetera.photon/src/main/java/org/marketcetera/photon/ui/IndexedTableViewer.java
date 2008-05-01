package org.marketcetera.photon.ui;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Table;

public class IndexedTableViewer extends TableViewer {

	private Table table;

	/**
	 * @param table
	 */
	public IndexedTableViewer(Table table) {
		super(table);
		this.table = table;
	}
	
	public void remove(final int index){
		preservingSelection(new Runnable() {
			public void run() {
				indexedRemove(index);
			}
		});

	}

	protected void indexedRemove(int index) {
		table.remove(index);
		
		// Workaround for 1GDGN4Q: ITPUI:WIN2000 - TableViewer icons get
		// scrunched
		if (table.getItemCount() == 0) {
			table.removeAll();
		}
	}
	


}
