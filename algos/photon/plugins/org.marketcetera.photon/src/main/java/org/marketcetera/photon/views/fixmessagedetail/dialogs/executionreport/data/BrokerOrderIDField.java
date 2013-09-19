package org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data;

import org.marketcetera.photon.Messages;
import org.marketcetera.trade.ExecutionReport;

import quickfix.Message;
import quickfix.field.OrderID;

/**
 * Broker order ID execution report field
 * 
 * @author milan
 *
 */

public class BrokerOrderIDField extends ExecutionReportField 
{
		
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
	public void insertField(Message message) 
	{
	    if(fValue != null && fValue != EMPTY_STRING) {
	        message.setField(new OrderID(fValue));
	    }
	}

	@Override
	public void parseFromReport(ExecutionReport executionReport) 
	{
		fValue = (executionReport.getBrokerOrderID() == null) ? EMPTY_STRING : executionReport.getBrokerOrderID();
	}

	@Override
	public int getFieldTag() 
	{
		return OrderID.FIELD;
	}

}
