package org.marketcetera.photon.db;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DBTableModel {

	private Map<String, Integer> columnMap = new HashMap<String, Integer>();

	ResultSetMetaData metaData;

	String[] columnNames;


	public DBTableModel(ResultSetMetaData metaData) {
		this.metaData = metaData;
		int count;
		try {
			count = metaData.getColumnCount();
			columnNames = new String[count];
			for (int i = 0; i < count; i++) {
				columnNames[i] = metaData.getColumnName(i + 1);
				columnMap.put(columnNames[i], new Integer(i));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int getColumnIndex(String property) {
		Integer ind = (Integer) columnMap.get(property);
		int i = -1;
		if (ind != null) {
			i = ind.intValue();
		}

		return i;
	}

	/**
	 * 
	 */
	public String[] getColumns() throws SQLException {
		return columnNames;
	}
	
	public int getNumColumns()
	{
		return columnNames.length;
	}

}