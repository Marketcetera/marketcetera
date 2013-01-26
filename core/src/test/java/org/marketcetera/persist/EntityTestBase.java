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
                     service.readAll().size());
        for(int i=0;i<10;i++) {
            Clazz newEntity = getNewEntity();
            service.create(newEntity);
        }
        List<Clazz> objects = service.readAll();
        assertEquals(10,
                     objects.size());
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
                     service.readAll().size());
        // does not exist
        new ExpectedFailure<EntityNotFoundException>() {
            @Override
            protected void run()
                    throws Exception
            {
                service.read(0);
            }
        };
        // create a new one
        Clazz newEntity = getNewEntity();
        newEntity = service.create(newEntity);
        // retrieve by the ID and compare
        Clazz entityCopy = service.read(newEntity.getId());
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
        Clazz entity = getNewEntity();
        assertNull(entity.getCreated());
        assertNull(entity.getUpdated());
        entity = service.create(entity);
        try {
            assertNotNull(entity.getCreated());
            assertNotNull(entity.getUpdated());
            assertEquals(entity.getCreated(),
                         entity.getUpdated());
            Thread.sleep(1000);
            changeEntity(entity);
            entity = service.update(entity);
            assertNotEquals(entity.getCreated(),
                            entity.getUpdated());
        } finally {
            service.delete(entity);
        }
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
                         service.readAll().size());
            service.setAfterException(new RuntimeException("this exception is expected"));
            new ExpectedFailure<RuntimeException>() {
                @Override
                protected void run()
                        throws Exception
                {
                    service.create(getNewEntity());
                }
            };
            service.resetExceptions();
            // entity still doesn't exist
            assertEquals(0,
                         service.readAll().size());
            // create the entity
            final Clazz entity = service.create(getNewEntity());
            entitiesToCleanUp.add(entity);
            // entity exists
            assertNotNull(service.read(entity.getId()));
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
            assertNotNull(service.read(entity.getId()));
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
        assertEquals(inExpectedValue.getCreated(),
                     inActualValue.getCreated());
        assertEquals(inExpectedValue.getUpdated(),
                     inActualValue.getUpdated());
        assertEquals(inExpectedValue.getVersion(),
                     inActualValue.getVersion());
    }
    protected abstract Class<? extends TestEntityService<Clazz>> getEntityServiceType();
    protected abstract Clazz getNewEntity();
    protected abstract void changeEntity(Clazz inEntity);
    protected TestEntityService<Clazz> service;
}
