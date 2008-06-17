package org.marketcetera.core.resourcepool;

import java.util.Iterator;

import org.marketcetera.core.LoggerAdapter;
import org.marketcetera.core.MessageKey;

/**
 * Maintains a pool of resources.
 * 
 * This object regulates a pool of resources.  A {@link Resource} is requested via a call to {@link #execute(ExecutableBlock)}.
 * The caller passes in a block of commands to execute under the auspices of a {@link Resource}.
 * The pool issues the {@link Resource} for the duration of the block.  At the conclusion of the
 * block, the {@link Resource} is reclaimed by the pool for reuse.
 * 
 * <p>This class manages synchronization for implementing subclasses.  No further
 * synchronization is needed unless the subclass is adding new behavior that so warrants.  The
 * number and type of resources in the pool is regulated by the subclass.
 * 
 * TODO The <code>ResourcePool</code> should track allocated resources to order them
 *   to shut down.
 * TODO Add an action thread used to make the calls to resource (returned, released, etc).
 *   That way, a piggy resource can't shut down the whole pool.  The corallary of this, of course,
 *   is that the ResourcePool might re-allocate a resource before it has completed being returned. 
 * 
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 */ 
public abstract class ResourcePool
{    
    protected enum STATUS { NOT_READY, READY, SHUTTING_DOWN, SHUT_DOWN };
    
    private STATUS mStatus = STATUS.NOT_READY;
    
    /**
     * Executes the given block of commands with a {@link Resource} from the <code>ResourcePool</code>.
     * 
     * <p>The <code>ResourcePool</code> allows the caller to execute an arbitrary block of commands
     * with the benefit of a {@link Resource} from the <code>ResourcePool</code>.  The {@link Resource}
     * will be allocated and passed to the block.  At the conclusion of the block, the {@link Resource}
     * will be automatically returned to the pool to allow it to be used by someone else.
     * 
     * @param inBlock an <code>ExecutableBlock</code> value
     * @return TODO
     * @throws ResourcePoolException if an error occurs during the allocation of the resource or the execution
     *   of the block
     */
    public Object execute(ExecutableBlock inBlock) 
        throws ResourcePoolException
    {
        return execute(inBlock,
                       null);
    }
    
    /**
     * Executes the given block of commands with a {@link Resource} from the <code>ResourcePool</code>.
     * 
     * <p>The <code>ResourcePool</code> allows the caller to execute an arbitrary block of commands
     * with the benefit of a {@link Resource} from the <code>ResourcePool</code>.  The {@link Resource}
     * will be allocated and passed to the block.  At the conclusion of the block, the {@link Resource}
     * will be automatically returned to the pool to allow it to be used by someone else.
     * 
     * <p>The <code>inData</code> parameter can be used by the specific implementation to assist in the
     * allocation of a specific {@link Resource} or to allocate a {@link Resource} in a specific way.
     * The implementation of the subclass will advise what this value should be.
     * 
     * @param inBlock an <code>ExecutableBlock</code> value
     * @param inData an <code>Object</code> value used to assist in the allocation of the {@link Resource}
     * @return TODO
     * @throws ResourcePoolException if an error occurs during the allocation of the resource or the execution
     *   of the block
     */
    public Object execute(ExecutableBlock inBlock,
                        Object inData) 
        throws ResourcePoolException
    {
        Resource resource = null;
        try {
            resource = requestResource(inData);
        } catch (Throwable t) {
            throw new ResourcePoolException(MessageKey.ERROR_RESOURCE_POOL_COULD_NOT_ALLOCATE_NEW_RESOURCE,
                                            t);
        }
        try {
            return inBlock.execute(resource);
        } catch (Throwable t) {
            throw new ResourcePoolException(MessageKey.ERROR_RESOURCE_POOL_EXECUTABLE_BLOCK_ERROR,
                                            t);
        } finally {
            try {
                returnResource(resource);
            } catch (ResourcePoolException e) {
                throw e;
            } catch (Throwable t) {
                throw new ResourcePoolException(t);
            }
        }
    }
    
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
                    resource.stop();
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
     * one will be added.  Accordingly, the execution
     * time of this method may vary.
     * 
     * <p>It is the caller's responsibility to return the <code>Resource</code>
     * to the pool.
     *
     * @param inData an <code>Object</code> value containing any information that might be
     *   relevant to the underlying implementation to assist in retrieving the next {@link Resource}
     * @return a <code>Resource</code> value
     * @throws ResourcePoolException if an error occurs while allocation a <code>Resource</code> from the pool 
     */
    protected Resource requestResource(Object inData)
        throws ResourcePoolException
    {
        if(LoggerAdapter.isDebugEnabled(this)) {
            LoggerAdapter.debug("Requesting resource from " + this + " with supplementary info: " + inData,
                                this);
        }
        try {
            // check to see if, for some reason, the pool should reject this request
            // TODO the exception is too specific, there could be other reasons why the pool might shut down
            if(rejectNewRequests()) {
                throw new ResourcePoolShuttingDownException(MessageKey.ERROR_RESOURCE_POOL_SHUTTING_DOWN);
            }
            // lock up the resource pool for the attempt to retrieve a resource
            synchronized(getPoolLock()) {
                Resource resource = getNextResource(inData);
                try {
                    resource.allocated();
                } catch (Throwable t) {
                    if(LoggerAdapter.isDebugEnabled(this)) {
                        LoggerAdapter.debug("Resource " + resource + " threw an exception when allocated, continuing",
                                            t,
                                            this);
                    }
                }
                return resource;
            }
        } catch (ResourcePoolException e) {
            throw e;
        } catch (Throwable t) {
            throw new ResourcePoolException(t);
        }
    }

