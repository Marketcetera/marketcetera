package org.marketcetera.modules.requester;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.module.*;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/* $License$ */

/**
 * Provides access for non-modules to receive module data flows.
 * 
 * <p>Module Features
 * <table>
 * <tr><th>Capabilities</th><td>Data Flow Requester, Data Receiver</td></tr>
 * <tr><th>Factory</th><td>{@link RequesterModuleFactory}</td></tr>
 * </table></p>
 * 
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ThreadSafe
@ClassVersion("$Id$")
public class RequesterModule
        extends Module
        implements DataReceiver, DataFlowRequester, Requester
{
    /**
     * Gets the requester that corresponds to the given <code>ModuleURN</code> value. 
     *
     * @param inInstanceUrn a <code>ModuleURN</code> value
     * @return a <code>Requester</code> or <code>null</code>
     */
    public static Requester getRequesterFor(ModuleURN inInstanceUrn)
    {
        synchronized(instances) {
            return instances.get(inInstanceUrn);
        }
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
            throws ReceiveDataException
    {
        Lock receiveLock = dataLock.readLock();
        receiveLock.lock();
        try {
            ISubscriber subscriber = requesters.get(inFlowID);
            if(subscriber != null &&
               subscriber.isInteresting(inData)) {
                subscriber.publishTo(inData);
            }
        } finally {
            receiveLock.unlock();
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.modules.requester.Requester#subscribeToDataFlow(org.marketcetera.core.publisher.ISubscriber, org.marketcetera.module.DataRequest[])
     */
    @Override
    public void subscribeToDataFlow(ISubscriber inSubscriber,
                                    DataRequest[] inRequests)
    {
        // remap requests array to include us
        Deque<DataRequest> mappedRequests = new LinkedList<DataRequest>(Arrays.asList(inRequests));
        mappedRequests.addLast(new DataRequest(getURN()));
        Lock subscribeLock = dataLock.writeLock();
        subscribeLock.lock();
        try {
            DataFlowID dataFlow = dataFlowSupport.createDataFlow(mappedRequests.toArray(new DataRequest[mappedRequests.size()]),
                                                                 false);
            requesters.put(dataFlow,
                           inSubscriber);
            dataFlows.put(inSubscriber,
                          dataFlow);
        } finally {
            subscribeLock.unlock();
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.modules.requester.Requester#cancelSubscription(org.marketcetera.core.publisher.ISubscriber)
     */
    @Override
    public void cancelSubscription(ISubscriber inSubscriber)
    {
        Lock cancelLock = dataLock.writeLock();
        cancelLock.lock();
        try {
            Collection<DataFlowID> dataFlowsToCancel = dataFlows.removeAll(inSubscriber);
            if(dataFlowsToCancel != null) {
                for(DataFlowID dataFlow : dataFlowsToCancel) {
                    SLF4JLoggerProxy.debug(RequesterModule.class,
                                           "Canceling data flow {}",
                                           dataFlow);
                    dataFlowSupport.cancel(dataFlow);
                    requesters.remove(dataFlow);
                }
            }
        } finally {
            cancelLock.unlock();
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.Module#preStart()
     */
    @Override
    protected void preStart()
            throws ModuleException
    {
        Lock startLock = dataLock.writeLock();
        startLock.lock();
        try {
            requesters.clear();
            dataFlows.clear();
        } finally {
            startLock.unlock();
        }
        synchronized(instances) {
            instances.put(getURN(),
                          this);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.Module#preStop()
     */
    @Override
    protected void preStop()
            throws ModuleException
    {
        Lock stopLock = dataLock.writeLock();
        stopLock.lock();
        try {
            requesters.clear();
            dataFlows.clear();
        } finally {
            stopLock.unlock();
        }
        synchronized(instances) {
            instances.remove(getURN());
        }
    }
    /**
     * Create a new RequesterModule instance.
     *
     * @param inName a <code>String</code> value
     */
    private RequesterModule(String inName)
    {
        super(new ModuleURN(RequesterModuleFactory.PROVIDER_URN,
                            inName),
              true);
    }
    /**
     * Creates <code>RequesterModule</code> objects.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    @ClassVersion("$Id$")
    public static class RequesterModuleFactory
            extends ModuleFactory
    {
        /**
         * Create a new RequesterModuleFactory instance.
         */
        public RequesterModuleFactory()
        {
            super(PROVIDER_URN,
                  Messages.PROVIDER_DESCRIPTION,
                  true,
                  false,
                  String.class);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.module.ModuleFactory#create(java.lang.Object[])
         */
        @Override
        public Module create(Object... inParameters)
                throws ModuleCreationException
        {
            return new RequesterModule(String.valueOf(inParameters[0]));
        }
        /**
         * provider URN value
         */
        public static final ModuleURN PROVIDER_URN = new ModuleURN("metc:module:requester");
    }
    /**
     * data flow support value
     */
    private volatile DataFlowSupport dataFlowSupport;
    /**
     * requesters value
     */
    @GuardedBy("dataLock")
    private final Map<DataFlowID,ISubscriber> requesters = new HashMap<DataFlowID,ISubscriber>();
    /**
     * data flows value
     */
    @GuardedBy("dataLock")
    private final Multimap<ISubscriber,DataFlowID> dataFlows = HashMultimap.create();
    /**
     * data lock value
     */
    private final ReadWriteLock dataLock = new ReentrantReadWriteLock();
    /**
     * instances value
     */
    @GuardedBy("instances")
    private static final Map<ModuleURN,Requester> instances = new HashMap<ModuleURN,Requester>();
}
