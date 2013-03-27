package org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data;

import java.util.ArrayList;
import java.util.List;

import org.marketcetera.photon.Messages;
import org.marketcetera.trade.OrderType;

import quickfix.field.OrdType;

/**
 * Order type execution report field
 * 
 * @author milan
 *
 */
public class OrderTypeField extends ExecutionReportField 
{
	private int FIELD;
	
	public OrderTypeField(int field)
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
		return Messages.EXECUTION_REPORT_FIELD_ORDER_TYPE.getText();
	}

	@Override
	public String[] getValues() 
	{
		List<String> orderTypeValues = new ArrayList<String>();
		for(OrderType orderType: OrderType.values())
		{
			orderTypeValues.add(orderType.name());
		}
	
		return (String[]) orderTypeValues.toArray(new String[orderTypeValues.size()]);
	}

	@Override
	public Object getFieldValue() 
	{
		OrderType ordType = OrderType.valueOf(fSelectedValue);
		return new OrdType(ordType.getFIXValue());
	}
}
