package org.marketcetera.persist;

import javax.annotation.concurrent.NotThreadSafe;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/* $License$ */

/**
 * Provides access to the application context.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Component
@NotThreadSafe
public class ApplicationContextRepository
        implements ApplicationContextAware
{
    /**
     * Gets the single bean with the given type.
     *
     * @param inType a <code>Class&lt;Clazz&gt;</code> value
     * @return a <code>Clazz</code> value
     */
    public <Clazz> Clazz getBean(Class<Clazz> inType)
    {
        return applicationContext.getBean(inType);
    }
    /**
     * Gets the singleton instance of this object.
     *
     * @return an <code>ApplicationContextRepository</code> value
     * @throws IllegalStateException if the repository has not been initialized
     */
    public static ApplicationContextRepository getInstance()
    {
        if(instance == null) {
            throw new IllegalStateException();
        }
        return instance;
    }
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
     * Get the applicationContext value.
     *
     * @return a <code>ApplicationContext</code> value
     */
    public ApplicationContext getApplicationContext()
    {
        return applicationContext;
    }
    /**
     * Create a new ApplicationContextRepository instance.
     */
    public ApplicationContextRepository()
    {
        instance = this;
    }
    /**
     * holds the application context for use in this application
     */
    private ApplicationContext applicationContext;
    /**
     * static instance
     */
    private static ApplicationContextRepository instance;
}
