package org.marketcetera.persist;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
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
@ContextConfiguration(locations={"classpath:persist.xml"})
@TransactionConfiguration(defaultRollback=true)
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode=ClassMode.AFTER_EACH_TEST_METHOD)
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
     * Get the entityManager value.
     *
     * @return a <code>EntityManager</code> value
     */
    protected EntityManager getEntityManager()
    {
        return entityManager;
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
    /**
     * provides access to datastore managed entities
     */
    @PersistenceContext
    private EntityManager entityManager;
}
