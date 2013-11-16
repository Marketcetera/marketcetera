package org.marketcetera.persist.impl;

import org.marketcetera.persist.*;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/* $License$ */

/**
 * Provides access to {@link PersistentSystemInfo} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Service
@Transactional(readOnly=true,propagation=Propagation.REQUIRED)
@ClassVersion("$Id$")
public class SystemInfoServiceImpl
        implements SystemInfoService
{
    /* (non-Javadoc)
     * @see org.marketcetera.persist.SystemInfoService#getDatabaseVersion()
     */
    @Override
    public DatabaseVersion getDatabaseVersion()
    {
        PersistentSystemInfo databaseVersion = systemInfoDao.findByName(SystemInfoService.DATABASE_VERSION);
        if(databaseVersion == null) {
            return DatabaseVersion.NO_VERSION;
        } else {
            return new DatabaseVersion(databaseVersion);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.persist.SystemInfoService#verifyDatabaseVersion()
     */
    @Override
    public void verifyDatabaseVersion()
    {
        DatabaseVersion actualVersion = getDatabaseVersion();
        if(!DatabaseVersion.CURRENT_VERSION.equals(actualVersion)) {
            throw new DatabaseVersionMismatch(actualVersion);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.persist.SystemInfoService#initializeDatabaseVersion()
     */
    @Override
    @Transactional(readOnly=false,propagation=Propagation.REQUIRED)
    public void initializeDatabaseVersion()
    {
        PersistentSystemInfo databaseVersion = systemInfoDao.findByName(SystemInfoService.DATABASE_VERSION);
        if(databaseVersion != null) {
            throw new DatabaseAlreadyInitialized();
        }
        databaseVersion = new PersistentSystemInfo();
        databaseVersion.setName(SystemInfoService.DATABASE_VERSION);
        databaseVersion.setDescription("indicates current database schema version");
        databaseVersion.setValue(DatabaseVersion.CURRENT_VERSION.getVersion());
        systemInfoDao.save(databaseVersion);
    }
    /**
     * provides datastore access to system info objects
     */
    @Autowired
    private SystemInfoDao systemInfoDao;
}
