package org.marketcetera.photon.module.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.marketcetera.photon.module.SinkConsoleControllerTest;

/* $License$ */

/**
 * Test suite that must be run as a Plug-in JUnit suite. 
 * 
 * Note: {@link HeadlessSuite Headless tests} are also run.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
	HeadlessSuite.class,
	SinkConsoleControllerTest.class
})
public class UISuite {

}

