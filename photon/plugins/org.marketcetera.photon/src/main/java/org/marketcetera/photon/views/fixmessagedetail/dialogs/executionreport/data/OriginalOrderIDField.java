package org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data;

import org.marketcetera.photon.Messages;
import org.marketcetera.trade.OrderID;

/**
 * Original order ID execution report field
 * 
 * @author milan
 *
 */
public class OriginalOrderIDField extends ExecutionReportField 
{
	private int FIELD;
	
	public OriginalOrderIDField(int field)
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
		return Messages.EXECUTION_REPORT_FIELD_ORIGINAL_ORDER_ID.getText();
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
