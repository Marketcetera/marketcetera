package org.marketcetera.core.resourcepool;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.marketcetera.core.LoggerAdapter;
import org.marketcetera.core.MessageKey;

/**
 * Maintains a pool of arbitrary resources.
 * 
 * This object regulates a pool of resources.  The pool issues and reclaims
 * resources for reuse.  The caller is responsible for returning the resources
 * via {@link #returnResource(Resource)}.  A {@link Resource} that declares itself
 * non-functional (see {@link Resource#isFunctional()}) will not be re-added to the
 * resource pool.
 * 
 * <p>The number of resources in the pool will be at least as large
 * as the value returned by {@link #getMinResources()} (subject to lazy-loading).
 * The number of resources in the pool will never be larger than the value returned
 * by {@link #getMaxResources()} except occasionally while the <code>ResourcePool</code>
 * is adding new <code>Resource</code> objects.
 * 
 * <p>This class manages synchronization for implementing subclasses.  No further
 * synchronization is needed unless the subclass is adding new behavior that so warrants.
 * 
 * @todo TODO The <code>ResourcePool</code> should track allocated resources to order them
 *   to shut down.
 * @todo TODO Add an action thread used to make the calls to resource (returned, released, etc).
 *   That way, a piggy resource can't shut down the whole pool.  The corallary of this, of course,
 *   is that the ResourcePool might re-allocate a resource before it has completed being returned. 
 * 
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: $
 */ 
public abstract class ResourcePool
{    
    protected enum STATUS { NOT_READY, READY, SHUTTING_DOWN, SHUT_DOWN };
    
    private STATUS mStatus = STATUS.NOT_READY;
    
    /**
     * Constructs a new <code>ResourcePool</code> object.
     */
    protected ResourcePool()
    {
        setStatus(STATUS.READY);
    }
    
    /**
     * Shutdown the <code>ResourcePool</code>.
     * 
     * Prevents further requests for {@link Resource} objects from the
     * <code>ResourcePool</code> and notifies each {@link Resource}
     * to shutdown.
     * 
     * <p>If the <code>ResourcePool</code> is already shutting down or has
     * shut down, this method does nothing.
     */
    public void shutdown()
    {
        if(LoggerAdapter.isDebugEnabled(this)) {
            LoggerAdapter.debug("Resource pool " + this + " shutdown request",
                                this);
        }
        // acquire a lock on the pool before proceeding
        synchronized(getPoolLock()) {
            // first, check to see if the pool is in a state such that we
            //  should reject the request to shut down
            if(rejectNewRequests()) {
                if(LoggerAdapter.isDebugEnabled(this)) {
                    LoggerAdapter.debug("Resource pool " + this + " is already shutting down or has shut down",
                                        this);
                }
                // TODO - shouldn't assume we know why the pool rejected the request
                //  throw an exception instead
                return;
            }
            // the pool is ready to be shut down
            // set the pool to deny further requests
            setStatus(STATUS.SHUTTING_DOWN);
            // iterate over all resources in the pool and tell them to shutdown
            Iterator<Resource> poolIterator = getPoolIterator();
            while(poolIterator.hasNext()) {
                Resource resource = poolIterator.next();
                try {
                    resource.shutdown(this);
                } catch (Throwable t) {
                    if(LoggerAdapter.isDebugEnabled(this)) {
                        LoggerAdapter.debug("Resource error during shutdown: " + resource,
                                            t,
                                            this);
                    }
                }
            }
            // TODO - need to tell all resources in the wild to shut down, too
            setStatus(STATUS.SHUT_DOWN);
        }
    }
    
