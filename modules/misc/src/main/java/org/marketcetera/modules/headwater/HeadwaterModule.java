package org.marketcetera.modules.headwater;

import java.util.Map;
import java.util.Set;

import org.marketcetera.module.DataEmitter;
import org.marketcetera.module.DataEmitterSupport;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.Module;
import org.marketcetera.module.ModuleException;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.module.RequestDataException;
import org.marketcetera.module.RequestID;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/* $License$ */

/**
 * Provides an insertion point to a module framework data flow.
 * 
 * <p>Module Features
 * <table>
 * <tr><th>Capabilities</th><td>Data Emitter</td></tr>
 * <tr><th>DataFlow Request Parameters</th><td>none</td></tr>
 * <tr><th>Stops data flows</th><td>n/a</td></tr>
 * <tr><th>Start Operation</th><td>n/a</td></tr>
 * <tr><th>Stop Operation</th><td>n/a</td></tr>
 * <tr><th>Management Interface</th>n/a</td></tr>
 * <tr><th>Factory</th><td>{@link HeadwaterModuleFactory}</td></tr>
 * </table>
 * </p>
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class HeadwaterModule
        extends Module
        implements DataEmitter
{
    /**
     * Get a <code>HeadwaterInstance</code> instance, creating it if necessary.
     *
     * @param inInstanceName a <code>String</code> value
     * @return a <code>HeadwaterModule</code> value
     */
    public static HeadwaterModule getInstance(String inInstanceName)
    {
        synchronized(instances) {
            if(instances.containsKey(inInstanceName)) {
                return instances.get(inInstanceName);
            } else {
                ModuleManager moduleManager = ModuleManager.getInstance();
                moduleManager.createModule(HeadwaterModuleFactory.PROVIDER_URN,
                                           inInstanceName);
                return instances.get(inInstanceName);
            }
        }
    }
    /**
     * Emit the given data to the given data flows, if specified.
     * 
     * <p>If no data flows are specified, the data is emitted to all data flows in which this module participates.
     *
     * @param inData an <code>Object</code> value
     * @param inDataFlowIds a <code>DataFlowID[]</code> value or <code>null</code>
     */
    public void emit(Object inData,
                     DataFlowID...inDataFlowIds)
    {
        Set<DataEmitterSupport> targets = Sets.newHashSet();
        synchronized(dataSupport) {
            if(inDataFlowIds == null || inDataFlowIds.length == 0) {
                targets.addAll(dataSupport.values());
            } else {
                for(DataFlowID dataFlowId : inDataFlowIds) {
                    DataEmitterSupport dataEmitterSupport = dataSupport.get(dataFlowId);
                    if(dataEmitterSupport == null) {
                        SLF4JLoggerProxy.warn(this,
                                              "{} has no target for {}",
                                              getURN(),
                                              dataFlowId);
                    } else {
                        targets.add(dataEmitterSupport);
                    }
                }
            }
        }
        for(DataEmitterSupport target : targets) {
            SLF4JLoggerProxy.trace(this,
                                   "{} sending {} to {}",
                                   getURN(),
                                   inData,
                                   target);
            target.send(inData);
        }
    }
    /**
     * Create a new HeadwaterModule instance.
     *
     * @param inUrn a <code>ModuleURN</code> value
     * @param inInstanceName a <code>String</code> value
     */
    protected HeadwaterModule(ModuleURN inUrn,
                              String inInstanceName)
    {
        super(inUrn,
              true);
        synchronized(instances) {
            if(instances.containsKey(inInstanceName)) {
                throw new UnsupportedOperationException("Duplicate instance: " + inInstanceName);
            }
            instances.put(inInstanceName,
                          this);
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
        synchronized(dataSupport) {
            dataSupport.put(inSupport.getFlowID(),
                            inSupport);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.DataEmitter#cancel(org.marketcetera.module.DataFlowID, org.marketcetera.module.RequestID)
     */
    @Override
    public void cancel(DataFlowID inFlowID,
                       RequestID inRequestID)
    {
        synchronized(dataSupport) {
            dataSupport.remove(inFlowID);
        }
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
     * if wired into a multi-module flow, this object will assist in passing data to the next object in the flow
     */
    private final Map<DataFlowID,DataEmitterSupport> dataSupport = Maps.newHashMap();
    /**
     * tracks all module instances
     */
    private static final Map<String,HeadwaterModule> instances = Maps.newHashMap();
}
