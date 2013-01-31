package org.marketcetera.persist.sample;

import org.marketcetera.persist.EntityService;
import org.springframework.data.repository.NoRepositoryBean;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@NoRepositoryBean
public interface FruitService
        extends EntityService<Fruit>
{
}
