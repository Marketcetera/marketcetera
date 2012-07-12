package org.marketcetera.photon.internal.positions.ui.glazed;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.marketcetera.util.misc.ClassVersion;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.TransformedList;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.impl.adt.Barcode;
import ca.odell.glazedlists.swt.EventTableViewer;
import ca.odell.glazedlists.swt.GlazedListsSWT;

/* $License$ */

/**
 * A view helper that displays an EventList in an SWT table.
 *
 * <p>This class is not thread safe. It must be used exclusively with the SWT
 * event handler thread.
 * 
 * Note this only supports SWT.VIRTUAL trees at this point.
 * 
 * Derived from {@link EventTableViewer}.
 * 
 * @see EventTableViewer
 *
 * @author <a href="mailto:jesse@swank.ca">Jesse Wilson</a>
 * @author <a href="mailto:will.horn@gmail.com">Will Horn</a>
 */
@ClassVersion("$Id$")
public class EventTreeViewer<E> implements ListEventListener<E> {

    /** the heavyweight tree */
    private Tree tree;

    /** the proxy that moves events to the SWT user interface thread */
    protected TransformedList<E,E> swtThreadSource;

    /** the actual EventList to which this EventTableViewer is listening */
    protected EventList<E> source;
    
    /** to determine Tree structure */
    private EventTreeModel<E> treeModel;
    
    /** to manipulate Trees in a generic way */
    private TreeHandler<E> treeHandler;

    /** Specifies how to render table headers and sort */
    private TableFormat<? super E> tableFormat;

    /** Specifies how to render column values represented by TreeItems. */
    private TreeItemConfigurer<? super E> treeItemConfigurer;

    /**
     * Creates a new viewer for the given {@link Tree} that updates the tree
     * contents in response to changes on the specified {@link EventList}. The
     * {@link Tree} is formatted with the specified {@link TableFormat}.
     *
     * @param source the EventList that provides the row objects
     * @param tree the Tree viewing the source objects
     * @param tableFormat the object responsible for extracting column data
     *      from the row objects
     * @throws IllegalArgumentException if any parameters are null
     */
    @SuppressWarnings("unchecked")
	public EventTreeViewer(EventList<E> source, Tree tree, TableFormat<? super E> tableFormat, EventTreeModel<E> treeModel) {
        this(source, tree, tableFormat, treeModel, TreeItemConfigurer.DEFAULT);
    }

    /**
     * Creates a new viewer for the given {@link Tree} that updates the tree
     * contents in response to changes on the specified {@link EventList}. The
     * {@link Tree} is formatted with the specified {@link TableFormat}.
     *
     * @param source the EventList that provides the row objects
     * @param tree the Tree viewing the source objects
     * @param tableFormat the object responsible for extracting column data
     *      from the row objects
     * @param treeItemConfigurer responsible for configuring table items
     * @throws IllegalArgumentException if any parameters are null
     */
    public EventTreeViewer(EventList<E> source, Tree tree, TableFormat<? super E> tableFormat, EventTreeModel<E> treeModel,
            TreeItemConfigurer<? super E> treeItemConfigurer) {
        // check for valid arguments early
        if (source == null)
            throw new IllegalArgumentException("source list may not be null"); //$NON-NLS-1$
        if (tree == null)
            throw new IllegalArgumentException("Tree may not be null"); //$NON-NLS-1$
        if (tableFormat == null)
            throw new IllegalArgumentException("TableFormat may not be null"); //$NON-NLS-1$
        if (treeModel == null)
            throw new IllegalArgumentException("TreeModel may not be null"); //$NON-NLS-1$
        if (treeItemConfigurer == null)
            throw new IllegalArgumentException("TreeItemConfigurer may not be null"); //$NON-NLS-1$

        // lock the source list for reading since we want to prevent writes
        // from occurring until we fully initialize this EventTableViewer
        source.getReadWriteLock().writeLock().lock();
        try {
            // insert a list to move ListEvents to the SWT event dispatch thread
            final TransformedList<E,E> decorated = createSwtThreadProxyList(source, tree.getDisplay());
            if (decorated != null && decorated != source)
                this.source = swtThreadSource = decorated;
            else
                this.source = source;

            this.tree = tree;
            this.tableFormat = tableFormat;
            this.treeModel = treeModel;
            this.treeItemConfigurer = treeItemConfigurer;

            // configure how the Table will be manipulated
            if(isTreeVirtual()) {
                treeHandler = new VirtualTreeHandler();
            } else {
                // only virtual supported now
                throw new UnsupportedOperationException();
            }

            // setup the Table with initial values
            initTree();
            treeHandler.populateTree();

            // prepare listeners
            this.source.addListEventListener(this);
        } finally {
            source.getReadWriteLock().writeLock().unlock();
        }
    }
    /**
     * This method exists as a hook for subclasses that may have custom
     * threading needs within their EventTreeViewers. By default, this method
     * will wrap the given <code>source</code> in a SWTThreadProxyList if it
     * is not already a SWTThreadProxyList. Subclasses may replace this logic
     * and return either a custom ThreadProxyEventList of their choosing, or
     * return <code>null</code> or the <code>source</code> unchanged in order
     * to indicate that <strong>NO</strong> ThreadProxyEventList is desired.
     * In these cases it is expected that some external mechanism will ensure
     * that threading is handled correctly.
     *
     * @param source the EventList that provides the row objects
     * @return the source wrapped in some sort of ThreadProxyEventList if
     *      Thread-proxying is desired, or either <code>null</code> or the
     *      <code>source</code> unchanged to indicate that <strong>NO</strong>
     *      Thread-proxying is desired
     */
    protected TransformedList<E,E> createSwtThreadProxyList(EventList<E> source, Display display) {
        return GlazedListsSWT.isSWTThreadProxyList(source) ? null : GlazedListsSWT.swtThreadProxyList(source, display);
    }

