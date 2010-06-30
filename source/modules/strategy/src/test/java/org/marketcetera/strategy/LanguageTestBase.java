package org.marketcetera.strategy;

import static org.junit.Assert.*;
import static org.marketcetera.event.LogEventLevel.DEBUG;
import static org.marketcetera.event.LogEventLevel.ERROR;
import static org.marketcetera.event.LogEventLevel.INFO;
import static org.marketcetera.event.LogEventLevel.WARN;
import static org.marketcetera.module.Messages.MODULE_NOT_STARTED_STATE_INCORRECT;
import static org.marketcetera.module.Messages.MODULE_NOT_STOPPED_STATE_INCORRECT;
import static org.marketcetera.strategy.Status.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.Future;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.marketcetera.client.Client;
import org.marketcetera.client.brokers.BrokerStatus;
import org.marketcetera.client.brokers.BrokersStatus;
import org.marketcetera.core.notifications.Notification;
import org.marketcetera.core.position.PositionKey;
import org.marketcetera.event.Event;
import org.marketcetera.event.EventTestBase;
import org.marketcetera.event.LogEvent;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.marketdata.DateUtils;
import org.marketcetera.marketdata.MarketDataFeedTestBase;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.MarketDataRequestBuilder;
import org.marketcetera.marketdata.MarketDataModuleTestBase.DataSink;
import org.marketcetera.marketdata.bogus.BogusFeedModuleFactory;
import org.marketcetera.module.*;
import org.marketcetera.module.CopierModule.SynchronousRequest;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.strategy.StrategyTestBase.MockRecorderModule.DataReceived;
import org.marketcetera.trade.*;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.NamedThreadFactory;
import org.marketcetera.util.test.CollectionAssert;
import org.marketcetera.util.test.UnicodeData;

