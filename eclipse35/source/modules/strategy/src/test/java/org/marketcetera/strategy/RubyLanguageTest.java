package org.marketcetera.strategy;

import java.io.File;

import org.junit.Test;
import org.marketcetera.module.ModuleURN;

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
}
