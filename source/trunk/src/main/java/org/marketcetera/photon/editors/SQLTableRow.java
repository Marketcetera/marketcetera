package org.marketcetera.photon.editors;

import java.sql.ResultSet;
import java.sql.SQLException;


public class SQLTableRow {
	DBTableModel tableModel;

	final Object[] internalElements;

	SQLTableRow(Object[] objects, DBTableModel pTableModel) {
		this.tableModel = pTableModel;
		internalElements = objects;
	}

	/**
	 * Takes a ResultSet that is queued up to the row to be represented
	 * by this SQLTableRow.
	 * 
	 * @param resultSet
	 * @throws SQLException
	 */
	SQLTableRow(ResultSet resultSet) throws SQLException{
		this.tableModel = new DBTableModel(resultSet.getMetaData());
		int numColumns = tableModel.getNumColumns();
		internalElements = new Object[numColumns];
		for (int i = 0; i < numColumns; i++){
			internalElements[i] = resultSet.getObject(i+1); // Stupid JDBC indexes start with one
		}
	};

	public final int getSize() {
		return internalElements.length;
	}

	public final Object getValue(String property) {
		int i = tableModel.getColumnIndex(property);
		if (i != -1) {
			return getValue(i);
		}
		return null;
	}

	public final Object getInternalValue(String property) {
		int i = tableModel.getColumnIndex(property);
		if (i != -1) {
			return getInternalValue(i);
		}
		return null;
	}
	public final Object getValue(int k) {
		if (internalElements[k] != null)
			return internalElements[k].toString();
		return "<NULL>"; //$NON-NLS-1$
	}

	public final Object getInternalValue(int k) {
		return internalElements[k];
	}

	public final Object[] getInternalArray() {
		return internalElements;
	}


}