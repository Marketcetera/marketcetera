package org.marketcetera.persist;

import java.sql.Types;

import org.hibernate.dialect.MySQLDialect;
import org.hibernate.dialect.DatabaseVersion;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides a customized MySQL dialect to work around some Hibernate issues.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.3.1
 */
@ClassVersion("$Id$")
public class MarketceteraMySQL5Dialect
        extends MySQLDialect
{
    /**
     * Create a new MarketceteraMysql5Dialect instance.
     */
    public MarketceteraMySQL5Dialect()
    {
        // Use MySQL 5.7 as the database version
        super(DatabaseVersion.make(5,7));
    }
    
    /**
     * Return the SQL type for the given JDBC type.
     *
     * @param sqlType The JDBC type
     * @return The SQL type
     */
    @Override
    protected String columnType(int sqlType)
    {
        if (sqlType == Types.TIMESTAMP) {
            // For timestamps, use timestamp(3) to handle milliseconds
            return "timestamp(3)";
        }
        return super.columnType(sqlType);
    }
}