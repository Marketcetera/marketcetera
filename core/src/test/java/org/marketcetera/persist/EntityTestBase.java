package org.marketcetera.persist;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.junit.Before;
import org.junit.Test;
import org.marketcetera.module.ExpectedFailure;

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
        service.create(newEntity);
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
        service.create(entity);
        assertNotNull(entity.getCreated());
        assertNotNull(entity.getUpdated());
        assertEquals(entity.getCreated(),
                     entity.getUpdated());
        Thread.sleep(250);
        changeEntity(entity);
        service.update(entity);
        assertNotEquals(entity.getCreated(),
                        entity.getUpdated());
    }
    @Test
    public void testUpdateTransaction()
    {
        Clazz entity = getNewEntity();
        service.create(entity);
        changeEntity(entity);
        System.out.println("Entity id is " + entity.getId());
        service.setAfterException(new RuntimeException("this exception is expected"));
        try {
            System.out.println("Calling busted update");
            service.update(entity);
        } catch (Exception e) {}
        System.out.println(service.read(entity.getId()));
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
