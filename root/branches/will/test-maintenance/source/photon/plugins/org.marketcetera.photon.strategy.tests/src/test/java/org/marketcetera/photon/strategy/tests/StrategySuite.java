package org.marketcetera.photon.strategy.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.marketcetera.photon.internal.strategy.AbstractStrategyConnectionTest;
import org.marketcetera.photon.internal.strategy.MessagesTest;
import org.marketcetera.photon.internal.strategy.RemoteAgentManagerTest;
import org.marketcetera.photon.internal.strategy.RemoteStrategyAgentTest;
import org.marketcetera.photon.internal.strategy.StrategyManagerTest;
import org.marketcetera.photon.internal.strategy.StrategyPropertyTesterTest;
import org.marketcetera.photon.internal.strategy.StrategyTest;
import org.marketcetera.photon.internal.strategy.StrategyValidationTest;
import org.marketcetera.photon.internal.strategy.TradeSuggestionManagerTest;

/* $License$ */

/**
 * Test suite for this bundle.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.1.0
 */
@RunWith(Suite.class)
@Suite.SuiteClasses( { AbstractStrategyConnectionTest.class,
        StrategyTest.class, MessagesTest.class,
        StrategyPropertyTesterTest.class, StrategyValidationTest.class,
        RemoteStrategyAgentTest.class, StrategyManagerTest.class,
        TradeSuggestionManagerTest.class, RemoteAgentManagerTest.class })
public class StrategySuite {
}
