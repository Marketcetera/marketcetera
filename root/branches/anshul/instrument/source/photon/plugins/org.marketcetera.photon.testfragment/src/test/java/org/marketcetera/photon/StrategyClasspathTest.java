package org.marketcetera.photon;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/* $License$ */

/**
 * Verify the strategy classpath is set (requires photon to be running).
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class StrategyClasspathTest {

    @Test
    public void verifyStrategyClasspath() throws Exception {
            assertNotNull(System.getProperty(org.marketcetera.strategy.Strategy.CLASSPATH_PROPERTYNAME));
    }
}
