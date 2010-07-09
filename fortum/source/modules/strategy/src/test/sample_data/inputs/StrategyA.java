import org.marketcetera.strategy.java.Strategy;
import org.marketcetera.test.StrategyB;

/* $License$ */

/**
 * Strategy which depends on another strategy to compile. 
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
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
