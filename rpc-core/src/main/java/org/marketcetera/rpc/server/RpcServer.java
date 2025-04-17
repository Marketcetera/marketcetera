package org.marketcetera.rpc.server;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

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
     * Get the useSsl value.
     *
     * @return a <code>boolean</code> value
     */
    public boolean useSsl()
    {
        return useSsl;
    }
    /**
     * Sets the useSsl value.
     *
     * @param inUseSsl a <code>boolean</code> value
     */
    public void setUseSsl(boolean inUseSsl)
    {
        useSsl = inUseSsl;
    }
    /**
     * Get the publicKey value.
     *
     * @return a <code>File</code> value
     */
    public File getPublicKeyPath()
    {
        return publicKey;
    }
    /**
     * Sets the publicKey value.
     *
     * @param inPublicKey a <code>File</code> value
     */
    public void setPublicKey(File inPublicKey)
    {
        publicKey = inPublicKey;
    }
    /**
     * Get the privateKey value.
     *
     * @return a <code>File</code> value
     */
    public File getPrivateKey()
    {
        return privateKey;
    }
    /**
     * Sets the privateKey value.
     *
     * @param inPrivateKey a <code>File</code> value
     */
    public void setPrivateKey(File inPrivateKey)
    {
        privateKey = inPrivateKey;
    }
    /**
     * Validate and start the server.
     *
     * @throws Exception if an unexpected error occurs.
     */
    @PostConstruct
    public synchronized void start()
            throws Exception
    {
        // If server is already running, don't start it again
        if (alive.get() && server != null) {
            SLF4JLoggerProxy.info(this, "Server {} is already running, not starting again", description);
            return;
        }
        
        Validate.notNull(hostname);
        Validate.isTrue(port > 0 && port < 65536);
        
        try {
            // TODO bind to host?
            ServerBuilder<?> serverBuilder = ServerBuilder.forPort(port);
            if(useSsl()) {
                Validate.notNull(publicKey);
                Validate.notNull(privateKey);
                Validate.isTrue(publicKey.exists());
                Validate.isTrue(privateKey.exists());
                Validate.isTrue(publicKey.canRead());
                Validate.isTrue(privateKey.canRead());
                serverBuilder = serverBuilder.useTransportSecurity(publicKey, privateKey);
            }
            
            for(BindableService serverServiceDefinition : serverServiceDefinitions) {
                serverBuilder.addService(serverServiceDefinition);
            }
            
            server = serverBuilder.build();
            
            Messages.SERVER_STARTING.info(this,
                                         description,
                                         hostname,
                                         String.valueOf(port));
            
            server.start();
            
            // Wait a short time to make sure server has started properly
            Thread.sleep(500);
            
            ports.add(new PortDescriptor(port, description));
            alive.set(true);
            
            SLF4JLoggerProxy.info(this, "Server {} started successfully on {}:{}", 
                                 description, hostname, port);
        } catch (Exception e) {
            alive.set(false);
            server = null;
            SLF4JLoggerProxy.error(this, e, "Failed to start server {} on {}:{}", 
                                  description, hostname, port);
            throw e;
        }
    }
    
    /**
     * Stop the service.
     */
    @PreDestroy
    public synchronized void stop()
    {
        if (!alive.get() || server == null) {
            SLF4JLoggerProxy.info(this, "Server {} is not running, nothing to stop", description);
            alive.set(false);
            return;
        }
        
        try {
            Messages.SERVER_STOPPING.info(this, description);
            try {
                // First try graceful shutdown with a timeout
                server.shutdown();
                if (!server.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS)) {
                    SLF4JLoggerProxy.warn(this, "Server {} did not shut down gracefully, forcing shutdown", description);
                    server.shutdownNow();
                }
                SLF4JLoggerProxy.info(this, "Server {} stopped successfully", description);
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(this, e, "Error while stopping server {}", description);
                // Try force shutdown as a last resort
                try {
                    server.shutdownNow();
                } catch (Exception e2) {
                    SLF4JLoggerProxy.error(this, e2, "Failed to force shutdown server {}", description);
                }
            }
        } finally {
            server = null;
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
     * @return a <code>List&lt;BindableService&gt;</code> value
     */
    public List<BindableService> getServerServiceDefinitions()
    {
        return serverServiceDefinitions;
    }
    /**
     * Sets the serverServiceDefinitions value.
     *
     * @param inServerServiceDefinitions a <code>List&lt;BindableService&gt;</code> value
     */
    public void setServerServiceDefinitions(List<BindableService> inServerServiceDefinitions)
    {
        serverServiceDefinitions = inServerServiceDefinitions;
    }
    /**
     * Get the description value.
     *
     * @return a <code>String</code> value
     */
    public String getDescription()
    {
        return description;
    }
    /**
     * Sets the description value.
     *
     * @param inDescription a <code>String</code> value
     */
    public void setDescription(String inDescription)
    {
        description = inDescription;
    }
    /**
     * SSL public key or <code>null</code>
     */
    private File publicKey;
    /**
     * SSL private key or <code>null</code>
     */
    private File privateKey;
    /**
     * indicates if the RPC server should be configured to use SSL
     */
    private boolean useSsl = false;
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
    /**
     * server description
     */
    private String description = "RPC Server";
}
