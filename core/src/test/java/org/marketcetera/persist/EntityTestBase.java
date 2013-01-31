package org.marketcetera.persist;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Before;
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
    @Before
    public void setup()
            throws Exception
    {
        repository = getBean(getRepositoryType());
    }
    /**
     * Tests adding new instances via the entity repository.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testAdd()
            throws Exception
    {
        assertEquals(0,
                     repository.count());
        for(int i=0;i<10;i++) {
            Clazz newEntity = getNewEntity();
            repository.save(newEntity);
        }
        Iterable<Clazz> objects = repository.findAll();
        long count = 0;
        for(@SuppressWarnings("unused")Clazz object : objects) {
            count += 1;
        }
        assertEquals(10,
                     count);
    }
    /**
     * Tests the ability to retrieve a specific entity by id.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testGetById()
            throws Exception
    {
        assertEquals(0,
                     repository.count());
        // does not exist
        assertNull(repository.findOne(0l));
        // create a new one
        Clazz newEntity = getNewEntity();
        newEntity = repository.save(newEntity);
        // retrieve by the ID and compare
        Clazz entityCopy = repository.findOne(newEntity.getId());
        verifyEntity(newEntity,
                     entityCopy);
    }
    /**
     * 
     *
     *
     * @throws Exception
     */
    @Test
    public void testFindAll()
            throws Exception
    {
        assertEquals(0,
                     repository.count());
        Collection<Clazz> someEntities = new ArrayList<Clazz>();
        for(int i=0;i<10;i++) {
            Clazz entity = getNewEntity();
            someEntities.add(entity);
            repository.save(entity);
        }
        assertEquals(someEntities.size(),
                     repository.count());
    }
    /**
     * Tests that an entity can be updated properly.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testUpdate()
            throws Exception
    {
        assertEquals(0,
                     repository.count());
        Clazz entity = getNewEntity();
        entity = repository.save(entity);
        assertEquals(0,
                     entity.getVersion());
        assertEquals(1,
                     repository.count());
        changeEntity(entity);
        entity = repository.save(entity);
        Clazz entityCopy = repository.findOne(entity.getId());
        verifyEntity(entity,
                     entityCopy);
    }
    /**
     * Tests that the created and updated values are set properly.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testCreatedAndUpdated()
            throws Exception
    {
        Clazz entity = getNewEntity();
        assertNull(entity.getCreated());
        assertNull(entity.getUpdated());
        entity = repository.save(entity);
        assertNotNull(entity.getCreated());
        assertNotNull(entity.getUpdated());
        assertEquals(entity.getCreated(),
                     entity.getUpdated());
//        Thread.sleep(1000);
//        changeEntity(entity);
//        entity = repository.save(entity);
//        Clazz entityCopy = repository.findOne(entity.getId());
//        assertNotEquals(entityCopy.getCreated(),
//                        entityCopy.getUpdated());
    }
    protected void verifyEntity(Clazz inExpectedValue,
                                Clazz inActualValue)
    {
        assertEquals(inExpectedValue.getId(),
                     inActualValue.getId());
        assertEquals(inExpectedValue.getCreated(),
                     inActualValue.getCreated());
        assertEquals(inExpectedValue.getUpdated(),
                     inActualValue.getUpdated());
        assertEquals(inExpectedValue.getVersion(),
                     inActualValue.getVersion());
    }
    protected abstract Class<? extends EntityRepository<Clazz>> getRepositoryType();
    protected abstract Clazz getNewEntity();
    protected abstract void changeEntity(Clazz inEntity);
    private EntityRepository<Clazz> repository;
}
