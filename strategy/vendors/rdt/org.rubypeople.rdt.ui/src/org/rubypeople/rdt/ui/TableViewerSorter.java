package org.rubypeople.rdt.ui;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * A generic TableViewer Sorter. 
 * 
 * Mostly taken from https://bugs.eclipse.org/bugs/show_bug.cgi?id=158112
 * 
 * @author brad
 */
public class TableViewerSorter extends ViewerSorter {
	private int columnIndex = 0;

	public TableViewerSorter(int columnIndex) {
		this.columnIndex = columnIndex;
	}

	/**
	 * Compares the two objects provided.
	 * 
	 * If numbers are in the relevant string then the objects are returned in
	 * number order (rather than string order).
	 */
	public int compare(Viewer viewer, Object e1, Object e2) {
		int order = 0;
		if (viewer instanceof TableViewer) {
			TableViewer tv = (TableViewer) viewer;
			Table table = tv.getTable();
			table.setSortColumn(table.getColumn(columnIndex));
			int idx1 = -1, idx2 = -1;
			for (int i = 0; i < table.getItemCount(); i++) {
				Object obj = tv.getElementAt(i);
				if (obj.equals(e1)) {
					idx1 = i;
				}
				else if (obj.equals(e2)) {
					idx2 = i;
				}
				if (idx1 > 0 && idx2 > 0) {
					break;
				}
			}

			if (idx1 > -1 && idx2 > -1) {
				String str1 = table.getItems()[idx1].getText(this.columnIndex);
				String str2 = table.getItems()[idx2].getText(this.columnIndex);
				order = str1.compareTo(str2);

				try {
					Double d1 = Double.valueOf(str1);
					Double d2 = Double.valueOf(str2);
					order = d1.compareTo(d2);
				}
				catch (NumberFormatException e) {
					// do nothing
				}

				if (table.getSortDirection() != SWT.UP) {
					order *= -1;
				}
			}
		}

		return order;
	}

	/**
	 * The TableViewer passed in will be set up to use this sorter when a column
	 * is clicked.
	 */
	public static void bind(final TableViewer tableViewer) {
		final Table table = tableViewer.getTable();
		for (int i = 0; i < table.getColumnCount(); i++) {
			final int columnNum = i;
			TableColumn column = table.getColumn(i);
			column.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(final SelectionEvent e) {
					TableViewerSorter sorter = new TableViewerSorter(columnNum);
					if (table.getSortDirection() == SWT.UP) {
						table.setSortDirection(SWT.DOWN);
					}
					else if (table.getSortDirection() == SWT.DOWN) {
						table.setSortDirection(SWT.UP);
					}
					else {
						table.setSortDirection(SWT.UP);
					}
					tableViewer.setSorter(sorter);
				}
			});
		}
	}
	
	public static void bind(final TableViewer tableViewer, int columnIndex) {
		bind(tableViewer);
		TableViewerSorter sorter = new TableViewerSorter(columnIndex);
		tableViewer.setSorter(sorter);
	}
}
