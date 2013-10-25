package org.marketcetera.ors.dao;

import java.util.List;

import org.marketcetera.ors.security.SimpleUser;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface UserService
{
    /**
     * 
     *
     *
     * @param inUser
     * @return
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
     * @return a <code>SimpleUser</code> value
     */
    SimpleUser updateUserActiveStatus(String inUsername,
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
     *
     *
     * @param inUser
     */
    void delete(SimpleUser inUser);
    /**
     *
     *
     * @return
     */
    List<SimpleUser> findAll();
}
