package org.marketcetera.webui.service;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.marketcetera.trade.client.TradeClient;
import org.marketcetera.trade.client.TradeClientFactory;
import org.marketcetera.trade.client.TradeClientParameters;
import org.marketcetera.trading.rpc.TradeRpcClientParametersImpl;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;

/* $License$ */

/**
 * Provides services with the {@link TradeClient}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class TradeClientService
        implements ConnectableService
{
    /* (non-Javadoc)
     * @see org.marketcetera.webui.service.ConnectableService#connect(java.lang.String, java.lang.String, java.lang.String, int)
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
                                      "Unable to stop existing trade client for {}: {}",
                                      inUsername,
                                      ExceptionUtils.getRootCauseMessage(e));
            } finally {
                tradeClient = null;
            }
        }
        SLF4JLoggerProxy.debug(this,
                               "Creating trade client for {} to {}:{}",
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
     * Get the tradeClient value.
     *
     * @return a <code>TradeClient</code> value
     */
    public TradeClient getTradeClient()
    {
        if(tradeClient == null || !tradeClient.isRunning()) {
            throw new IllegalStateException("Trade client is not running");
        }
        return tradeClient;
    }
    /**
     * client object used to communicate with the server
     */
    private TradeClient tradeClient;
    /**
     * 
     */
    @Autowired
    private TradeClientFactory<TradeClientParameters> tradeClientFactory;
}
