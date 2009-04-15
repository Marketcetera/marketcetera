package org.marketcetera.strategy;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.MarketstatEvent;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.OrderCancelReject;

/* $License$ */

/**
 * An interface to a running strategy that facilitates communication to the strategy. 
 * 
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public interface RunningStrategy
{
    /**
     * Indicates a <code>TradeEvent</code> has been received.
     * 
     * @param inTrade a <code>TradeEvent</code> value
     */
    public void onTrade(TradeEvent inTrade);
    /**
     * Indicates a <code>BidEvent</code> has been received.
     * 
     * @param inBid a <code>BidEvent</code> value
     */
    public void onBid(BidEvent inBid);
    /**
     * Indicates an <code>AskEvent</code> has been received.
     * 
     * @param inAsk an <code>AskEvent</code> value
     */
    public void onAsk(AskEvent inAsk);
    /**
     * Indicates a <code>MarketstatEvent</code> has been received. 
     *
     * @param inStatistics a <code>MarketstatEvent</code> value
     */
    public void onMarketstat(MarketstatEvent inStatistics);
    /**
     * Indicates an <code>ExecutionReport</code> has been received.
     * 
     * @param inExecutionReport an <code>ExecutionReport</code> value
     */
    public void onExecutionReport(ExecutionReport inExecutionReport);
    /**
     * Indicates an <code>OrderCancelReject</code> has been received.
     *
     * @param inCancelReject an <code>OrderCancelReject</code> value
     */
    public void onCancelReject(OrderCancelReject inCancelReject);
    /**
     * Indicates an event has occurred that does not fit any of the other callbacks.
     * 
     * @param inEvent an <code>Object</code> value
     */
    public void onOther(Object inEvent);
    /**
     * Indicates a scheduled callback has been executed.
     * 
     * @param inData an <code>Object</code> value passed to the request for callback method 
     */
    public void onCallback(Object inData);
    /**
     * Called when a strategy is started.
     */
    public void onStart();
    /**
     * Called when a strategy is about to be stopped.
     */
    public void onStop();
}
