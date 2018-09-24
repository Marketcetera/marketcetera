package org.marketcetera.web.marketdata.service;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.marketcetera.marketdata.MarketDataClient;
import org.marketcetera.marketdata.rpc.client.MarketDataRpcClientFactory;
import org.marketcetera.marketdata.rpc.client.MarketDataRpcClientParameters;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.web.service.ConnectableService;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.spring.annotation.SpringComponent;

/* $License$ */

/**
 * Provides client market data services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringComponent
public class MarketDataClientService
        implements ConnectableService
{
    /* (non-Javadoc)
     * @see org.marketcetera.web.services.ConnectableService#connect(java.lang.String, java.lang.String, java.lang.String, int)
     */
    @Override
    public boolean connect(String inUsername,
                           String inPassword,
                           String inHostname,
                           int inPort)
            throws Exception
    {
        if(marketDataClient != null) {
            try {
                marketDataClient.stop();
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(this,
                                      "Unable to stop existing market data client for {}: {}",
                                      inUsername,
                                      ExceptionUtils.getRootCauseMessage(e));
            } finally {
                marketDataClient = null;
            }
        }
        SLF4JLoggerProxy.debug(this,
                               "Creating market data client for {} to {}:{}",
                               inUsername,
                               inHostname,
                               inPort);
        MarketDataRpcClientParameters params = new MarketDataRpcClientParameters();
        params.setHostname(inHostname);
        params.setPort(inPort);
        params.setUsername(inUsername);
        params.setPassword(inPassword);
        marketDataClient = MarketDataClientFactory.create(params);
        marketDataClient.start();
        return marketDataClient.isRunning();
    }
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
    {
        SLF4JLoggerProxy.info(this,
                              "Starting market data client service");
        instance = this;
    }
    /**
     * Get the instance value.
     *
     * @return a <code>MarketDataClientService</code> value
     */
    public static MarketDataClientService getInstance()
    {
        return instance;
    }
    /**
     * static instance of this object
     */
    private static MarketDataClientService instance;
    /**
     * provides access to MarketData services
     */
    private MarketDataClient marketDataClient;
    /**
     * creates {@link MarketDataClient} objects
     */
    @Autowired
    private MarketDataRpcClientFactory MarketDataClientFactory;
}
