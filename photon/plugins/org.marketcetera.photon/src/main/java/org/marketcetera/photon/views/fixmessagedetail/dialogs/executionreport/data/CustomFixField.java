package org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data;

import org.marketcetera.photon.Messages;
import org.marketcetera.trade.ExecutionReport;

import quickfix.Message;
import quickfix.StringField;

/**
 * Custom execution report field. Both field name
 * and value are user-defined.
 * 
 * @author milan
 *
 */
public class CustomFixField extends ExecutionReportField 
{	
	/** Custom field number */
	private final String fFieldName;

	/** Custom field tag */
	private final int fFieldTag;

	public CustomFixField(String fieldName, int fieldTag)
	{
		fFieldName = fieldName;
		fFieldTag = fieldTag;
	}
		
	@Override
	public String getFieldName() 
	{
		if(fFieldName == null)
		{
			return String.valueOf(fFieldTag);
		}
		
		return fFieldName;
	}

	@Override
	public String[] getValues() 
	{
		return NULL_VALUE;
	}

	@Override
	public void insertField(Message message) 
	{
		if(fValue != null)
		{
			message.setField(new StringField(Integer.parseInt(getFieldName()), fValue));
		}
	}

	@Override
	public boolean validateValue() {
		if(!super.validateValue())
		{
			return false;
		}
		try
		{
			Integer.parseInt(fFieldName);
		}
		catch(NumberFormatException nfe)
		{
			return false;
		}
		return true;
	}
	
	@Override
	public String getValidateMessage() {
		if(!super.validateValue())
		{
			return super.getValidateMessage();
		}
		else
		{
			return Messages.ADD_EXECUTION_REPORT_NUMBER_FORMAT_ERROR_CUSTOM.getText();
		}
	}

	@Override
	public void parseFromReport(ExecutionReport executionReport)
	{
	}

	@Override
	public int getFieldTag() 
	{
		return fFieldTag;
	}
}
