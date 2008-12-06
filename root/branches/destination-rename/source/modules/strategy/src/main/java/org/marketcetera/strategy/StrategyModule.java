package org.marketcetera.strategy;

import static org.marketcetera.strategy.Messages.CANNOT_CREATE_CONNECTION;
import static org.marketcetera.strategy.Messages.CANNOT_INITIALIZE_CLIENT;
import static org.marketcetera.strategy.Messages.CANNOT_SEND_EVENT_TO_CEP;
import static org.marketcetera.strategy.Messages.CEP_REQUEST_FAILED;
import static org.marketcetera.strategy.Messages.COMBINED_DATA_REQUEST_FAILED;
import static org.marketcetera.strategy.Messages.EMPTY_INSTANCE_ERROR;
import static org.marketcetera.strategy.Messages.EMPTY_NAME_ERROR;
import static org.marketcetera.strategy.Messages.EXECUTION_REPORT_REQUEST_FAILED;
import static org.marketcetera.strategy.Messages.FAILED_TO_START;
import static org.marketcetera.strategy.Messages.FILE_DOES_NOT_EXIST_OR_IS_NOT_READABLE;
import static org.marketcetera.strategy.Messages.INVALID_CEP_REQUEST;
import static org.marketcetera.strategy.Messages.INVALID_COMBINED_DATA_REQUEST;
import static org.marketcetera.strategy.Messages.INVALID_EVENT;
import static org.marketcetera.strategy.Messages.INVALID_LANGUAGE_ERROR;
import static org.marketcetera.strategy.Messages.INVALID_MARKET_DATA_REQUEST;
import static org.marketcetera.strategy.Messages.INVALID_MESSAGE;
import static org.marketcetera.strategy.Messages.INVALID_ORDER;
import static org.marketcetera.strategy.Messages.INVALID_TRADE_SUGGESTION;
import static org.marketcetera.strategy.Messages.MARKET_DATA_REQUEST_FAILED;
import static org.marketcetera.strategy.Messages.NO_CEP_HANDLE;
import static org.marketcetera.strategy.Messages.NO_MARKET_DATA_HANDLE;
import static org.marketcetera.strategy.Messages.NULL_PARAMETER_ERROR;
import static org.marketcetera.strategy.Messages.PARAMETER_COUNT_ERROR;
import static org.marketcetera.strategy.Messages.PARAMETER_TYPE_ERROR;
import static org.marketcetera.strategy.Messages.SEND_MESSAGE_FAILED;
import static org.marketcetera.strategy.Messages.STOP_ERROR;
import static org.marketcetera.strategy.Messages.UNABLE_TO_CANCEL_CEP_REQUEST;
import static org.marketcetera.strategy.Messages.UNABLE_TO_CANCEL_MARKET_DATA_REQUEST;

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

import org.marketcetera.client.Client;
import org.marketcetera.client.ClientManager;
import org.marketcetera.client.ClientModuleFactory;
import org.marketcetera.client.ConnectionException;
import org.marketcetera.client.dest.DestinationStatus;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.MSymbol;
import org.marketcetera.core.Util;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.core.publisher.PublisherEngine;
import org.marketcetera.event.EventBase;
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
import org.marketcetera.module.ModuleURN;
import org.marketcetera.module.RequestID;
import org.marketcetera.module.StopDataFlowException;
import org.marketcetera.module.UnsupportedDataTypeException;
import org.marketcetera.module.UnsupportedRequestParameterType;
import org.marketcetera.trade.DestinationID;
import org.marketcetera.trade.FIXOrder;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.OrderCancel;
import org.marketcetera.trade.OrderReplace;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.trade.Suggestion;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.I18NBoundMessage2P;
import org.marketcetera.util.log.I18NBoundMessage3P;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import quickfix.Message;

/* $License$ */

