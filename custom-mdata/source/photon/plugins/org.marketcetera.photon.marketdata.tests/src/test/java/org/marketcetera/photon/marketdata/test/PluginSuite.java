package org.marketcetera.photon.marketdata.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/* $License$ */

/**
 * Test suite that must be run in an OSGi environment.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
	HeadlessSuite.class
})
public class PluginSuite {

}
