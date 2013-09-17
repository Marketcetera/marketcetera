package org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Data used for populating the Execution report fields combo box with 
 * predefined field elements
 * 
 * @author milan
 *
 */
public class ExecutionReportFixFields
{
	private List<ExecutionReportField> fExecutionReportFields;
	
	public ExecutionReportFixFields()
	{
		fExecutionReportFields = new ArrayList<ExecutionReportField>();

		fExecutionReportFields.add(new BrokerIDField());
		fExecutionReportFields.add(new AccountField());
		fExecutionReportFields.add(new AvgPxField());
		fExecutionReportFields.add(new OrderIDField());
		fExecutionReportFields.add(new BrokerOrderIDField());
		fExecutionReportFields.add(new CumQtyField());
		fExecutionReportFields.add(new ExecutionIDField());
		fExecutionReportFields.add(new ExecutionTypeField());
		fExecutionReportFields.add(new ExecTransTypeField());
		fExecutionReportFields.add(new LastQtyField());
		fExecutionReportFields.add(new LastPriceField());
		fExecutionReportFields.add(new LeavesQtyField());
		fExecutionReportFields.add(new OriginalOrderIDField());
		fExecutionReportFields.add(new OrderStatusField());
		fExecutionReportFields.add(new OrderTypeField());
		fExecutionReportFields.add(new SendingTimeField());
		fExecutionReportFields.add(new SideField());
		fExecutionReportFields.add(new OrderQtyField());
		fExecutionReportFields.add(new PriceField());
		fExecutionReportFields.add(new SymbolField());
		fExecutionReportFields.add(new SecurityTypeField());
		fExecutionReportFields.add(new MaturityMonthYearField());
		fExecutionReportFields.add(new PutOrCallField());
		fExecutionReportFields.add(new StrikePriceField());
		fExecutionReportFields.add(new TimeInForceField());
		fExecutionReportFields.add(new TextField());
		fExecutionReportFields.add(new TransactTimeField());
	}
	
	public void addExecutionReportField(ExecutionReportField field)
	{
		if(fExecutionReportFields == null)
			fExecutionReportFields = new ArrayList<ExecutionReportField>();
		
		if(fExecutionReportFields.contains(field))
			return;
		
		fExecutionReportFields.add(field);
	}
	
	public void removeExecutionReportField(ExecutionReportField field)
	{
		if(fExecutionReportFields == null)
			return;
		
		if(fExecutionReportFields.contains(field))
			fExecutionReportFields.remove(field);
	}
		
	public ExecutionReportField[] getExecutionReportFields()
	{
		if(fExecutionReportFields == null)
			return new ExecutionReportField[] {};
		
		return (ExecutionReportField[]) fExecutionReportFields.toArray(new ExecutionReportField[fExecutionReportFields.size()]);
	}
}
