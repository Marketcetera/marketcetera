package org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.providers;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data.ExecutionReportField;

/**
 * Comparator class for the Filters table
 * 
 * @author milan
 * 
 */
public class ExecutionReportComparator extends ViewerComparator
{
	
	
	public ExecutionReportComparator()
	{
		// TODO Auto-generated constructor stub
	}

	public int compare(Viewer viewer, Object object1, Object object2)
	{
		if ((object1 instanceof ExecutionReportField) && (object2 instanceof ExecutionReportField))
		{
			/**
			 * Do comparison
			 */
		}
		return super.compare(viewer, object1, object2);
	}

	public boolean isSorterProperty(Object element, String property)
	{
		return true;
	}
	
}
