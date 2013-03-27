package org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data;

import org.marketcetera.photon.Messages;

/**
 * Account execution report field
 * 
 * @author milan
 *
 */
public class ExecutionIDField extends ExecutionReportField 
{
	private int FIELD;
	
	public ExecutionIDField(int field)
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
		return Messages.EXECUTION_REPORT_FIELD_EXECUTION_ID.getText();
	}

	@Override
	public String[] getValues() 
	{
		return NULL_VALUE;
	}

	@Override
	public Object getFieldValue() 
	{
		return fSelectedValue;
	}
}
