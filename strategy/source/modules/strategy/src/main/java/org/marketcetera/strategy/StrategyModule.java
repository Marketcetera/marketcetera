package org.marketcetera.strategy;

import static org.marketcetera.strategy.Messages.EMPTY_NAME_ERROR;
import static org.marketcetera.strategy.Messages.FILE_DOES_NOT_EXIST_OR_IS_NOT_READABLE;
import static org.marketcetera.strategy.Messages.INVALID_LANGUAGE_ERROR;
import static org.marketcetera.strategy.Messages.NULL_PARAMETER_ERROR;
import static org.marketcetera.strategy.Messages.PARAMETER_COUNT_ERROR;
import static org.marketcetera.strategy.Messages.PARAMETER_TYPE_ERROR;

import java.io.File;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;

import org.marketcetera.core.ClassVersion;
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
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.I18NBoundMessage2P;
import org.marketcetera.util.log.I18NBoundMessage3P;
import org.marketcetera.util.log.SLF4JLoggerProxy;

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
        implements DataEmitter, DataFlowRequester, DataReceiver, OutboundServices
{
    /* (non-Javadoc)
     * @see org.marketcetera.module.DataEmitter#cancel(org.marketcetera.module.RequestID)
     */
    @Override
    public void cancel(RequestID inRequestID)
    {
        OutputType.unsubscribe(inRequestID);
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
        request.subscribe(inSupport);
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
        OutputType.ORDERS.publish(inOrder);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.OutboundServices#sendSuggestion(java.lang.Object)
     */
    @Override
    public void sendSuggestion(Object inSuggestion)
    {
        OutputType.SUGGESTIONS.publish(inSuggestion);
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
        // must have 6 parameters, though the last three may be null
        if(inParameters == null ||
           inParameters.length != 6) {
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
        // parameter 5 is a ModuleURN.  This parameter may be null.  If non-null, it must describe a module instance that is started and able
        //  to receive data.  Orders will be sent to this module when they are created.
        ModuleURN ordersInstance = null;
        if(inParameters[4] != null) {
            if(inParameters[4] instanceof ModuleURN) {
                ordersInstance = (ModuleURN)inParameters[4];
            } else {
                throw new ModuleCreationException(new I18NBoundMessage3P(PARAMETER_TYPE_ERROR,
                                                                         5,
                                                                         ModuleURN.class.getName(),
                                                                         inParameters[4].getClass().getName()));
            }
        }
        // parameter 6 is a ModuleURN.  This parameter may be null.  If non-null, it must describe a module instance that is started and able
        //  to receive data.  Trade suggestions will be sent to this module when they are created.
        ModuleURN suggestionsInstance = null;
        if(inParameters[5] != null) {
            if(inParameters[5] instanceof ModuleURN) {
                suggestionsInstance = (ModuleURN)inParameters[5];
            } else {
                throw new ModuleCreationException(new I18NBoundMessage3P(PARAMETER_TYPE_ERROR,
                                                                         6,
                                                                         ModuleURN.class.getName(),
                                                                         inParameters[5].getClass().getName()));
            }
        }
        return new StrategyModule(generateInstanceURN(name),
                                  name,
                                  type,
                                  source,
                                  parameters,
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
        if(ordersDestination != null) {
            ordersFlowID = dataFlowSupport.createDataFlow(new DataRequest[] { new DataRequest(getURN(),
                                                                                              OutputType.ORDERS),
                                                                              new DataRequest(ordersDestination) },
                                                          false);
        }
        if(suggestionsDestination != null) {
            suggestionsFlowID = dataFlowSupport.createDataFlow(new DataRequest[] { new DataRequest(getURN(),
                                                                                                   OutputType.SUGGESTIONS),
                                                                                   new DataRequest(suggestionsDestination) },
                                                               false);
        }
        strategy = new StrategyImpl(name,
                                    getURN().getValue(),
                                    type,
                                    source,
                                    parameters,
                                    this);
        strategy.start();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.Module#preStop()
     */
    @Override
    protected void preStop()
            throws ModuleException
    {
        strategy.stop();
        if(ordersFlowID != null) {
            dataFlowSupport.cancel(ordersFlowID);
        }
        if(suggestionsFlowID != null) {
            dataFlowSupport.cancel(suggestionsFlowID);
        }
    }
    /**
     * Generates an instance URN guaranteed to be unique with the scope of this JVM.
     *
     * @return a <code>ModuleURN</code> value
     */
    private static final ModuleURN generateInstanceURN(String inName)
    {
        // TODO sanitize inName and use it as part of the instance URN
        String[] namePieces = inName.split("[A-Z|a-z|0-9]");
        return new ModuleURN(StrategyModuleFactory.PROVIDER_URN,
                             String.format("strategy%s", //$NON-NLS-1$
//                             String.format("strategy%s%s", //$NON-NLS-1$
//                                           inName,
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
    private final Properties parameters;
    /**
     * the instanceURN of a destination for Orders, may be null.  if non-null, the object contract is to plumb a route from this object to the instance contained herein for orders before start.
     */
    private final ModuleURN ordersDestination;
    /**
     * the instanceURN of a destination for Suggestions, may be null.  if non-null, the object contract is to plumb a route from this object to the instance contained herein for suggestions before start.
     */
    private final ModuleURN suggestionsDestination;
    /**
     * the flow identifier for the orders flow created as a side-effect of being given a non-null {@link #ordersDestination} during creation.  may be null if no orders flow was established.
     */
    private DataFlowID ordersFlowID = null;
    /**
     * the flow identifier for the suggestions flow created as a side-effect of being given a non-null {@link #suggestionsDestination} during creation.  may be null if no suggestions flow was established.
     */
    private DataFlowID suggestionsFlowID = null;
    /**
     * the strategy object that represents the actual running strategy
     */
    private Strategy strategy;
    /**
     * services for data flow creation
     */
    private DataFlowSupport dataFlowSupport;
    /**
     * counter used to guarantee unique strategy URNs
     */
    private static final AtomicLong counter = new AtomicLong();
}
