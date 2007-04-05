package org.marketcetera.quickfix;

import junit.framework.Test;
import org.marketcetera.core.*;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.Group;
import quickfix.field.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashSet;

/**
 * @author Graham Miller
 * @version $Id$
 */
@ClassVersion("$Id$")
public class FIXMessageUtilTest extends FIXVersionedTestCase {
    public FIXMessageUtilTest(String inName, FIXVersion version) {
        super(inName, version);
    }

    public static Test suite() {
        HashSet<String> set = new HashSet<String>();
        set.add("testMarketDataRequst_ALL");
        set.add("testMDR_oneSymbol");
        set.add("testMDR_ManySymbols");
        return new FIXVersionTestSuite(FIXMessageUtilTest.class, FIXVersionTestSuite.ALL_VERSIONS,
                set, FIXVersionTestSuite.FIX42_PLUS_VERSIONS);
    }

    public void testNewLimitOrder() throws Exception {
        String orderID = "asdf";
        char side = Side.BUY;
        String  quantity = "200";
        String symbol = "IBM";
        String priceString = "123.45";
        char timeInForce = TimeInForce.DAY;
        Message aMessage = msgFactory.newLimitOrder(orderID, side, new BigDecimal(quantity),
                                                 new MSymbol(symbol), new BigDecimal(priceString), timeInForce, null);

        assertEquals(MsgType.ORDER_SINGLE, aMessage.getHeader().getString(MsgType.FIELD));
        assertEquals(OrdType.LIMIT, aMessage.getChar(OrdType.FIELD));
        assertEquals(priceString, aMessage.getString(Price.FIELD));
        assertEquals(orderID, aMessage.getString(ClOrdID.FIELD));
        assertEquals(symbol, aMessage.getString(Symbol.FIELD));
        assertEquals(side, aMessage.getChar(Side.FIELD));
        assertEquals(quantity, aMessage.getString(OrderQty.FIELD));
        assertEquals(timeInForce, aMessage.getChar(TimeInForce.FIELD));
    }

    /** want to test the case where price is specified or NULL (market orders) */
    public void testNewExecutionReport() throws Exception {
        String clOrderID = "asdf";
        String orderID = "bob";
        char side = Side.BUY;
        BigDecimal  quantity = new BigDecimal("200");
        String symbol = "IBM";
        BigDecimal price = new BigDecimal("123.45");
        Message aMessage = msgFactory.newExecutionReport(orderID, clOrderID, "execID",
                OrdStatus.NEW, side, quantity, price,
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, new MSymbol("IBM"),
                "accountName");

        assertEquals(MsgType.EXECUTION_REPORT, aMessage.getHeader().getString(MsgType.FIELD));
        assertEquals(price.toPlainString(), aMessage.getString(Price.FIELD));
        assertEquals(clOrderID, aMessage.getString(ClOrdID.FIELD));
        assertEquals(symbol, aMessage.getString(Symbol.FIELD));
        assertEquals(side, aMessage.getChar(Side.FIELD));
        assertEquals(quantity.toPlainString(), aMessage.getString(OrderQty.FIELD));
        assertEquals("accountName", aMessage.getString(Account.FIELD));

        // now send in a market order with null price
        aMessage = msgFactory.newExecutionReport(orderID, clOrderID, "execID",
                OrdStatus.NEW, side, quantity, null,
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, new MSymbol("IBM"), "accountName");
        assertFalse(aMessage.isSetField(Price.FIELD));

        // now send an order w/out account name
        try {
            aMessage = msgFactory.newExecutionReport(orderID, clOrderID, "execID",
                    OrdStatus.NEW, side, quantity, null,
                    BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, new MSymbol("IBM"), null);
            aMessage.getString(Account.FIELD);

        } catch (FieldNotFound ex) {
            // expected
        }
    }

    /** Creates a NewOrderSingle */
    public static Message createNOS(String symbol, double price, double qty, char side, FIXMessageFactory msgFactory)
    {
        Message newSingle = createNOSHelper(symbol, qty, side, new OrdType(OrdType.LIMIT), msgFactory);
        newSingle.setField(new Price(price));

        return newSingle;
    }

