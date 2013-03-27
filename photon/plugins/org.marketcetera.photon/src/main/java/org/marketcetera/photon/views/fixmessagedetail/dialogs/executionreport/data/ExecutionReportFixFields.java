package org.marketcetera.photon.views.fixmessagedetail.dialogs.executionreport.data;

import java.util.ArrayList;
import java.util.List;

import quickfix.field.Account;
import quickfix.field.AvgPx;
import quickfix.field.BrokerOfCredit;
import quickfix.field.CumQty;
import quickfix.field.ExecID;
import quickfix.field.ExecType;
import quickfix.field.InstrumentPartyID;
import quickfix.field.LastPx;
import quickfix.field.LastQty;
import quickfix.field.LastShares;
import quickfix.field.LeavesQty;
import quickfix.field.OrdStatus;
import quickfix.field.OrdType;
import quickfix.field.OrderID;
import quickfix.field.OrderQty;
import quickfix.field.Price;
import quickfix.field.SendingTime;
import quickfix.field.Side;
import quickfix.field.Text;
import quickfix.field.TimeInForce;
import quickfix.field.TransactTime;

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

		// Check FIELD
		fExecutionReportFields.add(new StrategyTagField(1001));
		fExecutionReportFields.add(new OrderIDField(OrderID.FIELD));
		
		// Check FIELD
		fExecutionReportFields.add(new OriginalOrderIDField(OrderID.FIELD));
		fExecutionReportFields.add(new OrderStatusField(OrdStatus.FIELD));
		
		// Check FIELD
		fExecutionReportFields.add(new BrokerIDField(BrokerOfCredit.FIELD));
		fExecutionReportFields.add(new SendingTimeField(SendingTime.FIELD));
		
		// Check FIELD
		fExecutionReportFields.add(new BrokerOrderIDField(1002));
		fExecutionReportFields.add(new TransactTimeField(TransactTime.FIELD));
		fExecutionReportFields.add(new SideField(Side.FIELD));
		fExecutionReportFields.add(new ExecutionIDField(ExecID.FIELD));
		fExecutionReportFields.add(new ExecutionTypeField(ExecType.FIELD));
		fExecutionReportFields.add(new LeavesQtyField(LeavesQty.FIELD));
		fExecutionReportFields.add(new CumQtyField(CumQty.FIELD));
		fExecutionReportFields.add(new AvgPxField(AvgPx.FIELD));
		fExecutionReportFields.add(new LastQtyField(LastQty.FIELD));
		fExecutionReportFields.add(new LastPriceField(LastPx.FIELD));
		fExecutionReportFields.add(new OrderQtyField(OrderQty.FIELD));
		fExecutionReportFields.add(new PriceField(Price.FIELD));
		
		// Check FIELD
		fExecutionReportFields.add(new InstrumentField(InstrumentPartyID.FIELD));
		fExecutionReportFields.add(new AccountField(Account.FIELD));
		fExecutionReportFields.add(new OrderTypeField(OrdType.FIELD));
		fExecutionReportFields.add(new TimeInForceField(TimeInForce.FIELD));
		fExecutionReportFields.add(new TextField(Text.FIELD));
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
