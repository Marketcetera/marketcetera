package org.marketcetera.oms;

import junit.framework.Test;
import junit.framework.TestCase;
import org.jcyclone.core.handler.EventHandlerException;
import org.marketcetera.core.*;
import org.marketcetera.quickfix.*;
import org.marketcetera.quickfix.DefaultOrderModifier.MessageFieldType;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Toli Kuznets
 * @version $Id$
 */
@ClassVersion("$Id$")
public class OrderManagerTest extends TestCase
{

	/* a bunch of random made-up header/trailer/field values */
    public static final String HEADER_57_VAL = "CERT";
    public static final String HEADER_12_VAL = "12-gauge";
    public static final String TRAILER_2_VAL = "2-trailer";
    public static final String FIELDS_37_VAL = "37-regField";
    public static final String FIELDS_14_VAL = "14-regField";

    public OrderManagerTest(String inName)
   {
       super(inName);
   }

    public static Test suite()
    {
    	OrderManagementSystem.init();
    	return new MarketceteraTestSuite(OrderManagerTest.class, OrderManagementSystem.OMS_MESSAGE_BUNDLE_INFO);
    }

    public void testNewExecutionReportFromOrder() throws Exception
    {
    	OutgoingMessageHandler handler = new OutgoingMessageHandler();
        handler.setOrderRouteManager(new OrderRouteManager());
    	Message newOrder = FIXMessageUtil.newMarketOrder(new InternalID("bob"), Side.BUY, new BigDecimal(100), new MSymbol("IBM"),
                                                      TimeInForce.DAY, new AccountID("bob"));
        Message execReport = handler.executionReportFromNewOrder(newOrder);
        verifyExecutionReport(execReport);
        // verify the acount id is present
        assertEquals("bob", execReport.getString(Account.FIELD));

        // on a non-single order should get back null
        assertNull(handler.executionReportFromNewOrder(FIXMessageUtil.newCancel(new InternalID("bob"), new InternalID("bob"),
                                                                  Side.BUY, new BigDecimal(100), new MSymbol("IBM"), "counterparty")));
    }

    // test one w/out incoming account
    public void testNewExecutionReportFromOrder_noAccount() throws Exception
    {
    	OutgoingMessageHandler handler = new OutgoingMessageHandler();
        handler.setOrderRouteManager(new OrderRouteManager());
        Message newOrder = FIXMessageUtil.newMarketOrder(new InternalID("bob"), Side.BUY, new BigDecimal(100), new MSymbol("IBM"),
                                                      TimeInForce.DAY, new AccountID("bob"));
        // remove account ID
        newOrder.removeField(Account.FIELD);

        final Message execReport = handler.executionReportFromNewOrder(newOrder);
        verifyExecutionReport(execReport);
        // verify the acount id is not present
        (new ExpectedTestFailure(FieldNotFound.class) {
            protected void execute() throws Exception {
                execReport.getString(Account.FIELD);
            }
        }).run();
    }

    private void verifyExecutionReport(Message inExecReport) throws Exception
    {
        FIXMessageUtilTest.verifyExecutionReport(inExecReport, "100", "IBM", Side.BUY);
    }

