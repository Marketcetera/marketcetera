package org.marketcetera.orderloader;

import org.marketcetera.core.MarketceteraTestSuite;
import org.marketcetera.core.ClassVersion;
import junit.framework.Test;
import junit.framework.TestCase;

/**
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public class OrderLoaderMessageKeyTest extends TestCase {
    public OrderLoaderMessageKeyTest(String inName) {
        super(inName);
    }

    public static Test suite() {
        return new MarketceteraTestSuite(OrderLoaderMessageKeyTest.class);
    }
    public void testInstantiateAll() throws Exception {
        new OrderLoader("enduser","enduser_password");

        for(OrderLoaderMessageKey key : OrderLoaderMessageKey.values()) {
            String localized = key.getLocalizedMessage();
            assertNotNull(key.toString(), localized);
            assertTrue(key.toString(), localized.length() > 0);
        }
    }
}
