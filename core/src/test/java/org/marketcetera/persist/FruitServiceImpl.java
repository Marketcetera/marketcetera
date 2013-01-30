package org.marketcetera.persist;

import org.marketcetera.persist.Fruit.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.PagingAndSortingRepository;
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
        extends TestAbstractEntityService<Fruit>
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
        return getRepository().save(fruit);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.persist.AbstractEntityService#getRepository()
     */
    @Override
    protected PagingAndSortingRepository<Fruit,Long> getRepository()
    {
        return repository;
    }
    /**
     * provides datastore access to Clazz objects
     */
    @Autowired
    private FruitRepository repository;
}