    /**
     * Returns a <code>Resource</code> to the pool.
     * 
     * Resources returned will be added back to the pool for other callers
     * to re-use.  A <code>Resource</code> will be re-used iff it
     * deems itself functional (see {@link Resource#isFunctional()}).  If
     * this condition fails, the <code>Resource</code> is discarded.
     * If a {@link Resource} is discarded, its {@link Resource#released()}
     * method is invoked.
     * 
     * @param inResource a <code>Resource</code> value
     * @throws <code>ReturnedPoolException</code> if an error occurs while returning
     * the <code>Resource</code>
     */
    protected void returnResource(Resource inResource)
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
            // nominally, the resource will now be added back to the pool.  however, if
            //  the resource reports itself to be non-functional, release it
            
            // if the resource isn't functional, discard it (condition above)
            boolean shouldRelease = false;
            try {
                shouldRelease = !inResource.isFunctional();
            } catch (Throwable t) {
                if(LoggerAdapter.isDebugEnabled(this)) {
                    LoggerAdapter.debug("Resource " + inResource + " LOGIN_FAILED when trying to report its state, releasing", 
                                        t, 
                                        this);
                }
                shouldRelease = true;
            }
            // before returning the resource to the pool, make sure the subclass doesn't mind
            try {
                verifyResourceReturn(inResource);
            } catch (Throwable t) {
                if(LoggerAdapter.isDebugEnabled(this)) {
                    LoggerAdapter.debug("The resource pool decided not to allow resource " + inResource + " back in the pool, releasing",
                                        t,
                                        this);
                }
                shouldRelease = true;
            }
            // determined if the resource is functional or not, release if necessary
            if(shouldRelease) {
                if(LoggerAdapter.isDebugEnabled(this)) {
                    LoggerAdapter.debug("Resource " + inResource + " is non-functional, releasing", 
                                        this);
                }
                try {
                    releaseResource(inResource);
                    return;
                } catch (ResourcePoolException e) {
                    if(LoggerAdapter.isDebugEnabled(this)) {
                        LoggerAdapter.debug("Resource " + inResource + " threw an exception while releasing, continuing",
                                            e,
                                            this);
                    }
                    return;
                }
            }
        
