package org.marketcetera.tensorflow.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

/* $License$ */

/**
 * Provides access to the {@link PersistentGraphContainer} data store.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface GraphContainerDao
        extends JpaRepository<PersistentGraphContainer,Long>,QueryDslPredicateExecutor<PersistentGraphContainer>
{
    /**
     * Find the graph with the given name.
     *
     * @param inName a <code>String</code> value
     * @return a <code>PersistentGraphContainer</code> value or <code>null</code>
     */
    PersistentGraphContainer findByName(String inName);
}
