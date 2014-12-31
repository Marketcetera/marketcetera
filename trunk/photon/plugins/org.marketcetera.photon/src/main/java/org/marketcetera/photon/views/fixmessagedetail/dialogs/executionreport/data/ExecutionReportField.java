package org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.marketcetera.event.HasFIXMessage;
import org.marketcetera.photon.Messages;
import org.marketcetera.trade.ExecutionReport;

import quickfix.Message;


/**
 * Execution report field class representing a single 
 * row in the execution report table
 * 
 * @author milan
 *
 */
public abstract class ExecutionReportField
{
	/*YYYYMMDD-HH:MM:SS*/
	protected static final DateTimeFormatter utc1 = new DateTimeFormatterBuilder().
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
	protected static final DateTimeFormatter utc2 = new DateTimeFormatterBuilder().
			append(utc1).
			appendLiteral('.').
			appendMillisOfSecond(3).
			toFormatter();
	//allowed formats
	protected String allowedFormats = "YYYYMMDD-HH:MM:SS, YYYYMMDD-HH:MM:SS.sss";
	
	protected String fValue;
	
	public static final String[] NULL_VALUE = null;
	public static final String EMPTY_STRING = "";
	
	public abstract String getFieldName();
	
	public abstract String[] getValues();
	
	public abstract void insertField(Message message);
	
	public abstract void parseFromReport(ExecutionReport executionReport);
	
	public abstract int getFieldTag();
	
	public boolean isFixField(){
		return true;
	}
	
	public boolean validateValue() 
	{
		return fValue != null && !fValue.equals(EMPTY_STRING);
	}
	
	public void setSelectedValue(String selectedValue)
	{
		fValue = selectedValue;
	}
	
	public void setSelectedDateValue(Date selectedValue)
	{
		DateTime dt = new DateTime(selectedValue);
		fValue = utc2.print(dt); 
	}
	
	public String getSelectedValue()
	{
		return fValue;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(o instanceof ExecutionReportField)
		{
			ExecutionReportField reportField = (ExecutionReportField) o;
			
			return getFieldTag() == reportField.getFieldTag();
		}
		
		return false;
	}

	public String getValidateMessage() 
	{
		return Messages.ADD_EXECUTION_REPORT_STRING_FORMAT_ERROR.getText();
	}
	
	protected Message getMessageFromExecutionReport(ExecutionReport executionReport)
	{
		HasFIXMessage hasFixMessage = (HasFIXMessage) executionReport;
		return hasFixMessage.getMessage();
	}
}
