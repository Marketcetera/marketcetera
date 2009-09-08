package org.marketcetera.photon.strategy.engine.strategyagent;

import org.junit.Test;
import org.marketcetera.photon.test.OSGITestUtil;


/* $License$ */

/**
 * Tests {@link StrategyAgentEngines}.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class StrategyAgentEnginesTest {

    @Test
    public void testBundle() throws Exception {
        OSGITestUtil.assertBundle(StrategyAgentEngines.PLUGIN_ID);
    }
}
