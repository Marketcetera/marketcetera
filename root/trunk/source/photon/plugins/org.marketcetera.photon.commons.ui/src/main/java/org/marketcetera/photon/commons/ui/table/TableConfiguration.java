package org.marketcetera.photon.commons.ui.table;

import org.eclipse.swt.SWT;
import org.marketcetera.util.misc.ClassVersion;

/**
 * Configuration for {@link TableSupport}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public final class TableConfiguration {

	/**
	 * Returns a configuration with no columns, no header, and the following
	 * default values:
	 * <ul>
	 * <li>Table Style: <code>SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL
			| SWT.BORDER</code></li>
	 * </ul>
	 * 
	 * @return the default configuration
	 */
	public static TableConfiguration defaults() {
		return new TableConfiguration();
	}
	
	private int mTableStyle = SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL
			| SWT.BORDER;
	private ColumnConfiguration[] mColumns = new ColumnConfiguration[0];
	private Class<?> mItemClass;
	private boolean mDynamicColumns;
	private boolean mHeaderVisible;

	private TableConfiguration() {
	}

	/**
	 * Configures the SWT style bits for the table widget.
	 * 
	 * @param tableStyle
	 *            SWT style bits for the table widget
	 * @return the current configuration for method chaining
	 */
	public TableConfiguration tableStyle(int tableStyle) {
		mTableStyle = tableStyle;
		return this;
	}

	/**
	 * Configures the columns this table will contain.
	 * 
	 * @param columns
	 *            the configuration of the table columns
	 * @return the current configuration for method chaining
	 */
	public TableConfiguration columns(ColumnConfiguration[] columns) {
		mColumns = columns;
		return this;
	}

	/**
	 * Configures the class for items (rows) in the table.
	 * 
	 * @param itemClass
	 *            the item class
	 * @return the current configuration for method chaining
	 */
	public TableConfiguration itemClass(Class<?> itemClass) {
		mItemClass = itemClass;
		return this;
	}

	/**
	 * Configures whether columns should update dynamically from the model. If
	 * <code>true</code>, then the class specified by {@link #itemClass(Class)}
	 * must support property change listeners.
	 * 
	 * @param dynamicColumns
	 *            whether columns should update dynamically from the model
	 * @return the current configuration for method chaining
	 */
	public TableConfiguration dynamicColumns(boolean dynamicColumns) {
		mDynamicColumns = dynamicColumns;
		return this;
	}

	/**
	 * Configures whether the table header should be visible.
	 * 
	 * @param headerVisible
	 *            whether the table header should be visible
	 * @return the current configuration for method chaining
	 */
	public TableConfiguration headerVisible(boolean headerVisible) {
		mHeaderVisible = headerVisible;
		return this;
	}

	int getTableStyle() {
		return mTableStyle;
	}

	ColumnConfiguration[] getColumns() {
		return mColumns;
	}

	Class<?> getItemClass() {
		return mItemClass;
	}

	boolean isDynamicColumns() {
		return mDynamicColumns;
	}

	boolean isHeaderVisible() {
		return mHeaderVisible;
	}
}