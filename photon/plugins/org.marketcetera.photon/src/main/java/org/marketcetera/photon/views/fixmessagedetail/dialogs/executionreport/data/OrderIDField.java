package org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data;

import org.marketcetera.photon.Messages;
import org.marketcetera.trade.ExecutionReport;

import quickfix.Message;
import quickfix.field.ClOrdID;

/**
 * Order ID execution report field
 * 
 * @author milan
 *
 */
public class OrderIDField extends ExecutionReportField 
{	
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
	public void insertField(Message message) 
	{
	    if(fValue != null && fValue != EMPTY_STRING) {
	        message.setField(new ClOrdID(fValue));
	    }
	}

	@Override
	public void parseFromReport(ExecutionReport executionReport) 
	{
		fValue = (executionReport.getOrderID() == null) ? EMPTY_STRING : executionReport.getOrderID().getValue();
	}

	@Override
	public int getFieldTag() 
	{
		return ClOrdID.FIELD;
	}
}
