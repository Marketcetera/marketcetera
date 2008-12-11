import org.marketcetera.event.AskEvent;
import org.marketcetera.event.EventBase;
import org.marketcetera.strategy.java.Strategy;

/* $License$ */

/**
 * Sample strategy which tests the ability to send events to CEP and event subscribers.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.0.0
 */
public class SendEvent
        extends Strategy
{
    private int askCounter = 0;
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.java.Strategy#onAsk(org.marketcetera.event.AskEvent)
     */
    @Override
    public void onAsk(AskEvent inAsk)
    {
        askCounter += 1;
        setProperty("askCount",
                    Integer.toString(askCounter));
        setProperty("ask",
                    inAsk.toString());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.java.Strategy#onCallback(java.lang.Object)
     */
    @Override
    public void onCallback(Object inData)
    {
        if(getProperty("shouldRequestCEPData") != null) {
            doCepRequest();
        } else {
            cancelAllDataRequests();
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.java.Strategy#onOther(java.lang.Object)
     */
    @Override
    public void onOther(Object inEvent)
    {
        doSendEvent((EventBase)inEvent);
    }
    /**
     * Sends the given event.
     *
     * @param inEvent an <code>EventBase</code> value
     */
    private void doSendEvent(EventBase inEvent)
    {
        if(getProperty("eventOnlyTest") != null) {
            if(getProperty("nilEvent") != null) {
                sendEvent(null);
                return;
            }
            sendEvent(inEvent);
        } else {
            String source = getProperty("source");
            if(getProperty("nilSource") != null) {
                sendEventToCEP(inEvent,
                               null);  
                return;
            }
            if(getProperty("nilEvent") != null) {
                sendEventToCEP(null,
                               source);
                return;
            }
            sendEventToCEP(inEvent,
                           source);
        }
    }
    /**
     * Sets up a CEP request.
     */
    private void doCepRequest()
    {
        String cepDataSource = getProperty("source");
        String statementString = null;
        if(cepDataSource != null) {
            statementString = getProperty("statements");
        }
        String[] statements;
        if(statementString != null) { 
            statements = statementString.split("#");
        } else { 
            statements = null;
        }
        setProperty("requestID",
                    Long.toString(requestCEPData(statements,
                                                 cepDataSource)));
    }
}
