package org.marketcetera.admin.dao;

import org.marketcetera.admin.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

/* $License$ */

/**
 * Provides datastore access to {@link Role} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: PersistentRoleDao.java 84462 2015-03-09 00:04:29Z colin $
 * @since 1.0.1
 */
public interface PersistentRoleDao
        extends JpaRepository<PersistentRole,Long>,QuerydslPredicateExecutor<PersistentRole>
{
    /**
     * Gets the role with the given name.
     *
     * @param inName a <code>String</code> value
     * @return a <code>PersistentRole</code> value or <code>null</code>
     */
    PersistentRole findByName(String inName);
}
