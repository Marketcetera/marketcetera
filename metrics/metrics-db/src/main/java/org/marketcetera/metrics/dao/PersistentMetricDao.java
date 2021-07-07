package org.marketcetera.metrics.dao;

import org.springframework.data.jpa.repository.JpaRepository;

/* $License$ */

/**
 * Provides access to the {@link PersistentMetric} data store.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface PersistentMetricDao
        extends JpaRepository<PersistentMetric,Long>
{
}
