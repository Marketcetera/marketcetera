package org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data;

import org.marketcetera.photon.Messages;

import quickfix.field.OrderID;

/**
 * Broker order ID execution report field
 * 
 * @author milan
 *
 */

public class BrokerOrderIDField extends ExecutionReportField 
{
	private int FIELD;
	
	public BrokerOrderIDField(int field)
	{
		FIELD = field;
	}
	
	@Override
	public int getField() 
	{
		return FIELD;
	}
	
	@Override
	public String getFieldName() 
	{
		return Messages.EXECUTION_REPORT_FIELD_BROKER_ORDER_ID.getText();
	}

	@Override
	public String[] getValues() 
	{
		return NULL_VALUE;
	}

	@Override
	public Object getFieldValue() {
		return new OrderID(fSelectedValue);
	}
}
