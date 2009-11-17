package org.marketcetera.photon.strategy.engine.sa.ui.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.marketcetera.photon.internal.strategy.engine.sa.ui.StrategyAgentConnectionCompositeTest;
import org.marketcetera.photon.strategy.engine.sa.ui.NewStrategyAgentWizardTest;
import org.marketcetera.photon.strategy.engine.sa.ui.StrategyAgentEngineUITest;
import org.marketcetera.photon.strategy.engine.sa.ui.StrategyAgentEnginesSupportPersistenceTest;
import org.marketcetera.photon.strategy.engine.sa.ui.StrategyAgentEnginesSupportTest;

/* $License$ */

/**
 * Test suite for this bundle.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@RunWith(Suite.class)
@Suite.SuiteClasses( {
        StrategyAgentConnectionCompositeTest.class,
        org.marketcetera.photon.internal.strategy.engine.sa.ui.MessagesTest.class,
        org.marketcetera.photon.strategy.engine.sa.ui.MessagesTest.class,
        StrategyAgentEngineUITest.class, NewStrategyAgentWizardTest.class,
        StrategyAgentEnginesSupportTest.class,
        StrategyAgentEnginesSupportPersistenceTest.class })
public class StrategyAgentEngineUISuite {
}
