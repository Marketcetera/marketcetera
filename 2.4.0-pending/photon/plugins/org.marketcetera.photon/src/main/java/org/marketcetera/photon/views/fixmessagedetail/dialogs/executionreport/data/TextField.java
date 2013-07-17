package org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data;
import org.marketcetera.photon.Messages;

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
		message.setField(new Text("fValue"));
		
	}

}
