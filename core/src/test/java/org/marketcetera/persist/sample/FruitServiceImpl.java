package org.marketcetera.persist.sample;

import org.marketcetera.persist.AbstractEntityService;
import org.springframework.stereotype.Component;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Component
public class FruitServiceImpl
        extends AbstractEntityService<Fruit>
        implements FruitService
{
    /* (non-Javadoc)
     * @see org.marketcetera.persist.AbstractEntityService#getRepositoryType()
     */
    @Override
    protected Class<FruitRepository> getRepositoryType()
    {
        return FruitRepository.class;
    }
}
