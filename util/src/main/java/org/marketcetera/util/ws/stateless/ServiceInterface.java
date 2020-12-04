package org.marketcetera.util.ws.stateless;

import org.apache.cxf.endpoint.Server;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A service interface handle. It is used to terminate a service.
 * 
 * @author tlerios@marketcetera.com
 * @since 1.5.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
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

