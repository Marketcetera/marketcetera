package org.marketcetera.web.service.trade;

import java.io.IOException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.trade.OrderSummary;
import org.marketcetera.trade.client.TradeClient;
import org.marketcetera.trading.rpc.TradeRpcClientFactory;
import org.marketcetera.trading.rpc.TradeRpcClientParametersImpl;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.web.service.ConnectableService;
import org.marketcetera.web.service.ServiceManager;

/* $License$ */

/**
 * Provides access to trade services for a given user.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class TradeClientService
        implements ConnectableService
{
    /**
     * Get the <code>TradeClientService</code> instance for the current session.
     *
     * @return a <code>TradeClientService</code> value or <code>null</code>
     */
    public static TradeClientService getInstance()
    {
        return ServiceManager.getInstance().getService(TradeClientService.class);
    }
    /**
     * Create a new TradeClientService instance.
     */
    public TradeClientService() {}
    /**
     *
     *
     * @param inPageRequest
     * @return
     */
    public CollectionPageResponse<OrderSummary> getOpenOrders(PageRequest inPageRequest)
    {
        return tradeClient.getOpenOrders(inPageRequest);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.service.ConnectableService#isRunning()
     */
    @Override
    public boolean isRunning()
    {
        return tradeClient != null && tradeClient.isRunning();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.service.ConnectableService#disconnect()
     */
    @Override
    public void disconnect()
    {
        if(tradeClient != null) {
            try {
                tradeClient.close();
            } catch (IOException e) {
                SLF4JLoggerProxy.warn(this,
                                      e);
            }
        }
        tradeClient = null;
    }
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
        if(tradeClient != null) {
            try {
                tradeClient.stop();
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(this,
                                      "Unable to stop existing Trade client for {}: {}",
                                      inUsername,
                                      ExceptionUtils.getRootCauseMessage(e));
            } finally {
                tradeClient = null;
            }
        }
        SLF4JLoggerProxy.debug(this,
                               "Creating Trade client for {} to {}:{}",
                               inUsername,
                               inHostname,
                               inPort);
        TradeRpcClientParametersImpl params = new TradeRpcClientParametersImpl();
        params.setHostname(inHostname);
        params.setPort(inPort);
        params.setUsername(inUsername);
        params.setPassword(inPassword);
        tradeClient = tradeClientFactory.create(params);
        tradeClient.start();
        return tradeClient.isRunning();
    }
    /**
     * Sets the TradeClientFactory value.
     *
     * @param inTradeClientFactory an <code>TradeRpcClientFactory</code> value
     */
    public void setTradeClientFactory(TradeRpcClientFactory inTradeClientFactory)
    {
        tradeClientFactory = inTradeClientFactory;
    }
    /**
     * creates an Trade client to connect to the Trade server
     */
    private TradeRpcClientFactory tradeClientFactory;
    /**
     * client object used to communicate with the server
     */
    private TradeClient tradeClient;
}
