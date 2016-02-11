package org.marketcetera.util.ws.stateless;

import java.util.HashMap;
import java.util.Map;

import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.ContextClassProvider;

/* $License$ */

/**
 * A server node for stateless communication.
 * 
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */
@ClassVersion("$Id$")
public class StatelessServer
        extends Node
{
    /**
     * Create a new StatelessServer instance.
     *
     * @param inHost a <code>String</code> value
     * @param inPort an <code>int</code> value
     * @param inContextClassProvider a <code>ContextClassProvider</code> value
     */
    public StatelessServer(String inHost,
                           int inPort,
                           ContextClassProvider inContextClassProvider)
    {
        super(inHost,
              inPort);
        contextClassProvider = inContextClassProvider;
    }
    /**
     * Creates a new server node with the given server host name and port.
     *
     * @param inHost a <code>String</code> value
     * @param inPort an <code>int</code> value
     */
    public StatelessServer(String inHostname,
                           int inPort)
    {
        this(inHostname,
             inPort,
             null);
    }
    /**
     * Creates a new server node with the default server host name and port.
     */
    public StatelessServer()
    {
        this(DEFAULT_SERVER_HOST,
             DEFAULT_PORT);
    }
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
    public <T extends StatelessServiceBase> ServiceInterface publish(T impl,
                                                                     Class<T> iface)
    {
        factory = new JaxWsServerFactoryBean();
        Map<String,Object> props = factory.getProperties();
        if(props == null) {
            props = new HashMap<String,Object>();
        }
        if(contextClassProvider != null) {
            SLF4JLoggerProxy.debug(this,
                                   "Using additional context: {}", //$NON-NLS-1$
                                   contextClassProvider);
            props.put("jaxb.additionalContextClasses",  //$NON-NLS-1$
                      contextClassProvider.getContextClasses());
        }
        factory.setProperties(props); 
        factory.setServiceClass(iface);
        factory.setAddress(getConnectionUrl(iface));
        factory.setServiceBean(impl);
        server = factory.create();
        return new ServiceInterface(server);
    }
    /**
     * Shuts down the receiver.
     */
    public void stop()
    {
        if(server != null) {
            server.stop();
            server.destroy();
            server = null;
        }
        if(factory != null) {
            factory.getBus().shutdown(true);
            factory = null;
        }
    }
    /**
     * context classes to add to the server context, if any
     */
    private final ContextClassProvider contextClassProvider;
    /**
     * published server object
     */
    private Server server;
    /**
     * factory used to create server objects
     */
    private JaxWsServerFactoryBean factory;
}
