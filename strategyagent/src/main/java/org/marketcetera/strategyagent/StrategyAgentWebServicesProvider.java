package org.marketcetera.strategyagent;

import java.util.concurrent.atomic.AtomicBoolean;

import org.marketcetera.saclient.SAService;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.ContextClassProvider;
import org.marketcetera.util.ws.stateful.*;
import org.marketcetera.util.ws.stateless.ServiceInterface;
import org.springframework.context.Lifecycle;

/* $License$ */

/**
 * Provides web services for the Strategy Agent.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class StrategyAgentWebServicesProvider
        implements Lifecycle, ServerProvider<ClientSession>
{
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#isRunning()
     */
    @Override
    public boolean isRunning()
    {
        return running.get();
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#start()
     */
    @Override
    public void start()
    {
        server = new Server<ClientSession>(hostname,
                                           port,
                                           authenticator,
                                           sessionManager,
                                           contextClassProvider);
        remoteService = server.publish(serviceProvider,
                                       SAService.class);
        Messages.LOG_REMOTE_WS_CONFIGURED.info(this,
                                               hostname,
                                               String.valueOf(port));
        running.set(true);
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#stop()
     */
    @Override
    public void stop()
    {
        try {
            if(remoteService != null) {
                remoteService.stop();
                remoteService = null;
            }
            if(server != null) {
                try {
                    server.stop();
                    server = null;
                } catch (Exception ignored) {}
            }
        } finally {
            running.set(false);
        }
    }
    /**
     * Get the sessionManager value.
     *
     * @return a <code>SessionManager<ClientSession></code> value
     */
    public SessionManager<ClientSession> getSessionManager()
    {
        return sessionManager;
    }
    /**
     * Sets the sessionManager value.
     *
     * @param inSessionManager a <code>SessionManager<ClientSession></code> value
     */
    public void setSessionManager(SessionManager<ClientSession> inSessionManager)
    {
        sessionManager = inSessionManager;
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
     * Get the authenticator value.
     *
     * @return an <code>Authenticator</code> value
     */
    public Authenticator getAuthenticator()
    {
        return authenticator;
    }
    /**
     * Sets the authenticator value.
     *
     * @param inAuthenticator an <code>Authenticator</code> value
     */
    public void setAuthenticator(Authenticator inAuthenticator)
    {
        authenticator = inAuthenticator;
    }
    /**
     * Get the remoteService value.
     *
     * @return a <code>ServiceInterface</code> value
     */
    public ServiceInterface getRemoteService()
    {
        return remoteService;
    }
    /**
     * Get the server value.
     *
     * @return a <code>Server<ClientSession></code> value
     */
    @Override
    public Server<ClientSession> getServer()
    {
        return server;
    }
    /**
     * Get the serviceProvider value.
     *
     * @return an <code>SAService</code> value
     */
    public SAService getServiceProvider()
    {
        return serviceProvider;
    }
    /**
     * Sets the serviceProvider value.
     *
     * @param inServiceProvider an <code>SAService</code> value
     */
    public void setServiceProvider(SAService inServiceProvider)
    {
        serviceProvider = inServiceProvider;
    }
    /**
     * Get the contextClasses value.
     *
     * @return a <code>ContextClassProvider</code> value
     */
    public ContextClassProvider getContextClasses()
    {
        return contextClassProvider;
    }
    /**
     * Sets the contextClasses value.
     *
     * @param inContextClassProvider a <code>ContextClassProvider</code> value
     */
    public void setContextClasses(ContextClassProvider inContextClassProvider)
    {
        contextClassProvider = inContextClassProvider;
    }
    /**
     * 
     */
    private SessionManager<ClientSession> sessionManager;
    /**
     * 
     */
    private String hostname;
    /**
     * 
     */
    private int port;
    /**
     * 
     */
    private Authenticator authenticator;
    /**
     * The handle to the remote web service.
     */
    private ServiceInterface remoteService;
    /**
     * 
     */
    private Server<ClientSession> server;
    /**
     * 
     */
    private SAService serviceProvider;
    /**
     * 
     */
    private ContextClassProvider contextClassProvider;
    /**
     * 
     */
    private final AtomicBoolean running = new AtomicBoolean(false);
}
