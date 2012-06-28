package org.marketcetera.dao.hibernate;

import org.marketcetera.core.container.AbstractSpringApplication;

/* $License$ */

/**
 * Provides a hibernate framework for unit tests.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: MockHibernateApp.java 82312 2012-03-07 22:07:37Z colin $
 * @since $Release$
 */
public class MockHibernateApp
        extends AbstractSpringApplication
{
    /* (non-Javadoc)
     * @see org.marketcetera.core.container.AbstractSpringApplication#getLoggerCategory()
     */
    @Override
    protected Class<? extends AbstractSpringApplication> getLoggerCategory()
    {
        return MockHibernateApp.class;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.container.AbstractSpringApplication#getName()
     */
    @Override
    protected String getName()
    {
        return "Mock Hibernate Application";
    }
}
