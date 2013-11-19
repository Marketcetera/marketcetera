package org.marketcetera.ors.dao;

import org.marketcetera.util.misc.ClassVersion;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

/* $License$ */

/**
 * Initializes the database schema version.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
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
     * provides access to system info objects
     */
    @Autowired
    private SystemInfoService systemInfoService;
}
