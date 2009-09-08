package org.marketcetera.strategy;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Test;
import org.marketcetera.event.LogEvent;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.module.ModuleException;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.module.SinkDataListener;
import org.marketcetera.module.SinkModuleFactory;
import org.marketcetera.util.test.SerializableAssert;

import static org.marketcetera.strategy.Status.FAILED;

/* $License$ */

/**
 * Tests Ruby language support.
 * 
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.0.0
 */
public class RubyLanguageTest
        extends LanguageTestBase
{
    public static final File STRATEGY = new File(StrategyTestBase.SAMPLE_STRATEGY_DIR,
                                                 "RubyStrategy.rb");
    public static final String STRATEGY_NAME = "RubyStrategy";
    public static final File BAD_STRATEGY = new File(StrategyTestBase.SAMPLE_STRATEGY_DIR,
                                                     "BadRubyStrategy.rb");
    public static final String BAD_STRATEGY_NAME = "BadRubyStrategy";
    public static final File WRONG_STRATEGY = new File(StrategyTestBase.SAMPLE_STRATEGY_DIR,
                                                       "WrongClass.rb");
    public static final String WRONG_STRATEGY_NAME = "WrongClass";
    public static final File MULTIPLE_CLASS_STRATEGY = new File(StrategyTestBase.SAMPLE_STRATEGY_DIR,
                                                                "StrategyWithHelpers.rb");
    public static final String MULTIPLE_CLASS_STRATEGY_NAME = "StrategyWithHelpers";
    public static final File EMPTY_STRATEGY = new File(StrategyTestBase.SAMPLE_STRATEGY_DIR,
                                                       "EmptyStrategy.rb");
    public static final String EMPTY_STRATEGY_NAME = "EmptyStrategy";
    public static final File PARAMETER_STRATEGY = new File(StrategyTestBase.SAMPLE_STRATEGY_DIR,
                                                           "ParameterStrategy.rb");
    public static final String PARAMETER_STRATEGY_NAME = "ParameterStrategy";
    public static final File SUGGESTION_STRATEGY = new File(StrategyTestBase.SAMPLE_STRATEGY_DIR,
                                                            "suggest_trades.rb");
    public static final String SUGGESTION_STRATEGY_NAME = "SuggestTrades";
    public static final File ORDER_STRATEGY = new File(StrategyTestBase.SAMPLE_STRATEGY_DIR,
                                                       "orders.rb");
    public static final String ORDER_STRATEGY_NAME = "Orders";
    public static final File OTHER_STRATEGY = new File(StrategyTestBase.SAMPLE_STRATEGY_DIR,
                                                       "send_other.rb");
    public static final String OTHER_STRATEGY_NAME = "SendOther";
    public static final File MESSAGE_STRATEGY = new File(StrategyTestBase.SAMPLE_STRATEGY_DIR,
                                                         "send_message.rb");
    public static final String MESSAGE_STRATEGY_NAME = "SendMessage";
    public static final File PART1_STRATEGY = new File(StrategyTestBase.SAMPLE_STRATEGY_DIR,
                                                       "part1.rb");
    public static final String PART1_STRATEGY_NAME = "Part1";
    public static final File PART1_REDEFINED_STRATEGY = new File(StrategyTestBase.SAMPLE_STRATEGY_DIR,
                                                                 "part1_redefined.rb");
    public static final File PART2_STRATEGY = new File(StrategyTestBase.SAMPLE_STRATEGY_DIR,
                                                       "part2.rb");
    public static final String PART2_STRATEGY_NAME = "Part2";
    public static final File EVENT_STRATEGY = new File(StrategyTestBase.SAMPLE_STRATEGY_DIR,
                                                       "send_event.rb");
    public static final String EVENT_STRATEGY_NAME = "SendEvent";
    public static final File COMBINED_STRATEGY = new File(StrategyTestBase.SAMPLE_STRATEGY_DIR,
                                                          "combined_request.rb");
    public static final String COMBINED_STRATEGY_NAME = "CombinedRequest";
    public static final File REQUIRES_STRATEGY = new File(StrategyTestBase.SAMPLE_STRATEGY_DIR,
                                                          "require.rb");
    public static final String REQUIRES_STRATEGY_NAME = "Require";
    public static final File BAD_ON_START_STRATEGY = new File(StrategyTestBase.SAMPLE_STRATEGY_DIR,
                                                              "bad_on_start.rb");
    public static final String BAD_ON_START_STRATEGY_NAME = "BadOnStart";
    public static final File BAD_ON_STOP_STRATEGY = new File(StrategyTestBase.SAMPLE_STRATEGY_DIR,
                                                             "bad_on_stop.rb");
    public static final String BAD_ON_STOP_STRATEGY_NAME = "BadOnStop";
    public static final File DATA_FLOW_STRATEGY = new File(StrategyTestBase.SAMPLE_STRATEGY_DIR,
                                                           "data_flow.rb");
    public static final String DATA_FLOW_STRATEGY_NAME = "DataFlow";
    /**
     * Verifies that a Ruby strategy that requires another Ruby strategy works.
     *
     * @throws Exception if an error occurs
     */
    @Test
    public void requires()
        throws Exception
    {
        StrategyCoordinates strategy = getRequiresStrategy();
        verifyNullProperties();
        // and the right classpath
        ModuleURN strategyURN = createStrategy(strategy.getName(),
                                               getLanguage(),
                                               strategy.getFile(),
                                               null,
                                               null,
                                               null);
        doSuccessfulStartTestNoVerification(strategyURN);
        verifyPropertyNonNull("onStart");
        stopStrategy(strategyURN);
        verifyPropertyNonNull("onStop");
    }
    /**
     * Tests that all LogEvents are serializable when a strategy has a syntax error in on_start
     * or in on_stop.
     *
     * @throws Exception if an error occurs
     */
     @Test
     public void serializableError()
         throws Exception
     {
         // runtime error in onStart
         final StrategyCoordinates strategy = getBadOnStartStrategy();
         final Properties parameters = new Properties();
         parameters.setProperty("shouldFailOnStart",
                                "true");
         
         // We want to listen for LogEvents emitted to the Sink module
         final List<LogEvent> logEventList = new ArrayList<LogEvent>();
         moduleManager.addSinkListener(new SinkDataListener() {
             @Override
             public void receivedData(DataFlowID inFlowID, Object inData) {
                 if (inData instanceof LogEvent)
                     logEventList.add((LogEvent) inData);
             }
         });
         final ModuleURN strategyURN = moduleManager.createModule(StrategyModuleFactory.PROVIDER_URN,
                                                                  null,
                                                                  strategy.getName(),
                                                                  getLanguage(),
                                                                  strategy.getFile(),
                                                                  parameters,
                                                                  null,
                                                                  SinkModuleFactory.INSTANCE_URN);
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
         final StrategyCoordinates strategy2 = getBadOnStopStrategy();
         ModuleURN strategyURN2 = createStrategy(strategy2.getName(),
                                                 getLanguage(),
                                                 strategy2.getFile(),
                                                 parameters,
                                                 null,
                                                 SinkModuleFactory.INSTANCE_URN);
         doSuccessfulStartTestNoVerification(strategyURN2);
         stopStrategy(strategyURN2);
         AbstractRunningStrategy.setProperty("shouldFailOnStop",
                                             null);
         
         // Test that all LogEvents emitted are serializable
         // assertSerializable throws error if they are not
         for (LogEvent l : logEventList)
             SerializableAssert.assertSerializable(UNSERIALIZABLE_LOG_EVENT, l);
         
     }
    /*
     * (non-Javadoc)
     * 
     * @see org.marketcetera.strategy.LanguageTestBase#getLanguage()
     */
    @Override
    protected Language getLanguage()
    {
        return Language.RUBY;
    }
    /*
     * (non-Javadoc)
     * 
     * @see org.marketcetera.strategy.LanguageTestBase#getStrategyWillNotCompile()
     */
    @Override
    protected StrategyCoordinates getStrategyWillNotCompile()
    {
        return StrategyCoordinates.get(RubyLanguageTest.BAD_STRATEGY,
                                       RubyLanguageTest.BAD_STRATEGY_NAME);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.LanguageTestBase#getStrategyCompiles()
     */
    @Override
    protected StrategyCoordinates getStrategyCompiles()
    {
        return StrategyCoordinates.get(RubyLanguageTest.STRATEGY,
                                       RubyLanguageTest.STRATEGY_NAME);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.LanguageTestBase#getStrategyWrongClass()
     */
    @Override
    protected StrategyCoordinates getStrategyWrongClass()
    {
        return StrategyCoordinates.get(RubyLanguageTest.WRONG_STRATEGY,
                                       RubyLanguageTest.WRONG_STRATEGY_NAME);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.LanguageTestBase#getStrategyMultipleClasses()
     */
    @Override
    protected StrategyCoordinates getStrategyMultipleClasses()
    {
        return StrategyCoordinates.get(RubyLanguageTest.MULTIPLE_CLASS_STRATEGY,
                                       RubyLanguageTest.MULTIPLE_CLASS_STRATEGY_NAME);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.LanguageTestBase#getEmptyStrategy()
     */
    @Override
    protected StrategyCoordinates getEmptyStrategy()
    {
        return StrategyCoordinates.get(RubyLanguageTest.EMPTY_STRATEGY,
                                       RubyLanguageTest.EMPTY_STRATEGY_NAME);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.LanguageTestBase#getParameterStrategy()
     */
    @Override
    protected StrategyCoordinates getParameterStrategy()
    {
        return StrategyCoordinates.get(RubyLanguageTest.PARAMETER_STRATEGY,
                                       RubyLanguageTest.PARAMETER_STRATEGY_NAME);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.LanguageTestBase#getSuggestionStrategy()
     */
    @Override
    protected StrategyCoordinates getSuggestionStrategy()
    {
        return StrategyCoordinates.get(RubyLanguageTest.SUGGESTION_STRATEGY,
                                       RubyLanguageTest.SUGGESTION_STRATEGY_NAME);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.LanguageTestBase#getMessageStrategy()
     */
    @Override
    protected StrategyCoordinates getMessageStrategy()
    {
        return StrategyCoordinates.get(RubyLanguageTest.MESSAGE_STRATEGY,
                                       RubyLanguageTest.MESSAGE_STRATEGY_NAME);
    }
   /* (non-Javadoc)
     * @see org.marketcetera.strategy.LanguageTestBase#getPart1Strategy()
     */
    @Override
    protected StrategyCoordinates getPart1Strategy()
    {
        return StrategyCoordinates.get(RubyLanguageTest.PART1_STRATEGY,
                                       RubyLanguageTest.PART1_STRATEGY_NAME);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.LanguageTestBase#getPart2Strategy()
     */
    @Override
    protected StrategyCoordinates getPart2Strategy()
    {
        return StrategyCoordinates.get(RubyLanguageTest.PART2_STRATEGY,
                                       RubyLanguageTest.PART2_STRATEGY_NAME);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.LanguageTestBase#getPart1RedefinedStrategy()
     */
    @Override
    protected StrategyCoordinates getPart1RedefinedStrategy()
    {
        return StrategyCoordinates.get(RubyLanguageTest.PART1_REDEFINED_STRATEGY,
                                       RubyLanguageTest.PART1_STRATEGY_NAME);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.LanguageTestBase#getOrdersStrategy()
     */
    @Override
    protected StrategyCoordinates getOrdersStrategy()
    {
        return StrategyCoordinates.get(RubyLanguageTest.ORDER_STRATEGY,
                                       RubyLanguageTest.ORDER_STRATEGY_NAME);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.LanguageTestBase#getEventStrategy()
     */
    @Override
    protected StrategyCoordinates getEventStrategy()
    {
        return StrategyCoordinates.get(RubyLanguageTest.EVENT_STRATEGY,
                                       RubyLanguageTest.EVENT_STRATEGY_NAME);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.LanguageTestBase#getCombinedStrategy()
     */
    @Override
    protected StrategyCoordinates getCombinedStrategy()
    {
        return StrategyCoordinates.get(RubyLanguageTest.COMBINED_STRATEGY,
                                       RubyLanguageTest.COMBINED_STRATEGY_NAME);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.LanguageTestBase#getOtherStrategy()
     */
    @Override
    protected StrategyCoordinates getOtherStrategy()
    {
        return StrategyCoordinates.get(RubyLanguageTest.OTHER_STRATEGY,
                                       RubyLanguageTest.OTHER_STRATEGY_NAME);
    }
    /**
     * Gets a strategy which requires other strategies.
     *
     * @return a <code>StrategyCoordinates</code> value
     */
    private StrategyCoordinates getRequiresStrategy()
    {
        return StrategyCoordinates.get(REQUIRES_STRATEGY,
                                       REQUIRES_STRATEGY_NAME);
    }
    /**
     * Gets a strategy which has a syntax error in on_start to test serialization of LogEvent
     * 
     * @return a <code>StrategyCoordinates</code> value
     */
     private StrategyCoordinates getBadOnStartStrategy()
     {
         return StrategyCoordinates.get(BAD_ON_START_STRATEGY, 
                                        BAD_ON_START_STRATEGY_NAME);
     }
     /**
      * Gets a strategy which has a syntax error in the on_stop to test serialization of LogEvent
      * 
      * @return a <code>StrategyCoordinates</code> value
      */
     private StrategyCoordinates getBadOnStopStrategy()
     {
         return StrategyCoordinates.get(BAD_ON_STOP_STRATEGY,
                                        BAD_ON_STOP_STRATEGY_NAME);
     }
     /* (non-Javadoc)
      * @see org.marketcetera.strategy.LanguageTestBase#getDataFlowStrategy()
      */
     @Override
     protected StrategyCoordinates getDataFlowStrategy()
     {
         return StrategyCoordinates.get(DATA_FLOW_STRATEGY,
                                        DATA_FLOW_STRATEGY_NAME);
     }
     private final String UNSERIALIZABLE_LOG_EVENT = "An unserializable LogEvent was emitted in on_start or on_stop.  See log for details.";
}
