package org.marketcetera.api.security;

import java.util.List;

import org.marketcetera.api.dao.Role;

/* $License$ */

/**
 * Provides access to and management of <code>Role</code> objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface RoleManagerService
{
    /**
     * Gets the <code>Role</code> with the given name.
     *
     * @param inName a <code>String</code> value
     * @return an <code>Role</code> value
     */
    public Role getRoleByName(String inName);
    /**
     * Gets the <code>Role</code> with the given id.
     *
     * @param inId a <code>long</code> value
     * @return an <code>Role</code> value
     */
    public Role getRoleById(long inId);
    /**
     * Gets all <code>Role</code> values.
     *
     * @return a <code>List&lt;Role&gt;</code> value
     */
    public List<Role> getAllRoles();
    /**
     * Adds the given <code>Role</code> value.
     *
     * @param inRole an <code>Role</code> value
     */
    public void addRole(Role inRole);
    /**
     * Saves the given <code>Role</code> value.
     *
     * @param inRole an <code>Role</code> value
     */
    public void saveRole(Role inRole);
    /**
     * Deletes the given <code>Role</code> value.
     *
     * @param inRole an <code>Role</code> value
     */
    public void deleteRole(Role inRole);
}
