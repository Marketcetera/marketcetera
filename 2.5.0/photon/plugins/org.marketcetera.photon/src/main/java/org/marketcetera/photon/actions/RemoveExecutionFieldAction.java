package org.marketcetera.photon.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableItem;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.AddExecutionReportDialog;
import org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data.ExecutionReportField;

public class RemoveExecutionFieldAction extends Action
{
	/** Parent dialog */
	private final AddExecutionReportDialog fParentDialog;
	
	/** Execution report table */
	private final TableViewer fTableViewer;
	
	public RemoveExecutionFieldAction(TableViewer tableViewer, AddExecutionReportDialog parentDialog)
	{
		fTableViewer = tableViewer;
		fParentDialog = parentDialog;
	}
	
	@Override
	public boolean isEnabled()
	{
		if(fTableViewer.getTable().getSelectionCount() == 0)
			return false;
		
		return true;
	}
	
	@Override
	public String getText()
	{
		return Messages.ADD_EXECUTION_REPORT_DIALOG_TABLE_CONTEXT_REMOVE.getText();
	}
	
	@Override
	public void run()
	{
		TableItem[] items = fTableViewer.getTable().getSelection();
		
		List<ExecutionReportField> executionReportPairs = new ArrayList<ExecutionReportField>();
		
		for(TableItem item: items)
		{
			executionReportPairs.add((ExecutionReportField) item.getData());
		}
		
		// Update model
		fParentDialog.removeFromExecutionReport((ExecutionReportField[]) executionReportPairs.toArray(new ExecutionReportField[items.length]));
	}
}
