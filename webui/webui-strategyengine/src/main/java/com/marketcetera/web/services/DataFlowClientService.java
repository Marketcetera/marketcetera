package com.marketcetera.web.services;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.Maps;
import com.marketcetera.web.view.dataflows.DecoratedStrategyEngine;
import com.vaadin.server.VaadinSession;

/* $License$ */

/**
 * Provides access to a Strategy Agent.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class DataFlowClientService
{
    /**
     * Get the <code>SaClientService</code> instance for the current session.
     *
     * @return a <code>SaClientService</code> value
     */
    public static DataFlowClientService getInstance()
    {
        synchronized(DataFlowClientService.class) {
            DataFlowClientService saClientService = VaadinSession.getCurrent().getAttribute(DataFlowClientService.class);
            if(saClientService == null) {
                saClientService = new DataFlowClientService();
                VaadinSession.getCurrent().setAttribute(DataFlowClientService.class,
                                                        saClientService);
            }
            return saClientService;
        }
    }
    /**
     * Get the service instance for the given SE descriptor.
     *
     * @param inEngine a <code>DecoratedStrategyEngine</code> value
     * @return an <code>SAClientServiceInstance</code> value
     */
    public DataFlowClientServiceInstance getServiceInstance(DecoratedStrategyEngine inEngine)
    {
        synchronized(instancesByName) {
            DataFlowClientServiceInstance instance = instancesByName.get(inEngine.getName());
            if(instance == null) {
                instance = new DataFlowClientServiceInstance(inEngine);
                instancesByName.put(inEngine.getName(),
                                    instance);
            }
            return instance;
        }
    }
    /**
     * Get the strategy engines known to the system for this user.
     *
     * @return a <code>Collection&lt;DecoratedStrategyEngine&gt;</code> value
     */
    public Collection<DecoratedStrategyEngine> getStrategyEngines()
    {
        throw new UnsupportedOperationException();
//        List<DecoratedStrategyEngine> results = Lists.newArrayList();
//        UserAttribute userAttribute = adminClient.getUserAttribute(VaadinSession.getCurrent().getAttribute(SessionUser.class).getUsername(),
//                                                                   UserAttributeType.STRATEGY_ENGINES);
//        if(userAttribute != null) {
//            String rawValue = userAttribute.getAttribute();
//            try {
//                Properties engines = Util.propertiesFromString(rawValue);
//                for(Map.Entry<Object,Object> entry : engines.entrySet()) {
//                    String key = String.valueOf(entry.getKey());
//                    String value = String.valueOf(entry.getValue());
//                    DecoratedStrategyEngine engine = new DecoratedStrategyEngine();
//                    engine.setName(key);
//                    String[] components = value.split("\\|");
//                    engine.setHostname(components[0]);
//                    engine.setPort(Integer.parseInt(components[1]));
//                    engine.setUrl(components[2]);
//                    results.add(engine);
//                }
//            } catch (Exception e) {
//                SLF4JLoggerProxy.warn(this,
//                                      e,
//                                      "Unable to translate {} to engine list",
//                                      rawValue);
//            }
//        }
//        return results;
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
     * tracks SAClient instances by name
     */
    private final Map<String,DataFlowClientServiceInstance> instancesByName = Maps.newHashMap();
}
