package org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data;

import java.math.BigDecimal;

import org.marketcetera.photon.Messages;

import quickfix.Message;
import quickfix.field.StrikePrice;

public class StrikePriceField extends ExecutionReportField {

	@Override
	public String getFieldName() {
		return Messages.EXECUTION_REPORT_FIELD_STRIKE_PRICE.getText();
	}

	@Override
	public String[] getValues() {
		return NULL_VALUE;
	}

	@Override
	public void insertField(Message message) 
	{
		message.setField(new StrikePrice(new BigDecimal(fValue)));	
	}

	@Override
	public boolean validateValue() {
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
	public String getValidateMessage() {
		return Messages.ADD_EXECUTION_REPORT_NUMBER_FORMAT_ERROR.getText();
	}
	
	
}
