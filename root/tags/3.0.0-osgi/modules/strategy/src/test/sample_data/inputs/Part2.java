import org.marketcetera.strategy.java.Strategy;


/* $License$ */

/**
 * Test strategy (part two of two parts) that tests what happens when a strategy helper-class is redefined. 
 *
 * @version $Id: Part2.java 16063 2012-01-31 18:21:55Z colin $
 * @since 1.0.0
 */
public class Part2
        extends Strategy
{
    private Helper helper;
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.java.Strategy#onStart()
     */
    @Override
    public void onStart()
    {
        helper = new Helper();
        helper.doSomethingCommon();
        helper.doSomethingPart2();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.java.Strategy#onStop()
     */
    @Override
    public void onStop()
    {
        helper.doSomethingCommon();
        helper.doSomethingPart2();
    }

    private static class Helper
    {
        private void doSomethingCommon()
        {
        }  
        private void doSomethingPart2()
        {
        }
    }
}
