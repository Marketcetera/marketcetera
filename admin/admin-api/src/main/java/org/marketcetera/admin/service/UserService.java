package org.marketcetera.admin.service;

import java.util.List;

import org.marketcetera.admin.User;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.trade.UserID;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides access to {@link User} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: UserService.java 17339 2017-08-10 02:14:34Z colin $
 * @since 2.4.2
 */
@ClassVersion("$Id: UserService.java 17339 2017-08-10 02:14:34Z colin $")
public interface UserService
{
    /**
     * Saves the given user to the datastore.
     *
     * @param inUser a <code>User</code> value
     * @return a <code>User</code> value
     */
    User save(User inUser);
    /**
     * Gets the user with the given name.
     *
     * @param inUsername a <code>String</code> value
     * @return a <code>User</code> value
     */
    User findByName(String inUsername);
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
     * @return a <code>List&lt;? extends User&gt;</code> value
     */
    List<? extends User> listUsers(String inNameFilter,
                                   Boolean inActiveFilter);
    /**
     * Deletes the given user.
     *
     * @param inUser a <code>User</code> value
     */
    void delete(User inUser);
    /**
     * Finds all current users.
     *
     * @return a <code>List&lt;? extends User&gt;</code> value
     */
    List<? extends User> findAll();
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
     * @return a <code>User</code> value or <code>null</code>
     */
    User findOne(long inValue);
    /**
     * Find the user associated with the given user id.
     *
     * @param inUserId a <code>UserID</code> value
     * @return a <code>User</code> value or <code>null</code>
     */
    User findByUserId(UserID inUserId);
    /**
     * Change the password of the given user to the given value.
     * 
     * <p>Password values must be pre-hashed.
     *
     * @param inUser a <code>User</code> value
     * @param inOldPassword a <code>String</code> value
     * @param inNewPassword a <code>String</code> value
     * @return a <code>User</code> value
     */
    User changeUserPassword(User inUser,
                            String inOldPassword,
                            String inNewPassword);
}
