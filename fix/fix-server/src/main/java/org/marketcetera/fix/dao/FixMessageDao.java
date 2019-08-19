package org.marketcetera.fix.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

/* $License$ */

/**
 * Provides access to the {@link PersistentFixMessage} data store.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface FixMessageDao
        extends JpaRepository<PersistentFixMessage,Long>,QuerydslPredicateExecutor<PersistentFixMessage>
{
}
