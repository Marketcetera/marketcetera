package org.marketcetera.trade.dao;

import org.marketcetera.util.misc.ClassVersion;
import org.springframework.data.jpa.repository.JpaRepository;

/* $License$ */

/**
 * Provides datastore access to {@link PersistentDatabaseID} values.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: DatabaseIdDao.java 16522 2014-12-31 16:33:08Z colin $
 * @since 2.4.2
 */
@ClassVersion("$Id: DatabaseIdDao.java 16522 2014-12-31 16:33:08Z colin $")
public interface DatabaseIdDao
        extends JpaRepository<PersistentDatabaseID,Long>
{
}
