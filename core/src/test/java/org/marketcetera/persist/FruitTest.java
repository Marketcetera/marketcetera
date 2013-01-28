package org.marketcetera.persist;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

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
    /**
     * 
     *
     *
     * @throws Exception
     */
    @Test
    public void testMultiQueryAll()
            throws Exception
    {
        MultiFruitQuery query = MultiFruitQuery.all();
    }
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
    /* (non-Javadoc)
     * @see org.marketcetera.persist.EntityTestBase#verifyEntity(org.marketcetera.persist.EntityBase, org.marketcetera.persist.EntityBase)
     */
    @Override
    protected void verifyEntity(Fruit inExpectedValue,
                                Fruit inActualValue)
    {
        super.verifyEntity(inExpectedValue,
                           inActualValue);
        assertEquals(inExpectedValue.getType(),
                     inActualValue.getType());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.persist.EntityTestBase#changeEntity(org.marketcetera.persist.EntityBase)
     */
    @Override
    protected void changeEntity(Fruit inEntity)
    {
        inEntity.setName("name-changed-" + System.nanoTime());
    }
}
