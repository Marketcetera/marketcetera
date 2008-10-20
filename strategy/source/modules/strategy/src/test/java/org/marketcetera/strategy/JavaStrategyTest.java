package org.marketcetera.strategy;

import static org.marketcetera.strategy.StrategyLanguage.JAVA;

import java.io.File;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id:$
 * @since $Release$
 */
public class JavaStrategyTest
    extends StrategyTypeTestBase
{
    static final String STRATEGY_NAME = "JavaStrategy";
    static final String BAD_STRATEGY_NAME = "BadJavaStrategy";
    static final String STRATEGY_FILENAME = STRATEGY_NAME + ".java";
    static final String BAD_STRATEGY_FILENAME = BAD_STRATEGY_NAME + ".java";
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.StrategyTypeTestBase#getLanguage()
     */
    @Override
    protected StrategyLanguage getLanguage()
    {
        return JAVA;
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
                        "SimpleStrategy.java");
    }
}
