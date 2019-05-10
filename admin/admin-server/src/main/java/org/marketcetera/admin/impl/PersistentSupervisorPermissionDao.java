package org.marketcetera.admin.impl;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

/* $License$ */

/**
 * Provides datastore access to {@link PersistentSupervisorPermission} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface PersistentSupervisorPermissionDao
        extends JpaRepository<PersistentSupervisorPermission, Long>,QuerydslPredicateExecutor<PersistentSupervisorPermission>
{
    /**
     * Find the supervisor permission with the given name.
     *
     * @param inName a <code>String</code> value
     * @return a <code>PersistentSupervisorPermission</code> value
     */
    PersistentSupervisorPermission findByName(String inName);
    /**
     * Find the supervisor permissions by the given user.
     *
     * @param inSupervisor a <code>PersistentUser</code> value
     * @return a <code>Set&lt;PersistentSupervisorPermission&gt;</code> value
     */
    Set<PersistentSupervisorPermission> findBySupervisor(PersistentUser inSupervisor);
}
