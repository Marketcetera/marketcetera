package org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.providers;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data.ExecutionReportField;

/**
 * Label provider for the Execution report table
 * 
 * @author milan
 * 
 */
public class ExecutionReportLabelProvider extends LabelProvider implements ITableLabelProvider
{

	private static final String EMPTY_VALUE = ""; //$NON-NLS-1$

	@Override
	public Image getColumnImage(Object element, int columnIndex)
	{
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex)
	{
		ExecutionReportField executionReportPair = (ExecutionReportField) element;

		switch (columnIndex)
		{
		case 0:
			return executionReportPair.getFieldName();
		case 1:
			return executionReportPair.getSelectedValue();
		default:
			return EMPTY_VALUE;
		}
	}

}
