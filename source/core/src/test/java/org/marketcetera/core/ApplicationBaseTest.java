package org.marketcetera.core;

import junit.framework.Test;
import junit.framework.TestCase;

import java.util.List;
import java.util.ArrayList;

/**
 * Test of some useful functions in the top-level {@link ApplicationBase} class.
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public class ApplicationBaseTest extends TestCase {
    public ApplicationBaseTest(String inName) {
        super(inName);
    }

    public static Test suite() {
        return new MarketceteraTestSuite(ApplicationBaseTest.class);
    }


    /** Verifies that the config file that is being parsed for jcyclone config
     * is being read correctly
     * @throws Exception
     */
    public void testBasicConstructor() throws Exception {
        ApplicationBase app = new ApplicationBase() {
            protected List<MessageBundleInfo> getLocalMessageBundles() {
                return new ArrayList<MessageBundleInfo>();
            }
        };
        assertNotNull(app.getLocalMessageBundles());
        assertEquals(0, app.getLocalMessageBundles().size());
    }
}
