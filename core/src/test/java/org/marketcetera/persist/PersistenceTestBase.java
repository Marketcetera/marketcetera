package org.marketcetera.persist;

import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:persist.xml"})
@TransactionConfiguration(defaultRollback=true)
public abstract class PersistenceTestBase
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
    protected <Clazz> Clazz getBean(Class<Clazz> inType)
    {
        return (Clazz)applicationContext.getBean(inType);
    }
    /**
     * Gets the <code>FruitService</code> for this application.
     *
     * @return a <code>FruitService</code> value
     */
    protected FruitService getFruitService()
    {
        return getBean(FruitService.class);
    }
    /**
     * Gets the <code>FruitServiceDataAccessObject</code> for this application.
     *
     * @return a <code>FruitDataAccessObject</code> value
     */
    protected FruitDataAccessObject getFruitDao()
    {
        return getBean(FruitDataAccessObject.class);
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
