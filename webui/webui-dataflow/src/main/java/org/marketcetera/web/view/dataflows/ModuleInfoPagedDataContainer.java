package org.marketcetera.web.view.dataflows;

import java.util.Collection;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.marketcetera.module.ModuleInfo;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.web.service.dataflow.DataFlowClientService;
import org.marketcetera.web.service.dataflow.DataFlowClientServiceInstance;
import org.marketcetera.web.view.PagedDataContainer;
import org.marketcetera.web.view.PagedViewProvider;

/* $License$ */

/**
 * Provides a data container for strategy engine module info.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class ModuleInfoPagedDataContainer
        extends PagedDataContainer<DecoratedModuleInfo>
{
    /**
     * Create a new ModuleInfoPagedDataContainer instance.
     *
     * @param inModuleView a <code>ModuleView</code> value
     * @param inStrategyEngine a <code>DecoratedStrategyEngine</code> value
     * @throws IllegalArgumentException if the container cannot be constructed
     */
    public ModuleInfoPagedDataContainer(ModuleView inModuleView,
                                        DecoratedStrategyEngine inStrategyEngine)
            throws IllegalArgumentException
    {
        super(DecoratedModuleInfo.class,
              inModuleView);
        strategyEngine = inStrategyEngine;
    }
    /**
     * Create a new ModuleInfoPagedDataContainer instance.
     *
     * @param inPagedViewProvider a <code>PagedViewProvider</code> value
     * @param inStrategyEngine a <code>DecoratedStrategyEngine</code> value
     * @throws IllegalArgumentException if the container cannot be constructed
     */
    public ModuleInfoPagedDataContainer(PagedViewProvider inPagedViewProvider,
                                        DecoratedStrategyEngine inStrategyEngine)
            throws IllegalArgumentException
    {
        super(DecoratedModuleInfo.class,
              inPagedViewProvider);
        strategyEngine = inStrategyEngine;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.PagedDataContainer#getDataContainerContents(org.marketcetera.persist.PageRequest)
     */
    @Override
    protected CollectionPageResponse<DecoratedModuleInfo> getDataContainerContents(PageRequest inPageRequest)
    {
        CollectionPageResponse<DecoratedModuleInfo> result = new CollectionPageResponse<>();
        DataFlowClientServiceInstance seServiceInstance = DataFlowClientService.getInstance().getServiceInstance(strategyEngine);
        Collection<ModuleURN> providers = seServiceInstance.getProviders();
        for(ModuleURN providerUrn : providers) {
            Collection<ModuleURN> instances = seServiceInstance.getInstances(providerUrn);
            for(ModuleURN instanceUrn : instances) {
                ModuleInfo moduleInfo = seServiceInstance.getModuleInfo(instanceUrn);
                result.getElements().add(new DecoratedModuleInfo(instanceUrn,
                                                                 moduleInfo));
            }
        }
        // TODO apply sort and paging manually
        result.setPageMaxSize(result.getElements().size());
        result.setPageNumber(0);
        result.setPageSize(result.getElements().size());
        result.setTotalPages(1);
        result.setTotalSize(result.getElements().size());
        return result;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.PagedDataContainer#isDeepEquals(java.lang.Object, java.lang.Object)
     */
    @Override
    protected boolean isDeepEquals(DecoratedModuleInfo inO1,
                                   DecoratedModuleInfo inO2)
    {
        return new EqualsBuilder().append(inO1.getUrn(),inO2.getUrn()).append(inO1.getState(),inO2.getState()).isEquals();
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.PagedDataContainer#getDescription()
     */
    @Override
    protected String getDescription()
    {
        return "Module Info";
    }
    /**
     * strategy engine that owns these data flows
     */
    private final DecoratedStrategyEngine strategyEngine;
    private static final long serialVersionUID = -3586258123638581426L;
}
