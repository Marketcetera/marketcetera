/**
 * 
 */
package org.marketcetera.photon.views.fixmessagedetail;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.marketcetera.photon.EclipseUtils;

import ca.odell.glazedlists.gui.TableFormat;

class FIXMessageDetailLabelProvider implements TableFormat<FIXMessageDetailTableRow>, ITableLabelProvider {
	private Table underlyingTable;

	public FIXMessageDetailLabelProvider(Table underlyingTable) {
		this.underlyingTable = underlyingTable;
		createTableColumns();
	}

	private void createZeroFirstColumn() {
		TableColumn zeroFirstColumn = new TableColumn(underlyingTable, SWT.LEFT);
		zeroFirstColumn.setWidth(0);
		zeroFirstColumn.setResizable(false);
		zeroFirstColumn.setMoveable(false);
		zeroFirstColumn.setText(""); //$NON-NLS-1$
		zeroFirstColumn.setImage(null);
	}

	private void createTableColumns() {
		createZeroFirstColumn();
		FIXMessageDetailColumnType[] columns = FIXMessageDetailColumnType
				.values();
		for (FIXMessageDetailColumnType columnInfo : columns) {
			int alignment = SWT.LEFT;
			if (columnInfo == FIXMessageDetailColumnType.Field) {
				alignment = SWT.RIGHT;
			} else if (columnInfo == FIXMessageDetailColumnType.Tag) {
				alignment = SWT.CENTER;
			}
			TableColumn column = new TableColumn(underlyingTable, alignment);
			column.setText(columnInfo.getName());
			int width = getColumnWidthHint(columnInfo.getNumChars());
			column.setWidth(width);
		}
	}

	private int getColumnWidthHint(int numChars) {
		return EclipseUtils.getTextAreaSize(underlyingTable, null, numChars,
				1.0).x;
	}

	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		if (element == null || !(element instanceof FIXMessageDetailTableRow)) {
			return null;
		}
		FIXMessageDetailTableRow row = (FIXMessageDetailTableRow) element;
		Object columnValue = row.getColumnValue(columnIndex);
		if(columnValue == null) {
			return null;
		}
		return columnValue.toString();
	}
	
	public void addListener(ILabelProviderListener listener) {
	}

	public void dispose() {
	}

	public boolean isLabelProperty(Object element, String property) {
		return true;
	}

	public void removeListener(ILabelProviderListener listener) {
	}

	public int getColumnCount() {
		return underlyingTable.getColumnCount();
	}

	public String getColumnName(int index) {
		TableColumn column = underlyingTable.getColumn(index);
		return column.getText();
	}

	public Object getColumnValue(FIXMessageDetailTableRow row, int columnIndex) {
		return row.getColumnValue(columnIndex);
	}
}