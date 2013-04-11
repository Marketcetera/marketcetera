package org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data;

import java.util.ArrayList;
import java.util.List;

import org.marketcetera.photon.Messages;
import org.marketcetera.trade.OptionType;

import quickfix.Message;
import quickfix.field.PutOrCall;

public class PutOrCallField extends ExecutionReportField {

	@Override
	public String getFieldName() {
		return Messages.EXECUTION_REPORT_FIELD_OPTION_TYPE.getText();
	}

	@Override
	public String[] getValues() {
		
		List<String> optionTypeValues = new ArrayList<String>();
		for(OptionType optionType: OptionType.values())
		{
			optionTypeValues.add(optionType.name());
		}
	
		return (String[]) optionTypeValues.toArray(new String[optionTypeValues.size()]);
	}

	@Override
	public void insertField(Message message) {
		message.setField(new PutOrCall(OptionType.valueOf(fValue).getFIXValue()));
		
	}

}
