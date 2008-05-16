package org.marketcetera.ors;

import org.marketcetera.core.MarketceteraTestSuite;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.ors.ORSMessageKey;
import org.marketcetera.ors.OrderRoutingSystem;

import junit.framework.Test;
import junit.framework.TestCase;

/**
 * @author toli
 * @version $Id: ORSMessageKeyTest.java 3587 2008-04-24 23:38:47Z tlerios $
 */

@ClassVersion("$Id: ORSMessageKeyTest.java 3587 2008-04-24 23:38:47Z tlerios $")
public class ORSMessageKeyTest extends TestCase {
    public ORSMessageKeyTest(String inName) {
        super(inName);
    }

    public static Test suite() {
        return new MarketceteraTestSuite(ORSMessageKeyTest.class);
    }
    public void testInstantiateAll() throws Exception {
    	new OrderRoutingSystem();
        for(ORSMessageKey key : ORSMessageKey.values()) {
            String localized = key.getLocalizedMessage();
            assertNotNull(key.toString(), localized);
            assertTrue(key.toString(), localized.length() > 0);
        }
    }
}
