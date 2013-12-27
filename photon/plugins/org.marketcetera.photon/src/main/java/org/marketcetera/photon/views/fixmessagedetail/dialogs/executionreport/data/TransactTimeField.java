package org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data;

import java.util.Date;

import org.joda.time.DateTime;
import org.marketcetera.photon.Messages;
import org.marketcetera.trade.ExecutionReport;

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
	public void insertField(Message message) 
	{
        if(fValue != null && fValue != EMPTY_STRING) {
            try 
            {
                message.setField(new TransactTime(new Date(utc1.parseDateTime(fValue).getMillis())));
            } 
            catch (IllegalArgumentException e) 
            {
                message.setField(new TransactTime(new Date(utc2.parseDateTime(fValue).getMillis())));
            }
        }
	}

	@Override
	public String getValidateMessage() 
	{
		return Messages.ADD_EXECUTION_REPORT_DATE_FORMAT_ERROR.getText(allowedFormats);
	}

	@Override
	public void parseFromReport(ExecutionReport executionReport) 
	{
		if(executionReport.getTransactTime() != null) 
		{
			DateTime dt = new DateTime(executionReport.getTransactTime());
			fValue = utc2.print(dt);
		} 
		else
		{
			fValue = EMPTY_STRING;
		}
	}

	@Override
	public int getFieldTag() 
	{
		return TransactTime.FIELD;
	}
}
