package org.marketcetera.db;

import org.springframework.data.repository.PagingAndSortingRepository;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface TestRepository
        extends PagingAndSortingRepository<Test,Long>
{
}
