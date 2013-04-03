package org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data;

import org.marketcetera.photon.Messages;

import quickfix.Message;

/**
 * Strategy tag execution report field
 * 
 * @author milan
 *
 */
public class StrategyTagField extends ExecutionReportNoneFixField 
{	
	
	public static final String STRATEGY_TAG_FIELD_NAME = Messages.EXECUTION_REPORT_FIELD_STRATEGY_TAG.getText();
	
	@Override
	public String getFieldName() 
	{
		return STRATEGY_TAG_FIELD_NAME;
	}

	@Override
	public String[] getValues() 
	{
		return NULL_VALUE;
	}

	@Override
	public void insertField(Message message) {
		//message.setField(new StringField(9999, fValue));
	}
}
