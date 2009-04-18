package org.marketcetera.photon.marketdata.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.marketcetera.photon.internal.marketdata.DepthOfBookManagerTest;
import org.marketcetera.photon.internal.marketdata.LatestTickManagerTest;
import org.marketcetera.photon.internal.marketdata.MarketstatManagerTest;
import org.marketcetera.photon.internal.marketdata.TopOfBookManagerTest;
import org.marketcetera.photon.marketdata.MarketDataFeedTest;
import org.marketcetera.photon.marketdata.MarketDataReceiverModuleTest;

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
	MarketDataFeedTest.class,
	MarketDataReceiverModuleTest.class,
	LatestTickManagerTest.class,
	TopOfBookManagerTest.class,
	MarketstatManagerTest.class,
	DepthOfBookManagerTest.class
})
public class PluginSuite {

}
