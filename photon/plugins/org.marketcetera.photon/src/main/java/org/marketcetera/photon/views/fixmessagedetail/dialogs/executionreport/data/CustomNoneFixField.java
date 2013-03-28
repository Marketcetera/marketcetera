package org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data;

import quickfix.Message;
import quickfix.StringField;

public class CustomNoneFixField extends ExecutionReportNoneFixField {

	/** Custom field number */
	private final String fFieldName;
	
	public CustomNoneFixField(String fieldName)
	{
		fFieldName = fieldName;
	}
		
	@Override
	public String getFieldName() 
	{
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
		message.setField(new StringField(Integer.parseInt(fFieldName),fValue));
		
	}

	@Override
	public boolean validateValue() {
		if(!super.validateValue())
			return false;
		return fFieldName != null && !fFieldName.equals(EMPTY_STRING);
	}

}
