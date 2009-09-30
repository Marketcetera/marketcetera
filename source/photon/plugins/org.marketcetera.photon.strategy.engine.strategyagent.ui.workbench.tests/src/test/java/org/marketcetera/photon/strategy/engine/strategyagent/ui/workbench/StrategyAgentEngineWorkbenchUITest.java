package org.marketcetera.photon.strategy.engine.strategyagent.ui.workbench;

import org.junit.Test;
import org.marketcetera.photon.test.OSGITestUtil;


/* $License$ */

/**
 * Tests {@link StrategyAgentEngineWorkbenchUI}.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class StrategyAgentEngineWorkbenchUITest {

    @Test
    public void testPluginId() {
        OSGITestUtil.assertBundle(StrategyAgentEngineWorkbenchUI.PLUGIN_ID);
    }
}
