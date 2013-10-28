package org.marketcetera.core;

import org.springframework.data.jpa.repository.JpaRepository;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface DatabaseIdDao
        extends JpaRepository<PersistentDatabaseID,Long>
{
}
