package org.marketcetera.core.resourcepool;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashSet;
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
 * @todo TODO the requesters for a specific resource should queue in FIFO order.  As is currently implemented,
 * it's a toss-up as to who gets to allocate the resource upon its return.
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
    private Hashtable<Object,Resource> mResourceReservations;
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
        mResourceReservations = new Hashtable<Object,Resource>();
        mResources = new LinkedHashSet<Resource>();
    }

    /**
     * Derives a set of reservation credentials from the given <code>Resource</code>.
     * 
     * <p>The reservation credentials returned will be used to identify the
     * <code>Resource</code>.  Future requests for <code>Resource</code> allocation
     * that refer to the same credentials will be interpretted as a request for
     * the same <code>Resource</code>.  This method must not return <code>null</code>.
     * 
     * @param inResource a <code>Resource</code> value
     * @return an <code>Object</code> value
     */
    protected abstract Object renderReservationKey(Resource inResource);
    
    protected void addResource(Resource inResource) 
        throws ResourcePoolException
    {
        if(LoggerAdapter.isDebugEnabled(this)) { 
            LoggerAdapter.debug("Adding resource " + inResource + " to " + this,
                                this); 
        }
        // determine the key for the reservation
        Object key = null;
        try {
            // the resource is used to determine the key for the reservation table
            key = renderReservationKey(inResource);
            if(LoggerAdapter.isDebugEnabled(this)) { 
                LoggerAdapter.debug("Resource " + inResource + " maps to reservation key " + key,
                                    this); 
            }
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
        }
        catch (Throwable t) {
            if(LoggerAdapter.isDebugEnabled(this)) { 
                LoggerAdapter.debug("renderReservationKey threw an exception - this is an error condition",
                                    t,
                                    this); 
            }
            throw new ResourcePoolException(t);
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
                                      inResource);
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
                throw new ResourcePoolTryAllocationAgainException("");
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
        return mResourceReservations.get(inData);
    }

    protected void releaseResource(Resource inResource)
            throws ReleasedResourceException
    {
        synchronized(mResourceReservations) {
            mResourceReservations.remove(renderReservationKey(inResource));
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
}

