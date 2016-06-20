package org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data;
import org.marketcetera.photon.Messages;
import org.marketcetera.trade.ExecutionReport;

import quickfix.Message;
import quickfix.field.Text;

/**
 * Execution report text
 * 
 * @author Milos Djuric
 *
 */
public class TextField extends ExecutionReportField {


	@Override
	public String getFieldName() 
	{
		return Messages.EXECUTION_REPORT_FIELD_TEXT.getText();
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
            message.setField(new Text(fValue));
        }
	}

	@Override
	public void parseFromReport(ExecutionReport executionReport) 
	{
		fValue = (executionReport.getText() == null) ? EMPTY_STRING : executionReport.getText();
	}

	@Override
	public int getFieldTag() 
	{
		return Text.FIELD;
	}
}
