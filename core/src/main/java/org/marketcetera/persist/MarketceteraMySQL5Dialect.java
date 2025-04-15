package org.marketcetera.persist;

import java.sql.Types;

import org.hibernate.dialect.DatabaseVersion;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.type.SqlTypes;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides a customized MySQL5 dialect to work around some Hibernate issues.
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
        // Set minimum MySQL version to 5.7 for consistent behavior
        super(DatabaseVersion.make(5, 7));
        
        // In Hibernate 6, we need to use getSqlTypeDescriptorRegistry().addDescriptor 
        // instead of the old registerColumnType method
        // However, we're just going to rely on MySQL's default mappings in Hibernate 6
    }
}