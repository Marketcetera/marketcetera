package org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data;

import java.util.ArrayList;
import java.util.List;

import org.marketcetera.quickfix.FIXVersion;
import quickfix.Message;


/**
 * Class representing table contents of the Execution Report
 * 
 * @author milan
 *
 */
public class ExecutionReportContainer
{
	/** Execution report fields */
	private List<ExecutionReportField> fExecutionReportFields;
	
	public ExecutionReportContainer()
	{
	}
	
	public void addExecutionReportField(ExecutionReportField field)
	{
		if(fExecutionReportFields == null)
			fExecutionReportFields = new ArrayList<ExecutionReportField>();
		
		if(fExecutionReportFields.contains(field))
		{
			int index = fExecutionReportFields.indexOf(field);
			ExecutionReportField existingField = fExecutionReportFields.get(index);
			
			existingField.setSelectedValue(field.getSelectedValue());
		}
		else
		{
			fExecutionReportFields.add(field);
		}
	}
	
	public void removeExecutionReportFields(ExecutionReportField[] fields)
	{
		if(fExecutionReportFields == null)
			return;
		
		for(ExecutionReportField field: fields)
		{
			if(fExecutionReportFields.contains(field))
				fExecutionReportFields.remove(field);
		}
	}
		
	public ExecutionReportField[] getExecutionReportFields()
	{
		if(fExecutionReportFields == null)
			return new ExecutionReportField[] {};
		
		return (ExecutionReportField[]) fExecutionReportFields.toArray(new ExecutionReportField[fExecutionReportFields.size()]);
	}
	
	public Message createExecutionReport()
	{
		Message message = FIXVersion.FIX_SYSTEM
				.getMessageFactory()
					.newExecutionReportEmpty();
		if(fExecutionReportFields != null)
		{
			for(ExecutionReportField reportField: fExecutionReportFields)
			{
				if(reportField.isFixField())
					reportField.insertField(message);
			}
		}
		return message;
	}	
	
	public ExecutionReportNoneFixField[] getNoneFixFields()
	{
		ArrayList<ExecutionReportNoneFixField> nonFixFields = new ArrayList<ExecutionReportNoneFixField>();
		if(fExecutionReportFields != null)
		{
			for(ExecutionReportField reportField: fExecutionReportFields)
			{
				if(!reportField.isFixField())
					nonFixFields.add((ExecutionReportNoneFixField)reportField);
			}
		}
		return nonFixFields.toArray(new ExecutionReportNoneFixField[nonFixFields.size()]);
	}
}
