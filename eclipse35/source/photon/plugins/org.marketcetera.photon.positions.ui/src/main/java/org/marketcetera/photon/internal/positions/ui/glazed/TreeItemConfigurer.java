package org.marketcetera.photon.internal.positions.ui.glazed;
import org.eclipse.swt.widgets.TreeItem;
import org.marketcetera.util.misc.ClassVersion;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swt.TableItemConfigurer;

/* $License$ */

/**
 * A <code>TreeItemConfigurer</code> can be provided to an
 * {@link EventTreeViewer} to customize the initial format and appearance of
 * column values, each represented by a {@link TreeItem}.
 *
 * Derived from {@link TableItemConfigurer}.
 * 
 * @see TableItemConfigurer
 * @see EventTreeViewer#setTreeItemConfigurer(TreeItemConfigurer)
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public interface TreeItemConfigurer<E> {

    /**
     * Default configurer that converts the column value to a string and sets it
     * as the text of the TableItem.
     */
    @SuppressWarnings("unchecked")
	public static final TreeItemConfigurer DEFAULT = new DefaultTreeItemConfigurer();

    /**
     * Callback method that allows the configuration of the TableItem properties
     * for the specified row and column.
     *
     * @param item the TableItem at index <code>row</code>
     * @param rowValue the list element from the source {@link EventList} at
     *        index <code>row</code>
     * @param columnValue the column value, e.g. the value returned by
     *        {@link TableFormat#getColumnValue(Object, int)}
     * @param row the row index
     * @param column the column index
     */
    void configure(TreeItem item, E rowValue, Object columnValue, int row, int column);

    /**
     * Default configurer that converts the column value to a string and sets it
     * as the text of the TableItem.
     */
    class DefaultTreeItemConfigurer<E> implements TreeItemConfigurer<E> {

        /** {@inheritDoc} */
        public void configure(TreeItem item, E rowValue, Object columnValue, int row, int column) {
            item.setText(column, columnValue == null ? "" : columnValue.toString()); //$NON-NLS-1$
        }
    }
}