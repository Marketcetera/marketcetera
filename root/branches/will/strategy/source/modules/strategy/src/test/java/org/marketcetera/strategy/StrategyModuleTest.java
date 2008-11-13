package org.marketcetera.strategy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.marketcetera.module.Messages.CANNOT_CREATE_MODULE_WRONG_PARAM_NUM;
import static org.marketcetera.module.Messages.CANNOT_CREATE_MODULE_WRONG_PARAM_TYPE;
import static org.marketcetera.module.Messages.DATAFLOW_REQ_MODULE_STOPPED;
import static org.marketcetera.module.Messages.ILLEGAL_REQ_PARM_VALUE;
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
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.bogus.BogusFeedModuleFactory;
import org.marketcetera.module.DataFlowException;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.module.IllegalRequestParameterValue;
import org.marketcetera.module.ModuleCreationException;
import org.marketcetera.module.ModuleNotFoundException;
import org.marketcetera.module.ModuleStateException;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.module.UnsupportedRequestParameterType;
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
                                                        JAVA_STRATEGY_NAME,
                                                        Language.JAVA,
                                                        JAVA_STRATEGY,
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
        doWrongParameterCountTest(JAVA_STRATEGY_NAME);
        doWrongParameterCountTest(JAVA_STRATEGY_NAME,
                                  Language.JAVA);
        doWrongParameterCountTest(JAVA_STRATEGY_NAME,
                                  Language.JAVA,
                                  JAVA_STRATEGY);
        doWrongParameterCountTest(JAVA_STRATEGY_NAME,
                                  Language.JAVA,
                                  JAVA_STRATEGY,
                                  new Properties());
        doWrongParameterCountTest(JAVA_STRATEGY_NAME,
                                  Language.JAVA,
                                  JAVA_STRATEGY,
                                  new Properties(),
                                  ordersURN);
        // muddle types
        doWrongTypeParameterTest(0,
                                 this,
                                 Language.JAVA,
                                 JAVA_STRATEGY,
                                 new Properties(),
                                 new String[0],
                                 ordersURN,
                                 suggestionsURN);
        doWrongTypeParameterTest(1,
                                 JAVA_STRATEGY_NAME,
                                 this,
                                 JAVA_STRATEGY,
                                 new Properties(),
                                 new String[0],
                                 ordersURN,
                                 suggestionsURN);
        doWrongTypeParameterTest(2,
                                 JAVA_STRATEGY_NAME,
                                 Language.JAVA,
                                 this,
                                 new Properties(),
                                 new String[0],
                                 ordersURN,
                                 suggestionsURN);
        doWrongTypeParameterTest(3,
                                 JAVA_STRATEGY_NAME,
                                 Language.JAVA,
                                 JAVA_STRATEGY,
                                 this,
                                 new String[0],
                                 ordersURN,
                                 suggestionsURN);
        doWrongTypeParameterTest(4,
                                 JAVA_STRATEGY_NAME,
                                 Language.JAVA,
                                 JAVA_STRATEGY,
                                 new Properties(),
                                 this,
                                 ordersURN,
                                 suggestionsURN);
        doWrongTypeParameterTest(5,
                                 JAVA_STRATEGY_NAME,
                                 Language.JAVA,
                                 JAVA_STRATEGY,
                                 new Properties(),
                                 new String[0],
                                 this,
                                 suggestionsURN);
        doWrongTypeParameterTest(6,
                                 JAVA_STRATEGY_NAME,
                                 Language.JAVA,
                                 JAVA_STRATEGY,
                                 new Properties(),
                                 new String[0],
                                 ordersURN,
                                 this);
        // create a good 'un just to prove we can
        ModuleURN strategy = createStrategy(JAVA_STRATEGY_NAME,
                                            Language.JAVA,
                                            JAVA_STRATEGY,
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
        // start the market data provider
        moduleManager.start(BogusFeedModuleFactory.INSTANCE_URN);
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
        // stop the market data provider
        moduleManager.stop(BogusFeedModuleFactory.INSTANCE_URN);
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
                verifyStrategyStartsAndStops((index[0]==0 ? null : JAVA_STRATEGY_NAME),
                                             (index[0]==1 ? null : Language.JAVA),
                                             (index[0]==2 ? null : JAVA_STRATEGY),
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
                        verifyStrategyStartsAndStops((index[0]==0 ? null : JAVA_STRATEGY_NAME),
                                                     (index[0]==1 ? null : Language.JAVA),
                                                     (index[0]==2 ? null : JAVA_STRATEGY),
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
        String nonEmptyName = "some name here " + System.nanoTime() + " &^%@&$&$";
        String nonAsciiName = UnicodeData.GOATS_LNB;
        new ExpectedFailure<ModuleCreationException>(EMPTY_NAME_ERROR) {
            @Override
            protected void run()
                throws Exception
            {
                verifyStrategyStartsAndStops(emptyName,
                                             Language.JAVA,
                                             JAVA_STRATEGY,
                                             null,
                                             null,
                                             null,
                                             null);
            }
        };
        verifyStrategyStartsAndStops(nonEmptyName,
                                     Language.JAVA,
                                     JAVA_STRATEGY,
                                     null,
                                     null,
                                     null,
                                     null);
        verifyStrategyStartsAndStops(nonAsciiName,
                                     Language.JAVA,
                                     JAVA_STRATEGY,
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
                verifyStrategyStartsAndStops(JAVA_STRATEGY_NAME,
                                             invalidLanguage,
                                             JAVA_STRATEGY,
                                             null,
                                             null,
                                             null,
                                             null);
            }
        };
        verifyStrategyStartsAndStops(JAVA_STRATEGY_NAME,
                                     validLanguage,
                                     JAVA_STRATEGY,
                                     null,
                                     null,
                                     null,
                                     null);
        // test again with a mixed case string
        verifyStrategyStartsAndStops(JAVA_STRATEGY_NAME,
                                     validMixedCaseLanguage,
                                     JAVA_STRATEGY,
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
                verifyStrategyStartsAndStops(JAVA_STRATEGY_NAME,
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
        verifyStrategyStartsAndStops(JAVA_STRATEGY_NAME,
                                     Language.JAVA,
                                     JAVA_STRATEGY,
                                     properties,
                                     null,
                                     null,
                                     null);
        // non-empty properties
        properties.setProperty("some-key",
                               "some value " + System.nanoTime());
        verifyStrategyStartsAndStops(JAVA_STRATEGY_NAME,
                                     Language.JAVA,
                                     JAVA_STRATEGY,
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
        verifyStrategyStartsAndStops(JAVA_STRATEGY_NAME,
                                     Language.JAVA,
                                     JAVA_STRATEGY,
                                     null,
                                     new String[0],
                                     null,
                                     null);
        // non-empty classpath
        verifyStrategyStartsAndStops(JAVA_STRATEGY_NAME,
                                     Language.JAVA,
                                     JAVA_STRATEGY,
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
        // start the valid (not receiver) module
        moduleManager.start(validURNNotReceiver);
        assertFalse(moduleManager.getModuleInfo(BogusFeedModuleFactory.INSTANCE_URN).isReceiver());
        // test the above URNs for orders
        // first, invalid URN
        new ExpectedFailure<ModuleNotFoundException>(MODULE_NOT_FOUND,
                                                     invalidURN.toString()) {
            @Override
            protected void run()
                throws Exception
            {
                verifyStrategyStartsAndStops(JAVA_STRATEGY_NAME,
                                             Language.JAVA,
                                             JAVA_STRATEGY,
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
                verifyStrategyStartsAndStops(JAVA_STRATEGY_NAME,
                                             Language.JAVA,
                                             JAVA_STRATEGY,
                                             null,
                                             null,
                                             validUnstartedURN,
                                             null);
            }
        };
        // last, valid, started URN, but not a data-receiver
        new ExpectedFailure<DataFlowException>(MODULE_NOT_RECEIVER,
                                               BogusFeedModuleFactory.INSTANCE_URN.toString()) {
            @Override
            protected void run()
                throws Exception
            {
                verifyStrategyStartsAndStops(JAVA_STRATEGY_NAME,
                                             Language.JAVA,
                                             JAVA_STRATEGY,
                                             null,
                                             null,
                                             BogusFeedModuleFactory.INSTANCE_URN,
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
                verifyStrategyStartsAndStops(JAVA_STRATEGY_NAME,
                                             Language.JAVA,
                                             JAVA_STRATEGY,
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
                verifyStrategyStartsAndStops(JAVA_STRATEGY_NAME,
                                             Language.JAVA,
                                             JAVA_STRATEGY,
                                             null,
                                             null,
                                             null,
                                             validUnstartedURN);
            }
        };
        // last, valid, started URN, but not a data-receiver
        new ExpectedFailure<DataFlowException>(MODULE_NOT_RECEIVER,
                                               BogusFeedModuleFactory.INSTANCE_URN.toString()) {
            @Override
            protected void run()
                throws Exception
            {
                verifyStrategyStartsAndStops(JAVA_STRATEGY_NAME,
                                             Language.JAVA,
                                             JAVA_STRATEGY,
                                             null,
                                             null,
                                             null,
                                             BogusFeedModuleFactory.INSTANCE_URN);
            }
        };
        // stop the non-receiver
        moduleManager.stop(BogusFeedModuleFactory.INSTANCE_URN);
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
