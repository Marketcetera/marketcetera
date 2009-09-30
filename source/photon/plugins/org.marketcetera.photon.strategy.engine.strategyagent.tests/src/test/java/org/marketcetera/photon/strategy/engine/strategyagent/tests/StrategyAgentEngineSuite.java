package org.marketcetera.photon.strategy.engine.strategyagent.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.marketcetera.photon.internal.strategy.engine.strategyagent.InternalStrategyAgentEngineTest;
import org.marketcetera.photon.internal.strategy.engine.strategyagent.MessagesTest;
import org.marketcetera.photon.internal.strategy.engine.strategyagent.StrategyAgentConnectionTest;
import org.marketcetera.photon.strategy.engine.strategyagent.StrategyAgentEnginesTest;

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
