package org.marketcetera.photon.strategy.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.marketcetera.photon.internal.strategy.AbstractStrategyConnectionTest;
import org.marketcetera.photon.internal.strategy.MessagesTest;
import org.marketcetera.photon.internal.strategy.RemoteStrategyAgentTest;
import org.marketcetera.photon.internal.strategy.StrategyPropertyTesterTest;
import org.marketcetera.photon.internal.strategy.StrategyTest;
import org.marketcetera.photon.internal.strategy.StrategyValidationTest;

/* $License$ */

/**
 * Test suite that can run as a regular JUnit suite.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
	AbstractStrategyConnectionTest.class,
	StrategyTest.class,
	MessagesTest.class,
	StrategyPropertyTesterTest.class,
	StrategyValidationTest.class,
	RemoteStrategyAgentTest.class
})
public class HeadlessSuite {
}
