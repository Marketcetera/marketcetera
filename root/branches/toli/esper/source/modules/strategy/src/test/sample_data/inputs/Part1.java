import org.marketcetera.event.AskEvent;
import org.marketcetera.strategy.java.Strategy;

/* $License$ */

/**
 * Test strategy (part one of two parts) that tests what happens when a strategy helper-class is redefined. 
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
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
        setProperty("ask" + this,
                    "part1");
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.java.Strategy#onCallback(java.lang.Object)
     */
    @Override
    public void onCallback(Object inData)
    {
        long callbackCounter = Long.parseLong(getProperty("onCallback"));
        callbackCounter += 1;
        setProperty("onCallback",
                    Long.toString(callbackCounter));
        setProperty("callback" + Long.toString(callbackCounter),
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
        helper.doSomethingPart1();      
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.java.Strategy#onStop()
     */
    @Override
    public void onStop()
    {
        helper.doSomethingCommon();
        helper.doSomethingPart1();
    }

    private static class Helper
    {
        private void doSomethingCommon()
        {
        }  
        private void doSomethingPart1()
        {
        }
    }
}