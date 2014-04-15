package org.marketcetera.saclient.rpc;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.marketcetera.module.ModuleInfo;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.saclient.CreateStrategyParameters;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/* $License$ */

/**
 * Provides an implementation of <code>SAClientServiceAdapter</code> for testing.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MockSAClientServiceAdapter
        implements SAServiceAdapter
{
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.rpc.SAClientServiceAdapter#getProviders()
     */
    @Override
    public List<ModuleURN> getProviders()
    {
        providersCount.incrementAndGet();
        return providersToReturn;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.rpc.SAClientServiceAdapter#getInstances(org.marketcetera.module.ModuleURN)
     */
    @Override
    public List<ModuleURN> getInstances(ModuleURN inProvider)
    {
        instancesRequests.add(inProvider);
        return instancesToReturn;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.rpc.SAClientServiceAdapter#getModuleInfo(org.marketcetera.module.ModuleURN)
     */
    @Override
    public ModuleInfo getModuleInfo(ModuleURN inInstance)
    {
        moduleInfoRequests.add(inInstance);
        return moduleInfoToReturn;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.rpc.SAClientServiceAdapter#start(org.marketcetera.module.ModuleURN)
     */
    @Override
    public void start(ModuleURN inInstance)
    {
        startRequests.add(inInstance);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.rpc.SAClientServiceAdapter#stop(org.marketcetera.module.ModuleURN)
     */
    @Override
    public void stop(ModuleURN inInstance)
    {
        stopRequests.add(inInstance);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.rpc.SAClientServiceAdapter#delete(org.marketcetera.module.ModuleURN)
     */
    @Override
    public void delete(ModuleURN inInstance)
    {
        deleteRequests.add(inInstance);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.rpc.SAClientServiceAdapter#getProperties(org.marketcetera.module.ModuleURN)
     */
    @Override
    public Map<String,Object> getProperties(ModuleURN inInstance)
    {
        propertiesRequests.add(inInstance);
        return propertiesToReturn;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.rpc.SAClientServiceAdapter#setProperties(org.marketcetera.module.ModuleURN, java.util.Map)
     */
    @Override
    public Map<String,Object> setProperties(ModuleURN inInstance,
                                            Map<String,Object> inProperties)
    {
        propertiesRequests.add(inInstance);
        return inProperties;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.rpc.SAClientServiceAdapter#createStrategy(org.marketcetera.saclient.CreateStrategyParameters)
     */
    @Override
    public ModuleURN createStrategy(CreateStrategyParameters inParameters)
    {
        createStrategyRequests.add(inParameters);
        return createModuleURNToReturn;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.rpc.SAClientServiceAdapter#getStrategyCreateParms(org.marketcetera.module.ModuleURN)
     */
    @Override
    public CreateStrategyParameters getStrategyCreateParms(ModuleURN inInstance)
    {
        strategyCreateParmsRequests.add(inInstance);
        return parametersToReturn;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.saclient.rpc.SAClientServiceAdapter#sendData(java.lang.Object)
     */
    @Override
    public void sendData(Object inData)
    {
        sentData = inData;
    }
    /**
     * Get the providersToReturn value.
     *
     * @return a <code>List&lt;ModuleURN&gt;</code> value
     */
    public List<ModuleURN> getProvidersToReturn()
    {
        return providersToReturn;
    }
    /**
     * Get the providersCount value.
     *
     * @return an <code>AtomicInteger</code> value
     */
    public AtomicInteger getProvidersCount()
    {
        return providersCount;
    }
    /**
     * Get the instancesToReturn value.
     *
     * @return a <code>List&lt;ModuleURN&gt;</code> value
     */
    public List<ModuleURN> getInstancesToReturn()
    {
        return instancesToReturn;
    }
    /**
     * Get the instancesRequests value.
     *
     * @return a <code>List&lt;ModuleURN&gt;</code> value
     */
    public List<ModuleURN> getInstancesRequests()
    {
        return instancesRequests;
    }
    /**
     * Get the moduleInfoToReturn value.
     *
     * @return a <code>ModuleInfo</code> value
     */
    public ModuleInfo getModuleInfoToReturn()
    {
        return moduleInfoToReturn;
    }
    /**
     * Sets the moduleInfoToReturn value.
     *
     * @param inModuleInfoToReturn a <code>ModuleInfo</code> value
     */
    public void setModuleInfoToReturn(ModuleInfo inModuleInfoToReturn)
    {
        moduleInfoToReturn = inModuleInfoToReturn;
    }
    /**
     * Get the moduleInfoRequests value.
     *
     * @return a <code>List&lt;ModuleURN&gt;</code> value
     */
    public List<ModuleURN> getModuleInfoRequests()
    {
        return moduleInfoRequests;
    }
    /**
     * Get the startRequests value.
     *
     * @return a <code>List&lt;ModuleURN&gt;</code> value
     */
    public List<ModuleURN> getStartRequests()
    {
        return startRequests;
    }
    /**
     * Get the stopRequests value.
     *
     * @return a <code>List&lt;ModuleURN&gt;</code> value
     */
    public List<ModuleURN> getStopRequests()
    {
        return stopRequests;
    }
    /**
     * Get the deleteRequests value.
     *
     * @return a <code>List&lt;ModuleURN&gt;</code> value
     */
    public List<ModuleURN> getDeleteRequests()
    {
        return deleteRequests;
    }
    /**
     * Get the propertiesRequests value.
     *
     * @return a <code>List&lt;ModuleURN&gt;</code> value
     */
    public List<ModuleURN> getPropertiesRequests()
    {
        return propertiesRequests;
    }
    /**
     * Sets the propertiesRequests value.
     *
     * @param inPropertiesRequests a <code>List&lt;ModuleURN&gt;</code> value
     */
    public void setPropertiesRequests(List<ModuleURN> inPropertiesRequests)
    {
        propertiesRequests = inPropertiesRequests;
    }
    /**
     * Get the propertiesToReturn value.
     *
     * @return a <code>Map&lt;String,Object&gt;</code> value
     */
    public Map<String,Object> getPropertiesToReturn()
    {
        return propertiesToReturn;
    }
    /**
     * Get the createModuleURNToReturn value.
     *
     * @return a <code>ModuleURN</code> value
     */
    public ModuleURN getCreateModuleURNToReturn()
    {
        return createModuleURNToReturn;
    }
    /**
     * Sets the createModuleURNToReturn value.
     *
     * @param inCreateModuleURNToReturn a <code>ModuleURN</code> value
     */
    public void setCreateModuleURNToReturn(ModuleURN inCreateModuleURNToReturn)
    {
        createModuleURNToReturn = inCreateModuleURNToReturn;
    }
    /**
     * Get the createStrategyRequests value.
     *
     * @return a <code>List&lt;CreateStrategyParameters&gt;</code> value
     */
    public List<CreateStrategyParameters> getCreateStrategyRequests()
    {
        return createStrategyRequests;
    }
    /**
     * Get the parametersToReturn value.
     *
     * @return a <code>CreateStrategyParameters</code> value
     */
    public CreateStrategyParameters getParametersToReturn()
    {
        return parametersToReturn;
    }
    /**
     * Sets the parametersToReturn value.
     *
     * @param inParametersToReturn a <code>CreateStrategyParameters</code> value
     */
    public void setParametersToReturn(CreateStrategyParameters inParametersToReturn)
    {
        parametersToReturn = inParametersToReturn;
    }
    /**
     * Get the strategyCreateParmsRequests value.
     *
     * @return a <code>List&lt;ModuleURN&gt;</code> value
     */
    public List<ModuleURN> getStrategyCreateParmsRequests()
    {
        return strategyCreateParmsRequests;
    }
    /**
     * Get the sentData value.
     *
     * @return an <code>Object</code> value
     */
    public Object getSentData()
    {
        return sentData;
    }
    /**
     * Sets the sentData value.
     *
     * @param inSentData an <code>Object</code> value
     */
    public void setSentData(Object inSentData)
    {
        sentData = inSentData;
    }
    /**
     * Resets test values.
     */
    public void reset()
    {
        providersToReturn.clear();
        providersCount.set(0);
        instancesToReturn.clear();
        instancesRequests.clear();
        moduleInfoRequests.clear();
        moduleInfoToReturn = null;
        startRequests.clear();
        stopRequests.clear();
        deleteRequests.clear();
        propertiesRequests.clear();
        propertiesToReturn.clear();
        createStrategyRequests.clear();
        createModuleURNToReturn = null;
        parametersToReturn = null;
        strategyCreateParmsRequests.clear();
        sentData = null;
    }
    /**
     * provides to return value
     */
    private final List<ModuleURN> providersToReturn = Lists.newArrayList();
    /**
     * count of providers invocation
     */
    private final AtomicInteger providersCount = new AtomicInteger(0);
    /**
     * instances to return value
     */
    private final List<ModuleURN> instancesToReturn = Lists.newArrayList();
    /**
     * instances requests
     */
    private final List<ModuleURN> instancesRequests = Lists.newArrayList();
    /**
     * module info requests
     */
    private final List<ModuleURN> moduleInfoRequests = Lists.newArrayList();
    /**
     * module info to return
     */
    private ModuleInfo moduleInfoToReturn;
    /**
     * start requests value
     */
    private final List<ModuleURN> startRequests = Lists.newArrayList();
    /**
     * stop requests value
     */
    private final List<ModuleURN> stopRequests = Lists.newArrayList();
    /**
     * delete requests value
     */
    private final List<ModuleURN> deleteRequests = Lists.newArrayList();
    /**
     * properties requests value
     */
    private List<ModuleURN> propertiesRequests = Lists.newArrayList();
    /**
     * properties to return value
     */
    private final Map<String,Object> propertiesToReturn = Maps.newHashMap();
    /**
     * create strategy requests value
     */
    private final List<CreateStrategyParameters> createStrategyRequests = Lists.newArrayList();
    /**
     * create module urn to return
     */
    private ModuleURN createModuleURNToReturn;
    /**
     * create strategy parameters to return
     */
    private CreateStrategyParameters parametersToReturn;
    /**
     * create strategy params requests
     */
    private final List<ModuleURN> strategyCreateParmsRequests = Lists.newArrayList();
    /**
     * object sent to {@link #sendData(Object)}
     */
    private Object sentData;
}
