package org.marketcetera.strategy;

import static org.marketcetera.strategy.Status.UNSTARTED;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.management.AttributeChangeNotification;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanNotificationInfo;
import javax.management.NotificationBroadcasterSupport;
import javax.management.NotificationEmitter;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;

import org.marketcetera.client.Client;
import org.marketcetera.client.ClientManager;
import org.marketcetera.client.ClientModuleFactory;
import org.marketcetera.client.ConnectionException;
import org.marketcetera.client.brokers.BrokerStatus;
import org.marketcetera.core.Util;
import org.marketcetera.core.notifications.Notification;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.core.publisher.PublisherEngine;
import org.marketcetera.event.EventBase;
import org.marketcetera.event.LogEvent;
import org.marketcetera.event.LogEvent.Level;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.module.DataEmitter;
import org.marketcetera.module.DataEmitterSupport;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.DataFlowRequester;
import org.marketcetera.module.DataFlowSupport;
import org.marketcetera.module.DataReceiver;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.IllegalRequestParameterValue;
import org.marketcetera.module.Module;
import org.marketcetera.module.ModuleCreationException;
import org.marketcetera.module.ModuleException;
import org.marketcetera.module.ModuleStateException;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.module.RequestID;
import org.marketcetera.module.StopDataFlowException;
import org.marketcetera.module.UnsupportedDataTypeException;
import org.marketcetera.module.UnsupportedRequestParameterType;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.FIXOrder;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.MSymbol;
import org.marketcetera.trade.OrderCancel;
import org.marketcetera.trade.OrderReplace;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.trade.Suggestion;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.I18NBoundMessage2P;
import org.marketcetera.util.log.I18NBoundMessage3P;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

import quickfix.Message;

/* $License$ */

