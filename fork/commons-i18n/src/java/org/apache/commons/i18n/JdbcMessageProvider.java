package org.apache.commons.i18n;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

/**
 * The <code>JdbcMessageProvider</code> provides messages stored in a database (or other data source)
 * accessible via JDBC. The <code>JdbcMessageProvider</code> only has support for different languages,
 * but if support for country or variant is required one could easily subclass it and override the
 * <code>getLocale</code> method. If <code>getLocale</code> is overridden, the languageColumn parameter
 * (or <code>jdbc.sql.locale.column<code> Map entry) of the constructors may be null, since it will not be used.
 * @author Mattias Jiderhamn
 */
public class JdbcMessageProvider implements MessageProvider {
    /**
     * This Map has locale or language as key, and a Map with the different
     * messages as value.
     */
    private final Map<Locale,Map<String,Map<String,String>>> locales = new HashMap<>();

    private String idColumn;

    private String languageColumn;

    /**
     * Create new JDBC <code>MessageProvider</code> using the provided connection.
     * @param conn The connection to use for initialization.
     * @param table The name of the table holding the messages
     * @param idColumn The name of the column holding the message ID
     * @param languageColumn The name of the column containing the ISO-639 language code.
     * @throws SQLException If there is an error getting data from the table
     */
    public JdbcMessageProvider(Connection conn, String table, String idColumn, String languageColumn)
            throws SQLException {
        this.idColumn = idColumn;
        this.languageColumn = languageColumn;
        init(conn, table);
    }

    /**
     * Create new JDBC <code>MessageProvider</code> using a connection from the provided <code>DataSource</code>. Will
     * get a connection from the <code>DataSource</code>, initialize and then return the connection.
     * @param ds The connection to use for initialization.
     * @param table The name of the table holding the messages
     * @param idColumn The name of the column holding the message ID
     * @param languageColumn The name of the column containing the ISO-639 language code.
     * @throws SQLException If there is an error getting data from the table
     */
    public JdbcMessageProvider(DataSource ds, String table, String idColumn, String languageColumn)
            throws SQLException {
        this.idColumn = idColumn;
        this.languageColumn = languageColumn;
        Connection conn = null;
        try {
            conn = ds.getConnection();
            init(conn, table);
        }
        finally {
            if(conn != null)
                conn.close();
        }
    }

    /**
     * Create JDBC <code>MessageProvider</code> from properties in a Map, such
     * as a <code>java.util.Properties</code> object. The following are the properties in use, which
     * are the same as for <code>JDBCResources</code> of Apache Commons Resources
     * jdbc.connect.driver               = org.gjt.mm.mysql.Driver
     * jdbc.connect.url                  = jdbc:mysql://localhost/resources
     * jdbc.connect.login                = resourcesTest
     * jdbc.connect.password             = resourcesTest
     *
     * jdbc.sql.table                    = resources
     * jdbc.sql.locale.column            = locale
     * jdbc.sql.key.column               = msgKey
     */
    public JdbcMessageProvider(Properties properties) throws ClassNotFoundException, SQLException {
        String driver = properties.getProperty("jdbc.connect.driver");
        String url    = properties.getProperty("jdbc.connect.url");
        String user = properties.getProperty("jdbc.connect.login");
        String pass = properties.getProperty("jdbc.connect.password");

        String table = properties.getProperty("jdbc.sql.table");
        this.idColumn = properties.getProperty("jdbc.sql.key.column");
        this.languageColumn = properties.getProperty("jdbc.sql.locale.column");

        Class.forName(driver);
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, pass);
            init(conn, table);
        }
        finally {
            if(conn != null)
                conn.close();
        }
    }

    ///////////////////////////////////////////////////////////////////////
    // Methods for initialization
    ///////////////////////////////////////////////////////////////////////

    private void init(Connection conn, String table) throws SQLException {
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM " + table);
            String[] valueColumns = getValueColumns(rs);
            while(rs.next()) {
                String id = rs.getString(idColumn);
                Locale locale = getLocale(rs);
                Map<String,String> entries = new HashMap<>();
                for(int i = 0; i < valueColumns.length; i++) {
                    String entry = rs.getString(valueColumns[i]);
                    if(entry != null)
                        entries.put(valueColumns[i], entry);
                }
                Map<String,Map<String,String>> localeMap = locales.get(locale);
                if(localeMap == null) { // If first record for this Locale
                    localeMap = new HashMap<>();
                    locales.put(locale, localeMap);
                }
                localeMap.put(id, entries);
            }
        }
        finally {
            if(stmt != null)
                stmt.close();
            if(rs != null)
                rs.close();
        }
    }

    /**
     * Get a String of all the column names, except the ID column and the
     * language column.
     * @param rs A <code>ResultSet</code> ready for reading meta data.
     * @return A String array with the text value column names.
     * @throws SQLException If an SQL error occurs.
     */
    protected String[] getValueColumns(ResultSet rs) throws SQLException {
        List<String> output = new LinkedList<>();
        ResultSetMetaData metadata = rs.getMetaData();
        int count = metadata.getColumnCount();
        for(int i = 0; i < count; i++) {
            String columnName = metadata.getColumnName(i+1); // (Count from 1)
            if(! columnName.equals(idColumn) && ! columnName.equals(languageColumn) )
                output.add(columnName);
        }
        return output.toArray(new String[0]);
    }

    /**
     * Get <code>Locale</code> for the current record in the ResultSet. May be overridden
     * by subclasses to allow for proprietary interpretation of language data.
     * The default implementation assumes the column with the name provided as languageColumn
     * for the constructor contains the ISO-639 code.
     * @return The <code>Locale</code> of the current <code>ResultSet</code> record.
     */
    protected Locale getLocale(ResultSet rs) throws SQLException {
        return new Locale(rs.getString(languageColumn).toLowerCase());
    }

    ///////////////////////////////////////////////////////////////////////
    // Methods to implement MessageProvider
    ///////////////////////////////////////////////////////////////////////

    public String getText(String id, String entry, Locale locale) {
        // TODO: Add Logging
        Map<String,String> entries = findEntries(id, locale);
        if(entries != null) {
            // TODO: Consider whether we need to recurse up if entries does not contain requested entry
            return entries.get(entry);
        }
        else
            return null;
    }

    public Map<String,String> getEntries(String id, Locale locale) {
        Map<String,String> entries = findEntries(id,locale);
        if(entries == null) { // If not found by using specified or default locale
            throw new MessageNotFoundException(MessageFormat.format(
                    I18nUtils.INTERNAL_MESSAGES.getString(I18nUtils.NO_MESSAGE_ENTRIES_FOUND),
                    (Object[])new String[] { id }));
        }
        return entries;
    }

    private Map<String,String> findEntries(String id, Locale locale) {
        Map<String,String> entries = findEntriesRecursively(id,locale);
        if(entries == null) { // If not found by using specified locale, try to use default
            return findEntriesRecursively(id,Locale.getDefault());
        }
        else
            return entries;
    }

    /**
     * Find entries by looking at the parent locale (language, country, variant ->
     * language, country -> language) until entry is found. If entry not found for topmost
     * Locale (language only), null is returned.
     */
    private Map<String,String> findEntriesRecursively(String id, Locale locale) {
        Map<String,Map<String,String>> localeIds = locales.get(locale);
        if(localeIds != null) {
            Map<String,String> entries = localeIds.get(id);
            if(entries != null)
              return entries;
        }
        Locale parentLocale = I18nUtils.getParentLocale(locale);
        if(parentLocale == null)
            return null;
        else
            return findEntriesRecursively(id, parentLocale); // Recursive call
    }
}
