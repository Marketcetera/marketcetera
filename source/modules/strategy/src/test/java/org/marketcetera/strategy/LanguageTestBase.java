package org.marketcetera.strategy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;
import static org.marketcetera.event.LogEvent.Level.DEBUG;
import static org.marketcetera.event.LogEvent.Level.ERROR;
import static org.marketcetera.event.LogEvent.Level.INFO;
import static org.marketcetera.event.LogEvent.Level.WARN;
import static org.marketcetera.module.Messages.MODULE_NOT_STARTED_STATE_INCORRECT;
import static org.marketcetera.module.Messages.MODULE_NOT_STOPPED_STATE_INCORRECT;
import static org.marketcetera.strategy.Language.JAVA;
import static org.marketcetera.strategy.Status.FAILED;
import static org.marketcetera.strategy.Status.RUNNING;
import static org.marketcetera.strategy.Status.STARTING;
import static org.marketcetera.strategy.Status.STOPPED;
import static org.marketcetera.strategy.Status.STOPPING;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Callable;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.marketcetera.client.brokers.BrokerStatus;
import org.marketcetera.client.brokers.BrokersStatus;
import org.marketcetera.core.notifications.Notification;
import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.EventBase;
import org.marketcetera.event.LogEvent;
import org.marketcetera.event.LogEventTest;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.marketdata.MarketDataFeedTestBase;
import org.marketcetera.marketdata.bogus.BogusFeedModuleFactory;
import org.marketcetera.module.CopierModuleFactory;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.module.ModuleException;
import org.marketcetera.module.ModuleStateException;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.module.CopierModule.SynchronousRequest;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.strategy.StrategyTestBase.MockRecorderModule.DataReceived;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.FIXOrder;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.MSymbol;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.trade.OrderSingleSuggestion;
import org.marketcetera.trade.OrderType;
import org.marketcetera.trade.Side;
import org.marketcetera.trade.TimeInForce;
import org.marketcetera.trade.TypesTestBase;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.test.UnicodeData;

import quickfix.Message;
import quickfix.field.TransactTime;

import com.sun.jna.Platform;

/* $License$ */

