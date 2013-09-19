package org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data;

import java.util.ArrayList;
import java.util.List;

import org.marketcetera.photon.Messages;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.ExecutionType;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.ExecType;

/**
 * Execution type execution report field
 * 
 * @author milan
 *
 */
public class ExecutionTypeField extends ExecutionReportField 
{
	
	@Override
	public String getFieldName() 
	{
		return Messages.EXECUTION_REPORT_FIELD_EXECUTION_TYPE.getText();
	}

	@Override
	public String[] getValues() 
	{
		List<String> executionTypeValues = new ArrayList<String>();
		for(ExecutionType executionType: ExecutionType.values())
		{
			executionTypeValues.add(executionType.name());
		}
	
		return executionTypeValues.toArray(new String[executionTypeValues.size()]);
	}

	@Override
	public void insertField(Message message) 
	{
        if(fValue != null && fValue != EMPTY_STRING) {
			message.setField(new ExecType(ExecutionType.valueOf(fValue).getFIXValue()));
		}
	}

	@Override
	public void parseFromReport(ExecutionReport executionReport) 
	{
		Message message = getMessageFromExecutionReport(executionReport);
		
		if(message != null && message.isSetField(ExecType.FIELD)) 
		{
			ExecType execType = new ExecType();
			try 
			{
				fValue = ExecutionType.getInstanceForFIXValue(message.getField(execType).getValue()).name();
			} 
			catch (FieldNotFound e) 
			{
			}
		}
	}

	@Override
	public int getFieldTag() 
	{
		return ExecType.FIELD;
	}
}
