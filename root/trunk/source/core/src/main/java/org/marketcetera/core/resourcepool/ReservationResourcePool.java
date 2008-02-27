package org.marketcetera.core.resourcepool;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import org.marketcetera.core.LoggerAdapter;

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
    
    protected abstract Object renderReservationKey(Resource inResource);

    protected void addResourceToPool(Resource inResource)
    {
        if(LoggerAdapter.isDebugEnabled(this)) { 
            LoggerAdapter.debug("Adding " + inResource + " to resource pool",
                                this); 
        }
        dumpReservationTable();
        // this method is called when either a resource is added or when a resource is returned
        // in either case, we want to add the resource to the resource container
        mResources.add(inResource);
        // we also want to check if the resource exists in the reservation book.  if it does,
        //  that means we are processing a return - do nothing
        // if it doesn't, that means we are processing an add - add a reservation to the book
        Object key = getReservationKey(inResource);
        if(LoggerAdapter.isDebugEnabled(this)) { 
            LoggerAdapter.debug("Resource yields reservation key: " + key,
                                this); 
        }
        if(reservationExistsFor(key)) {
            if(LoggerAdapter.isDebugEnabled(this)) { 
                LoggerAdapter.debug("Reservation exists",
                                    this); 
            }
        } else {
            if(LoggerAdapter.isDebugEnabled(this)) { 
                LoggerAdapter.debug("Reservation does not yet exist, adding it to the book",
                                    this); 
            }
            // this is a new add
            addToReservationBook(key,
                                 inResource);
        }    
    }
    
    protected boolean reservationExistsFor(Object inKey)
    {
        synchronized(mResourceReservations) {
            return mResourceReservations.containsKey(inKey);
        }
    }
    
    protected ReservationEntry getReservationByKey(Object inKey)
    {
        synchronized(mResourceReservations) {
            return mResourceReservations.get(inKey);
        }
    }
    
    protected void addToReservationBook(Object inKey,
                                        Resource inResource)
    {
        synchronized(mResourceReservations) {
            ReservationEntry wrapper = new ReservationEntry(inResource);
            mResourceReservations.put(inKey, 
                                      wrapper);
        }
    }

    protected Object getReservationKey(Resource inResource)
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

    /**
     * Searches for the reservation key for a <code>Resource</code>.
     * 
     * <p>This method should be used only as a last resort.  Worst-case performance for this algorithm
     * is O(n + (n * C)): you've been warned.
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

    protected Resource getNextResource(Object inData)
            throws ResourcePoolException
    {
        if(LoggerAdapter.isDebugEnabled(this)) { 
            LoggerAdapter.debug(this + " received request for resource with the following credentials: " + inData,
                                this); 
        }
        // there are two kinds of resource requests we can process here:
        //  1) inData is null - this is a request for "any old resource"
        //  2) inData is non-null - this is a request for the specific resource implied by inData
        
        Resource desiredResource = null;
        if(inData == null) {
            if(LoggerAdapter.isDebugEnabled(this)) { 
                LoggerAdapter.debug("Credentials are null, assigning next available resource",
                                    this); 
            }
            // this is a request for any resource
            if(mResources.isEmpty()) {
                if(LoggerAdapter.isDebugEnabled(this)) { 
                    LoggerAdapter.debug("No resources available, trying to create a new one to assign",
                                        this); 
                }
                // if the pool is empty, there are no resources to get so we will have to add one
                desiredResource = createResource(inData);
                // add the new resource to the system
                addResourceToPool(desiredResource);
            }
            desiredResource = allocateResource();
            if(LoggerAdapter.isDebugEnabled(this)) { 
                LoggerAdapter.debug("Returning resource: " + desiredResource,
                                    this); 
            }
            return desiredResource;
        }
        // this is a request for a specific resource
        // the criteria passed are non-null, which means this is a request for specific resource
        // the resource may or may not exist at this point
        // look up the reservation implied by the passed object
        ReservationEntry reservationEntry = getReservationByKey(inData);
        if(LoggerAdapter.isDebugEnabled(this)) { 
            LoggerAdapter.debug("A specific resource has been requested, a search for a reservationEntry yielded: " + reservationEntry,
                                this); 
        }
        if(reservationEntry == null) {
            if(LoggerAdapter.isDebugEnabled(this)) { 
                LoggerAdapter.debug("No reservationEntry exists for these credentials, creating a new resource to match",
                                    this); 
            }
            // a specific resource has been requested, but a reservation entry for that resource does not
            //  exist.  this implies that the resource does not exist.
            // try to create a resource that matches the resource implied by the passed parameter.
            desiredResource = createResource(inData);
            // the resource should match the given criteria, but that's up to the subclass to determine
            addResourceToPool(desiredResource);
            // jump back into this method now that the reservation should be able to be honored
            if(LoggerAdapter.isDebugEnabled(this)) { 
                LoggerAdapter.debug("About to recurse to find the newly created resource...",
                                    this); 
            }
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
        // add ourselves in the queue waiting for this resource
        reservationEntry.addRequester(this);
        // signal that we're waiting on a blocked resource
        throw new ReservationForAllocatedResourceException(desiredResource);
    }
    
    protected Resource allocateResource(Resource inResource)
    {
        mResources.remove(inResource);
        return inResource;
    }
    
    protected Resource allocateResource()
    {
        return mResources.iterator().next();
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

    protected void verifyResourceReturn(Resource inResource)
            throws ResourcePoolException
    {
        // duplicate resource?
    }

    void dumpReservationTable()
    {
        if(LoggerAdapter.isDebugEnabled(this)) {
            synchronized(mResourceReservations) {                
                LoggerAdapter.debug((mResourceReservations.size() != 0 ? "ReservationEntry table contains:" : "ReservationEntry table is empty"),
                                    this);
                for(Object key : mResourceReservations.keySet()) {
                    LoggerAdapter.debug(key + "(" + key.hashCode() + ") " + mResourceReservations.get(key),
                                        this);
                }
            }
        }
    }

    /**
     * Encapsulates a <code>Resource</code> and all waiting reservations for that <code>Resource</code>.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id: $
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
