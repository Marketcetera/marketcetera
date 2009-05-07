package org.marketcetera.strategy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.marketcetera.client.OrderValidationException;
import org.marketcetera.client.Validations;
import org.marketcetera.client.brokers.BrokerStatus;
import org.marketcetera.core.notifications.Notification;
import org.marketcetera.event.EventBase;
import org.marketcetera.event.LogEvent;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.MSymbol;
import org.marketcetera.trade.OrderCancel;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.OrderReplace;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.trade.OrderSingleSuggestion;
import org.marketcetera.trade.Originator;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

import quickfix.Message;

/* $License$ */

/**
 * Base class for running strategies.
 * 
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public abstract class AbstractRunningStrategy
        implements RunningStrategy, Messages
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
        // no new callbacks will be allowed
        callbackService.shutdown();
        // terminate existing callbacks, best effort
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
            NO_PARAMETERS.warn(AbstractRunningStrategy.class,
                               strategy);
            return null;
        }
        return parameters.getProperty(inName);
    }
    /**
     * Requests market data.
     *
     * @param inRequest a <code>MarketDataRequest</code> value containing the request to execute
     * @return an <code>int</code> value containing the handle of the request or 0 if the request failed
     */
    protected final int requestMarketData(MarketDataRequest inRequest)
    {
        if(!canReceiveData()) {
            StrategyModule.log(LogEvent.warn(CANNOT_REQUEST_DATA,
                                             String.valueOf(strategy),
                                             strategy.getStatus()),
                               strategy);
            return 0;
        }
        if(inRequest == null) {
            StrategyModule.log(LogEvent.warn(INVALID_MARKET_DATA_REQUEST,
                                             String.valueOf(strategy),
                                             inRequest),
                               strategy);
            return 0;
        }
        try {
            StrategyModule.log(LogEvent.debug(SUBMITTING_MARKET_DATA_REQUEST,
                                              String.valueOf(strategy),
                                              String.valueOf(inRequest)),
                               strategy);
            return strategy.getOutboundServicesProvider().requestMarketData(inRequest);
        } catch (Exception e) {
            StrategyModule.log(LogEvent.warn(INVALID_MARKET_DATA_REQUEST,
                                             e,
                                             String.valueOf(strategy),
                                             inRequest),
                               strategy);
            return 0;
        }
    }
    /**
     * Requests market data.
     *
     * @param inRequest a <code>String</code> value containing the representation of a {@link MarketDataRequest} to execute
     * @return an <code>int</code> value containing the handle of the request or 0 if the request failed
     */
    protected final int requestMarketData(String inRequest)
    {
        try {
            MarketDataRequest request = MarketDataRequest.newRequestFromString(inRequest);
            StrategyModule.log(LogEvent.debug(SUBMITTING_MARKET_DATA_REQUEST,
                                              String.valueOf(strategy),
                                              request),
                               strategy);
            return strategy.getOutboundServicesProvider().requestMarketData(request);
        } catch (Exception e) {
            StrategyModule.log(LogEvent.warn(INVALID_MARKET_DATA_REQUEST,
                                             e,
                                             String.valueOf(strategy),
                                             inRequest),
                               strategy);
            return 0;
        }
    }
    /**
     * Requests market data processed by the given complex event processor.
     *
     * @param inRequest a <code>MarketDataRequest</code> value containing the request to execute
     * @param inStatements a <code>String[]</code> value containing the statements to pass to the
     *   complex event processor.  The meaning of the statements varies according to the actual
     *   event processor that handles them.
     * @param inCepSource a <code>String</code> value containing the name of the complex event processor
     *   to which to send the query request
     * @return an <code>int</code> value containing the handle of the request or 0 if the request failed
     */
    protected final int requestProcessedMarketData(MarketDataRequest inRequest,
                                                   String[] inStatements,
                                                   String inCepSource)
    {
        if(!canReceiveData()) {
            StrategyModule.log(LogEvent.warn(CANNOT_REQUEST_DATA,
                                             String.valueOf(strategy),
                                             strategy.getStatus()),
                               strategy);
            return 0;
        }
        if(inRequest == null) {
            StrategyModule.log(LogEvent.warn(INVALID_MARKET_DATA_REQUEST,
                                             String.valueOf(strategy),
                                             inRequest),
                               strategy);
            return 0;
        }
        if(inStatements == null ||
           inStatements.length == 0 ||
           inCepSource == null ||
           inCepSource.isEmpty()) {
            StrategyModule.log(LogEvent.warn(INVALID_CEP_REQUEST,
                                             String.valueOf(strategy),
                                             Arrays.toString(inStatements),
                                             inCepSource,
                                             strategy.getDefaultNamespace()),
                               strategy);
            return 0;
        }
        String namespace = strategy.getDefaultNamespace();
        try {
            // retrieve CEP default namespace
            StrategyModule.log(LogEvent.debug(SUBMITTING_PROCESSED_MARKET_DATA_REQUEST,
                                              String.valueOf(strategy),
                                              String.valueOf(inRequest),
                                              Arrays.toString(inStatements),
                                              inCepSource,
                                              namespace),
                               strategy);
            return strategy.getOutboundServicesProvider().requestProcessedMarketData(inRequest,
                                                                                     inStatements,
                                                                                     inCepSource,
                                                                                     namespace);
        } catch (Exception e) {
            StrategyModule.log(LogEvent.warn(COMBINED_DATA_REQUEST_FAILED,
                                             e,
                                             String.valueOf(inRequest),
                                             Arrays.toString(inStatements),
                                             inCepSource,
                                             namespace),
                               strategy);
            return 0;
        }
    }
    /**
     * Requests market data processed by the given complex event processor from the given source.
     *
     * @param inRequest a <code>String</code> value containing the representation of a {@link MarketDataRequest} to execute
     * @param inStatements a <code>String[]</code> value containing the statements to pass to the
     *   complex event processor.  The meaning of the statements varies according to the actual
     *   event processor that handles them.
     * @param inCepSource a <code>String</code> value containing the name of the complex event processor
     *   to which to send the query request
     * @return an <code>int</code> value containing the handle of the request or 0 if the request failed
     */
    protected final int requestProcessedMarketData(String inRequest,
                                                   String[] inStatements,
                                                   String inCepSource)
    {
        if(!canReceiveData()) {
            StrategyModule.log(LogEvent.warn(CANNOT_REQUEST_DATA,
                                             String.valueOf(strategy),
                                             strategy.getStatus()),
                               strategy);
            return 0;
        }
        if(inRequest == null) {
            StrategyModule.log(LogEvent.warn(INVALID_MARKET_DATA_REQUEST,
                                             String.valueOf(strategy),
                                             inRequest),
                               strategy);
            return 0;
        }
        if(inStatements == null ||
           inStatements.length == 0 ||
           inCepSource == null ||
           inCepSource.isEmpty()) {
            StrategyModule.log(LogEvent.warn(INVALID_CEP_REQUEST,
                                             String.valueOf(strategy),
                                             Arrays.toString(inStatements),
                                             inCepSource,
                                             strategy.getDefaultNamespace()),
                               strategy);
            return 0;
        }
        String namespace = strategy.getDefaultNamespace();
        try {
            // retrieve CEP default namespace
            StrategyModule.log(LogEvent.debug(SUBMITTING_PROCESSED_MARKET_DATA_REQUEST,
                                              String.valueOf(strategy),
                                              String.valueOf(inRequest),
                                              Arrays.toString(inStatements),
                                              inCepSource,
                                              namespace),
                               strategy);
            return strategy.getOutboundServicesProvider().requestProcessedMarketData(MarketDataRequest.newRequestFromString(inRequest),
                                                                                     inStatements,
                                                                                     inCepSource,
                                                                                     namespace);
        } catch (Exception e) {
            StrategyModule.log(LogEvent.warn(COMBINED_DATA_REQUEST_FAILED,
                                             e,
                                             String.valueOf(inRequest),
                                             Arrays.toString(inStatements),
                                             inCepSource,
                                             namespace),
                               strategy);
            return 0;
        }
    }
    /**
     * Cancels the given data request.
     *
     * @param inRequestID an <code>int</code> value containing the identifier of the data request to cancel
     */
    protected final void cancelDataRequest(int inRequestID)
    {
        StrategyModule.log(LogEvent.debug(CANCELING_DATA_REQUEST,
                                          String.valueOf(strategy),
                                          String.valueOf(inRequestID)),                           
                           strategy);                           
        strategy.getOutboundServicesProvider().cancelDataRequest(inRequestID);
    }
    /**
     * Cancels all data requests from this strategy.
     */
    protected final void cancelAllDataRequests()
    {
        StrategyModule.log(LogEvent.debug(CANCELING_ALL_DATA_REQUESTS,
                                          String.valueOf(strategy)),                           
                           strategy);                           
        strategy.getOutboundServicesProvider().cancelAllDataRequests();
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
        if(!canReceiveData()) {
            StrategyModule.log(LogEvent.warn(CANNOT_REQUEST_DATA,
                                             String.valueOf(strategy),
                                             strategy.getStatus()),
                               strategy);
            return 0;
        }
        if(inStatements == null ||
           inStatements.length == 0 ||
           inSource == null ||
           inSource.isEmpty()) {
            StrategyModule.log(LogEvent.warn(INVALID_CEP_REQUEST,
                                             String.valueOf(strategy),
                                             Arrays.toString(inStatements),
                                             inSource,
                                             strategy.getDefaultNamespace()),
                               strategy);
                 return 0;
             }
        try {
            StrategyModule.log(LogEvent.debug(SUBMITTING_CEP_REQUEST,
                                              String.valueOf(strategy),
                                              Arrays.toString(inStatements),
                                              inSource,
                                              strategy.getDefaultNamespace()),
                               strategy);
            return strategy.getOutboundServicesProvider().requestCEPData(inStatements,
                                                                         inSource,
                                                                         strategy.getDefaultNamespace());
        } catch (Exception e) {
            StrategyModule.log(LogEvent.warn(CEP_REQUEST_FAILED,
                                             e,
                                             String.valueOf(strategy)),
                               strategy);
            return 0;
        }
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
        if(!canSendData()) {
            StrategyModule.log(LogEvent.warn(CANNOT_SEND_DATA,
                                             String.valueOf(strategy),
                                             strategy.getStatus()),
                               strategy);
            return;
        }
        if(inOrder == null ||
           inScore == null ||
           inIdentifier == null ||
           inIdentifier.isEmpty()) {
            StrategyModule.log(LogEvent.warn(INVALID_TRADE_SUGGESTION,
                                             String.valueOf(strategy)),
                               strategy);
            return;
        }
        assert(strategy != null);
        OrderSingleSuggestion suggestion = Factory.getInstance().createOrderSingleSuggestion();
        suggestion.setOrder(inOrder);
        suggestion.setScore(inScore);
        suggestion.setIdentifier(inIdentifier);
        StrategyModule.log(LogEvent.debug(SUBMITTING_TRADE_SUGGESTION,
                                          String.valueOf(strategy),
                                          suggestion),
                           strategy);
        strategy.getOutboundServicesProvider().sendSuggestion(suggestion);
    }
    /**
     * Sends an order to order subscribers.
     * 
     * @param inOrder an <code>OrderSingle</code> value
     * @return an <code>OrderID</code> value representing the submitted order or null if the order could not be sent
     */
    protected final OrderID sendOrder(OrderSingle inOrder)
    {
        if(!canSendData()) {
            StrategyModule.log(LogEvent.warn(CANNOT_SEND_DATA,
                                             String.valueOf(strategy),
                                             strategy.getStatus()),
                               strategy);
            return null;
        }
        if(inOrder == null ||
           inOrder.getOrderID() == null) {
            StrategyModule.log(LogEvent.warn(INVALID_ORDER,
                                             String.valueOf(strategy)),
                               strategy);
            return null;
        }
        try {
            Validations.validate(inOrder);
        } catch (OrderValidationException e) {
            StrategyModule.log(LogEvent.warn(ORDER_VALIDATION_FAILED,
                                             e,
                                             String.valueOf(strategy)),
                               strategy);
            return null;
        }
        StrategyModule.log(LogEvent.debug(SUBMITTING_ORDER,
                                          String.valueOf(strategy),
                                          inOrder,
                                          inOrder.getOrderID()),
                           strategy);
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
        if(!canSendData()) {
            StrategyModule.log(LogEvent.warn(CANNOT_SEND_DATA,
                                             String.valueOf(strategy),
                                             strategy.getStatus()),
                               strategy);
            return false;
        }
        if(inOrderID == null) {
            StrategyModule.log(LogEvent.warn(INVALID_CANCEL,
                                             String.valueOf(strategy)),
                               strategy);
            return false;
        }
        Entry order = submittedOrderManager.remove(inOrderID);
        if(order == null) {
            StrategyModule.log(LogEvent.warn(INVALID_ORDERID,
                                             String.valueOf(strategy),
                                             String.valueOf(inOrderID)),
                               strategy);
            return false;
        }
        OrderCancel cancelRequest;
        ExecutionReport executionReportToUse = selectExecutionReportForCancel(order);
        if(executionReportToUse == null) {
            // use an empty execution report
            cancelRequest = Factory.getInstance().createOrderCancel(null);
            cancelRequest.setOriginalOrderID(inOrderID);
            cancelRequest.setBrokerID(order.underlyingOrder.getBrokerID());
            cancelRequest.setQuantity(order.underlyingOrder.getQuantity());
            cancelRequest.setSymbol(order.underlyingOrder.getSymbol());
            cancelRequest.setSide(order.underlyingOrder.getSide());
        } else {
            // use the most recent execution report to seed the cancel request
            cancelRequest = Factory.getInstance().createOrderCancel(executionReportToUse);
        }
        StrategyModule.log(LogEvent.debug(SUBMITTING_CANCEL_ORDER_REQUEST,
                                          String.valueOf(strategy),
                                          String.valueOf(cancelRequest)),                           
                           strategy);                           
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
     * @return an <code>int</code> value containing the number of orders for which cancels were submitted
     */
    protected final int cancelAllOrders()
    {
        if(!canSendData()) {
            StrategyModule.log(LogEvent.warn(CANNOT_SEND_DATA,
                                             String.valueOf(strategy),
                                             strategy.getStatus()),
                               strategy);
            return 0;
        }
        StrategyModule.log(LogEvent.debug(SUBMITTING_CANCEL_ALL_ORDERS_REQUEST,
                                          String.valueOf(strategy)),
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
                StrategyModule.log(LogEvent.warn(ORDER_CANCEL_FAILED,
                                                 e,
                                                 String.valueOf(strategy),
                                                 order.getOrderID()),
                                   strategy);
            }
        }
        StrategyModule.log(LogEvent.debug(CANCEL_REQUEST_SUBMITTED,
                                          String.valueOf(strategy),
                                          count),
                           strategy);
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
     * @return an <code>OrderID</code> value containing the <code>OrderID</code> of the new order or null if the old order could not be canceled and the new one could not be sent
     */
    protected final OrderID cancelReplace(OrderID inOrderID,
                                          OrderSingle inNewOrder)
    {
        if(!canSendData()) {
            StrategyModule.log(LogEvent.warn(CANNOT_SEND_DATA,
                                             String.valueOf(strategy),
                                             strategy.getStatus()),
                               strategy);
            return null;
        }
        if(inOrderID == null ||
           inNewOrder == null ||
           inNewOrder.getOrderID() == null) {
            StrategyModule.log(LogEvent.warn(INVALID_REPLACEMENT_ORDER,
                                             String.valueOf(strategy)),
                               strategy);
            return null;
        }
        Entry order = submittedOrderManager.remove(inOrderID);
        if(order == null) {
            StrategyModule.log(LogEvent.warn(INVALID_ORDERID,
                                             String.valueOf(strategy),
                                             String.valueOf(inOrderID)),
                               strategy);
            return null;
        }
        assert(inNewOrder.getOrderID() != null);
        // first, try to find an ExecutionReport for this order
        ExecutionReport executionReport = selectExecutionReportForCancel(order);
        OrderReplace replaceOrder;
        if(executionReport == null) {
            replaceOrder = Factory.getInstance().createOrderReplace(null);
            replaceOrder.setOriginalOrderID(inOrderID);
            replaceOrder.setBrokerID(order.underlyingOrder.getBrokerID());
            replaceOrder.setSymbol(order.underlyingOrder.getSymbol());
            replaceOrder.setSide(order.underlyingOrder.getSide());
            replaceOrder.setOrderType(order.underlyingOrder.getOrderType());
        } else {
            replaceOrder = Factory.getInstance().createOrderReplace(executionReport);
        }
        replaceOrder.setQuantity(inNewOrder.getQuantity());
        replaceOrder.setPrice(inNewOrder.getPrice());
        replaceOrder.setTimeInForce(inNewOrder.getTimeInForce());
        StrategyModule.log(LogEvent.debug(SUBMITTING_CANCEL_REPLACE_REQUEST,
                                          String.valueOf(strategy),
                                          String.valueOf(replaceOrder)),
                           strategy);
        submittedOrderManager.add(inNewOrder);
        strategy.getOutboundServicesProvider().cancelReplace(replaceOrder);
        return inNewOrder.getOrderID();
    }
    /**
     * Sends a FIX message.
     *
     * @param inMessage a <code>Message</code> value
     * @param inBroker a <code>BrokerID</code> value
     */
    protected final void sendMessage(Message inMessage,
                                     BrokerID inBroker)
    {
        if(!canSendData()) {
            StrategyModule.log(LogEvent.warn(CANNOT_SEND_DATA,
                                             String.valueOf(strategy),
                                             strategy.getStatus()),
                               strategy);
            return;
        }
        if(inMessage == null ||
           inBroker == null) {
            StrategyModule.log(LogEvent.warn(INVALID_MESSAGE,
                                             String.valueOf(strategy)),
                               strategy);
            return;
        }
        StrategyModule.log(LogEvent.debug(SUBMITTING_FIX_MESSAGE,
                                          String.valueOf(strategy),
                                          inMessage,
                                          inBroker),
                           strategy);
        strategy.getOutboundServicesProvider().sendMessage(inMessage,
                                                           inBroker);
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
        if(!canSendData()) {
            StrategyModule.log(LogEvent.warn(CANNOT_SEND_DATA,
                                             String.valueOf(strategy),
                                             strategy.getStatus()),
                               strategy);
            return;
        }
        if(inEvent == null ||
           inProvider == null ||
           inProvider.isEmpty()) {
            StrategyModule.log(LogEvent.warn(INVALID_EVENT_TO_CEP,
                                             String.valueOf(strategy),
                                             inEvent,
                                             inProvider),
                               strategy);
            return;
        }
        String namespace = strategy.getDefaultNamespace();
        StrategyModule.log(LogEvent.debug(SUBMITTING_EVENT_TO_CEP,
                                          String.valueOf(strategy),
                                          inEvent,
                                          inProvider,
                                          namespace),
                           strategy);
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
        if(!canSendData()) {
            StrategyModule.log(LogEvent.warn(CANNOT_SEND_DATA,
                                             String.valueOf(strategy),
                                             strategy.getStatus()),
                               strategy);
            return;
        }
       if(inEvent == null) {
           StrategyModule.log(LogEvent.warn(INVALID_EVENT,
                                            String.valueOf(strategy)),
                              strategy);
           return;
       }
       strategy.getOutboundServicesProvider().sendEvent(inEvent,
                                                        null,
                                                        null);
    }
    /**
     * Sends the given notification to the appropriate subscribers.
     *
     * @param inNotification a <code>Notification</code> value
     */
    protected final void sendNotification(Notification inNotification)
    {
        if(inNotification == null) {
            StrategyModule.log(LogEvent.warn(INVALID_NOTIFICATION,
                                             String.valueOf(strategy)),
                               strategy);
            return;
        }
        strategy.getOutboundServicesProvider().sendNotification(inNotification);
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
                                              strategy,
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
     * Returns the list of brokers known to the system.
     *
     * <p>These values can be used to create and send orders with {@link #sendMessage(Message, BrokerID)}
     * or {@link #sendOrder(OrderSingle)}.
     *
     * @return a <code>BrokerStatus[]</code> value
     */
    protected final BrokerStatus[] getBrokers()
    {
        try {
            if(!canReceiveData()) {
                StrategyModule.log(LogEvent.warn(CANNOT_REQUEST_DATA,
                                                 String.valueOf(strategy),
                                                 strategy.getStatus()),
                                   strategy);
                return new BrokerStatus[0];
            }
            List<BrokerStatus> brokers = strategy.getInboundServicesProvider().getBrokers();
            StrategyModule.log(LogEvent.debug(RECEIVED_BROKERS,
                                              String.valueOf(strategy),
                                              String.valueOf(brokers)),
                               strategy);
            return brokers.toArray(new BrokerStatus[brokers.size()]);
        } catch (Exception e) {
            StrategyModule.log(LogEvent.warn(CANNOT_RETRIEVE_BROKERS,
                                             e,
                                             String.valueOf(strategy)),
                               strategy);
            return new BrokerStatus[0];
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
        if(!canReceiveData()) {
            StrategyModule.log(LogEvent.warn(CANNOT_REQUEST_DATA,
                                             String.valueOf(strategy),
                                             strategy.getStatus()),
                               strategy);
            return null;
        }
        if(inDate == null ||
           inSymbol == null ||
           inSymbol.isEmpty()) {
            StrategyModule.log(LogEvent.warn(INVALID_POSITION_REQUEST,
                                             String.valueOf(strategy),
                                             inDate,
                                             inSymbol),
                               strategy);
            return null;
        }
        try {
            BigDecimal result = strategy.getInboundServicesProvider().getPositionAsOf(inDate,
                                                                                      new MSymbol(inSymbol)); 
            StrategyModule.log(LogEvent.debug(RECEIVED_POSITION,
                                              String.valueOf(strategy),
                                              result,
                                              inDate,
                                              inSymbol),
                               strategy);
            return result;
        } catch (Exception e) {
            StrategyModule.log(LogEvent.warn(CANNOT_RETRIEVE_POSITION,
                                             e,
                                             String.valueOf(strategy),
                                             inSymbol,
                                             inDate),
                               strategy);
            return null;
        }
    }
    /**
     * Emits the given debug message to the strategy log output.
     *
     * @param inMessage a <code>String</code> value
     */
    protected void debug(String inMessage)
    {
        if(inMessage == null) {
            StrategyModule.log(LogEvent.warn(INVALID_LOG,
                                             String.valueOf(strategy)),
                               strategy);
            return;
        }
        strategy.getOutboundServicesProvider().log(LogEvent.debug(MESSAGE_1P,
                                                                  inMessage));
    }
    /**
     * Emits the given info message to the strategy log output.
     *
     * @param inMessage a <code>String</code> value
     */
    protected void info(String inMessage)
    {
        if(inMessage == null) {
            StrategyModule.log(LogEvent.warn(INVALID_LOG,
                                             String.valueOf(strategy)),
                               strategy);
            return;
        }
        strategy.getOutboundServicesProvider().log(LogEvent.info(MESSAGE_1P,
                                                                 inMessage));
    }
    /**
     * Emits the given warn message to the strategy log output.
     *
     * @param inMessage a <code>String</code> value
     */
    protected void warn(String inMessage)
    {
        if(inMessage == null) {
            StrategyModule.log(LogEvent.warn(INVALID_LOG,
                                             String.valueOf(strategy)),
                               strategy);
            return;
        }
        strategy.getOutboundServicesProvider().log(LogEvent.warn(MESSAGE_1P,
                                                                 inMessage));
    }
    /**
     * Emits the given error message to the strategy log output.
     *
     * @param inMessage a <code>String</code> value
     */
    protected void error(String inMessage)
    {
        if(inMessage == null) {
            StrategyModule.log(LogEvent.warn(INVALID_LOG,
                                             String.valueOf(strategy)),
                               strategy);
            return;
        }
        strategy.getOutboundServicesProvider().log(LogEvent.error(MESSAGE_1P,
                                                                  inMessage));
    }
    /**
     * Searches for an appropriate <code>ExecutionReport</code> suitable for
     * constructing a cancel order.
     *
     * @param inEntry an <code>Entry</code> value representing the order to cancel
     * @return an <code>ExecutionReport</code> value or null if no appropriate <code>ExecutionReport</code>
     *   value exists
     */
    private ExecutionReport selectExecutionReportForCancel(Entry inEntry)
    {
        // first, try to find an ExecutionReport for this order
        List<ExecutionReport> executionReports = inEntry.executionReports;
        StrategyModule.log(LogEvent.debug(EXECUTION_REPORTS_FOUND,
                                          String.valueOf(strategy),
                                          executionReports.size(),
                                          String.valueOf(inEntry)),
                           strategy);
        // get list iterator set to last element of the list
        ListIterator<ExecutionReport> iterator = executionReports.listIterator(executionReports.size());
        // traverse backwards until a usable execution report is found
        while(iterator.hasPrevious()) {
            ExecutionReport report = iterator.previous();
            if(Originator.Server.equals(report.getOriginator())) {
                StrategyModule.log(LogEvent.debug(USING_EXECUTION_REPORT,
                                                  String.valueOf(strategy),
                                                  report),
                                   strategy);
                return report;
            }
        }
        StrategyModule.log(LogEvent.debug(NO_EXECUTION_REPORT,
                                          String.valueOf(strategy)),
                           strategy);
        return null;
    }
    /**
     * Indicates if outgoing data can be sent.
     *
     * @return a <code>boolean</code> value
     */
    private boolean canSendData()
    {
        return strategy.getStatus().canSendData();
    }
    /**
     * Indicates if incoming data can be received.
     *
     * @return a <code>boolean</code> value
     */
    private boolean canReceiveData()
    {
        return strategy.getStatus().canReceiveData();
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
     * @since 1.0.0
     */
    @ClassVersion("$Id$")
    private static final class Callback
            implements Runnable
    {
        /**
         * the base strategy
         */
        private final Strategy strategy;
        /**
         * the strategy which to call
         */
        private final RunningStrategy runningStrategy;
        /**
         * the data payload to deliver, may be null
         */
        private final Object data;
        /**
         * Create a new Callback instance.
         *
         * @param inRunningStrategy a <code>RunningStrategy</code> instance
         * @param inStrategy a <code>Strategy</code> value containing the base strategy
         * @param inData an <code>Object</code> value to deliver to the {@link RunningStrategy}
         *   or null
         */
        private Callback(RunningStrategy inRunningStrategy,
                         Strategy inStrategy,
                         Object inData)
        {
            runningStrategy = inRunningStrategy;
            strategy = inStrategy;
            data = inData;
        }
        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run()
        {
            StrategyModule.log(LogEvent.debug(EXECUTING_CALLBACK,
                                              String.valueOf(runningStrategy),
                                              new Date()),
                               strategy);
            try {
                runningStrategy.onCallback(data);
            } catch (Exception e) {
                if(strategy.getExecutor() != null) {
                    StrategyModule.log(LogEvent.warn(CALLBACK_ERROR,
                                                     String.valueOf(strategy),
                                                     strategy.getExecutor().interpretRuntimeException(e)),
                                       strategy);
                } else {
                    StrategyModule.log(LogEvent.warn(CALLBACK_ERROR,
                                                     e,
                                                     String.valueOf(strategy),
                                                     e.getLocalizedMessage()),
                                       strategy);
                }
            }
        }
    }
    /**
     * Tracks orders submitted and execution reports received during this
     * strategy session.
     * 
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 1.0.0
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
     * @since 1.0.0
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
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            return String.format("Order %s with execution reports: %s", //$NON-NLS-1$
                                 underlyingOrder,
                                 executionReports);
        }
    }
}
