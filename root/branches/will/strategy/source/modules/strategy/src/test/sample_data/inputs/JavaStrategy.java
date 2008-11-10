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
     * @see
     * org.marketcetera.strategy.java.JavaStrategy#onAsk(org.marketcetera.event
     * .AskEvent)
     */
    public void onAsk(AskEvent inAsk)
    {
        inAsk.toString();
        setProperty("onAsk",
                    Long.toString(System.nanoTime()));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.marketcetera.strategy.java.JavaStrategy#onBid(org.marketcetera.event
     * .BidEvent)
     */
    public void onBid(BidEvent inBid)
    {
        inBid.toString();
        setProperty("onBid",
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
        setProperty("onCallback",
                    Long.toString(System.nanoTime()));
        // execute all services
        setProperty("getCurrentTime",
                    getCurrentTime().toString());
        setProperty("getExecutionReport",
                    Arrays.toString(getExecutionReport(null).toArray()));
        setProperty("getCurrentPositionAtOpen",
                    getCurrentPositionAtOpen(new MSymbol("symbol")).toString());
        setProperty("getCurrentPosition",
                    getCurrentPosition(new MSymbol("symbol")).toString());
        System.out.println("onCallback!");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.marketcetera.strategy.java.JavaStrategy#onCancel(org.marketcetera
     * .event.CancelEvent)
     */
    public void onCancel(CancelEvent inCancel)
    {
        inCancel.toString();
        setProperty("onCancel",
                    Long.toString(System.nanoTime()));
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.marketcetera.strategy.java.JavaStrategy#onExecutionReport(org.
     * marketcetera.event.ExecutionReport)
     */
    public void onExecutionReport(ExecutionReport inExecutionReport)
    {
        inExecutionReport.toString();
        setProperty("onExecutionReport",
                    Long.toString(System.nanoTime()));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.marketcetera.strategy.java.JavaStrategy#onNews(org.marketcetera.core
     * .MSymbol, java.lang.String)
     */
    public void onNews(MSymbol inSecurity,
                       String inText)
    {
        inSecurity.toString();
        inText.toString();
        setProperty("onNews",
                    Long.toString(System.nanoTime()));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.marketcetera.strategy.java.JavaStrategy#onTrade(org.marketcetera.
     * event.TradeEvent)
     */
    public void onTrade(TradeEvent inTrade)
    {
        inTrade.toString();
        setProperty("onTrade",
                    Long.toString(System.nanoTime()));
    }
}
