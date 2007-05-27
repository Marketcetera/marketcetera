package org.marketcetera.photon.preferences;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class FIXMessageFieldChooserLabelProvider extends LabelProvider implements ITableLabelProvider {

	public Image getColumnImage(Object element, int columnIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		String result = "";
		switch (columnIndex) {
			case 0 :
				result = "("+ element + ")";  //cl todo:add field name as well
				break;
			default :
				break; 	
		}
		return result;
	}
	

}
