package org.marketcetera.ors;

import junit.framework.Test;
import org.marketcetera.core.*;
import org.marketcetera.quickfix.*;
import org.marketcetera.ors.filters.*;
import org.marketcetera.ors.filters.DefaultMessageModifier.MessageFieldType;
import org.marketcetera.spring.MockJmsTemplate;
import org.marketcetera.trade.MSymbol;
import org.marketcetera.util.log.I18NBoundMessage0P;
import org.marketcetera.util.log.I18NBoundMessage1P;
import quickfix.*;
import quickfix.field.*;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.jms.core.JmsOperations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.HashSet;

/**
 * Tests the code coming out of {@link OutgoingMessageHandler} class
 * @author Toli Kuznets
 * @version $Id$
 */
@org.junit.Ignore
@ClassVersion("$Id$")
public class RequestHandlerTest
    extends FIXVersionedTestCase
{
	/* a bunch of random made-up header/trailer/field values */
    public static final String HEADER_57_VAL = "CERT"; //$NON-NLS-1$
    public static final String HEADER_12_VAL = "12.24"; //$NON-NLS-1$
    public static final String TRAILER_2_VAL = "2-trailer"; //$NON-NLS-1$
    public static final String FIELDS_37_VAL = "37-regField"; //$NON-NLS-1$
    public static final String FIELDS_14_VAL = "37"; //$NON-NLS-1$

    public RequestHandlerTest(String inName, FIXVersion version) {
        super(inName, version);
    }

    public static Test suite()
    {
/*
        MarketceteraTestSuite suite = new MarketceteraTestSuite();
        suite.addTest(new RequestHandlerTest("testNewExecutionReportFromOrder", FIXVersion.FIX41));
        suite.init();
        return suite;
/*/
        return new FIXVersionTestSuite(RequestHandlerTest.class,
                FIXVersionTestSuite.ALL_VERSIONS,
                new HashSet<String>(Arrays.asList("testIncompatibleFIXVersions")), //$NON-NLS-1$
                new FIXVersion[]{FIXVersion.FIX40});
   }

    public void testNewExecutionReportFromOrder() throws Exception
    {
        /*
    	RequestHandler handler = new MyRequestHandler();
    	Message newOrder = msgFactory.newMarketOrder("bob", Side.BUY, new BigDecimal(100), new MSymbol("IBM"), //$NON-NLS-1$ //$NON-NLS-2$
                                                      TimeInForce.DAY, "bob"); //$NON-NLS-1$
        // add symbol sfx
        newOrder.setField(new SymbolSfx(SymbolSfx.WHEN_ISSUED));
        Message execReport = handler.executionReportFromNewOrder(newOrder);
        // put an orderID in since immediate execReport doesn't have one and we need one for validation
        execReport.setField(new OrderID("fake-order-id")); //$NON-NLS-1$
        verifyExecutionReport(execReport);
        // verify the acount id is present
        assertEquals("bob", execReport.getString(Account.FIELD)); //$NON-NLS-1$
        assertTrue("sendingTime not set", execReport.getHeader().isSetField(SendingTime.FIELD)); //$NON-NLS-1$
        assertEquals(SymbolSfx.WHEN_ISSUED, execReport.getString(SymbolSfx.FIELD));

        // on a non-single order should get back null
        assertNull(handler.executionReportFromNewOrder(msgFactory.newCancel("bob", "bob", //$NON-NLS-1$ //$NON-NLS-2$
                                                                  Side.BUY, new BigDecimal(100), new MSymbol("IBM"), "counterparty"))); //$NON-NLS-1$ //$NON-NLS-2$
        */
    }

    /** Bug #416 - make sure sending time changes between messages */
    @SuppressWarnings({"ThrowableInstanceNeverThrown"})
    public void testSendingTimeChanges() throws Exception {
        /*
        RequestHandler handler = new MyRequestHandler();
        Message newOrder = msgFactory.newMarketOrder("bob", Side.BUY, new BigDecimal(100), new MSymbol("IBM"), //$NON-NLS-1$ //$NON-NLS-2$
                                                      TimeInForce.DAY, "bob"); //$NON-NLS-1$
        Message execReport = handler.executionReportFromNewOrder(newOrder);
        //        Message reject1 = handler.createRejectionMessage(new Exception(), newOrder);
        Thread.sleep(3000);
        Message execReport2 = handler.executionReportFromNewOrder(newOrder);
        Message reject2 = handler.createRejectionMessage(new Exception(), newOrder);
        String sendTime1 = execReport.getHeader().getString(SendingTime.FIELD);
        String sendTime2 = execReport2.getHeader().getString(SendingTime.FIELD);
        assertFalse("sending times are equal: "+ sendTime1 + "/"+ sendTime2, //$NON-NLS-1$ //$NON-NLS-2$
                sendTime1.equals(sendTime2));

        assertFalse("reject sending times are equal", //$NON-NLS-1$
                reject1.getHeader().getString(SendingTime.FIELD).equals(reject2.getHeader().getString(SendingTime.FIELD)));
        */
    }

    // test one w/out incoming account
    public void testNewExecutionReportFromOrder_noAccount() throws Exception
    {
        /*
    	RequestHandler handler = new MyRequestHandler();
        Message newOrder = msgFactory.newMarketOrder("bob", Side.BUY, new BigDecimal(100), new MSymbol("IBM"), //$NON-NLS-1$ //$NON-NLS-2$
                                                      TimeInForce.DAY, "bob"); //$NON-NLS-1$
        // remove account ID
        newOrder.removeField(Account.FIELD);

        final Message execReport = handler.executionReportFromNewOrder(newOrder);
        // put an orderID in since immediate execReport doesn't have one and we need one for validation
        execReport.setField(new OrderID("fake-order-id")); //$NON-NLS-1$
        verifyExecutionReport(execReport);
        // verify the acount id is not present
        (new ExpectedTestFailure(FieldNotFound.class) {
            protected void execute() throws Exception {
                execReport.getString(Account.FIELD);
            }
        }).run();
        */
    }

    // verifies that we get an error when an unsupported order type is sent 
    public void testNotNOSOrder() throws Exception {
        final RequestHandler handler = new MyRequestHandler();
        Message wrongMsg = msgFactory.newOrderCancelReject();
        wrongMsg.getHeader().setField(new MsgSeqNum(23));
        Message reject = null; //handler.replyToMessage(wrongMsg);
        //        verifyBMRejection(reject, msgFactory, new I18NBoundMessage1P(Messages.ERROR_UNSUPPORTED_ORDER_TYPE,
        //                fixDD.getHumanFieldValue(MsgType.FIELD, MsgType.ORDER_CANCEL_REJECT)));
    }

    private void verifyExecutionReport(Message inExecReport) throws Exception
    {
        FIXMessageUtilTest.verifyExecutionReport(inExecReport, "100", "IBM", Side.BUY, msgFactory, fixDD); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /** Create a few default fields and verify they get placed
     * into the message
     */
    public void testInsertDefaultFields() throws Exception
    {
        NullQuickFIXSender quickFIXSender = new NullQuickFIXSender();
        RequestHandler handler = new MyRequestHandler(quickFIXSender);

        Message msg = msgFactory.newMarketOrder("bob", Side.BUY, new BigDecimal(100), new MSymbol("IBM"), //$NON-NLS-1$ //$NON-NLS-2$
                                                      TimeInForce.DAY, "bob"); //$NON-NLS-1$
        Message response = null; //handler.replyToMessage(msg);

        assertNotNull(response);
        assertEquals(1, quickFIXSender.getCapturedMessages().size());
        Message modifiedMessage = quickFIXSender.getCapturedMessages().get(0);

        // verify that all the default fields have been set
        assertEquals(HEADER_57_VAL, modifiedMessage.getHeader().getString(57));
        assertEquals(HEADER_12_VAL, modifiedMessage.getHeader().getString(12));
        assertEquals(TRAILER_2_VAL, modifiedMessage.getTrailer().getString(2));
        assertEquals(FIELDS_37_VAL, modifiedMessage.getString(37));
        assertEquals(FIELDS_14_VAL, modifiedMessage.getString(14));

        if(msgFactory.getMsgAugmentor().needsTransactTime(modifiedMessage)) {
            // verify that transaction date is set as well, but it'd be set anyway b/c new order sets it
            assertNotNull(modifiedMessage.getString(TransactTime.FIELD));
        }
        
        // field 14 and 37 doesn't really belong in NOS so get rid of it before verification, same with field 2 in trailer
        modifiedMessage.removeField(14);
        modifiedMessage.removeField(37);
        modifiedMessage.getTrailer().removeField(2);
        fixDD.getDictionary().validate(modifiedMessage, true);
        // put an orderID in since immediate execReport doesn't have one and we need one for validation
        response.setField(new OrderID("fake-order-id")); //$NON-NLS-1$
        fixDD.getDictionary().validate(response, true);
    }

    public void testImmediateReportAfterRouteMgr() throws Exception {
        NullQuickFIXSender quickFIXSender = new NullQuickFIXSender();
        RequestHandler handler = new MyRequestHandler(quickFIXSender);
        MessageRouteManager routeManager = new MessageRouteManager();
        routeManager.setSeparateSuffix(true);

        Message msg = msgFactory.newMarketOrder("bob", Side.BUY, new BigDecimal(100), new MSymbol("EUR/USD"), //$NON-NLS-1$ //$NON-NLS-2$
                                                      TimeInForce.DAY, "bob"); //$NON-NLS-1$
        Message response = null; //handler.replyToMessage(msg);
        assertNotNull(quickFIXSender.getCapturedMessages().get(0));
        assertEquals("no symbol suffix in sent msg", "USD", quickFIXSender.getCapturedMessages().get(0).getString(SymbolSfx.FIELD)); //$NON-NLS-1$ //$NON-NLS-2$

        assertNotNull(response);
        assertEquals("verify symbol has been separated", "EUR", response.getString(Symbol.FIELD)); //$NON-NLS-1$ //$NON-NLS-2$

        // this is to test #362
        assertEquals("didn't pick up SymbolSfx", "USD", response.getString(SymbolSfx.FIELD)); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /** Send a forex order EUR/USD and make sure that the symbol comes through unchanged */
    public void testForexOrder_withSeparateSuffix() throws Exception {
        NullQuickFIXSender quickFIXSender = new NullQuickFIXSender();
        RequestHandler handler = new MyRequestHandler(quickFIXSender);
        MessageRouteManager routeManager = new MessageRouteManager();
        routeManager.setSeparateSuffix(true);

        Message msg = msgFactory.newMarketOrder("bob", Side.BUY, new BigDecimal(100), new MSymbol("EUR/USD"), //$NON-NLS-1$ //$NON-NLS-2$
                                                      TimeInForce.DAY, "bob"); //$NON-NLS-1$
        // change it to be forex
        msg.setField(new SecurityType(SecurityType.FOREIGN_EXCHANGE_CONTRACT));
        msg.setField(new OrdType(OrdType.FOREX_MARKET));

        Message response = null; //handler.replyToMessage(msg);
        assertNotNull(quickFIXSender.getCapturedMessages().get(0));
        assertFalse("should not have symbol suffix in sent msg", quickFIXSender.getCapturedMessages().get(0).isSetField(SymbolSfx.FIELD)); //$NON-NLS-1$

        assertNotNull(response);
        assertEquals("verify symbol has not been separated", "EUR/USD", response.getString(Symbol.FIELD)); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /** Send a generic event and a single-order event.
     * Verify get an executionReport (not content of it) and that the msg come out
     * on the sink
     *
     * Should get 3 outputs:
     * execution report
     * original new order report
     * cancel report
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public void testHandleEvents() throws Exception
    {
        NullQuickFIXSender quickFIXSender = new NullQuickFIXSender();
        RequestHandler handler = new MyRequestHandler(quickFIXSender);

		Message newOrder = msgFactory.newMarketOrder("bob", Side.BUY, new BigDecimal(100), new MSymbol("IBM"), //$NON-NLS-1$ //$NON-NLS-2$
                                                      TimeInForce.DAY, "bob"); //$NON-NLS-1$
        Message cancelOrder = msgFactory.newCancel("bob", "bob", //$NON-NLS-1$ //$NON-NLS-2$
                                                    Side.SELL, new BigDecimal(7), new MSymbol("TOLI"), "redParty"); //$NON-NLS-1$ //$NON-NLS-2$

        List<Message> orderList = Arrays.asList(newOrder, cancelOrder);
        List<Message> responses = new LinkedList<Message>();
        for (Message message : orderList) {
            responses.add(null); //handler.replyToMessage(message));
		}
        
        // verify that we have 2 orders on the mQF sink and 1 on the incomingJMS
        assertEquals("not enough events on the QF output", 2, quickFIXSender.getCapturedMessages().size()); //$NON-NLS-1$
        assertEquals("first output should be outgoing execReport", MsgType.EXECUTION_REPORT, //$NON-NLS-1$
                     responses.get(0).getHeader().getString(MsgType.FIELD));
        assertEquals("2nd event should be original buy order", newOrder, //$NON-NLS-1$
                quickFIXSender.getCapturedMessages().get(0));
        assertEquals("3rd event should be cancel order", cancelOrder, //$NON-NLS-1$
                quickFIXSender.getCapturedMessages().get(1));

        // put an orderID in since immediate execReport doesn't have one and we need one for validation
        responses.get(0).setField(new OrderID("fake-order-id")); //$NON-NLS-1$
        verifyExecutionReport(responses.get(0));
    }

    /** verify that sending a malformed buy order (ie missing Side) results in a reject exectuionReport */
    public void testHandleMalformedEvent() throws Exception {
        Message buyOrder = FIXMessageUtilTest.createNOS("toli", new BigDecimal("12.34"), new BigDecimal("234"), Side.BUY, msgFactory); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        buyOrder.removeField(Side.FIELD);

        NullQuickFIXSender quickFIXSender = new NullQuickFIXSender();
        RequestHandler handler = new MyRequestHandler(quickFIXSender);

		final Message result = null; //handler.replyToMessage(buyOrder);
		assertNotNull(result);
		assertEquals(0, quickFIXSender.getCapturedMessages().size());
		assertEquals("first output should be outgoing execReport", MsgType.EXECUTION_REPORT, //$NON-NLS-1$
                     result.getHeader().getString(MsgType.FIELD));
        assertEquals("should be a reject execReport", OrdStatus.REJECTED, result.getChar(OrdStatus.FIELD)); //$NON-NLS-1$
        assertTrue("Error message should say field Side was missing", result.getString(Text.FIELD).contains("field Side")); //$NON-NLS-1$ //$NON-NLS-2$
        if(!msgFactory.getBeginString().equals(FIXVersion.FIX40.toString())) {
            assertEquals("execType should be a reject", ExecType.REJECTED, result.getChar(ExecType.FIELD)); //$NON-NLS-1$
        }
        // validation will fail b/c we didn't send a side in to begin with
        new ExpectedTestFailure(RuntimeException.class, "field="+Side.FIELD) { //$NON-NLS-1$
            protected void execute() throws Throwable {
                fixDD.getDictionary().validate(result, true);
            }
        }.run();
    }

    /** Basically, this is a test for bug #15 where any error in the internal code
     * should result in a rejection being sent back.
     * @throws Exception
     */
    public void testMalformedPrice() throws Exception {
        NullQuickFIXSender quickFIXSender = new NullQuickFIXSender();
        RequestHandler handler = new MyRequestHandler(quickFIXSender);

        Message buyOrder = FIXMessageUtilTest.createNOS("toli", new BigDecimal("12.34"), new BigDecimal("234"), Side.BUY, msgFactory); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        buyOrder.setString(Price.FIELD, "23.23.3"); //$NON-NLS-1$

        assertNotNull(buyOrder.getString(ClOrdID.FIELD));
        Message result = null; //handler.replyToMessage(buyOrder);
        assertNotNull(result);
        assertEquals("first output should be outgoing execReport", MsgType.EXECUTION_REPORT, //$NON-NLS-1$
        		result.getHeader().getString(MsgType.FIELD));
        assertEquals("should be a reject execReport", OrdStatus.REJECTED, result.getChar(OrdStatus.FIELD)); //$NON-NLS-1$
        if(!msgFactory.getBeginString().equals(FIXVersion.FIX40.toString())) {
            assertEquals("execType should be a reject", ExecType.REJECTED, result.getChar(ExecType.FIELD)); //$NON-NLS-1$
        }
        assertNotNull("rejectExecReport doesn't have a ClOrdID set", result.getString(ClOrdID.FIELD)); //$NON-NLS-1$
        assertNotNull("no useful rejection message", result.getString(Text.FIELD)); //$NON-NLS-1$
    }

    /** brain-dead: make sure that incoming orders just get placed on the sink */
    @SuppressWarnings("unchecked")
    public void testHandleFIXMessages() throws Exception
    {
        NullQuickFIXSender quickFIXSender = new NullQuickFIXSender();
        RequestHandler handler = new MyRequestHandler(quickFIXSender);

		Message newOrder = msgFactory.newCancelReplaceShares("bob", "orig", new BigDecimal(100)); //$NON-NLS-1$ //$NON-NLS-2$
		newOrder.setField(new Symbol("ASDF")); //$NON-NLS-1$
		Message cancelOrder = msgFactory.newCancel("bob", "bob", //$NON-NLS-1$ //$NON-NLS-2$
                                                    Side.SELL, new BigDecimal(7), new MSymbol("TOLI"), "redParty"); //$NON-NLS-1$ //$NON-NLS-2$
        //handler.replyToMessage(newOrder);
        //handler.replyToMessage(cancelOrder);

        assertEquals("not enough events on the OM quickfix sink", 2, quickFIXSender.getCapturedMessages().size()); //$NON-NLS-1$
        assertEquals("1st event should be original buy order", newOrder, //$NON-NLS-1$
        		quickFIXSender.getCapturedMessages().get(0));
        assertEquals("2nd event should be cancel order", cancelOrder, //$NON-NLS-1$
        		quickFIXSender.getCapturedMessages().get(1));
        fixDD.getDictionary().validate(quickFIXSender.getCapturedMessages().get(1), true);
    }

    public void testInvalidSessionID() throws Exception {
        QuickFIXSender quickFIXSender = new QuickFIXSender();
        RequestHandler handler = new MyRequestHandler(quickFIXSender);
        SessionID sessionID = new SessionID(msgFactory.getBeginString(), "no-sender", "no-target"); //$NON-NLS-1$ //$NON-NLS-2$
        //        handler.setDefaultSessionID(sessionID);

	    Message newOrder = msgFactory.newMarketOrder("123", Side.BUY, new BigDecimal(100), new MSymbol("SUNW"), //$NON-NLS-1$ //$NON-NLS-2$
                TimeInForce.DAY, "dummyaccount"); //$NON-NLS-1$

        // verify we got an execReport that's a rejection with the sessionNotfound error message
        Message result = null; //handler.replyToMessage(newOrder);
        //        verifyRejection(result, msgFactory, new I18NBoundMessage1P(org.marketcetera.core.Messages.ERROR_FIX_SESSION_NOT_FOUND, ObjectUtils.toString(sessionID,null)));
    }

    /** Create props with a route manager entry, and make sure the FIX message is
     * modified but the other ones are not
     * @throws Exception
     */
    public void testWithOrderRouteManager() throws Exception {
        final NullQuickFIXSender quickFIXSender = new NullQuickFIXSender();
        RequestHandler handler = new MyRequestHandler(quickFIXSender);
        MessageRouteManager orm = MessageRouteManagerTest.getORMWithOrderRouting(MessageRouteManager.FIELD_100_METHOD);


    	
        // 1. create a "incoming JMS buy" order and verify that it doesn't have routing in it
        final Message newOrder = msgFactory.newMarketOrder("bob", Side.BUY, new BigDecimal(100), new MSymbol("IBM"), //$NON-NLS-1$ //$NON-NLS-2$
                                                      TimeInForce.DAY, "bob"); //$NON-NLS-1$
        // verify there's no route info
        new ExpectedTestFailure(FieldNotFound.class) {
            protected void execute() throws Throwable {
                newOrder.getField(new ExDestination());
            }
        }.run();

        Message result = null; //handler.replyToMessage(newOrder);

        assertNotNull(result);
        assertEquals(1, quickFIXSender.getCapturedMessages().size());
        new ExpectedTestFailure(FieldNotFound.class) {
            protected void execute() throws Throwable {
                newOrder.getHeader().getString(100);
                quickFIXSender.getCapturedMessages().get(1).getField(new ExDestination());
        }}.run();

        // now send a FIX-related message through order manager and make sure routing does show up
        orderRouterTesterHelper(handler, "BRK/A", null, "A"); //$NON-NLS-1$ //$NON-NLS-2$
        orderRouterTesterHelper(handler, "IFLI.IM", "Milan", null); //$NON-NLS-1$ //$NON-NLS-2$
        orderRouterTesterHelper(handler, "BRK/A.N", "SIGMA", "A"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    public void testIncomingNullMessage() throws Exception {
        RequestHandler handler = new MyRequestHandler();
        assertNull(null); //handler.replyToMessage(null));        
    }

    /** verify the ORS sends back a rejection when it receives a message of incompatible or unknown verison
     * this test is hardcoded with ORS at fix40 so exclude it from multi-version tests */
    public void testIncompatibleFIXVersions() throws Exception {
        RequestHandler handler = new MyRequestHandler();
        Message msg = new quickfix.fix41.Message();
        Message reject = null; //handler.replyToMessage(msg);
        assertEquals("didn't get an execution report", MsgType.EXECUTION_REPORT, reject.getHeader().getString(MsgType.FIELD)); //$NON-NLS-1$
        assertEquals("didn't get a reject", OrdStatus.REJECTED+"", reject.getString(OrdStatus.FIELD)); //$NON-NLS-1$ //$NON-NLS-2$
        //        assertEquals("didn't get a right reason", //$NON-NLS-1$
        //                Messages.ERROR_MISMATCHED_FIX_VERSION.getText(FIXVersion.FIX40.toString(), FIXVersion.FIX41.toString()),
        //                reject.getString(Text.FIELD));

        // now test it with no fix version at all
        reject = null; //handler.replyToMessage(new Message());
        //        verifyRejection(reject, msgFactory, new I18NBoundMessage0P(Messages.ERROR_MESSAGE_MALFORMED_NO_FIX_VERSION));
    }

    public void testOrderListNotSupported() throws Exception {
        RequestHandler handler = new MyRequestHandler();
        Message orderList = msgFactory.createMessage(MsgType.ORDER_LIST);
        orderList.setField(new Symbol("TOLI")); //$NON-NLS-1$
        orderList.getHeader().setField(new MsgSeqNum(23));
        Message reject = null; //handler.replyToMessage(orderList);
        //        verifyBMRejection(reject, msgFactory, new I18NBoundMessage1P(Messages.ERROR_UNSUPPORTED_ORDER_TYPE,
        //                fixDD.getHumanFieldValue(MsgType.FIELD, MsgType.ORDER_LIST)));
    }


    /** verify that ORS rejects messages if it's not connected to a FIX destination */
    public void testMessageRejectedLoggedOutORS() throws Exception {
        NullQuickFIXSender sender = new NullQuickFIXSender();
        MyRequestHandler handler = new MyRequestHandler(sender);
        Message execReport = null; //handler.replyToMessage(FIXMessageUtilTest.createNOS("TOLI", new BigDecimal("23.33"), new BigDecimal("100"), Side.BUY, msgFactory)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(1, sender.getCapturedMessages().size());
        assertTrue(FIXMessageUtil.isExecutionReport(execReport));
        assertEquals(OrdStatus.PENDING_NEW, execReport.getChar(OrdStatus.FIELD));

        // now set it to be logged out and verify a reject
        sender.getCapturedMessages().clear();
        handler.getQFApp().onLogout(new SessionID(msgFactory.getBeginString(), "sender", "target")); //$NON-NLS-1$ //$NON-NLS-2$
        execReport = null; //handler.replyToMessage(FIXMessageUtilTest.createNOS("TOLI", new BigDecimal("23.33"), new BigDecimal("100"), Side.BUY, msgFactory)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(0, sender.getCapturedMessages().size());
        //        verifyRejection(execReport, msgFactory, new I18NBoundMessage0P(Messages.ERROR_NO_DESTINATION_CONNECTION));

        // verify goes through again after log on
        sender.getCapturedMessages().clear();
        handler.getQFApp().onLogon(null);
        execReport = null; //handler.replyToMessage(FIXMessageUtilTest.createNOS("TOLI", new BigDecimal("23.33"), new BigDecimal("100"), Side.BUY, msgFactory)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(1, sender.getCapturedMessages().size());
        assertEquals(MsgType.EXECUTION_REPORT, execReport.getHeader().getString(MsgType.FIELD));
        assertEquals(OrdStatus.PENDING_NEW, execReport.getChar(OrdStatus.FIELD));
    }

    /** bug #433 - need to modify the reject being sent back in case the ORS is not logged on
     * verify the reject coming back does not have the OrdStatus set
     */
    public void testOrderCancelRejectWhenORSNotLoggedOn() throws Exception {
        NullQuickFIXSender sender = new NullQuickFIXSender();
        MyRequestHandler handler = new MyRequestHandler(sender);

        // now set it to be logged out and verify a reject
        sender.getCapturedMessages().clear();
        handler.getQFApp().onLogout(new SessionID(msgFactory.getBeginString(), "sender", "target")); //$NON-NLS-1$ //$NON-NLS-2$
        Message reject = null; //handler.replyToMessage(msgFactory.newCancelFromMessage(FIXMessageUtilTest.createNOS("TOLI", new BigDecimal("23.33"), new BigDecimal("100"), Side.BUY, msgFactory))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals(0, sender.getCapturedMessages().size());
        assertEquals(MsgType.ORDER_CANCEL_REJECT, reject.getHeader().getString(MsgType.FIELD));
        assertEquals("OrdStatus should not set", OrdStatus.REJECTED, reject.getChar(OrdStatus.FIELD)); //$NON-NLS-1$
        //        assertEquals("didn't get a right reason", Messages.ERROR_NO_DESTINATION_CONNECTION.getText(), //$NON-NLS-1$
        //                reject.getString(Text.FIELD));
    }

    /** Test that incoming commands are copied on copy-commands-topic */
    public void testCommandsCopiedToTopic() throws Exception {
        MockJmsTemplate copyJmsTemplate = new MockJmsTemplate();
        MyRequestHandler handler = new MyRequestHandler
            (null,copyJmsTemplate);

        Message nos = FIXMessageUtilTest.createNOS("abc", new BigDecimal("10"), new BigDecimal("100"), Side.BUY, msgFactory); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        //handler.replyToMessage(nos);

        assertEquals(1, copyJmsTemplate.getSentMessages().size());
        assertEquals(nos, copyJmsTemplate.getSentMessages().get(0));
    }


    /** Helper method that takes an OrderManager, the stock symbol and the expect exchange
     * and verifies that the route parsing comes back correct
     * @param symbol    Symbol, can contain either a share class or an exchange (or both)
     * @param   expectedExchange    Exchange we expec (or null)
     * @param   shareClass      Share class (or null)
     */
    private void orderRouterTesterHelper(RequestHandler handler, String symbol,
                                         String expectedExchange, String shareClass)
            throws Exception {
        final Message qfMsg = msgFactory.newMarketOrder("bob", Side.BUY, new BigDecimal(100), //$NON-NLS-1$
                new MSymbol(symbol),  TimeInForce.DAY, "bob"); //$NON-NLS-1$
        NullQuickFIXSender nullQuickFIXSender = ((NullQuickFIXSender)handler.getSender());
		nullQuickFIXSender.getCapturedMessages().clear();
        Message result = null; //handler.replyToMessage(qfMsg);

        assertNotNull(result);
        assertEquals(1,nullQuickFIXSender.getCapturedMessages().size());
        Message incomingMsg = nullQuickFIXSender.getCapturedMessages().get(0);
        if(expectedExchange != null) {
            assertEquals(expectedExchange, incomingMsg.getString(ExDestination.FIELD));
        }
        if(shareClass != null) {
            assertEquals(shareClass, incomingMsg.getString(SymbolSfx.FIELD));
        }
    }


    /** Helper method for creating a set of properties with defaults to be reused   */
    public static List<MessageModifier> getMessageModifiers()
    {
    	List<MessageModifier> messageModifiers = new LinkedList<MessageModifier>();

    	DefaultMessageModifier defaultOrderModifier = new DefaultMessageModifier();
    	defaultOrderModifier.addDefaultField(57, HEADER_57_VAL, MessageFieldType.HEADER);
    	messageModifiers.add(defaultOrderModifier);

    	defaultOrderModifier = new DefaultMessageModifier();
    	defaultOrderModifier.addDefaultField(12, HEADER_12_VAL, MessageFieldType.HEADER);
    	messageModifiers.add(defaultOrderModifier);

    	defaultOrderModifier = new DefaultMessageModifier();
    	defaultOrderModifier.addDefaultField(2, TRAILER_2_VAL, MessageFieldType.TRAILER);
    	messageModifiers.add(defaultOrderModifier);

    	defaultOrderModifier = new DefaultMessageModifier();
    	defaultOrderModifier.addDefaultField(37, FIELDS_37_VAL, MessageFieldType.MESSAGE);
    	messageModifiers.add(defaultOrderModifier);

    	defaultOrderModifier = new DefaultMessageModifier();
    	defaultOrderModifier.addDefaultField(14, FIELDS_14_VAL, MessageFieldType.MESSAGE);
    	messageModifiers.add(defaultOrderModifier);
    	
    	return messageModifiers;
    }

    private void verifyBMRejection(Message inMsg, FIXMessageFactory msgFactory, I18NBoundMessage1P msg) throws Exception
    {
        if(msgFactory.getBeginString().equals(FIXVersion.FIX40.toString()) ||
                msgFactory.getBeginString().equals(FIXVersion.FIX41.toString())) {
            assertEquals("didn't get a session-level reject", MsgType.REJECT, inMsg.getHeader().getString(MsgType.FIELD)); //$NON-NLS-1$
        } else {
            assertEquals("didn't get a business-message reject", MsgType.BUSINESS_MESSAGE_REJECT, inMsg.getHeader().getString(MsgType.FIELD)); //$NON-NLS-1$
        }
        assertEquals("didn't get a right reason", //$NON-NLS-1$
                msg.getText(),
                inMsg.getString(Text.FIELD));
    }

    public static void verifyRejection(Message inMsg, FIXMessageFactory msgFactory,
                                       I18NBoundMessage0P msg) throws Exception
    {
        assertEquals("didn't get an execution report", MsgType.EXECUTION_REPORT, inMsg.getHeader().getString(MsgType.FIELD)); //$NON-NLS-1$
        assertEquals("didn't get a reject", OrdStatus.REJECTED+"", inMsg.getString(OrdStatus.FIELD)); //$NON-NLS-1$ //$NON-NLS-2$
        if(!msgFactory.getBeginString().equals(FIXVersion.FIX40.toString())) {
            assertEquals("execType should be a reject", ExecType.REJECTED, inMsg.getChar(ExecType.FIELD)); //$NON-NLS-1$
        }
        assertEquals("didn't get a right reason", //$NON-NLS-1$
                msg.getText(),
                inMsg.getString(Text.FIELD));
        assertTrue("rejects should have sending time in them too", inMsg.getHeader().isSetField(SendingTime.FIELD)); //$NON-NLS-1$

    }

    public static void verifyRejection(Message inMsg, FIXMessageFactory msgFactory,
                                       I18NBoundMessage1P msg) throws Exception
    {
        assertEquals("didn't get an execution report", MsgType.EXECUTION_REPORT, inMsg.getHeader().getString(MsgType.FIELD)); //$NON-NLS-1$
        assertEquals("didn't get a reject", OrdStatus.REJECTED+"", inMsg.getString(OrdStatus.FIELD)); //$NON-NLS-1$ //$NON-NLS-2$
        if(!msgFactory.getBeginString().equals(FIXVersion.FIX40.toString())) {
            assertEquals("execType should be a reject", ExecType.REJECTED, inMsg.getChar(ExecType.FIELD)); //$NON-NLS-1$
        }
        assertEquals("didn't get a right reason", //$NON-NLS-1$
                msg.getText(),
                inMsg.getString(Text.FIELD));
        assertTrue("rejects should have sending time in them too", inMsg.getHeader().isSetField(SendingTime.FIELD)); //$NON-NLS-1$

    }

    public static class MyRequestHandler extends RequestHandler {

        private static int factoryStart = (int) Math.round((Math.random() * 1000));

        private QuickFIXApplication qfApp = new QuickFIXApplicationTest.MockQuickFIXApplication(null);

        public MyRequestHandler
            (IQuickFIXSender inQuickFIXSender,
             JmsOperations inIncomingCommandsCopier)
                throws ClassNotFoundException,
                       ConfigError, FieldConvertError, CoreException {
            super(null,null,null,null,
                  inQuickFIXSender,null,
                  new InMemoryIDFactory(factoryStart));
            // simulate logon
            qfApp.onLogon(null);
        }

        public MyRequestHandler
            (IQuickFIXSender sender)
                throws ClassNotFoundException,
                       ConfigError, FieldConvertError, CoreException {
            this(sender, null);
        }

        public MyRequestHandler()
                throws ClassNotFoundException,
                       ConfigError, FieldConvertError, CoreException {
            this(null);
        }

        public QuickFIXApplication getQFApp() { return qfApp; }
    }
}
