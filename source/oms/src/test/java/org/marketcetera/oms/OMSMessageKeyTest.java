package org.marketcetera.oms;

import org.marketcetera.core.MarketceteraTestSuite;
import org.marketcetera.core.ClassVersion;
import junit.framework.Test;
import junit.framework.TestCase;

/**
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public class OMSMessageKeyTest extends TestCase {
    public OMSMessageKeyTest(String inName) {
        super(inName);
    }

    public static Test suite() {
        return new MarketceteraTestSuite(OMSMessageKeyTest.class);
    }
    public void testInstantiateAll() throws Exception {
    	OrderManagementSystem.init();
        for(OMSMessageKey key : OMSMessageKey.values()) {
            String localized = key.getLocalizedMessage();
            assertNotNull(key.toString(), localized);
            assertTrue(key.toString(), localized.length() > 0);
        }
    }
}
