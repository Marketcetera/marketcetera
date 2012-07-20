package org.marketcetera.dao.hibernate;

import org.marketcetera.core.AbstractSpringApplication;

/* $License$ */

/**
 * Provides a hibernate framework for unit tests.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MockHibernateApp
        extends AbstractSpringApplication
{
    /* (non-Javadoc)
     * @see org.marketcetera.core.AbstractSpringApplication#getLoggerCategory()
     */
    @Override
    protected Class<? extends AbstractSpringApplication> getLoggerCategory()
    {
        return MockHibernateApp.class;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.AbstractSpringApplication#getName()
     */
    @Override
    protected String getName()
    {
        return "Mock Hibernate Application";
    }
}
