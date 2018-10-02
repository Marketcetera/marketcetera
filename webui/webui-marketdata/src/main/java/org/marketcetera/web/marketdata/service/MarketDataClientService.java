package org.marketcetera.web.marketdata.service;

import java.io.IOException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.marketcetera.marketdata.MarketDataClient;
import org.marketcetera.marketdata.MarketDataListener;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.marketdata.rpc.client.MarketDataRpcClientFactory;
import org.marketcetera.marketdata.rpc.client.MarketDataRpcClientParameters;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.web.service.ConnectableService;
import org.marketcetera.web.service.ServiceManager;

/* $License$ */

/**
 * Provides client market data services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
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
        marketDataClient = marketDataClientFactory.create(params);
        marketDataClient.start();
        return marketDataClient.isRunning();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.service.ConnectableService#disconnect()
     */
    @Override
    public void disconnect()
    {
        if(marketDataClient != null) {
            try {
                marketDataClient.close();
            } catch (IOException e) {
                SLF4JLoggerProxy.warn(this,
                                      e);
            }
        }
        marketDataClient = null;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.service.ConnectableService#isRunning()
     */
    @Override
    public boolean isRunning()
    {
        return marketDataClient != null && marketDataClient.isRunning();
    }
    /**
     * Request market data.
     * 
     * @param inRequest a <code>MarketDataRequest</code> value
     * @param inMarketDataListener a <code>MarketDataListener</code> value
     * @return a <code>String</code> value containing the request ID
     */
    public String request(MarketDataRequest inRequest,
                          MarketDataListener inMarketDataListener)
    {
        return marketDataClient.request(inRequest,
                                        inMarketDataListener);
    }
    /**
     * Cancels a market data request.
     *
     * @param inRequestId a <code>String</code> value
     */
    public void cancel(String inRequestId)
    {
        
    }
    /**
     * Get the instance value.
     *
     * @return a <code>MarketDataClientService</code> value
     */
    public static MarketDataClientService getInstance()
    {
        return ServiceManager.getInstance().getService(MarketDataClientService.class);
    }
    /**
     * Sets the marketDataClientFactory value.
     *
     * @param inMarketDataClientFactory a <code>MarketDataRpcClientFactory</code> value
     */
    public void setMarketDataClientFactory(MarketDataRpcClientFactory inMarketDataClientFactory)
    {
        marketDataClientFactory = inMarketDataClientFactory;
    }
    /**
     * provides access to MarketData services
     */
    private MarketDataClient marketDataClient;
    /**
     * creates {@link MarketDataClient} objects
     */
    private MarketDataRpcClientFactory marketDataClientFactory;
}
