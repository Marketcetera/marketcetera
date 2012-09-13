package org.marketcetera.api.dao;

import java.util.List;

/* $License$ */

/**
 * Provides data access for {@link Permission} objects.
 *
 * @version $Id$
 * @since $Release$
 */
public interface PermissionDao
{
    /**
     * Adds the given <code>Permission</code> to the database.
     *
     * @param inData an <code>Permission</code> value
     */
    public void add(Permission inData);
    /**
     * Saves the given <code>Permission</code> to the database.
     *
     * @param inData an <code>Permission</code> value
     */
    public void save(Permission inData);
    /**
     * Gets the <code>Permission</code> corresponding to the given name.
     *
     * @param inName a <code>String</code> value
     * @return an <code>Permission</code> value
     */
    public Permission getByName(String inName);
    /**
     * Gets the <code>Permission</code> corresponding to the given id.
     *
     * @param inId a <code>long</code> value
     * @return an <code>Permission</code> value
     */
    public Permission getById(long inId);
    /**
     * Gets all <code>Permission</code> values.
     *
     * @return a <code>List&lt;Permission&gt;</code> value
     */
    public List<Permission> getAll();
    /**
     * Deletes the given <code>Permission</code> from the database.
     *
     * @param inPermission an <code>Permission</code> value
     */
    public void delete(Permission inPermission);
    /**
     * Indicates if the permission with the given id is in use by any {@link Role}.
     * 
     * <p>If there is permission with the given id, this method returns <code>false</code>.
     *
     * @param inId a <code>long</code> value
     * @return a <code>boolean</code> value
     */
    public boolean isInUseByRole(long inId);
}
