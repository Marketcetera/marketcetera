package org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data;

import org.marketcetera.photon.Messages;
import org.marketcetera.trade.ExecutionReport;

import quickfix.Message;
import quickfix.field.ExecID;

/**
 * Account execution report field
 * 
 * @author milan
 *
 */
public class ExecutionIDField extends ExecutionReportField 
{
	
	@Override
	public String getFieldName() 
	{
		return Messages.EXECUTION_REPORT_FIELD_EXECUTION_ID.getText();
	}

	@Override
	public String[] getValues() 
	{
		return NULL_VALUE;
	}

	@Override
	public void insertField(Message message) 
	{
		message.setField(new ExecID(fValue));
	}

	@Override
	public void parseFromReport(ExecutionReport executionReport) 
	{
		fValue = (executionReport.getExecutionID() == null) ? EMPTY_STRING : executionReport.getExecutionID();
	}

	@Override
	public int getFieldTag() 
	{
		return ExecID.FIELD;
	}
}
