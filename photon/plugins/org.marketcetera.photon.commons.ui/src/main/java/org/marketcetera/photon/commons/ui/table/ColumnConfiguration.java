package org.marketcetera.photon.commons.ui.table;

import java.util.Comparator;

import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.swt.SWT;
import org.marketcetera.util.misc.ClassVersion;

/**
 * Configuration for columns in a {@link TableConfiguration}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public final class ColumnConfiguration {

	/**
	 * Returns a configuration for a movable, resizable column with the following default values:
	 * <ul>
	 * <li>Column weight: 10</code></li>
	 * </ul>
	 * 
	 * @return the default configuration
	 */
	public static ColumnConfiguration defaults() {
		return new ColumnConfiguration();
	}

	/**
	 * Returns a configuration for a movable column that is initially hidden.
	 * 
	 * @return the default configuration for a hidden column
	 */
	public static ColumnConfiguration hidden() {
		return new ColumnConfiguration().layoutData(new ColumnPixelData(0, false, false));
	}

	private int mColumnStyle = SWT.LEFT;
	private boolean mMovable = true;
	private boolean mSortable = true;
	private String mHeading;
	private String mBeanProperty;
	private ColumnLayoutData mLayoutData = new ColumnWeightData(10);
	private Comparator<?> mComparator;

	private ColumnConfiguration() {
	}

	public ColumnConfiguration heading(String heading) {
		mHeading = heading;
		return this;
	}

	/**
	 * Configures the SWT style bit for this column.
	 * 
	 * @param style
	 *            the SWT style bit
	 * @return the current configuration for method chaining
	 */
	public ColumnConfiguration style(int style) {
		mColumnStyle = style;
		return this;
	}

	/**
	 * Configures the bean property corresponding to this column.
	 * 
	 * @param beanProperty
	 *            the bean property
	 * @return the current configuration for method chaining
	 */
	public ColumnConfiguration beanProperty(String beanProperty) {
		mBeanProperty = beanProperty;
		return this;
	}

	/**
	 * Configures the {@link ColumnLayoutData} for this column.
	 * 
	 * @param layoutData
	 *            the column layout data
	 * @return the current configuration for method chaining
	 */
	public ColumnConfiguration layoutData(ColumnLayoutData layoutData) {
		mLayoutData = layoutData;
		return this;
	}

	/**
	 * Configures whether the column should be movable.
	 * 
	 * @param movable
	 *            whether the column should be movable
	 * @return the current configuration for method chaining
	 */
	public ColumnConfiguration movable(boolean movable) {
		mMovable = movable;
		return this;
	}

	/**
	 * Configures whether the column should be sortable.
	 * 
	 * @param sortable
	 *            whether the column should be sortable
	 * @return the current configuration for method chaining
	 */
	public ColumnConfiguration sortable(boolean sortable) {
		mSortable = sortable;
		return this;
	}

	/**
	 * Configures the comparator for objects in this column. If no comparator is specified,
	 * {@link TableSupport} will attempt to cast the objects to {@link Comparable}. If the case
	 * fails, the objects will be compared by the results of their {@link #toString()} method.
	 * 
	 * @param comparator
	 *            the comparator
	 * @return the current configuration for method chaining
	 */
	public ColumnConfiguration comparator(Comparator<?> comparator) {
		mComparator = comparator;
		return this;
	}

	int getStyle() {
		return mColumnStyle;
	}

	String getHeading() {
		return mHeading;
	}

	String getBeanProperty() {
		return mBeanProperty;
	}

	ColumnLayoutData getLayoutData() {
		return mLayoutData;
	}

	boolean isResizable() {
		return mLayoutData.resizable;
	}

	boolean isMovable() {
		return mMovable;
	}

	boolean isSortable() {
		return mSortable;
	}

	Comparator<?> getComparator() {
		return mComparator;
	}
}