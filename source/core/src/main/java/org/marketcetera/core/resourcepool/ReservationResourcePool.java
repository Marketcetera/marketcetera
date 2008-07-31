package org.marketcetera.core.resourcepool;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import org.marketcetera.core.Messages;
import org.marketcetera.util.log.SLF4JLoggerProxy;

/**
 * An implementation of {@link ResourcePool} that allows callers to
 * request a specific {@link Resource}.
 *
 * <p>If the caller wants a particular <code>Resource</code>, the call to {@link #requestResource(Object)}
 * will return that <code>Resource</code> if it's available.  If the <code>Resource</code> exists, but
 * is not available, i.e., it has been allocated to someone else, the call will block until the
 * <code>Resource</code> becomes available (note the block does not preclude some else from returning
 * that resource).  If the <code>Resource</code> does not exist, the pool will try to add it.  If the
 * <code>Resource</code> can not be added due pool max size, an exception is thrown.  Otherwise, the
 * desired <code>Resource</code> will be allocated.
 * 
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.43-SNAPSHOT
 */
public abstract class ReservationResourcePool
        extends ResourcePool
{
    /**
     * a collection of all existing resources, either ALLOCATED or READY, keyed
     * by reservation credentials
     */
    private final Hashtable<Object,ReservationEntry> mResourceReservations;
    /**
     * Collection of pool resources
     */
    private final LinkedHashSet<Resource> mResources;

    /**
     * Create a new <code>ReservationResourcePool</code> object.
     */
    public ReservationResourcePool()
    {
        super();
        mResourceReservations = new Hashtable<Object,ReservationEntry>();
        mResources = new LinkedHashSet<Resource>();
    }
    
    /**
     * Uniquely describe the given <code>Resource</code>.
     * 
     * <p>This method provides a mechanism to uniquely refer to the
     * given <code>Resource</code>. The value returned by this method
     * must be constructed such that the following is true:
     * <pre>
     *     Object key1 = renderReservationKey(resource1);
     *     Object key2 = renderReservationKey(resource2)); 
     *     if(resource1.equals(resource2)) {
     *         assert(key1.equals(key2));
     *         assert(key2.equals(key1));
     *         assert(key1.hashCode() == key2.hashCode());
     *     } else {
     *         assert(!key1.equals(key2));
     *         assert(!key2.equals(key1));
     *         assert(key1.hashCode() != key2.hashCode());
     *     }
     * </pre>
     * 
     * @param inResource a <code>Resource</code> value
     * @return an <code>Object</code> value
     */
    protected abstract Object renderReservationKey(Resource inResource);

    protected void addResourceToPool(Resource inResource)
    {
        SLF4JLoggerProxy.debug(this, "Adding {} to resource pool", inResource); //$NON-NLS-1$

        if(inResource == null) {
            throw new NullPointerException();
        }
        dumpReservationTable();
        // this method is called when either a resource is added or when a resource is returned
        // in either case, we want to add the resource to the resource container
        mResources.add(inResource);
        // we also want to check if the resource exists in the reservation book.  if it does,
        //  that means we are processing a return - do nothing
        // if it doesn't, that means we are processing an add - add a reservation to the book
        Object key = getReservationKey(inResource);
        if(key == null) {
            SLF4JLoggerProxy.debug(this, "Unable to determine reservation key for {}, can't add it to the pool", inResource); //$NON-NLS-1$

            throw new NullPointerException();
        }
        SLF4JLoggerProxy.debug(this, "Resource yields reservation key: {}", key); //$NON-NLS-1$
        if(reservationExistsFor(key)) {
            SLF4JLoggerProxy.debug(this, "Reservation exists"); //$NON-NLS-1$
        } else {
            SLF4JLoggerProxy.debug(this, "Reservation does not yet exist, adding it to the book"); //$NON-NLS-1$
            // this is a new add
            addToReservationBook(key,
                                 inResource);
        }    
    }
    
    /**
     * Determines if the registration book contains an entry for the given key.
     * 
     * @param inKey an <code>Object</code> value
     * @return a <code>boolean</code> value
     */
    protected boolean reservationExistsFor(Object inKey)
    {
        if(inKey == null) {
            return false;
        }
        synchronized(mResourceReservations) {
            return mResourceReservations.containsKey(inKey);
        }
    }
    
    /**
     * Returns the reservation entry for the given key.
     * 
     * @param inKey an <code>Object</code> value
     * @return a <code>ReservationEntry</code> value or null
     */
    protected ReservationEntry getReservationByKey(Object inKey)
    {
        if(inKey == null) {
            return null;
        }
        synchronized(mResourceReservations) {
            return mResourceReservations.get(inKey);
        }
    }
    
    /**
     * Adds the given <code>Resource</code> to the reservation book, keyed by the given key.
     * 
     * @param inKey an <code>Object</code> value
     * @param inResource a <code>Resource</code> value
     */
    protected void addToReservationBook(Object inKey,
                                        Resource inResource)
    {
        if(inKey == null || 
           inResource == null) {
            throw new NullPointerException();
        }
        synchronized(mResourceReservations) {
            ReservationEntry wrapper = new ReservationEntry(inResource);
            mResourceReservations.put(inKey, 
                                      wrapper);
        }
    }

    /**
     * Determines the key for the given <code>Resource</code>.
     * 
     * <p>This method relies on {@link #renderReservationKey(Resource)} to produce the
     * key value.  If, for some reason, {@link #renderReservationKey(Resource)} method 
     * returns null or throws an exception, neither of which it should do, this method
     * will then call {@link #manualSearchForResourceWrapper(Resource)}, which can be
     * very expensive.  Every effort must be made to make sure that {@link #renderReservationKey(Resource)}
     * returns a non-null value and never throws an exception.
     * 
     * @param inResource a <code>Resource</code> value
     * @return an <code>Object</code> value
     */
    protected Object getReservationKey(Resource inResource)
    {
        Object key = null;
        try {
            key = renderReservationKey(inResource);
        } catch (Throwable t) {
            SLF4JLoggerProxy.debug(this, t, "Exception thrown while rendering reservation key from resource {}", inResource); //$NON-NLS-1$

        }
        if(key == null) {
            SLF4JLoggerProxy.debug(this, "Attempting inefficient manual traverse for key"); //$NON-NLS-1$
            key = manualSearchForResourceWrapper(inResource);
        }

        SLF4JLoggerProxy.debug(this, "Key render returns {} for resource {}", key, inResource); //$NON-NLS-1$
        return key;
    }

    /**
     * Searches for the reservation key for a <code>Resource</code>.
     * 
     * <p>This method should be used only as a last resort.  Worst-case performance for this algorithm
     * is O(n + (n * c)): you've been warned.
     * 
     * @param inResource a <code>Resource</code> value
     * @return an <code>Object</code> value containing the key that matches the given <code>Resource</code> or null if no match was found
     */
    private Object manualSearchForResourceWrapper(Resource inResource)
    {
        SLF4JLoggerProxy.debug(this, "Executing manual reservation traverse for resource {}", inResource); //$NON-NLS-1$

        synchronized(mResourceReservations) {
            for(Object key : mResourceReservations.keySet()) {
                if(mResourceReservations.get(key).equals(inResource)) {
                    SLF4JLoggerProxy.debug(this, "Manual reservation traverse for resource {} yielded key {}", inResource, key); //$NON-NLS-1$
                    return key;
                }
            }
        }
        SLF4JLoggerProxy.debug(this, "Manual reservation traverse for resource {} LOGIN_FAILED, giving up", inResource); //$NON-NLS-1$
        return null;
    }

    protected Resource getNextResource(Object inData)
            throws ResourcePoolException
    {
        SLF4JLoggerProxy.debug(this, "{} received request for resource with the following credentials: {}", this, inData); //$NON-NLS-1$
        // there are two kinds of resource requests we can process here:
        //  1) inData is null - this is a request for "any old resource"
        //  2) inData is non-null - this is a request for the specific resource implied by inData

        Resource desiredResource = null;

        try {
            if(inData == null) {
                SLF4JLoggerProxy.debug(this, "Credentials are null, assigning next available resource"); //$NON-NLS-1$

                // this is a request for any resource
                if(mResources.isEmpty()) {
                    SLF4JLoggerProxy.debug(this, "No resources available, trying to create a new one to assign"); //$NON-NLS-1$
                    // if the pool is empty, there are no resources to get so we will have to add one
                    desiredResource = createResource(inData);
                    // make sure we got a valid resource back
                    if(desiredResource == null) {
                        throw new ResourcePoolException(Messages.ERROR_RESOURCE_POOL_COULD_NOT_ALLOCATE_NEW_RESOURCE);
                    }
                    // add the new resource to the system
                    addResourceToPool(desiredResource);
                }
                desiredResource = allocateResource();
                SLF4JLoggerProxy.debug(this, "Returning resource: {}", desiredResource); //$NON-NLS-1$
                return desiredResource;
            }
            // this is a request for a specific resource
            // the criteria passed are non-null, which means this is a request for specific resource
            // the resource may or may not exist at this point
            // look up the reservation implied by the passed object
            ReservationEntry reservationEntry = getReservationByKey(inData);
            SLF4JLoggerProxy.debug(this, "A specific resource has been requested, a search for a reservationEntry yielded: {}", reservationEntry); //$NON-NLS-1$
            if(reservationEntry == null) {
                SLF4JLoggerProxy.debug(this, "No reservationEntry exists for these credentials, creating a new resource to match"); //$NON-NLS-1$
                // a specific resource has been requested, but a reservation entry for that resource does not
                //  exist.  this implies that the resource does not exist.
                // try to create a resource that matches the resource implied by the passed parameter.
                desiredResource = createResource(inData);
                // make sure we got a valid resource back
                if(desiredResource == null) {
                    throw new ResourcePoolException(Messages.ERROR_RESOURCE_POOL_COULD_NOT_ALLOCATE_NEW_RESOURCE);
                }
                // the resource should match the given criteria, but that's up to the subclass to determine
                addResourceToPool(desiredResource);
                // jump back into this method now that the reservation should be able to be honored
                SLF4JLoggerProxy.debug(this, "About to recurse to find the newly created resource..."); //$NON-NLS-1$
                return getNextResource(inData);
            }
            // a specific resource has been requested and a reservation entry for that resource exists
            desiredResource = reservationEntry.getResource();
            // the last question left to answer is whether that resources exists in the pool (READY) or
            //  outside (ALLOCATED)
            if(poolContains(desiredResource)) {
                // the reservation exists in the pool (READY)
                return allocateResource(desiredResource);
            }
            // the reservation exists, but the reservation is ALLOCATED - this is the most complicated
            //  case.  we have to wait until the resource becomes available, which is fine, but we have
            //  to wait *outside* the parent's synchronization lock or the resource could never be
            //  returned
            return resourceContention(reservationEntry);
        } catch (ReservationForAllocatedResourceException e) {
            throw e;
        } catch (Throwable t) {
            // we can't control what createResource or addResourceToPool will do - they're not supposed to throw
            //  an exception, but we need to handle the case where they do
            // if a resource was allocated, release it
            if(desiredResource != null) {
                releaseResource(desiredResource);
            }
            // bail on the resource retrieval
            if(t instanceof ResourcePoolException) {
                throw (ResourcePoolException)t;
            }
            throw new ResourcePoolException(t,
                                            Messages.ERROR_RESOURCE_POOL_COULD_NOT_ALLOCATE_NEW_RESOURCE);
        }        
    }
    
    /**
     * Called when the given <code>Resource</code> is requested but it is already allocated.
     * 
     * <p>Subclasses should be <strong>very</strong> careful when overriding this method.  If the
     * parent method (this method) is not called, and its exception not allowed to be thrown, then
     * resource contention may not be handled properly.
     * 
     * @param inReservationEntry a <code>ReservationEntry</code> value
     * @return a <code>Resource</code> value containing the <code>Resource</code> to return
     * @throws ResourcePoolException
     */
    protected Resource resourceContention(ReservationEntry inReservationEntry) 
        throws ResourcePoolException
    {
        // add ourselves in the queue waiting for this resource
        inReservationEntry.addRequester(this);
        // signal that we're waiting on a blocked resource
        throw new ReservationForAllocatedResourceException(inReservationEntry.getResource());
    }
    
    /* (non-Javadoc)
     * @see org.marketcetera.core.resourcepool.ResourcePool#requestResource(java.lang.Object)
     */
    protected Resource requestResource(Object inData)
            throws ResourcePoolException
    {
        // this method is overridden in order to allow the pool to process requests for ALLOCATED resources
        // the plan is to keep trying to allocate the resource until it is returned and our request is
        //  answered
        // this loop is *outside* the parent synchronization
        while(true) {
            try {
                return super.requestResource(inData);
            } catch (ReservationForAllocatedResourceException e) {
                // the current requester has been added to the reservationEntry queue for the resource
                // we should now wait until the resource comes back in (the only way out of the while
                //  loop is if we get the resource, the parent request throws an exception, or we're
                //  interrupted during the wait)
                SLF4JLoggerProxy.debug(this, "{} waiting for resource {}...", this, e.getResource()); //$NON-NLS-1$
                try {
                    synchronized(this) {
                        wait();
                    }
                    // woken up from waiting
                    Object key = getReservationKey(e.getResource());
                    ReservationEntry entry = getReservationByKey(key);
                    if(entry.getCanceled()) {
                        throw new ResourcePoolException(Messages.ERROR_RESOURCE_POOL_RESERVATION_CANCELLED);
                    }
                } catch (InterruptedException e1) {
                    throw new ResourcePoolException(e1);
                }
            }
        }
    }

    /**
     * Removes the given <code>Resource</code> from the pool to be assigned.
     * 
     * @param inResource a <code>Resource</code> value or null if the given <code>Resource</code>
     *   is not in the pool
     * @return a <code>Resource</code> value
     */
    protected Resource allocateResource(Resource inResource)
    {
        if(mResources.remove(inResource)) {
            return inResource;
        }
        return null;
    }
    
    /**
     * Removes the next <code>Resource</code> from the pool to be assigned.
     * 
     * <p>The <code>Resource</code> to be allocated is the next resource in FIFO order.
     * 
     * @return a <code>Resource</code> value
     */
    protected Resource allocateResource()
    {
        Iterator<Resource> iterator = mResources.iterator();
        Resource r = iterator.next();
        iterator.remove();
        return r;
    }

    protected Iterator<Resource> getPoolIterator()
    {
        return mResources.iterator();
    }

    protected Object getPoolLock()
    {
        return mResources;
    }

    protected boolean poolContains(Resource inResource)
    {
        return mResources.contains(inResource);
    }

    /* (non-Javadoc)
     * @see org.marketcetera.core.resourcepool.ResourcePool#releaseResource(org.marketcetera.core.resourcepool.Resource)
     */
    protected void releaseResource(Resource inResource)
            throws ReleasedResourceException
    {
        // a resource is about to be released - this happens if the resource is unusable        
        super.releaseResource(inResource);
        try {
            // notify any blocked waiters that their reservation has been cancelled
            Object key = getReservationKey(inResource);
            ReservationEntry reservation = getReservationByKey(key);
            if(reservation == null) {
                // this can occur if the resource goes bad before it's added to the
                //  reservation book.  in this case, just give up (there can't be
                //  anybody waiting for the reservation in this case).
                return;
            }
            reservation.cancelReservation(this);
        } catch (Throwable t) {
            throw new ReleasedResourceException(t);
        }
    }

    /* (non-Javadoc)
     * @see org.marketcetera.core.resourcepool.ResourcePool#returnResource(org.marketcetera.core.resourcepool.Resource)
     */
    protected void returnResource(Resource inResource)
            throws ResourcePoolException
    {
        // a resource is about to be returned
        super.returnResource(inResource);
        try {
            // find any reservations for this resource
            Object key = getReservationKey(inResource);
            ReservationEntry reservation = getReservationByKey(key);
            reservation.notifyNextRequester();
        } catch (Throwable t) {
            throw new ReleasedResourceException(t);
        }
    }

    void dumpReservationTable()
    {
        synchronized(mResourceReservations) {                
            SLF4JLoggerProxy.debug(this, mResourceReservations.size() != 0 ? "ReservationEntry table contains:" : "ReservationEntry table is empty"); //$NON-NLS-1$ //$NON-NLS-2$
            for(Object key : mResourceReservations.keySet()) {
                SLF4JLoggerProxy.debug(this, "{}({}) {}", key, key.hashCode(), mResourceReservations.get(key)); //$NON-NLS-1$
            }
        }
    }

    /**
     * Encapsulates a <code>Resource</code> and all waiting reservations for that <code>Resource</code>.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 0.43-SNAPSHOT
     */
    static class ReservationEntry
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
         * Creates a new <code>ReservationEntry</code> instance.
         *
         * @param inResource a <code>Resource</code> value
         */
        private ReservationEntry(Resource inResource)
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
        Resource getResource()
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
            output.append(getResource().toString()).append(" with ").append((mRequesters == null ? "0" : mRequesters.size())).append(" requesters"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
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
            SLF4JLoggerProxy.debug(this, "cancelling all reservations for {}", getResource()); //$NON-NLS-1$
            setCanceled(true);
            if(mRequesters != null) {
                for(ReservationResourcePool r : mRequesters) {
                    SLF4JLoggerProxy.debug(this, "Notifying {} of cancellation", r); //$NON-NLS-1$
                    synchronized(r) {
                        r.notify();
                    }
                }
            }
        }
        
        private void notifyNextRequester()
        {
            ReservationResourcePool requester = getNextRequester();
            if(requester != null) {
                synchronized(requester) {
                    requester.notify();
                }
            }
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
