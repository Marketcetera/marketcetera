package org.marketcetera.ui.strategy.service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Optional;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.marketcetera.strategy.FileUploadRequest;
import org.marketcetera.strategy.StrategyClient;
import org.marketcetera.strategy.StrategyEventListener;
import org.marketcetera.strategy.StrategyInstance;
import org.marketcetera.strategy.StrategyRpcClientFactory;
import org.marketcetera.strategy.StrategyRpcClientParameters;
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
     * Unload a strategy instance.
     *
     * @param inStrategyInstanceName a <code>String</code> value
     */
    public void unloadStrategyInstance(String inStrategyInstanceName)
    {
        strategyClient.unloadStrategyInstance(inStrategyInstanceName);
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
     * Finds the strategy instance with the given name.
     *
     * @param inName a <code>String</code> value
     * @returns a <code>java.util.Optional<? extends StrategyInstance></code> value
     */
    public Optional<? extends StrategyInstance> findByName(String inName)
    {
        return strategyClient.findByName(inName);
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
     * Add the given strategy event listener to start receiving strategy events.
     *
     * @param inListener a <code>StrategyEventListener</code> value
     */
    public void addStrategyEventListener(StrategyEventListener inListener)
    {
        strategyClient.addStrategyEventListener(inListener);
    }
    /**
     * Remove the given event listener to stop receiving strategy events.
     *
     * @param inListener a <code>StrategyEventListener</code> value
     */
    public void removeStrategyEventListener(StrategyEventListener inListener)
    {
        strategyClient.removeStrategyEventListener(inListener);
    }
    /**
     * Start a strategy instance.
     *
     * @param inStrategyInstanceName a <code>String</code> value
     */
    public void startStrategyInstance(String inStrategyInstanceName)
    {
        strategyClient.startStrategyInstance(inStrategyInstanceName);
    }
    /**
     * Stop a strategy instance.
     *
     * @param inStrategyInstanceName a <code>String</code> value
     */
    public void stopStrategyInstance(String inStrategyInstanceName)
    {
        strategyClient.stopStrategyInstance(inStrategyInstanceName);
    }
    /**
     * Upload the file in the given file request.
     *
     * @param inRequest a <code>FileUploadRequest</code> value
     * @throws IOException if the file could not be read
     * @throws NoSuchAlgorithmException if the file could not be hashed
     */
    public void uploadFile(FileUploadRequest inRequest)
            throws IOException, NoSuchAlgorithmException
    {
        strategyClient.uploadFile(inRequest);
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
