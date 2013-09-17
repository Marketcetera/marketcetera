package org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data;

import java.util.ArrayList;
import java.util.List;

import org.marketcetera.photon.Messages;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.OrderType;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.OrdType;

/**
 * Order type execution report field
 * 
 * @author milan
 *
 */
public class OrderTypeField extends ExecutionReportField 
{
	
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
	public void insertField(Message message) 
	{
		if(fValue != null)
		{
			message.setField(new OrdType(OrderType.valueOf(fValue).getFIXValue()));
		}
	}

	@Override
	public void parseFromReport(ExecutionReport executionReport) 
	{
		Message message = getMessageFromExecutionReport(executionReport);
		
		if(message != null && message.isSetField(OrdType.FIELD)) 
		{
			OrdType orderType = new OrdType();
			try 
			{
				fValue = OrderType.getInstanceForFIXValue(message.getField(orderType).getValue()).name();
			} 
			catch (FieldNotFound e) 
			{
			}
		}
	}

	@Override
	public int getFieldTag() 
	{
		return OrdType.FIELD;
	}
}
