package org.marketcetera.strategy.java;

import org.marketcetera.core.notifications.Notification;
import org.marketcetera.core.event.*;
import org.marketcetera.strategy.AbstractRunningStrategy;
import org.marketcetera.strategy.RunningStrategy;
import org.marketcetera.core.trade.ExecutionReport;
import org.marketcetera.core.trade.OrderCancelReject;
import org.marketcetera.core.util.misc.ClassVersion;

/* $License$ */

/**
 * {@link RunningStrategy} implementation for Java strategies to extend.  
 * 
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: Strategy.java 16063 2012-01-31 18:21:55Z colin $
 * @since 1.0.0
 */
public class Strategy
        extends AbstractRunningStrategy
{
    /*
     * (non-Javadoc)
     * 
     * @see org.marketcetera.strategy.RunningStrategy#onAsk(org.marketcetera.core.event.AskEvent)
     */
    @Override
    public void onAsk(AskEvent inAsk)
    {
    }
    /*
     * (non-Javadoc)
     * 
     * @see org.marketcetera.strategy.RunningStrategy#onBid(org.marketcetera.core.event.BidEvent)
     */
    @Override
    public void onBid(BidEvent inBid)
    {
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.RunningStrategy#onMarketstat(org.marketcetera.core.event.MarketstatEvent)
     */
    @Override
    public void onMarketstat(MarketstatEvent inStatistics)
    {
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.RunningStrategy#onDividend(org.marketcetera.core.event.DividendEvent)
     */
    @Override
    public void onDividend(DividendEvent inStatistics)
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
     * @see org.marketcetera.strategy.RunningStrategy#onExecutionReport(org.marketcetera.core.event.ExecutionReport)
     */
    @Override
    public void onExecutionReport(ExecutionReport inExecutionReport)
    {
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.RunningStrategy#onCancel(org.marketcetera.core.trade.OrderCancelReject)
     */
    @Override
    public void onCancelReject(OrderCancelReject inCancel)
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
     * @see org.marketcetera.strategy.RunningStrategy#onTrade(org.marketcetera.core.event.TradeEvent)
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
    // services start here
    /**
     * Creates and issues a {@link Notification} at low priority.
     *
     * @param inSubject a <code>String</code> value
     * @param inBody a <code>String</code> value
     */
    protected final void notifyLow(String inSubject,
                                   String inBody)
    {
        sendNotification(Notification.low(inSubject,
                                          inBody,
                                          this.toString()));
    }
    /**
     * Creates and issues a {@link Notification} at medium priority.
     *
     * @param inSubject a <code>String</code> value
     * @param inBody a <code>String</code> value
     */
    protected final void notifyMedium(String inSubject,
                                      String inBody)
    {
        sendNotification(Notification.medium(inSubject,
                                             inBody,
                                             this.toString()));
    }
    /**
     * Creates and issues a {@link Notification} at high priority.
     *
     * @param inSubject a <code>String</code> value
     * @param inBody a <code>String</code> value
     */
    protected final void notifyHigh(String inSubject,
                                    String inBody)
    {
        sendNotification(Notification.high(inSubject,
                                           inBody,
                                           this.toString()));
    }
    /**
     * Emits the given debug message to the strategy log output.
     *
     * @param inMessage a <code>String</code> value
     */
    @Override
    protected final void debug(String inMessage)
    {
        super.debug(inMessage);
    }
    /**
     * Emits the given info message to the strategy log output.
     *
     * @param inMessage a <code>String</code> value
     */
    @Override
    protected final void info(String inMessage)
    {
        super.info(inMessage);
    }
    /**
     * Emits the given warn message to the strategy log output.
     *
     * @param inMessage a <code>String</code> value
     */
    @Override
    protected final void warn(String inMessage)
    {
        super.warn(inMessage);
    }
    /**
     * Emits the given error message to the strategy log output.
     *
     * @param inMessage a <code>String</code> value
     */
    @Override
    protected final void error(String inMessage)
    {
        super.error(inMessage);
    }
    /**
     * Sends an order to order subscribers.
     * 
     * @param inData an <code>Object</code> value
     * @return a <code>boolean</code> value indicating whether the object was successfully transmitted or not
     */
    @Override
    protected final boolean send(Object inData)
    {
        return super.send(inData);
    }
}
