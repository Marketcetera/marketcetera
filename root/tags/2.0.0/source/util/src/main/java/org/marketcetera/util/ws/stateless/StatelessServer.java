package org.marketcetera.util.ws.stateless;

import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A server node for stateless communication.
 * 
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class StatelessServer
    extends Node
{

    // CONSTRUCTORS.

    /**
     * Creates a new server node with the given server host name and
     * port.
     *
     * @param host The host name.
     * @param port The port.
     */    

    public StatelessServer
        (String host,
         int port)
    {
        super(host,port);
    }

    /**
     * Creates a new server node with the default server host name and
     * port.
     */    

    public StatelessServer()
    {
        this(DEFAULT_HOST,DEFAULT_PORT);
    }


    // INSTANCE METHODS.

    /**
     * Publishes the given service interface, supported by the given
     * implementation, and returns a handle that can be used to stop
     * the interface.
     *
     * @param impl The implementation.
     * @param iface The interface class.
     *
     * @return The handle.
     */

    public <T extends StatelessServiceBase> ServiceInterface publish
        (T impl,
         Class<T> iface)
    {
        JaxWsServerFactoryBean f=new JaxWsServerFactoryBean();
        f.setServiceClass(iface);
        f.setAddress(getConnectionUrl(iface));
        f.setServiceBean(impl);
        return new ServiceInterface(f.create());
    }

    /**
     * Shuts down the receiver.
     */

    public void stop() {}
}
