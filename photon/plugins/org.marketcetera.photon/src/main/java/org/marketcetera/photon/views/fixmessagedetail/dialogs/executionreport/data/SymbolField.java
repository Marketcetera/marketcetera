package org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data;

import org.marketcetera.photon.Messages;

import quickfix.Message;
import quickfix.field.Symbol;

public class SymbolField extends ExecutionReportField {

	@Override
	public String getFieldName() 
	{
		return Messages.EXECUTION_REPORT_FIELD_SYMBOL.getText();
	}

	@Override
	public String[] getValues() 
	{
		return NULL_VALUE;
	}

	@Override
	public void insertField(Message message) 
	{
		message.setField(new Symbol(fValue));	
	}

}
