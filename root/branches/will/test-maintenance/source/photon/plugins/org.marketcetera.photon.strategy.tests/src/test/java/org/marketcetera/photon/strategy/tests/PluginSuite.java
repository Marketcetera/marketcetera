package org.marketcetera.photon.strategy.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.marketcetera.photon.internal.strategy.RemoteAgentManagerTest;
import org.marketcetera.photon.internal.strategy.StrategyManagerTest;
import org.marketcetera.photon.internal.strategy.TradeSuggestionManagerTest;

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
	StrategyManagerTest.class,
	TradeSuggestionManagerTest.class,
	RemoteAgentManagerTest.class
})
public class PluginSuite {

}
