package org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data;

import java.util.ArrayList;
import java.util.List;

import org.marketcetera.photon.Messages;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.OptionType;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.PutOrCall;

public class PutOrCallField extends ExecutionReportField {

	@Override
	public String getFieldName() 
	{
		return Messages.EXECUTION_REPORT_FIELD_OPTION_TYPE.getText();
	}

	@Override
	public String[] getValues() 
	{
		List<String> optionTypeValues = new ArrayList<String>();
		for(OptionType optionType: OptionType.values())
		{
			optionTypeValues.add(optionType.name());
		}
	
		return (String[]) optionTypeValues.toArray(new String[optionTypeValues.size()]);
	}

	@Override
	public void insertField(Message message) 
	{
		if(fValue != null)
		{
			message.setField(new PutOrCall(OptionType.valueOf(fValue).getFIXValue()));
		}
	}

	@Override
	public void parseFromReport(ExecutionReport executionReport) 
	{
		Message message = getMessageFromExecutionReport(executionReport);
		
		if(message != null && message.isSetField(PutOrCall.FIELD)) 
		{
			PutOrCall putOrCall = new PutOrCall();
			try 
			{
				fValue = OptionType.getInstanceForFIXValue(message.getField(putOrCall).getValue()).name();
			} 
			catch (FieldNotFound e) 
			{
			}
		}
	}

	@Override
	public int getFieldTag() 
	{
		return PutOrCall.FIELD;
	}
}
