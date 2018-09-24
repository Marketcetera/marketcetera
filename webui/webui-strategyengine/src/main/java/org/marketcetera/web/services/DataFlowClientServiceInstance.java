package org.marketcetera.web.services;

import java.io.IOException;
import java.util.Collection;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jsoup.helper.Validate;
import org.marketcetera.dataflow.client.DataFlowClient;
import org.marketcetera.dataflow.client.rpc.DataFlowRpcClientFactory;
import org.marketcetera.dataflow.client.rpc.DataFlowRpcClientParameters;
import org.marketcetera.module.ModuleInfo;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.ws.ContextClassProvider;
import org.marketcetera.web.SessionUser;
import org.marketcetera.web.config.AppConfiguration;
import org.marketcetera.web.config.dataflows.DataFlowConfiguration.DataFlowEngineDescriptor;
import org.springframework.context.ApplicationContext;

import com.vaadin.server.VaadinSession;

/* $License$ */

/**
 * Provides access to a data flow server instance.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class DataFlowClientServiceInstance
{
    /**
     * Get the module info for the given instance.
     *
     * @param inInstanceUrn a <code>ModuleURN</code> value
     * @return a <code>ModuleInfo</code> value
     */
    public ModuleInfo getModuleInfo(ModuleURN inInstanceUrn)
    {
        return dataFlowClient.getModuleInfo(inInstanceUrn);
    }
    /**
     * Get the available instances for the given provider.
     *
     * @param inProviderUrn a <code>ModuleURN</code> value
     * @return a <code>Collection&lt;ModuleURN&gt;</code> value
     */
    public Collection<ModuleURN> getInstances(ModuleURN inProviderUrn)
    {
        return dataFlowClient.getInstances(inProviderUrn);
    }
    /**
     * Get the available providers.
     *
     * @return a <code>Collection&lt;ModuleURN&gt;</code> value
     */
    public Collection<ModuleURN> getProviders()
    {
        return dataFlowClient.getProviders();
    }
    /**
     * Create a new DataFlowClientServiceInstance instance.
     *
     * @param inEngineDescriptor
     */
    public DataFlowClientServiceInstance(DataFlowEngineDescriptor inEngineDescriptor)
    {
        ApplicationContext applicationContext = AppConfiguration.getApplicationContext();
        dataFlowClientFactory = applicationContext.getBean(DataFlowRpcClientFactory.class);
        hostname = inEngineDescriptor.getHostname();
        port = inEngineDescriptor.getPort();
        name = inEngineDescriptor.getName();
    }
    /**
     * Connect to the Admin server.
     *
     * @param inPassword a <code>String</code> value
     * @return a <code>boolean</code> value
     * @throws Exception 
     */
    public boolean connect()
            throws Exception
    {
        SessionUser currentUser = VaadinSession.getCurrent().getAttribute(SessionUser.class);
        Validate.notNull(currentUser,
                         "No current user");
        String username = currentUser.getUsername();
        String password = currentUser.getPassword();
        if(dataFlowClient != null) {
            try {
                dataFlowClient.stop();
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(this,
                                      "Unable to stop existing SA client for {}: {}",
                                      username,
                                      ExceptionUtils.getRootCauseMessage(e));
            } finally {
                dataFlowClient = null;
            }
        }
        SLF4JLoggerProxy.debug(this,
                               "Creating SA client for {} to {}:{}",
                               username,
                               hostname,
                               port);
        ContextClassProvider contextProvider = AppConfiguration.getApplicationContext().getBean(ContextClassProvider.class,
                                                                                                "strategyEngineContextProvider");
        DataFlowRpcClientParameters params = new DataFlowRpcClientParameters();
        params.setHostname(hostname);
        params.setPort(port);
        params.setUsername(username);
        params.setPassword(password);
        params.setContextClassProvider(contextProvider);
        dataFlowClient = dataFlowClientFactory.create(params);
        dataFlowClient.start();
        return dataFlowClient.isRunning();
    }
    /**
     * Start the module with the given URN.
     *
     * @param inInstanceUrn a <code>ModuleURN</code> value
     */
    public void startModule(ModuleURN inInstanceUrn)
    {
        dataFlowClient.startModule(inInstanceUrn);
    }
    /**
     * Stop the module with the given URN.
     *
     * @param inInstanceUrn a <code>ModuleURN</code> value
     */
    public void stopModule(ModuleURN inInstanceUrn)
    {
        dataFlowClient.stopModule(inInstanceUrn);
    }
    /**
     * Delete the module with the given URN.
     *
     * @param inInstanceUrn a <code>ModuleURN</code> value
     */
    public void deleteModule(ModuleURN inInstanceUrn)
    {
        dataFlowClient.deleteModule(inInstanceUrn);
    }
    /**
     * Disconnect from the data flow server.
     * 
     * @throws IOException 
     */
    public void disconnect()
            throws IOException
    {
        if(dataFlowClient == null) {
            return;
        }
        try {
            dataFlowClient.close();
        } finally {
            dataFlowClient = null;
        }
    }
    /**
     * Indicate if the client connection is running or not.
     *
     * @return a <code>boolean</code> value
     */
    public boolean isRunning()
    {
        if(dataFlowClient == null) {
            return false;
        }
        return dataFlowClient.isRunning();
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
     * Get the assigned name value.
     *
     * @return a <code>String</code> value
     */
    public String getName()
    {
        return name;
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
     * creates an SA client to connect to the SA server
     */
    private final DataFlowRpcClientFactory dataFlowClientFactory;
    /**
     * client object used to communicate with the server
     */
    private DataFlowClient dataFlowClient;
    /**
     * assigned name of this instance
     */
    private final String name;
    /**
     * server hostname to connect to
     */
    private final String hostname;
    /**
     * server port to connect to
     */
    private final int port;
}
