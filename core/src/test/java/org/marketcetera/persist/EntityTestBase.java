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
public abstract class EntityTestBase<Clazz extends EntityBase>
        extends PersistenceTestBase
{
    @Test
    public void testAdd()
            throws Exception
    {
        EntityService<Clazz> service = getEntityService();
        assertEquals(0,
                     service.findAll().size());
        for(int i=0;i<10;i++) {
            Clazz newEntity = getNewEntity();
            service.create(newEntity);
        }
        assertEquals(10,
                     service.findAll().size());
    }
    protected EntityService<Clazz> getEntityService()
    {
        return getBean(getEntityServiceType());
    }
    protected abstract Class<? extends EntityService<Clazz>> getEntityServiceType();
    protected abstract Clazz getNewEntity();
}