    /**
     * Gets whether the tree is virtual or not.
     */
    private boolean isTreeVirtual() {
        return ((tree.getStyle() & SWT.VIRTUAL) == SWT.VIRTUAL);
    }

    /**
     * Builds the columns and headers for the {@link Tree}
     */
    private void initTree() {
        tree.setHeaderVisible(true);
        final TreeColumnConfigurer configurer = getTableColumnConfigurer();
        for(int c = 0; c < tableFormat.getColumnCount(); c++) {
            TreeColumn column = new TreeColumn(tree, SWT.LEFT, c);
            column.setText(tableFormat.getColumnName(c));
            column.setWidth(80);
            if (configurer != null) {
                configurer.configure(column, c);
            }
        }
    }

    /**
     * Sets all of the column values on a {@link TreeItem}.
     */
    private void renderTreeItem(TreeItem item, E value, int row) {
        for(int i = 0; i < tableFormat.getColumnCount(); i++) {
            final Object cellValue = tableFormat.getColumnValue(value, i);
            treeItemConfigurer.configure(item, value, cellValue, row, i);
        }
        item.setData(value);
        EventList<E> children = treeModel.getChildren(value);
        if (children != null) {
            item.setItemCount(children.size());
        }
    }

    /**
     * Gets the {@link TableFormat}.
     */
    public TableFormat<? super E> getTableFormat() {
        return tableFormat;
    }

    /**
     * Sets this {@link Table} to be formatted by a different
     * {@link TableFormat}.  This method is not yet implemented for SWT.
     */
    public void setTableFormat(TableFormat<E> tableFormat) {
        throw new UnsupportedOperationException();
    }

    /**
     * Gets the {@link TreeItemConfigurer}.
     */
    public TreeItemConfigurer<? super E> getTreeItemConfigurer() {
        return treeItemConfigurer;
    }

    /**
     * Sets a new {@link TreeItemConfigurer}. The cell values of existing,
     * non-virtual tree items will be reconfigured with the specified configurer.
     */
    public void setTreeItemConfigurer(TreeItemConfigurer<? super E> treeItemConfigurer) {
        if (treeItemConfigurer == null)
            throw new IllegalArgumentException("TreeItemConfigurer may not be null"); //$NON-NLS-1$

        this.treeItemConfigurer = treeItemConfigurer;
        // determine the index of the last, non-virtual table item
        final int maxIndex = treeHandler.getLastIndex();
        if (maxIndex < 0) return;
        // Disable redraws so that the table is updated in bulk
        tree.setRedraw(false);
        source.getReadWriteLock().readLock().lock();
        try {
            // reprocess all table items between indexes 0 and maxIndex
            for (int i = 0; i <= maxIndex; i++) {
                final E rowValue = source.get(i);
                renderTreeItem(tree.getItem(i), rowValue, i);
            }
        } finally {
            source.getReadWriteLock().readLock().unlock();
        }
        // Re-enable redraws to update the table
        tree.setRedraw(true);
    }

    /**
     * Gets the {@link TreeColumnConfigurer} or <code>null</code> if not
     * available.
     */
    private TreeColumnConfigurer getTableColumnConfigurer() {
        if (tableFormat instanceof TreeColumnConfigurer) {
            return (TreeColumnConfigurer) tableFormat;
        }
        return null;
    }

    /**
     * Gets the {@link Tree} that is being managed by this
     * {@link EventTreeViewer}.
     */
    public Tree getTree() {
        return tree;
    }

    /**
     * Get the source of this {@link EventTreeViewer}.
     */
    public EventList<E> getSourceList() {
        return source;
    }

    /**
     * When the source list is changed, this forwards the change to the
     * displayed {@link Tree}.
     */
    public void listChanged(ListEvent<E> listChanges) {
        // if the tree is no longer available, we don't want to do anything as
        // it will result in a "Widget is disposed" exception
        if (tree.isDisposed()) return;

        // Disable redraws so that the table is updated in bulk
        tree.setRedraw(false);

        // Apply changes to the list
        while (listChanges.next()) {
            int changeIndex = listChanges.getIndex();
            int changeType = listChanges.getType();
            if (changeType == ListEvent.INSERT) {
                 treeHandler.addRow(changeIndex, source.get(changeIndex));
            } else if (changeType == ListEvent.UPDATE) {
                treeHandler.updateRow(changeIndex, source.get(changeIndex));
            } else if (changeType == ListEvent.DELETE) {
                treeHandler.removeRow(changeIndex);
            }
        }

        // Re-enable redraws to update the tree
        tree.setRedraw(true);
    }

