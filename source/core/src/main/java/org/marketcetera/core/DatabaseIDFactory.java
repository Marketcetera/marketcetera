package org.marketcetera.core;


import javax.sql.DataSource;
import java.sql.*;

@ClassVersion("$Id$")
public class DatabaseIDFactory extends DBBackedIDFactory {

    public static final String TABLE_NAME = "id_repository";
    public static final String COL_NAME = "nextAllowedID";
    public static final int NUM_IDS_GRABBED = 1000;

    private String dbTable;
    private String dbColumn;
    private int mCacheQuantity;
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
                             String column, int quantity) {
        super("");
        this.dataSource = ds;
        dbTable = table;
        dbColumn = column;
        mCacheQuantity = quantity;
    }

    public final void init() throws ClassNotFoundException, NoMoreIDsException {
        try {
            grabIDs();
        } catch (NoMoreIDsException e) {
            if(LoggerAdapter.isInfoEnabled(this)) {
                LoggerAdapter.info(MessageKey.ERROR_DB_ID_FACTORY_INIT.
                        getLocalizedMessage(e.getMessage()), this);
            }
            throw e;
        }
    }

    /**
     * Helper function intended to be overwritten by subclasses.
     * Thsi is where the real requiest for IDs happens
     * It is wrapped by a try/catch block higher up, so that we can
     * fall back onto an inMemory id factory if the request fails.
     */
    protected void performIDRequest() throws Exception {
        Connection dbConnection = null;

        try {
            try {
                dbConnection = dataSource.getConnection();
                Statement stmt = dbConnection.createStatement(
                        ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
                ResultSet set = null;
                set = stmt.executeQuery("SELECT id, " + dbColumn + " FROM " + dbTable);
                if (!set.next()) {
                    set.moveToInsertRow();
                    set.insertRow();
                    set.updateInt(dbColumn, NUM_IDS_GRABBED);
                    set.moveToCurrentRow();
                    set.next();
                }
                int nextID = set.getInt(dbColumn);
                int upTo = nextID + mCacheQuantity;
                set.updateInt(dbColumn, upTo);
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
