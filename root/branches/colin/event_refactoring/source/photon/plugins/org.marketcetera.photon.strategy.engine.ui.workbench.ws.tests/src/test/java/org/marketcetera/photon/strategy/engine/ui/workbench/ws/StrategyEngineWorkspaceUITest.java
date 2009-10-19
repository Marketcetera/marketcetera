package org.marketcetera.photon.strategy.engine.ui.workbench.ws;

import org.junit.Test;
import org.marketcetera.photon.strategy.engine.ui.workbench.ws.StrategyEngineWorkspaceUI;
import org.marketcetera.photon.test.OSGITestUtil;

/* $License$ */

/**
 * Tests {@link StrategyEngineWorkspaceUI}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class StrategyEngineWorkspaceUITest {

    @Test
    public void testPluginId() {
        OSGITestUtil.assertBundle(StrategyEngineWorkspaceUI.PLUGIN_ID);
    }
}