    /** Create a configData that creates a few default fields and verify they get placed
     * into the message
     */
    public void testInsertDefaultFields() throws Exception
    {

        OutgoingMessageHandler handler = new OutgoingMessageHandler();
        handler.setOrderRouteManager(new OrderRouteManager());
        handler.setOrderModifiers(getOrderModifiers());
        NullQuickFIXSender quickFIXSender = new NullQuickFIXSender();
		handler.setQuickFIXSender(quickFIXSender);

        Message msg = FIXMessageUtil.newMarketOrder(new InternalID("bob"), Side.BUY, new BigDecimal(100), new MSymbol("IBM"),
                                                      TimeInForce.DAY, new AccountID("bob"));
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

        // verify that transaction date is set as well, but it'd be set anyway b/c new order sets it
        assertNotNull(modifiedMessage.getString(TransactTime.FIELD));
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
        OutgoingMessageHandler handler = new OutgoingMessageHandler();
        handler.setOrderRouteManager(new OrderRouteManager());
        NullQuickFIXSender quickFIXSender = new NullQuickFIXSender();
		handler.setQuickFIXSender(quickFIXSender);

		Message newOrder = FIXMessageUtil.newMarketOrder(new InternalID("bob"), Side.BUY, new BigDecimal(100), new MSymbol("IBM"),
                                                      TimeInForce.DAY, new AccountID("bob"));
        Message cancelOrder = FIXMessageUtil.newCancel(new InternalID("bob"), new InternalID("bob"),
                                                    Side.SELL, new BigDecimal(7), new MSymbol("TOLI"), "redParty");

        List<Message> orderList = Arrays.asList(new Message [] {newOrder, cancelOrder});
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

        verifyExecutionReport(responses.get(0));
    }

    /** verify that sendign a malformed buy order (ie missing Side) results in a reject exectuionReport */
    public void testHandleMalformedEvent() throws Exception {
        Message buyOrder = FIXMessageUtilTest.createNOS("toli", 12.34, 234, Side.BUY);
        buyOrder.removeField(Side.FIELD);

        OutgoingMessageHandler handler = new OutgoingMessageHandler();
        handler.setOrderRouteManager(new OrderRouteManager());
        NullQuickFIXSender quickFIXSender = new NullQuickFIXSender();
		handler.setQuickFIXSender(quickFIXSender);

		Message result = handler.handleMessage(buyOrder);
		assertNotNull(result);
		assertEquals(0, quickFIXSender.getCapturedMessages().size());
		assertEquals("first output should be outgoing execReport", MsgType.EXECUTION_REPORT,
                     result.getHeader().getString(MsgType.FIELD));
        assertEquals("should be a reject execReport", OrdStatus.REJECTED, result.getChar(OrdStatus.FIELD));
        assertEquals("execType should be a reject", ExecType.REJECTED, result.getChar(ExecType.FIELD));
    }

    /** Basically, this is a test for bug #15 where any error in the internal code
     * should result in a rejection being sent back.
     * @throws Exception
     */
    public void testMalformedPrice() throws Exception {
        OutgoingMessageHandler handler = new OutgoingMessageHandler();
        handler.setOrderRouteManager(new OrderRouteManager());
        NullQuickFIXSender quickFIXSender = new NullQuickFIXSender();
		handler.setQuickFIXSender(quickFIXSender);

    	Message buyOrder = FIXMessageUtilTest.createNOS("toli", 12.34, 234, Side.BUY);
        buyOrder.setString(Price.FIELD, "23.23.3");

        assertNotNull(buyOrder.getString(ClOrdID.FIELD));
        Message result = handler.handleMessage(buyOrder);
        assertNotNull(result);
        assertEquals("first output should be outgoing execReport", MsgType.EXECUTION_REPORT,
        		result.getHeader().getString(MsgType.FIELD));
        assertEquals("should be a reject execReport", OrdStatus.REJECTED, result.getChar(OrdStatus.FIELD));
        assertEquals("execType should be a reject", ExecType.REJECTED, result.getChar(ExecType.FIELD));
        assertNotNull("rejectExecReport doesn't have a ClOrdID set", result.getString(ClOrdID.FIELD));
        assertNotNull("no useful rejection message", result.getString(Text.FIELD));
    }

