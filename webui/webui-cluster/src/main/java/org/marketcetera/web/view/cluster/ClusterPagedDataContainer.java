package org.marketcetera.web.view.cluster;

import java.util.Collection;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.marketcetera.cluster.service.ClusterMember;
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
        extends PagedDataContainer<ClusterMember>
{
    /**
     * Create a new ClusterPagedDataContainer instance.
     *
     * @param inCollection a <code>Collection&lt;? extends ClusterMember&gt;</code> value
     * @param inPagedViewProvider a <code>PagedViewProvider</code> value
     * @throws IllegalArgumentException if the container cannot be constructed
     */
    public ClusterPagedDataContainer(Collection<? extends ClusterMember> inCollection,
                                     PagedViewProvider inPagedViewProvider)
            throws IllegalArgumentException
    {
        super(ClusterMember.class,
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
        super(ClusterMember.class,
              inPagedViewProvider);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.PagedDataContainer#getDataContainerContents(org.marketcetera.core.PageRequest)
     */
    @Override
    protected CollectionPageResponse<ClusterMember> getDataContainerContents(PageRequest inPageRequest)
    {
        Collection<ClusterMember> clusterMembers = ClusterClientService.getInstance().getClusterMembers();
        CollectionPageResponse<ClusterMember> response = new CollectionPageResponse<>();
        response.getElements().addAll(clusterMembers);
        response.setHasContent(!clusterMembers.isEmpty());
        response.setPageMaxSize(inPageRequest.getPageSize());
        response.setPageNumber(inPageRequest.getPageNumber());
        response.setPageSize(clusterMembers.size());
        response.setSortOrder(inPageRequest.getSortOrder());
        response.setTotalPages(1);
        response.setTotalSize(response.getPageSize());
        return response;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.PagedDataContainer#isDeepEquals(java.lang.Object, java.lang.Object)
     */
    @Override
    protected boolean isDeepEquals(ClusterMember inO1,
                                   ClusterMember inO2)
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
