package org.marketcetera.persist;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

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
        service = getEntityService();
        service.resetExceptions();
    }
    /**
     * Tests adding new instances via the entity service.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    @Transactional
    public void testAdd()
            throws Exception
    {
        assertEquals(0,
                     service.count());
        for(int i=0;i<10;i++) {
            Clazz newEntity = getNewEntity();
            service.save(newEntity);
        }
        Iterable<Clazz> objects = service.findAll();
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
                     service.count());
        // does not exist
        new ExpectedFailure<EntityNotFoundException>() {
            @Override
            protected void run()
                    throws Exception
            {
                service.findOne(0l);
            }
        };
        // create a new one
        Clazz newEntity = getNewEntity();
        newEntity = service.save(newEntity);
        // retrieve by the ID and compare
        Clazz entityCopy = service.findOne(newEntity.getId());
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
//        entity = service.save(entity);
//        try {
//            assertNotNull(entity.getCreatedDate());
//            assertNotNull(entity.getLastModifiedDate());
//            assertEquals(entity.getCreatedDate(),
//                         entity.getLastModifiedDate());
//            Thread.sleep(1000);
//            changeEntity(entity);
//            entity = service.save(entity);
//            assertNotEquals(entity.getCreatedDate(),
//                            entity.getLastModifiedDate());
//        } finally {
//            service.delete(entity);
//        }
    }
    /**
     * Tests that transactions are rolled back in the service layer if an exception occurs before commit.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testTransactionRollback()
            throws Exception
    {
        // explicitly do not make this test transactional because we need
        //  to pull the unchanged value from the db instead of the transaction cache to prove
        //  we busted the transaction with an exception. note that we have to clean up because of this
        List<Clazz> entitiesToCleanUp = new ArrayList<Clazz>();
        try {
            // entity doesn't exist yet
            assertEquals(0,
                         service.count());
            service.setAfterException(new RuntimeException("this exception is expected"));
            new ExpectedFailure<RuntimeException>() {
                @Override
                protected void run()
                        throws Exception
                {
                    service.save(getNewEntity());
                }
            };
            service.resetExceptions();
            // entity still doesn't exist
            assertEquals(0,
                         service.count());
            // create the entity
            final Clazz entity = service.save(getNewEntity());
            entitiesToCleanUp.add(entity);
            // entity exists
            assertNotNull(service.findOne(entity.getId()));
            // set services to blow chunks
            service.setAfterException(new RuntimeException("this exception is expected"));
            new ExpectedFailure<RuntimeException>() {
                @Override
                protected void run()
                        throws Exception
                {
                    service.delete(entity);
                }
            };
            service.resetExceptions();
            // entity exists (not deleted)
            assertNotNull(service.findOne(entity.getId()));
        } finally {
            for(Clazz entity : entitiesToCleanUp) {
                service.delete(entity);
            }
        }
    }
    protected TestEntityService<Clazz> getEntityService()
    {
        return getBean(getEntityServiceType());
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
    protected abstract Class<? extends TestEntityService<Clazz>> getEntityServiceType();
    protected abstract Clazz getNewEntity();
    protected abstract void changeEntity(Clazz inEntity);
    protected TestEntityService<Clazz> service;
}
