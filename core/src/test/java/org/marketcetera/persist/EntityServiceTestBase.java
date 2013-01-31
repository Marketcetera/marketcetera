package org.marketcetera.persist;

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
public abstract class EntityServiceTestBase<Clazz extends EntityBase>
        extends PersistenceTestBase
{
    /**
     * Runs before each test.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Before
    public void setup()
            throws Exception
    {
        service = getBean(getServiceType());
    }
    @Test
    public void oneTest()
    {
        
    }
    protected abstract Class<? extends EntityService<Clazz>> getServiceType();
    private EntityService<Clazz> service;
}
