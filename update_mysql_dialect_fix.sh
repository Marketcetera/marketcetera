#!/bin/bash
# Script to fix MySQL dialect for Hibernate 6

cat > /tmp/MarketceteraMySQL5Dialect.java <<'EOF'
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
        super(DatabaseVersion.make(5,7));
        // in order to allow MySQL to store milliseconds in timestamps, this mod is required.
        this.getTypeMappings().put(Types.TIMESTAMP, "timestamp(3)");
    }
}
EOF

# Replace the original file
mv /tmp/MarketceteraMySQL5Dialect.java /Users/colin/marketcetera/workspaces/sixer-java11/code/marketcetera/core/src/main/java/org/marketcetera/persist/MarketceteraMySQL5Dialect.java

echo "MarketceteraMySQL5Dialect updated for Hibernate 6 with proper registerColumnType"