package org.marketcetera.api.security;

import java.util.List;

/**
 * @author <a href="mailto:topping@codehaus.org">Brian Topping</a>
 * @version $Id$
 * @date 8/20/12 3:46 PM
 */

public interface UserManagerService {
    /**
     * Gets the <code>User</code> corresponding to the given username.
     *
     * @param inUsername a <code>String</code> value
     * @return a <code>User</code> value
     */
    public User getUserByName(String inUsername);
    /**
     * Adds the given <code>User</code> to the database.
     *
     * @param inData a <code>User</code> value
     */
    public void addUser(User inData);
    /**
     * Saves the given <code>User</code> to the database.
     *
     * @param inData a <code>User</code> value
     */
    public void saveUser(User inData);
    /**
     * Deletes the given <code>User</code> from the database.
     *
     * @param inData a <code>User</code> value
     */
    public void deleteUser(User inData);
    /**
     * Gets the <code>User</code> corresponding to the given id.
     *
     * @param inId a <code>long</code> value
     * @return a <code>User</code> value
     */
    public User getUserById(long inId);
    /**
     * Gets all <code>User</code> values.
     *
     * @return a <code>List&lt;User&gt;</code> value
     */
    public List<User> getAllUsers();

}
