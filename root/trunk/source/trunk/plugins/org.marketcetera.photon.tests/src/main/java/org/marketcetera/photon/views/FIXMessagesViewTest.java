package org.marketcetera.photon.views;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.marketcetera.photon.model.FIXMessageHistory;
import org.marketcetera.photon.model.IncomingMessageHolder;

import quickfix.Message;
import quickfix.field.AvgPx;
import quickfix.field.CumQty;
import quickfix.field.ExecID;
import quickfix.field.ExecTransType;
import quickfix.field.ExecType;
import quickfix.field.LeavesQty;
import quickfix.field.OrdStatus;
import quickfix.field.OrderID;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.fix42.ExecutionReport;

public class FIXMessagesViewTest extends ViewTestBase {

	public FIXMessagesViewTest(String name) {
		super(name);
	}

	public void testShowMessage() throws Exception {
		FIXMessageHistory hist = new FIXMessageHistory();
		FIXMessagesView view = (FIXMessagesView) getTestView();
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
		TableItem item = table.getItem(0);
		IncomingMessageHolder returnedMessageHolder = (IncomingMessageHolder) item.getData();
		Message message = returnedMessageHolder.getMessage();
		assertEquals("orderid1", message.getString(OrderID.FIELD));
	}
	
	@Override
	protected String getViewID() {
		return FIXMessagesView.ID;
	}

}
