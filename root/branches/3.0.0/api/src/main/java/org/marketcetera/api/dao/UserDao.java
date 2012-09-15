package org.marketcetera.api.dao;

import java.util.List;

import org.marketcetera.api.security.User;

/* $License$ */

/**
 * Provides data access for {@link User} objects.
 *
 * @version $Id$
 * @since $Release$
 */
public interface UserDao {
    /**
     * Gets the <code>User</code> corresponding to the given username.
     *
     * @param inUsername a <code>String</code> value
     * @return a <code>MutableUser</code> value
     */
    public MutableUser getByName(String inUsername);

    /**
     * Adds the given <code>User</code> to the database.
     *
     * @param inData a <code>User</code> value
     */
    public void add(User inData);

    /**
     * Saves the given <code>User</code> to the database.
     *
     * @param inData a <code>User</code> value
     */
    public void save(User inData);
    /**
     * Deletes the given <code>User</code> from the database.
     *
     * @param inData a <code>User</code> value
     */
    public void delete(User inData);
    /**
     * Gets the <code>User</code> corresponding to the given id.
     *
     * @param inId a <code>long</code> value
     * @return a <code>MutableUser</code> value
     */
    public MutableUser getById(long inId);
    /**
     * Gets all <code>User</code> values.
     *
     * @return a <code>List&lt;MutableUser&gt;</code> value
     */
    public List<MutableUser> getAll();
}
