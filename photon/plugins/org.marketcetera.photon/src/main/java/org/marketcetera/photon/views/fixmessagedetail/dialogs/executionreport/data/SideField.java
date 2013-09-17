package org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data;

import java.util.ArrayList;
import java.util.List;

import org.marketcetera.photon.Messages;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.Side;

import quickfix.FieldNotFound;
import quickfix.Message;

/**
 * Side execution report field
 * 
 * @author milan
 *
 */
public class SideField extends ExecutionReportField 
{	
	@Override
	public String getFieldName() 
	{
		return Messages.EXECUTION_REPORT_FIELD_SIDE.getText();
	}

	@Override
	public String[] getValues() 
	{
		List<String> sideValues = new ArrayList<String>();
		for(Side side: Side.values())
		{
			sideValues.add(side.name());
		}
	
		return (String[]) sideValues.toArray(new String[sideValues.size()]);
	}

	@Override
	public void insertField(Message message) 
	{
        if(fValue != null && fValue != EMPTY_STRING) {
			message.setField(new quickfix.field.Side(Side.valueOf(fValue).getFIXValue()));
		}
	}

	@Override
	public void parseFromReport(ExecutionReport executionReport) 
	{
		Message message = getMessageFromExecutionReport(executionReport);
		
		if(message != null && message.isSetField(quickfix.field.Side.FIELD)) 
		{
			quickfix.field.Side side = new quickfix.field.Side();
			try 
			{
				fValue = Side.getInstanceForFIXValue(message.getField(side).getValue()).name();
			} 
			catch (FieldNotFound e) 
			{
			}
		}	
	}

	@Override
	public int getFieldTag() 
	{
		return quickfix.field.Side.FIELD;
	}
}
