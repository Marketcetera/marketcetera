package org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data;

import org.marketcetera.photon.Messages;
import org.marketcetera.trade.ExecutionReport;

import quickfix.Message;
import quickfix.field.OrigClOrdID;

/**
 * Original order ID execution report field
 * 
 * @author milan
 *
 */
public class OriginalOrderIDField extends ExecutionReportField 
{	
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
	public void insertField(Message message) 
	{
		message.setField(new OrigClOrdID(fValue));		
	}

	@Override
	public void parseFromReport(ExecutionReport executionReport) 
	{
		fValue = (executionReport.getOriginalOrderID() == null) ? EMPTY_STRING : executionReport.getOriginalOrderID().getValue();
	}

	@Override
	public int getFieldTag() 
	{
		return OrigClOrdID.FIELD;
	}
}
