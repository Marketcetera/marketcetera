package org.marketcetera.persist;

import com.mysema.query.jpa.impl.JPAQuery;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface EntityService<Clazz extends EntityBase>
        extends EntityRepository<Clazz>
{
    /**
     * Gets a query object that can be used to dynamically construct queries.
     *
     * @return a <code>JPAQuery</code> value
     */
    public JPAQuery createCustomQuery();
}
