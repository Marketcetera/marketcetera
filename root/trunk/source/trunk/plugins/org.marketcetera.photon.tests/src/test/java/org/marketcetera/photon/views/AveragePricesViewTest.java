package org.marketcetera.photon.views;

import java.math.BigDecimal;

import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.marketcetera.photon.core.FIXMessageHistory;
import org.marketcetera.photon.core.IncomingMessageHolder;
import org.marketcetera.photon.ui.IndexedTableViewer;
import org.marketcetera.quickfix.FIXVersion;

import quickfix.Message;
import quickfix.field.AvgPx;
import quickfix.field.CumQty;
import quickfix.field.ExecID;
import quickfix.field.ExecTransType;
import quickfix.field.ExecType;
import quickfix.field.LastPx;
import quickfix.field.LastShares;
import quickfix.field.LeavesQty;
import quickfix.field.OrdStatus;
import quickfix.field.OrderID;
import quickfix.field.OrderQty;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.fix42.ExecutionReport;

public class AveragePricesViewTest extends ViewTestBase {

	public AveragePricesViewTest(String name) {
		super(name);
	}

	public void testShowMessage() throws Exception {
		FIXMessageHistory hist = new FIXMessageHistory(FIXVersion.FIX42.getMessageFactory());
		AveragePriceView view = (AveragePriceView) getTestView();
		view.setInput(hist);
		
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
		
	}
	
	@Override
	protected String getViewID() {
		return AveragePriceView.ID;
	}

}
