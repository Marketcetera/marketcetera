package org.marketcetera.oms;

import java.util.*;
import java.math.BigDecimal;

import junit.framework.Test;
import junit.framework.TestCase;

import quickfix.field.*;
import quickfix.Message;
import quickfix.FieldNotFound;
import org.marketcetera.quickfix.*;
import org.marketcetera.core.*;
import org.marketcetera.jcyclone.FIXStageOutput;
import org.marketcetera.jcyclone.StageElement;
import org.marketcetera.jcyclone.DummyJMSStageOutput;
import org.marketcetera.jcyclone.JMSStageOutput;
import org.jcyclone.core.handler.EventHandlerException;

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
        try {
            // create an instance of OMS
            new OrderManagementSystemIT.MyOMS(OrderManagementSystem.CONFIG_FILE_NAME);
        } catch (ConfigFileLoadingException ex) {
            // do nothing
        }
        return new MarketceteraTestSuite(OrderManagerTest.class, OrderManagementSystem.OMS_MESSAGE_BUNDLE_INFO);
    }

    public void testNewExecutionReportFromOrder() throws Exception
    {
        ConfigData props = new PropertiesConfigData(new Properties());
        OrderManager om = new OrderManager();
        om.postInitialize(props);
        Message newOrder = FIXMessageUtil.newMarketOrder(new InternalID("bob"), Side.BUY, new BigDecimal(100), new MSymbol("IBM"),
                                                      TimeInForce.DAY, new AccountID("bob"));
        Message execReport = om.executionReportFromNewOrder(newOrder);
        verifyExecutionReport(execReport);
        // verify the acount id is present
        assertEquals("bob", execReport.getString(Account.FIELD));

        // on a non-single order should get back null
        assertNull(om.executionReportFromNewOrder(FIXMessageUtil.newCancel(new InternalID("bob"), new InternalID("bob"),
                                                                  Side.BUY, new BigDecimal(100), new MSymbol("IBM"), "counterparty")));
    }

    // test one w/out incoming account
    public void testNewExecutionReportFromOrder_noAccount() throws Exception
    {
        ConfigData props = new PropertiesConfigData(new Properties());
        OrderManager om = new OrderManager();
        om.postInitialize(props);
        Message newOrder = FIXMessageUtil.newMarketOrder(new InternalID("bob"), Side.BUY, new BigDecimal(100), new MSymbol("IBM"),
                                                      TimeInForce.DAY, new AccountID("bob"));
        // remove account ID
        newOrder.removeField(Account.FIELD);

        final Message execReport = om.executionReportFromNewOrder(newOrder);
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

    /** Put one extra field in the config and make sure that it appears
     * in the vanilla message after initializing the modifier and running the message
     * through it
     * @throws Exception
     */
    public void testReadDefaultFieldHelper() throws Exception
    {
        Properties props = new Properties();
        props.setProperty(OrderManager.FIX_HEADER_PREFIX+"57", HEADER_57_VAL);

        DefaultOrderModifier mod = new DefaultOrderModifier();
        PropertiesConfigData pcd = new PropertiesConfigData(props);
        mod.init(pcd);

        OrderManager om = new OrderManager();
        om.readDefaultFieldsHelper(pcd, OrderManager.FIX_HEADER_PREFIX+"57",
                OrderManager.FIX_HEADER_PREFIX, mod,
                DefaultOrderModifier.MessageFieldType.HEADER);

        Message msg = FIXMessageUtil.createNewMessage();
        assertTrue(mod.modifyOrder(msg));
        assertEquals(HEADER_57_VAL, msg.getHeader().getString(57));
    }

    /** Create a configData that creates a few default fields and verify they get placed
     * into the message
     */
    public void testInsertDefaultFields() throws Exception
    {
        Properties props = getPropsWithDefaults();
        DefaultOrderModifier mod = new DefaultOrderModifier();
        mod.init(new PropertiesConfigData(props));

        MyOrderManager mom = new MyOrderManager();
        mom.postInitialize(new PropertiesConfigData(props));
        mom.defaultFieldModifier = mod; // set the parent's modifier

        Message msg = FIXMessageUtil.newMarketOrder(new InternalID("bob"), Side.BUY, new BigDecimal(100), new MSymbol("IBM"),
                                                      TimeInForce.DAY, new AccountID("bob"));
        FIXStageOutput output = new FIXStageOutput(msg, null);
        mom.handleEvent(output);

        assertEquals(2, mom.sink.size());
        assertEquals(JMSStageOutput.class, mom.sink.events.get(0).getClass());
        FIXStageOutput modifiedOutput = (FIXStageOutput) mom.sink.events.get(1);
        Message modifiedMessage = (Message) modifiedOutput.getElement();

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
        Message newOrder = FIXMessageUtil.newMarketOrder(new InternalID("bob"), Side.BUY, new BigDecimal(100), new MSymbol("IBM"),
                                                      TimeInForce.DAY, new AccountID("bob"));
        Message cancelOrder = FIXMessageUtil.newCancel(new InternalID("bob"), new InternalID("bob"),
                                                    Side.SELL, new BigDecimal(7), new MSymbol("TOLI"), "redParty");
        MyOrderManager mom = new MyOrderManager();

        List orderList = Arrays.asList(new StageElement[]{new JMSStageOutput(newOrder, null),
                new JMSStageOutput(cancelOrder, null)});
        mom.handleEvents(orderList);
        // verify that we have 2 orders on the mQF sink and 1 on the incomingJMS
        assertEquals("not enough events on the OM output sink", 3, mom.sink.events.size());
        StageElement output = (StageElement)mom.sink.events.get(0);
        assertEquals("first output should be outgoing execReport", MsgType.EXECUTION_REPORT,
                     ((Message)output.getElement()).getHeader().getString(MsgType.FIELD));
        assertEquals("2nd event should be original buy order", newOrder,
                ((StageElement)mom.sink.events.get(1)).getElement());
        assertEquals("3rd event should be cancel order", cancelOrder,
                ((StageElement)mom.sink.events.get(2)).getElement());

        verifyExecutionReport((Message)((JMSStageOutput)mom.sink.events.get(0)).getElement());
    }

    /** verify that sendign a malformed buy order (ie missing Side) results in a reject exectuionReport */
    public void testHandleMalformedEvent() throws Exception {
        Message buyOrder = FIXMessageUtilTest.createNOS("toli", 12.34, 234, Side.BUY);
        buyOrder.removeField(Side.FIELD);
        MyOrderManager mom = new MyOrderManager();

        mom.handleEvent(new JMSStageOutput(buyOrder, null));
        assertEquals("should only get 1 reject", 1, mom.sink.events.size());
        Message outMsg = (Message) ((StageElement)mom.sink.events.get(0)).getElement();
        assertEquals("first output should be outgoing execReport", MsgType.EXECUTION_REPORT,
                     outMsg.getHeader().getString(MsgType.FIELD));
        assertEquals("should be a reject execReport", OrdStatus.REJECTED, outMsg.getChar(OrdStatus.FIELD));
        assertEquals("execType should be a reject", ExecType.REJECTED, outMsg.getChar(ExecType.FIELD));
    }

    /** Basically, this is a test for bug #15 where any error in the internal code
     * should result in a rejection being sent back.
     * @throws Exception
     */
    public void testMalformedPrice() throws Exception {
        Message buyOrder = FIXMessageUtilTest.createNOS("toli", 12.34, 234, Side.BUY);
        buyOrder.setString(Price.FIELD, "23.23.3");
        MyOrderManager mom = new MyOrderManager();

        assertNotNull(buyOrder.getString(ClOrdID.FIELD));
        mom.handleEvent(new JMSStageOutput(buyOrder, null));
        assertEquals("should only get 1 reject", 1, mom.sink.events.size());
        Message outMsg = (Message) ((StageElement)mom.sink.events.get(0)).getElement();
        assertEquals("first output should be outgoing execReport", MsgType.EXECUTION_REPORT,
                     outMsg.getHeader().getString(MsgType.FIELD));
        assertEquals("should be a reject execReport", OrdStatus.REJECTED, outMsg.getChar(OrdStatus.FIELD));
        assertEquals("execType should be a reject", ExecType.REJECTED, outMsg.getChar(ExecType.FIELD));
        assertNotNull("rejectExecReport doesn't have a ClOrdID set", outMsg.getString(ClOrdID.FIELD));
        assertNotNull("no useful rejection message", outMsg.getString(Text.FIELD));
    }

    /** brain-dead: make sure that incoming orders just get placed on the sink */
    @SuppressWarnings("unchecked")
    public void testHandleFIXMessages() throws Exception
    {
        Message newOrder = FIXMessageUtil.newCancelReplaceShares(new InternalID("bob"), new InternalID("orig"), new BigDecimal(100));
        Message cancelOrder = FIXMessageUtil.newCancel(new InternalID("bob"), new InternalID("bob"),
                                                    Side.SELL, new BigDecimal(7), new MSymbol("TOLI"), "redParty");
        MyOrderManager mom = new MyOrderManager();

        ArrayList orderList = new ArrayList(Arrays.asList(new StageElement[]{new JMSStageOutput(newOrder, null),
                new JMSStageOutput(cancelOrder, null)}));
        mom.handleEvents(orderList);
        assertEquals("not enough events on the OM quickfix sink", 2, mom.sink.events.size());
        assertEquals("1st event should be original buy order", newOrder,
                ((StageElement)mom.sink.events.get(0)).getElement());
        assertEquals("2st event should be cancel order", cancelOrder,
                ((StageElement)mom.sink.events.get(1)).getElement());
    }

    /** Create props with a route manager entry, and make sure the FIX message is
     * modified but the other ones are not
     * @throws Exception
     */
    public void testWithOrderRouteManager() throws Exception {
        ConfigData props = OrderRouteManagerTest.getPropsWithOrderRouting();
        final MyOrderManager mom = new MyOrderManager();
        mom.postInitialize(props);

        // 1. create a "incoming JMS buy" order and verify that it doesn't have routing in it
        final Message newOrder = FIXMessageUtil.newMarketOrder(new InternalID("bob"), Side.BUY, new BigDecimal(100), new MSymbol("IBM"),
                                                      TimeInForce.DAY, new AccountID("bob"));
        // verify there's no route info
        new ExpectedTestFailure(FieldNotFound.class) {
            protected void execute() throws Throwable {
                newOrder.getField(new ExDestination());
            }
        }.run();

        mom.handleEvent(new DummyJMSStageOutput(newOrder, null));

        // verify got 2 orders and that none of them have route info in them
        assertEquals(2, mom.sink.events.size());
        new ExpectedTestFailure(FieldNotFound.class) {
            protected void execute() throws Throwable {
                newOrder.getHeader().getString(100);
                ((Message)((StageElement)mom.sink.events.get(1)).getElement()).getField(new ExDestination());
        }}.run();

        // now send a FIX-related message through order manager and make sure routing does show up
        orderRouterTesterHelper(mom, "BRK/A", null, "A");
        orderRouterTesterHelper(mom, "IFLI.IM", "Milan", null);
        orderRouterTesterHelper(mom, "BRK/A.N", "SIGMA", "A");
    }

    /** Helper method that takes an OrderManager, the stock symbol and the expect exchange
     * and verifies that the route parsing comes back correct
     * @param mom
     * @param symbol    Symbol, can contain either a share class or an exchange (or both)
     * @param   expectedExchange    Exchange we expec (or null)
     * @param   shareClass      Share class (or null)
     * @throws EventHandlerException
     * @throws FieldNotFound
     */
    private void orderRouterTesterHelper(MyOrderManager mom, String symbol,
                                         String expectedExchange, String shareClass)
            throws Exception {
        final Message qfMsg = FIXMessageUtil.newMarketOrder(new InternalID("bob"), Side.BUY, new BigDecimal(100),
                new MSymbol(symbol),  TimeInForce.DAY, new AccountID("bob"));
        mom.sink.events.clear();
        mom.handleEvent(new FIXStageOutput(qfMsg, null));
        // skip the first event - it'll be auto-generated execReport
        Message incomingMsg = ((Message) ((StageElement) mom.sink.events.get(1)).getElement());
        if(expectedExchange != null) {
            assertEquals(expectedExchange, incomingMsg.getString(ExDestination.FIELD));
        }
        if(shareClass != null) {
            assertEquals(shareClass, incomingMsg.getString(SymbolSfx.FIELD));
        }
    }


    /** Helper method for creating a set of properties with defaults to be reused   */
    public static Properties getPropsWithDefaults()
    {
        Properties props = new Properties();
        props.setProperty(OrderManager.FIX_HEADER_PREFIX+"57", HEADER_57_VAL);
        props.setProperty(OrderManager.FIX_HEADER_PREFIX+"12", HEADER_12_VAL);
        props.setProperty(OrderManager.FIX_TRAILER_PREFIX+"2", TRAILER_2_VAL);
        props.setProperty(OrderManager.FIX_FIELDS_PREFIX+"37", FIELDS_37_VAL);
        props.setProperty(OrderManager.FIX_FIELDS_PREFIX+"14", FIELDS_14_VAL);
        return props;
    }

    /** Subclass ordermanager to subsitute my own sinks */
    private static class MyOrderManager extends OrderManager
    {
        public DummyISink sink = new DummyISink();

        public MyOrderManager() throws Exception{
            FIXDataDictionaryManager.setFIXVersion(QuickFIXInitiator.FIX_VERSION_DEFAULT);
        }

        public DummyISink getNextStage()
        {
            return sink;
        }
    }
}
