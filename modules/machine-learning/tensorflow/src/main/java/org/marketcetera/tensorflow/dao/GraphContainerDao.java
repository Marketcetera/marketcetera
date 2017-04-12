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
     * 
     *
     *
     * @param inName
     * @return
     */
    PersistentGraphContainer findByName(String inName);
}
