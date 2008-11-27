package org.marketcetera.strategy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.marketcetera.strategy.Messages.FAILED_TO_START;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Callable;

import org.junit.Ignore;
import org.junit.Test;
import org.marketcetera.core.MSymbol;
import org.marketcetera.core.notifications.NotificationManager;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.event.BidEvent;
import org.marketcetera.marketdata.MarketDataFeedTestBase;
import org.marketcetera.marketdata.bogus.BogusFeedModuleFactory;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.module.ModuleException;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.strategy.StrategyTestBase.MockRecorderModule.DataReceived;
import org.marketcetera.trade.DestinationID;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.FIXOrder;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.trade.OrderSingleSuggestion;
import org.marketcetera.trade.OrderType;
import org.marketcetera.trade.Side;
import org.marketcetera.trade.TimeInForce;
import org.marketcetera.trade.TypesTestBase;

import quickfix.Message;
import quickfix.field.TransactTime;

/* $License$ */

/**
 * Provides a set of tests for a script language.
 * 
 * <p>Each new script language to support should extend this class.
 * 
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class LanguageTestBase
        extends StrategyTestBase
{
    /**
     * Tests a strategy that should compile and make sure its enclosing module can start up.
     * 
     * @throws Exception if an error occurs
     */
    @Test
    public void compilesAndTestsCallbacks()
            throws Exception
    {
        StrategyCoordinates strategy = getStrategyCompiles();
        ModuleURN strategyModule = createStrategy(strategy.getName(),
                                                  getLanguage(),
                                                  strategy.getFile(),
                                                  null,
                                                  null,
                                                  null,
                                                  null);
        verifyPropertyNonNull("onStart");
        doSuccessfulStartTest(strategyModule);
        moduleManager.stop(strategyModule);
        assertFalse(moduleManager.getModuleInfo(strategyModule).getState().isStarted());
        verifyPropertyNonNull("onStop");
    }
    /**
     * Tests a strategy that will not start due to a compilation error.
     * 
     * @throws Exception if an error occurs.
     */
    @Test
    public void doesNotCompile()
            throws Exception
    {
        new ExpectedFailure<ModuleException>(FAILED_TO_START) {
            @Override
            protected void run()
                    throws Exception
            {
                StrategyCoordinates strategy = getStrategyWillNotCompile();
                verifyStrategyStartsAndStops(strategy.getName(),
                                             getLanguage(),
                                             strategy.getFile(),
                                             null,
                                             null,
                                             null,
                                             null);
            }
        };
    }
    /**
     * Tests that the script won't compile if the wrong language is specified.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void wrongLanguage()
        throws Exception
    {
        // this picks a language that is *not* the one we are testing, doesn't really matter which one
        final Language wrongLanguage = Language.values()[(getLanguage().ordinal() + 1) % Language.values().length];
        new ExpectedFailure<ModuleException>(FAILED_TO_START) {
            @Override
            protected void run()
                    throws Exception
            {
                StrategyCoordinates strategy = getStrategyCompiles();
                verifyStrategyStartsAndStops(strategy.getName(),
                                             wrongLanguage,
                                             strategy.getFile(),
                                             null,
                                             null,
                                             null,
                                             null);
            }
        };
    }
    /**
     * Tests that the script won't execute if it does not contain a class that subclasses Strategy.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void noStrategySubclass()
        throws Exception
    {
        new ExpectedFailure<ModuleException>(FAILED_TO_START) {
            @Override
            protected void run()
                    throws Exception
            {
                StrategyCoordinates strategy = getStrategyWrongClass();
                verifyStrategyStartsAndStops(strategy.getName(),
                                             getLanguage(),
                                             strategy.getFile(),
                                             null,
                                             null,
                                             null,
                                             null);
            }
        };
    }
    /**
     * Verifies that a strategy can contain multiple classes.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void multipleClasses()
        throws Exception
    {
        StrategyCoordinates strategy = getStrategyMultipleClasses();
        doSuccessfulStartTest(createStrategy(strategy.getName(),
                                             getLanguage(),
                                             strategy.getFile(),
                                             null,
                                             null,
                                             null,
                                             null));
    }
    /**
     * Tests the scenario where the name of the strategy does not match
     * a class in the script.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void noMatchingName()
        throws Exception
    {
        final StrategyCoordinates strategy2 = getStrategyCompiles();
        new ExpectedFailure<ModuleException>(FAILED_TO_START) {
            @Override
            protected void run()
                    throws Exception
            {
                verifyStrategyStartsAndStops("SomeNameThatDoesNotMatch",
                                             getLanguage(),
                                             strategy2.getFile(),
                                             null,
                                             null,
                                             null,
                                             null);
            }
        };
    }
    /**
     * Tests a strategy that overrides only {@link RunningStrategy#onAsk(org.marketcetera.event.AskEvent)}.
     *
     * <p>This test makes sure that a strategy that selectively overrides call-backs does not fail when
     * the non-overridden call-backs are executed.
     * 
     * @throws Exception if an error occurs
     */
    @Test
    public void almostEmptyStrategy()
        throws Exception
    {
        StrategyCoordinates strategy = getEmptyStrategy();
        // need to piece this together manually as only "onAsk" will be set
        ModuleURN strategyURN = createStrategy(strategy.getName(),
                                               getLanguage(),
                                               strategy.getFile(),
                                               null,
                                               null,
                                               null,
                                               null);
        verifyNullProperties();
        // create an emitter module that will emit the types of data that the strategy must be able to process
        ModuleURN dataEmitterURN = createModule(StrategyTestBase.StrategyDataEmissionModule.Factory.PROVIDER_URN);
        // plumb the emitter together with the strategy (the data is transmitted when the request is made)
        DataFlowID dataFlowID = moduleManager.createDataFlow(new DataRequest[] { new DataRequest(dataEmitterURN,
                                                                                                 null),
                                                                                 new DataRequest(strategyURN) },
                                                             false);
        // shut down the flow
        moduleManager.cancel(dataFlowID);
        // verify the data was received
        verifyPropertyNonNull("onAsk");
    }
    /**
     * Tests a scenario's ability to retrieve parameters.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void parameterStrategy()
        throws Exception
    {
        StrategyCoordinates strategy = getParameterStrategy();
        Properties parameters = new Properties();
        parameters.setProperty("onAsk",
                               "onAskValue");
        parameters.setProperty("onBid",
                               "onBidValue");
        parameters.setProperty("onExecutionReport",
                               "onExecutionReportValue");
        parameters.setProperty("onTrade",
                               "onTradeValue");
        // onOther deliberately omitted in order to test request for non-existent parameter
        ModuleURN strategyURN = createStrategy(strategy.getName(),
                                               getLanguage(),
                                               strategy.getFile(),
                                               parameters,
                                               null,
                                               null,
                                               null);
        verifyNullProperties();
        // create an emitter module that will emit the types of data that the strategy must be able to process
        ModuleURN dataEmitterURN = createModule(StrategyTestBase.StrategyDataEmissionModule.Factory.PROVIDER_URN);
        // plumb the emitter together with the strategy (the data is transmitted when the request is made)
        DataFlowID dataFlowID = moduleManager.createDataFlow(new DataRequest[] { new DataRequest(dataEmitterURN,
                                                                                                 null),
                                                                                 new DataRequest(strategyURN) },
                                                             false);
        // shut down the flow
        moduleManager.cancel(dataFlowID);
        // verify the data was received
        verifyPropertyNonNull("onAsk");
        verifyPropertyNonNull("onBid");
        verifyPropertyNonNull("onTrade");
        verifyPropertyNonNull("onExecutionReport");
    }
    /**
     * Tests what happens if a runtime error occurs in the strategy script.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void runtimeError()
        throws Exception
    {
        // runtime error in onStart
        final StrategyCoordinates strategy = getStrategyCompiles();
        final Properties parameters = new Properties();
        parameters.setProperty("shouldFailOnStart",
                               "true");
        new ExpectedFailure<ModuleException>(FAILED_TO_START) {
            @Override
            protected void run()
                    throws Exception
            {
                doSuccessfulStartTest(createStrategy(strategy.getName(),
                                                     getLanguage(),
                                                     strategy.getFile(),
                                                     parameters,
                                                     null,
                                                     null,
                                                     null));
            }
        };
        parameters.clear();
        parameters.setProperty("shouldFailOnStop",
                               "true");
        // runtime error in onStop
        // TODO this is not working as expected
//        final ModuleURN strategyURN = createStrategy(strategy.getName(),
//                                                     getLanguage(),
//                                                     strategy.getFile(),
//                                                     parameters,
//                                                     null,
//                                                     null,
//                                                     null);
//        doSuccessfulStartTest(strategyURN);
//        moduleManager.stop(strategyURN);
        // runtime error in each callback
        doCallbackFailsTest("shouldFailOnAsk",
                            new String[] { "onBid", "onCancel", "onExecutionReport", "onTrade", "onOther" });
        doCallbackFailsTest("shouldFailOnBid",
                            new String[] { "onAsk", "onCancel", "onExecutionReport", "onTrade", "onOther" });
        doCallbackFailsTest("shouldFailOnExecutionReport",
                            new String[] { "onAsk", "onBid", "onCancel", "onTrade", "onOther" });
        doCallbackFailsTest("shouldFailOnTrade",
                            new String[] { "onAsk", "onBid", "onCancel", "onExecutionReport", "onOther" });
        doCallbackFailsTest("shouldFailOnOther",
                            new String[] { "onAsk", "onBid", "onCancel", "onExecutionReport", "onTrade" });
    }
    @Test@Ignore
    public void endlessLoopOnStart()
        throws Exception
    {
        final StrategyCoordinates strategy = getStrategyCompiles();
        final Properties parameters = new Properties();
        parameters.setProperty("shouldLoopOnStart",
                               "true");
        doSuccessfulStartTest(createStrategy(strategy.getName(),
                                             getLanguage(),
                                             strategy.getFile(),
                                             parameters,
                                             null,
                                             null,
                                             null));
    }
    /**
     * Tests receipt of market data from a valid, started market data provider.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void marketDataRequests()
        throws Exception
    {
        getMarketData(BogusFeedModuleFactory.IDENTIFIER,
                      "GOOG,YHOO,MSFT,METC");
        // strategy is now receiving data
        Thread.sleep(5000);
        // verify that bid/ask/trades have been received
        // TODO substitute a market data provider that provides a known script of events
        verifyPropertyNonNull("onAsk");
        verifyPropertyNonNull("onBid");
        // TODO almost certainly Bogus will provide a trade within 5 seconds, but it's nonetheless
        //  not deterministic.  the fix is to implement the deterministic provider described above
//        verifyPropertyNonNull("onTrade");
    }
    /**
     * Tests a market data request from a market data provider that does not exist.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void marketDataRequestFromNonexistentSource()
        throws Exception
    {
        getMarketData("provider-does-not-exist",
                      "GOOG,YHOO,MSFT,METC");
        // TODO same note as above: create a market data provider that deterministically produces data 
        Thread.sleep(5000);
        // the script does not fail, but no market data was provided
        verifyNullProperties();
    }
    /**
     * Tests a market data request from a market data provider that exists but has not been started.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void marketDataRequestFromUnstartedSource()
        throws Exception
    {
        // stop the bogus provider
        assertTrue(moduleManager.getModuleInfo(BogusFeedModuleFactory.INSTANCE_URN).getState().isStarted());
        moduleManager.stop(BogusFeedModuleFactory.INSTANCE_URN);
        assertFalse(moduleManager.getModuleInfo(BogusFeedModuleFactory.INSTANCE_URN).getState().isStarted());
        // request market data from the stopped provider
        getMarketData(BogusFeedModuleFactory.IDENTIFIER,
                      "GOOG,YHOO,MSFT,METC");
        // TODO same note as above: create a market data provider that deterministically produces data 
        Thread.sleep(5000);
        // the script does not fail, but no market data was provided
        verifyNullProperties();
        // start the bogus module again
        moduleManager.start(BogusFeedModuleFactory.INSTANCE_URN);
        assertTrue(moduleManager.getModuleInfo(BogusFeedModuleFactory.INSTANCE_URN).getState().isStarted());
    }
    /**
     * Tests a strategy's ability to cancel a market data request.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void cancelMarketDataRequest()
        throws Exception
    {
        getMarketData(BogusFeedModuleFactory.IDENTIFIER,
                      "GOOG,YHOO,MSFT,METC");
        // TODO same note as above: create a market data provider that deterministically produces data 
        Thread.sleep(5000);
        // market data request has produced some data, verify that now
        verifyPropertyNonNull("onAsk");
        verifyPropertyNonNull("onBid");
        // TODO almost certainly Bogus will provide a trade within 5 seconds, but it's nonetheless
        //  not deterministic.  the fix is to implement the deterministic provider described above
//        verifyPropertyNonNull("onTrade");
        // retrieve the id of the market data request
        verifyPropertyNonNull("requestID");
        Properties properties = AbstractRunningStrategy.getProperties();
        long id = Long.parseLong(properties.getProperty("requestID"));
        // reset properties to clear data received markers
        setPropertiesToNull();
        // set the indicators back in the properties to tell the script what to cancel
        properties.setProperty("shouldCancel",
                               "true");
        properties.setProperty("requestID",
                               Long.toString(id));
        // execute the onCallback method in the running strategy to force the market data
        //  request cancel
        getFirstRunningStrategyAsAbstractRunningStrategy().onCallback(this);
        // collect more market data, or, give it the chance to, anyway
        Thread.sleep(5000);
        // make sure no more data was received
        verifyPropertyNull("onAsk");
        verifyPropertyNull("onBid");
    }
    /**
     * Tests what happens when a strategy tries to cancel a non-existent market data request.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void cancelNonExistentMarketDataRequest()
        throws Exception
    {
        final StrategyCoordinates strategy = getStrategyCompiles();
        // create a strategy that does not request market data
        ModuleURN strategyURN = createStrategy(strategy.getName(),
                                               getLanguage(),
                                               strategy.getFile(),
                                               null,
                                               null,
                                               null,
                                               null);
        verifyNullProperties();
        // set the indicators to tell the script what to cancel
        Properties properties = new Properties();
        properties.setProperty("shouldCancel",
                               "true");
        properties.setProperty("requestID",
                               Long.toString(System.currentTimeMillis()));
        // execute the onCallback method in the running strategy to force the market data
        //  request cancel
        getFirstRunningStrategyAsAbstractRunningStrategy().onCallback(this);
        // no error should result from this
        // plumb the faux market data provider to the strategy to verify the strategy
        //  is still working
        doSuccessfulStartTest(strategyURN);
    }
    /**
     * Tests a strategy's ability to request and receive a callback after a certain interval.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void callbackAfter()
        throws Exception
    {
        // start a strategy
        final StrategyCoordinates strategy = getStrategyCompiles();
        final Properties parameters = new Properties();
        parameters.setProperty("shouldRequestCallbackAfter",
                               "1000");
        verifyPropertyNull("onCallback");
        doSuccessfulStartTest(createStrategy(strategy.getName(),
                                             getLanguage(),
                                             strategy.getFile(),
                                             parameters,
                                             null,
                                             null,
                                             null));
        // make sure to wait until at least 1000ms after start
        Thread.sleep(2000);
        verifyPropertyNonNull("onCallback");
    }
    /**
     * Tests a strategy's ability to request and receive a callback at a certain time.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void callbackAt()
        throws Exception
    {
        // start a strategy
        final StrategyCoordinates strategy = getStrategyCompiles();
        final Properties parameters = new Properties();
        Date callbackAt = new Date(System.currentTimeMillis() + 2000);
        parameters.setProperty("shouldRequestCallbackAt",
                               Long.toString(callbackAt.getTime()));
        verifyPropertyNull("onCallback");
        doSuccessfulStartTest(createStrategy(strategy.getName(),
                                             getLanguage(),
                                             strategy.getFile(),
                                             parameters,
                                             null,
                                             null,
                                             null));
        // make sure to wait until at least 2000ms after start
        Thread.sleep(2000);
        verifyPropertyNonNull("onCallback");
    }
    /**
     * Tests a strategy's ability to request and receive a callback after a negative interval.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void callbackAfterEarlier()
        throws Exception
    {
        // start a strategy
        final StrategyCoordinates strategy = getStrategyCompiles();
        final Properties parameters = new Properties();
        parameters.setProperty("shouldRequestCallbackAfter",
                               "-1000");
        verifyPropertyNull("onCallback");
        doSuccessfulStartTest(createStrategy(strategy.getName(),
                                             getLanguage(),
                                             strategy.getFile(),
                                             parameters,
                                             null,
                                             null,
                                             null));
        // make sure to wait until at least 1000ms after start
        Thread.sleep(1000);
        String callbackTime = verifyPropertyNonNull("onCallback");
        assertTrue(Long.parseLong(callbackTime) < System.currentTimeMillis());
    }
    /**
     * Tests a strategy's ability to request and receive a callback before the current time.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void callbackAtEarlier()
        throws Exception
    {
        // start a strategy
        final StrategyCoordinates strategy = getStrategyCompiles();
        final Properties parameters = new Properties();
        Date callbackAt = new Date(System.currentTimeMillis() - 2000);
        parameters.setProperty("shouldRequestCallbackAt",
                               Long.toString(callbackAt.getTime()));
        verifyPropertyNull("onCallback");
        doSuccessfulStartTest(createStrategy(strategy.getName(),
                                             getLanguage(),
                                             strategy.getFile(),
                                             parameters,
                                             null,
                                             null,
                                             null));
        // callback should happen immediately, but wait a second or so
        Thread.sleep(1000);
        String callbackTime = verifyPropertyNonNull("onCallback");
        assertTrue(Long.parseLong(callbackTime) < System.currentTimeMillis());
    }
    /**
     * Tests a strategy's ability to request and receive a callback after a zero interval.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void callbackAfterZero()
        throws Exception
    {
        // start a strategy
        final StrategyCoordinates strategy = getStrategyCompiles();
        final Properties parameters = new Properties();
        parameters.setProperty("shouldRequestCallbackAfter",
                               "0");
        verifyPropertyNull("onCallback");
        doSuccessfulStartTest(createStrategy(strategy.getName(),
                                             getLanguage(),
                                             strategy.getFile(),
                                             parameters,
                                             null,
                                             null,
                                             null));
        // make sure to wait until at least 1000ms after start
        Thread.sleep(1000);
        String callbackTime = verifyPropertyNonNull("onCallback");
        assertTrue(Long.parseLong(callbackTime) < System.currentTimeMillis());
    }
    /**
     * Tests a strategy's ability to request and receive a callback at the current time.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void callbackAtZero()
        throws Exception
    {
        // start a strategy
        final StrategyCoordinates strategy = getStrategyCompiles();
        final Properties parameters = new Properties();
        Date callbackAt = new Date();
        parameters.setProperty("shouldRequestCallbackAt",
                               Long.toString(callbackAt.getTime()));
        verifyPropertyNull("onCallback");
        doSuccessfulStartTest(createStrategy(strategy.getName(),
                                             getLanguage(),
                                             strategy.getFile(),
                                             parameters,
                                             null,
                                             null,
                                             null));
        // callback should happen immediately, but wait a second or so
        Thread.sleep(1000);
        String callbackTime = verifyPropertyNonNull("onCallback");
        assertTrue(Long.parseLong(callbackTime) < System.currentTimeMillis());
    }
    /**
     * Tests what happens when a strategy commits a run-time error during a callback.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void callbackFails()
        throws Exception
    {
        // start a strategy
        final StrategyCoordinates strategy = getStrategyCompiles();
        final Properties parameters = new Properties();
        parameters.setProperty("shouldFailOnCallback",
                               "true");
        parameters.setProperty("shouldRequestCallbackAfter",
                               "0");
        verifyPropertyNull("onCallback");
        ModuleURN strategyURN = createStrategy(strategy.getName(),
                                               getLanguage(),
                                               strategy.getFile(),
                                               parameters,
                                               null,
                                               null,
                                               null); 
        doSuccessfulStartTest(strategyURN);
        // callback should happen immediately, but wait a second or so
        Thread.sleep(1000);
        // strategy should not have completed onCallback loop, but should still be running
        verifyPropertyNull("onCallback");
        setPropertiesToNull();
        // make sure the strategy is still alive and kicking
        doSuccessfulStartTestNoVerification(strategyURN);
        verifyNonNullProperties();
    }
    /**
     * Tests that a strategy can request and receive a null call-back payload without failing.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void callbackAtWithNullPayload()
        throws Exception
    {
        final StrategyCoordinates strategy = getStrategyCompiles();
        final Properties parameters = new Properties();
        Date callbackAt = new Date();
        parameters.setProperty("shouldRequestCallbackAt",
                               Long.toString(callbackAt.getTime()));
        parameters.setProperty("callbackDataIsNull",
                               "true");
        verifyPropertyNull("onCallback");
        doSuccessfulStartTest(createStrategy(strategy.getName(),
                                             getLanguage(),
                                             strategy.getFile(),
                                             parameters,
                                             null,
                                             null,
                                             null));
        // callback should happen immediately, but wait a second or so
        Thread.sleep(1000);
        String callbackTime = verifyPropertyNonNull("onCallback");
        assertTrue(Long.parseLong(callbackTime) < System.currentTimeMillis());
    }
    /**
     * Tests that simultaneous callbacks are executed properly.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void simultaneousCallbacks()
        throws Exception
    {
        // start a strategy
        final StrategyCoordinates strategy = getStrategyCompiles();
        final Properties parameters = new Properties();
        Date callbackAt = new Date(System.currentTimeMillis());
        parameters.setProperty("shouldRequestCallbackAt",
                               Long.toString(callbackAt.getTime()));
        parameters.setProperty("shouldDoubleCallbacks",
                               "true");
        verifyPropertyNull("onCallback");
        doSuccessfulStartTest(createStrategy(strategy.getName(),
                                             getLanguage(),
                                             strategy.getFile(),
                                             parameters,
                                             null,
                                             null,
                                             null));
        // make sure to wait until at least 2000ms after start
        Thread.sleep(2000);
        // make sure 2 callbacks were received
        assertEquals("2",
                     verifyPropertyNonNull("onCallback"));
    }
    /**
     * Tests that callbacks are not executed after the strategy is stopped.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void callbacksAfterStop()
        throws Exception
    {
        final StrategyCoordinates strategy = getStrategyCompiles();
        final Properties parameters = new Properties();
        Date callbackAt = new Date(System.currentTimeMillis()+2000);
        parameters.setProperty("shouldRequestCallbackAt",
                               Long.toString(callbackAt.getTime()));
        verifyPropertyNull("onCallback");
        ModuleURN strategyURN = createStrategy(strategy.getName(),
                                               getLanguage(),
                                               strategy.getFile(),
                                               parameters,
                                               null,
                                               null,
                                               null); 
        doSuccessfulStartTest(strategyURN);
        moduleManager.stop(strategyURN);
        assertTrue("The strategy should have been stopped before the callback - increase the callback delay",
                   System.currentTimeMillis() < callbackAt.getTime());
        Thread.sleep(2500);
        // callback should have happened
        assertTrue(System.currentTimeMillis() > callbackAt.getTime());
        verifyPropertyNull("onCallback");
    }
    /**
     * Tests that sequential callbacks are executed properly.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void sequentialCallbacks()
        throws Exception
    {
        final StrategyCoordinates strategy = getStrategyCompiles();
        final Properties parameters = new Properties();
        Date callbackAt = new Date(System.currentTimeMillis() + 1000);
        parameters.setProperty("shouldRequestCallbackAt",
                               Long.toString(callbackAt.getTime()));
        parameters.setProperty("shouldDoubleCallbacks",
                               "true");
        Date callback2At = new Date(System.currentTimeMillis() + 1500);
        AbstractRunningStrategy.getProperties().setProperty("shouldRequestCallbackAt",
                                                            Long.toString(callback2At.getTime()));
        verifyPropertyNull("onCallback");
        doSuccessfulStartTest(createStrategy(strategy.getName(),
                                             getLanguage(),
                                             strategy.getFile(),
                                             parameters,
                                             null,
                                             null,
                                             null));
        // make sure to wait until at least 2000ms after start
        Thread.sleep(2000);
        // make sure 4 callbacks were received (param + props * doubled)
        assertEquals("4",
                     verifyPropertyNonNull("onCallback"));
    }
    /**
     * Tests a strategy's ability to suggest trades.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void suggestions()
        throws Exception
    {
        Properties parameters = new Properties();
        // null suggestion
        parameters.setProperty("orderShouldBeNull",
                               "true");
        doSuggestionTest(parameters,
                         new OrderSingleSuggestion[0]);
        // null score
        parameters.clear();
        doSuggestionTest(parameters,
                         new OrderSingleSuggestion[0]);
        // null identifier
        parameters.setProperty("score",
                               "1");
        doSuggestionTest(parameters,
                         new OrderSingleSuggestion[0]);
        // zero length identifier
        parameters.setProperty("identifier",
                               "");
        doSuggestionTest(parameters,
                         new OrderSingleSuggestion[0]);
        // first complete suggestion
        parameters.setProperty("identifier",
                               "some identifier");
        OrderSingleSuggestion expectedSuggestion = Factory.getInstance().createOrderSingleSuggestion();
        expectedSuggestion.setScore(new BigDecimal("1"));
        expectedSuggestion.setIdentifier("some identifier");
        OrderSingle suggestedOrder = Factory.getInstance().createOrderSingle();
        expectedSuggestion.setOrder(suggestedOrder);
        doSuggestionTest(parameters,
                         new OrderSingleSuggestion[] { expectedSuggestion });
        // add an account
        parameters.setProperty("account",
                               "some account");
        suggestedOrder.setAccount("some account");
        expectedSuggestion.setOrder(suggestedOrder);
        doSuggestionTest(parameters,
                         new OrderSingleSuggestion[] { expectedSuggestion });
        // add order type
        parameters.setProperty("orderType",
                               OrderType.Market.name());
        suggestedOrder.setOrderType(OrderType.Market);
        expectedSuggestion.setOrder(suggestedOrder);
        doSuggestionTest(parameters,
                         new OrderSingleSuggestion[] { expectedSuggestion });
        // add price
        parameters.setProperty("price",
                               "100.23");
        suggestedOrder.setPrice(new BigDecimal("100.23"));
        expectedSuggestion.setOrder(suggestedOrder);
        doSuggestionTest(parameters,
                         new OrderSingleSuggestion[] { expectedSuggestion });
        // add quantity
        parameters.setProperty("quantity",
                               "10000");
        suggestedOrder.setQuantity(new BigDecimal("10000"));
        expectedSuggestion.setOrder(suggestedOrder);
        doSuggestionTest(parameters,
                         new OrderSingleSuggestion[] { expectedSuggestion });
        // add side
        parameters.setProperty("side",
                               Side.Buy.name());
        suggestedOrder.setSide(Side.Buy);
        expectedSuggestion.setOrder(suggestedOrder);
        doSuggestionTest(parameters,
                         new OrderSingleSuggestion[] { expectedSuggestion });
        // add symbol
        parameters.setProperty("symbol",
                               "METC");
        suggestedOrder.setSymbol(new MSymbol("METC"));        
        expectedSuggestion.setOrder(suggestedOrder);
        doSuggestionTest(parameters,
                         new OrderSingleSuggestion[] { expectedSuggestion });
    }
    /**
     * Tests a strategy's ability to send <code>FIX</code> messages.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void sendMessages()
        throws Exception
    {
        Properties parameters = new Properties();
        Date messageDate = new Date();
        parameters.setProperty("date",
                               Long.toString(messageDate.getTime()));
        // null message
        parameters.setProperty("nullMessage",
                               "true");
        doMessageTest(parameters,
                      new FIXOrder[0]);
        // null destination
        parameters.clear();
        parameters.setProperty("date",
                               Long.toString(messageDate.getTime()));
        parameters.setProperty("nullDestination",
                               "true");
        doMessageTest(parameters,
                      new FIXOrder[0]);
        // send a valid message
        parameters.clear();
        parameters.setProperty("date",
                               Long.toString(messageDate.getTime()));
        Message msg = FIXVersion.FIX_SYSTEM.getMessageFactory().newBasicOrder();
        msg.setField(new TransactTime(messageDate));
        doMessageTest(parameters,
                      new FIXOrder[] { Factory.getInstance().createOrder(msg,
                                                                         new DestinationID("some-destination")) } );
    }
    /**
     * Takes a single strategy and starts and stops it many times.
     *
     * @throws Exception
     */
    @Test
    public void startStop()
        throws Exception
    {
        StrategyCoordinates strategy = getStrategyCompiles();
        ModuleURN strategyModule = moduleManager.createModule(StrategyModuleFactory.PROVIDER_URN,
                                                              strategy.getName(),
                                                              getLanguage(),
                                                              strategy.getFile(),
                                                              null,
                                                              null,
                                                              null,
                                                              null);
        int index = 0;
        while (index++ < 500) {
            moduleManager.start(strategyModule);
            moduleManager.stop(strategyModule);
        }
    }
    /**
     * Starts and stops many different strategies.
     *
     * @throws Exception
     */
    @Test
    public void manyStrategiesStartStop()
        throws Exception
    {
        StrategyCoordinates strategy = getStrategyCompiles();
        int index = 0;
        while (index++ < 500) {
            ModuleURN strategyModule = moduleManager.createModule(StrategyModuleFactory.PROVIDER_URN,
                                                                  strategy.getName(),
                                                                  getLanguage(),
                                                                  strategy.getFile(),
                                                                  null,
                                                                  null,
                                                                  null,
                                                                  null);
            moduleManager.start(strategyModule);
            moduleManager.stop(strategyModule);
            moduleManager.deleteModule(strategyModule);
        }
    }
    /**
     * Starts and stops many different strategies.
     *
     * @throws Exception
     */
    @Test
    public void manyStrategiesStartWithoutStop()
        throws Exception
    {
        StrategyCoordinates strategy = getStrategyCompiles();
        int index = 0;
        while (index++ < 500) {
            ModuleURN strategyModule = moduleManager.createModule(StrategyModuleFactory.PROVIDER_URN,
                                                                  strategy.getName(),
                                                                  getLanguage(),
                                                                  strategy.getFile(),
                                                                  null,
                                                                  null,
                                                                  null,
                                                                  null);
            moduleManager.start(strategyModule);
        }
    }
    @Test
    public void mxBeanOperations()
        throws Exception
    {
        StrategyCoordinates strategy = getParameterStrategy(); 
        // create a strategy with no parameters
        ModuleURN strategyURN = createStrategy(strategy.getName(),
                                               getLanguage(),
                                               strategy.getFile(),
                                               null,
                                               null,
                                               null,
                                               null);
        // make sure the starting state is what we think it is
        verifyNullProperties();
        final MockRecorderModule suggestionRecorder = MockRecorderModule.Factory.recorders.get(suggestionsURN);
        assertNotNull("Must be able to find the recorder created",
                      suggestionRecorder);
        final MockRecorderModule orderRecorder = MockRecorderModule.Factory.recorders.get(ordersURN);
        assertNotNull("Must be able to find the recorder created",
                      orderRecorder);
        assertTrue(suggestionRecorder.getDataReceived().isEmpty());
        assertTrue(orderRecorder.getDataReceived().isEmpty());
        // fire events at the strategy
        doSuccessfulStartTestNoVerification(strategyURN);
        // nothing got through because the triggering parameters are not there
        verifyNullProperties();
        assertTrue(suggestionRecorder.getDataReceived().isEmpty());
        assertTrue(orderRecorder.getDataReceived().isEmpty());
        // set new parameters that will cause onAsk to be received
        StrategyMXBean strategyProxy = getMXProxy(strategyURN);
        strategyProxy.setParameters("onAsk=true:emitSuggestion=true:emitMessage=true");
        doSuccessfulStartTestNoVerification(strategyURN);
        // nothing got through because the module was not restarted
        verifyNullProperties();
        assertTrue(suggestionRecorder.getDataReceived().isEmpty());
        assertTrue(orderRecorder.getDataReceived().isEmpty());
        // now cycle the strategy
        moduleManager.stop(strategyURN);
        moduleManager.start(strategyURN);
        doSuccessfulStartTestNoVerification(strategyURN);
        // onAsk got through, but there are still no destinations for the orders and suggestions
        verifyPropertyNonNull("onAsk");
        assertTrue(suggestionRecorder.getDataReceived().isEmpty());
        assertTrue(orderRecorder.getDataReceived().isEmpty());
        // reset
        setPropertiesToNull();
        // now set the suggestions destination
        strategyProxy.setSuggestionsDestination(suggestionsURN.getValue());
        // fire the events
        doSuccessfulStartTestNoVerification(strategyURN);
        // onAsk still goes through, but the others won't until the strategy is cycled
        verifyPropertyNonNull("onAsk");
        assertTrue(suggestionRecorder.getDataReceived().isEmpty());
        assertTrue(orderRecorder.getDataReceived().isEmpty());
        // reset
        setPropertiesToNull();
        // cycle the strategy again
        moduleManager.stop(strategyURN);
        moduleManager.start(strategyURN);
        // fire the events again
        doSuccessfulStartTestNoVerification(strategyURN);
        // onAsk set again
        verifyPropertyNonNull("onAsk");
        // still no order
        assertTrue(orderRecorder.getDataReceived().isEmpty());
        // suggestion now gets through
        assertEquals(1,
                     suggestionRecorder.getDataReceived().size());
        // reset
        setPropertiesToNull();
        suggestionRecorder.resetDataReceived();
        // now set the orders destination
        strategyProxy.setOrdersDestination(ordersURN.getValue());
        // fire the events
        doSuccessfulStartTestNoVerification(strategyURN);
        // onAsk still goes through, and the suggestions goes through, but orders won't until the strategy is cycled
        verifyPropertyNonNull("onAsk");
        assertEquals(1,
                     suggestionRecorder.getDataReceived().size());
        assertTrue(orderRecorder.getDataReceived().isEmpty());
        // reset
        setPropertiesToNull();
        suggestionRecorder.resetDataReceived();
        // cycle the strategy again
        moduleManager.stop(strategyURN);
        moduleManager.start(strategyURN);
        // fire the events again
        doSuccessfulStartTestNoVerification(strategyURN);
        // onAsk set again
        verifyPropertyNonNull("onAsk");
        // order and suggestion got through
        assertEquals(1,
                     suggestionRecorder.getDataReceived().size());
        assertEquals(1,
                     orderRecorder.getDataReceived().size());
        // now make them all go away
        strategyProxy.setParameters(null);
        strategyProxy.setOrdersDestination(null);
        strategyProxy.setSuggestionsDestination(null);
        // reset
        setPropertiesToNull();
        suggestionRecorder.resetDataReceived();
        orderRecorder.resetDataReceived();
        // cycle
        moduleManager.stop(strategyURN);
        moduleManager.start(strategyURN);
        // fire
        doSuccessfulStartTestNoVerification(strategyURN);
        // verify
        verifyNullProperties();
        assertTrue(suggestionRecorder.getDataReceived().isEmpty());
        assertTrue(orderRecorder.getDataReceived().isEmpty());
    }
    /**
     * Makes sure that subscribers to output from one strategy are independent of subscribers to another strategy.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void distinguishingSubscribers()
        throws Exception
    {
        StrategyCoordinates strategy = getSuggestionStrategy();
        Properties parameters = new Properties();
        parameters.setProperty("score",
                               "1");
        parameters.setProperty("identifier",
                               "some identifier");
        // create two strategies that will emit a suggestion, but sign up a different receiver for each
        //  strategy
        createStrategy(strategy.getName(),
                       getLanguage(),
                       strategy.getFile(),
                       parameters,
                       null,
                       null,
                       suggestionsURN);
        createStrategy(strategy.getName(),
                       getLanguage(),
                       strategy.getFile(),
                       parameters,
                       null,
                       null,
                       ordersURN);
        // strategies have now emitted their suggestions, measure the results
        final MockRecorderModule strategy1Recorder = MockRecorderModule.Factory.recorders.get(suggestionsURN);
        final MockRecorderModule strategy2Recorder = MockRecorderModule.Factory.recorders.get(ordersURN);
        // each strategy should have received one and only one suggestion
        assertEquals(1,
                     strategy1Recorder.getDataReceived().size());
        assertEquals(1,
                     strategy2Recorder.getDataReceived().size());
    }
    /**
     * Tests that a Ruby class may be dynamically redefined.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void helperRedefinition()
            throws Exception
    {
        StrategyCoordinates strategy1 = getPart1Strategy();
        doSuccessfulStartTestNoVerification(createStrategy(strategy1.getName(),
                                                           getLanguage(),
                                                           strategy1.getFile(),
                                                           null,
                                                           null,
                                                           null,
                                                           null));
        StrategyCoordinates strategy2 = getPart2Strategy();
        doSuccessfulStartTestNoVerification(createStrategy(strategy2.getName(),
                                                           getLanguage(),
                                                           strategy2.getFile(),
                                                           null,
                                                           null,
                                                           null,
                                                           null));
    }
    /**
     * Tests a strategy's ability to emit notifications.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void notifications()
        throws Exception
    {
        // subscribe to notifications
        final List<Object> publications = new ArrayList<Object>();
        NotificationManager.getNotificationManager().subscribe(new ISubscriber() {
            @Override
            public boolean isInteresting(Object inData)
            {
                return true;
            }
            @Override
            public void publishTo(Object inData)
            {
                publications.add(inData);
            }
        });
        // create a strategy that can emit notifications
        StrategyCoordinates strategy = getStrategyCompiles();
        Properties parameters = new Properties();
        parameters.setProperty("shouldNotify",
                               "true");
        doSuccessfulStartTest(createStrategy(strategy.getName(),
                                             getLanguage(),
                                             strategy.getFile(),
                                             parameters,
                                             null,
                                             null,
                                             null));
        MarketDataFeedTestBase.wait(new Callable<Boolean>() {
            @Override
            public Boolean call()
                    throws Exception
            {
                return publications.size() == 3;
            }
        });
    }
    /**
     * Test a strategy's ability to create and send orders.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void orders()
        throws Exception
    {
        List<OrderSingle> cumulativeOrders = new ArrayList<OrderSingle>();
        ModuleURN strategy = generateOrders(getOrdersStrategy(),
                                            ordersURN);
        // null order
        AbstractRunningStrategy.setProperty("orderShouldBeNull",
                                            "true");
        doOrderTest(strategy,
                    new OrderSingle[0],
                    cumulativeOrders);
        // reset the make-a-null-order flag
        AbstractRunningStrategy.getProperties().clear();
        // create the expected order we'll use as a model for all the test
        OrderSingle expectedOrder = Factory.getInstance().createOrderSingle();
        cumulativeOrders.add(expectedOrder);
        // add an account
        AbstractRunningStrategy.setProperty("account",
                                            "some account");
        expectedOrder.setAccount("some account");
        // add order type
        AbstractRunningStrategy.setProperty("orderType",
                                            OrderType.Market.name());
        expectedOrder.setOrderType(OrderType.Market);
        // add price
        AbstractRunningStrategy.setProperty("price",
                                            "100.23");
        expectedOrder.setPrice(new BigDecimal("100.23"));
        // add quantity
        AbstractRunningStrategy.setProperty("quantity",
                                            "10000");
        expectedOrder.setQuantity(new BigDecimal("10000"));
        // add side
        AbstractRunningStrategy.setProperty("side",
                                            Side.Buy.name());
        expectedOrder.setSide(Side.Buy);
        // add symbol
        AbstractRunningStrategy.setProperty("symbol",
                                            "METC");
        expectedOrder.setSymbol(new MSymbol("METC"));     
        doOrderTest(strategy,
                    new OrderSingle[] { expectedOrder },
                    cumulativeOrders);
        // now do another couple of runs to test the order tracking feature
        OrderSingle expectedOrder2 = Factory.getInstance().createOrderSingle();
        AbstractRunningStrategy.getProperties().clear();
        AbstractRunningStrategy.setProperty("account",
                                            "some other account");
        expectedOrder2.setAccount("some other account");
        AbstractRunningStrategy.setProperty("price",
                                            "400.50");
        expectedOrder2.setPrice(new BigDecimal("400.50"));
        AbstractRunningStrategy.setProperty("symbol",
                                            "GOOG");
        expectedOrder2.setSymbol(new MSymbol("GOOG"));
        AbstractRunningStrategy.setProperty("side",
                                            Side.SellShort.name());
        expectedOrder2.setSide(Side.SellShort);
        cumulativeOrders.add(expectedOrder2);
        doOrderTest(strategy,
                    new OrderSingle[] { expectedOrder2 },
                    cumulativeOrders);
        // three time's a charm
        OrderSingle expectedOrder3 = Factory.getInstance().createOrderSingle();
        AbstractRunningStrategy.getProperties().clear();
        AbstractRunningStrategy.setProperty("account",
                                            "still another account");
        expectedOrder3.setAccount("still another account");
        AbstractRunningStrategy.setProperty("price",
                                            "10000.25");
        expectedOrder3.setPrice(new BigDecimal("10000.25"));
        AbstractRunningStrategy.setProperty("symbol",
                                            "JAVA");
        expectedOrder3.setSymbol(new MSymbol("JAVA"));
        AbstractRunningStrategy.setProperty("side",
                                            Side.Sell.name());
        expectedOrder3.setSide(Side.Sell);
        cumulativeOrders.add(expectedOrder3);
        doOrderTest(strategy,
                    new OrderSingle[] { expectedOrder3 },
                    cumulativeOrders);
        // cycle the strategy, proving that cumulative orders gets reset
        // this will prevent the strategy from sending an order when we next execute the test
        AbstractRunningStrategy.setProperty("orderShouldBeNull",
                                            "true");
        // restart and make sure no orders are in this session
        moduleManager.stop(strategy);
        moduleManager.start(strategy);
        cumulativeOrders.clear();
        doOrderTest(strategy,
                    new OrderSingle[] { },
                    cumulativeOrders);
    }
    /**
     * Tests a strategy's ability to retrieve <code>ExecutionReport</code> values related to its own orders.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void executionReports()
        throws Exception
    {
        // create a strategy that sends its orders to a known module that can also emit execution reports
        generateOrders(getOrdersStrategy(),
                       ordersURN);
        doExecutionReportTest(0,
                              false);
       // create an order and have the strategy submit it
        MockRecorderModule.shouldSendExecutionReports = false;
        doExecutionReportTest(0,
                              true);
        MockRecorderModule.shouldSendExecutionReports = true;
        doExecutionReportTest(1,
                              true);
        // test an order that is split into several execution reports
        StrategyTestBase.executionReportMultiplicity = 4;
        doExecutionReportTest(4,
                              true);
    }
    /**
     * Tests a strategy's ability to cancel all orders submitted.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void cancelAllOrders()
        throws Exception
    {
        // cancel 0 orders
        ModuleURN strategy = generateOrders(getOrdersStrategy(),
                                            ordersURN);
        AbstractRunningStrategy runningStrategy = getFirstRunningStrategyAsAbstractRunningStrategy();
        AbstractRunningStrategy.setProperty("cancelAll",
                                            "true");
        // trigger cancel
        runningStrategy.onTrade(tradeEvent);
        assertEquals(AbstractRunningStrategy.getProperty("ordersCanceled"),
                     "0");
        // create an order to cancel
        runningStrategy.onAsk(askEvent);
        // trigger cancel
        runningStrategy.onTrade(tradeEvent);
        assertEquals(AbstractRunningStrategy.getProperty("ordersCanceled"),
                     "1");
        // create two orders to cancel
        runningStrategy.onAsk(askEvent);
        runningStrategy.onAsk(askEvent);
        // trigger cancel
        runningStrategy.onTrade(tradeEvent);
        assertEquals("2",
                     AbstractRunningStrategy.getProperty("ordersCanceled"));
        // submit another order
        AbstractRunningStrategy.setProperty("ordersCanceled",
                                            "0");
        runningStrategy.onAsk(askEvent);
        // cycle the module to get a fresh session
        moduleManager.stop(strategy);
        moduleManager.start(strategy);
        // trigger a cancel (should do nothing)
        runningStrategy = getFirstRunningStrategyAsAbstractRunningStrategy();
        runningStrategy.onTrade(tradeEvent);
        assertEquals("0",
                     AbstractRunningStrategy.getProperty("ordersCanceled"));
    }
    /**
     * Provides a more in-depth test of strategy order cancel involving {@link ExecutionReport} objects.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void cancelSingleOrder()
        throws Exception
    {
        // set up data for the orders to be created
        // set price
        AbstractRunningStrategy.setProperty("price",
                                            "100.23");
        // add quantity
        AbstractRunningStrategy.setProperty("quantity",
                                            "10000");
        // add side
        AbstractRunningStrategy.setProperty("side",
                                            Side.Buy.name());
        // add symbol
        AbstractRunningStrategy.setProperty("symbol",
                                            "METC");
        // try to cancel an order with a null orderID
        ModuleURN strategy = generateOrders(getOrdersStrategy(),
                                            ordersURN);
        AbstractRunningStrategy runningStrategy = getFirstRunningStrategyAsAbstractRunningStrategy();
        assertNull(AbstractRunningStrategy.getProperty("orderCanceled"));
        runningStrategy.onOther(this);
        assertEquals("false",
                     AbstractRunningStrategy.getProperty("orderCanceled"));
        // now for an orderID that does not match a submitted order
        AbstractRunningStrategy.setProperty("orderCanceled",
                                            "");
        runningStrategy.onOther(new OrderID("this-order-does-not-exist-" + System.nanoTime()));
        assertEquals("false",
                     AbstractRunningStrategy.getProperty("orderCanceled"));
        // submit an order
        assertNull(AbstractRunningStrategy.getProperty("orderID"));
        runningStrategy.onAsk(askEvent);
        String orderIDString = AbstractRunningStrategy.getProperty("orderID"); 
        assertNotNull(orderIDString);
        // start and stop the strategy
        moduleManager.stop(strategy);
        moduleManager.start(strategy);
        AbstractRunningStrategy.setProperty("orderCanceled",
                                            "");
        runningStrategy = getFirstRunningStrategyAsAbstractRunningStrategy();
        // make sure that order cannot be canceled now
        runningStrategy.onOther(new OrderID(orderIDString));
        assertEquals("false",
                     AbstractRunningStrategy.getProperty("orderCanceled"));
        // submit an order that will produce a single ER
        AbstractRunningStrategy.setProperty("executionReportsReceived",
                                            "0");
        AbstractRunningStrategy.setProperty("orderCanceled",
                                            "");
        MockRecorderModule.shouldSendExecutionReports = true;
        StrategyTestBase.executionReportMultiplicity = 1;
        runningStrategy.onAsk(askEvent);
        assertEquals("1",
                     AbstractRunningStrategy.getProperty("executionReportsReceived"));
        // cancel that order (using the received ER)
        orderIDString = AbstractRunningStrategy.getProperty("orderID"); 
        assertNotNull(orderIDString);        
        runningStrategy.onOther(new OrderID(orderIDString));
        assertEquals("true",
                     AbstractRunningStrategy.getProperty("orderCanceled"));
        // submit an order that will produce multiple ERs
        AbstractRunningStrategy.setProperty("executionReportsReceived",
                                            "0");
        AbstractRunningStrategy.setProperty("orderCanceled",
                                            "");
        StrategyTestBase.executionReportMultiplicity = 10;
        runningStrategy.onAsk(askEvent);
        assertEquals("10",
                     AbstractRunningStrategy.getProperty("executionReportsReceived"));
        // cancel that order (using the received ER)
        orderIDString = AbstractRunningStrategy.getProperty("orderID"); 
        assertNotNull(orderIDString);        
        runningStrategy.onOther(new OrderID(orderIDString));
        assertEquals("true",
                     AbstractRunningStrategy.getProperty("orderCanceled"));
    }
    /**
     * Tests a strategy's ability to cancel an existing order and replace it with a new one.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void cancelReplace()
        throws Exception
    {
        // for now, forbid the creation of execution reports
        MockRecorderModule.shouldSendExecutionReports = false;
        // set up data for the orders to be created
        // set price
        AbstractRunningStrategy.setProperty("price",
                                            "100.23");
        // add quantity
        AbstractRunningStrategy.setProperty("quantity",
                                            "10000");
        // add side
        AbstractRunningStrategy.setProperty("side",
                                            Side.Buy.name());
        // add symbol
        AbstractRunningStrategy.setProperty("symbol",
                                            "METC");
        // add time-in-force
        AbstractRunningStrategy.setProperty("timeInForce",
                                            TimeInForce.Day.toString());
        // create a strategy to use as our test vehicle
        ModuleURN strategy = generateOrders(getOrdersStrategy(),
                                            ordersURN);
        AbstractRunningStrategy runningStrategy = getFirstRunningStrategyAsAbstractRunningStrategy();
        // submit an order
        assertNull(AbstractRunningStrategy.getProperty("orderID"));
        runningStrategy.onAsk(askEvent);
        // save the orderID of the order we created, we'll need it later
        String orderIDString = AbstractRunningStrategy.getProperty("orderID"); 
        assertNotNull(orderIDString);
        // try to cancel/replace with a null OrderID
        AbstractRunningStrategy.setProperty("orderID",
                                            null);
        AbstractRunningStrategy.setProperty("newOrderID",
                                            "");
        OrderSingle newOrder = Factory.getInstance().createOrderSingle();
        assertNotNull(AbstractRunningStrategy.getProperty("newOrderID"));
        newOrder.setPrice(new BigDecimal("1000.50"));
        newOrder.setQuantity(new BigDecimal("1"));
        newOrder.setTimeInForce(TimeInForce.GoodTillCancel);
        // try to cancel/replace with a null OrderID
        runningStrategy.onOther(newOrder);
        assertNull(AbstractRunningStrategy.getProperty("newOrderID"));
        // try to cancel/replace with an unsubmitted OrderID
        OrderID unsubmittedOrderID = new OrderID("this-order-id-does-not-exist-" + System.nanoTime());
        AbstractRunningStrategy.setProperty("orderID",
                                            unsubmittedOrderID.toString());
        AbstractRunningStrategy.setProperty("newOrderID",
                                            "");
        assertNotNull(AbstractRunningStrategy.getProperty("newOrderID"));
        runningStrategy.onOther(newOrder);
        assertNull(AbstractRunningStrategy.getProperty("newOrderID"));
        // try with a null replacement order
        // put back the orderID of the order to replace
        AbstractRunningStrategy.setProperty("orderID",
                                            orderIDString);
        AbstractRunningStrategy.setProperty("newOrderID",
                                            "");
        runningStrategy.onOther("");
        assertNull(AbstractRunningStrategy.getProperty("newOrderID"));
        // now allow the cancel/replace to succeed
        runningStrategy.onOther(newOrder);
        assertNotNull(AbstractRunningStrategy.getProperty("newOrderID"));
        // hooray, it worked
        // turn on execution reports
        MockRecorderModule.shouldSendExecutionReports = true;
        StrategyTestBase.executionReportMultiplicity = 20;
        // re-submit the order
        runningStrategy.onAsk(askEvent);
        assertNotNull(AbstractRunningStrategy.getProperty("orderID"));
        assertFalse(orderIDString.equals(AbstractRunningStrategy.getProperty("orderID")));
        AbstractRunningStrategy.setProperty("newOrderID",
                                            null);
        // cancel/replace again (this time using execution reports)
        runningStrategy.onOther(newOrder);
        assertNotNull(AbstractRunningStrategy.getProperty("newOrderID"));
        // submit an order again
        AbstractRunningStrategy.setProperty("orderID",
                                            null);
        runningStrategy.onAsk(askEvent);
        orderIDString = AbstractRunningStrategy.getProperty("orderID"); 
        assertNotNull(orderIDString);
        // cycle the strategy
        moduleManager.stop(strategy);
        moduleManager.start(strategy);
        runningStrategy = getFirstRunningStrategyAsAbstractRunningStrategy();
        AbstractRunningStrategy.setProperty("newOrderID",
                                            null);
        // try to cancel/replace again, won't work because the order to be replaced was in the last strategy session (before the stop)
        runningStrategy.onOther(newOrder);
        assertNull(AbstractRunningStrategy.getProperty("newOrderID"));
    }
    /**
     * Gets the language to use for this test.
     *
     * @return a <code>Language</code> value
     */
    protected abstract Language getLanguage();
    /**
     * Get a strategy that will not compile in the given language.
     *
     * @return a <code>StrategyCoordinates</code> value
     */
    protected abstract StrategyCoordinates getStrategyWillNotCompile();
    /**
     * Get a strategy that will compile in the given language.
     *
     * @return a <code>StrategyCoordinates</code> value
     */
    protected abstract StrategyCoordinates getStrategyCompiles();
    /**
     * Get a strategy whose main class does not subclass {@link RunningStrategy}.
     *
     * @return a <code>StrategyCoordinates</code> value
     */
    protected abstract StrategyCoordinates getStrategyWrongClass();
    /**
     * Get a strategy with multiple classes. 
     *
     * @return a <code>StrategyCoordinates</code> value
     */
    protected abstract StrategyCoordinates getStrategyMultipleClasses();
    /**
     * Get a strategy which overrides only "onAsk".
     *
     * @return a <code>StrategyCoordinates</code> value
     */
    protected abstract StrategyCoordinates getEmptyStrategy();
    /**
     * Get a strategy which expects parameters.
     *
     * @return a <code>StrategyCoordinates</code> value
     */
    protected abstract StrategyCoordinates getParameterStrategy();
    /**
     * Get a strategy which executes trade suggestions.
     *
     * @return a <code>StrategyCoordinates</code> value
     */
    protected abstract StrategyCoordinates getSuggestionStrategy();
    /**
     * Get a strategy which sends FIX messages.
     *
     * @return a <code>StrategyCoordinates</code> value
     */
    protected abstract StrategyCoordinates getMessageStrategy();
    /**
     * Get a strategy that sends orders.
     *
     * @return a <code>StrategyCoordinates</code> value
     */
    protected abstract StrategyCoordinates getOrdersStrategy();
    /**
     * Gets a strategy that defines and calls into a helper class. 
     *
     * @return a <code>StrategyCoordinates</code> value
     */
    protected abstract StrategyCoordinates getPart1Strategy();
    /**
     * Get a strategy that redefines the class and helper defined in {@link #getPart1Strategy()}. 
     *
     * @return a <code>StrategyCoordinates</code> value
     */
    protected abstract StrategyCoordinates getPart1RedefinedStrategy();
    /**
     * Get a strategy that complements the classes defined in {@link #getPart1Strategy()}. 
     *
     * @return a <code>StrategyCoordinates</code> value
     */
    protected abstract StrategyCoordinates getPart2Strategy();
    /**
     * Starts a strategy module which generates <code>FIX</code> messages and measures them against the
     * give expected results.  
     *
     * @param inParameters a <code>Properties</code> value
     * @param inExpectedOrders a <code>FIXOrder[]</code> value
     * @throws Exception if an error occurs
     */
    private void doMessageTest(Properties inParameters,
                               final FIXOrder[] inExpectedOrders)
        throws Exception
    {
        ModuleURN suggestionReceiver = generateOrders(getMessageStrategy(),
                                                      inParameters);
        final MockRecorderModule recorder = MockRecorderModule.Factory.recorders.get(suggestionReceiver);
        assertNotNull("Must be able to find the recorder created",
                      recorder);
        List<DataReceived> messages = recorder.getDataReceived();
        assertEquals("The number of expected messages does not match the number of actual messages",
                     inExpectedOrders.length,
                     messages.size());
        int index = 0;
        for(DataReceived datum : messages) {
            TypesTestBase.assertOrderFIXEquals(inExpectedOrders[index++],
                                               (FIXOrder)datum.getData());
        }
        recorder.resetDataReceived();
    }
    /**
     * Creates a strategy module from the given script with the given parameters and returns the
     * <code>ModuleURN</code> of the module that received any generated orders.
     *
     * @param inStrategy a <code>StrategyCoordinates</code> value
     * @param inParameters a <code>Properties</code> value
     * @return a <code>ModuleURN</code> value
     * @throws Exception if an error occurs
     */
    private ModuleURN generateOrders(StrategyCoordinates inStrategy,
                                     Properties inParameters)
        throws Exception
    {
        // start the strategy pointing at the suggestion receiver for its suggestions
        createStrategy(inStrategy.getName(),
                       getLanguage(),
                       inStrategy.getFile(),
                       inParameters,
                       null,
                       ordersURN,
                       null);
        return ordersURN;
    }
    /**
     * Starts a strategy module which generates suggestions and measures them against the
     * given expected results.
     *
     * @param inParameters a <code>Properties</code> value
     * @param inExpectedSuggestions an <code>OrderSingleSuggestion[]</code> value
     * @throws Exception if an error occurs
     */
    private void doSuggestionTest(Properties inParameters,
                                  final OrderSingleSuggestion[] inExpectedSuggestions)
        throws Exception
    {
        ModuleURN suggestionReceiver = generateSuggestions(getSuggestionStrategy(),
                                                           inParameters,
                                                           suggestionsURN);
        final MockRecorderModule recorder = MockRecorderModule.Factory.recorders.get(suggestionReceiver);
        assertNotNull("Must be able to find the recorder created",
                      recorder);
        List<DataReceived> suggestions = recorder.getDataReceived();
        assertEquals("The number of expected suggestions does not match the number of actual suggestions",
                     inExpectedSuggestions.length,
                     suggestions.size());
        int index = 0;
        for(DataReceived datum : suggestions) {
            TypesTestBase.assertOrderSuggestionEquals(inExpectedSuggestions[index++],
                                                      (OrderSingleSuggestion)datum.getData(),
                                                      true);
        }
        recorder.resetDataReceived();
    }
    /**
     * Performs a single iteration of an <code>ExecutionReport</code> test. 
     *
     * @param inExecutionReportCount an <code>int</code> value containing the number of execution reports expected
     * @param inSendOrders a <code>boolean</code> value indicating if the orders should be submitted or not (simulate failure)
     * @throws Exception if an error occurs
     */
    private void doExecutionReportTest(final int inExecutionReportCount,
                                       boolean inSendOrders)
        throws Exception
    {
        AbstractRunningStrategy.setProperty("executionReportCount",
                                            "0");
        AbstractRunningStrategy.setProperty("price",
                                            "1000");
        AbstractRunningStrategy.setProperty("quantity",
                                            "500");
        AbstractRunningStrategy.setProperty("side",
                                            Side.Sell.toString());
        AbstractRunningStrategy.setProperty("symbol",
                                            "METC");
        // generate expected order
        List<ExecutionReport> expectedExecutionReports = new ArrayList<ExecutionReport>();
        StrategyImpl runningStrategy = getFirstRunningStrategy();
        OrderID orderID = null;
        if(inSendOrders) {
            // this will trigger the strategy to submit an order
            runningStrategy.dataReceived(askEvent);
            // generate expected result
            OrderSingle expectedOrder = Factory.getInstance().createOrderSingle();
            expectedOrder.setPrice(new BigDecimal("1000"));
            expectedOrder.setQuantity(new BigDecimal("500"));
            expectedOrder.setSide(Side.Sell);
            expectedOrder.setSymbol(new MSymbol("METC"));
            String orderIDString = AbstractRunningStrategy.getProperty("orderID");
            if(orderIDString != null) {
                orderID = new OrderID(orderIDString);
                expectedOrder.setOrderID(new OrderID(orderIDString));
            }
            if(MockRecorderModule.shouldSendExecutionReports) {
                expectedExecutionReports.addAll(generateExecutionReports(expectedOrder));
            }
        }
        runningStrategy.dataReceived(new BidEvent(System.nanoTime(),
                                                  System.currentTimeMillis(),
                                                  "METC",
                                                  "Q",
                                                  new BigDecimal("100.00"),
                                                  new BigDecimal("10000")));
        assertEquals(inExecutionReportCount,
                     Integer.parseInt(AbstractRunningStrategy.getProperty("executionReportCount")));
        List<ExecutionReport> actualExecutionReports = ((AbstractRunningStrategy)runningStrategy.getRunningStrategy()).getExecutionReports(orderID);
        assertEquals(expectedExecutionReports.size(),
                     actualExecutionReports.size());
        int index = 0;
        for(ExecutionReport actualExecutionReport : actualExecutionReports) {
            TypesTestBase.assertExecReportEquals(expectedExecutionReports.get(index++),
                                                 actualExecutionReport);
        }
        AbstractRunningStrategy.getProperties().clear();
        MockRecorderModule.ordersReceived = 0;    
    }
    /**
     * Starts a strategy module which generates orders and measures them against the
     * given expected results.
     * @param inStrategy a <code>ModuleURN</code> value
     * @param inExpectedOrders an <code>OrderSingle[]</code> value
     * @param inCumulativeOrders a <code>List&lt;OrderSingle&gt;</code> value
     * @throws Exception if an error occurs
     */
    private void doOrderTest(ModuleURN inStrategy,
                             final OrderSingle[] inExpectedOrders,
                             List<OrderSingle> inExpectedCumulativeOrders)
        throws Exception
    {
        final MockRecorderModule recorder = MockRecorderModule.Factory.recorders.get(ordersURN);
        assertNotNull("Must be able to find the recorder created",
                      recorder);
        // this will execute onAsk on the strategies, which will generate the desired order
        recorder.resetDataReceived();
        doSuccessfulStartTestNoVerification(inStrategy);
        List<DataReceived> orders = recorder.getDataReceived();
        assertEquals("The number of expected orders does not match the number of actual orders",
                     inExpectedOrders.length,
                     orders.size());
        int index = 0;
        for(DataReceived datum : orders) {
            TypesTestBase.assertOrderSingleEquals(inExpectedOrders[index++],
                                                  (OrderSingle)datum.getData(),
                                                  true);
        }
        StrategyImpl runningStrategy = getFirstRunningStrategy();
        List<OrderSingle> actualCumulativeOrders = ((AbstractRunningStrategy)runningStrategy.getRunningStrategy()).getSubmittedOrders();
        assertEquals(inExpectedCumulativeOrders.size(),
                     actualCumulativeOrders.size());
        index = 0;
        for(OrderSingle actualOrder : actualCumulativeOrders) {
            TypesTestBase.assertOrderSingleEquals(inExpectedCumulativeOrders.get(index++),
                                                  actualOrder,
                                                  true);
        }
    }
    /**
     * Creates a strategy module from the given script with the given parameters and returns the
     * <code>ModuleURN</code> of the module that received any generated order suggestions.
     *
     * @param inStrategy a <code>StrategyCoordinates</code> value
     * @param inParameters a <code>Properties</code> value
     * @param inSuggestionsURN a <code>ModuleURN</code> value containing the destination to which to emit suggestions
     * @return a <code>ModuleURN</code> value
     * @throws Exception if an error occurs
     */
    private ModuleURN generateSuggestions(StrategyCoordinates inStrategy,
                                          Properties inParameters,
                                          ModuleURN inSuggestionsURN)
        throws Exception
    {
        // start the strategy pointing at the suggestion receiver for its suggestions
        createStrategy(inStrategy.getName(),
                       getLanguage(),
                       inStrategy.getFile(),
                       inParameters,
                       null,
                       null,
                       inSuggestionsURN);
        return inSuggestionsURN;
    }
    /**
     * Creates a strategy module from the given script with the given parameters and returns the
     * <code>ModuleURN</code> of the module that received any generated orders.
     *
     * @param inStrategy a <code>StrategyCoordinates</code> value
     * @param inOrdersURN a <code>ModuleURN</code> value containing the destination to which to emit suggestions
     * @return a <code>ModuleURN</code> value
     * @throws Exception if an error occurs
     */
    private ModuleURN generateOrders(StrategyCoordinates inStrategy,
                                     ModuleURN inOrdersURN)
        throws Exception
    {
        // start the strategy pointing at the orders receiver for its orders
        ModuleURN strategy = createStrategy(inStrategy.getName(),
                                            getLanguage(),
                                            inStrategy.getFile(),
                                            null,
                                            null,
                                            inOrdersURN,
                                            null);
        return strategy;
    }
    /**
     * Creates a strategy that requests market data from the given provider for the given symbols.
     *
     * @param inProvider a <code>String</code> value containing the instance identifier of a market data provider
     * @param inSymbols a <code>String</code> value containing a comma-separated list of symbols for which to
     *   request market data
     * @return a <code>ModuleURN</code> value containing the instance URN of the strategy guaranteed to be running
     * @throws Exception if an error occurs
     */
    private ModuleURN getMarketData(String inProvider,
                                    String inSymbols)
        throws Exception
    {
        final StrategyCoordinates strategy = getStrategyCompiles();
        final Properties parameters = new Properties();
        parameters.setProperty("shouldRequestData",
                               inProvider);
        parameters.setProperty("symbols",
                               inSymbols);
        return createStrategy(strategy.getName(),
                              getLanguage(),
                              strategy.getFile(),
                              parameters,
                              null,
                              null,
                              null);
    }
    /**
     * Tests that the given strategy is functional by verifying it receives data sent to it.
     *
     * @param inStrategy a <code>ModuleURN</code> value containing a reference to a started strategy
     * @throws Exception if an error occurs
     */
    private void doSuccessfulStartTest(ModuleURN inStrategy)
        throws Exception
    {
        doSuccessfulStartTestNoVerification(inStrategy);
        // verify the data was received
        verifyNonNullProperties();
    }
    /**
     * Tests that the given strategy has been started and can receive data.
     *
     * @param inStrategy a <code>ModuleURN</code> value
     * @throws Exception if an error occurs
     */
    protected void doSuccessfulStartTestNoVerification(ModuleURN inStrategy)
        throws Exception
    {
        // create an emitter module that will emit the types of data that the strategy must be able to process
        ModuleURN dataEmitterURN = createModule(StrategyTestBase.StrategyDataEmissionModule.Factory.PROVIDER_URN);
        // plumb the emitter together with the strategy (the data is transmitted when the request is made)
        DataFlowID dataFlowID = moduleManager.createDataFlow(new DataRequest[] { new DataRequest(dataEmitterURN,
                                                                                                 null),
                                                                                                 new DataRequest(inStrategy) },
                                                                                                 false);
        // shut down the flow
        moduleManager.cancel(dataFlowID);
    }
    /**
     * Tests that the given callback fails as expected. 
     *
     * <p>Note that this test requires the cooperation of the strategy being tested.  The strategy
     * is supposed to look for the presence of the given parameter and fail in the appropriate callback
     * however it likes.
     * 
     * @param inParameterThatCausesACallbackToFail a <code>String</code> value
     * @param inCallbacksThatShouldHaveSucceeded a <code>String[]</code> value
     * @throws Exception if an error occurs
     */
    private void doCallbackFailsTest(String inParameterThatCausesACallbackToFail,
                                     String[] inCallbacksThatShouldHaveSucceeded)
        throws Exception
    {
        setPropertiesToNull();
        final StrategyCoordinates strategy = getStrategyCompiles();
        final Properties parameters = new Properties();
        parameters.setProperty(inParameterThatCausesACallbackToFail,
                               "true");
        doSuccessfulStartTestNoVerification(createStrategy(strategy.getName(),
                                                           getLanguage(),
                                                           strategy.getFile(),
                                                           parameters,
                                                           null,
                                                           null,
                                                           null));
        Set<String> allCallbacks = new HashSet<String>(Arrays.asList(new String[] { "onAsk", "onBid", "onCancel", "onExecutionReport", "onTrade", "onOther" }));
        for(String callback : inCallbacksThatShouldHaveSucceeded) {
            verifyPropertyNonNull(callback);
            allCallbacks.remove(callback);
        }
        for(String callbackShouldBeNull : allCallbacks) {
            verifyPropertyNull(callbackShouldBeNull);
        }
    }
    /**
     * Tests that two strategies with the same class name can co-exist.
     *
     * @throws Exception
     */
    @Test
    public void strategiesOfSameClass()
            throws Exception
    {
        StrategyCoordinates strategy1 = getPart1Strategy();
        ModuleURN strategy1URN = createStrategy(strategy1.getName(),
                                                getLanguage(),
                                                strategy1.getFile(),
                                                null,
                                                null,
                                                null,
                                                null);
        ModuleURN strategy2URN = createStrategy(strategy1.getName(),
                                                getLanguage(),
                                                strategy1.getFile(),
                                                null,
                                                null,
                                                null,
                                                null);
        doSuccessfulStartTestNoVerification(strategy1URN);
        doSuccessfulStartTestNoVerification(strategy2URN);
        Set<StrategyImpl> runningStrategies = StrategyImpl.getRunningStrategies();
        // should be two running strategies
        assertEquals(2,
                     runningStrategies.size());
        // execute the onCallback method in each running strategy
        for(StrategyImpl runningStrategy : runningStrategies) {
            runningStrategy.getRunningStrategy().onCallback(null);
        }
        // both strategies get their own onCallback called
        assertEquals("2",
                     AbstractRunningStrategy.getProperty("onCallback"));
        // there should be two callbacks registered
        String strategyName1 = AbstractRunningStrategy.getProperty("callback1");
        String strategyName2 = AbstractRunningStrategy.getProperty("callback2");
        assertFalse(strategyName1.equals(strategyName2));
    }
    /**
     * Verifies that a Strategy may be dynamically redefined. 
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void redefinedStrategy()
            throws Exception
    {
        StrategyCoordinates strategy1 = getPart1Strategy();
        StrategyCoordinates strategy2 = getPart1RedefinedStrategy();
        ModuleURN strategy1URN = createStrategy(strategy1.getName(),
                                                getLanguage(),
                                                strategy1.getFile(),
                                                null,
                                                null,
                                                null,
                                                null);
        ModuleURN strategy2URN = createStrategy(strategy2.getName(),
                                                getLanguage(),
                                                strategy2.getFile(),
                                                null,
                                                null,
                                                null,
                                                null);
        doSuccessfulStartTestNoVerification(strategy1URN);
        doSuccessfulStartTestNoVerification(strategy2URN);
        setPropertiesToNull();
        // strategies have started and are working
        Set<StrategyImpl> runningStrategies = StrategyImpl.getRunningStrategies();
        // should be two running strategies
        assertEquals(2,
                     runningStrategies.size());
        // execute the onAsk method in each running strategy
        for(StrategyImpl runningStrategy : runningStrategies) {
            runningStrategy.getRunningStrategy().onAsk(askEvent);
        }
        // both strategies should get their onAsk called, but the definition should be the second one
        Properties properties = AbstractRunningStrategy.getProperties();
        int askCounter = 0;
        for(Object key : properties.keySet()) {
            String keyString = (String)key;
            if(keyString.startsWith("ask")) {
                askCounter += 1;
                assertTrue(properties.getProperty(keyString).startsWith("part1"));
            }
        }
        assertEquals(2,
                     askCounter);
    }
}
