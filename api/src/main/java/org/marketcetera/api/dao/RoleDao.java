package org.marketcetera.api.dao;

import java.util.List;

/* $License$ */

/**
 * Provides data access for {@link Role} objects.
 *
 * @version $Id: RoleDao.java 82354 2012-05-11 17:46:11Z colin $
 * @since $Release$
 */
public interface RoleDao
{
    /**
     * Gets the <code>Role</code> corresponding to the given name.
     *
     * @param inName a <code>String</code> value
     * @return a <code>Role</code> value
     */
    public Role getByName(String inName);
    /**
     * Adds the given <code>Role</code> to the database.
     *
     * @param inData a <code>Role</code> value
     */
    public void add(Role inData);
    /**
     * Saves the given <code>Role</code> to the database.
     *
     * @param inData a <code>Role</code> value
     */
    public void save(Role inData);
    /**
     * Gets the <code>Role</code> corresponding to the given id.
     *
     * @param inId a <code>long</code> value
     * @return a <code>Role</code> value
     */
    public Role getById(long inId);
    /**
     * Gets all <code>Role</code> values.
     *
     * @return a <code>List&lt;Role&gt;</code> value
     */
    public List<Role> getAll();
    /**
     * Deletes the given <code>Role</code> from the database.
     *
     * @param inData a <code>Role</code> value
     */
    public void delete(Role inData);
}
