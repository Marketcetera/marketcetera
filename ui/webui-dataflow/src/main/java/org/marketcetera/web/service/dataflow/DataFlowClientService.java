package org.marketcetera.web.service.dataflow;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.web.config.dataflows.DataFlowConfiguration;
import org.marketcetera.web.config.dataflows.DataFlowConfiguration.DataFlowEngineDescriptor;
import org.marketcetera.web.service.ConnectableService;
import org.marketcetera.web.service.ServiceManager;
import org.marketcetera.web.view.dataflows.DecoratedStrategyEngine;
import org.springframework.context.ApplicationContext;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.vaadin.server.VaadinSession;

/* $License$ */

/**
 * Provides access to a data flow server.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class DataFlowClientService
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
        if(dataFlowConfiguration.getEngineDescriptors().isEmpty()) {
            SLF4JLoggerProxy.warn(this,
                                  "No data flow engines in configuration");
            return false;
        }
        VaadinSession.getCurrent().setAttribute(DataFlowClientService.class,
                                                this);
        SLF4JLoggerProxy.info(this,
                              "Data flow engine configuration: {}",
                              dataFlowConfiguration.getEngineDescriptors());
        for(DataFlowEngineDescriptor engineDescriptor : dataFlowConfiguration.getEngineDescriptors()) {
            DataFlowClientServiceInstance serviceInstance = instancesByName.getIfPresent(engineDescriptor.getName());
            if(serviceInstance != null) {
                try {
                    serviceInstance.disconnect();
                } catch (Exception e) {
                    SLF4JLoggerProxy.warn(this,
                                          "Unable to stop existing data flow client {} for {}: {}",
                                          engineDescriptor,
                                          inUsername,
                                          ExceptionUtils.getRootCauseMessage(e));
                } finally {
                    serviceInstance = null;
                }
            }
            SLF4JLoggerProxy.debug(this,
                                   "Creating data flow client {} for {}",
                                   engineDescriptor,
                                   inUsername);
            serviceInstance = new DataFlowClientServiceInstance(engineDescriptor,
                                                                applicationContext);
            if(serviceInstance.connect()) {
                instancesByName.put(engineDescriptor.getName(),
                                    serviceInstance);
            }
        }
        running = true;
        return running;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.service.ConnectableService#disconnect()
     */
    @Override
    public void disconnect()
    {
        running = false;
        for(DataFlowClientServiceInstance instance : instancesByName.asMap().values()) {
            try {
                instance.disconnect();
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(this,
                                      e);
            }
        }
        instancesByName.invalidateAll();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.service.ConnectableService#isRunning()
     */
    @Override
    public boolean isRunning()
    {
        return running;
    }
    /**
     * Get the <code>DataFlowClientService</code> instance for the current session.
     *
     * @return a <code>DataFlowClientService</code> value
     */
    public static DataFlowClientService getInstance()
    {
        return ServiceManager.getInstance().getService(DataFlowClientService.class);
    }
    /**
     * Get the service instance for the given SE descriptor.
     *
     * @param inEngine a <code>DecoratedStrategyEngine</code> value
     * @return a <code>DataFlowClientServiceInstance</code> value
     * @throws IllegalArgumentException if there is no client service instance for the given engine
     */
    public DataFlowClientServiceInstance getServiceInstance(DecoratedStrategyEngine inEngine)
    {
        DataFlowClientServiceInstance instance = instancesByName.getIfPresent(inEngine.getName());
        Validate.notNull(instance,
                         "No data flow client service instance for " + inEngine.getName());
        return instance;
    }
    /**
     * Get the strategy engines known to the system for this user.
     *
     * @return a <code>Collection&lt;DecoratedStrategyEngine&gt;</code> value
     */
    public Collection<DecoratedStrategyEngine> getStrategyEngines()
    {
        // TODO the old code did this to find strategy engines, meaning the set of strategy engines would follow a user from one session
        //  to another rather than read from the config. thoughts?
//      List<DecoratedStrategyEngine> results = Lists.newArrayList();
//      UserAttribute userAttribute = adminClient.getUserAttribute(VaadinSession.getCurrent().getAttribute(SessionUser.class).getUsername(),
//                                                                 UserAttributeType.STRATEGY_ENGINES);
        List<DecoratedStrategyEngine> results = Lists.newArrayList();
        instancesByName.asMap().values().stream().forEach(serviceInstance->results.add(new DecoratedStrategyEngine(serviceInstance)));
        return results;
    }
    /**
     * Set the strategy engines value for the current user.
     *
     * @param inStrategyEngines a <code>Collection&lt;DecoratedStrategyEngine&gt;</code> value
     */
    public void setStrategyEngines(Collection<DecoratedStrategyEngine> inStrategyEngines)
    {
        throw new UnsupportedOperationException();
//        Properties engines = new Properties();
//        for(DecoratedStrategyEngine engine : inStrategyEngines) {
//            engines.setProperty(engine.getName(),
//                                engine.getHostname()+"|"+engine.getPort()+"|"+engine.getUrl());
//        }
//        String encodedEngines = Util.propertiesToString(engines);
//        adminClient.setUserAttribute(VaadinSession.getCurrent().getAttribute(SessionUser.class).getUsername(),
//                                     UserAttributeType.STRATEGY_ENGINES,
//                                     encodedEngines);
    }
    /**
     * Get the applicationContext value.
     *
     * @return an <code>ApplicationContext</code> value
     */
    public ApplicationContext getApplicationContext()
    {
        return applicationContext;
    }
    /**
     * Sets the applicationContext value.
     *
     * @param inApplicationContext an <code>ApplicationContext</code> value
     */
    public void setApplicationContext(ApplicationContext inApplicationContext)
    {
        applicationContext = inApplicationContext;
    }
    /**
     * Get the dataFlowConfiguration value.
     *
     * @return a <code>DataFlowConfiguration</code> value
     */
    public DataFlowConfiguration getDataFlowConfiguration()
    {
        return dataFlowConfiguration;
    }
    /**
     * Sets the dataFlowConfiguration value.
     *
     * @param inDataFlowConfiguration a <code>DataFlowConfiguration</code> value
     */
    public void setDataFlowConfiguration(DataFlowConfiguration inDataFlowConfiguration)
    {
        dataFlowConfiguration = inDataFlowConfiguration;
    }
    /**
     * indicates if the service is running
     */
    private volatile boolean running = false;
    /**
     * provides access to application configuration
     */
    private ApplicationContext applicationContext;
    /**
     * tracks data flow client instances by name
     */
    private Cache<String,DataFlowClientServiceInstance> instancesByName = CacheBuilder.newBuilder().build();
    /**
     * provides configuration for connecting to data flow engines
     */
    private DataFlowConfiguration dataFlowConfiguration;
}
