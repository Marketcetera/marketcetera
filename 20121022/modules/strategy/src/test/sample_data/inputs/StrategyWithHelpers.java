import java.lang.Override;

import org.marketcetera.core.event.AskEvent;
import org.marketcetera.core.event.BidEvent;
import org.marketcetera.core.event.DividendEvent;
import org.marketcetera.core.event.TradeEvent;
import org.marketcetera.core.trade.ExecutionReport;
import org.marketcetera.core.trade.OrderCancelReject;
import org.marketcetera.strategy.java.Strategy;

public class StrategyWithHelpers
    extends Strategy
{
    private Helper helper = new Helper();
    @Override
    public void onStart()
    {
    }

    @Override
    public void onStop()
    {
    }

    @Override
    public void onAsk(AskEvent ask)
    {
        helper.doesSomething(this,
                             "onAsk",
                             ask.toString());
    }

    @Override
    public void onBid(BidEvent bid)
    {
        helper.doesSomething(this,
                             "onBid",
                             bid.toString());
    }

    @Override
    public void onCallback(Object data)
    {
        helper.doesSomething(this,
                             "onCallback",
                             data.toString());
    }

    @Override
    public void onExecutionReport(ExecutionReport executionReport)
    {
        helper.doesSomething(this,
                             "onExecutionReport",
                             executionReport.toString());
    }  

    @Override
    public void onCancelReject(OrderCancelReject cancel)
    {
        helper.doesSomething(this,
                             "onCancel",
                             cancel.toString());
    }

    @Override
    public void onTrade(TradeEvent trade)
    {
        helper.doesSomething(this,
                             "onTrade",
                             trade.toString());
    }

    @Override
    public void onOther(Object data)
    {
        helper.doesSomething(this,
                             "onOther",
                             data.toString());
    }
    
    @Override
    public void onDividend(DividendEvent dividend)
    {
        helper.doesSomething(this,
                             "onDividend",
                             dividend.toString());
    }

    private static class Helper
    {
        private void doesSomething(Strategy inStrategy,
                                   String inKey,
                                   Object inData)
        {
            inStrategy.setProperty(inKey,
                                   inData.toString());
        }
    }
}
