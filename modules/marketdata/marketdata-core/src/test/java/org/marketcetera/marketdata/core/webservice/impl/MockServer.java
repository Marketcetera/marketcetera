package org.marketcetera.marketdata.core.webservice.impl;

import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang.Validate;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.ws.ContextClassProvider;
import org.marketcetera.util.ws.stateful.Authenticator;
import org.marketcetera.util.ws.stateful.Server;
import org.marketcetera.util.ws.stateful.ServerProvider;
import org.marketcetera.util.ws.stateful.SessionManager;
import org.springframework.context.Lifecycle;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MockServer
        implements Lifecycle, ServerProvider<MockSession>
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
        Validate.notNull(hostname);
        Validate.isTrue(port > 1024);
        Validate.notNull(authenticator);
        Validate.notNull(sessionManager);
        SLF4JLoggerProxy.debug(this,
                               "Starting mock server on {}:{}",
                               hostname,
                               port);
        server = new Server<MockSession>(hostname,
                                         port,
                                         authenticator,
                                         sessionManager,
                                         contextClassProvider);
        running.set(true);
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#stop()
     */
    @Override
    public void stop()
    {
        SLF4JLoggerProxy.debug(this,
                               "Stopping mock server");
        try {
            server.stop();
            server = null;
        } finally {
            running.set(false);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.util.ws.stateful.ServerProvider#getServer()
     */
    @Override
    public Server<MockSession> getServer()
    {
        return server;
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
     * @return a <code>int</code> value
     */
    public int getPort()
    {
        return port;
    }
    /**
     * Sets the port value.
     *
     * @param inPort a <code>int</code> value
     */
    public void setPort(int inPort)
    {
        port = inPort;
    }
    /**
     * Get the contextClassProvider value.
     *
     * @return a <code>ContextClassProvider</code> value
     */
    public ContextClassProvider getContextClassProvider()
    {
        return contextClassProvider;
    }
    /**
     * Sets the contextClassProvider value.
     *
     * @param inContextClassProvider a <code>ContextClassProvider</code> value
     */
    public void setContextClassProvider(ContextClassProvider inContextClassProvider)
    {
        contextClassProvider = inContextClassProvider;
    }
    /**
     * Get the sessionManager value.
     *
     * @return a <code>SessionManager<MockSession></code> value
     */
    public SessionManager<MockSession> getSessionManager()
    {
        return sessionManager;
    }
    /**
     * Sets the sessionManager value.
     *
     * @param inSessionManager a <code>SessionManager<MockSession></code> value
     */
    public void setSessionManager(SessionManager<MockSession> inSessionManager)
    {
        sessionManager = inSessionManager;
    }
    /**
     * Get the authenticator value.
     *
     * @return a <code>Authenticator</code> value
     */
    public Authenticator getAuthenticator()
    {
        return authenticator;
    }
    /**
     * Sets the authenticator value.
     *
     * @param inAuthenticator a <code>Authenticator</code> value
     */
    public void setAuthenticator(Authenticator inAuthenticator)
    {
        authenticator = inAuthenticator;
    }
    /**
     * 
     */
    private ContextClassProvider contextClassProvider;
    /**
     * 
     */
    private SessionManager<MockSession> sessionManager;
    /**
     * 
     */
    private Authenticator authenticator;
    /**
     * 
     */
    private Server<MockSession> server;
    /**
     * 
     */
    private String hostname = "127.0.0.1";
    /**
     * 
     */
    private int port = 12345;
    /**
     * 
     */
    private final AtomicBoolean running = new AtomicBoolean(false);
}
