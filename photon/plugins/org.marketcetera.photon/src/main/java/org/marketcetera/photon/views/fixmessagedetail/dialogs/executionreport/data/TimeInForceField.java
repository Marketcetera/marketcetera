package org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data;

import java.util.ArrayList;
import java.util.List;

import org.marketcetera.photon.Messages;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.TimeInForce;

import quickfix.FieldNotFound;
import quickfix.Message;

/**
 * Time in force execution report field
 * 
 * @author milan
 *
 */
public class TimeInForceField extends ExecutionReportField 
{
	
	@Override
	public String getFieldName() 
	{
		return Messages.EXECUTION_REPORT_FIELD_TIME_IN_FORCE.getText();
	}

	@Override
	public String[] getValues() 
	{
		List<String> timeInForceValues = new ArrayList<String>();
		for(TimeInForce timeInForce: TimeInForce.values())
		{
			timeInForceValues.add(timeInForce.name());
		}
	
		return (String[]) timeInForceValues.toArray(new String[timeInForceValues.size()]);
	}

	@Override
	public void insertField(Message message) 
	{
		if(fValue != null)
		{
			message.setField(new quickfix.field.TimeInForce(TimeInForce.valueOf(fValue).getFIXValue()));
		}
	}

	@Override
	public void parseFromReport(ExecutionReport executionReport) 
	{
		Message message = getMessageFromExecutionReport(executionReport);
		
		if(message != null && message.isSetField(quickfix.field.TimeInForce.FIELD)) 
		{
			quickfix.field.TimeInForce timeInForce = new quickfix.field.TimeInForce();
			try 
			{
				fValue = TimeInForce.getInstanceForFIXValue(message.getField(timeInForce).getValue()).name();
			} 
			catch (FieldNotFound e) 
			{
			}
		}	
	}

	@Override
	public int getFieldTag() 
	{
		return quickfix.field.TimeInForce.FIELD;
	}
}
