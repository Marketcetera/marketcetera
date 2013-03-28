package org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.marketcetera.photon.Messages;

import quickfix.Message;
import quickfix.field.SendingTime;

/**
 * Sending time execution report field
 * 
 * @author milan
 *
 */
public class SendingTimeField extends ExecutionReportField 
{
	private static final String timeFormat = "dd-MMM-yyyy HH:mm:ss:SSS"; //$NON-NLS-1$
	
	@Override
	public String getFieldName() 
	{
		return Messages.EXECUTION_REPORT_FIELD_SENDING_TIME.getText();
	}

	@Override
	public String[] getValues() 
	{
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
		try 
		{
			Calendar transactTime = Calendar.getInstance();
			SimpleDateFormat transactTimeFormat = new SimpleDateFormat(timeFormat);
			transactTime.setTime(transactTimeFormat.parse(fValue));
			message.getHeader().setField(new SendingTime(transactTime.getTime()));
		} catch (ParseException e) {
		}
	}
}
