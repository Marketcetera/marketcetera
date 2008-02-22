package org.marketcetera.core.resourcepool;

/**
 * Represents an abstract resource in a {@link ResourcePool}.
 * 
 * A {@link ResourcePool} is a tool for managing a group of similar
 * objects that provide a service, e.g. a JDBC Connection object.
 * Callers need to request a <code>Resource</code> for a period of
 * time, but they don't care precisely which <code>Resource</code>
 * they get, as they all perform the same function.  The caller
 * returns the <code>Resource</code> to the {@link ResourcePool}
 * when the caller is done doing whatever the <code>Resource</code>
 * allows the caller to do.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: $
 */
public interface Resource
{
    /**
     * Called when a <code>Resource</code> is created.
     *
     * This method will be called once over the lifetime of a
     * given <code>Resource</code>, when it is initially created.
     * Upon completion of this method, the <code>Resource</code>
     * should be ready for service.
     *
     * @param inPool a <code>ResourcePool</code> value
     * @throws Throwable if an error occurs during initialization - note,
     *   this will cause the <code>Resource</code> to be discarded
     */
    public void initialize(ResourcePool inPool)
        throws Throwable;
    
    /**
     * Called when a <code>Resource</code> is issued to a caller.
     * 
     * This method may be called many times over the lifetime of
     * a given <code>Resource</code>.  When a caller requests a
     * <code>Resource</code> from a {@link ResourcePool}, this method
     * is called on the <code>Resource</code> before it is issued.
     *
     * @param inPool a <code>ResourcePool</code> value
     * @throws Throwable if an error occurs - note, throwing an exception
     *   in this method will cause the {@link ResourcePool} to reject the
     *   request from the caller for a <code>Resource</code>.  The <code>Resource</code>
     *   will subsequently be released by the {@link ResourcePool} (@see {@link #released(ResourcePool)}).
     */
    public void allocated(ResourcePool inPool)
        throws Throwable;
    
    /**
     * Called when a <code>Resource</code> is at the end of service.
     *
     * This method is called when a <code>Resource</code> is discarded from
     * the {@link ResourcePool} when it is no longer needed.  This can happen
     * if:
     * <ol>
     *   <li>{@link Resource#isFunctional()} returns <code>false</code> when
     *     the <code>Resource</code> is returned to tthe {@link ResourcePool}</li>
     *   <li>the <code>Resource</code> is returned to the {@link ResourcePool}, but
     *     the {@link ResourcePool} already has the maximum number of <code>Resource</code>
     *     objects (@see {@link ResourcePool#getMaxResources()})</li>
     * </ol>
     *
     * @param inPool a <code>ResourcePool</code> value
     * @throws Throwable if an error occurs
     */
    public void released(ResourcePool inPool)
        throws Throwable;
    
    /**
     * Determines if the <code>Resource</code> is in a fit state to be used.
     * 
     * This method may be called any time during the lifetime of the
     * <code>Resource</code>.  The <code>Resource</code> should evaluate its
     * state and determine if it is fit to be used for its purpose.
     *
     * @return a <code>boolean</code> value
     */
    public boolean isFunctional();
    
    /**
     * Called when a <code>Resource</code> is returned to the {@link ResourcePool}.
     * 
     * This method may be called many times over the lifetime of a <code>Resource</code>.
     * When a caller is done with a <code>Resource</code> and returns it for reuse,
     * this method is called.  If any work needs to be done to return the <code>Resource</code>
     * to a usable state before it can be reused, this is the place to do it.  The <code>Resource</code>
     * should be ready to be used again when this method is complete.
     * 
     * <p>This method is called after the <code>Resource</code> has been returned to the pool
     * but before the lock on the pool is released.
     *
     * @param inPool a <code>ResourcePool</code> value
     * @throws Throwable if a <code>Resource</code> throws an exception
     *   when it's returned, the <code>Resource</code> is still returned for re-use.  If the intent
     *   is to make the <code>Resource</code> be discarded, make sure {@link Resource#isFunctional()} returns
     *   false when the <code>Resource</code> is returned.
     */
    public void returned(ResourcePool inPool)
        throws Throwable;
    
    /**
     * Called when the {@link ResourcePool} receives a request to shut down.
     * 
     * The <code>Resource</code> should finish outstanding work and
     * shut down as soon as possible.  The {@link ResourcePool} will not call
     * {@link #released(ResourcePool)} or {@link #returned(ResourcePool)}.
     * 
     * @param inPool a <code>ResourcePool</code> value
     * @throws Throwable if an error occurs.  Throwing this exception will indicate
     *   to the {@link ResourcePool} that this <code>Resource</code> is as shut down
     *   as it's going to get.
     */
    public void shutdown(ResourcePool inPool)
        throws Throwable;
}
