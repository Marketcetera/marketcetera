package org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.providers;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data.ExecutionReportFixFields;

/**
 * Content provider for the Fields combo box inside the Execution report dialog
 * 
 * @author milan
 * 
 */
public class ExecutionReportFieldContentProvider implements IStructuredContentProvider
{

	private ExecutionReportFixFields fExecutionReportExistingFields;

	@Override
	public void dispose()
	{
		fExecutionReportExistingFields = null;
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
		fExecutionReportExistingFields = (ExecutionReportFixFields) newInput;
	}

	@Override
	public Object[] getElements(Object inputElement)
	{
		return fExecutionReportExistingFields.getExecutionReportFields();
	}

}
