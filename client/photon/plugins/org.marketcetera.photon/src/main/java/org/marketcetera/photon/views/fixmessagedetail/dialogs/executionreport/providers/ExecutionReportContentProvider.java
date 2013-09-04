package org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.providers;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data.ExecutionReportContainer;

/**
 * Content provider for the Execution report table
 * 
 * @author milan
 * 
 */
public class ExecutionReportContentProvider implements IStructuredContentProvider
{

	private ExecutionReportContainer fExecutionReport;

	@Override
	public void dispose()
	{
		fExecutionReport = null;
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
		fExecutionReport = (ExecutionReportContainer) newInput;
	}

	@Override
	public Object[] getElements(Object inputElement)
	{
		return fExecutionReport.getExecutionReportFields();
	}

}
