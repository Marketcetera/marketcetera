package org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.providers;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data.ExecutionReportField;

/**
 * Label provider for the Fields combo box inside the Execution report dialog
 * 
 * @author milan
 * 
 */
public class ExecutionReportFieldLabelProvider extends LabelProvider implements ITableLabelProvider
{

	@Override
	public String getColumnText(Object element, int columnIndex)
	{
		return super.getText(element);
	}

	@Override
	public String getText(Object element)
	{
		if(element instanceof ExecutionReportField) 
		{
			ExecutionReportField executionReportField = (ExecutionReportField) element;
			
			return executionReportField.getFieldName();
			
		}
		
		return super.getText(element);
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex)
	{
		return null;
	}

}
