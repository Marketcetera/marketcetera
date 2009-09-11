package org.marketcetera.photon.strategy.engine.strategyagent.ui.workbench.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.marketcetera.photon.internal.strategy.engine.strategyagent.ui.workbench.ConnectHandlerTest;
import org.marketcetera.photon.internal.strategy.engine.strategyagent.ui.workbench.DisconnectHandlerTest;
import org.marketcetera.photon.internal.strategy.engine.strategyagent.ui.workbench.NewStrategyAgentHandlerTest;
import org.marketcetera.photon.internal.strategy.engine.strategyagent.ui.workbench.StrategyAgentConnectionPropertyPageTest;

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
        org.marketcetera.photon.internal.strategy.engine.strategyagent.ui.workbench.MessagesTest.class,
        NewStrategyAgentHandlerTest.class, ConnectHandlerTest.class,
        DisconnectHandlerTest.class,
        StrategyAgentConnectionPropertyPageTest.class })
public class StrategyAgentEngineWorkbenchUISuite {
}
