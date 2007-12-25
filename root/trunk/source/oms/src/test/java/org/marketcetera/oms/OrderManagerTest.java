package org.marketcetera.oms;

import junit.framework.Test;
import org.marketcetera.core.*;
import org.marketcetera.quickfix.*;
import org.marketcetera.quickfix.DefaultMessageModifier.MessageFieldType;
import org.marketcetera.spring.MockJmsTemplate;
import quickfix.*;
import quickfix.field.*;

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
@ClassVersion("$Id$")
public class OrderManagerTest extends FIXVersionedTestCase
{
	/* a bunch of random made-up header/trailer/field values */
    public static final String HEADER_57_VAL = "CERT";
    public static final String HEADER_12_VAL = "12.24";
    public static final String TRAILER_2_VAL = "2-trailer";
    public static final String FIELDS_37_VAL = "37-regField";
    public static final String FIELDS_14_VAL = "37";

    public OrderManagerTest(String inName, FIXVersion version) {
        super(inName, version);
    }

    public static Test suite()
    {
/*
        MarketceteraTestSuite suite = new MarketceteraTestSuite();
        suite.addTest(new OrderManagerTest("testDictionaryLoad", FIXVersion.FIX41));
        suite.init(new MessageBundleInfo[]{OrderManagementSystem.OMS_MESSAGE_BUNDLE_INFO});
        return suite;
/*/
        return new FIXVersionTestSuite(OrderManagerTest.class, OrderManagementSystem.OMS_MESSAGE_BUNDLE_INFO,
                FIXVersionTestSuite.ALL_VERSIONS,
                new HashSet<String>(Arrays.asList("testIncompatibleFIXVersions")),
                new FIXVersion[]{FIXVersion.FIX40});
   }

    public void testNewExecutionReportFromOrder() throws Exception
    {
    	OutgoingMessageHandler handler = new MyOutgoingMessageHandler(msgFactory);
        handler.setOrderRouteManager(new MessageRouteManager());
    	Message newOrder = msgFactory.newMarketOrder("bob", Side.BUY, new BigDecimal(100), new MSymbol("IBM"),
                                                      TimeInForce.DAY, "bob");
        // add symbol sfx
        newOrder.setField(new SymbolSfx(SymbolSfx.WHEN_ISSUED));
        Message execReport = handler.executionReportFromNewOrder(newOrder);
        // put an orderID in since immediate execReport doesn't have one and we need one for validation
        execReport.setField(new OrderID("fake-order-id"));
        verifyExecutionReport(execReport);
        // verify the acount id is present
        assertEquals("bob", execReport.getString(Account.FIELD));
        assertTrue("sendingTime not set", execReport.getHeader().isSetField(SendingTime.FIELD));
        assertEquals(SymbolSfx.WHEN_ISSUED, execReport.getString(SymbolSfx.FIELD));

        // on a non-single order should get back null
        assertNull(handler.executionReportFromNewOrder(msgFactory.newCancel("bob", "bob",
                                                                  Side.BUY, new BigDecimal(100), new MSymbol("IBM"), "counterparty")));
    }

    /** Bug #416 - make sure sending time changes between messages */
    @SuppressWarnings({"ThrowableInstanceNeverThrown"})
    public void testSendingTimeChanges() throws Exception {
        OutgoingMessageHandler handler = new MyOutgoingMessageHandler(msgFactory);
        handler.setOrderRouteManager(new MessageRouteManager());
        Message newOrder = msgFactory.newMarketOrder("bob", Side.BUY, new BigDecimal(100), new MSymbol("IBM"),
                                                      TimeInForce.DAY, "bob");
        Message execReport = handler.executionReportFromNewOrder(newOrder);
        Message reject1 = handler.createRejectionMessage(new Exception(), newOrder);
        Thread.sleep(3000);
        Message execReport2 = handler.executionReportFromNewOrder(newOrder);
        Message reject2 = handler.createRejectionMessage(new Exception(), newOrder);
        String sendTime1 = execReport.getHeader().getString(SendingTime.FIELD);
        String sendTime2 = execReport2.getHeader().getString(SendingTime.FIELD);
        assertFalse("sending times are equal: "+ sendTime1 + "/"+ sendTime2,
                sendTime1.equals(sendTime2));

        assertFalse("reject sending times are equal",
                reject1.getHeader().getString(SendingTime.FIELD).equals(reject2.getHeader().getString(SendingTime.FIELD)));
    }

