package org.marketcetera.photon.views;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.marketcetera.photon.core.FIXMessageHistory;
import org.marketcetera.photon.core.IncomingMessageHolder;

import quickfix.Message;
import quickfix.field.AvgPx;
import quickfix.field.CumQty;
import quickfix.field.ExecID;
import quickfix.field.ExecTransType;
import quickfix.field.ExecType;
import quickfix.field.LastPx;
import quickfix.field.LastQty;
import quickfix.field.LeavesQty;
import quickfix.field.OrdStatus;
import quickfix.field.OrderID;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.fix42.ExecutionReport;

public class FillsViewTest extends ViewTestBase {

	public FillsViewTest(String name) {
		super(name);
	}

	public void testShowMessage() throws Exception {
		FIXMessageHistory hist = new FIXMessageHistory();
		FillsView view = (FillsView) getTestView();
		view.setInput(hist);
		
		
		hist.addIncomingMessage(new ExecutionReport(
				new OrderID("orderid1"),
				new ExecID("execid1"),
				new ExecTransType(ExecTransType.STATUS),
				new ExecType(ExecType.PARTIAL_FILL),
				new OrdStatus(OrdStatus.PARTIALLY_FILLED),
				new Symbol("symbol1"),
				new Side(Side.BUY),
				new LeavesQty(1),
				new CumQty(2),
				new AvgPx(3)));
		delay(1);
		TableViewer tableViewer = view.getMessagesViewer();
		Table table = tableViewer.getTable();
		assertEquals(0, table.getItemCount());
		
		ExecutionReport fill = new ExecutionReport(
				new OrderID("orderid2"),
				new ExecID("execid2"),
				new ExecTransType(ExecTransType.STATUS),
				new ExecType(ExecType.PARTIAL_FILL),
				new OrdStatus(OrdStatus.PARTIALLY_FILLED),
				new Symbol("symbol2"),
				new Side(Side.BUY),
				new LeavesQty(4),
				new CumQty(5),
				new AvgPx(6));
		fill.setField(new LastPx(81));
		fill.setField(new LastQty(91));
		hist.addIncomingMessage(fill);
		delay(1);
		
		TableItem item = table.getItem(0);
		IncomingMessageHolder returnedMessageHolder = (IncomingMessageHolder) item.getData();
		Message message = returnedMessageHolder.getMessage();
		assertEquals("orderid2", message.getString(OrderID.FIELD));
		
		
	}
	
	@Override
	protected String getViewID() {
		return FillsView.ID;
	}

}
