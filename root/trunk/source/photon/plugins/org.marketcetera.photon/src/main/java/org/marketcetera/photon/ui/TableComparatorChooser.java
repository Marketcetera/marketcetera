package org.marketcetera.photon.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.marketcetera.photon.Messages;
import org.marketcetera.util.misc.ClassVersion;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.gui.AbstractTableComparatorChooser;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swt.EventTableViewer;

/**
 * Copy of {@link ca.odell.glazedlists.swt.TableComparatorChooser} that does not require a {@link EventTableViewer}.
 * 
 * Upgrade note: {@link Messages#TABLE_COMPARATOR_CHOOSER_INVALID_LISTENER} was used to externalize a string. 
 *
 * @see ca.odell.glazedlists.swt.TableComparatorChooser
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public final class TableComparatorChooser<E> extends AbstractTableComparatorChooser<E> {

    private final Object sortingStrategy;

    /** the table being sorted */
    private Table table;

    /** listeners to sort change events */
    private List<Listener> sortListeners = new ArrayList<Listener>();

    /** listeners for column headers */
    private ColumnListener columnListener = new ColumnListener();

    /**
     * Creates and installs a TableComparatorChooser.
     */
    private TableComparatorChooser(Table table, TableFormat<E> format, SortedList<E> sortedList, boolean multipleColumnSort) {
        super(sortedList, format);

        // save the SWT-specific state
        this.table = table;

        // listen for events on the specified table
        for(int c = 0; c < table.getColumnCount(); c++) {
            table.getColumn(c).addSelectionListener(columnListener);
        }

        // sort using the specified approach
        sortingStrategy = SINGLE_COLUMN;
    }

    /**
     * Installs a new TableComparatorChooser that responds to clicks
     * on the specified table and uses them to sort the specified list.
     *
     * @param table the table to be sorted
     * @param format the table format to be used
     * @param sortedList the sorted list to update.
     * @param multipleColumnSort <code>true</code> to sort by multiple columns
     *      at a time, or <code>false</code> to sort by a single column. Although
     *      sorting by multiple columns is more powerful, the user interface is
     *      not as simple and this strategy should only be used where necessary.
     */
    public static <E> TableComparatorChooser<E> install(Table table, TableFormat<E> format, SortedList<E> sortedList, boolean multipleColumnSort) {
        return new TableComparatorChooser<E>(table, format, sortedList,  multipleColumnSort);
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
        throw new IllegalArgumentException(Messages.TABLE_COMPARATOR_CHOOSER_INVALID_LISTENER.getText(sortListener));
    }

    /**
     * Handles column clicks.
     */
    class ColumnListener implements org.eclipse.swt.events.SelectionListener {
        public void widgetSelected(SelectionEvent e) {
            TableColumn column = (TableColumn)e.widget;
            Table table = column.getParent();
            int columnIndex = table.indexOf(column);
            // Using reflection to access internal code
            try {
				sortingStrategy.getClass().getMethods()[0].invoke(sortingStrategy, sortingState, columnIndex, 1, false, false);
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
        }
        public void widgetDefaultSelected(SelectionEvent e) {
            // Do Nothing
        }
    }

    /**
     * Updates the SWT table to indicate sorting icon on the primary sort column.
     */
    protected final void updateTableSortColumn() {
        final List<Integer> sortedColumns = getSortingColumns();
        if (sortedColumns.isEmpty()) {
            // no columns sorted
            table.setSortColumn(null);
            table.setSortDirection(SWT.NONE);
        } else {
            // make GL primary sort column the SWT table sort column
            final int primaryColumnIndex = sortedColumns.get(0).intValue();
            final int sortDirection = isColumnReverse(primaryColumnIndex) ? SWT.DOWN : SWT.UP;
            table.setSortColumn(table.getColumn(primaryColumnIndex));
            table.setSortDirection(sortDirection);
        }
    }

    /**
     * Updates the comparator in use and applies it to the table.
     *
     * <p>This method is called when the sorting state changed.</p>
     */
    protected final void rebuildComparator() {
        super.rebuildComparator();
        // update sorting icon in SWT table
        updateTableSortColumn();
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
    }
}