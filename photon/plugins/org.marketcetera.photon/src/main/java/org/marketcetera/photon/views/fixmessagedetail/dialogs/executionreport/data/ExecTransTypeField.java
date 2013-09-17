package org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data;

import java.util.ArrayList;
import java.util.List;

import org.marketcetera.photon.Messages;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.ExecutionTransType;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.ExecTransType;

/**
 * Execution transact type execution report field
 * 
 * @author milan
 *
 */
public class ExecTransTypeField extends ExecutionReportField {
	@Override
	public String getFieldName() 
	{
		return Messages.EXECUTION_REPORT_FIELD_EXEC_TRANS_TYPE.getText();
	}

	@Override
	public String[] getValues() 
	{
		List<String> executionTransTypeValues = new ArrayList<String>();
		for(ExecutionTransType executionTransType: ExecutionTransType.values())
		{
			executionTransTypeValues.add(executionTransType.name());
		}
	
		return executionTransTypeValues.toArray(new String[executionTransTypeValues.size()]);
	}

	@Override
	public void insertField(Message message) 
	{
		if(fValue != null)
		{
			message.setField(new ExecTransType(ExecutionTransType.valueOf(fValue).getFIXValue()));
		}
	}

	@Override
	public void parseFromReport(ExecutionReport executionReport) 
	{
		Message message = getMessageFromExecutionReport(executionReport);
		
		if(message != null && message.isSetField(ExecTransType.FIELD)) 
		{
			ExecTransType execTransType = new ExecTransType();
			try 
			{
				fValue = ExecutionTransType.getInstanceForFIXValue(message.getField(execTransType).getValue()).name();
			} 
			catch (FieldNotFound e) 
			{
			}
		}		
	}

	@Override
	public int getFieldTag()
	{
		return ExecTransType.FIELD;
	}
}
