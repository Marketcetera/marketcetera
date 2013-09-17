package org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data;

import org.marketcetera.photon.Messages;
import org.marketcetera.trade.ExecutionReport;

import quickfix.FieldNotFound;
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
		if(fValue != null)
		{
			message.setField(new Symbol(fValue));
		}
	}

	@Override
	public void parseFromReport(ExecutionReport executionReport) 
	{
		Message message = getMessageFromExecutionReport(executionReport);
		
		if(message != null && message.isSetField(Symbol.FIELD)) 
		{
			Symbol symbol = new Symbol();
			try 
			{
				fValue = message.getField(symbol).getValue();
			} 
			catch (FieldNotFound e) 
			{
			}
		}			
	}

	@Override
	public int getFieldTag() 
	{
		return Symbol.FIELD;
	}

}
