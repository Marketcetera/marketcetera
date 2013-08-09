package org.marketcetera.routes.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

import org.marketcetera.routes.LocalDataRoute;
import org.marketcetera.service.Service;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@NotThreadSafe
public class MultiLocalDataRoute
        implements LocalDataRoute
{
    /* (non-Javadoc)
     * @see org.marketcetera.agent.DataRoute#route(java.io.Serializable)
     */
    @Override
    public void route(Serializable inData)
    {
        for(Service service : services) {
            try {
                service.accept(inData);
            } catch (RuntimeException e) {
                // TODO handle exception
            }
        }
    }
    /**
     * Get the services value.
     *
     * @return a <code>List&lt;Service&gt;</code> value
     */
    public List<Service> getServices()
    {
        return services;
    }
    /**
     * Sets the services value.
     *
     * @param inServices a <code>List&lt;Service&gt;</code> value
     */
    public void setServices(List<Service> inServices)
    {
        services.clear();
        if(inServices != null) {
            services.addAll(inServices);
        }
    }
    /**
     * 
     */
    private final List<Service> services = new ArrayList<Service>();
}
