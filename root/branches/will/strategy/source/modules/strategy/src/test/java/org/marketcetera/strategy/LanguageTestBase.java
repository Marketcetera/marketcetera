package org.marketcetera.strategy;

import static org.junit.Assert.assertFalse;
import static org.marketcetera.strategy.Messages.FAILED_TO_START;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.module.ModuleException;
import org.marketcetera.module.ModuleURN;

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
        final StrategyCoordinates strategy1 = getStrategyMultipleClasses();
        final StrategyCoordinates strategy2 = getStrategyCompiles();
        new ExpectedFailure<ModuleException>(FAILED_TO_START) {
            @Override
            protected void run()
                    throws Exception
            {
                verifyStrategyStartsAndStops(strategy1.getName(),
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
                            new String[] { "onBid", "onExecutionReport", "onTrade", "onOther" });
        doCallbackFailsTest("shouldFailOnBid",
                            new String[] { "onAsk", "onExecutionReport", "onTrade", "onOther" });
        doCallbackFailsTest("shouldFailOnExecutionReport",
                            new String[] { "onAsk", "onBid", "onTrade", "onOther" });
        doCallbackFailsTest("shouldFailOnTrade",
                            new String[] { "onAsk", "onBid", "onExecutionReport", "onOther" });
        doCallbackFailsTest("shouldFailOnOther",
                            new String[] { "onAsk", "onBid", "onExecutionReport", "onTrade" });
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
    private void doSuccessfulStartTestNoVerification(ModuleURN inStrategy)
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
        Set<String> allCallbacks = new HashSet<String>(Arrays.asList(new String[] { "onAsk", "onBid", "onExecutionReport", "onTrade", "onOther" }));
        for(String callback : inCallbacksThatShouldHaveSucceeded) {
            verifyPropertyNonNull(callback);
            allCallbacks.remove(callback);
        }
        for(String callbackShouldBeNull : allCallbacks) {
            verifyPropertyNull(callbackShouldBeNull);
        }
    }
}