            // no complaints?  fine, return the resource
            try {                
                addResourceToPool(inResource);
                inResource.returned();
            } catch (Throwable t) {
                // it is unknown whether the resource was actually added back to the pool or not
                // scenario #1 - the resource was successfully added back to the pool, should be marked as "returned"
                // scenario #2 - the the resource was *not* successfully added back to the pool, should be marked as "released"
                try {
                    if(poolContains(inResource)) {
                        if(LoggerAdapter.isDebugEnabled(this)) {
                            LoggerAdapter.debug("There was a problem adding " + inResource + 
                                                " back to the pool, but the resource is back in the pool, continuing",
                                                t,
                                                this);
                        }
                        inResource.returned();
                    } else {
                        if(LoggerAdapter.isDebugEnabled(this)) {
                            LoggerAdapter.debug("There was a problem adding " + inResource + 
                                                " back to the pool, and the resource is not back in the pool, releasing the resource and continuing",
                                                t,
                                                this);
                        }
                        inResource.released();
                    }
                } catch (Throwable t1) {
                    if(LoggerAdapter.isDebugEnabled(this)) {
                        LoggerAdapter.debug("There was a problem returning or releasing " + inResource + 
                                            ", continuing",
                                            t1,
                                            this);
                    }
                }
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
        try {
            inResource.released();
        } catch (Throwable t) {
            throw new ReleasedResourceException(t);
        }
    }
    
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
     * after the completion of the constructor.  It is strongly suggested
     * this this object also be <code>final</code>.
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
     * Removes the next <code>Resource</code> from the pool and
     * returns it for allocation.
     * 
     * <p>The <code>Resource</code> returned should be the next
     * <code>Resource</code> as defined by the subclass implementation.
     * 
     * @param inData an <code>Object</code> value used to decide what resource to allocate
     * @return a <code>Resource</code> value
     * @throws ResourcePoolException if the <code>Resource</code> cannot be retrieved 
     */
    protected abstract Resource getNextResource(Object inData) 
        throws ResourcePoolException;
    
    /**
     * Adds a <code>Resource</code> to the pool.
     * 
     * <p>This method will be called when a new <code>Resource</code> is created
     * and when a <code>Resource</code> is returned to the pool.  The subclass
     * should add the <code>Resource</code> in a manner that is consistent with
     * its implementation (e.g. FIFO, LIFO, etc). 
     * 
     * @param inResource a <code>Resource</code> value
     */
    protected abstract void addResourceToPool(Resource inResource);
    
    /**
     * Create a new <code>Resource</code>.
     * 
     * <p>The <code>inData</code> object may be used to advise the creation of the
     * <code>Resource</code>.  This object may be null.
     * 
     * @param inData an <code>Object</code> value or null
     * @return a <code>Resource</code> value
     * @throws ResourcePoolException if the <code>Resource</code> could not be created
     */
    protected abstract Resource createResource(Object inData)
        throws ResourcePoolException;

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

    /**
     * Determines if a given <code>Resource</code> is in the pool.
     * 
     * <p>It is incumbent on the subclass to implement this method
     * as efficiently as possible, ideally, O(c).
     * 
     * @param inResource a <code>Resource</code> value
     * @return a <code>boolean</code> value
     */
    protected abstract boolean poolContains(Resource inResource);

    /**
     * Called when a <code>Resource</code> is being returned to the pool.
     * 
     * <p>The default implementation of this method is to make sure that the <code>Resource</code>
     * returned isn't already in the pool.  Subclasses may provide their own implementation.  If this
     * method throws an exception, the <code>Resource</code> will be discarded.
     * 
     * @param inResource a <code>Resource</code> value being returned to the pool
     * @throws ResourcePoolException if the <code>Resource</code> being returned should be discarded instead.
     */
    protected void verifyResourceReturn(Resource inResource)
            throws ResourcePoolException
    {
        if(inResource == null) {
            throw new NullPointerException();
        }
        try {
            if(poolContains(inResource)) {
                throw new DuplicateResourceReturnException(MessageKey.ERROR_RESOURCE_POOL_RESOURCE_ALREADY_RETURNED);
            }
        } catch (ResourcePoolException e) {
            throw e;
        } catch (Throwable t) {
            throw new ResourcePoolException(t);
        }
    }
}
