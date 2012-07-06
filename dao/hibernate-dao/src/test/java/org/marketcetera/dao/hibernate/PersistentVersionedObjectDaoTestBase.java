package org.marketcetera.dao.hibernate;

import org.junit.Test;
import org.marketcetera.core.ExpectedFailure;
import org.marketcetera.core.systemmodel.SystemObject;
import org.marketcetera.core.systemmodel.VersionedObject;
import org.springframework.orm.hibernate4.HibernateOptimisticLockingFailureException;

/* $License$ */

/**
 * Provides common behavior for persistent versioned object tests.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: PersistentVersionedObjectDaoTestBase.java 82316 2012-03-21 21:13:27Z colin $
 * @since $Release$
 */
public abstract class PersistentVersionedObjectDaoTestBase<DataType extends VersionedObject & SystemObject>
        extends PersistentSystemObjectDaoTestBase<DataType>
{
    /**
     * Tests version protection of writes.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testDirtyWrite()
            throws Exception
    {
        final DataType object1 = createNew();
        add(object1);
        DataType copyOfObject1 = getById(object1.getId());
        save(copyOfObject1);
        new ExpectedFailure<HibernateOptimisticLockingFailureException>() {
            @Override
            protected void run()
                    throws Exception
            {
                save(object1);
            }
        };
    }
    /**
     * Save the given <code>DataType</code>.
     *
     * @param inData a <code>DataType</code> value
     */
    protected abstract void save(DataType inData);
}