    // test one w/out incoming account
    public void testNewExecutionReportFromOrder_noAccount() throws Exception
    {
    	OutgoingMessageHandler handler = new MyOutgoingMessageHandler(msgFactory);
        handler.setOrderRouteManager(new MessageRouteManager());
        Message newOrder = msgFactory.newMarketOrder("bob", Side.BUY, new BigDecimal(100), new MSymbol("IBM"),
                                                      TimeInForce.DAY, "bob");
        // remove account ID
        newOrder.removeField(Account.FIELD);

        final Message execReport = handler.executionReportFromNewOrder(newOrder);
        // put an orderID in since immediate execReport doesn't have one and we need one for validation
        execReport.setField(new OrderID("fake-order-id"));
        verifyExecutionReport(execReport);
        // verify the acount id is not present
        (new ExpectedTestFailure(FieldNotFound.class) {
            protected void execute() throws Exception {
                execReport.getString(Account.FIELD);
            }
        }).run();
    }

    // verifies that we get an error when an unsupported order type is sent 
    public void testNotNOSOrder() throws Exception {
        final OutgoingMessageHandler handler = new MyOutgoingMessageHandler(msgFactory);
        Message wrongMsg = msgFactory.newOrderCancelReject();
        wrongMsg.getHeader().setField(new MsgSeqNum(23));
        Message reject = handler.handleMessage(wrongMsg);
        verifyBMRejection(reject, msgFactory, OMSMessageKey.ERROR_UNSUPPORTED_ORDER_TYPE,
                fixDD.getHumanFieldValue(MsgType.FIELD, MsgType.ORDER_CANCEL_REJECT));
    }

    private void verifyExecutionReport(Message inExecReport) throws Exception
    {
        FIXMessageUtilTest.verifyExecutionReport(inExecReport, "100", "IBM", Side.BUY, msgFactory, fixDD);
    }

    /** Create a few default fields and verify they get placed
     * into the message
     */
    public void testInsertDefaultFields() throws Exception
    {
        OutgoingMessageHandler handler = new MyOutgoingMessageHandler(msgFactory);
        handler.setOrderRouteManager(new MessageRouteManager());
        handler.setMessageModifierMgr(new MessageModifierManager(getMessageModifiers(), msgFactory));
        NullQuickFIXSender quickFIXSender = new NullQuickFIXSender();
		handler.setQuickFIXSender(quickFIXSender);

        Message msg = msgFactory.newMarketOrder("bob", Side.BUY, new BigDecimal(100), new MSymbol("IBM"),
                                                      TimeInForce.DAY, "bob");
        Message response = handler.handleMessage(msg);

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
        response.setField(new OrderID("fake-order-id"));
        fixDD.getDictionary().validate(response, true);
    }

    public void testImmediateReportAfterRouteMgr() throws Exception {
        OutgoingMessageHandler handler = new MyOutgoingMessageHandler(msgFactory);
        MessageRouteManager routeManager = new MessageRouteManager();
        routeManager.setSeparateSuffix(true);
        handler.setOrderRouteManager(routeManager);
        handler.setMessageModifierMgr(new MessageModifierManager(new LinkedList<MessageModifier>(), msgFactory));
        NullQuickFIXSender quickFIXSender = new NullQuickFIXSender();
		handler.setQuickFIXSender(quickFIXSender);

        Message msg = msgFactory.newMarketOrder("bob", Side.BUY, new BigDecimal(100), new MSymbol("EUR/USD"),
                                                      TimeInForce.DAY, "bob");
        Message response = handler.handleMessage(msg);
        assertNotNull(quickFIXSender.getCapturedMessages().get(0));
        assertEquals("no symbol suffix in sent msg", "USD", quickFIXSender.getCapturedMessages().get(0).getString(SymbolSfx.FIELD));

        assertNotNull(response);
        assertEquals("verify symbol has been separated", "EUR", response.getString(Symbol.FIELD));

        // this is to test #362
        assertEquals("didn't pick up SymbolSfx", "USD", response.getString(SymbolSfx.FIELD));
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
        OutgoingMessageHandler handler = new MyOutgoingMessageHandler(msgFactory);
        handler.setOrderRouteManager(new MessageRouteManager());
        NullQuickFIXSender quickFIXSender = new NullQuickFIXSender();
		handler.setQuickFIXSender(quickFIXSender);

		Message newOrder = msgFactory.newMarketOrder("bob", Side.BUY, new BigDecimal(100), new MSymbol("IBM"),
                                                      TimeInForce.DAY, "bob");
        Message cancelOrder = msgFactory.newCancel("bob", "bob",
                                                    Side.SELL, new BigDecimal(7), new MSymbol("TOLI"), "redParty");

        List<Message> orderList = Arrays.asList(newOrder, cancelOrder);
        List<Message> responses = new LinkedList<Message>();
        for (Message message : orderList) {
            responses.add(handler.handleMessage(message));
		}
        
        // verify that we have 2 orders on the mQF sink and 1 on the incomingJMS
        assertEquals("not enough events on the QF output", 2, quickFIXSender.getCapturedMessages().size());
        assertEquals("first output should be outgoing execReport", MsgType.EXECUTION_REPORT,
                     responses.get(0).getHeader().getString(MsgType.FIELD));
        assertEquals("2nd event should be original buy order", newOrder,
                quickFIXSender.getCapturedMessages().get(0));
        assertEquals("3rd event should be cancel order", cancelOrder,
                quickFIXSender.getCapturedMessages().get(1));

        // put an orderID in since immediate execReport doesn't have one and we need one for validation
        responses.get(0).setField(new OrderID("fake-order-id"));
        verifyExecutionReport(responses.get(0));
    }

