package org.marketcetera.photon.commons.ui.table;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Comparator;

import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Utility class that provides configurable common functionality for a JFace {@link TableViewer}.
 * <p>
 * Features:
 * <ul>
 * <li>Sortable Columns</li>
 * <li>Bean aware content and label providers</li>
 * <ul>
 * 
 * @see TableConfiguration
 * @see ColumnConfiguration
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public class TableSupport {

	/**
	 * Creates a {@link TableSupport} with the provided configuration.
	 * 
	 * @param configuration
	 *            table configuration
	 * @return the TableSupport
	 */
	public static TableSupport create(TableConfiguration configuration) {
		return new TableSupport(configuration);
	}

	private final TableConfiguration mConfiguration;

	private boolean mControlCreated;

	private TableViewer mTableViewer;

	private TableSupport(TableConfiguration configuration) {
		mConfiguration = configuration;
	}

	/**
	 * Creates the table on the provided parent composite.
	 * 
	 * @param parent
	 *            the parent widget
	 */
	public void createTable(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		TableColumnLayout layout = new TableColumnLayout();
		container.setLayout(layout);
		final Table table = new Table(container, mConfiguration.getTableStyle());
		mTableViewer = new TableViewer(table);

		final ColumnComparator comparator = new ColumnComparator();

		SelectionListener listener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// determine new sort column and direction
				TableColumn sortColumn = table.getSortColumn();
				TableColumn currentColumn = (TableColumn) e.widget;
				final int index = table.indexOf(currentColumn);
				int dir = table.getSortDirection();
				if (sortColumn == currentColumn) {
					dir = dir == SWT.UP ? SWT.DOWN : SWT.UP;
				} else {
					table.setSortColumn(currentColumn);
					dir = SWT.UP;
				}
				table.setSortDirection(dir);
				comparator.setSort(dir == SWT.UP ? 1 : -1);
				comparator.setIndex(index);
				mTableViewer.refresh();
			}
		};

		ColumnConfiguration[] descriptors = mConfiguration.getColumns();
		String[] beanProperties = new String[descriptors.length];
		for (int i = 0; i < descriptors.length; i++) {
			ColumnConfiguration descriptor = descriptors[i];
			TableColumn c = new TableColumn(table, descriptor.getStyle());
			if (mConfiguration.isHeaderVisible() && descriptor.getHeading() != null) {
				c.setText(descriptor.getHeading());
			}
			c.setResizable(descriptor.isResizable());
			c.setMoveable(descriptor.isMovable());
			if (descriptor.isSortable()) {
				c.addSelectionListener(listener);
			}
			layout.setColumnData(c, descriptor.getLayoutData());
			beanProperties[i] = descriptor.getBeanProperty();
			new TableViewerColumn(mTableViewer, c);
		}
		mTableViewer.getTable().setHeaderVisible(mConfiguration.isHeaderVisible());

		// if no class was provided, don't set up comparator, content, or label providers, assume
		// the users of this class will handle manually
		if (mConfiguration.getItemClass() != null) {
			mTableViewer.setComparator(comparator);
			ObservableListContentProvider contentProvider = new ObservableListContentProvider();
			mTableViewer.setContentProvider(contentProvider);
			final IObservableMap[] columnMaps;
			if (mConfiguration.isDynamicColumns()) {
				columnMaps = BeansObservables.observeMaps(contentProvider.getKnownElements(),
						mConfiguration.getItemClass(), beanProperties);
			} else {
				columnMaps = PojoObservables.observeMaps(contentProvider.getKnownElements(),
						mConfiguration.getItemClass(), beanProperties);
			}
			mTableViewer.setLabelProvider(new ObservableMapLabelProvider(columnMaps));
		}

		mControlCreated = true;
	}

	public void setInput(IObservableList input) {
		mTableViewer.setInput(input);
	}

	/**
	 * Returns the {@link TableViewer} managed by this object.
	 * 
	 * @return the TableViewer managed by this object
	 * @throws IllegalStateException
	 *             if createTable has not been called
	 */
	public TableViewer getTableViewer() {
		if (!mControlCreated) throw new IllegalStateException();
		return mTableViewer;
	}

	/**
	 * Focuses on the table managed by this object.
	 * 
	 * @throws IllegalStateException
	 *             if createTable has not been called
	 */
	public void setFocus() {
		if (!mControlCreated) throw new IllegalStateException();
		mTableViewer.getTable().setFocus();
	}

	/**
	 * Handles column sorting.
	 * 
	 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
	 * @version $Id$
	 * @since 1.0.0
	 */
	@ClassVersion("$Id$")
	private class ColumnComparator extends ViewerComparator {

		private int mIndex = -1;
		private int mDirection = 0;

		/**
		 * Sets the sort column index.
		 * 
		 * @param index
		 *            index of sort column
		 */
		void setIndex(int index) {
			mIndex = index;
		}

		/**
		 * Sets the sort direction.
		 * 
		 * @param direction
		 *            sort direction, 1 for ascending and -1 for descending
		 */
		void setSort(int direction) {
			mDirection = direction;
		}

		@SuppressWarnings("unchecked")
		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			if (mIndex == -1) return 0;
			ColumnConfiguration descriptor = mConfiguration.getColumns()[mIndex];
			Object item1 = getColumnObject(e1);
			Object item2 = getColumnObject(e2);
			int compare = 0;
			if (item1 == null && item2 == null) {
				return 0;
			} else if (item1 == null) {
				compare = -1;
			} else if (item2 == null) {
				compare = 1;
			} else {
				Comparator comparator = descriptor.getComparator();
				if (comparator != null) {
					compare = comparator.compare(item1, item2);
				} else if (item1 instanceof Comparable) {
					compare = ((Comparable) item1).compareTo(item2);
				} else {
					compare = item1.toString().compareTo(item2.toString());
				}
			}
			return mDirection * compare;
		}

		private Object getColumnObject(Object row) {
			PropertyDescriptor property = getPropertyDescriptor(mConfiguration.getItemClass(),
					mConfiguration.getColumns()[mIndex].getBeanProperty());
			try {
				return property.getReadMethod().invoke(row);
			} catch (Exception e) {
				throw new IllegalArgumentException(row.getClass().getName(), e);
			}
		}

		private PropertyDescriptor getPropertyDescriptor(Class<?> beanClass, String propertyName) {
			BeanInfo beanInfo;
			try {
				beanInfo = Introspector.getBeanInfo(beanClass);
			} catch (IntrospectionException e) {
				// cannot introspect, give up
				throw new IllegalArgumentException(beanClass.getName(), e);
			}
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			for (int i = 0; i < propertyDescriptors.length; i++) {
				PropertyDescriptor descriptor = propertyDescriptors[i];
				if (descriptor.getName().equals(propertyName)) {
					return descriptor;
				}
			}
			throw new IllegalArgumentException(
					"Could not find property with name " + propertyName + " in class " + beanClass); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

}
