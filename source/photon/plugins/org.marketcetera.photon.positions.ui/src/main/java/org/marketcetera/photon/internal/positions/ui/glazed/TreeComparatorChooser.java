package org.marketcetera.photon.internal.positions.ui.glazed;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.marketcetera.util.misc.ClassVersion;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.gui.AbstractTableComparatorChooser;
import ca.odell.glazedlists.impl.gui.MouseOnlySortingStrategy;
import ca.odell.glazedlists.impl.gui.SortingStrategy;
import ca.odell.glazedlists.swt.TableComparatorChooser;

/* $License$ */

/**
 * A TreeComparatorChooser is a tool that allows the user to sort a ListTable by clicking
 * on the table's headers. It requires that the ListTable has a SortedList as
 * a source as the sorting on that list is used.
 * 
 * Modeled after the Glazed Lists {@link TableComparatorChooser}.
 *
 * @see TableComparatorChooser
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public final class TreeComparatorChooser<E> extends AbstractTableComparatorChooser<E> {

    private final SortingStrategy sortingStrategy;

    /** the tree being sorted */
    private Tree tree;

    /** listeners to sort change events */
    private List<Listener> sortListeners = new ArrayList<Listener>();

    /** listeners for column headers */
    private ColumnListener columnListener = new ColumnListener();

    /**
     * Creates and installs a TreeComparatorChooser.
     */
    private TreeComparatorChooser(EventTreeViewer<E> eventTreeViewer, SortedList<E> sortedList, boolean multipleColumnSort) {
        super(sortedList, eventTreeViewer.getTableFormat());

        // save the SWT-specific state
        this.tree = eventTreeViewer.getTree();

        // listen for events on the specified table
        for(int c = 0; c < tree.getColumnCount(); c++) {
            tree.getColumn(c).addSelectionListener(columnListener);
        }

        // sort using the specified approach
        sortingStrategy = new MouseOnlySortingStrategy(multipleColumnSort);
    }

    /**
     * Installs a new TreeComparatorChooser that responds to clicks
     * on the specified tree and uses them to sort the specified list.
     *
     * @param eventTreeViewer the tree viewer for the tree to be sorted
     * @param sortedList the sorted list to update.
     * @param multipleColumnSort <code>true</code> to sort by multiple columns
     *      at a time, or <code>false</code> to sort by a single column. Although
     *      sorting by multiple columns is more powerful, the user interface is
     *      not as simple and this strategy should only be used where necessary.
     */
    public static <E> TreeComparatorChooser<E> install(EventTreeViewer<E> eventTreeViewer, SortedList<E> sortedList, boolean multipleColumnSort) {
        return new TreeComparatorChooser<E>(eventTreeViewer, sortedList,  multipleColumnSort);
    }

    /**
     * Registers the specified {@link Listener} to receive notification whenever
     * the {@link Table} is sorted by this {@link TreeComparatorChooser}.
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
        throw new IllegalArgumentException("Cannot remove nonexistent listener " + sortListener); //$NON-NLS-1$
    }

    /**
     * Handles column clicks.
     */
    class ColumnListener implements org.eclipse.swt.events.SelectionListener {
        public void widgetSelected(SelectionEvent e) {
            TreeColumn column = (TreeColumn)e.widget;
            int columnIndex = tree.indexOf(column);
            sortingStrategy.columnClicked(sortingState, columnIndex, 1, false, false);
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
            tree.setSortColumn(null);
            tree.setSortDirection(SWT.NONE);
        } else {
            // make GL primary sort column the SWT table sort column
            final int primaryColumnIndex = sortedColumns.get(0).intValue();
            final int sortDirection = isColumnReverse(primaryColumnIndex) ? SWT.DOWN : SWT.UP;
            tree.setSortColumn(tree.getColumn(primaryColumnIndex));
            tree.setSortDirection(sortDirection);
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
        sortEvent.widget = tree;
        for(Iterator<Listener> i = sortListeners.iterator(); i.hasNext(); ) {
            i.next().handleEvent(sortEvent);
        }
    }

    /**
     * Releases the resources consumed by this {@link TreeComparatorChooser} so that it
     * may eventually be garbage collected.
     *
     * <p>A {@link TreeComparatorChooser} will be garbage collected without a call to
     * {@link #dispose()}, but not before its source {@link EventList} is garbage
     * collected. By calling {@link #dispose()}, you allow the {@link TreeComparatorChooser}
     * to be garbage collected before its source {@link EventList}. This is
     * necessary for situations where an {@link TreeComparatorChooser} is short-lived but
     * its source {@link EventList} is long-lived.
     *
     * <p><strong><font color="#FF0000">Warning:</font></strong> It is an error
     * to call any method on a {@link TreeComparatorChooser} after it has been disposed.
     */
    public void dispose() {
        // stop listening for events on the specified table
        for(int c = 0; c < tree.getColumnCount(); c++) {
            tree.getColumn(c).removeSelectionListener(columnListener);
        }
    }
}