/**
 * Strategy Agent implementation for the <code>Strategy</code> module.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
final class StrategyModule
        extends Module
        implements DataEmitter, DataFlowRequester, DataReceiver, OutboundServicesProvider, StrategyMXBean, InboundServicesProvider, NotificationEmitter, Messages
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
        assertStateForReceiveData();
        SLF4JLoggerProxy.trace(StrategyModule.class,
                               "{} received {}", //$NON-NLS-1$
                               strategy,
                               inData);
        if(inData instanceof EventBase) {
            EventBase event = (EventBase)inData;
            synchronized(dataFlowsByRequest) {
                event.setSource(requestsByDataFlow.get(inFlowID));
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
            StrategyModule.log(LogEvent.warn(INVALID_MARKET_DATA_REQUEST,
                                             String.valueOf(strategy),
                                             inRequest),
                               strategy);
            return 0;
        }
        int requestID = counter.incrementAndGet();
        try {
            DataFlowID dataFlowID = dataFlowSupport.createDataFlow(new DataRequest[] { new DataRequest(constructMarketDataUrn(inRequest.getProvider()),
                                                                                                       inRequest),
                                                                                       new DataRequest(getURN()) },
                                                                   false);
            synchronized(dataFlowsByRequest) {
                dataFlowsByRequest.put(requestID,
                                       dataFlowID);
                requestsByDataFlow.put(dataFlowID,
                                       requestID);
            }
        } catch (Exception e) {
            StrategyModule.log(LogEvent.warn(MARKET_DATA_REQUEST_FAILED,
                                             e,
                                             inRequest),
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
            StrategyModule.log(LogEvent.warn(INVALID_COMBINED_DATA_REQUEST,
                                             String.valueOf(strategy),
                                             inRequest,
                                             Arrays.toString(inStatements),
                                             inCEPSource,
                                             inNamespace),
                               strategy);
            return 0;
        }
        int requestID = counter.incrementAndGet();
        // construct a request that connects the provider to the cep query
        try {
            DataFlowID dataFlowID = dataFlowSupport.createDataFlow(new DataRequest[] { new DataRequest(constructMarketDataUrn(inRequest.getProvider()),
                                                                                                       inRequest),
                                                                                       new DataRequest(constructCepUrn(inCEPSource,
                                                                                                                       inNamespace),
                                                                                                       determineCepStatements(inCEPSource,
                                                                                                                              inStatements)),
                                                                                       new DataRequest(getURN()) },
                                                                   false);
            // add request to both counters
            synchronized(dataFlowsByRequest) {
                dataFlowsByRequest.put(requestID,
                                       dataFlowID);
                requestsByDataFlow.put(dataFlowID,
                                       requestID);
            }
            return requestID;
        } catch (Exception e) {
            StrategyModule.log(LogEvent.warn(COMBINED_DATA_REQUEST_FAILED,
                                             e,
                                             inRequest,
                                             Arrays.toString(inStatements),
                                             inCEPSource,
                                             inNamespace),
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
        synchronized(dataFlowsByRequest) {
            // create a copy of the list because the cancel call is going to modify the collection
            List<Integer> activeRequests = new ArrayList<Integer>(dataFlowsByRequest.keySet());
            for(int request : activeRequests) {
                try {
                    cancelDataRequest(request);
                } catch (Exception e) {
                    StrategyModule.log(LogEvent.warn(UNABLE_TO_CANCEL_DATA_REQUEST,
                                                     e,
                                                     String.valueOf(strategy),
                                                     request),
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
        synchronized(dataFlowsByRequest) {
            if(!dataFlowsByRequest.containsKey(inDataRequestID)) {
                StrategyModule.log(LogEvent.warn(NO_DATA_HANDLE,
                                                 String.valueOf(strategy),
                                                 inDataRequestID),
                                   strategy);
                return;
            }
            try {
                doCancelDataRequest(inDataRequestID);
            } catch (Exception e) {
                StrategyModule.log(LogEvent.warn(UNABLE_TO_CANCEL_DATA_REQUEST,
                                                 e,
                                                 String.valueOf(strategy),
                                                 inDataRequestID),
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
            StrategyModule.log(LogEvent.warn(INVALID_CEP_REQUEST,
                                             String.valueOf(strategy),
                                             Arrays.toString(inStatements),
                                             String.valueOf(inSource),
                                             String.valueOf(inNamespace)),
                               strategy);
            return 0;
        }
        int requestID = counter.incrementAndGet();
        ModuleURN providerURN = constructCepUrn(inSource,
                                                inNamespace);
        try {
            synchronized(dataFlowsByRequest) {
                DataFlowID flowID = dataFlowSupport.createDataFlow(new DataRequest[] { new DataRequest(providerURN,
                                                                                                       determineCepStatements(inSource,
                                                                                                                              inStatements)),
                                                                                       new DataRequest(getURN()) },
                                                                   false); 
                dataFlowsByRequest.put(requestID,
                                       flowID);
                requestsByDataFlow.put(flowID,
                                       requestID);
            }
        } catch (Exception e) {
            StrategyModule.log(LogEvent.warn(CEP_REQUEST_FAILED,
                                             e,
                                             Arrays.toString(inStatements),
                                             inSource),
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
     * @see org.marketcetera.strategy.StrategyMXBean#setIsRountingOrdersToORS(boolean)
     */
    @Override
    public void setIsRountingOrdersToORS(boolean inValue)
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
            StrategyModule.log(LogEvent.warn(INVALID_MESSAGE,
                                             String.valueOf(strategy)),
                               strategy);
            return;
        }
        try {
            publish(Factory.getInstance().createOrder(inMessage,
                                                      inBroker));
        } catch (Exception e) {
            StrategyModule.log(LogEvent.warn(SEND_MESSAGE_FAILED,
                                             e,
                                             String.valueOf(strategy),
                                             inMessage,
                                             inBroker),
                               strategy);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.OutboundServices#sendOrder(java.lang.Object)
     */
    @Override
    public void sendOrder(OrderSingle inOrder)
    {
        if(inOrder == null) {
            StrategyModule.log(LogEvent.warn(INVALID_ORDER,
                                             String.valueOf(strategy)),
                               strategy);
            return;
        }
        publish(inOrder);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.OutboundServices#sendSuggestion(java.lang.Object)
     */
    @Override
    public void sendSuggestion(Suggestion inSuggestion)
    {
        if(inSuggestion == null) {
            StrategyModule.log(LogEvent.warn(INVALID_TRADE_SUGGESTION,
                                             String.valueOf(strategy)),
                               strategy);
            return;
        }
        publish(inSuggestion);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.OutboundServicesProvider#sendEvent(org.marketcetera.event.EventBase)
     */
    @Override
    public void sendEvent(EventBase inEvent,
                          String inProvider,
                          String inNamespace)
    {
        // event must not be null, but the other two parameters may be
        if(inEvent == null) {
            StrategyModule.log(LogEvent.warn(INVALID_EVENT,
                                             String.valueOf(strategy)),
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
                StrategyModule.log(LogEvent.warn(CANNOT_SEND_EVENT_TO_CEP,
                                                 String.valueOf(strategy),
                                                 inEvent,
                                                 cepModuleURN),
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
            StrategyModule.log(LogEvent.warn(INVALID_NOTIFICATION,
                                             String.valueOf(strategy)),
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
            StrategyModule.log(LogEvent.warn(INVALID_LOG,
                                             String.valueOf(strategy)),
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
        throws ConnectionException
    {
        assert(orsClient != null);
        return orsClient.getBrokersStatus().getBrokers();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.InboundServicesProvider#getPositionAsOf(java.util.Date, org.marketcetera.trade.MSymbol)
     */
    @Override
    public BigDecimal getPositionAsOf(Date inDate,
                                      MSymbol inSymbol)
        throws ConnectionException
    {
        assert(orsClient != null);
        return orsClient.getPositionAsOf(inDate,
                                         inSymbol);
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
     * @see org.marketcetera.strategy.StrategyMXBean#interrupt()
     */
    @Override
    public void interrupt()
    {
        if(strategy != null) {
            strategy.getExecutor().interrupt();
        }
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
     * @param inMessage a <code>LogEvent</code> value
     * @param inStrategy a <code>Strategy</code> value
     */
    static void log(LogEvent inEvent,
                    Strategy inStrategy)
    {
        if(shouldLog(inEvent)) {
            doLogger(inEvent);
            inStrategy.getOutboundServicesProvider().log(inEvent);
        }
    }
    /**
     * Determines if the given event should be emitted or not.
     *
     * @param inEvent a <code>LogEvent</code> value
     * @return a <code>boolean</code> value
     */
    private static boolean shouldLog(LogEvent inEvent) {
        return LogEvent.shouldLog(inEvent, Strategy.STRATEGY_MESSAGES);
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
        if(Level.DEBUG.equals(inEvent.getLevel())) {
            if(exception == null) {
                MESSAGE_1P.debug(Strategy.STRATEGY_MESSAGES,
                                 message);
            } else {
                MESSAGE_1P.debug(Strategy.STRATEGY_MESSAGES,
                                 exception,
                                 message);
            }
        } else if(Level.INFO.equals(inEvent.getLevel())) {
            if(exception == null) {
                MESSAGE_1P.info(Strategy.STRATEGY_MESSAGES,
                                message);
            } else {
                MESSAGE_1P.info(Strategy.STRATEGY_MESSAGES,
                                exception,
                                message);
            }
        } else if(Level.WARN.equals(inEvent.getLevel())) {
            if(exception == null) {
                MESSAGE_1P.warn(Strategy.STRATEGY_MESSAGES,
                                message);
            } else {
                MESSAGE_1P.warn(Strategy.STRATEGY_MESSAGES,
                                exception,
                                message);
            }
        } else if(Level.ERROR.equals(inEvent.getLevel())) {
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
        // this is a special case.  if the strategy has already been started and stopped, but the strategy is still running
        //  either its onStart or onStop loop, both of which are run asynchronously, do not allow a new strategy to start
        //  with the same URN.  the old one *must* finish first.
        if(strategy != null) {
            if(!strategy.getStatus().canChangeStatusTo(UNSTARTED)) {
                throw new ModuleStateException(new I18NBoundMessage2P(STRATEGY_STILL_RUNNING,
                                                                      strategy.toString(),
                                                                      strategy.getStatus()));
            }
        }
        assertStateForPreStart();
        // add destination data flows, if specified by the object parameters
        synchronized(dataFlows) {
            if(outputDestination != null) {
                dataFlows.add(dataFlowSupport.createDataFlow(new DataRequest[] { new DataRequest(getURN(),
                                                                                                 OutputType.ALL),
                                                                                 new DataRequest(outputDestination) },
                                                             false));
            }
            // set the connection to the ORS to the correct value
            if(routeOrdersToORS) {
                establishORSRouting();
            } else {
                disconnectORSRouting();
            }
            // request execution reports from the ORS client
            try {
                dataFlows.add(dataFlowSupport.createDataFlow(new DataRequest[] { new DataRequest(ClientModuleFactory.INSTANCE_URN),
                                                                                 new DataRequest(getURN()) },
                                                             false));
            } catch (Exception e) {
                EXECUTION_REPORT_REQUEST_FAILED.warn(StrategyModule.class,
                                                     name,
                                                     ClientModuleFactory.INSTANCE_URN);
            }
        }
        try {
            initializeClient();
            strategy = new StrategyImpl(name,
                                        getURN().getValue(),
                                        type,
                                        source,
                                        parameters,
                                        getURN().instanceName(),
                                        this,
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
        cancelAllDataRequests();
        disconnectORSRouting();
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
        synchronized(dataFlows) {
            for(DataFlowID flow : dataFlows) {
                try {
                    dataFlowSupport.cancel(flow);
                } catch (Exception e) {
                    SLF4JLoggerProxy.debug(StrategyModule.class,
                                           e,
                                           "Unable to cancel dataflow {} - continuing", //$NON-NLS-1$
                                           flow);
                }
            }
            dataFlows.clear();
        }
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
                                EventBase inEvent)
        throws ModuleException
    {
        assert(inCEPModule != null);
        assert(inEvent != null);
        // see if we have a connection to this module already
        DataEmitterSupport establishedConnection = internalDataFlows.get(inCEPModule);
        if(establishedConnection == null) {
            // no connection exists yet to the CEP module.  create one.
            dataFlows.add(dataFlowSupport.createDataFlow(new DataRequest[] { new DataRequest(getURN(),
                                                                                             new InternalRequest(inCEPModule)),
                                                                             new DataRequest(inCEPModule) },
                                                         false));
            establishedConnection = internalDataFlows.get(inCEPModule);
            if(establishedConnection == null) {
                StrategyModule.log(LogEvent.warn(CANNOT_CREATE_CONNECTION,
                                                 String.valueOf(strategy),
                                                 inCEPModule),
                                   strategy);
                return;
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
            ordersPublisher.publish(inObject);
        } else if(inObject instanceof Suggestion) {
            suggestionsPublisher.publish(inObject);
        } else if(inObject instanceof EventBase) {
            eventsPublisher.publish(inObject);
        } else if(inObject instanceof Notification) {
            notificationsPublisher.publish(inObject);
        } else if(inObject instanceof String) {
            logPublisher.publish(inObject);
        }
        allPublisher.publish(inObject);
    }
    /**
     * Prepares the <code>orsClient</code> to be used. 
     */
    private void initializeClient()
    {
        try {
            if(orsClient == null) {
                orsClient = ClientManager.getInstance();
            }
        } catch (Exception e) {
            CANNOT_INITIALIZE_CLIENT.warn(StrategyModule.class,
                                          e);
        }
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
        DataFlowID dataFlowID = dataFlowsByRequest.remove(inRequest);
        requestsByDataFlow.remove(dataFlowID);
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
                dataFlows.add(orsFlow);
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
     * client to use for services
     */
    static Client orsClient; // this is non-final and non-private in order to provide a loophole for testing
    /**
     * counter used to guarantee unique identifiers
     */
    private static final AtomicInteger counter = new AtomicInteger();
    /**
     * active data requests for this strategy 
     */
    private final Map<Integer,DataFlowID> dataFlowsByRequest = new HashMap<Integer,DataFlowID>();
    private final Map<DataFlowID,Integer> requestsByDataFlow = new HashMap<DataFlowID,Integer>();
    /**
     * the list of dataflows started during the lifetime of this strategy
     */
    private final List<DataFlowID> dataFlows = new ArrayList<DataFlowID>();
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
}