/**
 * Strategy Agent implementation for the <code>Strategy</code> module.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public final class StrategyModule
        extends Module
        implements DataEmitter, DataFlowRequester, DataReceiver, OutboundServicesProvider, StrategyMXBean, InboundServicesProvider
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
        SLF4JLoggerProxy.trace(Strategy.STRATEGY_MESSAGES,
                               "{} received {}", //$NON-NLS-1$
                               strategy,
                               inData);
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
    public int requestMarketData(MarketDataRequest inRequest,
                                 String inSource)
    {
        if(inRequest == null ||
           inSource == null ||
           inSource.isEmpty()) {
            INVALID_MARKET_DATA_REQUEST.warn(Strategy.STRATEGY_MESSAGES,
                                             strategy,
                                             inRequest,
                                             inSource);
            return 0;
        }
        int requestID = counter.incrementAndGet();
        try {
            DataFlowID dataFlowID = dataFlowSupport.createDataFlow(new DataRequest[] { new DataRequest(constructMarketDataUrn(inSource),
                                                                                                       inRequest),
                                                                                       new DataRequest(getURN()) },
                                                                   false);
            synchronized(marketDataRequests) {
                marketDataRequests.put(requestID,
                                       dataFlowID);
            }
        } catch (Exception e) {
            MARKET_DATA_REQUEST_FAILED.warn(Strategy.STRATEGY_MESSAGES,
                                            e,
                                            inRequest,
                                            inSource);
            return 0;
        }
        return requestID;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.OutboundServicesProvider#requestMarketDataWithCEP(org.marketcetera.marketdata.MarketDataRequest, java.lang.String, java.lang.String[], java.lang.String, java.lang.String)
     */
    @Override
    public int requestProcessedMarketData(MarketDataRequest inRequest,
                                          String inMarketDataSource,
                                          String[] inStatements,
                                          String inCEPSource,
                                          String inNamespace)
    {
        if(inRequest == null ||
           inMarketDataSource == null ||
           inMarketDataSource.isEmpty() ||
           inStatements == null ||
           inStatements.length == 0 ||
           inCEPSource == null ||
           inCEPSource.isEmpty() ||
           inNamespace == null ||
           inNamespace.isEmpty()) {
            INVALID_COMBINED_DATA_REQUEST.warn(Strategy.STRATEGY_MESSAGES,
                                               strategy,
                                               inRequest,
                                               inMarketDataSource,
                                               Arrays.toString(inStatements),
                                               inCEPSource,
                                               inNamespace);
            return 0;
        }
        int requestID = counter.incrementAndGet();
        // construct a request that connects the provider to the cep query
        try {
            DataFlowID dataFlowID = dataFlowSupport.createDataFlow(new DataRequest[] { new DataRequest(constructMarketDataUrn(inMarketDataSource),
                                                                                                       inRequest),
                                                                                       new DataRequest(constructCepUrn(inCEPSource,
                                                                                                                       inNamespace),
                                                                                                       determineCepStatements(inCEPSource,
                                                                                                                              inStatements)),
                                                                                       new DataRequest(getURN()) },
                                                                   false);
            // add request to both counters
            synchronized(marketDataRequests) {
                marketDataRequests.put(requestID,
                                       dataFlowID);
            }
            synchronized(cepRequests) {
                cepRequests.put(requestID,
                                dataFlowID);
            }
            return requestID;
        } catch (Exception e) {
            COMBINED_DATA_REQUEST_FAILED.warn(Strategy.STRATEGY_MESSAGES,
                                              e,
                                              inRequest,
                                              inMarketDataSource,
                                              Arrays.toString(inStatements),
                                              inCEPSource,
                                              inNamespace);
            return 0;
        }        
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.OutboundServices#cancelAllMarketDataRequests()
     */
    @Override
    public void cancelAllMarketDataRequests()
    {
        synchronized(marketDataRequests) {
            // create a copy of the list because the cancel call is going to modify the collection
            List<Integer> activeRequests = new ArrayList<Integer>(marketDataRequests.keySet());
            for(int request : activeRequests) {
                try {
                    cancelDataRequest(request);
                } catch (Exception e) {
                    UNABLE_TO_CANCEL_MARKET_DATA_REQUEST.warn(Strategy.STRATEGY_MESSAGES,
                                                              e,
                                                              strategy,
                                                              request);
                }
            }
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.OutboundServices#cancelMarketDataRequest(int)
     */
    @Override
    public void cancelMarketDataRequest(int inDataRequestID)
    {
        synchronized(marketDataRequests) {
            if(!marketDataRequests.containsKey(inDataRequestID)) {
                NO_MARKET_DATA_HANDLE.warn(Strategy.STRATEGY_MESSAGES,
                                           strategy,
                                           inDataRequestID);
                return;
            }
            try {
                cancelDataRequest(inDataRequestID);
            } catch (Exception e) {
                UNABLE_TO_CANCEL_MARKET_DATA_REQUEST.warn(Strategy.STRATEGY_MESSAGES,
                                                          e,
                                                          strategy,
                                                          inDataRequestID);
            }
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.OutboundServicesProvider#cancelAllCEPRequests()
     */
    @Override
    public void cancelAllCEPRequests()
    {
        synchronized(cepRequests) {
            // create a copy of the list because the cancel call is going to modify the collection
            List<Integer> activeRequests = new ArrayList<Integer>(cepRequests.keySet());
            for(int request : activeRequests) {
                try {
                    cancelDataRequest(request);
                } catch (Exception e) {
                    UNABLE_TO_CANCEL_CEP_REQUEST.warn(Strategy.STRATEGY_MESSAGES,
                                                      e,
                                                      strategy,
                                                      request);
                }
            }
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.OutboundServicesProvider#cancelCEPRequest(int)
     */
    @Override
    public void cancelCEPRequest(int inDataRequestID)
    {
        synchronized(cepRequests) {
            if(!cepRequests.containsKey(inDataRequestID)) {
                NO_CEP_HANDLE.warn(Strategy.STRATEGY_MESSAGES,
                                   strategy,
                                   inDataRequestID);
                return;
            }
            try {
                cancelDataRequest(inDataRequestID);
            } catch (Exception e) {
                UNABLE_TO_CANCEL_CEP_REQUEST.warn(Strategy.STRATEGY_MESSAGES,
                                                  e,
                                                  strategy,
                                                  inDataRequestID);
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
            INVALID_CEP_REQUEST.warn(Strategy.STRATEGY_MESSAGES,
                                     strategy,
                                     Arrays.toString(inStatements),
                                     String.valueOf(inSource) + ":" + String.valueOf(inNamespace)); //$NON-NLS-1$
            return 0;
        }
        int requestID = counter.incrementAndGet();
        ModuleURN providerURN = constructCepUrn(inSource,
                                                inNamespace);
        try {
            synchronized(cepRequests) {
                cepRequests.put(requestID,
                                dataFlowSupport.createDataFlow(new DataRequest[] { new DataRequest(providerURN,
                                                                                                   determineCepStatements(inSource,
                                                                                                                          inStatements)),
                                                                                   new DataRequest(getURN()) },
                                                               false));
            }
        } catch (Exception e) {
            CEP_REQUEST_FAILED.warn(Strategy.STRATEGY_MESSAGES,
                                    e,
                                    Arrays.toString(inStatements),
                                    inSource);
            return 0;
        }
        return requestID;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.StrategyMXBean#setOrdersDestination(java.lang.String)
     */
    @Override
    public void setOrdersDestination(String inDestination)
    {
        if(inDestination == null ||
           inDestination.isEmpty()) {
            ordersDestination = null;
            SLF4JLoggerProxy.debug(StrategyModule.class,
                                   "Setting orders destination to null"); //$NON-NLS-1$
            return;
        }
        ordersDestination = new ModuleURN(inDestination);
        SLF4JLoggerProxy.debug(StrategyModule.class,
                               "Setting orders destination to {}", //$NON-NLS-1$
                               ordersDestination);
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
     * @see org.marketcetera.strategy.StrategyMXBean#setSuggestionsDestination(java.lang.String)
     */
    @Override
    public void setSuggestionsDestination(String inDestination)
    {
        if(inDestination == null ||
           inDestination.isEmpty()) {
            suggestionsDestination = null;
            SLF4JLoggerProxy.debug(StrategyModule.class,
                                   "Setting suggestions destination to null"); //$NON-NLS-1$
            return;
        }
        suggestionsDestination = new ModuleURN(inDestination);
        SLF4JLoggerProxy.debug(StrategyModule.class,
                               "Setting suggestions destination to {}", //$NON-NLS-1$
                               suggestionsDestination);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.StrategyMXBean#getOrdersDestination()
     */
    @Override
    public String getOrdersDestination()
    {
        if(ordersDestination == null) {
            return null;
        }
        return ordersDestination.getValue();
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
     * @see org.marketcetera.strategy.StrategyMXBean#getSuggestionsDestination()
     */
    @Override
    public String getSuggestionsDestination()
    {
        if(suggestionsDestination == null) {
            return null;
        }
        return suggestionsDestination.getValue();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.OutboundServicesProvider#setMessage(quickfix.Message)
     */
    @Override
    public void sendMessage(Message inMessage,
                            DestinationID inDestination)
    {
        if(inMessage == null ||
           inDestination == null) {
            INVALID_MESSAGE.warn(Strategy.STRATEGY_MESSAGES,
                                 strategy);
            return;
        }
        try {
            publish(Factory.getInstance().createOrder(inMessage,
                                                      inDestination));
        } catch (Exception e) {
            SEND_MESSAGE_FAILED.warn(Strategy.STRATEGY_MESSAGES,
                                     e,
                                     strategy,
                                     inMessage,
                                     inDestination);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.OutboundServices#sendOrder(java.lang.Object)
     */
    @Override
    public void sendOrder(OrderSingle inOrder)
    {
        if(inOrder == null) {
            INVALID_ORDER.warn(Strategy.STRATEGY_MESSAGES,
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
            INVALID_TRADE_SUGGESTION.warn(Strategy.STRATEGY_MESSAGES,
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
            INVALID_EVENT.warn(Strategy.STRATEGY_MESSAGES,
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
                CANNOT_SEND_EVENT_TO_CEP.warn(Strategy.STRATEGY_MESSAGES,
                                              e,
                                              strategy,
                                              inEvent,
                                              cepModuleURN);
            }
            // done sending event to CEP, for better or worse
        }
        // send to subscribers
        publish(inEvent);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.InboundServicesProvider#getDestinations()
     */
    @Override
    public List<DestinationStatus> getDestinations()
        throws ConnectionException
    {
        assert(orsClient != null);
        return orsClient.getDestinationsStatus().getDestinations();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.InboundServicesProvider#getPositionAsOf(java.util.Date, org.marketcetera.core.MSymbol)
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
        // must have 8 parameters, though the first and the last four may be null
        if(inParameters == null ||
           inParameters.length != 8) {
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
        // parameter 6 is the classpath.  This parameter may be null.  If non-null, it contains an array of Strings which are passed to the
        //  script executor for use in a language-dependent fashion.
        String[] classpath = null;
        if(inParameters[5] != null) {
            if(inParameters[5] instanceof String[]) {
                classpath = (String[])inParameters[5];
            } else {
                throw new ModuleCreationException(new I18NBoundMessage3P(PARAMETER_TYPE_ERROR,
                                                                         6,
                                                                         String[].class.getName(),
                                                                         inParameters[5].getClass().getName()));
            }
        }
        // parameter 7 is a ModuleURN.  This parameter may be null.  If non-null, it must describe a module instance that is started and able
        //  to receive data.  Orders will be sent to this module when they are created.
        ModuleURN ordersInstance = null;
        if(inParameters[6] != null) {
            if(inParameters[6] instanceof ModuleURN) {
                ordersInstance = (ModuleURN)inParameters[6];
            } else {
                throw new ModuleCreationException(new I18NBoundMessage3P(PARAMETER_TYPE_ERROR,
                                                                         7,
                                                                         ModuleURN.class.getName(),
                                                                         inParameters[6].getClass().getName()));
            }
        }
        // parameter 8 is a ModuleURN.  This parameter may be null.  If non-null, it must describe a module instance that is started and able
        //  to receive data.  Trade suggestions will be sent to this module when they are created.
        ModuleURN suggestionsInstance = null;
        if(inParameters[7] != null) {
            if(inParameters[7] instanceof ModuleURN) {
                suggestionsInstance = (ModuleURN)inParameters[7];
            } else {
                throw new ModuleCreationException(new I18NBoundMessage3P(PARAMETER_TYPE_ERROR,
                                                                         8,
                                                                         ModuleURN.class.getName(),
                                                                         inParameters[7].getClass().getName()));
            }
        }
        return new StrategyModule(instanceName == null ? generateInstanceURN(name) : new ModuleURN(StrategyModuleFactory.PROVIDER_URN,
                                                                                                   instanceName),
                                  name,
                                  type,
                                  source,
                                  parameters,
                                  classpath,
                                  ordersInstance,
                                  suggestionsInstance);
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
            if(ordersDestination != null) {
                dataFlows.add(dataFlowSupport.createDataFlow(new DataRequest[] { new DataRequest(getURN(),
                                                                                                 OutputType.ORDERS),
                                                                                 new DataRequest(ordersDestination) },
                                                             false));
            }
            if(suggestionsDestination != null) {
                dataFlows.add(dataFlowSupport.createDataFlow(new DataRequest[] { new DataRequest(getURN(),
                                                                                                 OutputType.SUGGESTIONS),
                                                                                 new DataRequest(suggestionsDestination) },
                                                             false));
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
                                        classpath,
                                        getURN().instanceName(),
                                        this,
                                        this);
            // make sure that an endless loop does not derail the module start process
            // note that this implies it is the strategy author's responsibility to manage concurrency in the case
            //  of a strategy with a long preStart, i.e. messages could start coming in before the preStart loop completes
            strategy.start();
//            // TODO for now, because of significant issues this pattern raises, synchronously wait for the start loop to end
//            //  this should be resolved before release
//            executor.submit(new Callable<Strategy>() {
//                @Override
//                public Strategy call()
//                        throws Exception
//                {
//                    strategy.start();
//                    return strategy;
//                }
//            }).get();
        } catch (Exception e) {
            throw new ModuleException(e,
                                      FAILED_TO_START);
        }
    }
//    private static final ExecutorService executor = Executors.newCachedThreadPool();
    /* (non-Javadoc)
     * @see org.marketcetera.module.Module#preStop()
     */
    @Override
    protected void preStop()
            throws ModuleException
    {
        cancelAllMarketDataRequests();
        cancelAllCEPRequests();
        // TODO for now, because of significant issues this pattern raises, synchronously wait for the start loop to end
        //  this should be resolved before release
        try {
            strategy.stop();
//            // make sure that an endless loop does not derail the module stop process
//            executor.submit(new Callable<Strategy>() {
//                @Override
//                public Strategy call()
//                        throws Exception
//                {
//                    strategy.stop();
//                    return strategy;
//                }
//            }).get();
        } catch (Exception e) {
            // a strategy may not prevent itself from being stopped
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
     * 
     *
     *
     * @param inSource
     * @param inStatements
     * @return
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
     * 
     *
     *
     * @param inSource
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
     * @param inOrdersInstance a <code>ModuleURN</code> value containing a {@link DataReceiver} to which to pass orders generated by this strategy.  may be null.
     * @param inSuggestionsInstance a <code>ModuleURN</code> value containing a {@link DataReceiver} to which to pass suggestions generated by this strategy.  may be null.
     * @throws ModuleCreationException if the strategy cannot be created
     */
    private StrategyModule(ModuleURN inURN,
                           String inName,
                           Language inType,
                           File inSource,
                           Properties inParameters,
                           String[] inClasspath,
                           ModuleURN inOrdersInstance,
                           ModuleURN inSuggestionsInstance)
        throws ModuleCreationException
    {
        super(inURN,
              false);
        name = inName;
        type = inType;
        source = inSource;
        parameters = inParameters;
        classpath = inClasspath;
        ordersDestination = inOrdersInstance;
        suggestionsDestination = inSuggestionsInstance;
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
                CANNOT_CREATE_CONNECTION.warn(Strategy.STRATEGY_MESSAGES,
                                              strategy,
                                              inCEPModule);
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
        SLF4JLoggerProxy.debug(Strategy.STRATEGY_MESSAGES,
                               "{} publishing {}", //$NON-NLS-1$
                               strategy,
                               inObject);
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
    private void cancelDataRequest(int inRequest)
        throws ModuleException
    {
        DataFlowID dataFlowID = marketDataRequests.remove(inRequest);
        if(dataFlowID != null) {
            dataFlowSupport.cancel(dataFlowID);
        }
        dataFlowID = cepRequests.remove(inRequest);
        if(dataFlowID != null) {
            dataFlowSupport.cancel(dataFlowID);
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
     * the parameters to present to the strategy, may be empty or null.  may be null or empty.
     */
    private Properties parameters;
    /**
     * the classpath for the script executor to use in a language-dependent fashion
     */
    private final String[] classpath;
    /**
     * the instanceURN of a destination for Orders, may be null.  if non-null, the object contract is to plumb a route from this object to the instance contained herein for orders before start.
     */
    private ModuleURN ordersDestination;
    /**
     * the instanceURN of a destination for Suggestions, may be null.  if non-null, the object contract is to plumb a route from this object to the instance contained herein for suggestions before start.
     */
    private ModuleURN suggestionsDestination;
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
    private Strategy strategy;
    /**
     * services for data flow creation
     */
    private DataFlowSupport dataFlowSupport;
    /**
     * client to use for services
     */
    static Client orsClient; // this is non-final and non-private in order to provide a loophole for testing
    /**
     * counter used to guarantee unique identifiers
     */
    private static final AtomicInteger counter = new AtomicInteger();
    /**
     * active market data requests for this strategy 
     */
    private final Map<Integer,DataFlowID> marketDataRequests = new HashMap<Integer,DataFlowID>();
    /**
     * active cep requests for this strategy 
     */
    private final Map<Integer,DataFlowID> cepRequests = new HashMap<Integer,DataFlowID>();
    /**
     * the list of dataflows started during the lifetime of this strategy
     */
    private final List<DataFlowID> dataFlows = new ArrayList<DataFlowID>();
    /**
     * the collection of data flows created to send data to a specific URN
     */
    private final Map<ModuleURN,DataEmitterSupport> internalDataFlows = new HashMap<ModuleURN,DataEmitterSupport>();
    /**
     * Request for data that comes from within strategy to strategy.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
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
     * @since $Release$
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
            } else if(requestType.equals(OutputType.ALL)) {
                allPublisher.unsubscribe(this);
            } else {
                throw new IllegalArgumentException(); // this is a development-time exception to indicate the logic needs to be expanded because of a new request type
            }
        }
    }
}
