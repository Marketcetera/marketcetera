/* Glazed Lists                                                 (c) 2003-2006 */
/* http://publicobject.com/glazedlists/                      publicobject.com,*/
/*                                                     O'Dell Engineering Ltd.*/
package org.marketcetera.photon.ui;

// the core Glazed Lists packages
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.gui.AbstractTableComparatorChooser;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.impl.gui.MouseOnlySortingStrategy;
import ca.odell.glazedlists.impl.gui.SortingStrategy;
import ca.odell.glazedlists.impl.gui.SortingState.SortingColumn;

/**
 * A TableComparatorChooser is a tool that allows the user to sort a ListTable by clicking
 * on the table's headers. It requires that the ListTable has a SortedList as
 * a source as the sorting on that list is used.
 *
 * <p><strong>Warning:</strong> This class is a a developer preview and subject to
 * many bugs and API changes.
 *
 * @see <a href="http://dev.eclipse.org/viewcvs/index.cgi/org.eclipse.swt.snippets/src/org/eclipse/swt/snippets/Snippet2.java?rev=HEAD">Snippet 2</a>
 *
 * @author <a href="mailto:jesse@swank.ca">Jesse Wilson</a>
 */
public final class TableComparatorChooser<T> extends AbstractTableComparatorChooser<T> {

    private final SortingStrategy sortingStrategy;

    /** the table being sorted */
    private Table table;

    /** listeners to sort change events */
    private List<Listener> sortListeners = new ArrayList<Listener>();

    /** listeners for column headers */
    private ColumnListener columnListener = new ColumnListener();

    private SortIndicatorHelper sortIndicatorHelper;
    
    private TableColumn lastSortColumn;
    
    private boolean multipleColumnSortEnabled;
    
    /**
     * Creates a new TableComparatorChooser that responds to clicks
     * on the specified table and uses them to sort the specified list.
     *
     * @param sortedList the sorted list to update.
     * @param multipleColumnSort <code>true</code> to sort by multiple columns
     *      at a time, or <code>false</code> to sort by a single column. Although
     *      sorting by multiple columns is more powerful, the user interface is
     *      not as simple and this strategy should only be used where necessary.
     */
    public TableComparatorChooser(Table table, TableFormat<T> format, SortedList<T> sortedList, boolean multipleColumnSort) {
        super(sortedList, format);

        // save the SWT-specific state
        this.table = table;

        this.sortIndicatorHelper = new SortIndicatorHelper(table.getDisplay());
        
        this.multipleColumnSortEnabled = multipleColumnSort;
        
        // listen for events on the specified table
        for(int c = 0; c < table.getColumnCount(); c++) {
            table.getColumn(c).addSelectionListener(columnListener);
        }

        // sort using the specified approach
        sortingStrategy = new MouseOnlySortingStrategy(multipleColumnSort);
    }

    /**
     * Registers the specified {@link Listener} to receive notification whenever
     * the {@link Table} is sorted by this {@link TableComparatorChooser}.
     */
    public void addSortListener(final Listener sortListener) {
        sortListeners.add(sortListener);
    }
    /**
     * Deregisters the specified {@link Listener} to no longer receive events.
     */
    public void removeSortActionListener(final Listener sortListener) {
        for(Iterator<Listener> i = sortListeners.iterator(); i.hasNext(); ) {
            if(sortListener == i.next()) {
                i.remove();
                return;
            }
        }
        throw new IllegalArgumentException("Cannot remove nonexistent listener " + sortListener);
    }

    /**
     * Handles column clicks.
     */
    class ColumnListener implements org.eclipse.swt.events.SelectionListener {
        public void widgetSelected(SelectionEvent e) {
            TableColumn column = (TableColumn)e.widget;
            Table table = column.getParent();
            int columnIndex = table.indexOf(column);
            sortingStrategy.columnClicked(sortingState, columnIndex, 1, false, false);
            
            updateSortIndicatorIcon(column, columnIndex);
        }
        
        void updateSortIndicatorIcon(TableColumn column, int columnIndex) {
        	List<SortingColumn> sortingColumns = sortingState.getColumns();
			SortingColumn sortingColumn = sortingColumns.get(columnIndex);
			if (sortingColumn.getComparatorIndex() >= 0) {
				if(lastSortColumn != null && !multipleColumnSortEnabled) {
					if(!lastSortColumn.isDisposed()) {
						lastSortColumn.setImage(null);
						lastSortColumn.pack();
					}
				}
				lastSortColumn = column;
				int direction = SWT.UP;
				if (sortingColumn.isReverse()) {
					direction = SWT.DOWN;
				}
				Image sortIndicatorImage = sortIndicatorHelper
						.getSortImage(direction);
				column.setImage(sortIndicatorImage);
				column.pack();
			} else {
				column.setImage(null);
				column.pack();
			}
        }
        
        public void widgetDefaultSelected(SelectionEvent e) {
            // Do Nothing
        }
    }
    
    public void updateSortIndicatorIconupdateSortIndicatorIcon(int columnIndex) {
		TableColumn column = table.getColumn(columnIndex);
		columnListener.updateSortIndicatorIcon(column, columnIndex);
	}

	public void removeSortIndicators() {
		TableColumn[] columns = table.getColumns();
		if (columns != null) {
			for (TableColumn column : columns) {
				column.setImage(null);
			}
		}
	}

    /**
	 * Updates the comparator in use and applies it to the table.
	 */
    protected final void rebuildComparator() {
        super.rebuildComparator();

        // notify interested listeners that the sorting has changed
        Event sortEvent = new Event();
        sortEvent.widget = table;
        for(Iterator<Listener> i = sortListeners.iterator(); i.hasNext(); ) {
            i.next().handleEvent(sortEvent);
        }
    }

    /**
     * Releases the resources consumed by this {@link TableComparatorChooser} so that it
     * may eventually be garbage collected.
     *
     * <p>A {@link TableComparatorChooser} will be garbage collected without a call to
     * {@link #dispose()}, but not before its source {@link EventList} is garbage
     * collected. By calling {@link #dispose()}, you allow the {@link TableComparatorChooser}
     * to be garbage collected before its source {@link EventList}. This is
     * necessary for situations where an {@link TableComparatorChooser} is short-lived but
     * its source {@link EventList} is long-lived.
     *
     * <p><strong><font color="#FF0000">Warning:</font></strong> It is an error
     * to call any method on a {@link TableComparatorChooser} after it has been disposed.
     */
    public void dispose() {
        // stop listening for events on the specified table
        for(int c = 0; c < table.getColumnCount(); c++) {
            table.getColumn(c).removeSelectionListener(columnListener);
        }
        sortIndicatorHelper.dispose();
    }
}