package org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data;

import java.util.Date;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.marketcetera.photon.Messages;

import quickfix.Message;
import quickfix.field.TransactTime;

/**
 * Transaction time execution report field
 * 
 * @author milan
 *
 */
public class TransactTimeField extends ExecutionReportField 
{
	/*YYYYMMDD-HH:MM:SS*/
	private static final DateTimeFormatter utc1 = new DateTimeFormatterBuilder().
			appendYear(4, 4).
			appendMonthOfYear(2).
			appendDayOfMonth(2).
			appendLiteral('-').
			appendHourOfDay(2).
			appendLiteral(':').
			appendMinuteOfHour(2).
			appendLiteral(':').
			appendSecondOfMinute(2).
			toFormatter();
	/*YYYYMMDD-HH:MM:SS.sss*/
	private static final DateTimeFormatter utc2 = new DateTimeFormatterBuilder().
			append(utc1).
			appendLiteral('.').
			appendMillisOfSecond(3).
			toFormatter();
	//allowed formats
	private String allowedFormats = "YYYYMMDD-HH:MM:SS, YYYYMMDD-HH:MM:SS.sss";
	
	@Override
	public String getFieldName() 
	{
		return Messages.EXECUTION_REPORT_FIELD_TRANSACT_TIME.getText();
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
		try 
		{
			utc1.parseDateTime(fValue);
			return true;
		} 
		catch (IllegalArgumentException iae1) 
		{
			try 
			{
				utc2.parseDateTime(fValue);
				return true;
			}catch (IllegalArgumentException iae2)
			{}
		}
		return false;	
	}

	@Override
	public void insertField(Message message) {
		try 
		{
			message.setField(new TransactTime(new Date(utc1.parseDateTime(fValue).getMillis())));
		} 
		catch (IllegalArgumentException e) 
		{
			message.setField(new TransactTime(new Date(utc2.parseDateTime(fValue).getMillis())));
		}
	}

	@Override
	public String getValidateMessage() {
		return Messages.ADD_EXECUTION_REPORT_DATE_FORMAT_ERROR.getText(allowedFormats);
	}
}
