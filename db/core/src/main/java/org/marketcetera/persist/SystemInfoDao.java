package org.marketcetera.persist;

import org.marketcetera.util.misc.ClassVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

/* $License$ */

/**
 * Provides datastore access to {@link PersistentSystemInfo} values.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface SystemInfoDao
        extends JpaRepository<PersistentSystemInfo,Long>,QueryDslPredicateExecutor<PersistentSystemInfo>
{
    /**
     * Gets the <code>PersistentSystemInfo</code> value with the given name.
     *
     * @return a <code>PersistentSystemInfo</code> value
     */
    PersistentSystemInfo findByName(String inName);
}
