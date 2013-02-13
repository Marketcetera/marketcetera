package org.marketcetera.persist.sample;

import org.marketcetera.persist.AbstractEntityService;
import org.springframework.stereotype.Component;

import com.mysema.query.types.path.EntityPathBase;

/* $License$ */

/**
 * Provides services to manage <code>Fruit</code> objects.
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
    /* (non-Javadoc)
     * @see org.marketcetera.persist.AbstractEntityService#getBaseType()
     */
    @Override
    protected EntityPathBase<Fruit> getBaseType()
    {
        throw new UnsupportedOperationException();
    }
}
