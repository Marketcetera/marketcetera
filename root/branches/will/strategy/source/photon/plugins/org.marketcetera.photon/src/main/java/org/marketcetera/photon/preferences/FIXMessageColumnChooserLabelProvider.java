package org.marketcetera.photon.preferences;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class FIXMessageColumnChooserLabelProvider extends LabelProvider implements ITableLabelProvider {

	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		String result = ""; //$NON-NLS-1$
		switch (columnIndex) {
			case 0 :
				result = "" + element; //$NON-NLS-1$
				break;
			default :
				break; 	
		}
		return result;
	}
	

}
