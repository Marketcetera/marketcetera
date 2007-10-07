package org.marketcetera.photon.views;

import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.marketcetera.photon.core.FIXMessageHistory;
import org.marketcetera.photon.core.IncomingMessageHolder;
import org.marketcetera.photon.ui.IndexedTableViewer;
import org.marketcetera.quickfix.FIXVersion;
import quickfix.Message;
import quickfix.field.*;
import quickfix.fix42.ExecutionReport;
import quickfix.fix42.NewOrderSingle;

import java.math.BigDecimal;
import java.util.Date;

public class AveragePricesViewTest extends ViewTestBase {

	public AveragePricesViewTest(String name) {
		super(name);
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
		assertEquals(0, new BigDecimal("81").compareTo(new BigDecimal(message.getString(AvgPx.FIELD))));
		BigDecimal foundOrdQty = new BigDecimal(message.getString(OrderQty.FIELD));
		assertEquals("Order quantity incorrect: "+foundOrdQty, 0, new BigDecimal("200").compareTo(foundOrdQty));
		
	}
	
	@Override
	protected String getViewID() {
		return AveragePriceView.ID;
	}

}
