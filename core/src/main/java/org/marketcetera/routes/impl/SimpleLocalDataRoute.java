package org.marketcetera.routes.impl;

import java.io.Serializable;

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
public class SimpleLocalDataRoute
        implements LocalDataRoute
{
    /* (non-Javadoc)
     * @see org.marketcetera.agent.DataRoute#route(java.io.Serializable)
     */
    @Override
    public void route(Serializable inData)
    {
        destination.accept(inData);
    }
    /**
     * Get the destination value.
     *
     * @return a <code>Service</code> value
     */
    public Service getDestination()
    {
        return destination;
    }
    /**
     * Sets the destination value.
     *
     * @param inDestination a <code>Service</code> value
     */
    public void setDestination(Service inDestination)
    {
        destination = inDestination;
    }
    /**
     * 
     */
    private Service destination;
}
