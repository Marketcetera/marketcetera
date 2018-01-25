package org.marketcetera.rpc.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang.Validate;
import org.marketcetera.rpc.Messages;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.ws.stateful.PortDescriptor;
import org.marketcetera.util.ws.stateful.UsesPort;

import com.google.common.collect.Lists;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

/* $License$ */

/**
 * Provides an RPC server implementation that can run multiple {@link BindableService} services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class RpcServer
        implements UsesPort
{
    /* (non-Javadoc)
     * @see org.marketcetera.util.ws.stateful.UsesPort#getPortDescriptors()
     */
    @Override
    public Collection<PortDescriptor> getPortDescriptors()
    {
        return ports;
    }
    /**
     * Validate and start the server.
     *
     * @throws Exception if an unexpected error occcurs.
     */
    @PostConstruct
    public synchronized void start()
            throws Exception
    {
        Validate.notNull(hostname);
        Validate.isTrue(port > 0 && port < 65536);
        Messages.SERVER_STARTING.info(this,
                                      hostname,
                                      String.valueOf(port));
        // TODO bind to host?
        ServerBuilder<?> serverBuilder = ServerBuilder.forPort(port);
        for(BindableService serverServiceDefinition : serverServiceDefinitions) {
            serverBuilder.addService(serverServiceDefinition);
        }
        server = serverBuilder.build();
        server.start();
        ports.add(new PortDescriptor(port,
                "RPC Service"));
        alive.set(true);
    }
    /**
     * Stop the service.
     */
    @PreDestroy
    public synchronized void stop()
    {
        try {
            Messages.SERVER_STOPPING.info(this);
            if(server != null) {
                try {
                    server.shutdownNow();
                } catch (Exception e) {
                    SLF4JLoggerProxy.warn(this,
                                          e);
                }
                server = null;
            }
        } finally {
            alive.set(false);
        }
    }
    /**
     * Indicate if the service is alive or not.
     *
     * @return a <code>boolean</code> value
     */
    public boolean isRunning()
    {
        return alive.get();
    }
    /**
     * Get the port value.
     *
     * @return an <code>int</code> value
     */
    public int getPort()
    {
        return port;
    }
    /**
     * Sets the port value.
     *
     * @param inPort an <code>int</code> value
     */
    public void setPort(int inPort)
    {
        port = inPort;
    }
    /**
     * Get the hostname value.
     *
     * @return a <code>String</code> value
     */
    public String getHostname()
    {
        return hostname;
    }
    /**
     * Sets the hostname value.
     *
     * @param inHostname a <code>String</code> value
     */
    public void setHostname(String inHostname)
    {
        hostname = inHostname;
    }
    /**
     * Get the serverServiceDefinitions value.
     *
     * @return a <code>List<BindableService></code> value
     */
    public List<BindableService> getServerServiceDefinitions()
    {
        return serverServiceDefinitions;
    }
    /**
     * Sets the serverServiceDefinitions value.
     *
     * @param inServerServiceDefinitions a <code>List<BindableService></code> value
     */
    public void setServerServiceDefinitions(List<BindableService> inServerServiceDefinitions)
    {
        serverServiceDefinitions = inServerServiceDefinitions;
    }
    /**
     * indicates if the server is alive or not
     */
    private final AtomicBoolean alive = new AtomicBoolean(false);
    /**
     * port at which to find RPC services
     */
    private int port;
    /**
     * host to bind to
     */
    private String hostname;
    /**
     * internal service object
     */
    private Server server;
    /**
     * collection of services to provide
     */
    private List<BindableService> serverServiceDefinitions = new ArrayList<>();
    /**
     * indicates ports in use
     */
    private final Collection<PortDescriptor> ports = Lists.newArrayList();
}
