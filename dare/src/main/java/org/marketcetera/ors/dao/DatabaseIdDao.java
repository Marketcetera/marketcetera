package org.marketcetera.ors.dao;

import org.marketcetera.util.misc.ClassVersion;
import org.springframework.data.jpa.repository.JpaRepository;

/* $License$ */

/**
 * Provides datastore access to {@link PersistentDatabaseID} values.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.2
 */
@ClassVersion("$Id$")
public interface DatabaseIdDao
        extends JpaRepository<PersistentDatabaseID,Long>
{
}
