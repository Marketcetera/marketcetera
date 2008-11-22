package org.marketcetera.strategy;

import static org.marketcetera.strategy.Messages.EMPTY_NAME_ERROR;
import static org.marketcetera.strategy.Messages.FAILED_TO_START;
import static org.marketcetera.strategy.Messages.FILE_DOES_NOT_EXIST_OR_IS_NOT_READABLE;
import static org.marketcetera.strategy.Messages.INVALID_LANGUAGE_ERROR;
import static org.marketcetera.strategy.Messages.MARKET_DATA_REQUEST_FAILED;
import static org.marketcetera.strategy.Messages.NULL_PARAMETER_ERROR;
import static org.marketcetera.strategy.Messages.PARAMETER_COUNT_ERROR;
import static org.marketcetera.strategy.Messages.PARAMETER_TYPE_ERROR;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.core.publisher.PublisherEngine;
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
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.MessageCreationException;
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
final class StrategyModule
        extends Module
        implements DataEmitter, DataFlowRequester, DataReceiver, OutboundServicesProvider, StrategyMXBean
{
    /* (non-Javadoc)
     * @see org.marketcetera.module.DataEmitter#cancel(org.marketcetera.module.RequestID)
     */
    @Override
    public void cancel(RequestID inRequestID)
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
        SLF4JLoggerProxy.debug(this,
                               "Strategy {} received {}", //$NON-NLS-1$
                               strategy,
                               inData);
        strategy.dataReceived(inData);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.OutboundServices#sendOrder(java.lang.Object)
     */
    @Override
    public void sendOrder(Object inOrder)
    {
        ordersPublisher.publish(inOrder);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.OutboundServices#sendSuggestion(java.lang.Object)
     */
    @Override
    public void sendSuggestion(Suggestion inSuggestion)
    {
        suggestionsPublisher.publish(inSuggestion);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.OutboundServices#marketDataRequest(org.marketcetera.marketdata.DataRequest, java.lang.String)
     */
    @Override
    public long requestMarketData(org.marketcetera.marketdata.DataRequest inRequest,
                                  String inSource)
    {
        long requestID = counter.incrementAndGet();
        StringBuilder providerURNAsString = new StringBuilder();
        // TODO need a cleaner way to construct this URN
        providerURNAsString.append("metc:mdata:").append(inSource); //$NON-NLS-1$
        ModuleURN providerURN = new ModuleURN(providerURNAsString.toString());
        try {
            DataFlowID dataFlowID = dataFlowSupport.createDataFlow(new DataRequest[] { new DataRequest(providerURN,
                                                                                                       inRequest),
                                                                                       new DataRequest(getURN()) },
                                                                   false);
            synchronized(marketDataRequests) {
                marketDataRequests.put(requestID,
                                       dataFlowID);
            }
        } catch (ModuleException e) {
            // warn, but do not cause the strategy to stop working
            MARKET_DATA_REQUEST_FAILED.warn(this,
                                            e,
                                            inRequest,
                                            inSource);
        }
        return requestID;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.OutboundServices#cancelAllMarketDataRequests()
     */
    @Override
    public void cancelAllMarketDataRequests()
    {
        synchronized(marketDataRequests) {
            for(DataFlowID dataFlowID : marketDataRequests.values()) {
                try {
                    doMarketDataRequestCancel(dataFlowID);
                } catch (ModuleException e) {
                    // TODO handle exception
                    e.printStackTrace();
                }
            }
            marketDataRequests.clear();
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.OutboundServices#cancelMarketDataRequest(long)
     */
    @Override
    public void cancelMarketDataRequest(long inDataRequestID)
    {
        synchronized(marketDataRequests) {
            DataFlowID dataFlowID = marketDataRequests.get(inDataRequestID);
            if(dataFlowID == null) {
                // TODO warn about null request
                return;
            }
            try {
                doMarketDataRequestCancel(dataFlowID);
            } catch (ModuleException e) {
                // TODO handle exception
                e.printStackTrace();
            }
        }
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
        parameters = propertiesFromString(inParameters);
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
        return propertiesToString(parameters);
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
        throws MessageCreationException
    {
        SLF4JLoggerProxy.debug(this,
                               "{} publishing {}", //$NON-NLS-1$
                               strategy,
                               inMessage);
        ordersPublisher.publish(Factory.getInstance().createOrder(inMessage,
                                                                  inDestination));
    }
    /**
     * Creates a <code>Properties</code> object from the given <code>String</code>.
     *
     * <p>This function assumes that the <code>String</code> consists of a series of key/value pairs separated by
     * the {@link StrategyMXBean#KEY_VALUE_DELIMITER}.  The key/value pairs themselves are separated by the {@link StrategyMXBean#KEY_VALUE_SEPARATOR}.
     * Any malformed entries are discarded.  A best-effort will be made to retain as many key/value pairs as possible.
     * 
     * @param inCondensedProperties a <code>String</code> value
     * @return a <code>Properties</code> value or null if <code>inCondensedProperties</code> is null or empty
     */
    public static final Properties propertiesFromString(String inCondensedProperties)
    {
        if(inCondensedProperties == null ||
           inCondensedProperties.isEmpty()) {
            return null;
        }
        String[] statements = inCondensedProperties.split(StrategyMXBean.KEY_VALUE_DELIMITER);
        Properties props = new Properties();
        // each statement should be "x=y" - we are going to assume this is the case
        for(String statement : statements) {
            String[] subStatements = statement.split(StrategyMXBean.KEY_VALUE_SEPARATOR);
            if(subStatements != null &&
               subStatements.length == 2) {
                props.setProperty(subStatements[0],
                                  subStatements[1]);
            } else {
                SLF4JLoggerProxy.debug(StrategyModule.class,
                                       "Putative key/value \"{}\" discarded",
                                       (subStatements == null ? "null" : Arrays.toString(subStatements)));
            }
        }
        return props;
    }
    /**
     * Creates a <code>String</code> object from the given <code>Properties</code> object. 
     *
     * <p>This function returns a <code>String</code> containing a series of key/value pairs representing this object.
     * Each key/value pair is separated by the {@link StrategyMXBean#KEY_VALUE_DELIMITER}.  The pairs themselves are separated by
     * {@link StrategyMXBean#KEY_VALUE_SEPARATOR}.
     * 
     * <p>Note that if any of the keys or values of the <code>Properties</code> object contains either the
     * {@link StrategyMXBean#KEY_VALUE_DELIMITER} or the {@link StrategyMXBean#KEY_VALUE_SEPARATOR} character, the resulting String will
     * not be parseable with {@link #propertiesFromString(String)}.
     *
     * @param inProperties a <code>Properties</code> value
     * @return a <code>String</code> value or null if <code>inProperties</code> is null or empty
     */
    public static String propertiesToString(Properties inProperties)
    {
        if(inProperties == null ||
           inProperties.isEmpty()) {
            return null;
        }
        StringBuffer output = new StringBuffer();
        boolean delimiterNeeded = false;
        for(Object key : inProperties.keySet()) {
            if(delimiterNeeded) {
                output.append(StrategyMXBean.KEY_VALUE_DELIMITER);
            } else {
                delimiterNeeded = true;
            }
            output.append(key).append(StrategyMXBean.KEY_VALUE_SEPARATOR).append(inProperties.getProperty((String)key));
        }
        return output.toString();
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
        // must have 7 parameters, though the last four may be null
        if(inParameters == null ||
           inParameters.length != 7) {
            throw new ModuleCreationException(PARAMETER_COUNT_ERROR);
        }
        // parameter 1 is the strategy name (human-readable) and must be non-null and have non-zero length
        String name;
        if(inParameters[0] == null) {
            throw new ModuleCreationException(new I18NBoundMessage2P(NULL_PARAMETER_ERROR,
                                                                     1,
                                                                     String.class.getName()));
        }
        if(inParameters[0] instanceof String) {
            if(((String)inParameters[0]).isEmpty()) {
                throw new ModuleCreationException(EMPTY_NAME_ERROR);
            } else {
                name = (String)inParameters[0];
            }
        } else {
            throw new ModuleCreationException(new I18NBoundMessage3P(PARAMETER_TYPE_ERROR,
                                                                     1,
                                                                     String.class.getName(),
                                                                     inParameters[0].getClass().getName()));
        }
        // parameter 2 is the language.  the parameter may be of type Language or may be a String describing the contents of a Language enum value 
        Language type = null;
        if(inParameters[1] == null) {
            throw new ModuleCreationException(new I18NBoundMessage2P(NULL_PARAMETER_ERROR,
                                                                     2,
                                                                     Language.class.getName()));
        }
        if(inParameters[1] instanceof Language) {
            type = (Language)inParameters[1];
        } else {
            if(inParameters[1] instanceof String) {
                try {
                    type = Language.valueOf(((String)inParameters[1]).toUpperCase());
                } catch (Exception e) {
                    throw new ModuleCreationException(new I18NBoundMessage1P(INVALID_LANGUAGE_ERROR,
                                                                             inParameters[1].toString()));
                }
            } else {
                throw new ModuleCreationException(new I18NBoundMessage3P(PARAMETER_TYPE_ERROR,
                                                                         2,
                                                                         Language.class.getName(),
                                                                         inParameters[1].getClass().getName()));
            }
        }
        // parameter 3 is the strategy source.  the parameter must be of type File, must be non-null, must exist, and must be readable
        File source;
        if(inParameters[2] == null) {
            throw new ModuleCreationException(new I18NBoundMessage2P(NULL_PARAMETER_ERROR,
                                                                     3,
                                                                     File.class.getName()));
        }
        if(inParameters[2] instanceof File) {
            source = (File)inParameters[2];
            if(!(source.exists() ||
                    source.canRead())) {
                throw new ModuleCreationException(new I18NBoundMessage1P(FILE_DOES_NOT_EXIST_OR_IS_NOT_READABLE,
                                                                         source.getAbsolutePath()));
            }
        } else {
            throw new ModuleCreationException(new I18NBoundMessage3P(PARAMETER_TYPE_ERROR,
                                                                     3,
                                                                     File.class.getName(),
                                                                     inParameters[2].getClass().getName()));
        }
        // parameter 4 is a Properties object.  The parameter may be null.  If non-null, these values are made available to the running strategy.
        Properties parameters = null;
        if(inParameters[3] != null) {
            if(inParameters[3] instanceof Properties) {
                parameters = (Properties)inParameters[3];
            } else {
                throw new ModuleCreationException(new I18NBoundMessage3P(PARAMETER_TYPE_ERROR,
                                                                         4,
                                                                         Properties.class.getName(),
                                                                         inParameters[3].getClass().getName()));
            }
        }
        // parameter 5 is the classpath.  This parameter may be null.  If non-null, it contains an array of Strings which are passed to the
        //  script executor for use in a language-dependent fashion.
        String[] classpath = null;
        if(inParameters[4] != null) {
            if(inParameters[4] instanceof String[]) {
                classpath = (String[])inParameters[4];
            } else {
                throw new ModuleCreationException(new I18NBoundMessage3P(PARAMETER_TYPE_ERROR,
                                                                         5,
                                                                         String[].class.getName(),
                                                                         inParameters[4].getClass().getName()));
            }
        }
        // parameter 6 is a ModuleURN.  This parameter may be null.  If non-null, it must describe a module instance that is started and able
        //  to receive data.  Orders will be sent to this module when they are created.
        ModuleURN ordersInstance = null;
        if(inParameters[5] != null) {
            if(inParameters[5] instanceof ModuleURN) {
                ordersInstance = (ModuleURN)inParameters[5];
            } else {
                throw new ModuleCreationException(new I18NBoundMessage3P(PARAMETER_TYPE_ERROR,
                                                                         6,
                                                                         ModuleURN.class.getName(),
                                                                         inParameters[5].getClass().getName()));
            }
        }
        // parameter 7 is a ModuleURN.  This parameter may be null.  If non-null, it must describe a module instance that is started and able
        //  to receive data.  Trade suggestions will be sent to this module when they are created.
        ModuleURN suggestionsInstance = null;
        if(inParameters[6] != null) {
            if(inParameters[6] instanceof ModuleURN) {
                suggestionsInstance = (ModuleURN)inParameters[6];
            } else {
                throw new ModuleCreationException(new I18NBoundMessage3P(PARAMETER_TYPE_ERROR,
                                                                         7,
                                                                         ModuleURN.class.getName(),
                                                                         inParameters[6].getClass().getName()));
            }
        }
        return new StrategyModule(generateInstanceURN(name),
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
        }
        try {
            strategy = new StrategyImpl(name,
                                        getURN().getValue(),
                                        type,
                                        source,
                                        parameters,
                                        classpath,
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
        cancelAllMarketDataRequests();
        try {
            strategy.stop();
        } catch (StrategyException e) {
            // TODO should this be swallowed or propagated - ask Anshul
            e.printStackTrace();
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
                                           Long.toHexString(counter.incrementAndGet())));
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
     * Cancels a given market data request.
     *
     * @param inDataFlowID a <code>DataFlowID</code> value
     * @throws ModuleException if an error occurs while canceling the market data request
     */
    private void doMarketDataRequestCancel(DataFlowID inDataFlowID)
        throws ModuleException
    {
        dataFlowSupport.cancel(inDataFlowID);
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
    private final PublisherEngine ordersPublisher = new PublisherEngine();
    /**
     * the publishing engine for suggestions
     */
    private final PublisherEngine suggestionsPublisher = new PublisherEngine();
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
     * counter used to guarantee unique identifiers
     */
    private static final AtomicLong counter = new AtomicLong();
    /**
     * active market data requests for this strategy 
     */
    private final Map<Long,DataFlowID> marketDataRequests = new HashMap<Long,DataFlowID>();
    /**
     * the list of dataflows started during the lifetime of this strategy
     */
    private final List<DataFlowID> dataFlows = new ArrayList<DataFlowID>();
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
        private void unsubscribe()
        {
            if(requestType.equals(OutputType.ORDERS)) {
                ordersPublisher.unsubscribe(this);
            } else if(requestType.equals(OutputType.SUGGESTIONS)) {
                suggestionsPublisher.unsubscribe(this);
            } else {
                throw new IllegalArgumentException(); // this is a development-time exception to indicate the logic needs to be expanded because of a new request type
            }
        }
    }
}
