package org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data;

import org.marketcetera.photon.Messages;

import quickfix.Message;
import quickfix.field.MaturityMonthYear;

public class MaturityMonthYearField extends ExecutionReportField {

	@Override
	public String getFieldName() 
	{
		return Messages.EXECUTION_REPORT_FIELD_EXPIRY.getText();
	}

	@Override
	public String[] getValues() {
		return NULL_VALUE;
	}

	@Override
	public void insertField(Message message) {
		message.setField(new MaturityMonthYear(fValue));
		
	}


}
