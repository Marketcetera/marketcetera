package org.marketcetera.module;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;

/* $License$ */

/**
 * Specifies modules to create and/or start and data flows to create.
 * 
 * <p>This class is intended to be used as part of Spring configuration.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class DataRequestDescriptor
{
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
    {
        for(ModuleDescriptor moduleDescriptor : createModules) {
            ModuleURN instanceUrn;
            if(moduleDescriptor.getParameters().isEmpty()) {
                instanceUrn = moduleManager.createModule(moduleDescriptor.getModuleProviderUrn());
            } else {
                instanceUrn = moduleManager.createModule(moduleDescriptor.getModuleProviderUrn(),
                                                         moduleDescriptor.getParameters());
            }
            createdInstances.add(instanceUrn);
        }
        for(ModuleDescriptor moduleUrn : startModules) {
            moduleManager.start(moduleUrn.getModuleInstanceUrn());
        }
        for(List<DataRequest> dataRequest : dataRequests) {
            dataFlowIds.add(moduleManager.createDataFlow(dataRequest.toArray(new DataRequest[dataRequest.size()])));
        }
    }
    /**
     * Stop the object.
     */
    @PreDestroy
    public void stop()
    {
        for(DataFlowID dataFlowId : dataFlowIds) {
            try {
                moduleManager.cancel(dataFlowId);
            } catch (Exception ignored) {}
        }
        dataFlowIds.clear();
        for(ModuleDescriptor moduleDescriptor : startModules) {
            try {
                moduleManager.stop(moduleDescriptor.getModuleInstanceUrn());
            } catch (Exception ignored) {}
        }
        for(ModuleURN moduleURN : createdInstances) {
            try {
                moduleManager.deleteModule(moduleURN);
            } catch (Exception ignored) {}
            createdInstances.clear();
        }
    }
    /**
     * Get the moduleManager value.
     *
     * @return a <code>ModuleManager</code> value
     */
    public ModuleManager getModuleManager()
    {
        return moduleManager;
    }
    /**
     * Sets the moduleManager value.
     *
     * @param inModuleManager a <code>ModuleManager</code> value
     */
    public void setModuleManager(ModuleManager inModuleManager)
    {
        moduleManager = inModuleManager;
    }
    /**
     * Get the startModules value.
     *
     * @return a <code>List&lt;ModuleDescriptor&gt;</code> value
     */
    public List<ModuleDescriptor> getStartModules()
    {
        return startModules;
    }
    /**
     * Sets the startModules value.
     *
     * @param inStartModules a <code>List&lt;ModuleDescriptor&gt;</code> value
     */
    public void setStartModules(List<ModuleDescriptor> inStartModules)
    {
        startModules = inStartModules;
    }
    /**
     * Get the createModules value.
     *
     * @return a <code>List&lt;ModuleDescriptor&gt;</code> value
     */
    public List<ModuleDescriptor> getCreateModules()
    {
        return createModules;
    }
    /**
     * Sets the createModules value.
     *
     * @param inCreateModules a <code>List&lt;ModuleDescriptor&gt;</code> value
     */
    public void setCreateModules(List<ModuleDescriptor> inCreateModules)
    {
        createModules = inCreateModules;
    }
    /**
     * Get the dataRequests value.
     *
     * @return a <code>List&lt;List&lt;DataRequest&gt;&gt;</code> value
     */
    public List<List<DataRequest>> getDataRequests()
    {
        return dataRequests;
    }
    /**
     * Sets the dataRequests value.
     *
     * @param inDataRequests a <code>List&lt;List&lt;DataRequest&gt;&gt;</code> value
     */
    public void setDataRequests(List<List<DataRequest>> inDataRequests)
    {
        dataRequests = inDataRequests;
    }
    /**
     * list of modules to start
     */
    private List<ModuleDescriptor> startModules = Lists.newArrayList();
    /**
     * list of modules to create
     */
    private List<ModuleDescriptor> createModules = Lists.newArrayList();
    /**
     * list of data requests to create
     */
    private List<List<DataRequest>> dataRequests = Lists.newArrayList();
    /** 
     * instances created
     */
    private List<ModuleURN> createdInstances = Lists.newArrayList();
    /** 
     * data flows created
     */
    private List<DataFlowID> dataFlowIds = Lists.newArrayList();
    /** 
     * provides access to the module framework
     */
    @Autowired
    private ModuleManager moduleManager;
}
