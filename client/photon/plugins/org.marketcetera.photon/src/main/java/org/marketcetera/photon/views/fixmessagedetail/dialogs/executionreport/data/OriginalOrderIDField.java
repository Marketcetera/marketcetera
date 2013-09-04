package org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data;

import org.marketcetera.photon.Messages;

import quickfix.Message;
import quickfix.field.ClOrdID;

/**
 * Original order ID execution report field
 * 
 * @author milan
 *
 */
public class OriginalOrderIDField extends ExecutionReportField 
{	
	@Override
	public String getFieldName() 
	{
		return Messages.EXECUTION_REPORT_FIELD_ORIGINAL_ORDER_ID.getText();
	}

	@Override
	public String[] getValues() 
	{
		return NULL_VALUE;
	}

	@Override
	public void insertField(Message message) {
		message.setField(new ClOrdID());		
	}
}
