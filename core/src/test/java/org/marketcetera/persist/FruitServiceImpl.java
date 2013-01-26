package org.marketcetera.persist;

import org.marketcetera.persist.Fruit.Type;
import org.springframework.beans.factory.annotation.Autowired;
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
     * @see org.marketcetera.persist.FruitService#add(java.lang.String, java.lang.String, org.marketcetera.persist.Fruit.Type)
     */
    @Override
    public Fruit add(String inName,
                     String inDescription,
                     Type inType)
    {
        Fruit fruit = new Fruit(inName,
                                inDescription,
                                inType);
        System.out.println("Adding " + fruit);
        fruitDao.add(fruit);
        System.out.println("Done: " + fruitDao.getAll());
        return fruit;
    }
//    /* (non-Javadoc)
//     * @see org.marketcetera.persist.FruitService#getAllByType(org.marketcetera.persist.Fruit.Type)
//     */
//    @Override
//    public List<Fruit> getAllByType(Type inType)
//    {
//        return fruitDao.getByType(inType);
//    }
    @Autowired
    private FruitDataAccessObject fruitDao;
}
