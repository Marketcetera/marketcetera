package org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.marketcetera.event.HasFIXMessage;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.quickfix.FIXDataDictionary;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.trade.ExecutionReport;

import quickfix.Field;
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

	public void fillFromExecutionReport(ExecutionReport executionReport) 
	{
        Map<Integer, ExecutionReportField> executionReportFields = new HashMap<Integer, ExecutionReportField>();
		
		ExecutionReportFixFields fixFields = new ExecutionReportFixFields();
		
		ExecutionReportField[] presetReportFields = fixFields.getExecutionReportFields();
		for(ExecutionReportField field: presetReportFields)
		{
			field.parseFromReport(executionReport);
			executionReportFields.put(new Integer(field.getFieldTag()), field);
		}
		
		Message message = getMessageFromExecutionReport(executionReport);
		
		Iterator<Field<?>> fieldIterator = message.iterator();
		while (fieldIterator.hasNext()) 
		{
			Field<?> field = (Field<?>) fieldIterator.next();
			Integer fieldTag = new Integer(field.getTag());
			
			if(!executionReportFields.containsKey(fieldTag))
			{
				ExecutionReportField reportField = new CustomFixField(field.getTag());
				reportField.setSelectedValue(field.getObject().toString());
				executionReportFields.put(fieldTag, reportField);
			}
		}
		
		for(Integer fieldTag: executionReportFields.keySet())
		{
			addExecutionReportField(executionReportFields.get(fieldTag));
		}
	}
	
	private Message getMessageFromExecutionReport(ExecutionReport executionReport)
	{
		HasFIXMessage hasFixMessage = (HasFIXMessage) executionReport;
		return hasFixMessage.getMessage();
	}
}
