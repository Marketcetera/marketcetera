package org.marketcetera.persist;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.apache.commons.lang.Validate;

/* $License$ */

/**
 * Provides common datasource provider behavior.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class AbstractDataSourceServiceProvider
{
    /**
     * Validates and starts the object.
     */
    @PostConstruct
    public void start()
    {
        Validate.notNull(dataSource,
                         "Datasource is required");
    }
    /**
     * Get the DataSource value.
     *
     * @return a <code>DataSource</code> value
     */
    public DataSource getDataSource()
    {
        return dataSource;
    }
    /**
     * Sets the DataSource value.
     *
     * @param inDataSource a <code>DataSource</code> value
     */
    public void setDataSource(DataSource inDataSource)
    {
        dataSource = inDataSource;
    }
    /**
     * provides access to JDBC
     */
    private DataSource dataSource;
}
