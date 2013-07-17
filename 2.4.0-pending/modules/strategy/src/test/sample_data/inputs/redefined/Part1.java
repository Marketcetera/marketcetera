import org.marketcetera.event.AskEvent;
import org.marketcetera.strategy.java.Strategy;


/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.0.0
 */
public class Part1
        extends Strategy
{
    private Helper helper;
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.java.Strategy#onAsk(org.marketcetera.event.AskEvent)
     */
    @Override
    public void onAsk(AskEvent inAsk)
    {
        setProperty("ask" + this.toString(),
                    "part1");
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.java.Strategy#onCallback(java.lang.Object)
     */
    @Override
    public void onCallback(Object inData)
    {
        int callbackCounter = Integer.parseInt(getProperty("onCallback"));
        callbackCounter += 1;
        setProperty("onCallback",
                    Integer.toString(callbackCounter));
        setProperty("callback" + Integer.toString(callbackCounter),
                    this.toString());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.java.Strategy#onStart()
     */
    @Override
    public void onStart()
    {
        String callbackCounter = getProperty("onCallback");
        if(callbackCounter == null) {
            setProperty("onCallback",
                         "0");
        }
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
