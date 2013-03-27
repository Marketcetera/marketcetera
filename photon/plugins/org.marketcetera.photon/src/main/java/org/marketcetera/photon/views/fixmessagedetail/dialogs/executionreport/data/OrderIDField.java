package org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data;

import org.marketcetera.photon.Messages;
import org.marketcetera.trade.OrderID;

/**
 * Order ID execution report field
 * 
 * @author milan
 *
 */
public class OrderIDField extends ExecutionReportField 
{
	private int FIELD;
	
	public OrderIDField(int field)
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
		return Messages.EXECUTION_REPORT_FIELD_ORDER_ID.getText();
	}

	@Override
	public String[] getValues() 
	{
		return NULL_VALUE;
	}

	@Override
	public Object getFieldValue() 
	{
		return new OrderID(fSelectedValue);
	}
}
