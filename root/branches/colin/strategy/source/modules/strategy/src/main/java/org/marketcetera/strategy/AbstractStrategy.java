package org.marketcetera.strategy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.marketcetera.core.MSymbol;
import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.CancelEvent;
import org.marketcetera.event.EventBase;
import org.marketcetera.event.ExecutionReport;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.marketdata.IMarketDataFeedToken;
import org.marketcetera.old_strategy.IStrategyManager;
import org.marketcetera.systemmodel.Order;
import org.marketcetera.systemmodel.OrderID;
import org.marketcetera.systemmodel.OrderImpl;
import org.marketcetera.systemmodel.Position;

import quickfix.Message;
import quickfix.field.OrdType;
import quickfix.field.Side;
import quickfix.field.TimeInForce;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id:$
 * @since $Release$
 */
public abstract class AbstractStrategy
        implements IStrategy
{
    private static final AtomicInteger sStrategyCounter = new AtomicInteger(0);
    private final int mCounter = sStrategyCounter.getAndIncrement();
    private boolean mIsRunning = false;
    private static final Properties sCommonSpace = new Properties();
    /**
     * Create a new Strategy instance.
     *
     */
    public AbstractStrategy()
    {
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#isRunning()
     */
    @Override
    public final boolean isRunning()
    {
        return mIsRunning;
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#start()
     */
    @Override
    public final void start()
    {
        onStart();
        mIsRunning = true;
        reportStrategyStart();
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#stop()
     */
    @Override
    public final void stop()
    {
        try {
            onStop();
        } finally {
            mIsRunning = false;
            reportStrategyStop();
        }
    }
    /*
     * services
     */
    /**
     * Returns the current time in UTC.
     * 
     * @return a <code>Date</code>
     */
    protected final Date getCurrentTime()
    {
        return new Date();
    }
    /**
     * Gets the <code>ExecutionReport</code> objects for the given order.
     * 
     * @param inOrderHandle a <code>String</code> value
     * @return a <code>List&lt;ExecutionReport&gt;</code> value
     */
    protected final List<ExecutionReport> getExecutionReport(OrderID inOrderID)
    {
        // TODO
        return new ArrayList<ExecutionReport>();
    }
    /**
     * Gets the current position in the given security.
     * 
     * @param inSecurity a <code>String</code> value
     * @return an <code>IPosition</code> value
     */
    protected final Position getCurrentPositionAtOpen(final MSymbol inSecurity)
    
    {
        return new Position(){
            @Override
            public Date asOf()
            {
                return new Date();
            }
            @Override
            public BigDecimal getQuantity()
            {
                return new BigDecimal("100.00");
            }
            @Override
            public MSymbol getSymbol()
            {
                return inSecurity;
            }};
    }
    /**
     * Gets the current position for the given security.
     * 
     * @param inSecurity
     * @return
     */
    protected final Position getCurrentPosition(final MSymbol inSecurity)
    {
        return new Position(){
            @Override
            public Date asOf()
            {
                return new Date();
            }
            @Override
            public BigDecimal getQuantity()
            {
                return new BigDecimal("100.00");
            }
            @Override
            public MSymbol getSymbol()
            {
                return inSecurity;
            }};
    }
    /*
     * actions
     */
    /**
     * Sends an order create message for the given order. 
     * 
     * @param inOrder an <code>IOrder</code> value
     * @return an <code>OrderID</code> value containing the order handle
     */
    protected final OrderID sendOrder(Order inOrder)
    {
        return new OrderID(){};
    }
    protected final Order createOrder(Date inTransactTime,
                                      MSymbol inSymbol,
                                      BigDecimal inQuantity,
                                      BigDecimal inPrice,
                                      Side inSide,
                                      TimeInForce inTimeInForce,
                                      OrdType inOrderType,
                                      String inAccount)
    {
        return new OrderImpl(inSymbol,
                             inSide,
                             inOrderType,
                             inQuantity,
                             inPrice,
                             inTimeInForce,
                             inAccount);
    }
    /**
     * Sends a cancel request for the given order.
     * 
     * @param inOrderHandle an <code>OrderID</code> value
     */
    protected final void cancelOrder(OrderID inOrderID)
    {
    }
    /**
     * Sends a cancel-replace request for the given order.
     * 
     * @param inOrderID an <code>OrderID</code> value indicating the order to cancel
     * @param inOrder an <code>Order</code> value indicating the order to replace the canceled order
     * @return an <code>OrderID</code> value containing the order handle
     */
    protected final OrderID cancelReplaceOrder(OrderID inOrderID,
                                               Order inOrder)
    {
        return new OrderID(){};
    }
    /**
     * Sends cancel requests for all open orders created by this <code>Strategy</code>.
     */
    protected final void cancelAllOrders()
    {
    }
    /**
     * Sends the given FIX message.
     * 
     * @param inMessage a <code>Message</code> value
     */
    protected final void sendMessage(Message inMessage)
    {
    }
    /**
     * Suggests a trade to subscribers.
     * 
     * <p>The given suggestion representing a trade will be sent to all subscribers
     * of this <code>IStrategy</code> object's parent {@link IStrategyManager}.
     *
     * @param inSuggestion a <code>Suggestion</code> value
     */
    protected final void suggestTrade(TradeSuggestion inSuggestion)
    {
    }
    /**
     * Sends an event to the <code>Complex Event Processor</code>.
     * 
     * @param inEvent an <code>EventBase</code> value
     */
    protected final void sendEvent(EventBase inEvent)
    {
    }
    /**
     * Schedules a callback.
     * 
     * @param inDelay a <code>long</code> value containing the number of milliseconds before
     * the callback is executed.
     * @see {@link #onCallback(Object)}
     */
    protected final void requestCallback(long inDelay,
                                         TimeUnit inTimeUnit,
                                         Object inData)
    {
    }
    /**
     * Subscribes to news events for the given security.
     * 
     * @param inSecurity a <code>MSymbol</code> value
     */
    protected final void subscribeToNews(MSymbol inSecurity)
    {
    }
    /**
     * Subscribes to market data events for the given security.
     * 
     * @param inSecurity a <code>MSymbol</code> value
     * @return an <code>IMarketDataFeedToken</code> value representing a handle to the subscription
     */
    @SuppressWarnings("unchecked")
    protected final IMarketDataFeedToken subscribeToMarketData(MSymbol inSecurity)
    {
        // TODO
        return null;
    }
    /**
     * Cancels future updates from the given subscription.
     *
     * @param inToken an <code>IMarketDataFeedToken</code> value representing a handle to a subscription
     */
    @SuppressWarnings("unchecked")
    protected final void cancelSubscription(IMarketDataFeedToken inToken)
    {
    }
    protected static void setProperty(String inKey,
                                      String inValue)
    {
        synchronized(sCommonSpace) {
            sCommonSpace.setProperty(inKey,
                                     inValue);
        }
    }
    protected static String getProperty(String inKey)
    {
        synchronized(sCommonSpace) {
            return sCommonSpace.getProperty(inKey);
        }
    }
    protected void onStart()
    {
    }
    protected void onStop()
    {
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + mCounter;
        return result;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final AbstractStrategy other = (AbstractStrategy) obj;
        if (mCounter != other.mCounter)
            return false;
        return true;
    }
    private void reportStrategyStart()
    {
        StrategyManager.getInstance().reportStrategyRunning(this);
    }
    private void reportStrategyStop()
    {
        StrategyManager.getInstance().reportStrategyStopping(this);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.IStrategy#getStrategyID()
     */
    @Override
    public int getStrategyID()
    {
        return mCounter;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.IStrategy#onAsk(org.marketcetera.event.AskEvent)
     */
    @Override
    public void onAsk(AskEvent inAsk)
    {
        // TODO Auto-generated method stub
        
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.IStrategy#onBid(org.marketcetera.event.BidEvent)
     */
    @Override
    public void onBid(BidEvent inBid)
    {
        // TODO Auto-generated method stub
        
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.IStrategy#onCallback()
     */
    @Override
    public void onCallback(Object inData)
    {
        // TODO Auto-generated method stub
        
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.IStrategy#onCancel(org.marketcetera.event.CancelEvent)
     */
    @Override
    public void onCancel(CancelEvent inCancel)
    {
        // TODO Auto-generated method stub
        
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.IStrategy#onExecutionReport(org.marketcetera.event.ExecutionReport)
     */
    @Override
    public void onExecutionReport(ExecutionReport inExecutionReport)
    {
        // TODO Auto-generated method stub
        
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.IStrategy#onNews(org.marketcetera.core.MSymbol, java.lang.String)
     */
    @Override
    public void onNews(MSymbol inSecurity,
                       String inText)
    {
        // TODO Auto-generated method stub
        
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.IStrategy#onTrade(org.marketcetera.event.TradeEvent)
     */
    @Override
    public void onTrade(TradeEvent inTrade)
    {
        // TODO Auto-generated method stub
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.IStrategy#onOther(java.lang.Object)
     */
    @Override
    public void onOther(Object inEvent)
    {
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public final String toString()
    {
        StringBuilder output = new StringBuilder();
        output.append("Strategy ").append(getStrategyID());
        return output.toString();
    }
}
