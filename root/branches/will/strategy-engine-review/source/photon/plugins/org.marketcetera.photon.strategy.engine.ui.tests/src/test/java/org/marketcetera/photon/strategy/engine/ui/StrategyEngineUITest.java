package org.marketcetera.photon.strategy.engine.ui;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.eclipse.core.runtime.Platform;
import org.junit.Test;

/* $License$ */

/**
 * Tests {@link StrategyEngineUI}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class StrategyEngineUITest {

    @Test
    public void testPluginId() {
        // this tests both that the constant id is correct and that the plugin
        // is started correctly
        assertThat(Platform.getBundle(StrategyEngineUI.PLUGIN_ID),
                not(nullValue()));
    }
}
