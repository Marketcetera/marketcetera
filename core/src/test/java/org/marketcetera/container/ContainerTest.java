package org.marketcetera.container;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.context.ConfigurableApplicationContext;

/* $License$ */

/**
 * Tests {@link ApplicationContainer}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class ContainerTest
{
    /**
     * Tests application startup.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testStartup()
            throws Exception
    {
        ApplicationContainer.main(new String[0]);
        ConfigurableApplicationContext context = ApplicationContainer.getContext();
        MockApplication application = context.getBean(MockApplication.class);
        assertTrue(application.isRunning());
        application.stop();
        assertFalse(application.isRunning());
    }
}
