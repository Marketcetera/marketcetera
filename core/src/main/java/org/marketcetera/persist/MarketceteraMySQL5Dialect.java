package org.marketcetera.persist;

import org.hibernate.dialect.MySQL5Dialect;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides a customized MySQL5 dialect to work around some Hibernate 4 bugs.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.3.1
 */
@ClassVersion("$Id$")
public class MarketceteraMySQL5Dialect
        extends MySQL5Dialect
{
    /**
     * Create a new MarketceteraMysql5Dialect instance.
     */
    public MarketceteraMySQL5Dialect()
    {
        super();
        // this is a bug in Hibernate4 that prevents Hibernate from validating its own generated DDL
        // a boolean field will be rendered as a "bit", which is acceptable, but the schema validation
        //  expects to see it as a "boolean". This fix explicitly matches the two up.
        // this appears to cover the issue: https://hibernate.atlassian.net/browse/HHH-468.
        // it is marked as fixed, but the problem is still evident in H4.
        registerColumnType(java.sql.Types.BOOLEAN,
                           "bit");
        // in order to allow MySQL to store milliseconds in timestamps, this mod is required. note that
        //  this will work only with MySQL versions 5.6.4 or later
        registerColumnType(java.sql.Types.TIMESTAMP,
                           "timestamp(3)");
    }
}
