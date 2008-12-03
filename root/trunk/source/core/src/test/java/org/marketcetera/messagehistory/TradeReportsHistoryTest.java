package org.marketcetera.messagehistory;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Vector;

import junit.framework.Test;

import org.marketcetera.core.AccessViolator;
import org.marketcetera.core.FIXVersionTestSuite;
import org.marketcetera.core.FIXVersionedTestCase;
import org.marketcetera.core.MSymbol;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.trade.*;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.AvgPx;
import quickfix.field.ClOrdID;
import quickfix.field.CumQty;
import quickfix.field.CxlRejReason;
import quickfix.field.CxlRejResponseTo;
import quickfix.field.ExecID;
import quickfix.field.ExecTransType;
import quickfix.field.ExecType;
import quickfix.field.LastPx;
import quickfix.field.LastShares;
import quickfix.field.LeavesQty;
import quickfix.field.MsgType;
import quickfix.field.OrdStatus;
import quickfix.field.OrderID;
import quickfix.field.OrderQty;
import quickfix.field.OrigClOrdID;
import quickfix.field.SendingTime;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.TimeInForce;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;

/* $License$ */

/**
 * Test {@link TradeReportsHistory}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class TradeReportsHistoryTest extends FIXVersionedTestCase {

    public TradeReportsHistoryTest(String inName, FIXVersion version) {
        super(inName, version);
    }

    public static Test suite() {
        return new FIXVersionTestSuite(TradeReportsHistoryTest.class,
                FIXVersion.values());
    }

    protected TradeReportsHistory createMessageHistory() {
        return new TradeReportsHistory(FIXVersion.FIX_SYSTEM.getMessageFactory());
    }

    public void testAddIncomingMessage() throws Exception {
        TradeReportsHistory history = createMessageHistory();
        String orderID1 = "1"; //$NON-NLS-1$
        String clOrderID1 = "2"; //$NON-NLS-1$
        String execID = "3"; //$NON-NLS-1$
        char execType = ExecType.PARTIAL_FILL;
        char ordStatus = OrdStatus.PARTIALLY_FILLED;
        char side = Side.SELL_SHORT;
        BigDecimal orderQty = new BigDecimal(1000);
        BigDecimal orderPrice = new BigDecimal(789);
        BigDecimal lastQty = new BigDecimal(100);
        BigDecimal lastPrice = new BigDecimal("12.3"); //$NON-NLS-1$
        BigDecimal cumQty = new BigDecimal(100);
        BigDecimal avgPrice = new BigDecimal("12.3"); //$NON-NLS-1$
        MSymbol symbol = new MSymbol("ASDF"); //$NON-NLS-1$

        Message message = msgFactory.newExecutionReport(orderID1, clOrderID1,
                execID, ordStatus, side, orderQty, orderPrice, lastQty,
                lastPrice, cumQty, avgPrice, symbol, null);

        {
            history.addIncomingMessage(createReport(message));
            EventList<ReportHolder> historyList = history.getAllMessagesList();
            assertEquals(1, historyList.size());
            ReportHolder holder = historyList.get(0);
            Message historyMessage = holder.getMessage();
            assertEquals(orderID1, historyMessage.getString(OrderID.FIELD));
            assertEquals(clOrderID1, historyMessage.getString(ClOrdID.FIELD));
            assertEquals(execID, historyMessage.getString(ExecID.FIELD));
            if (historyMessage.isSetField(ExecType.FIELD)) { // in case of FIX 4.0 where ExecType doesn't exist
                assertEquals(
                        "" + execType, historyMessage.getString(ExecType.FIELD)); //$NON-NLS-1$
            }
            assertEquals(
                    "" + ordStatus, historyMessage.getString(OrdStatus.FIELD)); //$NON-NLS-1$
            assertEquals("" + side, historyMessage.getString(Side.FIELD)); //$NON-NLS-1$
            assertEquals(orderQty, historyMessage.getDecimal(OrderQty.FIELD));
            assertEquals(lastQty, historyMessage.getDecimal(LastShares.FIELD));
            assertEquals(lastPrice, historyMessage.getDecimal(LastPx.FIELD));
            assertEquals(cumQty, historyMessage.getDecimal(CumQty.FIELD));
            assertEquals(avgPrice, historyMessage.getDecimal(AvgPx.FIELD));
            assertEquals(symbol.getFullSymbol(), historyMessage
                    .getString(Symbol.FIELD));
        }

        {
            String orderID2 = "1001"; //$NON-NLS-1$
            String clOrderID2 = "1002"; //$NON-NLS-1$
            Message message2 = msgFactory.newExecutionReport(orderID2,
                    clOrderID2, execID, ordStatus, side, orderQty, orderPrice,
                    lastQty, lastPrice, cumQty, avgPrice, symbol, null);
            history.addIncomingMessage(createReport(message2));
            EventList<ReportHolder> historyList = history.getAllMessagesList();
            assertEquals(2, historyList.size());
            ReportHolder holder = historyList.get(1);
            Message historyMessage = holder.getMessage();
            assertEquals(orderID2, historyMessage.getString(OrderID.FIELD));
            assertEquals(clOrderID2, historyMessage.getString(ClOrdID.FIELD));
            assertEquals(execID, historyMessage.getString(ExecID.FIELD));
            if (historyMessage.isSetField(ExecType.FIELD)) { // in case of FIX 4.0 where ExecType doesn't exist
                assertEquals(
                        "" + execType, historyMessage.getString(ExecType.FIELD)); //$NON-NLS-1$
            }
            assertEquals(
                    "" + ordStatus, historyMessage.getString(OrdStatus.FIELD)); //$NON-NLS-1$
            assertEquals("" + side, historyMessage.getString(Side.FIELD)); //$NON-NLS-1$
            assertEquals(orderQty, historyMessage.getDecimal(OrderQty.FIELD));
            assertEquals(lastQty, historyMessage.getDecimal(LastShares.FIELD));
            assertEquals(lastPrice, historyMessage.getDecimal(LastPx.FIELD));
            assertEquals(cumQty, historyMessage.getDecimal(CumQty.FIELD));
            assertEquals(avgPrice, historyMessage.getDecimal(AvgPx.FIELD));
            assertEquals(symbol.getFullSymbol(), historyMessage
                    .getString(Symbol.FIELD));
        }
    }

    public void testGetLatestExecutionReports() throws Exception {
        long currentTime = System.currentTimeMillis();
        TradeReportsHistory history = createMessageHistory();
        Message executionReportForOrder1 = msgFactory
                .newExecutionReport(
                        "1001", "1", "2001", OrdStatus.NEW, Side.BUY, new BigDecimal(1000), new BigDecimal(789), null, null, BigDecimal.ZERO, BigDecimal.ZERO, new MSymbol("ASDF"), null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        executionReportForOrder1.getHeader().setField(
                new SendingTime(new Date(currentTime - 10000)));
        Message order2 = msgFactory
                .newLimitOrder(
                        "3", Side.SELL, new BigDecimal(2000), new MSymbol("QWER"), new BigDecimal("12.3"), TimeInForce.DAY, "1"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        Message executionReportForOrder2 = msgFactory
                .newExecutionReport(
                        "1003", "3", "2003", OrdStatus.NEW, Side.SELL, new BigDecimal(2000), new BigDecimal(789), null, null, BigDecimal.ZERO, BigDecimal.ZERO, new MSymbol("QWER"), null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        executionReportForOrder2.getHeader().setField(
                new SendingTime(new Date(currentTime - 8000)));
        Message secondExecutionReportForOrder1 = msgFactory
                .newExecutionReport(
                        "1001", "1", "2004", OrdStatus.PARTIALLY_FILLED, Side.BUY, new BigDecimal(1000), new BigDecimal(789), new BigDecimal(100), new BigDecimal("11.5"), new BigDecimal(100), new BigDecimal("11.5"), new MSymbol("ASDF"), null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
        secondExecutionReportForOrder1.getHeader().setField(
                new SendingTime(new Date(currentTime - 7000)));

        history.addIncomingMessage(createReport(executionReportForOrder1));
        history.addIncomingMessage(createReport(executionReportForOrder2));
        history
                .addIncomingMessage(createReport(secondExecutionReportForOrder1));

        ExecutionReport historyExecutionReportForOrder1 = (ExecutionReport) history
                .getLatestExecutionReport(new org.marketcetera.trade.OrderID(
                        "1")); //$NON-NLS-1$
        assertNotNull(historyExecutionReportForOrder1);
        ExecutionReport historyExecutionReportForOrder2 = (ExecutionReport) history
                .getLatestExecutionReport(new org.marketcetera.trade.OrderID(
                        "3")); //$NON-NLS-1$
        assertNotNull(historyExecutionReportForOrder2);

        assertEquals("1001", historyExecutionReportForOrder1
                .getDestinationOrderID());
        assertEquals("2004", historyExecutionReportForOrder1.getExecutionID()); //$NON-NLS-1$
        assertEquals(executionReportForOrder1.getString(ClOrdID.FIELD),
                historyExecutionReportForOrder1.getOrderID().getValue());
        assertSame(org.marketcetera.trade.Side.Buy,
                historyExecutionReportForOrder1.getSide());
        assertEquals(executionReportForOrder1.getString(OrderQty.FIELD),
                historyExecutionReportForOrder1.getOrderQuantity().toString());
        assertEquals(executionReportForOrder1.getString(Symbol.FIELD),
                historyExecutionReportForOrder1.getSymbol().toString());

        assertEquals("1003", historyExecutionReportForOrder2
                .getDestinationOrderID());
        assertEquals("2003", historyExecutionReportForOrder2.getExecutionID()); //$NON-NLS-1$
        assertEquals(order2.getString(ClOrdID.FIELD),
                historyExecutionReportForOrder2.getOrderID().getValue());
        assertSame(org.marketcetera.trade.Side.Sell,
                historyExecutionReportForOrder2.getSide());
        assertEquals(order2.getString(OrderQty.FIELD),
                historyExecutionReportForOrder2.getOrderQuantity().toString());
        assertEquals(order2.getString(Symbol.FIELD),
                historyExecutionReportForOrder2.getSymbol().toString());
    }

    public void testGetLatestMessage() throws Exception {
        long currentTime = System.currentTimeMillis();
        TradeReportsHistory history = createMessageHistory();
        Message executionReportForOrder1 = msgFactory
                .newExecutionReport(
                        "1001", "1", "2001", OrdStatus.NEW, Side.BUY, new BigDecimal(1000), new BigDecimal(789), null, null, BigDecimal.ZERO, BigDecimal.ZERO, new MSymbol("ASDF"), null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        executionReportForOrder1.getHeader().setField(
                new SendingTime(new Date(currentTime - 10000)));
        Message order2 = msgFactory
                .newLimitOrder(
                        "3", Side.SELL, new BigDecimal(2000), new MSymbol("QWER"), new BigDecimal("12.3"), TimeInForce.DAY, "1"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        Message executionReportForOrder2 = msgFactory
                .newExecutionReport(
                        "1003", "3", "2003", OrdStatus.NEW, Side.SELL, new BigDecimal(2000), new BigDecimal(789), null, null, BigDecimal.ZERO, BigDecimal.ZERO, new MSymbol("QWER"), null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        executionReportForOrder2.getHeader().setField(
                new SendingTime(new Date(currentTime - 8000)));
        Message secondExecutionReportForOrder1 = msgFactory
                .newExecutionReport(
                        "1001", "1", "2004", OrdStatus.PARTIALLY_FILLED, Side.BUY, new BigDecimal(1000), new BigDecimal(789), new BigDecimal(100), new BigDecimal("11.5"), new BigDecimal(100), new BigDecimal("11.5"), new MSymbol("ASDF"), null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
        secondExecutionReportForOrder1.getHeader().setField(
                new SendingTime(new Date(currentTime - 7000)));

        Message aMessage = msgFactory.createMessage(MsgType.EXECUTION_REPORT);

        history.addIncomingMessage(createReport(aMessage));
        history.addIncomingMessage(createReport(executionReportForOrder1));
        history.addIncomingMessage(createReport(executionReportForOrder2));
        history
                .addIncomingMessage(createReport(secondExecutionReportForOrder1));

        Message historyExecutionReportForOrder1 = history
                .getLatestMessage(new org.marketcetera.trade.OrderID("1")); //$NON-NLS-1$
        assertNotNull(historyExecutionReportForOrder1);
        Message historyExecutionReportForOrder2 = history
                .getLatestMessage(new org.marketcetera.trade.OrderID("3")); //$NON-NLS-1$
        assertNotNull(historyExecutionReportForOrder2);

        assertEquals(
                "1001", historyExecutionReportForOrder1.getString(OrderID.FIELD)); //$NON-NLS-1$
        assertEquals(
                "2004", historyExecutionReportForOrder1.getString(ExecID.FIELD)); //$NON-NLS-1$
        assertEquals(executionReportForOrder1.getString(ClOrdID.FIELD),
                historyExecutionReportForOrder1.getString(ClOrdID.FIELD));
        assertEquals(executionReportForOrder1.getString(Side.FIELD),
                historyExecutionReportForOrder1.getString(Side.FIELD));
        assertEquals(executionReportForOrder1.getString(OrderQty.FIELD),
                historyExecutionReportForOrder1.getString(OrderQty.FIELD));
        assertEquals(executionReportForOrder1.getString(Symbol.FIELD),
                historyExecutionReportForOrder1.getString(Symbol.FIELD));

        assertEquals(
                "1003", historyExecutionReportForOrder2.getString(OrderID.FIELD)); //$NON-NLS-1$
        assertEquals(
                "2003", historyExecutionReportForOrder2.getString(ExecID.FIELD)); //$NON-NLS-1$
        assertEquals(order2.getString(ClOrdID.FIELD),
                historyExecutionReportForOrder2.getString(ClOrdID.FIELD));
        assertEquals(order2.getString(Side.FIELD),
                historyExecutionReportForOrder2.getString(Side.FIELD));
        assertEquals(order2.getString(OrderQty.FIELD),
                historyExecutionReportForOrder2.getString(OrderQty.FIELD));
        assertEquals(order2.getString(Symbol.FIELD),
                historyExecutionReportForOrder2.getString(Symbol.FIELD));
    }

    public void testOrderCancelReject() throws Exception {
        TradeReportsHistory history = createMessageHistory();
        {
            Message executionReportForOrder1 = msgFactory
                    .newExecutionReport(
                            "1001", "1", "2001", OrdStatus.NEW, Side.BUY, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                            new BigDecimal(1000), new BigDecimal(789), null,
                            null, BigDecimal.ZERO, BigDecimal.ZERO,
                            new MSymbol("ASDF"), null); //$NON-NLS-1$
            history.addIncomingMessage(createReport(executionReportForOrder1));

            assertEquals(
                    OrderStatus.New,
                    ((ExecutionReport) history
                            .getLatestExecutionReport(new org.marketcetera.trade.OrderID(
                                    "1"))).getOrderStatus()); //$NON-NLS-1$

            Message cancelReject = msgFactory
                    .createMessage(MsgType.ORDER_CANCEL_REJECT);
            cancelReject.setField(new OrderID("1001")); //$NON-NLS-1$
            cancelReject.setField(new ClOrdID("2")); //$NON-NLS-1$
            cancelReject.setField(new OrigClOrdID("1")); //$NON-NLS-1$
            cancelReject.setField(new OrdStatus(OrdStatus.FILLED));
            cancelReject.setField(new CxlRejResponseTo(
                    CxlRejResponseTo.ORDER_CANCEL_REQUEST));
            history.addIncomingMessage(Factory.getInstance()
                    .createOrderCancelReject(cancelReject,
                            new DestinationID("ABC")));

            assertEquals(
                    OrderStatus.Filled,
                    ((ExecutionReport) history
                            .getLatestExecutionReport(new org.marketcetera.trade.OrderID(
                                    "1"))).getOrderStatus()); //$NON-NLS-1$
        }

        {
            Message executionReportForOrder2 = msgFactory
                    .newExecutionReport(
                            "1002", "2", "2002", OrdStatus.NEW, Side.BUY, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                            new BigDecimal(1000), new BigDecimal(789), null,
                            null, BigDecimal.ZERO, BigDecimal.ZERO,
                            new MSymbol("ASDF"), null); //$NON-NLS-1$
            history.addIncomingMessage(createReport(executionReportForOrder2));

            assertEquals(
                    OrderStatus.New,
                    ((ExecutionReport) history
                            .getLatestExecutionReport(new org.marketcetera.trade.OrderID(
                                    "1"))).getOrderStatus()); //$NON-NLS-1$

            Message cancelReject = msgFactory
                    .createMessage(MsgType.ORDER_CANCEL_REJECT);
            cancelReject.setField(new OrderID("1001")); //$NON-NLS-1$
            cancelReject.setField(new ClOrdID("2")); //$NON-NLS-1$
            cancelReject.setField(new OrigClOrdID("1")); //$NON-NLS-1$
            // Don't set ord-status
            cancelReject.setField(new CxlRejResponseTo(
                    CxlRejResponseTo.ORDER_CANCEL_REQUEST));
            history.addIncomingMessage(Factory.getInstance()
                    .createOrderCancelReject(cancelReject,
                            new DestinationID("ABC")));

            assertEquals(
                    OrderStatus.New,
                    ((ExecutionReport) history
                            .getLatestExecutionReport(new org.marketcetera.trade.OrderID(
                                    "1"))).getOrderStatus()); //$NON-NLS-1$
        }

    }

    public void testAddFIXMessageListener() throws Exception {
        TradeReportsHistory history = createMessageHistory();

        Message executionReportForOrder1 = msgFactory
                .newExecutionReport(
                        "1001", "1", "2001", OrdStatus.NEW, Side.BUY, new BigDecimal(1000), new BigDecimal(789), null, null, BigDecimal.ZERO, BigDecimal.ZERO, new MSymbol("ASDF"), null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

        ListEventListener<ReportHolder> fixMessageListener = new ListEventListener<ReportHolder>() {
            public int numIncomingMessages = 0;

            @SuppressWarnings("unchecked")//$NON-NLS-1$
            public void listChanged(ListEvent<ReportHolder> event) {
                if (event.hasNext()) {
                    event.next();
                    if (event.getType() == ListEvent.INSERT) {
                        EventList<ReportHolder> source = (EventList<ReportHolder>) event
                                .getSource();
                        int index = event.getIndex();
                        ReportHolder holder = source.get(index);
                        try {
                            assertEquals(
                                    "1001", holder.getMessage().getString(OrderID.FIELD)); //$NON-NLS-1$
                            numIncomingMessages++;
                        } catch (FieldNotFound e) {
                            fail(e.getMessage());
                        }
                    }
                }
            }

        };
        history.getAllMessagesList().addListEventListener(fixMessageListener);

        history.addIncomingMessage(createReport(executionReportForOrder1));
        // just use the AccessViolator to get the fields out of the anon inner
        // class
        AccessViolator violator = new AccessViolator(fixMessageListener
                .getClass());
        assertEquals(1, violator.getField(
                "numIncomingMessages", fixMessageListener)); //$NON-NLS-1$
    }

    public void testRemovePortfolioListener() throws Exception {
        TradeReportsHistory history = createMessageHistory();

        Message executionReportForOrder1 = msgFactory
                .newExecutionReport(
                        "1", "1", "2001", OrdStatus.NEW, Side.BUY, new BigDecimal(1000), new BigDecimal(789), null, null, BigDecimal.ZERO, BigDecimal.ZERO, new MSymbol("ASDF"), null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

        ListEventListener<ReportHolder> fixMessageListener = new ListEventListener<ReportHolder>() {
            public int numIncomingMessages = 0;

            public void listChanged(ListEvent<ReportHolder> event) {
                if (event.getType() == ListEvent.INSERT) {
                    ReportHolder source = (ReportHolder) event.getSource();
                    try {
                        assertEquals(
                                "1001", source.getMessage().getString(OrderID.FIELD)); //$NON-NLS-1$
                        numIncomingMessages++;
                    } catch (FieldNotFound e) {
                        fail(e.getMessage());
                    }

                }
            }

        };

        history.getAllMessagesList().addListEventListener(fixMessageListener);
        history.getAllMessagesList()
                .removeListEventListener(fixMessageListener);

        history.addIncomingMessage(createReport(executionReportForOrder1));
        // just use the AccessViolator to get the fields out of the anon inner
        // class
        AccessViolator violator = new AccessViolator(fixMessageListener
                .getClass());
        assertEquals(0, violator.getField(
                "numIncomingMessages", fixMessageListener)); //$NON-NLS-1$
    }

    public void testAveragePriceList() throws Exception {
        TradeReportsHistory messageHistory = createMessageHistory();
        String orderID1 = "1"; //$NON-NLS-1$
        String clOrderID1 = "1"; //$NON-NLS-1$
        String execID = "300"; //$NON-NLS-1$
        char ordStatus = OrdStatus.PARTIALLY_FILLED;
        char side = Side.SELL_SHORT;
        BigDecimal orderQty = new BigDecimal(1000);
        BigDecimal orderPrice = new BigDecimal(789);
        BigDecimal lastQty = new BigDecimal(100);
        BigDecimal lastPrice = new BigDecimal("12.3"); //$NON-NLS-1$
        BigDecimal cumQty = new BigDecimal("100"); //$NON-NLS-1$
        BigDecimal avgPrice = new BigDecimal("12.3"); //$NON-NLS-1$
        MSymbol symbol = new MSymbol("ASDF"); //$NON-NLS-1$

        Message message = msgFactory.newExecutionReport(orderID1, clOrderID1,
                execID, ordStatus, side, orderQty, orderPrice, lastQty,
                lastPrice, cumQty, avgPrice, symbol, null);
        messageHistory.addIncomingMessage(createReport(message));

        orderID1 = "1"; //$NON-NLS-1$
        clOrderID1 = "1"; //$NON-NLS-1$
        execID = "301"; //$NON-NLS-1$
        lastQty = new BigDecimal(900);
        lastPrice = new BigDecimal("12.4"); //$NON-NLS-1$
        cumQty = new BigDecimal(900);
        avgPrice = new BigDecimal("12.4"); //$NON-NLS-1$

        message = msgFactory.newExecutionReport(orderID1, clOrderID1, execID,
                ordStatus, side, orderQty, orderPrice, lastQty, lastPrice,
                cumQty, avgPrice, symbol, null);
        messageHistory.addIncomingMessage(createReport(message));

        EventList<ReportHolder> averagePriceList = messageHistory
                .getAveragePricesList();

        assertEquals(1, averagePriceList.size());

        ReportHolder holder = averagePriceList.get(0);
        Message returnedMessage = holder.getMessage();
        assertEquals(MsgType.EXECUTION_REPORT, returnedMessage.getHeader()
                .getString(MsgType.FIELD));

        BigDecimal returnedAvgPrice = returnedMessage.getDecimal(AvgPx.FIELD);
        assertTrue(new BigDecimal("1000").compareTo(returnedMessage.getDecimal(CumQty.FIELD)) == 0); //$NON-NLS-1$
        assertEquals(((12.3 * 100) + (12.4 * 900)) / 1000, returnedAvgPrice
                .doubleValue(), .0001);
        assertEquals(Side.SELL_SHORT, returnedMessage.getChar(Side.FIELD));

        orderID1 = "1"; //$NON-NLS-1$
        clOrderID1 = "1"; //$NON-NLS-1$
        execID = "302"; //$NON-NLS-1$
        lastQty = new BigDecimal(900);
        lastPrice = new BigDecimal("12.4"); //$NON-NLS-1$
        cumQty = new BigDecimal(900);
        avgPrice = new BigDecimal("12.4"); //$NON-NLS-1$
        side = Side.BUY;

        message = msgFactory.newExecutionReport(orderID1, clOrderID1, execID,
                ordStatus, side, orderQty, orderPrice, lastQty, lastPrice,
                cumQty, avgPrice, symbol, null);
        messageHistory.addIncomingMessage(createReport(message));

        assertEquals(2, messageHistory.getAveragePricesList().size());
        holder = averagePriceList.get(1);
        returnedMessage = holder.getMessage();
        assertEquals(MsgType.EXECUTION_REPORT, returnedMessage.getHeader()
                .getString(MsgType.FIELD));

        returnedAvgPrice = returnedMessage.getDecimal(AvgPx.FIELD);
        assertEquals(Side.BUY, returnedMessage.getChar(Side.FIELD));
        assertEquals(12.4, returnedAvgPrice.doubleValue(), .0001);
        assertTrue(new BigDecimal("900").compareTo(returnedMessage.getDecimal(CumQty.FIELD)) == 0); //$NON-NLS-1$

        orderID1 = "1"; //$NON-NLS-1$
        clOrderID1 = "1"; //$NON-NLS-1$
        execID = "305"; //$NON-NLS-1$
        lastQty = new BigDecimal(900);
        lastPrice = new BigDecimal("12.4"); //$NON-NLS-1$
        cumQty = new BigDecimal(900);
        avgPrice = new BigDecimal("12.4"); //$NON-NLS-1$
        side = Side.SELL_SHORT;

        message = msgFactory.newExecutionReport(orderID1, clOrderID1, execID,
                ordStatus, side, orderQty, orderPrice, lastQty, lastPrice,
                cumQty, avgPrice, symbol, null);
        messageHistory.addIncomingMessage(createReport(message));

        assertEquals(2, messageHistory.getAveragePricesList().size());
        holder = averagePriceList.get(0);
        returnedMessage = holder.getMessage();
        assertEquals(MsgType.EXECUTION_REPORT, returnedMessage.getHeader()
                .getString(MsgType.FIELD));

        returnedAvgPrice = returnedMessage.getDecimal(AvgPx.FIELD);
        assertEquals(Side.SELL_SHORT, returnedMessage.getChar(Side.FIELD));
        assertEquals(((12.3 * 100) + (12.4 * 900) + (12.4 * (900))) / 1900,
                returnedAvgPrice.doubleValue(), .0001);
        assertTrue(new BigDecimal("1900").compareTo(returnedMessage.getDecimal(CumQty.FIELD)) == 0); //$NON-NLS-1$

    }

    public void testAveragePriceList2() throws Exception {
        TradeReportsHistory hist = createMessageHistory();

        Message fill = msgFactory.newExecutionReport("clordid1", "clordid1",
                "execid1", OrdStatus.PARTIALLY_FILLED, Side.BUY,
                new BigDecimal(100), null, new BigDecimal(91), new BigDecimal(
                        82), new BigDecimal(91), new BigDecimal(3),
                new MSymbol("symbol1"), "account");

        fill.setField(new ExecTransType(ExecTransType.STATUS));
        fill.setField(new ExecType(ExecType.PARTIAL_FILL));
        fill.setField(new LeavesQty(909));
        hist.addIncomingMessage(createReport(fill));
        assertEquals(1, hist.getAveragePricesList().size());

        fill = msgFactory.newExecutionReport("clordid1", "orderid2", "execid1",
                OrdStatus.PARTIALLY_FILLED, Side.BUY, new BigDecimal(1000),
                null, new BigDecimal(91), new BigDecimal(80),
                new BigDecimal(91), new BigDecimal(6), new MSymbol("symbol1"),
                "account");

        fill.setField(new ExecTransType(ExecTransType.STATUS));
        fill.setField(new ExecType(ExecType.PARTIAL_FILL));
        fill.setField(new LeavesQty(909));
        hist.addIncomingMessage(createReport(fill));
        assertEquals(1, hist.getAveragePricesList().size());

        fill = msgFactory.newExecutionReport("clordid1", "orderid2", "execid2",
                OrdStatus.PARTIALLY_FILLED, Side.BUY, new BigDecimal(1000),
                null, new BigDecimal(909), new BigDecimal(808), new BigDecimal(
                        1000), new BigDecimal(6), new MSymbol("symbol3"),
                "account");

        fill.setField(new ExecTransType(ExecTransType.STATUS));
        fill.setField(new ExecType(ExecType.PARTIAL_FILL));
        fill.setField(new LeavesQty(909));
        hist.addIncomingMessage(createReport(fill));
        assertEquals(2, hist.getAveragePricesList().size());

        ReportHolder returnedMessageHolder = hist.getAveragePricesList().get(0);
        Message message = returnedMessageHolder.getMessage();
        assertEquals("symbol1", message.getString(Symbol.FIELD)); //$NON-NLS-1$
        assertEquals(0,
                new BigDecimal("81").compareTo(message.getDecimal(AvgPx.FIELD))); //$NON-NLS-1$
    }

    public void testExecutionReportOrder() throws Exception {
        String orderID1 = "1"; //$NON-NLS-1$
        String clOrderID1 = "1"; //$NON-NLS-1$
        String execID = "3"; //$NON-NLS-1$
        char ordStatus = OrdStatus.PARTIALLY_FILLED;
        char side = Side.SELL_SHORT;
        BigDecimal orderQty = new BigDecimal(1000);
        BigDecimal orderPrice = new BigDecimal(789);
        BigDecimal lastQty = new BigDecimal(100);
        BigDecimal lastPrice = new BigDecimal("12.3"); //$NON-NLS-1$
        BigDecimal cumQty = new BigDecimal(100);
        BigDecimal avgPrice = new BigDecimal("12.3"); //$NON-NLS-1$
        MSymbol symbol = new MSymbol("ASDF"); //$NON-NLS-1$

        SendingTime stField = new SendingTime(new Date(10000000));
        SendingTime stFieldLater = new SendingTime(new Date(10010000));

        Message message1 = msgFactory.newExecutionReport(null, clOrderID1,
                execID, ordStatus, side, orderQty, orderPrice, lastQty,
                lastPrice, cumQty, avgPrice, symbol, null);
        message1.getHeader().setField(stField);

        lastQty = new BigDecimal(200);
        Message message2 = msgFactory.newExecutionReport(orderID1, clOrderID1,
                execID, ordStatus, side, orderQty, orderPrice, lastQty,
                lastPrice, cumQty, avgPrice, symbol, null);
        message2.getHeader().setField(stField);

        lastQty = new BigDecimal(300);
        Message message3 = msgFactory.newExecutionReport(orderID1, clOrderID1,
                execID, ordStatus, side, orderQty, orderPrice, lastQty,
                lastPrice, cumQty, avgPrice, symbol, null);
        message3.getHeader().setField(stFieldLater);

        TradeReportsHistory history = createMessageHistory();
        history.addIncomingMessage(createReport(message1));
        history.addIncomingMessage(createReport(message2));
        assertEquals(new BigDecimal(200), ((ExecutionReport) history
                .getLatestExecutionReport(new org.marketcetera.trade.OrderID(
                        clOrderID1))).getLastQuantity());
        assertEquals(orderID1, ((ExecutionReport) history
                .getLatestExecutionReport(new org.marketcetera.trade.OrderID(
                        clOrderID1))).getOrderID().getValue());

        // execution reports come in out of order, use the one that has the
        // OrderID in it.
        history = createMessageHistory();
        history.addIncomingMessage(createReport(message2));
        history.addIncomingMessage(createReport(message1));
        assertEquals(new BigDecimal(200), ((ExecutionReport) history
                .getLatestExecutionReport(new org.marketcetera.trade.OrderID(
                        clOrderID1))).getLastQuantity());
        assertNotNull(((ExecutionReport) history
                .getLatestExecutionReport(new org.marketcetera.trade.OrderID(
                        clOrderID1))).getOrderID());

        // expecting 3, since it's later in order and later with sending time
        history = createMessageHistory();
        history.addIncomingMessage(createReport(message1));
        history.addIncomingMessage(createReport(message2));
        history.addIncomingMessage(createReport(message3));
        assertEquals(new BigDecimal(300), ((ExecutionReport) history
                .getLatestExecutionReport(new org.marketcetera.trade.OrderID(
                        clOrderID1))).getLastQuantity());
        assertEquals(orderID1, ((ExecutionReport) history
                .getLatestExecutionReport(new org.marketcetera.trade.OrderID(
                        clOrderID1))).getOrderID().getValue());

        // 3rd msg is later by time, but arrives first, so expect msg2 to come
        // through
        history = createMessageHistory();
        history.addIncomingMessage(createReport(message3));
        history.addIncomingMessage(createReport(message2));
        history.addIncomingMessage(createReport(message1));
        assertEquals(new BigDecimal(200), ((ExecutionReport) history
                .getLatestExecutionReport(new org.marketcetera.trade.OrderID(
                        clOrderID1))).getLastQuantity());
        assertEquals(orderID1, ((ExecutionReport) history
                .getLatestExecutionReport(new org.marketcetera.trade.OrderID(
                        clOrderID1))).getOrderID().getValue());

        // 3rd msg is later by time, but arrives first, so expect msg2 to come
        // through
        history = createMessageHistory();
        history.addIncomingMessage(createReport(message1));
        history.addIncomingMessage(createReport(message3));
        history.addIncomingMessage(createReport(message2));
        assertEquals(new BigDecimal(200), ((ExecutionReport) history
                .getLatestExecutionReport(new org.marketcetera.trade.OrderID(
                        clOrderID1))).getLastQuantity());
        assertEquals(orderID1, ((ExecutionReport) history
                .getLatestExecutionReport(new org.marketcetera.trade.OrderID(
                        clOrderID1))).getOrderID().getValue());

        // 3rd msg is later by time, but arrives first, so expect msg2 to come
        // through
        history = createMessageHistory();
        history.addIncomingMessage(createReport(message3));
        history.addIncomingMessage(createReport(message1));
        history.addIncomingMessage(createReport(message2));
        assertEquals(new BigDecimal(200), ((ExecutionReport) history
                .getLatestExecutionReport(new org.marketcetera.trade.OrderID(
                        clOrderID1))).getLastQuantity());
        assertEquals(orderID1, ((ExecutionReport) history
                .getLatestExecutionReport(new org.marketcetera.trade.OrderID(
                        clOrderID1))).getOrderID().getValue());
    }

    String[] messageStrings = {
            "8=FIX.4.29=14135=86=011=1171508063701-server02/127.0.0.114=017=ZZ-INTERNAL20=\u000031=032=038=1039=044=1054=155=R60=20070215-02:54:27150=0151=1010=237", //$NON-NLS-1$
            "8=FIX.4.29=20635=834=449=MRKTC-EXCH52=20070215-02:54:29.43056=sender-2026-ORS6=011=1171508063701-server02/127.0.0.114=017=1203720=331=032=037=732438=1039=044=1054=155=R60=20070215-02:54:29150=0151=1010=201", //$NON-NLS-1$
            "8=FIX.4.29=14335=86=011=1171508063702-server02/127.0.0.114=017=ZZ-INTERNAL20=\u000031=032=038=1239=044=10.154=155=R60=20070215-02:55:06150=0151=1210=081", //$NON-NLS-1$
            "8=FIX.4.29=20835=834=649=MRKTC-EXCH52=20070215-02:55:09.08456=sender-2026-ORS6=011=1171508063702-server02/127.0.0.114=017=1203820=331=032=037=732538=1239=044=10.154=155=R60=20070215-02:55:09150=0151=1210=054", //$NON-NLS-1$
            "8=FIX.4.29=13535=86=011=1171508063703-server02/127.0.0.114=017=ZZ-INTERNAL20=\u000031=032=038=2239=054=555=R60=20070215-02:55:27150=0151=2210=246", //$NON-NLS-1$
            "8=FIX.4.29=21535=834=749=MRKTC-EXCH52=20070215-02:55:29.37856=sender-2026-ORS6=10.111=1171508063702-server02/127.0.0.114=1217=1203920=331=10.132=1237=732538=1239=244=10.154=155=R60=20070215-02:55:29150=2151=010=151", //$NON-NLS-1$
            "8=FIX.4.29=20835=834=849=MRKTC-EXCH52=20070215-02:55:29.37956=sender-2026-ORS6=10.111=1171508063703-server02/127.0.0.114=1217=1204020=331=10.132=1237=732638=2239=154=555=R60=20070215-02:55:29150=1151=1010=099", //$NON-NLS-1$
            "8=FIX.4.29=20935=834=949=MRKTC-EXCH52=20070215-02:55:29.38056=sender-2026-ORS6=1011=1171508063701-server02/127.0.0.114=1017=1204120=331=1032=1037=732438=1039=244=1054=155=R60=20070215-02:55:29150=2151=010=105", //$NON-NLS-1$
            "8=FIX.4.29=23735=834=1049=MRKTC-EXCH52=20070215-02:55:29.38156=sender-2026-ORS6=10.0545454545454545454545454545454511=1171508063703-server02/127.0.0.114=2217=1204220=331=1032=1037=732638=2239=254=555=R60=20070215-02:55:29150=2151=010=085", //$NON-NLS-1$
            "8=FIX.4.29=14135=86=011=1171508063705-server02/127.0.0.114=017=ZZ-INTERNAL20=\u000031=032=038=1039=044=1054=155=T60=20070215-02:57:58150=0151=1010=250", //$NON-NLS-1$
            "8=FIX.4.29=20735=834=1649=MRKTC-EXCH52=20070215-02:58:00.93056=sender-2026-ORS6=011=1171508063705-server02/127.0.0.114=017=1204320=331=032=037=732738=1039=044=1054=155=T60=20070215-02:58:00150=0151=1010=250", //$NON-NLS-1$
            "8=FIX.4.29=20635=834=1749=MRKTC-EXCH52=20070215-02:58:09.39356=sender-2026-ORS6=011=1171508063705-server02/127.0.0.114=017=1204420=331=032=037=732738=1039=444=1054=155=T60=20070215-02:58:09150=4151=010=231" //$NON-NLS-1$
    };

    public void testStrandedOpenOrder() throws Exception {
    	TradeReportsHistory history = createMessageHistory();
        for (String aMessageString : messageStrings) {
            Message aMessage = new Message(aMessageString);
            history.addIncomingMessage(createReport(aMessage));
        }

        FilterList<ReportHolder> openOrdersList = history.getOpenOrdersList();
        assertEquals(0, openOrdersList.size());
    }

    public void testFirstReport() throws Exception {
        TradeReportsHistory history = createMessageHistory();
        history.addIncomingMessage(createReport(msgFactory.newExecutionReport(
                "1001", "1", "2001", OrdStatus.NEW, Side.BUY, new BigDecimal(
                        1000), new BigDecimal(789), null, null,
                BigDecimal.ZERO, BigDecimal.ZERO, new MSymbol("ASDF"), null)));
        // Only PENDING NEW or PENDING REPLACE should be considered the first
        // report
        assertNull(history.getFirstReport(new org.marketcetera.trade.OrderID(
                "1")));
        Message report = msgFactory.newExecutionReport("1001", "1", "2001",
                OrdStatus.PENDING_NEW, Side.BUY, new BigDecimal(1000),
                new BigDecimal(789), null, null, BigDecimal.ZERO,
                BigDecimal.ZERO, new MSymbol("ASDF"), null);
        history.addIncomingMessage(createReport(report));
        assertSame(report, history.getFirstReport(
                new org.marketcetera.trade.OrderID("1")).getMessage());
        history.addIncomingMessage(createReport(msgFactory.newExecutionReport(
                "1001", "1", "2001", OrdStatus.REPLACED, Side.BUY,
                new BigDecimal(1000), new BigDecimal(789), null, null,
                BigDecimal.ZERO, BigDecimal.ZERO, new MSymbol("ASDF"), null)));
        // first report should not change
        assertSame(report, history.getFirstReport(
                new org.marketcetera.trade.OrderID("1")).getMessage());
        history.addIncomingMessage(createReport(msgFactory.newExecutionReport(
                "1001", "1", "2001", OrdStatus.PENDING_REPLACE, Side.BUY,
                new BigDecimal(1000), new BigDecimal(789), null, null,
                BigDecimal.ZERO, BigDecimal.ZERO, new MSymbol("ASDF"), null)));
        // first report should not change
        assertSame(report, history.getFirstReport(
                new org.marketcetera.trade.OrderID("1")).getMessage());
        history.addIncomingMessage(createReport(msgFactory.newExecutionReport(
                "1001", "1", "2001", OrdStatus.PENDING_NEW, Side.BUY,
                new BigDecimal(1000), new BigDecimal(789), null, null,
                BigDecimal.ZERO, BigDecimal.ZERO, new MSymbol("ASDF"), null)));
        // first report should not change
        assertSame(report, history.getFirstReport(
                new org.marketcetera.trade.OrderID("1")).getMessage());
        history.addIncomingMessage(createReport(msgFactory.newExecutionReport(
                "1001", "1", "2001", OrdStatus.REJECTED, Side.BUY,
                new BigDecimal(1000), new BigDecimal(789), null, null,
                BigDecimal.ZERO, BigDecimal.ZERO, new MSymbol("ASDF"), null)));
        // first report should not change
        assertSame(report, history.getFirstReport(
                new org.marketcetera.trade.OrderID("1")).getMessage());
        history.addIncomingMessage(Factory.getInstance().createOrderCancelReject(msgFactory
                .newOrderCancelReject(new OrderID("1001"), new ClOrdID("1"),
                        new OrigClOrdID("1"), "ABC", new CxlRejReason(
                                CxlRejReason.TOO_LATE_TO_CANCEL)), new DestinationID("1")));
        // first report should not change
        assertSame(report, history.getFirstReport(
                new org.marketcetera.trade.OrderID("1")).getMessage());
    }

    public void testFirstReportReplace() throws Exception {
        TradeReportsHistory history = createMessageHistory();
        history.addIncomingMessage(createReport(msgFactory.newExecutionReport(
                "1001", "1", "2001", OrdStatus.REPLACED, Side.BUY,
                new BigDecimal(1000), new BigDecimal(789), null, null,
                BigDecimal.ZERO, BigDecimal.ZERO, new MSymbol("ASDF"), null)));
        // Only PENDING NEW or PENDING REPLACE should be considered the first
        // report
        assertNull(history.getFirstReport(new org.marketcetera.trade.OrderID(
                "1")));
        Message report = msgFactory.newExecutionReport("1001", "1", "2001",
                OrdStatus.PENDING_REPLACE, Side.BUY, new BigDecimal(1000),
                new BigDecimal(789), null, null, BigDecimal.ZERO,
                BigDecimal.ZERO, new MSymbol("ASDF"), null);
        history.addIncomingMessage(createReport(report));
        assertSame(report, history.getFirstReport(
                new org.marketcetera.trade.OrderID("1")).getMessage());
        history.addIncomingMessage(createReport(msgFactory.newExecutionReport(
                "1001", "1", "2001", OrdStatus.REPLACED, Side.BUY,
                new BigDecimal(1000), new BigDecimal(789), null, null,
                BigDecimal.ZERO, BigDecimal.ZERO, new MSymbol("ASDF"), null)));
        // first report should not change
        assertSame(report, history.getFirstReport(
                new org.marketcetera.trade.OrderID("1")).getMessage());
        history.addIncomingMessage(createReport(msgFactory.newExecutionReport(
                "1001", "1", "2001", OrdStatus.PENDING_NEW, Side.BUY,
                new BigDecimal(1000), new BigDecimal(789), null, null,
                BigDecimal.ZERO, BigDecimal.ZERO, new MSymbol("ASDF"), null)));
        // first report should not change
        assertSame(report, history.getFirstReport(
                new org.marketcetera.trade.OrderID("1")).getMessage());
        history.addIncomingMessage(createReport(msgFactory.newExecutionReport(
                "1001", "1", "2001", OrdStatus.REJECTED, Side.BUY,
                new BigDecimal(1000), new BigDecimal(789), null, null,
                BigDecimal.ZERO, BigDecimal.ZERO, new MSymbol("ASDF"), null)));
        // first report should not change
        assertSame(report, history.getFirstReport(
                new org.marketcetera.trade.OrderID("1")).getMessage());
        history.addIncomingMessage(Factory.getInstance().createOrderCancelReject(msgFactory
                .newOrderCancelReject(new OrderID("1001"), new ClOrdID("1"),
                        new OrigClOrdID("1"), "ABC", new CxlRejReason(
                                CxlRejReason.TOO_LATE_TO_CANCEL)), new DestinationID("1")));
        // first report should not change
        assertSame(report, history.getFirstReport(
                new org.marketcetera.trade.OrderID("1")).getMessage());
    }

    public void testVisitOpenExecReports() throws Exception {
        TradeReportsHistory history = createMessageHistory();
        history
                .addIncomingMessage(createReport(msgFactory
                        .newExecutionReport(
                                "1001", "1", "2001", OrdStatus.NEW, Side.BUY, new BigDecimal(1000), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                new BigDecimal(789), null, null,
                                BigDecimal.ZERO, BigDecimal.ZERO, new MSymbol(
                                        "ASDF"), null))); //$NON-NLS-1$
        history
                .addIncomingMessage(createReport(msgFactory
                        .newExecutionReport(
                                "1002", "2", "2002", OrdStatus.NEW, Side.BUY, new BigDecimal(1000), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                new BigDecimal(789), null, null,
                                BigDecimal.ZERO, BigDecimal.ZERO, new MSymbol(
                                        "LERA"), null))); //$NON-NLS-1$
        history
                .addIncomingMessage(createReport(msgFactory
                        .newExecutionReport(
                                "1003", "3", "2003", OrdStatus.NEW, Side.BUY, new BigDecimal(1000), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                new BigDecimal(789), null, null,
                                BigDecimal.ZERO, BigDecimal.ZERO, new MSymbol(
                                        "FRED"), null))); //$NON-NLS-1$

        final Vector<Message> visited = new Vector<quickfix.Message>();
        MessageVisitor visitor = new MessageVisitor() {
            public void visitOpenOrderExecutionReports(Message message) {
                visited.add(message);
            }
        };
        history.visitOpenOrdersExecutionReports(visitor);
        assertEquals(3, visited.size());
    }

    public void testOpenOrderDupes() throws Exception {
        String openOrder1String = "8=FIX.4.2\u00019=293\u000135=8\u000134=1624\u000149=VTRD1\u000152=20070926-18:23:56\u000156=VTrader\u000157=VTRD:TEST\u00011=VTRDT:VTRDM:VTRDS:VTRDC\u00016=0.0\u000111=bob95001\u000114=0\u000117=65002:1011722479.0:0.1\u000120=0\u000121=2\u000130=CBOE\u000137=RDC6688-20070926\u000138=10\u000139=0\u000140=2\u000144=7.0\u000154=1\u000155=MOT\u000159=0\u000160=20070926-18:23:56\u000177=O\u0001150=0\u0001151=10\u0001167=OPT\u0001200=200710\u0001201=1\u0001202=22.5\u000110=240\u0001"; //$NON-NLS-1$
        String openOrder2String = "8=FIX.4.2\u00019=293\u000135=8\u000134=1625\u000149=VTRD1\u000152=20070926-18:24:10\u000156=VTrader\u000157=VTRD:TEST\u00011=VTRDT:VTRDM:VTRDS:VTRDC\u00016=0.0\u000111=bob95003\u000114=0\u000117=65002:1011722485.0:0.1\u000120=0\u000121=2\u000130=CBOE\u000137=RDC6689-20070926\u000138=20\u000139=0\u000140=2\u000144=7.0\u000154=1\u000155=MOT\u000159=0\u000160=20070926-18:24:10\u000177=O\u0001150=0\u0001151=20\u0001167=OPT\u0001200=200710\u0001201=1\u0001202=22.5\u000110=225\u0001"; //$NON-NLS-1$
        String openOrder1PendingReplaceString = "8=FIX.4.2\u00019=305\u000135=8\u000134=1626\u000149=VTRD1\u000152=20070926-18:24:13\u000156=VTrader\u000157=VTRD:TEST\u00011=VTRDT:VTRDM:VTRDS:VTRDC\u00016=0.0\u000111=bob95005\u000114=0\u000117=65002:1011722479.37647492.0\u000120=0\u000121=2\u000130=CBOE\u000137=RDC6690-20070926\u000138=10\u000139=6\u000140=2\u000141=bob95001\u000144=7.0\u000154=1\u000155=MOT\u000159=0\u000160=20070926-18:24:13\u0001150=6\u0001151=10\u0001167=OPT\u0001200=200710\u0001201=1\u0001202=22.5\u000110=210\u0001"; //$NON-NLS-1$

        Message openOrder1 = new Message(openOrder1String, fixDD
                .getDictionary());
        Message openOrder2 = new Message(openOrder2String, fixDD
                .getDictionary());
        Message openOrder1PendingReplace = new Message(
                openOrder1PendingReplaceString, fixDD.getDictionary());

        TradeReportsHistory history = createMessageHistory();
        history.addIncomingMessage(createReport(openOrder1));
        history.addIncomingMessage(createReport(openOrder2));
        assertEquals(2, history.getOpenOrdersList().size());
        history.addIncomingMessage(createReport(openOrder1PendingReplace));
        assertEquals(2, history.getOpenOrdersList().size());
    }

    public void testChainReplaces() throws Exception {
        Message executionReportA = msgFactory
                .newExecutionReport(
                        "ORD1", "A", "EXEC1", OrdStatus.NEW, Side.BUY, BigDecimal.TEN, BigDecimal.ONE, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, new MSymbol("ABC"), null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        Message executionReportB = msgFactory
                .newExecutionReport(
                        "ORD2", "B", "EXEC2", OrdStatus.NEW, Side.BUY, BigDecimal.TEN, BigDecimal.ONE, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, new MSymbol("ABC"), null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        Message executionReportC = msgFactory
                .newExecutionReport(
                        "ORD1", "C", "EXEC3", OrdStatus.REPLACED, Side.BUY, BigDecimal.TEN, BigDecimal.TEN, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, new MSymbol("ABC"), null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        executionReportC.setField(new OrigClOrdID("A")); //$NON-NLS-1$
        Message executionReportD = msgFactory
                .newExecutionReport(
                        "ORD2", "D", "EXEC4", OrdStatus.REPLACED, Side.BUY, BigDecimal.TEN, BigDecimal.TEN, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, new MSymbol("ABC"), null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        executionReportD.setField(new OrigClOrdID("C")); //$NON-NLS-1$

        TradeReportsHistory history = createMessageHistory();
        history.addIncomingMessage(createReport(executionReportA));
        assertEquals(1, history.getOpenOrdersList().size());
        history.addIncomingMessage(createReport(executionReportB));
        assertEquals(2, history.getOpenOrdersList().size());
        history.addIncomingMessage(createReport(executionReportC));
        assertEquals(2, history.getOpenOrdersList().size());
        history.addIncomingMessage(createReport(executionReportD));
        assertEquals(2, history.getOpenOrdersList().size());

        assertEquals(
                "D", history.getOpenOrdersList().get(0).getMessage().getString(ClOrdID.FIELD)); //$NON-NLS-1$
        assertEquals(
                "B", history.getOpenOrdersList().get(1).getMessage().getString(ClOrdID.FIELD)); //$NON-NLS-1$
    }

    /**
     * This tests to see that we ignore LastShares and LastPx in ExecutionReport
     * messages with "finished" OrdStatus values. That is, PAX tells us that we
     * should only pay attention to LastShares and LastPx in ExecutionReports
     * with OrdStatus of PARTIALLY_FILLED, FILLED, or PENDING_CANCEL, so that's
     * what we do.
     * 
     * @throws FieldNotFound
     */
    public void testMerrillPAXIgnoreLastShares() throws Exception {
        Message executionReportA = msgFactory
                .newExecutionReport(
                        "ORD1", "A", "EXEC1", OrdStatus.CANCELED, Side.BUY, BigDecimal.TEN, BigDecimal.ONE, BigDecimal.TEN, BigDecimal.TEN, BigDecimal.ZERO, BigDecimal.ZERO, new MSymbol("ABC"), null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        Message executionReportB = msgFactory
                .newExecutionReport(
                        "ORD2", "B", "EXEC2", OrdStatus.FILLED, Side.BUY, BigDecimal.TEN, BigDecimal.ONE, BigDecimal.TEN, BigDecimal.TEN, BigDecimal.ZERO, BigDecimal.ZERO, new MSymbol("ABC"), null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

        TradeReportsHistory history = createMessageHistory();
        history.addIncomingMessage(createReport(executionReportA));
        assertEquals(0, history.getFillsList().size());
        history.addIncomingMessage(createReport(executionReportB));
        assertEquals(1, history.getFillsList().size());

    }
    
    public void testDuplicateReports() throws Exception {
    	ExecutionReport report1 = createReport(getTestableExecutionReport());
        ReportBaseImpl.assignReportID((ReportBaseImpl) report1, new ReportID(12));
        ExecutionReport report2 = createReport(getTestableExecutionReport());
        ReportBaseImpl.assignReportID((ReportBaseImpl) report2, new ReportID(22));
    	TradeReportsHistory history = createMessageHistory();
    	history.addIncomingMessage(report1);
    	assertEquals(1, history.size());
    	history.addIncomingMessage(report1);
    	assertEquals(1, history.size());
    	history.addIncomingMessage(report2);
    	assertEquals(2, history.size());
    	history.addIncomingMessage(report2);
    	assertEquals(2, history.size());
    	history.addIncomingMessage(report1);
    	assertEquals(2, history.size());
    }
    
    private Message getTestableExecutionReport() throws FieldNotFound {
        return msgFactory.newExecutionReport("456", "clorderid", "987", OrdStatus.PARTIALLY_FILLED, Side.BUY, new BigDecimal(1000), new BigDecimal("12.3"), new BigDecimal(500),
                        new BigDecimal("12.3"), new BigDecimal(500), new BigDecimal("12.3"), new MSymbol("IBM"), null);
    }

    private ExecutionReport createReport(Message message)
            throws MessageCreationException {
        return Factory.getInstance().createExecutionReport(message,
                new DestinationID("null"), Originator.Server);
    }

}
