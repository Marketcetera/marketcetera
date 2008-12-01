package org.marketcetera.photon.views;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.marketcetera.photon.messagehistory.FIXRegexMatcher;
import org.marketcetera.photon.messagehistory.FIXStringMatcher;

import quickfix.Message;
import quickfix.field.AvgPx;
import quickfix.field.ClOrdID;
import quickfix.field.CumQty;
import quickfix.field.ExecID;
import quickfix.field.ExecTransType;
import quickfix.field.ExecType;
import quickfix.field.LastPx;
import quickfix.field.LastQty;
import quickfix.field.LeavesQty;
import quickfix.field.MsgSeqNum;
import quickfix.field.OrdStatus;
import quickfix.field.OrderID;
import quickfix.field.OrderQty;
import quickfix.field.Price;
import quickfix.field.SenderCompID;
import quickfix.field.SendingTime;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.TargetCompID;
import quickfix.field.TransactTime;
import quickfix.field.Urgency;
import quickfix.fix42.ExecutionReport;

/* $License$ */
/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @since 0.7.0
 * @version $Id$
 */
public class OpenOrdersViewTest
    extends ViewTestBase
{
    /**
     * Create a new <code>OpenOrdersViewTest</code> instance.
     *
     * @param inName
     */
    public OpenOrdersViewTest(String inName)
    {
        super(inName);
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
    /* (non-Javadoc)
     * @see org.marketcetera.photon.views.ViewTestBase#getViewID()
     */
    @Override
    protected String getViewID()
    {
        return OpenOrdersView.ID;
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
                                                    new AvgPx(0));
        fill1.setField(new LastPx(1));
        fill1.setField(new LastQty(2));
        fill1.setField(new ClOrdID("orderid1"));
        fill1.setField(new MsgSeqNum(0));
        fill1.setField(new OrderQty(new BigDecimal(100)));
        fill1.setField(new Price(new BigDecimal(100)));
        fill1.setField(new SenderCompID("sender-comp"));
        fill1.setField(new SendingTime(new Date()));
        fill1.setField(new TargetCompID("target-comp"));
        fill1.setField(new TransactTime(new Date()));
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
        fill2.setField(new LastPx(1));
        fill2.setField(new LastQty(2));
        fill2.setField(new ClOrdID("orderid2"));
        fill2.setField(new MsgSeqNum(0));
        fill2.setField(new OrderQty(new BigDecimal(100)));
        fill2.setField(new Price(new BigDecimal(100)));
        fill2.setField(new SenderCompID("sender-comp"));
        fill2.setField(new SendingTime(new Date()));
        fill2.setField(new TargetCompID("target-comp"));
        fill2.setField(new TransactTime(new Date()));
        messages.add(fill2);
        ExecutionReport fill3 = new ExecutionReport(new OrderID("orderid3"),
                                                    new ExecID("execid3"),
                                                    new ExecTransType(ExecTransType.STATUS),
                                                    new ExecType(ExecType.PARTIAL_FILL),
                                                    new OrdStatus(OrdStatus.PARTIALLY_FILLED),
                                                    new Symbol("symbol3"),
                                                    new Side(Side.SELL),
                                                    new LeavesQty(4),
                                                    new CumQty(5),
                                                    new AvgPx(6));
        fill3.setField(new LastPx(1));
        fill3.setField(new LastQty(2));
        fill3.setField(new ClOrdID("orderid3"));
        fill3.setField(new MsgSeqNum(0));
        fill3.setField(new OrderQty(new BigDecimal(100)));
        fill3.setField(new Price(new BigDecimal(100)));
        fill3.setField(new SenderCompID("sender-comp"));
        fill3.setField(new SendingTime(new Date()));
        fill3.setField(new TargetCompID("target-comp"));
        fill3.setField(new TransactTime(new Date()));
        messages.add(fill3);
        return messages;
    }
}
