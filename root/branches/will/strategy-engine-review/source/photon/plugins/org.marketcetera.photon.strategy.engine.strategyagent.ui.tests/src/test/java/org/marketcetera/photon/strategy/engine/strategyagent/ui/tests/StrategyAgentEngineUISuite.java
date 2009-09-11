package org.marketcetera.photon.strategy.engine.strategyagent.ui.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.marketcetera.photon.internal.strategy.engine.strategyagent.ui.StrategyAgentConnectionCompositeTest;
import org.marketcetera.photon.strategy.engine.strategyagent.ui.NewStrategyAgentWizardTest;
import org.marketcetera.photon.strategy.engine.strategyagent.ui.StrategyAgentEngineUITest;

/* $License$ */

/**
 * Test suite for this bundle.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@RunWith(Suite.class)
@Suite.SuiteClasses( {
        StrategyAgentConnectionCompositeTest.class,
        org.marketcetera.photon.internal.strategy.engine.strategyagent.ui.MessagesTest.class,
        org.marketcetera.photon.strategy.engine.strategyagent.ui.MessagesTest.class,
        StrategyAgentEngineUITest.class, NewStrategyAgentWizardTest.class })
public class StrategyAgentEngineUISuite {
}
