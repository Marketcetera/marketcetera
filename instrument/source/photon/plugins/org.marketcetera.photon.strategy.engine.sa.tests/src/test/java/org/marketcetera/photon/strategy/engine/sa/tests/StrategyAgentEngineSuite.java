package org.marketcetera.photon.strategy.engine.sa.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.marketcetera.photon.internal.strategy.engine.sa.InternalStrategyAgentEngineTest;
import org.marketcetera.photon.internal.strategy.engine.sa.MessagesTest;
import org.marketcetera.photon.internal.strategy.engine.sa.StrategyAgentConnectionTest;
import org.marketcetera.photon.strategy.engine.sa.StrategyAgentEnginesTest;

/* $License$ */

/**
 * Test suite for this bundle.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@RunWith(Suite.class)
@Suite.SuiteClasses( { StrategyAgentConnectionTest.class,
        InternalStrategyAgentEngineTest.class, StrategyAgentEnginesTest.class,
        MessagesTest.class })
public class StrategyAgentEngineSuite {
}