import quickfix.Message;
import quickfix.field.ExecType;
import quickfix.field.OrdStatus;
import quickfix.field.OrigClOrdID;
import quickfix.field.TransactTime;

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
        // runtime error in onStart
        final StrategyCoordinates strategy = getStrategyCompiles();
        final Properties parameters = new Properties();
        parameters.setProperty("shouldFailOnStart",
                               "true");
        final ModuleURN strategyURN = moduleManager.createModule(StrategyModuleFactory.PROVIDER_URN,
                                                                 null,
                                                                 strategy.getName(),
                                                                 getLanguage(),
                                                                 strategy.getFile(),
                                                                 parameters,
                                                                 null,
                                                                 null);
        // failed "onStart" means that the strategy is in error status and will not receive any data
        new ExpectedFailure<ModuleException>(FAILED_TO_START) {
            @Override
            protected void run()
                    throws Exception
            {
                moduleManager.start(strategyURN);
            }
        };
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
        ModuleURN strategyURN2 = createStrategy(strategy.getName(),
                                                getLanguage(),
                                                strategy.getFile(),
                                                parameters,
                                                null,
                                                null);
        doSuccessfulStartTest(strategyURN2);
        stopStrategy(strategyURN2);
        AbstractRunningStrategy.setProperty("shouldFailOnStop",
                                            null);
        // runtime error in each callback
        doCallbackFailsTest("shouldFailOnAsk",
                            new String[] { "onBid", "onCancel", "onExecutionReport", "onTrade", "onOther", "onDividend" });
        doCallbackFailsTest("shouldFailOnBid",
                            new String[] { "onAsk", "onCancel", "onExecutionReport", "onTrade", "onOther", "onDividend" });
        doCallbackFailsTest("shouldFailOnExecutionReport",
                            new String[] { "onAsk", "onBid", "onCancel", "onTrade", "onOther", "onDividend" });
        doCallbackFailsTest("shouldFailOnTrade",
                            new String[] { "onAsk", "onBid", "onCancel", "onExecutionReport", "onOther", "onDividend" });
        doCallbackFailsTest("shouldFailOnOther",
                            new String[] { "onAsk", "onBid", "onCancel", "onExecutionReport", "onTrade", "onDividend" });
        doCallbackFailsTest("shouldFailOnDividend",
                            new String[] { "onAsk", "onBid", "onCancel", "onExecutionReport", "onTrade", "onOther" });
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
        final StrategyCoordinates strategy = getStrategyCompiles();
        final Properties parameters = new Properties();
        parameters.setProperty("shouldLoopOnStart",
                               "true");
        assertNull(AbstractRunningStrategy.getProperty("loopDone"));
        // need to manually start the strategy because it will be in "STARTING" status for a long long time
        final ModuleURN strategyURN = moduleManager.createModule(StrategyModuleFactory.PROVIDER_URN,
                                                                 null,
                                                                 strategy.getName(),
                                                                 getLanguage(),
                                                                 strategy.getFile(),
                                                                 parameters,
                                                                 null,
                                                                 null);
        // start the strategy in another thread
        Future<ModuleURN> future = doAsynchronous(new Callable<ModuleURN>() {
            @Override
            public ModuleURN call()
                    throws Exception
            {
                moduleManager.start(strategyURN);
                return strategyURN;
            }
        });
        // wait until the strategy enters "STARTING"
        MarketDataFeedTestBase.wait(new Callable<Boolean>(){
            @Override
            public Boolean call()
                    throws Exception
            {
                try {
                    return getStatus(strategyURN).equals(STARTING);
                } catch (Exception e) {
                    return false;
                }
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
        future.get();
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
        moduleManager.stop(strategyURN);
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
        // begin stop process
        assertNull(AbstractRunningStrategy.getProperty("loopDone"));
        final List<Throwable> thrownExceptions = new ArrayList<Throwable>();
        // stop the strategy in another thread
        Future<ModuleURN> future = doAsynchronous(new Callable<ModuleURN>() {
            @Override
            public ModuleURN call()
                    throws Exception
            {
                moduleManager.stop(strategyURN);
                return strategyURN;
            }
        });
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
        future.get();
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
        assertTrue(thrownExceptions.isEmpty());
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
        // stop the strategy in another thread
        Future<ModuleURN> future = doAsynchronous(new Callable<ModuleURN>() {
            @Override
            public ModuleURN call()
                    throws Exception
            {
                moduleManager.stop(strategyURN);
                return strategyURN;
            }
        });
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
        future.get();
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
        suggestedOrder.setInstrument(new Equity("METC"));        
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
            verifyEvent((LogEvent)notificationSubscriber.getDataReceived().get(3).getData(),
                        WARN,
                        null,
                        INVALID_LOG,
                        String.valueOf(runningStrategy));
            verifyEvent((LogEvent)notificationSubscriber.getDataReceived().get(4).getData(),
                        DEBUG,
                        null,
                        MESSAGE_1P,
                        "");
            verifyEvent((LogEvent)notificationSubscriber.getDataReceived().get(5).getData(),
                        DEBUG,
                        null,
                        MESSAGE_1P,
                        "Some statement");
            verifyEvent((LogEvent)notificationSubscriber.getDataReceived().get(6).getData(),
                        DEBUG,
                        null,
                        MESSAGE_1P,
                        UnicodeData.HOUSE_AR);
            verifyEvent((LogEvent)notificationSubscriber.getDataReceived().get(7).getData(),
                        WARN,
                        null,
                        INVALID_LOG,
                        String.valueOf(runningStrategy));
            verifyEvent((LogEvent)notificationSubscriber.getDataReceived().get(8).getData(),
                        INFO,
                        null,
                        MESSAGE_1P,
                        "");
            verifyEvent((LogEvent)notificationSubscriber.getDataReceived().get(9).getData(),
                        INFO,
                        null,
                        MESSAGE_1P,
                        "Some statement");
            verifyEvent((LogEvent)notificationSubscriber.getDataReceived().get(10).getData(),
                        INFO,
                        null,
                        MESSAGE_1P,
                        UnicodeData.HOUSE_AR);
            verifyEvent((LogEvent)notificationSubscriber.getDataReceived().get(11).getData(),
                        WARN,
                        null,
                        INVALID_LOG,
                        String.valueOf(runningStrategy));
            verifyEvent((LogEvent)notificationSubscriber.getDataReceived().get(12).getData(),
                        WARN,
                        null,
                        MESSAGE_1P,
                        "");
            verifyEvent((LogEvent)notificationSubscriber.getDataReceived().get(13).getData(),
                        WARN,
                        null,
                        MESSAGE_1P,
                        "Some statement");
            verifyEvent((LogEvent)notificationSubscriber.getDataReceived().get(14).getData(),
                        WARN,
                        null,
                        MESSAGE_1P,
                        UnicodeData.HOUSE_AR);
            verifyEvent((LogEvent)notificationSubscriber.getDataReceived().get(15).getData(),
                        WARN,
                        null,
                        INVALID_LOG,
                        String.valueOf(runningStrategy));
            verifyEvent((LogEvent)notificationSubscriber.getDataReceived().get(16).getData(),
                        ERROR,
                        null,
                        MESSAGE_1P,
                        "");
            verifyEvent((LogEvent)notificationSubscriber.getDataReceived().get(17).getData(),
                        ERROR,
                        null,
                        MESSAGE_1P,
                        "Some statement");
            verifyEvent((LogEvent)notificationSubscriber.getDataReceived().get(18).getData(),
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
        expectedOrder.setInstrument(new Equity("METC"));   
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
        expectedOrder2.setInstrument(new Equity("GOOG"));
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
        expectedOrder3.setInstrument(new Equity("JAVA"));
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
        // execute the test one more time making sure that orders can be sent while stopping
        AbstractRunningStrategy.setProperty("orderShouldBeNull",
                                            "");
        AbstractRunningStrategy.setProperty("sendResult",
                                            "");
        stopStrategy(strategy);
        assertEquals("true",
                     AbstractRunningStrategy.getProperty("sendResult"));
    }
    /**
     * Tests a strategy's ability to send arbitrary objects.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void other()
        throws Exception
    {
        theStrategy = createStrategy(getOtherStrategy().getName(),
                                     getLanguage(),
                                     getOtherStrategy().getFile(),
                                     null,
                                     null,
                                     outputURN);
        // run the strategy with nothing set
        setPropertiesToNull();
        doOtherTest(theStrategy,
                    new Object[0]);
        assertTrue("Expected properties to be empty, but was: " + AbstractRunningStrategy.getProperties().toString(),
                   AbstractRunningStrategy.getProperties().isEmpty());
        // have the strategy send null
        AbstractRunningStrategy.setProperty("sendNull",
                                            "true");
        doOtherTest(theStrategy,
                    new Object[0]);
        // make sure only the property we set above is set
        assertEquals(1,
                     AbstractRunningStrategy.getProperties().size());
        assertEquals(AbstractRunningStrategy.getProperty("sendNull"),
                     "true");
        // have the strategy send a string
        setPropertiesToNull();
        AbstractRunningStrategy.setProperty("sendString",
                                            "true");
        doOtherTest(theStrategy,
                    new Object[] { "test string" });
        assertEquals("Properties were " + AbstractRunningStrategy.getProperties(),
                     1,
                     AbstractRunningStrategy.getProperties().size());
        assertEquals(AbstractRunningStrategy.getProperty("sendString"),
                     "true");
        // have the strategy send two BigDecimals
        setPropertiesToNull();
        AbstractRunningStrategy.setProperty("sendTwo",
                                            "true");
        doOtherTest(theStrategy,
                    new Object[] { BigDecimal.ONE, BigDecimal.TEN });
        assertEquals("Properties was " + AbstractRunningStrategy.getProperties(),
                     1,
                     AbstractRunningStrategy.getProperties().size());
        assertEquals(AbstractRunningStrategy.getProperty("sendTwo"),
                     "true");
        // route orders to strategy and check output
        setPropertiesToNull();
        theStrategy = createStrategy(getOtherStrategy().getName(),
                                     getLanguage(),
                                     getOtherStrategy().getFile(),
                                     null,
                                     true,
                                     outputURN);
        AbstractRunningStrategy.setProperty("sendTwo",
                                            "true");
        doOtherTest(theStrategy,
                    new Object[] { BigDecimal.ONE, BigDecimal.TEN });
        assertEquals("Properties was " + AbstractRunningStrategy.getProperties(),
                     1,
                     AbstractRunningStrategy.getProperties().size());
        assertEquals(AbstractRunningStrategy.getProperty("sendTwo"),
                     "true");
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
                       outputURN);
        doExecutionReportTest(0,
                              false);
       // create an order and have the strategy submit it
        MockRecorderModule.shouldSendExecutionReports = false;
        doExecutionReportTest(0,
                              true);
        MockRecorderModule.shouldSendExecutionReports = true;
        MockRecorderModule.shouldFullyFillOrders = false;
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
        MockRecorderModule.shouldFullyFillOrders = false;
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
        assertEquals("1",
                     AbstractRunningStrategy.getProperty("ordersCanceled"));
        // create two orders to cancel
        runningStrategy.onAsk(askEvent);
        runningStrategy.onAsk(askEvent);
        // trigger cancel
        runningStrategy.onTrade(tradeEvent);
        // without a functioning ER sender, the previous order will stay in the collection of orders to cancel
        assertEquals("3",
                     AbstractRunningStrategy.getProperty("ordersCanceled"));
        // submit another order
        AbstractRunningStrategy.setProperty("ordersCanceled",
                                            "0");
        runningStrategy.onAsk(askEvent);
        // make sure an order cancel all can be submitted during strategy stop
        AbstractRunningStrategy.setProperty("allOrdersCanceled",
                                            "");
        stopStrategy(strategy);
        // payload of orders to stop includes the previous 3, the one sent via "onAsk" above, and one sent in "onStop"
        assertEquals("5",
                     AbstractRunningStrategy.getProperty("allOrdersCanceled"));
        // cycle the module to get a fresh session
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
        MockRecorderModule recorder = MockRecorderModule.Factory.recorders.get(outputURN);
        assertNotNull("Must be able to find the recorder created",
                      recorder);
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
        // add account
        AbstractRunningStrategy.setProperty("account",
                                            "account-" + System.nanoTime());
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
        MockRecorderModule.shouldFullyFillOrders = false;
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
        // submit another order, create a cancel order, but don't submit it
        recorder.resetDataReceived();
        AbstractRunningStrategy.setProperty("executionReportsReceived",
                                            "0");
        AbstractRunningStrategy.setProperty("orderCanceled",
                                            null);
        runningStrategy.onAsk(askEvent);
        assertEquals("1",
                     AbstractRunningStrategy.getProperty("executionReportsReceived"));
        assertEquals(recorder.getDataReceived().toString(),
                     1,
                     recorder.getDataReceived().size());
        assertTrue(recorder.getDataReceived().get(0).getData() instanceof OrderSingle);
        recorder.resetDataReceived();
        // set up the strategy to not submit the cancel
        AbstractRunningStrategy.setProperty("skipSubmitOrders",
                                            "true");
        // generate the cancel
        orderIDString = AbstractRunningStrategy.getProperty("orderID"); 
        assertNotNull(orderIDString);        
        runningStrategy.onOther(new OrderID(orderIDString));
        // verify it was properly generated
        assertEquals("true",
                     AbstractRunningStrategy.getProperty("orderCanceled"));
        // verify it was not submitted
        assertTrue(recorder.getDataReceived().isEmpty());
        // submit, generate cancel, modify it, submit it after delay
        recorder.resetDataReceived();
        AbstractRunningStrategy.setProperty("executionReportsReceived",
                                            "0");
        AbstractRunningStrategy.setProperty("orderCanceled",
                                            null);
        runningStrategy.onAsk(askEvent);
        assertEquals("1",
                     AbstractRunningStrategy.getProperty("executionReportsReceived"));
        assertEquals(recorder.getDataReceived().toString(),
                     1,
                     recorder.getDataReceived().size());
        assertTrue(recorder.getDataReceived().get(0).getData() instanceof OrderSingle);
        String modifiedAccountName = "modified account-" + System.nanoTime();
        assertFalse(((OrderSingle)recorder.getDataReceived().get(0).getData()).getAccount().equals(modifiedAccountName));
        recorder.resetDataReceived();
        // set up the strategy to not submit the cancel
        AbstractRunningStrategy.setProperty("delaySubmitOrders",
                                            "true");
        AbstractRunningStrategy.setProperty("newAccountName",
                                            modifiedAccountName);
        // generate the cancel
        orderIDString = AbstractRunningStrategy.getProperty("orderID"); 
        assertNotNull(orderIDString);        
        runningStrategy.onOther(new OrderID(orderIDString));
        // verify it was properly generated
        assertEquals("true",
                     AbstractRunningStrategy.getProperty("orderCanceled"));
        // verify it was submitted
        assertEquals(recorder.getDataReceived().toString(),
                     1,
                     recorder.getDataReceived().size());
        assertTrue(recorder.getDataReceived().get(0).getData() instanceof OrderCancel);
        OrderCancel orderCancel = (OrderCancel)recorder.getDataReceived().get(0).getData();
        assertEquals(modifiedAccountName,
                     orderCancel.getAccount());
        assertNull(orderCancel.getBrokerOrderID());
        // clean up before continuing
        AbstractRunningStrategy.setProperty("skipSubmitOrders",
                                            null);
        AbstractRunningStrategy.setProperty("delaySubmitOrders",
                                            null);
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
        // make sure an order cancel can be submitted during strategy stop
        AbstractRunningStrategy.setProperty("orderCancelNull",
                                            "");
        stopStrategy(strategy);
        assertEquals("false",
                     AbstractRunningStrategy.getProperty("orderCancelNull"));
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
        MockRecorderModule recorder = MockRecorderModule.Factory.recorders.get(outputURN);
        assertNotNull("Must be able to find the recorder created",
                      recorder);
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
        assertEquals(1,
                     runningStrategy.getSubmittedOrders().size());
        assertEquals(OrderType.Market,
                     runningStrategy.getSubmittedOrders().get(0).getOrderType());
        assertNotNull(orderIDString);
        // try to cancel/replace with a null OrderID
        AbstractRunningStrategy.setProperty("orderID",
                                            null);
        AbstractRunningStrategy.setProperty("newOrderID",
                                            "");
        OrderSingle newOrder = Factory.getInstance().createOrderSingle();
        assertNotNull(AbstractRunningStrategy.getProperty("newOrderID"));
        BigDecimal quantity = new BigDecimal("1000.50"); 
        newOrder.setQuantity(quantity);
        newOrder.setPrice(new BigDecimal("1"));
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
        String replaceIDString = AbstractRunningStrategy.getProperty("newOrderID");
        assertNotNull(replaceIDString);
        assertFalse(replaceIDString.equals(newOrder.getOrderID().toString()));
        // hooray, it worked
        // send a new order
        runningStrategy.onAsk(askEvent);
        orderIDString = AbstractRunningStrategy.getProperty("orderID");
        assertNotNull(orderIDString);
        AbstractRunningStrategy.setProperty("orderID",
                                            orderIDString);
        // cause a cancel/replace again, but this time don't allow the cancel/replace to be submitted
        recorder.resetDataReceived();
        AbstractRunningStrategy.setProperty("skipSubmitOrders",
                                            "true");
        runningStrategy.onOther(newOrder);
        assertNotNull(replaceIDString);
        assertTrue(recorder.getDataReceived().isEmpty());
        // can't cancel/replace this order any more, so create a new one
        runningStrategy.onAsk(askEvent);
        recorder.resetDataReceived();
        orderIDString = AbstractRunningStrategy.getProperty("orderID");
        assertNotNull(orderIDString);
        AbstractRunningStrategy.setProperty("orderID",
                                            orderIDString);
        // cancel/replace this order, don't automatically submit it, then modify it and submit it manually
        AbstractRunningStrategy.setProperty("delaySubmitOrders",
                                            "true");
        runningStrategy.onOther(newOrder);
        assertNotNull(replaceIDString);
        assertEquals(recorder.getDataReceived().toString(),
                     1,
                     recorder.getDataReceived().size());
        Object receivedData = recorder.getDataReceived().get(0).getData();
        assertTrue("Object is " + receivedData,
                   receivedData instanceof OrderReplace);
        OrderReplace receivedOrder = (OrderReplace)receivedData;
        assertNull(receivedOrder.getBrokerOrderID());
        assertEquals(quantity.add(BigDecimal.ONE),
                     receivedOrder.getQuantity());
        AbstractRunningStrategy.setProperty("delaySubmitOrders",
                                            null);
        AbstractRunningStrategy.setProperty("skipSubmitOrders",
                                            null);
        // when canceling a market order, price must *not* be specified in the replace
        assertNull(receivedOrder.getPrice());
        // turn on execution reports
        MockRecorderModule.shouldSendExecutionReports = true;
        MockRecorderModule.shouldFullyFillOrders = false;
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
        // make sure an order replace can be submitted during strategy stop
        AbstractRunningStrategy.setProperty("orderReplaceNull",
                                            "");
        AbstractRunningStrategy.setProperty("shouldReplace",
                                            "true");
        stopStrategy(strategy);
        assertEquals("false",
                     AbstractRunningStrategy.getProperty("orderReplaceNull"));
        // do a final test making sure that a price *is* specified for a non-market replace
        AbstractRunningStrategy.setProperty("orderType",
                                            OrderType.Limit.name());
        recorder.resetDataReceived();
        startStrategy(strategy);
        runningStrategy = (AbstractRunningStrategy)getRunningStrategy(strategy).getRunningStrategy();
        AbstractRunningStrategy.setProperty("newOrderID",
                                            null);
        runningStrategy.onAsk(askEvent);
        orderIDString = AbstractRunningStrategy.getProperty("orderID"); 
        assertNotNull(orderIDString);
        assertEquals(1,
                     runningStrategy.getSubmittedOrders().size());
        assertEquals(OrderType.Limit,
                     runningStrategy.getSubmittedOrders().get(0).getOrderType());
        recorder.resetDataReceived();
        AbstractRunningStrategy.setProperty("orderID",
                                            orderIDString);
        AbstractRunningStrategy.setProperty("newOrderID",
                                            "");
        newOrder.setPrice(new BigDecimal("12345"));
        runningStrategy.onOther(newOrder);
        receivedData = recorder.getDataReceived().get(0).getData();
        assertTrue("Object is " + receivedData,
                   receivedData instanceof OrderReplace);
        receivedOrder = (OrderReplace)receivedData;
        assertEquals(new BigDecimal("12345"),
                     receivedOrder.getPrice());
    }
    /**
     * Tests {@link AbstractRunningStrategy#getPositionAsOf(Date, String)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void positionAsOf()
            throws Exception
    {
        Instrument validEquity = null;
        for(Instrument instrument : positions.keySet()) {
            if(instrument instanceof Equity) {
                validEquity = instrument;
                break;
            }
        }
        assertNotNull(validEquity);
        Position position = positions.get(validEquity);
        assertNotNull(position);
        String invalidSymbol = "there-is-no-position-for-this-symbol-" + System.nanoTime();
        assertFalse(positions.containsKey(new Equity(invalidSymbol)));
        // null symbol
        doPositionAsOfTest(null,
                           new Date(),
                           null);
        // invalid symbol
        doPositionAsOfTest(invalidSymbol,
                           new Date(),
                           null);
        // null date
        doPositionAsOfTest(validEquity.getSymbol(),
                           null,
                           null);
        // call fails
        MockClient.getPositionFails = true;
        doPositionAsOfTest(validEquity.getSymbol(),
                           new Date(),
                           null);
        MockClient.getPositionFails = false;
        getClientFails = true;
        doPositionAsOfTest(validEquity.getSymbol(),
                           new Date(),
                           null);
        getClientFails = false;
        // date in the past (before position begins)
        Interval<BigDecimal> openingBalance = position.getPositionView().get(0);
        doPositionAsOfTest(validEquity.getSymbol(),
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
        doPositionAsOfTest(validEquity.getSymbol(),
                           date,
                           expectedValue);
        // date exactly now
        date = new Date();
        expectedValue = position.getPositionAt(date);
        dataPoint = view.get(view.size() - 1);
        assertEquals("value at " + date + ": " + position,
                     dataPoint.getValue(),
                     expectedValue);
        doPositionAsOfTest(validEquity.getSymbol(),
                           date,
                           expectedValue);
        // pick a data point two weeks into the future
        date = new Date(System.currentTimeMillis() + (1000 * 60 * 60 * 24 * 14));
        expectedValue = position.getPositionAt(date);
        dataPoint = view.get(view.size() - 1);
        assertEquals("value at " + date + ": " + position,
                     dataPoint.getValue(),
                     expectedValue);
        doPositionAsOfTest(validEquity.getSymbol(),
                       date,
                       expectedValue);
    }
    /**
     * Tests {@link AbstractRunningStrategy#getAllPositionsAsOf(Date)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void allPositionsAsOf()
            throws Exception
    {
        // null date
        doAllPositionsAsOfTest(null,
                               null);
        // call fails
        MockClient.getPositionFails = true;
        doAllPositionsAsOfTest(new Date(),
                               null);
        MockClient.getPositionFails = false;
        // the date of the earliest position of all instruments
        Date date = new Date();
        for(Position position : positions.values()) {
            // examine only equities to make sure that the position we find is for an equity
            if(position.getInstrument() instanceof Equity) {
                date = new Date(Math.min(date.getTime(),
                                         position.getPositionView().get(0).getDate().getTime()));
            }
        }
        MockClient client = new MockClient();
        date = new Date(date.getTime() - 1000);
        doAllPositionsAsOfTest(date, // 1s before the open of the position
                               client.getAllEquityPositionsAsOf(date));
        // date in the past (after position begins)
        date = new Date(date.getTime() + 2000); // 1s after the open of the position
        assertTrue(date.getTime() < System.currentTimeMillis());
        // found a date somewhere in the middle of the position and earlier than today
        doAllPositionsAsOfTest(date,
                               client.getAllEquityPositionsAsOf(date));
        // date exactly now
        date = new Date();
        doAllPositionsAsOfTest(date,
                               client.getAllEquityPositionsAsOf(date));
        // pick a data point two weeks into the future
        date = new Date(System.currentTimeMillis() + (1000 * 60 * 60 * 24 * 14));
        doAllPositionsAsOfTest(date,
                               client.getAllEquityPositionsAsOf(date));
    }
    /**
     * Tests {@link AbstractRunningStrategy#getOptionPositionAsOf(Date, String, String, BigDecimal, OptionType)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void optionPositionAsOf()
            throws Exception
    {
        Option validOption = null;
        for(Instrument instrument : positions.keySet()) {
            if(instrument instanceof Option) {
                validOption = (Option)instrument;
                break;
            }
        }
        assertNotNull(validOption);
        Position position = positions.get(validOption);
        assertNotNull(position);
        String invalidSymbol = "there-is-no-position-for-this-symbol-" + System.nanoTime();
        Option invalidOption = new Option(invalidSymbol,
                                          DateUtils.dateToString(new Date(),
                                                                 DateUtils.DAYS),
                                          EventTestBase.generateDecimalValue(),
                                          OptionType.Call);
        assertFalse(positions.containsKey(invalidOption));
        // null option root
        doOptionPositionAsOfTest(null,
                                 validOption.getExpiry(),
                                 validOption.getStrikePrice(),
                                 validOption.getType(),
                                 new Date(),
                                 null);
        // null expiry
        doOptionPositionAsOfTest(validOption.getSymbol(),
                                 null,
                                 validOption.getStrikePrice(),
                                 validOption.getType(),
                                 new Date(),
                                 null);
        // null strike price
        doOptionPositionAsOfTest(validOption.getSymbol(),
                                 validOption.getExpiry(),
                                 null,
                                 validOption.getType(),
                                 new Date(),
                                 null);
        // null option type
        doOptionPositionAsOfTest(validOption.getSymbol(),
                                 validOption.getExpiry(),
                                 validOption.getStrikePrice(),
                                 null,
                                 new Date(),
                                 null);
        // null date
        doOptionPositionAsOfTest(validOption.getSymbol(),
                                 validOption.getExpiry(),
                                 validOption.getStrikePrice(),
                                 validOption.getType(),
                                 null,
                                 null);
        // option doesn't exist
        doOptionPositionAsOfTest(invalidOption.getSymbol(),
                                 invalidOption.getExpiry(),
                                 invalidOption.getStrikePrice(),
                                 invalidOption.getType(),
                                 new Date(),
                                 null);
        // call fails
        MockClient.getPositionFails = true;
        doOptionPositionAsOfTest(validOption.getSymbol(),
                                 validOption.getExpiry(),
                                 validOption.getStrikePrice(),
                                 validOption.getType(),
                                 new Date(),
                                 null);
        MockClient.getPositionFails = false;
        // date in the past (before position begins)
        Interval<BigDecimal> openingBalance = position.getPositionView().get(0);
        doOptionPositionAsOfTest(validOption.getSymbol(),
                                 validOption.getExpiry(),
                                 validOption.getStrikePrice(),
                                 validOption.getType(),
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
        doOptionPositionAsOfTest(validOption.getSymbol(),
                                 validOption.getExpiry(),
                                 validOption.getStrikePrice(),
                                 validOption.getType(),
                                 date,
                                 expectedValue);
        // date exactly now
        date = new Date();
        expectedValue = position.getPositionAt(date);
        dataPoint = view.get(view.size() - 1);
        assertEquals("value at " + date + ": " + position,
                     dataPoint.getValue(),
                     expectedValue);
        doOptionPositionAsOfTest(validOption.getSymbol(),
                                 validOption.getExpiry(),
                                 validOption.getStrikePrice(),
                                 validOption.getType(),
                                 date,
                                 expectedValue);
        // pick a data point two weeks into the future
        date = new Date(System.currentTimeMillis() + (1000 * 60 * 60 * 24 * 14));
        expectedValue = position.getPositionAt(date);
        dataPoint = view.get(view.size() - 1);
        assertEquals("value at " + date + ": " + position,
                     dataPoint.getValue(),
                     expectedValue);
        doOptionPositionAsOfTest(validOption.getSymbol(),
                                 validOption.getExpiry(),
                                 validOption.getStrikePrice(),
                                 validOption.getType(),
                                 date,
                                 expectedValue);
    }
    /**
     * Tests {@link AbstractRunningStrategy#getAllOptionPositionsAsOf(Date)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void allOptionPositionsAsOf()
            throws Exception
    {
        // null date
        doAllOptionPositionsAsOfTest(null,
                                     null);
        // call fails
        MockClient.getPositionFails = true;
        doAllOptionPositionsAsOfTest(new Date(),
                                     null);
        MockClient.getPositionFails = false;
        // the date of the earliest position of all instruments
        Date date = new Date();
        for(Position position : positions.values()) {
            if(position.getInstrument() instanceof Option) {
                date = new Date(Math.min(date.getTime(),
                                         position.getPositionView().get(0).getDate().getTime()));
            }
        }
        MockClient client = new MockClient();
        date = new Date(date.getTime() - 1000);
        doAllOptionPositionsAsOfTest(date, // 1s before the open of the position
                                     client.getAllOptionPositionsAsOf(date));
        // date in the past (after position begins)
        date = new Date(date.getTime() + 2000); // 1s after the open of the position
        assertTrue(date.getTime() < System.currentTimeMillis());
        // found a date somewhere in the middle of the position and earlier than today
        doAllOptionPositionsAsOfTest(date,
                                     client.getAllOptionPositionsAsOf(date));
        // date exactly now
        date = new Date();
        doAllOptionPositionsAsOfTest(date,
                                     client.getAllOptionPositionsAsOf(date));
        // pick a data point two weeks into the future
        date = new Date(System.currentTimeMillis() + (1000 * 60 * 60 * 24 * 14));
        doAllOptionPositionsAsOfTest(date,
                                     client.getAllOptionPositionsAsOf(date));
    }
    /**
     * Tests {@link AbstractRunningStrategy#getOptionPositionsAsOf(Date, String...)}. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void optionPositionsAsOf()
            throws Exception
    {
        MockClient client = new MockClient();
        Option validOption1 = null;
        Option validOption2 = null;
        for(Instrument instrument : positions.keySet()) {
            if(instrument instanceof Option &&
               validOption1 == null) {
                validOption1 = (Option)instrument;
            } else {
                if(instrument instanceof Option &&
                   validOption2 == null) {
                    validOption2 = (Option)instrument;
                }
            }
        }
        assertNotNull(validOption1);
        assertNotNull(validOption2);
        Position position1 = positions.get(validOption1);
        Position position2 = positions.get(validOption2);
        assertNotNull(position1);
        assertNotNull(position2);
        String invalidSymbol = "there-is-no-position-for-this-symbol-" + System.nanoTime();
        Option invalidOption = new Option(invalidSymbol,
                                          DateUtils.dateToString(new Date(),
                                                                 DateUtils.DAYS),
                                          EventTestBase.generateDecimalValue(),
                                          OptionType.Call);
        assertFalse(positions.containsKey(invalidOption));
        // null option roots
        doOptionPositionsAsOfTest(null,
                                  new Date(),
                                  null,
                                  null);
        // empty option roots
        doOptionPositionsAsOfTest(new String[0],
                                  new Date(),
                                  null,
                                  null);
        String[] optionRoots = new String[] { validOption1.getSymbol(), invalidOption.getSymbol(), null };
        Date date = new Date();
        // a mix of valid and invalid
        doOptionPositionsAsOfTest(optionRoots,
                                  date,
                                  null,
                                  client.getOptionPositionsAsOf(date,
                                                                optionRoots));
        // null date
        doOptionPositionsAsOfTest(new String[] { validOption1.getSymbol() },
                                  null,
                                  null,
                                  null);
        optionRoots = new String[] { invalidOption.getSymbol() };
        date = new Date();
        // option doesn't exist
        doOptionPositionsAsOfTest(optionRoots,
                                  date,
                                  null,
                                  client.getOptionPositionsAsOf(date,
                                                                optionRoots));
        // call fails
        MockClient.getPositionFails = true;
        doOptionPositionsAsOfTest(new String[] { validOption1.getSymbol() },
                                  new Date(),
                                  null,
                                  null);
        MockClient.getPositionFails = false;
        // date in the past (before position begins)
        optionRoots = new String[] { validOption1.getSymbol(), validOption2.getSymbol() };
        Interval<BigDecimal> openingBalance1 = position1.getPositionView().get(0);
        Interval<BigDecimal> openingBalance2 = position2.getPositionView().get(0);
        date = new Date(Math.min(openingBalance1.getDate().getTime(),
                                 openingBalance2.getDate().getTime()) - 1000); // 1s before the open of the position
        doOptionPositionsAsOfTest(optionRoots,
                                  date,
                                  null,
                                  client.getOptionPositionsAsOf(date,
                                                                optionRoots));
        // date in the past (after position begins)
        List<Interval<BigDecimal>> view = position1.getPositionView(); 
        int median = view.size() / 2;
        assertTrue("Position " + position1 + " contains no data!",
                   median > 0);
        Interval<BigDecimal> dataPoint = position1.getPositionView().get(median);
        date = dataPoint.getDate();
        assertTrue(date.getTime() < System.currentTimeMillis());
        // found a date somewhere in the middle of the position and earlier than today
        doOptionPositionsAsOfTest(optionRoots,
                                  date,
                                  null,
                                  client.getOptionPositionsAsOf(date,
                                                                optionRoots));
        // date exactly now
        date = new Date();
        doOptionPositionsAsOfTest(optionRoots,
                                  date,
                                  null,
                                  client.getOptionPositionsAsOf(date,
                                                                optionRoots));
        // pick a data point two weeks into the future
        date = new Date(System.currentTimeMillis() + (1000 * 60 * 60 * 24 * 14));
        doOptionPositionsAsOfTest(optionRoots,
                                  date,
                                  null,
                                  client.getOptionPositionsAsOf(date,
                                                                optionRoots));
        // do a pair of special tests that need special handling
        Properties parameters = new Properties();
        parameters.setProperty("nullOptionRoot",
                               "true");
        doOptionPositionsAsOfTest(new String[] { validOption1.getSymbol() },
                                  date,
                                  parameters,
                                  null);
        parameters.clear();
        parameters.setProperty("emptyOptionRoot",
                               "true");
        doOptionPositionsAsOfTest(new String[] { validOption1.getSymbol() },
                                  date,
                                  parameters,
                                  null);
    }
    /**
     * Tests {@link AbstractRunningStrategy#getUnderlying(String)}. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void underlying()
            throws Exception
    {
        MockClient client = new MockClient();
        Option validOption = null;
        for(Instrument instrument : positions.keySet()) {
            if(instrument instanceof Option) {
                validOption = (Option)instrument;
                break;
            }
        }
        assertNotNull(validOption);
        assertTrue(underlyings.containsKey(validOption.getSymbol()));
        String invalidSymbol = "there-is-no-underlying-for-this-symbol-" + System.nanoTime();
        assertFalse(underlyings.containsKey(invalidSymbol));
        // null option root
        doUnderlyingTest(null,
                         null);
        // empty option root
        doUnderlyingTest("",
                         null);
        // invalid option root
        doUnderlyingTest(invalidSymbol,
                         null);
        // call fails
        MockClient.getPositionFails = true;
        doUnderlyingTest(validOption.getSymbol(),
                         null);
        MockClient.getPositionFails = false;
        // valid option root
        doUnderlyingTest(validOption.getSymbol(),
                         client.getUnderlying(validOption.getSymbol()));
    }
    /**
     * Tests {@link AbstractRunningStrategy#getOptionRoots(String)}. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void optionRoots()
            throws Exception
    {
        MockClient client = new MockClient();
        String underlyingSymbol = roots.keySet().iterator().next();
        assertNotNull(underlyingSymbol);
        String invalidUnderlyingSymbol = "not-an-underlying-symbol";
        assertFalse(roots.keySet().contains(invalidUnderlyingSymbol));
        // null underlying symbol
        doOptionRootsTest(null,
                          null);
        // empty underlying symbol
        doOptionRootsTest("",
                          null);
        // invalid underlying symbol
        doOptionRootsTest(invalidUnderlyingSymbol,
                          client.getOptionRoots(invalidUnderlyingSymbol));
        // call fails
        MockClient.getPositionFails = true;
        doOptionRootsTest(underlyingSymbol,
                          null);
        MockClient.getPositionFails = false;
        // valid underlying symbol
        doOptionRootsTest(underlyingSymbol,
                          client.getOptionRoots(underlyingSymbol));
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
        String validEsperStatement1 = "select * from trade where instrumentAsString='METC'";
        String validEsperStatement2 = "select * from ask where instrumentAsString='ORCL'";
        String validSystemStatement1 = "select * from trade";
        String validSystemStatement2 = "select * from ask";
        String invalidStatement = "this statement is not syntactically valid";
        Event[] events = new Event[] { EventTestBase.generateEquityTradeEvent(System.nanoTime(), System.currentTimeMillis(), new Equity("METC"), "Q", new BigDecimal("1"), new BigDecimal("100")),
                                       EventTestBase.generateEquityTradeEvent(System.nanoTime(), System.currentTimeMillis(), new Equity("ORCL"), "Q", new BigDecimal("2"), new BigDecimal("200")),
                                       EventTestBase.generateEquityAskEvent(System.nanoTime(), System.currentTimeMillis(), new Equity("METC"), "Q", new BigDecimal("3"), new BigDecimal("300")),
                                       EventTestBase.generateEquityAskEvent(System.nanoTime(), System.currentTimeMillis(), new Equity("ORCL"), "Q", new BigDecimal("4"), new BigDecimal("400")),
                                       EventTestBase.generateEquityBidEvent(System.nanoTime(), System.currentTimeMillis(), new Equity("METC"), "Q", new BigDecimal("5"), new BigDecimal("500")),
                                       EventTestBase.generateEquityBidEvent(System.nanoTime(), System.currentTimeMillis(), new Equity("ORCL"), "Q", new BigDecimal("6"), new BigDecimal("600")) };
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
        Event[] events = new Event[] { EventTestBase.generateEquityTradeEvent(System.nanoTime(), System.currentTimeMillis(), new Equity("METC"), "Q", new BigDecimal("1"), new BigDecimal("100")),
                                       EventTestBase.generateEquityTradeEvent(System.nanoTime(), System.currentTimeMillis(), new Equity("ORCL"), "Q", new BigDecimal("2"), new BigDecimal("200")),
                                       EventTestBase.generateEquityAskEvent(System.nanoTime(), System.currentTimeMillis(), new Equity("METC"), "Q", new BigDecimal("3"), new BigDecimal("300")),
                                       EventTestBase.generateEquityAskEvent(System.nanoTime(), System.currentTimeMillis(), new Equity("ORCL"), "Q", new BigDecimal("4"), new BigDecimal("400")),
                                       EventTestBase.generateEquityBidEvent(System.nanoTime(), System.currentTimeMillis(), new Equity("METC"), "Q", new BigDecimal("5"), new BigDecimal("500")),
                                       EventTestBase.generateEquityBidEvent(System.nanoTime(), System.currentTimeMillis(), new Equity("ORCL"), "Q", new BigDecimal("6"), new BigDecimal("600")) };
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
        Event[] events = new Event[] { EventTestBase.generateEquityTradeEvent(System.nanoTime(), System.currentTimeMillis(), new Equity("METC"), "Q", new BigDecimal("1"), new BigDecimal("100")),
                                               EventTestBase.generateEquityTradeEvent(System.nanoTime(), System.currentTimeMillis(), new Equity("ORCL"), "Q", new BigDecimal("2"), new BigDecimal("200")),
                                               EventTestBase.generateEquityAskEvent(System.nanoTime(), System.currentTimeMillis(), new Equity("METC"), "Q", new BigDecimal("3"), new BigDecimal("300")),
                                               EventTestBase.generateEquityAskEvent(System.nanoTime(), System.currentTimeMillis(), new Equity("ORCL"), "Q", new BigDecimal("4"), new BigDecimal("400")),
                                               EventTestBase.generateEquityBidEvent(System.nanoTime(), System.currentTimeMillis(), new Equity("METC"), "Q", new BigDecimal("5"), new BigDecimal("500")),
                                               EventTestBase.generateEquityBidEvent(System.nanoTime(), System.currentTimeMillis(), new Equity("ORCL"), "Q", new BigDecimal("6"), new BigDecimal("600")) };
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
        // setup onStop test
        DataSink dataSink = new DataSink();
        moduleManager.addSinkListener(dataSink);
        stopStrategy(theStrategy);
        Map<DataFlowID,List<Object>> sinkData = dataSink.getAllData();
        // should have created a single data flow
        assertEquals(1,
                     sinkData.keySet().size());
        DataFlowID dataFlowID = sinkData.keySet().iterator().next();
        assertNotNull(dataFlowID);
        List<Object> data = sinkData.get(dataFlowID);
        // the data flow will have 1 event
        assertEquals(1,
                     data.size());
        assertTrue("Expected " + data.get(0) + " to be a TradeEvent",
                   data.get(0) instanceof TradeEvent);
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
        eventSubscriber.resetDataReceived();
        // cleanup
        moduleManager.cancel(eventSubscription);
        moduleManager.cancel(allSubscription);
        // stop and make sure the event there gets sent
        DataSink dataSink = new DataSink();
        moduleManager.addSinkListener(dataSink);
        stopStrategy(theStrategy);
        Map<DataFlowID,List<Object>> sinkData = dataSink.getAllData();
        // should have created a single data flow
        assertEquals(1,
                     sinkData.keySet().size());
        DataFlowID dataFlowID = sinkData.keySet().iterator().next();
        assertNotNull(dataFlowID);
        List<Object> data = sinkData.get(dataFlowID);
        // the data flow will have 1 event
        assertEquals(1,
                     data.size());
        assertTrue("Expected " + data.get(0) + " to be a TradeEvent",
                   data.get(0) instanceof TradeEvent);
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
        for(int apiCounter=0;apiCounter<=1;apiCounter++) {
            boolean useStringAPI = (apiCounter == 1);
            AbstractRunningStrategy.getProperties().clear();
            if(useStringAPI) {
                AbstractRunningStrategy.setProperty("useStringAPI",
                                                    "true");
            }
            // these are the nominal test values
            String symbols = "METC,ORCL,GOOG,YHOO";
            String marketDataSource = BogusFeedModuleFactory.IDENTIFIER;
            String compressedStatements = createConsolidatedCEPStatement(new String[] { "select * from ask where instrumentAsString='METC'" });
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
            // test empty market data source (uses "default" bogus source so returns data)
            AbstractRunningStrategy.setProperty("marketDataSource",
                                                "");
            executeProcessedMarketDataRequest(true,
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
            // start the strategy for this mini-test
            theStrategy = createStrategy(getCombinedStrategy().getName(),
                                         getLanguage(),
                                         getCombinedStrategy().getFile(),
                                         null,
                                         null,
                                         null);
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
        final ModuleURN strategyURN = moduleManager.createModule(StrategyModuleFactory.PROVIDER_URN,
                                                                 null,
                                                                 strategy.getName(),
                                                                 getLanguage(),
                                                                 strategy.getFile(),
                                                                 parameters,
                                                                 null,
                                                                 null);
        final List<Throwable> thrownExceptions = new ArrayList<Throwable>();
        // start the strategy in another thread
        Thread helperThread = new Thread(new Runnable() {
            @Override
            public void run()
            {
                try {
                    moduleManager.start(strategyURN);
                } catch (ModuleException e) {
                    thrownExceptions.add(e);
                }
            }});
        helperThread.start();
        // strategy is now somewhere in the journey from UNSTARTED->COMPILING->STARTING.  this change is atomic with respect
        //  to module operations
        // wait until the strategy enters "STARTING"
        MarketDataFeedTestBase.wait(new Callable<Boolean>(){
            @Override
            public Boolean call()
                    throws Exception
            {
                try {
                    return getStatus(strategyURN).equals(STARTING) &&
                                     AbstractRunningStrategy.getProperty("onStartBegins") != null;
                } catch (Exception e) {
                    return false;
                }
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
        // try to stop the module (STARTING->STOPPING)
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
        // release the running strategy (or it will keep running beyond the end of the test)
        AbstractRunningStrategy.setProperty("shouldStopLoop",
                                            "true");
        helperThread.join();
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
        helperThread = new Thread(new Runnable() {
            @Override
            public void run()
            {
                try {
                    moduleManager.stop(strategyURN);
                } catch (ModuleException e) {
                    thrownExceptions.add(e);
                }
            }});
        helperThread.start();
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
        // let the strategy stop
        AbstractRunningStrategy.setProperty("shouldStopLoop",
                                            "true");
        helperThread.join();
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
        moduleManager.stop(strategyURN);
    }
    /**
     * Tests the ability for a strategy to request and receive {@link org.marketcetera.marketdata.Content#MARKET_STAT} data.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void statistics()
        throws Exception
    {
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
     * Tests the ability of a strategy to request and cancel data flows from other modules.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void createDataFlows()
        throws Exception
    {
        // invalid URN
        final ModuleURN invalidURN = new ModuleURN("metc:something:something");
        // valid URN, but not started
        final ModuleURN validUnstartedURN = moduleManager.createModule(MockRecorderModule.Factory.PROVIDER_URN);
        // valid URN, started, but not emitter
        final ModuleURN validURNNotEmitter = SinkModuleFactory.INSTANCE_URN;
        // valid URN, started, but not receiver
        final ModuleURN validURNNotReceiver = BogusFeedModuleFactory.INSTANCE_URN;
        assertFalse(moduleManager.getModuleInfo(SinkModuleFactory.INSTANCE_URN).isEmitter());
        // valid, started receiver
        ModuleURN dataEmitterURN = createModule(StrategyDataEmissionModule.Factory.PROVIDER_URN);
        final Properties parameters = new Properties();
        // null URN list
        doDataFlowTest(parameters,
                       false);
        // single URN (invalid flow, need two at least)
        parameters.setProperty("urns",
                               dataEmitterURN.getValue());
        doDataFlowTest(parameters,
                       false);
        // two URNs, but one is unstarted
        parameters.setProperty("urns",
                               validUnstartedURN.getValue());
        parameters.setProperty("useStrategyURN",
                               "true");
        doDataFlowTest(parameters,
                       false);
        // two URNs, but one is invalid
        parameters.setProperty("urns",
                               invalidURN.getValue());
        doDataFlowTest(parameters,
                       false);
        // valid, started, but not emitter
        parameters.setProperty("urns",
                               validURNNotEmitter.getValue());
        doDataFlowTest(parameters,
                       false);
        // valid, started, but not receiver
        parameters.setProperty("urns",
                               dataEmitterURN.getValue() + "," + validURNNotReceiver.getValue());
        doDataFlowTest(parameters,
                       false);
        // valid simple test with 2 URNs
        parameters.setProperty("urns",
                               dataEmitterURN.getValue());
        doDataFlowTest(parameters,
                       true);
        // again, but this time setting up extra data flow tests
        parameters.setProperty("shouldCancelDataFlow",
                               "true");
        doDataFlowTest(parameters,
                       true);
        parameters.remove("shouldCancelDataFlow");
        // repeat the test, but sabotage the manual cancel
        parameters.setProperty("urns",
                               dataEmitterURN.getValue());
        parameters.setProperty("shouldSkipCancel",
                               "true");
        doDataFlowTest(parameters,
                       true);
        parameters.remove("shouldSkipCancel");
        // all tests so far have been without the sink, now add the sink
        parameters.setProperty("routeToSink",
                               "true");
        doDataFlowTest(parameters,
                       true);
        // do a test that sets up a data flow that doesn't involve the strategy
        parameters.remove("useStrategyURN");
        parameters.remove("routeToSink");
        parameters.setProperty("urns",
                               dataEmitterURN.getValue() + "," + MockRecorderModule.Factory.recorders.get(outputURN).getURN());
        doDataFlowTest(parameters,
                       true);
        // a new test that tries to set up a data flow when the strategy can't accept new data
        parameters.setProperty("urns",
                               dataEmitterURN.getValue());
        parameters.setProperty("useStrategyURN",
                               "true");
        parameters.setProperty("shouldMakeNewRequest",
                               "true");
        doDataFlowTest(parameters,
                       true);
    }
    /**
     * Tests the Strategy API mechanism for tracking submitted open orders.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void orderRetention()
        throws Exception
    {
        // construct some sets of actions that test a series of actions that might be performed on an order
        OrderRetentionAction[] EMPTY = new OrderRetentionAction[] { };
        OrderRetentionAction[] PARTIAL_FILL = new OrderRetentionAction[] { OrderRetentionAction.PARTIALLY_FILL };
        OrderRetentionAction[] FULL_FILL = new OrderRetentionAction[] { OrderRetentionAction.FULLY_FILL };
        OrderRetentionAction[] CANCEL = new OrderRetentionAction[] { OrderRetentionAction.CANCEL };
        OrderRetentionAction[] REPLACE = new OrderRetentionAction[] { OrderRetentionAction.REPLACE };
        OrderRetentionAction[] COMPOSITE_FILL = new OrderRetentionAction[] { OrderRetentionAction.PARTIALLY_FILL, OrderRetentionAction.FULLY_FILL };
        OrderRetentionAction[] COMPOSITE_CANCEL = new OrderRetentionAction[] { OrderRetentionAction.PARTIALLY_FILL, OrderRetentionAction.CANCEL };
        OrderRetentionAction[] COMPOSITE_REPLACE = new OrderRetentionAction[] { OrderRetentionAction.PARTIALLY_FILL, OrderRetentionAction.REPLACE };
        OrderRetentionAction[] COMPLICATED_SEQUENCE = new OrderRetentionAction[] { OrderRetentionAction.PARTIALLY_FILL, OrderRetentionAction.REPLACE,
                                                                                   OrderRetentionAction.PARTIALLY_FILL, OrderRetentionAction.REPLACE,
                                                                                   OrderRetentionAction.PARTIALLY_FILL, OrderRetentionAction.CANCEL };
        // this list represents a valid series of actions that might be performed by a strategy - note that these actions span more than one order
        OrderRetentionAction[][] PERMUTATIONS = new OrderRetentionAction[][] { EMPTY,
                                                                               PARTIAL_FILL,
                                                                               FULL_FILL,
                                                                               CANCEL,
                                                                               PARTIAL_FILL,
                                                                               REPLACE,
                                                                               COMPOSITE_FILL,
                                                                               COMPOSITE_CANCEL,
                                                                               COMPOSITE_REPLACE,
                                                                               COMPLICATED_SEQUENCE };
        for(FIXVersion version : FIXVersion.values()) {
            Properties parameters = new Properties();
            parameters.setProperty("ordersToSubmit",
                                   "0");
            // test with no execution reports
            doOrderRetentionTest(parameters,
                                 new OrderRetentionAction[][] {},
                                 version);
            // test with a single generated order
            parameters.setProperty("ordersToSubmit",
                                   "1");
            doOrderRetentionTest(parameters,
                                 PERMUTATIONS,
                                 version);
            // test with all the permutations
            parameters.setProperty("ordersToSubmit",
                                   String.valueOf(PERMUTATIONS.length));
            doOrderRetentionTest(parameters,
                                 PERMUTATIONS,
                                 version);
        }
    }
    /**
     * Tests the ability to set and retrieve user data.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void userdata()
            throws Exception
    {
        Client testClient = StrategyModule.clientFactory.getClient();
        assertNull(testClient.getUserData());
        StrategyCoordinates strategy = getStrategyCompiles();
        ModuleURN strategyModule = createStrategy(strategy.getName(),
                                                  getLanguage(),
                                                  strategy.getFile(),
                                                  null,
                                                  null,
                                                  null);
        verifyPropertyNonNull("onStart");
        doSuccessfulStartTest(strategyModule);
        Properties userdata = testClient.getUserData();
        assertNotNull(userdata);
        assertNotNull(userdata.getProperty("onStart"));
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
     * Get a strategy that sends other data.
     *
     * @return a <code>StrategyCoordinates</code> value
     */
    protected abstract StrategyCoordinates getOtherStrategy();
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
     * Gets a strategy that requests and cancels data flows.
     *
     * @return a <code>StrategyCoordinates</code> value
     */
    protected abstract StrategyCoordinates getDataFlowStrategy();
    /**
     * Gets a strategy that exercises the strategy API's ability to retain and
     * release <code>OrderID</code> values.
     *
     * @return a <code>StrategyCoordinates</code> value
     */
    protected abstract StrategyCoordinates getOrderRetentionStrategy();
    /**
     * Gets a strategy that exercises the Strategy API calls related to
     * positions.
     *
     * @return a <code>StrategyCoordinates</code> value
     */
    protected abstract StrategyCoordinates getPositionsStrategy();
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
        theStrategy = createStrategy(getCombinedStrategy().getName(),
                                     getLanguage(),
                                     getCombinedStrategy().getFile(),
                                     null,
                                     null,
                                     null);
        AbstractRunningStrategy strategy = (AbstractRunningStrategy)getRunningStrategy(theStrategy).getRunningStrategy();
        AbstractRunningStrategy.setProperty("finished",
                                            null);
        // reset any stored results from the previous test
        Set<Object> keys = new HashSet<Object>(AbstractRunningStrategy.getProperties().keySet());
        for(Object rawKey : keys) {
            String key = (String)rawKey;
            if(key.startsWith("ask") ||
               key.startsWith("bid")) {
                AbstractRunningStrategy.getProperties().remove(rawKey);
            }
        }
        AbstractRunningStrategy.setProperty("bid",
                                            null);
        AbstractRunningStrategy.setProperty("ask",
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
                assertEquals("0",
                             requestIDString);
            } else {
                assertNull(requestIDString);
            }
        }
        // check results
        doProcessedMarketDataRequestVerification(inSucceeds);
        stopStrategy(theStrategy);
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
                     inSuggestion.getOrder().getInstrument().getSymbol());
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
     * @param inEvents an <code>Event[]</code> value
     * @param inCEPModule a <code>ModuleURN</code> value containing a CEP module
     * @return a <code>DataFlowID</code> value representing the channel by which the events are fed
     * @throws Exception if an error occurs
     */
    private DataFlowID feedEventsToCEP(Event[] inEvents,
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
     * @param inEvents an <code>Event[]</code> value containing the events to cause to be passed to the CEP module
     * @param inCleanup a <code>boolean</code> value indicating whether the strategy started during the test should be stopped or not
     * @return a <code>List&lt;OrderSingleSuggestion&gt;</code> value containing the suggestions received
     * @throws Exception if an error occurs
     */
    private List<OrderSingleSuggestion> doCEPTest(String inProvider,
                                                  String[] inStatements,
                                                  Event[] inEvents,
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
        MockRecorderModule recorder = MockRecorderModule.Factory.recorders.get(suggestionReceiver);
        assertNotNull("Must be able to find the recorder created",
                      recorder);
        validateMessages(recorder,
                         inExpectedOrders);
        stopStrategy(theStrategy);
        validateMessages(recorder,
                         inExpectedOrders);
    }
    /**
     * Validates sent messages.
     *
     * @param inRecorder a <code>MockRecorderModule</code> value
     * @param inExpectedOrders an <code>FIXOrder[]</code> value
     */
    private void validateMessages(MockRecorderModule inRecorder,
                                  FIXOrder[] inExpectedOrders)
    {
        List<DataReceived> messages = inRecorder.getDataReceived();
        assertEquals("The number of expected messages does not match the number of actual messages",
                     inExpectedOrders.length,
                     messages.size());
        int index = 0;
        for(DataReceived datum : messages) {
            TypesTestBase.assertOrderFIXEquals(inExpectedOrders[index++],
                                               (FIXOrder)datum.getData(),true);
        }
        inRecorder.resetDataReceived();
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
        // verify the data
        verifySuggestions(inExpectedSuggestions,
                          suggestionReceiver);
        // do the same test while stopping
        stopStrategy(theStrategy);
        // verify again
        verifySuggestions(inExpectedSuggestions,
                          suggestionReceiver);
    }
    /**
     * Verifies that the expected suggestions were sent.
     *
     * @param inExpectedSuggestions an <code>OrderSingleSuggestion[]</code> value
     * @param inSuggestionReceiver a <code>ModuleURN</code> value
     */
    private void verifySuggestions(OrderSingleSuggestion[] inExpectedSuggestions,
                                   ModuleURN inSuggestionReceiver)
    {
        final MockRecorderModule recorder = MockRecorderModule.Factory.recorders.get(inSuggestionReceiver);
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
            expectedOrder.setInstrument(new Equity("METC"));
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
        runningStrategy.dataReceived(EventTestBase.generateEquityBidEvent(System.nanoTime(),
                                                                          System.currentTimeMillis(),
                                                                          new Equity("METC"),
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
        MockRecorderModule.shouldSendExecutionReports = false;
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
     * Starts a strategy module which generates data and measures against the
     * given expected results.
     * 
     * @param inStrategy a <code>ModuleURN</code> value
     * @param inExpectedObjects an <code>Object[]</code> value
     * @throws Exception if an error occurs
     */
    private void doOtherTest(ModuleURN inStrategy,
                             Object[] inExpectedObjects)
        throws Exception
    {
        MockRecorderModule recorder = MockRecorderModule.Factory.recorders.get(outputURN);
        assertNotNull("Must be able to find the recorder created",
                      recorder);
        // this will execute onAsk on the strategies, which will generate the desired data
        recorder.resetDataReceived();
        AbstractRunningStrategy runningStrategy = (AbstractRunningStrategy)getRunningStrategy(inStrategy).getRunningStrategy();
        runningStrategy.onAsk(askEvent);
        List<DataReceived> objects = recorder.getDataReceived();
        int index = 0;
        for(DataReceived datum : objects) {
            assertEquals(inExpectedObjects[index++],
                         datum.getData());
        }
    }
    /**
     * Executes a single data flow test.
     *
     * @param inParameters a <code>Properties</code> value
     * @param inDataExpected a <code>boolean</code> value indicating whether the test is expected to produce data (succeed) or not
     * @throws Exception if an error occurs
     */
    private void doDataFlowTest(Properties inParameters,
                                boolean inDataExpected)
        throws Exception
    {
        MockRecorderModule recorder = MockRecorderModule.Factory.recorders.get(outputURN);
        DataSink dataSink = new DataSink();
        moduleManager.addSinkListener(dataSink);
        assertNotNull("Must be able to find the recorder created",
                      recorder);
        recorder.resetDataReceived();
        setPropertiesToNull();
        StrategyCoordinates strategy = getDataFlowStrategy();
        theStrategy = createStrategy(strategy.getName(),
                                     getLanguage(),
                                     strategy.getFile(),
                                     inParameters,
                                     null,
                                     outputURN);
        List<Object> inExpectedData = new ArrayList<Object>();
        if(inDataExpected) {
            inExpectedData.addAll(StrategyDataEmissionModule.getDataToSend());
            assertNotNull(AbstractRunningStrategy.getProperty("dataFlowID"));
        }
        List<DataReceived> dataReceived = recorder.getDataReceived();
        assertEquals("Expected " + inExpectedData + " but got " + dataReceived,
                     inExpectedData.size(),
                     dataReceived.size());
        int index = 0;
        for(DataReceived datum : dataReceived) {
            assertEquals("Expected " + inExpectedData.get(index) + " but got " + datum,
                         inExpectedData.get(index++),
                         datum.getData());
        }
        // check the sink
        if(inParameters.getProperty("routeToSink") == "true") {
            Map<DataFlowID,List<Object>> sinkData = dataSink.getAllData();
            // all our tests create a single data flow at a time
            assertEquals(1,
                         sinkData.keySet().size());
            DataFlowID dataFlowID = sinkData.keySet().iterator().next();
            assertTrue("Expected " + inExpectedData + " but got " + sinkData.get(dataFlowID),
                       Arrays.equals(inExpectedData.toArray(),
                                     sinkData.get(dataFlowID).toArray()));
        } else {
            assertTrue(dataSink.getAllData().isEmpty());
        }
        String rawDataFlowID = AbstractRunningStrategy.getProperty("dataFlowID");
        assertEquals(inDataExpected,
                     rawDataFlowID != null);
        // do an extra test, if necessary, canceling a few data flows
        if(inParameters.getProperty("shouldCancelDataFlow") == "true") {
            // cancel the main flow first (later setting us up to cancel a stopped flow)
            if(inDataExpected) {
                DataFlowID dataFlowID = new DataFlowID(rawDataFlowID);
                assertTrue(moduleManager.getDataFlows(true).contains(dataFlowID));
                getRunningStrategy(theStrategy).getRunningStrategy().onCallback(dataFlowID);
                assertNotNull(AbstractRunningStrategy.getProperty("localDataFlowStopped"));
                assertFalse(moduleManager.getDataFlows(true).contains(dataFlowID));
                AbstractRunningStrategy.setProperty("localDataFlowStopped",
                                                    null);
                // create a bogus data flow
                dataFlowID = new DataFlowID("not-a-data-flow-id");
                getRunningStrategy(theStrategy).getRunningStrategy().onCallback(dataFlowID);
                assertNotNull(AbstractRunningStrategy.getProperty("localDataFlowStopped"));
                assertFalse(moduleManager.getDataFlows(true).contains(dataFlowID));
                AbstractRunningStrategy.setProperty("localDataFlowStopped",
                                                    null);
                // create a data flow (not created by the strategy)
                MarketDataRequest inRequest = MarketDataRequestBuilder.newRequest().withProvider(BogusFeedModuleFactory.IDENTIFIER).withSymbols("METC").create();
                dataFlowID = moduleManager.createDataFlow(new DataRequest[] { new DataRequest(new ModuleURN(String.format("metc:mdata:%s",
                                                                                                                          inRequest.getProvider())),
                                                                                              inRequest)},
                                                          true);
                assertNotNull(dataFlowID);
                assertTrue(moduleManager.getDataFlows(true).contains(dataFlowID));
                getRunningStrategy(theStrategy).getRunningStrategy().onCallback(dataFlowID);
                assertNotNull(AbstractRunningStrategy.getProperty("localDataFlowStopped"));
                assertFalse(moduleManager.getDataFlows(true).contains(dataFlowID));
            }
        }
        // cancel the data flow
        stopStrategy(theStrategy);
        // one way or the other, stopping the strategy will cause the data flow to stop, either directly
        //  or indirectly when the strategy module stops
        if(inDataExpected) {
            DataFlowID dataFlowID = new DataFlowID(rawDataFlowID);
            // verifies that the data flow is not still running
            assertFalse(moduleManager.getDataFlows(true).contains(dataFlowID));
            if(inParameters.getProperty("shouldSkipCancel") != null) {
                assertNull(AbstractRunningStrategy.getProperty("dataFlowStopped"));
            } else {
                assertNotNull(AbstractRunningStrategy.getProperty("dataFlowStopped"));
            }
        }
        // check to see if the strategy tried to create a new data flow while stopping
        if(inParameters.getProperty("shouldMakeNewRequest") == "true") {
            assertEquals("true",
                         AbstractRunningStrategy.getProperty("newDataFlowAttempt"));
            assertEquals("null",
                         AbstractRunningStrategy.getProperty("newDataFlowID"));
        }
    }
    /**
     * Performs an order retention test with the given parameters and actions.
     * 
     * <p>This test is distressingly complicated.  The basic plan-of-attack is to run a strategy
     * that creates a given number of orders and submits them.  The <code>OrderID</code> for each
     * submitted order is stored by the Strategy API.
     * 
     * <p>When the strategy has started, it has submitted all the orders that it was supposed to.  It
     * has also recorded the <code>OrderID</code> of each order it submitted.  Since the orders aren't routed
     * anywhere, they are still open.  The open orders are also tracked by the Strategy API.  That's the
     * mechanism being tested here.  When the Strategy API receives an <code>ExecutionReport</code>, if
     * the <code>ExecutionReport</code> changes the status of an open order such that it is no longer open,
     * the <code>OrderID</code> is removed from the Strategy API tracker.
     * 
     * <p>The next part of the test is to generate a set of <code>ExecutionReport</code> objects based on the
     * given set of actions and fling them at the strategy.  As the Strategy API receives the <code>ExecutionReport</code>
     * objects, it adjusts its open order tracker.  The test builds a set of open orders on its own and compares
     * the expected set with the actual set tracked by the Strategy API.
     *
     * @param inParameters a <code>Properties</code> value containing the parameters to pass to the strategy
     * @param inActions an <code>OrderRetentionAction[][]</code> value containing the actions to perform on each order
     * 
     * @throws Exception if an unexpected error occurs
     */
    private void doOrderRetentionTest(Properties inParameters,
                                      OrderRetentionAction[][] inActions,
                                      FIXVersion inFIXVersion)
        throws Exception
    {
        StrategyCoordinates testStrategy = getOrderRetentionStrategy();
        setPropertiesToNull();
        createStrategy(testStrategy.getName(),
                       getLanguage(),
                       testStrategy.getFile(),
                       inParameters,
                       null,
                       outputURN);
        // create handles to the running strategy in its various forms
        StrategyImpl strategy = getRunningStrategy(theStrategy);
        AbstractRunningStrategy runningStrategy = (AbstractRunningStrategy)strategy.getRunningStrategy();
        // retrieve the OrderIDs generated during test start
        String orderIDList = AbstractRunningStrategy.getProperty("orderIDs");
        // this collection will contain the OrderIDs of the orders actually submitted when the strategy starts
        //  these values are the ones that are being tracked by the OrderID tracker in the Strategy API
        List<OrderID> generatedOrderIDs = new ArrayList<OrderID>();
        if(orderIDList != null &&
           !orderIDList.isEmpty()) {
            String[] rawOrderIDs = orderIDList.split(",");
            for(String rawOrderID : rawOrderIDs) {
                if(rawOrderID != null &&
                   !rawOrderID.trim().isEmpty()) {
                    generatedOrderIDs.add(new OrderID(rawOrderID.trim()));
                }
            }
        }
        // verify that the number of orders submitted matches the number expected
        assertEquals(Integer.parseInt(inParameters.getProperty("ordersToSubmit")),
                     generatedOrderIDs.size());
        // create the list of expected order IDs - this will change based on the execution reports we're sending in below
        List<OrderID> expectedRetainedOrderIDs = new ArrayList<OrderID>(generatedOrderIDs);
        // create and transmit execution reports to change the retained orders collection
        List<ExecutionReport> executionReportsToSend = new ArrayList<ExecutionReport>();
        // send an execution report that doesn't relate to any of the submitted orders
        OrderSingle unrelatedOrder = createOrderWithID(null);
        assertFalse(generatedOrderIDs.contains(unrelatedOrder.getOrderID()));
        executionReportsToSend.addAll(generateExecutionReports(unrelatedOrder));
        List<OrderID> openGeneratedOrderIDs = new ArrayList<OrderID>();
        // add some execution reports depending on the specified actions for this test
        boolean executedCancelTest = false;
        boolean executedReplaceTest = false;
        boolean attemptedCancelTest = false;
        boolean attemptedReplaceTest = false;
        int index = 0;
        for(OrderID orderID : generatedOrderIDs) {
            OrderRetentionAction[] actionsForThisOrder = inActions[index++];
            for(OrderRetentionAction action : actionsForThisOrder) {
                switch(action) {
                    case CANCEL :
                        attemptedCancelTest = true;
                        if(!openGeneratedOrderIDs.isEmpty()) {
                            OrderID orderToCancel = openGeneratedOrderIDs.remove(0);
                            expectedRetainedOrderIDs.remove(orderToCancel);
                            Message fixCancelReport = generateFixExecutionReport(createOrderWithID(orderID),
                                                                                 OrdStatus.CANCELED,
                                                                                 BigDecimal.ZERO,
                                                                                 BigDecimal.ZERO,
                                                                                 inFIXVersion);
                            if(inFIXVersion.compareTo(FIXVersion.FIX42) > 0) {
                                fixCancelReport.setChar(ExecType.FIELD,
                                                        ExecType.CANCELED);
                            }
                            fixCancelReport.setString(OrigClOrdID.FIELD,
                                                      orderToCancel.getValue());
                            ExecutionReport cancelReport = org.marketcetera.trade.Factory.getInstance().createExecutionReport(fixCancelReport,
                                                                                                                              new BrokerID("broker"),
                                                                                                                              Originator.Broker,
                                                                                                                              null,
                                                                                                                              null);
                            executionReportsToSend.add(cancelReport);
                            expectedRetainedOrderIDs.remove(orderID);
                            executedCancelTest = true;
                        }
                        continue;
                    case FULLY_FILL :
                        MockRecorderModule.shouldFullyFillOrders = true;
                        executionReportsToSend.addAll(generateExecutionReports(createOrderWithID(orderID)));
                        expectedRetainedOrderIDs.remove(orderID);
                        openGeneratedOrderIDs.remove(orderID);
                        continue;
                    case PARTIALLY_FILL :
                        MockRecorderModule.shouldFullyFillOrders = false;
                        executionReportsToSend.addAll(generateExecutionReports(createOrderWithID(orderID)));
                        openGeneratedOrderIDs.add(orderID);
                        continue;
                    case REPLACE :
                        attemptedReplaceTest = true;
                        if(!openGeneratedOrderIDs.isEmpty()) {
                            OrderID orderToReplace = openGeneratedOrderIDs.remove(0);
                            expectedRetainedOrderIDs.remove(orderToReplace);
                            Message fixReplaceReport = generateFixExecutionReport(createOrderWithID(orderID),
                                                                                  OrdStatus.REPLACED,
                                                                                  BigDecimal.ZERO,
                                                                                  BigDecimal.ZERO,
                                                                                  inFIXVersion);
                            if(inFIXVersion.compareTo(FIXVersion.FIX42) > 0) {
                                fixReplaceReport.setChar(ExecType.FIELD,
                                                         ExecType.REPLACE);
                            }
                            fixReplaceReport.setString(OrigClOrdID.FIELD,
                                                       orderToReplace.getValue());
                            ExecutionReport replaceReport = org.marketcetera.trade.Factory.getInstance().createExecutionReport(fixReplaceReport,
                                                                                                                               new BrokerID("broker"),
                                                                                                                               Originator.Broker,
                                                                                                                               null,
                                                                                                                               null);
                            executionReportsToSend.add(replaceReport);
                            executedReplaceTest = true;
                        }
                        continue;
                    default :
                        fail("Expand this switch statement to include the new OrderRetentionAction");
                }
            }
        }
        assertFalse("There was no open order to cancel - make sure that the test is structured such that at least one open order is created to be canceled",
                    attemptedCancelTest && !executedCancelTest);
        assertFalse("There was no open order to replace - make sure that the test is structured such that at least one open order is created to be replaced",
                    attemptedReplaceTest && !executedReplaceTest);
        // transmit all the execution reports for this test execution
        for(ExecutionReport executionReport : executionReportsToSend) {
            strategy.dataReceived(executionReport);
        }
        // stop the strategy
        stopStrategy(theStrategy);
        // measure the results
        // check the retained order ids against the expecteds
        CollectionAssert.assertArrayPermutation(expectedRetainedOrderIDs.toArray(),
                                                runningStrategy.getSubmittedOrderIDs().toArray());
        // verify all the execution reports were received
        assertEquals(String.valueOf(executionReportsToSend.size()),
                     AbstractRunningStrategy.getProperty("executionReportCounter"));
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
        Set<String> allCallbacks = new HashSet<String>(Arrays.asList(new String[] { "onAsk", "onBid", "onCancel", "onExecutionReport", "onTrade", "onOther", "onDividend" }));
        for(String callback : inCallbacksThatShouldHaveSucceeded) {
            verifyPropertyNonNull(callback);
            allCallbacks.remove(callback);
        }
        for(String callbackShouldBeNull : allCallbacks) {
            verifyPropertyNull(callbackShouldBeNull);
        }
    }
    /**
     * Executes one iteration of the getPositionAsOf test. 
     *
     * @param inSymbol a <code>String</code> value
     * @param inDate a <code>Date</code> value
     * @param inExpectedPosition a <code>BigDecimal</code> value
     * @throws Exception if an unexpected error occurs
     */
    private void doPositionAsOfTest(String inSymbol,
                                    Date inDate,
                                    BigDecimal inExpectedPosition)
            throws Exception
    {
        StrategyCoordinates strategy = getPositionsStrategy();
        setPropertiesToNull();
        AbstractRunningStrategy.setProperty("positionAsOfDuringStop",
                                            "not-empty");
        if(inSymbol != null) {
            AbstractRunningStrategy.setProperty("symbol",
                                                inSymbol);
        }
        if(inDate != null) {
            AbstractRunningStrategy.setProperty("date",
                                                Long.toString(inDate.getTime()));
        }
        verifyStrategyStartsAndStops(strategy.getName(),
                                     getLanguage(),
                                     strategy.getFile(),
                                     null,
                                     null,
                                     null);
        // verify expected results
        assertEquals((inExpectedPosition == null ? null : inExpectedPosition.toString()),
                     AbstractRunningStrategy.getProperty("positionAsOf"));
        assertNull(AbstractRunningStrategy.getProperty("positionAsOfDuringStop"));
    }
    /**
     * Executes one iteration of the <code>getAllPositionsAsOf</code> test. 
     *
     * @param inDate a <code>Date</code> value
     * @param inExpectedPositions a <code>Map&lt;PositionKey&lt;Equity&gt;,BigDecimal&gt;</code> value
     * @throws Exception if an unexpected error occurs
     */
    private void doAllPositionsAsOfTest(Date inDate,
                                        Map<PositionKey<Equity>,BigDecimal> inExpectedPositions)
            throws Exception
    {
        StrategyCoordinates strategy = getPositionsStrategy();
        setPropertiesToNull();
        AbstractRunningStrategy.setProperty("allPositionsAsOfDuringStop",
                                            "not-empty");
        if(inDate != null) {
            AbstractRunningStrategy.setProperty("date",
                                                Long.toString(inDate.getTime()));
        }
        verifyStrategyStartsAndStops(strategy.getName(),
                                     getLanguage(),
                                     strategy.getFile(),
                                     null,
                                     null,
                                     null);
        // verify expected results
        assertEquals((inExpectedPositions == null ? null : inExpectedPositions.toString()),
                     AbstractRunningStrategy.getProperty("allPositionsAsOf"));
        assertNull(AbstractRunningStrategy.getProperty("allPositionsAsOfDuringStop"));
    }
    /**
     * Executes a single iteration of the <code>getOptionPositionAsOf</code> test.
     *
     * @param inOptionRoot a <code>String</code> value
     * @param inExpiry a <code>String</code> value
     * @param inStrikePrice a <code>BigDecimal</code> value
     * @param inOptionType an <code>OptionType</code> value
     * @param inDate a <code>Date</code> value
     * @param inExpectedPosition a <code>BigDecimal</code> value
     * @throws Exception if an unexpected error occurs
     */
    private void doOptionPositionAsOfTest(String inOptionRoot,
                                          String inExpiry,
                                          BigDecimal inStrikePrice,
                                          OptionType inOptionType,
                                          Date inDate,
                                          BigDecimal inExpectedPosition)
            throws Exception
    {
        StrategyCoordinates strategy = getPositionsStrategy();
        setPropertiesToNull();
        AbstractRunningStrategy.setProperty("optionPositionAsOfDuringStop",
                                            "not-empty");
        if(inOptionRoot != null) {
            AbstractRunningStrategy.setProperty("optionRoot",
                                                inOptionRoot);
        }
        if(inExpiry != null) {
            AbstractRunningStrategy.setProperty("expiry",
                                                inExpiry);
        }
        if(inStrikePrice != null) {
            AbstractRunningStrategy.setProperty("strikePrice",
                                                inStrikePrice.toPlainString());
        }
        if(inOptionType != null) {
            AbstractRunningStrategy.setProperty("optionType",
                                                inOptionType.toString());
        }
        if(inDate != null) {
            AbstractRunningStrategy.setProperty("date",
                                                Long.toString(inDate.getTime()));
        }
        verifyStrategyStartsAndStops(strategy.getName(),
                                     getLanguage(),
                                     strategy.getFile(),
                                     null,
                                     null,
                                     null);
        // verify expected results
        assertEquals((inExpectedPosition == null ? null : inExpectedPosition.toString()),
                     AbstractRunningStrategy.getProperty("optionPositionAsOf"));
        assertNull(AbstractRunningStrategy.getProperty("optionPositionAsOfDuringStop"));
    }
    /**
     * Executes one iteration of the <code>getAllOptionPositionsAsOf</code> test.
     *
     * @param inDate a <code>Date</code> value
     * @param inExpectedPositions a <code>Map&lt;PositionKey&lt;Option&gt;,BigDecimal&gt;</code> value
     * @throws Exception if an unexpected error occurs
     */
    private void doAllOptionPositionsAsOfTest(Date inDate,
                                              Map<PositionKey<Option>,BigDecimal> inExpectedPositions)
            throws Exception
    {
        StrategyCoordinates strategy = getPositionsStrategy();
        setPropertiesToNull();
        AbstractRunningStrategy.setProperty("allOptionPositionsAsOfDuringStop",
                                            "not-empty");
        if(inDate != null) {
            AbstractRunningStrategy.setProperty("date",
                                                Long.toString(inDate.getTime()));
        }
        verifyStrategyStartsAndStops(strategy.getName(),
                                     getLanguage(),
                                     strategy.getFile(),
                                     null,
                                     null,
                                     null);
        // verify expected results
        assertEquals((inExpectedPositions == null ? null : inExpectedPositions.toString()),
                     AbstractRunningStrategy.getProperty("allOptionPositionsAsOf"));
        assertNull(AbstractRunningStrategy.getProperty("allOptionPositionsAsOfDuringStop"));
    }
    /**
     * Executes one iteration of the <code>getOptionPositionsAsOf</code> test. 
     *
     * @param inOptionRoots a <code>String[]</code> value
     * @param inDate a <code>Date</code> value
     * @param inParameters a <code>Properties</code> value to use as parameters if non-null
     * @param inExpectedPositions a <code>Map&lt;PositionKey&lt;Option&gt;,BigDecimal&gt;</code> value
     * @throws Exception if an unexpected error occurs
     */
    private void doOptionPositionsAsOfTest(String[] inOptionRoots,
                                           Date inDate,
                                           Properties inParameters,
                                           Map<PositionKey<Option>,BigDecimal> inExpectedPositions)
            throws Exception
    {
        StrategyCoordinates strategy = getPositionsStrategy();
        setPropertiesToNull();
        AbstractRunningStrategy.setProperty("optionPositionsAsOfDuringStop",
                                            "not-empty");
        if(inOptionRoots != null &&
           inOptionRoots.length > 0) {
            StringBuilder builder = new StringBuilder();
            for(String optionRoot : inOptionRoots) {
                builder.append(optionRoot).append(',');
            }
            AbstractRunningStrategy.setProperty("optionRoots",
                                                builder.toString());
        }
        if(inDate != null) {
            AbstractRunningStrategy.setProperty("date",
                                                Long.toString(inDate.getTime()));
        }
        verifyStrategyStartsAndStops(strategy.getName(),
                                     getLanguage(),
                                     strategy.getFile(),
                                     inParameters,
                                     null,
                                     null);
        // verify expected results
        assertEquals((inExpectedPositions == null ? null : inExpectedPositions.toString()),
                     AbstractRunningStrategy.getProperty("optionPositionsAsOf"));
        assertNull(AbstractRunningStrategy.getProperty("optionPositionsAsOfDuringStop"));
    }
    /**
     * Executes one iteration of the <code>getUnderlying</code> test.
     *
     * @param inOptionRoot a <code>String</code> value
     * @param inExpectedUnderlyingSymbol a <code>String</code> value
     * @throws Exception if an unexpected error occurs
     */
    private void doUnderlyingTest(String inOptionRoot,
                                  String inExpectedUnderlyingSymbol)
            throws Exception
    {
        StrategyCoordinates strategy = getPositionsStrategy();
        setPropertiesToNull();
        AbstractRunningStrategy.setProperty("underlyingDuringStop",
                                            "not-empty");
        if(inOptionRoot != null) {
            AbstractRunningStrategy.setProperty("optionRoot",
                                                inOptionRoot);
        }
        verifyStrategyStartsAndStops(strategy.getName(),
                                     getLanguage(),
                                     strategy.getFile(),
                                     null,
                                     null,
                                     null);
        // verify expected results
        assertEquals((inExpectedUnderlyingSymbol == null ? null : inExpectedUnderlyingSymbol),
                     AbstractRunningStrategy.getProperty("underlying"));
        assertNull(AbstractRunningStrategy.getProperty("underlyingDuringStop"));
    }
    /**
     * Executes one iteration of the <code>getOptionRoots</code> test. 
     *
     * @param inUnderlyingSymbol a <code>String</code> value
     * @param inExpectedOptionRoots a <code>Collection&lt;String&gt;</code> value
     * @throws Exception if an unexpected error occurs
     */
    private void doOptionRootsTest(String inUnderlyingSymbol,
                                   Collection<String> inExpectedOptionRoots)
            throws Exception
    {
        StrategyCoordinates strategy = getPositionsStrategy();
        setPropertiesToNull();
        AbstractRunningStrategy.setProperty("optionRootsDuringStop",
                                            "not-empty");
        if(inUnderlyingSymbol != null) {
            AbstractRunningStrategy.setProperty("underlyingSymbol",
                                                inUnderlyingSymbol);
        }
        verifyStrategyStartsAndStops(strategy.getName(),
                                     getLanguage(),
                                     strategy.getFile(),
                                     null,
                                     null,
                                     null);
        // verify expected results
        assertEquals((inExpectedOptionRoots == null ? null : inExpectedOptionRoots.toString()),
                     AbstractRunningStrategy.getProperty("optionRoots"));
        assertNull(AbstractRunningStrategy.getProperty("optionRootsDuringStop"));
    }
    /**
     * Executes the given block asynchronously.
     * 
     * @param inBlock a <code>Callable&lt;T&gt;</code> value
     * @return a <code>Future&lt;T&gt;</code> value
     * @throws InterruptedException if an error occurs
     * @throws ExecutionException if an error occurs
     */
    private <T> Future<T> doAsynchronous(Callable<T> inBlock)
        throws InterruptedException, ExecutionException
    {
        return executor.submit(inBlock);
    }
    /**
     * used for asynchronous test blocks
     */
    private final ExecutorService executor = Executors.newCachedThreadPool(new NamedThreadFactory("LanguageTestBase"));  //$NON-NLS-1$
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
    /**
     * An action that an <code>ExecutionReport</code> could perform on an open order.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 2.0.0
     */
    private enum OrderRetentionAction
    {
        PARTIALLY_FILL,
        FULLY_FILL,
        CANCEL,
        REPLACE
    }
}
