package org.marketcetera.photon.strategy.engine.sa.tests;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.marketcetera.photon.strategy.engine.model.core.ConnectionState;
import org.marketcetera.photon.strategy.engine.model.sa.StrategyAgentEngine;
import org.marketcetera.photon.strategy.engine.model.sa.StrategyAgentEngineFactory;

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

    public static void assertStrategyAgentEngine(StrategyAgentEngine actual,
            StrategyAgentEngine expected) {
        assertStrategyAgentEngine(actual, expected.getName(), expected
                .getDescription(), expected.getJmsUrl(), actual
                .getWebServiceHostname(), expected.getWebServicePort(),
                expected.getConnectionState());
    }

    public static StrategyAgentEngine createStrategyAgentEngine(String name,
            String description, String url, String host, int port) {
        StrategyAgentEngine engine = StrategyAgentEngineFactory.eINSTANCE
                .createStrategyAgentEngine();
        engine.setName(name);
        engine.setDescription(description);
        engine.setJmsUrl(url);
        engine.setWebServiceHostname(host);
        engine.setWebServicePort(port);
        return engine;
    }

    public static StrategyAgentEngine createStrategyAgentEngine(String name) {
        return createStrategyAgentEngine(name, "Description", "url", "host",
                100);
    }

    private StrategyAgentEngineTestUtil() {
        throw new AssertionError("non-instantiable"); //$NON-NLS-1$
    }
}
