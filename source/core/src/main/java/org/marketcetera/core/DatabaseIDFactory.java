package org.marketcetera.core;


import java.sql.*;

@ClassVersion("$Id$")
public class DatabaseIDFactory extends DBBackedIDFactory {

    public static final String TABLE_NAME = "id_repository";
    public static final String COL_NAME = "nextAllowedID";
    public static final int NUM_IDS_GRABBED = 1000;

    private String dbURL;
    private String dbDriver;
    private String dbTable;
    private String dbColumn;
    private String dbLogin;
    private String dbPassword;
    private int mCacheQuantity;
    private Connection dbConnection;

    public DatabaseIDFactory(String dburl, String driver, String login, String password, String table,
                             String column, int quantity) {
        this(dburl, driver, login, password, table, column, quantity, "");
    }

    public DatabaseIDFactory(String dburl, String driver, String login, String password, String table,
                             String column, int quantity, String prefix) {
        super(prefix);
        mCacheQuantity = quantity;
        dbColumn = column;
        dbDriver = driver;
        dbTable = table;
        dbURL = dburl;

        dbLogin = login;
        dbPassword = password;
    }

    public final void init() throws ClassNotFoundException, NoMoreIDsException {
        Class.forName(dbDriver);

        try {
            dbConnection = DriverManager.getConnection(dbURL, dbLogin, dbPassword);
            dbConnection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        } catch(Exception ex) {
            if(LoggerAdapter.isInfoEnabled(this)) {
                LoggerAdapter.info(MessageKey.ERROR_DB_ID_FACTORY_INIT.getLocalizedMessage(ex.getMessage()), this);
            }
            throw new NoMoreIDsException(ex);
        } finally {
            grabIDs();
        }
    }


    /** Helper function intended to be overwritten by subclasses.
     * Thsi is where the real requiest for IDs happens
     * It is wrapped by a try/catch block higher up, so that we can
     * fall back onto an inMemory id factory if the request fails.
     */
    protected void performIDRequest() throws Exception {
        if(dbConnection == null) {
            throw new NoMoreIDsException(MessageKey.ERROR_DB_ID_FACTORY_DB_CONN_ERROR.getLocalizedMessage());
        }
        
        Statement stmt = dbConnection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
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
    }

}
