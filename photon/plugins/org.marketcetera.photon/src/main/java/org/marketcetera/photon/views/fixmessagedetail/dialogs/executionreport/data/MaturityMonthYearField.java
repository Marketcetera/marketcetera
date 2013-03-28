package org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import quickfix.Message;
import quickfix.field.MaturityMonthYear;

public class MaturityMonthYearField extends ExecutionReportField {
	
	private static final String timeFormat = "yyyyMM"; //$NON-NLS-1$

	@Override
	public String getFieldName() 
	{
		return "Expiry";
	}

	@Override
	public String[] getValues() {
		return NULL_VALUE;
	}
	
	@Override
	public boolean validateValue() 
	{
		if(!super.validateValue())
		{
			return false;
		}
		try {
			SimpleDateFormat transactTimeFormat = new SimpleDateFormat(timeFormat);
			transactTimeFormat.parse(fValue);			
			return true;
		} 
		catch (ParseException e) 
		{}
		return false;	
	}

	@Override
	public void insertField(Message message) {
		message.setField(new MaturityMonthYear(fValue));
		
	}


}
