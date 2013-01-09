package org.marketcetera.strategy;

import static org.marketcetera.strategy.Messages.*;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.management.*;

import org.marketcetera.client.*;
import org.marketcetera.client.brokers.BrokerStatus;
import org.marketcetera.core.Util;
import org.marketcetera.core.notifications.Notification;
import org.marketcetera.core.position.PositionKey;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.core.publisher.PublisherEngine;
import org.marketcetera.event.Event;
import org.marketcetera.event.LogEvent;
import org.marketcetera.event.LogEventLevel;
import org.marketcetera.event.impl.LogEventBuilder;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.metrics.ThreadedMetric;
import org.marketcetera.module.*;
import org.marketcetera.trade.*;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.I18NBoundMessage2P;
import org.marketcetera.util.log.I18NBoundMessage3P;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

import quickfix.Message;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/* $License$ */

/**
 * Strategy Agent implementation for the <code>Strategy</code> module.
 * <p>
 * Module Features
 * <table>
 * <tr><th>Capabilities</th><td>Data Emitter, Data Receiver, Data Flow Requester</td></tr>
 * <tr><th>Stops data flows</th><td>No</td></tr>
 * <tr><th>Start Operation</th><td>Plumbs all the data flows, compiles the strategy.</td></tr>
 * <tr><th>Stop Operation</th><td>Stops the strategy, unplumbs all the dataflows</td></tr>
 * <tr><th>Management Interface</th><td>{@link StrategyMXBean}</td></tr>
 * <tr><th>MX Notification</th><td>{@link AttributeChangeNotification}
 * whenever {@link #getStatus()} changes. </td></tr>
 * <tr><th>Factory</th><td>{@link StrategyModuleFactory}</td></tr>
 * </table>
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
final class StrategyModule
        extends Module
        implements DataEmitter, DataFlowRequester, DataReceiver, ServicesProvider, StrategyMXBean, NotificationEmitter
{
    /* (non-Javadoc)
     * @see org.marketcetera.module.DataEmitter#cancel(org.marketcetera.module.RequestID)
     */
    @Override
    public void cancel(DataFlowID inFlowID,
                       RequestID inRequestID)
    {
        unsubscribe(inRequestID);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.DataEmitter#requestData(org.marketcetera.module.DataRequest, org.marketcetera.module.DataEmitterSupport)
     */
    @Override
    public void requestData(DataRequest inRequest,
                            DataEmitterSupport inSupport)
            throws UnsupportedRequestParameterType, IllegalRequestParameterValue
    {
        Object requestPayload = inRequest.getData();
        OutputType request;
        if(requestPayload == null) {
            throw new IllegalRequestParameterValue(getURN(),
                                                   null);
        }
        if(requestPayload instanceof String) {
            try {
                request = OutputType.valueOf(((String)requestPayload).toUpperCase());
            } catch (Exception e) {
                throw new IllegalRequestParameterValue(getURN(),
                                                       requestPayload);
            }
        } else if(requestPayload instanceof OutputType) {
            request = (OutputType)requestPayload;
        } else if(requestPayload instanceof InternalRequest) {
            InternalRequest internalRequest = (InternalRequest)requestPayload;
            SLF4JLoggerProxy.debug(StrategyModule.class,
                                   "{} received a request to set up a specialized data flow to {}", //$NON-NLS-1$
                                   strategy,
                                   internalRequest.originalRequester); //$NON-NLS-1$
            internalDataFlows.put(internalRequest.originalRequester,
                                  inSupport);
            return;
        } else {
            throw new UnsupportedRequestParameterType(getURN(),
                                                      requestPayload);
        }
        subscribe(request,
                  inSupport);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.DataFlowRequester#setFlowSupport(org.marketcetera.module.DataFlowSupport)
     */
    @Override
    public void setFlowSupport(DataFlowSupport inSupport)
    {
        dataFlowSupport = inSupport;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.DataReceiver#receiveData(org.marketcetera.module.DataFlowID, java.lang.Object)
     */
    @Override
    public void receiveData(DataFlowID inFlowID,
                            Object inData)
            throws UnsupportedDataTypeException, StopDataFlowException
    {
        ThreadedMetric.event("strategy-IN");  //$NON-NLS-1$
        assertStateForReceiveData();
        SLF4JLoggerProxy.trace(StrategyModule.class,
                               "{} received {}", //$NON-NLS-1$
                               strategy,
                               inData);
        if(inData instanceof Event) {
            Event event = (Event)inData;
            synchronized(dataFlows) {
                event.setSource(dataFlows.get(inFlowID));
            }
        }
        strategy.dataReceived(inData);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.OutboundServicesProvider#cancelOrder(org.marketcetera.trade.OrderCancel)
     */
    @Override
    public void cancelOrder(OrderCancel inCancel)
    {
        publish(inCancel);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.OutboundServicesProvider#cancelReplace(org.marketcetera.trade.OrderReplace)
     */
    @Override
    public void cancelReplace(OrderReplace inReplace)
    {
        publish(inReplace);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.OutboundServices#marketDataRequest(org.marketcetera.marketdata.MarketDataRequest, java.lang.String)
     */
    @Override
    public int requestMarketData(MarketDataRequest inRequest)
    {
        if(inRequest == null) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(INVALID_MARKET_DATA_REQUEST,
                                                                  String.valueOf(strategy),
                                                                  inRequest).create(),
                               strategy);
            return 0;
        }
        int requestID = counter.incrementAndGet();
        try {
            ModuleURN marketDataURN = constructMarketDataUrn(inRequest.getProvider());
            SLF4JLoggerProxy.debug(StrategyModule.class,
                                   "{} received a market data request {} for data from {}", //$NON-NLS-1$
                                   strategy,
                                   inRequest,
                                   marketDataURN);
            DataFlowID dataFlowID = dataFlowSupport.createDataFlow(new DataRequest[] { new DataRequest(marketDataURN,
                                                                                                       inRequest),
                                                                                       new DataRequest(getURN()) },
                                                                   false);
            synchronized(dataFlows) {
                dataFlows.put(dataFlowID,
                              requestID);
            }
        } catch (Exception e) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(MARKET_DATA_REQUEST_FAILED,
                                                                  inRequest)
                                                     .withException(e).create(),
                               strategy);
            return 0;
        }
        return requestID;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.OutboundServicesProvider#requestMarketDataWithCEP(org.marketcetera.marketdata.MarketDataRequest, java.lang.String, java.lang.String[], java.lang.String, java.lang.String)
     */
    @Override
    public int requestProcessedMarketData(MarketDataRequest inRequest,
                                          String[] inStatements,
                                          String inCEPSource,
                                          String inNamespace)
    {
        if(inRequest == null ||
           inStatements == null ||
           inStatements.length == 0 ||
           inCEPSource == null ||
           inCEPSource.isEmpty() ||
           inNamespace == null ||
           inNamespace.isEmpty()) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(INVALID_COMBINED_DATA_REQUEST,
                                                                  String.valueOf(strategy),
                                                                  inRequest,
                                                                  Arrays.toString(inStatements),
                                                                  inCEPSource,
                                                                  inNamespace).create(),
                               strategy);
            return 0;
        }
        int requestID = counter.incrementAndGet();
        // construct a request that connects the provider to the cep query
        try {
            ModuleURN marketDataURN = constructMarketDataUrn(inRequest.getProvider());
            ModuleURN cepDataURN = constructCepUrn(inCEPSource,
                                                   inNamespace);
            SLF4JLoggerProxy.debug(StrategyModule.class,
                                   "{} received a processed market data request {} for market data from {} via {}", //$NON-NLS-1$
                                   strategy,
                                   inRequest,
                                   marketDataURN,
                                   cepDataURN);
            DataFlowID dataFlowID = dataFlowSupport.createDataFlow(new DataRequest[] { new DataRequest(marketDataURN,
                                                                                                       inRequest),
                                                                                       new DataRequest(cepDataURN,
                                                                                                       determineCepStatements(inCEPSource,
                                                                                                                              inStatements)),
                                                                                       new DataRequest(getURN()) },
                                                                   false);
            // add request to both counters
            synchronized(dataFlows) {
                dataFlows.put(dataFlowID,
                              requestID);
            }
            return requestID;
        } catch (Exception e) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(COMBINED_DATA_REQUEST_FAILED,
                                                                  inRequest,
                                                                  Arrays.toString(inStatements),
                                                                  inCEPSource,
                                                                  inNamespace)
                                                      .withException(e).create(),
                               strategy);
            return 0;
        }        
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.OutboundServices#cancelAllDataRequests()
     */
    @Override
    public void cancelAllDataRequests()
    {
        synchronized(dataFlows) {
            // create a copy of the list because the cancel call is going to modify the collection
            List<Integer> activeRequests = new ArrayList<Integer>(dataFlows.inverse().keySet());
            for(int request : activeRequests) {
                try {
                    cancelDataRequest(request);
                } catch (Exception e) {
                    StrategyModule.log(LogEventBuilder.warn().withMessage(UNABLE_TO_CANCEL_DATA_REQUEST,
                                                                          String.valueOf(strategy),
                                                                          request)
                                                             .withException(e).create(),
                                       strategy);
                }
            }
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.OutboundServices#cancelDataRequest(int)
     */
    @Override
    public void cancelDataRequest(int inDataRequestID)
    {
        synchronized(dataFlows) {
            if(!dataFlows.inverse().containsKey(inDataRequestID)) {
                StrategyModule.log(LogEventBuilder.warn().withMessage(NO_DATA_HANDLE,
                                                                      String.valueOf(strategy),
                                                                      inDataRequestID).create(),
                                   strategy);
                return;
            }
            try {
                doCancelDataRequest(inDataRequestID);
            } catch (Exception e) {
                StrategyModule.log(LogEventBuilder.warn().withMessage(UNABLE_TO_CANCEL_DATA_REQUEST,
                                                                      String.valueOf(strategy),
                                                                      inDataRequestID)
                                                         .withException(e).create(),
                                   strategy);
            }
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.OutboundServicesProvider#requestCEPData(java.lang.String[], java.lang.String)
     */
    @Override
    public int requestCEPData(String[] inStatements,
                              String inSource,
                              String inNamespace)
    {
        if(inStatements == null ||
           inStatements.length == 0) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(INVALID_CEP_REQUEST,
                                                                  String.valueOf(strategy),
                                                                  Arrays.toString(inStatements),
                                                                  String.valueOf(inSource),
                                                                  String.valueOf(inNamespace)).create(),
                               strategy);
            return 0;
        }
        int requestID = counter.incrementAndGet();
        ModuleURN providerURN = constructCepUrn(inSource,
                                                inNamespace);
        try {
            synchronized(dataFlows) {
                DataFlowID flowID = dataFlowSupport.createDataFlow(new DataRequest[] { new DataRequest(providerURN,
                                                                                                       determineCepStatements(inSource,
                                                                                                                              inStatements)),
                                                                                       new DataRequest(getURN()) },
                                                                   false); 
                dataFlows.put(flowID,
                              requestID);
            }
        } catch (Exception e) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(CEP_REQUEST_FAILED,
                                                                  Arrays.toString(inStatements),
                                                                  inSource)
                                                     .withException(e).create(),
                               strategy);
            return 0;
        }
        return requestID;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.StrategyMXBean#setOrdersDestination(java.lang.String)
     */
    @Override
    public void setOutputDestination(String inDestination)
    {
        if(inDestination == null ||
           inDestination.isEmpty()) {
            outputDestination = null;
            SLF4JLoggerProxy.debug(StrategyModule.class,
                                   "Setting output destination to null"); //$NON-NLS-1$
            return;
        }
        outputDestination = new ModuleURN(inDestination);
        SLF4JLoggerProxy.debug(StrategyModule.class,
                               "Setting output destination to {}", //$NON-NLS-1$
                               outputDestination);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.StrategyMXBean#setParameters(java.lang.String)
     */
    @Override
    public void setParameters(String inParameters)
    {
        if(inParameters == null ||
           inParameters.isEmpty()) {
            parameters = null;
            SLF4JLoggerProxy.debug(StrategyModule.class,
                                   "Setting parameters to null"); //$NON-NLS-1$
            return;
        }
        if(parameters != null) {
            parameters.clear();
        }
        parameters = Util.propertiesFromString(inParameters);
        SLF4JLoggerProxy.debug(StrategyModule.class,
                               "Setting parameters to {}", //$NON-NLS-1$
                               parameters);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.StrategyMXBean#getOrdersDestination()
     */
    @Override
    public String getOutputDestination()
    {
        if(outputDestination == null) {
            return null;
        }
        return outputDestination.getValue();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.StrategyMXBean#isRoutingOrdersToORS()
     */
    @Override
    public boolean isRoutingOrdersToORS()
    {
        synchronized(this) {
            return routeOrdersToORS;
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.StrategyMXBean#setRoutingOrdersToORS(boolean)
     */
    @Override
    public void setRoutingOrdersToORS(boolean inValue)
    {
        try {
            if(routeOrdersToORS != inValue) {
                if(getState().isStarted()) {
                    if(inValue) {
                        establishORSRouting();
                    } else {
                        disconnectORSRouting();
                    }
                }
            }
            // change the value only if the above call succeeded
            routeOrdersToORS = inValue;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.StrategyMXBean#getName()
     */
    @Override
    public String getName()
    {
        return name;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.StrategyMXBean#getLanguage()
     */
    @Override
    public Language getLanguage()
    {
        return type;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.StrategyMXBean#getParameters()
     */
    @Override
    public String getParameters()
    {
        if(parameters == null) {
            return null;
        }
        return Util.propertiesToString(parameters);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.OutboundServicesProvider#setMessage(quickfix.Message)
     */
    @Override
    public void sendMessage(Message inMessage,
                            BrokerID inBroker)
    {
        if(inMessage == null ||
           inBroker == null) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(INVALID_MESSAGE,
                                                                  String.valueOf(strategy)).create(),
                               strategy);
            return;
        }
        try {
            publish(Factory.getInstance().createOrder(inMessage,
                                                      inBroker));
        } catch (Exception e) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(SEND_MESSAGE_FAILED,
                                                                  String.valueOf(strategy),
                                                                  inMessage,
                                                                  inBroker)
                                                     .withException(e).create(),
                               strategy);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.OutboundServicesProvider#sendOther(java.lang.Object)
     */
    @Override
    public void send(Object inData)
    {
        if(inData == null) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(INVALID_DATA,
                                                                  String.valueOf(strategy)).create(),
                               strategy);
            return;
        }
        publish(inData);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.OutboundServices#sendSuggestion(java.lang.Object)
     */
    @Override
    public void sendSuggestion(Suggestion inSuggestion)
    {
        if(inSuggestion == null) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(INVALID_TRADE_SUGGESTION,
                                                                  String.valueOf(strategy)).create(),
                               strategy);
            return;
        }
        publish(inSuggestion);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.OutboundServicesProvider#sendEvent(org.marketcetera.event.EventBase)
     */
    @Override
    public void sendEvent(Event inEvent,
                          String inProvider,
                          String inNamespace)
    {
        // event must not be null, but the other two parameters may be
        if(inEvent == null) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(INVALID_EVENT,
                                                                  String.valueOf(strategy)).create(),
                               strategy);
            return;
        }
        // event is not null, check that provider and namespace are not null
        if(inProvider != null &&
           !inProvider.isEmpty() &&
           inNamespace != null &&
           !inNamespace.isEmpty()) {
            // event, provider, and namespace are not null, send the event to the described cep module
            ModuleURN cepModuleURN = constructCepUrn(inProvider,
                                                     inNamespace);
            try {
                sendEventToCEP(cepModuleURN,
                               inEvent);
            } catch (Exception e) {
                // warn that the event was not sent to CEP, but continue to send to subscribers
                // this may not be an error as the CEP module may not exist
                StrategyModule.log(LogEventBuilder.warn().withMessage(CANNOT_SEND_EVENT_TO_CEP,
                                                                      String.valueOf(strategy),
                                                                      inEvent,
                                                                      cepModuleURN).create(),
                                   strategy);
            }
            // done sending event to CEP, for better or worse
        }
        // send to subscribers
        publish(inEvent);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.OutboundServicesProvider#setNotification(org.marketcetera.core.notifications.Notification)
     */
    @Override
    public void sendNotification(Notification inNotification)
    {
        if(inNotification == null) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(INVALID_NOTIFICATION,
                                                                  String.valueOf(strategy)).create(),
                               strategy);
            return;
        }
        publish(inNotification);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.OutboundServicesProvider#log(java.lang.String)
     */
    @Override
    public void log(LogEvent inEvent)
    {
        if(inEvent == null) {
            StrategyModule.log(LogEventBuilder.warn().withMessage(INVALID_LOG,
                                                                  String.valueOf(strategy)).create(),
                               strategy);
            return;
        }
        if(shouldLog(inEvent)) {
            publish(inEvent);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.InboundServicesProvider#getBrokers()
     */
    @Override
    public List<BrokerStatus> getBrokers()
        throws ConnectionException, ClientInitException
    {
        return clientFactory.getClient().getBrokersStatus().getBrokers();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.InboundServicesProvider#getEquityPositionAsOf(java.util.Date, org.marketcetera.trade.Equity)
     */
    @Override
    public BigDecimal getPositionAsOf(Date inDate,
                                      Equity inEquity)
        throws ConnectionException, ClientInitException
    {
        return clientFactory.getClient().getEquityPositionAsOf(inDate,
                                                               inEquity);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.ServicesProvider#getAllOptionPositionsAsOf(java.util.Date)
     */
    @Override
    public Map<PositionKey<Option>, BigDecimal> getAllOptionPositionsAsOf(Date inDate)
            throws ConnectionException, ClientInitException
    {
        return clientFactory.getClient().getAllOptionPositionsAsOf(inDate);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.ServicesProvider#getAllFuturePositionsAsOf(java.util.Date)
     */
    @Override
    public Map<PositionKey<Future>, BigDecimal> getAllFuturePositionsAsOf(Date inDate)
            throws ConnectionException, ClientInitException
    {
        return clientFactory.getClient().getAllFuturePositionsAsOf(inDate);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.ServicesProvider#getFuturePositionAsOf(java.util.Date, org.marketcetera.trade.Future)
     */
    @Override
    public BigDecimal getFuturePositionAsOf(Date inDate,
                                            Future inFuture)
            throws ConnectionException, ClientInitException
    {
        return clientFactory.getClient().getFuturePositionAsOf(inDate,
                                                               inFuture);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.ServicesProvider#getOptionPositionAsOf(java.util.Date, org.marketcetera.trade.Option)
     */
    @Override
    public BigDecimal getOptionPositionAsOf(Date inDate,
                                            Option inOption)
            throws ConnectionException, ClientInitException
    {
        return clientFactory.getClient().getOptionPositionAsOf(inDate,
                                                               inOption);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.ServicesProvider#getOptionPositionsAsOf(java.util.Date, java.lang.String[])
     */
    @Override
    public Map<PositionKey<Option>, BigDecimal> getOptionPositionsAsOf(Date inDate,
                                                                       String... inOptionRoots)
            throws ConnectionException, ClientInitException
    {
        return clientFactory.getClient().getOptionPositionsAsOf(inDate,
                                                                inOptionRoots);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.ServicesProvider#getOptionRoots(java.lang.String)
     */
    @Override
    public Collection<String> getOptionRoots(String inUnderlying)
            throws ConnectionException, ClientInitException
    {
        return clientFactory.getClient().getOptionRoots(inUnderlying);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.ServicesProvider#getAllPositionsAsOf(java.util.Date)
     */
    @Override
    public Map<PositionKey<Equity>, BigDecimal> getAllPositionsAsOf(Date inDate)
            throws ConnectionException, ClientInitException
    {
        return clientFactory.getClient().getAllEquityPositionsAsOf(inDate);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.ServicesProvider#getUnderlying(java.lang.String)
     */
    @Override
    public String getUnderlying(String inOptionRoot)
            throws ConnectionException, ClientInitException
    {
        return clientFactory.getClient().getUnderlying(inOptionRoot);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.StrategyMXBean#getStatus()
     */
    @Override
    public String getStatus()
    {
        return strategy.getStatus().toString();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.OutboundServicesProvider#statusChanged(org.marketcetera.strategy.Status, org.marketcetera.strategy.Status)
     */
    @Override
    public void statusChanged(Status inOldStatus,
                              Status inNewStatus)
    {
        notificationDelegate.sendNotification(new AttributeChangeNotification(this,
                                                                              jmxNotificationCounter.getAndIncrement(),
                                                                              System.currentTimeMillis(),
                                                                              STATUS_CHANGED.getText(),
                                                                              "Status", //$NON-NLS-1$
                                                                              "String", //$NON-NLS-1$
                                                                              inOldStatus,
                                                                              inNewStatus));
    }
    /* (non-Javadoc)
     * @see javax.management.NotificationEmitter#removeNotificationListener(javax.management.NotificationListener, javax.management.NotificationFilter, java.lang.Object)
     */
    @Override
    public void removeNotificationListener(NotificationListener inListener,
                                           NotificationFilter inFilter,
                                           Object inHandback)
            throws ListenerNotFoundException
    {
        notificationDelegate.removeNotificationListener(inListener,
                                                        inFilter,
                                                        inHandback);
    }
    /* (non-Javadoc)
     * @see javax.management.NotificationBroadcaster#addNotificationListener(javax.management.NotificationListener, javax.management.NotificationFilter, java.lang.Object)
     */
    @Override
    public void addNotificationListener(NotificationListener inListener,
                                        NotificationFilter inFilter,
                                        Object inHandback)
            throws IllegalArgumentException
    {
        notificationDelegate.addNotificationListener(inListener,
                                                     inFilter,
                                                     inHandback);
    }
    /* (non-Javadoc)
     * @see javax.management.NotificationBroadcaster#getNotificationInfo()
     */
    @Override
    public MBeanNotificationInfo[] getNotificationInfo()
    {
        return notificationDelegate.getNotificationInfo();
    }
    /* (non-Javadoc)
     * @see javax.management.NotificationBroadcaster#removeNotificationListener(javax.management.NotificationListener)
     */
    @Override
    public void removeNotificationListener(NotificationListener inListener)
            throws ListenerNotFoundException
    {
        notificationDelegate.removeNotificationListener(inListener);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.OutboundServicesProvider#cancelDataFlow(org.marketcetera.module.DataFlowID)
     */
    @Override
    public void cancelDataFlow(DataFlowID inDataFlowID)
        throws ModuleException
    {
        synchronized(dataFlows) {
            dataFlowSupport.cancel(inDataFlowID);
            dataFlows.remove(inDataFlowID);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.OutboundServicesProvider#createDataFlow(org.marketcetera.module.DataRequest[], boolean)
     */
    @Override
    public DataFlowID createDataFlow(DataRequest[] inRequests,
                                     boolean inAppendDataSink)
        throws ModuleException
    {
        synchronized(dataFlows) {
            DataFlowID dataFlowID = dataFlowSupport.createDataFlow(inRequests,
                                                                   inAppendDataSink); 
            dataFlows.put(dataFlowID,
                          counter.incrementAndGet());
            return dataFlowID;
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.ServicesProvider#getUserData()
     */
    @Override
    public Properties getUserData()
            throws ConnectionException, ClientInitException
    {
        Properties data = clientFactory.getClient().getUserData();
        return data == null ? new Properties() : data;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.ServicesProvider#setUserData(java.util.Properties)
     */
    @Override
    public void setUserData(Properties inData)
            throws ConnectionException, ClientInitException
    {
        clientFactory.getClient().setUserData(inData);
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder output = new StringBuilder();
        output.append(type).append(" strategy ").append(name); //$NON-NLS-1$
        return output.toString();
    }
    /**
     * Logs the given event. 
     *
     * @param inEvent a <code>LogEvent</code> value
     * @param inStrategy a <code>Strategy</code> value
     */
    static void log(LogEvent inEvent,
                    Strategy inStrategy)
    {
        if(shouldLog(inEvent)) {
            doLogger(inEvent);
            inStrategy.getServicesProvider().log(inEvent);
        }
    }
    /**
     * Determines if the given event should be emitted or not.
     *
     * @param inEvent a <code>LogEvent</code> value
     * @return a <code>boolean</code> value
     */
    private static boolean shouldLog(LogEvent inEvent) {
        return LogEventLevel.shouldLog(inEvent,
                                       Strategy.STRATEGY_MESSAGES);
    }
    /**
     * Logs the given message to the standard log.
     *
     * @param inEvent a <code>LogEvent</code> value
     */
    private static void doLogger(LogEvent inEvent)
    {
        Throwable exception = inEvent.getException();
        String message = inEvent.getMessage();
        if(LogEventLevel.DEBUG.equals(inEvent.getLevel())) {
            if(exception == null) {
                MESSAGE_1P.debug(Strategy.STRATEGY_MESSAGES,
                                 message);
            } else {
                MESSAGE_1P.debug(Strategy.STRATEGY_MESSAGES,
                                 exception,
                                 message);
            }
        } else if(LogEventLevel.INFO.equals(inEvent.getLevel())) {
            if(exception == null) {
                MESSAGE_1P.info(Strategy.STRATEGY_MESSAGES,
                                message);
            } else {
                MESSAGE_1P.info(Strategy.STRATEGY_MESSAGES,
                                exception,
                                message);
            }
        } else if(LogEventLevel.WARN.equals(inEvent.getLevel())) {
            if(exception == null) {
                MESSAGE_1P.warn(Strategy.STRATEGY_MESSAGES,
                                message);
            } else {
                MESSAGE_1P.warn(Strategy.STRATEGY_MESSAGES,
                                exception,
                                message);
            }
        } else if(LogEventLevel.ERROR.equals(inEvent.getLevel())) {
            if(exception == null) {
                MESSAGE_1P.error(Strategy.STRATEGY_MESSAGES,
                                 message);
            } else {
                MESSAGE_1P.error(Strategy.STRATEGY_MESSAGES,
                                 exception,
                                 message);
            }
        }
    }
    /**
     * Gets a <code>StrategyModule</code> instance with the given parameters.
     * 
     * <p>The module returned is guaranteed to be a valid, unstarted <code>StrategyModule</code>.
     *
     * @param inParameters an <code>Object...</code> value
     * @return a <code>StrategyModule</code> value
     * @throws ModuleCreationException if the module cannot be created
     */
    static StrategyModule getStrategyModule(Object...inParameters)
        throws ModuleCreationException
    {
        // must have 7 parameters, though the first and the last four may be null
        if(inParameters == null ||
           inParameters.length != 7) {
            throw new ModuleCreationException(PARAMETER_COUNT_ERROR);
        }
        // parameter 1 is the URN name of the strategy or null for the system to create a name
        String instanceName = null;
        if(inParameters[0] != null) {
            if(inParameters[0] instanceof String) {
                if(((String)inParameters[0]).isEmpty()) {
                    throw new ModuleCreationException(EMPTY_INSTANCE_ERROR);
                } else {
                    instanceName = (String)inParameters[0];
                }                
            } else {
                throw new ModuleCreationException(new I18NBoundMessage3P(PARAMETER_TYPE_ERROR,
                                                                         1,
                                                                         String.class.getName(),
                                                                         inParameters[0].getClass().getName()));
            }
        }
        // parameter 2 is the strategy name (human-readable) and must be non-null and have non-zero length
        String name;
        if(inParameters[1] == null) {
            throw new ModuleCreationException(new I18NBoundMessage2P(NULL_PARAMETER_ERROR,
                                                                     2,
                                                                     String.class.getName()));
        }
        if(inParameters[1] instanceof String) {
            if(((String)inParameters[1]).isEmpty()) {
                throw new ModuleCreationException(EMPTY_NAME_ERROR);
            } else {
                name = (String)inParameters[1];
            }
        } else {
            throw new ModuleCreationException(new I18NBoundMessage3P(PARAMETER_TYPE_ERROR,
                                                                     2,
                                                                     String.class.getName(),
                                                                     inParameters[1].getClass().getName()));
        }
        // parameter 3 is the language.  the parameter may be of type Language or may be a String describing the contents of a Language enum value 
        Language type = null;
        if(inParameters[2] == null) {
            throw new ModuleCreationException(new I18NBoundMessage2P(NULL_PARAMETER_ERROR,
                                                                     3,
                                                                     Language.class.getName()));
        }
        if(inParameters[2] instanceof Language) {
            type = (Language)inParameters[2];
        } else {
            if(inParameters[2] instanceof String) {
                try {
                    type = Language.valueOf(((String)inParameters[2]).toUpperCase());
                } catch (Exception e) {
                    throw new ModuleCreationException(new I18NBoundMessage1P(INVALID_LANGUAGE_ERROR,
                                                                             inParameters[2].toString()));
                }
            } else {
                throw new ModuleCreationException(new I18NBoundMessage3P(PARAMETER_TYPE_ERROR,
                                                                         3,
                                                                         Language.class.getName(),
                                                                         inParameters[2].getClass().getName()));
            }
        }
        // parameter 4 is the strategy source.  the parameter must be of type File, must be non-null, must exist, and must be readable
        File source;
        if(inParameters[3] == null) {
            throw new ModuleCreationException(new I18NBoundMessage2P(NULL_PARAMETER_ERROR,
                                                                     4,
                                                                     File.class.getName()));
        }
        if(inParameters[3] instanceof File) {
            source = (File)inParameters[3];
            if(!(source.exists() ||
                    source.canRead())) {
                throw new ModuleCreationException(new I18NBoundMessage1P(FILE_DOES_NOT_EXIST_OR_IS_NOT_READABLE,
                                                                         source.getAbsolutePath()));
            }
        } else {
            throw new ModuleCreationException(new I18NBoundMessage3P(PARAMETER_TYPE_ERROR,
                                                                     4,
                                                                     File.class.getName(),
                                                                     inParameters[3].getClass().getName()));
        }
        // parameter 5 is a Properties object.  The parameter may be null.  If non-null, these values are made available to the running strategy.
        Properties parameters = null;
        if(inParameters[4] != null) {
            if(inParameters[4] instanceof Properties) {
                parameters = (Properties)inParameters[4];
            } else {
                throw new ModuleCreationException(new I18NBoundMessage3P(PARAMETER_TYPE_ERROR,
                                                                         5,
                                                                         Properties.class.getName(),
                                                                         inParameters[4].getClass().getName()));
            }
        }
        // parameter 6 is a Boolean.  This parameter may be null.  If non-null and true, it indicates that this strategy should route outgoing
        //  orders to the ORS client.  Otherwise, orders will be swallowed.
        boolean routeOrdersToORS = false;
        if(inParameters[5] != null) {
            if(inParameters[5] instanceof Boolean) {
                routeOrdersToORS = (Boolean)inParameters[5];
            } else {
                throw new ModuleCreationException(new I18NBoundMessage3P(PARAMETER_TYPE_ERROR,
                                                                         6,
                                                                         Boolean.class.getName(),
                                                                         inParameters[5].getClass().getName()));
            }
        }
        // parameter 7 is a ModuleURN.  This parameter may be null.  If non-null, it must describe a module instance that is started and able
        //  to receive data.  All output will be sent to this module.
        ModuleURN outputInstance = null;
        if(inParameters[6] != null) {
            if(inParameters[6] instanceof ModuleURN) {
                outputInstance = (ModuleURN)inParameters[6];
            } else {
                throw new ModuleCreationException(new I18NBoundMessage3P(PARAMETER_TYPE_ERROR,
                                                                         7,
                                                                         ModuleURN.class.getName(),
                                                                         inParameters[6].getClass().getName()));
            }
        }
        return new StrategyModule(instanceName == null ? generateInstanceURN(name) : new ModuleURN(StrategyModuleFactory.PROVIDER_URN,
                                                                                                   instanceName),
                                  name,
                                  type,
                                  source,
                                  parameters,
                                  routeOrdersToORS,
                                  outputInstance);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.Module#preStart()
     */
    @Override
    protected void preStart()
            throws ModuleException
    {
        assertStateForPreStart();
        // add destination data flows, if specified by the object parameters
        synchronized(dataFlows) {
            if(outputDestination != null) {
                createDataFlow(new DataRequest[] { new DataRequest(getURN(),
                                                                   OutputType.ALL),
                                                   new DataRequest(outputDestination) },
                               false);
            }
            // set the connection to the ORS to the correct value
            if(routeOrdersToORS) {
                establishORSRouting();
            } else {
                disconnectORSRouting();
            }
            // request execution reports from the ORS client
            try {
                dataFlows.put(dataFlowSupport.createDataFlow(new DataRequest[] { new DataRequest(ClientModuleFactory.INSTANCE_URN),
                                                                                 new DataRequest(getURN()) },
                                                                false),
                              counter.incrementAndGet());
            } catch (Exception e) {
                EXECUTION_REPORT_REQUEST_FAILED.warn(StrategyModule.class,
                                                     name,
                                                     ClientModuleFactory.INSTANCE_URN);
            }
        }
        try {
            strategy = new StrategyImpl(name,
                                        getURN().getValue(),
                                        type,
                                        source,
                                        parameters,
                                        getURN().instanceName(),
                                        this);
            strategy.start();
        } catch (Exception e) {
            throw new ModuleException(e,
                                      FAILED_TO_START);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.Module#preStop()
     */
    @Override
    protected void preStop()
            throws ModuleException
    {
        try {
            strategy.stop();
        } catch (ModuleException e) {
            throw e;
        } catch (Exception e) {
            // otherwise a strategy may not prevent itself from being stopped
            STOP_ERROR.warn(StrategyModule.class,
                            e,
                            strategy);
        }
        cancelAllDataRequests();
        disconnectORSRouting();
    }
    /**
     * Generates an instance URN guaranteed to be unique with the scope of this JVM.
     *
     * @return a <code>ModuleURN</code> value
     */
    private static final ModuleURN generateInstanceURN(String inName)
    {
        // the sanitized name is constructed to match the restrictions of the module framework for URN names
        // notice that this works for ASCII stuff only, which is what we want.  if the incoming name is non-ASCII,
        //  all the characters will be removed, again, according to how the module framework demands.
        String sanitizedName = inName.replaceAll("[^A-Z|a-z|0-9]", //$NON-NLS-1$
                                                 ""); //$NON-NLS-1$
        return new ModuleURN(StrategyModuleFactory.PROVIDER_URN,
                             String.format("strategy%s%s", //$NON-NLS-1$
                                           sanitizedName,
                                           Integer.toHexString(counter.incrementAndGet())));
    }
    /**
     * Constructs a <code>ModuleURN</code> for a <code>CEP</code> module from the given components. 
     *
     * @param inSource a <code>String</code> value containing the name of a <code>CEP</code> provider
     * @param inNamespace a <code>String</code> value containing the namespace of a <code>CEP</code> provider query
     * @return a <code>ModuleURN</code> value containing the URN of the CEP module - there is no guarantee that this
     *   URN exists or is started
     */
    private static ModuleURN constructCepUrn(String inSource,
                                             String inNamespace)
    {
        // this should live somewhere in CEP - don't like this
        assert(inSource != null);
        assert(inNamespace != null);
        return new ModuleURN(String.format("metc:cep:%s:%s", //$NON-NLS-1$
                                           inSource,
                                           inNamespace));
    }
    /**
     * Determines the correct set of statements to return depending on the CEP source. 
     *
     * This method is a hack to get around the lack of a consistent API for CEP.  One provider
     * takes a different set of parameters than the other.
     * 
     * @param inSource a <code>String</code> value containing the name of the CEP provider
     * @param inStatements a <code>String[]</code> value containing the CEP statements
     * @return an <code>Object</code> value containing the CEP statements to pass to the provider
     */
    private static Object determineCepStatements(String inSource,
                                                 String[] inStatements)
    {
        if(inSource.equals("esper")) { //$NON-NLS-1$
            return inStatements;
        } else {
            return inStatements[0];
        }
    }
    /**
     * Constructs a <code>ModuleURN</code> to use to request market data.
     *
     * @param inSource a <code>String</code> value containing the name of the provider
     * @return
     */
    private static ModuleURN constructMarketDataUrn(String inSource)
    {
        return new ModuleURN(String.format("metc:mdata:%s", //$NON-NLS-1$
                                           inSource));
    }
    /**
     * Create a new StrategyModule instance.
     *
     * @param inURN a <code>ModuleURN</code> value containing the instance URN for this strategy
     * @param inName a <code>String</code> value containing the user-specified, human-readable name for this strategy
     * @param inType a <code>Language</code> value containing the language type as which to execute this strategy
     * @param inSource a <code>File</code> value containing the source of the strategy code
     * @param inParameters a <code>Properties</code> value containing parameters to pass to the strategy.  may be null.
     * @param inRouteOrdersToORS a <code>boolean</code> value indicating whether to route orders to the ORS or not
     * @param inOutputInstance a <code>ModuleURN</code> value containing a {@link DataReceiver} to which to pass outputs generated by this strategy.  may be null.
     * @throws ModuleCreationException if the strategy cannot be created
     */
    private StrategyModule(ModuleURN inURN,
                           String inName,
                           Language inType,
                           File inSource,
                           Properties inParameters,
                           boolean inRouteOrdersToORS,
                           ModuleURN inOutputInstance)
        throws ModuleCreationException
    {
        super(inURN,
              false);
        name = inName;
        type = inType;
        source = inSource;
        parameters = inParameters;
        routeOrdersToORS = inRouteOrdersToORS;
        outputDestination = inOutputInstance;
        MBeanNotificationInfo notifyInfo = new MBeanNotificationInfo(new String[] { AttributeChangeNotification.ATTRIBUTE_CHANGE },
                                                                     AttributeChangeNotification.class.getName(),
                                                                     BEAN_ATTRIBUTE_CHANGED.getText());
        notificationDelegate = new NotificationBroadcasterSupport(notifyInfo);
    }
    /**
     * Sends the given event to the <code>CEP</code> module specified.
     *
     * <p>There is no guarantee that the given event can be delivered if the URN does
     * not exist or is not started.
     * 
     * @param inCEPModule a <code>ModuleURN</code> value
     * @param inEvent an <code>EventBase</code> value
     * @throws ModuleException if the event could not be sent
     */
    private void sendEventToCEP(ModuleURN inCEPModule,
                                Event inEvent)
        throws ModuleException
    {
        assert(inCEPModule != null);
        assert(inEvent != null);
        // see if we have a connection to this module already
        DataEmitterSupport establishedConnection = internalDataFlows.get(inCEPModule);
        if(establishedConnection == null) {
            synchronized(dataFlows) {
                // no connection exists yet to the CEP module.  create one.
                dataFlows.put(dataFlowSupport.createDataFlow(new DataRequest[] { new DataRequest(getURN(),
                                                                                                 new InternalRequest(inCEPModule)),
                                                                                 new DataRequest(inCEPModule) },
                                                             false),
                              counter.incrementAndGet());
                establishedConnection = internalDataFlows.get(inCEPModule);
                if(establishedConnection == null) {
                    StrategyModule.log(LogEventBuilder.warn().withMessage(CANNOT_CREATE_CONNECTION,
                                                                          String.valueOf(strategy),
                                                                          inCEPModule).create(),
                                       strategy);
                    return;
                }
            }
        }
        assert(establishedConnection != null);
        establishedConnection.send(inEvent);
    }
    /**
     * Confirms that the object attributes are in the state they are expected to be in at the beginning of {@link #preStart()}.
     */
    private void assertStateForPreStart()
    {
        assert(dataFlowSupport != null);
        assert(name != null);
        assert(!name.isEmpty());
        assert(type != null);
        assert(source != null);
        assert(source.exists());
        assert(source.canRead());
    }
    /**
     * Confirms that the object attributes are in the state they are expected to be in at the beginning of {@link #receiveData(DataFlowID, Object)}.
     */
    private void assertStateForReceiveData()
    {
        assertStateForPreStart();
        assert(strategy != null);
    }
    /**
     * Unsubscribes the given requester from any subscribed flows.
     *
     * @param inRequestID
     */
    private void unsubscribe(RequestID inRequestID)
    {
        synchronized(subscribers) {
            DataRequester requester = subscribers.remove(inRequestID);
            if(requester != null) {
                requester.unsubscribe();
            }
        }
    }
    /**
     * Subscribes the given data requester to the data flow indicated by the request type.
     *
     * @param inRequest an <code>OutputType</code> value
     * @param inSupport a <code>DataEmitterSupport</code> value
     */
    private void subscribe(OutputType inRequest,
                           DataEmitterSupport inSupport)
    {
        synchronized(subscribers) {
            DataRequester requester = new DataRequester(inSupport,
                                                        inRequest);
            requester.subscribe();
            subscribers.put(inSupport.getRequestID(),
                            requester);
        }
    }
    /**
     * Publishes the given <code>Object</code> to the appropriate subscribers.
     *
     * @param inObject an <code>Object</code> value
     */
    private void publish(Object inObject)
    {
        assert(inObject != null);
        if(inObject instanceof FIXOrder ||
           inObject instanceof OrderSingle ||
           inObject instanceof OrderCancel ||
           inObject instanceof OrderReplace) {
            ThreadedMetric.event("strategy-OUT");  //$NON-NLS-1$
            ordersPublisher.publish(inObject);
        } else if(inObject instanceof Suggestion) {
            suggestionsPublisher.publish(inObject);
        } else if(inObject instanceof Event) {
            eventsPublisher.publish(inObject);
        } else if(inObject instanceof Notification) {
            notificationsPublisher.publish(inObject);
        } else if(inObject instanceof String) {
            logPublisher.publish(inObject);
        }
        allPublisher.publish(inObject);
    }
    /**
     * Cancels the given request whether it's a market data request, a cep request,
     * or both.
     *
     * @param inRequest an <code>int</code> value containing a request handle
     * @throws ModuleException if an error occurs
     */
    private void doCancelDataRequest(int inRequest)
        throws ModuleException
    {
        DataFlowID dataFlowID = dataFlows.inverse().remove(inRequest);
        if(dataFlowID != null) {
            dataFlowSupport.cancel(dataFlowID);
        }
    }
    /**
     * Disconnects the strategy from the ORS, if necessary.
     * 
     * If the strategy is not currently connected to the ORS, this method does nothing
     *
     * @throws ModuleException if the data flow cannot be disconnected
     */
    private void disconnectORSRouting()
        throws ModuleException
    {
        SLF4JLoggerProxy.debug(this,
                               "Breaking connection to ORS"); //$NON-NLS-1$
        synchronized(dataFlows) {
            if(orsFlow != null) {
                try {
                    dataFlowSupport.cancel(orsFlow);
                } catch (Exception e) {
                    SLF4JLoggerProxy.debug(StrategyModule.class,
                                           e,
                                           "Unable to cancel dataflow {} - continuing", //$NON-NLS-1$
                                           orsFlow);
                }
                dataFlows.remove(orsFlow);
                orsFlow = null;
            }
        }
    }
    /**
     * Connects the strategy to the ORS, if necessary. 
     *
     * If the strategy is already connected to the ORS, this method does nothing
     * 
     * @throws ModuleException if the data flow cannot be established
     */
    private void establishORSRouting()
        throws ModuleException
    {
        SLF4JLoggerProxy.debug(this,
                               "Establishing connection to ORS"); //$NON-NLS-1$
        synchronized(dataFlows) {
            if(orsFlow == null) {
                // no current routing, establish one
                orsFlow = dataFlowSupport.createDataFlow(new DataRequest[] { new DataRequest(getURN(),
                                                                                             OutputType.ORDERS),
                                                                             new DataRequest(ClientModuleFactory.INSTANCE_URN) },
                                                         false);
                dataFlows.put(orsFlow,
                              counter.incrementAndGet());
            }
        }
    }
    /**
     * the name of the strategy being run - this name is chosen by the module caller and has no mandatory correlation 
     * to the contents of the strategy
     */
    private final String name;
    /**
     * the language type of the strategy being run - this is asserted by the module caller
     */
    private final Language type;
    /**
     * the contents of the strategy - contains the code to be executed
     */
    private final File source;
    /**
     * indicates if orders should be routed to the ORS client or not
     */
    private boolean routeOrdersToORS;
    /**
     * the parameters to present to the strategy, may be empty or null.  may be null or empty.
     */
    private Properties parameters;
    /**
     * the instanceURN of a destination for outputs, may be null.  if non-null, the object contract is to plumb a route from this object to the instance contained herein for all outputs before start.
     */
    private ModuleURN outputDestination;
    /**
     * the publishing engine for orders
     */
    private final PublisherEngine ordersPublisher = new PublisherEngine(true);
    /**
     * the publishing engine for suggestions
     */
    private final PublisherEngine suggestionsPublisher = new PublisherEngine(true);
    /**
     * the publishing engine for events
     */
    private final PublisherEngine eventsPublisher = new PublisherEngine(true);
    /**
     * the publishing engine for notification objects
     */
    private final PublisherEngine notificationsPublisher = new PublisherEngine(true);
    /**
     * the publishing engine for log output
     */
    private final PublisherEngine logPublisher = new PublisherEngine(true);
    /**
     * the publishing engine for all objects
     */
    private final PublisherEngine allPublisher = new PublisherEngine(true);
    /**
     * tracks subscriber objects by requestIDs
     */
    private final Map<RequestID,DataRequester> subscribers = new HashMap<RequestID,DataRequester>();
    /**
     * the strategy object that represents the actual running strategy
     */
    private StrategyImpl strategy;
    /**
     * services for data flow creation
     */
    private DataFlowSupport dataFlowSupport;
    /**
     * the data flow ID of the route to the ORS, if extant
     */
    private DataFlowID orsFlow;
    /**
     * default method for connecting to the client
     */
    static volatile ClientFactory clientFactory = new ClientFactory() {
        @Override
        public Client getClient()
                throws ClientInitException
        {
            return ClientManager.getInstance();
        }
    };
    /**
     * counter used to guarantee unique identifiers
     */
    private static final AtomicInteger counter = new AtomicInteger();
    /**
     * active data requests for this strategy 
     */
    private final BiMap<DataFlowID,Integer> dataFlows = HashBiMap.create();
    /**
     * the collection of data flows created to send data to a specific URN
     */
    private final Map<ModuleURN,DataEmitterSupport> internalDataFlows = new HashMap<ModuleURN,DataEmitterSupport>();
    /**
     * provides JMX notification support 
     */
    private final NotificationBroadcasterSupport notificationDelegate;
    /**
     * counter used for JMX notifications
     */
    private final AtomicLong jmxNotificationCounter = new AtomicLong();
    /**
     * Request for data that comes from within strategy to strategy.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 1.0.0
     */
    @ClassVersion("$Id$")
    private static class InternalRequest
    {
        /**
         * the URN of the module that made the original request for data 
         */
        private final ModuleURN originalRequester;
        /**
         * Create a new OneShotRequest instance.
         *
         * @param inOriginalRequester
         */
        private InternalRequest(ModuleURN inOriginalRequester)
        {
            originalRequester = inOriginalRequester;
        }
    }
    /**
     * Represents a request for a subscription to data this strategy can emit.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 1.0.0
     */
    @ClassVersion("$Id$")
    private class DataRequester
        implements ISubscriber
    {
        /**
         * the subscriber
         */
        private final DataEmitterSupport emitterSupport;
        /**
         * the type of data request
         */
        private final OutputType requestType;
        /**
         * Create a new DataRequester instance.
         *
         * @param inEmitterSupport a <code>DataEmitterSupport</code> value
         * @param inRequestType an <code>OutputType</code> value
         */
        private DataRequester(DataEmitterSupport inEmitterSupport,
                              OutputType inRequestType)
        {
            assert(inEmitterSupport != null);
            assert(inEmitterSupport.getRequestID() != null);
            emitterSupport = inEmitterSupport;
            requestType = inRequestType;
        }
        /**
         * Subscribes to the appropriate publisher.
         */
        private void subscribe()
        {
            if(requestType.equals(OutputType.ORDERS)) {
                ordersPublisher.subscribe(this);
            } else if(requestType.equals(OutputType.SUGGESTIONS)) {
                suggestionsPublisher.subscribe(this);
            } else if(requestType.equals(OutputType.EVENTS)) {
                eventsPublisher.subscribe(this);
            } else if(requestType.equals(OutputType.NOTIFICATIONS)) {
                notificationsPublisher.subscribe(this);
            } else if(requestType.equals(OutputType.LOG)) {
                logPublisher.subscribe(this);
            } else if(requestType.equals(OutputType.ALL)) {
                allPublisher.subscribe(this);
            } else {
                throw new IllegalArgumentException(); // this is a development-time exception to indicate the logic needs to be expanded because of a new request type
            }
        }
        /* (non-Javadoc)
         * @see org.marketcetera.core.publisher.ISubscriber#isInteresting(java.lang.Object)
         */
        @Override
        public boolean isInteresting(Object inData)
        {
            return true;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.core.publisher.ISubscriber#publishTo(java.lang.Object)
         */
        @Override
        public void publishTo(Object inData)
        {
            emitterSupport.send(inData);
        }
        /**
         * Unsubscribes from the appropriate publisher.
         */
        private void unsubscribe()
        {
            if(requestType.equals(OutputType.ORDERS)) {
                ordersPublisher.unsubscribe(this);
            } else if(requestType.equals(OutputType.SUGGESTIONS)) {
                suggestionsPublisher.unsubscribe(this);
            } else if(requestType.equals(OutputType.EVENTS)) {
                eventsPublisher.unsubscribe(this);
            } else if(requestType.equals(OutputType.NOTIFICATIONS)) {
                notificationsPublisher.unsubscribe(this);
            } else if(requestType.equals(OutputType.LOG)) {
                logPublisher.unsubscribe(this);
            } else if(requestType.equals(OutputType.ALL)) {
                allPublisher.unsubscribe(this);
            } else {
                throw new IllegalArgumentException(); // this is a development-time exception to indicate the logic needs to be expanded because of a new request type
            }
        }
    }
    /**
     * Constructs a <code>Client</code> connection.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 2.1.0
     */
    @ClassVersion("$Id$")
    static interface ClientFactory
    {
        /**
         * Returns the <code>Client</code> instance to use to connect to the server. 
         *
         * @return a <code>Client</code> value
         * @throws ClientInitException if the client could not be created
         */
        Client getClient()
                throws ClientInitException;
    }
}
