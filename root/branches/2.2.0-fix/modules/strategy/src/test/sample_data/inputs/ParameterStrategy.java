import java.math.BigDecimal;

import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.strategy.java.Strategy;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.OrderSingle;

import quickfix.Message;

/**
 * Test strategy that exercises a strategy's ability to retrieve parameters.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.0.0
 */
public class ParameterStrategy
        extends Strategy
{
    @Override
    public void onAsk(AskEvent ask)
    {
        String askParameter = getParameter("onAsk");
        setProperty("onAsk",
                    askParameter);
    }

    @Override
    public void onBid(BidEvent bid)
    {
        String bidParameter = getParameter("onBid");
        setProperty("onBid",
                    bidParameter);
        String emitSuggestion = getParameter("emitSuggestion");
        if(emitSuggestion != null) {
            OrderSingle suggestedOrder = Factory.getInstance().createOrderSingle();
            suggestTrade(suggestedOrder,
                         new BigDecimal("1.1"),
                         "identifier");
        }
    }

    @Override
    public void onCallback(Object data)
    {
        String callbackParameter = getParameter("onCallback");
        setProperty("onCallback",
                    callbackParameter);
    }

    @Override
    public void onExecutionReport(ExecutionReport executionReport)
    {
        String executionReportParameter = getParameter("onExecutionReport");
        setProperty("onExecutionReport",
                    executionReportParameter);
    }

    @Override
    public void onTrade(TradeEvent trade)
    {
        String tradeParameter = getParameter("onTrade");
        setProperty("onTrade",
                    tradeParameter);
        String emitMessage = getParameter("emitMessage");
        if(emitMessage != null) {
            Message message = FIXVersion.FIX_SYSTEM.getMessageFactory().newBasicOrder();
            sendMessage(message,
                        new BrokerID("some-broker"));
        }
    }

    @Override
    public void onOther(Object data)
    {
        String otherParameter = getParameter("onOther");
        setProperty("onOther",
                    otherParameter);
    }
}
