package org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data;

import java.math.BigDecimal;

import quickfix.Message;
import quickfix.field.StrikePrice;

public class StrikePriceField extends ExecutionReportField {

	@Override
	public String getFieldName() {
		return "Strike price";
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

}
