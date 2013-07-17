import java.math.BigDecimal;

import org.marketcetera.event.AskEvent;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.strategy.java.Strategy;

/* $License$ */

/**
 * Sample strategy to test the ability to send arbitrary data.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.0.0
 */
public class SendOther
        extends Strategy
{
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.java.Strategy#onAsk(org.marketcetera.event.AskEvent)
     */
    @Override
    public void onAsk(AskEvent inAsk)
    {
        if(getProperty("sendNull") != null) {
            send(null);
        } else if(getProperty("sendString") != null) {
            send("test string");
        } else if(getProperty("sendTwo") != null) {
            send(BigDecimal.ONE);
            send(BigDecimal.TEN);
        }
    }
}
