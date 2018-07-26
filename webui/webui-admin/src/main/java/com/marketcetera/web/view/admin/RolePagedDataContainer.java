package com.marketcetera.web.view.admin;

import java.util.Collection;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.marketcetera.admin.Role;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.web.services.AdminClientService;
import org.marketcetera.web.view.PagedDataContainer;
import org.marketcetera.web.view.PagedViewProvider;

/* $License$ */

/**
 * Provides a <code>PagedDataContainer</code> implementation for <code>Role</code> values.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class RolePagedDataContainer
        extends PagedDataContainer<Role>
{
    /**
     * Create a new RolePagedDataContainer instance.
     *
     * @param inCollection a <code>Collection&lt;? extends Role&gt;</code> value
     * @param inPagedViewProvider a <code>PagedViewProvider</code> value
     * @throws IllegalArgumentException if the container cannot be constructed
     */
    public RolePagedDataContainer(Collection<? extends Role> inCollection,
                                  PagedViewProvider inPagedViewProvider)
            throws IllegalArgumentException
    {
        super(Role.class,
              inCollection,
              inPagedViewProvider);
    }
    /**
     * Create a new RolePagedDataContainer instance.
     *
     * @param inPagedViewProvider a <code>PagedViewProvider</code> value
     * @throws IllegalArgumentException if the container cannot be constructed
     */
    public RolePagedDataContainer(PagedViewProvider inPagedViewProvider)
            throws IllegalArgumentException
    {
        super(Role.class,
              inPagedViewProvider);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.PagedDataContainer#getDataContainerContents(org.marketcetera.core.PageRequest)
     */
    @Override
    protected CollectionPageResponse<Role> getDataContainerContents(PageRequest inPageRequest)
    {
        return AdminClientService.getInstance().getRoles(inPageRequest);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.PagedDataContainer#isDeepEquals(java.lang.Object, java.lang.Object)
     */
    @Override
    protected boolean isDeepEquals(Role inO1,
                                   Role inO2)
    {
        return new EqualsBuilder().append(inO1.getName(),inO2.getName()).append(inO1.getDescription(),inO2.getDescription()).isEquals();
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.PagedDataContainer#getDescription()
     */
    @Override
    protected String getDescription()
    {
        return "Role";
    }
    private static final long serialVersionUID = -1643583263489594148L;
}
