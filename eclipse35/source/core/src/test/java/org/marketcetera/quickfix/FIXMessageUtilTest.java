package org.marketcetera.quickfix;

import static org.marketcetera.quickfix.Messages.CANNOT_CREATE_FIX_FIELD;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import junit.framework.Test;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.CoreException;
import org.marketcetera.core.ExpectedTestFailure;
import org.marketcetera.core.FIXVersionTestSuite;
import org.marketcetera.core.FIXVersionedTestCase;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.trade.MSymbol;

import quickfix.DataDictionary;
import quickfix.FieldNotFound;
import quickfix.Group;
import quickfix.InvalidMessage;
import quickfix.Message;
import quickfix.StringField;
import quickfix.field.Account;
import quickfix.field.AvgPx;
import quickfix.field.CFICode;
import quickfix.field.ClOrdID;
import quickfix.field.CumQty;
import quickfix.field.EncodedText;
import quickfix.field.EncodedTextLen;
import quickfix.field.ExecID;
import quickfix.field.ExecTransType;
import quickfix.field.ExecType;
import quickfix.field.HandlInst;
import quickfix.field.LastPx;
import quickfix.field.LastQty;
import quickfix.field.LeavesQty;
import quickfix.field.MDEntryPx;
import quickfix.field.MDEntrySize;
import quickfix.field.MDEntryType;
import quickfix.field.MDReqID;
import quickfix.field.MaturityMonthYear;
import quickfix.field.MsgType;
import quickfix.field.NoMDEntries;
import quickfix.field.NoMDEntryTypes;
import quickfix.field.NoRelatedSym;
import quickfix.field.OrdStatus;
import quickfix.field.OrdType;
import quickfix.field.OrderID;
import quickfix.field.OrderQty;
import quickfix.field.Price;
import quickfix.field.PutOrCall;
import quickfix.field.SecurityExchange;
import quickfix.field.SecurityType;
import quickfix.field.Side;
import quickfix.field.StrikePrice;
import quickfix.field.SubscriptionRequestType;
import quickfix.field.Symbol;
import quickfix.field.SymbolSfx;
import quickfix.field.Text;
import quickfix.field.TimeInForce;
import quickfix.field.TransactTime;
/* $License$ */
/**
 * @author Graham Miller
 * @version $Id$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class FIXMessageUtilTest extends FIXVersionedTestCase {
	/// this is to prevent errors on windows where orderIDs get same system.currentMillis
	// b/c windows doesn't have fine-grained enough system clock
	private static long nosSuffixCounter = 1;
    public FIXMessageUtilTest(String inName, FIXVersion version) {
        super(inName, version);
    }

    public static Test suite() {
        LoggerConfiguration.logSetup();
/*
        MarketceteraTestSuite suite = new MarketceteraTestSuite();
        suite.addTest(new FIXMessageUtilTest("testFillFieldsFromExistingMessage", FIXVersion.FIX40));
        suite.addTest(new FIXMessageUtilTest("testFillFieldsFromExistingMessage", FIXVersion.FIX41));
        suite.addTest(new FIXMessageUtilTest("testFillFieldsFromExistingMessage", FIXVersion.FIX42));
        return suite;
/*/
        return new FIXVersionTestSuite(FIXMessageUtilTest.class, FIXVersionTestSuite.ALL_VERSIONS,
                new HashSet<String>(Arrays.asList("testMarketDataRequst_ALL", "testMDR_oneSymbol", "testMDR_ManySymbols")), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                FIXVersionTestSuite.FIX42_PLUS_VERSIONS);
    }

    public void testNewLimitOrder() throws Exception {
        String orderID = "asdf"; //$NON-NLS-1$
        char side = Side.BUY;
        String  quantity = "200"; //$NON-NLS-1$
        String symbol = "IBM"; //$NON-NLS-1$
        String priceString = "123.45"; //$NON-NLS-1$
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
        String clOrderID = "asdf"; //$NON-NLS-1$
        String orderID = "bob"; //$NON-NLS-1$
        char side = Side.BUY;
        BigDecimal quantity = new BigDecimal("200"); //$NON-NLS-1$
        String symbol = "IBM"; //$NON-NLS-1$
        BigDecimal price = new BigDecimal("123.45"); //$NON-NLS-1$
        Message aMessage = msgFactory.newExecutionReport(orderID, clOrderID, "execID", //$NON-NLS-1$
                OrdStatus.NEW, side, quantity, price,
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, new MSymbol("IBM"), //$NON-NLS-1$
                "accountName"); //$NON-NLS-1$

        assertEquals(MsgType.EXECUTION_REPORT, aMessage.getHeader().getString(MsgType.FIELD));
        assertEquals(price, aMessage.getDecimal(Price.FIELD));
        assertEquals(clOrderID, aMessage.getString(ClOrdID.FIELD));
        assertEquals(symbol, aMessage.getString(Symbol.FIELD));
        assertFalse(aMessage.isSetField(SecurityType.FIELD));
        assertEquals(side, aMessage.getChar(Side.FIELD));
        assertEquals(quantity, aMessage.getDecimal(OrderQty.FIELD));
        assertEquals("accountName", aMessage.getString(Account.FIELD)); //$NON-NLS-1$

        // now send in a market order with null price
        aMessage = msgFactory.newExecutionReport(orderID, clOrderID, "execID", //$NON-NLS-1$
                OrdStatus.NEW, side, quantity, null,
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                BigDecimal.ZERO, new MSymbol("IBM",//$NON-NLS-1$
                org.marketcetera.trade.SecurityType.Option), "accountName"); //$NON-NLS-1$
        assertFalse(aMessage.isSetField(Price.FIELD));
        assertEquals(symbol, aMessage.getString(Symbol.FIELD));
        assertEquals(org.marketcetera.trade.SecurityType.Option.getFIXValue(),
                aMessage.getString(SecurityType.FIELD));

        // now send an order w/out account name
        try {
            aMessage = msgFactory.newExecutionReport(orderID, clOrderID, "execID", //$NON-NLS-1$
                    OrdStatus.NEW, side, quantity, null,
                    BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, new MSymbol("IBM"), null); //$NON-NLS-1$
            aMessage.getString(Account.FIELD);

        } catch (FieldNotFound ex) {
            // expected
        }
    }

    /** Creates a NewOrderSingle */
    public static Message createNOS(String symbol, BigDecimal price, BigDecimal qty, char side, FIXMessageFactory msgFactory)
    {
        Message newSingle = createNOSHelper(symbol, qty, side, new OrdType(OrdType.LIMIT), msgFactory);
        newSingle.setField(new Price(price));

        return newSingle;
    }

    public static Message createOptionNOS(String optionRoot, String optionContractSpecifier,
                                          String maturityMonthYear, BigDecimal strikePrice,
                                          int putOrCall, BigDecimal price, BigDecimal qty,
                                          char side, FIXMessageFactory msgFactory)
    {
        String optionContractSymbol = optionRoot + "+" + optionContractSpecifier; //$NON-NLS-1$
        Message newSingle = createNOS(optionRoot, price, qty, side, msgFactory);
        newSingle.setField(new Symbol(optionContractSymbol));
        newSingle.setField(new MaturityMonthYear(maturityMonthYear));
        newSingle.setField(new StrikePrice(strikePrice));
        newSingle.setField(new PutOrCall(putOrCall));

        return newSingle;
    }

    /** This actually creats a NewOrderSingle with a *set* OrderID - which isn't how
     * it comes in through FIX connection originallY
     */
    public static Message createMarketNOS(String symbol, BigDecimal qty, char side, FIXMessageFactory msgFactory)
    {
        return createNOSHelper(symbol, qty, side, new OrdType(OrdType.MARKET), msgFactory);
    }

    /** This needs to be modeled off {@link FIXMessageFactory#newOrderHelper} */
    public static Message createNOSHelper(String symbol, BigDecimal qty, char side, OrdType ordType, FIXMessageFactory msgFactory)
    {
        long suffix = System.currentTimeMillis();
        Message newSingle = msgFactory.newBasicOrder();
        newSingle.setField(new ClOrdID("123-"+(++nosSuffixCounter)+"-"+suffix)); //$NON-NLS-1$ //$NON-NLS-2$
        newSingle.setField(new Symbol(symbol));
        newSingle.setField(new Side(side));
        newSingle.setField(new TransactTime(new Date()));
        newSingle.setField(ordType);
        // technically, the OrderID is set by the exchange but for tests we'll set it too b/c OrderProgress expects it
        newSingle.setField(new OrderID("456"+suffix)); //$NON-NLS-1$
        newSingle.setField(new OrderQty(qty));
        newSingle.setField(new HandlInst(HandlInst.AUTOMATED_EXECUTION_ORDER_PRIVATE));
        newSingle.setField(new TimeInForce(TimeInForce.DAY));
        newSingle.setField(new Account("testAccount")); //$NON-NLS-1$
        return newSingle;
    }

    /** Verifies that the message is a "virgin" executionReport (no half-fills, etc) for a given symbol/side */
    public static void verifyExecutionReport(Message inExecReport, String qty, String symbol, char side, BigDecimal leavesQty,
                                             BigDecimal lastQty, BigDecimal cumQty, BigDecimal lastPrice,
                                             BigDecimal avgPrice, char ordStatus, char execType, char execTransType,
                                             FIXMessageFactory msgFactory, FIXDataDictionary fixDD) throws Exception {
        try {
            assertEquals("quantity", qty, inExecReport.getString(OrderQty.FIELD)); //$NON-NLS-1$
            assertEquals("side", side, inExecReport.getChar(Side.FIELD)); //$NON-NLS-1$
            assertEquals("symbol", symbol, inExecReport.getString(Symbol.FIELD)); //$NON-NLS-1$
            if(!msgFactory.getBeginString().equals(FIXVersion.FIX40.toString())) {
                assertEquals("leavesQty", leavesQty, inExecReport.getDecimal(LeavesQty.FIELD)); //$NON-NLS-1$
            }
            if (lastQty != null) {
            assertEquals("lastQty",lastQty, inExecReport.getDecimal(LastQty.FIELD)); //$NON-NLS-1$
            }
            if (lastPrice != null) {
                assertEquals("lastPrice", lastPrice, inExecReport.getDecimal(LastPx.FIELD)); //$NON-NLS-1$
            }
            assertEquals("cumQty", cumQty, inExecReport.getDecimal(CumQty.FIELD)); //$NON-NLS-1$
            assertEquals("ordStatus", ordStatus, inExecReport.getChar(OrdStatus.FIELD)); //$NON-NLS-1$
            if(!msgFactory.getBeginString().equals(FIXVersion.FIX40.toString())) {
                assertEquals("execType", execType, inExecReport.getChar(ExecType.FIELD)); //$NON-NLS-1$
            }
            assertNotNull(inExecReport.getString(TransactTime.FIELD));

            assertEquals("avgPrice", avgPrice, inExecReport.getDecimal(AvgPx.FIELD)); //$NON-NLS-1$
            if(version42orBelow(msgFactory)) {
                assertEquals("execTransType", execTransType, inExecReport.getChar(ExecTransType.FIELD)); //$NON-NLS-1$
            }
            fixDD.getDictionary().validate(inExecReport, true);
        } catch(FieldNotFound fnf) {
            fail("Field "+fixDD.getHumanFieldName(fnf.field) + " not found in message: "+inExecReport); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }


    /** Useful for verifying execReports for new orders - assumes nothing is filled */
    public static void verifyExecutionReport(Message inExecReport, String qty, String symbol, char side,
                                             FIXMessageFactory msgFactory, FIXDataDictionary fixDD) throws Exception
    {
        verifyExecutionReport(inExecReport, qty, symbol, side, new BigDecimal(qty), BigDecimal.ZERO, BigDecimal.ZERO,
                BigDecimal.ZERO,BigDecimal.ZERO, OrdStatus.PENDING_NEW, ExecType.PENDING_NEW, ExecTransType.NEW, msgFactory, fixDD);
    }


    public void testMarketDataRequst_ALL() throws Exception {
        Message req = msgFactory.newMarketDataRequest("toliID", new ArrayList<MSymbol>(0)); //$NON-NLS-1$
        assertEquals("sending 0 numSymbols doesn't work", 0, req.getInt(NoRelatedSym.FIELD)); //$NON-NLS-1$
        assertEquals("toliID", req.getString(MDReqID.FIELD)); //$NON-NLS-1$
        assertEquals(SubscriptionRequestType.SNAPSHOT, req.getChar(SubscriptionRequestType.FIELD));
        assertEquals(2, req.getInt(NoMDEntryTypes.FIELD));
        Group entryTypeGroup =  msgFactory.createGroup(MsgType.MARKET_DATA_REQUEST, NoMDEntryTypes.FIELD);
        req.getGroup(1, entryTypeGroup);
        assertEquals(MDEntryType.BID, entryTypeGroup.getChar(MDEntryType.FIELD));
        req.getGroup(2, entryTypeGroup);
        assertEquals(MDEntryType.OFFER, entryTypeGroup.getChar(MDEntryType.FIELD));
    }
    
    public void testSecurityListRequest()
        throws Exception
    {
        FIXVersion thisVersion = FIXVersion.getFIXVersion(msgFactory.getBeginString());
        if(thisVersion.equals(FIXVersion.FIX43) ||
           thisVersion.equals(FIXVersion.FIX44)) {
            Message request = msgFactory.newSecurityListRequest("reqID"); //$NON-NLS-1$
            String messageType = request.getHeader().getString(MsgType.FIELD);
            assertEquals(MsgType.SECURITY_LIST_REQUEST,
                         messageType);
            fixDD.getDictionary().validate(request,
                                           true);
        } else {
            new ExpectedTestFailure(IllegalStateException.class) {
                @Override
                protected void execute()
                        throws Throwable
                {
                    msgFactory.newSecurityListRequest("reqID"); //$NON-NLS-1$
                }                
            }.run();
        }
    }

    public void testDerivativeSecurityListRequest()
        throws Exception
    {
        FIXVersion thisVersion = FIXVersion.getFIXVersion(msgFactory.getBeginString());
        if(thisVersion.equals(FIXVersion.FIX43) ||
           thisVersion.equals(FIXVersion.FIX44)) {
            Message request = msgFactory.newDerivativeSecurityListRequest("reqID"); //$NON-NLS-1$
            String messageType = request.getHeader().getString(MsgType.FIELD);
            assertEquals(MsgType.DERIVATIVE_SECURITY_LIST_REQUEST,
                         messageType);
            fixDD.getDictionary().validate(request,
                                           true);
        } else {
            new ExpectedTestFailure(IllegalStateException.class) {
                @Override
                protected void execute()
                        throws Throwable
                {
                    msgFactory.newDerivativeSecurityListRequest("reqID"); //$NON-NLS-1$
                }                
            }.run();
        }
    }

    public void testMDR_oneSymbol()
        throws Exception
    {
        List<MSymbol> list = Arrays.asList(new MSymbol("TOLI"));
        verifyMDR(msgFactory.newMarketDataRequest("toliID",
                                                  list),
                  list,
                  null);
        verifyMDR(msgFactory.newMarketDataRequest("toliID",
                                                  list,
                                                  "Q"),
                  list,
                  "Q");
    }

    public void testMDR_ManySymbols()
        throws Exception
    {
        List<MSymbol> list = Arrays.asList(new MSymbol("TOLI"),
                                           new MSymbol("GRAHAM"),
                                           new MSymbol("LENA"),
                                           new MSymbol("COLIN"));
        verifyMDR(msgFactory.newMarketDataRequest("toliID",
                                                  list),
                  list,
                  null);
        verifyMDR(msgFactory.newMarketDataRequest("toliID",
                                                  list,
                                                  "Q"),
                  list,
                  "Q");
    }

    /**
     * Verifies that the given <code>Message</code> represents the given symbols and exchange.
     *
     * @param inActualMessage a <code>Message</code> containing the message to test
     * @param inExpectedSymbols a <code>&lt;MSymbol&gt;</code> value containing the expected symbols
     * @param inExpectedExchange a <code>String</code> value containing the expected exchange or null for no exchange 
     * @throws Exception if an error occurs
     */
    private void verifyMDR(Message inActualMessage,
                           List<MSymbol> inExpectedSymbols,
                           String inExpectedExchange)
        throws Exception
    {
        assertEquals(inExpectedSymbols.size(),
                     inActualMessage.getInt(NoRelatedSym.FIELD));
        for(int i=0;i<inExpectedSymbols.size(); i++) {
            final Group symbolGroup = msgFactory.createGroup(MsgType.MARKET_DATA_REQUEST,
                                                             NoRelatedSym.FIELD);
            inActualMessage.getGroup(i+1,
                                     symbolGroup);
            assertEquals("quote for symbol["+i+"] is wrong",
                         inExpectedSymbols.get(i).getFullSymbol(),
                         symbolGroup.getString(Symbol.FIELD));
            if(inExpectedExchange == null ||
               inExpectedExchange.isEmpty()) {
                new ExpectedFailure<FieldNotFound>(null) {
                    protected void run()
                    throws Exception
                    {
                        symbolGroup.getString(SecurityExchange.FIELD);
                    }
                };
            } else {
                assertEquals(inExpectedExchange,
                             symbolGroup.getString(SecurityExchange.FIELD));
            }
        }
    }

    /** Takes 2 messages A and B (outgoing and existing), and tries to extract required fields
     * necessary in A from B
     * @throws Exception
     */
    public void testFillFieldsFromExistingMessage() throws Exception {
        Message buy = createNOS("GAP", new BigDecimal("23.45"), new BigDecimal("2385"), Side.BUY, msgFactory); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        buy.removeField(Side.FIELD);
        if(!msgFactory.getBeginString().equals(FIXVersion.FIX40.toString())) {
            buy.setString(LeavesQty.FIELD, "33"); //$NON-NLS-1$
        }
        buy.setChar(ExecTransType.FIELD, ExecTransType.NEW);
        buy.setChar(OrdStatus.FIELD, OrdStatus.NEW);
        buy.setString(ClOrdID.FIELD, "someClOrd"); //$NON-NLS-1$
        buy.setString(ExecID.FIELD, "anExecID"); //$NON-NLS-1$

        Message execReport = new Message();
        execReport.getHeader().setString(MsgType.FIELD, MsgType.EXECUTION_REPORT);
        execReport.setString(Text.FIELD, "dummyMessage"); //$NON-NLS-1$

        FIXMessageUtil.fillFieldsFromExistingMessage(execReport, buy);
        assertFalse(execReport.isSetField(Side.FIELD));
        // no LeavesQty in fix40
        if(!msgFactory.getBeginString().equals(FIXVersion.FIX40.toString())) {
            assertEquals("33", execReport.getString(LeavesQty.FIELD)); //$NON-NLS-1$
        }
        assertEquals("GAP", execReport.getString(Symbol.FIELD)); //$NON-NLS-1$
        assertEquals("dummyMessage", execReport.getString(Text.FIELD)); //$NON-NLS-1$
        if(version42orBelow(msgFactory)) {
            assertTrue(execReport.isSetField(ExecTransType.FIELD));
        }
        assertTrue(execReport.isSetField(OrdStatus.FIELD));
        assertFalse("clOrdID is not required so should not be transferred", execReport.isSetField(ClOrdID.FIELD)); //$NON-NLS-1$
        assertTrue(execReport.isSetField(OrderID.FIELD));
        assertTrue(execReport.isSetField(ExecID.FIELD));
    }

    public void testFillFieldsFromExistingMessage_ExtraInvalidFields() throws Exception {
        Message buy = createNOS("GAP", new BigDecimal("23.45"), new BigDecimal("2385"), Side.BUY, msgFactory); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        buy.removeField(Side.FIELD);
        if(!msgFactory.getBeginString().equals(FIXVersion.FIX40.toString())) {
            buy.setString(LeavesQty.FIELD, "33"); //$NON-NLS-1$
        }
        buy.setChar(ExecTransType.FIELD, ExecTransType.NEW);
        buy.setChar(OrdStatus.FIELD, OrdStatus.NEW);
        buy.setString(ClOrdID.FIELD, "someClOrd"); //$NON-NLS-1$
        buy.setString(ExecID.FIELD, "anExecID"); //$NON-NLS-1$
        buy.setField(new HandlInst(HandlInst.AUTOMATED_EXECUTION_ORDER_PUBLIC));
        buy.setString(1900, "bogusField"); //$NON-NLS-1$
        buy.setField(new SymbolSfx(SymbolSfx.WHEN_ISSUED));

        Message execReport = msgFactory.newExecutionReport("orderID", "clOrderID", "1234", OrdStatus.CANCELED, Side.BUY,  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                new BigDecimal(2385), new BigDecimal("23.45"), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, //$NON-NLS-1$
                new MSymbol("GAP"), "account"); //$NON-NLS-1$ //$NON-NLS-2$
        execReport.setString(Text.FIELD, "dummyMessage"); //$NON-NLS-1$

        FIXMessageUtil.fillFieldsFromExistingMessage(execReport, buy, false);
        fixDD.getDictionary().validate(execReport, true);
        assertFalse(execReport.isSetField(1900));
        assertEquals(SymbolSfx.WHEN_ISSUED, execReport.getString(SymbolSfx.FIELD));
    }

    public void testGetTextOrEncodedText() throws InvalidMessage {
    	{
            Message buy = createNOS("GAP", new BigDecimal("23.45"), new BigDecimal("2385"), Side.BUY, msgFactory); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	        String unencodedMessage = "some unencoded message text"; //$NON-NLS-1$
	        buy.setField(new Text(unencodedMessage));
	        Message copy = new Message(buy.toString());
	        assertEquals(unencodedMessage, FIXMessageUtil.getTextOrEncodedText(copy, "none")); //$NON-NLS-1$
    	}
    	{
            Message buy = createNOS("GAP", new BigDecimal("23.45"), new BigDecimal("2385"), Side.BUY, msgFactory); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	        String encodedMessage = "some encoded message text"; //$NON-NLS-1$
	        buy.setField(new EncodedTextLen(encodedMessage.length()));
	        buy.setField(new EncodedText(encodedMessage));
	        Message copy = new Message(buy.toString());
	        assertEquals(encodedMessage, FIXMessageUtil.getTextOrEncodedText(copy, "none")); //$NON-NLS-1$
    	}
    }
    
    public void testGetCorrelationField() throws Exception {
		String requestString = "REQUEST"; //$NON-NLS-1$
    	Field[] fields = MsgType.class.getDeclaredFields();
    	for (Field field : fields) {
			String fieldName = field.getName();
			if (field.getType().equals(String.class)
					&& !fieldName.equals("TEST_REQUEST")	// omit session-level message //$NON-NLS-1$
					&& !fieldName.equals("RESEND_REQUEST")	// omit session-level message //$NON-NLS-1$
					&& !fieldName.equals("BID_REQUEST")		// omit  //$NON-NLS-1$
					&& !fieldName.startsWith("ORDER_")		// omit order-related messages //$NON-NLS-1$
					&& !fieldName.startsWith("CROSS_ORDER_")// omit order-related messages //$NON-NLS-1$
					&& !fieldName.endsWith("_ACK")			// omit "ack" messages //$NON-NLS-1$
					&& !fieldName.startsWith("LIST_")		// omit list-order-related messages //$NON-NLS-1$
					&& (fieldName.startsWith(requestString) 
					|| fieldName.endsWith("REQUEST"))) //$NON-NLS-1$
			{
				String msgTypeValue = (String) field.get(null);
				DataDictionary dictionary = fixDD.getDictionary();
				if (dictionary.isMsgType(msgTypeValue)){
					StringField correlationField = FIXMessageUtil.getCorrelationField(fixVersion, msgTypeValue);
					assertTrue("Couldn't find correlation for "+fieldName, correlationField!=null); //$NON-NLS-1$
					assertTrue(correlationField.getField()+" is not a valid value for "+fieldName+" in "+this.fixVersion, //$NON-NLS-1$ //$NON-NLS-2$
							dictionary.isMsgField(msgTypeValue, correlationField.getField()));
				}
			}
		}
    }
    
    /**
     * Test that trailing zeroes are preserved in decimal fields of QuickFIX messages
     * @throws InvalidMessage
     * @throws FieldNotFound
     */
    public void testTrailingZeroesAssumption() throws InvalidMessage, FieldNotFound
    {
    	String noZeroes = "1.111"; //$NON-NLS-1$
    	String twoZeroes = "1.100"; //$NON-NLS-1$
    	String aLotOfZeroes = "1.1000000"; //$NON-NLS-1$
    	String separator = "\u0001"; //$NON-NLS-1$

    	Message execReport = msgFactory.createMessage(MsgType.EXECUTION_REPORT);
    	execReport.setField(new OrderID("1")); //$NON-NLS-1$
    	execReport.setField(new ExecID("2")); //$NON-NLS-1$
    	execReport.setField(new ExecTransType(ExecTransType.NEW));
    	execReport.setField(new OrdStatus(OrdStatus.NEW));
    	execReport.setField(new Symbol("A")); //$NON-NLS-1$
    	execReport.setField(new Side(Side.BUY));
		execReport.setField(new OrderQty(new BigDecimal(noZeroes))); 
		execReport.setField(new CumQty(new BigDecimal(twoZeroes))); 
		execReport.setField(new AvgPx(new BigDecimal(aLotOfZeroes))); 

    	String execReportString = execReport.toString();
    	assertTrue(execReportString.contains(separator+OrderQty.FIELD+"="+noZeroes+separator)); //$NON-NLS-1$
    	assertTrue(execReportString.contains(separator+CumQty.FIELD+"="+twoZeroes+separator)); //$NON-NLS-1$
    	assertTrue(execReportString.contains(separator+AvgPx.FIELD+"="+aLotOfZeroes+separator)); //$NON-NLS-1$
    	
    	Message reconstituted = new Message(execReportString);
    	assertEquals(noZeroes, reconstituted.getString(OrderQty.FIELD));
    	assertEquals(twoZeroes, reconstituted.getString(CumQty.FIELD));
    	assertEquals(aLotOfZeroes, reconstituted.getString(AvgPx.FIELD));
    }
    
    public void testMergeMarketDataMessages() throws Exception {
    	FIXMessageFactory messageFactory = FIXVersion.FIX44.getMessageFactory();
    	Message marketDataSnapshotFullRefresh = messageFactory.createMessage(MsgType.MARKET_DATA_SNAPSHOT_FULL_REFRESH);
    	marketDataSnapshotFullRefresh.setField(new Symbol("IBM")); //$NON-NLS-1$
    	Group group;
		group = messageFactory.createGroup(MsgType.MARKET_DATA_SNAPSHOT_FULL_REFRESH, NoMDEntries.FIELD);
    	group.setField(new MDEntryType(MDEntryType.BID));
    	group.setField(new MDEntryPx(1234));
    	marketDataSnapshotFullRefresh.addGroup(group);
		group = messageFactory.createGroup(MsgType.MARKET_DATA_SNAPSHOT_FULL_REFRESH, NoMDEntries.FIELD);
    	group.setField(new MDEntryType(MDEntryType.OFFER));
    	group.setField(new MDEntryPx(1236));
    	marketDataSnapshotFullRefresh.addGroup(group);
    	
    	Message marketDataIncrementalRefresh = messageFactory.createMessage(MsgType.MARKET_DATA_INCREMENTAL_REFRESH);
		group = messageFactory.createGroup(MsgType.MARKET_DATA_INCREMENTAL_REFRESH, NoMDEntries.FIELD);
    	group.setField(new MDEntryType(MDEntryType.TRADE));
    	group.setField(new MDEntryPx(1239));
    	group.setField(new MDEntrySize(4000));
    	marketDataIncrementalRefresh.addGroup(group);

    	assertEquals(2,marketDataSnapshotFullRefresh.getInt(NoMDEntries.FIELD));
    	assertEquals(1,marketDataIncrementalRefresh.getInt(NoMDEntries.FIELD));
    	FIXMessageUtil.mergeMarketDataMessages(marketDataSnapshotFullRefresh, marketDataIncrementalRefresh, messageFactory);
    	
    	assertEquals(3,marketDataSnapshotFullRefresh.getInt(NoMDEntries.FIELD));
    	assertEquals(1,marketDataIncrementalRefresh.getInt(NoMDEntries.FIELD));

    	
    	group = messageFactory.createGroup(MsgType.MARKET_DATA_SNAPSHOT_FULL_REFRESH, NoMDEntries.FIELD);
    	Group bidGroup = null;
    	Group offerGroup = null;
    	Group tradeGroup = null;
		for (int i = 1; i <= 3; i++){
			group = messageFactory.createGroup(MsgType.MARKET_DATA_SNAPSHOT_FULL_REFRESH, NoMDEntries.FIELD);
    		marketDataSnapshotFullRefresh.getGroup(i, group);
    		if (MDEntryType.BID == group.getChar(MDEntryType.FIELD)){
    			bidGroup = group;
    		}
    		if (MDEntryType.OFFER == group.getChar(MDEntryType.FIELD)){
    			offerGroup = group;
    		}
    		if (MDEntryType.TRADE == group.getChar(MDEntryType.FIELD)){
    			tradeGroup = group;
    		}
    	}
    	assertNotNull(bidGroup);
    	assertNotNull(offerGroup);
    	assertNotNull(tradeGroup);
    	assertEquals(1234, bidGroup.getInt(MDEntryPx.FIELD));
    	assertEquals(1236, offerGroup.getInt(MDEntryPx.FIELD));
    	assertEquals(1239, tradeGroup.getInt(MDEntryPx.FIELD));
    	assertEquals(4000, tradeGroup.getInt(MDEntrySize.FIELD));
    }

    public void testExceptionsInMergeMarketDataMessages() throws Exception {
        final FIXMessageFactory messageFactory = FIXVersion.FIX44.getMessageFactory();
        final Message incremental = messageFactory.createMessage(MsgType.MARKET_DATA_INCREMENTAL_REFRESH);
        final Message nos = createNOS("IFLI", new BigDecimal("23.3"), new BigDecimal("230"), Side.BUY, messageFactory); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        new ExpectedTestFailure(IllegalArgumentException.class, Messages.FIX_MD_MERGE_INVALID_INCOMING_SNAPSHOT.getText()) {
            protected void execute() throws Throwable {
                FIXMessageUtil.mergeMarketDataMessages(nos, incremental, messageFactory);
            }
        }.run();

        new ExpectedTestFailure(IllegalArgumentException.class, Messages.FIX_MD_MERGE_INVALID_INCOMING_INCREMENTAL.getText()) {
            protected void execute() throws Throwable {
                FIXMessageUtil.mergeMarketDataMessages(messageFactory.createMessage(MsgType.MARKET_DATA_SNAPSHOT_FULL_REFRESH),
                        nos, messageFactory);
            }
        }.run();
    }

    public void testIsCancellable() throws Exception {
		assertTrue(FIXMessageUtil.isCancellable(OrdStatus.ACCEPTED_FOR_BIDDING));
		assertTrue(FIXMessageUtil.isCancellable(OrdStatus.CALCULATED));
		assertTrue(FIXMessageUtil.isCancellable(OrdStatus.NEW));
		assertTrue(FIXMessageUtil.isCancellable(OrdStatus.PARTIALLY_FILLED));
		assertTrue(FIXMessageUtil.isCancellable(OrdStatus.PENDING_CANCEL));
		assertTrue(FIXMessageUtil.isCancellable(OrdStatus.PENDING_NEW));
		assertTrue(FIXMessageUtil.isCancellable(OrdStatus.PENDING_REPLACE));
		assertTrue(FIXMessageUtil.isCancellable(OrdStatus.STOPPED));
		assertTrue(FIXMessageUtil.isCancellable(OrdStatus.SUSPENDED));
		assertTrue(FIXMessageUtil.isCancellable(OrdStatus.REPLACED));
		
		assertFalse(FIXMessageUtil.isCancellable(OrdStatus.CANCELED));
		assertFalse(FIXMessageUtil.isCancellable(OrdStatus.DONE_FOR_DAY));
		assertFalse(FIXMessageUtil.isCancellable(OrdStatus.EXPIRED));
		assertFalse(FIXMessageUtil.isCancellable(OrdStatus.FILLED));
		assertFalse(FIXMessageUtil.isCancellable(OrdStatus.REJECTED));
		
		
		Message aMessage = msgFactory.newExecutionReport("ordid", "clordid", //$NON-NLS-1$ //$NON-NLS-2$
				"execid", OrdStatus.PENDING_REPLACE, Side.BUY, BigDecimal.TEN, //$NON-NLS-1$
				BigDecimal.TEN, BigDecimal.TEN, BigDecimal.TEN, BigDecimal.TEN,
				BigDecimal.TEN, new MSymbol("ABC"), null); //$NON-NLS-1$
		assertTrue(FIXMessageUtil.isCancellable(aMessage));
		assertFalse(FIXMessageUtil.isCancellable(FIXMessageUtilTest.createMarketNOS("ABC", new BigDecimal(10), Side.BUY, msgFactory))); //$NON-NLS-1$

    }

    public void testIsEquityOptionOrder() throws Exception {
        Message equity = FIXMessageUtilTest.createNOS("bob", new BigDecimal("23.11"), new BigDecimal("100"), Side.BUY, msgFactory); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertFalse(equity.getString(Symbol.FIELD), FIXMessageUtil.isEquityOptionOrder(equity));
        equity.setField(new Symbol("FRED.A")); //$NON-NLS-1$
        assertFalse("equity with route doesn't work: FRED.A", FIXMessageUtil.isEquityOptionOrder(equity)); //$NON-NLS-1$
        equity.setField(new Symbol("FRED.+")); //$NON-NLS-1$
        assertFalse("equity with route doesn't work: FRED.+", FIXMessageUtil.isEquityOptionOrder(equity)); //$NON-NLS-1$

        Message option = FIXMessageUtilTest.createOptionNOS("XYZ", "GE", "200708", new BigDecimal("10.25"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                PutOrCall.CALL, new BigDecimal("33.23"), new BigDecimal("10"), Side.BUY, msgFactory); //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue("option didn't work", FIXMessageUtil.isEquityOptionOrder(option)); //$NON-NLS-1$
        assertTrue("didn't work on IBM+IB plain order", //$NON-NLS-1$
                FIXMessageUtil.isEquityOptionOrder(FIXMessageUtilTest.createNOS("IBM+IB", new BigDecimal("22.22"), //$NON-NLS-1$ //$NON-NLS-2$
                        new BigDecimal("100"), Side.BUY, msgFactory))); //$NON-NLS-1$
        equity.setField(new CFICode("OCASPS")); //$NON-NLS-1$
        assertTrue("option CFICode didn't work", FIXMessageUtil.isEquityOptionOrder(equity)); //$NON-NLS-1$
    }

    public void testIsTradingSessionStatus() throws Exception {
        Message nos = FIXMessageUtilTest.createNOS("bob", new BigDecimal("23.11"), new BigDecimal("100"), Side.BUY, msgFactory); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertFalse(FIXMessageUtil.isTradingSessionStatus(nos));

        Message msg = new Message();
        msg.getHeader().setField(new MsgType(MsgType.TRADING_SESSION_STATUS));
        assertTrue(FIXMessageUtil.isTradingSessionStatus(msg));
    }
    public void testQuickFixFieldFromString()
        throws Exception
    {
        // value is null
        new ExpectedTestFailure(NullPointerException.class) {
            @Override
            protected void execute()
                    throws Throwable
            {
                FIXMessageUtil.getQuickFixFieldFromName(null);
            }
        }.run();
        // existing quickfix class
        quickfix.Field<?> field = FIXMessageUtil.getQuickFixFieldFromName("Side"); //$NON-NLS-1$
        assertNotNull(field);
        assertEquals(Side.FIELD,
                     field.getField());
        // class does not exist
        new ExpectedTestFailure(CoreException.class,
                                CANNOT_CREATE_FIX_FIELD.getText("bogus")) { //$NON-NLS-1$
            @Override
            protected void execute()
                    throws Throwable
            {
                FIXMessageUtil.getQuickFixFieldFromName("bogus"); //$NON-NLS-1$
            }
        }.run();
        // class does not exist but it can be interpreted as an int
        field = FIXMessageUtil.getQuickFixFieldFromName("0"); //$NON-NLS-1$
        assertNotNull(field);
        assertEquals(0,
                     field.getTag());
    }

    /**
     * Tests {@link FIXMessageUtil#toPrettyString(quickfix.Message, FIXDataDictionary)}
     *
     * @throws Exception if there were errors
     */
    public void testPrettyString() throws Exception {
        //Test an order
        Message message = createNOS("bob",
                new BigDecimal("23.11"), new BigDecimal("100"),
                Side.BUY, msgFactory);
        message.setString(5001,"customValue");
        String str = FIXMessageUtil.toPrettyString(message,fixDD);
        //Verify if we have certain text values
        //field tag strings & values
        assertTrue(str, str.contains("BeginString(8)="));
        assertTrue(str, str.contains("Side(54)=BUY(1)"));
        assertTrue(str, str.contains("MsgType(35)=NewOrderSingle(D)"));

        //check custom field
        assertTrue(str, str.contains("5001=customValue"));

        //Test an exec report
        message = msgFactory.newExecutionReport("ord1", "clord1", "execID",
                OrdStatus.NEW, Side.SELL, new BigDecimal("234.43"),
                new BigDecimal("98.34"), BigDecimal.ZERO, BigDecimal.ZERO,
                BigDecimal.ZERO, BigDecimal.ZERO, new MSymbol("IBM"),
                "accountName");
        message.setString(5001,"customValue");
        str = FIXMessageUtil.toPrettyString(message,fixDD);
        //field tag string
        assertTrue(str, str.contains("BeginString(8)="));
        assertTrue(str, str.contains("Side(54)=SELL(2)"));
        assertTrue(str, str.contains("MsgType(35)=ExecutionReport(8)"));

        //check custom field
        assertTrue(str, str.contains("5001=customValue"));

    }
}
