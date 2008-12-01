package org.marketcetera.photon.views;

import java.util.ArrayList;
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
import quickfix.field.Urgency;
import quickfix.fix42.ExecutionReport;

public class FillsViewTest
    extends ViewTestBase
{
    public FillsViewTest(String name)
    {
        super(name);
    }
    public void testShowMessage() throws Exception
    {
        FIXMessageHistory hist = new FIXMessageHistory(FIXVersion.FIX_SYSTEM.getMessageFactory());
        FillsView view = (FillsView) getTestView();
        view.setInput(hist);
        hist.addIncomingMessage(new ExecutionReport(new OrderID("orderid1"),
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
        IndexedTableViewer tableViewer = view.getMessagesViewer();
        Table table = tableViewer.getTable();
        assertEquals(0,
                     table.getItemCount());
        ExecutionReport fill = new ExecutionReport(new OrderID("orderid2"),
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
        assertEquals("orderid2",
                     message.getString(OrderID.FIELD));
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
    @Override
    protected String getViewID()
    {
        return FillsView.ID;
    }
    /*
     * (non-Javadoc)
     * 
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
                                               new int[] {}));
        // string match on non-existent field
        conditions.add(new FilterTestCondition(new FIXStringMatcher(Urgency.FIELD,
                                                                    "0"),
                                               new int[] {}));
        // regex match on existing field
        conditions.add(new FilterTestCondition(new FIXRegexMatcher(Symbol.FIELD,
                                                                   "[a-z]*bol[1|2]"),
                                               new int[] { 0, 1 }));
        // regex match on non-existent field
        conditions.add(new FilterTestCondition(new FIXRegexMatcher(Symbol.FIELD,
                                                                   "[a-z]*bxol[1|2]"),
                                               new int[] {}));
        // regex no match in non-existent field
        conditions.add(new FilterTestCondition(new FIXRegexMatcher(Urgency.FIELD,
                                                                   ".*"),
                                               new int[] {}));
        return conditions;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.photon.views.ViewTestBase#getFilterTestMessages()
     */
    @Override
    protected List<Message> getFilterTestMessages()
    {
        List<Message> messages = new ArrayList<Message>();
        ExecutionReport fill1 = new ExecutionReport(new OrderID("orderid1"),
                                                    new ExecID("execid1"),
                                                    new ExecTransType(ExecTransType.STATUS),
                                                    new ExecType(ExecType.PARTIAL_FILL),
                                                    new OrdStatus(OrdStatus.PARTIALLY_FILLED),
                                                    new Symbol("symbol1"),
                                                    new Side(Side.BUY),
                                                    new LeavesQty(4),
                                                    new CumQty(5),
                                                    new AvgPx(6));
        fill1.setField(new LastPx(81));
        fill1.setField(new LastQty(91));
        messages.add(fill1);
        ExecutionReport fill2 = new ExecutionReport(new OrderID("orderid2"),
                                                    new ExecID("execid2"),
                                                    new ExecTransType(ExecTransType.STATUS),
                                                    new ExecType(ExecType.PARTIAL_FILL),
                                                    new OrdStatus(OrdStatus.PARTIALLY_FILLED),
                                                    new Symbol("symbol2"),
                                                    new Side(Side.BUY),
                                                    new LeavesQty(4),
                                                    new CumQty(5),
                                                    new AvgPx(6));
        fill2.setField(new LastPx(81));
        fill2.setField(new LastQty(91));
        messages.add(fill2);
        ExecutionReport fill3 = new ExecutionReport(new OrderID("orderid3"),
                                                    new ExecID("execid3"),
                                                    new ExecTransType(ExecTransType.STATUS),
                                                    new ExecType(ExecType.FILL),
                                                    new OrdStatus(OrdStatus.FILLED),
                                                    new Symbol("symbol3"),
                                                    new Side(Side.SELL),
                                                    new LeavesQty(4),
                                                    new CumQty(5),
                                                    new AvgPx(6));
        fill3.setField(new LastPx(81));
        fill3.setField(new LastQty(91));
        messages.add(fill3);
        return messages;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.photon.views.ViewTestBase#addMessage(quickfix.Message, org.marketcetera.messagehistory.FIXMessageHistory)
     */
    @Override
    protected void addMessage(Message inMessage,
                              FIXMessageHistory inHistory)
    {
        inHistory.addIncomingMessage(inMessage);
    }
}
