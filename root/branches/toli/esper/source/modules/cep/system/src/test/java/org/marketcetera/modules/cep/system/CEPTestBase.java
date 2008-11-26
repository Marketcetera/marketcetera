package org.marketcetera.modules.cep.system;

import org.junit.*;
import static org.junit.Assert.assertEquals;
import org.marketcetera.core.notifications.Notification;
import org.marketcetera.event.*;
import org.marketcetera.event.ExecutionReport;
import org.marketcetera.module.*;
import org.marketcetera.quickfix.CurrentFIXDataDictionary;
import org.marketcetera.quickfix.FIXDataDictionary;
import org.marketcetera.quickfix.FIXMessageUtilTest;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.trade.*;
import quickfix.Message;
import quickfix.field.Symbol;
import quickfix.field.Text;

import java.math.BigDecimal;
import java.util.Map;
import java.util.HashMap;

/**
 * Base case for CEP test classes - has some basic functionality for
 * running data flows through and checking for output
 *
 * @author admin
 * @version $Id$
 * @since $Release$
 */
public abstract class CEPTestBase extends ModuleTestBase {
    protected static ModuleManager sManager;
    protected static DummySink sink;
    protected static Factory factory;

    // Subclasses shoudl specify
    protected abstract ModuleURN getModuleURN();

    @Before public void before() {
        sink = new DummySink();
        sManager.addSinkListener(sink);
    }

    @After
    public void after() {
        sManager.removeSinkListener(sink);
    }

    /** Setup two data flows
     * Send some events through first one
     * Cancel it
     * verify that statements are gone, and then when you send similar events only 2nd data flow gets it
     */
    @Test
    public void testCancel() throws Exception {
        DataFlowID flow1 = sManager.createDataFlow(new DataRequest[] {
                // Copier -> System: send 3 events
                new DataRequest(CopierModuleFactory.INSTANCE_URN, new EventBase[] {
                        new TradeEvent(3, 4, "IBM", "NYSE", new BigDecimal("85"), new BigDecimal("200")),
                        new BidEvent(1, 2, "IBM", "NYSE", new BigDecimal("85"), new BigDecimal("100")),
                        new AskEvent(5, 6, "JAVA", "NASDAQ", new BigDecimal("1.23"), new BigDecimal("300"))
                }),
                // System -> Sink: only get 1 bid event
                new DataRequest(getModuleURN(), "select * from "+BidEvent.class.getName())
        });

        BidEvent theBid = (BidEvent) sink.getReceived().take();
        assertEquals("didnt' get bid event", "IBM", theBid.getSymbol());
        assertEquals("didnt' get right size", new BigDecimal("85"), theBid.getPrice());

        sManager.cancel(flow1);

        DataFlowID flow2 = sManager.createDataFlow(new DataRequest[] {
                // Copier -> System: send 3 events
                new DataRequest(CopierModuleFactory.INSTANCE_URN, new EventBase[] {
                        new BidEvent(1, 2, "GOOG", "NYSE", new BigDecimal("300"), new BigDecimal("100")),
                        new TradeEvent(3, 4, "IBM", "NYSE", new BigDecimal("85"), new BigDecimal("200")),
                        new AskEvent(5, 6, "JAVA", "NASDAQ", new BigDecimal("1.23"), new BigDecimal("300"))
                }),
                // System -> Sink: only get 1 bid event
                new DataRequest(getModuleURN(), "select * from "+BidEvent.class.getName())
        });
        theBid = (BidEvent) sink.getReceived().take();
        assertEquals("didnt' get bid event", "GOOG", theBid.getSymbol());
        assertEquals("didnt' get right size", new BigDecimal("300"), theBid.getPrice());
        sManager.cancel(flow2);
    }

