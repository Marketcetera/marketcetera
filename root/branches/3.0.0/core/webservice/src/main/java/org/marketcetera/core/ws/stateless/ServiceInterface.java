package org.marketcetera.core.ws.stateless;

import org.apache.cxf.endpoint.Server;

/**
 * A service interface handle. It is used to terminate a service.
 * 
 * @since 1.5.0
 * @version $Id: ServiceInterface.java 82324 2012-04-09 20:56:08Z colin $
 */

/* $License$ */

public class ServiceInterface
{

    // INSTANCE DATA.

    private Server mServer;


    // CONSTRUCTORS.

    /**
     * Creates a new handle wrapping the given CXF-specific service
     * interface representation.
     *
     * @param server The representation.
     */

    ServiceInterface
        (Server server)
    {
        mServer=server;
    }


    // INSTANCE METHODS.

    /**
     * Returns the receiver's CXF-specific representation.
     *
     * @return The representation.
     */

    private Server getServer()
    {
        return mServer;
    }

    /**
     * Stops the receiver's service.
     */

    public void stop()
    {
        getServer().stop();
    }
}

