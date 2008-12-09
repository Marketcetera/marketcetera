import java.util.Date;

import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.strategy.java.Strategy;
import org.marketcetera.trade.BrokerID;

import quickfix.Message;
import quickfix.field.TransactTime;

/**
 * Test strategy to send messages via the FIX escape hatch.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.0.0
 */
public class SendMessage
        extends Strategy
{
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.java.Strategy#onStart()
     */
    @Override
    public void onStart()
    {
        long messageDate = Long.parseLong(getParameter("date"));
        String nullMessage = getParameter("nullMessage");
        Message message;
        if(nullMessage == null) {
            message = FIXVersion.FIX_SYSTEM.getMessageFactory().newBasicOrder();
            message.setField(new TransactTime(new Date(messageDate)));
        } else {
            message = null;
        }
        String nullBroker = getParameter("nullBroker");
        BrokerID broker;
        if(nullBroker == null) {
            broker = new BrokerID("some-broker");
        } else {
            broker = null;
        }
        sendMessage(message,
                    broker);
        setProperty("onStart",
                    Long.toString(System.currentTimeMillis()));
    }
}
