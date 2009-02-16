package org.marketcetera.photon.strategy.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.marketcetera.photon.internal.strategy.TradeSuggestionReceiverTest;

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
	TradeSuggestionReceiverTest.class
})
public class PluginSuite {

}
