package org.marketcetera.modules.cep.system;

import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import org.marketcetera.core.notifications.Notification;
import org.marketcetera.core.ExpectedTestFailure;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.MSymbol;
import org.marketcetera.event.*;
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
import java.util.*;

import static junit.framework.Assert.assertSame;

/**
 * Base case for CEP test classes - has some basic functionality for
 * running data flows through and checking for output
 *
 * @author toli@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@SuppressWarnings({"ThrowableResultOfMethodCallIgnored"})
public abstract class CEPTestBase extends ModuleTestBase {
    protected FIXDataDictionary fixDD;
    protected ModuleManager sManager;
    protected static DummySink sSink;
    protected static Factory sFactory;

    // Subclasses shoudl specify
    protected abstract ModuleURN getModuleURN();

    // List of all events that we send in to all the test cases along with individual nams for them
    protected List<Object> allSentEvents;
    protected BidEvent bid1, bid2;
    protected AskEvent ask1, ask2;
    protected TradeEvent trade1, trade2;
    protected LogEvent log1, log2;
    protected MarketstatEvent mStat1, mStat2;
    protected Suggestion sug1, sug2;
    protected Notification not1, not2;
    protected OrderSingle os1, os2;
    protected OrderCancel oc1, oc2;
    protected OrderReplace or1, or2;
    protected OrderCancelReject ocr1, ocr2;
    protected FIXOrder fo1, fo2;
    protected ExecutionReport er1, er2;
    protected Map<Integer, String>  map1, map2;


    @Before public void before() throws Exception {
        sSink = new DummySink();
        sManager = new ModuleManager();
        sManager.init();
        sManager.addSinkListener(sSink);
        CurrentFIXDataDictionary.setCurrentFIXDataDictionary(new FIXDataDictionary(FIXVersion.FIX_SYSTEM.getDataDictionaryURL()));

        //  pre-create and pre-specify all events
        ask1 = new AskEvent(1, 2, new MSymbol("ABC"), "nyse", new BigDecimal("23"), new BigDecimal("23"));
        ask2 = new AskEvent(1, 2, new MSymbol("BIDU"), "nyse", new BigDecimal("23"), new BigDecimal("23"));
        bid1 = new BidEvent(1, 2, new MSymbol("CSCO"), "nyse", new BigDecimal("23"), new BigDecimal("23"));
        bid2 = new BidEvent(1, 2, new MSymbol("DELL"), "nyse", new BigDecimal("23"), new BigDecimal("23"));
        trade1 = new TradeEvent(1, 2, new MSymbol("ECHO"), "nyse", new BigDecimal("23"), new BigDecimal("23"));
        trade2 = new TradeEvent(1, 2, new MSymbol("FIGA"), "nyse", new BigDecimal("23"), new BigDecimal("23"));
        log1 = LogEvent.debug(Messages.PROVIDER_DESCRIPTION);
        log2 = LogEvent.error(Messages.PROVIDER_DESCRIPTION);
        mStat1 = new MarketstatEvent(new MSymbol("ABC"),new Date(),
                BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE,
                BigDecimal.ONE, BigDecimal.ONE, new Date(), new Date(),
                new Date(), new Date(), "OYSE", "HYSE", "LYSE","CYSE");
        mStat2 = new MarketstatEvent(new MSymbol("BIDU"),new Date(), 
                BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE,
                BigDecimal.ONE, BigDecimal.ONE, new Date(), new Date(),
                new Date(), new Date(), "OYSE", "HYSE", "LYSE","CYSE");
        sug1 = Factory.getInstance().createOrderSingleSuggestion();
        sug1.setIdentifier("acura");
        sug2 = Factory.getInstance().createOrderSingleSuggestion();
        sug2.setIdentifier("integra");
        not1 = Notification.low("kathmandu", "kathmandu", this.toString());
        not2 = Notification.low("pokhara", "pokhara", this.toString());
        os1 = sFactory.createOrderSingle();
        os1.setBrokerID(new BrokerID("os1"));
        os2 = sFactory.createOrderSingle();
        os2.setBrokerID(new BrokerID("os2"));
        // order cancel
        Message nos = FIXMessageUtilTest.createNOS("LADA", BigDecimal.ZERO, BigDecimal.ZERO, 'a', FIXVersion.FIX_SYSTEM.getMessageFactory());
        Message can1 = FIXVersion.FIX_SYSTEM.getMessageFactory().newCancelFromMessage(nos);
        oc1 = sFactory.createOrderCancel(can1, new BrokerID("dest1"));
        Message can2 = FIXVersion.FIX_SYSTEM.getMessageFactory().newCancelFromMessage(nos);
        can2.setField(new Symbol("ZAPO"));
        oc2 = sFactory.createOrderCancel(can2, new BrokerID("dest2"));
        // order replace
        Message cxr1 = FIXVersion.FIX_SYSTEM.getMessageFactory().newCancelReplaceFromMessage(nos);
        or1 = sFactory.createOrderReplace(cxr1, new BrokerID("lada"));
        Message cxr2 = FIXVersion.FIX_SYSTEM.getMessageFactory().newCancelReplaceFromMessage(nos);
        cxr2.setField(new Symbol("ZAPO"));
        or2 = sFactory.createOrderReplace(cxr2, new BrokerID("zapo"));
        // fix order
        nos = FIXMessageUtilTest.createNOS("fixORDER", BigDecimal.ZERO, BigDecimal.ZERO, 'a', FIXVersion.FIX_SYSTEM.getMessageFactory());
        fo1 = Factory.getInstance().createOrder(nos, new BrokerID("chuck"));
        fo2 = Factory.getInstance().createOrder(nos, new BrokerID("morgan"));
        // order cancel reject
        Message rej1 = FIXVersion.FIX42.getMessageFactory().newOrderCancelReject();
        rej1.setField(new Text("GOOG"));
        ocr1 = sFactory.createOrderCancelReject(rej1, new BrokerID("dest"), Originator.Server, null, null);
        Message rej2 = FIXVersion.FIX42.getMessageFactory().newOrderCancelReject();
        rej2.setField(new Text("CSCO"));
        ocr2 = sFactory.createOrderCancelReject(rej2, new BrokerID("dest"), Originator.Server, null, null);
        // execution report
        Message er1_fix = FIXVersion.FIX42.getMessageFactory().newExecutionReport("orderid", "clOrdID", "execID", 'a', 'b', BigDecimal.ZERO,
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, new MSymbol("IFLI"), "acct");
        er1 = sFactory.createExecutionReport(er1_fix, new BrokerID("dest1"), Originator.Server, null, null);
        Message er2_fix = FIXVersion.FIX42.getMessageFactory().newExecutionReport("orderid", "clOrdID", "execID", 'a', 'b', BigDecimal.ZERO,
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, new MSymbol("GOOG"), "acct");
        er2 = sFactory.createExecutionReport(er2_fix, new BrokerID("dest2"), Originator.Server, null, null);
        // map
        map1 = new HashMap<Integer, String>();
        map1.put(0, "bob");
        map1.put(1, "bubba");
        map2 = new HashMap<Integer, String>();
        map2.put(3, "fred");
        map2.put(4, "fedya");

        // initialize the mongo array we'll be passing in to all data flows, plus some random other objects
        allSentEvents = Arrays.asList(ask1, ask2, bid1, bid2, trade1, trade2, sug1, sug2, not1, not2, os1, 37, os2, oc1, oc2, or1, or2,
                                      ocr1, ocr2, fo1, fo2, er1, 42, er2, "pupkin", map1, map2, log1, log2, mStat1, mStat2);

    }

    @After
    public void after() throws Exception {
        sManager.removeSinkListener(sSink);
        sManager.stop();
    }

    @Test
    public void testInvalidDataRequestArgument() throws Exception {
        // send in a null request
        new ExpectedTestFailure(IllegalRequestParameterValue.class,
                org.marketcetera.module.Messages.ILLEGAL_REQ_PARM_VALUE.getText(getModuleURN(), null)) {
            protected void execute() throws Throwable {
                sManager.createDataFlow(new DataRequest[] {new DataRequest(getModuleURN(), null)});
            }
        }.run();
    }

    /** See what happens when you send in a non-string request parameter - should error out */
    @Test(timeout=120000)
    public void testNonStringRequestParameter() throws Exception {
        new ExpectedTestFailure(UnsupportedRequestParameterType.class,
                org.marketcetera.module.Messages.UNSUPPORTED_REQ_PARM_TYPE.getText(getModuleURN(), Integer.class.getName())) {
            protected void execute() throws Throwable {
                sManager.createDataFlow(new DataRequest[] {
                        // Copier -> System: send 1 events
                        new DataRequest(CopierModuleFactory.INSTANCE_URN, new EventBase[] {
                                new TradeEvent(3, 4, new MSymbol("IBM"), "NYSE", new BigDecimal("85"), new BigDecimal("200")),
                        }),
                        // System -> Sink: only get 1 bid event
                        new DataRequest(getModuleURN(), 37)  // invalid request param
                });
            }
        }.run();
    }

    /** Subclasses should specify the error class that's thrown in case of incorrect syntax for query */
    protected abstract Class getIncorrectQueryException();

    /** unit test that verifies failure for incorrect query syntax and invalid type name */
    @Test(timeout=120000)
    public void testIncorrectQuerySyntax() throws Exception {
        final String query = "man, is this syntax incorrect or what??";
        new ExpectedTestFailure(getIncorrectQueryException()) {
            protected void execute() throws Throwable {
                sManager.createDataFlow(new DataRequest[] {
                        // Copier -> System: send 1 events
                        new DataRequest(CopierModuleFactory.INSTANCE_URN, new EventBase[] {
                                new TradeEvent(3, 4, new MSymbol("IBM"), "NYSE", new BigDecimal("85"), new BigDecimal("200")),
                        }),
                        // System -> Sink: only get 1 bid event
                        new DataRequest(getModuleURN(), query)  // invalid request param
                });
            }
        }.run();
    }

    /** Subclasses should implement a test that verifies the right exception is thrown in case of invalid type name in select */
    public abstract void testUnknownAlias() throws Exception;

    /** Verify that a request with valid java class name can be created */
    @Test(timeout=120000)
    public void testValidJavaClass() throws Exception {
        final DataFlowID flow1 = sManager.createDataFlow(new DataRequest[] {
                new DataRequest(CopierModuleFactory.INSTANCE_URN, new EventBase[] {
                        new TradeEvent(3, 4, new MSymbol("IBM"), "NYSE", new BigDecimal("85"), new BigDecimal("200")),
                        new BidEvent(1, 2, new MSymbol("IBM"), "NYSE", new BigDecimal("85"), new BigDecimal("100")),
                        new AskEvent(5, 6, new MSymbol("JAVA"), "NASDAQ", new BigDecimal("1.23"), new BigDecimal("300"))
                }),
                new DataRequest(getModuleURN(), "select * from "+java.awt.BorderLayout.class.getName())
        });
        
        sManager.cancel(flow1);
    }

    // send in an unmapped java object, like Integer for instance and verify that it gets filtered correctly.
    @Test(timeout=120000)
    public void testUnmappedJavaObject() throws Exception {
        final DataFlowID flow1 = sManager.createDataFlow(new DataRequest[] {
                new DataRequest(CopierModuleFactory.INSTANCE_URN, new Object[] {
                        new TradeEvent(3, 4, new MSymbol("IBM"), "NYSE", new BigDecimal("85"), new BigDecimal("200")),
                        new BidEvent(1, 2, new MSymbol("IBM"), "NYSE", new BigDecimal("85"), new BigDecimal("100")),
                        37,
                        new AskEvent(5, 6, new MSymbol("JAVA"), "NASDAQ", new BigDecimal("1.23"), new BigDecimal("300"))
                }),
                new DataRequest(getModuleURN(), "select * from "+java.lang.Integer.class.getName())
        });
        assertEquals(37, sSink.getReceived().take());
        sManager.cancel(flow1);
    }

    /** Setup two data flows
     * Send some events through first one
     * Cancel it
     * verify that statements are gone, and then when you send similar events only 2nd data flow gets it
     * Then cancel the 2nd flow, and do a 3rd one that gets totally different events
     * verify that only 3rd-flow events are coming through, and not ones from 2nd or 1st flow
     */
    @Test(timeout=120000)
    public void testCancel() throws Exception {
        final DataFlowID flow1 = sManager.createDataFlow(new DataRequest[] {
                // Copier -> System: send 3 events
                new DataRequest(CopierModuleFactory.INSTANCE_URN, new EventBase[] {
                        new TradeEvent(3, 4, new MSymbol("IBM"), "NYSE", new BigDecimal("85"), new BigDecimal("200")),
                        new BidEvent(1, 2, new MSymbol("IBM"), "NYSE", new BigDecimal("85"), new BigDecimal("100")),
                        new AskEvent(5, 6, new MSymbol("JAVA"), "NASDAQ", new BigDecimal("1.23"), new BigDecimal("300"))
                }),
                // System -> Sink: only get 1 bid event
                new DataRequest(getModuleURN(), "select * from "+BidEvent.class.getName())
        }); 

        BidEvent theBid = (BidEvent) sSink.getReceived().take();
        assertEquals("didnt' get bid event", "IBM", theBid.getSymbolAsString());
        assertEquals("didnt' get right size", new BigDecimal("85"), theBid.getPrice());
        assertEquals("CEP sent out extra events", 1, sManager.getDataFlowInfo(flow1).getFlowSteps()[1].getNumEmitted());
        sManager.cancel(flow1);
        new ExpectedTestFailure(DataFlowNotFoundException.class, flow1.toString()) {
            protected void execute() throws Throwable {
                sManager.getDataFlowInfo(flow1);
            }
        }.run();

        final DataFlowID flow2 = sManager.createDataFlow(new DataRequest[] {
                // Copier -> System: send 3 events
                new DataRequest(CopierModuleFactory.INSTANCE_URN, new EventBase[] {
                        new BidEvent(1, 2, new MSymbol("GOOG"), "NYSE", new BigDecimal("300"), new BigDecimal("100")),
                        new TradeEvent(3, 4, new MSymbol("IBM"), "NYSE", new BigDecimal("85"), new BigDecimal("200")),
                        new AskEvent(5, 6, new MSymbol("JAVA"), "NASDAQ", new BigDecimal("1.23"), new BigDecimal("300"))
                }),
                // System -> Sink: only get 1 bid event
                new DataRequest(getModuleURN(), "select * from "+BidEvent.class.getName())
        });
        theBid = (BidEvent) sSink.getReceived().take();
        assertEquals("didnt' get bid event", "GOOG", theBid.getSymbolAsString());
        assertEquals("didnt' get right size", new BigDecimal("300"), theBid.getPrice());
        assertEquals("CEP sent out extra events", 1, sManager.getDataFlowInfo(flow2).getFlowSteps()[1].getNumEmitted());
        sManager.cancel(flow2);
        new ExpectedTestFailure(DataFlowNotFoundException.class, flow2.toString()) {
            protected void execute() throws Throwable {
                sManager.getDataFlowInfo(flow2);
            }
        }.run();

        // now subscribe to a totally different set of events, send sme events through and verify that we only get 3rd kind of events
        TradeEvent tradeEvent = new TradeEvent(3, 4, new MSymbol("IBM"), "NYSE", new BigDecimal("85"), new BigDecimal("200"));
        final DataFlowID flow3 = sManager.createDataFlow(new DataRequest[] {
                // Copier -> System: send 3 events
                new DataRequest(CopierModuleFactory.INSTANCE_URN, new EventBase[] {
                        new BidEvent(1, 2, new MSymbol("ZOOG"), "NYSE", new BigDecimal("300"), new BigDecimal("100")),
                        new AskEvent(5, 6, new MSymbol("JAVA"), "NASDAQ", new BigDecimal("1.23"), new BigDecimal("300")),
                        tradeEvent,
                }),
                // CEP -> Sink: should only get trade event
                new DataRequest(getModuleURN(), "select * from "+TradeEvent.class.getName())
        });
        TradeEvent theTrade = (TradeEvent) sSink.getReceived().take();
        assertSame("wrong event received", tradeEvent, theTrade);
        assertEquals("didnt' get bid event", "IBM", theTrade.getSymbolAsString());
        assertEquals("didnt' get right size", new BigDecimal("85"), theTrade.getPrice());
        assertEquals("CEP didn't receive all events", 3, sManager.getDataFlowInfo(flow3).getFlowSteps()[1].getNumReceived());
        assertEquals("CEP sent out extra events", 1, sManager.getDataFlowInfo(flow3).getFlowSteps()[1].getNumEmitted());
        sManager.cancel(flow3);
        new ExpectedTestFailure(DataFlowNotFoundException.class, flow3.toString()) {
            protected void execute() throws Throwable {
                sManager.getDataFlowInfo(flow3);
            }
        }.run();
    }

    /** Test multiple data flows with same queries but different sinks. Make sure that  

    /** Run all the varous event types through */
    @Test(timeout=120000)
    public void testAsk() throws Exception {
        flowTestHelperWrapper(CEPDataTypes.ASK, AskEvent.class.getName(), new EventBase[] {ask1, ask2});
    }

    public void testBid() throws Exception {
        flowTestHelperWrapper(CEPDataTypes.BID, BidEvent.class.getName(), new EventBase[] {bid1, bid2});
    }

    @Test(timeout=120000)
    public void testTrade() throws Exception {
        flowTestHelperWrapper(CEPDataTypes.TRADE, TradeEvent.class.getName(), new EventBase[] {trade1, trade2});
    }

    @Test(timeout=120000)
    public void testExecutionReport() throws Exception {
        flowTestHelperWrapper(CEPDataTypes.REPORT, ExecutionReport.class.getName(), er1, er2);
    }

    // validation inspects Text field
    @Test(timeout=120000)
    public void testOrderCancelReject() throws Exception {
        flowTestHelperWrapper(CEPDataTypes.CANCEL_REJECT, OrderCancelReject.class.getName(), ocr1, ocr2);
    }

    // on orders we look at destinations
    @Test(timeout=120000)
    public void testOrderSingle() throws Exception {
        flowTestHelperWrapper(CEPDataTypes.ORDER_SINGLE, OrderSingle.class.getName(), os1, os2);
    }

    // on orders we look at dest id
    @Test(timeout=120000)
    public void testOrderCancel() throws Exception {
        flowTestHelperWrapper(CEPDataTypes.ORDER_CANCEL, OrderCancel.class.getName(), oc1, oc2);
    }

    // on orders we look at dest id
    @Test(timeout=120000)
    public void testOrderReplace() throws Exception {
        flowTestHelperWrapper(CEPDataTypes.ORDER_REPLACE, OrderReplace.class.getName(), or1, or2);
    }

    // on orders we look at dest id
    @Test(timeout=120000)
    public void testFIXOrder() throws Exception {
        flowTestHelperWrapper(CEPDataTypes.FIX_ORDER, FIXOrder.class.getName(), fo1, fo2);
    }

    // checks on body
    @Test(timeout=120000)
    public void testNotification() throws Exception {
        flowTestHelperWrapper(CEPDataTypes.NOTIFICATION, Notification.class.getName(), not1, not2);
    }

    // checks on identifier
    @Test(timeout=120000)
    public void testSuggestion() throws Exception {
        flowTestHelperWrapper(CEPDataTypes.SUGGEST, Suggestion.class.getName(), sug1, sug2);
    }

    @Test(timeout=120000)
    public void testMap() throws Exception {
        flowTestHelperWrapper(CEPDataTypes.MAP, Map.class.getName(), map1, map2);
    }

    @Test(timeout=120000)
    public void testMarketData() throws Exception {
        flowTestHelperWrapper(CEPDataTypes.MARKET_DATA, SymbolExchangeEvent.class.getName(), ask1, ask2, bid1, bid2, trade1, trade2);
    }

    @Test(timeout=120000)
    public void testLog() throws Exception {
        flowTestHelperWrapper(CEPDataTypes.LOG, LogEvent.class.getName(),
                log1, log2);
    }

    @Test(timeout=120000)
    public void testMarketstatEvent() throws Exception {
        flowTestHelperWrapper(CEPDataTypes.MARKET_STAT,
                MarketstatEvent.class.getName(), mStat1, mStat2);
    }


    protected void flowTestHelperWrapper(String expectedAlias,
                                         String expectedClass,
                                         Object... expectedEvents) throws Exception {
        flowTestHelper(expectedAlias, expectedEvents);
        flowTestHelper(expectedClass, expectedEvents);
    }


    /** Helper to run multiple data types through the flow. We will be matching on 'symbol', or something
     * similar in the particular event type if that's available
     */
    protected void flowTestHelper(String type, Object[] expectedEvents) throws Exception {
        // verify module not found before data flow is started
        new ExpectedTestFailure(ModuleNotFoundException.class) {
            protected void execute() throws Throwable {
                sManager.getModuleInfo(getModuleURN());
            }
        }.run();

        DataFlowID flowID = sManager.createDataFlow(new DataRequest[] {
                // Copier -> CEP - send all the events in
                new DataRequest(CopierModuleFactory.INSTANCE_URN, allSentEvents),
                // System -> Sink: only get events that are specified in the incoming type
                new DataRequest(getModuleURN(), "select * from "+type)
        });

        for (int i = 0; i < expectedEvents.length; i++) {
            Object event = sSink.getReceived().take();
            if(expectedEvents[i] instanceof Map) {
                // for some reason, instead of returning the same Map esper re-creates it. so do this the hard way

                Object[] eventKeys = ((Map) event).keySet().toArray();
                Arrays.sort(eventKeys);

                Object[] expectedKeys = ((Map) expectedEvents[i]).keySet().toArray();
                Arrays.sort(expectedKeys);
                assertArrayEquals("keys not equal", eventKeys, expectedKeys);
                for (Object expectedKey : expectedKeys) {
                    assertEquals("value for key not the same"+expectedKey, ((Map)expectedEvents[i]).get(expectedKey), ((Map)event).get(expectedKey));
                }
            } else {
                assertSame("Wrong event received in["+i+"] " + event, expectedEvents[i], event);
            }
        }
        assertEquals("CEP didn't send out right # of events", expectedEvents.length, sManager.getDataFlowInfo(flowID).getFlowSteps()[1].getNumEmitted());
        sManager.cancel(flowID);
        // verify module not found after data flow is started
        new ExpectedTestFailure(ModuleNotFoundException.class) {
            protected void execute() throws Throwable {
                sManager.getModuleInfo(getModuleURN());
            }
        }.run();
    }
}
