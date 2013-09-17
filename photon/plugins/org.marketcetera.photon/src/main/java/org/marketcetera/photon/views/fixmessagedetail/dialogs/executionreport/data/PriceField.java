package org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data;

import java.math.BigDecimal;

import org.marketcetera.photon.Messages;
import org.marketcetera.trade.ExecutionReport;

import quickfix.Message;
import quickfix.field.Price;

/**
 * Price execution report field
 * 
 * @author milan
 *
 */
public class PriceField extends ExecutionReportField 
{
	@Override
	public String getFieldName() 
	{
		return Messages.EXECUTION_REPORT_FIELD_PRICE.getText();
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
			message.setField(new Price(new BigDecimal(fValue)));
		}
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
			new BigDecimal(fValue);
		}
		catch(NumberFormatException nfe)
		{
			return false;
		}
		return true;
	}
	
	@Override
	public String getValidateMessage() 
	{
		return Messages.ADD_EXECUTION_REPORT_NUMBER_FORMAT_ERROR.getText();
	}

	@Override
	public void parseFromReport(ExecutionReport executionReport) 
	{
		fValue = (executionReport.getPrice() == null) ? EMPTY_STRING : executionReport.getPrice().toPlainString();
	}

	@Override
	public int getFieldTag() 
	{
		return Price.FIELD;
	}
}
