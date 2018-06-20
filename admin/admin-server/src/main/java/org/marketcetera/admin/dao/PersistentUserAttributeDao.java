package org.marketcetera.admin.dao;

import org.marketcetera.admin.User;
import org.marketcetera.admin.UserAttributeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

/* $License$ */

/**
 * Provides data store access to <code>PersistentUserAttribute</code> values.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: PersistentUserAttributeDao.java 84561 2015-03-31 18:18:14Z colin $
 * @since 1.2.0
 */
public interface PersistentUserAttributeDao
        extends JpaRepository<PersistentUserAttribute,Long>,QuerydslPredicateExecutor<PersistentUserAttribute>
{
    /**
     * Finds the user attribute value with the given attributes.
     *
     * @param inUser a <code>User</code> value
     * @param inType a <code>UserAttributeType</code> value
     * @return a <code>PersistentUserAttribute</code> value
     */
    PersistentUserAttribute findByUserAndUserAttributeType(User inUser,
                                                           UserAttributeType inType);
}
