package org.marketcetera.persist;


/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class FruitTest
        extends EntityTestBase<Fruit>
{
    /* (non-Javadoc)
     * @see org.marketcetera.persist.EntityTestBase#getNewEntity()
     */
    @Override
    protected Fruit getNewEntity()
    {
        Fruit fruit = new Fruit("My Fruit-" + System.nanoTime(),
                                "Description of my fruit",
                                Fruit.Type.APPLE);
        return fruit;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.persist.EntityTestBase#getEntityServiceType()
     */
    @Override
    protected Class<FruitService> getEntityServiceType()
    {
        return FruitService.class;
    }
}