    /** Run all the varous event types through */
    @Test
    public void testAsk() throws Exception {
        // ask
        flowTestHelper(CEPDataTypes.ASK, new EventBase[] {
                new AskEvent(1, 2, "ABC", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                new BidEvent(1, 2, "ABC", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                new AskEvent(1, 2, "GOOG", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                new TradeEvent(1, 2, "GOOG", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                new AskEvent(1, 2, "TOLI", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                new BidEvent(1, 2, "ABC", "nyse", new BigDecimal("23"), new BigDecimal("23")),
        }, new String[] {"ABC", "GOOG", "TOLI"});
        // package.ask
        flowTestHelper(AskEvent.class.getName(), new EventBase[] {
                new AskEvent(1, 2, "ABC", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                new BidEvent(1, 2, "ABC", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                new AskEvent(1, 2, "GOOG", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                new TradeEvent(1, 2, "GOOG", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                new AskEvent(1, 2, "TOLI", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                new BidEvent(1, 2, "ABC", "nyse", new BigDecimal("23"), new BigDecimal("23")),
        }, new String[] {"ABC", "GOOG", "TOLI"});
    }

    public void testBid() throws Exception {
        // bid
        flowTestHelper(CEPDataTypes.BID, new EventBase[] {
                new AskEvent(1, 2, "ABC", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                new BidEvent(1, 2, "ABC", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                new AskEvent(1, 2, "GOOG", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                new TradeEvent(1, 2, "GOOG", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                new AskEvent(1, 2, "TOLI", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                new BidEvent(1, 2, "XYZ", "nyse", new BigDecimal("23"), new BigDecimal("23")),
        }, new String[] {"ABC", "XYZ"});
        // package.bid
        flowTestHelper(BidEvent.class.getName(), new EventBase[] {
                new AskEvent(1, 2, "ABC", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                new BidEvent(1, 2, "ABC", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                new AskEvent(1, 2, "GOOG", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                new TradeEvent(1, 2, "GOOG", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                new AskEvent(1, 2, "TOLI", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                new BidEvent(1, 2, "XYZ", "nyse", new BigDecimal("23"), new BigDecimal("23")),
        }, new String[] {"ABC", "XYZ"});
    }

    @Test
    public void testTrade() throws Exception {
    // trade
        flowTestHelper(CEPDataTypes.TRADE, new EventBase[] {
                new AskEvent(1, 2, "FRO", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                new TradeEvent(1, 2, "FRO", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                new AskEvent(1, 2, "GOOG", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                new TradeEvent(1, 2, "GOOG", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                new AskEvent(1, 2, "TOLI", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                new BidEvent(1, 2, "XYZ", "nyse", new BigDecimal("23"), new BigDecimal("23")),
        }, new String[] {"FRO", "GOOG"});
        // package.trade
        flowTestHelper(TradeEvent.class.getName(), new EventBase[] {
                new AskEvent(1, 2, "FRO", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                new TradeEvent(1, 2, "FRO", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                new AskEvent(1, 2, "GOOG", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                new TradeEvent(1, 2, "GOOG", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                new AskEvent(1, 2, "TOLI", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                new BidEvent(1, 2, "XYZ", "nyse", new BigDecimal("23"), new BigDecimal("23")),
        }, new String[] {"FRO", "GOOG"});
    }

    @Test
    public void testExecutionReport() throws Exception {
        // executionreport
        flowTestHelper(CEPDataTypes.REPORT, new Object[] {
                new AskEvent(1, 2, "IFLI", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                new ExecutionReport(1, 2, "execID", "clOrdID", 'a', 'b', 'c', "IFLI", 'd', BigDecimal.ZERO,
                        BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, "nyse"),
                new AskEvent(1, 2, "GOOG", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                new TradeEvent(1, 2, "GOOG", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                new ExecutionReport(1, 2, "execID", "clOrdID", 'a', 'b', 'c', "GOOG", 'd', BigDecimal.ZERO,
                        BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, "nyse"),
                new BidEvent(1, 2, "XYZ", "nyse", new BigDecimal("23"), new BigDecimal("23")),
        }, new String[] {"IFLI", "GOOG"});
        // package.executinoReport
        flowTestHelper(ExecutionReport.class.getName(), new EventBase[] {
                new AskEvent(1, 2, "IFLI", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                new ExecutionReport(1, 2, "execID", "clOrdID", 'a', 'b', 'c', "IFLI", 'd', BigDecimal.ZERO,
                        BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, "nyse"),
                new AskEvent(1, 2, "GOOG", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                new TradeEvent(1, 2, "GOOG", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                new ExecutionReport(1, 2, "execID", "clOrdID", 'a', 'b', 'c', "GOOG", 'd', BigDecimal.ZERO,
                        BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, "nyse"),
                new BidEvent(1, 2, "XYZ", "nyse", new BigDecimal("23"), new BigDecimal("23")),
        }, new String[] {"IFLI", "GOOG"});
    }

    // validation inspects Text field
    @Test
    public void testOrderCancelReject() throws Exception {
        Message rej1 = FIXVersion.FIX42.getMessageFactory().newOrderCancelReject();
        rej1.setField(new Text("GOOG"));
        OrderCancelReject rejPojo = factory.createOrderCancelReject(rej1, new DestinationID("dest"));
        Message rej2 = FIXVersion.FIX42.getMessageFactory().newOrderCancelReject();
        rej2.setField(new Text("CSCO"));
        OrderCancelReject rejPojo2 = factory.createOrderCancelReject(rej2, new DestinationID("dest"));
        flowTestHelper(CEPDataTypes.CANCEL_REJECT, new Object[] {
                new AskEvent(1, 2, "IFLI", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                rejPojo,
                new AskEvent(1, 2, "GOOG", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                new TradeEvent(1, 2, "GOOG", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                rejPojo2,
        }, new String[] {"GOOG", "CSCO"});
        // package.cancelReject
        flowTestHelper(OrderCancelReject.class.getName(), new Object[] {
                new AskEvent(1, 2, "IFLI", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                rejPojo,
                new AskEvent(1, 2, "GOOG", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                new TradeEvent(1, 2, "GOOG", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                rejPojo2,
        }, new String[] {"GOOG", "CSCO"});
    }

    // on orders we look at destinations
    @Test
    public void testOrderSingle() throws Exception {
        OrderSingle os = factory.createOrderSingle();
        os.setDestinationID(new DestinationID("GOOG"));
        OrderSingle os2 = factory.createOrderSingle();
        os2.setDestinationID(new DestinationID("PFZ"));
        flowTestHelper(CEPDataTypes.ORDER_SINGLE, new Object[] {
                new AskEvent(1, 2, "IFLI", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                os,
                new AskEvent(1, 2, "GOOG", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                new TradeEvent(1, 2, "GOOG", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                os2,
        }, new String[] {"GOOG", "PFZ"});
        // package.order
        flowTestHelper(OrderSingle.class.getName(), new Object[] {
                new AskEvent(1, 2, "IFLI", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                os,
                new AskEvent(1, 2, "GOOG", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                new TradeEvent(1, 2, "GOOG", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                os2,
        }, new String[] {"GOOG", "PFZ"});
    }

    // on orders we look at dest id
    @Test
    public void testOrderCancel() throws Exception {
        CurrentFIXDataDictionary.setCurrentFIXDataDictionary(new FIXDataDictionary(FIXVersion.FIX_SYSTEM.getDataDictionaryURL()));
        Message nos = FIXMessageUtilTest.createNOS("LADA", BigDecimal.ZERO, BigDecimal.ZERO, 'a', FIXVersion.FIX_SYSTEM.getMessageFactory());
        Message can1 = FIXVersion.FIX_SYSTEM.getMessageFactory().newCancelFromMessage(nos);
        OrderCancel cancelPojo = factory.createOrderCancel(can1, new DestinationID("dest1"));
        Message can2 = FIXVersion.FIX_SYSTEM.getMessageFactory().newCancelFromMessage(nos);
        can2.setField(new Symbol("ZAPO"));
        OrderCancel cancelPojo2 = factory.createOrderCancel(can2, new DestinationID("dest2"));
        flowTestHelper(CEPDataTypes.ORDER_CANCEL, new Object[] {
                new AskEvent(1, 2, "IFLI", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                cancelPojo,
                new AskEvent(1, 2, "GOOG", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                new TradeEvent(1, 2, "GOOG", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                cancelPojo2,
        }, new String[] {"dest1", "dest2"});
        // package.orderCANCEL
        flowTestHelper(OrderCancel.class.getName(), new Object[] {
                new AskEvent(1, 2, "IFLI", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                cancelPojo,
                new AskEvent(1, 2, "GOOG", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                new TradeEvent(1, 2, "GOOG", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                cancelPojo2,
        }, new String[] {"dest1", "dest2"});
    }

    // on orders we look at dest id
    @Test
    public void testOrderReplace() throws Exception {
        CurrentFIXDataDictionary.setCurrentFIXDataDictionary(new FIXDataDictionary(FIXVersion.FIX_SYSTEM.getDataDictionaryURL()));
        Message nos = FIXMessageUtilTest.createNOS("LADA", BigDecimal.ZERO, BigDecimal.ZERO, 'a', FIXVersion.FIX_SYSTEM.getMessageFactory());
        Message cxr1 = FIXVersion.FIX_SYSTEM.getMessageFactory().newCancelReplaceFromMessage(nos);
        OrderReplace cxrPojo1 = factory.createOrderReplace(cxr1, new DestinationID("lada"));
        Message cxr2 = FIXVersion.FIX_SYSTEM.getMessageFactory().newCancelReplaceFromMessage(nos);
        cxr2.setField(new Symbol("ZAPO"));
        OrderReplace cxrPojo2 = factory.createOrderReplace(cxr2, new DestinationID("zapo"));
        flowTestHelper(CEPDataTypes.ORDER_REPLACE, new Object[] {
                new AskEvent(1, 2, "IFLI", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                cxrPojo1,
                new AskEvent(1, 2, "GOOG", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                new TradeEvent(1, 2, "GOOG", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                cxrPojo2,
        }, new String[] {"lada", "zapo"});
        // package.orderReplace
        flowTestHelper(OrderReplace.class.getName(), new Object[] {
                new AskEvent(1, 2, "IFLI", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                cxrPojo1,
                new AskEvent(1, 2, "GOOG", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                new TradeEvent(1, 2, "GOOG", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                cxrPojo2,
        }, new String[] {"lada", "zapo"});
    }

    // on orders we look at dest id
    @Test
    public void testFIXOrder() throws Exception {
        CurrentFIXDataDictionary.setCurrentFIXDataDictionary(new FIXDataDictionary(FIXVersion.FIX_SYSTEM.getDataDictionaryURL()));
        Message nos = FIXMessageUtilTest.createNOS("LADA", BigDecimal.ZERO, BigDecimal.ZERO, 'a', FIXVersion.FIX_SYSTEM.getMessageFactory());
        FIXOrder order1 = Factory.getInstance().createOrder(nos, new DestinationID("chuck"));
        FIXOrder order2 = Factory.getInstance().createOrder(nos, new DestinationID("morgan"));
        flowTestHelper(CEPDataTypes.FIX_ORDER, new Object[] {
                new AskEvent(1, 2, "IFLI", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                order1,
                new AskEvent(1, 2, "GOOG", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                new TradeEvent(1, 2, "GOOG", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                order2,
        }, new String[] {"chuck", "morgan"});
        // package.FIXOrder
        flowTestHelper(FIXOrder.class.getName(), new Object[] {
                new AskEvent(1, 2, "IFLI", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                order1,
                new AskEvent(1, 2, "GOOG", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                new TradeEvent(1, 2, "GOOG", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                order2,
        }, new String[] {"chuck", "morgan"});
    }

    // checks on body
    @Test//(timeout=30000)
    public void testNotification() throws Exception {
        Notification notif1 = Notification.low("kathmandu", "kathmandu", this.getClass());
        Notification notif2 = Notification.low("pokhara", "pokhara", this.getClass());
        flowTestHelper(CEPDataTypes.NOTIFICATION, new Object[] {
                new AskEvent(1, 2, "IFLI", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                notif1,
                new AskEvent(1, 2, "GOOG", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                new TradeEvent(1, 2, "GOOG", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                notif2,
        }, new String[] {"kathmandu", "pokhara"});
        // package.Notification
        flowTestHelper(Notification.class.getName(), new Object[] {
                new AskEvent(1, 2, "IFLI", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                notif1,
                new AskEvent(1, 2, "GOOG", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                new TradeEvent(1, 2, "GOOG", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                notif2,
        }, new String[] {"kathmandu", "pokhara"});
    }

    // checks on identifier
    @Test(timeout=30*1000)
    public void testSuggestion() throws Exception {
        Suggestion sug1 = Factory.getInstance().createOrderSingleSuggestion();
        sug1.setIdentifier("acura");
        Suggestion sug2 = Factory.getInstance().createOrderSingleSuggestion();
        sug2.setIdentifier("integra");
        flowTestHelper(CEPDataTypes.SUGGEST, new Object[] {
                new AskEvent(1, 2, "IFLI", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                sug1,
                new AskEvent(1, 2, "GOOG", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                new TradeEvent(1, 2, "GOOG", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                sug2,
        }, new String[] {"acura", "integra"});
        // package.Notification
        flowTestHelper(Suggestion.class.getName(), new Object[] {
                new AskEvent(1, 2, "IFLI", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                sug1,
                new AskEvent(1, 2, "GOOG", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                new TradeEvent(1, 2, "GOOG", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                sug2,
        }, new String[] {"acura", "integra"});
    }

    @Test
    public void testMap() throws Exception {
        Map<Integer, String> map1 = new HashMap<Integer, String>();
        map1.put(0, "bob");
        Map<Integer, String> map2 = new HashMap<Integer, String>();
        map2.put(1, "fred");
        flowTestHelper(CEPDataTypes.MAP, new Object[]{
                new AskEvent(1, 2, "IFLI", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                map1,
                new AskEvent(1, 2, "GOOG", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                new TradeEvent(1, 2, "GOOG", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                map2,
        }, new String[]{"bob", "fred"});
        // package.Map
        flowTestHelper(Map.class.getName(), new Object[]{
                new AskEvent(1, 2, "IFLI", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                map1,
                new AskEvent(1, 2, "GOOG", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                new TradeEvent(1, 2, "GOOG", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                map2,
        }, new String[]{"bob", "fred"});
    }

    @Test
    public void testMarketData() throws Exception {
        Suggestion sug2 = Factory.getInstance().createOrderSingleSuggestion();
        sug2.setIdentifier("integra");
        flowTestHelper(CEPDataTypes.MARKET_DATA, new Object[] {
                new AskEvent(1, 2, "ABC", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                new BidEvent(1, 2, "DEF", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                new AskEvent(1, 2, "GOOG", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                new TradeEvent(1, 2, "LOS", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                sug2,
                new AskEvent(1, 2, "TOLI", "nyse", new BigDecimal("23"), new BigDecimal("23")),
                new BidEvent(1, 2, "XYZ", "nyse", new BigDecimal("23"), new BigDecimal("23")),
        }, new String[] {"ABC", "DEF", "GOOG", "LOS", "TOLI", "XYZ"});
    }

    /** Helper to run multiple data types through the flow. We will be matching on 'symbol', or something
     * similar in the particular event type if that's available
     */
    protected void flowTestHelper(String expectedType, Object[] events, String[] expectedSymbols) throws Exception {
        DataFlowID flowID = sManager.createDataFlow(new DataRequest[] {
                // Copier -> System: send 3 events
                new DataRequest(CopierModuleFactory.INSTANCE_URN, events),
                // System -> Sink: only get 2 trade events
                new DataRequest(getModuleURN(), "select * from "+expectedType)
        });

        for (int i = 0; i < expectedSymbols.length; i++) {
            String expectedSymbol = expectedSymbols[i];
            Object event = sink.getReceived().take();
            if (event instanceof SymbolExchangeEvent) {
                assertEquals("Wrong event received in["+i+"] " + event, ((SymbolExchangeEvent) event).getSymbol(), expectedSymbol);
            } else if (event instanceof ExecutionReport) {
                assertEquals("Wrong event received in["+i+"] " + event, ((ExecutionReport) event).getSymbol(), expectedSymbol);
            } else if (event instanceof OrderCancelReject) {
                assertEquals("Wrong event received in["+i+"] " + event, ((OrderCancelReject) event).getText(), expectedSymbol);
            } else if (event instanceof MessageEvent) {
                assertEquals("Wrong event received in ["+i+"] " + event, ((MessageEvent) event).getMessage().getString(Symbol.FIELD), expectedSymbol);
            } else if(event instanceof Suggestion) {
                assertEquals("Wrong event received in ["+i+"] " + event, ((Suggestion) event).getIdentifier(), expectedSymbol);
            } else if (event instanceof Notification) {
                assertEquals("Wrong event received in ["+i+"] " + event, ((Notification) event).getBody(), expectedSymbol);
            } else if(event instanceof Order) {
                assertEquals("Wrong event received in ["+i+"] " + event, ((Order) event).getDestinationID().getValue(), expectedSymbol);
            } else if(event instanceof Map) {
                // for a map, keys are index, and values are passwed in
                assertEquals("Wrong event received in ["+i+"] " + event, ((Map) event).get(i), expectedSymbol);
            }
        }
        sManager.cancel(flowID);
    }
}