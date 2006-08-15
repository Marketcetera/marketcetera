package org.marketcetera.core;


import java.sql.*;

@ClassVersion("$Id$")
public class DatabaseIDFactory implements IDFactory {

    public static final String TABLE_NAME = "IDRepository";
    public static final String COL_NAME = "nextAllowedID";

    private String dbURL;
    private String dbDriver;
    private String dbTable;
    private String dbColumn;
    private String dbLogin;
    private String dbPassword;
    private int mCacheQuantity;
    private int mUpTo = 0;
    private int mNextID = 0;
    private Connection dbConnection;

    public static DatabaseIDFactory getInstance(ConfigData inProps) throws Exception
    {
        DatabaseIDFactory factory = new DatabaseIDFactory(
            inProps.get(ConfigPropertiesLoader.DB_URL_KEY, ""),
            inProps.get(ConfigPropertiesLoader.DB_DRIVER_KEY, null),
            inProps.get(ConfigPropertiesLoader.DB_USER_KEY, null),
            inProps.get(ConfigPropertiesLoader.DB_PASS_KEY, null),
            TABLE_NAME,
            COL_NAME,
            1);

        factory.init();
        return factory;
    }

    protected DatabaseIDFactory(String dburl, String driver, String login, String password, String table,
                                String column, int quantity) {
        mCacheQuantity = quantity;
        dbColumn = column;
        dbDriver = driver;
        dbTable = table;
        dbURL = dburl;

        dbLogin = login;
        dbPassword = password;
    }

    public void init() throws SQLException, ClassNotFoundException {
        Class.forName(dbDriver);

        dbConnection = DriverManager.getConnection(dbURL, dbLogin, dbPassword);
        dbConnection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        grabIDs();
    }

    public String getNext() throws NoMoreIDsException
    {
        synchronized (this) {
            if (mNextID >= mUpTo) {
                try {
                    grabIDs();
                } catch (SQLException ex) {
                    LoggerAdapter.error(MessageKey.DB_ID_FETCH.getLocalizedMessage(mNextID), ex, this);
                    throw new NoMoreIDsException(ex);
                }
            }
            return "" + (mNextID++);
        }
    }

    /** Lock the table to prevent concurrent access with {@link ResultSet.CONCUR_UPDATABLE} */
    protected void grabIDs() throws SQLException {
        Statement stmt = dbConnection.createStatement(
                ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
        ResultSet set = stmt.executeQuery("SELECT [" + dbColumn + "] FROM ["
                                          + dbTable + "]");
        if (!set.next()) {
            set.insertRow();
            set.updateInt(dbColumn, 1000);
        }
        mNextID = set.getInt(dbColumn);
        mUpTo = mNextID + mCacheQuantity;
        set.updateInt(dbColumn, mUpTo);
        set.updateRow();
        stmt.close();
    }

}
