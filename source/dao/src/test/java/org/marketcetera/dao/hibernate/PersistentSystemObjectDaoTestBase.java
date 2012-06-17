package org.marketcetera.dao.hibernate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.core.ExpectedFailure;
import org.marketcetera.systemmodel.SystemObject;
import org.springframework.dao.DataIntegrityViolationException;

/* $License$ */

/**
 * Provides common behavior for persistent system object tests.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: PersistentSystemObjectDaoTestBase.java 82316 2012-03-21 21:13:27Z colin $
 * @since $Release$
 */
public abstract class PersistentSystemObjectDaoTestBase<DataType extends SystemObject>
        extends HibernateTestBase
{
    /**
     * Run before each test. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Before
    public void setup()
            throws Exception
    {
        super.setup();
        clearTable();
    }
    /**
     * Tests adding a new object the test type.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testAdd()
            throws Exception
    {
        // get an object to add
        final DataType newObject = createNew();
        // object should not yet exist in the db
        add(newObject);
        assertTrue(newObject.getId() != 0);
        // can't re-add
        new ExpectedFailure<DataIntegrityViolationException>() {
            @Override
            protected void run()
                    throws Exception
            {
                add(newObject);
            }
        };
        // verify object was added
        DataType typeToCompare = getById(newObject.getId());
        assertEquals(newObject,
                     typeToCompare);
    }
    /**
     * Tests the ability to retrieve objects by id.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void getById()
            throws Exception
    {
        // odds are poor that this will be a valid ID
        long badId = System.nanoTime();
        assertNull(getById(badId));
        final DataType newObject = createNew();
        add(newObject);
        DataType typeToCompare = getById(newObject.getId());
        assertEquals(newObject,
                     typeToCompare);
    }
    /**
     * Clears all records in the test type table.
     *
     * @throws Exception if an unexpected error occurs
     */
    protected void clearTable()
            throws Exception
    {
        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        session.createQuery("delete from " + getTableClass().getName()).executeUpdate();
        session.getTransaction().commit();
    }
    /**
     * Add the given <code>DataType</code>. 
     *
     * @param inData a <code>DataType</code> value
     */
    protected abstract void add(DataType inData);
    /**
     * Get the <code>DataType</code> by id.
     *
     * @param inId a <code>long</code> value
     * @return a <code>DataType</code> value or <code>null</code>
     */
    protected abstract DataType getById(long inId);
    /**
     * Creates a new object of the test type.
     *
     * @return a <code>DataType</code>
     */
    protected abstract DataType createNew();
    /**
     * Gets the persistent class of the test type.
     *
     * @return a <code>Class&lt;? extends DataType&gt;</code> value
     */
    protected abstract Class<? extends DataType> getTableClass();
}
