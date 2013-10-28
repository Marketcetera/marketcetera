package org.marketcetera.ors.dao;

import java.util.List;

import org.marketcetera.ors.security.SimpleUser;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides access to {@link SimpleUser} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface UserService
{
    /**
     * Saves the given user to the datastore.
     *
     * @param inUser a <code>SimpleUser</code> value
     * @return a <code>SimpleUser</code> value
     */
    SimpleUser save(SimpleUser inUser);
    /**
     * Gets the user with the given name.
     *
     * @param inUsername a <code>String</code> value
     * @return a <code>SimpleUser</code> value
     */
    SimpleUser findByName(String inUsername);
    /**
     * Updates the user data for the user with the given name.
     *
     * @param inUsername a <code>String</code> value
     * @param inUserData a <code>String</code> value
     */
    void updateUserDataByName(String inUsername,
                              String inUserData);
    /**
     * Updates the active/inactive status for the user with the given name.
     *
     * @param inUsername a <code>String</code> value
     * @param inIsActive a <code>boolean</code> value
     */
    void updateUserActiveStatus(String inUsername,
                                boolean inIsActive);
    /**
     * Updates the superuser status for the user with the given name.
     *
     * @param inUsername a <code>String</code> value
     * @param inIsSuperuser a <code>boolean</code> value
     */
    void updateSuperUser(String inUsername,
                         boolean inIsSuperuser);
    /**
     * Lists all users taking into account the given optional filters.
     *
     * @param inNameFilter a <code>String</code> value
     * @param inActiveFilter a <code>Boolean</code> value
     * @return a <code>List&lt;SimpleUser&gt;</code> value
     */
    List<SimpleUser> listUsers(String inNameFilter,
                               Boolean inActiveFilter);
    /**
     * Deletes the given user.
     *
     * @param inUser a <code>SimpleUser</code> value
     */
    void delete(SimpleUser inUser);
    /**
     * Finds all current users.
     *
     * @return a <code>List&lt;SimpleUser&gt;</code> value
     */
    List<SimpleUser> findAll();
    /**
     * Finds a single user with the given id.
     *
     * @param inValue a <code>long</code> value
     * @return a <code>SimpleUser</code> value or <code>null</code>
     */
    SimpleUser findOne(long inValue);
}
