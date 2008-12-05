package org.marketcetera.strategy;

import static org.marketcetera.strategy.Messages.CALLBACK_ERROR;
import static org.marketcetera.strategy.Messages.CANNOT_RETRIEVE_DESTINATIONS;
import static org.marketcetera.strategy.Messages.CANNOT_RETRIEVE_POSITION;
import static org.marketcetera.strategy.Messages.CEP_REQUEST_FAILED;
import static org.marketcetera.strategy.Messages.COMBINED_DATA_REQUEST_FAILED;
import static org.marketcetera.strategy.Messages.INVALID_CANCEL;
import static org.marketcetera.strategy.Messages.INVALID_CEP_REQUEST;
import static org.marketcetera.strategy.Messages.INVALID_EVENT;
import static org.marketcetera.strategy.Messages.INVALID_EVENT_TO_CEP;
import static org.marketcetera.strategy.Messages.INVALID_MARKET_DATA_REQUEST;
import static org.marketcetera.strategy.Messages.INVALID_MESSAGE;
import static org.marketcetera.strategy.Messages.INVALID_ORDER;
import static org.marketcetera.strategy.Messages.INVALID_ORDERID;
import static org.marketcetera.strategy.Messages.INVALID_POSITION_REQUEST;
import static org.marketcetera.strategy.Messages.INVALID_REPLACEMENT_ORDER;
import static org.marketcetera.strategy.Messages.INVALID_TRADE_SUGGESTION;
import static org.marketcetera.strategy.Messages.NO_PARAMETERS;
import static org.marketcetera.strategy.Messages.NULL_PROPERTY_KEY;
import static org.marketcetera.strategy.Messages.ORDER_CANCEL_FAILED;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.marketcetera.client.dest.DestinationStatus;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.MSymbol;
import org.marketcetera.event.EventBase;
import org.marketcetera.marketdata.DataRequest;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.trade.DestinationID;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.OrderCancel;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.OrderReplace;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.trade.OrderSingleSuggestion;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import quickfix.Message;

/* $License$ */