    /**
     * Requests a resource from the pool.
     * 
     * Returns the next <code>Resource</code> in the
     * pool as determined by the specific subclass.  
     * If no <code>Resource</code> objects exist in the pool,
     * a sufficient number will be created to meet the lower bound
     * set by {@link #getMinResources()}.  Accordingly, the execution
     * time of this method may vary.
     * 
     * <p>If the number of <code>Resource</code> objects is below the threshold
     * set by {@link #getMinResources()}, this method will try to create
     * new <code>Resource</code> objects until the threshold is met.  When a resource
     * is requested and the current number of resources is below the minimum threshold,
     * new <code>Resource</code> objects will be created until the pool has minimum + 1
     * objects.  After creating the new <code>Resource</code> objects to meet minimum +1,
     * one object will be allocated to the caller, making the pool size upon return equal
     * to the minimum.  For the case where {@link #getMinResources()} == {@link #getMaxResources()},
     * the number of resources in the pool will momentarily increase to {@link #getMaxResources()} + 1
     * during the allocation process.
     * 
     * <p>It is the caller's responsibility to return the <code>Resource</code>
     * to the pool.
     * 
     * @return a <code>Resource</code> value
     * @throws ResourcePoolException if an error occurs while allocation a <code>Resource</code> from the pool 
     */
    public Resource getResource() 
        throws ResourcePoolException
    {
        return getResource(null);
    }
    
    /**
     * Requests a resource from the pool.
     * 
     * Returns the next <code>Resource</code> in the
     * pool as determined by the specific subclass.  
     * If no <code>Resource</code> objects exist in the pool,
     * a sufficient number will be created to meet the lower bound
     * set by {@link #getMinResources()}.  Accordingly, the execution
     * time of this method may vary.
     * 
     * <p>If the number of <code>Resource</code> objects is below the threshold
     * set by {@link #getMinResources()}, this method will try to create
     * new <code>Resource</code> objects until the threshold is met.  When a resource
     * is requested and the current number of resources is below the minimum threshold,
     * new <code>Resource</code> objects will be created until the pool has minimum + 1
     * objects.  After creating the new <code>Resource</code> objects to meet minimum +1,
     * one object will be allocated to the caller, making the pool size upon return equal
     * to the minimum.  For the case where {@link #getMinResources()} == {@link #getMaxResources()},
     * the number of resources in the pool will momentarily increase to {@link #getMaxResources()} + 1
     * during the allocation process.
     * 
     * <p>It is the caller's responsibility to return the <code>Resource</code>
     * to the pool.
     *
     * @param inData an <code>Object</code> value containing any information that might be
     *   relevant to the underlying implementation to assist in retrieving the next {@link Resource}
     * @return a <code>Resource</code> value
     * @throws ResourcePoolException if an error occurs while allocation a <code>Resource</code> from the pool 
     */
    public Resource getResource(Object inData)
        throws ResourcePoolException
    {
        if(LoggerAdapter.isDebugEnabled(this)) {
            LoggerAdapter.debug("Requesting resource from " + this + " with supplementary info: " + inData,
                                this);
        }
        // check to see if, for some reason, the pool should reject this request
        // TODO the exception is too specific, there could be other reasons why the pool might shut down
        if(rejectNewRequests()) {
            throw new ResourcePoolShuttingDownException(MessageKey.ERROR_RESOURCE_POOL_SHUTTING_DOWN);
        }
        // holds the last exception thrown by the creation method, if any
        //  this value is retained to send back to the caller, if necessary,
        //  in case the max number of attempts to create a resource is exceeded.
        ResourcePoolException lastCreationException = null;
        // check the configuration, as long as we're here
        checkResourcePoolConfiguration();
        // lock up the resource pool for the balance of the method
        synchronized(getPoolLock()) {
            // this is the number of consecutive times the pool method has failed to allocate
            //  a new resource
            int creationFailures = 0;
            // we need to find a resource, start by checking to see if
            //  the number in the pool is below the minimum threshold
            // repeat this loop until the pool contains at least the number
            //  of resources to maintain (+1 because we're going to remove one right away so we
            //  want to maintain the minimum *after* we take one out)
            while(getCurrentPoolSize() < getMinResources() + 1) {
                try {
                    createNewResource(null);
                    creationFailures = 0;
                } catch (ResourcePoolException e) {
                    if(LoggerAdapter.isDebugEnabled(this)) { 
                        LoggerAdapter.debug("ResourePool failed to allocate resource",
                                            e,
                                            this); 
                    }
                    // hold on to this exception - we *might* want to wrap it
                    //  and send it back to the caller
                    lastCreationException = e;
                    // count how many times we try to create new resources -
                    //  make sure we don't exceed the limit set by subclasses
                    creationFailures += 1;
                    if(creationFailures > getMaxResourceCreationFailures()) {
                        if(LoggerAdapter.isDebugEnabled(this)) { 
                            LoggerAdapter.debug("ResourcePool cannot allocate a resource after " + creationFailures + " failed attempt(s)",
                                                this); 
                        }
                        break;
                    }
                    // allow subclasses to perform some action, perhaps wait a little,
                    //  perhaps fix something, before trying again
                    try {
                        resourceCreationFailed();
                    } catch (Throwable t) {
                        // resource creation recovery threw an exception - log it and continue
                        if(LoggerAdapter.isDebugEnabled(this)) {
                            LoggerAdapter.debug("ResourcePool was unable to recover from a failed resource creation attempt", 
                                                t,
                                                this);
                        }
                    }
                }
            }
            // at this point, the number of resources in the resource pool is at
            //  least equal to the minimum threshold of resources allowed
            try {
                // retrieve the next resource from the pool and return it
                return allocateResource(inData);
            } catch (NoSuchElementException e) {
                if(LoggerAdapter.isDebugEnabled(this)) { 
                    LoggerAdapter.debug("ResourcePool cannot return a resource because there are none to allocate and no new resources could be allocated (see above)",
                                        this); 
                }
                // there are no resources to be removed
                throw new NoResourceException(MessageKey.ERROR_RESOURCE_POOL_COULD_NOT_ALLOCATE_NEW_RESOURCE,
                                              lastCreationException);
            } catch (ReservationForAllocatedResourceException e) {
                throw e;
            } catch (Throwable t) {
                // this comes from "allocateResource" or "createResource", which means that the resource to be returned
                //  could not be allocated, likely because the callback to Resource threw an exception
                throw new NoResourceException(MessageKey.ERROR_RESOURCE_POOL_COULD_NOT_ALLOCATE_NEW_RESOURCE,
                                              t);
            }
        }
    }
    
