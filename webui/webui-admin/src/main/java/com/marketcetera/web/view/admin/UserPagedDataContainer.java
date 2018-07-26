package com.marketcetera.web.view.admin;

import java.util.Collection;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.marketcetera.admin.User;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.web.services.AdminClientService;
import org.marketcetera.web.view.PagedDataContainer;
import org.marketcetera.web.view.PagedViewProvider;

/* $License$ */

/**
 * Provides a <code>PagedDataContainer</code> implementation for <code>User</code> values.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class UserPagedDataContainer
        extends PagedDataContainer<User>
{
    /**
     * Create a new UserPagedDataContainer instance.
     *
     * @param inCollection a <code>Collection&lt;? extends User&gt;</code> value
     * @param inPagedViewProvider a <code>PagedViewProvider</code> value
     * @throws IllegalArgumentException if the container cannot be constructed
     */
    public UserPagedDataContainer(Collection<? extends User> inCollection,
                                  PagedViewProvider inPagedViewProvider)
            throws IllegalArgumentException
    {
        super(User.class,
              inCollection,
              inPagedViewProvider);
    }
    /**
     * Create a new UserPagedDataContainer instance.
     *
     * @param inPagedViewProvider a <code>PagedViewProvider</code> value
     * @throws IllegalArgumentException if the container cannot be constructed
     */
    public UserPagedDataContainer(PagedViewProvider inPagedViewProvider)
            throws IllegalArgumentException
    {
        super(User.class,
              inPagedViewProvider);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.PagedDataContainer#getDataContainerContents(org.marketcetera.core.PageRequest)
     */
    @Override
    protected CollectionPageResponse<User> getDataContainerContents(PageRequest inPageRequest)
    {
        return AdminClientService.getInstance().getUsers(inPageRequest);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.PagedDataContainer#isDeepEquals(java.lang.Object, java.lang.Object)
     */
    @Override
    protected boolean isDeepEquals(User inO1,
                                   User inO2)
    {
        return new EqualsBuilder().append(inO1.getName(),inO2.getName()).append(inO1.getDescription(),inO2.getDescription()).isEquals();
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.PagedDataContainer#getDescription()
     */
    @Override
    protected String getDescription()
    {
        return "User";
    }
    private static final long serialVersionUID = 8528851564601678785L;
}
