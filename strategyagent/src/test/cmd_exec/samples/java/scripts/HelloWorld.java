package sample;

import org.marketcetera.strategy.java.Strategy;


/* $License$ */
/**
 * Sample Hello World strategy.
 *
 * @author anshul@marketcetera.com
 * @version $Id: HelloWorld.java 16901 2014-05-11 16:14:11Z colin $
 * @since 2.0.0
 */
public class HelloWorld extends Strategy {
    /**
     * Executed when the strategy is started.
     * Use this method to set up data flows
     * and other initialization tasks.
     */
    @Override
    public void onStart() {
        warn("Hello World!");
    }

    /**
     * Executed when the strategy is stopped.
     */
    @Override
    public void onStop() {
        warn("Good Bye!");
    }
}
