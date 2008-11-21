package org.marketcetera.strategy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.marketcetera.strategy.Messages.FAILED_TO_START;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;
import org.marketcetera.core.MSymbol;
import org.marketcetera.marketdata.bogus.BogusFeedModuleFactory;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.module.ModuleException;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.strategy.StrategyTestBase.MockRecorderModule.DataReceived;
import org.marketcetera.trade.*;

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
     * <p>TODO This test is disabled for now because the Java compiler isn't up and running yet, so Ruby tests won't fail. 
     * 
     * @throws Exception if an error occurs
     */
    @Test @Ignore
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
     * Tests the scenario where the name of the scenario does not match
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
    @Test @Ignore
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
        Set<StrategyImpl> runningStrategies = StrategyImpl.getRunningStrategies();
        // should be only one running strategy, makes the job easier to find it
        assertEquals(1,
                     runningStrategies.size());
        // execute the onCallback method in the running strategy to force the market data
        //  request cancel
        runningStrategies.iterator().next().getRunningStrategy().onCallback(this);
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
        Set<StrategyImpl> runningStrategies = StrategyImpl.getRunningStrategies();
        // should be only one running strategy, makes the job easier to find it
        assertEquals(1,
                     runningStrategies.size());
        // execute the onCallback method in the running strategy to force the market data
        //  request cancel
        runningStrategies.iterator().next().getRunningStrategy().onCallback(this);
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
     * Takes a single strategy and starts and stops it many times.
     *
     * @throws Exception
     */
    @Test
    public void startStop()
        throws Exception
    {
        StrategyCoordinates strategy = StrategyCoordinates.get(RubyLanguageTest.STRATEGY,
                                                               RubyLanguageTest.STRATEGY_NAME);
        ModuleURN strategyModule = moduleManager.createModule(StrategyModuleFactory.PROVIDER_URN,
                                                              strategy.getName(),
                                                              Language.RUBY,
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
        StrategyCoordinates strategy = StrategyCoordinates.get(RubyLanguageTest.STRATEGY,
                                                               RubyLanguageTest.STRATEGY_NAME);
        int index = 0;
        while (index++ < 500) {
            ModuleURN strategyModule = moduleManager.createModule(StrategyModuleFactory.PROVIDER_URN,
                                                                  strategy.getName(),
                                                                  Language.RUBY,
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
        StrategyCoordinates strategy = StrategyCoordinates.get(RubyLanguageTest.STRATEGY,
                                                               RubyLanguageTest.STRATEGY_NAME);
        int index = 0;
        while (index++ < 500) {
            ModuleURN strategyModule = moduleManager.createModule(StrategyModuleFactory.PROVIDER_URN,
                                                                  strategy.getName(),
                                                                  Language.RUBY,
                                                                  strategy.getFile(),
                                                                  null,
                                                                  null,
                                                                  null,
                                                                  null);
            moduleManager.start(strategyModule);
        }
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
     * Starts a strategy module which generates suggestions and measures them against the
     * given expected results.
     *
     * @param inParameters a <code>Properties</code> value
     * @param inExpectedSuggestions an <code>OrderSingleSuggestion[]</code> value
     * @throws Exception if an error occurs
     */
    private void doSuggestionTest(Properties inParameters,
                                  OrderSingleSuggestion[] inExpectedSuggestions)
        throws Exception
    {
        ModuleURN suggestionReceiver = generateSuggestions(getSuggestionStrategy(),
                                                           inParameters);
        MockRecorderModule recorder = MockRecorderModule.Factory.recorders.get(suggestionReceiver);
        assertNotNull("Must be able to find the recorder created",
                      recorder);
        List<DataReceived> suggestions = recorder.getDataReceived();
        assertEquals("The number of expected suggestions does not match the number of actual suggestions",
                     inExpectedSuggestions.length,
                     suggestions.size());
        int index = 0;
        for(DataReceived datum : suggestions) {
            TypesTestBase.assertOrderSuggestionEquals(inExpectedSuggestions[index++],
                                                      (OrderSingleSuggestion)datum.getData(), true);
        }
        recorder.resetDataReceived();
    }
    /**
     * Creates a strategy module from the given script with the given parameters and returns the
     * <code>ModuleURN</code> of the module that received any generated order suggestions.
     *
     * @param inStrategy a <code>StrategyCoordinates</code> value
     * @param inParameters a <code>Properties</code> value
     * @return a <code>ModuleURN</code> value
     * @throws Exception if an error occurs
     */
    private ModuleURN generateSuggestions(StrategyCoordinates inStrategy,
                                          Properties inParameters)
        throws Exception
    {
        // start the strategy pointing at the suggestion receiver for its suggestions
        ModuleURN strategyURN = createStrategy(inStrategy.getName(),
                                               getLanguage(),
                                               inStrategy.getFile(),
                                               inParameters,
                                               null,
                                               null,
                                               suggestionsURN);
        moduleManager.stop(strategyURN);
        return suggestionsURN;
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
}
