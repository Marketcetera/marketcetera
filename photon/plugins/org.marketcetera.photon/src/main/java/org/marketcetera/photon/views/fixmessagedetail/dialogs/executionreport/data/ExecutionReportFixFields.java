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
	
	public static final boolean WITH_OPTIONS_FIELDS = true;
	
	public ExecutionReportFixFields()
	{
		fExecutionReportFields = new ArrayList<ExecutionReportField>();

		fExecutionReportFields.add(new BrokerIDField());
		fExecutionReportFields.add(new AccountField());
		fExecutionReportFields.add(new AvgPxField());
		fExecutionReportFields.add(new OrderIDField());
		fExecutionReportFields.add(new CumQtyField());
		fExecutionReportFields.add(new ExecutionIDField());
		fExecutionReportFields.add(new ExecutionTypeField());
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
		if(WITH_OPTIONS_FIELDS)
		{
			fExecutionReportFields.add(new SecurityTypeField());
		}
		fExecutionReportFields.add(new MaturityMonthYearField());
		if(WITH_OPTIONS_FIELDS)
		{
			fExecutionReportFields.add(new PutOrCallField());
			fExecutionReportFields.add(new StrikePriceField());
		}
		fExecutionReportFields.add(new TimeInForceField());
		fExecutionReportFields.add(new TextField());
		fExecutionReportFields.add(new TransactTimeField());
		fExecutionReportFields.add(new BrokerOrderIDField());
		fExecutionReportFields.add(new StrategyTagField());
	}
	
	/*|6|AvgPx|float| 
	|na|BrokerID|yes|valid broker ID| 
	|37|OrderID|yes|string| 
	|14|CumQty|no|float| 
	|17|ExecID|yes|string| 
	|150|ExecType|yes|org.marketcetera.trade.ExecutionType.getFIXValue()| 
	|32|LastShares|no|float| 
	|31|LastPx|no|float| 
	|151|LeavesQty|yes|float| 
	|11|ClOrdID|yes|string| 
	|39|OrdStatus|yes|org.marketcetera.trade.OrderStatus.getFIXValue()| 
	|40|OrdType|no|org.marketcetera.trade.OrderType.getFIXValue()| 
	|52|SendingTime*|yes|UTCTimestamp| 
	|54|Side|yes|org.marketcetera.trade.Side.getFIXValue()| 
	|55|Symbol|yes|string| 
	|58|Text|no|string| 
	|167|SecurityType|yes|org.marketcetera.trade.SecurityType.getFIXValue()| 
	|200|MaturityMonthYear|no|month-year| 
	|201|PutOrCall|no|org.marketcetera.trade.OptionType.getFIXValue()| 
	|202|StrikePrice|no|float| 
	|59|TimeInForce|no|org.marketcetera.trade.TimeInForce.getFIXValue()| 
	|60|TransactTime|no|UTCTimestamp| */
	
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
