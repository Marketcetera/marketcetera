package org.marketcetera.tensorflow.model;

import org.marketcetera.module.AutowiredModule;
import org.marketcetera.module.DataEmitter;
import org.marketcetera.module.DataEmitterSupport;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.DataReceiver;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.Module;
import org.marketcetera.module.ModuleCreationException;
import org.marketcetera.module.ModuleException;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.module.ReceiveDataException;
import org.marketcetera.module.RequestDataException;
import org.marketcetera.module.RequestID;
import org.marketcetera.tensorflow.GraphContainer;
import org.marketcetera.tensorflow.Messages;
import org.marketcetera.tensorflow.service.TensorFlowService;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.tensorflow.Tensor;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/* $License$ */

/**
 * Triggers an existing tensor flow model in a data flow.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@AutowiredModule
public class TensorFlowModelModule
        extends Module
        implements DataEmitter, DataReceiver
{
    /**
     * Create a new TensorFlowModelModule instance.
     *
     * @param inURN
     * @param inModelName
     */
    public TensorFlowModelModule(ModuleURN inURN,
                                 String inModelName)
    {
        super(inURN,
              true);
        modelName = inModelName;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.DataReceiver#receiveData(org.marketcetera.module.DataFlowID, java.lang.Object)
     */
    @Override
    public void receiveData(DataFlowID inFlowId,
                            Object inData)
            throws ReceiveDataException
    {
        if(inData == null || !(inData instanceof Tensor)) {
            SLF4JLoggerProxy.warn(this,
                                  "{} ignored {} from {} because it is not a tensor",
                                  getURN(),
                                  inData,
                                  inFlowId);
            return;
        }
        // the lifecycle of the input tensor is owned by this module
        try(Tensor input = (Tensor)inData) {
            RequestMetaData request = dataRequestsByDataFlowId.getIfPresent(inFlowId);
            if(request == null) {
                SLF4JLoggerProxy.warn(this,
                                      "{} ignored {} from {} because the data flow no longer exists",
                                      getURN(),
                                      inData,
                                      inFlowId);
                return;
            }
            Object output = request.fetch(input);
            SLF4JLoggerProxy.trace(this,
                                   "{} sending {} to {}",
                                   getURN(),
                                   output,
                                   inFlowId);
            request.send(output);
        }
    }
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
                       RequestID inRequestID)
    {
        dataRequestsByDataFlowId.invalidate(inFlowId);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.Module#preStart()
     */
    @Override
    protected void preStart()
            throws ModuleException
    {
        /*
         * note that this implies the following:
         * - graphs are read only (the persistent implementation is not updated by the execution of this module)
         * - graphs are not updated from persistence during the lifetime of this module
         */
        GraphContainer graphContainer = tensorFlowService.findByName(modelName);
        if(graphContainer == null) {
            throw new ModuleCreationException(new I18NBoundMessage1P(Messages.NO_MODEL_ERROR,
                                                                     modelName));
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.Module#preStop()
     */
    @Override
    protected void preStop()
            throws ModuleException
    {
        dataRequestsByDataFlowId.invalidateAll();
    }
    private class RequestMetaData
    {
        private Object fetch(Tensor inInput)
        {
            return runner.fetch(request,
                                inInput);
        }
        /**
         * 
         *
         *
         * @param inTensor
         */
        private void send(Object inTensor)
        {
            dataEmitterSupport.send(inTensor);
        }
        /**
         * Create a new RequestMetaData instance.
         *
         * @param inDataRequest
         * @param inDataEmitterSupport
         */
        private RequestMetaData(DataRequest inDataRequest,
                                DataEmitterSupport inDataEmitterSupport)
        {
            request = inDataRequest;
            dataEmitterSupport = inDataEmitterSupport;
            Object data = inDataRequest.getData();
            if(data instanceof TensorFlowRunner) {
                runner = (TensorFlowRunner)data;
            } else if(data instanceof String) {
                runner = applicationContext.getBean(String.valueOf(data),
                                                    TensorFlowRunner.class);
            } else {
                throw new RequestDataException(new I18NBoundMessage1P(Messages.UNKNOWN_REQUEST_TYPE,
                                                                      inDataRequest.getClass().getSimpleName()));
            }
        }
        private final DataRequest request;
        private final TensorFlowRunner runner;
        private final DataEmitterSupport dataEmitterSupport;
    }
    /**
     * provides access to the Spring context of the application
     */
    @Autowired
    private ApplicationContext applicationContext;
    /**
     * provides access to Tensor Flow services
     */
    @Autowired
    private TensorFlowService tensorFlowService;
    /**
     * name of the model for this
     */
    private final String modelName;
    /**
     * 
     */
    private final Cache<DataFlowID,RequestMetaData> dataRequestsByDataFlowId = CacheBuilder.newBuilder().build();
}
