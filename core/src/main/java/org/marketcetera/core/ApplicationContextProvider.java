package org.marketcetera.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/* $License$ */

/**
 * Provides access to the system {@link ApplicationContext} outside of Spring.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Component
public class ApplicationContextProvider
{
    /**
     * Get the applicationContext value.
     *
     * @return an <code>ApplicationContext</code> value
     */
    public ApplicationContext getApplicationContext()
    {
        return applicationContext;
    }
    /**
     * Sets the applicationContext value.
     *
     * @param an <code>ApplicationContext</code> value
     */
    public void setApplicationContext(ApplicationContext inApplicationContext)
    {
        applicationContext = inApplicationContext;
    }
    /**
     * Get the instance value.
     *
     * @return an <code>ApplicationContextProvider</code> value
     */
    public static ApplicationContextProvider getInstance()
    {
        return instance;
    }
    /**
     * Create a new ApplicationContextProvider instance.
     */
    public ApplicationContextProvider()
    {
        instance = this;
    }
    /**
     * instance value
     */
    private static ApplicationContextProvider instance;
    /**
     * application context value
     */
    @Autowired
    private ApplicationContext applicationContext;
}
