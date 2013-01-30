package org.marketcetera.persist;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public void testMultiQueryAll()
            throws Exception
    {
        FruitRepository fruitRepo = getBean(FruitRepository.class);
        assertEquals(0,
                     fruitRepo.count());
        Collection<Fruit> someFruit = new ArrayList<Fruit>();
        for(int i=0;i<10;i++) {
            Fruit fruit = getNewEntity();
            someFruit.add(fruit);
            fruitRepo.save(fruit);
        }
        assertEquals(someFruit.size(),
                     fruitRepo.count());
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
    /* (non-Javadoc)
     * @see org.marketcetera.persist.EntityTestBase#getRepositoryType()
     */
    @Override
    protected Class<FruitRepository> getRepositoryType()
    {
        return FruitRepository.class;
    }
}
