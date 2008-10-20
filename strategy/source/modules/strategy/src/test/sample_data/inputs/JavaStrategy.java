import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.lang.Override;

import org.marketcetera.core.MSymbol;
import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.CancelEvent;
import org.marketcetera.event.ExecutionReport;
import org.marketcetera.event.TradeEvent;

public class JavaStrategy
        extends org.marketcetera.strategy.java.Strategy
{
    /*
     * (non-Javadoc)
     * 
     * @see org.marketcetera.strategy.java.JavaStrategy#onAsk(org.marketcetera.event.AskEvent)
     */
    public void onAsk(AskEvent inAsk)
    {
        inAsk.toString();
        setCommonProperty("onAsk",
                          Long.toString(System.nanoTime()));
    }
    /*
     * (non-Javadoc)
     * 
     * @see org.marketcetera.strategy.java.JavaStrategy#onBid(org.marketcetera.event.BidEvent)
     */
    public void onBid(BidEvent inBid)
    {
        inBid.toString();
        setCommonProperty("onBid",
                          Long.toString(System.nanoTime()));
    }
    /*
     * (non-Javadoc)
     * 
     * @see org.marketcetera.strategy.java.JavaStrategy#onCallback()
     */
    public void onCallback(Object inData)
    {
        List strings = new ArrayList();
        setCommonProperty("onCallback",
                          Long.toString(System.nanoTime()));
        // execute all services
        setCommonProperty("getCurrentTime",
                          getCurrentTime().toString());
        setCommonProperty("getExecutionReport",
                          Arrays.toString(getExecutionReport(null).toArray()));
        setCommonProperty("getCurrentPositionAtOpen",
                          getCurrentPositionAtOpen(new MSymbol("symbol")).toString());
        setCommonProperty("getCurrentPosition",
                          getCurrentPosition(new MSymbol("symbol")).toString());
        setCommonProperty("getGoal",
                          getGoal(new MSymbol("symbol")).toString());
    }
    /*
     * (non-Javadoc)
     * 
     * @see org.marketcetera.strategy.java.JavaStrategy#onCancel(org.marketcetera.event.CancelEvent)
     */
    public void onCancel(CancelEvent inCancel)
    {
        inCancel.toString();
        setCommonProperty("onCancel",
                          Long.toString(System.nanoTime()));
    }
    /*
     * (non-Javadoc)
     * 
     * @see org.marketcetera.strategy.java.JavaStrategy#onExecutionReport(org.marketcetera.event.ExecutionReport)
     */
    public void onExecutionReport(ExecutionReport inExecutionReport)
    {
        inExecutionReport.toString();
        setCommonProperty("onExecutionReport",
                          Long.toString(System.nanoTime()));
    }
    /*
     * (non-Javadoc)
     * 
     * @see org.marketcetera.strategy.java.JavaStrategy#onNews(org.marketcetera.core.MSymbol,
     *      java.lang.String)
     */
    public void onNews(MSymbol inSecurity,
                       String inText)
    {
        inSecurity.toString();
        inText.toString();
        setCommonProperty("onNews",
                          Long.toString(System.nanoTime()));
    }
    /*
     * (non-Javadoc)
     * 
     * @see org.marketcetera.strategy.java.JavaStrategy#onTrade(org.marketcetera.event.TradeEvent)
     */
    public void onTrade(TradeEvent inTrade)
    {
        inTrade.toString();
        setCommonProperty("onTrade",
                          Long.toString(System.nanoTime()));
    }
}
