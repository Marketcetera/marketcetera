package org.marketcetera.admin.dao;

import java.util.Collection;

import org.marketcetera.admin.User;
import org.marketcetera.admin.UserAttribute;
import org.marketcetera.admin.UserAttributeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

/* $License$ */

/**
 * Provides data store access to <code>PersistentUserAttribute</code> values.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
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
    /**
     * Find the user attributes for the given user.
     *
     * @param inUser a <code>User</code> value
     * @return a <code>Collection&lt;UserAttribute&gt;</code> value
     */
    Collection<UserAttribute> findByUser(User inUser);
}
