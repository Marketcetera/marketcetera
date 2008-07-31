package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.unicode.UnicodeInputStreamReader;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import javax.sql.DataSource;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.Reader;
import java.io.IOException;

/* $License$ */
/**
 * A class that executes SQL statements from a file. This class is used
 * to initialize the schema and run other SQL statements during unit testing.
 * The class extracts SQL statements from the supplied file by using
 * ';' as the delimiter and runs them one by one.
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class ExecuteSQLFromFile {
    /**
     * Runs the set of SQL files on the specified data source.
     * @param mSQLFile the sets of SQL files that need to be run
     * @param mDataSource the data source to run the SQL files on
     * @throws IOException if there were IO errors
     * @throws SQLException if there SQL execution errors
     */
    public ExecuteSQLFromFile(URL [] mSQLFile, DataSource mDataSource)
            throws IOException, SQLException {
        this.mSQLFile = mSQLFile;
        this.mDataSource = mDataSource;
        run();
    }
    private void run() throws SQLException, IOException {
        Statement statement = mDataSource.getConnection().createStatement();

        for (URL sql: mSQLFile) {
            processURL(statement, sql);
        }
    }

    private void processURL(Statement statement, URL url) throws IOException, SQLException {
        SLF4JLoggerProxy.debug(this, "Processing SQL File: "+url); //$NON-NLS-1$
        //Read the file
        Reader reader = new UnicodeInputStreamReader(url.openStream());
        StringBuilder sb;
        try {
            sb = new StringBuilder();
            char[] buffer = new char[10240];
            int count;
            while((count = reader.read(buffer)) > 0) {
                sb.append(buffer,0,count);
            }
        } finally {
            reader.close();
        }
        final String content = sb.toString();
        //Parse SQL statements out from it
        String [] sqls = content.split(";"); //$NON-NLS-1$
        for (String sql: sqls) {
            if (sql.trim().length() > 0) {
                SLF4JLoggerProxy.debug(this, "Executing SQL: "+sql); //$NON-NLS-1$
                boolean isResultSet = statement.execute(sql);
                int updateCount;
                do {
                    if (!isResultSet) {
                        updateCount = statement.getUpdateCount();
                        SLF4JLoggerProxy.debug(this, "Last SQL had an update count: "+updateCount); //$NON-NLS-1$
                        if(updateCount < 0) {
                            break;
                        }
                    }
                    isResultSet = statement.getMoreResults();
                } while (true);
            }
        }
    }

    private URL[] mSQLFile;
    private DataSource mDataSource;
}
