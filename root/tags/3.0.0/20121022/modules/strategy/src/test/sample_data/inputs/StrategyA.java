import org.marketcetera.strategy.java.Strategy;
import org.marketcetera.test.StrategyB;

/* $License$ */

/**
 * Strategy which depends on another strategy to compile. 
 *
 * @version $Id: StrategyA.java 16063 2012-01-31 18:21:55Z colin $
 * @since 2.1.0
 */
public class StrategyA
        extends Strategy
{
    private static final StrategyB strategyB = new StrategyB();
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.java.Strategy#onStart()
     */
    @Override
    public void onStart()
    {
        setProperty("onStart",
                    Long.toString(System.currentTimeMillis()));
    }
}
