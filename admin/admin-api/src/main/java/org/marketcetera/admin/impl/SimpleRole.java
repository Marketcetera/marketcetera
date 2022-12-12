package org.marketcetera.admin.impl;

import java.util.HashSet;
import java.util.Set;

import org.marketcetera.admin.MutableRole;
import org.marketcetera.admin.Permission;
import org.marketcetera.admin.Role;
import org.marketcetera.admin.User;
import org.marketcetera.persist.NDEntityBase;

/* $License$ */

/**
 * Provides a simple {@link Role} implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SimpleRole
        extends NDEntityBase
        implements MutableRole
{
    /**
     * Create a new SimpleRole instance.
     */
    public SimpleRole() {}
    /**
     * Create a new SimpleRole instance.
     *
     * @param inRole a <code>Role</code> value
     */
    public SimpleRole(Role inRole)
    {
        setDescription(inRole.getDescription());
        setId(inRole.getId());
        setLastUpdated(inRole.getLastUpdated());
        setName(inRole.getName());
        setPermissions(inRole.getPermissions());
        setSubjects(inRole.getSubjects());
        setUpdateCount(inRole.getUpdateCount());
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.Role#getPermissions()
     */
    @Override
    public Set<Permission> getPermissions()
    {
        return permissions;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.admin.Role#getSubjects()
     */
    @Override
    public Set<User> getSubjects()
    {
        return subjects;
    }
    /**
     * Sets the permissions value.
     *
     * @param inPermissions a <code>Set&lt;Permission&gt;</code> value
     */
    public void setPermissions(Set<Permission> inPermissions)
    {
        permissions = inPermissions;
    }
    /**
     * Sets the subjects value.
     *
     * @param inSubjects a <code>Set&lt;User&gt;</code> value
     */
    public void setSubjects(Set<User> inSubjects)
    {
        subjects = inSubjects;
    }
    /**
     * permission values
     */
    private Set<Permission> permissions = new HashSet<>();
    /**
     * subject values
     */
    private Set<User> subjects = new HashSet<>();
    private static final long serialVersionUID = 8378759911980134527L;
}
