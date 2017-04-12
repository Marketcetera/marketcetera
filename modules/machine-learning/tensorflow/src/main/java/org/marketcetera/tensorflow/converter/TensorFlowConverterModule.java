package org.marketcetera.tensorflow.converter;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
import org.marketcetera.tensorflow.converters.TensorFromObjectConverter;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.tensorflow.Tensor;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.UncheckedExecutionException;

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
    /**
     * Create a new TensorFlowModule instance.
     *
     * @param inURN a <code>ModuleURN</code> value
     */
    public TensorFlowConverterModule(ModuleURN inURN)
    {
        super(inURN,
              true);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.DataEmitter#requestData(org.marketcetera.module.DataRequest, org.marketcetera.module.DataEmitterSupport)
     */
    @Override
    public void requestData(DataRequest inRequest,
                            DataEmitterSupport inSupport)
            throws RequestDataException
    {
        dataRequests.put(inSupport.getFlowID(),
                         inSupport);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.DataEmitter#cancel(org.marketcetera.module.DataFlowID, org.marketcetera.module.RequestID)
     */
    @Override
    public void cancel(DataFlowID inFlowId,
                       RequestID inRequestId)
    {
        dataRequests.remove(inFlowId);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.DataReceiver#receiveData(org.marketcetera.module.DataFlowID, java.lang.Object)
     */
    @Override
    public void receiveData(DataFlowID inFlowId,
                            Object inData)
            throws ReceiveDataException
    {
        try {
            Tensor tensor = getTensorConverter(inData).convert(inData);
            SLF4JLoggerProxy.trace(this,
                                   "{} converted data flow {} object {} to {}",
                                   getURN(),
                                   inFlowId,
                                   inData,
                                   tensor);
            emitData(inFlowId,
                     tensor);
        } catch (UncheckedExecutionException e) {
            SLF4JLoggerProxy.warn(this,
                                  "{} unable to process data flow {} object {} because no tensor converter exists for {}",
                                  getURN(),
                                  inFlowId,
                                  inData,
                                  inData.getClass().getSimpleName());
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.Module#preStart()
     */
    @Override
    protected void preStart()
            throws ModuleException
    {
        if(converterCache == null) {
            converterCache = CacheBuilder.newBuilder().build(new CacheLoader<Class<?>,TensorFromObjectConverter<?>>(){
                @Override
                public TensorFromObjectConverter<?> load(Class<?> inKey)
                        throws Exception
                {
                    for(TensorFromObjectConverter<?> converter : converters) {
                        Class<?> converterClass = converter.getType();
                        // TODO this could be improved by doing an initial pass to find a converter for the exact type followed by a pass for an assignable type
                        if(converterClass.isAssignableFrom(inKey)) {
                            return converter;
                        }
                    }
                    throw new UnsupportedOperationException("No tensor converter for " + inKey.getSimpleName());
                }});
        } else {
            converterCache.invalidateAll();
            converterCache = null;
        }
        SLF4JLoggerProxy.info(this,
                              "Available tensor converters: {}",
                              converters);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.Module#preStop()
     */
    @Override
    protected void preStop()
            throws ModuleException
    {
        if(converterCache != null) {
            converterCache.invalidateAll();
            converterCache = null;
        }
    }
    /**
     * Emits the given tensor to data requesters.
     * 
     * @param inFlowId a <code>DataFlowID</code> value
     * @param inTensor a <code>Tensor</code> value
     */
    private void emitData(DataFlowID inFlowId,
                          Tensor inTensor)
    {
        DataEmitterSupport dataEmitterSupport = dataRequests.get(inFlowId);
        if(dataEmitterSupport != null) {
            dataEmitterSupport.send(inTensor);
        }
    }
    /**
     * Get the appropriate tensor converter for the given data type.
     *
     * @param inData a <code>T</code> value
     * @return a <code>TensorConverter&lt;T&gt;</code> value
     * @throws UnsupportedOperationException if no converter exists for the given data type
     */
    @SuppressWarnings("unchecked")
    private <T> TensorFromObjectConverter<T> getTensorConverter(T inData)
    {
        return (TensorFromObjectConverter<T>)converterCache.getUnchecked(inData.getClass());
    }
    /**
     * holds data requests
     */
    private final Map<DataFlowID,DataEmitterSupport> dataRequests = Maps.newConcurrentMap();
    /**
     * caches available tensor converters
     */
    private LoadingCache<Class<?>,TensorFromObjectConverter<?>> converterCache;
    /**
     * provides available tensor converters
     */
    @Autowired(required=false)
    private Set<TensorFromObjectConverter<?>> converters = new HashSet<>();
}
