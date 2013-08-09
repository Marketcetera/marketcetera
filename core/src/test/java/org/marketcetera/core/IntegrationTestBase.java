package org.marketcetera.core;

import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ContextConfiguration(locations={"file:src/test/sample_data/main.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public abstract class IntegrationTestBase
        implements ApplicationContextAware
{
    /* (non-Javadoc)
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    @Override
    public void setApplicationContext(ApplicationContext inApplicationContext)
            throws BeansException
    {
        applicationContext = inApplicationContext;
    }
    /**
     * Gets the Spring context for this test application.
     *
     * @return an <code>ApplicationContext</code> value
     */
    protected ApplicationContext getApplicationContext()
    {
        return applicationContext;
    }
    /**
     * underlying spring context for tests
     */
    private ApplicationContext applicationContext;
}
