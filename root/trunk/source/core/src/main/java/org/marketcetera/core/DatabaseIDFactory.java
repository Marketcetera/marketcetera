package org.marketcetera.core;


import java.sql.*;

@ClassVersion("$Id$")
public class DatabaseIDFactory extends ExternalIDFactory {

    public static final String TABLE_NAME = "id_repository";
    public static final String COL_NAME = "nextAllowedID";

    private String dbURL;
    private String dbDriver;
    private String dbTable;
    private String dbColumn;
    private String dbLogin;
    private String dbPassword;
    private int mCacheQuantity;
    private Connection dbConnection;

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

    public void init() throws SQLException, ClassNotFoundException, NoMoreIDsException {
        Class.forName(dbDriver);

        dbConnection = DriverManager.getConnection(dbURL, dbLogin, dbPassword);
        dbConnection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        grabIDs();
    }


    /** Lock the table to prevent concurrent access with {@link ResultSet#CONCUR_UPDATABLE} */
    protected void grabIDs() throws NoMoreIDsException {
        try {
            Statement stmt = dbConnection.createStatement(
                    ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
            ResultSet set = null;
                set = stmt.executeQuery("SELECT [" + dbColumn + "] FROM ["
                                                  + dbTable + "]");
            if (!set.next()) {
                set.insertRow();
                set.updateInt(dbColumn, 1000);
            }
            int nextID = set.getInt(dbColumn);
            int upTo = nextID + mCacheQuantity;
            set.updateInt(dbColumn, upTo);
            set.updateRow();
            stmt.close();
            setMaxAllowedID(upTo);
            setNextID(nextID);
        } catch (SQLException e) {
            throw new NoMoreIDsException(e);
        }
    }

}
