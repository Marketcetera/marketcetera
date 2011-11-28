package org.marketcetera.strategy;

import static org.marketcetera.strategy.Messages.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.marketcetera.client.ClientInitException;
import org.marketcetera.client.OrderValidationException;
import org.marketcetera.client.Validations;
import org.marketcetera.client.brokers.BrokerStatus;
import org.marketcetera.client.utils.LiveOrderHistoryManager;
import org.marketcetera.core.notifications.Notification;
import org.marketcetera.core.position.PositionKey;
import org.marketcetera.event.Event;
import org.marketcetera.event.impl.LogEventBuilder;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.MarketDataRequestBuilder;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.DataFlowSupport;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.trade.*;
import org.marketcetera.util.collections.UnmodifiableDeque;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.misc.NamedThreadFactory;

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
     * Called when the <code>AbstractRunningStrategy</code> starts.
     * @throws ClientInitException if an error occurs during start 
     */
    final void start()
            throws ClientInitException
    {
        orderHistoryManager = new LiveOrderHistoryManager(getReportHistoryOriginDate());
        orderHistoryManager.start();
        openOrders = orderHistoryManager.getOpenOrders();
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
        orderHistoryManager.add(inExecutionReport);
        // now notify the strategy
        onExecutionReport(inExecutionReport);
    }
    /**
     * Provides a non-overridable route for {@link OrderCancelReject} data to
     * allow this object to process the data before handing it to the strategy.
     * 
     * @param inCancelReject an <code>OrderCanceleject</code> value
     */
    final void onCancelRejectRedirected(OrderCancelReject inCancelReject)
    {
        // record the order cancel reject
        orderHistoryManager.add(inCancelReject);
        // now notify the strategy
        onCancelReject(inCancelReject);
    }
    /**
     * Returns the list of open orders created during this session in the order they
     * were submitted.
     * 
     * <p>Returns all orders regardless of their state.
     * 
     * @return a <code>Set&lt;OrderSingle&gt;</code> value
     */
    final Set<OrderSingle> getSubmittedOrders()
    {
        return Collections.unmodifiableSet(submittedOrders);
    }
    /**
     * Returns the list of open order IDs created during this session in the order they
     * were submitted.
     * 
     * <p>Returns all order IDs regardless of their state.
     * 
     * @return a <code>Set&lt;OrderID&gt;</code> value
     */
    protected final Set<OrderID> getSubmittedOrderIDs()
    {
        return Collections.unmodifiableSet(submittedOrderIDs);
    }
    /**
     * Returns the list of <code>OrderID</code> values for open orders created in this
     * session in the order they were submitted.
     *
     * <p>Returns IDs of open orders only.  Orders that were canceled, replaced, filled, or
     * otherwise are no longer open will not be returned.  For orders submitted
     * via {@link AbstractRunningStrategy#cancelReplace(OrderID, OrderSingle, boolean)},
     * the ID of the {@link OrderReplace} value sent to the broker is returned, not the 
     * {@link OrderSingle} value used to create the <code>OrderReplace</code>.
     *
     * @return a <code>Set&lt;OrderID&gt;</code> value
     */
    protected final Set<OrderID> getOpenOrderIDs()
    {
        return openOrders.keySet();
    }
    /**
     * Gets the collection of open orders represented by the most recent <code>ExecutionReport</code>.
     *
     * @return a <code>Collection&lt;ExecutionReport&gt;</code> value
     */
    protected final Collection<ExecutionReport> getOpenOrders()
    {
        return openOrders.values();
    }
    /**
     * Gets the <code>OrderStatus</code> for the given <code>OrderID</code>.
     * 
     * <p>The given <code>OrderID</code> may be any part of the order chain. For example, if an order is replaced,
     * either the original <code>OrderID</code> or the current <code>OrderID</code> will return the same value,
     * although only the current <code>OrderID</code> is open.
     *
     * @param inOrderID an <code>OrderID</code> value or <code>null</code> if the given order cannot be found
     * @return an <code>OrderStatus</code> value
     */
    protected final OrderStatus getOrderStatus(OrderID inOrderID)
    {
        ReportBase latestReport = orderHistoryManager.getLatestReportFor(inOrderID);
        if(latestReport == null) {
            return null;
        }
        return latestReport.getOrderStatus();
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
     * Gets the report history origin date to use for the order history.
     * 
     * <p>Strategies may override this method to return a date. For performance
     * reasons, it is best to use the most recent date possible. The default is
     * to return the beginning of time.
     *
     * @return a <code>Date</code> value
     */
    protected Date getReportHistoryOriginDate()
    {
        return new Date(0);
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
            StrategyModule.log(LogEventBuilder.warn().withMessage(CANNOT_REQUEST_DATA,
                                                                  String.valueOf(strategy),
                                                                  strategy.getStatus()).create(),
                               strategy);
            return 0;
        }
        if(inRequest == null) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(INVALID_MARKET_DATA_REQUEST,
                                                                  String.valueOf(strategy),
                                                                  inRequest).create(),
                               strategy);
            return 0;
        }
        try {
            StrategyModule.log(LogEventBuilder.debug().withMessage(SUBMITTING_MARKET_DATA_REQUEST,
                                                                   String.valueOf(strategy),
                                                                   String.valueOf(inRequest)).create(),
                               strategy);
            return strategy.getServicesProvider().requestMarketData(inRequest);
        } catch (Exception e) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(INVALID_MARKET_DATA_REQUEST,
                                                                  String.valueOf(strategy),
                                                                  inRequest)
                                                     .withException(e).create(),
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
            MarketDataRequest request = MarketDataRequestBuilder.newRequestFromString(inRequest);
            StrategyModule.log(LogEventBuilder.debug().withMessage(SUBMITTING_MARKET_DATA_REQUEST,
                                                                   String.valueOf(strategy),
                                                                   request).create(),
                               strategy);
            return strategy.getServicesProvider().requestMarketData(request);
        } catch (Exception e) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(INVALID_MARKET_DATA_REQUEST,
                                                                  String.valueOf(strategy),
                                                                  inRequest)
                                                     .withException(e).create(),
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
            StrategyModule.log(LogEventBuilder.warn().withMessage(CANNOT_REQUEST_DATA,
                                                                  String.valueOf(strategy),
                                                                  strategy.getStatus()).create(),
                               strategy);
            return 0;
        }
        if(inRequest == null) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(INVALID_MARKET_DATA_REQUEST,
                                                                  String.valueOf(strategy),
                                                                  inRequest).create(),
                               strategy);
            return 0;
        }
        if(inStatements == null ||
           inStatements.length == 0 ||
           inCepSource == null ||
           inCepSource.isEmpty()) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(INVALID_CEP_REQUEST,
                                                                  String.valueOf(strategy),
                                                                  Arrays.toString(inStatements),
                                                                  inCepSource,
                                                                  strategy.getDefaultNamespace()).create(),
                               strategy);
            return 0;
        }
        String namespace = strategy.getDefaultNamespace();
        try {
            // retrieve CEP default namespace
            StrategyModule.log(LogEventBuilder.debug().withMessage(SUBMITTING_PROCESSED_MARKET_DATA_REQUEST,
                                                                   String.valueOf(strategy),
                                                                   String.valueOf(inRequest),
                                                                   Arrays.toString(inStatements),
                                                                   inCepSource,
                                                                   namespace).create(),
                               strategy);
            return strategy.getServicesProvider().requestProcessedMarketData(inRequest,
                                                                             inStatements,
                                                                             inCepSource,
                                                                             namespace);
        } catch (Exception e) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(COMBINED_DATA_REQUEST_FAILED,
                                                                  String.valueOf(inRequest),
                                                                  Arrays.toString(inStatements),
                                                                  inCepSource,
                                                                  namespace)
                                                     .withException(e).create(),
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
            StrategyModule.log(LogEventBuilder.warn().withMessage(CANNOT_REQUEST_DATA,
                                                                  String.valueOf(strategy),
                                                                  strategy.getStatus()).create(),
                               strategy);
            return 0;
        }
        if(inRequest == null) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(INVALID_MARKET_DATA_REQUEST,
                                                                  String.valueOf(strategy),
                                                                  inRequest).create(),
                               strategy);
            return 0;
        }
        if(inStatements == null ||
           inStatements.length == 0 ||
           inCepSource == null ||
           inCepSource.isEmpty()) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(INVALID_CEP_REQUEST,
                                                                  String.valueOf(strategy),
                                                                  Arrays.toString(inStatements),
                                                                  inCepSource,
                                                                  strategy.getDefaultNamespace()).create(),
                               strategy);
            return 0;
        }
        String namespace = strategy.getDefaultNamespace();
        try {
            // retrieve CEP default namespace
            StrategyModule.log(LogEventBuilder.debug().withMessage(SUBMITTING_PROCESSED_MARKET_DATA_REQUEST,
                                                                   String.valueOf(strategy),
                                                                   String.valueOf(inRequest),
                                                                   Arrays.toString(inStatements),
                                                                   inCepSource,
                                                                   namespace).create(),
                               strategy);
            return strategy.getServicesProvider().requestProcessedMarketData(MarketDataRequestBuilder.newRequestFromString(inRequest),
                                                                             inStatements,
                                                                             inCepSource,
                                                                             namespace);
        } catch (Exception e) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(COMBINED_DATA_REQUEST_FAILED,
                                                                  String.valueOf(inRequest),
                                                                  Arrays.toString(inStatements),
                                                                  inCepSource,
                                                                  namespace)
                                                     .withException(e).create(),
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
        StrategyModule.log(LogEventBuilder.debug().withMessage(CANCELING_DATA_REQUEST,
                                                               String.valueOf(strategy),
                                                               String.valueOf(inRequestID)).create(),
                           strategy);                           
        strategy.getServicesProvider().cancelDataRequest(inRequestID);
    }
    /**
     * Cancels all data requests from this strategy.
     */
    protected final void cancelAllDataRequests()
    {
        StrategyModule.log(LogEventBuilder.debug().withMessage(CANCELING_ALL_DATA_REQUESTS,
                                                               String.valueOf(strategy)).create(),                           
                           strategy);                           
        strategy.getServicesProvider().cancelAllDataRequests();
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
            StrategyModule.log(LogEventBuilder.warn().withMessage(CANNOT_REQUEST_DATA,
                                                                  String.valueOf(strategy),
                                                                  strategy.getStatus()).create(),
                               strategy);
            return 0;
        }
        if(inStatements == null ||
           inStatements.length == 0 ||
           inSource == null ||
           inSource.isEmpty()) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(INVALID_CEP_REQUEST,
                                                                  String.valueOf(strategy),
                                                                  Arrays.toString(inStatements),
                                                                  inSource,
                                                                  strategy.getDefaultNamespace()).create(),
                               strategy);
                 return 0;
             }
        try {
            StrategyModule.log(LogEventBuilder.debug().withMessage(SUBMITTING_CEP_REQUEST,
                                                                   String.valueOf(strategy),
                                                                   Arrays.toString(inStatements),
                                                                   inSource,
                                                                   strategy.getDefaultNamespace()).create(),
                               strategy);
            return strategy.getServicesProvider().requestCEPData(inStatements,
                                                                 inSource,
                                                                 strategy.getDefaultNamespace());
        } catch (Exception e) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(CEP_REQUEST_FAILED,
                                                                  Arrays.toString(inStatements),
                                                                  inSource)
                                                     .withException(e).create(),
                               strategy);
            return 0;
        }
    }
    /**
     * Gets the <code>ReportBase</code> values representing the order history of the given <code>OrderID</code>.
     * 
     * <p>The <code>ReportBase</code> objects returned by this call represent the history of the order represented
     * by the given <code>OrderID</code>. if there is no order history for the given <code>OrderID</code>, this operation
     * will return an empty collection.
     * 
     * <p>The values returned by this operation are sorted from newest to oldest: the order's current status is represented
     * by the first element in the collection.
     * 
     * <p>The collection returned by this operation will be updated as the underlying report history changes. The collection itself
     * may not be modified.
     * 
     * <p>The contents of the returned collection are limited by the value returned by {@link #getReportHistoryOriginDate()}. The
     * default value is all reports. No reports with a sending time before the origin date will be returned.
     * 
     * @param inOrderID an <code>OrderID</code> value corresponding to an existing order, either open or closed
     * @return a <code>Deque&lt;ReportBase&gt;</code> value containing the <code>ReportBase</code> objects
     */
    protected final Deque<ReportBase> getExecutionReports(OrderID inOrderID)
    {
        if (inOrderID == null) {
            return EMPTY_REPORTS;
        }
        return orderHistoryManager.getReportHistoryFor(inOrderID); 
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
            StrategyModule.log(LogEventBuilder.warn().withMessage(INVALID_TRADE_SUGGESTION,
                                                                  String.valueOf(strategy)).create(),
                               strategy);
            return;
        }
        assert(strategy != null);
        OrderSingleSuggestion suggestion = Factory.getInstance().createOrderSingleSuggestion();
        suggestion.setOrder(inOrder);
        suggestion.setScore(inScore);
        suggestion.setIdentifier(inIdentifier);
        StrategyModule.log(LogEventBuilder.debug().withMessage(SUBMITTING_TRADE_SUGGESTION,
                                                               String.valueOf(strategy),
                                                               suggestion).create(),
                           strategy);
        strategy.getServicesProvider().sendSuggestion(suggestion);
    }
    /**
     * Sends an order to order subscribers.
     * 
     * <p><code>OrderSingle</code> objects passed to this method will be added to the list of submitted orders
     * but other object types will not.  In order to track, for example, <code>OrderReplace</code> and <code>OrderCancel</code>
     * objects, they must have first been created via {@link #cancelReplace(OrderID, OrderSingle, boolean)} and
     * {@link #cancelOrder(OrderID, boolean)} respectively. 
     * 
     * @param inData an <code>Object</code> value
     * @return a <code>boolean</code> value indicating whether the object was successfully transmitted or not
     */
    protected boolean send(Object inData)
    {
        if(inData == null) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(INVALID_DATA,
                                                                  String.valueOf(strategy)).create(),
                               strategy);
            return false;
        }
        if(inData instanceof OrderSingle) {
            OrderSingle order = (OrderSingle)inData;
            if(order.getOrderID() == null) {
                StrategyModule.log(LogEventBuilder.warn().withMessage(INVALID_ORDER,
                                                                      String.valueOf(strategy)).create(),
                                   strategy);
                     return false;
            }
            try {
                Validations.validate(order);
            } catch (OrderValidationException e) {
                StrategyModule.log(LogEventBuilder.warn().withMessage(ORDER_VALIDATION_FAILED,
                                                                      String.valueOf(strategy))
                                                         .withException(e).create(),
                                   strategy);
                return false;
            }
            StrategyModule.log(LogEventBuilder.debug().withMessage(SUBMITTING_ORDER,
                                                                   String.valueOf(strategy),
                                                                   order,
                                                                   order.getOrderID()).create(),
                               strategy);
            SLF4JLoggerProxy.debug(AbstractRunningStrategy.class,
                                   "{} created {}", //$NON-NLS-1$
                                   strategy,
                                   order);
            submittedOrders.add(order);
        }
        strategy.getServicesProvider().send(inData);
        return true;
    }
    /**
     * Submits a request to cancel the <code>OrderSingle</code> with the given <code>OrderID</code>.
     * 
     * <p>The order must currently be open or this operation will fail. Note that the strategy's concept of
     * open orders is based on its report history origin date as {@link #getReportHistoryOriginDate() specified}.
     * 
     * @param inOrderID an <code>OrderID</code> value containing the ID of the open order to cancel
     * @param inSendOrder a <code>boolean</code> value indicating whether the <code>OrderCancel</code> should be submitted or just returned to the caller
     *   If <code>false</code>, it is the caller's responsibility to submit the <code>OrderCancel</code> with {@link #send(Object)}.
     * @return an <code>OrderCancel</code> value containing the cancel order or <code>null</code> if the <code>OrderCancel</code> could not be constructed
     */
    protected final OrderCancel cancelOrder(OrderID inOrderID,
                                            boolean inSendOrder)
    {
        if(inOrderID == null) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(INVALID_CANCEL,
                                                                  String.valueOf(strategy)).create(),
                               strategy);
            return null;
        }
        ExecutionReport report = openOrders.get(inOrderID);
        if(report == null) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(INVALID_ORDERID,
                                                                  String.valueOf(strategy),
                                                                  String.valueOf(inOrderID)).create(),
                               strategy);
            return null;
        }
        OrderCancel cancelRequest = Factory.getInstance().createOrderCancel(report);
        // set the BrokerOrderID to null (EG-762) because the ER acks we get back from the ORS (server)
        //  do not have the BrokerOrderID set correctly.  it is OK to send no BrokerOrderID, but it is
        //  not OK to send an invalid BrokerOrderID.
        cancelRequest.setBrokerOrderID(null);
        SLF4JLoggerProxy.debug(AbstractRunningStrategy.class,
                               "{} created {}", //$NON-NLS-1$
                               strategy,
                               cancelRequest);
        if(inSendOrder) {
            StrategyModule.log(LogEventBuilder.debug().withMessage(SUBMITTING_CANCEL_ORDER_REQUEST,
                                                                   String.valueOf(strategy),
                                                                   String.valueOf(cancelRequest)).create(),                           
                               strategy);
            strategy.getServicesProvider().cancelOrder(cancelRequest);
        }
        return cancelRequest;
    }
    /**
     * Submits cancel requests for all <code>OrderSingle</code> open orders owned by the strategy's owner.
     * 
     * <p> This method will make a best-effort attempt to cancel all orders. If an
     * attempt to cancel one order fails, that order will be skipped and the
     * others will still be attempted in their turn.
     * 
     * @return an <code>int</code> value containing the number of orders for which cancels were submitted
     */
    protected final int cancelAllOrders()
    {
        StrategyModule.log(LogEventBuilder.debug().withMessage(SUBMITTING_CANCEL_ALL_ORDERS_REQUEST,
                                                               String.valueOf(strategy)).create(),
                           strategy);
        // gets a copy of the open orders list - iterate over a copy in order to prevent concurrent update problems
        Set<OrderID> openOrdersCopy = new HashSet<OrderID>(openOrders.keySet());
        SLF4JLoggerProxy.debug(AbstractRunningStrategy.class,
                               "Found {} open orders to cancel",
                               openOrdersCopy);
        int count = 0;
        for(OrderID orderId : openOrdersCopy) {
            try {
                if(cancelOrder(orderId,
                               true) != null) {
                    count += 1;
                }
            } catch (Exception e) {
                StrategyModule.log(LogEventBuilder.warn().withMessage(ORDER_CANCEL_FAILED,
                                                                      String.valueOf(strategy),
                                                                      orderId)
                                                         .withException(e).create(),
                                   strategy);
            }
        }
        StrategyModule.log(LogEventBuilder.debug().withMessage(CANCEL_REQUEST_SUBMITTED,
                                                               String.valueOf(strategy),
                                                               count).create(),
                           strategy);
        return count;
    }
    /**
     * Submits a cancel-replace order for the given <code>OrderID</code> with
     * the given <code>Order</code>.
     * 
     * <p>The order must be open or this call will have no effect.
     * 
     * <p>If <code>inSendOrder</code> is <code>false</code>, it is the caller's responsibility
     * to submit the <code>OrderReplace</code>.
     * 
     * @param inOrderID an <code>OrderID</code> value containing the order to cancel
     * @param inNewOrder an <code>OrderSingle</code> value containing the order with which to replace the existing order
     * @param inSendOrder a <code>boolean</code> value indicating whether the <code>OrderReplace</code> should be submitted or just returned to the caller.  If <code>false</code>,
     *   it is the caller's responsibility to submit the <code>OrderReplace</code> with {@link #send(Object)}.
     * @return an <code>OrderReplace</code> value containing the new order or <code>null</code> if the old order could not be canceled and the new one could not be sent
     */
    protected final OrderReplace cancelReplace(OrderID inOrderID,
                                               OrderSingle inNewOrder,
                                               boolean inSendOrder)
    {
        if(inOrderID == null ||
           inNewOrder == null ||
           inNewOrder.getOrderID() == null) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(INVALID_REPLACEMENT_ORDER,
                                                                  String.valueOf(strategy)).create(),
                               strategy);
            return null;
        }
        ExecutionReport executionReport = openOrders.get(inOrderID);
        if(executionReport == null) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(INVALID_ORDERID,
                                                                  String.valueOf(strategy),
                                                                  String.valueOf(inOrderID)).create(),
                               strategy);
            return null;
        }
        OrderReplace replaceOrder = Factory.getInstance().createOrderReplace(executionReport);
        // set the BrokerOrderID to null (EG-762) because the ER acks we get back from the ORS (server)
        //  do not have the BrokerOrderID set correctly.  it is OK to send no BrokerOrderID, but it is
        //  not OK to send an invalid BrokerOrderID.
        replaceOrder.setBrokerOrderID(null);
        replaceOrder.setQuantity(inNewOrder.getQuantity());
        // special case: when replacing a market order, it is absolutely verbotten to specify a price
        if(OrderType.Market.equals(executionReport.getOrderType())) {
            replaceOrder.setPrice(null);
        } else {
            replaceOrder.setPrice(inNewOrder.getPrice());
        }
        replaceOrder.setTimeInForce(inNewOrder.getTimeInForce());
        SLF4JLoggerProxy.debug(AbstractRunningStrategy.class,
                               "{} created {}", //$NON-NLS-1$
                               strategy,
                               replaceOrder);
        if(inSendOrder) {
            StrategyModule.log(LogEventBuilder.debug().withMessage(SUBMITTING_CANCEL_REPLACE_REQUEST,
                                                                   String.valueOf(strategy),
                                                                   String.valueOf(replaceOrder)).create(),
                               strategy);
            strategy.getServicesProvider().cancelReplace(replaceOrder);
        }
        return replaceOrder;
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
        if(inMessage == null ||
           inBroker == null) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(INVALID_MESSAGE,
                                                                  String.valueOf(strategy)).create(),
                               strategy);
            return;
        }
        StrategyModule.log(LogEventBuilder.debug().withMessage(SUBMITTING_FIX_MESSAGE,
                                                               String.valueOf(strategy),
                                                               inMessage,
                                                               inBroker).create(),
                           strategy);
        strategy.getServicesProvider().sendMessage(inMessage,
                                                   inBroker);
    }
    /**
     * Sends the given event to the CEP module indicated by the provider.
     * 
     * <p>The corresponding CEP module must already exist or the message will not be sent.
     *
     * @param inEvent an <code>Event</code> value containing the event to be sent
     * @param inProvider a <code>String</code> value containing the name of a CEP provider
     */
    protected final void sendEventToCEP(Event inEvent,
                                        String inProvider)
    {
        if(inEvent == null ||
           inProvider == null ||
           inProvider.isEmpty()) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(INVALID_EVENT_TO_CEP,
                                                                  String.valueOf(strategy),
                                                                  inEvent,
                                                                  inProvider).create(),
                               strategy);
            return;
        }
        String namespace = strategy.getDefaultNamespace();
        StrategyModule.log(LogEventBuilder.debug().withMessage(SUBMITTING_EVENT_TO_CEP,
                                                               String.valueOf(strategy),
                                                               inEvent,
                                                               inProvider,
                                                               namespace).create(),
                           strategy);
        strategy.getServicesProvider().sendEvent(inEvent,
                                                 inProvider,
                                                 namespace);
    }
    /**
     * Sends the given event to the appropriate subscribers. 
     *
     * @param inEvent an <code>Event</code> value
     */
    protected final void sendEvent(Event inEvent)
    {
       if(inEvent == null) {
           StrategyModule.log(LogEventBuilder.warn().withMessage(INVALID_EVENT,
                                                                 String.valueOf(strategy)).create(),
                              strategy);
           return;
       }
       strategy.getServicesProvider().sendEvent(inEvent,
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
            StrategyModule.log(LogEventBuilder.warn().withMessage(INVALID_NOTIFICATION,
                                                                  String.valueOf(strategy)).create(),
                               strategy);
            return;
        }
        strategy.getServicesProvider().sendNotification(inNotification);
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
     * Requests a callback periodically after a specified period in milliseconds.
     *
     * <p>The callback will be executed as close to the specified millisecond
     * as possible.  There is no guarantee that the timing will be exact.  If
     * more than one callback is requested by the same {@link RunningStrategy}
     * for the same millisecond, the requests will be processed serially in
     * FIFO order.  This implies that a long-running callback request may
     * delay other callbacks from the same {@link RunningStrategy} unless the
     * caller takes steps to mitigate the bottleneck.
     * @param inDelay a <code>long</code> value indicating how many milliseconds
     *   to wait before executing the first callback. A value <= 0 will be interpreted
     *   as a request for an immediate callback.
     * @param inPeriod a <code>long</code> value indicating how many milliseconds
     *   to wait before executing the second callback, and thereafter repeatedly
     *   The value must be > 0.
     * @param inData an <code>Object</code> value to deliver along with the callback,
     *   may be null
     */
    protected final void requestCallbackEvery(long inDelay, long inPeriod,
                                              Object inData)
    {
        callbackService.scheduleAtFixedRate(new Callback(this,
                strategy,
                inData), inDelay, inPeriod, TimeUnit.MILLISECONDS);
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
     * or {@link #send(Object)}.
     *
     * @return a <code>BrokerStatus[]</code> value
     */
    protected final BrokerStatus[] getBrokers()
    {
        try {
            if(!canReceiveData()) {
                StrategyModule.log(LogEventBuilder.warn().withMessage(CANNOT_REQUEST_DATA,
                                                                      String.valueOf(strategy),
                                                                      strategy.getStatus()).create(),
                                   strategy);
                return new BrokerStatus[0];
            }
            List<BrokerStatus> brokers = strategy.getServicesProvider().getBrokers();
            StrategyModule.log(LogEventBuilder.debug().withMessage(RECEIVED_BROKERS,
                                                                   String.valueOf(strategy),
                                                                   String.valueOf(brokers)).create(),
                               strategy);
            return brokers.toArray(new BrokerStatus[brokers.size()]);
        } catch (Exception e) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(CANNOT_RETRIEVE_BROKERS,
                                                                  String.valueOf(strategy))
                                                     .withException(e).create(),
                               strategy);
            return new BrokerStatus[0];
        }
    }
    /**
     * Gets the position in the given <code>Equity</code> at the given point in time.
     *
     * <p>Note that this method will not retrieve <code>Option</code> positions.  To retrieve
     * <code>Option</code> positions, use {@link #getOptionPositionAsOf(Date, String, String, BigDecimal, OptionType)}.
     * 
     * @param inDate a <code>Date</code> value indicating the point in time for which to search
     * @param inSymbol a <code>String</code> value containing the <code>Equity</code> symbol
     * @return a <code>BigDecimal</code> value or <code>null</code> if no position could be found 
     */
    protected final BigDecimal getPositionAsOf(Date inDate,
                                               String inSymbol)
    {
        if(!canReceiveData()) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(CANNOT_REQUEST_DATA,
                                                                  String.valueOf(strategy),
                                                                  strategy.getStatus()).create(),
                               strategy);
            return null;
        }
        if(inDate == null ||
           inSymbol == null ||
           inSymbol.isEmpty()) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(INVALID_EQUITY_POSITION_REQUEST,
                                                                  String.valueOf(strategy),
                                                                  inDate,
                                                                  inSymbol).create(),
                               strategy);
            return null;
        }
        try {
            BigDecimal result = strategy.getServicesProvider().getPositionAsOf(inDate,
                                                                               new Equity(inSymbol)); 
            StrategyModule.log(LogEventBuilder.debug().withMessage(RECEIVED_POSITION,
                                                                   String.valueOf(strategy),
                                                                   result,
                                                                   inDate,
                                                                   inSymbol).create(),
                               strategy);
            return result;
        } catch (Exception e) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(CANNOT_RETRIEVE_EQUITY_POSITION,
                                                                  String.valueOf(strategy),
                                                                  inSymbol,
                                                                  inDate)
                                                     .withException(e).create(),
                               strategy);
            return null;
        }
    }
    /**
     * Gets all open <code>Equity</code> positions at the given point in time.
     *
     * @param inDate a <code>Date</code> value indicating the point in time for which to search
     * @return a <code>Map&lt;PositionKey&lt;Equity&gt;,BigDecimal&gt;</code> value
     */
    protected final Map<PositionKey<Equity>,BigDecimal> getAllPositionsAsOf(Date inDate)
    {
        if(!canReceiveData()) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(CANNOT_REQUEST_DATA,
                                                                  String.valueOf(strategy),
                                                                  strategy.getStatus()).create(),
                               strategy);
            return null;
        }
        if(inDate == null) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(INVALID_POSITIONS_REQUEST,
                                                                  String.valueOf(strategy)).create(),
                               strategy);
            return null;
        }
        try {
            Map<PositionKey<Equity>,BigDecimal> result = strategy.getServicesProvider().getAllPositionsAsOf(inDate); 
            StrategyModule.log(LogEventBuilder.debug().withMessage(RECEIVED_POSITIONS,
                                                                   String.valueOf(strategy),
                                                                   String.valueOf(result),
                                                                   inDate).create(),
                               strategy);
            return result;
        } catch (Exception e) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(CANNOT_RETRIEVE_POSITIONS,
                                                                  String.valueOf(strategy),
                                                                  inDate)
                                                     .withException(e).create(),
                               strategy);
            return null;
        }
    }
    /**
     * Gets the position in the given <code>Future</code> at the given point in time.
     *
     * <p>Note that this method will not retrieve <code>Option</code> or <code>Equity</code> positions.  To retrieve
     * <code>Option</code> positions, use {@link #getOptionPositionAsOf(Date, String, String, BigDecimal, OptionType)}.
     *   To retrieve
     * <code>Equity</code> positions, use {@link #getPositionAsOf(Date, String)}.
     * 
     * @param inDate a <code>Date</code> value indicating the point in time for which to search
     * @param inUnderlyingSymbol a <code>String</code> value containing the underlying <code>Future</code> symbol
     * @param inExpirationMonth a <code>FutureExpirationMonth</code> value
     * @param inExpirationYear an <code>int</code> value
     * @return a <code>BigDecimal</code> value or <code>null</code> if no position could be found 
     */
    protected final BigDecimal getFuturePositionAsOf(Date inDate,
                                                     String inUnderlyingSymbol,
                                                     FutureExpirationMonth inExpirationMonth,
                                                     int inExpirationYear)
    {
        if(!canReceiveData()) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(CANNOT_REQUEST_DATA,
                                                                  String.valueOf(strategy),
                                                                  strategy.getStatus()).create(),
                               strategy);
            return null;
        }
        if(inDate == null ||
           inUnderlyingSymbol == null ||
           inUnderlyingSymbol.isEmpty()) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(INVALID_FUTURE_POSITION_REQUEST,
                                                                  String.valueOf(strategy),
                                                                  inDate,
                                                                  inUnderlyingSymbol).create(),
                               strategy);
            return null;
        }
        try {
            BigDecimal result = strategy.getServicesProvider().getFuturePositionAsOf(inDate,
                                                                                     new Future(inUnderlyingSymbol,
                                                                                                inExpirationMonth,
                                                                                                inExpirationYear)); 
            StrategyModule.log(LogEventBuilder.debug().withMessage(RECEIVED_POSITION,
                                                                   String.valueOf(strategy),
                                                                   result,
                                                                   inDate,
                                                                   inUnderlyingSymbol).create(),
                               strategy);
            return result;
        } catch (Exception e) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(CANNOT_RETRIEVE_FUTURE_POSITION,
                                                                  String.valueOf(strategy),
                                                                  inUnderlyingSymbol,
                                                                  inDate)
                                                     .withException(e).create(),
                               strategy);
            return null;
        }
    }
    /**
     * Gets all open <code>Future</code> positions at the given point in time.
     *
     * @param inDate a <code>Date</code> value indicating the point in time for which to search
     * @return a <code>Map&lt;PositionKey&lt;Equity&gt;,BigDecimal&gt;</code> value
     */
    protected final Map<PositionKey<Future>,BigDecimal> getAllFuturePositionsAsOf(Date inDate)
    {
        if(!canReceiveData()) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(CANNOT_REQUEST_DATA,
                                                                  String.valueOf(strategy),
                                                                  strategy.getStatus()).create(),
                               strategy);
            return null;
        }
        if(inDate == null) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(INVALID_POSITIONS_REQUEST,
                                                                  String.valueOf(strategy)).create(),
                               strategy);
            return null;
        }
        try {
            Map<PositionKey<Future>,BigDecimal> result = strategy.getServicesProvider().getAllFuturePositionsAsOf(inDate); 
            StrategyModule.log(LogEventBuilder.debug().withMessage(RECEIVED_POSITIONS,
                                                                   String.valueOf(strategy),
                                                                   String.valueOf(result),
                                                                   inDate).create(),
                               strategy);
            return result;
        } catch (Exception e) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(CANNOT_RETRIEVE_POSITIONS,
                                                                  String.valueOf(strategy),
                                                                  inDate)
                                                     .withException(e).create(),
                               strategy);
            return null;
        }
    }
    /**
     * Gets the position in the given <code>Option</code> at the given point in time.
     *
     * @param inDate a <code>Date</code> value indicating the point in time for which to search
     * @param inOptionRoot a <code>String</code> value
     * @param inExpiry a <code>String</code> value
     * @param inStrikePrice a <code>BigDecimal</code> value
     * @param inType an <code>OptionType</code> value
     * @return a <code>BigDecimal</code> value or <code>null</code> if no position could be found
     */
    protected final BigDecimal getOptionPositionAsOf(Date inDate,
                                                     String inOptionRoot,
                                                     String inExpiry,
                                                     BigDecimal inStrikePrice,
                                                     OptionType inType)
    {
        if(!canReceiveData()) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(CANNOT_REQUEST_DATA,
                                                                  String.valueOf(strategy),
                                                                  strategy.getStatus()).create(),
                               strategy);
            return null;
        }
        if(inDate == null ||
           inOptionRoot == null ||
           inOptionRoot.isEmpty() ||
           inExpiry == null ||
           inExpiry.isEmpty() ||
           inStrikePrice == null ||
           inType == null) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(INVALID_OPTION_POSITION_REQUEST,
                                                                  String.valueOf(strategy),
                                                                  inDate,
                                                                  inOptionRoot,
                                                                  inExpiry,
                                                                  inStrikePrice,
                                                                  inType).create(),
                               strategy);
            return null;
        }
        try {
            Option option = new Option(inOptionRoot,
                                       inExpiry,
                                       inStrikePrice,
                                       inType);
            BigDecimal result = strategy.getServicesProvider().getOptionPositionAsOf(inDate,
                                                                                     option); 
            StrategyModule.log(LogEventBuilder.debug().withMessage(RECEIVED_POSITION,
                                                                   String.valueOf(strategy),
                                                                   result,
                                                                   inDate,
                                                                   option).create(),
                               strategy);
            return result;
        } catch (Exception e) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(CANNOT_RETRIEVE_OPTION_POSITION,
                                                                  String.valueOf(strategy),
                                                                  inOptionRoot,
                                                                  inExpiry,
                                                                  inStrikePrice,
                                                                  inType,
                                                                  inDate)
                                                     .withException(e).create(),
                               strategy);
            return null;
        }
    }
    /**
     * Gets all open <code>Option</code> positions at the given point in time.
     *
     * @param inDate a <code>Date</code> value indicating the point in time for which to search
     * @return a <code>Map&lt;PositionKey&lt;Option&gt;,BigDecimal&gt;</code> value
     */
    protected final Map<PositionKey<Option>,BigDecimal> getAllOptionPositionsAsOf(Date inDate)
    {
        if(!canReceiveData()) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(CANNOT_REQUEST_DATA,
                                                                  String.valueOf(strategy),
                                                                  strategy.getStatus()).create(),
                               strategy);
            return null;
        }
        if(inDate == null) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(INVALID_POSITIONS_REQUEST,
                                                                  String.valueOf(strategy)).create(),
                               strategy);
            return null;
        }
        try {
            Map<PositionKey<Option>,BigDecimal> result = strategy.getServicesProvider().getAllOptionPositionsAsOf(inDate); 
            StrategyModule.log(LogEventBuilder.debug().withMessage(RECEIVED_POSITIONS,
                                                                   String.valueOf(strategy),
                                                                   String.valueOf(result),
                                                                   inDate).create(),
                               strategy);
            return result;
        } catch (Exception e) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(CANNOT_RETRIEVE_POSITIONS,
                                                                  String.valueOf(strategy),
                                                                  inDate)
                                                     .withException(e).create(),
                               strategy);
            return null;
        }
    }
    /**
     * Gets open positions for the options specified by the given option roots at the given point in time. 
     *
     * @param inDate a <code>Date</code> value indicating the point in time for which to search
     * @param inOptionRoots a <code>String[]</code> value containing the specific option roots for which to search
     * @return a <code>Map&lt;PositionKey&lt;Option&gt;,BigDecimal&gt;</code> value
     */
    protected final Map<PositionKey<Option>,BigDecimal> getOptionPositionsAsOf(Date inDate,
                                                                               String...inOptionRoots)
    {
        if(!canReceiveData()) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(CANNOT_REQUEST_DATA,
                                                                  String.valueOf(strategy),
                                                                  strategy.getStatus()).create(),
                               strategy);
            return null;
        }
        if(inDate == null ||
           inOptionRoots == null ||
           inOptionRoots.length == 0) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(INVALID_POSITIONS_BY_OPTION_ROOTS_REQUEST,
                                                                  String.valueOf(strategy)).create(),
                               strategy);
            return null;
        }
        for(String optionRoot : inOptionRoots) {
            if(optionRoot == null ||
               optionRoot.isEmpty()) {
                StrategyModule.log(LogEventBuilder.warn().withMessage(INVALID_POSITIONS_BY_OPTION_ROOTS_REQUEST,
                                                                      String.valueOf(strategy)).create(),
                                   strategy);
                return null;
            }
        }
        try {
            Map<PositionKey<Option>,BigDecimal> result = strategy.getServicesProvider().getOptionPositionsAsOf(inDate,
                                                                                                               inOptionRoots); 
            StrategyModule.log(LogEventBuilder.debug().withMessage(RECEIVED_POSITIONS,
                                                                   String.valueOf(strategy),
                                                                   String.valueOf(result),
                                                                   inDate).create(),
                               strategy);
            return result;
        } catch (Exception e) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(CANNOT_RETRIEVE_POSITIONS_BY_OPTION_ROOTS,
                                                                  String.valueOf(strategy),
                                                                  Arrays.toString(inOptionRoots),
                                                                  inDate)
                                                     .withException(e).create(),
                               strategy);
            return null;
        }
    }
    /**
     * Gets the underlying symbol for the given option root, if available.
     *
     * @param inOptionRoot a <code>String</code> value containing an option root
     * @return a <code>String</code> value containing the symbol for the underlying instrument or <code>null</code> if
     *  no underlying instrument could be found
     */
    protected final String getUnderlying(String inOptionRoot)
    {
        if(!canReceiveData()) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(CANNOT_REQUEST_DATA,
                                                                  String.valueOf(strategy),
                                                                  strategy.getStatus()).create(),
                               strategy);
            return null;
        }
        if(inOptionRoot == null ||
           inOptionRoot.isEmpty()) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(INVALID_UNDERLYING_REQUEST,
                                                                  String.valueOf(strategy),
                                                                  inOptionRoot).create(),
                               strategy);
            return null;
        }
        try {
            String result = strategy.getServicesProvider().getUnderlying(inOptionRoot); 
            StrategyModule.log(LogEventBuilder.debug().withMessage(RECEIVED_UNDERLYING,
                                                                   String.valueOf(strategy),
                                                                   result,
                                                                   inOptionRoot).create(),
                               strategy);
            return result;
        } catch (Exception e) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(CANNOT_RETRIEVE_UNDERLYING,
                                                                  String.valueOf(strategy),
                                                                  inOptionRoot)
                                                     .withException(e).create(),
                               strategy);
            return null;
        }
    }
    /**
     * Gets the set of of known option roots for the given underlying symbol. 
     *
     * @param inUnderlying a <code>String</code> value containing the symbol of an underlying instrument
     * @return a <code>Collection&lt;String&gt;</code> value sorted lexicographically by option root or <code>null</code>
     *  if no option roots could be found
     */
    protected final Collection<String> getOptionRoots(String inUnderlying)
    {
        if(!canReceiveData()) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(CANNOT_REQUEST_DATA,
                                                                  String.valueOf(strategy),
                                                                  strategy.getStatus()).create(),
                               strategy);
            return null;
        }
        if(inUnderlying == null ||
           inUnderlying.isEmpty()) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(INVALID_OPTION_ROOTS_REQUEST,
                                                                  String.valueOf(strategy)).create(),
                               strategy);
            return null;
        }
        try {
            Collection<String> result = strategy.getServicesProvider().getOptionRoots(inUnderlying); 
            StrategyModule.log(LogEventBuilder.debug().withMessage(RECEIVED_OPTION_ROOTS,
                                                                   String.valueOf(strategy),
                                                                   String.valueOf(result),
                                                                   inUnderlying).create(),
                               strategy);
            return result;
        } catch (Exception e) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(CANNOT_RETRIEVE_OPTION_ROOTS,
                                                                  String.valueOf(strategy),
                                                                  inUnderlying)
                                                     .withException(e).create(),
                               strategy);
            return null;
        }
    }
    /**
     * Initiates a data flow request.
     * 
     * <p>See {@link DataFlowSupport#createDataFlow(DataRequest[], boolean)}. 
     * @param inAppendDataSink a <code>boolean</code> value indicating if the sink module should be appended to the
     *   data pipeline, if it's not already requested as the last module and the last module is capable of emitting data.
     * @param inRequests a <code>DataRequest...</code> value containing the ordered list of requests. Each instance
     *   identifies a stage of the data pipeline. The data from the first stage is piped to the next.
     *
     * @return a <code>DataFlowID</code> value containing a unique ID identifying the data flow. The ID can be used to cancel
     *   the data flow request and get more details on it.  Returns <code>null</code> if the request could not be created.
     */
    protected final DataFlowID createDataFlow(boolean inAppendDataSink,
                                              DataRequest... inRequests)
    {
        if(!canReceiveData()) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(CANNOT_REQUEST_DATA,
                                                                  String.valueOf(strategy),
                                                                  strategy.getStatus()).create(),
                               strategy);
            return null;
        }
        if(inRequests == null ||
           inRequests.length == 0) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(INVALID_DATA_REQUEST,
                                                                  String.valueOf(strategy)).create(),
                               strategy);
            return null;
        }
        try {
            return strategy.getServicesProvider().createDataFlow(inRequests,
                                                                 inAppendDataSink);
        } catch (Exception e) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(DATA_REQUEST_FAILED,
                                                                  String.valueOf(strategy),
                                                                  Arrays.toString(inRequests))
                                                     .withException(e).create(),
                               strategy);
            return null;
        }
    }
    /**
     * Cancels a data flow identified by the supplied data flow ID.
     *
     * <p>See {@link DataFlowSupport#cancel(DataFlowID)}.
     *
     * @param inDataFlowID a <code>DataFlowID</code> value containing the request handle that was returned from
     *   a prior call to {@link #createDataFlow(boolean, DataRequest[])}
     */
    protected final void cancelDataFlow(DataFlowID inDataFlowID)
    {
        if(inDataFlowID == null) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(INVALID_DATA_REQUEST_CANCEL,
                                                                  String.valueOf(strategy)).create(),
                               strategy);
            return;
        }
        try {
            strategy.getServicesProvider().cancelDataFlow(inDataFlowID);
        } catch (Exception e) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(DATA_REQUEST_CANCEL_FAILED,
                                                                  String.valueOf(strategy),
                                                                  inDataFlowID)
                                                     .withException(e).create(),
                               strategy);
        }
    }
    /**
     * Gets the {@link ModuleURN} of this strategy.
     *
     * @return a <code>ModuleURN</code> value
     */
    protected final ModuleURN getURN()
    {
        return strategy.getServicesProvider().getURN();
    }
    /**
     * Gets the user data associated with the current user. 
     *
     * @return a <code>Properties</code> value
     */
    protected final Properties getUserData()
    {
        try {
            return strategy.getServicesProvider().getUserData();
        } catch (Exception e) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(FAILED_TO_RETRIEVE_USER_DATA,
                                                                  String.valueOf(strategy))
                                                     .withException(e).create(),
                               strategy);
            return null;
        }
    }
    /**
     * Sets the user data associated with the current user.
     *
     * @param inUserData a <code>Properties</code> value
     */
    protected final void setUserData(Properties inUserData)
    {
        try {
            strategy.getServicesProvider().setUserData(inUserData);
        } catch (Exception e) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(FAILED_TO_SET_USER_DATA,
                                                                  String.valueOf(strategy))
                                                     .withException(e).create(),
                               strategy);
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
            StrategyModule.log(LogEventBuilder.warn().withMessage(INVALID_LOG,
                                                                  String.valueOf(strategy)).create(),
                               strategy);
            return;
        }
        strategy.getServicesProvider().log(LogEventBuilder.debug().withMessage(MESSAGE_1P,
                                                                               inMessage).create());
    }
    /**
     * Emits the given info message to the strategy log output.
     *
     * @param inMessage a <code>String</code> value
     */
    protected void info(String inMessage)
    {
        if(inMessage == null) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(INVALID_LOG,
                                                                  String.valueOf(strategy)).create(),
                               strategy);
            return;
        }
        strategy.getServicesProvider().log(LogEventBuilder.info().withMessage(MESSAGE_1P,
                                                                              inMessage).create());
    }
    /**
     * Emits the given warn message to the strategy log output.
     *
     * @param inMessage a <code>String</code> value
     */
    protected void warn(String inMessage)
    {
        if(inMessage == null) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(INVALID_LOG,
                                                                  String.valueOf(strategy)).create(),
                               strategy);
            return;
        }
        strategy.getServicesProvider().log(LogEventBuilder.warn().withMessage(MESSAGE_1P,
                                                                              inMessage).create());
    }
    /**
     * Emits the given error message to the strategy log output.
     *
     * @param inMessage a <code>String</code> value
     */
    protected void error(String inMessage)
    {
        if(inMessage == null) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(INVALID_LOG,
                                                                  String.valueOf(strategy)).create(),
                               strategy);
            return;
        }
        strategy.getServicesProvider().log(LogEventBuilder.error().withMessage(MESSAGE_1P,
                                                                               inMessage).create());
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
    private final ScheduledExecutorService callbackService = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("StrategyCallback"));  //$NON-NLS-1$
    /**
     * tracks submitted orders
     */
    private final Set<OrderSingle> submittedOrders = new LinkedHashSet<OrderSingle>();
    /**
     * tracks submitted orderIDs
     */
    private final Set<OrderID> submittedOrderIDs = new LinkedHashSet<OrderID>();
    /**
     * tracks orders based on execution reports
     */
    private volatile LiveOrderHistoryManager orderHistoryManager;
    /**
     * dynamically updated list of open orders
     */
    private volatile Map<OrderID,ExecutionReport> openOrders;
    /**
     * static strategy object of which this object is a running representation
     */
    private Strategy strategy;
    /**
     * empty collection used to indicate the lack of reports for an order
     */
    private static final Deque<ReportBase> EMPTY_REPORTS = new UnmodifiableDeque<ReportBase>(new LinkedList<ReportBase>());
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
            StrategyModule.log(LogEventBuilder.debug().withMessage(EXECUTING_CALLBACK,
                                                                   String.valueOf(runningStrategy),
                                                                   new Date()).create(),
                               strategy);
            try {
                runningStrategy.onCallback(data);
            } catch (Exception e) {
                if(strategy.getExecutor() != null) {
                    StrategyModule.log(LogEventBuilder.warn().withMessage(CALLBACK_ERROR,
                                                                          String.valueOf(strategy),
                                                                          strategy.getExecutor().interpretRuntimeException(e))
                                                             .withException(e).create(),
                                       strategy);
                } else {
                    StrategyModule.log(LogEventBuilder.warn().withMessage(CALLBACK_ERROR,
                                                                          String.valueOf(strategy),
                                                                          e.getLocalizedMessage())
                                                             .withException(e).create(),
                                       strategy);
                }
            }
        }
    }
}
