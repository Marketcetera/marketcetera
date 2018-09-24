package org.marketcetera.web.services;

import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.web.config.dataflows.DataFlowConfiguration;
import org.marketcetera.web.config.dataflows.DataFlowConfiguration.DataFlowEngineDescriptor;
import org.marketcetera.web.view.dataflows.DecoratedStrategyEngine;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.vaadin.server.VaadinSession;
import com.vaadin.spring.annotation.SpringComponent;

/* $License$ */

/**
 * Provides access to a data flow server.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringComponent
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
        SLF4JLoggerProxy.info(this,
                              "Data flow engine configuration: {}",
                              dataFlowConfiguration.getEngineDescriptors());
        boolean atLeastOne = false;
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
            serviceInstance = new DataFlowClientServiceInstance(engineDescriptor);
            if(serviceInstance.connect()) {
                instancesByName.put(engineDescriptor.getName(),
                                    serviceInstance);
                atLeastOne = true;
            }
        }
        return atLeastOne;
    }
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
    {
        SLF4JLoggerProxy.info(this,
                              "Starting data flow client service");
    }
    /**
     * Get the <code>SaClientService</code> instance for the current session.
     *
     * @return a <code>SaClientService</code> value
     */
    public static DataFlowClientService getInstance()
    {
        synchronized(DataFlowClientService.class) {
            DataFlowClientService dataFlowClientService = VaadinSession.getCurrent().getAttribute(DataFlowClientService.class);
            if(dataFlowClientService == null) {
                dataFlowClientService = new DataFlowClientService();
                VaadinSession.getCurrent().setAttribute(DataFlowClientService.class,
                                                        dataFlowClientService);
            }
            return dataFlowClientService;
        }
    }
    /**
     * Get the service instance for the given SE descriptor.
     *
     * @param inEngine a <code>DecoratedStrategyEngine</code> value
     * @return a <code>DataFlowClientServiceInstance</code> value
     */
    public DataFlowClientServiceInstance getServiceInstance(DecoratedStrategyEngine inEngine)
    {
        throw new UnsupportedOperationException();
//        synchronized(instancesByName) {
//            DataFlowClientServiceInstance instance = instancesByName.get(inEngine.getName());
//            if(instance == null) {
//                instance = new DataFlowClientServiceInstance(inEngine);
//                instancesByName.put(inEngine.getName(),
//                                    instance);
//            }
//            return instance;
//        }
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
     * tracks data flow client instances by name
     */
    private Cache<String,DataFlowClientServiceInstance> instancesByName = CacheBuilder.newBuilder().build();
    /**
     * provides configuration for connecting to data flow engines
     */
    @Autowired
    private DataFlowConfiguration dataFlowConfiguration;
}
