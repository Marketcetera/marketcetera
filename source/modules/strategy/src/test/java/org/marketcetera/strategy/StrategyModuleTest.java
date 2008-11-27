package org.marketcetera.strategy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.marketcetera.module.Messages.CANNOT_CREATE_MODULE_WRONG_PARAM_NUM;
import static org.marketcetera.module.Messages.CANNOT_CREATE_MODULE_WRONG_PARAM_TYPE;
import static org.marketcetera.module.Messages.DATAFLOW_REQ_MODULE_STOPPED;
import static org.marketcetera.module.Messages.ILLEGAL_REQ_PARM_VALUE;
import static org.marketcetera.module.Messages.INVALID_URN_SCHEME;
import static org.marketcetera.module.Messages.MODULE_NOT_FOUND;
import static org.marketcetera.module.Messages.MODULE_NOT_RECEIVER;
import static org.marketcetera.module.Messages.UNSUPPORTED_REQ_PARM_TYPE;
import static org.marketcetera.strategy.Messages.EMPTY_NAME_ERROR;
import static org.marketcetera.strategy.Messages.FILE_DOES_NOT_EXIST_OR_IS_NOT_READABLE;
import static org.marketcetera.strategy.Messages.INVALID_LANGUAGE_ERROR;
import static org.marketcetera.strategy.Messages.NULL_PARAMETER_ERROR;
import static org.marketcetera.strategy.Messages.PARAMETER_COUNT_ERROR;
import static org.marketcetera.strategy.Messages.PARAMETER_TYPE_ERROR;

import java.io.File;
import java.util.Properties;

import org.junit.Test;
import org.marketcetera.core.Util;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.bogus.BogusFeedModuleFactory;
import org.marketcetera.module.DataFlowException;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.module.IllegalRequestParameterValue;
import org.marketcetera.module.InvalidURNException;
import org.marketcetera.module.ModuleCreationException;
import org.marketcetera.module.ModuleNotFoundException;
import org.marketcetera.module.ModuleStateException;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.module.UnsupportedRequestParameterType;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.test.UnicodeData;

/* $License$ */

