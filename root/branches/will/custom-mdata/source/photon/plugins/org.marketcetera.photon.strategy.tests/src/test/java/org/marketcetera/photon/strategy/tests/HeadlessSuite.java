package org.marketcetera.photon.strategy.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.marketcetera.photon.internal.strategy.MessagesTest;
import org.marketcetera.photon.internal.strategy.StrategyPropertyTesterTest;
import org.marketcetera.photon.internal.strategy.StrategyTest;
import org.marketcetera.photon.internal.strategy.StrategyValidationTest;
import org.marketcetera.photon.internal.strategy.TradeSuggestionReceiverTest;
import org.marketcetera.photon.internal.strategy.table.TableSupportTest;

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
	StrategyTest.class,
	MessagesTest.class,
	StrategyPropertyTesterTest.class,
	StrategyValidationTest.class,
	TradeSuggestionReceiverTest.class,
	TableSupportTest.class
})
public class HeadlessSuite {

}