/**
 * Base class for running strategies.
 * 
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public abstract class AbstractRunningStrategy
        implements RunningStrategy
{
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public final String toString()
    {
        if(strategy == null) {
            return super.toString();
        }
        return strategy.toString();
    }
    /**
     * Gets the shared properties store.
     * 
     * <p>All running strategies have access to this properties store.  Changes
     * made to the object returned from this method will effect the original
     * object.
     *
     * @return a <code>Properties</code> value
     */
    static final Properties getProperties()
    {
        return properties;
    }
    /**
     * Sets the strategy object associated with this {@link RunningStrategy}.
     *
     * @param inStrategy a <code>Strategy</code> value
     */
    final void setStrategy(Strategy inStrategy)
    {
        strategy = inStrategy;
    }
    /**
     * Indicates to the <code>AbstractRunningStrategy</code> that it should stop running now.
     */
    final void stop()
    {
        callbackService.shutdownNow();
    }
    /**
     * Provides a non-overridable route for {@link ExecutionReport} data to
     * allow this object to process the data before handing it to the strategy.
     * 
     * @param inExecutionReport an <code>ExecutionReport</code> value
     */
    final void onExecutionReportRedirected(ExecutionReport inExecutionReport)
    {
        // record the execution report
        submittedOrderManager.add(inExecutionReport);
        // now notify the strategy
        onExecutionReport(inExecutionReport);
    }
    /**
     * Returns the list of orders created during this session in the order they
     * were submitted.
     * 
     * @return a <code>List&lt;OrderSingle&gt;</code> value
     */
    final List<OrderSingle> getSubmittedOrders()
    {
        return submittedOrderManager.getOrders();
    }
    /**
     * Sets the given key to the given value.
     * 
     * <p>All running strategies have access to this properties store.
     * 
     * @param inKey a <code>String</code> value
     * @param inValue a <code>String</code> value
     */
    protected static void setProperty(String inKey,
                                      String inValue)
    {
        if(inKey == null) {
            NULL_PROPERTY_KEY.warn(AbstractRunningStrategy.class);
            return;
        }
        if(inValue == null) {
            properties.remove(inKey);
            return;
        }
        properties.setProperty(inKey,
                               inValue);
    }
    /**
     * Gets the value associated with the given key.
     *
     * <p>All running strategies have access to this properties store.
     * 
     * @param inKey a <code>String</code> value
     */
    protected static String getProperty(String inKey)
    {
        if(inKey == null) {
            NULL_PROPERTY_KEY.warn(AbstractRunningStrategy.class);
            return null;
        }
        return properties.getProperty(inKey);
    }
    /**
     * Gets the parameter associated with the given name.
     *
     * @param inName a <code>String</code> value containing the key of a parameter key/value value
     * @return a <code>String</code> value or null if no parameter is associated with the given name
     */
    protected final String getParameter(String inName)
    {
        Properties parameters = strategy.getParameters();
        if(parameters == null) {
            NO_PARAMETERS.info(AbstractRunningStrategy.class,
                               strategy);
            return null;
        }
        return parameters.getProperty(inName);
    }
    /**
     * Requests market data from the given source.
     *
     * @param inSymbols a <code>String</code> value containing a comma-separated list of symbols
     * @param inSource a <code>String</code> value containing a string corresponding to a market data provider identifier
     * @return an <code>int</code> value containing the handle of the request or 0 if the request failed
     */
    protected final int requestMarketData(String inSymbols,
                                          String inSource)
    {
        if(inSymbols != null &&
           !inSymbols.isEmpty()) {
            try {
                MarketDataRequest request = constructMarketDataRequest(inSymbols);
                SLF4JLoggerProxy.debug(Strategy.STRATEGY_MESSAGES,
                                       "{} requesting market data {} from {}", //$NON-NLS-1$
                                       strategy,
                                       request,
                                       inSource);
                return strategy.getOutboundServicesProvider().requestMarketData(request,
                                                                                inSource);
            } catch (Exception e) {
                INVALID_MARKET_DATA_REQUEST.warn(Strategy.STRATEGY_MESSAGES,
                                                 e,
                                                 strategy,
                                                 inSymbols,
                                                 inSource);
                return 0;
            }
        }
        INVALID_MARKET_DATA_REQUEST.warn(Strategy.STRATEGY_MESSAGES,
                                         strategy,
                                         inSymbols,
                                         inSource);
        return 0;
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
     * @return an <code>int</code> value containing the handle of the request or 0 if the request failed
     */
    protected final int requestProcessedMarketData(String inSymbols,
                                                   String inMarketDataSource,
                                                   String[] inStatements,
                                                   String inCepSource)
    {
        if(inSymbols == null ||
           inSymbols.isEmpty() ||
           inMarketDataSource == null ||
           inMarketDataSource.isEmpty()) {
            INVALID_MARKET_DATA_REQUEST.warn(Strategy.STRATEGY_MESSAGES,
                                             strategy,
                                             inSymbols,
                                             inMarketDataSource);
            return 0;
        }
        if(inStatements == null ||
           inStatements.length == 0 ||
           inCepSource == null ||
           inCepSource.isEmpty()) {
            INVALID_CEP_REQUEST.warn(Strategy.STRATEGY_MESSAGES,
                                     strategy,
                                     Arrays.toString(inStatements),
                                     inCepSource);
            return 0;
        }
        MarketDataRequest marketDataRequest = null;
        String namespace = strategy.getDefaultNamespace();
        try {
            // construct market data request
            marketDataRequest = constructMarketDataRequest(inSymbols);
            // retrieve CEP default namespace
            SLF4JLoggerProxy.debug(Strategy.STRATEGY_MESSAGES,
                                   "{} requesting market data {} from {} with cep query {} from {}:{}", //$NON-NLS-1$
                                   strategy,
                                   marketDataRequest.toString(),
                                   inMarketDataSource,
                                   Arrays.toString(inStatements),
                                   inCepSource,
                                   namespace);
            return strategy.getOutboundServicesProvider().requestProcessedMarketData(marketDataRequest,
                                                                                   inMarketDataSource,
                                                                                   inStatements,
                                                                                   inCepSource,
                                                                                   namespace);
        } catch (Exception e) {
            COMBINED_DATA_REQUEST_FAILED.warn(Strategy.STRATEGY_MESSAGES,
                                              e,
                                              marketDataRequest,
                                              inMarketDataSource,
                                              Arrays.toString(inStatements),
                                              inCepSource,
                                              namespace);
            return 0;
        }
   }
    /**
     * Cancels the given market data request.
     *
     * @param inRequestID an <code>int</code> value containing the identifier of the data request to cancel
     */
    protected final void cancelMarketDataRequest(int inRequestID)
    {
        SLF4JLoggerProxy.debug(Strategy.STRATEGY_MESSAGES,
                               "{} cancelling market data request {}", //$NON-NLS-1$
                               strategy,
                               inRequestID);
        strategy.getOutboundServicesProvider().cancelMarketDataRequest(inRequestID);
    }
    /**
     * Cancels all market data requests from this strategy.
     */
    protected final void cancelAllMarketDataRequests()
    {
        SLF4JLoggerProxy.debug(Strategy.STRATEGY_MESSAGES,
                               "{} cancelling all market data requests", //$NON-NLS-1$
                               strategy);
        strategy.getOutboundServicesProvider().cancelAllMarketDataRequests();
    }
    /**
     * Creates a complex event processor query.
     *
     * @param inStatements a <code>String[]</code> value containing the statements to pass to the
     *   complex event processor.  The meaning of the statements varies according to the actual
     *   event processor that handles them.
     * @param inSource a <code>String</code> value containing the name of the complex event processor
     *   to which to send the query request
     * @return an <code>int</code> value containing the identifier of this request or 0 if the request
     *   failed
     */
    protected final int requestCEPData(String[] inStatements,
                                       String inSource)
    {
        if(inStatements == null ||
           inStatements.length == 0 ||
           inSource == null ||
           inSource.isEmpty()) {
                 INVALID_CEP_REQUEST.warn(Strategy.STRATEGY_MESSAGES,
                                          strategy,
                                          Arrays.toString(inStatements),
                                          inSource);
                 return 0;
             }
        try {
            SLF4JLoggerProxy.debug(Strategy.STRATEGY_MESSAGES,
                                   "{} requesting CEP data {} from {} ({})", //$NON-NLS-1$
                                   strategy,
                                   Arrays.toString(inStatements),
                                   inSource,
                                   strategy.getDefaultNamespace());
            return strategy.getOutboundServicesProvider().requestCEPData(inStatements,
                                                                         inSource,
                                                                         strategy.getDefaultNamespace());
        } catch (Exception e) {
            CEP_REQUEST_FAILED.warn(Strategy.STRATEGY_MESSAGES,
                                     e,
                                     strategy);
            return 0;
        }
    }
    /**
     * Cancels the given complex event processor data request.
     *
     * @param inRequestID an <code>int</code> value containing the identifier of the data request to cancel
     */
    protected final void cancelCEPRequest(int inRequestID)
    {
        SLF4JLoggerProxy.debug(Strategy.STRATEGY_MESSAGES,
                               "{} canceling CEP request {}", //$NON-NLS-1$
                               strategy,
                               inRequestID);
        strategy.getOutboundServicesProvider().cancelCEPRequest(inRequestID);
    }
    /**
     * Cancels all complex event processor data requests from this strategy.
     */
    protected final void cancelAllCEPRequests()
    {
        SLF4JLoggerProxy.debug(Strategy.STRATEGY_MESSAGES,
                               "{} cancelling all cep requests", //$NON-NLS-1$
                               strategy);
        strategy.getOutboundServicesProvider().cancelAllCEPRequests();
    }
    /**
     * Gets the <code>ExecutionReport</code> values generated during the current
     * session that match the given <code>OrderID</code>.
     * 
     * <p> Note that the <code>OrderID</code> must match an <code>OrderSingle</code>
     * generated by this strategy during the current session. If not, an empty
     * list will be returned.
     * 
     * @param inOrderID an <code>OrderID</code> value corresponding to an
     *            <code>OrderSingle</code> generated during this session by this
     *            strategy via {@link #sendOrder(OrderSingle)} or
     *            {@link #cancelReplace(OrderID, OrderSingle)}.
     * @return an <code>ExecutionReport[]</code> value containing the
     *         <code>ExecutionReport</code> objects as limited according to the
     *         conditions enumerated above
     */
    protected final ExecutionReport[] getExecutionReports(OrderID inOrderID)
    {
        if (inOrderID == null) {
            return new ExecutionReport[0];
        }
        List<ExecutionReport> reports = submittedOrderManager.getExecutionReports(inOrderID); 
        return reports.toArray(new ExecutionReport[reports.size()]);
    }
    /**
     * Suggests a trade.
     *
     * @param inOrder an <code>OrderSingle</code> value containing the trade to suggest
     * @param inScore a <code>BigDecimal</code> value containing the score of this suggestion.  this value is determined by the user
     *   but is recommended to fit in the interval [0..1]
     * @param inIdentifier a <code>String</code> value containing a user-specified string to identify the suggestion
     */
    protected final void suggestTrade(OrderSingle inOrder,
                                      BigDecimal inScore,
                                      String inIdentifier)
    {
        if(inOrder == null ||
           inScore == null ||
           inIdentifier == null ||
           inIdentifier.isEmpty()) {
            INVALID_TRADE_SUGGESTION.warn(Strategy.STRATEGY_MESSAGES,
                                          strategy);
            return;
        }
        assert(strategy != null);
        OrderSingleSuggestion suggestion = Factory.getInstance().createOrderSingleSuggestion();
        suggestion.setOrder(inOrder);
        suggestion.setScore(inScore);
        suggestion.setIdentifier(inIdentifier);
        SLF4JLoggerProxy.debug(Strategy.STRATEGY_MESSAGES,
                               "{} suggesting trade {}", //$NON-NLS-1$
                               strategy,
                               suggestion);
        strategy.getOutboundServicesProvider().sendSuggestion(suggestion);
    }
    /**
     * Sends an order to order subscribers.
     * 
     * @param inOrder an <code>OrderSingle</code> value
     * @return an <code>OrderID</code> value representing the submitted order
     */
    protected final OrderID sendOrder(OrderSingle inOrder)
    {
        if(inOrder == null ||
           inOrder.getOrderID() == null) {
            INVALID_ORDER.warn(Strategy.STRATEGY_MESSAGES,
                               strategy);
            return null;
        }
        SLF4JLoggerProxy.debug(Strategy.STRATEGY_MESSAGES,
                               "{} sending order {}({})", //$NON-NLS-1$
                               strategy,
                               inOrder,
                               inOrder.getOrderID());
        submittedOrderManager.add(inOrder);
        strategy.getOutboundServicesProvider().sendOrder(inOrder);
        return inOrder.getOrderID();
    }
    /**
     * Submits a request to cancel the <code>OrderSingle</code> with the given
     * <code>OrderID</code>.
     * 
     * <p> The order must have been submitted by this strategy during this session
     * or this call will have no effect.
     * 
     * @param inOrderID an <code>OrderID</code> value
     * @return a <code>boolean</code> value indicating whether the cancel was
     *         submitted or not
     */
    protected final boolean cancelOrder(OrderID inOrderID)
    {
        if(inOrderID == null) {
            INVALID_CANCEL.warn(Strategy.STRATEGY_MESSAGES,
                                strategy);
            return false;
        }
        Entry order = submittedOrderManager.remove(inOrderID);
        if(order == null) {
            INVALID_ORDERID.warn(Strategy.STRATEGY_MESSAGES,
                                 strategy,
                                 inOrderID);
            return false;
        }
        OrderCancel cancelRequest;
        // first, try to find an ExecutionReport for this order
        List<ExecutionReport> executionReports = order.executionReports;
        SLF4JLoggerProxy.debug(Strategy.STRATEGY_MESSAGES,
                               "{} found {} execution report(s) for {}", //$NON-NLS-1$
                               strategy,
                               executionReports.size(),
                               inOrderID);
        if(!executionReports.isEmpty()) {
            // use the most recent execution report to seed the cancel request
            cancelRequest = Factory.getInstance().createOrderCancel(executionReports.get(executionReports.size() - 1));
        } else {
            // use an empty execution report
            cancelRequest = Factory.getInstance().createOrderCancel(null);
            cancelRequest.setOriginalOrderID(inOrderID);
            cancelRequest.setDestinationID(order.underlyingOrder.getDestinationID());
            cancelRequest.setQuantity(order.underlyingOrder.getQuantity());
            cancelRequest.setSymbol(order.underlyingOrder.getSymbol());
            cancelRequest.setSide(order.underlyingOrder.getSide());
        }
        SLF4JLoggerProxy.debug(Strategy.STRATEGY_MESSAGES,
                               "{} submitting cancel request {}", //$NON-NLS-1$
                               strategy,
                               cancelRequest);
        strategy.getOutboundServicesProvider().cancelOrder(cancelRequest);
        return true;
    }
    /**
     * Submits cancel requests for all <code>OrderSingle</code> objects created
     * during this session.
     * 
     * <p> This method will make a best-effort attempt to cancel all orders. If an
     * attempt to cancel one order fails, that order will be skipped and the
     * others will still be attempted in their turn.
     * 
     * @return an <code>int</code> value containing the number of orders to cancel
     */
    protected final int cancelAllOrders()
    {
        SLF4JLoggerProxy.debug(Strategy.STRATEGY_MESSAGES,
                               "{} submitting request to cancel all orders", //$NON-NLS-1$
                               strategy);
        // gets a copy of the submitted orders list - iterate over the copy in
        // order to prevent concurrent update problems
        int count = 0;
        for(OrderSingle order : getSubmittedOrders()) {
            try {
                if(cancelOrder(order.getOrderID())) {
                    count += 1;
                }
            } catch (Exception e) {
                ORDER_CANCEL_FAILED.warn(Strategy.STRATEGY_MESSAGES,
                                         e,
                                         strategy,
                                         order.getOrderID());
            }
        }
        SLF4JLoggerProxy.debug(Strategy.STRATEGY_MESSAGES,
                               "{} submitted request to cancel {} order(s)", //$NON-NLS-1$
                               strategy,
                               count);
        return count;
    }
    /**
     * Submits a cancel-replace order for the given <code>OrderID</code> with
     * the given <code>Order</code>.
     * 
     * <p> The order must have been submitted by this strategy during this session
     * or this call will have no effect.
     * 
     * @param inOrderID an <code>OrderID</code> value containing the order to cancel
     * @param inNewOrder an <code>OrderSingle</code> value containing the order with which to replace the existing order
     * @return an <code>OrderID</code> value containing the <code>OrderID</code> of the new order
     */
    protected final OrderID cancelReplace(OrderID inOrderID,
                                          OrderSingle inNewOrder)
    {
        if(inOrderID == null ||
           inNewOrder == null ||
           inNewOrder.getOrderID() == null) {
            INVALID_REPLACEMENT_ORDER.warn(Strategy.STRATEGY_MESSAGES,
                                           strategy);
            return null;
        }
        Entry order = submittedOrderManager.remove(inOrderID);
        if(order == null) {
            INVALID_ORDERID.warn(Strategy.STRATEGY_MESSAGES,
                                 strategy,
                                 inOrderID);
            return null;
        }
        assert(inNewOrder.getOrderID() != null);
        // first, try to find an ExecutionReport for this order
        List<ExecutionReport> executionReports = order.executionReports;
        SLF4JLoggerProxy.debug(Strategy.STRATEGY_MESSAGES,
                               "{} found {} execution report(s) for {}", //$NON-NLS-1$
                               strategy,
                               executionReports.size(),
                               inOrderID);
        OrderReplace replaceOrder;
        if(!executionReports.isEmpty()) {
            replaceOrder = Factory.getInstance().createOrderReplace(executionReports.get(executionReports.size() - 1));

        } else {
            replaceOrder = Factory.getInstance().createOrderReplace(null);
            replaceOrder.setOriginalOrderID(inOrderID);
            replaceOrder.setDestinationID(order.underlyingOrder.getDestinationID());
            replaceOrder.setSymbol(order.underlyingOrder.getSymbol());
            replaceOrder.setSide(order.underlyingOrder.getSide());
            replaceOrder.setOrderType(order.underlyingOrder.getOrderType());
        }
        replaceOrder.setQuantity(inNewOrder.getQuantity());
        replaceOrder.setPrice(inNewOrder.getPrice());
        replaceOrder.setTimeInForce(inNewOrder.getTimeInForce());
        SLF4JLoggerProxy.debug(Strategy.STRATEGY_MESSAGES,
                               "{} submitting cancel replace request {}", //$NON-NLS-1$
                               strategy,
                               replaceOrder);
        submittedOrderManager.add(inNewOrder);
        strategy.getOutboundServicesProvider().cancelReplace(replaceOrder);
        return inNewOrder.getOrderID();
    }
    /**
     * Sends a FIX message.
     *
     * @param inMessage a <code>Message</code> value
     * @param inDestination a <code>DestinationID</code> value
     */
    protected final void sendMessage(Message inMessage,
                                     DestinationID inDestination)
    {
        if(inMessage == null ||
           inDestination == null) {
            INVALID_MESSAGE.warn(Strategy.STRATEGY_MESSAGES,
                                 strategy);
            return;
        }
        SLF4JLoggerProxy.debug(Strategy.STRATEGY_MESSAGES,
                               "{} sending FIX message {} to {}", //$NON-NLS-1$
                               strategy,
                               inMessage,
                               inDestination);
        strategy.getOutboundServicesProvider().sendMessage(inMessage,
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
    protected final void sendEventToCEP(EventBase inEvent,
                                        String inProvider)
    {
        if(inEvent == null ||
           inProvider == null ||
           inProvider.isEmpty()) {
            INVALID_EVENT_TO_CEP.warn(Strategy.STRATEGY_MESSAGES,
                                      strategy,
                                      inEvent,
                                      inProvider);
            return;
        }
        String namespace = strategy.getDefaultNamespace();
        SLF4JLoggerProxy.debug(Strategy.STRATEGY_MESSAGES,
                               "{} sending {} to CEP {}:{}", //$NON-NLS-1$
                               strategy,
                               inEvent,
                               inProvider,
                               namespace);
        strategy.getOutboundServicesProvider().sendEvent(inEvent,
                                                         inProvider,
                                                         namespace);
    }
    /**
     * Sends the given event to the appropriate subscribers. 
     *
     * @param inEvent an <code>EventBase</code> value
     */
    protected final void sendEvent(EventBase inEvent)
    {
       if(inEvent == null) {
           INVALID_EVENT.warn(Strategy.STRATEGY_MESSAGES,
                              strategy);
           return;
       }
       strategy.getOutboundServicesProvider().sendEvent(inEvent,
                                                        null,
                                                        null);
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
    protected final void requestCallbackAfter(long inDelay,
                                              Object inData)
    {
        callbackService.schedule(new Callback(this,
                                              inData),
                                 inDelay,
                                 TimeUnit.MILLISECONDS);
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
    protected final void requestCallbackAt(Date inDate,
                                           Object inData)
    {
        requestCallbackAfter(inDate.getTime() - System.currentTimeMillis(),
                             inData);
    }
    /**
     * Returns the list of destinations known to the system.
     *
     * <p>These values can be used to create and send orders with {@link #sendMessage(Message, DestinationID)}
     * or {@link #sendOrder(OrderSingle)}.
     *
     * @return a <code>DestinationStatus[]</code> value
     */
    protected final DestinationStatus[] getDestinations()
    {
        try {
            List<DestinationStatus> destinations = strategy.getInboundServicesProvider().getDestinations();
            SLF4JLoggerProxy.debug(Strategy.STRATEGY_MESSAGES,
                                   "{} received the following destinations: {}", //$NON-NLS-1$
                                   strategy,
                                   destinations == null ? "null" : Arrays.toString(destinations.toArray())); //$NON-NLS-1$
            return destinations.toArray(new DestinationStatus[destinations.size()]);
        } catch (Exception e) {
            CANNOT_RETRIEVE_DESTINATIONS.warn(Strategy.STRATEGY_MESSAGES,
                                              e,
                                              strategy);
            return new DestinationStatus[0];
        }
    }
    /**
     * Gets the position in the given security at the given point in time.
     *
     * @param inDate a <code>Date</code> value
     * @param inSymbol a <code>String</code> value
     * @return a <code>BigDecimal</code> value containing the position or null if the position could not be retrieved
     */
    protected final BigDecimal getPositionAsOf(Date inDate,
                                               String inSymbol)
    {
        if(inDate == null ||
           inSymbol == null ||
           inSymbol.isEmpty()) {
            INVALID_POSITION_REQUEST.warn(Strategy.STRATEGY_MESSAGES,
                                          strategy,
                                          inDate,
                                          inSymbol);
            return null;
        }
        try {
            BigDecimal result = strategy.getInboundServicesProvider().getPositionAsOf(inDate,
                                                                                      new MSymbol(inSymbol)); 
            SLF4JLoggerProxy.debug(Strategy.STRATEGY_MESSAGES,
                                   "{} found position {} as of {} for {}", //$NON-NLS-1$
                                   strategy,
                                   result,
                                   inDate,
                                   inSymbol);
            return result;
        } catch (Exception e) {
            CANNOT_RETRIEVE_POSITION.warn(Strategy.STRATEGY_MESSAGES,
                                          e,
                                          strategy,
                                          inSymbol,
                                          inDate);
            return null;
        }
    }
    /**
     * Constructs a market data request from the given string of symbols.
     *
     * @param inSymbols a <code>String</code> value containing the list of symbols for which to request market data
     * @return a <code>MarketDataRequest</code> value
     * @throws Exception if an error occurs
     */
    private MarketDataRequest constructMarketDataRequest(String inSymbols)
        throws Exception
    {
        return (MarketDataRequest)DataRequest.newRequestFromString(String.format("type=marketdata:symbols=%s", //$NON-NLS-1$
                                                                                 inSymbols));
    }
    /**
     * common properties store shared among all strategies
     */
    private static final Properties properties = new Properties();
    /**
     * scheduler for request callbacks
     */
    private final ScheduledExecutorService callbackService = Executors.newSingleThreadScheduledExecutor();
    /**
     * tracks submitted orders and execution reports for this strategy during
     * this session
     */
    private final SubmittedOrderManager submittedOrderManager = new SubmittedOrderManager();
    /**
     * static strategy object of which this object is a running representation
     */
    private Strategy strategy;
    /**
     * Executes a callback to a specific {@link RunningStrategy}.
     * 
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    @ClassVersion("$Id$")
    private static final class Callback
            implements Runnable
    {
        /**
         * the strategy which to call
         */
        private final RunningStrategy strategy;
        /**
         * the data payload to deliver, may be null
         */
        private final Object data;
        /**
         * Create a new Callback instance.
         *
         * @param inStrategy a <code>RunningStrategy</code> instance
         * @param inData an <code>Object</code> value to deliver to the {@link RunningStrategy}
         *   or null
         */
        private Callback(RunningStrategy inStrategy,
                         Object inData)
        {
            strategy = inStrategy;
            data = inData;
        }
        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run()
        {
            SLF4JLoggerProxy.debug(Strategy.STRATEGY_MESSAGES,
                                   "Executing callback for {} at {}", //$NON-NLS-1$
                                   strategy,
                                   new Date());
            try {
                strategy.onCallback(data);
            } catch (Exception e) {
                CALLBACK_ERROR.warn(Strategy.STRATEGY_MESSAGES,
                                    strategy);
            }
        }
    }
    /**
     * Tracks orders submitted and execution reports received during this
     * strategy session.
     * 
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    @ClassVersion("$Id$")
    private static class SubmittedOrderManager
    {
        /**
         * orders submitted by this strategy in this session in the order they
         * were submitted, oldest to newest
         */
        private final Map<OrderID, Entry> submittedOrders = new LinkedHashMap<OrderID, Entry>();
        /**
         * Gets the orders submitted during this strategy session in the order
         * they were submitted.
         * 
         * @return a <code>List&lt;OrderSingle&gt;</code> value
         */
        private List<OrderSingle> getOrders()
        {
            synchronized(submittedOrders) {
                List<OrderSingle> orders = new ArrayList<OrderSingle>();
                for(Entry entry : submittedOrders.values()) {
                    orders.add(entry.underlyingOrder);
                }
                return orders;
            }
        }
        /**
         * Removes an order from the order tracker.
         * 
         * @param inOrderID an <code>OrderID</code> value
         * @return an <code>Entry</code> value or null if the given <code>OrderID</code> does not correspond to a submitted order
         */
        private Entry remove(OrderID inOrderID)
        {
            synchronized(submittedOrders) {
                Entry entry = submittedOrders.remove(inOrderID);
                if(entry == null) {
                    return null;
                }
                return entry;
            }
        }
        /**
         * Adds an <code>ExecutionReport</code> to the order tracker.
         * 
         * @param inExecutionReport an <code>ExecutionReport</code> value
         */
        private void add(ExecutionReport inExecutionReport)
        {
            assert(inExecutionReport.getOrderID() != null);
            synchronized(submittedOrders) {
                Entry entry = submittedOrders.get(inExecutionReport.getOrderID());
                if(entry != null) {
                    entry.executionReports.add(inExecutionReport);
                } else {
                    SLF4JLoggerProxy.debug(AbstractRunningStrategy.class,
                                           "No matching order id {}: ignoring {}", //$NON-NLS-1$
                                           inExecutionReport.getOrderID(),
                                           inExecutionReport);
                }
            }
        }
        /**
         * Adds an <code>OrderSingle</code> to the order tracker.
         * 
         * @param inOrder an <code>OrderSingle</code> value
         */
        private void add(OrderSingle inOrder)
        {
            assert(inOrder.getOrderID() != null);
            synchronized(submittedOrders) {
                submittedOrders.put(inOrder.getOrderID(),
                                    new Entry(inOrder));
            }
        }
        /**
         * Gets the <code>ExecutionReport</code> objects, if any, received
         * during this strategy session for the given <code>OrderID</code>.
         * 
         * @param inOrderID an <code>OrderID</code> value
         * @return a <code>List&lt;ExecutionReport&gt;</code> value
         */
        private List<ExecutionReport> getExecutionReports(OrderID inOrderID)
        {
            assert(inOrderID != null);
            synchronized(submittedOrders) {
                Entry entry = submittedOrders.get(inOrderID);
                if(entry == null) {
                    return new ArrayList<ExecutionReport>();
                }
                return new ArrayList<ExecutionReport>(entry.executionReports);
            }
        }
    }
    /**
     * Tracks a specific <code>OrderSingle</code> value and its
     * <code>ExecutionReport</code> objects as they are received.
     * 
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    @ClassVersion("$Id$")
    private static class Entry
    {
        /**
         * the underlying order
         */
        private final OrderSingle underlyingOrder;
        /**
         * the execution reports in the order they were received
         */
        private final List<ExecutionReport> executionReports = new ArrayList<ExecutionReport>();
        /**
         * Create a new Entry instance.
         * 
         * @param inOrder an <code>OrderSingle</code> value
         */
        private Entry(OrderSingle inOrder)
        {
            assert(inOrder.getOrderID() != null);
            underlyingOrder = inOrder;
        }
    }
}
