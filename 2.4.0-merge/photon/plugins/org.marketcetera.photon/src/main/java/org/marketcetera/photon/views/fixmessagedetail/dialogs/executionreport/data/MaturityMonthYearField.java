package org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data;

import org.marketcetera.photon.Messages;
import org.marketcetera.trade.ExecutionReport;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.MaturityMonthYear;

public class MaturityMonthYearField extends ExecutionReportField {

	@Override
	public String getFieldName() 
	{
		return Messages.EXECUTION_REPORT_FIELD_EXPIRY.getText();
	}

	@Override
	public String[] getValues() 
	{
		return NULL_VALUE;
	}

	@Override
	public void insertField(Message message) 
	{
        if(fValue != null && fValue != EMPTY_STRING) {
			message.setField(new MaturityMonthYear(fValue));
		}
	}

	@Override
	public void parseFromReport(ExecutionReport executionReport) 
	{
		Message message = getMessageFromExecutionReport(executionReport);
		
		if(message != null && message.isSetField(MaturityMonthYear.FIELD)) 
		{
			MaturityMonthYear maturityMonthYear = new MaturityMonthYear();
			try 
			{
				fValue = message.getField(maturityMonthYear).getValue();
			} 
			catch (FieldNotFound e) 
			{
			}
		}		
	}

	@Override
	public int getFieldTag() 
	{
		return MaturityMonthYear.FIELD;
	}
}
