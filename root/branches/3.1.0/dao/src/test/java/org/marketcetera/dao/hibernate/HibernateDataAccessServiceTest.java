package org.marketcetera.dao.hibernate;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

/* $License$ */

/**
 * Tests {@link HibernategetDao()}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: HibernateDataAccessServiceTest.java 82384 2012-07-20 19:09:59Z colin $
 * @since $Release$
 */
public class HibernateDataAccessServiceTest
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
    }
    /**
     * Tests that data access objects are present as expected.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testDao()
            throws Exception
    {
        assertNotNull(getDao());
        assertNotNull(getDao().getUserDao());
        assertNotNull(getDao().getGroupDao());
        assertNotNull(getDao().getAuthorityDao());
    }
}
