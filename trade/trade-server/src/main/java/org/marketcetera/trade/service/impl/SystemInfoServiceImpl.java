package org.marketcetera.trade.service.impl;

import org.marketcetera.trade.dao.DatabaseVersion;
import org.marketcetera.trade.dao.DatabaseVersionInitializer;
import org.marketcetera.trade.dao.DatabaseVersionMismatch;
import org.marketcetera.trade.dao.PersistentSystemInfo;
import org.marketcetera.trade.dao.SystemInfoDao;
import org.marketcetera.trade.dao.SystemInfoService;
import org.marketcetera.util.log.SLF4JLoggerProxy;
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
 * @version $Id: SystemInfoServiceImpl.java 17266 2017-04-28 14:58:00Z colin $
 * @since 2.4.2
 */
@Service
@Transactional(readOnly=true,propagation=Propagation.REQUIRED)
@ClassVersion("$Id: SystemInfoServiceImpl.java 17266 2017-04-28 14:58:00Z colin $")
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
            SLF4JLoggerProxy.info(this,
                                  "Database already initialized");
            return;
        }
        databaseVersion = new PersistentSystemInfo();
        databaseVersion.setName(SystemInfoService.DATABASE_VERSION);
        databaseVersion.setDescription("indicates current database schema version");
        databaseVersion.setValue(DatabaseVersion.CURRENT_VERSION.getVersion());
        systemInfoDao.save(databaseVersion);
    }
    /**
     * Get the systemInfoDao value.
     *
     * @return a <code>SystemInfoDao</code> value
     */
    public SystemInfoDao getSystemInfoDao()
    {
        return systemInfoDao;
    }
    /**
     * Sets the systemInfoDao value.
     *
     * @param inSystemInfoDao a <code>SystemInfoDao</code> value
     */
    public void setSystemInfoDao(SystemInfoDao inSystemInfoDao)
    {
        systemInfoDao = inSystemInfoDao;
    }
    /**
     * provides datastore access to system info objects
     */
    @Autowired
    private SystemInfoDao systemInfoDao;
    /**
     * initializes the database system info, if supplied
     */
    @SuppressWarnings("unused")
    @Autowired(required=false)
    private DatabaseVersionInitializer databaseVersionInitiator;
}
