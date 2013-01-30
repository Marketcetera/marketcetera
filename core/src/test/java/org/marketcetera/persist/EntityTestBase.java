package org.marketcetera.persist;

import static org.junit.Assert.assertEquals;

import javax.persistence.EntityNotFoundException;

import org.junit.Before;
import org.junit.Test;
import org.marketcetera.module.ExpectedFailure;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
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
    @Transactional
    public void testGetById()
            throws Exception
    {
        assertEquals(0,
                     repository.count());
        // does not exist
        new ExpectedFailure<EntityNotFoundException>() {
            @Override
            protected void run()
                    throws Exception
            {
                repository.findOne(0l);
            }
        };
        // create a new one
        Clazz newEntity = getNewEntity();
        newEntity = repository.save(newEntity);
        // retrieve by the ID and compare
        Clazz entityCopy = repository.findOne(newEntity.getId());
        verifyEntity(newEntity,
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
//        Clazz entity = getNewEntity();
//        assertNull(entity.getCreatedDate());
//        assertNull(entity.getLastModifiedDate());
//        entity = repository.save(entity);
//        try {
//            assertNotNull(entity.getCreatedDate());
//            assertNotNull(entity.getLastModifiedDate());
//            assertEquals(entity.getCreatedDate(),
//                         entity.getLastModifiedDate());
//            Thread.sleep(1000);
//            changeEntity(entity);
//            entity = repository.save(entity);
//            assertNotEquals(entity.getCreatedDate(),
//                            entity.getLastModifiedDate());
//        } finally {
//            repository.delete(entity);
//        }
    }
    protected void verifyEntity(Clazz inExpectedValue,
                                Clazz inActualValue)
    {
        assertEquals(inExpectedValue.getId(),
                     inActualValue.getId());
//        assertEquals(inExpectedValue.getCreatedDate(),
//                     inActualValue.getCreatedDate());
//        assertEquals(inExpectedValue.getLastModifiedDate(),
//                     inActualValue.getLastModifiedDate());
        assertEquals(inExpectedValue.getVersion(),
                     inActualValue.getVersion());
    }
    protected abstract Class<? extends EntityRepository<Clazz>> getRepositoryType();
    protected abstract Clazz getNewEntity();
    protected abstract void changeEntity(Clazz inEntity);
    private EntityRepository<Clazz> repository;
}
