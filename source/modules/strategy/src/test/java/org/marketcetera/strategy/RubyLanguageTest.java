package org.marketcetera.strategy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.math.BigDecimal;
import java.util.Properties;
import java.util.Set;

import org.junit.Test;
import org.marketcetera.event.AskEvent;
import org.marketcetera.module.ModuleURN;

/* $License$ */

/**
 * Tests Ruby language support.
 * 
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
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
     * Verifies that a Ruby class which inherits from a Java class may be dynamically redefined. 
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
            runningStrategy.getRunningStrategy().onAsk(new AskEvent(System.nanoTime(),
                                                                    System.currentTimeMillis(),
                                                                    "METC",
                                                                    "Exchange",
                                                                    new BigDecimal("1"),
                                                                    new BigDecimal("2")));
        }
        // both strategies should get their onAsk called, but the definition should be the second one
        Properties properties = AbstractRunningStrategy.getProperties();
        int askCounter = 0;
        for(Object key : properties.keySet()) {
            String keyString = (String)key;
            if(keyString.startsWith("ask")) {
                askCounter += 1;
                assertEquals("part1-redefined",
                             properties.getProperty(keyString));
            }
        }
        assertEquals(2,
                     askCounter);
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
    protected StrategyCoordinates getPart1Strategy()
    {
        return StrategyCoordinates.get(RubyLanguageTest.PART1_STRATEGY,
                                       RubyLanguageTest.PART1_STRATEGY_NAME);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.LanguageTestBase#getPart2Strategy()
     */
    protected StrategyCoordinates getPart2Strategy()
    {
        return StrategyCoordinates.get(RubyLanguageTest.PART2_STRATEGY,
                                       RubyLanguageTest.PART2_STRATEGY_NAME);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.LanguageTestBase#getPart1RedefinedStrategy()
     */
    protected StrategyCoordinates getPart1RedefinedStrategy()
    {
        return StrategyCoordinates.get(RubyLanguageTest.PART1_REDEFINED_STRATEGY,
                                       RubyLanguageTest.PART1_STRATEGY_NAME);
    }
}
