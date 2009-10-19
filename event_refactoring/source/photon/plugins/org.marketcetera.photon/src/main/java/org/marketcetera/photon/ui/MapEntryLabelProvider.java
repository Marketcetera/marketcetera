package org.marketcetera.photon.ui;

import java.util.Map;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class MapEntryLabelProvider extends LabelProvider implements ITableLabelProvider {

	public Image getColumnImage(Object element, int columnIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		String result = ""; //$NON-NLS-1$
		Map.Entry<String, String> entry = (Map.Entry<String, String>) element;
		switch (columnIndex) {
			case 0 :
				result = ""+entry.getKey(); //$NON-NLS-1$
				break;
			case 1 :
				result = ""+entry.getValue(); //$NON-NLS-1$
				break;
			default :
				break; 	
		}
		return result;
	}
	

}
