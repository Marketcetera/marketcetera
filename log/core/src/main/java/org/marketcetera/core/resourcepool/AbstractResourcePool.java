package org.marketcetera.core.resourcepool;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.lang.Validate;
import org.marketcetera.core.CloseableLock;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides access to a pool of similar resources.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public abstract class AbstractResourcePool<ResourceClazz extends Resource<ResourceAllocationHintClazz>,ResourceAllocationHintClazz>
        implements ResourcePool<ResourceClazz,ResourceAllocationHintClazz>
{
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#isRunning()
     */
    @Override
    public final boolean isRunning()
    {
        return running.get();
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#start()
     */
    @Override
    public synchronized final void start()
    {
        if(running.get()) {
            try {
                stop();
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(this,
                                      e);
            }
        }
        setStatus(ResourcePoolStatus.STARTING);
        doStart();
        int availableResourceCount = 0;
        for(ResourceClazz resource : getResources()) {
            try {
                availableResourceCount += (resource.getResourceStatus().isReady() ? 1 : 0);
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(this,
                                      e);
            }
        }
        Validate.isTrue(availableResourceCount > 0);
        setStatus(ResourcePoolStatus.READY);
        running.set(true);
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#stop()
     */
    @Override
    public synchronized final void stop()
    {
        setStatus(ResourcePoolStatus.STOPPING);
        try {
            doStop();
        } catch (Exception e){
            SLF4JLoggerProxy.warn(this,
                                  e);
        } finally {
            running.set(false);
            setStatus(ResourcePoolStatus.NOT_READY);
        }
    }
    /* (non-Javadoc)
     * @see com.marketcetera.marketdata.exegy.resource.ResourcePool#getStatus()
     */
    @Override
    public ResourcePoolStatus getStatus()
    {
        return status;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.marketdata.exegy.resource.ResourcePool#getResource()
     */
    @Override
    public ResourceClazz getResource()
    {
        return getResource(null);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.marketdata.exegy.resource.ResourcePool#getResource(java.lang.Object)
     */
    @Override
    public ResourceClazz getResource(ResourceAllocationHintClazz inHint)
    {
        try(CloseableLock theLock = CloseableLock.create(getResourceLock().writeLock())) {
            theLock.lock();
            Iterator<ResourceClazz> resourceCandidateIterator = getResources().iterator();
            ResourceClazz candidateResource = null;
            while(resourceCandidateIterator.hasNext()) {
                candidateResource = resourceCandidateIterator.next();
                if(candidateResource.getResourceStatus().isReady() && candidateResource.isSuitable(inHint)) {
                    resourceCandidateIterator.remove();
                    candidateResource.allocated();
                    break;
                }
                candidateResource = null;
            }
            Validate.notNull(candidateResource);
            return candidateResource;
        }
    }
    /* (non-Javadoc)
     * @see com.marketcetera.marketdata.exegy.resource.ResourcePool#returnResource(com.marketcetera.marketdata.exegy.resource.Resource)
     */
    @Override
    public void returnResource(ResourceClazz inResource)
    {
        if(inResource.isRunning() && inResource.getResourceStatus().isReady()) {
            returnResourceToPool(inResource);
            inResource.returned();
        } else {
            inResource.stop();
            inResource.released();
        }
    }
    /**
     * Sets the status value.
     *
     * @param inStatus a <code>ResourcePoolStatus</code> value
     */
    protected final void setStatus(ResourcePoolStatus inStatus)
    {
        status = inStatus;
    }
    /**
     * Gets the lock object used to control access to pool resources.
     *
     * @return a <code>ReadWriteLock</code> value
     */
    protected final ReadWriteLock getResourceLock()
    {
        return resourceLock;
    }
    /**
     * Executed when the pool is starting.
     * 
     * <p>The subclass is expected to allocate and start its resources in this method.
     */
    protected abstract void doStart();
    /**
     * Executed when the pool is stopping.
     * 
     * <p>The subclass is expected to shut down its resources in this method.
     */
    protected abstract void doStop();
    /**
     * Gets the resources in the pool.
     * 
     * <p>The order and contents of the resource pool are determined by the subclass and affect
     * what resources are allocated. The subclass is expected to modulate the order and contents
     * of this collection according to its whims. When a request for a resource is made, the
     * resources in this collection will be interrogated in the order of this collection.
     *
     * @return a <code>Collection&lt;ResourceClazz&gt;</code> value
     */
    protected abstract Collection<ResourceClazz> getResources();
    /**
     * Returns the given resource to the resource pool.
     * 
     * <p>It is the responsibility of the subclass to return this resource to the pool or not as it
     * sees fit.
     *
     * @param inResource a <code>ResourceClazz</code> value
     */
    protected abstract void returnResourceToPool(ResourceClazz inResource);
    /**
     * allows access to critical resources to be controlled
     */
    private final ReadWriteLock resourceLock = new ReentrantReadWriteLock();
    /**
     * indicates the status of the resource pool
     */
    private volatile ResourcePoolStatus status = ResourcePoolStatus.NOT_READY;
    /**
     * indicates if the resource pool is running or not
     */
    private final AtomicBoolean running = new AtomicBoolean(false);
}
