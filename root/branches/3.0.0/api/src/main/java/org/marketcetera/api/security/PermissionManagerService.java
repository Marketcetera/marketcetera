package org.marketcetera.api.security;

import java.util.List;

import org.marketcetera.api.dao.Permission;

/* $License$ */

/**
 * Provides access to and management of <code>Permission</code> objects.
 *
 * @version $Id$
 * @since $Release$
 */
public interface PermissionManagerService
{
    /**
     * Gets the <code>Permission</code> with the given name.
     *
     * @param inName a <code>String</code> value
     * @return an <code>Permission</code> value
     */
    public Permission getPermissionByName(String inName);
    /**
     * Gets the <code>Permission</code> with the given id.
     *
     * @param inId a <code>long</code> value
     * @return an <code>Permission</code> value
     */
    public Permission getPermissionById(long inId);
    /**
     * Gets all <code>Permission</code> values.
     *
     * @return a <code>List&lt;Permission&gt;</code> value
     */
    public List<Permission> getAllPermissions();
    /**
     * Adds the given <code>Permission</code> value.
     *
     * @param inPermission an <code>Permission</code> value
     */
    public void addPermission(Permission inPermission);
    /**
     * Saves the given <code>Permission</code> value.
     *
     * @param inPermission an <code>Permission</code> value
     */
    public void savePermission(Permission inPermission);
    /**
     * Deletes the given <code>Permission</code> value.
     *
     * @param inPermission an <code>Permission</code> value
     */
    public void deletePermission(Permission inPermission);
}
