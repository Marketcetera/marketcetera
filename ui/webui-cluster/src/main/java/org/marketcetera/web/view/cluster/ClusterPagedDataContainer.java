package org.marketcetera.web.view.cluster;

import java.util.Collection;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.marketcetera.cluster.ClusterData;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.web.view.PagedDataContainer;
import org.marketcetera.web.view.PagedViewProvider;

/* $License$ */

/**
 * Provides a <code>PagedDataContainer</code> implementation for <code>ActiveFixSession</code> values.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class ClusterPagedDataContainer
        extends PagedDataContainer<ClusterData>
{
    /**
     * Create a new ClusterPagedDataContainer instance.
     *
     * @param inCollection a <code>Collection&lt;? extends ClusterData&gt;</code> value
     * @param inPagedViewProvider a <code>PagedViewProvider</code> value
     * @throws IllegalArgumentException if the container cannot be constructed
     */
    public ClusterPagedDataContainer(Collection<? extends ClusterData> inCollection,
                                     PagedViewProvider inPagedViewProvider)
            throws IllegalArgumentException
    {
        super(ClusterData.class,
              inCollection,
              inPagedViewProvider);
    }
    /**
     * Create a new ClusterPagedDataContainer instance.
     *
     * @param inPagedViewProvider a <code>PagedViewProvider</code> value
     * @throws IllegalArgumentException if the container cannot be constructed
     */
    public ClusterPagedDataContainer(PagedViewProvider inPagedViewProvider)
            throws IllegalArgumentException
    {
        super(ClusterData.class,
              inPagedViewProvider);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.PagedDataContainer#getDataContainerContents(org.marketcetera.core.PageRequest)
     */
    @Override
    protected CollectionPageResponse<ClusterData> getDataContainerContents(PageRequest inPageRequest)
    {
        Collection<ClusterData> ClusterDatas = ClusterClientService.getInstance().getClusterData();
        CollectionPageResponse<ClusterData> response = new CollectionPageResponse<>();
        response.getElements().addAll(ClusterDatas);
        response.setHasContent(!ClusterDatas.isEmpty());
        response.setPageMaxSize(inPageRequest.getPageSize());
        response.setPageNumber(inPageRequest.getPageNumber());
        response.setPageSize(ClusterDatas.size());
        response.setSortOrder(inPageRequest.getSortOrder());
        response.setTotalPages(1);
        response.setTotalSize(response.getPageSize());
        return response;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.PagedDataContainer#isDeepEquals(java.lang.Object, java.lang.Object)
     */
    @Override
    protected boolean isDeepEquals(ClusterData inO1,
                                   ClusterData inO2)
    {
        return new EqualsBuilder().append(inO1.getUuid(),inO2.getUuid()).isEquals();
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.PagedDataContainer#getDescription()
     */
    @Override
    protected String getDescription()
    {
        return "FixSession";
    }
    private static final long serialVersionUID = -1643583263489594148L;
}
