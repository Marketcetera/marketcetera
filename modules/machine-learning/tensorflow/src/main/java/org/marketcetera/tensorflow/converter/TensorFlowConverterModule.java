package org.marketcetera.tensorflow.converter;

import org.marketcetera.module.AutowiredModule;
import org.marketcetera.module.DataEmitter;
import org.marketcetera.module.DataEmitterSupport;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.DataReceiver;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.Module;
import org.marketcetera.module.ModuleException;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.module.ReceiveDataException;
import org.marketcetera.module.RequestDataException;
import org.marketcetera.module.RequestID;
import org.marketcetera.tensorflow.Messages;
import org.marketcetera.tensorflow.converters.TensorFromObjectConverter;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.tensorflow.Tensor;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/* $License$ */

/**
 * Converts conventional data types to {@link Tensor} objects.
 * 
 * <p>Module Features
 * <table>
 * <tr><th>Capabilities</th><td>Data Emitter, Data Receiver</td></tr>
 * <tr><th>DataFlow Request Parameters</th><td>none</td></tr>
 * <tr><th>Stops data flows</th><td>n/a</td></tr>
 * <tr><th>Start Operation</th><td>n/a</td></tr>
 * <tr><th>Stop Operation</th><td>n/a</td></tr>
 * <tr><th>Management Interface</th>n/a</td></tr>
 * <tr><th>Factory</th><td>{@link TensorFlowConverterModuleFactory}</td></tr>
 * </table>
 * </p>
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@AutowiredModule
public class TensorFlowConverterModule
        extends Module
        implements DataReceiver,DataEmitter
{
    /* (non-Javadoc)
     * @see org.marketcetera.module.DataEmitter#requestData(org.marketcetera.module.DataRequest, org.marketcetera.module.DataEmitterSupport)
     */
    @Override
    public void requestData(DataRequest inRequest,
                            DataEmitterSupport inSupport)
            throws RequestDataException
    {
        dataRequestsByDataFlowId.put(inSupport.getFlowID(),
                                     new RequestMetaData(inRequest,
                                                         inSupport));
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.DataEmitter#cancel(org.marketcetera.module.DataFlowID, org.marketcetera.module.RequestID)
     */
    @Override
    public void cancel(DataFlowID inFlowId,
                       RequestID inRequestId)
    {
        dataRequestsByDataFlowId.invalidate(inFlowId);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.DataReceiver#receiveData(org.marketcetera.module.DataFlowID, java.lang.Object)
     */
    @Override
    public void receiveData(DataFlowID inFlowId,
                            Object inData)
            throws ReceiveDataException
    {
        RequestMetaData request = dataRequestsByDataFlowId.getIfPresent(inFlowId);
        if(request == null) {
            SLF4JLoggerProxy.warn(this,
                                  "{} ignored {} from {} because the data flow no longer exists",
                                  getURN(),
                                  inData,
                                  inFlowId);
            return;
        }
        // the lifecycle of the input tensor is owned by a later module in the data flow
        Tensor output = request.fetch(inData);
        SLF4JLoggerProxy.trace(this,
                               "{} sending {} to {}",
                               getURN(),
                               output,
                               inFlowId);
        request.send(output);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.Module#preStart()
     */
    @Override
    protected void preStart()
            throws ModuleException
    {
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.Module#preStop()
     */
    @Override
    protected void preStop()
            throws ModuleException
    {
    }
    /**
     * Create a new TensorFlowModule instance.
     *
     * @param inURN a <code>ModuleURN</code> value
     */
    TensorFlowConverterModule(ModuleURN inURN)
    {
        super(inURN,
              true);
    }
    /**
     * Manages the data for a data flow request.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private class RequestMetaData
    {
        /**
         * Fetch the output data from the given tensor input.
         *
         * @param inInput an <code>Object</code> value
         * @return a <code>Tensor</code> value
         */
        private Tensor fetch(Object inInput)
        {
            return converter.convert(inInput);
        }
        /**
         * Send the given output to the data flow.
         *
         * @param inOutput an <code>Object</code> value
         */
        private void send(Object inOutput)
        {
            dataEmitterSupport.send(inOutput);
        }
        /**
         * Create a new RequestMetaData instance.
         *
         * @param inDataRequest a <code>DataRequest</code> value
         * @param inDataEmitterSupport a <code>DataEmitterSupport</code> value
         */
        private RequestMetaData(DataRequest inDataRequest,
                                DataEmitterSupport inDataEmitterSupport)
        {
            request = inDataRequest;
            dataEmitterSupport = inDataEmitterSupport;
            Object data = inDataRequest.getData();
            if(data instanceof TensorFromObjectConverter) {
                converter = (TensorFromObjectConverter<?>)data;
            } else if(data instanceof String) {
                converter = applicationContext.getBean(String.valueOf(data),
                                                       TensorFromObjectConverter.class);
            } else {
                throw new RequestDataException(new I18NBoundMessage1P(Messages.UNKNOWN_REQUEST_TYPE,
                                                                      inDataRequest.getClass().getSimpleName()));
            }
        }
        /**
         * data request value used to initiate the request
         */
        @SuppressWarnings("unused")
        private final DataRequest request;
        /**
         * used to convert objects to tensors
         */
        private final TensorFromObjectConverter<?> converter;
        /**
         * data emitter support value for data flows
         */
        private final DataEmitterSupport dataEmitterSupport;
    }
    /**
     * provides access to the Spring context of the application
     */
    @Autowired
    private ApplicationContext applicationContext;
    /**
     * holds request data by data flow id
     */
    private final Cache<DataFlowID,RequestMetaData> dataRequestsByDataFlowId = CacheBuilder.newBuilder().build();
}
