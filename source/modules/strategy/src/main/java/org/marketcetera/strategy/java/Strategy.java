package org.marketcetera.strategy.java;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.strategy.AbstractRunningStrategy;
import org.marketcetera.strategy.RunningStrategy;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.OrderCancelReject;

/* $License$ */

/**
 * {@link RunningStrategy} implementation for Java strategies to extend.  
 * 
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class Strategy
        extends AbstractRunningStrategy
{
    /*
     * (non-Javadoc)
     * 
     * @see org.marketcetera.strategy.RunningStrategy#onAsk(org.marketcetera.event.AskEvent)
     */
    @Override
    public void onAsk(AskEvent inAsk)
    {
    }
    /*
     * (non-Javadoc)
     * 
     * @see org.marketcetera.strategy.RunningStrategy#onBid(org.marketcetera.event.BidEvent)
     */
    @Override
    public void onBid(BidEvent inBid)
    {
    }
    /*
     * (non-Javadoc)
     * 
     * @see org.marketcetera.strategy.RunningStrategy#onCallback(java.lang.Object)
     */
    @Override
    public void onCallback(Object inData)
    {
    }
    /*
     * (non-Javadoc)
     * 
     * @see org.marketcetera.strategy.RunningStrategy#onExecutionReport(org.marketcetera.event.ExecutionReport)
     */
    @Override
    public void onExecutionReport(ExecutionReport inExecutionReport)
    {
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.RunningStrategy#onCancel(org.marketcetera.trade.OrderCancelReject)
     */
    @Override
    public void onCancel(OrderCancelReject inCancel)
    {
    }
    /*
     * (non-Javadoc)
     * 
     * @see org.marketcetera.strategy.RunningStrategy#onOther(java.lang.Object)
     */
    @Override
    public void onOther(Object inEvent)
    {
    }
    /*
     * (non-Javadoc)
     * 
     * @see org.marketcetera.strategy.RunningStrategy#onTrade(org.marketcetera.event.TradeEvent)
     */
    @Override
    public void onTrade(TradeEvent inTrade)
    {
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.RunningStrategy#onStart()
     */
    @Override
    public void onStart()
    {
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.RunningStrategy#onStop()
     */
    @Override
    public void onStop()
    {
    }
}