    protected void createNewResource(Object inData) 
        throws ResourcePoolException
    {
        // try to create a new resource and add it to the pool
        Resource resource = null;
        try {
            resource = createResource(inData);
            resource.initialize(this);                        
        } catch (ResourceCreationException e) {
            // rethrow to avoid excess nesting
            throw e;
        } catch (Throwable t) {
            throw new ResourceCreationException(t);
        }
        addResource(resource);
    }
    
    /**
     * Chooses a <code>Resource</code> object from the resource pool.
     *
     * The <code>Resource</code> objects are returned in the order set
     * by the <code>ResourcePool</code> subclass.
     * 
     * @param inData an <code>Object</code> value used to decide what resource to allocate
     * @return a <code>Resource</code> value
     * @throws Throwable if an error occurs during the call to {@link Resource#allocated(ResourcePool)}.
     */
    protected Resource allocateResource(Object inData)
        throws Throwable
    {
        synchronized(getPoolLock()) {
            Resource resource = getNextResource(inData);
            try {
                resource.allocated(this);
            } catch (Throwable t) {
                try {
                    releaseResource(resource);
                } catch (Throwable t1) {
                    // this release is all fouled up
                    // log this exception, it doesn't change the flow
                    if(LoggerAdapter.isDebugEnabled(this)) {
                        LoggerAdapter.debug("During allocation, resource " + resource + " failed so it was released." +
                                            "Get this: It failed again during release.  Forget this resource, it's dead.", 
                                            t1,
                                            this);
                    }
                }
                throw t;
            }

            if(LoggerAdapter.isDebugEnabled(this)) {
                LoggerAdapter.debug("Resource " + resource + " allocated", 
                                    this);
            }
            return resource;
        }
    }

