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
     * @param inData a <code>Permission</code> value
     */
    public void add(Permission inData);
    /**
     * Saves the given <code>Permission</code> to the database.
     *
     * @param inData a <code>Permission</code> value
     */
    public void save(Permission inData);
    /**
     * Gets the <code>Permission</code> corresponding to the given name.
     *
     * @param inName a <code>String</code> value
     * @return a <code>MutablePermission</code> value
     */
    public MutablePermission getByName(String inName);
    /**
     * Gets the <code>Permission</code> corresponding to the given id.
     *
     * @param inId a <code>long</code> value
     * @return a <code>MutablePermission</code> value
     */
    public MutablePermission getById(long inId);
    /**
     * Gets all <code>Permission</code> values.
     *
     * @return a <code>List&lt;MutablePermission&gt;</code> value
     */
    public List<MutablePermission> getAll();
    /**
     * Deletes the given <code>Permission</code> from the database.
     *
     * @param inPermission a <code>Permission</code> value
     */
    public void delete(Permission inPermission);
    /**
     * Gets the <code>Permission</code> objects assigned to the given user id.
     *
     * @param inUserId a <code>long</code> value
     * @return a <code>List&lt;Permission&gt;</code> value
     */
    public List<Permission> getAllByUserId(long inUserId);
}
