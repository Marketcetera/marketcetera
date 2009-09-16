package org.marketcetera.photon.strategy.engine.strategyagent.tests;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.marketcetera.photon.strategy.engine.model.core.ConnectionState;
import org.marketcetera.photon.strategy.engine.model.strategyagent.StrategyAgentEngine;

/* $License$ */

/**
 * Utilities for testing strategy agent engine model objects.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class StrategyAgentEngineTestUtil {

    public static void assertStrategyAgentEngine(StrategyAgentEngine engine,
            String name, String description, String jmsUrl, String hostname,
            int port, ConnectionState state) {
        assertThat(engine.getName(), is(name));
        assertThat(engine.getDescription(), is(description));
        assertThat(engine.getJmsUrl(), is(jmsUrl));
        assertThat(engine.getWebServiceHostname(), is(hostname));
        assertThat(engine.getWebServicePort(), is(port));
        assertThat(engine.getConnectionState(), is(state));
    }

    private StrategyAgentEngineTestUtil() {
        throw new AssertionError("non-instantiable"); //$NON-NLS-1$
    }
}
