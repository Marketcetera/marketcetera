package org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data;

import java.util.ArrayList;
import java.util.List;

import org.marketcetera.photon.Messages;
import org.marketcetera.trade.TimeInForce;

/**
 * Time in force execution report field
 * 
 * @author milan
 *
 */
public class TimeInForceField extends ExecutionReportField 
{
	private int FIELD;
	
	public TimeInForceField(int field)
	{
		FIELD = field;
	}
	
	@Override
	public int getField() 
	{
		return FIELD;
	}
	
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
	public Object getFieldValue() 
	{
		TimeInForce timeInForce = TimeInForce.valueOf(fSelectedValue);
		return new quickfix.field.TimeInForce(timeInForce.getFIXValue());
	}
}
