package org.marketcetera.core.resourcepool;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import org.marketcetera.core.LoggerAdapter;
import org.marketcetera.core.MessageKey;

/**
 * An implementation of {@link ResourcePool} that allows callers to
 * request a specific {@link Resource}.
 *
 * <p>If the caller wants a particular <code>Resource</code>, the call to {@link #getResource(Object)}
 * will return that <code>Resource</code> if it's available.  If the <code>Resource</code> exists, but
 * is not available, i.e., it has been allocated to someone else, the call will block until the
 * <code>Resource</code> becomes available (note the block does not preclude some else from returning
 * that resource).  If the <code>Resource</code> does not exist, the pool will try to add it.  If the
 * <code>Resource</code> can not be added due pool max size, an exception is thrown.  Otherwise, the
 * desired <code>Resource</code> will be allocated.
 * 
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: $
 * @since 0.43-SNAPSHOT
 */
public abstract class ReservationResourcePool
        extends ResourcePool
{
    /**
     * a collection of all existing resources, either ALLOCATED or READY, keyed
     * by reservation credentials
     */
    private Hashtable<Object,ResourceWrapper> mResourceReservations;
    /**
     * Collection of pool resources
     */
    private LinkedHashSet<Resource> mResources;

    /**
     * Create a new <code>ReservationResourcePool</code> object.
     */
    public ReservationResourcePool()
    {
        super();
        mResourceReservations = new Hashtable<Object,ResourceWrapper>();
        mResources = new LinkedHashSet<Resource>();
    }

    /**
     * Derives a set of reservation credentials from the given <code>Resource</code>.
     * 
     * <p>The reservation credentials returned will be used to identify the
     * <code>Resource</code>.  Future requests for <code>Resource</code> allocation
     * that refer to the same credentials will be interpretted as a request for
     * the same <code>Resource</code>.  This method must not return <code>null</code>
     * and must not throw an exception.  In other words, it <em>must</em> return a
     * value or it's possible that blocked requesters will never be given their
     * <code>Resource</code>.
     * 
     * @param inResource a <code>Resource</code> value
     * @return an <code>Object</code> value
     */
    protected abstract Object renderReservationKey(Resource inResource);
    
    protected Object getReservationKeyFromResource(Resource inResource)
    {
        Object key = null;
        try {
            key = renderReservationKey(inResource);
        } catch (Throwable t) {
            if(LoggerAdapter.isDebugEnabled(this)) { 
                LoggerAdapter.debug("Exception thrown while rendering reservation key from resource " + inResource,
                                    t,
                                    this); 
            }
        }
        if(key == null) {
            if(LoggerAdapter.isDebugEnabled(this)) { 
                LoggerAdapter.debug("Attempting inefficient manual traverse for key",
                                    this); 
            }
            key = manualSearchForResourceWrapper(inResource);
        }
        
        if(LoggerAdapter.isDebugEnabled(this)) { 
            LoggerAdapter.debug("Key render returns " + key + " for resource " + inResource,
                                this); 
        }
        return key;
    }
    
    protected void addResource(Resource inResource) 
        throws ResourcePoolException
    {
        if(LoggerAdapter.isDebugEnabled(this)) { 
            LoggerAdapter.debug("Adding resource " + inResource + " to " + this,
                                this); 
        }
        // determine the key for the reservation
        Object key = null;
        // the resource is used to determine the key for the reservation table
        key = getReservationKeyFromResource(inResource);
        // the subclass is required to return a non-null value - if it does return null,
        //  give up and throw an exception
        if(key == null) {
            // baaad, this must not be null
            if(LoggerAdapter.isDebugEnabled(this)) { 
                LoggerAdapter.debug("The reservation key must not be null - this is an error condition",
                                    this); 
            }
            throw new ResourcePoolException(new NullPointerException());
        }
        if(LoggerAdapter.isDebugEnabled(this)) { 
            LoggerAdapter.debug("Adding " + inResource + " to the reservation collection keyed by " + key,
                                this); 
        }
        // add the resource to the pool
        atomicAddResourceToPool(key,
                                inResource);
    }
    
    /**
     * Adds the given <code>Resource</code> to the pool and reservation map.
     * 
     * @param inKey an <code>Object</code> value
     * @param inResource a <code>Resource</code> value
     */
    protected void atomicAddResourceToPool(Object inKey,
                                           Resource inResource)
    {
        synchronized(getPoolLock()) {
            mResourceReservations.put(inKey,
                                      new ResourceWrapper(inResource));
            mResources.add(inResource);
        }
    }

    protected Resource atomicRemoveNextResourceFromPool()
        throws NoSuchElementException
    {
        synchronized(getPoolLock()) {
            Resource r = mResources.iterator().next();
            mResources.remove(r);
            return r;
        }
    }
    
    protected void atomicRemoveResourceFromPool(Resource inResource)
    {
        synchronized(getPoolLock()) {
            mResources.remove(inResource);            
        }
    }
    
    protected boolean poolContains(Resource inResource)
    {
        synchronized(getPoolLock()) {
            return mResources.contains(inResource);
        }
    }
    
    protected Resource getNextResource(Object inData) 
        throws ResourcePoolException 
    {
        // if the incoming object is null, no specific reservation is requested (a "walkup")
        // issue any old resource, no guarantees about order
        if(inData == null) {
            if(LoggerAdapter.isDebugEnabled(this)) { 
                LoggerAdapter.debug("ReservationResourePool received a request for a resource w/o a reservation, issuing next (random) resource",
                                    this); 
            }
            try {
                return atomicRemoveNextResourceFromPool();
            } catch (NoSuchElementException e) {
                throw new ResourcePoolException(e);
            }
        }
        // the incoming object is non-null, so a specific resource was requested
        // check for the reservation in the "big book" of resources
        Resource desiredResource = lookupReservation(inData);
        // if desiredResource is non-null, then the resource exists, though
        //  we don't yet know whether it's READY (in the pool) or ALLOCATED (in the wild)
        if(LoggerAdapter.isDebugEnabled(this)) { 
            LoggerAdapter.debug("ReservationResourePool received a request for a resource with a reservation: " + inData + " found resource: " + desiredResource,
                                this); 
        }
        if(desiredResource == null) {
            // the desired resource does not yet exist
            // if there is capacity in the pool, create a new resource that fits this
            //  reservation and block for it
            if(LoggerAdapter.isDebugEnabled(this)) { 
                LoggerAdapter.debug("There is no matching resource for the reservation, trying to add a new resource to match",
                                    this); 
            }
            if(getCurrentPoolSize() < getMaxResources()) {
                if(LoggerAdapter.isDebugEnabled(this)) { 
                    LoggerAdapter.debug("The pool is currently below max, so adding the new resource to match the reservation",
                                        this); 
                }
                // there is capacity to add this resource, try to add it
                createNewResource(inData);
                // the resource has been added, now retrieve it
                return getNextResource(inData);
            } else {
                // there is no capacity to add this resource
                // the situation, then, is that the caller demands a specific resource
                //  but there is no capacity to create it.  throw an exception.
                if(LoggerAdapter.isDebugEnabled(this)) { 
                    LoggerAdapter.debug("The pool is currently at max, so can't create a new resource, can't fill the reservation",
                                        this); 
                }
                throw new ResourceCreationException(MessageKey.ERROR_CANNOT_CREATE_RESOURCE_FOR_RESERVATION);
            }
        } else {
            // the desired resource is non-null, which means it exists
            // the resource may be either READY, meaning it is in the queue, or ALLOCATED, meaining it is in the wild
            // the resource itself doesn't have this information, but the resource pool superclass does
            if(LoggerAdapter.isDebugEnabled(this)) { 
                LoggerAdapter.debug("A resource matching the reservation already exists, either in the pool or in the wild",
                                    this); 
            }
            if(poolContains(desiredResource)) {
                // the resource is in READY state
                // remove the desired resource from the resource pool
                if(LoggerAdapter.isDebugEnabled(this)) { 
                    LoggerAdapter.debug("The resource exists in the pool, this is the easy case",
                                        this); 
                }
                atomicRemoveResourceFromPool(desiredResource);
                // return the read resource
                if(LoggerAdapter.isDebugEnabled(this)) { 
                    LoggerAdapter.debug("Returning resource " + desiredResource + " to match reservation " + inData,
                                        this); 
                }
                return desiredResource;
            } else {
                // the resource is at large in the wild
                if(LoggerAdapter.isDebugEnabled(this)) { 
                    LoggerAdapter.debug("The resource exists, but is currently allocated to someone else, we'll wait a bit and check again",
                                        this); 
                }
                resourceContention(inData,
                                   desiredResource);
                // throw a special exception that indicates to the parent pool that the resource wasn't allocated
                //  but that the process should be repeated until the resource is available
                throw new ReservationForAllocatedResourceException("");
            }
        }
    }
    
    /**
     * This method is executed when a specific {@link Resource} is requested by reservation but the {@link Resource}
     * is already allocated.
     * 
     * <p>Upon completion of this method, the request for the resource will be re-triggered.  This behavior
     * will continue until the {@link Resource} is available.
     * 
     * <p>Subclasses should override this method if special handling is desired for this scenario.  For example,
     * this method could be used to count the number of times a reservation resource will be re-requested before
     * giving up (throwing an exception).  The default behavior is to continue asking until the request is honored
     * or the pool is shut down.
     *   
     * @param inData an <code>Object</code> value
     * @param inDesiredResource a <code>Resource</code> value
     * @throws ResourcePoolException to prevent triggering the resource re-request
     */
    protected void resourceContention(Object inData,
                                      Resource inDesiredResource)
        throws ResourcePoolException
    {
    }
    
    /**
     * Returns the <code>Resource</code> associated with the key.
     * 
     * @param inData a <code>Resource</code> value or null if no <code>Resource</code> is associated with
     * the given key
     * @return a <code>Resource</code> value or null
     */
    protected Resource lookupReservation(Object inData)
    {
        ResourceWrapper r = mResourceReservations.get(inData);
        return (r == null ? null : r.getResource());
    }

    protected void releaseResource(Resource inResource)
            throws ReleasedResourceException
    {
        synchronized(mResourceReservations) {
            Object key = getReservationKeyFromResource(inResource);
            if(key == null) {
                if(LoggerAdapter.isDebugEnabled(this)) {
                    LoggerAdapter.debug("Failed to remove reservation from reservation resource pool because renderKey returned null - renderKey must not return null",
                                        this);
                }
                throw new NullPointerException();
            }
            mResourceReservations.remove(key);
            super.releaseResource(inResource);
        }
    }
    
    void dumpReservationTable()
    {
        if(LoggerAdapter.isDebugEnabled(this)) {
            synchronized(mResourceReservations) {                
                LoggerAdapter.debug((mResourceReservations.size() != 0 ? "Reservation table contains:" : "Reservation table is empty"),
                                    this);
                for(Object key : mResourceReservations.keySet()) {
                    LoggerAdapter.debug(key + " " + mResourceReservations.get(key),
                                        this);
                }
            }
        }
    }

    protected int getCurrentPoolSize()
    {
        synchronized(getPoolLock()) {
            return mResources.size();
        }
    }

    protected Iterator<Resource> getPoolIterator()
    {
        return mResources.iterator();
    }

    protected Object getPoolLock()
    {
        return mResources;
    }

    /* (non-Javadoc)
     * @see org.marketcetera.core.resourcepool.ResourcePool#checkResourcePoolConfiguration()
     */
    protected void checkResourcePoolConfiguration()
            throws ResourcePoolConfigurationException
    {
        // a ReservationResourcePool needs to have its max set to min +1 (the parent requirement is max >= min)
        // this is to address the scenario where the pool is below minimum (only possible before first request), 
        //  the maximum = minimum, and a request comes in for a request with a reservation.  the pool behavior
        //  is to add resources up to minimum, then add a new resource specifically for the reservation.  rather
        //  than crap out and demand the configuration be changed on the first try, just check for this condition
        //  on startup
        if(getMaxResources() == getMinResources()) {
            throw new ResourcePoolConfigurationException(MessageKey.ERROR_RESOURCE_POOL_RESERVATION_RESOURCE_MAXIMUM_CONFIGURATION);
        }
        super.checkResourcePoolConfiguration();
    }

    /* (non-Javadoc)
     * @see org.marketcetera.core.resourcepool.ResourcePool#getResource(java.lang.Object)
     */
    public Resource getResource(Object inData)
            throws ResourcePoolException
    {
        // the purpose of overriding this method is to allow the ReservationPool to catch the
        //  case where a resource is requested that is allocated to someone else
        while(true) {
            try {
                return super.getResource(inData);
            } catch (ReservationForAllocatedResourceException e) {
                // this is thrown if a reservation for a resource cannot be honored because the
                //  resource is already allocated
                if(LoggerAdapter.isDebugEnabled(this)) {
                    LoggerAdapter.debug("ResourcePool received a request to try the allocation again...", 
                                        this);
                }
                // this thread is now outside the parent synchronization loop which means the resource pool
                //  is not locked.  this thread wants to notify the pool that it is waiting for a particular
                //  resource.  when that resource is returned to the pool, this thread will be notified
                //  to proceed with its reservation.
                try {
                    return processContention(inData);
                } catch (ResourcePoolException e2) {
                    throw e2;
                } catch (Throwable t) {
                    throw new NoResourceException(MessageKey.ERROR_RESOURCE_POOL_COULD_NOT_ALLOCATE_NEW_RESOURCE,
                                                  t);
                }
            } catch (ResourcePoolException e2) {
                throw e2;
            }
        }
    }
    
    /**
     * This method is called when the current request is for an allocated {@link Resource}.
     * 
     * <p>This method must handle the scenario where the current {@link Resource} request
     * is for a <code>Resource</code> that is allocated.  The default behavior is to wait
     * for the <code>Resource</code> to be returned.
     * 
     * @param inKey an <code>Object</code> value describing the reservation
     * @return a <code>Resource</code> value when the contention has been resolved
     * @throws InterruptedException if the thread is interrupted waiting for the resource
     * @throws ResourcePoolException if the resource cannot be allocated
     */
    protected Resource processContention(Object inKey)
        throws InterruptedException, ResourcePoolException
    {
        ResourceWrapper r = null;
        // the current thread is waiting for the resource indicated by the given key
        // this method is being executed *outside* the parent pool lock
        synchronized(mResourceReservations) {
            // add the current request (represented by the pool) to the queue of requesters for the same resource
            r = mResourceReservations.get(inKey);
            r.addRequester(this);
        }
        if(LoggerAdapter.isDebugEnabled(this)) {
            LoggerAdapter.debug("Thread " + Thread.currentThread() + " is waiting for the return of resource " + r.getResource(), 
                                this);
        }
        // back outside the synchronization loop for the reservation table
        // wait to be notified that the resource has been returned
        synchronized(this) {
            wait();
        }
        if(LoggerAdapter.isDebugEnabled(this)) {
            LoggerAdapter.debug(this + " has been woken up because of a change of status of resource " + r.getResource(), 
                                this);            
        }
        // we have been notified of a change in status, but we don't yet know if the resource was returned or if the reservation was cancelled
        
        // retrieve the resource from the reservation table (it never makes it to the formal pool, it's sort of hanging out in limbo)        
        ResourceWrapper resourceWrapper = null;
        synchronized(mResourceReservations) {
            resourceWrapper = mResourceReservations.get(inKey);
        }
        Resource resource = resourceWrapper.getResource();
        try {
            if(resourceWrapper.getCanceled()) {
                if(LoggerAdapter.isDebugEnabled(this)) {
                    LoggerAdapter.debug("Reservation by " + this + " for resource " + resource + " has been cancelled", 
                                        this);            
                }
                throw new NoResourceException(MessageKey.ERROR_RESOURCE_POOL_COULD_NOT_ALLOCATE_NEW_RESOURCE);
            } else {
                // resource is ready (and functional)
                resource.allocated(this);
                return resource;
            }
        } catch (Throwable e) {
            throw new NoResourceException(MessageKey.ERROR_RESOURCE_POOL_COULD_NOT_ALLOCATE_NEW_RESOURCE,
                                          e);
        }
    }
            
    /* (non-Javadoc)
     * @see org.marketcetera.core.resourcepool.ResourcePool#returnResource(org.marketcetera.core.resourcepool.Resource)
     */
    public void returnResource(Resource inResource)
            throws ResourcePoolException
    {
        // this code is outside of the parent pool lock
        synchronized(getPoolLock()) {
            try {
                if(LoggerAdapter.isDebugEnabled(this)) {
                    LoggerAdapter.debug(this + " intercepted request to return resource " + inResource, 
                                        this);
                }
                Object key = null;
                ResourceWrapper r = null;
                key = getReservationKeyFromResource(inResource);
                if(key == null) {
                    if(LoggerAdapter.isDebugEnabled(this)) {
                        LoggerAdapter.debug("No key match was found for the resource, giving up",
                                            this);
                    }
                    throw new ResourcePoolException(MessageKey.ERROR_CANNOT_RETURN_RESOURCE_TO_RESERVATION_POOL);
                }
                r = mResourceReservations.get(key);
                
                if(LoggerAdapter.isDebugEnabled(this)) {
                    LoggerAdapter.debug("ResourceWrapper for this resource: " + r, 
                                        this);
                }
                // if the resource is non-functional, must cancel all waiting reservations for it
                if(!inResource.isFunctional()) {
                    if(LoggerAdapter.isDebugEnabled(this)) {
                        LoggerAdapter.debug(inResource + " is non-functional, must cancel any waiting reservations", 
                                            this);
                    }
                    r.cancelReservation(this);
                    return;
                } else {
                    if(LoggerAdapter.isDebugEnabled(this)) {
                        LoggerAdapter.debug(inResource + " is functional, checking for waiting reservations for this resource", 
                                            this);
                    }
                    if(LoggerAdapter.isDebugEnabled(this)) {
                        LoggerAdapter.debug("ResourceWrapper for this resource: " + r, 
                                            this);
                    }
                    ReservationResourcePool requester = r.getNextRequester();
                    if(requester != null) {
                        inResource.returned(this);
                        if(LoggerAdapter.isDebugEnabled(this)) {
                            LoggerAdapter.debug("Notifying requester: " + requester + " of return of " + inResource, 
                                                this);
                        }
                        synchronized(requester) {
                            requester.notify();
                        }
                        if(LoggerAdapter.isDebugEnabled(this)) {
                            LoggerAdapter.debug("Done notifying requester, ending resource return process", 
                                                this);
                        }
                        return;
                    }
                }
                if(LoggerAdapter.isDebugEnabled(this)) {
                    LoggerAdapter.debug("Proceeding to normal return process", 
                                        this);
                }
            } catch (Throwable t) {
                throw new ResourcePoolException(t);
            }
        }
        super.returnResource(inResource);
    }
    
    /**
     * Searches for the reservation key for a <code>Resource</code>.
     * 
     * <p>This method should be used only as a last resort.  O notation for this algorithm
     * is O(n + n): you've been warned.
     * 
     * @param inResource a <code>Resource</code> value
     * @return an <code>Object</code> value containing the key that matches the given <code>Resource</code> or null if no match was found
     */
    private Object manualSearchForResourceWrapper(Resource inResource)
    {
        if(LoggerAdapter.isDebugEnabled(this)) {
            LoggerAdapter.debug("Executing manual reservation traverse for resource " + inResource, 
                                this);
        }
        synchronized(mResourceReservations) {
            for(Object key : mResourceReservations.keySet()) {
                if(mResourceReservations.get(key).equals(inResource)) {
                    if(LoggerAdapter.isDebugEnabled(this)) {
                        LoggerAdapter.debug("Manual reservation traverse for resource " + inResource + " yielded key " + key, 
                                            this);
                    }
                    return key;
                }
            }
        }
        if(LoggerAdapter.isDebugEnabled(this)) {
            LoggerAdapter.debug("Manual reservation traverse for resource " + inResource + " failed, giving up", 
                                this);
        }
        return null;
    }
    
    /**
     * Encapsulates a <code>Resource</code> and all waiting reservations for that <code>Resource</code>.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id: $
     * @since 0.43-SNAPSHOT
     */
    private static class ResourceWrapper
    {
        /**
         * the resource for which reservations are stored
         */
    	private Resource mResource;
        /**
         * FIFO queue of requesters for this resource
         */
    	private LinkedList<ReservationResourcePool> mRequesters;
        /**
         * indicates if reservations should be canceled upon notify
         */
        private boolean mCanceled;
    	
        /**
         * Creates a new <code>ResourceWrapper</code> instance.
         *
         * @param inResource a <code>Resource</code> value
         */
        private ResourceWrapper(Resource inResource)
    	{
    		setResource(inResource);
            setCanceled(false);
    	}

        /**
         * Adds a new reservation for the <code>Resource</code>.
         * 
         * @param inRequester a <code>ReservationResourcePool</code> value
         */
        private synchronized void addRequester(ReservationResourcePool inRequester)
        {
            if(mRequesters == null) {
                mRequesters = new LinkedList<ReservationResourcePool>();
            }
            mRequesters.addLast(inRequester);
        }
        
        /**
         * Returns the next requester for the <code>Resource</code>
         * 
         * @return a <code>ReservationResourcePool</code> value or null if no requesters are waiting for this resource
         */
        private synchronized ReservationResourcePool getNextRequester()
        {
            if(mRequesters == null ||
               mRequesters.size() == 0) {
                return null;
            }
            return mRequesters.removeFirst();
        }
        
        /**
         * Gets <code>Resource</code> for which reservations are stored.
         * 
         * @return a <code>Resource</code> value
         */
        private Resource getResource()
        {
            return mResource;
        }

        /**
         * Sets the <code>Resource</code> for which reservations are stored.
         * 
         * @param inResource a <code>Resource</code> value
         */
        private void setResource(Resource inResource)
        {
            mResource = inResource;
        }

        public boolean equals(Object inObj)
        {
            return getResource().equals(inObj);
        }

        public int hashCode()
        {
            return getResource().hashCode();
        }
        
        public String toString()
        {
            StringBuffer output = new StringBuffer();
            output.append(getResource().toString()).append(" with ").append((mRequesters == null ? "0" : mRequesters.size())).append(" requesters");
            return output.toString();
        }

        /**
         * Cancels all reservations waiting for this <code>Resource</code>.
         * 
         * @param inCanceller a <code>ReservationResourcePool</code> value which initiates the cancel
         * @throws Throwable if an error occurs while cancelling reservations
         */
        private synchronized void cancelReservation(ReservationResourcePool inCanceller) 
            throws Throwable
        {
            if(LoggerAdapter.isDebugEnabled(this)) {
                LoggerAdapter.debug("cancelling all reservations for " + getResource(), 
                                    this);
            }
            setCanceled(true);
            if(mRequesters != null) {
                for(ReservationResourcePool r : mRequesters) {
                    if(LoggerAdapter.isDebugEnabled(this)) {
                        LoggerAdapter.debug("Notifying " + r + " of cancellation", 
                                            this);
                    }
                    synchronized(r) {
                        r.notify();
                    }
                }
            }
            if(LoggerAdapter.isDebugEnabled(this)) {
                LoggerAdapter.debug("Releasing resource " + getResource(), 
                                    this);
            }
            inCanceller.releaseResource(getResource());
        }

        /**
         * Indicates if this reservation has been cancelled.
         * 
         * @return a <code>boolean</code> value
         */
        private boolean getCanceled()
        {
            return mCanceled;
        }

        /**
         * Indicates if this reservation has been cancelled.
         * 
         * @param inCanceled a <code>boolean</code> value
         */
        private void setCanceled(boolean inCanceled)
        {
            mCanceled = inCanceled;
        }
    }
}
