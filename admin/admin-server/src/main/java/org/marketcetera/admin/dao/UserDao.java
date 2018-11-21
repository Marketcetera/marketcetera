package org.marketcetera.admin.dao;

import org.marketcetera.admin.user.PersistentUser;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

/* $License$ */

/**
 * Provides datastore access to {@link PersistentUser} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: UserDao.java 17339 2017-08-10 02:14:34Z colin $
 * @since 2.4.2
 */
@ClassVersion("$Id: UserDao.java 17339 2017-08-10 02:14:34Z colin $")
public interface UserDao
        extends JpaRepository<PersistentUser,Long>,QuerydslPredicateExecutor<PersistentUser>
{
    /**
     * Finds the user with the given username.
     *
     * @param inUsername a <code>String</code> value
     * @return a <code>SimpleUser</code> value
     */
    PersistentUser findByName(String inUsername);
    /**
     * Updates the user data for the user with the given name. 
     *
     * @param inUsername a <code>String</code> value
     * @param inUserData a <code>String</code> value
     */
    @Modifying
    @Query("update user u set u.userData=?2 where u.name=?1")
    void updateUserByName(String inUsername,
                          String inUserData);
    /**
     * Updates the active status for the user with the given name.
     *
     * @param inUsername a <code>String</code> value
     * @param inIsActive a <code>boolean</code> value
     */
    @Modifying	
    @Query("update user u set u.active=?2 where u.name=?1")
    void updateUserActiveStatus(String inUsername,
                                boolean inIsActive);
}