    /** This actually creats a NewOrderSingle with a *set* OrderID - which isn't how
     * it comes in through FIX connection originallY
     */
    public static Message createMarketNOS(String symbol, double qty, char side, FIXMessageFactory msgFactory)
    {
        return createNOSHelper(symbol, qty, side, new OrdType(OrdType.MARKET), msgFactory);
    }

    /** This needs to be modeled off {@link FIXMessageFactory#newOrderHelper} */
    public static Message createNOSHelper(String symbol, double qty, char side, OrdType ordType, FIXMessageFactory msgFactory)
    {
        long suffix = System.currentTimeMillis();
        Message newSingle = msgFactory.createNewMessage();
        newSingle.setField(new ClOrdID("123-"+suffix));
        newSingle.setField(new Symbol(symbol));
        newSingle.setField(new Side(side));
        newSingle.setField(new TransactTime());
        newSingle.setField(ordType);
        // technically, the OrderID is set by the exchange but for tests we'll set it too b/c OrderProgress expects it
        newSingle.setField(new OrderID("456"+suffix));
        newSingle.setField(new OrderQty(qty));
        newSingle.setField(new HandlInst(HandlInst.MANUAL_ORDER));
        newSingle.setField(new TimeInForce(TimeInForce.DAY));
        newSingle.setField(new Account("testAccount"));
        return newSingle;
    }

    /** Verifies that the message is a "virgin" executionReport (no half-fills, etc) for a given symbol/side */
    public static void verifyExecutionReport(Message inExecReport, String qty, String symbol, char side, BigDecimal leavesQty,
                                             BigDecimal lastQty, BigDecimal cumQty, BigDecimal lastPrice,
                                             BigDecimal avgPrice, char ordStatus, char execType, char execTransType,
                                             FIXMessageFactory msgFactory) throws Exception {
        assertEquals("quantity", qty, inExecReport.getString(OrderQty.FIELD));
        assertEquals("side", side, inExecReport.getChar(Side.FIELD));
        assertEquals("symbol", symbol, inExecReport.getString(Symbol.FIELD));
        if(!msgFactory.getBeginString().equals(FIXVersion.FIX40.toString())) {
            assertEquals("leavesQty", leavesQty, new BigDecimal(inExecReport.getString(LeavesQty.FIELD)));
        }
        assertEquals("lastQty",lastQty, new BigDecimal(inExecReport.getString(LastQty.FIELD)));
        assertEquals("cumQty", cumQty, new BigDecimal(inExecReport.getString(CumQty.FIELD)));
        assertEquals("ordStatus", ordStatus, inExecReport.getChar(OrdStatus.FIELD));
        if(!msgFactory.getBeginString().equals(FIXVersion.FIX40.toString())) {
            assertEquals("execType", execType, inExecReport.getChar(ExecType.FIELD));
        }
        assertNotNull(inExecReport.getString(TransactTime.FIELD));

        assertEquals("lastPrice", lastPrice, new BigDecimal(inExecReport.getString(LastPx.FIELD)));
        assertEquals("avgPrice", avgPrice, new BigDecimal(inExecReport.getString(AvgPx.FIELD)));
        if(version42orBelow(msgFactory)) {
            assertEquals("execTransType", execTransType, inExecReport.getChar(ExecTransType.FIELD));
        }
        // todo: switch this to use validate(inExecReport, true) to only validate the body
        FIXDataDictionaryManager.getDictionary().validate(inExecReport);
    }


    /** Useful for verifying execReports for new orders - assumes nothing is filled */
    public static void verifyExecutionReport(Message inExecReport, String qty, String symbol, char side,
                                             FIXMessageFactory msgFactory) throws Exception
    {
        verifyExecutionReport(inExecReport, qty, symbol, side, new BigDecimal(qty), BigDecimal.ZERO, BigDecimal.ZERO,
                BigDecimal.ZERO,BigDecimal.ZERO, OrdStatus.NEW, ExecType.NEW, ExecTransType.NEW, msgFactory);
    }


