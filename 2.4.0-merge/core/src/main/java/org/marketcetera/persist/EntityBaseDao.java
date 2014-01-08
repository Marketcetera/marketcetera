package org.marketcetera.persist;

import org.springframework.data.repository.PagingAndSortingRepository;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface EntityBaseDao<Clazz extends EntityBase>
        extends PagingAndSortingRepository<Clazz,Long>
{
}
