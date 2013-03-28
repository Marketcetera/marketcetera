package org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data;

import java.util.ArrayList;
import java.util.List;

import org.marketcetera.photon.Messages;
import org.marketcetera.trade.TimeInForce;

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
	public void insertField(Message message) {
		message.setField(new quickfix.field.TimeInForce(TimeInForce.valueOf(fValue).getFIXValue()));
		
	}
}
