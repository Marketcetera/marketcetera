package org.marketcetera.marketdata.core.webservice.impl;

import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang.Validate;
import org.marketcetera.core.ApplicationVersion;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.core.webservice.MarketDataWebService;
import org.marketcetera.marketdata.core.webservice.MarketDataWebServiceClient;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.ContextClassProvider;
import org.marketcetera.util.ws.stateful.Client;
import org.marketcetera.util.ws.tags.AppId;
import org.marketcetera.util.ws.wrappers.RemoteException;

/* $License$ */

/**
 * Provides access to Market Data Nexus services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class MarketDataWebServiceClientImpl
        implements MarketDataWebServiceClient
{
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.webservice.MarketDataWebServiceClient#request(org.marketcetera.marketdata.MarketDataRequest)
     */
    @Override
    public long request(MarketDataRequest inRequest)
            throws RemoteException
    {
        return marketDataService.request(serviceClient.getContext(),
                                         inRequest);
    }
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
        Validate.isTrue(port > 0);
        serviceClient = new Client(hostname,
                                   port,
                                   new AppId(ApplicationVersion.getVersion().getVersionInfo()),
                                   contextClassProvider);
        try {
            serviceClient.login("user","password".toCharArray()); // TODO
        } catch (I18NException | RemoteException e) {
            SLF4JLoggerProxy.warn(this,
                                  e);
            throw new RuntimeException(e);
        }
        marketDataService = serviceClient.getService(MarketDataWebService.class);
        running.set(true);
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#stop()
     */
    @Override
    public void stop()
    {
        try {
            serviceClient.logout();
        } catch (RemoteException ignored) {
        } finally {
            serviceClient = null;
            marketDataService = null;
            running.set(false);
        }
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
    private ContextClassProvider contextClassProvider;
    /**
     * 
     */
    private MarketDataWebService marketDataService;
    /**
     * 
     */
    private Client serviceClient;
    /**
     * 
     */
    private final AtomicBoolean running = new AtomicBoolean(false);
}