/**
 * Tests {@link StrategyModule}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
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
                                                        JavaLanguageTest.JAVA_STRATEGY_NAME,
                                                        Language.JAVA,
                                                        JavaLanguageTest.JAVA_STRATEGY,
                                                        new Properties(),
                                                        new String[0],
                                                        ordersURN,
                                                        suggestionsURN);
        assertEquals(1,
                     moduleManager.getModuleInstances(StrategyModuleFactory.PROVIDER_URN).size());
        assertEquals(strategy,
                     moduleManager.getModuleInstances(StrategyModuleFactory.PROVIDER_URN).get(0));
        assertFalse(moduleManager.getModuleInfo(strategy).getState().isStarted());
        moduleManager.start(strategy);
        assertTrue(moduleManager.getModuleInfo(strategy).getState().isStarted());
        moduleManager.stop(strategy);
        assertEquals(1,
                     moduleManager.getModuleInstances(StrategyModuleFactory.PROVIDER_URN).size());
        assertEquals(strategy,
                     moduleManager.getModuleInstances(StrategyModuleFactory.PROVIDER_URN).get(0));
        moduleManager.deleteModule(strategy);
        assertTrue(moduleManager.getModuleInstances(StrategyModuleFactory.PROVIDER_URN).isEmpty());
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
        doWrongParameterCountTest(JavaLanguageTest.JAVA_STRATEGY_NAME);
        doWrongParameterCountTest(JavaLanguageTest.JAVA_STRATEGY_NAME,
                                  Language.JAVA);
        doWrongParameterCountTest(JavaLanguageTest.JAVA_STRATEGY_NAME,
                                  Language.JAVA,
                                  JavaLanguageTest.JAVA_STRATEGY);
        doWrongParameterCountTest(JavaLanguageTest.JAVA_STRATEGY_NAME,
                                  Language.JAVA,
                                  JavaLanguageTest.JAVA_STRATEGY,
                                  new Properties());
        doWrongParameterCountTest(JavaLanguageTest.JAVA_STRATEGY_NAME,
                                  Language.JAVA,
                                  JavaLanguageTest.JAVA_STRATEGY,
                                  new Properties(),
                                  ordersURN);
        // muddle types
        doWrongTypeParameterTest(0,
                                 this,
                                 Language.JAVA,
                                 JavaLanguageTest.JAVA_STRATEGY,
                                 new Properties(),
                                 new String[0],
                                 ordersURN,
                                 suggestionsURN);
        doWrongTypeParameterTest(1,
                                 JavaLanguageTest.JAVA_STRATEGY_NAME,
                                 this,
                                 JavaLanguageTest.JAVA_STRATEGY,
                                 new Properties(),
                                 new String[0],
                                 ordersURN,
                                 suggestionsURN);
        doWrongTypeParameterTest(2,
                                 JavaLanguageTest.JAVA_STRATEGY_NAME,
                                 Language.JAVA,
                                 this,
                                 new Properties(),
                                 new String[0],
                                 ordersURN,
                                 suggestionsURN);
        doWrongTypeParameterTest(3,
                                 JavaLanguageTest.JAVA_STRATEGY_NAME,
                                 Language.JAVA,
                                 JavaLanguageTest.JAVA_STRATEGY,
                                 this,
                                 new String[0],
                                 ordersURN,
                                 suggestionsURN);
        doWrongTypeParameterTest(4,
                                 JavaLanguageTest.JAVA_STRATEGY_NAME,
                                 Language.JAVA,
                                 JavaLanguageTest.JAVA_STRATEGY,
                                 new Properties(),
                                 this,
                                 ordersURN,
                                 suggestionsURN);
        doWrongTypeParameterTest(5,
                                 JavaLanguageTest.JAVA_STRATEGY_NAME,
                                 Language.JAVA,
                                 JavaLanguageTest.JAVA_STRATEGY,
                                 new Properties(),
                                 new String[0],
                                 this,
                                 suggestionsURN);
        doWrongTypeParameterTest(6,
                                 JavaLanguageTest.JAVA_STRATEGY_NAME,
                                 Language.JAVA,
                                 JavaLanguageTest.JAVA_STRATEGY,
                                 new Properties(),
                                 new String[0],
                                 ordersURN,
                                 this);
        // create a good 'un just to prove we can
        ModuleURN strategy = createStrategy(JavaLanguageTest.JAVA_STRATEGY_NAME,
                                            Language.JAVA,
                                            JavaLanguageTest.JAVA_STRATEGY,
                                            new Properties(),
                                            new String[0],
                                            ordersURN,
                                            suggestionsURN);
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
                                            Language.RUBY,
                                            RubyLanguageTest.STRATEGY,
                                            new Properties(),
                                            new String[0],
                                            ordersURN,
                                            suggestionsURN);
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
        // correct RequestType payload
        DataFlowID flowID3 = moduleManager.createDataFlow(new DataRequest[] { new DataRequest(StrategyModuleFactory.PROVIDER_URN,
                                                                                              OutputType.ORDERS) });
        DataFlowID flowID4 = moduleManager.createDataFlow(new DataRequest[] { new DataRequest(StrategyModuleFactory.PROVIDER_URN,
                                                                                              OutputType.SUGGESTIONS) });
        // TODO insert some code here to:
        //  1) have a test module make the above data requests
        //  2) have the strategy emit a trade suggestion and an order (verified in the module described in #1)
        moduleManager.cancel(flowID1);
        moduleManager.cancel(flowID2);
        moduleManager.cancel(flowID3);
        moduleManager.cancel(flowID4);
    }
    @Test 
    public void receiveData()
        throws Exception
    {
        // set up a strategy and plumb it externally with a market data provider
        ModuleURN strategy = createStrategy(RubyLanguageTest.STRATEGY_NAME,
                                            Language.RUBY,
                                            RubyLanguageTest.STRATEGY,
                                            new Properties(),
                                            new String[0],
                                            ordersURN,
                                            suggestionsURN);
        // plumb the market data provider externally to the strategy module - normally, this would be done internally, but, for this test,
        //  it is sufficient that the data is flowing, it doesn't matter how it gets there
        DataFlowID dataFlowID = moduleManager.createDataFlow(new DataRequest[] { new DataRequest(BogusFeedModuleFactory.PROVIDER_URN,
                                                                                                 MarketDataRequest.newFullBookRequest("GOOG")),
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
        for(index[0]=0;index[0]<7;index[0]++) {
            // parameters 4, 5, 6, and 7 are optional, so nulls are allowed
            if(index[0] == 3 ||
               index[0] == 4 ||
               index[0] == 5 ||
               index[0] == 6) {
                verifyStrategyStartsAndStops((index[0]==0 ? null : JavaLanguageTest.JAVA_STRATEGY_NAME),
                                             (index[0]==1 ? null : Language.JAVA),
                                             (index[0]==2 ? null : JavaLanguageTest.JAVA_STRATEGY),
                                             (index[0]==3 ? null : new Properties()),
                                             (index[0]==4 ? null : new String[0]),
                                             (index[0]==5 ? null : ordersURN),
                                             (index[0]==6 ? null : suggestionsURN));
            } else {
                new ExpectedFailure<ModuleCreationException>(NULL_PARAMETER_ERROR,
                                                             index[0] + 1,
                                                             expectedTypes[index[0]].getName()) {
                    @Override
                    protected void run()
                        throws Exception
                    {
                        verifyStrategyStartsAndStops((index[0]==0 ? null : JavaLanguageTest.JAVA_STRATEGY_NAME),
                                                     (index[0]==1 ? null : Language.JAVA),
                                                     (index[0]==2 ? null : JavaLanguageTest.JAVA_STRATEGY),
                                                     (index[0]==3 ? null : new Properties()),
                                                     (index[0]==4 ? null : new String[0]),
                                                     (index[0]==5 ? null : ordersURN),
                                                     (index[0]==6 ? null : suggestionsURN));
                    }
                };
            }
        }
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
                                             Language.JAVA,
                                             JavaLanguageTest.JAVA_STRATEGY,
                                             null,
                                             null,
                                             null,
                                             null);
            }
        };
        verifyStrategyStartsAndStops(JavaLanguageTest.JAVA_STRATEGY_NAME,
                                     Language.JAVA,
                                     JavaLanguageTest.JAVA_STRATEGY,
                                     null,
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
        String validLanguage = Language.JAVA.toString();
        String validMixedCaseLanguage = "JaVa";
        new ExpectedFailure<ModuleCreationException>(INVALID_LANGUAGE_ERROR,
                                                     invalidLanguage) {
            @Override
            protected void run()
                throws Exception
            {
                verifyStrategyStartsAndStops(JavaLanguageTest.JAVA_STRATEGY_NAME,
                                             invalidLanguage,
                                             JavaLanguageTest.JAVA_STRATEGY,
                                             null,
                                             null,
                                             null,
                                             null);
            }
        };
        verifyStrategyStartsAndStops(JavaLanguageTest.JAVA_STRATEGY_NAME,
                                     validLanguage,
                                     JavaLanguageTest.JAVA_STRATEGY,
                                     null,
                                     null,
                                     null,
                                     null);
        // test again with a mixed case string
        verifyStrategyStartsAndStops(JavaLanguageTest.JAVA_STRATEGY_NAME,
                                     validMixedCaseLanguage,
                                     JavaLanguageTest.JAVA_STRATEGY,
                                     null,
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
                verifyStrategyStartsAndStops(JavaLanguageTest.JAVA_STRATEGY_NAME,
                                             Language.JAVA,
                                             badFile,
                                             null,
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
        verifyStrategyStartsAndStops(JavaLanguageTest.JAVA_STRATEGY_NAME,
                                     Language.JAVA,
                                     JavaLanguageTest.JAVA_STRATEGY,
                                     properties,
                                     null,
                                     null,
                                     null);
        // non-empty properties
        properties.setProperty("some-key",
                               "some value " + System.nanoTime());
        verifyStrategyStartsAndStops(JavaLanguageTest.JAVA_STRATEGY_NAME,
                                     Language.JAVA,
                                     JavaLanguageTest.JAVA_STRATEGY,
                                     properties,
                                     null,
                                     null,
                                     null);
    }
    /**
     * Tests permutations of classpath parameter.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void classpathParameterTest()
        throws Exception
    {
        // empty classpath
        verifyStrategyStartsAndStops(JavaLanguageTest.JAVA_STRATEGY_NAME,
                                     Language.JAVA,
                                     JavaLanguageTest.JAVA_STRATEGY,
                                     null,
                                     new String[0],
                                     null,
                                     null);
        // non-empty classpath
        verifyStrategyStartsAndStops(JavaLanguageTest.JAVA_STRATEGY_NAME,
                                     Language.JAVA,
                                     JavaLanguageTest.JAVA_STRATEGY,
                                     null,
                                     new String[] { "/some/path/here", "/some/other/path with spaces/here" },
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
                verifyStrategyStartsAndStops(JavaLanguageTest.JAVA_STRATEGY_NAME,
                                             Language.JAVA,
                                             JavaLanguageTest.JAVA_STRATEGY,
                                             null,
                                             null,
                                             invalidURN,
                                             null);
            }
        };
        // next, valid, unstarted URN
        new ExpectedFailure<ModuleStateException>(DATAFLOW_REQ_MODULE_STOPPED,
                                                  validUnstartedURN.toString()) {
            @Override
            protected void run()
                throws Exception
            {
                verifyStrategyStartsAndStops(JavaLanguageTest.JAVA_STRATEGY_NAME,
                                             Language.JAVA,
                                             JavaLanguageTest.JAVA_STRATEGY,
                                             null,
                                             null,
                                             validUnstartedURN,
                                             null);
            }
        };
        // last, valid, started URN, but not a data-receiver
        new ExpectedFailure<DataFlowException>(MODULE_NOT_RECEIVER,
                                               validURNNotReceiver.toString()) {
            @Override
            protected void run()
                throws Exception
            {
                verifyStrategyStartsAndStops(JavaLanguageTest.JAVA_STRATEGY_NAME,
                                             Language.JAVA,
                                             JavaLanguageTest.JAVA_STRATEGY,
                                             null,
                                             null,
                                             validURNNotReceiver,
                                             null);
            }
        };
        // repeat tests with suggestions
        // first, invalid URN
        new ExpectedFailure<ModuleNotFoundException>(MODULE_NOT_FOUND,
                                                     invalidURN.toString()) {
            @Override
            protected void run()
                throws Exception
            {
                verifyStrategyStartsAndStops(JavaLanguageTest.JAVA_STRATEGY_NAME,
                                             Language.JAVA,
                                             JavaLanguageTest.JAVA_STRATEGY,
                                             null,
                                             null,
                                             null,
                                             invalidURN);
            }
        };
        // next, valid, unstarted URN
        new ExpectedFailure<ModuleStateException>(DATAFLOW_REQ_MODULE_STOPPED,
                                                  validUnstartedURN.toString()) {
            @Override
            protected void run()
                throws Exception
            {
                verifyStrategyStartsAndStops(JavaLanguageTest.JAVA_STRATEGY_NAME,
                                             Language.JAVA,
                                             JavaLanguageTest.JAVA_STRATEGY,
                                             null,
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
                verifyStrategyStartsAndStops(JavaLanguageTest.JAVA_STRATEGY_NAME,
                                             Language.JAVA,
                                             JavaLanguageTest.JAVA_STRATEGY,
                                             null,
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
        // stop the suggestions module to create a valid, stopped URN
        moduleManager.stop(suggestionsURN);
        // create our test data for the values to pass to the setDestination MXBean setters (suggestion and order)
        //  the values are null, invalid, stopped, empty, and started
        final String[] urnStrings = new String[] { null, "this is not a URN", suggestionsURN.getValue(), "", ordersURN.getValue() };
        // create the test data for the starting point for the strategy module
        //  the values are null and started (no stopped because then the strategy module itself would be un-startable before we even got around to
        //  testing the setters - no point in that
        final ModuleURN[] urns = new ModuleURN[] { null, ordersURN };
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
        // cycle through all the permutations for the starting point for suggestions and orders (2 values each) and the value to set the destinations to
        //  (5 values each) and new parameters (4 values each) and parameters starting point (3 values each), for a total of 1200 test cases (2*2*5*5*4*3)
        // while this bit of code may not be the most legible, it's easy to see that 1200 test conditions would be a fair bit more verbose
        for(int a=0;a<=4;a++) {
            for(int b=0;b<=4;b++) {
                for(int c=0;c<=1;c++) {
                    for(int d=0;d<=1;d++) {
                        for(int e=0;e<=4;e++) {
                            for(int f=0;f<=3;f++) {
                                final int aCounter = a;
                                final int bCounter = b;
                                final int cCounter = c;
                                final int dCounter = d;
                                final int eCounter = e;
                                final int fCounter = f;
                                SLF4JLoggerProxy.debug(this,
                                                       "Testing permutation: {} {} {} {} {} {}",
                                                       a,b,c,d,e,f);
                                // the a values need to be tested first (1 & 2) followed by the same tests for the b values - can't combine the boolean
                                //  expressions (like a==1 || b==1) because a==2 && b==1 would fail in the first check because the a==2 part would fail
                                //  before the b==1 and throw the "wrong" exception.  that may not be horribly clear, but suffice it to say that there's
                                //  a reason why the conditions are split apart
                                if(a == 1) { // invalid URN
                                    new ExpectedFailure<InvalidURNException>(INVALID_URN_SCHEME) {
                                        @Override
                                        protected void run()
                                        throws Exception
                                        {
                                            doOneMXInterfaceTest(urnStrings[aCounter],
                                                                 urnStrings[bCounter],
                                                                 urns[cCounter],
                                                                 urns[dCounter],
                                                                 parameterStrings[eCounter],
                                                                 parameters[fCounter]);
                                        }
                                    };
                                    continue;
                                }
                                if(a == 2) { // stopped URN
                                    new ExpectedFailure<ModuleStateException>(DATAFLOW_REQ_MODULE_STOPPED) {
                                        @Override
                                        protected void run()
                                        throws Exception
                                        {
                                            doOneMXInterfaceTest(urnStrings[aCounter],
                                                                 urnStrings[bCounter],
                                                                 urns[cCounter],
                                                                 urns[dCounter],
                                                                 parameterStrings[eCounter],
                                                                 parameters[fCounter]);
                                        }
                                    };
                                    continue;
                                }
                                if(b == 1) { // invalid URN
                                    new ExpectedFailure<InvalidURNException>(INVALID_URN_SCHEME) {
                                        @Override
                                        protected void run()
                                        throws Exception
                                        {
                                            doOneMXInterfaceTest(urnStrings[aCounter],
                                                                 urnStrings[bCounter],
                                                                 urns[cCounter],
                                                                 urns[dCounter],
                                                                 parameterStrings[eCounter],
                                                                 parameters[fCounter]);
                                        }
                                    };
                                    continue;
                                }
                                if(b == 2) { // stopped URN
                                    new ExpectedFailure<ModuleStateException>(DATAFLOW_REQ_MODULE_STOPPED) {
                                        @Override
                                        protected void run()
                                        throws Exception
                                        {
                                            doOneMXInterfaceTest(urnStrings[aCounter],
                                                                 urnStrings[bCounter],
                                                                 urns[cCounter],
                                                                 urns[dCounter],
                                                                 parameterStrings[eCounter],
                                                                 parameters[fCounter]);
                                        }
                                    };
                                    continue;
                                }
                                doOneMXInterfaceTest(urnStrings[a],
                                                     urnStrings[b],
                                                     urns[c],
                                                     urns[dCounter],
                                                     parameterStrings[eCounter],
                                                     parameters[fCounter]);
                            }
                        }
                    }
                }
            }
        }
    }
    /**
     * Executes a single permutation of a strategy attribute get/set test.
     * 
     * @param inOrdersDestination a <code>String</code> value or null
     * @param inSuggestionsDestination a <code>String</code> value or null
     * @param inOrdersStart a <code>ModuleURN</code> value or null
     * @param inSuggestionsStart a <code>ModuleURN</code> value or null
     * @param inNewParameters a <code>String</code> value containing a properly formatted properties string or null
     * @param inStartingParameters a <code>Properties</code> value containing the starting parameters value or null
     * @return a <code>ModuleURN</code> value containing the strategy module guaranteed to be started
     * @throws Exception if the strategy module could not be started or another error occurs
     */
    private ModuleURN doOneMXInterfaceTest(String inOrdersDestination,
                                           String inSuggestionsDestination,
                                           ModuleURN inOrdersStart,
                                           ModuleURN inSuggestionsStart,
                                           String inNewParameters,
                                           Properties inStartingParameters)
        throws Exception
    {
        ModuleURN strategy = createStrategy(RubyLanguageTest.STRATEGY_NAME,
                                            Language.RUBY,
                                            RubyLanguageTest.STRATEGY,
                                            inStartingParameters,
                                            null,
                                            inOrdersStart,
                                            inSuggestionsStart);
        StrategyMXBean mxBeanInterface = getMXProxy(strategy);
        if(inOrdersStart == null) {
            assertNull(mxBeanInterface.getOrdersDestination());
        } else {
            assertEquals(inOrdersStart.getValue(),
                         mxBeanInterface.getOrdersDestination());
        }
        if(inSuggestionsStart == null) {
            assertNull(mxBeanInterface.getSuggestionsDestination());
        } else {
            assertEquals(inSuggestionsStart.getValue(),
                         mxBeanInterface.getSuggestionsDestination());
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
        // make the change
        mxBeanInterface.setOrdersDestination(inOrdersDestination);
        mxBeanInterface.setSuggestionsDestination(inSuggestionsDestination);
        mxBeanInterface.setParameters(inNewParameters);
        // test the change
        if(inOrdersDestination == null ||
           inOrdersDestination.isEmpty()) {
            assertNull(mxBeanInterface.getOrdersDestination());
        } else {
            assertEquals(inOrdersDestination,
                         mxBeanInterface.getOrdersDestination());
        }
        if(inSuggestionsDestination == null ||
           inSuggestionsDestination.isEmpty()) {
            assertNull(mxBeanInterface.getSuggestionsDestination());
        } else {
            assertEquals(inSuggestionsDestination,
                         mxBeanInterface.getSuggestionsDestination());
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
        // cycle the module
        moduleManager.stop(strategy);
        moduleManager.start(strategy);
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
        // special case for parameter 2 (1 by 0-indexed counting, of course), language - the module factory framework cannot check for us
        if(badParameter != 1) {
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
     * should match the signature of {@link StrategyModule#StrategyModule(ModuleURN, String, Language, File, Properties, ModuleURN, ModuleURN)}. 
     */
    private static final Class<?>[] expectedTypes = new Class<?>[] { String.class, Language.class, File.class, Properties.class, String[].class, ModuleURN.class, ModuleURN.class };
}
