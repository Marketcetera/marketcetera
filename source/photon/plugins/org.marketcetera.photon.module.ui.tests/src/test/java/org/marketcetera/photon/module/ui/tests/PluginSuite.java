package org.marketcetera.photon.module.ui.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.marketcetera.photon.internal.module.ui.PreferencesAdapterTest;
import org.marketcetera.photon.internal.module.ui.SinkConsoleControllerTest;

/* $License$ */

/**
 * Test suite that must be run in an OSGi environment.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.1.0
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
	HeadlessSuite.class,
	PreferencesAdapterTest.class,
	SinkConsoleControllerTest.class	
})
public class PluginSuite {

}
