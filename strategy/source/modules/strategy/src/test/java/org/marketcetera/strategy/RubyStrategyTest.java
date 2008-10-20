package org.marketcetera.strategy;

import java.io.File;


/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id:$
 * @since $Release$
 */
public class RubyStrategyTest
        extends StrategyTypeTestBase
{
    private static final String STRATEGY_NAME = "RubyStrategy";
    private static final String BAD_STRATEGY_NAME = "BadRubyStrategy";
    private static final String STRATEGY_FILENAME = STRATEGY_NAME + ".rb";
    private static final String BAD_STRATEGY_FILENAME = BAD_STRATEGY_NAME + ".rb";
    /**
     * Create a new RubyStrategyTest instance.
     *
     */
    public RubyStrategyTest()
    {
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.StrategyTypeTestBase#getLanguage()
     */
    @Override
    protected StrategyLanguage getLanguage()
    {
        return StrategyLanguage.JRUBY;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.StrategyTypeTestBase#setupStrategyFiles()
     */
    @Override
    protected void setupStrategyFiles()
    {
        goodStrategy = new File(SAMPLE_STRATEGY_DIR,
                                STRATEGY_FILENAME);
        goodStrategyName = STRATEGY_NAME;
        badStrategy = new File(SAMPLE_STRATEGY_DIR,
                                BAD_STRATEGY_FILENAME);
        badStrategyName = BAD_STRATEGY_NAME;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.StrategyTypeTestBase#getSimpleStrategy()
     */
    @Override
    protected File getSimpleStrategy()
    {
        return new File(SAMPLE_STRATEGY_DIR,
                        "simple_strategy.rb");
    }
}
