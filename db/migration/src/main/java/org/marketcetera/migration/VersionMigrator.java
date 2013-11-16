package org.marketcetera.migration;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sql.DataSource;

import org.apache.commons.lang.Validate;
import org.marketcetera.core.Pair;
import org.marketcetera.persist.DatabaseVersion;
import org.marketcetera.persist.SystemInfoService;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.Lifecycle;

/* $License$ */

/**
 * Migrates database schemas based on version number differences.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class VersionMigrator
        implements Lifecycle
{
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#isRunning()
     */
    @Override
    public boolean isRunning()
    {
        return running.get();
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#start()
     */
    @Override
    public void start()
    {
        Validate.notNull(datasource);
        running.set(true);
        try {
            System.out.println(getDatasourceVersion());
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#stop()
     */
    @Override
    public void stop()
    {
        try {
            
        } finally {
            running.set(false);
        }
    }
    /**
     * Builds the ordered list of migration operations to be performed.
     *
     * @return a <code>List&lt;Pair&lt;Integer,Integer&gt;&gt;</code> value
     */
    private List<Pair<Integer,Integer>> getConversionOperations()
    {
        List<Pair<Integer,Integer>> operations = new ArrayList<Pair<Integer,Integer>>();
        DatabaseVersion targetVersion = systemInfoService.getDatabaseVersion();
        return operations;
    }
    /**
     * Get the datasource value.
     *
     * @return a <code>DataSource</code> value
     */
    public DataSource getDatasource()
    {
        return datasource;
    }
    /**
     * Sets the datasource value.
     *
     * @param inDatasource a <code>DataSource</code> value
     */
    public void setDatasource(DataSource inDatasource)
    {
        datasource = inDatasource;
    }
    /**
     * Determines the version of the datasource to be migrated.
     *
     * @return a <code>DatabaseVersion</code> value
     * @throws SQLException if an error occurs connecting to the datasource 
     */
    private DatabaseVersion getDatasourceVersion()
            throws SQLException
    {
        Connection dbConnection = null;
        try {
            dbConnection = datasource.getConnection();
        } catch (SQLException e) {
            SLF4JLoggerProxy.error(this,
                                   e,
                                   "Cannot connect to existing datasource");
            throw e;
        }
        Statement statement = dbConnection.createStatement();
        try {
            ResultSet result = statement.executeQuery("SELECT value from system_info where name='" + SystemInfoService.DATABASE_VERSION + "'");
            String version = result.getString(0);
            System.out.println("Version is " + version);
        } catch (SQLException e) {
            System.out.println("No version table, get creative...");
        }
        return DatabaseVersion.NO_VERSION;
    }
    /**
     * provides access to system info information
     */
    @Autowired
    private SystemInfoService systemInfoService;
    /**
     * connection to old database
     */
    private DataSource datasource;
    /**
     * indicates if the migrator is running or not
     */
    private final AtomicBoolean running = new AtomicBoolean(false);
}
