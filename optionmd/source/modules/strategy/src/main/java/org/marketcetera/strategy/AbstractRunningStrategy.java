package org.marketcetera.strategy;

import static org.marketcetera.strategy.Messages.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.marketcetera.client.OrderValidationException;
import org.marketcetera.client.Validations;
import org.marketcetera.client.brokers.BrokerStatus;
import org.marketcetera.core.notifications.Notification;
import org.marketcetera.core.position.PositionKey;
import org.marketcetera.event.Event;
import org.marketcetera.event.impl.LogEventBuilder;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.DataFlowSupport;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.trade.*;
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
     * Returns the list of open orders created during this session in the order they
     * were submitted.
     * 
     * <p>Returns open orders only.  Orders that were canceled, replaced, filled, or
     * otherwise are no longer open will not be returned.  For orders submitted
     * via {@link AbstractRunningStrategy#cancelReplace(OrderID, OrderSingle, boolean)},
     * the {@link OrderSingle} value is returned, not the {@link OrderReplace} value sent
     * to the broker.
     * 
     * @return a <code>List&lt;OrderSingle&gt;</code> value
     */
    final List<OrderSingle> getSubmittedOrders()
    {
        return submittedOrderManager.getOrders();
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
     * @return a <code>List&lt;OrderID&gt;</code> value
     */
    final List<OrderID> getSubmittedOrderIDs()
    {
        return submittedOrderManager.getOrderIDs();
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
            MarketDataRequest request = MarketDataRequest.newRequestFromString(inRequest);
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
            return strategy.getServicesProvider().requestProcessedMarketData(MarketDataRequest.newRequestFromString(inRequest),
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
     * Gets the <code>ExecutionReport</code> values generated during the current
     * session that match the given <code>OrderID</code>.
     * 
     * <p> Note that the <code>OrderID</code> must match an <code>OrderSingle</code>
     * generated by this strategy during the current session. If not, an empty
     * list will be returned.
     * 
     * @param inOrderID an <code>OrderID</code> value corresponding to an
     *            <code>OrderSingle</code> generated during this session by this
     *            strategy via {@link #send(Object)} or
     *            {@link #cancelReplace(OrderID, OrderSingle, boolean)}
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
            submittedOrderManager.add(order.getOrderID(),
                                      order);
        }
        strategy.getServicesProvider().send(inData);
        return true;
    }
    /**
     * Submits a request to cancel the <code>OrderSingle</code> with the given
     * <code>OrderID</code>.
     * 
     * <p> The order must have been submitted by this strategy during this session
     * or this call will have no effect.
     * 
     * @param inOrderID an <code>OrderID</code> value
     * @param inSendOrder a <code>boolean</code> value indicating whether the <code>OrderCancel</code> should be submitted or just returned to the caller.  If <code>false</code>,
     *   it is the caller's responsibility to submit the <code>OrderReplace</code> with {@link #send(Object)}.
     * @return an <code>OrderCancel</code> value containing the cancel order or <code>null</code> if
     *   the <code>OrderCancel</code> could not be constructed
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
        Entry order = submittedOrderManager.get(inOrderID);
        if(order == null) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(INVALID_ORDERID,
                                                                  String.valueOf(strategy),
                                                                  String.valueOf(inOrderID)).create(),
                               strategy);
            return null;
        }
        OrderCancel cancelRequest;
        ExecutionReport executionReportToUse = selectExecutionReportForCancel(order);
        if(executionReportToUse == null) {
            // use an empty execution report
            cancelRequest = Factory.getInstance().createOrderCancel(null);
            cancelRequest.setOriginalOrderID(inOrderID);
            cancelRequest.setBrokerID(order.getUnderlyingOrder().getBrokerID());
            cancelRequest.setQuantity(order.getUnderlyingOrder().getQuantity());
            cancelRequest.setInstrument(order.getUnderlyingOrder().getInstrument());
            cancelRequest.setSide(order.getUnderlyingOrder().getSide());
        } else {
            // use the most recent execution report to seed the cancel request
            cancelRequest = Factory.getInstance().createOrderCancel(executionReportToUse);
            // set the BrokerOrderID to null (EG-762) because the ER acks we get back from the ORS (server)
            //  do not have the BrokerOrderID set correctly.  it is OK to send no BrokerOrderID, but it is
            //  not OK to send an invalid BrokerOrderID.
            cancelRequest.setBrokerOrderID(null);
        }
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
        StrategyModule.log(LogEventBuilder.debug().withMessage(SUBMITTING_CANCEL_ALL_ORDERS_REQUEST,
                                                               String.valueOf(strategy)).create(),
                           strategy);
        // gets a copy of the submitted orders list - iterate over the copy in
        // order to prevent concurrent update problems
        int count = 0;
        for(OrderSingle order : getSubmittedOrders()) {
            try {
                if(cancelOrder(order.getOrderID(),
                               true) != null) {
                    count += 1;
                }
            } catch (Exception e) {
                StrategyModule.log(LogEventBuilder.warn().withMessage(ORDER_CANCEL_FAILED,
                                                                      String.valueOf(strategy),
                                                                      order.getOrderID())
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
     * <p>The order must have been submitted by this strategy during this session or this call will
     * have no effect.  If <code>inSendOrder</code> is <code>false</code>, it is the caller's responsibility to submit the <code>OrderReplace</code>.
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
        Entry order = submittedOrderManager.get(inOrderID);
        if(order == null) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(INVALID_ORDERID,
                                                                  String.valueOf(strategy),
                                                                  String.valueOf(inOrderID)).create(),
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
            replaceOrder.setBrokerID(order.getUnderlyingOrder().getBrokerID());
            replaceOrder.setInstrument(order.getUnderlyingOrder().getInstrument());
            replaceOrder.setSide(order.getUnderlyingOrder().getSide());
            replaceOrder.setOrderType(order.getUnderlyingOrder().getOrderType());
        } else {
            replaceOrder = Factory.getInstance().createOrderReplace(executionReport);
            // set the BrokerOrderID to null (EG-762) because the ER acks we get back from the ORS (server)
            //  do not have the BrokerOrderID set correctly.  it is OK to send no BrokerOrderID, but it is
            //  not OK to send an invalid BrokerOrderID.
            replaceOrder.setBrokerOrderID(null);
        }
        replaceOrder.setQuantity(inNewOrder.getQuantity());
        // special case: when replacing a market order, it is absolutely verbotten to specify a price
        if(OrderType.Market.equals(order.getUnderlyingOrder().getOrderType())) {
            replaceOrder.setPrice(null);
        } else {
            replaceOrder.setPrice(inNewOrder.getPrice());
        }
        replaceOrder.setTimeInForce(inNewOrder.getTimeInForce());
        SLF4JLoggerProxy.debug(AbstractRunningStrategy.class,
                               "{} created {}", //$NON-NLS-1$
                               strategy,
                               replaceOrder);
        submittedOrderManager.add(replaceOrder.getOrderID(),
                                  inNewOrder);
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
     * Gets the position in the given <code>Option</code> at the given point in time.
     *
     * @param inDate a <code>Date</code> value indicating the point in time for which to search
     * @param inOptionRoot a <code>String</code> value
     * @param inExpiry a <code>String</code> value
     * @param inStrikePrice a <code>BigDecimal</code> value
     * @param inOptionType an <code>OptionType</code> value
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
        StrategyModule.log(LogEventBuilder.debug().withMessage(EXECUTION_REPORTS_FOUND,
                                                               String.valueOf(strategy),
                                                               executionReports.size(),
                                                               String.valueOf(inEntry)).create(),
                           strategy);
        // get list iterator set to last element of the list
        ListIterator<ExecutionReport> iterator = executionReports.listIterator(executionReports.size());
        // traverse backwards until a usable execution report is found
        while(iterator.hasPrevious()) {
            ExecutionReport report = iterator.previous();
            if(Originator.Server.equals(report.getOriginator())) {
                StrategyModule.log(LogEventBuilder.debug().withMessage(USING_EXECUTION_REPORT,
                                                                       String.valueOf(strategy),
                                                                       report).create(),
                                   strategy);
                return report;
            }
        }
        StrategyModule.log(LogEventBuilder.debug().withMessage(NO_EXECUTION_REPORT,
                                                               String.valueOf(strategy)).create(),
                           strategy);
        return null;
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
         * Gets the open orders submitted during this strategy session in the order
         * they were submitted.
         * 
         * @return a <code>List&lt;OrderSingle&gt;</code> value
         */
        private List<OrderSingle> getOrders()
        {
            synchronized(submittedOrders) {
                List<OrderSingle> orders = new ArrayList<OrderSingle>();
                for(Entry entry : submittedOrders.values()) {
                    orders.add(entry.getUnderlyingOrder());
                }
                return orders;
            }
        }
        /**
         * Gets the order IDs of the open orders submitted during this strategy session in the order
         * they were submitted.
         * 
         * @return a <code>List&lt;OrderID&gt;</code> value
         */
        private List<OrderID> getOrderIDs()
        {
            synchronized(submittedOrders) {
                return new ArrayList<OrderID>(submittedOrders.keySet());
            }
        }
        /**
         * Gets an open order from the order tracker.
         * 
         * @param inOrderID an <code>OrderID</code> value
         * @return an <code>Entry</code> value or <code>null</code> if the given <code>OrderID</code> does not correspond to an open submitted order
         */
        private Entry get(OrderID inOrderID)
        {
            synchronized(submittedOrders) {
                return submittedOrders.get(inOrderID);
            }
        }
        /**
         * Adds an <code>ExecutionReport</code> to the order tracker.
         * 
         * @param inExecutionReport an <code>ExecutionReport</code> value
         */
        private void add(ExecutionReport inExecutionReport)
        {
            SLF4JLoggerProxy.debug(AbstractRunningStrategy.class,
                                   "{} analyzing {}", //$NON-NLS-1$
                                   this,
                                   inExecutionReport);
            assert(inExecutionReport.getOrderID() != null);
            synchronized(submittedOrders) {
                for(OrderID orderToRemove : findOrdersToRelease(inExecutionReport)) {
                    SLF4JLoggerProxy.debug(AbstractRunningStrategy.class,
                                           "Removing order {} from order-tracker", //$NON-NLS-1$
                                           orderToRemove);
                    submittedOrders.remove(orderToRemove);
                }
                Entry entry = submittedOrders.get(inExecutionReport.getOrderID());
                if(entry != null) {
                    SLF4JLoggerProxy.debug(AbstractRunningStrategy.class,
                                           "Recording execution report for {}", //$NON-NLS-1$
                                           inExecutionReport.getOrderID());
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
         * Examines the given <code>ExecutionReport</code> and determines the
         * <code>OrderID</code> objects that should be removed from the list of active
         * orders, if any.
         *
         * @param inExecutionReport an <code>ExecutionReport</code> value
         * @return a <code>Set&lt;OrderID&gt;</code> value containing the <code>OrderID</code>
         *  values to release, if any
         */
        private Set<OrderID> findOrdersToRelease(ExecutionReport inExecutionReport)
        {
            // check the list of scenarios that justify removing an order from the list of
            //  orders that are cancellable
            // first, if the order status is not cancellable, this indicates that the order
            //  has Moved On and is no longer our concern
            Set<OrderID> orders = new HashSet<OrderID>();
            if(!FIXMessageUtil.isCancellable(inExecutionReport.getOrderStatus().getFIXValue())) {
                // a non-cancellable order is, of course, itself done
                orders.add(inExecutionReport.getOrderID());
            }
            // next, check to see if the order is of a type that implies the demise
            //  and lamented passage of another order
            // the check for either ExecutionType or OrderStatus covers FIX versions
            //  4.2 and earlier (OrderStatus) and later than 4.2 (ExecutionType)
            if(OrderStatus.Canceled.equals(inExecutionReport.getOrderStatus()) ||
              (ExecutionType.Replace.equals(inExecutionReport.getExecutionType()) ||
               OrderStatus.Replaced.equals(inExecutionReport.getOrderStatus()))) {
                orders.add(inExecutionReport.getOriginalOrderID());
            }
            return orders;
        }
        /**
         * Adds an <code>OrderSingle</code> to the order tracker.
         * 
         * @param inOrderID an <code>OrderID</code> value
         * @param inOrder an <code>OrderSingle</code> value
         */
        private void add(OrderID inOrderID,
                         OrderSingle inOrder)
        {
            assert(inOrder != null);
            assert(inOrderID != null);
            synchronized(submittedOrders) {
                SLF4JLoggerProxy.debug(AbstractRunningStrategy.class,
                                       "Adding order {} to order-tracker", //$NON-NLS-1$
                                       inOrderID);
                submittedOrders.put(inOrderID,
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
        /**
         * Gets the underlying order.
         *
         * @return an <code>OrderSingle</code> value
         */
        private OrderSingle getUnderlyingOrder()
        {
            return underlyingOrder;
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
