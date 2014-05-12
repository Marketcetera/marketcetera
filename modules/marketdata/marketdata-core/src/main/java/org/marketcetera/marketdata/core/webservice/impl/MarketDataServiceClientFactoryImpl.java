package org.marketcetera.marketdata.core.webservice.impl;

import org.marketcetera.marketdata.core.webservice.MarketDataServiceClient;
import org.marketcetera.marketdata.core.webservice.MarketDataServiceClientFactory;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.ContextClassProvider;

/* $License$ */

/**
 * Creates {@link MarketDataServiceClient} implementations.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.0
 */
@ClassVersion("$Id$")
public class MarketDataServiceClientFactoryImpl
        implements MarketDataServiceClientFactory
{
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.webservice.MarketDataServiceClientFactory#create(java.lang.String, java.lang.String, java.lang.String, int, org.marketcetera.util.ws.ContextClassProvider)
     */
    @Override
    public MarketDataServiceClient create(String inUsername,
                                          String inPassword,
                                          String inHostname,
                                          int inPort,
                                          ContextClassProvider inContextClassProvider)
    {
        MarketDataServiceClientImpl client = new MarketDataServiceClientImpl();
        client.setContextClassProvider(inContextClassProvider);
        client.setHostname(inHostname);
        client.setPassword(inPassword);
        client.setPort(inPort);
        client.setUsername(inUsername);
        client.setHeartbeatInterval(heartbeatInterval);
        return client;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.core.webservice.MarketDataServiceClientFactory#create(java.lang.String, java.lang.String, java.lang.String, int)
     */
    @Override
    public MarketDataServiceClient create(String inUsername,
                                          String inPassword,
                                          String inHostname,
                                          int inPort)
    {
        return create(inUsername,
                      inPassword,
                      inHostname,
                      inPort,
                      contextClassProvider);
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
     * Get the heartbeatInterval value.
     *
     * @return a <code>long</code> value
     */
    public long getHeartbeatInterval()
    {
        return heartbeatInterval;
    }
    /**
     * Sets the heartbeatInterval value.
     *
     * @param inHeartbeatInterval a <code>long</code> value
     */
    public void setHeartbeatInterval(long inHeartbeatInterval)
    {
        heartbeatInterval = inHeartbeatInterval;
    }
    /**
     * interval at which heartbeats are executed
     */
    private long heartbeatInterval = 10000;
    /**
     * default context class provider value, may be <code>null</code>
     */
    private ContextClassProvider contextClassProvider;
}
