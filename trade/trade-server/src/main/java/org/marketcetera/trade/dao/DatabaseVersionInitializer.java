package org.marketcetera.trade.dao;

import org.marketcetera.util.misc.ClassVersion;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

/* $License$ */

/**
 * Initializes the database schema version.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: DatabaseVersionInitializer.java 16522 2014-12-31 16:33:08Z colin $
 * @since 2.4.2
 */
@ClassVersion("$Id: DatabaseVersionInitializer.java 16522 2014-12-31 16:33:08Z colin $")
public class DatabaseVersionInitializer
        implements InitializingBean
{
    /* (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet()
            throws Exception
    {
        systemInfoService.initializeDatabaseVersion();
    }
    /**
     * Get the systemInfoService value.
     *
     * @return a <code>SystemInfoService</code> value
     */
    public SystemInfoService getSystemInfoService()
    {
        return systemInfoService;
    }
    /**
     * Sets the systemInfoService value.
     *
     * @param inSystemInfoService a <code>SystemInfoService</code> value
     */
    @Autowired
    public void setSystemInfoService(SystemInfoService inSystemInfoService)
    {
        systemInfoService = inSystemInfoService;
    }
    /**
     * provides access to system info objects
     */
    private SystemInfoService systemInfoService;
}
