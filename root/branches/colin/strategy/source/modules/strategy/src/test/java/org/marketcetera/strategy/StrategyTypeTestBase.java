package org.marketcetera.strategy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import org.junit.Before;
import org.junit.Test;
import org.marketcetera.core.ExpectedTestFailure;
import org.marketcetera.core.MSymbol;
import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.CancelEvent;
import org.marketcetera.event.EventBase;
import org.marketcetera.event.ExecutionReport;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.marketdata.AbstractMarketDataFeed;
import org.marketcetera.marketdata.MarketDataFeedTestBase;

import quickfix.FieldNotFound;
import quickfix.field.OrdStatus;
import quickfix.field.Side;


/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id:$
 * @since $Release$
 */
public abstract class StrategyTypeTestBase
{
    static final File SAMPLE_STRATEGY_DIR = new File("src" + File.separator + "test" + File.separator + "sample_data",
                                                     "inputs");
    private final static String STRATEGY_PROPERTY = "STRATEGY_PROPERTY";
    protected File goodStrategy;
    protected String goodStrategyName;
    protected File badStrategy;
    protected String badStrategyName;
    protected StrategyManager strategyManager;
    /*
     x compilation fails
     x compilation succeeds
     x correct subclass
     x incorrect subclass
     * strategy runs
     * strategy halts
     */
    @Before
    public void beforeEachTest()
        throws Exception
    {
        strategyManager = StrategyManager.getInstance();
        Set<String> strategies = strategyManager.getRegisteredStrategyNames();
        for(String registeredStrategy : strategies) {
            strategyManager.unregister(registeredStrategy);
        }
        setupStrategyFiles();
    }
    protected abstract void setupStrategyFiles();
    @Test
    public void testCompilation()
        throws Exception
    {
        // begin tests
        assertTrue(strategyManager.getRunningStrategies().isEmpty());
        // does not compile
        new ExpectedTestFailure(StrategyExecutionException.class) {
            @Override
            protected void execute()
                throws Throwable
            {
                strategyManager.register(badStrategyName,
                                         getLanguage().ordinal(),
                                         badStrategy);
                strategyManager.execute(badStrategyName);
            }
        }.run();
        strategyManager.unregister(badStrategyName);
        assertTrue(strategyManager.getRunningStrategies().isEmpty());
        assertTrue(strategyManager.getRegisteredStrategyNames().isEmpty());
        // should compile, but the name does not match
        final String doesNotMatchName = goodStrategyName + "ExtraStuff";
        new ExpectedTestFailure(StrategyExecutionException.class) {
            @Override
            protected void execute()
                throws Throwable
            {
                strategyManager.register(doesNotMatchName,
                                         getLanguage().ordinal(),
                                         goodStrategy);
                strategyManager.execute(doesNotMatchName);
            }
        }.run();
        strategyManager.unregister(doesNotMatchName);
        assertTrue(strategyManager.getRunningStrategies().isEmpty());
        assertTrue(strategyManager.getRegisteredStrategyNames().isEmpty());
        // should compile, but of the wrong type
        final StrategyLanguage wrongLanguage = StrategyLanguage.values()[(getLanguage().ordinal() + 1) % StrategyLanguage.values().length];
        new ExpectedTestFailure(StrategyExecutionException.class) {
            @Override
            protected void execute()
                throws Throwable
            {
                strategyManager.register(goodStrategyName,
                                         wrongLanguage.ordinal(),
                                         goodStrategy);
                strategyManager.execute(goodStrategyName);
            }
        }.run();
        strategyManager.unregister(goodStrategyName);
        assertTrue(strategyManager.getRunningStrategies().isEmpty());
        assertTrue(strategyManager.getRegisteredStrategyNames().isEmpty());
        // compiles
        strategyManager.register(goodStrategyName,
                                 getLanguage().ordinal(),
                                 goodStrategy);
        strategyManager.execute(goodStrategyName);
        assertEquals(1,
                     strategyManager.getRegisteredStrategyNames().size());
        assertEquals(1,
                     strategyManager.getRunningStrategies().size());
        assertTrue(strategyManager.getRegisteredStrategyNames().contains(goodStrategyName));
        assertEquals(goodStrategyName,
                     strategyManager.getRunningStrategies().iterator().next().getName());
    }
    /**
     * Tests that a strategy will remain running until it is killed.
     *
     * @throws Exception
     */
    @Test
    public void testKeepRunning()
        throws Exception
    {
        prepareStrategy();
        // verify the test object is responsive
        Set<StrategyMetaData> strategies = strategyManager.getRunningStrategies();
        assertEquals(1,
                     strategies.size());
        // reset the alive marker for this strategy
        resetPropertyHistory(ON_CALLBACK_KEY);
        // execute a callback on the strategy
        IStrategy strategy = strategies.iterator().next().getStrategy();
        strategy.onCallback(null);
        // make sure the strategy got called
        assertPropertyIncremented(ON_CALLBACK_KEY);
        // wait for an arbitrary period and verify the test is still running
        Thread.sleep(5000);
        assertTrue(strategyManager.getRunningStrategyNames().contains(goodStrategyName));
        // call the strategy again
        strategy.onCallback(null);
        // check that there is a new timestamp for the strategy
        assertPropertyIncremented(ON_CALLBACK_KEY);
        // kill the strategy
        strategyManager.unregister(goodStrategyName);
        assertFalse(strategyManager.getRunningStrategyNames().contains(goodStrategyName));
        assertFalse(strategyManager.getRegisteredStrategyNames().contains(goodStrategyName));
    }
    @Test
    public void testStrategyCallbackFunctions()
        throws Exception
    {
        // check that no strategies are currently running
        assertTrue(strategyManager.getRunningStrategyNames().isEmpty());
        prepareStrategy();
        // make sure that all callbacks function
        strategyForFunctionTests = strategyManager.getRunningStrategies().iterator().next().getStrategy();
        doFunctionTest(ON_ASK_KEY,
                       onAskTest);
        doFunctionTest(ON_BID_KEY,
                       onBidTest);
        doFunctionTest(ON_CALLBACK_KEY,
                       onCallbackTest);
        doFunctionTest(ON_CANCEL_KEY,
                       onCancelTest);
        doFunctionTest(ON_EXECUTION_REPORT_KEY,
                       onExecutionReportTest);
        doFunctionTest(ON_NEWS_KEY,
                       onNewsTest);
        doFunctionTest(ON_TRADE_KEY,
                       onTradeTest);
    }
    @Test
    public void testSimpleStrategy()
        throws Exception
    {
        resetPropertyHistory(STRATEGY_PROPERTY);
        assertEquals("0",
                     AbstractStrategy.getCommonProperty(STRATEGY_PROPERTY));
        StrategyMetaData strategyData = loadAndStartStrategy("SimpleStrategy",
                                                             getSimpleStrategy());
        assertNotNull(strategyData);
        // strategy is now running, pass it some events
        List<EventBase> events = new ArrayList<EventBase>();
        // first group of events will *not* trigger the strategy to fire as the market width
        //  is too great
        events.add(new BidEvent(System.nanoTime(),
                                System.currentTimeMillis(),
                                "GOOG",
                                "Exchange",
                                new BigDecimal("100.0"),
                                new BigDecimal("1000")));
        events.add(new AskEvent(System.nanoTime(),
                                System.currentTimeMillis(),
                                "GOOG",
                                "Exchange",
                                new BigDecimal("110.00"),
                                new BigDecimal("1000")));
        sendEvents(events,
                   strategyData.getStrategy());
        assertEquals("0",
                     AbstractStrategy.getCommonProperty(STRATEGY_PROPERTY));
        // now, throw a new ask to trigger the strategy
        events.clear();
        events.add(new AskEvent(System.nanoTime(),
                                System.currentTimeMillis(),
                                "GOOG",
                                "Exchange",
                                new BigDecimal("100.01"),
                                new BigDecimal("2000")));
        sendEvents(events,
                   strategyData.getStrategy());
        assertPropertyIncremented(STRATEGY_PROPERTY);
    }
    protected final void sendEvents(List<EventBase> inEvents,
                                    IStrategy inStrategy)
        throws Exception
    {
        for(EventBase event : inEvents) {
            if(event instanceof AskEvent) {
                inStrategy.onAsk((AskEvent)event);
            } else if(event instanceof BidEvent) {
                inStrategy.onBid((BidEvent)event);
            } else if(event instanceof TradeEvent) {
                inStrategy.onTrade((TradeEvent)event);
            } else if(event instanceof CancelEvent) {
                inStrategy.onCancel((CancelEvent)event);
            } else if(event instanceof ExecutionReport) {
                inStrategy.onExecutionReport((ExecutionReport)event);
            } else {
                fail("Unknown event type: " + event);
            }
        }
    }
    protected StrategyMetaData loadAndStartStrategy(final String inName,
                                                    File inStrategy)
        throws Exception
    {
        strategyManager.register(inName,
                                 getLanguage().ordinal(),
                                 inStrategy);
        // execute the strategy
        strategyManager.execute(inName);
        // wait for the strategy to start 
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                return strategyManager.getRunningStrategyNames().contains(inName);
            }
        });
        Set<StrategyMetaData> runningStrategies = strategyManager.getRunningStrategies();
        for(StrategyMetaData data : runningStrategies) {
            if(data.getName().equals(inName)) {
                return data;
            }
        }
        fail("Unable to find running strategy");
        return null;
    }
    private void prepareStrategy()
        throws Exception
    {
        // check that no strategies are currently running
        assertTrue(strategyManager.getRunningStrategyNames().isEmpty());
        loadAndStartStrategy(goodStrategyName,
                             goodStrategy);
        // verify the strategy is running
        assertTrue(strategyManager.getRunningStrategyNames().contains(goodStrategyName));
    }
    private void doFunctionTest(String inKey,
                                Runnable inBlock)
        throws Exception
    {
        resetPropertyHistory(inKey);
        inBlock.run();
        assertPropertyIncremented(inKey);
    }
    protected final void resetPropertyHistory(String inKey)
        throws Exception
    {
        AbstractStrategy.setCommonProperty(inKey,
                                           "0");
        propertyHistory.put(inKey,
                            0l);
    }
    private final Map<String,Long> propertyHistory = new HashMap<String,Long>();
    protected final void assertPropertyIncremented(String inKey)
        throws Exception
    {
        long currentAliveMarker = Long.parseLong(AbstractStrategy.getCommonProperty(inKey));
        Long lastAliveMarker = propertyHistory.get(inKey);
        if(lastAliveMarker == null) {
            fail(String.format("There should be an alive marker for strategy %s",
                               inKey));
        }
        assertTrue(String.format("The strategy %s current alive marker %d is supposed to be greater than the last alive marker %d for this strategy",
                                 inKey,
                                 currentAliveMarker,
                                 lastAliveMarker),
                   currentAliveMarker > lastAliveMarker);
        // record the current alive marker in the strategy alive marker map
        propertyHistory.put(inKey,
                                    currentAliveMarker);
        // update the common properties alive marker
        AbstractStrategy.setCommonProperty(inKey,
                                           Long.toString(currentAliveMarker));
    }
    protected abstract StrategyLanguage getLanguage();
    protected void executeScript(String inName,
                                 File inStrategy)
        throws StrategyNotFoundException, StrategyExecutionException, NoExecutorException, StrategyAlreadyRegistedException
    {
        strategyManager.register(inName,
                                 getLanguage().ordinal(),
                                 inStrategy);
        strategyManager.execute(inName);
    }
    protected static final String ON_ASK_KEY = "onAsk";
    protected static final String ON_BID_KEY = "onBid";
    protected static final String ON_CALLBACK_KEY = "onCallback";
    protected static final String ON_CANCEL_KEY = "onCancel";
    protected static final String ON_EXECUTION_REPORT_KEY = "onExecutionReport";
    protected static final String ON_NEWS_KEY = "onNews";
    protected static final String ON_TRADE_KEY = "onTrade";
    private IStrategy strategyForFunctionTests;
    private final Runnable onAskTest = new Runnable() {
        public void run()
        {
            strategyForFunctionTests.onAsk(new AskEvent(System.nanoTime(),
                                                        System.currentTimeMillis(),
                                                        "SYMBOL",
                                                        "Exchange",
                                                        new BigDecimal(1),
                                                        new BigDecimal(2)));
        }
    };
    private final Runnable onBidTest = new Runnable() {
        public void run()
        {
            strategyForFunctionTests.onBid(new BidEvent(System.nanoTime(),
                                                        System.currentTimeMillis(),
                                                        "SYMBOL",
                                                        "Exchange",
                                                        new BigDecimal(1),
                                                        new BigDecimal(2)));
        }
    };
    private final Runnable onCallbackTest = new Runnable() {
        public void run()
        {
            strategyForFunctionTests.onCallback(null);
        }
    };
    private final Runnable onCancelTest = new Runnable() {
        public void run()
        {
            strategyForFunctionTests.onCancel(new CancelEvent(System.nanoTime(),
                                                              System.currentTimeMillis()));
                                                             
        }
    };
    private final Runnable onExecutionReportTest = new Runnable() {
        public void run()
        {
            try {
                strategyForFunctionTests.onExecutionReport(new ExecutionReport(System.nanoTime(),
                                                                               System.currentTimeMillis(),
                                                                               AbstractMarketDataFeed.DEFAULT_MESSAGE_FACTORY.getMessageFactory().newExecutionReport("orderid",
                                                                                                                                                                     "clOrderID",
                                                                                                                                                                     "execID",
                                                                                                                                                                     OrdStatus.FILLED,
                                                                                                                                                                     Side.BUY,
                                                                                                                                                                     new BigDecimal(100),
                                                                                                                                                                     new BigDecimal(200),
                                                                                                                                                                     new BigDecimal(300),
                                                                                                                                                                     new BigDecimal(400),
                                                                                                                                                                     new BigDecimal(500),
                                                                                                                                                                     new BigDecimal(600),
                                                                                                                                                                     new MSymbol("Symbol"),
                                                                                                                                                                     "account")));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (FieldNotFound e) {
                e.printStackTrace();
            } 
        }
    };
    private final Runnable onNewsTest = new Runnable() {
        public void run()
        {
            strategyForFunctionTests.onNews(new MSymbol("Symbol"),
                                            "some news about Symbol"); 
                                                             
        }
    };
    private final Runnable onTradeTest = new Runnable() {
        public void run()
        {
            strategyForFunctionTests.onTrade(new TradeEvent(System.nanoTime(),
                                                            System.currentTimeMillis(),
                                                            "SYMBOL",
                                                            "Exchange",
                                                            new BigDecimal(1),
                                                            new BigDecimal(2)));
        }
    };
    protected abstract File getSimpleStrategy();
}
