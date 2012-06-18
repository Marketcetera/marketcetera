import java.math.BigDecimal;

import org.marketcetera.event.AskEvent;
import org.marketcetera.event.Event;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.event.impl.TradeEventBuilder;
import org.marketcetera.marketdata.DateUtils;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.strategy.OutputType;
import org.marketcetera.strategy.java.Strategy;
import org.marketcetera.trade.Equity;

import java.util.Date;

/* $License$ */

/**
 * Sample strategy which tests the ability to send events to CEP and event subscribers.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: SendEvent.java 16063 2012-01-31 18:21:55Z colin $
 * @since 1.0.0
 */
public class SendEvent
        extends Strategy
{
    private int askCounter = 0;
    private DataFlowID dataFlowID;
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.java.Strategy#onStart()
     */
    @Override
    public void onStart()
    {
        dataFlowID = createDataFlow(true,
                                    new DataRequest(getURN(),
                                                    OutputType.ALL));
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.java.Strategy#onStart()
     */
    @Override
    public void onStop()
    {
        TradeEvent trade = TradeEventBuilder.equityTradeEvent().withInstrument(new Equity("METC"))
                                                               .withExchange("exchange")
                                                               .withPrice(BigDecimal.ONE)
                                                               .withSize(BigDecimal.TEN)
                                                               .withTradeDate(DateUtils.dateToString(new Date())).create();
        doSendEvent(trade);
        cancelDataFlow(dataFlowID);
    }
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
        doSendEvent((Event)inEvent);
    }
    /**
     * Sends the given event.
     *
     * @param inEvent an <code>Event</code> value
     */
    private void doSendEvent(Event inEvent)
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
