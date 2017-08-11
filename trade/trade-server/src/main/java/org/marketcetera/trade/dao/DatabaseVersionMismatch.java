package org.marketcetera.trade.dao;

import org.marketcetera.core.CoreException;
import org.marketcetera.persist.Messages;
import org.marketcetera.util.log.I18NBoundMessage2P;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Indicates that the expected database version does not match the actual database version.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: DatabaseVersionMismatch.java 16522 2014-12-31 16:33:08Z colin $
 * @since 2.4.2
 */
@ClassVersion("$Id: DatabaseVersionMismatch.java 16522 2014-12-31 16:33:08Z colin $")
public class DatabaseVersionMismatch
        extends CoreException
{
    /**
     * Create a new DatabaseVersionMismatch instance.
     *
     * @param inActualVersion
     */
    public DatabaseVersionMismatch(DatabaseVersion inActualVersion)
    {
        super(new I18NBoundMessage2P(Messages.DATABASE_VERSION_MISMATCH,
                                     inActualVersion.getVersion(),
                                     DatabaseVersion.CURRENT_VERSION.getVersion()));
        actualDatabaseVersion = inActualVersion;
    }
    /**
     * Get the actual database version value.
     *
     * @return a <code>DatabaseVersion</code> value
     */
    public DatabaseVersion getActualDatabaseVersion()
    {
        return actualDatabaseVersion;
    }
    /**
     * Get the expected database version value.
     *
     * @return a <code>DatabaseVersion</code> value
     */
    public DatabaseVersion getExpectedDatabaseVersion()
    {
        return DatabaseVersion.CURRENT_VERSION;
    }
    /**
     * Create a new DatabaseVersionMismatch instance.
     */
    @SuppressWarnings("unused")
    private DatabaseVersionMismatch()
    {
        actualDatabaseVersion = null;
    }
    /**
     * recorded actual value
     */
    private final DatabaseVersion actualDatabaseVersion;
    private static final long serialVersionUID = -6074629783563839493L;
}
