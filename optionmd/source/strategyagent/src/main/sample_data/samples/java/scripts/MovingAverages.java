import org.marketcetera.strategy.java.Strategy;

import java.util.Map;
import java.math.BigDecimal;

/* $License$ */
/**
 * Strategy that prints a message whenever a symbol's
 * moving average crosses its closing price.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
public class MovingAverages extends Strategy {
    /**
     * Executed when the strategy receives any other event.
     *
     * @param inEvent the received event.
     */
    @Override
    public void onOther(Object inEvent) {
        //Multi Column selects from cep query result in Map events.
        //the map keys correspond to the column names used in the cep query.
        if (inEvent instanceof Map) {
            Map map = (Map) inEvent;
            Double average = (Double) map.get("average");
            Double close = (Double) map.get("close");
            String date = (String) map.get("date");
            if(close.compareTo(average) < 0 && mWasAbove) {
                mWasAbove = false;
                warn(String.format("Close %f crossed below average %f on %s",close, average, date));
            } else if(close.compareTo(average) > 0 && (!mWasAbove)) {
                mWasAbove = true;
                warn(String.format("Close %f crossed above average %f on %s",close, average, date));
            }
        }
    }
    private boolean mWasAbove = false;
}
