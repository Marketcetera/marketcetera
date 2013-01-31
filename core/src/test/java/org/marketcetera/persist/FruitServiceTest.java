package org.marketcetera.persist;

import org.marketcetera.persist.sample.Fruit;
import org.marketcetera.persist.sample.FruitService;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class FruitServiceTest
        extends EntityServiceTestBase<Fruit>
{
    /* (non-Javadoc)
     * @see org.marketcetera.persist.EntityServiceTestBase#getServiceType()
     */
    @Override
    protected Class<FruitService> getServiceType()
    {
        return FruitService.class;
    }
}
