package org.marketcetera.module;

import org.marketcetera.util.log.SLF4JLoggerProxy;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/* $License$ */

/**
 * Provides a module framework that emits data it receives.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class AbstractDataReemitterModule
        extends Module
        implements DataEmitter,DataReceiver
{
    /* (non-Javadoc)
     * @see org.marketcetera.module.DataReceiver#receiveData(org.marketcetera.module.DataFlowID, java.lang.Object)
     */
    @Override
    public void receiveData(DataFlowID inFlowId,
                            Object inData)
            throws ReceiveDataException
    {
        DataEmitterSupport dataEmitterSupport = dataSupport.getIfPresent(inFlowId);
        SLF4JLoggerProxy.trace(this,
                               "Received {} for {}",
                               inData,
                               inFlowId);
        inData = onReceiveData(inData,
                               dataEmitterSupport);
        if(dataEmitterSupport != null && inData != null) {
            dataEmitterSupport.send(inData);
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
        SLF4JLoggerProxy.debug(this,
                               "Received request {} for {}",
                               inRequest,
                               inSupport);
        dataSupport.put(inSupport.getFlowID(),
                        inSupport);
        onRequestData(inRequest,
                      inSupport);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.DataEmitter#cancel(org.marketcetera.module.DataFlowID, org.marketcetera.module.RequestID)
     */
    @Override
    public void cancel(DataFlowID inFlowID,
                       RequestID inRequestID)
    {
        onCancel(inFlowID,
                 inRequestID);
        dataSupport.invalidate(inFlowID);
    }
    /**
     * Invoked when a data request is canceled.
     *
     * @param inFlowId a <code>DataFlowID</code> value
     * @param inRequestId a <code>RequestID</code> value
     */
    protected void onCancel(DataFlowID inFlowId,
                            RequestID inRequestId)
    {
    }
    /**
     * Invoked when a data request is made.
     *
     * @param inRequest a <code>DataRequest</code> value
     * @param inSupport a <code>DataEmitterSupport</code> value
     * @throws RequestDataException if an error occurs processing the request
     */
    protected void onRequestData(DataRequest inRequest,
                                 DataEmitterSupport inSupport)
            throws RequestDataException
    {
    }
    /**
     * Invoked when data is received as part of a data flow.
     *
     * @param inData an <code>Object</code> value
     * @param inDataSupport a <code>DataEmitterSupport</code> value
     * @return an <code>Object</code> value to re-emit
     */
    protected Object onReceiveData(Object inData,
                                   DataEmitterSupport inDataSupport)
    {
        return inData;
    }
    /**
     * Create a new AbstractDataReemitterModule instance.
     *
     * @param inURN a <code>ModuleURN</code> value
     * @param inAutoStart a <code>boolean</code> value
     */
    protected AbstractDataReemitterModule(ModuleURN inURN,
                                          boolean inAutoStart)
    {
        super(inURN,
              inAutoStart);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.Module#preStart()
     */
    @Override
    protected void preStart()
            throws ModuleException
    {
        dataSupport.invalidateAll();
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
     * Get the data support cache.
     *
     * @return a <code>Cache&lt;DataFlowID,DataEmitterSupport&gt;</code> value
     */
    protected Cache<DataFlowID,DataEmitterSupport> getDataSupport()
    {
        return dataSupport;
    }
    /**
     * if wired into a multi-module flow, this object will assist in passing data to the next object in the flow
     */
    private final Cache<DataFlowID,DataEmitterSupport> dataSupport = CacheBuilder.newBuilder().build();
}
