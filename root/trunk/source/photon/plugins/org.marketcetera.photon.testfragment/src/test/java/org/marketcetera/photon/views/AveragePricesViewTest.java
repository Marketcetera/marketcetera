package org.marketcetera.photon.views;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.marketcetera.messagehistory.FIXMessageHistory;
import org.marketcetera.messagehistory.IncomingMessageHolder;
import org.marketcetera.photon.messagehistory.FIXRegexMatcher;
import org.marketcetera.photon.messagehistory.FIXStringMatcher;
import org.marketcetera.photon.ui.IndexedTableViewer;
import org.marketcetera.quickfix.FIXVersion;

import quickfix.Message;
import quickfix.field.AvgPx;
import quickfix.field.ClOrdID;
import quickfix.field.CumQty;
import quickfix.field.ExecID;
import quickfix.field.ExecTransType;
import quickfix.field.ExecType;
import quickfix.field.HandlInst;
import quickfix.field.LastPx;
import quickfix.field.LastShares;
import quickfix.field.LeavesQty;
import quickfix.field.OrdStatus;
import quickfix.field.OrdType;
import quickfix.field.OrderID;
import quickfix.field.OrderQty;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.TransactTime;
import quickfix.field.Urgency;
import quickfix.fix42.ExecutionReport;
import quickfix.fix42.NewOrderSingle;

