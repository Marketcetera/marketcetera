package org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data;

import org.marketcetera.photon.Messages;
import org.marketcetera.photon.PhotonPlugin;
import org.marketcetera.quickfix.FIXDataDictionary;
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
	/** Custom field tag */
	private final int fFieldTag;

	public CustomFixField(int fieldTag)
	{
		fFieldTag = fieldTag;
	}
		
	@Override
	public String getFieldName() 
	{
		FIXDataDictionary fixDictionary = PhotonPlugin.getDefault()
                .getFIXDataDictionary();
		String fieldName = fixDictionary.getHumanFieldName(fFieldTag);
		if(fieldName != null)
			return fieldName;
		return String.valueOf(fFieldTag);	
	}

	@Override
	public String[] getValues() 
	{
		return NULL_VALUE;
	}

	@Override
	public void insertField(Message message) 
	{
        if(fValue != null && fValue != EMPTY_STRING) {
			message.setField(new StringField(getFieldTag(), fValue));
		}
	}

	@Override
	public boolean validateValue() {
		
		if(!super.validateValue())
		{
			return false;
		}
		return getFieldTag() > 0;
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