    /** verify that sending a malformed buy order (ie missing Side) results in a reject exectuionReport */
    public void testHandleMalformedEvent() throws Exception {
        Message buyOrder = FIXMessageUtilTest.createNOS("toli", 12.34, 234, Side.BUY, msgFactory);
        buyOrder.removeField(Side.FIELD);

        OutgoingMessageHandler handler = new MyOutgoingMessageHandler(fixVersion.getMessageFactory());
        handler.setOrderRouteManager(new MessageRouteManager());
        NullQuickFIXSender quickFIXSender = new NullQuickFIXSender();
		handler.setQuickFIXSender(quickFIXSender);

		final Message result = handler.handleMessage(buyOrder);
		assertNotNull(result);
		assertEquals(0, quickFIXSender.getCapturedMessages().size());
		assertEquals("first output should be outgoing execReport", MsgType.EXECUTION_REPORT,
                     result.getHeader().getString(MsgType.FIELD));
        assertEquals("should be a reject execReport", OrdStatus.REJECTED, result.getChar(OrdStatus.FIELD));
        assertTrue("Error message should say field Side was missing", result.getString(Text.FIELD).contains("field Side"));
        if(!msgFactory.getBeginString().equals(FIXVersion.FIX40.toString())) {
            assertEquals("execType should be a reject", ExecType.REJECTED, result.getChar(ExecType.FIELD));
        }
        // validation will fail b/c we didn't send a side in to begin with
        new ExpectedTestFailure(RuntimeException.class, "field="+Side.FIELD) {
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
        OutgoingMessageHandler handler = new MyOutgoingMessageHandler(fixVersion.getMessageFactory());
        handler.setOrderRouteManager(new MessageRouteManager());
        NullQuickFIXSender quickFIXSender = new NullQuickFIXSender();
		handler.setQuickFIXSender(quickFIXSender);

    	Message buyOrder = FIXMessageUtilTest.createNOS("toli", 12.34, 234, Side.BUY, msgFactory);
        buyOrder.setString(Price.FIELD, "23.23.3");

        assertNotNull(buyOrder.getString(ClOrdID.FIELD));
        Message result = handler.handleMessage(buyOrder);
        assertNotNull(result);
        assertEquals("first output should be outgoing execReport", MsgType.EXECUTION_REPORT,
        		result.getHeader().getString(MsgType.FIELD));
        assertEquals("should be a reject execReport", OrdStatus.REJECTED, result.getChar(OrdStatus.FIELD));
        if(!msgFactory.getBeginString().equals(FIXVersion.FIX40.toString())) {
            assertEquals("execType should be a reject", ExecType.REJECTED, result.getChar(ExecType.FIELD));
        }
        assertNotNull("rejectExecReport doesn't have a ClOrdID set", result.getString(ClOrdID.FIELD));
        assertNotNull("no useful rejection message", result.getString(Text.FIELD));
    }

    /** brain-dead: make sure that incoming orders just get placed on the sink */
    @SuppressWarnings("unchecked")
    public void testHandleFIXMessages() throws Exception
    {
        OutgoingMessageHandler handler = new MyOutgoingMessageHandler(msgFactory);
        handler.setOrderRouteManager(new MessageRouteManager());
        NullQuickFIXSender quickFIXSender = new NullQuickFIXSender();
		handler.setQuickFIXSender(quickFIXSender);

		Message newOrder = msgFactory.newCancelReplaceShares("bob", "orig", new BigDecimal(100));
		newOrder.setField(new Symbol("ASDF"));
		Message cancelOrder = msgFactory.newCancel("bob", "bob",
                                                    Side.SELL, new BigDecimal(7), new MSymbol("TOLI"), "redParty");
        handler.handleMessage(newOrder);
        handler.handleMessage(cancelOrder);

        assertEquals("not enough events on the OM quickfix sink", 2, quickFIXSender.getCapturedMessages().size());
        assertEquals("1st event should be original buy order", newOrder,
        		quickFIXSender.getCapturedMessages().get(0));
        assertEquals("2nd event should be cancel order", cancelOrder,
        		quickFIXSender.getCapturedMessages().get(1));
        fixDD.getDictionary().validate(quickFIXSender.getCapturedMessages().get(1), true);
    }

    public void testInvalidSessionID() throws Exception {
        OutgoingMessageHandler handler = new MyOutgoingMessageHandler(msgFactory);
        handler.setOrderRouteManager(new MessageRouteManager());
        SessionID sessionID = new SessionID(msgFactory.getBeginString(), "no-sender", "no-target");
        handler.setDefaultSessionID(sessionID);
        QuickFIXSender quickFIXSender = new QuickFIXSender();
		handler.setQuickFIXSender(quickFIXSender);

	    Message newOrder = msgFactory.newMarketOrder("123", Side.BUY, new BigDecimal(100), new MSymbol("SUNW"),
                TimeInForce.DAY, "dummyaccount");

        // verify we got an execReport that's a rejection with the sessionNotfound error message
        Message result = handler.handleMessage(newOrder);
        verifyRejection(result, msgFactory, true, MessageKey.SESSION_NOT_FOUND, sessionID);
    }

    /** Create props with a route manager entry, and make sure the FIX message is
     * modified but the other ones are not
     * @throws Exception
     */
    public void testWithOrderRouteManager() throws Exception {
        OutgoingMessageHandler handler = new MyOutgoingMessageHandler(msgFactory);
        MessageRouteManager orm = OrderRouteManagerTest.getORMWithOrderRouting(MessageRouteManager.FIELD_100_METHOD);
        handler.setOrderRouteManager(orm);

        final NullQuickFIXSender quickFIXSender = new NullQuickFIXSender();
		handler.setQuickFIXSender(quickFIXSender);

    	
        // 1. create a "incoming JMS buy" order and verify that it doesn't have routing in it
        final Message newOrder = msgFactory.newMarketOrder("bob", Side.BUY, new BigDecimal(100), new MSymbol("IBM"),
                                                      TimeInForce.DAY, "bob");
        // verify there's no route info
        new ExpectedTestFailure(FieldNotFound.class) {
            protected void execute() throws Throwable {
                newOrder.getField(new ExDestination());
            }
        }.run();

        Message result = handler.handleMessage(newOrder);

        assertNotNull(result);
        assertEquals(1, quickFIXSender.getCapturedMessages().size());
        new ExpectedTestFailure(FieldNotFound.class) {
            protected void execute() throws Throwable {
                newOrder.getHeader().getString(100);
                quickFIXSender.getCapturedMessages().get(1).getField(new ExDestination());
        }}.run();

        // now send a FIX-related message through order manager and make sure routing does show up
        orderRouterTesterHelper(handler, "BRK/A", null, "A");
        orderRouterTesterHelper(handler, "IFLI.IM", "Milan", null);
        orderRouterTesterHelper(handler, "BRK/A.N", "SIGMA", "A");
    }

    public void testIncomingNullMessage() throws Exception {
        OutgoingMessageHandler handler = new MyOutgoingMessageHandler(msgFactory);
        assertNull(handler.handleMessage(null));        
    }

    /** verify the OMS sends back a rejection when it receives a message of incompatible or unknown verison
     * this test is hardcoded with OMS at fix40 so exclude it from multi-version tests */
    public void testIncompatibleFIXVersions() throws Exception {
        OutgoingMessageHandler handler = new MyOutgoingMessageHandler(FIXVersion.FIX40.getMessageFactory());
        Message msg = new quickfix.fix41.Message();
        Message reject = handler.handleMessage(msg);
        assertEquals("didn't get an execution report", MsgType.EXECUTION_REPORT, reject.getHeader().getString(MsgType.FIELD));
        assertEquals("didn't get a reject", OrdStatus.REJECTED+"", reject.getString(OrdStatus.FIELD));
        assertEquals("didn't get a right reason",
                OMSMessageKey.ERROR_MISMATCHED_FIX_VERSION.getLocalizedMessage(FIXVersion.FIX40.toString(), FIXVersion.FIX41.toString()),
                reject.getString(Text.FIELD));

        // now test it with no fix version at all
        reject = handler.handleMessage(new Message());
        verifyRejection(reject, msgFactory, true, OMSMessageKey.ERROR_MALFORMED_MESSAGE_NO_FIX_VERSION);
    }

    public void testOrderListNotSupported() throws Exception {
        OutgoingMessageHandler handler = new MyOutgoingMessageHandler(msgFactory);
        Message orderList = msgFactory.createMessage(MsgType.ORDER_LIST);
        orderList.setField(new Symbol("TOLI"));
        orderList.getHeader().setField(new MsgSeqNum(23));
        Message reject = handler.handleMessage(orderList);
        verifyBMRejection(reject, msgFactory, OMSMessageKey.ERROR_UNSUPPORTED_ORDER_TYPE,
                fixDD.getHumanFieldValue(MsgType.FIELD, MsgType.ORDER_LIST));
    }


    /** verify that OMS rejects messages if it's not connected to a FIX destination */
    public void testMessageRejectedLoggedOutOMS() throws Exception {
        MyOutgoingMessageHandler handler = new MyOutgoingMessageHandler(msgFactory);
        NullQuickFIXSender sender = new NullQuickFIXSender();
        handler.setQuickFIXSender(sender);
        Message execReport = handler.handleMessage(FIXMessageUtilTest.createNOS("TOLI", 23.33, 100, Side.BUY, msgFactory));
        assertEquals(1, sender.getCapturedMessages().size());
        assertEquals(MsgType.EXECUTION_REPORT, execReport.getHeader().getString(MsgType.FIELD));
        assertEquals(OrdStatus.NEW, execReport.getChar(OrdStatus.FIELD));

        // now set it to be logged out and verify a reject
        sender.getCapturedMessages().clear();
        handler.getQFApp().onLogout(new SessionID(msgFactory.getBeginString(), "sender", "target"));
        execReport = handler.handleMessage(FIXMessageUtilTest.createNOS("TOLI", 23.33, 100, Side.BUY, msgFactory));
        assertEquals(0, sender.getCapturedMessages().size());
        verifyRejection(execReport, msgFactory, false, OMSMessageKey.ERROR_NO_DESTINATION_CONNECTION);

        // verify goes through again after log on
        sender.getCapturedMessages().clear();
        handler.getQFApp().onLogon(null);
        execReport = handler.handleMessage(FIXMessageUtilTest.createNOS("TOLI", 23.33, 100, Side.BUY, msgFactory));
        assertEquals(1, sender.getCapturedMessages().size());
        assertEquals(MsgType.EXECUTION_REPORT, execReport.getHeader().getString(MsgType.FIELD));
        assertEquals(OrdStatus.NEW, execReport.getChar(OrdStatus.FIELD));
    }

    /** bug #433 - need to modify the reject being sent back in case the OMS is not logged on
     * verify the reject coming back does not have the OrdStatus set
     */
    public void testOrderCancelRejectWhenOMSNotLoggedOn() throws Exception {
        MyOutgoingMessageHandler handler = new MyOutgoingMessageHandler(msgFactory);
        NullQuickFIXSender sender = new NullQuickFIXSender();
        handler.setQuickFIXSender(sender);

        // now set it to be logged out and verify a reject
        sender.getCapturedMessages().clear();
        handler.getQFApp().onLogout(new SessionID(msgFactory.getBeginString(), "sender", "target"));
        Message reject = handler.handleMessage(msgFactory.newCancelFromMessage(
                FIXMessageUtilTest.createNOS("TOLI", 23.33, 100, Side.BUY, msgFactory)));
        assertEquals(0, sender.getCapturedMessages().size());
        assertEquals(MsgType.ORDER_CANCEL_REJECT, reject.getHeader().getString(MsgType.FIELD));
        assertFalse("OrdStatus should not be set", reject.isSetField(OrdStatus.FIELD));
        assertEquals("didn't get a right reason", OMSMessageKey.ERROR_NO_DESTINATION_CONNECTION.getLocalizedMessage(),
                reject.getString(Text.FIELD));
    }

    /** Test that incoming commands are copied on copy-commands-topic */
    public void testCommandsCopiedToTopic() throws Exception {
        MyOutgoingMessageHandler handler = new MyOutgoingMessageHandler(msgFactory);
        MockJmsTemplate copyJmsTemplate = new MockJmsTemplate();
        handler.setIncomingCommandsCopier(copyJmsTemplate);

        Message nos = FIXMessageUtilTest.createNOS("abc", 10, 100, Side.BUY, msgFactory);
        handler.handleMessage(nos);

        assertEquals(1, copyJmsTemplate.getSentMessages().size());
        assertEquals(nos, copyJmsTemplate.getSentMessages().get(0));
    }


    /** Helper method that takes an OrderManager, the stock symbol and the expect exchange
     * and verifies that the route parsing comes back correct
     * @param symbol    Symbol, can contain either a share class or an exchange (or both)
     * @param   expectedExchange    Exchange we expec (or null)
     * @param   shareClass      Share class (or null)
     */
    private void orderRouterTesterHelper(OutgoingMessageHandler handler, String symbol,
                                         String expectedExchange, String shareClass)
            throws Exception {
        final Message qfMsg = msgFactory.newMarketOrder("bob", Side.BUY, new BigDecimal(100),
                new MSymbol(symbol),  TimeInForce.DAY, "bob");
        NullQuickFIXSender nullQuickFIXSender = ((NullQuickFIXSender)handler.getQuickFIXSender());
		nullQuickFIXSender.getCapturedMessages().clear();
        Message result = handler.handleMessage(qfMsg);

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

    private void verifyBMRejection(Message inMsg, FIXMessageFactory msgFactory, LocalizedMessage msgKey, Object ... args) throws Exception
    {
        if(msgFactory.getBeginString().equals(FIXVersion.FIX40.toString()) ||
                msgFactory.getBeginString().equals(FIXVersion.FIX41.toString())) {
            assertEquals("didn't get a session-level reject", MsgType.REJECT, inMsg.getHeader().getString(MsgType.FIELD));
        } else {
            assertEquals("didn't get a business-message reject", MsgType.BUSINESS_MESSAGE_REJECT, inMsg.getHeader().getString(MsgType.FIELD));
        }
        assertEquals("didn't get a right reason",
                msgKey.getLocalizedMessage(args),
                inMsg.getString(Text.FIELD));
    }

    public static void verifyRejection(Message inMsg, FIXMessageFactory msgFactory, boolean ordStatusExpected,
                                       LocalizedMessage msgKey, Object... args) throws Exception
    {
        assertEquals("didn't get an execution report", MsgType.EXECUTION_REPORT, inMsg.getHeader().getString(MsgType.FIELD));
        if(ordStatusExpected) {
            assertEquals("didn't get a reject", OrdStatus.REJECTED+"", inMsg.getString(OrdStatus.FIELD));
        } else {
            assertFalse("not expected to have OrdStatus", inMsg.isSetField(OrdStatus.FIELD));
        }
        if(!msgFactory.getBeginString().equals(FIXVersion.FIX40.toString())) {
            assertEquals("execType should be a reject", ExecType.REJECTED, inMsg.getChar(ExecType.FIELD));
        }
        assertEquals("didn't get a right reason",
                msgKey.getLocalizedMessage(args),
                inMsg.getString(Text.FIELD));
        assertTrue("rejects should have sending time in them too", inMsg.getHeader().isSetField(SendingTime.FIELD));

    }

    public static class MyOutgoingMessageHandler extends OutgoingMessageHandler {

        private static int factoryStart = (int) Math.round((Math.random() * 1000));

        public MyOutgoingMessageHandler(FIXMessageFactory inFactory)
                throws ConfigError, FieldConvertError, MarketceteraException {
            super(inFactory, OrderLimitsTest.createBasicOrderLimits(),
                    new QuickFIXApplicationTest.MockQuickFIXApplication(inFactory), new InMemoryIDFactory(factoryStart));
            // simulate logon
            qfApp.onLogon(null);
        }
        public MyOutgoingMessageHandler(FIXMessageFactory inFactory, OrderLimits limits)
                throws ConfigError, FieldConvertError, MarketceteraException {
            super(inFactory, limits, new QuickFIXApplicationTest.MockQuickFIXApplication(inFactory),
                    new InMemoryIDFactory(factoryStart));
            // simulate logon
            qfApp.onLogon(null);
        }

        public QuickFIXApplication getQFApp() { return qfApp; }
    }
}
