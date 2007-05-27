package org.marketcetera.photon.preferences;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class FIXMessageFieldChooserLabelProvider extends LabelProvider implements ITableLabelProvider {

	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		String result = "";
		switch (columnIndex) {
			case 0 :
				result = "" + element;
				break;
			default :
				break; 	
		}
		return result;
	}
	

}
