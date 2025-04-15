package org.marketcetera.admin.dao;

import org.marketcetera.admin.user.PersistentUser;
import org.marketcetera.util.misc.ClassVersion;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.querydsl.QuerydslPredicateExecutor; // TEMPORARILY COMMENTED OUT FOR SPRING BOOT 3 MIGRATION

/* $License$ */

/**
 * Provides datastore access to {@link PersistentUser} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.2
 */
@ClassVersion("$Id$")
public interface UserDao
        extends JpaRepository<PersistentUser,Long> // QuerydslPredicateExecutor temporarily removed for Spring Boot 3 migration
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
                                
    /**
     * Find users by active status.
     *
     * @param inActive a <code>boolean</code> value
     * @return a <code>List&lt;PersistentUser&gt;</code> value
     */
    List<PersistentUser> findByActive(Boolean inActive);
    
    /**
     * Find users by name like pattern.
     *
     * @param inNamePattern a <code>String</code> value
     * @return a <code>List&lt;PersistentUser&gt;</code> value
     */
    @Query("select u from user u where u.name like %?1%")
    List<PersistentUser> findByNameLike(String inNamePattern);
    
    /**
     * Find users with the exact name as a list.
     *
     * @param inUsername a <code>String</code> value
     * @return a <code>List&lt;PersistentUser&gt;</code> value
     */
    @Query("select u from user u where u.name = ?1")
    List<PersistentUser> findByNameAsList(String inUsername);
    
    /**
     * Find users by name like pattern and active status.
     *
     * @param inNamePattern a <code>String</code> value
     * @param inActive a <code>boolean</code> value
     * @return a <code>List&lt;PersistentUser&gt;</code> value
     */
    @Query("select u from user u where u.name like %?1% and u.active = ?2")
    List<PersistentUser> findByNameLikeAndActive(String inNamePattern, Boolean inActive);
    
    /**
     * Find users by name and active status.
     *
     * @param inName a <code>String</code> value
     * @param inActive a <code>boolean</code> value
     * @return a <code>List&lt;PersistentUser&gt;</code> value
     */
    List<PersistentUser> findByNameAndActive(String inName, Boolean inActive);
}