    /**
     * Returns a <code>Resource</code> to the pool.
     * 
     * Resources returned will be added back to the pool for other callers
     * to re-use.  A <code>Resource</code> will be re-used only iff it
     * deems itself functional (see {@link Resource#isFunctional()}) and
     * if the number of <code>Resource</code> objects is currently less than
     * the maximum threshold set in {@link #getMaxResources()}.  If either
     * of these two conditions fails, a <code>Resource</code> is discarded.  Note that
     * there is no guarantee that this {@link Resource} will be discarded.
     * {@link Resource} objects are stored in priority order and the lowest priority
     * {@link Resource} will be discarded in case of overflow.
     * 
     * <p>If a {@link Resource} is discarded, its {@link Resource#release()}
     * method is invoked.
     * 
     * <p>It is incumbent upon the caller to discard the {@link Resource} object after
     * it is returned; this object should not be re-used until it is reissued by the
     * <code>ResourcePool</code>.  This can be enforced by maintaining a state in the
     * {@link Resource} object when {@link Resource#returned(ResourcePool)} or
     * {@link Resource#released(ResourcePool)} is called by the <code>ResourcePool</code>.
     * 
     * <p>If a {@link Resource} is returned that is already in the pool, i.e., if a
     * {@link Resource} is returned twice, the pool throws an exception.
     * 
     * @param inResource a <code>Resource</code> value
     * @throws <code>ReturnedResourceException</code> if the {@link Resource} being returned throws an exception
     *   from {@link Resource#returned(ResourcePool)}
     * @throws <code>ReleasedResourceException</code> if the {@link Resource} being returned throws an exception
     *   from {@link Resource#released(ResourcePool)}
     * @throws <code>DuplicateResourceReturnException</code> if a {@link Resource} is returned twice without
     *   being reissued first
     */
    public void returnResource(Resource inResource)
        throws ResourcePoolException
    {
        if(LoggerAdapter.isDebugEnabled(this)) {
            LoggerAdapter.debug("Resource " + inResource + " being returned to ResourcePool", 
                                this);
        }
        // check to see if the pool is shutting down - if so, don't allow any more
        //  resources to be returned
        if(rejectNewRequests()) {
            throw new ResourcePoolShuttingDownException(MessageKey.ERROR_RESOURCE_POOL_SHUTTING_DOWN);
        }
        
        synchronized(getPoolLock()) {            
            // nominally, the resource will now be added back to the pool.  however,
            // there are three conditions under which the resource won't be returned:
            //  1) the resource reports itself to be non-functional
            //  2) the pool already contains the maximum number of resources - this means
            //      that a resource will be released, but it is not automatically the one
            //      being returned
            //  3) the pool already contains the resource being returned - this probably
            //      means that the resource has been returned twice
            
            // if the resource isn't functional, discard it (condition #1 above)
            try {
                if(!inResource.isFunctional()) {
                    if(LoggerAdapter.isDebugEnabled(this)) {
                        LoggerAdapter.debug("Resource " + inResource + " is non-functional, releasing", 
                                            this);
                    }
                    // resource is non-functional
                    releaseResource(inResource);
                    return;
                }
            } catch (Throwable t) {
                if(LoggerAdapter.isDebugEnabled(this)) {
                    LoggerAdapter.debug("Resource " + inResource + " failed when trying to report its state, releasing", 
                                        t, 
                                        this);
                }
                // resource is considered non-functional
                releaseResource(inResource);
                return;
            }
            
            // we already have enough resources, discard this one (condition #2 above)
            if(getCurrentPoolSize() >= getMaxResources()) {
                if(LoggerAdapter.isDebugEnabled(this)) {
                    LoggerAdapter.debug("ResourcePool already contains at least " + getMaxResources() + " resources, releasing " + inResource, 
                                        this);
                }
                // resource will not fit in the pool
                releaseResource(inResource);
                return;
            }
            
            // the resource has already been returned and not reissued (condition #3 above)
            if(poolContains(inResource)) {
                if(LoggerAdapter.isDebugEnabled(this)) {
                    LoggerAdapter.debug("ResourcePool already contains " + inResource + " - don't return the same Resource twice!", 
                                        this);
                }
                throw new DuplicateResourceReturnException(MessageKey.ERROR_RESOURCE_POOL_RESOURCE_ALREADY_RETURNED.getLocalizedMessage());
            }

            // we know three things:
            // 1) the resource being returned is not already in the pool
            // 2) the resource judges itself functional
            // 3) there are not already max resources in the pool
                
            // add the resource back to the pool
            addResource(inResource);
            try {
                // notify the resource that it has been returned
                inResource.returned(this);
            } catch (Throwable t) {
                throw new ReturnedResourceException(t);
            }                
        }
    }
    
