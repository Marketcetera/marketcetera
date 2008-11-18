package org.marketcetera.strategy;

import java.io.File;

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
}
