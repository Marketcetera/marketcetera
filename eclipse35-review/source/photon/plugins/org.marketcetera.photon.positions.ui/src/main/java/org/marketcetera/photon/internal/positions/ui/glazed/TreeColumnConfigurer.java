package org.marketcetera.photon.internal.positions.ui.glazed;
import org.eclipse.swt.widgets.TreeColumn;
import org.marketcetera.util.misc.ClassVersion;

import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swt.TableColumnConfigurer;

/* $License$ */

/**
 * Optional interface to be implemented by a {@link TableFormat} implementation usable by an
 * {@link EventTreeViewer}. For each tree column the viewer creates it calls the
 * {@link #configure(TreeColumn, int)} method to allow customization of the tree column.
 * 
 * Derived from {@link TableColumnConfigurer}.
 * 
 * @see TableColumnConfigurer 
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public interface TreeColumnConfigurer {

    /**
     * Callback method to allow customization of the specified tree column.
     *
     * @param treeColumn the tree column
     * @param column the corresponding column index
     */
    void configure(TreeColumn treeColumn, int column);
}