/**
 * Provides a set of tests for a script language.
 * 
 * <p>Each new script language to support should extend this class.
 * 
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.0.0
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
        assumeTrue(!(Platform.isWindows() && getLanguage().equals(JAVA)));
        StrategyCoordinates strategy = getStrategyCompiles();
        ModuleURN strategyModule = createStrategy(strategy.getName(),
                                                  getLanguage(),
                                                  strategy.getFile(),
                                                  null,
                                                  null,
                                                  null);
        verifyPropertyNonNull("onStart");
        doSuccessfulStartTest(strategyModule);
        stopStrategy(strategyModule);
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
        assumeTrue(!(Platform.isWindows() && getLanguage().equals(JAVA)));
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
        assumeTrue(!(Platform.isWindows() && getLanguage().equals(JAVA)));
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
        assumeTrue(!(Platform.isWindows() && getLanguage().equals(JAVA)));
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
        assumeTrue(!(Platform.isWindows() && getLanguage().equals(JAVA)));
        StrategyCoordinates strategy = getStrategyMultipleClasses();
        doSuccessfulStartTest(createStrategy(strategy.getName(),
                                             getLanguage(),
                                             strategy.getFile(),
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
        assumeTrue(!(Platform.isWindows() && getLanguage().equals(JAVA)));
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
        assumeTrue(!(Platform.isWindows() && getLanguage().equals(JAVA)));
        StrategyCoordinates strategy = getEmptyStrategy();
        // need to piece this together manually as only "onAsk" will be set
        ModuleURN strategyURN = createStrategy(strategy.getName(),
                                               getLanguage(),
                                               strategy.getFile(),
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
        assumeTrue(!(Platform.isWindows() && getLanguage().equals(JAVA)));
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
        assumeTrue(!(Platform.isWindows() && getLanguage().equals(JAVA)));
        // runtime error in onStart
        final StrategyCoordinates strategy = getStrategyCompiles();
        final Properties parameters = new Properties();
        parameters.setProperty("shouldFailOnStart",
                               "true");
        // failed "onStart" means that the strategy is in error status and will not receive any data
        ModuleURN strategyURN = createStrategy(strategy.getName(),
                                               getLanguage(),
                                               strategy.getFile(),
                                               parameters,
                                               null,
                                               null);
        // "onStart" has completed, but verify that the last statement in the strategy was never executed
        verifyPropertyNull("onStart");
        // verify the status of the strategy
        verifyStrategyStatus(strategyURN,
                             FAILED);
        setPropertiesToNull();
        parameters.clear();
        AbstractRunningStrategy.setProperty("shouldFailOnStop",
                                            "true");
        // runtime error in onStop
        strategyURN = createStrategy(strategy.getName(),
                                     getLanguage(),
                                     strategy.getFile(),
                                     parameters,
                                     null,
                                     null);
        doSuccessfulStartTest(strategyURN);
        stopStrategy(strategyURN);
        AbstractRunningStrategy.setProperty("shouldFailOnStop",
                                            null);
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
    /**
     * Tests a strategy with an arbitrarily long onStart.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void longRunningStart()
        throws Exception
    {
        assumeTrue(!(Platform.isWindows() && getLanguage().equals(JAVA)));
        final StrategyCoordinates strategy = getStrategyCompiles();
        final Properties parameters = new Properties();
        parameters.setProperty("shouldLoopOnStart",
                               "true");
        assertNull(AbstractRunningStrategy.getProperty("loopDone"));
        // need to manually start the strategy because it will be in "STARTING" status for a long long time
        final ModuleURN strategyURN = createModule(StrategyModuleFactory.PROVIDER_URN,
                                                   null,
                                                   strategy.getName(),
                                                   getLanguage(),
                                                   strategy.getFile(),
                                                   parameters,
                                                   null,
                                                   null);
        // wait until the strategy enters "STARTING"
        MarketDataFeedTestBase.wait(new Callable<Boolean>(){
            @Override
            public Boolean call()
                    throws Exception
            {
                return getStatus(strategyURN).equals(STARTING);
            }
        });
        // take a little snooze - long enough that the strategy "onStart" will have completed if it was going to
        Thread.sleep(5000);
        // verify the status hasn't changed and that "onStart" hasn't completed
        assertNull(AbstractRunningStrategy.getProperty("loopDone"));
        verifyStrategyStatus(strategyURN,
                             STARTING);
        // tell the loop to stop
        AbstractRunningStrategy.setProperty("shouldStopLoop",
                                            "true");
        // wait until the strategy has time to complete
        MarketDataFeedTestBase.wait(new Callable<Boolean>(){
            @Override
            public Boolean call()
                    throws Exception
            {
                return getStatus(strategyURN).equals(RUNNING);
            }
        });
        // verify that the "onStart" loop completed
        assertNotNull(AbstractRunningStrategy.getProperty("loopDone"));
    }
    /**
     * Tests a strategy with an arbitrarily long onStop.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void longRunningStop()
        throws Exception
    {
        assumeTrue(!(Platform.isWindows() && getLanguage().equals(JAVA)));
        final StrategyCoordinates strategy = getStrategyCompiles();
        final Properties parameters = new Properties();
        parameters.setProperty("shouldLoopOnStop",
                               "true");
        final ModuleURN strategyURN = createStrategy(strategy.getName(),
                                                     getLanguage(),
                                                     strategy.getFile(),
                                                     parameters,
                                                     null,
                                                     null);
        // being stop process
        assertNull(AbstractRunningStrategy.getProperty("loopDone"));
        moduleManager.stop(strategyURN);
        // wait until the strategy enters "STOPPING" status
        MarketDataFeedTestBase.wait(new Callable<Boolean>(){
            @Override
            public Boolean call()
                    throws Exception
            {
                return getStatus(strategyURN).equals(STOPPING);
            }
        });
        // take a little snooze - long enough that the strategy "onStop" will have completed if it was going to
        Thread.sleep(5000);
        // verify the status hasn't changed and that "onStop" hasn't completed
        assertNull(AbstractRunningStrategy.getProperty("loopDone"));
        verifyStrategyStatus(strategyURN,
                             STOPPING);
        // tell the loop to stop
        AbstractRunningStrategy.setProperty("shouldStopLoop",
                                            "true");
        // wait until the strategy has time to complete
        MarketDataFeedTestBase.wait(new Callable<Boolean>(){
            @Override
            public Boolean call()
                    throws Exception
            {
                return getStatus(strategyURN).equals(STOPPED);
            }
        });
        // verify that the "onStop" loop completed
        assertNotNull(AbstractRunningStrategy.getProperty("loopDone"));
    }
    /**
     * This test makes sure that data cannot be requested or sent after stop has begun.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void restrictedActionsDuringStop()
        throws Exception
    {
        assumeTrue(!(Platform.isWindows() && getLanguage().equals(JAVA)));
        StrategyCoordinates strategy = getStrategyCompiles();
        final Properties parameters = new Properties();
        // set up stop loop to request data *and* to delay stopping long enough for the request to be honored (if it were allowed)
        parameters.setProperty("shouldRequestDataOnStop",
                               "bogus");
        parameters.setProperty("symbols",
                               "METC");
        parameters.setProperty("shouldLoopOnStop",
                               "true");
        final ModuleURN strategyURN = createStrategy(strategy.getName(),
                                                     getLanguage(),
                                                     strategy.getFile(),
                                                     parameters,
                                                     null,
                                                     null);
        // strategy is in running state
        // nothing received
        verifyNullProperties();
        // no market data request made yet
        verifyPropertyNull("requestID");
        // the stop loop marker is not present
        verifyPropertyNull("loopDone");
        // trigger begin of stop loop
        moduleManager.stop(strategyURN);
        // wait until the strategy enters "STOPPING" status
        MarketDataFeedTestBase.wait(new Callable<Boolean>(){
            @Override
            public Boolean call()
                    throws Exception
            {
                return getStatus(strategyURN).equals(STOPPING);
            }
        });
        // strategy will wait in this status until we tell it to stop
        // wait long enough for some data to have leaked through
        Thread.sleep(5000);
        // tell the loop to complete its stop
        AbstractRunningStrategy.setProperty("shouldStopLoop",
                                            "true");
        // wait until strategy stops
        verifyStrategyStopped(strategyURN);
        // make sure the loop completed normally
        verifyPropertyNonNull("loopDone");
        // make sure that no data was received and that the request failed
        verifyNullProperties();
        assertEquals("0",
                     verifyPropertyNonNull("requestID"));
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
        assumeTrue(!(Platform.isWindows() && getLanguage().equals(JAVA)));
        for(int apiStringCounter=0;apiStringCounter<=0;apiStringCounter++) {
            getMarketData(BogusFeedModuleFactory.IDENTIFIER,
                          "GOOG,YHOO,MSFT,METC",
                          (apiStringCounter == 0),
                          new Properties());
            // strategy is now receiving data
            Thread.sleep(2500);
            // verify that bid/ask/trades have been received
            // TODO substitute a market data provider that provides a known script of events
            verifyPropertyNonNull("onAsk");
            verifyPropertyNonNull("onBid");
            // TODO almost certainly Bogus will provide a trade within 5 seconds, but it's nonetheless
            //  not deterministic.  the fix is to implement the deterministic provider described above
            verifyPropertyNonNull("onTrade");
            setPropertiesToNull();
        }
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
        assumeTrue(!(Platform.isWindows() && getLanguage().equals(JAVA)));
        for(int apiStringCounter=0;apiStringCounter<=0;apiStringCounter++) {
            verifyNullProperties();
            getMarketData("provider-does-not-exist",
                          "GOOG,YHOO,MSFT,METC",
                          (apiStringCounter == 0),
                          new Properties());
            // TODO same note as above: create a market data provider that deterministically produces data 
            Thread.sleep(2500);
            // the script does not fail, but no market data was provided
            verifyNullProperties();
        }
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
        assumeTrue(!(Platform.isWindows() && getLanguage().equals(JAVA)));
        for(int apiStringCounter=0;apiStringCounter<=0;apiStringCounter++) {
            // stop the bogus provider
            assertTrue(moduleManager.getModuleInfo(BogusFeedModuleFactory.INSTANCE_URN).getState().isStarted());
            moduleManager.stop(BogusFeedModuleFactory.INSTANCE_URN);
            assertFalse(moduleManager.getModuleInfo(BogusFeedModuleFactory.INSTANCE_URN).getState().isStarted());
            // request market data from the stopped provider
            getMarketData(BogusFeedModuleFactory.IDENTIFIER,
                          "GOOG,YHOO,MSFT,METC",
                          (apiStringCounter == 0),
                          new Properties());
            // TODO same note as above: create a market data provider that deterministically produces data 
            Thread.sleep(2500);
            // the script does not fail, but no market data was provided
            verifyNullProperties();
            // start the bogus module again
            moduleManager.start(BogusFeedModuleFactory.INSTANCE_URN);
            assertTrue(moduleManager.getModuleInfo(BogusFeedModuleFactory.INSTANCE_URN).getState().isStarted());
        }
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
        assumeTrue(!(Platform.isWindows() && getLanguage().equals(JAVA)));
        for(int apiStringCounter=0;apiStringCounter<=0;apiStringCounter++) {
            ModuleURN strategyURN = getMarketData(BogusFeedModuleFactory.IDENTIFIER,
                                                  "GOOG,YHOO,MSFT,METC",
                                                  (apiStringCounter == 0),
                                                  new Properties());
            // TODO same note as above: create a market data provider that deterministically produces data 
            Thread.sleep(5000);
            // market data request has produced some data, verify that now
            verifyPropertyNonNull("onAsk");
            verifyPropertyNonNull("onBid");
            // TODO almost certainly Bogus will provide a trade within 5 seconds, but it's nonetheless
            //  not deterministic.  the fix is to implement the deterministic provider described above
            verifyPropertyNonNull("onTrade");
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
            getRunningStrategy(strategyURN).getRunningStrategy().onCallback(this);
            // collect more market data, or, give it the chance to, anyway
            Thread.sleep(2500);
            setPropertiesToNull();
            Thread.sleep(2500);
            // make sure no more data was received
            verifyPropertyNull("onAsk");
            verifyPropertyNull("onBid");
        }
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
        assumeTrue(!(Platform.isWindows() && getLanguage().equals(JAVA)));
        final StrategyCoordinates strategy = getStrategyCompiles();
        // create a strategy that does not request market data
        ModuleURN strategyURN = createStrategy(strategy.getName(),
                                               getLanguage(),
                                               strategy.getFile(),
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
        getRunningStrategy(strategyURN).getRunningStrategy().onCallback(this);
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
        assumeTrue(!(Platform.isWindows() && getLanguage().equals(JAVA)));
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
        assumeTrue(!(Platform.isWindows() && getLanguage().equals(JAVA)));
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
        assumeTrue(!(Platform.isWindows() && getLanguage().equals(JAVA)));
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
        assumeTrue(!(Platform.isWindows() && getLanguage().equals(JAVA)));
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
        assumeTrue(!(Platform.isWindows() && getLanguage().equals(JAVA)));
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
        assumeTrue(!(Platform.isWindows() && getLanguage().equals(JAVA)));
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
        assumeTrue(!(Platform.isWindows() && getLanguage().equals(JAVA)));
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
        assumeTrue(!(Platform.isWindows() && getLanguage().equals(JAVA)));
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
        assumeTrue(!(Platform.isWindows() && getLanguage().equals(JAVA)));
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
        assumeTrue(!(Platform.isWindows() && getLanguage().equals(JAVA)));
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
                                               null); 
        doSuccessfulStartTest(strategyURN);
        stopStrategy(strategyURN);
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
        assumeTrue(!(Platform.isWindows() && getLanguage().equals(JAVA)));
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
        assumeTrue(!(Platform.isWindows() && getLanguage().equals(JAVA)));
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
        assumeTrue(!(Platform.isWindows() && getLanguage().equals(JAVA)));
        Properties parameters = new Properties();
        Date messageDate = new Date();
        parameters.setProperty("date",
                               Long.toString(messageDate.getTime()));
        // null message
        parameters.setProperty("nullMessage",
                               "true");
        doMessageTest(parameters,
                      new FIXOrder[0]);
        // null broker
        parameters.clear();
        parameters.setProperty("date",
                               Long.toString(messageDate.getTime()));
        parameters.setProperty("nullBroker",
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
                                                                         new BrokerID("some-broker")) } );
    }
    /**
     * Takes a single strategy and starts and stops it many times.
     *
     * @throws Exception
     */
    @PerformanceTest
    public void startStop()
        throws Exception
    {
        assumeTrue(!(Platform.isWindows() && getLanguage().equals(JAVA)));
        StrategyCoordinates strategy = getStrategyCompiles();
        ModuleURN strategyModule = moduleManager.createModule(StrategyModuleFactory.PROVIDER_URN,
                                                              "MyStrategy",
                                                              strategy.getName(),
                                                              getLanguage(),
                                                              strategy.getFile(),
                                                              null,
                                                              null,
                                                              null);
        int index = 0;
        while (index++ < 500) {
            startStrategy(strategyModule);
            stopStrategy(strategyModule);
        }
    }
    /**
     * Starts and stops many different strategies.
     *
     * @throws Exception
     */
    @PerformanceTest
    public void manyStrategiesStartStop()
        throws Exception
    {
        assumeTrue(!(Platform.isWindows() && getLanguage().equals(JAVA)));
        StrategyCoordinates strategy = getStrategyCompiles();
        int index = 0;
        while (index++ < 500) {
            ModuleURN strategyModule = moduleManager.createModule(StrategyModuleFactory.PROVIDER_URN,
                                                                  "MyStrategy",
                                                                  strategy.getName(),
                                                                  getLanguage(),
                                                                  strategy.getFile(),
                                                                  null,
                                                                  null,
                                                                  null);
            startStrategy(strategyModule);
            stopStrategy(strategyModule);
            moduleManager.deleteModule(strategyModule);
        }
    }
    /**
     * Starts and stops many different strategies.
     *
     * @throws Exception if an error occurs
     */
    @PerformanceTest
    public void manyStrategiesStartWithoutStop()
        throws Exception
    {
        assumeTrue(!(Platform.isWindows() && getLanguage().equals(JAVA)));
        StrategyCoordinates strategy = getStrategyCompiles();
        int index = 0;
        while (index++ < 500) {
            ModuleURN strategyModule = moduleManager.createModule(StrategyModuleFactory.PROVIDER_URN,
                                                                  null,
                                                                  strategy.getName(),
                                                                  getLanguage(),
                                                                  strategy.getFile(),
                                                                  null,
                                                                  null,
                                                                  null);
            moduleManager.start(strategyModule);
        }
    }
    /**
     * Tests the <code>MXBean</code> strategy interface.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void mxBeanOperations()
        throws Exception
    {
        assumeTrue(!(Platform.isWindows() && getLanguage().equals(JAVA)));
        StrategyCoordinates strategy = getParameterStrategy(); 
        // create a strategy with no parameters
        ModuleURN strategyURN = createStrategy(strategy.getName(),
                                               getLanguage(),
                                               strategy.getFile(),
                                               null,
                                               null,
                                               null);
        // make sure the starting state is what we think it is
        verifyNullProperties();
        final MockRecorderModule outputRecorder = MockRecorderModule.Factory.recorders.get(outputURN);
        assertNotNull("Must be able to find the recorder created",
                      outputRecorder);
        assertTrue(outputRecorder.getDataReceived().isEmpty());
        // fire events at the strategy
        doSuccessfulStartTestNoVerification(strategyURN);
        // nothing got through because the triggering parameters are not there
        verifyNullProperties();
        assertTrue(outputRecorder.getDataReceived().isEmpty());
        // set new parameters that will cause onAsk to be received
        StrategyMXBean strategyProxy = getMXProxy(strategyURN);
        strategyProxy.setParameters("onAsk=true:emitSuggestion=true:emitMessage=true");
        doSuccessfulStartTestNoVerification(strategyURN);
        // nothing got through because the module was not restarted
        verifyNullProperties();
        assertTrue(outputRecorder.getDataReceived().isEmpty());
        // now cycle the strategy
        stopStrategy(strategyURN);
        startStrategy(strategyURN);
        doSuccessfulStartTestNoVerification(strategyURN);
        // onAsk got through, but there are still no destinations for the orders and suggestions
        verifyPropertyNonNull("onAsk");
        assertTrue(outputRecorder.getDataReceived().isEmpty());
        // reset
        setPropertiesToNull();
        // now set the output destination
        strategyProxy.setOutputDestination(outputURN.getValue());
        // fire the events
        doSuccessfulStartTestNoVerification(strategyURN);
        // onAsk still goes through, but the others won't until the strategy is cycled
        verifyPropertyNonNull("onAsk");
        assertTrue(outputRecorder.getDataReceived().isEmpty());
        // reset
        setPropertiesToNull();
        // cycle the strategy again
        stopStrategy(strategyURN);
        startStrategy(strategyURN);
        // fire the events again
        doSuccessfulStartTestNoVerification(strategyURN);
        // onAsk set again
        verifyPropertyNonNull("onAsk");
        // suggestion and order now gets through
        assertEquals(2,
                     outputRecorder.getDataReceived().size());
        // now make them all go away
        strategyProxy.setParameters(null);
        strategyProxy.setOutputDestination(null);
        // reset
        setPropertiesToNull();
        outputRecorder.resetDataReceived();
        // cycle
        stopStrategy(strategyURN);
        startStrategy(strategyURN);
        // fire
        doSuccessfulStartTestNoVerification(strategyURN);
        // verify
        verifyNullProperties();
        assertTrue(outputRecorder.getDataReceived().isEmpty());
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
        assumeTrue(!(Platform.isWindows() && getLanguage().equals(JAVA)));
        ModuleURN alternateURN = createModule(MockRecorderModule.Factory.PROVIDER_URN);
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
                       outputURN);
        createStrategy(strategy.getName(),
                       getLanguage(),
                       strategy.getFile(),
                       parameters,
                       null,
                       alternateURN);
        // strategies have now emitted their suggestions, measure the results
        final MockRecorderModule strategy1Recorder = MockRecorderModule.Factory.recorders.get(outputURN);
        final MockRecorderModule strategy2Recorder = MockRecorderModule.Factory.recorders.get(alternateURN);
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
        assumeTrue(!(Platform.isWindows() && getLanguage().equals(JAVA)));
        StrategyCoordinates strategy1 = getPart1Strategy();
        doSuccessfulStartTestNoVerification(createStrategy(strategy1.getName(),
                                                           getLanguage(),
                                                           strategy1.getFile(),
                                                           null,
                                                           null,
                                                           null));
        StrategyCoordinates strategy2 = getPart2Strategy();
        doSuccessfulStartTestNoVerification(createStrategy(strategy2.getName(),
                                                           getLanguage(),
                                                           strategy2.getFile(),
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
        assumeTrue(!(Platform.isWindows() && getLanguage().equals(JAVA)));
        Level startingLevel = Logger.getLogger(Strategy.STRATEGY_MESSAGES).getLevel();
        try {
            Logger.getLogger(Strategy.STRATEGY_MESSAGES).setLevel(Level.ALL);
            MockRecorderModule.shouldIgnoreLogMessages = false;
            // set up module to receive notifications
            final MockRecorderModule notificationSubscriber = MockRecorderModule.Factory.recorders.get(outputURN);
            assertTrue(notificationSubscriber.getDataReceived().isEmpty());
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
                                                 outputURN));
            StrategyImpl runningStrategy = getRunningStrategy(theStrategy);
            MarketDataFeedTestBase.wait(new Callable<Boolean>() {
                @Override
                public Boolean call()
                        throws Exception
                {
                    return notificationSubscriber.getDataReceived().size() == 19;
                }
            });
            assertEquals("low subject",
                         ((Notification)(notificationSubscriber.getDataReceived().get(0).getData())).getSubject());
            assertEquals("medium subject",
                         ((Notification)(notificationSubscriber.getDataReceived().get(1).getData())).getSubject());
            assertEquals("high subject",
                         ((Notification)(notificationSubscriber.getDataReceived().get(2).getData())).getSubject());
            LogEventTest.verifyEvent((LogEvent)notificationSubscriber.getDataReceived().get(3).getData(),
                                     WARN,
                                     null,
                                     INVALID_LOG,
                                     String.valueOf(runningStrategy));
            LogEventTest.verifyEvent((LogEvent)notificationSubscriber.getDataReceived().get(4).getData(),
                                     DEBUG,
                                     null,
                                     MESSAGE_1P,
                                     "");
            LogEventTest.verifyEvent((LogEvent)notificationSubscriber.getDataReceived().get(5).getData(),
                                     DEBUG,
                                     null,
                                     MESSAGE_1P,
                                     "Some statement");
            LogEventTest.verifyEvent((LogEvent)notificationSubscriber.getDataReceived().get(6).getData(),
                                     DEBUG,
                                     null,
                                     MESSAGE_1P,
                                     UnicodeData.HOUSE_AR);
            LogEventTest.verifyEvent((LogEvent)notificationSubscriber.getDataReceived().get(7).getData(),
                                     WARN,
                                     null,
                                     INVALID_LOG,
                                     String.valueOf(runningStrategy));
            LogEventTest.verifyEvent((LogEvent)notificationSubscriber.getDataReceived().get(8).getData(),
                                     INFO,
                                     null,
                                     MESSAGE_1P,
                                     "");
            LogEventTest.verifyEvent((LogEvent)notificationSubscriber.getDataReceived().get(9).getData(),
                                     INFO,
                                     null,
                                     MESSAGE_1P,
                                     "Some statement");
            LogEventTest.verifyEvent((LogEvent)notificationSubscriber.getDataReceived().get(10).getData(),
                                     INFO,
                                     null,
                                     MESSAGE_1P,
                                     UnicodeData.HOUSE_AR);
            LogEventTest.verifyEvent((LogEvent)notificationSubscriber.getDataReceived().get(11).getData(),
                                     WARN,
                                     null,
                                     INVALID_LOG,
                                     String.valueOf(runningStrategy));
            LogEventTest.verifyEvent((LogEvent)notificationSubscriber.getDataReceived().get(12).getData(),
                                     WARN,
                                     null,
                                     MESSAGE_1P,
                                     "");
            LogEventTest.verifyEvent((LogEvent)notificationSubscriber.getDataReceived().get(13).getData(),
                                     WARN,
                                     null,
                                     MESSAGE_1P,
                                     "Some statement");
            LogEventTest.verifyEvent((LogEvent)notificationSubscriber.getDataReceived().get(14).getData(),
                                     WARN,
                                     null,
                                     MESSAGE_1P,
                                     UnicodeData.HOUSE_AR);
            LogEventTest.verifyEvent((LogEvent)notificationSubscriber.getDataReceived().get(15).getData(),
                                     WARN,
                                     null,
                                     INVALID_LOG,
                                     String.valueOf(runningStrategy));
            LogEventTest.verifyEvent((LogEvent)notificationSubscriber.getDataReceived().get(16).getData(),
                                     ERROR,
                                     null,
                                     MESSAGE_1P,
                                     "");
            LogEventTest.verifyEvent((LogEvent)notificationSubscriber.getDataReceived().get(17).getData(),
                                     ERROR,
                                     null,
                                     MESSAGE_1P,
                                     "Some statement");
            LogEventTest.verifyEvent((LogEvent)notificationSubscriber.getDataReceived().get(18).getData(),
                                     ERROR,
                                     null,
                                     MESSAGE_1P,
                                     UnicodeData.HOUSE_AR);
        } finally {
            Logger.getLogger(Strategy.STRATEGY_MESSAGES).setLevel(startingLevel);
            MockRecorderModule.shouldIgnoreLogMessages = true;
        }
    }
    /**
     * Tests a strategy's ability to retrieve available brokers.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void brokers()
        throws Exception
    {
        assumeTrue(!(Platform.isWindows() && getLanguage().equals(JAVA)));
        // call should fail
        MockClient.getBrokersFails = true;
        doBrokerTest(new BrokerStatus[0]);
        // succeeds and returns a non-empty list
        MockClient.getBrokersFails = false;
        doBrokerTest(brokers.getBrokers().toArray(new BrokerStatus[brokers.getBrokers().size()]));
        // succeeds and returns an empty list
        brokers=new BrokersStatus(new ArrayList<BrokerStatus>());
        doBrokerTest(new BrokerStatus[0]);
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
        assumeTrue(!(Platform.isWindows() && getLanguage().equals(JAVA)));
        List<OrderSingle> cumulativeOrders = new ArrayList<OrderSingle>();
        ModuleURN strategy = generateOrders(getOrdersStrategy(),
                                            outputURN);
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
        AbstractRunningStrategy.setProperty("orderType",
                                            OrderType.Market.name());
        AbstractRunningStrategy.setProperty("quantity",
                                            "10000");
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
        expectedOrder2.setOrderType(OrderType.Market);
        expectedOrder2.setQuantity(new BigDecimal("10000"));
        cumulativeOrders.add(expectedOrder2);
        doOrderTest(strategy,
                    new OrderSingle[] { expectedOrder2 },
                    cumulativeOrders);
        // three time's a charm
        OrderSingle expectedOrder3 = Factory.getInstance().createOrderSingle();
        AbstractRunningStrategy.getProperties().clear();
        AbstractRunningStrategy.setProperty("orderType",
                                            OrderType.Market.name());
        AbstractRunningStrategy.setProperty("quantity",
                                            "10000");
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
        expectedOrder3.setOrderType(OrderType.Market);
        expectedOrder3.setQuantity(new BigDecimal("10000"));
        cumulativeOrders.add(expectedOrder3);
        doOrderTest(strategy,
                    new OrderSingle[] { expectedOrder3 },
                    cumulativeOrders);
        // cycle the strategy, proving that cumulative orders gets reset
        // this will prevent the strategy from sending an order when we next execute the test
        AbstractRunningStrategy.setProperty("orderShouldBeNull",
                                            "true");
        // restart and make sure no orders are in this session
        stopStrategy(strategy);
        startStrategy(strategy);
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
        assumeTrue(!(Platform.isWindows() && getLanguage().equals(JAVA)));
        // create a strategy that sends its orders to a known module that can also emit execution reports
        generateOrders(getOrdersStrategy(),
                       outputURN);
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
        assumeTrue(!(Platform.isWindows() && getLanguage().equals(JAVA)));
        // cancel 0 orders
        ModuleURN strategy = generateOrders(getOrdersStrategy(),
                                            outputURN);
        AbstractRunningStrategy runningStrategy = (AbstractRunningStrategy)getRunningStrategy(strategy).getRunningStrategy();
        AbstractRunningStrategy.setProperty("cancelAll",
                                            "true");
        // trigger cancel
        runningStrategy.onTrade(tradeEvent);
        assertEquals("0",
                     AbstractRunningStrategy.getProperty("ordersCanceled"));
        AbstractRunningStrategy.setProperty("price",
                                            "1000");
        AbstractRunningStrategy.setProperty("quantity",
                                            "500");
        AbstractRunningStrategy.setProperty("side",
                                            Side.Sell.toString());
        AbstractRunningStrategy.setProperty("symbol",
                                            "METC");
        AbstractRunningStrategy.setProperty("orderType",
                                            OrderType.Market.name());
        AbstractRunningStrategy.setProperty("quantity",
                                            "10000");
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
        stopStrategy(strategy);
        startStrategy(strategy);
        // trigger a cancel (should do nothing)
        runningStrategy = (AbstractRunningStrategy)getRunningStrategy(strategy).getRunningStrategy();
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
        assumeTrue(!(Platform.isWindows() && getLanguage().equals(JAVA)));
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
        // add type
        AbstractRunningStrategy.setProperty("orderType",
                                            OrderType.Market.name());
        // try to cancel an order with a null orderID
        ModuleURN strategy = generateOrders(getOrdersStrategy(),
                                            outputURN);
        AbstractRunningStrategy runningStrategy = (AbstractRunningStrategy)getRunningStrategy(strategy).getRunningStrategy();
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
        stopStrategy(strategy);
        startStrategy(strategy);
        assertEquals(1,
                     StrategyImpl.getRunningStrategies().size());
        AbstractRunningStrategy.setProperty("orderCanceled",
                                            "");
        runningStrategy = (AbstractRunningStrategy)getRunningStrategy(strategy).getRunningStrategy();
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
        assumeTrue(!(Platform.isWindows() && getLanguage().equals(JAVA)));
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
        // add type
        AbstractRunningStrategy.setProperty("orderType",
                                            OrderType.Market.name());
        // create a strategy to use as our test vehicle
        ModuleURN strategy = generateOrders(getOrdersStrategy(),
                                            outputURN);
        AbstractRunningStrategy runningStrategy = (AbstractRunningStrategy)getRunningStrategy(strategy).getRunningStrategy();
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
        stopStrategy(strategy);
        startStrategy(strategy);
        runningStrategy = (AbstractRunningStrategy)getRunningStrategy(strategy).getRunningStrategy();
        AbstractRunningStrategy.setProperty("newOrderID",
                                            null);
        // try to cancel/replace again, won't work because the order to be replaced was in the last strategy session (before the stop)
        runningStrategy.onOther(newOrder);
        assertNull(AbstractRunningStrategy.getProperty("newOrderID"));
    }
    /**
     * Tests a strategy's ability to get the position of a security. 
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void positions()
        throws Exception
    {
        assumeTrue(!(Platform.isWindows() && getLanguage().equals(JAVA)));
        String validSymbol = positions.keySet().iterator().next().toString();
        Position position = positions.get(new MSymbol(validSymbol));
        String invalidSymbol = "there-is-no-position-for-this-symbol-" + System.nanoTime();
        assertFalse(positions.containsKey(new MSymbol(invalidSymbol)));
        // null symbol
        doPositionTest(null,
                       new Date(),
                       null);
        // empty symbol
        doPositionTest("",
                       new Date(),
                       null);
        // invalid symbol
        doPositionTest(invalidSymbol,
                       new Date(),
                       null);
        // null date
        doPositionTest(validSymbol,
                       null,
                       null);
        // call fails
        MockClient.getPositionFails = true;
        doPositionTest(validSymbol,
                       new Date(),
                       null);
        MockClient.getPositionFails = false;
        // date in the past (before position begins)
        Interval<BigDecimal> openingBalance = position.getPositionView().get(0);
        doPositionTest(validSymbol,
                       new Date(openingBalance.getDate().getTime() - 1000), // 1s before the open of the position
                       BigDecimal.ZERO);
        // date in the past (after position begins)
        List<Interval<BigDecimal>> view = position.getPositionView(); 
        int median = view.size() / 2;
        assertTrue("Position " + position + " contains no data!",
                   median > 0);
        Interval<BigDecimal> dataPoint = position.getPositionView().get(median);
        Date date = dataPoint.getDate();
        BigDecimal expectedValue = position.getPositionAt(date);
        assertEquals("value at " + date + ": " + position,
                     dataPoint.getValue(),
                     expectedValue);
        assertTrue(date.getTime() < System.currentTimeMillis());
        // found a date somewhere in the middle of the position and earlier than today
        doPositionTest(validSymbol,
                       date,
                       expectedValue);
        // date exactly now
        date = new Date();
        expectedValue = position.getPositionAt(date);
        dataPoint = view.get(view.size() - 1);
        assertEquals("value at " + date + ": " + position,
                     dataPoint.getValue(),
                     expectedValue);
        doPositionTest(validSymbol,
                       date,
                       expectedValue);
        // pick a data point two weeks into the future
        date = new Date(System.currentTimeMillis() + (1000 * 60 * 60 * 24 * 14));
        expectedValue = position.getPositionAt(date);
        dataPoint = view.get(view.size() - 1);
        assertEquals("value at " + date + ": " + position,
                     dataPoint.getValue(),
                     expectedValue);
        doPositionTest(validSymbol,
                       date,
                       expectedValue);
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
        assumeTrue(!(Platform.isWindows() && getLanguage().equals(JAVA)));
        StrategyCoordinates strategy1 = getPart1Strategy();
        ModuleURN strategy1URN = createStrategy(strategy1.getName(),
                                                getLanguage(),
                                                strategy1.getFile(),
                                                null,
                                                null,
                                                null);
        ModuleURN strategy2URN = createStrategy(strategy1.getName(),
                                                getLanguage(),
                                                strategy1.getFile(),
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
        assumeTrue(!(Platform.isWindows() && getLanguage().equals(JAVA)));
        StrategyCoordinates strategy1 = getPart1Strategy();
        StrategyCoordinates strategy2 = getPart1RedefinedStrategy();
        ModuleURN strategy1URN = createStrategy(strategy1.getName(),
                                                getLanguage(),
                                                strategy1.getFile(),
                                                null,
                                                null,
                                                null);
        ModuleURN strategy2URN = createStrategy(strategy2.getName(),
                                                getLanguage(),
                                                strategy2.getFile(),
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
    /**
     * Tests the stategy's ability to execute CEP queries.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void cep()
        throws Exception
    {
        assumeTrue(!(Platform.isWindows() && getLanguage().equals(JAVA)));
        String validEsperStatement1 = "select * from trade where symbolAsString='METC'";
        String validEsperStatement2 = "select * from ask where symbolAsString='ORCL'";
        String validSystemStatement1 = "select * from trade";
        String validSystemStatement2 = "select * from ask";
        String invalidStatement = "this statement is not syntactically valid";
        EventBase[] events = new EventBase[] { new TradeEvent(System.nanoTime(), System.currentTimeMillis(), new MSymbol("METC"), "Q", new BigDecimal("1"), new BigDecimal("100")),
                                               new TradeEvent(System.nanoTime(), System.currentTimeMillis(), new MSymbol("ORCL"), "Q", new BigDecimal("2"), new BigDecimal("200")),
                                               new AskEvent(System.nanoTime(), System.currentTimeMillis(), new MSymbol("METC"), "Q", new BigDecimal("3"), new BigDecimal("300")),
                                               new AskEvent(System.nanoTime(), System.currentTimeMillis(), new MSymbol("ORCL"), "Q", new BigDecimal("4"), new BigDecimal("400")),
                                               new BidEvent(System.nanoTime(), System.currentTimeMillis(), new MSymbol("METC"), "Q", new BigDecimal("5"), new BigDecimal("500")),
                                               new BidEvent(System.nanoTime(), System.currentTimeMillis(), new MSymbol("ORCL"), "Q", new BigDecimal("6"), new BigDecimal("600")) };
        String[] sources = new String[] { null, "esper", "system" };
        String[][] statements = new String[][] { { null }, { }, { invalidStatement },
                                                 { validSystemStatement1, validSystemStatement2 }, { validSystemStatement1 },
                                                 { validEsperStatement1 }, { validEsperStatement1, validEsperStatement2 } };
        for(int sourceCounter=0;sourceCounter<sources.length;sourceCounter++) {
            for(int statementCounter=0;statementCounter<statements.length;statementCounter++) {
                SLF4JLoggerProxy.debug(LanguageTestBase.class,
                                       "{}:{}",
                                       sourceCounter,
                                       statementCounter);
                List<OrderSingleSuggestion> suggestions = doCEPTest(sources[sourceCounter],
                                                                    statements[statementCounter],
                                                                    events,
                                                                    true);
                // verify results
                switch(sourceCounter) {
                    case 0 :
                        assertTrue(suggestions.isEmpty());
                        continue;
                    case 1 :
                        // esper conditions
                        switch(statementCounter) {
                            case 0 :
                                // null statements
                                assertTrue(suggestions.isEmpty());
                                continue;
                            case 1 :
                                // empty statements
                                assertTrue(suggestions.isEmpty());
                                continue;
                            case 2 :
                                // invalid statements
                                assertTrue(suggestions.isEmpty());
                                continue;
                            case 3 :
                                // 2 valid system statements (also valid esper statements)
                                // note that the last statement is the only one that will return data
                                assertEquals(2,
                                             suggestions.size());
                                verifyCEPSuggestion("METC",
                                                    new BigDecimal("3"),
                                                    new BigDecimal("300"),
                                                    suggestions.get(0));
                                verifyCEPSuggestion("ORCL",
                                                    new BigDecimal("4"),
                                                    new BigDecimal("400"),
                                                    suggestions.get(1));
                                continue;
                            case 4 :
                                // 1 valid system statement (also valid esper statement)
                                assertEquals(2,
                                             suggestions.size());
                                verifyCEPSuggestion("METC",
                                                    new BigDecimal("1"),
                                                    new BigDecimal("100"),
                                                    suggestions.get(0));
                                verifyCEPSuggestion("ORCL",
                                                    new BigDecimal("2"),
                                                    new BigDecimal("200"),
                                                    suggestions.get(1));
                                continue;
                            case 5 :
                                // 1 valid esper statement
                                assertEquals(1,
                                             suggestions.size());
                                verifyCEPSuggestion("METC",
                                                    new BigDecimal("1"),
                                                    new BigDecimal("100"),
                                                    suggestions.get(0));
                                continue;
                            case 6 :
                                // 2 valid esper statements
                                assertEquals(1,
                                             suggestions.size());
                                verifyCEPSuggestion("ORCL",
                                                    new BigDecimal("4"),
                                                    new BigDecimal("400"),
                                                    suggestions.get(0));
                                continue;
                            default :
                                fail("Unexpected statement");
                        }
                        continue;
                    case 2 :
                        // system conditions
                        switch(statementCounter) {
                            case 0 :
                                // null statements
                                assertTrue(suggestions.isEmpty());
                                continue;
                            case 1 :
                                // empty statements
                                assertTrue(suggestions.isEmpty());
                                continue;
                            case 2 :
                                // invalid statements
                                assertTrue(suggestions.isEmpty());
                                continue;
                            case 3 :
                                // 2 valid system statements
                                // note that the first statement is the only one that will return data
                                assertEquals(2,
                                             suggestions.size());
                                verifyCEPSuggestion("METC",
                                                    new BigDecimal("1"),
                                                    new BigDecimal("100"),
                                                    suggestions.get(0));
                                verifyCEPSuggestion("ORCL",
                                                    new BigDecimal("2"),
                                                    new BigDecimal("200"),
                                                    suggestions.get(1));
                                continue;
                            case 4 :
                                // 1 valid system statement
                                assertEquals(2,
                                             suggestions.size());
                                verifyCEPSuggestion("METC",
                                                    new BigDecimal("1"),
                                                    new BigDecimal("100"),
                                                    suggestions.get(0));
                                verifyCEPSuggestion("ORCL",
                                                    new BigDecimal("2"),
                                                    new BigDecimal("200"),
                                                    suggestions.get(1));
                                continue;
                            case 5 :
                                // 1 valid esper statement (not valid for system)
                                assertTrue(suggestions.isEmpty());
                                continue;
                            case 6 :
                                // 2 valid esper statements (not valid for system)
                                assertTrue(suggestions.isEmpty());
                                continue;
                            default :
                                fail("Unexpected statement");
                        }
                    default :
                        fail("Unexpected source");
                }
            }
        }
    }
    /**
     * Tests the ability to cancel a single CEP request.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void cancelSingleCep()
        throws Exception
    {
        assumeTrue(!(Platform.isWindows() && getLanguage().equals(JAVA)));
        EventBase[] events = new EventBase[] { new TradeEvent(System.nanoTime(), System.currentTimeMillis(), new MSymbol("METC"), "Q", new BigDecimal("1"), new BigDecimal("100")),
                                               new TradeEvent(System.nanoTime(), System.currentTimeMillis(), new MSymbol("ORCL"), "Q", new BigDecimal("2"), new BigDecimal("200")),
                                               new AskEvent(System.nanoTime(), System.currentTimeMillis(), new MSymbol("METC"), "Q", new BigDecimal("3"), new BigDecimal("300")),
                                               new AskEvent(System.nanoTime(), System.currentTimeMillis(), new MSymbol("ORCL"), "Q", new BigDecimal("4"), new BigDecimal("400")),
                                               new BidEvent(System.nanoTime(), System.currentTimeMillis(), new MSymbol("METC"), "Q", new BigDecimal("5"), new BigDecimal("500")),
                                               new BidEvent(System.nanoTime(), System.currentTimeMillis(), new MSymbol("ORCL"), "Q", new BigDecimal("6"), new BigDecimal("600")) };
        assertNull(AbstractRunningStrategy.getProperty("requestID"));
        // create a strategy that creates some suggestions
        List<OrderSingleSuggestion> suggestions = doCEPTest("esper",
                                                            new String[] { "select * from trade" },
                                                            events,
                                                            false);
        assertEquals(2,
                     suggestions.size());
        String requestIDString = AbstractRunningStrategy.getProperty("requestID");
        assertNotNull(requestIDString);
        // issue a cancel request for a non-existent id
        int badID = 10500;
        assertFalse(Integer.parseInt(requestIDString) == badID);
        AbstractRunningStrategy.setProperty("shouldCancelCEPData",
                                            "true");
        AbstractRunningStrategy.setProperty("requestID",
                                            Integer.toString(badID));
        // the strategy is still running and it has an active CEP query
        // if we send an "onOther" to the strategy, it will trigger a cancel request
        StrategyImpl strategy = getRunningStrategy(theStrategy);
        strategy.getRunningStrategy().onOther(this);
        ModuleURN cepModuleURN = new ModuleURN("metc:cep:esper:" + strategy.getDefaultNamespace());
        // verify that the cancel did not affect the existing query by triggering another set of suggestions via the CEP query
        assertEquals(0,
                     getReceivedSuggestions(outputURN).size());
        feedEventsToCEP(events,
                        cepModuleURN);
        assertEquals(2,
                     getReceivedSuggestions(outputURN).size());
        // cancel the actual query and verify no events are received from CEP
        AbstractRunningStrategy.setProperty("requestID",
                                            requestIDString);
        assertEquals(0,
                     getReceivedSuggestions(outputURN).size());
        // triggers the cancel
        strategy.getRunningStrategy().onOther(this);
        // triggers the events to CEP
        feedEventsToCEP(events,
                        cepModuleURN);
        // measure the result
        assertEquals(0,
                     getReceivedSuggestions(outputURN).size());
    }
    /**
     * Cancels all active cep requests.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void cancelAllCep()
        throws Exception
    {
        assumeTrue(!(Platform.isWindows() && getLanguage().equals(JAVA)));
        EventBase[] events = new EventBase[] { new TradeEvent(System.nanoTime(), System.currentTimeMillis(), new MSymbol("METC"), "Q", new BigDecimal("1"), new BigDecimal("100")),
                                               new TradeEvent(System.nanoTime(), System.currentTimeMillis(), new MSymbol("ORCL"), "Q", new BigDecimal("2"), new BigDecimal("200")),
                                               new AskEvent(System.nanoTime(), System.currentTimeMillis(), new MSymbol("METC"), "Q", new BigDecimal("3"), new BigDecimal("300")),
                                               new AskEvent(System.nanoTime(), System.currentTimeMillis(), new MSymbol("ORCL"), "Q", new BigDecimal("4"), new BigDecimal("400")),
                                               new BidEvent(System.nanoTime(), System.currentTimeMillis(), new MSymbol("METC"), "Q", new BigDecimal("5"), new BigDecimal("500")),
                                               new BidEvent(System.nanoTime(), System.currentTimeMillis(), new MSymbol("ORCL"), "Q", new BigDecimal("6"), new BigDecimal("600")) };
        assertEquals(2,
                     doCEPTest("esper",
                               new String[] { "select * from trade" },
                               events,
                               false).size());
        StrategyImpl strategy = getRunningStrategy(theStrategy);
        strategy.getRunningStrategy().onCallback(this);
        ModuleURN cepModuleURN = new ModuleURN("metc:cep:esper:" + strategy.getDefaultNamespace());
        feedEventsToCEP(events,
                        cepModuleURN);
        assertEquals(0,
                     getReceivedSuggestions(outputURN).size());
        // cancel again to make sure nothing breaks
        strategy.getRunningStrategy().onCallback(this);
        feedEventsToCEP(events,
                        cepModuleURN);
        assertEquals(0,
                     getReceivedSuggestions(outputURN).size());
    }
    /**
     * Tests a strategy's ability to send events to a targeted CEP module
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void sendEventToCEP()
        throws Exception
    {
        assumeTrue(!(Platform.isWindows() && getLanguage().equals(JAVA)));
        // start the event strategy
        StrategyCoordinates strategyFile = getEventStrategy();
        theStrategy = createStrategy(strategyFile.getName(),
                                     getLanguage(),
                                     strategyFile.getFile(),
                                     null,
                                     null,
                                     null);
        AbstractRunningStrategy.setProperty("source",
                                            "esper");
        // get a handle to that strategy
        StrategyImpl strategy = getRunningStrategy(theStrategy);
        // begin testing
        assertNull(AbstractRunningStrategy.getProperty("ask"));
        assertNull(AbstractRunningStrategy.getProperty("askCount"));
        // send an event to a null source
        AbstractRunningStrategy.setProperty("nilSource",
                                            "true");
        // start the reaction
        strategy.getRunningStrategy().onOther(askEvent);
        // make sure nothing got sent out
        assertNull(AbstractRunningStrategy.getProperty("ask"));
        assertNull(AbstractRunningStrategy.getProperty("askCount"));
        // next test - null event
        AbstractRunningStrategy.setProperty("nilSource",
                                            null);
        AbstractRunningStrategy.setProperty("nilEvent",
                                            "true");
        assertNull(AbstractRunningStrategy.getProperty("ask"));
        assertNull(AbstractRunningStrategy.getProperty("askCount"));
        // start the reaction
        strategy.getRunningStrategy().onOther(askEvent);
        // make sure nothing got sent out
        assertNull(AbstractRunningStrategy.getProperty("ask"));
        assertNull(AbstractRunningStrategy.getProperty("askCount"));
        // next test - empty provider
        AbstractRunningStrategy.setProperty("nilEvent",
                                            null);
        AbstractRunningStrategy.setProperty("source",
                                            "");
        // start the reaction
        strategy.getRunningStrategy().onOther(askEvent);
        // make sure nothing got sent out
        assertNull(AbstractRunningStrategy.getProperty("ask"));
        assertNull(AbstractRunningStrategy.getProperty("askCount"));
        // next - invalid provider
        AbstractRunningStrategy.setProperty("source",
                                            "this-cep-provider-does-not-exist");
        // start the reaction
        strategy.getRunningStrategy().onOther(askEvent);
        // make sure nothing got sent out
        assertNull(AbstractRunningStrategy.getProperty("ask"));
        assertNull(AbstractRunningStrategy.getProperty("askCount"));
        // next - valid but unstarted provider (since no query is established, the event gets sent but is never received back by the strategy)
        AbstractRunningStrategy.setProperty("source",
                                            "esper");
        // start the reaction
        strategy.getRunningStrategy().onOther(askEvent);
        // make sure nothing got sent out
        assertNull(AbstractRunningStrategy.getProperty("ask"));
        assertNull(AbstractRunningStrategy.getProperty("askCount"));
        // next, create a query that listens for ask events
        AbstractRunningStrategy.setProperty("shouldRequestCEPData",
                                            "true");
        String[] statements = new String[] { "select * from ask" };
        AbstractRunningStrategy.setProperty("statements",
                                            createConsolidatedCEPStatement(statements));
        // start the query
        strategy.getRunningStrategy().onCallback(this);
        // start the reaction, this will cause the event to be echoed back to the strategy
        strategy.getRunningStrategy().onOther(askEvent);
        assertEquals("1",
                     AbstractRunningStrategy.getProperty("askCount"));
        assertEquals(askEvent.toString(),
                     AbstractRunningStrategy.getProperty("ask"));
    }
    /**
     * Tests the strategy's ability to send an event to event subscribers.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void sendEvent()
        throws Exception
    {
        assumeTrue(!(Platform.isWindows() && getLanguage().equals(JAVA)));
        // these will be the subscribers to events
        ModuleURN alternateURN = createModule(MockRecorderModule.Factory.PROVIDER_URN);
        MockRecorderModule eventSubscriber = MockRecorderModule.Factory.recorders.get(outputURN);
        MockRecorderModule allSubscriber = MockRecorderModule.Factory.recorders.get(alternateURN);
        // start the event strategy
        StrategyCoordinates strategyFile = getEventStrategy();
        theStrategy = createStrategy(strategyFile.getName(),
                                     getLanguage(),
                                     strategyFile.getFile(),
                                     null,
                                     null,
                                     null);
        // get a handle to the running strategy
        StrategyImpl strategy = getRunningStrategy(theStrategy);
        // begin testing
        assertTrue(eventSubscriber.getDataReceived().isEmpty());
        assertTrue(allSubscriber.getDataReceived().isEmpty());
        AbstractRunningStrategy.setProperty("eventOnlyTest",
                                            "true");
        // send a null event
        AbstractRunningStrategy.setProperty("nilEvent",
                                            "true");
        // start reaction
        strategy.getRunningStrategy().onOther(askEvent);
        // make sure nothing got sent
        assertTrue(eventSubscriber.getDataReceived().isEmpty());
        assertTrue(allSubscriber.getDataReceived().isEmpty());
        // real event (no subscribers yet, though)
        AbstractRunningStrategy.setProperty("nilEvent",
                                            null);
        // start reaction
        strategy.getRunningStrategy().onOther(askEvent);
        // event got through, but didn't go anywhere
        assertTrue(eventSubscriber.getDataReceived().isEmpty());
        assertTrue(allSubscriber.getDataReceived().isEmpty());
        // set up a subscriber for the events channel and the all channel
        DataFlowID eventSubscription = moduleManager.createDataFlow(new DataRequest[] { new DataRequest(theStrategy,
                                                                                                        OutputType.EVENTS),
                                                                                        new DataRequest(outputURN) },
                                                                    false);
        DataFlowID allSubscription = moduleManager.createDataFlow(new DataRequest[] { new DataRequest(theStrategy,
                                                                                                      OutputType.ALL),
                                                                                      new DataRequest(alternateURN) },
                                                                  false);
        // start again
        strategy.getRunningStrategy().onOther(askEvent);
        // check results
        assertEquals(1,
                     eventSubscriber.getDataReceived().size());
        assertEquals(1,
                     allSubscriber.getDataReceived().size());
        assertEquals(askEvent,
                     eventSubscriber.getDataReceived().get(0).getData());
        assertEquals(askEvent,
                     allSubscriber.getDataReceived().get(0).getData());
        // cleanup
        moduleManager.cancel(eventSubscription);
        moduleManager.cancel(allSubscription);
    }
    /**
     * Tests a strategy's ability to create processed market data requests.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void processedMarketDataRequests()
        throws Exception
    {
        assumeTrue(!(Platform.isWindows() && getLanguage().equals(JAVA)));
        StrategyCoordinates strategy = getCombinedStrategy();
        for(int apiCounter=0;apiCounter<=1;apiCounter++) {
            boolean useStringAPI = (apiCounter == 1);
            AbstractRunningStrategy.getProperties().clear();
            if(useStringAPI) {
                AbstractRunningStrategy.setProperty("useStringAPI",
                                                    "true");
            }
            theStrategy = createStrategy(strategy.getName(),
                                         getLanguage(),
                                         strategy.getFile(),
                                         null,
                                         null,
                                         null);
            // these are the nominal test values
            String symbols = "METC,ORCL,GOOG,YHOO";
            String marketDataSource = BogusFeedModuleFactory.IDENTIFIER;
            String compressedStatements = createConsolidatedCEPStatement(new String[] { "select * from ask where symbolAsString='METC'" });
            String cepSource = "esper";
            // set the default values
            AbstractRunningStrategy.setProperty("symbols",
                                                symbols);
            AbstractRunningStrategy.setProperty("marketDataSource",
                                                marketDataSource);
            AbstractRunningStrategy.setProperty("statements",
                                                compressedStatements);
            AbstractRunningStrategy.setProperty("cepSource",
                                                cepSource);
            doProcessedMarketDataRequestVerification(false);
            // begin testing
            // test with null symbols
            AbstractRunningStrategy.setProperty("symbols",
                                                null);
            executeProcessedMarketDataRequest(false,
                                              false);
            // test with empty symbols string
            AbstractRunningStrategy.setProperty("symbols",
                                                "");
            executeProcessedMarketDataRequest(false,
                                              false);
            // done with negative symbols, replace the value
            AbstractRunningStrategy.setProperty("symbols",
                                                symbols);
            // test null market data source
            AbstractRunningStrategy.setProperty("marketDataSource",
                                                null);
            executeProcessedMarketDataRequest(false,
                                              true);
            // test empty market data source
            AbstractRunningStrategy.setProperty("marketDataSource",
                                                "");
            executeProcessedMarketDataRequest(false,
                                              true);
            // done with negative market data source, replace the value
            AbstractRunningStrategy.setProperty("marketDataSource",
                                                marketDataSource);
            // test null statements
            AbstractRunningStrategy.setProperty("statements",
                                                null);
            executeProcessedMarketDataRequest(false,
                                              true);
            // test zero statements
            AbstractRunningStrategy.setProperty("statements",
                                                createConsolidatedCEPStatement(new String[0]));
            executeProcessedMarketDataRequest(false,
                                              true);
            // done with negative statements, replace the value
            AbstractRunningStrategy.setProperty("statements",
                                                compressedStatements);
            // test null cep source
            AbstractRunningStrategy.setProperty("cepSource",
                                                null);
            executeProcessedMarketDataRequest(false,
                                              true);
            // test empty cep source
            AbstractRunningStrategy.setProperty("cepSource",
                                                "");
            executeProcessedMarketDataRequest(false,
                                              true);
            // done with negative cep source, replace the value
            AbstractRunningStrategy.setProperty("cepSource",
                                                cepSource);
            // turn off the md feed
            moduleManager.stop(bogusDataFeedURN);
            executeProcessedMarketDataRequest(false,
                                              true);
            // start the md feed again
            moduleManager.start(bogusDataFeedURN);
            // next test returns some values
            executeProcessedMarketDataRequest(true,
                                              true);
            // test cancellation of a combined request
            AbstractRunningStrategy.setProperty("cancelCep",
                                                "true");
            AbstractRunningStrategy runningStrategy = (AbstractRunningStrategy)getRunningStrategy(theStrategy).getRunningStrategy();
            runningStrategy.onCallback(this);
        }
    }
    /**
     * Tests that starting or stopping a strategy succeeds or fails depending on the strategy state.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void stateChanges()
        throws Exception
    {
        assumeTrue(!(Platform.isWindows() && getLanguage().equals(JAVA)));
        StrategyCoordinates strategy = getStrategyCompiles();
        final Properties parameters = new Properties();
        parameters.setProperty("shouldLoopOnStart",
                               "true");
        parameters.setProperty("shouldLoopOnStop",
                               "true");
        verifyPropertyNull("loopDone");
        verifyPropertyNull("onStartBegins");
        // strategy doesn't exist yet
        // need to manually start the strategy because it will be in "STARTING" status for a long long time (UNSTARTED->COMPILING->STARTING)
        final ModuleURN strategyURN = createModule(StrategyModuleFactory.PROVIDER_URN,
                                                   null,
                                                   strategy.getName(),
                                                   getLanguage(),
                                                   strategy.getFile(),
                                                   parameters,
                                                   null,
                                                   null);
        // strategy is now somewhere in the journey from UNSTARTED->COMPILING->STARTING.  this change is atomic with respect
        //  to module operations
        // wait until the strategy enters "STARTING"
        MarketDataFeedTestBase.wait(new Callable<Boolean>(){
            @Override
            public Boolean call()
                    throws Exception
            {
                return getStatus(strategyURN).equals(STARTING) &&
                       AbstractRunningStrategy.getProperty("onStartBegins") != null;
            }
        });
        // strategy is in STARTING state and will stay there until released by instrumentation that affects the running strategy
        // strategy is now looping
        // reset start counter
        AbstractRunningStrategy.setProperty("onStartBegins",
                                            null);
        // test to see what happens if the strategy is started again by the moduleManager (STARTING->UNSTARTED)
        new ExpectedFailure<ModuleStateException>(MODULE_NOT_STARTED_STATE_INCORRECT,
                                                  strategyURN.toString(),
                                                  ExpectedFailure.IGNORE,
                                                  ExpectedFailure.IGNORE) {
            @Override
            protected void run()
                throws Exception
            {
                moduleManager.start(strategyURN);
            }
        };
        verifyStrategyStatus(strategyURN,
                             STARTING);
        StrategyImpl strategyImpl = getRunningStrategy(strategyURN);
        // make sure the strategy module still thinks we're starting
        assertTrue(moduleManager.getModuleInfo(strategyURN).getState().isStarted());
        // try to stop the module (STARTING->STOPPING)
        new ExpectedFailure<ModuleStateException>(STRATEGY_STILL_RUNNING,
                                                  strategyImpl.toString(),
                                                  STARTING) {
            @Override
            protected void run()
                throws Exception
            {
                moduleManager.stop(strategyURN);
            }
        };
        // module is still started
        assertTrue(moduleManager.getModuleInfo(strategyURN).getState().isStarted());
        // release the running strategy (or it will keep running beyond the end of the test)
        AbstractRunningStrategy.setProperty("shouldStopLoop",
                                            "true");
        // strategy is now moving from STARTING->RUNNING
        // wait for the strategy to become ready
        verifyStrategyReady(strategyURN);
        // strategy is in RUNNING state
        verifyStrategyStatus(strategyURN,
                             RUNNING);
        // try to start again (RUNNING->UNSTARTED)
        new ExpectedFailure<ModuleStateException>(MODULE_NOT_STARTED_STATE_INCORRECT,
                                                  strategyURN.toString(),
                                                  ExpectedFailure.IGNORE,
                                                  ExpectedFailure.IGNORE) {
            @Override
            protected void run()
                throws Exception
            {
                moduleManager.start(strategyURN);
            }
        };
        // change status to STOPPING
        // make sure the strategy loops in onStop so we have time to play with it
        // reset all our flags and counters
        setPropertiesToNull();
        moduleManager.stop(strategyURN);
        // wait until the strategy enters "STOPPING"
        MarketDataFeedTestBase.wait(new Callable<Boolean>(){
            @Override
            public Boolean call()
                    throws Exception
            {
                return getStatus(strategyURN).equals(STOPPING) &&
                       AbstractRunningStrategy.getProperty("onStopBegins") != null;
            }
        });
        // strategy is now looping
        // reset stop counter
        AbstractRunningStrategy.setProperty("onStopBegins",
                                            null);
        // module is listed as stopped
        assertFalse(moduleManager.getModuleInfo(strategyURN).getState().isStarted());
        // test stopping (STOPPING->STOPPING)
        new ExpectedFailure<ModuleStateException>(MODULE_NOT_STOPPED_STATE_INCORRECT,
                                                  strategyURN.toString(),
                                                  ExpectedFailure.IGNORE,
                                                  ExpectedFailure.IGNORE) {
            @Override
            protected void run()
                throws Exception
            {
                moduleManager.stop(strategyURN);
            }
        };
        // test starting (STOPPING->UNSTARTED)
        new ExpectedFailure<ModuleStateException>(STRATEGY_STILL_RUNNING,
                                                  strategyImpl.toString(),
                                                  strategyImpl.getStatus()) {
            @Override
            protected void run()
                throws Exception
            {
                moduleManager.start(strategyURN);
            }
        };
        // let the strategy stop
        AbstractRunningStrategy.setProperty("shouldStopLoop",
                                            "true");
        // wait for the strategy to stop
        verifyStrategyStopped(strategyURN);
        verifyStrategyStatus(strategyURN,
                             STOPPED);
        // module is listed as stopped
        assertFalse(moduleManager.getModuleInfo(strategyURN).getState().isStarted());
        // cannot stop again (STOPPED->STOPPING)
        new ExpectedFailure<ModuleStateException>(MODULE_NOT_STOPPED_STATE_INCORRECT,
                strategyURN.toString(), ExpectedFailure.IGNORE,
                ExpectedFailure.IGNORE) {
            @Override
            protected void run()
                throws Exception
            {
                moduleManager.stop(strategyURN);
            }
        };
        // can start again (STOPPED->UNSTARTED->...)
        moduleManager.start(strategyURN);
        // (...->RUNNING)
        verifyStrategyReady(strategyURN);
        verifyStrategyStatus(strategyURN,
                             RUNNING);
        // strategy is RUNNING
        // move it to FAILED (RUNNING->STOPPING->FAILED)
        AbstractRunningStrategy.setProperty("shouldFailOnStop",
                                            "true");
        stopStrategy(strategyURN);
        verifyStrategyStatus(strategyURN,
                             FAILED);
        AbstractRunningStrategy.setProperty("shouldFailOnStop",
                                            null);
        // try to stop (FAILED->STOPPING)
        new ExpectedFailure<ModuleStateException>(MODULE_NOT_STOPPED_STATE_INCORRECT,
                strategyURN.toString(), ExpectedFailure.IGNORE,
                ExpectedFailure.IGNORE) {
            @Override
            protected void run()
                throws Exception
            {
                moduleManager.stop(strategyURN);
            }
        };
        // make sure it can start again (FAILED->UNSTARTED)
        moduleManager.start(strategyURN);
        verifyStrategyReady(strategyURN);
    }
    /**
     * Tests the ability for a strategy to request and receive {@link org.marketcetera.marketdata.MarketDataRequest.Content#MARKET_STAT} data.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void statistics()
        throws Exception
    {
        assumeTrue(!(Platform.isWindows() && getLanguage().equals(JAVA)));
        verifyPropertyNull("onStatistics");
        Properties parameters = new Properties();
        parameters.setProperty("content",
                               "MARKET_STAT");
        getMarketData(BogusFeedModuleFactory.IDENTIFIER,
                      "GOOG,YHOO,MSFT,METC",
                      false,
                      parameters);
        verifyPropertyNonNull("onStatistics");
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
     * Gets a strategy that sends events.
     *
     * @return a <code>StrategyCoordinates</code> value
     */
    protected abstract StrategyCoordinates getEventStrategy();
    /**
     * Gets a strategy that executes a processed market data request.
     *
     * @return a <code>StrategyCoordinates</code> value
     */
    protected abstract StrategyCoordinates getCombinedStrategy();
    /**
     * Indicates the number of expected compiler warnings for the given strategy.
     * 
     * <p>Subclasses may override this method to assist calculations for tests that
     * compute the number of expected messages.  The default implementation returns
     * zero.
     *
     * @param inStrategy a <code>StrategyCoordinates</code> value
     * @return an <code>int</code> value
     */
    protected int getExpectedCompilationWarningsFor(StrategyCoordinates inStrategy)
    {
        return 0;
    }
    /**
     * Executes a single interrupt test.
     *
     * @param inLoopOnStart a <code>boolean</code> value if the start loop should hang
     * @param inLoopOnStop a <code>boolean</code> value if the stop loop should hang
     * @throws Exception if an error occurs
     */
    protected void doInterruptTest(boolean inLoopOnStart,
                                   boolean inLoopOnStop)
        throws Exception
    {
        Properties parameters = new Properties();
        if(inLoopOnStart) {
            parameters.setProperty("shouldLoopOnStart",
                                   "true");
        }
        if(inLoopOnStop) {
            parameters.setProperty("shouldLoopOnStop",
                                   "true");
        }
        StrategyCoordinates strategy = getStrategyCompiles();
        final ModuleURN strategyURN = createModule(StrategyModuleFactory.PROVIDER_URN,
                                                   null,
                                                   strategy.getName(),
                                                   getLanguage(),
                                                   strategy.getFile(),
                                                   parameters,
                                                   null,
                                                   null);
        // wait for the appropriate condition before continuing
        if(inLoopOnStart) {
            // wait for the property to enter STARTING and the start loop to actually begin
            MarketDataFeedTestBase.wait(new Callable<Boolean>(){
                @Override
                public Boolean call()
                        throws Exception
                {
                    return getStatus(strategyURN).equals(STARTING) &&
                           AbstractRunningStrategy.getProperty("onStartBegins") != null;
                }
            });
            // strategy is looping in "onStart"
            verifyStrategyStatus(strategyURN,
                                 STARTING);
        } else {
            verifyStrategyReady(strategyURN);
            verifyStrategyStatus(strategyURN,
                                 RUNNING);
        }
        // strategy is in STARTING or RUNNING state
        // try to interrupt
        StrategyMXBean mxInterface = getMXProxy(strategyURN);
        mxInterface.interrupt();
        // status always ends up as STOPPED after interrupt
        verifyStrategyStatus(strategyURN,
                             STOPPED);
    }
    /**
     * Executes a single processed market data request test. 
     *
     * @param inSucceeds a <code>boolean</code> value indicating whether the test is expected to succeed or not
     * @param inCanConstructRequest a <code>boolean</code> value indicating whether the request can be constructed or not
     * @throws Exception if an error occurs
     */
    private void executeProcessedMarketDataRequest(boolean inSucceeds,
                                                   boolean inCanConstructRequest)
        throws Exception
    {
        AbstractRunningStrategy strategy = (AbstractRunningStrategy)getRunningStrategy(theStrategy).getRunningStrategy();
        AbstractRunningStrategy.setProperty("finished",
                                            null);
        // start test
        strategy.onOther(this);
        // retrieve the id returned by the request
        String requestIDString = AbstractRunningStrategy.getProperty("requestID");
        if(inCanConstructRequest) {
            assertNotNull("The request should have returned a value, either zero or non-zero, but null means the request didn't even happen",
                          requestIDString);
        } else {
            assertNull(requestIDString);
        }
        // wait until the agreed-upon number of events have arrived (if the strategy is going to work)
        if(inSucceeds) {
            assertTrue(Integer.parseInt(requestIDString) > 0);
            MarketDataFeedTestBase.wait(new Callable<Boolean>(){
                @Override
                public Boolean call()
                        throws Exception
                {
                    return AbstractRunningStrategy.getProperty("finished") != null;
                }
            });
        } else {
            if(inCanConstructRequest) {
                assertEquals(requestIDString,
                             "0");
            } else {
                assertNull(requestIDString);
            }
        }
        // check results
        doProcessedMarketDataRequestVerification(inSucceeds);
    }
    /**
     * Verify the result of a single processed market data request.
     *
     * @param inSucceeds a <code>boolean</code> value indicating whether the test is expected to succeed or not
     * @throws Exception if an error occurs
     */
    private void doProcessedMarketDataRequestVerification(boolean inSucceeds)
        throws Exception
    {
        Properties storedProperties = AbstractRunningStrategy.getProperties();
        for(Object rawKey : storedProperties.keySet()) {
            String key = (String)rawKey;
            if(key.contains("bid")) {
                fail("Should not have received any bids");
            }
            if(key.contains("ask")) {
                String value = storedProperties.getProperty(key);
                assertEquals(inSucceeds ? "Received an ask for a symbol other than 'METC': " + key : "Wasn't expecting any asks",
                             inSucceeds,
                             key.contains("METC"));
                assertTrue("Expected more than 0 asks for 'METC'",
                           Integer.parseInt(value) > 0);
            }
        }
    }
    /**
     * Verifies that a trade suggestion created as a result of a CEP query matches the expected values.
     *
     * @param inSymbol a <code>String</code> value containing the expected symbol
     * @param inPrice a <code>BigDecimal</code> value containing the expected price
     * @param inQuantity a <code>BigDecimal</code> value containing the expected quantity
     * @param inSuggestion an <code>OrderSingleSuggestion</code> containing the actual suggestion
     * @throws Exception if an error occurs
     */
    private void verifyCEPSuggestion(String inSymbol,
                                     BigDecimal inPrice,
                                     BigDecimal inQuantity,
                                     OrderSingleSuggestion inSuggestion)
        throws Exception
    {
        assertEquals(inSymbol,
                     inSuggestion.getOrder().getSymbol().getFullSymbol());
        assertEquals(inPrice,
                     inSuggestion.getOrder().getPrice());
        assertEquals(inQuantity,
                     inSuggestion.getOrder().getQuantity());
    }
    /**
     * Retrieves the suggestions stored by the given recorder.
     *
     * @param inRecorder a <code>ModuleURN</code> value containing a {@link MockRecorderModule} URN.
     * @return a <code>List&lt;OrderSingleSuggestion&gt;</code> value containing the recorded suggestions
     */
    private List<OrderSingleSuggestion> getReceivedSuggestions(ModuleURN inRecorder)
    {
        MockRecorderModule recorder = MockRecorderModule.Factory.recorders.get(inRecorder);
        List<OrderSingleSuggestion> suggestions = new ArrayList<OrderSingleSuggestion>();
        for(DataReceived datum : recorder.getDataReceived()) {
            suggestions.add((OrderSingleSuggestion)datum.getData());
        }
        recorder.resetDataReceived();
        return suggestions;
    }
    /**
     * Feeds the given events to the given <code>CEP</code> module. 
     *
     * @param inEvents an <code>EventBase[]</code> value
     * @param inCEPModule a <code>ModuleURN</code> value containing a CEP module
     * @return a <code>DataFlowID</code> value representing the channel by which the events are fed
     * @throws Exception if an error occurs
     */
    private DataFlowID feedEventsToCEP(EventBase[] inEvents,
                                       ModuleURN inCEPModule)
        throws Exception
    {
        SynchronousRequest request = new SynchronousRequest(inEvents);
        request.semaphore.acquire();
        DataFlowID dataFlowID =  moduleManager.createDataFlow(new DataRequest[] { new DataRequest(CopierModuleFactory.INSTANCE_URN,
                                                                                                  request),
                                                                                  new DataRequest(inCEPModule) },
                                                              false);
        // wait until the copier is ready
        request.semaphore.acquire();
        return dataFlowID;
    }
    /**
     * Creates a consolidated string used to communicate a set of CEP statements to a strategy.
     *
     * @param inStatements a <code>String[]</code> value
     * @return a <code>String</code> value
     */
    private String createConsolidatedCEPStatement(String[] inStatements)
    {
        StringBuilder statements = new StringBuilder();
        boolean separatorNeeded = false;
        for(String statement : inStatements) {
            if(separatorNeeded) {
                statements.append("#"); // arbitrary separator character
            } else {
                separatorNeeded = true;
            }
            statements.append(statement);
        }
        return statements.toString();
    }
    /**
     * Executes a single CEP test.
     *
     * @param inProvider a <code>String</code> value containing the provider to whom to make the request
     * @param inStatements a <code>String[]</code> value containing the statements to pass to the CEP module
     * @param inEvents an <code>EventBase[]</code> value containing the events to cause to be passed to the CEP module
     * @param inCleanup a <code>boolean</code> value indicating whether the strategy started during the test should be stopped or not
     * @return a <code>List&lt;OrderSingleSuggestion&gt;</code> value containing the suggestions received
     * @throws Exception if an error occurs
     */
    private List<OrderSingleSuggestion> doCEPTest(String inProvider,
                                                  String[] inStatements,
                                                  EventBase[] inEvents,
                                                  boolean inCleanup)
        throws Exception
    {
        setPropertiesToNull();
        MockRecorderModule recorder = MockRecorderModule.Factory.recorders.get(outputURN);
        recorder.resetDataReceived();
        Properties parameters = new Properties();
        parameters.setProperty("shouldRequestCEPData",
                               "true");
        if(inProvider != null) {
            parameters.setProperty("source",
                                   inProvider);
        }
        if(inStatements != null) {
            parameters.setProperty("statements",
                                   createConsolidatedCEPStatement(inStatements));
        }
        StrategyCoordinates strategyFile = getStrategyCompiles();
        theStrategy = createStrategy(strategyFile.getName(),
                                     getLanguage(),
                                     strategyFile.getFile(),
                                     parameters,
                                     null,
                                     outputURN);
        StrategyImpl strategy = getRunningStrategy(theStrategy);
        ModuleURN cepModuleURN = new ModuleURN("metc:cep:" + inProvider + ":" + strategy.getDefaultNamespace());
        long requestID = 0;
        if(AbstractRunningStrategy.getProperty("requestID") != null) {
            requestID = Long.parseLong(AbstractRunningStrategy.getProperty("requestID"));
        }
        // feed events into the cep module
        if(requestID != 0) {
            feedEventsToCEP(inEvents,
                            cepModuleURN);
        }
        // collect the suggestions created
        List<OrderSingleSuggestion> suggestions = getReceivedSuggestions(outputURN);
        if(inCleanup) {
            stopStrategy(theStrategy);
        }
        return suggestions;
    }
    /**
     * Performs a single brokers test.
     *
     * @param inExpectedBrokers a <code>BrokerStatus[]</code> value containing the expected brokers
     * @throws Exception if an error occurs
     */
    private void doBrokerTest(BrokerStatus[] inExpectedBrokers)
        throws Exception
    {
        StrategyCoordinates strategy = getStrategyCompiles();
        AbstractRunningStrategy.getProperties().clear();
        AbstractRunningStrategy.setProperty("askForBrokers",
                                            "true");
        verifyStrategyStartsAndStops(strategy.getName(),
                                     getLanguage(),
                                     strategy.getFile(),
                                     null,
                                     null,
                                     null);
        int counter = 0;
        for(BrokerStatus broker : inExpectedBrokers)
        {
            assertEquals(broker.toString(),
                         AbstractRunningStrategy.getProperty("" + counter++));
        }
        // verify there are no extra properties
        assertNull("Property " + inExpectedBrokers.length + " was non-null",
                   AbstractRunningStrategy.getProperty("" + inExpectedBrokers.length));
    }
    /**
     * Executes a single iteration of the get-current-position test.
     *
     * @param inSymbol a <code>String</code> value containing the symbol for which to search or null
     * @param inDate a <code>Date</code> value containing the time-point at which to search or null
     * @param inExpectedPosition a <code>BigDecimal</code> value containing the expected result
     * @throws Exception if an error occurs
     */
    private void doPositionTest(String inSymbol,
                                Date inDate,
                                BigDecimal inExpectedPosition)
        throws Exception
    {
        StrategyCoordinates strategy = getStrategyCompiles();
        MSymbol symbol = null;
        // set up data
        if(inSymbol != null) {
            symbol = new MSymbol(inSymbol);
            AbstractRunningStrategy.setProperty("symbol",
                                                symbol.toString());
        } else {
            AbstractRunningStrategy.setProperty("symbol",
                                                null);
        }
        if(inDate != null) {
            AbstractRunningStrategy.setProperty("date",
                                                Long.toString(inDate.getTime()));
        } else {
            AbstractRunningStrategy.setProperty("date",
                                                null);
        }
        AbstractRunningStrategy.setProperty("askForPosition",
                                            "true");
        AbstractRunningStrategy.setProperty("position",
                                            null);
        verifyStrategyStartsAndStops(strategy.getName(),
                                     getLanguage(),
                                     strategy.getFile(),
                                     null,
                                     null,
                                     null);
        assertEquals((inExpectedPosition == null ? null : inExpectedPosition.toString()),
                     AbstractRunningStrategy.getProperty("position"));
    }
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
                                               (FIXOrder)datum.getData(),true);
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
        createStrategy(null,
                       inStrategy.getName(),
                       getLanguage(),
                       inStrategy.getFile(),
                       inParameters,
                       false,
                       outputURN);
        return outputURN;
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
                                                           outputURN);
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
        AbstractRunningStrategy.setProperty("orderType",
                                            OrderType.Market.name());
        AbstractRunningStrategy.setProperty("quantity",
                                            "10000");
        // generate expected order
        List<ExecutionReport> expectedExecutionReports = new ArrayList<ExecutionReport>();
        StrategyImpl runningStrategy = getRunningStrategy(theStrategy);
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
            expectedOrder.setOrderType(OrderType.Market);
            expectedOrder.setQuantity(new BigDecimal(10000));
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
                                                  new MSymbol("METC"),
                                                  "Q",
                                                  new BigDecimal("100.00"),
                                                  new BigDecimal("10000")));
        assertEquals(inExecutionReportCount,
                     Integer.parseInt(AbstractRunningStrategy.getProperty("executionReportCount")));
        ExecutionReport[] actualExecutionReports = ((AbstractRunningStrategy)runningStrategy.getRunningStrategy()).getExecutionReports(orderID);
        assertEquals(expectedExecutionReports.size(),
                     actualExecutionReports.length);
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
     * @param inExpectedCumulativeOrders a <code>List&lt;OrderSingle&gt;</code> value
     * @throws Exception if an error occurs
     */
    private void doOrderTest(ModuleURN inStrategy,
                             final OrderSingle[] inExpectedOrders,
                             List<OrderSingle> inExpectedCumulativeOrders)
        throws Exception
    {
        final MockRecorderModule recorder = MockRecorderModule.Factory.recorders.get(outputURN);
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
        StrategyImpl runningStrategy = getRunningStrategy(inStrategy);
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
     * @param inOutputURN a <code>ModuleURN</code> value containing the destination to which to emit suggestions
     * @return a <code>ModuleURN</code> value
     * @throws Exception if an error occurs
     */
    private ModuleURN generateSuggestions(StrategyCoordinates inStrategy,
                                          Properties inParameters,
                                          ModuleURN inOutputURN)
        throws Exception
    {
        // start the strategy pointing at the suggestion receiver for its suggestions
        createStrategy(inStrategy.getName(),
                       getLanguage(),
                       inStrategy.getFile(),
                       inParameters,
                       null,
                       inOutputURN);
        return inOutputURN;
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
        theStrategy = createStrategy(inStrategy.getName(),
                                     getLanguage(),
                                     inStrategy.getFile(),
                                     null,
                                     null,
                                     inOrdersURN);
        setupMockORSConnection(theStrategy);
        return theStrategy;
    }
    /**
     * Creates a strategy that requests market data from the given provider for the given symbols.
     *
     * @param inProvider a <code>String</code> value containing the instance identifier of a market data provider
     * @param inSymbols a <code>String</code> value containing a comma-separated list of symbols for which to
     *   request market data
     * @param inUseStringAPI a <code>boolean</code> value indicating whether to use the string version of the new market data API
     * @param inParameters a <code>Properties</code> value containing parameters to pass to the strategy
     * @return a <code>ModuleURN</code> value containing the instance URN of the strategy guaranteed to be running
     * @throws Exception if an error occurs
     */
    private ModuleURN getMarketData(String inProvider,
                                    String inSymbols,
                                    boolean inUseStringAPI,
                                    Properties inParameters)
        throws Exception
    {
        final StrategyCoordinates strategy = getStrategyCompiles();
        inParameters.setProperty("shouldRequestData",
                               inProvider);
        inParameters.setProperty("symbols",
                               inSymbols);
        if(inUseStringAPI) {
            inParameters.setProperty("useStringAPI",
                                     "true");
        }
        return createStrategy(strategy.getName(),
                              getLanguage(),
                              strategy.getFile(),
                              inParameters,
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
        verifyStrategyReady(inStrategy);
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
     * Indicates that a <code>JUnit</code> test is designated as a performance test instead of a unit test.
     * 
     * <p>As this annotation becomes more widely used, an appropriate home can be chosen for it.  In the
     * long run, <code>LanguageTestBase</code> is not the right owner.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 1.0.0
     */
    public @interface PerformanceTest
    {
        boolean value() default true;
    }
}
