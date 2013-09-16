package org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data;

import org.marketcetera.photon.Messages;

import quickfix.Message;
import quickfix.field.Account;

/**
 * Account execution report field
 * 
 * @author milan
 *
 */
public class AccountField extends ExecutionReportField 
{	
	@Override
	public String getFieldName() 
	{
		return Messages.EXECUTION_REPORT_FIELD_ACCOUNT.getText();
	}

	@Override
	public String[] getValues() 
	{
		return NULL_VALUE;
	}

	@Override
	public void insertField(Message message) 
	{
		message.setField(new Account(fValue));
	}

}
