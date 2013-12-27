package org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.marketcetera.event.HasFIXMessage;
import org.marketcetera.photon.Messages;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.ExecutionType;
import org.marketcetera.trade.OrderStatus;

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

	public void fillBreakTradeFromExecutionReport(ExecutionReport executionReport)
	{
		Map<Integer, ExecutionReportField> executionReportFields = new HashMap<Integer, ExecutionReportField>();

		ExecutionReportFixFields fixFields = new ExecutionReportFixFields();

		ExecutionReportField[] presetReportFields = fixFields.getExecutionReportFields();
		for(ExecutionReportField field: presetReportFields)
		{
			if(field.getFieldName().equals(Messages.EXECUTION_REPORT_FIELD_CUM_QTY.getText()))
			{
				field.setSelectedValue(BigDecimal.ZERO.toPlainString());
			}
			else if(field.getFieldName().equals(Messages.EXECUTION_REPORT_FIELD_LAST_QTY.getText()))
			{
				field.setSelectedValue(BigDecimal.ZERO.toPlainString());
			}
			else if(field.getFieldName().equals(Messages.EXECUTION_REPORT_FIELD_LEAVES_QTY.getText()))
			{
				BigDecimal orderQty = executionReport.getOrderQuantity();
				if(orderQty != null)
				{
					field.setSelectedValue(orderQty.toPlainString());
				}
				else
				{
					field.parseFromReport(executionReport);
				}
			}
			else if(field.getFieldName().equals(Messages.EXECUTION_REPORT_FIELD_EXECUTION_TYPE.getText()))
			{
				field.setSelectedValue(ExecutionType.Restated.name());
			}
			else if(field.getFieldName().equals(Messages.EXECUTION_REPORT_FIELD_ORDER_STATUS.getText()))
			{
				field.setSelectedValue(OrderStatus.Expired.name());
			}
			else if(field.getFieldName().equals(Messages.EXECUTION_REPORT_FIELD_SENDING_TIME.getText()))
			{
				Date sendingTime = executionReport.getSendingTime();
				if(sendingTime != null)
				{
					Calendar cal = Calendar.getInstance();
					cal.setTime(sendingTime);
					cal.add(Calendar.SECOND, 1);
					field.setSelectedDateValue(cal.getTime());
				}
				else
				{
					field.parseFromReport(executionReport);	
				}
			}
			else
			{
				field.parseFromReport(executionReport);				
			}

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