public class AveragePricesViewTest
    extends ViewTestBase
{
    public AveragePricesViewTest(String name)
    {
        super(name);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.photon.views.ViewTestBase#getFilterTestConditions()
     */
    @Override
    protected List<FilterTestCondition> getFilterTestConditions()
    {
        List<FilterTestCondition> conditions = new ArrayList<FilterTestCondition>();
        // string match on existing field
        conditions.add(new FilterTestCondition(new FIXStringMatcher(Side.FIELD,
                                                                    "B"),
                                               new int[] { 0, 1 }));
        // string no match on existing field
        conditions.add(new FilterTestCondition(new FIXStringMatcher(Symbol.FIELD,
                                                                    "symbol-not-present"),
                                                                    new int[] { }));
        // string match on non-existent field
        conditions.add(new FilterTestCondition(new FIXStringMatcher(Urgency.FIELD,
                                                                    "0"),
                                               new int[] { }));
        // regex match on existing field
        conditions.add(new FilterTestCondition(new FIXRegexMatcher(Symbol.FIELD,
                                                                   "[a-z]*bol[1|2]"),
                                               new int[] { 0, 1 } ));
        // regex match on non-existent field
        conditions.add(new FilterTestCondition(new FIXRegexMatcher(Symbol.FIELD,
                                                                   "[a-z]*bxol[1|2]"),
                                               new int[] { } ));
        // regex no match in non-existent field
        conditions.add(new FilterTestCondition(new FIXRegexMatcher(Urgency.FIELD,
                                                                   ".*"),
                                               new int[] { } ));
        return conditions;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.photon.views.ViewTestBase#getFilterTestMessages()
     */
    @Override
    protected List<Message> getFilterTestMessages()
    {
        List<Message> messages = new ArrayList<Message>();
        NewOrderSingle order1 = new NewOrderSingle(new ClOrdID("clordid1"),
                                                   new HandlInst(HandlInst.AUTOMATED_EXECUTION_ORDER_PRIVATE),
                                                   new Symbol("symbol1"),
                                                   new Side(Side.BUY),
                                                   new TransactTime(new Date()),
                                                   new OrdType(OrdType.MARKET));
        order1.set(new OrderQty(100));
        messages.add(order1);
        NewOrderSingle order2 = new NewOrderSingle(new ClOrdID("clordid2"),
                                                   new HandlInst(HandlInst.AUTOMATED_EXECUTION_ORDER_PRIVATE),
                                                   new Symbol("symbol2"),
                                                   new Side(Side.BUY),
                                                   new TransactTime(new Date()),
                                                   new OrdType(OrdType.LIMIT)); 
        order2.set(new OrderQty(100));
        messages.add(order2);
        NewOrderSingle order3 = new NewOrderSingle(new ClOrdID("clordid3"),
                                                   new HandlInst(HandlInst.AUTOMATED_EXECUTION_ORDER_PRIVATE),
                                                   new Symbol("symbol3"),
                                                   new Side(Side.SELL),
                                                   new TransactTime(new Date()),
                                                   new OrdType(OrdType.LIMIT_ON_CLOSE)); 
        order3.set(new OrderQty(100));
        messages.add(order3);
        return messages;
    }
    /**
     * Tests the filtering ability of the view.
     * 
     * @throws Exception
     */
    public void testFilter()
        throws Exception
    {
        doFilterTest();
    }
    
	public void testShowMessage() throws Exception {
		FIXMessageHistory hist = new FIXMessageHistory(FIXVersion.FIX42.getMessageFactory());
		AveragePriceView view = (AveragePriceView) getTestView();
		view.setInput(hist);
		
		NewOrderSingle order1 = new NewOrderSingle(
				new ClOrdID("clordid1"),
				new HandlInst(HandlInst.AUTOMATED_EXECUTION_ORDER_PRIVATE),
				new Symbol("symbol1"),
				new Side(Side.BUY),
				new TransactTime(new Date()),
				new OrdType(OrdType.MARKET));
		order1.set(new OrderQty(100));
		hist.addOutgoingMessage(order1);
		
		NewOrderSingle order2 = new NewOrderSingle(
				new ClOrdID("clordid2"),
				new HandlInst(HandlInst.AUTOMATED_EXECUTION_ORDER_PRIVATE),
				new Symbol("symbol1"),
				new Side(Side.BUY),
				new TransactTime(new Date()),
				new OrdType(OrdType.MARKET));
		order2.set(new OrderQty(100));
		hist.addOutgoingMessage(order2);

		ExecutionReport fill = new ExecutionReport(
				new OrderID("orderid1"),
				new ExecID("execid1"),
				new ExecTransType(ExecTransType.STATUS),
				new ExecType(ExecType.PARTIAL_FILL),
				new OrdStatus(OrdStatus.PARTIALLY_FILLED),
				new Symbol("symbol1"),
				new Side(Side.BUY),
				new LeavesQty(909),
				new CumQty(91),
				new AvgPx(3));
		fill.setField(new OrderQty(1000));
		fill.setField(new LastPx(82));
		fill.setField(new LastShares(91));
		hist.addIncomingMessage(fill);
		delay(1);
		assertEquals(1, hist.getAveragePricesList().size());
		IndexedTableViewer tableViewer = view.getMessagesViewer();
		Table table = tableViewer.getTable();
		assertEquals(1, table.getItemCount());
		
		fill = new ExecutionReport(
				new OrderID("orderid2"),
				new ExecID("execid2"),
				new ExecTransType(ExecTransType.STATUS),
				new ExecType(ExecType.PARTIAL_FILL),
				new OrdStatus(OrdStatus.PARTIALLY_FILLED),
				new Symbol("symbol1"),
				new Side(Side.BUY),
				new LeavesQty(909),
				new CumQty(91),
				new AvgPx(6));
		fill.setField(new OrderQty(1000));
		fill.setField(new LastPx(80));
		fill.setField(new LastShares(91));
		hist.addIncomingMessage(fill);
		delay(1);
		assertEquals(1, table.getItemCount());
		
		fill = new ExecutionReport(
				new OrderID("orderid2"),
				new ExecID("execid2"),
				new ExecTransType(ExecTransType.STATUS),
				new ExecType(ExecType.PARTIAL_FILL),
				new OrdStatus(OrdStatus.PARTIALLY_FILLED),
				new Symbol("symbol3"),
				new Side(Side.BUY),
				new LeavesQty(909),
				new CumQty(1000),
				new AvgPx(6));
		fill.setField(new OrderQty(1000));
		fill.setField(new LastPx(808));
		fill.setField(new LastShares(909));
		hist.addIncomingMessage(fill);
		delay(1);
		
		assertEquals(2, table.getItemCount());
		TableItem item = table.getItem(0);
		IncomingMessageHolder returnedMessageHolder = (IncomingMessageHolder) item.getData();
		Message message = returnedMessageHolder.getMessage();
		assertEquals("symbol1", message.getString(Symbol.FIELD));
		assertEquals(0, new BigDecimal("81").compareTo(message.getDecimal(AvgPx.FIELD)));
		BigDecimal foundOrdQty = message.getDecimal(OrderQty.FIELD);
		assertEquals("Order quantity incorrect: "+foundOrdQty, 0, new BigDecimal("200").compareTo(foundOrdQty));
		
	}
	
	@Override
	protected String getViewID() {
		return AveragePriceView.ID;
	}
}