    public void testMarketDataRequst_ALL() throws Exception {
        Message req = msgFactory.newMarketDataRequest("toliID", new ArrayList<MSymbol>(0));
        assertEquals("sending 0 numSymbols doesn't work", 0, req.getInt(NoRelatedSym.FIELD));
        assertEquals("toliID", req.getString(MDReqID.FIELD));
        assertEquals(SubscriptionRequestType.SNAPSHOT, req.getChar(SubscriptionRequestType.FIELD));
        assertEquals(2, req.getInt(NoMDEntryTypes.FIELD));
        Group entryTypeGroup =  msgFactory.createGroup(MsgType.MARKET_DATA_REQUEST, NoMDEntryTypes.FIELD);
        req.getGroup(1, entryTypeGroup);
        assertEquals(MDEntryType.BID, entryTypeGroup.getChar(MDEntryType.FIELD));
        req.getGroup(2, entryTypeGroup);
        assertEquals(MDEntryType.OFFER, entryTypeGroup.getChar(MDEntryType.FIELD));
    }

    public void testMDR_oneSymbol() throws Exception {
        List<MSymbol> list = Arrays.asList(new MSymbol("TOLI"));
        Message req = msgFactory.newMarketDataRequest("toliID", list);
        assertEquals("sending 1 numSymbols doesn't work", 1, req.getInt(NoRelatedSym.FIELD));
        for(int i=0;i<list.size(); i++) {
            Group symbolGroup =  msgFactory.createGroup(MsgType.MARKET_DATA_REQUEST, NoRelatedSym.FIELD);
            req.getGroup(i+1, symbolGroup);
            assertEquals("quote for symbol["+i+"] is wrong", list.get(i).getFullSymbol(), symbolGroup.getString(Symbol.FIELD));
        }
    }

    public void testMDR_ManySymbols() throws Exception {
        List<MSymbol> list = Arrays.asList(new MSymbol("TOLI"), new MSymbol("GRAHAM"), new MSymbol("LENA"));
        Message req = msgFactory.newMarketDataRequest("toliID", list);
        assertEquals("sending 1 numSymbols doesn't work", list.size(), req.getInt(NoRelatedSym.FIELD));
        for(int i=0;i<list.size(); i++) {
            Group symbolGroup =  msgFactory.createGroup(MsgType.MARKET_DATA_REQUEST, NoRelatedSym.FIELD);
            req.getGroup(i+1, symbolGroup);
            assertEquals("quote for symbol["+i+"] is wrong", list.get(i).getFullSymbol(), symbolGroup.getString(Symbol.FIELD));
        }
    }

    /** Takes 2 messages A and B (outgoing and existing), and tries to extract required fields
     * necessary in A from B
     * @throws Exception
     */
    public void testFillFieldsFromExistingMessage() throws Exception {
        Message buy = createNOS("GAP", 23.45, 2385, Side.BUY, msgFactory);
        buy.removeField(Side.FIELD);
        if(!msgFactory.getBeginString().equals(FIXVersion.FIX40.toString())) {
            buy.setString(LeavesQty.FIELD, "33");
        }
        buy.setChar(ExecTransType.FIELD, ExecTransType.NEW);
        buy.setChar(OrdStatus.FIELD, OrdStatus.NEW);
        buy.setString(ClOrdID.FIELD, "someClOrd");
        buy.setString(ExecID.FIELD, "anExecID");

        Message execReport = new Message();
        execReport.getHeader().setString(MsgType.FIELD, MsgType.EXECUTION_REPORT);
        execReport.setString(Text.FIELD, "dummyMessage");

        FIXMessageUtil.fillFieldsFromExistingMessage(execReport, buy);
        assertFalse(execReport.isSetField(Side.FIELD));
        // no LeavesQty in fix40
        if(!msgFactory.getBeginString().equals(FIXVersion.FIX40.toString())) {
            assertEquals("33", execReport.getString(LeavesQty.FIELD));
        }
        assertEquals("GAP", execReport.getString(Symbol.FIELD));
        assertEquals("dummyMessage", execReport.getString(Text.FIELD));
        if(version42orBelow(msgFactory)) {
            assertTrue(execReport.isSetField(ExecTransType.FIELD));
        }
        assertTrue(execReport.isSetField(OrdStatus.FIELD));
        assertFalse("clOrdID is not required so should not be transferred", execReport.isSetField(ClOrdID.FIELD));
        assertTrue(execReport.isSetField(OrderID.FIELD));
        assertTrue(execReport.isSetField(ExecID.FIELD));
    }
}