    /**
     * Releases a <code>Resource</code>.
     * 
     * <p>This method will be called to mark a <code>Resource</code>
     * as unusable.  This <code>Resource</code> will be taken out of
     * service.
     * 
     * @param inResource a <code>Resource</code> value
     * @throws ReleasedResourceException
     */
    protected void releaseResource(Resource inResource) 
        throws ReleasedResourceException
    {
        synchronized(getPoolLock()) {
            try {
                inResource.released(this);
            } catch (Throwable t) {
                throw new ReleasedResourceException(t);
            }
        }
    }
    
    /**
     * Checks the configuration of the <code>ResourcePool</code>.
     * 
     * Subclasses may override, but overridden implementations should call this
     * method.
     * 
     * @throws ResourcePoolConfigurationException
     */
    protected void checkResourcePoolConfiguration()
        throws ResourcePoolConfigurationException
    {
        if(getMinResources() <= 0) {
            throw new ResourcePoolConfigurationException(MessageKey.ERROR_RESOURCE_POOL_RESOURCE_MINIMUM_CONFIGURATION);
        }
        if(getMaxResources() < getMinResources()) {
            throw new ResourcePoolConfigurationException(MessageKey.ERROR_RESOURCE_POOL_RESOURCE_MAXIMUM_CONFIGURATION);
        }        
    }
    
