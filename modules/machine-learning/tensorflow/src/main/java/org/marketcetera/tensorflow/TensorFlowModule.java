package org.marketcetera.tensorflow;

import java.util.HashSet;
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
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.tensorflow.Graph;
import org.tensorflow.Session;
import org.tensorflow.Tensor;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@AutowiredModule
public class TensorFlowModule
        extends Module
        implements DataReceiver,DataEmitter
{
    /**
     * Create a new TensorFlowModule instance.
     *
     * @param inURN
     */
    public TensorFlowModule(ModuleURN inURN)
    {
        super(inURN,
              true);
    }
    /**
     * Create a new TensorFlowModule instance.
     *
     * @param inURN
     * @param inAutoStart
     */
    public TensorFlowModule(ModuleURN inURN,
                            boolean inAutoStart)
    {
        super(inURN,
              inAutoStart);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.DataEmitter#requestData(org.marketcetera.module.DataRequest, org.marketcetera.module.DataEmitterSupport)
     */
    @Override
    public void requestData(DataRequest inRequest,
                            DataEmitterSupport inSupport)
            throws RequestDataException
    {
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.DataEmitter#cancel(org.marketcetera.module.DataFlowID, org.marketcetera.module.RequestID)
     */
    @Override
    public void cancel(DataFlowID inFlowID,
                       RequestID inRequestID)
    {
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.DataReceiver#receiveData(org.marketcetera.module.DataFlowID, java.lang.Object)
     */
    @Override
    public void receiveData(DataFlowID inFlowId,
                            Object inData)
            throws ReceiveDataException
    {
        // convert incoming data to tensor
        try {
            Tensor tensor = getTensorConverter(inData).convert(inData);
            SLF4JLoggerProxy.trace(this,
                                   "{} converted data flow {} object {} to {}",
                                   getURN(),
                                   inFlowId,
                                   inData,
                                   tensor);
            // TODO process tensor?
        } catch (UnsupportedOperationException e) {
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
            converterCache = CacheBuilder.newBuilder().build(new CacheLoader<Class<?>,TensorConverter<?>>(){
                @Override
                public TensorConverter<?> load(Class<?> inKey)
                        throws Exception
                {
                    for(TensorConverter<?> converter : converters) {
                        Class<?> converterClass = converter.getType();
                        if(inKey.isAssignableFrom(converterClass)) {
                            return converter;
                        }
                    }
                    throw new UnsupportedOperationException("No tensor converter for " + inKey.getSimpleName());
                }});
        } else {
            converterCache.invalidateAll();
            converterCache = null;
        }
        logConverters();
        graph = new Graph();
//        graph.toGraphDef()
//        graph.importGraphDef(graphDef);
        session = new Session(graph);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.Module#preStop()
     */
    @Override
    protected void preStop()
            throws ModuleException
    {
        if(session != null) {
            try {
                session.close();
            } catch (Exception ignored) {}
            session = null;
        }
        if(graph != null) {
            try {
                graph.close();
            } catch (Exception ignored) {}
            graph = null;
        }
        if(converterCache != null) {
            converterCache.invalidateAll();
            converterCache = null;
        }
    }
    /**
     * 
     *
     *
     */
    private void logConverters()
    {
        SLF4JLoggerProxy.info(this,
                              "Available tensor converters: {}",
                              converters);
    }
    @SuppressWarnings("unchecked")
    private <T> TensorConverter<T> getTensorConverter(T inData)
    {
        return (TensorConverter<T>)converterCache.getUnchecked(inData.getClass());
    }
    /**
     * 
     */
    private LoadingCache<Class<?>,TensorConverter<?>> converterCache;
    /**
     * 
     */
    @Autowired(required=false)
    private Set<TensorConverter<?>> converters = new HashSet<>();
    /**
     * 
     */
    private Graph graph;
    /**
     * 
     */
    private Session session;
}
