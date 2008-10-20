package org.marketcetera.strategy.ruby;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.marketcetera.core.MSymbol;
import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.CancelEvent;
import org.marketcetera.event.EventBase;
import org.marketcetera.event.ExecutionReport;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.marketdata.IMarketDataFeedToken;
import org.marketcetera.strategy.AbstractStrategy;
import org.marketcetera.systemmodel.Order;
import org.marketcetera.systemmodel.OrderID;
import org.marketcetera.systemmodel.Position;
import org.marketcetera.systemmodel.TradeSuggestion;

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
abstract class RubyStrategy
        extends AbstractStrategy
{
    /**
     * Create a new RubyStrategy instance.
     *
     */
    public RubyStrategy()
    {
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.IStrategy#onAsk(org.marketcetera.event.AskEvent)
     */
    @Override
    public void onAsk(AskEvent inAsk)
    {
        on_ask(inAsk);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.IStrategy#onBid(org.marketcetera.event.BidEvent)
     */
    @Override
    public void onBid(BidEvent inBid)
    {
        on_bid(inBid);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.IStrategy#onCallback()
     */
    @Override
    public void onCallback(Object inData)
    {
        on_callback(inData);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.IStrategy#onCancel(org.marketcetera.event.CancelEvent)
     */
    @Override
    public void onCancel(CancelEvent inCancel)
    {
        on_cancel(inCancel);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.IStrategy#onExecutionReport(org.marketcetera.event.ExecutionReport)
     */
    @Override
    public void onExecutionReport(ExecutionReport inExecutionReport)
    {
        on_execution_report(inExecutionReport);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.IStrategy#onNews(org.marketcetera.core.MSymbol, java.lang.String)
     */
    @Override
    public void onNews(MSymbol inSecurity,
                       String inText)
    {
        on_news(inSecurity,
                inText);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.IStrategy#onTrade(org.marketcetera.event.TradeEvent)
     */
    @Override
    public void onTrade(TradeEvent inTrade)
    {
        on_trade(inTrade);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.AbstractStrategy#onOther(java.lang.Object)
     */
    @Override
    public void onOther(Object inEvent)
    {
        on_other(inEvent);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.AbstractStrategy#onStart()
     */
    @Override
    protected final void onStart()
    {
        on_start();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.AbstractStrategy#onStop()
     */
    @Override
    protected final void onStop()
    {
        on_stop();
    }
    public final void cancel_all_orders()
    {
        super.cancelAllOrders();
    }
    public final void cancel_order(OrderID inOrderID)
    {
        super.cancelOrder(inOrderID);
    }
    public final OrderID cancel_replace_order(OrderID inOrderID,
                                              Order inOrder)
    {
        return super.cancelReplaceOrder(inOrderID,
                                        inOrder);
    }
    public final Position get_current_position(MSymbol inSecurity)
    {
        return super.getCurrentPosition(inSecurity);
    }
    public final Position get_current_position_at_open(MSymbol inSecurity)
    {
        return super.getCurrentPositionAtOpen(inSecurity);
    }
    public final Date get_current_time()
    {
        return super.getCurrentTime();
    }
    public final List<ExecutionReport> get_execution_report(OrderID inOrderID)
    {
        return super.getExecutionReport(inOrderID);
    }
    public final void request_callback(long inDelay,
                                       TimeUnit inUnit,
                                       Object inData)
    {
        super.requestCallback(inDelay,
                              inUnit,
                              inData);
    }
    public final void send_event(EventBase inEvent)
    {
        super.sendEvent(inEvent);
    }
    public final void send_message(Message inMessage)
    {
        super.sendMessage(inMessage);
    }
    public final OrderID send_order(Order inOrder)
    {
        return super.sendOrder(inOrder);
    }
    public final Order create_order(Date inTransactTime,
                                    MSymbol inSymbol,
                                    BigDecimal inQuantity,
                                    BigDecimal inPrice,
                                    Side inSide,
                                    TimeInForce inTimeInForce,
                                    OrdType inOrderType,
                                    String inAccount)
    {
        return super.createOrder(inTransactTime,
                                 inSymbol,
                                 inQuantity,
                                 inPrice,
                                 inSide,
                                 inTimeInForce,
                                 inOrderType,
                                 inAccount);
    }
    @SuppressWarnings("unchecked")
    public final IMarketDataFeedToken subscribe_to_market_data(MSymbol inSecurity)
    {
        return super.subscribeToMarketData(inSecurity);
    }
    @SuppressWarnings("unchecked")
    public final void cancel_subscription(IMarketDataFeedToken inToken)
    {
        super.cancelSubscription(inToken);
    }
    public final void subscribe_to_news(MSymbol inSecurity)
    {
        super.subscribeToNews(inSecurity);
    }
    public final void suggest_trade(TradeSuggestion inSuggestion)
    {
        super.suggestTrade(inSuggestion);
    }
    protected void on_ask(AskEvent inAsk)
    {
    }
    protected void on_bid(BidEvent inBid)
    {
    }
    protected void on_callback(Object inData)
    {
    }
    protected void on_cancel(CancelEvent inCancel)
    {
    }
    protected void on_execution_report(ExecutionReport inExecutionReport)
    {
    }
    protected void on_news(MSymbol inSecurity,
                           String inText)
    {
    }
    protected void on_trade(TradeEvent inTrade)
    {
    }
    protected void on_other(Object inEvent)
    {
    }
    public void on_start()
    {
    }
    public void on_stop()
    {
    }
    public final void set_common_property(String inKey,
                                          String inValue)
    {
        setProperty(inKey,
                          inValue);
    }
    public final String get_common_property(String inKey)
    {
        return getProperty(inKey);
    }
}