    /**
     * Releases the resources consumed by this {@link EventTreeViewer} so that it
     * may eventually be garbage collected.
     *
     * <p>An {@link EventTreeViewer} will be garbage collected without a call to
     * {@link #dispose()}, but not before its source {@link EventList} is garbage
     * collected. By calling {@link #dispose()}, you allow the {@link EventTreeViewer}
     * to be garbage collected before its source {@link EventList}. This is
     * necessary for situations where an {@link EventTreeViewer} is short-lived but
     * its source {@link EventList} is long-lived.
     *
     * <p><strong><font color="#FF0000">Warning:</font></strong> It is an error
     * to call any method on a {@link EventTreeViewer} after it has been disposed.
     */
    public void dispose() {
        treeHandler.dispose();
        if (swtThreadSource != null)
            swtThreadSource.dispose();
        source.removeListEventListener(this);

        // this encourages exceptions to be thrown if this model is incorrectly accessed again
        swtThreadSource = null;
        treeHandler = null;
        source = null;
    }

    /**
     * Defines how Trees will be manipulated.
     */
    private interface TreeHandler<E> {

        /**
         * Populate the Tree with data.
         */
        public void populateTree();

        /**
         * Add a row with the given value.
         */
        public void addRow(int row, E value);

        /**
         * Update a row with the given value.
         */
        public void updateRow(int row, E value);

        /**
         * Removes a row.
         */
        public void removeRow(int row);

        /**
         * Disposes of this TreeHandler
         */
        public void dispose();

        /**
         * Gets the last real, non-virtual row index. -1 means empty or
         * completely virtual tree
         */
        public int getLastIndex();
    }
    
    /**
     * Allows manipulation of Virtual Trees and handles additional aspects
     * like providing the SetData callback method and tracking which values
     * are Virtual.
     */
    private final class VirtualTreeHandler implements TreeHandler<E>, Listener {

        /** to keep track of what's been requested */
        private final Barcode requested = new Barcode();

        /**
         * Create a new VirtualTableHandler.
         */
        public VirtualTreeHandler() {
            requested.addWhite(0, source.size());
            tree.addListener(SWT.SetData, this);
        }

        /**
         * Populate the Table with initial data.
         */
        public void populateTree() {
            tree.setItemCount(source.size());
        }

        /**
         * Adds a row with the given value.
         */
        public void addRow(int row, E value) {
            // Adding before the last non-Virtual value
            if(row <= getLastIndex()) {
                requested.addBlack(row, 1);
                TreeItem item = new TreeItem(tree, 0, row);
                renderTreeItem(item, value, row);
            // Adding in the Virtual values at the end
            } else {
                requested.addWhite(requested.size(), 1);
                tree.setItemCount(tree.getItemCount() + 1);
            }
        }

        /**
         * Updates a row with the given value.
         */
        public void updateRow(int row, E value) {
            // Only set a row if it is NOT Virtual
            if(!isVirtual(row)) {
                requested.setBlack(row, 1);
                TreeItem item = tree.getItem(row);
                renderTreeItem(item, value, row);
                // not terribly efficient, basically refresh all children 
                item.clearAll(true);
            }
        }

        /**
         * Removes a row.
         */
        public void removeRow(int row) {
            // Sync the requested barcode to clear values that have been removed
            requested.remove(row, 1);
            tree.getItem(row).dispose();
        }

        /**
         * Returns the highest index that has been requested or -1 if the
         * Table is entirely Virtual.
         */
        public int getLastIndex() {
            // Everything is Virtual
            if(requested.blackSize() == 0) return -1;

            // Return the last index
            else return requested.getIndex(requested.blackSize() - 1, Barcode.BLACK);
        }

        /**
         * Returns whether a particular row is Virtual in the Table.
         */
        private boolean isVirtual(int rowIndex) {
            return requested.getBlackIndex(rowIndex) == -1;
        }

        /**
         * Respond to requests for values to fill Virtual rows.
         */
        @SuppressWarnings("unchecked")
		public void handleEvent(Event e) {
            // Get the TreeItem from the Tree
            TreeItem item = (TreeItem)e.item;
            
            // Set the value on the Virtual element
            requested.setBlack(e.index, 1);
            
            TreeItem parentItem = item.getParentItem();
            EventList<E> list;
            if (parentItem == null) {
                // root level
                list = source;
            } else {
                list = treeModel.getChildren((E) parentItem.getData());
            }
            E value = list.get(e.index);
            renderTreeItem(item, value, e.index);
        }

        /**
         * Allows this handler to clean up after itself.
         */
        public void dispose() {
            tree.removeListener(SWT.SetData, this);
        }
    }
}