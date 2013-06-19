import java.util.Date;

import org.marketcetera.core.quickfix.FIXVersion;
import org.marketcetera.strategy.java.Strategy;
import org.marketcetera.core.trade.BrokerID;

import quickfix.Message;
import quickfix.field.TransactTime;

/**
 * Test strategy to send messages via the FIX escape hatch.
 *
 * @version $Id: SendMessage.java 16063 2012-01-31 18:21:55Z colin $
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
        doSend();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.java.Strategy#onStop()
     */
    @Override
    public void onStop()
    {
        doSend();
    }
    /**
     * Sends messages according to strategy parameters.
     */
    private void doSend()
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
