package org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data;

import org.marketcetera.photon.Messages;

import quickfix.Message;

/**
 * Broker order ID execution report field
 * 
 * @author milan
 *
 */

public class BrokerOrderIDField extends ExecutionReportNoneFixField 
{
	
	public static final String BROKER_ORDER_ID_FIELD_NAME = Messages.EXECUTION_REPORT_FIELD_BROKER_ORDER_ID.getText();
	
	@Override
	public String getFieldName() 
	{
		return BROKER_ORDER_ID_FIELD_NAME;
	}

	@Override
	public String[] getValues() 
	{
		return NULL_VALUE;
	}
	
	@Override
	public void insertField(Message message) {
	}

}
