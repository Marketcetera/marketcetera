package org.marketcetera.core;

import org.marketcetera.util.log.SLF4JLoggerProxy;

import javax.sql.DataSource;
import java.sql.*;

@ClassVersion("$Id$") //$NON-NLS-1$
public class DatabaseIDFactory extends DBBackedIDFactory {

    public static final String TABLE_NAME = "id_repository"; //$NON-NLS-1$
    public static final String COL_NAME = "nextAllowedID"; //$NON-NLS-1$
    public static final long NUM_IDS_GRABBED = 1;

    private String dbTable;
    private String dbColumn;
    private long mCacheQuantity;
    private DataSource dataSource;

    /**
     * Initializes the ID factory with default table, column and ID quantity.
     * @param ds the data source to connect to the database.
     */
    public DatabaseIDFactory(DataSource ds) {
        this(ds,TABLE_NAME, COL_NAME, NUM_IDS_GRABBED);
    }

    /**
     * Creates an instance
     * @param ds The data source to connect to the database.
     * @param table the ID repository table name
     * @param column the ID repository table column name
     * @param quantity the quantity of IDs to fetch
     */
    public DatabaseIDFactory(DataSource ds, String table,
                             String column, long quantity) {
        super(""); //$NON-NLS-1$
        this.dataSource = ds;
        dbTable = table;
        dbColumn = column;
        mCacheQuantity = quantity;
    }

    public final void init() throws ClassNotFoundException, NoMoreIDsException {
        try {
            grabIDs();
        } catch (NoMoreIDsException e) {
            Messages.ERROR_DB_ID_FACTORY_INIT.info(this, e.getMessage());
            throw e;
        }
    }

    /**
     * Helper function intended to be overwritten by subclasses.
     * Thsi is where the real requiest for IDs happens
     */
    protected void performIDRequest() throws Exception {
        Connection dbConnection = null;

        try {
            try {
                dbConnection = dataSource.getConnection();
                Statement stmt = dbConnection.createStatement(
                        ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
                ResultSet set = null;
                set = stmt.executeQuery("SELECT id, " + dbColumn + " FROM " + dbTable); //$NON-NLS-1$ //$NON-NLS-2$
                if (!set.next()) {
                    set.moveToInsertRow();
                    set.insertRow();
                    set.updateLong(dbColumn, NUM_IDS_GRABBED);
                    set.moveToCurrentRow();
                    set.next();
                }
                long nextID = set.getLong(dbColumn);
                long upTo = nextID + mCacheQuantity;
                set.updateLong(dbColumn, upTo);
                set.updateRow();
                stmt.close();
                setMaxAllowedID(upTo);
                setNextID(nextID);
            } finally {
                if(dbConnection != null) {
                    dbConnection.close();
                }
            }
        } catch (SQLException e) {
            throw new NoMoreIDsException(e);
        }
    }

}
