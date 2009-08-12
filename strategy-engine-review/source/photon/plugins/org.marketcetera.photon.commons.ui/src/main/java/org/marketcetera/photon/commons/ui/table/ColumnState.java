package org.marketcetera.photon.commons.ui.table;

import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IMemento;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Persists and restores table/tree column state information.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public class ColumnState {

	private static final String RESTORED_WIDTH_KEY = "restoredWidth"; //$NON-NLS-1$
	private static final String COLUMN_WIDTHS = "COLUMN_WIDTHS"; //$NON-NLS-1$
	private static final String COLUMN_ORDER = "COLUMN_ORDER"; //$NON-NLS-1$
	private static final String COLUMN_RESTORED_WIDTHS = "COLUMN_RESTORED_WIDTHS"; //$NON-NLS-1$

	/**
	 * Saves tree column state to the given memento.
	 * 
	 * @param tree the tree
	 * @param memento the memento
	 */
	public static void save(Tree tree, IMemento memento) {
		memento.putString(COLUMN_ORDER, serialize(tree.getColumnOrder()));
		final TreeColumn[] columns = tree.getColumns();
		int[] columnWidths = new int[columns.length];
		for (int i = 0; i < columns.length; i++) {
			columnWidths[i] = columns[i].getWidth();
		}
		memento.putString(COLUMN_WIDTHS, serialize(columnWidths));
		int[] restoredWidths = new int[columns.length];
		for (int i = 0; i < columns.length; i++) {
			final Integer restoredWidth = (Integer) columns[i]
					.getData(RESTORED_WIDTH_KEY);
			restoredWidths[i] = restoredWidth == null ? 70 : restoredWidth;
		}
		memento.putString(COLUMN_RESTORED_WIDTHS, serialize(restoredWidths));
	}

	/**
	 * Restores state from the memento to the tree.
	 * 
	 * @param tree the tree
	 * @param memento the memento
	 */
	public static void restore(Tree tree, IMemento memento) {
		if (memento != null) {
			String columnOrderString = memento.getString(COLUMN_ORDER);
			if (columnOrderString != null) {
				int[] columnOrder = deserialize(columnOrderString);
				if (columnOrder.length == tree.getColumns().length) {
					tree.setColumnOrder(columnOrder);
				}
			}
			String columnWidthsString = memento.getString(COLUMN_WIDTHS);
			if (columnWidthsString != null) {
				int[] columnWidths = deserialize(columnWidthsString);
				if (columnWidths.length == tree.getColumns().length) {
					for (int i = 0; i < columnWidths.length; i++) {
						tree.getColumn(i).setWidth(columnWidths[i]);
					}
				}
			}
			String columnRestoredWidthsString = memento
					.getString(COLUMN_RESTORED_WIDTHS);
			if (columnRestoredWidthsString != null) {
				int[] restoredWidths = deserialize(columnRestoredWidthsString);
				if (restoredWidths.length == tree.getColumns().length) {
					for (int i = 0; i < restoredWidths.length; i++) {
						tree.getColumn(i).setData(RESTORED_WIDTH_KEY,
								restoredWidths[i]);
					}
				}
			}
		}
	}

	/**
	 * Saves table column state to the given memento.
	 * 
	 * @param table the table
	 * @param memento the memento
	 */
	public static void save(Table table, IMemento memento) {
		memento.putString(COLUMN_ORDER, serialize(table.getColumnOrder()));
		final TableColumn[] columns = table.getColumns();
		int[] columnWidths = new int[columns.length];
		for (int i = 0; i < columns.length; i++) {
			columnWidths[i] = columns[i].getWidth();
		}
		memento.putString(COLUMN_WIDTHS, serialize(columnWidths));
		int[] restoredWidths = new int[columns.length];
		for (int i = 0; i < columns.length; i++) {
			final Integer restoredWidth = (Integer) columns[i]
					.getData(RESTORED_WIDTH_KEY);
			restoredWidths[i] = restoredWidth == null ? 70 : restoredWidth;
		}
		memento.putString(COLUMN_RESTORED_WIDTHS, serialize(restoredWidths));
	}

	/**
	 * Restores state from the memento to the table.
	 * 
	 * @param table the table
	 * @param memento the memento
	 */
	public static void restore(Table table, IMemento memento) {
		if (memento != null) {
			String columnOrderString = memento.getString(COLUMN_ORDER);
			if (columnOrderString != null) {
				int[] columnOrder = deserialize(columnOrderString);
				if (columnOrder.length == table.getColumns().length) {
					table.setColumnOrder(columnOrder);
				}
			}
			String columnWidthsString = memento.getString(COLUMN_WIDTHS);
			if (columnWidthsString != null) {
				int[] columnWidths = deserialize(columnWidthsString);
				if (columnWidths.length == table.getColumns().length) {
					for (int i = 0; i < columnWidths.length; i++) {
						table.getColumn(i).setWidth(columnWidths[i]);
					}
				}
			}
			String columnRestoredWidthsString = memento
					.getString(COLUMN_RESTORED_WIDTHS);
			if (columnRestoredWidthsString != null) {
				int[] restoredWidths = deserialize(columnRestoredWidthsString);
				if (restoredWidths.length == table.getColumns().length) {
					for (int i = 0; i < restoredWidths.length; i++) {
						table.getColumn(i).setData(RESTORED_WIDTH_KEY,
								restoredWidths[i]);
					}
				}
			}
		}
	}

	private static String serialize(int[] array) {
		StringBuilder builder = new StringBuilder();
		if (array.length > 0) {
			builder.append(array[0]);
			for (int i = 1; i < array.length; i++) {
				builder.append(',');
				builder.append(array[i]);
			}
		}
		return builder.toString();
	}

	private static int[] deserialize(String string) {
		String[] split = string.split(","); //$NON-NLS-1$
		int[] array = new int[split.length];
		try {
			for (int i = 0; i < split.length; i++) {
				array[i] = Integer.parseInt(split[i]);
			}
		} catch (NumberFormatException e) {
			return new int[0];
		}
		return array;
	}
}
