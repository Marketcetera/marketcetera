package com.marketcetera.web.view.admin;

import java.util.Collection;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.marketcetera.admin.Permission;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;

import com.marketcetera.web.services.AdminClientService;
import com.marketcetera.web.view.PagedDataContainer;
import com.marketcetera.web.view.PagedViewProvider;

/* $License$ */

/**
 * Provides a <code>PagedDataContainer</code> implementation for <code>Permission</code> values.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class PermissionPagedDataContainer
        extends PagedDataContainer<Permission>
{
    /**
     * Create a new PermissionPagedDataContainer instance.
     *
     * @param inCollection a <code>Collection&lt;? extends Permission&gt;</code> value
     * @param inPagedViewProvider a <code>PagedViewProvider</code> value
     * @throws IllegalArgumentException if the container cannot be constructed
     */
    public PermissionPagedDataContainer(Collection<? extends Permission> inCollection,
                                        PagedViewProvider inPagedViewProvider)
            throws IllegalArgumentException
    {
        super(Permission.class,
              inCollection,
              inPagedViewProvider);
    }
    /**
     * Create a new PermissionPagedDataContainer instance.
     *
     * @param inPagedViewProvider a <code>PagedViewProvider</code> value
     * @throws IllegalArgumentException if the container cannot be constructed
     */
    public PermissionPagedDataContainer(PagedViewProvider inPagedViewProvider)
            throws IllegalArgumentException
    {
        super(Permission.class,
              inPagedViewProvider);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.PagedDataContainer#getDataContainerContents(org.marketcetera.core.PageRequest)
     */
    @Override
    protected CollectionPageResponse<Permission> getDataContainerContents(PageRequest inPageRequest)
    {
        return AdminClientService.getInstance().getPermissions(inPageRequest);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.PagedDataContainer#isDeepEquals(java.lang.Object, java.lang.Object)
     */
    @Override
    protected boolean isDeepEquals(Permission inO1,
                                   Permission inO2)
    {
        return new EqualsBuilder().append(inO1.getName(),inO2.getName()).append(inO1.getDescription(),inO2.getDescription()).isEquals();
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.PagedDataContainer#getDescription()
     */
    @Override
    protected String getDescription()
    {
        return "Permission";
    }
    private static final long serialVersionUID = -906364507473185980L;
}