    /**
     * Invoked if {@link #createResource(Object)} fails before another attempt is made.
     * 
     * <p>Subclasses may override to address the reason or reasons behind the resource
     * creation failure.  The default implementation is to wait a little while.
     * 
     * @throws ResourcePoolException if an error occurs - this exception will be logged by the
     *   {@link ResourcePool} to the default logger, but otherwise will be ignored
     */
    protected void resourceCreationFailed()
        throws ResourcePoolException
    {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new ResourcePoolException(e);
        }
    }

    /**
     * Create a new <code>Resource</code> to be added to the resource pool.
     * 
     * @param inData an <code>Object</code> that may be used to influence how the <code>Resource</code> is created
     * @return a <code>Resource</code> value
     * @throws <code>ResourceCreationException</code> if an error occurs while creating the resource
     */
    protected abstract Resource createResource(Object inData)
        throws ResourceCreationException;
    
    /**
     * Returns the minimum number of {@link Resource} objects to retain in the pool.
     * 
     * <p>This value must conform to: 0 &lt; value &lt;= {@link #getMaxResources()}.
     * 
     * <p>The higher this value, the more potential overhead associated with calls to
     * {@link #getResource()}.  The lower this value, the greater the likelihood that
     * new <code>Resource</code> objects will need to be created.
     *  
     * @return an <code>int</code> value
     */
    protected abstract int getMinResources();
    
    /**
     * Returns the maximum number of {@link Resource} objects to retain in the pool.
     * 
     * <p>This value must conform to: {@link #getMinResources()} &lt;= value.
     * 
     * <p>The higher this value, the greater resource (small 'r') burden placed on the
     * system.  The lower this value, the greater the likelihood that no <code>Resource</code>
     * objects will be available for use.
     * 
     * @return an <code>int</code> value
     */
    protected abstract int getMaxResources();
    
    /**
     * Sets the number of times the pool will try to create a new <code>Resource</code>
     * object before giving up.
     * 
     * <p>This value is checked every time the pool is unable to create a new {@link Resource}.
     * When the number of consecutive times the pool is unable to create a {@link Resource}
     * exceeds this value, the pool will give up on the current attempt to return a
     * {@link Resource} from {@link #getResource()}.
     * 
     * @return an <code>int</code> value
     */
    protected abstract int getMaxResourceCreationFailures();

    /**
     * Gets the <code>ResourcePool</code> status.
     * 
     * @return the a <code>STATUS</code> value
     */
    protected STATUS getStatus()
    {
        synchronized(mStatus) {
            return mStatus;
        }
    }

    /**
     * Sets the <code>ResourcePool</code> status.
     * 
     * @param status a <code>STATUS</code> value
     */
    protected void setStatus(STATUS status)
    {
        synchronized(mStatus) {
            if(LoggerAdapter.isDebugEnabled(this)) {
                LoggerAdapter.debug("Resource pool " + this + " status changing to " + status,
                                    this);
            }
            
            mStatus = status;
        }
    }
    
    /**
     * Determines if the pool should reject requests to change its state.
     * 
     * <p>This method can be used to indicate to the pool that a request
     * to change its state should be rejected (get, return, shutdown, etc.).
     * 
     * @return a <code>boolean</code> value
     */
    protected boolean rejectNewRequests()
    {        
        return (getStatus().equals(STATUS.NOT_READY) ||
                getStatus().equals(STATUS.SHUT_DOWN) ||
                getStatus().equals(STATUS.SHUTTING_DOWN));
    }
    
    /**
     * Return an object over which synchronization can be performed.
     * 
     * <p>This object <strong>must</strong> be non-null at all times
     * after the completion of the constructor.
     * 
     * @return an <code>Object</code> value
     */
    protected abstract Object getPoolLock();
    
    /**
     * Return an <code>Iterator</code> that describes the preferred order
     * of access for <code>Resource</code> objects in the pool.
     * 
     * <p>Note that this <code>Iterator</code> is not used to allocate
     * <code>Resource</code> objects.  Instead, it is used to perform
     * maintenance on the pool.  The <code>Iterator</code> returned by
     * this method should traverse the pool in an order intuitive to the
     * underlying <code>Resource</code> storage mechanism.
     * 
     * @return an <code>Iterator</code> value
     */
    protected abstract Iterator<Resource> getPoolIterator();

    /**
     * Returns the current size of the <code>Resource</code> pool.
     * 
     * @return an <code>int</code> value
     */
    protected abstract int getCurrentPoolSize();
    
    /**
     * Adds the given <code>Resource</code> to the resource pool.
     *
     * @param inResource a <code>Resource</code> value
     * @throws ResourceCreationException if the <code>Resource</code> could not be added during creation
     * @throws ReturnedResourceException 
     */
    protected abstract void addResource(Resource inResource) 
        throws ResourcePoolException;
    
    /**
     * Removes the next <code>Resource</code> from the pool and
     * returns it for allocation.
     * 
     * <p>The <code>Resource</code> returned should be the next
     * <code>Resource</code> as defined by the subclass implementation.
     * 
     * <p>Note that the underlying pool should always have at least
     * one <code>Resource</code> in it at this time.  Previous calls
     * to {@link ResourcePool#createResource(Object)} and 
     * {@link ResourcePool#addResource(Resource)} within the same
     * synchronization block should guarantee this.
     * 
     * @param inData an <code>Object</code> value used to decide what resource to allocate
     * @return a <code>Resource</code> value
     * @throws ResourcePoolException if the <code>Resource</code> cannot be retrieved 
     */
    protected abstract Resource getNextResource(Object inData) 
        throws ResourcePoolException;
    
    /**
     * Determines if the pool currently contains the given <code>Resource</code>.
     * 
     * <p>No guarantees are made for this method's performance, but the underlying
     * subclass should be careful optimize this method.
     * 
     * @param inResource a <code>Resource</code> value
     * @return a <code>boolean</code> value
     */
    protected abstract boolean poolContains(Resource inResource);
    
    void dumpResourcePool()
    {
        if(LoggerAdapter.isDebugEnabled(this)) {
            synchronized(getPoolLock()) {
                Iterator iterator = getPoolIterator();
                LoggerAdapter.debug((iterator.hasNext() ? "Resource pool contains:" : "Resource pool is empty"),
                                    this);
                while(iterator.hasNext()) {
                    LoggerAdapter.debug(iterator.next().toString(),
                                        this);
                }
            }
        }
    }
}
