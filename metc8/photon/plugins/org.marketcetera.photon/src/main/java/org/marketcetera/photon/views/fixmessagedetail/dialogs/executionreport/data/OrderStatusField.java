package org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data;

import java.util.ArrayList;
import java.util.List;

import org.marketcetera.photon.Messages;
import org.marketcetera.trade.OrderStatus;

import quickfix.Message;
import quickfix.field.OrdStatus;

/**
 * Order status execution report field
 * 
 * @author milan
 *
 */
public class OrderStatusField extends ExecutionReportField 
{
	@Override
	public String getFieldName() 
	{
		return Messages.EXECUTION_REPORT_FIELD_ORDER_STATUS.getText();
	}

	@Override
	public String[] getValues() 
	{
		List<String> orderStatusValues = new ArrayList<String>();
		for(OrderStatus orderStatus: OrderStatus.values())
		{
			orderStatusValues.add(orderStatus.name());
		}
		
		return (String[]) orderStatusValues.toArray(new String[orderStatusValues.size()]);

	}

	@Override
	public void insertField(Message message) {
		message.setField(new OrdStatus(OrderStatus.valueOf(fValue).getFIXValue()));
	}
}
