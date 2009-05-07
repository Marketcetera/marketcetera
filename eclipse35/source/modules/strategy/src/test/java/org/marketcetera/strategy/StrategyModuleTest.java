package org.marketcetera.strategy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.marketcetera.module.Messages.*;
import static org.marketcetera.strategy.Language.RUBY;
import static org.marketcetera.strategy.Status.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;

import javax.management.AttributeChangeNotification;
import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationListener;

import org.junit.Test;
import org.marketcetera.core.Util;
import org.marketcetera.marketdata.MarketDataFeedTestBase;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.bogus.BogusFeedModuleFactory;
import org.marketcetera.module.DataFlowException;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.module.IllegalRequestParameterValue;
import org.marketcetera.module.InvalidURNException;
import org.marketcetera.module.ModuleCreationException;
import org.marketcetera.module.ModuleException;
import org.marketcetera.module.ModuleNotFoundException;
import org.marketcetera.module.ModuleStateException;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.module.SinkModuleFactory;
import org.marketcetera.module.UnsupportedRequestParameterType;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.test.UnicodeData;

/* $License$ */

/**
 * Tests {@link StrategyModule}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.0.0
 */
public class StrategyModuleTest
    extends StrategyTestBase
{
    /**
     * Tests the function of the cancel method.
     *
     * @throws Exception if an error occurs
     */
    @Test 
    public void cancel()
        throws Exception
    {
        // TODO complete this test when order and suggestion creation is added
        // create an external module
        // ask it to subscribe to strategy orders
        // trigger the strategy to produce orders
        // test that it receives orders
        // cancel the request
        // verify that the module is no longer receiving orders even though the strategy is still producing them
        // repeat test with suggestions
    }
    /**
     * Tests starting and stopping of strategies.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void lifecycle()
        throws Exception
    {
        assertTrue(moduleManager.getModuleInstances(StrategyModuleFactory.PROVIDER_URN).isEmpty());
        ModuleURN strategy = moduleManager.createModule(StrategyModuleFactory.PROVIDER_URN,
                                                        "MyStategy",
                                                        RubyLanguageTest.STRATEGY_NAME,
                                                        RUBY,
                                                        RubyLanguageTest.STRATEGY,
                                                        new Properties(),
                                                        false,
                                                        outputURN);
        assertEquals(1,
                     moduleManager.getModuleInstances(StrategyModuleFactory.PROVIDER_URN).size());
        assertEquals(strategy,
                     moduleManager.getModuleInstances(StrategyModuleFactory.PROVIDER_URN).get(0));
        assertFalse(moduleManager.getModuleInfo(strategy).getState().isStarted());
        startStrategy(strategy);
        assertTrue(moduleManager.getModuleInfo(strategy).getState().isStarted());
        stopStrategy(strategy);
        assertEquals(1,
                     moduleManager.getModuleInstances(StrategyModuleFactory.PROVIDER_URN).size());
        assertEquals(strategy,
                     moduleManager.getModuleInstances(StrategyModuleFactory.PROVIDER_URN).get(0));
        moduleManager.deleteModule(strategy);
        assertTrue(moduleManager.getModuleInstances(StrategyModuleFactory.PROVIDER_URN).isEmpty());
    }
    /**
     * Tests the case where after an uncompiling strategy is fixed and restarted, it cannot be
     * stopped if the strategy originally requested orders to be routed to the ORS.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void uncompilingRoutedStrategy()
        throws Exception
    {
        // create a strategy written to a file that does not compile
        String badStrategy = "include_class \"org.marketcetera.strategy.ruby.Strategy\"\n" +
                             "include_class \"java.math.BigDecimal\"\n" +
                             "class MyStrategy < Strategy\n" +
                             "  TEST = BigDecimal.new(1)\n" +
                             "end\n";
        String goodStrategy = "include_class \"org.marketcetera.strategy.ruby.Strategy\"\n" +
                              "include_class \"java.math.BigDecimal\"\n" +
                              "class MyStrategy < Strategy\n" +
                              "  TEST = BigDecimal.new(\"1\")\n" +
                              "end\n";
        // start with the bad strategy
        File strategyFile = File.createTempFile("strategy",
                                                ".rb");
        strategyFile.deleteOnExit();
        BufferedWriter writer = new BufferedWriter(new FileWriter(strategyFile));
        writer.write(badStrategy);
        writer.close();
        final ModuleURN strategyURN = moduleManager.createModule(StrategyModuleFactory.PROVIDER_URN,
                                                                 "MyStategy",
                                                                 "MyStrategy",
                                                                 RUBY,
                                                                 strategyFile,
                                                                 new Properties(),
                                                                 true,
                                                                 outputURN);
        new ExpectedFailure<ModuleException>(null) {
            @Override
            protected void run()
                throws Exception
            {
                moduleManager.start(strategyURN);
            }
        };
        verifyStrategyReady(strategyURN);
        verifyStrategyStatus(strategyURN,
                             FAILED);
        // "correct" the strategy
        writer = new BufferedWriter(new FileWriter(strategyFile));
        writer.write(goodStrategy);
        writer.close();
        // start it again
        moduleManager.start(strategyURN);
        verifyStrategyReady(strategyURN);
        verifyStrategyStatus(strategyURN,
                             RUNNING);
        // stop it
        moduleManager.stop(strategyURN);
        verifyStrategyStopped(strategyURN);
    }
    /**
     * Tests processing of parameters with strategy module construction.
     *
     * @throws Exception if an error occurs
     */
    @Test 
    public void create()
        throws Exception
    {
        doWrongParameterCountTest((Object[])null);
        doWrongParameterCountTest(new Object[0]);
        doWrongParameterCountTest(RubyLanguageTest.STRATEGY_NAME);
        doWrongParameterCountTest(RubyLanguageTest.STRATEGY_NAME,
                                  RUBY);
        doWrongParameterCountTest(RubyLanguageTest.STRATEGY_NAME,
                                  RUBY,
                                  RubyLanguageTest.STRATEGY);
        doWrongParameterCountTest(RubyLanguageTest.STRATEGY_NAME,
                                  RUBY,
                                  RubyLanguageTest.STRATEGY,
                                  new Properties());
        doWrongParameterCountTest(RubyLanguageTest.STRATEGY_NAME,
                                  RUBY,
                                  RubyLanguageTest.STRATEGY,
                                  new Properties(),
                                  "false");
        // muddle types
        doWrongTypeParameterTest(0,
                                 this,
                                 RubyLanguageTest.STRATEGY_NAME,
                                 RUBY,
                                 RubyLanguageTest.STRATEGY,
                                 new Properties(),
                                 false,
                                 outputURN);
        doWrongTypeParameterTest(1,
                                 "MyStrategyURN",
                                 this,
                                 RUBY,
                                 RubyLanguageTest.STRATEGY,
                                 new Properties(),
                                 false,
                                 outputURN);
        doWrongTypeParameterTest(2,
                                 "MyStrategyURN",
                                 RubyLanguageTest.STRATEGY_NAME,
                                 this,
                                 RubyLanguageTest.STRATEGY,
                                 new Properties(),
                                 false,
                                 outputURN);
        doWrongTypeParameterTest(3,
                                 "MyStrategyURN",
                                 RubyLanguageTest.STRATEGY_NAME,
                                 RUBY,
                                 this,
                                 new Properties(),
                                 false,
                                 outputURN);
        doWrongTypeParameterTest(4,
                                 "MyStrategyURN",
                                 RubyLanguageTest.STRATEGY_NAME,
                                 RUBY,
                                 RubyLanguageTest.STRATEGY,
                                 this,
                                 false,
                                 outputURN);
        doWrongTypeParameterTest(5,
                                 "MyStrategyURN",
                                 RubyLanguageTest.STRATEGY_NAME,
                                 RUBY,
                                 RubyLanguageTest.STRATEGY,
                                 new Properties(),
                                 this,
                                 outputURN);
        doWrongTypeParameterTest(6,
                                 "MyStrategyURN",
                                 RubyLanguageTest.STRATEGY_NAME,
                                 RUBY,
                                 RubyLanguageTest.STRATEGY,
                                 new Properties(),
                                 false,
                                 this);
        // create a good 'un just to prove we can
        ModuleURN strategy = createStrategy(RubyLanguageTest.STRATEGY_NAME,
                                            RUBY,
                                            RubyLanguageTest.STRATEGY,
                                            new Properties(),
                                            false,
                                            outputURN);
        assertEquals(1,
                     moduleManager.getModuleInstances(StrategyModuleFactory.PROVIDER_URN).size());
        assertEquals(strategy,
                     moduleManager.getModuleInstances(StrategyModuleFactory.PROVIDER_URN).get(0));
    }
    /**
     * Tests processing of data requests.
     *
     * @throws Exception if an error occurs
     */
    @Test 
    public void requestData()
        throws Exception
    {
        ModuleURN strategy = createStrategy(RubyLanguageTest.STRATEGY_NAME,
                                            RUBY,
                                            RubyLanguageTest.STRATEGY,
                                            new Properties(),
                                            false,
                                            outputURN);
        assertTrue(moduleManager.getModuleInfo(strategy).getState().isStarted());
        // try some badly formed requests
        // null payload
        new ExpectedFailure<IllegalRequestParameterValue>(ILLEGAL_REQ_PARM_VALUE,
                                                          strategy.toString(),
                                                          null) {
            @Override
            protected void run()
                throws Exception
            {
                moduleManager.createDataFlow(new DataRequest[] { new DataRequest(StrategyModuleFactory.PROVIDER_URN,
                                                                                 null) });
            }
        };
        // String payload, but the wrong contents
        final String badPayload = "some stuff-" + System.nanoTime();
        new ExpectedFailure<IllegalRequestParameterValue>(ILLEGAL_REQ_PARM_VALUE,
                                                          strategy.toString(),
                                                          badPayload) {
            @Override
            protected void run()
                throws Exception
            {
                moduleManager.createDataFlow(new DataRequest[] { new DataRequest(StrategyModuleFactory.PROVIDER_URN,
                                                                                 badPayload) });
            }
        };
        // wrong payload type
        new ExpectedFailure<UnsupportedRequestParameterType>(UNSUPPORTED_REQ_PARM_TYPE,
                                                             strategy.toString(),
                                                             moduleManager.getClass().getName()) {
            @Override
            protected void run()
                throws Exception
            {
                moduleManager.createDataFlow(new DataRequest[] { new DataRequest(StrategyModuleFactory.PROVIDER_URN,
                                                                                 moduleManager) });
            }
        };
        // correct String payload
        DataFlowID flowID1 = moduleManager.createDataFlow(new DataRequest[] { new DataRequest(StrategyModuleFactory.PROVIDER_URN,
                                                                                              "OrDeRs") });
        DataFlowID flowID2 = moduleManager.createDataFlow(new DataRequest[] { new DataRequest(StrategyModuleFactory.PROVIDER_URN,
                                                                                              "SuGgEsTiOnS") });
        DataFlowID flowID3 = moduleManager.createDataFlow(new DataRequest[] { new DataRequest(StrategyModuleFactory.PROVIDER_URN,
                                                                                              "eVeNtS") });
        DataFlowID flowID4 = moduleManager.createDataFlow(new DataRequest[] { new DataRequest(StrategyModuleFactory.PROVIDER_URN,
                                                                                              "NoTiFiCaTiOnS") });
        DataFlowID flowID5 = moduleManager.createDataFlow(new DataRequest[] { new DataRequest(StrategyModuleFactory.PROVIDER_URN,
                                                                                              "LoG") });
        DataFlowID flowID6 = moduleManager.createDataFlow(new DataRequest[] { new DataRequest(StrategyModuleFactory.PROVIDER_URN,
                                                                                              "AlL") });
        // correct RequestType payload
        DataFlowID flowID7 = moduleManager.createDataFlow(new DataRequest[] { new DataRequest(StrategyModuleFactory.PROVIDER_URN,
                                                                                              OutputType.ORDERS) });
        DataFlowID flowID8 = moduleManager.createDataFlow(new DataRequest[] { new DataRequest(StrategyModuleFactory.PROVIDER_URN,
                                                                                              OutputType.SUGGESTIONS) });
        DataFlowID flowID9 = moduleManager.createDataFlow(new DataRequest[] { new DataRequest(StrategyModuleFactory.PROVIDER_URN,
                                                                                              OutputType.EVENTS) });
        DataFlowID flowID10 = moduleManager.createDataFlow(new DataRequest[] { new DataRequest(StrategyModuleFactory.PROVIDER_URN,
                                                                                              OutputType.NOTIFICATIONS) });
        DataFlowID flowID11 = moduleManager.createDataFlow(new DataRequest[] { new DataRequest(StrategyModuleFactory.PROVIDER_URN,
                                                                                               OutputType.LOG) });
        DataFlowID flowID12 = moduleManager.createDataFlow(new DataRequest[] { new DataRequest(StrategyModuleFactory.PROVIDER_URN,
                                                                                              OutputType.ALL) });
        // TODO insert some code here to:
        //  1) have a test module make the above data requests
        //  2) have the strategy emit a trade suggestion and an order (verified in the module described in #1)
        moduleManager.cancel(flowID1);
        moduleManager.cancel(flowID2);
        moduleManager.cancel(flowID3);
        moduleManager.cancel(flowID4);
        moduleManager.cancel(flowID5);
        moduleManager.cancel(flowID6);
        moduleManager.cancel(flowID7);
        moduleManager.cancel(flowID8);
        moduleManager.cancel(flowID9);
        moduleManager.cancel(flowID10);
        moduleManager.cancel(flowID11);
        moduleManager.cancel(flowID12);
    }
    @Test 
    public void receiveData()
        throws Exception
    {
        // set up a strategy and plumb it externally with a market data provider
        ModuleURN strategy = createStrategy(RubyLanguageTest.STRATEGY_NAME,
                                            RUBY,
                                            RubyLanguageTest.STRATEGY,
                                            new Properties(),
                                            false,
                                            outputURN);
        // plumb the market data provider externally to the strategy module - normally, this would be done internally, but, for this test,
        //  it is sufficient that the data is flowing, it doesn't matter how it gets there
        DataFlowID dataFlowID = moduleManager.createDataFlow(new DataRequest[] { new DataRequest(BogusFeedModuleFactory.PROVIDER_URN,
                                                                                                 MarketDataRequest.newRequest().fromExchange("Exchange").withSymbols("GOOG")),
                                                                                 new DataRequest(strategy) },
                                                             false);
        // TODO when the strategy services come on-line, use them to measure the data coming in, for now, take a little nap and let the data flow
        Thread.sleep(5000);
        // stop the data flow
        moduleManager.cancel(dataFlowID);
    }
    /**
     * Tests passing null as each parameter.
     *
     * @throws Exception if an error occurs
     */
    @Test 
    public void nullParameterTest()
        throws Exception
    {
        final int[] index = new int[1];
        for(index[0]=0;index[0]<6;index[0]++) {
            // parameters 1, 5, 6, and 7 are optional, so nulls are allowed
            if(index[0] == 0 ||
               index[0] == 4 ||
               index[0] == 5 ||
               index[0] == 6) {
                verifyStrategyStartsAndStops((index[0]==0 ? null : "MyStrategy"),
                                             (index[0]==1 ? null : RubyLanguageTest.STRATEGY_NAME),
                                             (index[0]==2 ? null : RUBY),
                                             (index[0]==3 ? null : RubyLanguageTest.STRATEGY),
                                             (index[0]==4 ? null : new Properties()),
                                             (index[0]==5 ? null : false),
                                             (index[0]==6 ? null : outputURN));
            } else {
                new ExpectedFailure<ModuleCreationException>(NULL_PARAMETER_ERROR,
                                                             index[0] + 1,
                                                             expectedTypes[index[0]].getName()) {
                    @Override
                    protected void run()
                        throws Exception
                    {
                        verifyStrategyStartsAndStops((index[0]==0 ? null : "MyStrategy"),
                                                     (index[0]==1 ? null : RubyLanguageTest.STRATEGY_NAME),
                                                     (index[0]==2 ? null : RUBY),
                                                     (index[0]==3 ? null : RubyLanguageTest.STRATEGY),
                                                     (index[0]==4 ? null : new Properties()),
                                                     (index[0]==5 ? null : false),
                                                     (index[0]==6 ? null : outputURN));
                    }
                };
            }
        }
    }
    /**
     * Tests permutations of an instance name.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void instanceParameterTest()
        throws Exception
    {
        final String emptyInstance = "";
        new ExpectedFailure<ModuleCreationException>(EMPTY_INSTANCE_ERROR) {
            @Override
            protected void run()
                throws Exception
            {
                verifyStrategyStartsAndStops(emptyInstance,
                                             RubyLanguageTest.STRATEGY_NAME,
                                             RUBY,
                                             RubyLanguageTest.STRATEGY,
                                             null,
                                             null,
                                             null);
            }
        };
        verifyStrategyStartsAndStops("MyStrategyInstance",
                                     RubyLanguageTest.STRATEGY_NAME,
                                     RUBY,
                                     RubyLanguageTest.STRATEGY,
                                     null,
                                     null,
                                     null);
    }
    /**
     * Tests permutations of strategy name.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void nameParameterTest()
        throws Exception
    {
        final String emptyName = "";
        new ExpectedFailure<ModuleCreationException>(EMPTY_NAME_ERROR) {
            @Override
            protected void run()
                throws Exception
            {
                verifyStrategyStartsAndStops(emptyName,
                                             RUBY,
                                             RubyLanguageTest.STRATEGY,
                                             null,
                                             null,
                                             null);
            }
        };
        verifyStrategyStartsAndStops(RubyLanguageTest.STRATEGY_NAME,
                                     RUBY,
                                     RubyLanguageTest.STRATEGY,
                                     null,
                                     null,
                                     null);
    }
    /**
     * Tests various ways to specify a strategy language.
     *
     * @throws Exception if an error occurs
     */
    @Test 
    public void languageParameterTest()
        throws Exception
    {
        final String invalidLanguage = "Language-" + System.currentTimeMillis();
        String validLanguage = RUBY.toString();
        String validMixedCaseLanguage = "RuBy";
        new ExpectedFailure<ModuleCreationException>(INVALID_LANGUAGE_ERROR,
                                                     invalidLanguage) {
            @Override
            protected void run()
                throws Exception
            {
                verifyStrategyStartsAndStops(RubyLanguageTest.STRATEGY_NAME,
                                             invalidLanguage,
                                             RubyLanguageTest.STRATEGY,
                                             null,
                                             null,
                                             null);
            }
        };
        verifyStrategyStartsAndStops(RubyLanguageTest.STRATEGY_NAME,
                                     validLanguage,
                                     RubyLanguageTest.STRATEGY,
                                     null,
                                     null,
                                     null);
        // test again with a mixed case string
        verifyStrategyStartsAndStops(RubyLanguageTest.STRATEGY_NAME,
                                     validMixedCaseLanguage,
                                     RubyLanguageTest.STRATEGY,
                                     null,
                                     null,
                                     null);
    }
    /**
     * Tests permutations of files passed as strategy source. 
     *
     * @throws Exception if error occurs
     */
    @Test 
    public void fileParameterTest()
        throws Exception
    {
        // error conditions here are a non-existent file and a non-readable file
        // non-existent file is easy, but non-readable is kinda platform-dependent (exists but cannot be read)
        final File badFile = new File("this-file-really-should-not-exist-" + System.nanoTime());
        new ExpectedFailure<ModuleCreationException>(FILE_DOES_NOT_EXIST_OR_IS_NOT_READABLE,
                                                     badFile.getAbsolutePath()) {
            @Override
            protected void run()
                throws Exception
            {
                verifyStrategyStartsAndStops(RubyLanguageTest.STRATEGY_NAME,
                                             RUBY,
                                             badFile,
                                             null,
                                             null,
                                             null);
            }
        };
    }
    /**
     * Tests permutations of properties parameter.
     *
     * @throws Exception if error occurs
     */
    @Test
    public void propertiesParameterTest()
        throws Exception
    {
        Properties properties = new Properties();
        // empty properties
        verifyStrategyStartsAndStops(RubyLanguageTest.STRATEGY_NAME,
                                     RUBY,
                                     RubyLanguageTest.STRATEGY,
                                     properties,
                                     null,
                                     null);
        // non-empty properties
        properties.setProperty("some-key",
                               "some value " + System.nanoTime());
        verifyStrategyStartsAndStops(RubyLanguageTest.STRATEGY_NAME,
                                     RUBY,
                                     RubyLanguageTest.STRATEGY,
                                     properties,
                                     null,
                                     null);
    }
    /**
     * Tests various error states of the URN parameters.
     *
     * @throws Exception if error occurs
     */
    @Test 
    public void urnParameterTest()
        throws Exception
    {
        // invalid URN
        final ModuleURN invalidURN = new ModuleURN("metc:something:something");
        // valid URN, but not started
        final ModuleURN validUnstartedURN = moduleManager.createModule(MockRecorderModule.Factory.PROVIDER_URN);
        // valid URN, started, but not receiver
        final ModuleURN validURNNotReceiver = BogusFeedModuleFactory.INSTANCE_URN;
        assertFalse(moduleManager.getModuleInfo(BogusFeedModuleFactory.INSTANCE_URN).isReceiver());
        // test the above URNs for orders
        // first, invalid URN
        new ExpectedFailure<ModuleNotFoundException>(MODULE_NOT_FOUND,
                                                     invalidURN.toString()) {
            @Override
            protected void run()
                throws Exception
            {
                verifyStrategyStartsAndStops(RubyLanguageTest.STRATEGY_NAME,
                                             RUBY,
                                             RubyLanguageTest.STRATEGY,
                                             null,
                                             null,
                                             invalidURN);
            }
        };
        // next, valid, unstarted URN
        new ExpectedFailure<ModuleStateException>(DATAFLOW_FAILED_PCPT_MODULE_STATE_INCORRECT,
                                                  validUnstartedURN.toString(), ExpectedFailure.IGNORE, ExpectedFailure.IGNORE) {
            @Override
            protected void run()
                throws Exception
            {
                verifyStrategyStartsAndStops(RubyLanguageTest.STRATEGY_NAME,
                                             RUBY,
                                             RubyLanguageTest.STRATEGY,
                                             null,
                                             null,
                                             validUnstartedURN);
            }
        };
        // last, valid, started URN, but not a data-receiver
        new ExpectedFailure<DataFlowException>(MODULE_NOT_RECEIVER,
                                               validURNNotReceiver.toString()) {
            @Override
            protected void run()
                throws Exception
            {
                verifyStrategyStartsAndStops(RubyLanguageTest.STRATEGY_NAME,
                                             RUBY,
                                             RubyLanguageTest.STRATEGY,
                                             null,
                                             null,
                                             validURNNotReceiver);
            }
        };
    }
    /**
     * Tests the ability to set strategy attributes with its MXBean interface.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void mxBean()
        throws Exception
    {
        // create a valid, stopped URN
        ModuleURN stoppedURN = moduleManager.createModule(MockRecorderModule.Factory.PROVIDER_URN);
        assertFalse(moduleManager.getModuleInfo(stoppedURN).getState().isStarted());
        // create our test data for the values to pass to the setDestination MXBean setters (suggestion and order)
        //  the values are null, invalid, stopped, empty, and started
        final String[] urnStrings = new String[] { null, "this is not a URN", stoppedURN.getValue(), "", outputURN.getValue() };
        // create the test data for the starting point for the strategy module
        //  the values are null and started (no stopped because then the strategy module itself would be un-startable before we even got around to
        //  testing the setters - no point in that
        final ModuleURN[] urns = new ModuleURN[] { null, outputURN };
        // create parameters test data
        // empty
        Properties emptyProperties = new Properties();
        // non-empty
        Properties nonEmptyProperties = new Properties();
        nonEmptyProperties.setProperty("key1",
                                       "value1");
        // non-ascii
        Properties nonASCIIProperties = new Properties();
        nonASCIIProperties.setProperty("key1",
                                       UnicodeData.HELLO_GR);
        // group the parameters together
        final String[] parameterStrings = new String[] { null, "ab:c=d::ef:", Util.propertiesToString(emptyProperties),
                                                         Util.propertiesToString(nonEmptyProperties),Util.propertiesToString(nonASCIIProperties) };
        final Properties[] parameters = new Properties[] { null, emptyProperties, nonEmptyProperties, nonASCIIProperties };
        // cycle through all the permutations for the starting point for outputs (2 values) and the value to set the destinations to
        //  (5 values) and new parameters (4 values) and parameters starting point (3 values), and route or not route (2 values) and change to route or not route (2 values),
        // for a total of 480 test cases (2*5*4*3*2*2) while this bit of code may not be the most legible, it's easy to see that 480 test 
        //  conditions would be a fair bit more verbose
        for(int urnStringIndex=0;urnStringIndex<urnStrings.length;urnStringIndex++) {
            for(int urnIndex=0;urnIndex<urns.length;urnIndex++) {
                for(int parameterStringIndex=0;parameterStringIndex<parameterStrings.length;parameterStringIndex++) {
                    for(int parameterIndex=0;parameterIndex<parameters.length;parameterIndex++) {
                        for(int startRoutingIndex=0;startRoutingIndex<=1;startRoutingIndex++) {
                            for(int changeRoutingIndex=0;changeRoutingIndex<=1;changeRoutingIndex++) {
                                final int urnStringCounter = urnStringIndex;
                                final int urnCounter = urnIndex;
                                final int parmaterStringCounter = parameterStringIndex;
                                final int parameterCounter = parameterIndex;
                                final int startRoutingCounter = startRoutingIndex;
                                final int changeRoutingCounter = changeRoutingIndex;
                                SLF4JLoggerProxy.debug(this,
                                                       "Testing permutation: {} {} {} {} {} {}",
                                                       urnStringIndex,urnIndex,parameterStringIndex,parameterIndex,startRoutingIndex,changeRoutingIndex);
                                if(urnStringIndex == 1) { // invalid URN
                                    new ExpectedFailure<InvalidURNException>(INVALID_URN_SCHEME) {
                                        @Override
                                        protected void run()
                                            throws Exception
                                        {
                                            doOneMXInterfaceTest(urnStrings[urnStringCounter],
                                                                 urns[urnCounter],
                                                                 parameterStrings[parmaterStringCounter],
                                                                 parameters[parameterCounter],
                                                                 (startRoutingCounter == 0 ? false : true),
                                                                 (changeRoutingCounter == 0 ? false : true));
                                        }
                                    };
                                    continue;
                                }
                                if(urnStringIndex == 2) { // stopped URN
                                    new ExpectedFailure<ModuleStateException>(DATAFLOW_FAILED_PCPT_MODULE_STATE_INCORRECT) {
                                        @Override
                                        protected void run()
                                            throws Exception
                                        {
                                            doOneMXInterfaceTest(urnStrings[urnStringCounter],
                                                                 urns[urnCounter],
                                                                 parameterStrings[parmaterStringCounter],
                                                                 parameters[parameterCounter],
                                                                 (startRoutingCounter == 0 ? false : true),
                                                                 (changeRoutingCounter == 0 ? false : true));
                                        }
                                    };
                                    continue;
                                }
                                doOneMXInterfaceTest(urnStrings[urnStringIndex],
                                                     urns[urnIndex],
                                                     parameterStrings[parmaterStringCounter],
                                                     parameters[parameterCounter],
                                                     (startRoutingCounter == 0 ? false : true),
                                                     (changeRoutingCounter == 0 ? false : true));
                            }
                        }
                    }
                }
            }
        }
    }
    /**
     * Tests what happens when a module which is not a data-emitter is passed as an OrdersURN.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void ordersNonEmitter()
        throws Exception
    {
        ModuleURN dataSink = SinkModuleFactory.INSTANCE_URN;
        assertTrue(moduleManager.getModuleInfo(dataSink).getState().isStarted());
        assertFalse(moduleManager.getModuleInfo(dataSink).isEmitter());
        ModuleURN strategy = moduleManager.createModule(StrategyModuleFactory.PROVIDER_URN,
                                                        "MyStrategy",
                                                        RubyLanguageTest.STRATEGY_NAME,
                                                        RUBY,
                                                        RubyLanguageTest.STRATEGY,
                                                        null,
                                                        null,
                                                        dataSink);
        startStrategy(strategy);
        assertTrue(moduleManager.getModuleInfo(strategy).getState().isStarted());
        stopStrategy(strategy);
        moduleManager.deleteModule(strategy);
    }
    /**
     * Tests what happens if duplicate instance names are specified.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void duplicateInstanceNames()
        throws Exception
    {
        ModuleURN strategy1 = moduleManager.createModule(StrategyModuleFactory.PROVIDER_URN,
                                                         "MyNewStrategy",
                                                         RubyLanguageTest.STRATEGY_NAME,
                                                         RUBY,
                                                         RubyLanguageTest.STRATEGY,
                                                         null,
                                                         null,
                                                         null);
        // try to create a strategy with the same specified instance name
        new ExpectedFailure<ModuleCreationException>(DUPLICATE_MODULE_URN,
                                                     "metc:strategy:system:MyNewStrategy") {
            @Override
            protected void run()
                throws Exception
            {
                moduleManager.createModule(StrategyModuleFactory.PROVIDER_URN,
                                           "MyNewStrategy",
                                           RubyLanguageTest.STRATEGY_NAME,
                                           RUBY,
                                           RubyLanguageTest.STRATEGY,
                                           null,
                                           null,
                                           null);
            }
        };
        // delete the first one
        moduleManager.deleteModule(strategy1);
        // now it can be created again with the same name
        strategy1 = moduleManager.createModule(StrategyModuleFactory.PROVIDER_URN,
                                               "MyNewStrategy",
                                               RubyLanguageTest.STRATEGY_NAME,
                                               RUBY,
                                               RubyLanguageTest.STRATEGY,
                                               null,
                                               null,
                                               null);
        moduleManager.deleteModule(strategy1);
    }
    /**
     * Tests that starting or stopping a strategy succeeds or fails depending on the strategy state.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void strategyStateChanges()
        throws Exception
    {
        final Properties parameters = new Properties();
        parameters.setProperty("shouldLoopOnStart",
                               "true");
        parameters.setProperty("shouldLoopOnStop",
                               "true");
        verifyPropertyNull("loopDone");
        verifyPropertyNull("onStartBegins");
        // need to manually start the strategy because it will be in "STARTING" status for a long long time
        final ModuleURN strategyURN = createModule(StrategyModuleFactory.PROVIDER_URN,
                                                   null,
                                                   RubyLanguageTest.STRATEGY_NAME,
                                                   RUBY,
                                                   RubyLanguageTest.STRATEGY,
                                                   parameters,
                                                   null,
                                                   null);
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
        // strategy is now looping
        // reset start counter
        AbstractRunningStrategy.setProperty("onStartBegins",
                                            null);
        // test to see what happens if the strategy is started again by the moduleManager
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
        // release the running strategy (or it will keep running beyond the end of the test)
        AbstractRunningStrategy.setProperty("shouldStopLoop",
                                            "true");
        // wait for the strategy to become ready
        verifyStrategyReady(strategyURN);
        StrategyImpl strategy = getRunningStrategy(strategyURN);
        verifyStrategyStatus(strategyURN,
                             RUNNING);
        // try to start again
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
        // test stopping
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
        // test starting
        new ExpectedFailure<ModuleStateException>(STRATEGY_STILL_RUNNING,
                                                  strategy.toString(),
                                                  strategy.getStatus()) {
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
        // now the strategy can start again
        moduleManager.start(strategyURN);
        verifyStrategyReady(strategyURN);
    }
    /**
     * Tests strategy status changes through the strategy lifecycle.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void statusNotification()
        throws Exception
    {
        // create a strategy (but don't start it yet)
        ModuleURN strategyURN = moduleManager.createModule(StrategyModuleFactory.PROVIDER_URN,
                                                           "MyStategy",
                                                           RubyLanguageTest.STRATEGY_NAME,
                                                           RUBY,
                                                           RubyLanguageTest.STRATEGY,
                                                           null,
                                                           null,
                                                           null);
        // not started yet
        assertFalse(moduleManager.getModuleInfo(strategyURN).getState().isStarted());
        // can get the JMX interface for an unstarted strategy
        StrategyMXBean strategyInterface = getMXProxy(strategyURN);
        final List<String> statusChanges = new ArrayList<String>();
        // add a subscriber
        NotificationListener subscriber = new NotificationListener() {
            @Override
            public void handleNotification(Notification inNotification,
                                           Object inHandback)
            {
                if(inNotification instanceof AttributeChangeNotification) {
                    AttributeChangeNotification change = (AttributeChangeNotification)inNotification;
                    statusChanges.add(change.getOldValue() + "->" + change.getNewValue());
                }
            }};
        ((NotificationEmitter)strategyInterface).addNotificationListener(subscriber,
                                                                         null,
                                                                         null);
        assertTrue(statusChanges.isEmpty());
        // start the module
        moduleManager.start(strategyURN);
        verifyStrategyReady(strategyURN);
        // verify that the strategy changes were received (UNSTARTED->COMPILING, COMPILING->STARTING, STARTING->RUNNING)
        assertEquals(3,
                     statusChanges.size());
        assertEquals("UNSTARTED->COMPILING",
                     statusChanges.get(0));
        assertEquals("COMPILING->STARTING",
                     statusChanges.get(1));
        assertEquals("STARTING->RUNNING",
                     statusChanges.get(2));
        // disconnect the listener
        ((NotificationEmitter)strategyInterface).removeNotificationListener(subscriber);
        // empty the list
        statusChanges.clear();
        moduleManager.stop(strategyURN);
        // make sure the change list hasn't grown
        assertTrue(statusChanges.isEmpty());
    }
    /**
     * Executes a single permutation of a strategy attribute get/set test.
     * 
     * @param inOutputDestination a <code>String</code> value or null
     * @param inOutputStart a <code>ModuleURN</code> value or null
     * @param inNewParameters a <code>String</code> value containing a properly formatted properties string or null
     * @param inStartingParameters a <code>Properties</code> value containing the starting parameters value or null
     * @param inStartingRouteToORS a <code>boolean</code> value indicating whether the strategy should initially route to the ORS or not
     * @param inNewRouting a <code>boolean</code> value indicating whether the strategy should be changed to route to the ORS or not
     * @return a <code>ModuleURN</code> value containing the strategy module guaranteed to be started
     * @throws Exception if the strategy module could not be started or another error occurs
     */
    private ModuleURN doOneMXInterfaceTest(String inOutputDestination,
                                           ModuleURN inOutputStart,
                                           String inNewParameters,
                                           Properties inStartingParameters,
                                           boolean inStartingRouteToORS,
                                           boolean inNewRouting)
        throws Exception
    {
        ModuleURN strategy = createStrategy(RubyLanguageTest.STRATEGY_NAME,
                                            RUBY,
                                            RubyLanguageTest.STRATEGY,
                                            inStartingParameters,
                                            inStartingRouteToORS,
                                            inOutputStart);
        StrategyMXBean mxBeanInterface = getMXProxy(strategy);
        verifyStrategyStatus(strategy,
                             RUNNING);
        if(inOutputStart == null) {
            assertNull(mxBeanInterface.getOutputDestination());
        } else {
            assertEquals(inOutputStart.getValue(),
                         mxBeanInterface.getOutputDestination());
        }
        if(inStartingParameters == null ||
           inStartingParameters.isEmpty()) {
            assertNull(mxBeanInterface.getParameters());
        } else {
            String propertiesString = mxBeanInterface.getParameters();
            Properties actualProperties = Util.propertiesFromString(propertiesString);
            assertEquals(actualProperties,
                         inStartingParameters);
        }
        assertEquals(inStartingRouteToORS,
                     mxBeanInterface.isRoutingOrdersToORS());
        // make the change
        mxBeanInterface.setOutputDestination(inOutputDestination);
        mxBeanInterface.setParameters(inNewParameters);
        mxBeanInterface.setIsRountingOrdersToORS(inNewRouting);
        // test the change
        if(inOutputDestination == null ||
           inOutputDestination.isEmpty()) {
            assertNull(mxBeanInterface.getOutputDestination());
        } else {
            assertEquals(inOutputDestination,
                         mxBeanInterface.getOutputDestination());
        }
        if(inNewParameters == null ||
           inNewParameters.isEmpty()) {
            assertNull(mxBeanInterface.getParameters());
        } else {
            String propertiesString = mxBeanInterface.getParameters();
            Properties actualProperties = Util.propertiesFromString(propertiesString);
            Properties expectedProperties = Util.propertiesFromString(inNewParameters);
            assertEquals(actualProperties,
                         expectedProperties);
        }
        assertEquals(inNewRouting,
                     mxBeanInterface.isRoutingOrdersToORS());
        // cycle the module
        stopStrategy(strategy);
        verifyStrategyStatus(strategy,
                             STOPPED);
        startStrategy(strategy);
        verifyStrategyStatus(strategy,
                             RUNNING);
        return strategy;
    }
    /**
     * Tries to create a strategy module with the given set of parameters.
     *
     * <p>It is expected that one of the strategies is of the wrong type.
     * 
     * @param inParameters an <code>int</code> value indicating the index of the first bad parameter
     * @throws Exception if an error occurs
     */
    private void doWrongTypeParameterTest(int badParameter,
                                          final Object...inParameters)
        throws Exception
    {
        // special case for parameter 3 (2 by 0-indexed counting, of course), language - the module factory framework cannot check for us
        if(badParameter != 2) {
            new ExpectedFailure<ModuleCreationException>(CANNOT_CREATE_MODULE_WRONG_PARAM_TYPE,
                                                         StrategyModuleFactory.PROVIDER_URN.toString(),
                                                         badParameter,
                                                         expectedTypes[badParameter].getName(),
                                                         inParameters[badParameter].getClass().getName()) {
                @Override
                protected void run()
                    throws Exception
                {
                    moduleManager.createModule(StrategyModuleFactory.PROVIDER_URN,
                                               inParameters);
                }
            };
        }
        new ExpectedFailure<ModuleCreationException>(PARAMETER_TYPE_ERROR,
                                                     badParameter + 1,
                                                     expectedTypes[badParameter].getName(),
                                                     inParameters[badParameter].getClass().getName()) {
            @Override
            protected void run()
                throws Exception
            {
                factory.create(inParameters);
            }
        };
    }
    /**
     * Tests that an incorrect parameter count is properly handled. 
     *
     * @param inParameters an <code>Object...</code> value
     * @throws Exception if an error occurs
     */
    private void doWrongParameterCountTest(final Object...inParameters)
        throws Exception
    {
        if(inParameters != null) {
            assertFalse("This test is supposed to test an incorrect number of parameters",
                        inParameters.length == 7);
        }
        new ExpectedFailure<ModuleCreationException>(CANNOT_CREATE_MODULE_WRONG_PARAM_NUM,
                                                     StrategyModuleFactory.PROVIDER_URN.toString(),
                                                     7,
                                                     (inParameters == null) ? 0 : inParameters.length) {
            @Override
            protected void run()
                throws Exception
            {
                moduleManager.createModule(StrategyModuleFactory.PROVIDER_URN,
                                           inParameters);
            }
        };
        new ExpectedFailure<ModuleCreationException>(PARAMETER_COUNT_ERROR) {
            @Override
            protected void run()
                throws Exception
            {
                factory.create(inParameters);
            }
        };
    }
    /**
     * should match the signature of {@link StrategyModule#StrategyModule(ModuleURN, String, Language, File, Properties, Boolean, ModuleURN)}. 
     */
    private static final Class<?>[] expectedTypes = new Class<?>[] { String.class, String.class, Language.class, File.class, Properties.class, Boolean.class, ModuleURN.class };
}