    /** brain-dead: make sure that incoming orders just get placed on the sink */
    @SuppressWarnings("unchecked")
    public void testHandleFIXMessages() throws Exception
    {
        OutgoingMessageHandler handler = new OutgoingMessageHandler();
        handler.setOrderRouteManager(new OrderRouteManager());
        NullQuickFIXSender quickFIXSender = new NullQuickFIXSender();
		handler.setQuickFIXSender(quickFIXSender);

		Message newOrder = FIXMessageUtil.newCancelReplaceShares(new InternalID("bob"), new InternalID("orig"), new BigDecimal(100));
		newOrder.setField(new Symbol("ASDF"));
		Message cancelOrder = FIXMessageUtil.newCancel(new InternalID("bob"), new InternalID("bob"),
                                                    Side.SELL, new BigDecimal(7), new MSymbol("TOLI"), "redParty");
        handler.handleMessage(newOrder);
        handler.handleMessage(cancelOrder);

        assertEquals("not enough events on the OM quickfix sink", 2, quickFIXSender.getCapturedMessages().size());
        assertEquals("1st event should be original buy order", newOrder,
        		quickFIXSender.getCapturedMessages().get(0));
        assertEquals("2st event should be cancel order", cancelOrder,
        		quickFIXSender.getCapturedMessages().get(1));
    }

    /** Create props with a route manager entry, and make sure the FIX message is
     * modified but the other ones are not
     * @throws Exception
     */
    public void testWithOrderRouteManager() throws Exception {
        OutgoingMessageHandler handler = new OutgoingMessageHandler();
        OrderRouteManager orm = OrderRouteManagerTest.getORMWithOrderRouting();
        handler.setOrderRouteManager(orm);

        final NullQuickFIXSender quickFIXSender = new NullQuickFIXSender();
		handler.setQuickFIXSender(quickFIXSender);

    	
        // 1. create a "incoming JMS buy" order and verify that it doesn't have routing in it
        final Message newOrder = FIXMessageUtil.newMarketOrder(new InternalID("bob"), Side.BUY, new BigDecimal(100), new MSymbol("IBM"),
                                                      TimeInForce.DAY, new AccountID("bob"));
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

    /** Helper method that takes an OrderManager, the stock symbol and the expect exchange
     * and verifies that the route parsing comes back correct
     * @param symbol    Symbol, can contain either a share class or an exchange (or both)
     * @param   expectedExchange    Exchange we expec (or null)
     * @param   shareClass      Share class (or null)
     * @throws EventHandlerException
     * @throws FieldNotFound
     */
    private void orderRouterTesterHelper(OutgoingMessageHandler handler, String symbol,
                                         String expectedExchange, String shareClass)
            throws Exception {
        final Message qfMsg = FIXMessageUtil.newMarketOrder(new InternalID("bob"), Side.BUY, new BigDecimal(100),
                new MSymbol(symbol),  TimeInForce.DAY, new AccountID("bob"));
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
    public static List<OrderModifier> getOrderModifiers()
    {
    	List<OrderModifier> orderModifiers = new LinkedList<OrderModifier>();

    	DefaultOrderModifier defaultOrderModifier = new DefaultOrderModifier();
    	defaultOrderModifier.addDefaultField(57, HEADER_57_VAL, MessageFieldType.HEADER);
    	orderModifiers.add(defaultOrderModifier);

    	defaultOrderModifier = new DefaultOrderModifier();
    	defaultOrderModifier.addDefaultField(12, HEADER_12_VAL, MessageFieldType.HEADER);
    	orderModifiers.add(defaultOrderModifier);

    	defaultOrderModifier = new DefaultOrderModifier();
    	defaultOrderModifier.addDefaultField(2, TRAILER_2_VAL, MessageFieldType.TRAILER);
    	orderModifiers.add(defaultOrderModifier);

    	defaultOrderModifier = new DefaultOrderModifier();
    	defaultOrderModifier.addDefaultField(37, FIELDS_37_VAL, MessageFieldType.MESSAGE);
    	orderModifiers.add(defaultOrderModifier);

    	defaultOrderModifier = new DefaultOrderModifier();
    	defaultOrderModifier.addDefaultField(14, FIELDS_14_VAL, MessageFieldType.MESSAGE);
    	orderModifiers.add(defaultOrderModifier);
    	
    	return orderModifiers;
    }

}
