package org.marketcetera.web.view.dataflows;

import java.util.Collection;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.web.service.dataflow.DataFlowClientService;
import org.marketcetera.web.service.dataflow.DataFlowClientServiceInstance;
import org.marketcetera.web.view.PagedDataContainer;
import org.marketcetera.web.view.PagedViewProvider;

/* $License$ */

/**
 * Provides a paged data container for <code>DecoratedStrategyEngine</code> objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class StrategyEnginePagedDataContainer
        extends PagedDataContainer<DecoratedStrategyEngine>
{
    /**
     * Create a new StrategyEnginePagedDataContainer instance.
     *
     * @param inCollection
     * @param inPagedViewProvider
     * @throws IllegalArgumentException
     */
    public StrategyEnginePagedDataContainer(Collection<? extends DecoratedStrategyEngine> inCollection,
                                            PagedViewProvider inPagedViewProvider)  
            throws IllegalArgumentException
    {
        super(DecoratedStrategyEngine.class,
              inCollection,
              inPagedViewProvider);
    }
    /**
     * Create a new StrategyEnginePagedDataContainer instance.
     *
     * @param inPagedViewProvider
     * @throws IllegalArgumentException
     */
    public StrategyEnginePagedDataContainer(PagedViewProvider inPagedViewProvider)
            throws IllegalArgumentException
    {
        super(DecoratedStrategyEngine.class,
              inPagedViewProvider);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.PagedDataContainer#getDataContainerContents(org.marketcetera.persist.PageRequest)
     */
    @Override
    protected CollectionPageResponse<DecoratedStrategyEngine> getDataContainerContents(PageRequest inPageRequest)
    {
        Collection<DecoratedStrategyEngine> engines = DataFlowClientService.getInstance().getStrategyEngines();
        for(DecoratedStrategyEngine engine : engines) {
            DataFlowClientServiceInstance serviceInstance = DataFlowClientService.getInstance().getServiceInstance(engine);
            engine.setIsConnected(serviceInstance.isRunning());
        }
        CollectionPageResponse<DecoratedStrategyEngine> pageResponse = new CollectionPageResponse<>();
        pageResponse.setElements(engines);
        pageResponse.setPageMaxSize(inPageRequest.getPageSize());
        pageResponse.setPageNumber(0);
        pageResponse.setPageSize(engines.size());
        pageResponse.setTotalPages(1);
        pageResponse.setTotalSize(engines.size());
        return pageResponse;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.PagedDataContainer#isDeepEquals(java.lang.Object, java.lang.Object)
     */
    @Override
    protected boolean isDeepEquals(DecoratedStrategyEngine inO1,
                                   DecoratedStrategyEngine inO2)
    {
        return new EqualsBuilder().append(inO1.getName(),inO2.getName()).append(inO1.getHostname(),inO2.getHostname()).append(inO1.getPort(),inO2.getPort()).append(inO1.isConnected(),inO2.isConnected()).isEquals();
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.PagedDataContainer#getDescription()
     */
    @Override
    protected String getDescription()
    {
        return "Strategy Engine";
    }
    private static final long serialVersionUID = 7331255596822309381L;
}
