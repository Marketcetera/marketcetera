package org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data;

import java.util.ArrayList;
import java.util.List;

import org.marketcetera.photon.Messages;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.OrderStatus;

import quickfix.FieldNotFound;
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
	public void insertField(Message message) 
	{
		if(fValue != null)
		{
			message.setField(new OrdStatus(OrderStatus.valueOf(fValue).getFIXValue()));
		}
	}

	@Override
	public void parseFromReport(ExecutionReport executionReport) 
	{
		Message message = getMessageFromExecutionReport(executionReport);
		
		if(message != null && message.isSetField(OrdStatus.FIELD)) 
		{
			OrdStatus orderStatus = new OrdStatus();
			try 
			{
				fValue = OrderStatus.getInstanceForFIXValue(message.getField(orderStatus).getValue()).name();
			} 
			catch (FieldNotFound e) 
			{
			}
		}	
	}

	@Override
	public int getFieldTag() 
	{
		return OrdStatus.FIELD;
	}
}
