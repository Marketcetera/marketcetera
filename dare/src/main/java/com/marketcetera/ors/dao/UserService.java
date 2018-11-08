package com.marketcetera.ors.dao;

import java.util.List;

import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.trade.UserID;
import org.marketcetera.util.misc.ClassVersion;

import com.marketcetera.admin.User;
import com.marketcetera.ors.security.SimpleUser;

/* $License$ */

/**
 * Provides access to {@link SimpleUser} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: UserService.java 17266 2017-04-28 14:58:00Z colin $
 * @since 2.4.2
 */
@ClassVersion("$Id: UserService.java 17266 2017-04-28 14:58:00Z colin $")
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
     * Find a page of users.
     *
     * @param inPageRequest a <code>PageRequest</code> value
     * @return a <code>CollectionPageResponse&lt;User&gt;</code> value
     */
    CollectionPageResponse<User> findAll(PageRequest inPageRequest);
    /**
     * Finds a single user with the given id.
     *
     * @param inValue a <code>long</code> value
     * @return a <code>SimpleUser</code> value or <code>null</code>
     */
    SimpleUser findOne(long inValue);
    /**
     * Find the user associated with the given user id.
     *
     * @param inUserId a <code>UserID</code> value
     * @return a <code>User</code> value or <code>null</code>
     */
    User findByUserId(UserID inUserId);
}
