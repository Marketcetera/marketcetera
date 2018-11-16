package org.marketcetera.fix.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

/* $License$ */

/**
 * Provides access to the Fix Session Attribute Descriptor data store.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface FixSessionAttributeDescriptorDao
        extends JpaRepository<PersistentFixSessionAttributeDescriptor,Long>,QueryDslPredicateExecutor<PersistentFixSessionAttributeDescriptor>
{
}
