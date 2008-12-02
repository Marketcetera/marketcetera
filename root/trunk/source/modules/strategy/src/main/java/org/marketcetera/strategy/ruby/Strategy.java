package org.marketcetera.strategy.ruby;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.marketcetera.client.dest.DestinationStatus;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.notifications.Notification;
import org.marketcetera.core.notifications.NotificationManager;
import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.EventBase;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.strategy.AbstractRunningStrategy;
import org.marketcetera.strategy.RunningStrategy;
import org.marketcetera.trade.DestinationID;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.OrderCancelReject;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.OrderSingle;

import quickfix.Message;

/* $License$ */

/**
 * {@link RunningStrategy} implementation for Ruby strategies to extend.
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
     * @see org.marketcetera.strategy.IStrategy#onAsk(org.marketcetera.event.AskEvent)
     */
    @Override
    public final void onAsk(AskEvent inAsk)
    {
        on_ask(inAsk);
    }
    /*
     * (non-Javadoc)
     * 
     * @see org.marketcetera.strategy.IStrategy#onBid(org.marketcetera.event.BidEvent)
     */
    @Override
    public final void onBid(BidEvent inBid)
    {
        on_bid(inBid);
    }
    /*
     * (non-Javadoc)
     * 
     * @see org.marketcetera.strategy.IStrategy#onCallback()
     */
    @Override
    public final void onCallback(Object inData)
    {
        on_callback(inData);
    }
    /*
     * (non-Javadoc)
     * 
     * @see org.marketcetera.strategy.IStrategy#onExecutionReport(org.marketcetera.event.ExecutionReport)
     */
    @Override
    public final void onExecutionReport(ExecutionReport inExecutionReport)
    {
        on_execution_report(inExecutionReport);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.RunningStrategy#onCancel(org.marketcetera.trade.OrderCancelReject)
     */
    @Override
    public final void onCancel(OrderCancelReject inCancel)
    {
        on_cancel(inCancel);
    }
    /*
     * (non-Javadoc)
     * 
     * @see org.marketcetera.strategy.IStrategy#onTrade(org.marketcetera.event.TradeEvent)
     */
    @Override
    public final void onTrade(TradeEvent inTrade)
    {
        on_trade(inTrade);
    }
    /*
     * (non-Javadoc)
     * 
     * @see org.marketcetera.strategy.AbstractStrategy#onOther(java.lang.Object)
     */
    @Override
    public final void onOther(Object inEvent)
    {
        on_other(inEvent);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.RunningStrategy#onStart()
     */
    @Override
    public final void onStart()
    {
        on_start();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.RunningStrategy#onStop()
     */
    @Override
    public final void onStop()
    {
        on_stop();
    }
    // callbacks that Ruby scripts may override
    /**
     * Invoked when the <code>Strategy</code> receives an {@link AskEvent}.
     * 
     * @param inAsk an <code>AskEvent</code> value
     */
    protected void on_ask(AskEvent inAsk)
    {
    }
    /**
     * Invoked when the <code>Strategy</code> receives a {@link BidEvent}.
     * 
     * @param inBid a <code>BidEvent</code> value
     */
    protected void on_bid(BidEvent inBid)
    {
    }
    /**
     * Invoked when the <code>Strategy</code> receives an {@link ExecutionReport}.
     * 
     * @param inExecutionReport an <code>ExecutionReport</code> value
     */
    protected void on_execution_report(ExecutionReport inExecutionReport)
    {
    }
    /**
     * Invoked when the <code>Strategy</code> receives an {@link OrderCancelReject}.
     * 
     * @param inCancel an <code>OrderCancelReject</code> value
     */
    protected void on_cancel(OrderCancelReject inCancel)
    {
    }
    /**
     * Invoked when the <code>Strategy</code> receives a {@link TradeEvent}.
     * 
     * @param inTrade a <code>TradeEvent</code> value
     */
    protected void on_trade(TradeEvent inTrade)
    {
    }
    /**
     * Invoked when the <code>Strategy</code> receives an object that does not fit any of the other categories.
     * 
     * @param inEvent an <code>Object</code> value
     */
    protected void on_other(Object inEvent)
    {
    }
    /**
     * Invoked when the <code>Strategy</code> receives a callback requested via {@link #request_callback_at(long, Object)}
     * or {@link #request_callback_after(long, Object)}.
     * 
     * @param inData an <code>Object</code> value which was passed to the request, may be null
     */
    protected void on_callback(Object inData)
    {
    }
    /**
     * Called when a strategy is started.
     */
    protected void on_start()
    {
    }
    /**
     * Called when a strategy is about to be stopped.
     */
    protected void on_stop()
    {
    }
    // services provided to Ruby scripts
    /**
     * Sets the given key to the given value in the storage area common to all running strategies.
     * 
     * @param inKey a <code>String</code> value
     * @param inValue a <code>String</code> value
     */
    public final void set_property(String inKey,
                                   String inValue)
    {
        setProperty(inKey,
                    inValue);
    }
    /**
     * Gets the parameter associated with the given name.
     * 
     * @param inName a <code>String</code> value containing the key of a parameter key/value value
     * @return a <code>String</code> value or null if no parameter is associated with the given name
     */
    public final String get_parameter(String inName)
    {
        return getParameter(inName);
    }
    /**
     * Gets the property associated with the given name.
     * 
     * @param inName a <code>String</code> value containing the key of a property key/value value
     * @return a <code>String</code> value or null if no property is associated with the given name
     */
    public final String get_property(String inName)
    {
        return getProperty(inName);
    }
    /**
     * Requests a callback after a specified delay in milliseconds.
     *
     * <p>The callback will be executed as close to the specified millisecond
     * as possible.  There is no guarantee that the timing will be exact.  If
     * more than one callback is requested by the same {@link RunningStrategy}
     * for the same millisecond, the requests will be processed serially in
     * FIFO order.  This implies that a long-running callback request may
     * delay other callbacks from the same {@link RunningStrategy} unless the
     * caller takes steps to mitigate the bottleneck.
     *
     * @param inDelay a <code>long</code> value indicating how many milliseconds
     *   to wait before executing the callback.  A value <= 0 will be interpreted
     *   as a request for an immediate callback.
     * @param inData an <code>Object</code> value to deliver along with the callback,
     *   may be null
     */
    public final void request_callback_after(long inDelay,
                                             Object inData)
    {
        requestCallbackAfter(inDelay,
                             inData);
    }
    /**
     * Requests a callback at a specific point in time.
     *
     * <p>The callback will be executed as close to the specified millisecond
     * as possible.  There is no guarantee that the timing will be exact.  If
     * more than one callback is requested by the same {@link RunningStrategy}
     * for the same millisecond, the requests will be processed serially in
     * FIFO order.  This implies that a long-running callback request may
     * delay other callbacks from the same {@link RunningStrategy} unless the
     * caller takes steps to mitigate the bottleneck.
     *
     * @param inDate a <code>Date</code> value at which to execute the callback.  A date
     *   value earlier than the present will be interpreted as a request for an
     *   immediate callback.
     * @param inData an <code>Object</code> value to deliver with the callback or null
     */
    public final void request_callback_at(Date inDate,
                                          Object inData)
    {
        requestCallbackAt(inDate,
                          inData);
    }
    /**
     * Creates a market data request.
     * 
     * <p>The <code>inSource</code> parameter must contain the identifier of a started market data provider
     * module.
     * 
     * @param inSymbols a <code>String</code> value containing a comma-separated list of symbols for which to request data
     * @param inSource a <code>String</code> value indicating what market data provider from which to request the data
     * @return a <code>long</code> value containing an identifier corresponding to this market data request or 0 if the request failed
     */
    public final long request_market_data(String inSymbols,
                                          String inSource)
    {
        return requestMarketData(inSymbols,
                                 inSource);
    }
    /**
     * Requests market data processed by the given complex event processor from the given source.
     *
     * @param inSymbols a <code>String</code> value containing a comma-separated list of symbols
     * @param inMarketDataSource a <code>String</code> value containing a string corresponding to a market data provider identifier
     * @param inStatements a <code>String[]</code> value containing the statements to pass to the
     *   complex event processor.  The meaning of the statements varies according to the actual
     *   event processor that handles them.
     * @param inCepSource a <code>String</code> value containing the name of the complex event processor
     *   to which to send the query request
     * @return a <code>long</code> value containing the handle of the request or 0 if the request failed
     */
    public final long request_processed_market_data(String inSymbols,
                                                    String inMarketDataSource,
                                                    String[] inStatements,
                                                    String inCepSource)
    {
        return requestProcessedMarketData(inSymbols,
                                          inMarketDataSource,
                                          inStatements,
                                          inCepSource);
    }
    /**
     * Cancels a given market data request.
     * 
     * <p>If the given <code>inRequestID</code> identifier does not correspond to an active market data
     * request, this method does nothing.
     *
     * @param inRequestID a <code>long</code> value identifying the market data request to cancel
     */
    public final void cancel_market_data_request(long inRequestID)
    {
        cancelMarketDataRequest(inRequestID);
    }
    /**
     * Cancels all market data requests for this {@link Strategy}.
     *
     * <p>If there are no active market data requests for this {@link Strategy}, this method does nothing.
     */
    public final void cancel_all_market_data_requests()
    {
        cancelAllMarketDataRequests();
    }
    /**
     * Creates a complex event processor request.
     * 
     * @param inStatements a <code>String[]</code> value containing an array of statements that comprises the request
     * @param inSource a <code>String</code> value indicating what market data provider from which to request the data
     * @return a <code>long</code> value containing an identifier corresponding to this market data request or 0 if the request failed
     */
    public final long request_cep_data(String[] inStatements,
                                       String inSource)
    {
        return requestCEPData(inStatements,
                              inSource);
    }
    /**
     * Cancels a given complex event processor request.
     * 
     * <p>If the given <code>inRequestID</code> identifier does not correspond to an active complex event processor data
     * request, this method does nothing.
     *
     * @param inRequestID a <code>long</code> value identifying the complex event processor data request to cancel
     */
    public final void cancel_cep_request(long inRequestID)
    {
        cancelCEPRequest(inRequestID);
    }
    /**
     * Cancels all complex event processor requests for this {@link Strategy}.
     *
     * <p>If there are no active complex event processor requests for this {@link Strategy}, this method does nothing.
     */
    public final void cancel_all_cep_requests()
    {
        cancelAllCEPRequests();
    }
    /**
     * Suggests a trade.
     *
     * @param inOrder an <code>OrderSingle</code> value containing the trade to suggest
     * @param inScore a <code>BigDecimal</code> value containing the score of this suggestion.  this value is determined by the user
     *   but is recommended to fit in the interval [0..1]
     * @param inIdentifier a <code>String</code> value containing a user-specified string to identify the suggestion
     */
    public final void suggest_trade(OrderSingle inOrder,
                                    BigDecimal inScore,
                                    String inIdentifier)
    {
        suggestTrade(inOrder,
                     inScore,
                     inIdentifier);
    }
    /**
     * Sends a <code>FIX</code> message to all destinations to which orders are sent.
     *
     * @param inMessage a <code>Message</code> value
     * @param inDestination a <code>Destination</code> value
     */
    public final void send_message(Message inMessage,
                                   DestinationID inDestination)
    {
        sendMessage(inMessage,
                    inDestination);
    }
    /**
     * Sends the given event to the CEP module indicated by the provider.
     * 
     * <p>The corresponding CEP module must already exist or the message will not be sent.
     *
     * @param inEvent an <code>EventBase</code> value containing the event to be sent
     * @param inProvider a <code>String</code> value containing the name of a CEP provider
     */
    public final void send_event_to_cep(EventBase inEvent,
                                        String inProvider)
    {
        sendEventToCEP(inEvent,
                       inProvider);
    }
    /**
     * Sends the given event to the appropriate subscribers. 
     *
     * @param inEvent an <code>EventBase</code> value
     */
    public final void send_event(EventBase inEvent)
    {
        sendEvent(inEvent);
    }
    /**
     * Sends an order to all destinations to which orders are sent.
     *
     * @param inOrder an <code>OrderSingle</code> value
     * @return an <code>OrderID</code> value representing the order sent
     */
    public final OrderID send_order(OrderSingle inOrder)
    {
        return sendOrder(inOrder);
    }
    /**
     * Submits a request to cancel the <code>OrderSingle</code> with the given <code>OrderID</code>.
     * 
     * <p>The order must have been submitted by this strategy during this session or this call will
     * have no effect.
     *
     * @param inOrderID an <code>OrderID</code> value
     * @return a <code>boolean</code> value indicating whether the cancel request was submitted or not
     */
    public final boolean cancel_order(OrderID inOrderID)
    {
        return cancelOrder(inOrderID);
    }
    /**
     * Submits a cancel-replace order for the given <code>OrderID</code> with the given <code>Order</code>. 
     *
     * <p>The order must have been submitted by this strategy during this session or this call will
     * have no effect.
     *
     * @param inOrderID an <code>OrderID</code> value containing the order to cancel
     * @param inNewOrder an <code>OrderSingle</code> value containing the order with which to replace the existing order
     * @return an <code>OrderID</code> value containing the <code>OrderID</code> of the new order
     */
    public final OrderID cancel_replace(OrderID inOrderID,
                                        OrderSingle inNewOrder)
    {
        return cancelReplace(inOrderID,
                             inNewOrder);
    }
    /**
     * Submits cancel requests for all <code>OrderSingle</code> objects created during this session.
     * 
     * <p>This method will make a best-effort attempt to cancel all orders.  If an attempt to cancel one order
     * fails, that order will be skipped and the others will still be attempted in their turn.
     * @return an <code>int</code> value containing the number of orders to cancel
     */
    public final int cancel_all_orders()
    {
        return cancelAllOrders();
    }
    /**
     * Gets the <code>ExecutionReport</code> values received during the current session that
     * match the given <code>OrderID</code>.
     * 
     * <p>Note that the <code>OrderID</code> must match an <code>OrderSingle</code> generated
     * by this strategy during the current session.  If not, an empty list will be returned.
     *
     * @param inOrderID an <code>OrderID</code> value corresponding to an <code>OrderSingle</code>
     *   generated during this session by this strategy via {@link #send_order(OrderSingle)} or {@link #cancel_replace(OrderID, OrderSingle)}.
     * @return a <code>List&lt;ExecutionReport&gt;</code> value containing the <code>ExecutionReport</code> objects as limited according to
     *   the conditions enumerated above
     */
    public final List<ExecutionReport> get_execution_reports(OrderID inOrderID)
    {
        return getExecutionReports(inOrderID);
    }
    /**
     * Creates and issues a {@link Notification} at low priority.
     *
     * @param inSubject a <code>String</code> value
     * @param inBody a <code>String</code> value
     */
    public final void notify_low(String inSubject,
                                 String inBody)
    {
        NotificationManager.getNotificationManager().publish(Notification.low(inSubject,
                                                                              inBody,
                                                                              Strategy.class));
    }
    /**
     * Creates and issues a {@link Notification} at medium priority.
     *
     * @param inSubject a <code>String</code> value
     * @param inBody a <code>String</code> value
     */
    public final void notify_medium(String inSubject,
                                    String inBody)
    {
        NotificationManager.getNotificationManager().publish(Notification.medium(inSubject,
                                                                                 inBody,
                                                                                 Strategy.class));
    }
    /**
     * Creates and issues a {@link Notification} at high priority.
     *
     * @param inSubject a <code>String</code> value
     * @param inBody a <code>String</code> value
     */
    public final void notify_high(String inSubject,
                                  String inBody)
    {
        NotificationManager.getNotificationManager().publish(Notification.high(inSubject,
                                                                               inBody,
                                                                               Strategy.class));
    }
    /**
     * Returns the list of destinations known to the system.
     *
     * <p>These values can be used to create and send orders with {@link #send_message(Message, DestinationID)}
     * or {@link #send_order(OrderSingle)}.
     *
     * @return a <code>List&lt;DestinationStatus&gt;</code> value
     */
    public final List<DestinationStatus> get_destinations()
    {
        return getDestinations();
    }
    /**
     * Gets the position in the given security at the given point in time.
     *
     * @param inDate a <code>Date</code> value
     * @param inSymbol a <code>String</code> value
     * @return a <code>BigDecimal</code> value containing the position or null if the position could not be retrieved
     */
    public final BigDecimal get_position_as_of(Date inDate,
                                               String inSymbol)
    {
        return getPositionAsOf(inDate,
                               inSymbol);
    }
}
