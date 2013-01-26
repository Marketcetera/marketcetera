package org.marketcetera.persist;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.marketcetera.core.LoggerConfiguration;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class PersistenceTestBase
{
    /**
     * Runs once before all tests.
     *
     * @throws Exception if an unexpected error occurs
     */
    @BeforeClass
    public static void onceBefore()
            throws Exception
    {
        LoggerConfiguration.logSetup();
        context = new ClassPathXmlApplicationContext(new String[] { "persist.xml" });
        context.start();
    }
    /**
     * Runs once after all tests.
     *
     * @throws Exception if an unexpected error occurs
     */
    @AfterClass
    public static void onceAfter()
            throws Exception
    {
        if(context != null) {
            context.stop();
        }
    }
    protected <Clazz> Clazz getBean(Class<Clazz> inType)
    {
        return (Clazz)context.getBean(inType);
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
     * @return an <code>AbstractApplicationContext</code> value
     */
    protected static AbstractApplicationContext getContext()
    {
        return context;
    }
    /**
     * underlying spring context for tests
     */
    private static AbstractApplicationContext context;
}
