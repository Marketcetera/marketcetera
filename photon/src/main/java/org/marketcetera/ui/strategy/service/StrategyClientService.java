package org.marketcetera.ui.strategy.service;

import java.io.IOException;
import java.util.Collection;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.marketcetera.strategy.StrategyClient;
import org.marketcetera.strategy.StrategyInstance;
import org.marketcetera.strategy.StrategyRpcClientFactory;
import org.marketcetera.strategy.StrategyRpcClientParameters;
import org.marketcetera.strategy.StrategyStatus;
import org.marketcetera.ui.service.ConnectableService;
import org.marketcetera.ui.service.ServiceManager;
import org.marketcetera.util.log.SLF4JLoggerProxy;

/* $License$ */

/**
 * Provides client strategy services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class StrategyClientService
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
        if(strategyClient != null) {
            try {
                strategyClient.stop();
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(this,
                                      "Unable to stop existing market data client for {}: {}",
                                      inUsername,
                                      ExceptionUtils.getRootCauseMessage(e));
            } finally {
                strategyClient = null;
            }
        }
        SLF4JLoggerProxy.debug(this,
                               "Creating market data client for {} to {}:{}",
                               inUsername,
                               inHostname,
                               inPort);
        StrategyRpcClientParameters params = new StrategyRpcClientParameters();
        params.setHostname(inHostname);
        params.setPort(inPort);
        params.setUsername(inUsername);
        params.setPassword(inPassword);
        strategyClient = strategyClientFactory.create(params);
        strategyClient.start();
        return strategyClient.isRunning();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.service.ConnectableService#disconnect()
     */
    @Override
    public void disconnect()
    {
        if(strategyClient != null) {
            try {
                strategyClient.close();
            } catch (IOException e) {
                SLF4JLoggerProxy.warn(this,
                                      e);
            }
        }
        strategyClient = null;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.service.ConnectableService#isRunning()
     */
    @Override
    public boolean isRunning()
    {
        return strategyClient != null && strategyClient.isRunning();
    }
    /**
     * Get the instance value.
     *
     * @return a <code>StrategyClientService</code> value
     */
    public static StrategyClientService getInstance()
    {
        return ServiceManager.getInstance().getService(StrategyClientService.class);
    }
    /**
     * Requests loaded strategy instances.
     *
     * @returns a <code>Collection&lt;? extends StrategyInstance&gt;</code> value
     */
    public Collection<? extends StrategyInstance> getStrategyInstances()
    {
        return strategyClient.getStrategyInstances();
    }
    /**
     * Load a new strategy instances.
     *
     * @param inStrategyInstance an <code>StrategyInstance</code> value
     * @returns an <code>StrategyStatus</code> value
     */
    public StrategyStatus loadStrategyInstance(StrategyInstance inStrategyInstance)
    {
        return strategyClient.loadStrategyInstance(inStrategyInstance);
    }
    /**
     * Sets the StrategyClientFactory value.
     *
     * @param inStrategyClientFactory a <code>StrategyRpcClientFactory</code> value
     */
    public void setStrategyClientFactory(StrategyRpcClientFactory inStrategyClientFactory)
    {
        strategyClientFactory = inStrategyClientFactory;
    }
    /**
     * provides access to strategy services
     */
    private StrategyClient strategyClient;
    /**
     * creates {@link StrategyClient} objects
     */
    private StrategyRpcClientFactory strategyClientFactory;
}
