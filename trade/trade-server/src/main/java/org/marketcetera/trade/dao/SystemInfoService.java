package org.marketcetera.trade.dao;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides access to {@link PersistentSystemInfo} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: SystemInfoService.java 16522 2014-12-31 16:33:08Z colin $
 * @since 2.4.2
 */
@ClassVersion("$Id: SystemInfoService.java 16522 2014-12-31 16:33:08Z colin $")
public interface SystemInfoService
{
    /**
     * Gets the database version value.
     *
     * @return a <code>DatabaseVersion</code> value
     */
    public DatabaseVersion getDatabaseVersion();
    /**
     * Verifies that the database schema version matches the expected database version.
     *
     * @throws DatabaseVersionMismatch if the actual database version does not match the expected database version
     */
    public void verifyDatabaseVersion();
    /**
     * Initializes the database schema value to the current version.
     * 
     * <p>This method does not modify any other tables.
     *
     * @throws DatabaseAlreadyInitialized if the database already has a version set
     */
    public void initializeDatabaseVersion();
    /**
     * key used to identify the database version
     */
    public static final String DATABASE_VERSION = "schema version";
}
