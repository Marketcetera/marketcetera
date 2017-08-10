package org.marketcetera.admin.dao;

import java.util.Set;

import org.marketcetera.admin.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

/* $License$ */

/**
 * Provides datastore access to {@link Permission} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: PersistentPermissionDao.java 84382 2015-01-20 19:43:06Z colin $
 * @since 1.0.1
 */
public interface PersistentPermissionDao
        extends JpaRepository<PersistentPermission,Long>,QueryDslPredicateExecutor<PersistentPermission>
{
    /**
     * Finds the permission with the given name.
     *
     * @param inName a <code>String</code> value
     * @return a <code>PersistentPermission</code> value or <code>null</code>
     */
    PersistentPermission findByName(String inName);
    /**
     * Gets the permissions granted to the user with the given username.
     *
     * @param inUsername a <code>String</code> value
     * @return a <code>Set&lt;PersistentPermission&gt;</code> value
     */
    @Query("select p from Permission p, IN(p.roles) r, IN(r.subjects) u where u.name = ?1")
    Set<PersistentPermission> findAllByUsername(String inUsername);
}
