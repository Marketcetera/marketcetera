package org.marketcetera.photon.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;

public class SQLTableContentProvider implements IStructuredContentProvider, ITableLabelProvider
{

	DBTableModel model = null;

	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof ResultSet) {
			ResultSet rs = (ResultSet) inputElement;

			try {
				ArrayList<SQLTableRow> rows = new ArrayList<SQLTableRow>();

				while (rs.next()) {
					rows.add(new SQLTableRow(rs));
				}
				return rows.toArray();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return new Object[0];
	}

	public void dispose() {
		// TODO Auto-generated method stub

	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput instanceof ResultSet) {
			ResultSet rs = (ResultSet) newInput;
			if (model == null) {
				try {
					model = new DBTableModel(rs.getMetaData());
				} catch (SQLException e) {
					e.printStackTrace();
					model = null;
				}
			}
		}
	}

	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		return ((SQLTableRow)element).getValue(columnIndex).toString();
	}


	public boolean isLabelProperty(Object element, String property) {
		return true;
	}

	public void addListener(ILabelProviderListener listener) {
	}

	public void removeListener(ILabelProviderListener listener) {
	}

}
